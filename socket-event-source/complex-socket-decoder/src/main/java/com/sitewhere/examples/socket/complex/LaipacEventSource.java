/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.socket.complex;

import org.apache.log4j.Logger;

import com.sitewhere.device.communication.InboundEventSource;
import com.sitewhere.examples.socket.complex.message.LaipacContext;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IInboundEventSource;

/**
 * Implementation of {@link IInboundEventSource} that reads messages in the form of
 * {@link LaipacContext} (usually produced by {@link LaipacEventReceiver}) and decodes
 * them into SiteWhere events and submits them for processing.
 * 
 * @author Derek
 */
public class LaipacEventSource extends InboundEventSource<LaipacContext> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(LaipacEventSource.class);

	/** Event receiver */
	private LaipacEventReceiver receiver;

	public LaipacEventSource() {
		receiver = new LaipacEventReceiver();
		getInboundEventReceivers().add(receiver);
		setDeviceEventDecoder(new LaipacEventDecoder());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.communication.InboundEventSource#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Starting Laipac inbound event source...");
		super.start();
	}

	/**
	 * Setter for port. (Injected by Spring bean)
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		receiver.setPort(port);
	}
}