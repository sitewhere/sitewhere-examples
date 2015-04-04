/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.airtraffic;

/**
 * Enumeration of large United States airports.
 * 
 * @author Derek
 */
public enum Airport {

	ATL("Hartsfield Jackson Atlanta International Airport", 33.6366997, -84.4281006),

	ORD("Chicago O'Hare International Airport", 41.9785996, -87.9048004),

	LAX("Chicago O'Hare International Airport", 33.9425011, -118.4079971),

	DFW("Chicago O'Hare International Airport", 32.896801, -97.038002),

	DEN("Denver International Airport", 39.8616982, -104.6729965),

	JFK("John F Kennedy International Airport", 40.639801, -73.7789002),

	SFO("San Francisco International Airport", 37.6189995, -122.375),

	CLT("Charlotte Douglas International Airport", 35.2140007, -80.9431),

	LAS("McCarran International Airport", 36.080101, -115.1520004),

	PHX("Phoenix Sky Harbor International Airport", 33.4342995, -112.012001),

	IAH("George Bush Intercontinental Houston Airport", 29.9843998, -95.3414001),

	MIA("Miami International Airport", 25.7931995, -80.2906036),

	MCO("Orlando International Airport", 28.4293995, -81.3089981),

	EWR("Newark Liberty International Airport", 40.6925011, -74.1687012),

	SEA("Seattle Tacoma International Airport", 47.4490013, -122.3089981),

	MSP("Minneapolis-St Paul International/Wold-Chamberlain Airport", 44.882, -93.2218018),

	DTW("Detroit Metropolitan Wayne County Airport", 42.2123985, -83.3534012);

	/** Airport code */
	private String name;

	/** Airport latitude */
	private double latitude;

	/** Airport longitude */
	private double longitude;

	private Airport(String name, double latitude, double longitude) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Choose a random airport.
	 * 
	 * @return
	 */
	public static Airport random() {
		Airport[] all = Airport.values();
		int slot = (int) Math.floor(Math.random() * all.length);
		return all[slot];
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
}