<!DOCTYPE html>
<html>
<head>
    <title>Sound Monitoring - Choose Assignment</title>
	<script src="${pageContext.request.contextPath}/scripts/jquery-1.10.2.min.js"></script>
	<script src="${pageContext.request.contextPath}/scripts/kendo.web.min.js"></script>
	<script src="${pageContext.request.contextPath}/scripts/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/scripts/sitewhere.js"></script>
	<link href="${pageContext.request.contextPath}/css/kendo.common.min.css" rel="stylesheet" />
	<link href="${pageContext.request.contextPath}/css/kendo.bootstrap.min.css" rel="stylesheet" />
	<link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet" media="screen">
	<link href="${pageContext.request.contextPath}/css/sitewhere.css" rel="stylesheet" media="screen">
	<link href="${pageContext.request.contextPath}/css/font-awesome.min.css" rel="stylesheet" media="screen">
</head>

<%@ include file="templateAssignmentEntry.inc"%>

<style>
body {
	font-family: "Calibri","Khmer UI";
}
</style>

<body>
<div class="container" style="border: none; margin: 25px;">
	<div id="assignments"></div>
</body>

<script type="text/javascript">

$(document).ready(function() {
	
	/** Token for site */
	var siteToken;
	
	/** List of assignments */
	var assignments;
	
	/** Assignments datasource */
	var assignmentsDS; 
	
	/** Load list of sites */
	loadSites();
	
	/** Make async call to load list of sites */
	function loadSites() {
		$.getJSON("/sitewhere/api/sites/", onSitesLoaded, onSitesFailed);
	}
	
	/** Called when sites have been loaded */
	function onSitesLoaded(data, status, jqXHR) {
		if (data.results.length > 0) {
			
			/** Use token from first site */
			var site = data.results[0];
			siteToken = site.token;
			
			/** Create AJAX datasource for assignments list */
			assignmentsDS = new kendo.data.DataSource({
				transport : {
					read : {
						url : "/sitewhere/api/sites/" + siteToken + "/assignments?includeDevice=true&includeAsset=true",
						dataType : "json",
					}
				},
				schema : {
					data: "results",
					total: "numResults",
					parse:function (response) {
					    $.each(response.results, function (index, item) {
					    	parseAssignmentData(item);
					    });
					    return response;
					}
				},
		        serverPaging: true,
		        serverSorting: true,
		        pageSize: 15,
			});
			
			/** Create the assignments list */
			assignments = $("#assignments").kendoListView({
				dataSource : assignmentsDS,
				template : kendo.template($("#tpl-assignment-entry").html())
			}).data("kendoListView");
		}
	}
	
	/** Handle error on getting site data */
	function onSitesFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load site data.");
	}
	
	/** Handle error on getting assignment data */
	function onAssignmentsFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load assignment data.");
	}
});
</script>

</html>