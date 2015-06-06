/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.socket.complex.message;

import com.sitewhere.examples.socket.complex.IDeviceMessage;
import com.sitewhere.examples.socket.complex.NMEAUtils;
import com.sitewhere.spi.SiteWhereException;

/**
 * Common base class for Laipac device messages.
 * 
 * @author Derek
 */
public abstract class LaipacMessage implements IDeviceMessage {

	/** Command type indicator */
	private LaipacCommandType type;

	/** Checksum value */
	private String checksum;

	public LaipacMessage(LaipacCommandType type) {
		this.type = type;
	}

	/**
	 * Parse a tokenized list of message parts into fields.
	 * 
	 * @param parts
	 * @throws SiteWhereException
	 */
	public abstract void parse(String[] parts) throws SiteWhereException;

	/**
	 * Build the NMEA sentence that describes this message.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public abstract String buildSentence() throws SiteWhereException;

	/**
	 * Update the checksum value based on current field contents.
	 * 
	 * @throws SiteWhereException
	 */
	public void updateChecksum() throws SiteWhereException {
		String sentence = buildSentence();
		String checksum = NMEAUtils.getChecksum(sentence);
		setChecksum(checksum);
	}

	/**
	 * Print a long value such that it can be added to a NMEA sentence.
	 * 
	 * @param value
	 * @return
	 * @throws SiteWhereException
	 */
	protected String printString(String value) throws SiteWhereException {
		if (value != null) {
			return value;
		}
		return "";
	}

	/**
	 * Parse a long field value.
	 * 
	 * @param part
	 * @return
	 * @throws SiteWhereException
	 */
	protected Long parseLong(String part) throws SiteWhereException {
		if (part.length() == 0) {
			return null;
		}
		try {
			return Long.parseLong(part);
		} catch (NumberFormatException e) {
			throw new SiteWhereException(e);
		}
	}

	/**
	 * Print a long value such that it can be added to a NMEA sentence.
	 * 
	 * @param value
	 * @return
	 * @throws SiteWhereException
	 */
	protected String printLong(Long value) throws SiteWhereException {
		if (value != null) {
			return String.valueOf(value);
		}
		return "";
	}

	/**
	 * Parse a double field value.
	 * 
	 * @param part
	 * @return
	 * @throws SiteWhereException
	 */
	protected Double parseDouble(String part) throws SiteWhereException {
		if (part.length() == 0) {
			return null;
		}
		try {
			return Double.parseDouble(part);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Print a double value such that it can be added to a NMEA sentence.
	 * 
	 * @param value
	 * @return
	 * @throws SiteWhereException
	 */
	protected String printDouble(Double value) throws SiteWhereException {
		if (value != null) {
			return String.valueOf(value);
		}
		return "";
	}

	/**
	 * Parse a boolean field value.
	 * 
	 * @param part
	 * @return
	 * @throws SiteWhereException
	 */
	protected Boolean parseBoolean(String part) throws SiteWhereException {
		if (part.length() == 0) {
			return null;
		}
		try {
			return "1".equals(part);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Print a boolean value such that it can be added to a NMEA sentence.
	 * 
	 * @param value
	 * @return
	 * @throws SiteWhereException
	 */
	protected String printBoolean(Boolean value) throws SiteWhereException {
		if (value != null) {
			return (value.booleanValue() ? "1" : "0");
		}
		return "";
	}

	public LaipacCommandType getType() {
		return type;
	}

	public void setType(LaipacCommandType type) {
		this.type = type;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
}