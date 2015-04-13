package com.sitewhere.examples.airtraffic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.activemq.transport.stomp.StompConnection;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.examples.airtraffic.rest.model.Flight;
import com.sitewhere.examples.airtraffic.rest.model.MarshaledRoute;
import com.sitewhere.rest.client.SiteWhereClient;
import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceSpecificationCreateRequest;
import com.sitewhere.rest.model.device.request.SiteCreateRequest;
import com.sitewhere.rest.model.search.DeviceAssignmentSearchResults;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.ISiteWhereClient;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.ISiteMapMetadata;
import com.sitewhere.spi.error.ErrorCode;

/**
 * Loads model for the air traffic example. Creates a site and devices used to track
 * planes in route between major cities in the United States. Once the model is loaded, a
 * thread constantly generates location and measurements event data for the planes to
 * simulate a live monitoring scenario.
 * 
 * @author Derek
 */
public class AirTrafficModelLoader extends HttpServlet {

	/** Serial version UID */
	private static final long serialVersionUID = 2497075428217483256L;

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(AirTrafficModelLoader.class);

	/** Asset module id for tracker assets */
	private static final String ASSET_MODULE_TRACKERS = "at-devices";

	/** Asset module id for plane assets */
	private static final String ASSET_MODULE_PLANES = "at-planes";

	/** UUID that identifies the Air Traffic site */
	private static final String SITE_ID = "9d0f4ddd-3d9c-480e-ab5b-5b1da0efcbd8";

	/** Metadata value for flight number */
	private static final String MD_FLIGHT_NUMBER = "flightNumber";

	/** Number of planes to track */
	private static final int PLANE_COUNT = 30;

	/** Number of steps in route */
	private static final int NUM_STEPS = 50;

	/** Mapper used for marshaling event create requests */
	private static ObjectMapper MAPPER = new ObjectMapper();

	/** Executor that runs processing in the background */
	private ExecutorService executor = Executors.newSingleThreadExecutor();

	/** SiteWhere client using default connectivity */
	private ISiteWhereClient client;

	/** Site information */
	private ISite site;

	/** Tracker assets */
	private List<HardwareAsset> trackerAssets;

	/** Plane assets */
	private List<HardwareAsset> planeAssets;

	/** Device specifications */
	private List<IDeviceSpecification> specifications;

	/** Tracker devices */
	private List<IDevice> devices;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		client = new SiteWhereClient();
		executor.execute(new AirTrafficModel());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	@Override
	public void destroy() {
		executor.shutdownNow();
		super.destroy();
	}

	/**
	 * Populates data model then generates sample data for many planes moving between
	 * destinations.
	 * 
	 * @author Derek
	 */
	private class AirTrafficModel implements Runnable {

		@Override
		public void run() {
			if (waitForSiteWhereToStart()) {
				try {
					site = client.getSiteByToken(SITE_ID);
					LOGGER.info("Found site. Assuming data model already populated.");
					generateEventData();
				} catch (SiteWhereException e) {
					if (e instanceof SiteWhereSystemException) {
						if (((SiteWhereSystemException) e).getCode() == ErrorCode.InvalidSiteToken) {
							try {
								createModel();
								generateEventData();
							} catch (SiteWhereException e1) {
								LOGGER.error("Error loading air traffic data model.", e);
							}
							return;
						}
					}
					LOGGER.error("Error loading site information.", e);
				}
			}
		}

		/**
		 * Wait indefinitely for SiteWhere to become available. Attempts to ping the
		 * server every five seconds until it gets a response.
		 */
		protected boolean waitForSiteWhereToStart() {
			while (true) {
				try {
					client.getSiteWhereVersion();
					return true;
				} catch (SiteWhereException e) {
					LOGGER.info("Waiting on SiteWhere REST services to become available.");
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					LOGGER.error("Interrupted while waiting for SiteWhere to start.");
					return false;
				}
			}
		}

		/**
		 * Create the air traffic data model.
		 * 
		 * @throws SiteWhereException
		 */
		protected void createModel() throws SiteWhereException {
			loadAssets();

			site = createSite();
			specifications = createDeviceSpecifications();
			devices = createDevices();
			createDeviceAssignments();
		}

