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
 * Corresponds to the EAVSYS message for Laipac devices.
 * 
 * @author Derek
 */
public class EAVSYSMessage extends LaipacMessage {

	/** Unit id */
	private String unitId;

	/** Sim card number */
	private String simCardNumber;

	/** Sim phone number */
	private String simPhoneNumber;

	/** Owner name */
	private String ownerName;

	/** Firmware version */
	private String firmwareVersion;

	public EAVSYSMessage() {
		super(LaipacCommandType.EAVSYS);
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
		this.simCardNumber = parts[2];
		this.simPhoneNumber = parts[3];
		this.ownerName = parts[4];
		this.firmwareVersion = parts[5];
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
		builder.append(printString(getSimCardNumber()));
		builder.append(',');
		builder.append(printString(getSimPhoneNumber()));
		builder.append(',');
		builder.append(printString(getOwnerName()));
		builder.append(',');
		builder.append(printString(getFirmwareVersion()));
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

	public String getSimCardNumber() {
		return simCardNumber;
	}

	public void setSimCardNumber(String simCardNumber) {
		this.simCardNumber = simCardNumber;
	}

	public String getSimPhoneNumber() {
		return simPhoneNumber;
	}

	public void setSimPhoneNumber(String simPhoneNumber) {
		this.simPhoneNumber = simPhoneNumber;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}
}