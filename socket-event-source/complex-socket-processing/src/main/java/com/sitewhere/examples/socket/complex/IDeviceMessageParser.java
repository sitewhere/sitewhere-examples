/* 
 * Copyright (C) SiteWhere, LLC - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package com.sitewhere.examples.socket.complex;

import com.sitewhere.spi.SiteWhereException;

/**
 * Interface for entity that parses an input into an {@link IDeviceMessage}.
 * 
 * @author Derek
 * 
 * @param <A>
 * @param <B>
 */
public interface IDeviceMessageParser<A, B extends IDeviceMessage> {

	/**
	 * Parse the given input into the message container.
	 * 
	 * @param input
	 * @return
	 * @throws SiteWhereException
	 */
	public B parse(A input) throws SiteWhereException;
}