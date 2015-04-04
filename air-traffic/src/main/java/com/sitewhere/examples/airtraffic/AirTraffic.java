/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.airtraffic;

import java.util.List;

import com.sitewhere.examples.airtraffic.rest.model.Flight;

/**
 * Singleton class that holds air traffic data.
 * 
 * @author Derek
 */
public class AirTraffic {

	/** Singleton instance */
	private static AirTraffic SINGLETON;

	/** List of flights */
	private List<Flight> flights;

	private AirTraffic() {
	}

	/**
	 * Get singleton instance.
	 * 
	 * @return
	 */
	public static AirTraffic getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new AirTraffic();
		}
		return SINGLETON;
	}

	public List<Flight> getFlights() {
		return flights;
	}

	public void setFlights(List<Flight> flights) {
		this.flights = flights;
	}
}