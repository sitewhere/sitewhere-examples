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
import com.sitewhere.examples.airtraffic.client.SiteWhereClientExt;
import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceSpecificationCreateRequest;
import com.sitewhere.rest.model.device.request.SiteCreateRequest;
import com.sitewhere.rest.model.search.DeviceAssignmentSearchResults;
import com.sitewhere.rest.model.search.SearchResults;
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

	/** Number of planes to track */
	private static final int PLANE_COUNT = 25;

	/** Number of steps in route */
	private static final int NUM_STEPS = 20;

	/** Mapper used for marshaling event create requests */
	private static ObjectMapper MAPPER = new ObjectMapper();

	/** Executor that runs processing in the background */
	private ExecutorService executor = Executors.newSingleThreadExecutor();

	/** SiteWhere client using default connectivity */
	private SiteWhereClientExt client;

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
		client = new SiteWhereClientExt();
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
				metadata.put("flightNumber", "SW-" + flightNumber);
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
					for (DeviceAssignment assignment : assignments) {
						Route route = routes.get(assignment.getToken());
						double latDelta =
								(route.getDestination().getLatitude() - route.getDeparture().getLatitude())
										/ (double) NUM_STEPS;
						double lonDelta =
								(route.getDestination().getLongitude() - route.getDeparture().getLongitude())
										/ (double) NUM_STEPS;
						double lat = route.getDeparture().getLatitude() + (i * latDelta);
						double lon = route.getDeparture().getLongitude() + (i * lonDelta);
						sendEvents(connection, assignment, route, lat, lon);
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						return;
					}
				}

				try {
					connection.disconnect();
				} catch (Exception e) {
					throw new SiteWhereException("Unable to disconnect from Stomp server.", e);
				}
			}
		}

		/**
		 * Send events for a single step of a route.
		 * 
		 * @param connection
		 * @param assignment
		 * @param route
		 * @param lat
		 * @param lon
		 * @throws SiteWhereException
		 */
		protected void sendEvents(StompConnection connection, DeviceAssignment assignment, Route route,
				double lat, double lon) throws SiteWhereException {

			String payload = createJson(assignment, route, lat, lon);

			try {
				connection.begin("tx1");
				connection.send("/queue/SITEWHERE.STOMP", payload, "tx1", null);
				connection.commit("tx1");
			} catch (Exception e) {
				LOGGER.error("Unable to send event data via Stomp.", e);
			}
		}

		/**
		 * Create JSON payload for events.
		 * 
		 * @param assignment
		 * @param route
		 * @param lat
		 * @param lon
		 * @return
		 * @throws SiteWhereException
		 */
		protected String createJson(DeviceAssignment assignment, Route route, double lat, double lon)
				throws SiteWhereException {
			DeviceEventBatch batch = new DeviceEventBatch();
			batch.setHardwareId(assignment.getDevice().getHardwareId());
			DeviceLocationCreateRequest location = new DeviceLocationCreateRequest();
			location.setElevation(0.0);
			location.setLatitude(lat);
			location.setLongitude(lon);
			location.setEventDate(new Date());

			Map<String, String> metadata = new HashMap<String, String>();
			metadata.put("departure", route.getDeparture().name());
			metadata.put("destination", route.getDestination().name());
			location.setMetadata(metadata);

			batch.getLocations().add(location);
			// DeviceMeasurementsCreateRequest request = new
			// DeviceMeasurementsCreateRequest();
			// request.setEventDate(new Date());
			// request.addOrReplaceMeasurement("engine.temp", 98.76);
			// batch.getMeasurements().add(request);
			try {
				return MAPPER.writeValueAsString(batch);
			} catch (JsonProcessingException e) {
				throw new SiteWhereException("Unable to marshal JSON payload.", e);
			}
		}
	}
}