		/**
		 * Create site that will hold flight tracking devices.
		 * 
		 * @return
		 * @throws SiteWhereException
		 */
		protected ISite createSite() throws SiteWhereException {
			SiteCreateRequest create = new SiteCreateRequest();
			create.setToken(SITE_ID);
			create.setName("Air Traffic Example");
			create.setDescription("Example project that emulates an air traffic monitoring system. "
					+ "The system tracks many plane assets that have associated monitoring devices which "
					+ "send events for plane locations and various other KPIs.");
			create.setImageUrl("https://s3.amazonaws.com/sitewhere-demo/airport/airport.gif");
			create.getMap().setType("mapquest");
			create.getMap().addOrReplaceMetadata(ISiteMapMetadata.MAP_CENTER_LATITUDE, "39.798122");
			create.getMap().addOrReplaceMetadata(ISiteMapMetadata.MAP_CENTER_LONGITUDE, "-98.7223078");
			create.getMap().addOrReplaceMetadata(ISiteMapMetadata.MAP_ZOOM_LEVEL, "5");
			LOGGER.info("Creating new air traffic site.");
			return client.createSite(create);
		}

		/**
		 * Loads all of the tracker and plane assets.
		 * 
		 * @throws SiteWhereException
		 */
		protected void loadAssets() throws SiteWhereException {
			// List all tracker assets.
			SearchResults<HardwareAsset> trackers = client.getAssetsByModuleId(ASSET_MODULE_TRACKERS, null);
			trackerAssets = trackers.getResults();

			// List all plane assets.
			SearchResults<HardwareAsset> planes = client.getAssetsByModuleId(ASSET_MODULE_PLANES, null);
			planeAssets = planes.getResults();
		}

		/**
		 * Create device specifications for each tracker type.
		 * 
		 * @return
		 * @throws SiteWhereException
		 */
		protected List<IDeviceSpecification> createDeviceSpecifications() throws SiteWhereException {
			List<IDeviceSpecification> specifications = new ArrayList<IDeviceSpecification>();
			for (HardwareAsset asset : trackerAssets) {
				DeviceSpecificationCreateRequest create = new DeviceSpecificationCreateRequest();
				create.setAssetId(asset.getId());
				create.setAssetModuleId(ASSET_MODULE_TRACKERS);
				create.setToken(UUID.randomUUID().toString());
				create.setName(asset.getName() + " Specification");
				create.setContainerPolicy(DeviceContainerPolicy.Standalone);
				specifications.add(client.createDeviceSpecification(create));
				LOGGER.info("Created specification: " + create.getName());
			}
			return specifications;
		}

		/**
		 * Get a random specification from the list.
		 * 
		 * @return
		 */
		protected IDeviceSpecification getRandomSpecification() {
			int count = specifications.size();
			int slot = (int) Math.floor(Math.random() * count);
			return specifications.get(slot);
		}

		/**
		 * Create tracking devices.
		 * 
		 * @return
		 * @throws SiteWhereException
		 */
		protected List<IDevice> createDevices() throws SiteWhereException {
			List<IDevice> devices = new ArrayList<IDevice>();
			for (int i = 0; i < PLANE_COUNT; i++) {
				IDeviceSpecification spec = getRandomSpecification();
				DeviceCreateRequest create = new DeviceCreateRequest();
				create.setHardwareId(UUID.randomUUID().toString());
				create.setSiteToken(site.getToken());
				create.setSpecificationToken(spec.getToken());
				create.setComments("Air traffic tracker device " + i + ".");
				devices.add(client.createDevice(create));
				LOGGER.info("Created device: " + create.getHardwareId());
			}
			return devices;
		}

		/**
		 * Get a random plane asset from the list.
		 * 
		 * @return
		 */
		protected HardwareAsset getRandomPlaneAsset() {
			int count = planeAssets.size();
			int slot = (int) Math.floor(Math.random() * count);
			return planeAssets.get(slot);
		}

