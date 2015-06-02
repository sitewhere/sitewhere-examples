Groovy Socket Decoder Example
=============================
This example shows how to set up a SiteWhere **socket-event-source** which listens
on a server socket and hands off processing to a [Groovy] (http://www.groovy-lang.org/)
script which decodes the messages into SiteWhere events.

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
