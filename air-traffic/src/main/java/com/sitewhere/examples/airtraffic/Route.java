/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.airtraffic;

/**
 * Route betweeen two airports.
 * 
 * @author Derek
 */
public class Route {

	/** Departure airport */
	private Airport departure;

	/** Destination airport */
	private Airport destination;

	/** Used to affect rate of fuel consumption */
	private double fuelMultiplier;

	/** Used to affect max altitude */
	private double altitudeMultiplier;

	/**
	 * Choose a random route.
	 * 
	 * @return
	 */
	public static Route random() {
		return Route.startingWith(Airport.random());
	}

	/**
	 * Create a route starting with a given airport.
	 * 
	 * @param departure
	 * @return
	 */
	public static Route startingWith(Airport departure) {
		Route route = new Route();
		route.setDeparture(departure);
		route.setDestination(Airport.random());
		while (route.getDeparture() == route.getDestination()) {
			route.setDestination(Airport.random());
		}
		route.setFuelMultiplier(Math.random() * 3);
		route.setAltitudeMultiplier(Math.random() * 20);
		return route;
	}

	public Airport getDeparture() {
		return departure;
	}

	public void setDeparture(Airport departure) {
		this.departure = departure;
	}

	public Airport getDestination() {
		return destination;
	}

	public void setDestination(Airport destination) {
		this.destination = destination;
	}

	public double getFuelMultiplier() {
		return fuelMultiplier;
	}

	public void setFuelMultiplier(double fuelMultiplier) {
		this.fuelMultiplier = fuelMultiplier;
	}

	public double getAltitudeMultiplier() {
		return altitudeMultiplier;
	}

	public void setAltitudeMultiplier(double altitudeMultiplier) {
		this.altitudeMultiplier = altitudeMultiplier;
	}
}