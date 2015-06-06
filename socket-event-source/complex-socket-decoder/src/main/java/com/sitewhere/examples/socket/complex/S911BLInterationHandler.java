/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.socket.complex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.examples.socket.complex.message.AVRMCMessage;
import com.sitewhere.examples.socket.complex.message.AVSYSMessage;
import com.sitewhere.examples.socket.complex.message.EAVACKMessage;
import com.sitewhere.examples.socket.complex.message.EAVSYSMessage;
import com.sitewhere.examples.socket.complex.message.ECHKMessage;
import com.sitewhere.examples.socket.complex.message.LaipacContext;
import com.sitewhere.examples.socket.complex.message.LaipacMessage;
import com.sitewhere.examples.socket.complex.message.LaipacMessageParser;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IInboundEventReceiver;
import com.sitewhere.spi.device.communication.socket.ISocketInteractionHandler;
import com.sitewhere.spi.device.communication.socket.ISocketInteractionHandlerFactory;

/**
 * Implementation of {@link ISocketInteractionHandler} that handles processing for the
 * Laipac S-911 BL device.
 * 
 * @author Derek
 */
public class S911BLInterationHandler implements ISocketInteractionHandler<LaipacContext> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(S911BLInterationHandler.class);

	/** Serial number provided by AVSYS */
	private String serialNumber;

	/** Unit identifier */
	private String unitId;

	/** Sim card number provided by EAVSYS */
	private String simCardNumber;

	/** Sim phone number provided by EAVSYS */
	private String simPhoneNumber;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.socket.ISocketInteractionHandler#process
	 * (java.net.Socket, com.sitewhere.spi.device.communication.IInboundEventReceiver)
	 */
	@Override
	public void process(Socket socket, IInboundEventReceiver<LaipacContext> receiver)
			throws SiteWhereException {
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String unparsed = null;
			LaipacMessageParser parser = new LaipacMessageParser();
			ObjectMapper json = new ObjectMapper();
			while ((unparsed = input.readLine()) != null) {
				if (unparsed.length() == 0) {
					continue;
				}
				try {
					LaipacMessage message = parser.parse(unparsed);
					String asJson = json.writeValueAsString(message);
					LOGGER.info(message.getType() + " " + asJson);
					switch (message.getType()) {
					case AVRMC: {
						handleAVRMC(socket, (AVRMCMessage) message, receiver);
						break;
					}
					case AVSYS: {
						handleAVSYS(socket, (AVSYSMessage) message);
						break;
					}
					case EAVSYS: {
						handleEAVSYS(socket, (EAVSYSMessage) message, receiver);
						break;
					}
					case ECHK: {
						handleECHK(socket, (ECHKMessage) message);
						break;
					}
					default: {
					}
					}
				} catch (SiteWhereException e) {
					LOGGER.error("Error parsing message.", e);
				}
			}
			input.close();
		} catch (IOException e) {
			throw new SiteWhereException("Exception processing request in socket interaction handler.", e);
		} catch (Throwable e) {
			throw new SiteWhereException("Unhandled exception in socket interaction handler.", e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					LOGGER.error("Unable to close client socket.", e);
				}
			}
		}
	}

	/**
	 * Handle AVRMC message.
	 * 
	 * @param socket
	 * @param avrmc
	 * @param eventSource
	 * @throws IOException
	 */
	protected void handleAVRMC(Socket socket, AVRMCMessage avrmc,
			IInboundEventReceiver<LaipacContext> receiver) throws IOException {
		switch (avrmc.getStatus()) {
		case RealTime_AckRequired:
		case InvaldGPS_AckRequired:
		case GPSPoweredDown_AckRequired:
		case Repeat_AckRequired: {
			EAVACKMessage ack = new EAVACKMessage();
			ack.setAckCode(String.valueOf(avrmc.getEventCode().getCode()));
			ack.setAckChecksum(avrmc.getChecksum());
			ack.updateChecksum();
			String sentence = ack.buildSentence();
			LOGGER.info("Writing: " + sentence);
			socket.getOutputStream().write(sentence.getBytes());
			socket.getOutputStream().write("\r\n".getBytes());
			socket.getOutputStream().flush();
		}
		case RealTime: {
			switch (avrmc.getEventCode()) {
			case RegularReport: {
				LaipacContext context =
						new LaipacContext(LaipacContext.Action.LiveWaypointReading, serialNumber);
				context.setIdentifier(unitId);
				context.setSimCardNumber(simCardNumber);
				context.setSimPhoneNumber(simPhoneNumber);
				context.getMessages().add(avrmc);
				receiver.onEventPayloadReceived(context);
				break;
			}
			case UnitOffOrOnCharger: {
				LaipacContext context =
						new LaipacContext(LaipacContext.Action.DeviceOffOrOnCharger, serialNumber);
				context.setIdentifier(unitId);
				context.setSimCardNumber(simCardNumber);
				context.setSimPhoneNumber(simPhoneNumber);
				context.getMessages().add(avrmc);
				receiver.onEventPayloadReceived(context);
			}
			default: {
				// Ignore unhandled conditions.
			}
			}
		}
		default: {
			// Ignore anything other than live data.
		}
		}
	}

	/**
	 * Handle AVSYS message.
	 * 
	 * @param socket
	 * @param avrmc
	 * @throws IOException
	 */
	protected void handleAVSYS(Socket socket, AVSYSMessage avsys) throws IOException {
		this.serialNumber = avsys.getSerialNumber();
		this.unitId = avsys.getUnitId();
	}

	/**
	 * Handle EAVSYS message.
	 * 
	 * @param socket
	 * @param eavsys
	 * @param eventSource
	 * @throws IOException
	 */
	protected void handleEAVSYS(Socket socket, EAVSYSMessage eavsys,
			IInboundEventReceiver<LaipacContext> receiver) throws IOException {
		this.simCardNumber = eavsys.getSimCardNumber();
		this.simPhoneNumber = eavsys.getSimPhoneNumber();

		LaipacContext context = new LaipacContext(LaipacContext.Action.IdentityProvided, serialNumber);
		context.setIdentifier(unitId);
		context.setSimCardNumber(simCardNumber);
		context.setSimPhoneNumber(simPhoneNumber);
		context.getMessages().add(eavsys);
		receiver.onEventPayloadReceived(context);
	}

	/**
	 * Handle an ECHK message by sending it back as an ack.
	 * 
	 * @param socket
	 * @param echk
	 * @throws IOException
	 */
	protected void handleECHK(Socket socket, ECHKMessage echk) throws IOException {
		echk.updateChecksum();
		String sentence = echk.buildSentence();
		LOGGER.info("Writing: " + sentence);
		socket.getOutputStream().write(sentence.getBytes());
		socket.getOutputStream().write("\r\n".getBytes());
		socket.getOutputStream().flush();
	}

	/**
	 * Produces instances of {@link S911BLInterationHandler}.
	 * 
	 * @author Derek
	 */
	public static class Factory implements ISocketInteractionHandlerFactory<LaipacContext> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sitewhere.spi.device.communication.socket.ISocketInteractionHandlerFactory
		 * #newInstance()
		 */
		@Override
		public ISocketInteractionHandler<LaipacContext> newInstance() {
			return new S911BLInterationHandler();
		}
	}
}