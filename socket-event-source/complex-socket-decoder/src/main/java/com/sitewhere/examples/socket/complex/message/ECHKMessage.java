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
 * Corresponds to the ECHK message for Laipac devices.
 * 
 * @author Derek
 */
public class ECHKMessage extends LaipacMessage {

	/** Unit id */
	private String unitId;

	/** Sequence number */
	private Long sequenceNumber;

	public ECHKMessage() {
		super(LaipacCommandType.ECHK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.ee.devices.laipac.message.LaipacMessage#parse(java.lang.String[])
	 */
	@Override
	public void parse(String[] parts) throws SiteWhereException {
		this.unitId = parts[1];
		this.sequenceNumber = parseLong(parts[2]);
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
		builder.append(printString(getUnitId()));
		builder.append(',');
		builder.append(printLong(getSequenceNumber()));
		builder.append(LaipacMessageParser.CHECKSUM_DELIMITER);
		builder.append(getChecksum());
		return builder.toString();
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public Long getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
}