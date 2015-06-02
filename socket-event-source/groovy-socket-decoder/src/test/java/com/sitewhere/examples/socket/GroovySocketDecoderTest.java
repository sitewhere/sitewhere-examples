/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.socket;

import java.net.Socket;

import org.junit.Test;

/**
 * Test case for Groovy socket decoder example.
 * 
 * @author Derek
 */
public class GroovySocketDecoderTest {

	/** Hardware id for test message */
	private static final String HARDWARE_ID = "74c79297-6197-47b2-85b1-ba140968f7c8";

	/** Port that server socket listens on */
	public static final int SERVER_SOCKET_PORT = 8585;

	@Test
	public void doGroovySocketDecoderTest() throws Exception {
		Socket socket = new Socket("localhost", SERVER_SOCKET_PORT);
		String message = "LOC," + HARDWARE_ID + ",33.7550,-84.3900";
		byte[] encoded = message.getBytes();
		socket.getOutputStream().write(encoded);
		socket.getOutputStream().flush();
		socket.getOutputStream().close();
		socket.close();
	}
}