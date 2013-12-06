<!DOCTYPE html>
<html>
<head>
    <title>Construction Example</title>
	<script src="${pageContext.request.contextPath}/scripts/jquery-1.10.2.min.js"></script>
	<script src="${pageContext.request.contextPath}/scripts/jquery.jcarousel.min.js"></script>
	<script src="${pageContext.request.contextPath}/scripts/kendo.web.min.js"></script>
	<script src="${pageContext.request.contextPath}/scripts/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/scripts/sitewhere.js"></script>
	<script src="${pageContext.request.contextPath}/scripts/d3.min.js"></script>
	<script src="${pageContext.request.contextPath}/scripts/rickshaw.min.js"></script>
	<script src="${pageContext.request.contextPath}/scripts/leaflet.js"></script>
	<script src="${pageContext.request.contextPath}/scripts/sitewhere-leaflet.js"></script>
	<link href="${pageContext.request.contextPath}/css/jcarousel.ajax.css" rel="stylesheet" media="screen">
	<link href="${pageContext.request.contextPath}/css/kendo.common.min.css" rel="stylesheet" />
	<link href="${pageContext.request.contextPath}/css/kendo.bootstrap.min.css" rel="stylesheet" />
	<link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet" media="screen">
	<link href="${pageContext.request.contextPath}/css/sitewhere.css" rel="stylesheet" media="screen">
	<link href="${pageContext.request.contextPath}/css/rickshaw.min.css" rel="stylesheet" media="screen">
	<link href="${pageContext.request.contextPath}/css/font-awesome.min.css" rel="stylesheet" media="screen">
</head>

<style>
body {
	font-family: "Calibri","Khmer UI";
}
.assignment-tile {
	position: relative;
	height: 99%;
}
.asset-image {
	position: absolute;
	width: 200px;
	height: 200px;
	left: 15px;
	top: 20px;
    border: 1px solid #ddd;
    background-color: #fff;
}
.asset-banner {
	position: absolute;
	width: 200px;
	left: 15px;
	top: 20px;
    background-color: #333;
    color: #fff;
    font-size: 10pt;
    text-align: center;
}
.device-image-wrapper {
	position: absolute;
	width: 200px;
	height: 200px;
	right: 15px;
	top: 20px;
    border: 1px solid #ddd;
    background-color: #fff;
}
.device-image {
	position: absolute;
	width: 160px;
	height: 160px;
	right: 35px;
	top: 50px;
}
.device-banner {
	position: absolute;
	width: 200px;
	right: 15px;
	top: 20px;
    background-color: #333;
    color: #fff;
    font-size: 10pt;
    text-align: center;
}
.assignment-data {
	position: absolute;
	width: 350px;
	left: 225px;
	top: 20px;
    color: #333;
}
.assignment-data h1 {
	margin: 0px;
    line-height: normal;
    font-size: 12pt;
	padding: 3px;
	background-color: #333;
	border: 1px solid #bbb;
	color: #fff;
	text-align: center;
}
.assignment-data h2 {
	margin: 0px;
	line-height: normal;
	font-size: 11pt;
	font-weight: normal;
	padding-bottom: 7px;
	padding-top: 6px;
}
.asset-details {
	border-left: 1px solid #bbb;
	border-right: 1px solid #bbb;
	border-bottom: 1px solid #bbb;
	background-color: rgb(255, 255, 255);
	padding: 5px 8px;
}
.fldlabel {
	font-weight: bold;
	min-width: 50px;
	display: inline-block;
}
.assignment-state {
	position: absolute;
	width: 350px;
	left: 225px;
	top: 130px;
}
.mx-view {
	position: absolute;
	width: 140px;
	border: 1px solid #ccc;
	padding: 15px;
	font-size: 25pt;
	height: 60px;
	text-align: center;
	background-color: #fff;
}
.mx-value {
	position: absolute;
	left: 45px;
	top: 50px;
	width: 60%;
    text-align: center;
}
.mx-icon {
	position: absolute;
	left: 10px;
	top: 43px;
}
.mx-title {
	position: absolute;
	left: 0px;
	top: 0px;
	width: 100%;
    background-color: #333;
    color: #fff;
    font-size: 10pt;
    text-align: center;
}
.k-content {
	min-height: 300px;
}
.chart-legend {
	position: absolute;
	top: 20px;
	right: 20px;
}
.swmap {
	height: 290px;
}
</style>

<%@ include file="../tpl-assignment.jsp"%>

<body>
<div class="container">
	<div style="position: relative; padding-top: 20px;">
		<div class="jcarousel">
			<ul id="assignments"></ul>
		</div>
		<a href="#" class="jcarousel-control-prev">&lsaquo;</a>
		<a href="#" class="jcarousel-control-next">&rsaquo;</a></div>
		<div style="height: 10px;"></div>
		<div id="tabs">
			<ul>
				<li class="k-state-active">Measurements</li>
				<!--  
				<li>Locations</li>
				-->
			</ul>
			<div style="position: relative;">
				<div id="chart"></div>
				<div id="legend" class="chart-legend"></div>
			</div>
			<!--  
			<div>
				<div id="swmap" class="swmap"></div>
			</div>
			-->
		</div>
	</div>
