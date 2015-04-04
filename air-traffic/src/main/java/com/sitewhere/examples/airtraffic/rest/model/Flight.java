/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.airtraffic.rest.model;

/**
 * Model object for a flight.
 * 
 * @author Derek
 */
public class Flight {

	/** Unique assignment token */
	private String assignmentToken;

	/** Unique device hardware id */
	private String deviceHardwareId;

	/** Plane model from asset name */
	private String planeModel;

	/** Flight number */
	private String flightNumber;

	/** Location latitude */
	private double latitude;

	/** Location longitude */
	private double longitude;

	/** Location elevation */
	private double elevation;

	/** Heading angle */
	private double heading;

	/** Fuel level */
	private double fuelLevel;

	/** Air speed */
	private double airspeed;

	public String getAssignmentToken() {
		return assignmentToken;
	}

	public void setAssignmentToken(String assignmentToken) {
		this.assignmentToken = assignmentToken;
	}

	public String getDeviceHardwareId() {
		return deviceHardwareId;
	}

	public void setDeviceHardwareId(String deviceHardwareId) {
		this.deviceHardwareId = deviceHardwareId;
	}

	public String getPlaneModel() {
		return planeModel;
	}

	public void setPlaneModel(String planeModel) {
		this.planeModel = planeModel;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getElevation() {
		return elevation;
	}

	public void setElevation(double elevation) {
		this.elevation = elevation;
	}

	public double getHeading() {
		return heading;
	}

	public void setHeading(double heading) {
		this.heading = heading;
	}

	public double getFuelLevel() {
		return fuelLevel;
	}

	public void setFuelLevel(double fuelLevel) {
		this.fuelLevel = fuelLevel;
	}

	public double getAirspeed() {
		return airspeed;
	}

	public void setAirspeed(double airspeed) {
		this.airspeed = airspeed;
	}
}