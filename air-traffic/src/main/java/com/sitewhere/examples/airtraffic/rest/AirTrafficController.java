/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.airtraffic.rest;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.examples.airtraffic.AirTraffic;
import com.sitewhere.examples.airtraffic.rest.model.Flight;
import com.sitewhere.spi.SiteWhereException;

/**
 * Controller for site operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/flights")
public class AirTrafficController {

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Flight> listFlights() throws SiteWhereException {
		return AirTraffic.getInstance().getFlights();
	}
}