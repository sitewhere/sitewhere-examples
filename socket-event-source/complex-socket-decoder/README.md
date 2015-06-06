Complex Socket Processing Example
=================================
This example shows how to set up a SiteWhere event source which handles
more complicated socket interactions where a dialog with the device is required to
access the desired information.

Interactions
------------
The following is an overview of the interactions between the remote device and
the SiteWhere event receiver:

1. Initial socket connection established.
2. Device sends **$AVSYS,99999999,V1.17,SN0000103,32768*16**, which includes information
   about the device such as serial number, firmware version, etc.
3. Device sends **$EAVSYS,99999999,12345678901234567890,9057621228,,,*0B**, which includes
   extended device information such as SIM card phone number and owner name.
4. SiteWhere sends **$ECHK, Unit ID, Seq No*CHKSUM** to acknowledge that identifying
   information has been received.
5. Device sends **$AVRMC,99999999,144811,A,4351.3789,N,07923.4712,W,0.00,153.45,091107,A,,161,1*64**, 
   which includes information about speed, course, location, battery voltage, etc.
6. SiteWhere sends **$EAVACK, ACK_Code, ACK_SUM * CHKSUM** to acknowledge that the location
   data has been received.
7. SiteWhere terminates socket and listens for new connections.

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

Understanding How it Works
--------------------------
