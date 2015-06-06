/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.socket.complex;

/**
 * Unique names used to identify Laipac measurements.
 * 
 * @author Derek
 */
public interface ILaipacConstants {

	/***************************/
	/** REGISTRATION METADATA **/
	/***************************/

	/** Constant for SIM card number metadata */
	public static final String META_SIM_CARD_NUMBER = "sim_card_number";

	/** Constant for SIM phone number metadata */
	public static final String META_SIM_PHONE_NUMBER = "sim_phone_number";

	/******************/
	/** MEASUREMENTS **/
	/******************/

	/** Constant for speed measurement */
	public static final String MX_SPEED = "speed";

	/** Constant for course measurement */
	public static final String MX_COURSE = "course";

	/** Constant for battery voltage measurement */
	public static final String MX_BATTERY_VOLTAGE = "battery.voltage";

	/** Constant for current mileage measurement */
	public static final String MX_CURRENT_MILEAGE = "current.mileage";

	/************/
	/** ALERTS **/
	/************/

	/** Constant for device off/charging alert */
	public static final String ALERT_DEVICE_OFF = "device.off.or.charging";
}