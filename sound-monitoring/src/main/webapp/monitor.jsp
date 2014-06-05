<!DOCTYPE html>
<html>
<head>
    <title>Sound Monitoring - Monitor Assignment</title>
	<script src="${pageContext.request.contextPath}/scripts/jquery-1.10.2.min.js"></script>
	<script src="${pageContext.request.contextPath}/scripts/kendo.web.min.js"></script>
	<script src="${pageContext.request.contextPath}/scripts/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/scripts/sitewhere.js"></script>
	<script src="${pageContext.request.contextPath}/scripts/highstock.js"></script>
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
svg text {
    font-family: FontAwesome;
}
.highcharts-title {
	font-size: 14pt;
	font-weight: bold;
	font-family: arial;
}
</style>

<body>

<div class="container" style="padding: 10px; border: 1px solid #ccc; margin-top: 20px;; border-top: 5px solid #dc0000;">
	<div class="sw-header" style="text-align: center; padding-bottom: 15px">
		<img src="images/sitewhere.jpg" style="max-width: 50%;"></img>
	</div>
	<div id="assignment"></div>
	<div id="highcharts" style="width: 940px;"></div>
</div>

</body>

<script type="text/javascript">

$(document).ready(function() {
	
	/** Token for site */
	var token = '<%= request.getParameter("token")%>';
	
	/** Data for current assignment */
	var assignment;
	
	/** Chart instance */
	var chart;
	
	/** Load list of sites */
	loadAssignment();
	
	/** Create the chart */
	chart = new Highcharts.StockChart({
        chart: {
        	renderTo: 'highcharts',
        	defaultSeriesType: 'spline',
            width: 930,
            height: 450,
        },
        title: {
            text: 'Dynamic Sound Level Processing'
        },
		rangeSelector : {
			selected : 1,
			inputEnabled: $('#container').width() > 480
		},
        plotOptions: {
            series: {
                animation: false,
                dataGrouping: {
                	enabled: false
                }
            },
            flags: {
            	cropThreshold: 1000,
            }
        },
        series: [{
        	type: 'flags',
            name: 'Alerts',
            data: [],
            onSeries: 'soundLevel',
            color: '#eee',
            fillColor: '#ffffcc',
            width: 12,
            style : {
				color : '#900',
				fontSize: '12pt'
			},
        }, {
        	id: 'soundLevel',
        	type: 'areaspline',
        	shadow : true,
        	name: 'Sound Level',
            data: [],
			fillColor : {
				linearGradient : {
					x1: 0, 
					y1: 0, 
					x2: 0, 
					y2: 1
				},
				stops : [
					[0, '#dc0000'], 
					[1, '#ffeeee']
				]
			}
        }],
        navigator: {
        	baseSeries: 'soundLevel'
        },
        colors: ['#dc0000'],
        yAxis: {
        	min: 0,
        	max: 1000,
			plotLines : [{
				value : 300,
				color : 'red',
				dashStyle : 'shortdash',
				width : 2,
				label : {
					text : 'Alert Trigger Level'
				}
			}]
        }
    });
	loadEvents();
	
	/** Loads information for the selected assignment */
	function loadAssignment() {
		$.getJSON("/sitewhere/api/assignments/" + token, loadGetSuccess, loadGetFailed);
	}
    
    /** Called on successful assignment load request */
    function loadGetSuccess(data, status, jqXHR) {
		var template = kendo.template($("#tpl-assignment-entry").html());
		parseAssignmentData(data);
		data.inDetailView = true;
		$('#assignment').html(template(data));
    }
    
	/** Handle error on getting assignment data */
	function loadGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load assignment data.");
	}
	
	/** Loads information for the selected assignment */
	function loadEvents() {
		$.getJSON("/sitewhere/api/assignments/" + token + "/events?page=1&pageSize=250", eventsGetSuccess, eventsGetFailed);
	}
    
    /** Called on successful assignment load request */
    function eventsGetSuccess(data, status, jqXHR) {
    	var results = data.results;
    	var value;
    	var time;
    	chart.series[0].setData([]);
    	chart.series[1].setData([]);
    	for (var i = 0; i < results.length; i++) {
    		if (results[i].eventType == 'Measurements') {
    			time = kendo.parseDate(results[i].eventDate).getTime();
    			value = [time, results[i].measurements['sound.level']];
    			chart.series[1].addPoint(value, false, false, false);
    		} else if (results[i].eventType == 'Alert') {
    			time = kendo.parseDate(results[i].eventDate).getTime();
    			value = {x: time, title: '\uf071', text: results[i].message};
    			chart.series[0].addPoint(value, false, false, false);
    		}
    	}
    	chart.redraw();
    	setTimeout(function(){loadEvents()}, 700);
    }
    
	/** Handle error on getting assignment data */
	function eventsGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load event data.");
	}
});
</script>

</html>