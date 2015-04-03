package com.sitewhere.examples.airtraffic;

import com.sitewhere.device.event.processor.DefaultEventStorageProcessor;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;

/**
 * Overrides methods of {@link DefaultEventStorageProcessor} to turn on flag for
 * persisting most recent events to device assignment.
 * 
 * @author Derek
 */
public class StateUpdateEventStorageProcessor extends DefaultEventStorageProcessor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.DefaultEventStorageProcessor#
	 * onDeviceAlertCreateRequest(java.lang.String, java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
	 */
	@Override
	public void onDeviceAlertCreateRequest(String hardwareId, String originator,
			IDeviceAlertCreateRequest request) throws SiteWhereException {
		DeviceAlertCreateRequest updated = new DeviceAlertCreateRequest();
		updated.setEventDate(request.getEventDate());
		updated.setLevel(request.getLevel());
		updated.setMessage(request.getMessage());
		updated.setMetadata(request.getMetadata());
		updated.setType(request.getType());
		updated.setUpdateState(true);
		super.onDeviceAlertCreateRequest(hardwareId, originator, updated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.DefaultEventStorageProcessor#
	 * onDeviceLocationCreateRequest(java.lang.String, java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
	 */
	@Override
	public void onDeviceLocationCreateRequest(String hardwareId, String originator,
			IDeviceLocationCreateRequest request) throws SiteWhereException {
		DeviceLocationCreateRequest updated = new DeviceLocationCreateRequest();
		updated.setEventDate(request.getEventDate());
		updated.setElevation(request.getElevation());
		updated.setLatitude(request.getLatitude());
		updated.setLongitude(request.getLongitude());
		updated.setMetadata(request.getMetadata());
		updated.setUpdateState(true);
		super.onDeviceLocationCreateRequest(hardwareId, originator, updated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.DefaultEventStorageProcessor#
	 * onDeviceMeasurementsCreateRequest(java.lang.String, java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
	 */
	@Override
	public void onDeviceMeasurementsCreateRequest(String hardwareId, String originator,
			IDeviceMeasurementsCreateRequest request) throws SiteWhereException {
		DeviceMeasurementsCreateRequest updated = new DeviceMeasurementsCreateRequest();
		updated.setEventDate(request.getEventDate());
		updated.setMeasurements(request.getMeasurements());
		updated.setMetadata(request.getMetadata());
		updated.setUpdateState(true);
		super.onDeviceMeasurementsCreateRequest(hardwareId, originator, request);
	}
}