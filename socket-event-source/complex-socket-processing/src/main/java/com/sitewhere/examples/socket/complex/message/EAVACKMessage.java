/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.socket.complex.message;

import com.sitewhere.spi.SiteWhereException;

/**
 * Corresponds to the EAVACK message for Laipac devices.
 * 
 * @author Derek
 */
public class EAVACKMessage extends LaipacMessage {

	/** ACK code */
	private String ackCode;

	/** ACK checksum */
	private String ackChecksum;

	public EAVACKMessage() {
		super(LaipacCommandType.EAVACK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.ee.devices.laipac.message.LaipacMessage#parse(java.lang.String[])
	 */
	@Override
	public void parse(String[] parts) throws SiteWhereException {
		this.ackCode = parts[1];
		this.ackChecksum = parts[2];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.ee.devices.laipac.message.LaipacMessage#buildSentence()
	 */
	@Override
	public String buildSentence() throws SiteWhereException {
		StringBuilder builder = new StringBuilder();
		builder.append(LaipacMessageParser.COMMAND_PREFIX);
		builder.append(getType().toString());
		builder.append(',');
		builder.append(printString(getAckCode()));
		builder.append(',');
		builder.append(printString(getAckChecksum()));
		builder.append(LaipacMessageParser.CHECKSUM_DELIMITER);
		builder.append(getChecksum());
		return builder.toString();
	}

	public String getAckCode() {
		return ackCode;
	}

	public void setAckCode(String ackCode) {
		this.ackCode = ackCode;
	}

	public String getAckChecksum() {
		return ackChecksum;
	}

	public void setAckChecksum(String ackChecksum) {
		this.ackChecksum = ackChecksum;
	}
}