		/**
		 * Assign planes to each of the tracker devices.
		 * 
		 * @return
		 * @throws SiteWhereException
		 */
		protected List<IDeviceAssignment> createDeviceAssignments() throws SiteWhereException {
			List<IDeviceAssignment> assignments = new ArrayList<IDeviceAssignment>();
			for (IDevice device : devices) {
				HardwareAsset plane = getRandomPlaneAsset();
				DeviceAssignmentCreateRequest create = new DeviceAssignmentCreateRequest();
				create.setAssetId(plane.getId());
				create.setAssetModuleId(ASSET_MODULE_PLANES);
				create.setAssignmentType(DeviceAssignmentType.Associated);
				create.setDeviceHardwareId(device.getHardwareId());

				int flightNumber = (int) Math.floor(Math.random() * 10000);
				Map<String, String> metadata = new HashMap<String, String>();
				metadata.put(MD_FLIGHT_NUMBER, "SW-" + flightNumber);
				create.setMetadata(metadata);

				assignments.add(client.createDeviceAssignment(create));
				LOGGER.info("Created assignment: " + plane.getName() + " (" + flightNumber + ")");
			}
			return assignments;
		}

		/**
		 * Generate event data by repeatedly creating routes and moving planes along them
		 * while reporting location and other KPIs via Stomp.
		 * 
		 * @throws SiteWhereException
		 */
		protected void generateEventData() throws SiteWhereException {
			Map<String, Route> routes = new HashMap<String, Route>();
			while (true) {
				DeviceAssignmentSearchResults results = client.listAssignmentsForSite(site.getToken());
				List<DeviceAssignment> assignments = results.getResults();

				// Create routes for all assignments.
				for (DeviceAssignment assignment : assignments) {
					Route existing = routes.get(assignment.getToken());
					Route route =
							(existing != null) ? Route.startingWith(existing.getDestination())
									: Route.random();
					routes.put(assignment.getToken(), route);
				}

				StompConnection connection = new StompConnection();
				try {
					connection.open("localhost", 2345);
					connection.connect("system", "manager");
				} catch (Exception e) {
					throw new SiteWhereException("Unable to connect to Stomp server.", e);
				}

				// Step through routes and calculate current position.
				for (int i = 0; i < NUM_STEPS; i++) {
					List<Flight> flights = new ArrayList<Flight>();

					try {
						connection.begin("tx1");
					} catch (Exception e) {
						throw new SiteWhereException("Unable to connect to Stomp server.", e);
					}

					for (DeviceAssignment assignment : assignments) {
						Route route = routes.get(assignment.getToken());
						double slat = route.getDeparture().getLatitude();
						double elat = route.getDestination().getLatitude();
						double slon = route.getDeparture().getLongitude();
						double elon = route.getDestination().getLongitude();
						double latDelta = (elat - slat) / (double) NUM_STEPS;
						double lonDelta = (elon - slon) / (double) NUM_STEPS;
						double lat = route.getDeparture().getLatitude() + (i * latDelta);
						double lon = route.getDeparture().getLongitude() + (i * lonDelta);
						double heading = (270 - Math.atan2(slat - elat, slon - elon) * 180 / Math.PI) % 360;
						double elevation =
								Math.sin(i / (double) NUM_STEPS * Math.PI)
										* (10000.0 + route.getAltitudeMultiplier() * 1000);
						double fuelLevel =
								1000 - (i / (double) NUM_STEPS * route.getFuelMultiplier() * 330)
										+ (Math.random() * 30.0) - 15.0;
						double airspeed =
								200.0 + (Math.sin(i / (double) NUM_STEPS * Math.PI) * 250.0)
										+ (Math.random() * 80.0) - 40.0;

						Flight flight =
								createFlight(assignment, route, lat, lon, elevation, heading, fuelLevel,
										airspeed);
						sendEvents(connection, assignment, route, flight);
						flights.add(flight);
					}

					try {
						connection.commit("tx1");
					} catch (Exception e) {
						throw new SiteWhereException("Unable to commit data on Stomp connection.", e);
					}

					AirTraffic.getInstance().setFlights(flights);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						return;
					}
				}

				try {
					connection.disconnect();
				} catch (Exception e) {
					throw new SiteWhereException("Unable to commit data on Stomp connection.", e);
				}
			}
		}

