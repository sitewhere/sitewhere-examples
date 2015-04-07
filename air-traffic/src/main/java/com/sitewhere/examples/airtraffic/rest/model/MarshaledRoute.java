/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.airtraffic.rest.model;

import com.sitewhere.examples.airtraffic.Route;

/**
 * Used to marshal a {@link Route} to get around default Enum marshaling.
 * 
 * @author Derek
 */
public class MarshaledRoute {

	/** Wrapped route */
	private Route route;

	public MarshaledRoute(Route route) {
		this.route = route;
	}

	public String getDepartureSymbol() {
		return route.getDeparture().name();
	}

	public String getDestinationSymbol() {
		return route.getDestination().name();
	}

	public String getDepartureName() {
		return truncate(route.getDeparture().getName());
	}

	public String getDestinationName() {
		return truncate(route.getDestination().getName());
	}

	protected String truncate(String value) {
		if (value.length() > 40) {
			value = value.substring(0, 40);
		}
		return value;
	}
}