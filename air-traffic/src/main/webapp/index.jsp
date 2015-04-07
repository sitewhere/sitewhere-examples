<!DOCTYPE html>
<html>
<head>
<title>Air Traffic Example</title>
<script src="${pageContext.request.contextPath}/scripts/jquery-1.10.2.min.js"></script>
<script src="${pageContext.request.contextPath}/scripts/kendo.web.min.js"></script>
<script src="${pageContext.request.contextPath}/scripts/kendo.dataviz.min.js"></script>
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

.banner {
	position: fixed;
	top: 10px;
	left: 50%;
	margin-left: -250px;
}

.detail-popup {
	position: fixed;
	top: 200px;
	left: 50%;
	margin-left: -300px;
	width: 600px;
	height: 400px;
	background-color: #fff;
	border: 3px solid #000;
	display: none;
}

.plane-info {
	position: relative;
	height: 100px;
}

.tabs {
	height: 300px;
}

.header-image {
	float: left;
	width: 80px;
	height: 80px;
	margin: 10px;
	border: 1px solid #999;
	background-color: #eee;
}

.header-title {
	font-size: 18pt;
	font-weight: bold;
	padding-top: 8px;
}

.header-sub {
	font-size: 14pt;
	padding-top: 5px;
}

.header-label {
	font-weight: bold;
	min-width: 160px;
	display: inline-block;
	font-size: 12pt;
	padding: 6px 5px;
}

.header-value {
	font-size: 12pt;
}
</style>

<!-- Kendo UI template for popup header -->
<script type="text/x-kendo-tmpl" id="tpl-popup-header">
	<div class="header-image" style="background-image: url(#:assignment.associatedHardware.imageUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;"></div>
	<div class="header-title">Flight #:flight.flightNumber#</div>
	<div class="header-sub">#:flight.planeModel#</div>
</script>

<!-- Kendo UI template for flight details -->
<script type="text/x-kendo-tmpl" id="tpl-flight-details">
	<div style="min-height: 235px; padding-top: 10px;">
		<div>
			<span class="header-label">Departure Airport</span>
			<span class="header-value">#:flight.route.departureName# (#:flight.route.departureSymbol#)</span>
		</div>
		<div>
			<span class="header-label">Destination Airport</span>
			<span class="header-value">#:flight.route.destinationName# (#:flight.route.destinationSymbol#)</span>
		</div>
		<div>
			<span class="header-label">Latitude</span>
			<span class="header-value">#:flight.latitude#</span>
		</div>
		<div>
			<span class="header-label">Longitude</span>
			<span class="header-value">#:flight.longitude#</span>
		</div>
		<div>
			<span class="header-label">Altitude</span>
			<span class="header-value">#:flight.elevation# feet</span>
		</div>
		<div>
			<span class="header-label">Airspeed</span>
			<span class="header-value">#:flight.airspeed# MPH</span>
		</div>
		<div>
			<span class="header-label">Fuel Level</span>
			<span class="header-value">#:flight.fuelLevel# gallons</span>
		</div>
	</div>
</script>

<body>
	<div id="map" style="width: 100%; height: 100%; position: absolute;"></div>
	<img src="images/AirTrafficBanner.png" class="banner" />
	<div id="detail-popup" class="detail-popup">
		<div id="plane-info" class="plane-info"></div>
		<div id="tabs" class="tabs">
			<ul>
				<li class="k-state-active">Flight Details</li>
				<li>Air Speed History</li>
				<li>Fuel Level History</li>
			</ul>
			<div></div>
			<div>
				<div id="air-speed-graph" style="height: 240px;"></div>
			</div>
			<div>
				<div id="fuel-level-graph" style="height: 240px;"></div>
			</div>
		</div>
		<button type="button" style="position: absolute; top: 10px; right: 10px;" onclick="closePopup()">&times;</button>
	</div>
</body>