		/**
		 * Create a {@link Flight} object from current data.
		 * 
		 * @param assignment
		 * @param route
		 * @param lat
		 * @param lon
		 * @param elevation
		 * @param heading
		 * @param fuelLevel
		 * @param airspeed
		 * @return
		 */
		protected Flight createFlight(DeviceAssignment assignment, Route route, double lat, double lon,
				double elevation, double heading, double fuelLevel, double airspeed) {
			Flight flight = new Flight();
			flight.setAssignmentToken(assignment.getToken());
			flight.setDeviceHardwareId(assignment.getDevice().getHardwareId());
			flight.setPlaneModel(assignment.getAssetName());
			flight.setRoute(new MarshaledRoute(route));
			flight.setLatitude(lat);
			flight.setLongitude(lon);
			flight.setElevation(roundToTwoDecimals(elevation));
			flight.setHeading(roundToTwoDecimals(heading));
			flight.setFuelLevel(roundToTwoDecimals(fuelLevel));
			flight.setAirspeed(roundToTwoDecimals(airspeed));
			flight.setFlightNumber(assignment.getMetadata(MD_FLIGHT_NUMBER));
			return flight;
		}

		/**
		 * Round a double value to two decimal points.
		 * 
		 * @param value
		 * @return
		 */
		protected double roundToTwoDecimals(double value) {
			return Math.floor(value * 100.0) / 100.0;
		}

		/**
		 * Send events for a single step of a route.
		 * 
		 * @param connection
		 * @param assignment
		 * @param route
		 * @param flight
		 * @throws SiteWhereException
		 */
		protected void sendEvents(StompConnection connection, DeviceAssignment assignment, Route route,
				Flight flight) throws SiteWhereException {

			boolean useStomp = true;
			DeviceEventBatch batch = createDeviceEventBatch(assignment, route, flight);
			if (useStomp) {
				String json = null;
				try {
					json = MAPPER.writeValueAsString(batch);
				} catch (JsonProcessingException e) {
					throw new SiteWhereException("Unable to marshal JSON payload.", e);
				}

				try {
					connection.send("/queue/SITEWHERE.STOMP", json, "tx1", null);
				} catch (Exception e) {
					LOGGER.error("Unable to send event data via Stomp.", e);
				}
			} else {
				(new SiteWhereClient()).addDeviceEventBatch(batch.getHardwareId(), batch);
			}
		}

		/**
		 * Create {@link DeviceEventBatch} from assignment data.
		 * 
		 * @param assignment
		 * @param route
		 * @param flight
		 * @return
		 * @throws SiteWhereException
		 */
		protected DeviceEventBatch createDeviceEventBatch(DeviceAssignment assignment, Route route,
				Flight flight) throws SiteWhereException {
			DeviceEventBatch batch = new DeviceEventBatch();
			batch.setHardwareId(assignment.getDevice().getHardwareId());

			// Create request for new device location event.
			DeviceLocationCreateRequest location = new DeviceLocationCreateRequest();
			location.setElevation(flight.getElevation());
			location.setLatitude(flight.getLatitude());
			location.setLongitude(flight.getLongitude());
			location.setEventDate(new Date());
			Map<String, String> metadata = new HashMap<String, String>();
			metadata.put("departure", route.getDeparture().name());
			metadata.put("destination", route.getDestination().name());
			location.setMetadata(metadata);
			batch.getLocations().add(location);

			// Create request for new device measurements event.
			DeviceMeasurementsCreateRequest request = new DeviceMeasurementsCreateRequest();
			request.setEventDate(new Date());
			request.addOrReplaceMeasurement("fuel.level", flight.getFuelLevel());
			request.addOrReplaceMeasurement("air.speed", flight.getAirspeed());
			request.addOrReplaceMeasurement("heading", flight.getHeading());
			batch.getMeasurements().add(request);

			return batch;
		}
	}
}