/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.socket.complex;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sitewhere.examples.socket.complex.message.AVRMCMessage;
import com.sitewhere.examples.socket.complex.message.LaipacContext;
import com.sitewhere.rest.model.device.communication.DecodedDeviceRequest;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceRegistrationRequest;
import com.sitewhere.server.device.DefaultDeviceModelInitializer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.device.communication.IDeviceEventDecoder;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;

/**
 * Decodes messages from Laipac devices into {@link IDecodedDeviceRequest}.
 * 
 * @author Derek
 */
public class LaipacEventDecoder implements IDeviceEventDecoder<LaipacContext> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(LaipacEventDecoder.class);

	/** Device specification to use for registering new devices */
	private static final String DEFAULT_SPECIFICATION_TOKEN =
			DefaultDeviceModelInitializer.LAIPAC_S911_SPEC_TOKEN;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(java.lang.Object)
	 */
	@Override
	public List<IDecodedDeviceRequest<?>> decode(LaipacContext laipac) throws SiteWhereException {
		List<IDecodedDeviceRequest<?>> events = new ArrayList<IDecodedDeviceRequest<?>>();
		switch (laipac.getAction()) {
		case IdentityProvided: {
			LOGGER.info("Processing registration entry for " + laipac.getSerialNumber());
			handleRegistration(laipac, events);
			break;
		}
		case LiveWaypointReading: {
			LOGGER.info("Processing live waypoint entry for " + laipac.getSerialNumber());
			handleLiveWaypointReading(laipac, events);
			break;
		}
		case DeviceOffOrOnCharger: {
			LOGGER.info("Processing device off or on charger for " + laipac.getSerialNumber());
			handleDeviceOffOrOnCharger(laipac, events);
			break;
		}
		}
		return events;
	}

	/**
	 * Handle case where identity has been provided by sending a registration request to
	 * SiteWhere.
	 * 
	 * @param laipac
	 * @param events
	 */
	protected void handleRegistration(LaipacContext laipac, List<IDecodedDeviceRequest<?>> events)
			throws SiteWhereException {
		DeviceRegistrationRequest registration = new DeviceRegistrationRequest();
		registration.setHardwareId(laipac.getSerialNumber());
		registration.setSpecificationToken(DEFAULT_SPECIFICATION_TOKEN);
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put(ILaipacConstants.META_SIM_CARD_NUMBER, laipac.getSimCardNumber());
		metadata.put(ILaipacConstants.META_SIM_PHONE_NUMBER, laipac.getSimPhoneNumber());
		registration.setMetadata(metadata);

		// TODO: This should come from messages.
		registration.setEventDate(new Date());

		DecodedDeviceRequest<IDeviceRegistrationRequest> decoded =
				new DecodedDeviceRequest<IDeviceRegistrationRequest>();
		decoded.setHardwareId(laipac.getSerialNumber());
		decoded.setRequest(registration);
		events.add(decoded);
	}

	/**
	 * Handle case where a live waypoint reading has been provided by sending a location
	 * event to SiteWhere.
	 * 
	 * @param laipac
	 * @param events
	 * @throws SiteWhereException
	 */
	protected void handleLiveWaypointReading(LaipacContext laipac, List<IDecodedDeviceRequest<?>> events)
			throws SiteWhereException {
		if (laipac.getMessages().size() < 1) {
			throw new SiteWhereException("Expected an AVRMC message with live waypoint reading.");
		}
		AVRMCMessage avrmc = (AVRMCMessage) laipac.getMessages().get(0);
		handleAVRMCLocation(laipac, avrmc, events);
		handleAVRMCMeasurements(laipac, avrmc, events);
	}

	/**
	 * Create a {@link DeviceLocationCreateRequest} from an AVRMC message.
	 * 
	 * @param laipac
	 * @param avrmc
	 * @param events
	 * @throws SiteWhereException
	 */
	protected void handleAVRMCLocation(LaipacContext laipac, AVRMCMessage avrmc,
			List<IDecodedDeviceRequest<?>> events) throws SiteWhereException {
		DeviceLocationCreateRequest location = new DeviceLocationCreateRequest();

		// Convert latitude from AVRMC format.
		Double lat = Math.floor(avrmc.getLatitude() / 100.0);
		Double latMin = (avrmc.getLatitude() - (lat * 100.0)) / 60.0;
		lat += latMin;
		if ("S".equals(avrmc.getNorthSouthIndicator())) {
			lat = -1.0 * lat;
		}
		location.setLatitude(lat);

		// Convert longitude from AVRMC format.
		Double lon = Math.floor(avrmc.getLongitude() / 100.0);
		Double lonMin = (avrmc.getLongitude() - (lon * 100.0)) / 60.0;
		lon += lonMin;
		if ("W".equals(avrmc.getEastWestIndicator())) {
			lon = -1.0 * lon;
		}
		location.setLongitude(lon);

		// We do not measure elevation.
		location.setElevation(0.0);

		// TODO: This should come from messages.
		location.setEventDate(new Date());

		DecodedDeviceRequest<IDeviceLocationCreateRequest> decoded =
				new DecodedDeviceRequest<IDeviceLocationCreateRequest>();
		decoded.setHardwareId(laipac.getSerialNumber());
		decoded.setRequest(location);
		events.add(decoded);
	}

	/**
	 * Create a {@link DeviceMeasurementsCreateRequest} from an AVRMC message.
	 * 
	 * @param laipac
	 * @param avrmc
	 * @param events
	 * @throws SiteWhereException
	 */
	protected void handleAVRMCMeasurements(LaipacContext laipac, AVRMCMessage avrmc,
			List<IDecodedDeviceRequest<?>> events) throws SiteWhereException {
		DeviceMeasurementsCreateRequest mx = new DeviceMeasurementsCreateRequest();
		mx.addOrReplaceMeasurement(ILaipacConstants.MX_SPEED, avrmc.getSpeed());
		mx.addOrReplaceMeasurement(ILaipacConstants.MX_COURSE, avrmc.getCourse());
		mx.addOrReplaceMeasurement(ILaipacConstants.MX_BATTERY_VOLTAGE,
				avrmc.getBatteryVoltage().doubleValue());
		mx.addOrReplaceMeasurement(ILaipacConstants.MX_CURRENT_MILEAGE,
				avrmc.getCurrentMileage().doubleValue());

		DecodedDeviceRequest<IDeviceMeasurementsCreateRequest> decoded =
				new DecodedDeviceRequest<IDeviceMeasurementsCreateRequest>();
		decoded.setHardwareId(laipac.getSerialNumber());
		decoded.setRequest(mx);
		events.add(decoded);
	}

	/**
	 * Handle case where device has been turned off or placed on charger.
	 * 
	 * @param laipac
	 * @param events
	 */
	protected void handleDeviceOffOrOnCharger(LaipacContext laipac, List<IDecodedDeviceRequest<?>> events) {
		DeviceAlertCreateRequest alert = new DeviceAlertCreateRequest();
		alert.setLevel(AlertLevel.Info);
		alert.setType(ILaipacConstants.ALERT_DEVICE_OFF);
		alert.setMessage("Device turned off or charger plugged in.");

		DecodedDeviceRequest<IDeviceAlertCreateRequest> decoded =
				new DecodedDeviceRequest<IDeviceAlertCreateRequest>();
		decoded.setHardwareId(laipac.getSerialNumber());
		decoded.setRequest(alert);
		events.add(decoded);
	}
}