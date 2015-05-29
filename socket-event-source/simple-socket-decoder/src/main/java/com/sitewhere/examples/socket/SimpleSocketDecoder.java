/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.socket;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sitewhere.rest.model.device.communication.DecodedDeviceRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.device.communication.IDeviceEventDecoder;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;

/**
 * Simple decoder that assumes that each byte array payload is a complete NMEA-style
 * message (http://www.gpsinformation.org/dale/nmea.htm). Two types of messages are being
 * expected:
 * 
 * $GPGLL,4916.45,N,12311.12,W,225444,A,*1D (GPS Position Update)
 * $GPMSS,55,27,318.0,100,*66 (Beacon Receiver Status)
 * 
 * Note that these messages do not include a device hardware id, so for the example the
 * hardware id is passed as a parameter to the decoder bean in the configuration file. In
 * most real-world cases, there is a stateful interchange that will supply the hardware
 * id. Otherwise, the hardware id should be part of every message.
 * 
 * @author Derek
 */
public class SimpleSocketDecoder implements IDeviceEventDecoder<byte[]> {

	/** Hardware id set via bean in config file */
	private String hardwareId;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(java.lang.Object)
	 */
	@Override
	public List<IDecodedDeviceRequest<?>> decode(byte[] payload) throws SiteWhereException {
		String message = new String(payload);
		if (!message.startsWith("$GP")) {
			throw new SiteWhereException("Invalid message: " + message);
		}
		message = message.substring(3);
		String[] parts = message.split(",");

		List<IDecodedDeviceRequest<?>> result = new ArrayList<IDecodedDeviceRequest<?>>();
		if ("GLL".equals(parts[0])) {
			result.add(decodeGLL(parts));
		} else if ("MSS".equals(parts[0])) {
			result.add(decodeMSS(parts));
		}
		return result;
	}

	/**
	 * Decode an NMEA GLL message into a location event.
	 * 
	 * @param parts
	 * @return
	 */
	protected IDecodedDeviceRequest<IDeviceLocationCreateRequest> decodeGLL(String[] parts) {
		DecodedDeviceRequest<IDeviceLocationCreateRequest> decoded =
				new DecodedDeviceRequest<IDeviceLocationCreateRequest>();
		decoded.setHardwareId(getHardwareId());

		double lat = Double.parseDouble(parts[1]) / 100.0;
		String northSouth = parts[2];

		if ("S".equals(northSouth)) {
			lat = -1.0 * lat;
		}

		double lon = Double.parseDouble(parts[3]) / 100.0;
		String eastWest = parts[4];

		if ("W".equals(eastWest)) {
			lon = -1.0 * lon;
		}

		DeviceLocationCreateRequest loc = new DeviceLocationCreateRequest();
		loc.setLatitude(lat);
		loc.setLongitude(lon);
		loc.setElevation(0.0);
		loc.setEventDate(new Date());
		decoded.setRequest(loc);
		return decoded;
	}

	/**
	 * Decode an NMEA GLL message into a location event.
	 * 
	 * @param parts
	 * @return
	 */
	protected IDecodedDeviceRequest<IDeviceMeasurementsCreateRequest> decodeMSS(String[] parts) {
		DecodedDeviceRequest<IDeviceMeasurementsCreateRequest> decoded =
				new DecodedDeviceRequest<IDeviceMeasurementsCreateRequest>();
		decoded.setHardwareId(getHardwareId());

		double signalStrength = Double.parseDouble(parts[1]);
		double signalToNoise = Double.parseDouble(parts[2]);
		double beaconFrequency = Double.parseDouble(parts[3]);
		double beaconBitrate = Double.parseDouble(parts[4]);

		DeviceMeasurementsCreateRequest mx = new DeviceMeasurementsCreateRequest();

		mx.addOrReplaceMeasurement("signalStrength", signalStrength);
		mx.addOrReplaceMeasurement("signalToNoise", signalToNoise);
		mx.addOrReplaceMeasurement("beaconFrequency", beaconFrequency);
		mx.addOrReplaceMeasurement("beaconBitrate", beaconBitrate);

		mx.setEventDate(new Date());
		decoded.setRequest(mx);
		return decoded;
	}

	public String getHardwareId() {
		return hardwareId;
	}

	public void setHardwareId(String hardwareId) {
		this.hardwareId = hardwareId;
	}
}