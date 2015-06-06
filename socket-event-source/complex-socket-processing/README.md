Complex Socket Processing Example
=================================
This example shows how to set up a SiteWhere event source which handles
more complicated socket interactions where a dialog with the device is required to
access the desired information.

Interactions
------------
The following is an overview of the interactions between the remote device
(a [Laipac S911] (http://www.laipac.com/bracelet_locator_gps.htm)) and
the SiteWhere event receiver:

1. Initial socket connection established.
2. Device sends **$AVSYS,99999999,V1.17,SN0000103,32768*16**, which includes information
   about the device such as serial number, firmware version, etc.
3. Device sends **$EAVSYS,99999999,12345678901234567890,9057621228,,,*0B**, which includes
   extended device information such as SIM card phone number and owner name.
4. Device sends sends **$ECHK, Unit ID, Seq No*CHKSUM** to request that SiteWhere
   send an **$ECHK** to verify handshaking and registration.
5. SiteWhere sends **$ECHK, Unit ID, Seq No*CHKSUM** to acknowledge that identifying
   information has been received.
6. Device sends **$AVRMC,99999999,144811,A,4351.3789,N,07923.4712,W,0.00,153.45,091107,0,123,161,1,1,1500,1700*64**, 
   which includes information about speed, course, location, battery voltage, etc. No acknowledgement is
   expected due to 'A' status code.
7. Device sends **$AVRMC,99999999,144811,a,4351.3789,N,07923.4712,W,0.00,153.45,091107,0,123,161,1,1,1500,1700*64**, 
   which includes information about speed, course, location, battery voltage, etc. An acknowledgement is
   expected due to 'a' status code.
8. SiteWhere sends **$EAVACK, ACK_Code, ACK_SUM * CHKSUM** to acknowledge that the location
   data has been received.
9. SiteWhere terminates socket and listens for new connections.

Configuration
-------------
In the **sitewhere-server.xml** file, the following XML declares an event source
that is completely defined by an external Spring Bean with name "customEventSource":

```XML
	<!-- Adds reference to custom event source for complex socket processing -->
	<sw:event-source ref="customEventSource"/>
```

The Spring bean that implements the event source is declared outside the of the
*<sw:configuration>* block. It creates an instance of our custom event source class
and sets properties for configuring it.

```XML
	<!-- Spring bean definition for custom event source to process complex socket input -->
	<bean name="customEventSource" class="com.sitewhere.examples.socket.complex.LaipacEventSource">
		<property name="sourceId" value="laipac"/>
		<property name="port" value="8585"/>
	</bean>
```

Building and Running the Example
--------------------------------
To build this example, run the command

	mvn clean install
	
in the root folder of the example. Once the build has completed, a **complex-socket-decoder-x.y.z.jar** file
will be located in the **target** folder. Copy the jar into your SiteWhere **/webapps/sitewhere/WEB-INF/lib**
folder. Also copy the configuration file from the **config/sitewhere** folder in the example into
your **/conf/sitewhere/** folder to replace the existing configuration. You may want to create a backup
of the original configuration beforehand. Start SiteWhere and it should be ready to process messages
sent to port 8585 in the expected format.

For an example of how to send data to the server socket from java, look at the 
[JUnit test case] (https://github.com/sitewhere/sitewhere-examples/blob/sitewhere-1.0.4/socket-event-source/complex-socket-processing/src/test/java/com/sitewhere/examples/socket/complex/ComplexSocketProcessingTest.java). Running the tests will send test messages to the server.
The SiteWhere server logs will reflect the messages being parsed and the dialog going on between
the device and the server.

Understanding How it Works
--------------------------
In order to create a custom event source, [LaipacEventSource] (https://github.com/sitewhere/sitewhere-examples/blob/sitewhere-1.0.4/socket-event-source/complex-socket-processing/src/main/java/com/sitewhere/examples/socket/complex/LaipacEventSource.java) extends the existing [InboundEventSource] (https://github.com/sitewhere/sitewhere/blob/master/sitewhere-core/src/main/java/com/sitewhere/device/communication/InboundEventSource.java)
base class and sets up a custom event receiver and event decoder. The
[LaipacEventReceiver] (https://github.com/sitewhere/sitewhere-examples/blob/sitewhere-1.0.4/socket-event-source/complex-socket-processing/src/main/java/com/sitewhere/examples/socket/complex/LaipacEventReceiver.java) class extends the 
[SocketInboundEventReceiver] (https://github.com/sitewhere/sitewhere/blob/master/sitewhere-core/src/main/java/com/sitewhere/device/communication/socket/SocketInboundEventReceiver.java) class and sets up a custom
[ISocketInteractionHandler] (https://github.com/sitewhere/sitewhere/blob/master/sitewhere-client/src/main/java/com/sitewhere/spi/device/communication/socket/ISocketInteractionHandler.java) that handles the dialog between SiteWhere and the device.

The [S911BLInterationHandler] (https://github.com/sitewhere/sitewhere-examples/blob/sitewhere-1.0.4/socket-event-source/complex-socket-processing/src/main/java/com/sitewhere/examples/socket/complex/S911BLInterationHandler.java) class 
handles all of the stateful logic in interacting with the device. It reads binary information from the socket, parsing
it into messages that can be decoded into meaningful information. It also sends binary information back to the device
in order to acknowledge receipt of information and request that specific data be sent. Note that the socket interaction
handler is produced by a factory and a new instance is created per request so that state may be kept for a particlar
session. This is required, since information often has to be assembled from many messages as the result of an extended
interaction. As messages are decoded from the information, they are fed back to the event receiver, which in turn
passes them to the 
[LaipacEventDecoder] (https://github.com/sitewhere/sitewhere-examples/blob/sitewhere-1.0.4/socket-event-source/complex-socket-processing/src/main/java/com/sitewhere/examples/socket/complex/LaipacEventDecoder.java) to be converted into SiteWhere events.

