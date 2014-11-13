<!DOCTYPE html>
<html>
<head>
    <title>SiteWhere - jQuery Example</title>
	<script src="${pageContext.request.contextPath}/scripts/jquery-1.10.2.min.js"></script>
</head>

<style>
body {
	font-family: "Calibri","Khmer UI";
	font-size: 16pt;
}
</style>

<body>
<div class="container" style="border: none; margin: 25px;">
	<div id="sites"></div>
</body>

<script type="text/javascript">

$.getJSON = function(url, username, password, onSuccess, onFail) {
	return jQuery.ajax({
		'type' : 'GET',
		'url' : url,
		'contentType' : 'application/json',
		'headers': {
    		"Authorization": "Basic " + btoa(username + ":" + password)
  		},
		'success' : onSuccess,
		'error' : onFail
	});
}

$(document).ready(function() {
	
	/** Load list of sites */
	listSites();
	
	/** Make async call to load list of sites */
	function listSites() {
		$.getJSON("/sitewhere/api/sites/", "admin", "password", onSitesLoaded, onSitesFailed);
	}
	
	/** Called when sites have been loaded */
	function onSitesLoaded(data, status, jqXHR) {
		if (data.results.length > 0) {
			var sites = data.results;
			var listHtml = '';
			for (var i = 0; i < sites.length; i++) {
				listHtml = listHtml + '<b>' + sites[i].name + ':</b> ' + sites[i].description.substring(0, 75) + '...';
			}
			$('#sites').html(listHtml);
		} else {
			$('#sites').html('No sites were found.');
		}
	}
	
	/** Handle error on getting site data */
	function onSitesFailed(jqXHR, textStatus, errorThrown) {
		alert("Unable to load site data.");
	}
});
</script>

</html>