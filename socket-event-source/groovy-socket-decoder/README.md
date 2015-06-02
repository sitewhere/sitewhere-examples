Groovy Socket Decoder Example
=============================
This example shows how to set up a SiteWhere **socket-event-source** which listens
on a server socket and hands off processing to a [Groovy] (http://www.groovy-lang.org/)
script which decodes the messages into SiteWhere events.

Configuration
-------------
In the **sitewhere-server.xml** file, the following XML declares the socket event source
which will listen on localhost port 8585 and hand off decoding the payload to a Groovy
script:

```XML
	<!-- Event source for processing custom messages from a socket -->
	<sw:socket-event-source port="8585" numThreads="10" sourceId="socket">
		<sw:read-all-interaction-handler-factory/>
		<sw:groovy-event-decoder scriptPath="decoder.groovy"/>
	</sw:socket-event-source>
```

In this case, the binary payload is handed to a script called **decoder.groovy**
which will be resolved from the **/conf/sitewhere/groovy** folder under SiteWhere root.
In order to use the **groovy-event-decoder**, you must add the following 
declaration in the **globals** section as well:

```XML
	<sw:globals>
		<sw:groovy-configuration debug="true" verbose="true"/>
	</sw:globals>
```
With this configuration, each time a client connects to the server socket on port 8585,
the data sent is routed through the Groovy script which generates SiteWhere events. An
example decoder script is shown below:

```Java
	import com.sitewhere.rest.model.device.communication.*;
	import com.sitewhere.rest.model.device.event.request.*;
	import com.sitewhere.spi.device.event.request.*;
	
	// Sanity-check payload.
	def message = new String(payload);
	def parts = message.split(",");
	if (parts.length < 2) {
	  logger.error("Invalid parameters")
	  return;
	}
	
	// Parse type and hardware id.
	def type = parts[0]
	def hwid = parts[1]
	
	// Handle location event in the form LOC,HWID,LAT,LONG
	if ("LOC".equals(type)) {
	  if (parts.length < 4) {
	    logger.error("Invalid location parameters")
	    return
	  }
	  
	  def decoded = new DecodedDeviceRequest<IDeviceLocationCreateRequest>()
	  decoded.setHardwareId(hwid);
	  
	  // Create a location object from the parsed data and added it to the list of decoded events.
	  def location = new DeviceLocationCreateRequest()
	  location.setLatitude(Double.parseDouble(parts[2]))
	  location.setLongitude(Double.parseDouble(parts[3]))
	  location.setElevation(0.0)
	  location.setEventDate(new java.util.Date())
	  decoded.setRequest(location)
	  
	  events.add(decoded);
	}
```

The script expects an input in the form of **LOC,XXXXXXXX,33.7550,-84.3900**, where the
first part is **LOC** to indicate a location packet, the second is the unique hardware
id for the device, and the third and fourth parts are latitude and longitude. The binary
payload is converted to a String and split as comma-separated data. The remaining code
does some sanity checking, then creates a [DecodedDeviceRequest] (http://docs.sitewhere.org/current/apidocs/com/sitewhere/rest/model/device/communication/DecodedDeviceRequest.html) 
which wraps a [DeviceLocationCreateRequest] (http://docs.sitewhere.org/current/apidocs/com/sitewhere/rest/model/device/event/request/DeviceLocationCreateRequest.html).
The resulting object is added to the **events** variable which is used by SiteWhere to
pass new events into the system to be stored and forwarded.

