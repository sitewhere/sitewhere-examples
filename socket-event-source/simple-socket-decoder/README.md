Simple Socket Decoder Example
=============================
This example shows how to set up a SiteWhere **socket-event-source** which listens
on a server socket and hands off processing to a custom decoder component. 

Configuration
-------------
In the **sitewhere-server.xml** file, the following XML declares the socket event source
which will listen on localhost port 8585:

```XML
	<!-- Event source for simple socket decoder example -->
	<sw:socket-event-source port="8585" numThreads="10" sourceId="socket">
		<sw:read-all-interaction-handler-factory/>
		<sw:event-decoder ref="simpleSocketDecoder"/>
	</sw:socket-event-source>
```

Note that this configuration spawns 10 threads that decode requests concurrently.
It also uses **read-all-interaction-handler-factory** which will read all of the 
content from the socket and hand off the result to the custom decoder. This 
configuration makes sense when there are no conversational aspects in the socket
interaction. The message is passed to a custom decoder declared in a Spring
bean as shown below:

```XML
	<!-- Spring bean definition for custom decoder to process socket input -->
	<bean name="simpleSocketDecoder" class="com.sitewhere.examples.socket.SimpleSocketDecoder">
		<!-- Should be a valid hardware id registered in the system -->
		<property name="hardwareId" value="74c79297-6197-47b2-85b1-ba140968f7c8"/>
	</bean>
```

In this example, the payloads being sent are standard [NMEA] (http://www.gpsinformation.org/dale/nmea.htm)
sentences for [geographic position] (http://www.gpsinformation.org/dale/nmea.htm#GLL) and
[beacon receiver status] (http://www.gpsinformation.org/dale/nmea.htm#MSS). Note that these messages
do not include the unique hardware id of the device sending the information. In the real world,
there will either be an initial message that identifies the device or the hardware id will be 
included in each message. In this example, the hardware id of the device is hard-coded. In order
to run the example, update the **value** attribute of the **hardwareId** property to a valid
device hardware id on your system.

Building and Running the Example
--------------------------------
To build this example, run the command

	mvn clean install
	
in the root folder of the example. Once the build has completed, a **simple-decoder-x.y.z.jar** file
will be located in the **target** folder. Copy the jar into your SiteWhere **/webapps/sitewhere/WEB-INF/lib**
folder. Also copy the configuration file from the **config/sitewhere** folder in the example into
your **/conf/sitewhere/** folder to replace the existing configuration. You may want to create a backup
of the original configuration beforehand. Start SiteWhere and it should be ready to process messages
sent to port 8585 in the expected format.

For an example of how to send data to the server socket from java, look at the 
[JUnit test case] (https://github.com/sitewhere/sitewhere-examples/blob/sitewhere-1.0.4/socket-event-source/simple-socket-decoder/src/main/java/com/sitewhere/examples/socket/SimpleSocketDecoderTest.java). Running the tests will send test messages to the server. To view the
data that has been posted to the device, open the SiteWhere administrative application and navigate to 
the current assignment for the device. New entries should appear under the **Locations** and **Measurements** 
tabs.

Understanding How it Works
--------------------------
In this example, very little code is needed since the socket interaction is very simple. The
**socket-event-source** takes care of setting up a server socket and handling multithreaded 
processing. The **read-all-interaction-handler-factory** takes care of reading data from the
socket and sending it to the custom decoder. In more complex cases, a custom interaction handler
can be used to allow for a back-and-forth exchange of messages between the device and the
system. In this example, the only custom code is the decoding of messages from the device into
SiteWhere events. This takes place in the [SimpleSocketDecoder] (https://github.com/sitewhere/sitewhere-examples/blob/sitewhere-1.0.4/socket-event-source/simple-socket-decoder/src/main/java/com/sitewhere/examples/socket/SimpleSocketDecoder.java), which takes a binary payload as input, converts it to a String, then
parses the String to extract data needed to create SiteWhere events.
