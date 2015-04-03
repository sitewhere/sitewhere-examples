/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.airtraffic.client;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;

import com.sitewhere.rest.client.SiteWhereClient;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.search.DeviceAssignmentSearchResults;
import com.sitewhere.spi.SiteWhereException;

/**
 * Adds a few methods to the existing client that are scheduled to be added for the 1.0.3
 * release.
 * 
 * @author Derek
 */
public class SiteWhereClientExt extends SiteWhereClient {

	public SiteWhereClientExt() {
	}

	public SiteWhereClientExt(String url, String username, String password) {
		super(url, username, password);
	}

	public SiteWhereClientExt(String url, String username, String password, int connectTimeoutMs) {
		super(url, username, password, connectTimeoutMs);
	}

	/**
	 * Get Site by unique tokenFOs
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public Site getSiteByToken(String token) throws SiteWhereException {
		Map<String, String> vars = new HashMap<String, String>();
		return sendRest(getBaseUrl() + "sites/" + token, HttpMethod.GET, null, Site.class, vars);
	}

	/**
	 * List all assets in a given asset module that meet the given criteria.
	 * 
	 * @param moduleId
	 * @return
	 * @throws SiteWhereException
	 */
	public HardwareAssetSearchResults getAssetsByModuleId(String moduleId, String criteria)
			throws SiteWhereException {
		Map<String, String> vars = new HashMap<String, String>();
		String url = "assets/" + moduleId;
		if ((criteria != null) && (criteria.length() > 0)) {
			url += "?criteria=" + criteria;
		}
		return sendRest(getBaseUrl() + url, HttpMethod.GET, null, HardwareAssetSearchResults.class, vars);
	}

	/**
	 * List all assignments for a site.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public DeviceAssignmentSearchResults listAssignmentsForSite(String token) throws SiteWhereException {
		Map<String, String> vars = new HashMap<String, String>();
		return sendRest(getBaseUrl() + "sites/" + token + "/assignments?includeDevice=true", HttpMethod.GET,
				null, DeviceAssignmentSearchResults.class, vars);
	}
}