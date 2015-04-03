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

	/**
	 * Choose a random route.
	 * 
	 * @return
	 */
	public static Route random() {
		Route route = new Route();
		route.setDeparture(Airport.random());
		route.setDestination(Airport.random());
		while (route.getDeparture() == route.getDestination()) {
			route.setDestination(Airport.random());
		}
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
}