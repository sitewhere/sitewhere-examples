/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.socket.complex;

import com.sitewhere.device.communication.socket.SocketInboundEventReceiver;
import com.sitewhere.examples.socket.complex.message.LaipacContext;
import com.sitewhere.spi.device.communication.IInboundEventReceiver;

/**
 * Implementation of {@link IInboundEventReceiver} that reads {@link LaipacContext}
 * messages generated from processing on the underlying socket.
 * 
 * @author Derek
 */
public class LaipacEventReceiver extends SocketInboundEventReceiver<LaipacContext> {

	public LaipacEventReceiver() {
		setHandlerFactory(new S911BLInterationHandler.Factory());
	}
}