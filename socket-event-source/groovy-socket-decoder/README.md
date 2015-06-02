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
		<sw:groovy-event-decoder scriptPath="binaryDecoder.groovy"/>
	</sw:socket-event-source>
```

In this case, the binary payload is handed to a script called **binaryDecoder.groovy**
which will be resolved from the **/conf/sitewhere/groovy** folder under SiteWhere root.
In order to use the **groovy-event-decoder**, you must also add the following 
declaration in the **globals** section as well:

```XML
	<sw:globals>
		<sw:groovy-configuration debug="true" verbose="true"/>
	</sw:globals>
```

