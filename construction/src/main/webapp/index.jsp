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
				<li>Locations</li>
			</ul>
			<div>Test1</div>
			<div>Test2</div>
		</div>
	</div>
</body>

<script type="text/javascript">

$(document).ready(function() {
	
	/** List of assignments */
	var assignments;
	
	// Keep reference to carousel.
    var jcarousel = $('.jcarousel').jcarousel({
        	itemLoadCallback: selectedItemChanged
    });
	
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
	}
	
	/** Handle error on getting site data */
	function onSitesFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load site data.");
	}
	
	/** Handle error on getting assignment data */
	function onAssignmentsFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load assignment data.");
	}
	
	/** Called when selected item changes */
	function selectedItemChanged(carousel, state) {
		alert(carousel.first);
	}
});
</script>

</html>