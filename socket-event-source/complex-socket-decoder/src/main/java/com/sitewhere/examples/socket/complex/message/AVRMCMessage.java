/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.socket.complex.message;

import com.sitewhere.spi.SiteWhereException;

/**
 * Corresponds to the AVRMC message for Laipac devices.
 * 
 * @author Derek
 */
public class AVRMCMessage extends LaipacMessage {

	/** Unit id */
	private String unitId;

	/** UTC time */
	private Long utcTime;

	/** Status */
	private Status status;

	/** Latitude */
	private Double latitude;

	/** North / south indicator */
	private String northSouthIndicator;

	/** Longitude */
	private Double longitude;

	/** East / west indicator */
	private String eastWestIndicator;

	/** Speed */
	private Double speed;

	/** Course */
	private Double course;

	/** UTC date */
	private Long utcDate;

	/** Event code */
	private EventCode eventCode;

	/** Battery voltage */
	private Long batteryVoltage;

	/** Current mileage */
	private Long currentMileage;

	/** GPS on / off */
	private Boolean gpsOnOff;

	/** Analog port 1 */
	private Long analogPort1;

	/** Analog port 2 */
	private Long analogPort2;

	public AVRMCMessage() {
		super(LaipacCommandType.AVRMC);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.ee.devices.laipac.message.LaipacMessage#parse(java.lang.String[])
	 */
	@Override
	public void parse(String[] parts) throws SiteWhereException {
		this.unitId = parts[1];
		this.utcTime = parseLong(parts[2]);
		this.status = lookupStatus(parts[3]);
		this.latitude = parseDouble(parts[4]);
		this.northSouthIndicator = parts[5];
		this.longitude = parseDouble(parts[6]);
		this.eastWestIndicator = parts[7];
		this.speed = parseDouble(parts[8]);
		this.course = parseDouble(parts[9]);
		this.utcDate = parseLong(parts[10]);
		this.eventCode = lookupEventCode(parts[11]);
		this.batteryVoltage = parseLong(parts[12]);
		this.currentMileage = parseLong(parts[13]);
		this.gpsOnOff = parseBoolean(parts[14]);
		this.analogPort1 = parseLong(parts[15]);
		this.analogPort2 = parseLong(parts[16]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.ee.devices.laipac.message.LaipacMessage#buildSentence()
	 */
	@Override
	public String buildSentence() throws SiteWhereException {
		StringBuilder builder = new StringBuilder();
		builder.append(LaipacMessageParser.COMMAND_PREFIX);
		builder.append(getType().toString());
		builder.append(',');
		builder.append(printString(getUnitId()));
		builder.append(',');
		builder.append(printLong(getUtcTime()));
		builder.append(',');
		builder.append(printString(String.valueOf(getStatus().getCode())));
		builder.append(',');
		builder.append(printDouble(getLatitude()));
		builder.append(',');
		builder.append(printString(getNorthSouthIndicator()));
		builder.append(',');
		builder.append(printDouble(getLongitude()));
		builder.append(',');
		builder.append(printString(getEastWestIndicator()));
		builder.append(',');
		builder.append(printDouble(getSpeed()));
		builder.append(',');
		builder.append(printDouble(getCourse()));
		builder.append(',');
		builder.append(printLong(getUtcDate()));
		builder.append(',');
		builder.append(printString(String.valueOf(getEventCode().getCode())));
		builder.append(',');
		builder.append(printLong(getBatteryVoltage()));
		builder.append(',');
		builder.append(printLong(getCurrentMileage()));
		builder.append(',');
		builder.append(printBoolean(getGpsOnOff()));
		builder.append(',');
		builder.append(printLong(getAnalogPort1()));
		builder.append(',');
		builder.append(printLong(getAnalogPort2()));
		builder.append(LaipacMessageParser.CHECKSUM_DELIMITER);
		builder.append(getChecksum());
		return builder.toString();
	}

	/**
	 * Set status based on String value from message.
	 * 
	 * @param input
	 * @return
	 */
	protected Status lookupStatus(String input) {
		if (input.length() == 0) {
			return null;
		}
		char first = input.charAt(0);
		return Status.getByCode(first);
	}

	/**
	 * Set event code based on String value from message.
	 * 
	 * @param input
	 * @return
	 */
	protected EventCode lookupEventCode(String input) {
		if (input.length() == 0) {
			return null;
		}
		char first = input.charAt(0);
		return EventCode.getByCode(first);
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public Long getUtcTime() {
		return utcTime;
	}

	public void setUtcTime(Long utcTime) {
		this.utcTime = utcTime;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public String getNorthSouthIndicator() {
		return northSouthIndicator;
	}

	public void setNorthSouthIndicator(String northSouthIndicator) {
		this.northSouthIndicator = northSouthIndicator;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getEastWestIndicator() {
		return eastWestIndicator;
	}

	public void setEastWestIndicator(String eastWestIndicator) {
		this.eastWestIndicator = eastWestIndicator;
	}

	public Double getSpeed() {
		return speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public Double getCourse() {
		return course;
	}

	public void setCourse(Double course) {
		this.course = course;
	}

	public Long getUtcDate() {
		return utcDate;
	}

	public void setUtcDate(Long utcDate) {
		this.utcDate = utcDate;
	}

	public EventCode getEventCode() {
		return eventCode;
	}

	public void setEventCode(EventCode eventCode) {
		this.eventCode = eventCode;
	}

	public Long getBatteryVoltage() {
		return batteryVoltage;
	}

	public void setBatteryVoltage(Long batteryVoltage) {
		this.batteryVoltage = batteryVoltage;
	}

	public Long getCurrentMileage() {
		return currentMileage;
	}

	public void setCurrentMileage(Long currentMileage) {
		this.currentMileage = currentMileage;
	}

	public Boolean getGpsOnOff() {
		return gpsOnOff;
	}

	public void setGpsOnOff(Boolean gpsOnOff) {
		this.gpsOnOff = gpsOnOff;
	}

	public Long getAnalogPort1() {
		return analogPort1;
	}

	public void setAnalogPort1(Long analogPort1) {
		this.analogPort1 = analogPort1;
	}

	public Long getAnalogPort2() {
		return analogPort2;
	}

	public void setAnalogPort2(Long analogPort2) {
		this.analogPort2 = analogPort2;
	}

	/**
	 * Status code for AVRMC message.
	 * 
	 * @author Derek
	 */
	public static enum Status {

		/** Data is realtime */
		RealTime('A'),

		/** Data based on invalid GPS fix */
		InvaldGPS('V'),

		/** Data is repeated from old GPS data */
		Repeat('R'),

		/** GPS is powered down. Data from last fix */
		GPSPoweredDown('P'),

		/** Data is realtime */
		RealTime_AckRequired('a'),

		/** Data based on invalid GPS fix */
		InvaldGPS_AckRequired('v'),

		/** Data is repeated from old GPS data */
		Repeat_AckRequired('r'),

		/** GPS is powered down. Data from last fix */
		GPSPoweredDown_AckRequired('p');

		/** Event code */
		private char code;

		private Status(char code) {
			this.code = code;
		}

		public static Status getByCode(char code) {
			for (Status value : Status.values()) {
				if (value.getCode() == code) {
					return value;
				}
			}
			return null;
		}

		public char getCode() {
			return code;
		}

		public void setCode(char code) {
			this.code = code;
		}
	}

	/**
	 * Event code for AVRMC message.
	 * 
	 * @author Derek
	 */
	public static enum EventCode {

		/** Under charging */
		Charging('a'),

		/** Without charging */
		NotCharging('b'),

		/** Low battery alert */
		LowBattery('Z'),

		/** Geo-fence enter alert */
		GeoFenceEnter('X'),

		/** Tamper detection switch is open alert */
		TamperDetectionOpen('T'),

		/** Tamper detection switch is close alert */
		TamperDetectionClose('S'),

		/** Mileage alert */
		MileageAlert('M'),

		/** Unit is powered off or charger is plugged in */
		UnitOffOrOnCharger('H'),

		/** GSM connection changed to roaming */
		GSMConnectionRoaming('F'),

		/** GSM connection back to home network */
		GSMConnectionHome('E'),

		/** G-Sensor alert 1 */
		GSensorAlert('8'),

		/** Instance Geo-fence exit alert */
		InstanceGeoFenceExitAlert('7'),

		/** Over speed alert */
		OverSpeedAlert('6'),

		/** Geo-fence exits alert */
		GeoFenceExitAlert('4'),

		/** Panic/SOS button pressed alert */
		PanicSOSPressedAlert('3'),

		/** SOS button pressed alert */
		SOSPressedAlert('1'),

		/** Regular report */
		RegularReport('0');

		/** Event code */
		private char code;

		private EventCode(char code) {
			this.code = code;
		}

		public static EventCode getByCode(char code) {
			for (EventCode value : EventCode.values()) {
				if (value.getCode() == code) {
					return value;
				}
			}
			return null;
		}

		public char getCode() {
			return code;
		}

		public void setCode(char code) {
			this.code = code;
		}
	}
}