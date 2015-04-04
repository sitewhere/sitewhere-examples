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
	height: 100%; margin: 0; padding: 0;
}
</style>

<body>
	<div id="map" style="width: 100%; height: 100%; position: absolute;"></div>
</body>

<script type="text/javascript">
	/** Google map */
	var map;

	$(document).ready(function() {
		var mapCenter = new google.maps.LatLng(39.2416975, -98.1939341);
		var options = {
			zoom : 5,
			center : mapCenter,
			mapTypeId : google.maps.MapTypeId.ROADMAP,
		}
		map = new google.maps.Map(document.getElementById("map"), options);
	});
</script>

</html>