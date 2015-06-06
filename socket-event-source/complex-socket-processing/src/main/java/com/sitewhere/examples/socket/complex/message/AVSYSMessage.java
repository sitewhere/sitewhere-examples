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
 * Corresponds to the AVSYS message for Laipac devices.
 * 
 * @author Derek
 */
public class AVSYSMessage extends LaipacMessage {

	/** Unit id */
	private String unitId;

	/** Firmware version */
	private String firmwareVersion;

	/** Serial number */
	private String serialNumber;

	/** Memory size */
	private Long memorySize;

	public AVSYSMessage() {
		super(LaipacCommandType.AVSYS);
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
		this.firmwareVersion = parts[2];
		this.serialNumber = parts[3];
		this.memorySize = parseLong(parts[4]);
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
		builder.append(printString(getFirmwareVersion()));
		builder.append(',');
		builder.append(printString(getSerialNumber()));
		builder.append(',');
		builder.append(printLong(getMemorySize()));
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

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Long getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(Long memorySize) {
		this.memorySize = memorySize;
	}
}