/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.socket.complex.message;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information gathered from a conversation with a Laipac device.
 * 
 * @author Derek
 */
public class LaipacContext {

	/** Action requested */
	private Action action;

	/** Serial number */
	private String serialNumber;

	/** Device identifier */
	private String identifier;

	/** Sim card number */
	private String simCardNumber;

	/** Sim phone number */
	private String simPhoneNumber;

	/** List of messages */
	private List<LaipacMessage> messages = new ArrayList<LaipacMessage>();

	public LaipacContext(Action action, String serialNumber) {
		this.action = action;
		this.serialNumber = serialNumber;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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

	public List<LaipacMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<LaipacMessage> messages) {
		this.messages = messages;
	}

	/**
	 * Enumerates actions trigger by underlying messages.
	 * 
	 * @author Derek
	 */
	public static enum Action {

		/** Identity information was provided by device */
		IdentityProvided,

		/** A live waypoint reading was received from device */
		LiveWaypointReading,

		/** Device was turned off or put on charger */
		DeviceOffOrOnCharger;
	}
}