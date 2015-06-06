/* 
 * Copyright (C) SiteWhere, LLC - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package com.sitewhere.examples.socket.complex;

import com.sitewhere.spi.SiteWhereException;

/**
 * Implementation of {@link IDeviceMessageParser} that parses a {@link String} message and
 * allows a subclass to create an {@link IDeviceMessage} instance.
 * 
 * @author Derek
 */
public abstract class DelimitedStringMessageParser implements IDeviceMessageParser<String, IDeviceMessage> {

	/** Default delimiter for breaking string */
	private static final String DEFAULT_DELIMITER = ",";

	/** Delimited used for parsing message */
	private String delimiter = DEFAULT_DELIMITER;

	public DelimitedStringMessageParser() {
	}

	public DelimitedStringMessageParser(String delimiter) {
		setDelimiter(delimiter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.ee.devices.message.IDeviceMessageParser#parse(java.lang.Object)
	 */
	@Override
	public IDeviceMessage parse(String input) throws SiteWhereException {
		String[] parts = input.split(getDelimiter());
		return createMessage(parts);
	}

	/**
	 * Create a message from the parts after string has been split.
	 * 
	 * @param parts
	 * @return
	 * @throws SiteWhereException
	 */
	protected abstract IDeviceMessage createMessage(String[] parts) throws SiteWhereException;

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
}