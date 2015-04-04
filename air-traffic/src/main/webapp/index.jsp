<!DOCTYPE html>
<html>
<head>
<title>Air Traffic Example</title>
<script src="${pageContext.request.contextPath}/scripts/jquery-1.10.2.min.js"></script>
<script src="${pageContext.request.contextPath}/scripts/kendo.web.min.js"></script>
<script src="${pageContext.request.contextPath}/scripts/bootstrap.min.js"></script>
<script src="https://maps.googleapis.com/maps/api/js?sensor=false"></script>
<link href="${pageContext.request.contextPath}/css/kendo.common.min.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/css/kendo.bootstrap.min.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"
	media="screen">
</head>

<style>
body {
	font-family: "Calibri", "Khmer UI";
}

.map-canvas {
	height: 100%;
	margin: 0;
	padding: 0;
}
</style>

<body>
	<div id="map" style="width: 100%; height: 100%; position: absolute;"></div>
</body>

<script type="text/javascript">
	/** Google map */
	var map;

	/** Map of markers by assignment token */
	var markers = [];

	/** Symbols used for planes */
	var symbols = [];

	$(document).ready(function() {
		var mapCenter = new google.maps.LatLng(39.2416975, -98.1939341);
		var options = {
			zoom : 5,
			center : mapCenter,
			mapTypeId : google.maps.MapTypeId.ROADMAP,
		}
		map = new google.maps.Map(document.getElementById("map"), options);
		setInterval(loadFlights, 1000);
	});

	/** Load flight information from AJAX call */
	function loadFlights() {
		jQuery.ajax({
			'type' : 'GET',
			'url' : "${pageContext.request.contextPath}/api/flights",
			'contentType' : 'application/json',
			'success' : onFlightsSuccess,
			'error' : onFlightsFail
		});
	}

	/** Called on successful flights load request */
	function onFlightsSuccess(flights, status, jqXHR) {
		for (var i = 0; i < flights.length; i++) {
			createOrUpdateMarkerForFlight(flights[i]);
		}
	}

	/** Handle error on getting flights data */
	function onFlightsFail(jqXHR, textStatus, errorThrown) {
		alert('Unable to load flight data.');
	}

	/** Creates or updates the location marker for a flight */
	function createOrUpdateMarkerForFlight(flight) {
		var existing = markers[flight.assignmentToken];
		var symbol = symbols[flight.assignmentToken];
		if (existing && symbol) {
			symbol.rotation = flight.heading;
			symbol.fillColor = getPlaneColor(flight.fuelLevel);
			existing.setPosition(new google.maps.LatLng(flight.latitude, flight.longitude));
			existing.setIcon(symbol);
		} else {
			var plane =
					{
						path : 'M-2.934-22.215c0.006-3.686,5.562-3.686,5.558,0.103v15.467L24.277,6.37v5.714L2.728,4.992v11.56 l4.987,3.896v4.52l-7.688-2.39l-7.688,2.39v-4.52l4.936-3.896V4.992l-21.552,7.092V6.37L-2.934-6.645V-22.215z',
						fillColor : getPlaneColor(flight.fuelLevel),
						fillOpacity : 1,
						rotation : 0,
						scale : 1,
						strokeColor : '#000',
						strokeWeight : 2
					};
			var marker = new google.maps.Marker({
				position : new google.maps.LatLng(flight.latitude, flight.longitude),
				title : flight.planeModel,
				icon : plane,
				map : map
			});
			markers[flight.assignmentToken] = marker;
			symbols[flight.assignmentToken] = plane;
		}
	}

	/** Get plane color based on fuel level */
	function getPlaneColor(fuelLevel) {
		if (fuelLevel < 200) {
			return "#c00";
		} else if (fuelLevel < 400) {
			return "#ff0";
		}
		return "#0c0";
	}
</script>

</html>