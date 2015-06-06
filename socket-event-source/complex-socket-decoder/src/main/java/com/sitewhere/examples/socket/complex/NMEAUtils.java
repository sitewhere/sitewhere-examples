/* 
 * Copyright (C) SiteWhere, LLC - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package com.sitewhere.examples.socket.complex;

/**
 * Utility methods for parsing NMEA messages.
 * 
 * @author Derek
 */
public class NMEAUtils {

	/**
	 * Generates the checksum for an NMEA sentence.
	 * 
	 * @param input
	 * @return
	 */
	public static String getChecksum(String input) {
		int checksum = 0;
		if (input.startsWith("$")) {
			input = input.substring(1);
		}

		int end = input.indexOf('*');
		if (end == -1) {
			end = input.length();
		}
		for (int i = 0; i < end; i++) {
			checksum = checksum ^ input.charAt(i);
		}
		String hex = Integer.toHexString(checksum);
		if (hex.length() == 1) {
			hex = "0" + hex;
		}
		return hex.toUpperCase();
	}
}