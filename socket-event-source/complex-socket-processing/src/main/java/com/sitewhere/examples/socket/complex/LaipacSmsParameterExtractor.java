/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.socket.complex;

import com.sitewhere.device.communication.sms.SmsParameters;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.communication.ICommandDeliveryParameterExtractor;

/**
 * Implementation of {@link ICommandDeliveryParameterExtractor} that populates
 * {@link SmsParameters} from metadata stored with the Laipac device.
 * 
 * @author Derek
 */
public class LaipacSmsParameterExtractor implements ICommandDeliveryParameterExtractor<SmsParameters> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.ICommandDeliveryParameterExtractor#
	 * extractDeliveryParameters(com.sitewhere.spi.device.IDeviceNestingContext,
	 * com.sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.command.IDeviceCommandExecution)
	 */
	@Override
	public SmsParameters extractDeliveryParameters(IDeviceNestingContext nesting,
			IDeviceAssignment assignment, IDeviceCommandExecution execution) throws SiteWhereException {
		SmsParameters sms = new SmsParameters();
		String phone = nesting.getGateway().getMetadata(ILaipacConstants.META_SIM_PHONE_NUMBER);
		sms.setSmsPhoneNumber(phone);
		return sms;
	}
}