</body>

<script type="text/javascript">

$(document).ready(function() {
	
	/** Token for site */
	var siteToken;
	
	/** List of assignments */
	var assignments;
	
	// Keep reference to carousel.
    var jcarousel = $('.jcarousel').jcarousel();
	
	// Hook up 'previous' control.
    $('.jcarousel-control-prev').on('jcarouselcontrol:active', function() {
        $(this).removeClass('inactive');
    }).on('jcarouselcontrol:inactive', function() {
        $(this).addClass('inactive');
    }).jcarouselControl({
        target: '-=1'
    });

	// Hook up 'next' control.
	$('.jcarousel-control-next').on('jcarouselcontrol:active', function() {
        $(this).removeClass('inactive');
    }).on('jcarouselcontrol:inactive', function() {
        $(this).addClass('inactive');
    }).jcarouselControl({
        target: '+=1'
    });
	
	$('.jcarousel').on('jcarousel:animateend', function(event, carousel) {
		var token = carousel.first().attr('id');
		showAssignment(token);
    });
	
	/** Create the tab strip */
	$("#tabs").kendoTabStrip({
		animation: false,
	}).data("kendoTabStrip");
	
	getFirstSite();
	
	/** Get the first site and load assignments from it */
	function getFirstSite() {
		$.getJSON("/sitewhere/api/sites/", onSitesLoaded, onSitesFailed);
	}
	
	/** Called when sites have been loaded */
	function onSitesLoaded(data, status, jqXHR) {
		if (data.results.length > 0) {
			var site = data.results[0];
			siteToken = site.token;
			$.getJSON("/sitewhere/api/sites/" + site.token + "/assignments?includeDevice=true&includeAsset=true", 
					onAssignmentsLoaded, onAssignmentsFailed);
		}
	}
	
	/** Called when assignments have been loaded */
	function onAssignmentsLoaded(data, status, jqXHR) {
		assignments = data.results;
		var template = kendo.template($("#tpl-assignment").html());
		var html = "";
		for (var x = 0; x < assignments.length; x++) {
			assignments[x].state.latestMeasurements.forEach(function (val, index, theArray) {
				assignments[x].state[val.name] = val;
			});
			html += template(assignments[x]);
		}
		$('#assignments').html(html);
		jcarousel.jcarousel('reload');
		showAssignment(assignments[0].token);
	}
	
	/** Handle error on getting site data */
	function onSitesFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load site data.");
	}
	
	/** Handle error on getting assignment data */
	function onAssignmentsFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load assignment data.");
	}
	
	/** Show assignment for the given token */
	function showAssignment(token) {
		$.getJSON("/sitewhere/api/assignments/" + token + "/measurements/series", onSeriesLoaded, onSeriesFailed);
		//$.getJSON("/sitewhere/api/assignments/" + token + "/locations", onLocationsLoaded, onLocationsFailed);
	}
	
	/** Called after chart series data is loaded */
	function onSeriesLoaded(swdata, status, jqXHR) {
		var series = [];
		for (var x=0; x < swdata.length; x++) {
			var swseries = swdata[x];
			var color = (swseries.measurementId == 'fuel.level') ? "red" : "steelblue";
			var name = (swseries.measurementId == 'fuel.level') ? "Fuel Level" : "Engine Temperature";
			var data = [];
			for (var y=0; y < swseries.entries.length; y++) {
				data.push({"x": swseries.entries[y].measurementDate / 1000, "y": swseries.entries[y].value});
			}
			var current = {"color": color, "data": data, "name": name};
			series.push(current);
		}
		createGraph(series);
	}
	
	function createGraph(series) {
		$('#chart').empty();
		$('#legend').empty();
		
		var graph = new Rickshaw.Graph({
	        element: document.querySelector("#chart"),
	        width: 905,
	        height: 295,
	        series: series
		});
		
		var axes = new Rickshaw.Graph.Axis.Time( { graph: graph } );
		axes.render();
		
		var legend = new Rickshaw.Graph.Legend({
	        element: document.querySelector('#legend'),
	        graph: graph
		});
		
		graph.render();
	}
	
	/** Handle error on getting chart series data */
	function onSeriesFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load chart series data.");
	}
	
	/** Called after location data is loaded */
	function onLocationsLoaded(data, status, jqXHR) {

		// Create the map.
		var map = L.Map.siteWhere('swmap', {
		    siteToken: siteToken,
		    onZonesLoaded: onZonesLoaded,
		});
	}
	
	/** Once map is loaded, add assignment data */
	function onZonesLoaded() {
	}
	
	/** Handle error on getting location data */
	function onLocationsFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load location data.");
	}
});
</script>

</html>