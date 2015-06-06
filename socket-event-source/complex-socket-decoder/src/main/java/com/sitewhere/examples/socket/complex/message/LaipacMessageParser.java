/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.socket.complex.message;

import com.sitewhere.examples.socket.complex.IDeviceMessageParser;
import com.sitewhere.spi.SiteWhereException;

/**
 * Implementation of {@link IDeviceMessageParser} that parses Strings into subclasses of
 * {@link LaipacMessage}.
 * 
 * @author Derek
 */
public class LaipacMessageParser implements IDeviceMessageParser<String, LaipacMessage> {

	/** Delimits checksum value */
	public static final String CHECKSUM_DELIMITER = "*";

	/** Delimits checksum value */
	public static final String CHECKSUM_REGEX = "\\*";

	/** Delimits command start */
	public static final String COMMAND_PREFIX = "$";

	/** Delimits field values */
	public static final String FIELD_DELIMITER = ",";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.ee.devices.message.IDeviceMessageParser#parse(java.lang.Object)
	 */
	@Override
	public LaipacMessage parse(String input) throws SiteWhereException {
		String[] csParts = input.split(CHECKSUM_REGEX);
		if (csParts.length < 2) {
			throw new SiteWhereException("Checksum delimiter not found.");
		}
		String command = csParts[0];
		String checksum = csParts[1];

		// Verify valid command.
		if (!command.startsWith(COMMAND_PREFIX)) {
			throw new SiteWhereException("Command should start with '" + COMMAND_PREFIX + "'. Received: "
					+ command);
		}
		command = command.substring(1);

		// Split into parts and validate.
		String[] parts = command.split(FIELD_DELIMITER);
		if (parts.length < 1) {
			throw new SiteWhereException("Invalid command: " + command);
		}
		try {
			LaipacCommandType type = LaipacCommandType.valueOf(parts[0]);
			LaipacMessage message = parseMessage(type, parts);
			message.setChecksum(checksum);
			return message;
		} catch (IllegalArgumentException e) {
			throw new SiteWhereException("Invalid command type: " + parts[0]);
		}
	}

	/**
	 * Parse the command parts into a {@link LaipacMessage}.
	 * 
	 * @param type
	 * @param parts
	 * @return
	 * @throws SiteWhereException
	 */
	protected LaipacMessage parseMessage(LaipacCommandType type, String[] parts) throws SiteWhereException {
		switch (type) {
		case AVRMC: {
			return buildAVRMC(parts);
		}
		case AVRSP: {
			return buildAVRSP(parts);
		}
		case AVSYS: {
			return buildAVSYS(parts);
		}
		case EAVSYS: {
			return buildEAVSYS(parts);
		}
		case ECHK: {
			return buildECHK(parts);
		}
		default: {
			throw new SiteWhereException("Unhandled message type: " + type.toString());
		}
		}
	}

	/**
	 * Build the AVSYS message.
	 * 
	 * @param parts
	 * @return
	 * @throws SiteWhereException
	 */
	protected AVSYSMessage buildAVSYS(String[] parts) throws SiteWhereException {
		AVSYSMessage message = new AVSYSMessage();
		message.parse(parts);
		return message;
	}

	/**
	 * Build the AVRMC message.
	 * 
	 * @param parts
	 * @return
	 * @throws SiteWhereException
	 */
	protected AVRMCMessage buildAVRMC(String[] parts) throws SiteWhereException {
		AVRMCMessage message = new AVRMCMessage();
		message.parse(parts);
		return message;
	}

	/**
	 * Build the AVRSP message.
	 * 
	 * @param parts
	 * @return
	 * @throws SiteWhereException
	 */
	protected AVRSPMessage buildAVRSP(String[] parts) throws SiteWhereException {
		AVRSPMessage message = new AVRSPMessage();
		message.parse(parts);
		return message;
	}

	/**
	 * Build the ECHK message.
	 * 
	 * @param parts
	 * @return
	 * @throws SiteWhereException
	 */
	protected ECHKMessage buildECHK(String[] parts) throws SiteWhereException {
		ECHKMessage message = new ECHKMessage();
		message.parse(parts);
		return message;
	}

	/**
	 * Build the EAVSYS message.
	 * 
	 * @param parts
	 * @return
	 * @throws SiteWhereException
	 */
	protected EAVSYSMessage buildEAVSYS(String[] parts) throws SiteWhereException {
		EAVSYSMessage message = new EAVSYSMessage();
		message.parse(parts);
		return message;
	}
}