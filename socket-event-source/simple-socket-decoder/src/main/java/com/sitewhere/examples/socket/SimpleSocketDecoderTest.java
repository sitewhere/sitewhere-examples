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
 * Sends test messages to a SiteWhere server socket.
 * 
 * @author Derek
 *
 */
public class SimpleSocketDecoderTest {

	/** Port on which the server socket is listening */
	private static final int PORT = 8585;

	@Test
	public void sendAll() throws Exception {
		sendGLL();
		sendMSS();
	}

	@Test
	public void sendGLL() throws Exception {
		Socket socket = new Socket("localhost", PORT);
		byte[] encoded = "$GPGLL,4916.45,N,12311.12,W,225444,A,*1D".getBytes();
		socket.getOutputStream().write(encoded);
		socket.getOutputStream().flush();
		socket.getOutputStream().close();
		socket.close();
	}

	@Test
	public void sendMSS() throws Exception {
		Socket socket = new Socket("localhost", PORT);
		byte[] encoded = "$GPMSS,55,27,318.0,100,*66".getBytes();
		socket.getOutputStream().write(encoded);
		socket.getOutputStream().flush();
		socket.getOutputStream().close();
		socket.close();
	}
}