<script type="text/javascript">
	/** Google map */
	var map;

	/** Map of markers by assignment token */
	var markers = [];

	/** Symbols used for planes */
	var symbols = [];

	/** Most recent list of flights */
	var lastFlights = [];

	/** Reference to tab panel */
	var tabs;
	
	/** Air speed chart */
	var airSpeedChart;
	
	/** Fuel level chart */
	var fuelLevelChart;
	
	/** Assignment token for popup */
	var popupToken;

	$(document).ready(function() {
		var mapCenter = new google.maps.LatLng(39.2416975, -98.1939341);
		var options = {
			zoom : 5,
			center : mapCenter,
			mapTypeId : google.maps.MapTypeId.SATELLITE,
		}
		map = new google.maps.Map(document.getElementById("map"), options);

		/** Create the tab strip */
		tabs = $("#tabs").kendoTabStrip({
			animation : false,
		}).data("kendoTabStrip");

		airSpeedChart = $("#air-speed-graph").kendoChart({
			seriesDefaults : {
				type : "line"
			},
			series : [ {
				name : "Air Speed (MPH)",
				data : [],
				color : "#000099",
				markers : {
					visible : false
				},
				visibleInLegend: false,
			} ],
		}).data("kendoChart");

		fuelLevelChart = $("#fuel-level-graph").kendoChart({
			seriesDefaults : {
				type : "line"
			},
			series : [ {
				name : "Fuel Level (Gallons)",
				data : [],
				color : "#009900",
				markers : {
					visible : false
				},
				visibleInLegend: false,
			} ],
		}).data("kendoChart");

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
		if (popupToken) {
			doShowAssignment(popupToken);
		}
	}

	/** Called on successful flights load request */
	function onFlightsSuccess(flights, status, jqXHR) {
		lastFlights = [];
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
		lastFlights[flight.assignmentToken] = flight;
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
				map : map,
				assignmentToken : flight.assignmentToken
			});

			// Show popup when plane icon is clicked.
			google.maps.event.addListener(marker, 'click', function() {
				doShowAssignment(this.assignmentToken);
			});

			// Store markers and symbols by assignment token.
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

	/** Show the popup with assignment information */
	function doShowAssignment(token) {
		popupToken = token;
		jQuery.ajax({
			'type' : 'GET',
			'url' : "/sitewhere/api/assignments/" + token,
			'headers' : {
				"Authorization" : "Basic " + btoa("admin:password")
			},
			'contentType' : 'application/json',
			'success' : onAssignmentSuccess,
			'error' : onAssignmentFail
		});
		jQuery.ajax({
			'type' : 'GET',
			'url' : "/sitewhere/api/assignments/" + token + "/measurements/series?page=1&pageSize=100&measurementIds=air.speed%2Cfuel.level",
			'headers' : {
				"Authorization" : "Basic " + btoa("admin:password")
			},
			'contentType' : 'application/json',
			'success' : onGraphDataSuccess,
			'error' : onGraphDataFail
		});
	}

	/** Called on successful assignment load request */
	function onAssignmentSuccess(assignment, status, jqXHR) {
		var data = {
			'assignment' : assignment,
			'flight' : lastFlights[assignment.token]
		};

		var headerTpl = kendo.template($("#tpl-popup-header").html());
		$('#plane-info').html(headerTpl(data));

		var detailsTpl = kendo.template($("#tpl-flight-details").html());
		var detailsHtml = detailsTpl(data);
		$('#tabs-1').html(detailsHtml);

		$("#detail-popup").show();
	}

	/** Handle error on getting assignment data */
	function onAssignmentFail(jqXHR, textStatus, errorThrown) {
		alert('Unable to load assignment data.');
	}

	/** Called on successful chart data load request */
	function onGraphDataSuccess(data, status, jqXHR) {
		for (var i=0; i< data.length; i++) {
			if (data[i].measurementId == 'air.speed') {
				airSpeedChart.options.series[0].data = convertChartData(data[i].entries);
				airSpeedChart.refresh();
			} else if (data[i].measurementId == 'fuel.level') {
				fuelLevelChart.options.series[0].data = convertChartData(data[i].entries);
				fuelLevelChart.refresh();
			}
		}
	}
	
	/** Convert chart data for use with KendoUI */
	function convertChartData(entries) {
		var result = [];
		for (var i = 0; i < entries.length; i++) {
			var date = kendo.parseDate(entries[i].measurementDate);
			result.push({value: entries[i].value, date: date});
		}
		return result;
	}

	/** Handle error on getting chart data */
	function onGraphDataFail(jqXHR, textStatus, errorThrown) {
		alert('Unable to load chart data.');
	}

	/** Close the popup */
	function closePopup() {
		popupToken = null;
		$("#detail-popup").hide();
	}
</script>

</html>