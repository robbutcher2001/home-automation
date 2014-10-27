<?xml version="1.0" encoding="ISO-8859-1" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0">
	<jsp:directive.page contentType="text/html; charset=ISO-8859-1" 
		pageEncoding="ISO-8859-1" session="false"/>
	<jsp:output doctype-root-element="html"
		doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
		omit-xml-declaration="true" />
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>Rob's Apartment</title>
		<link rel="shortcut icon" href="resources/images/favicon.ico" />
		<link href="resources/styling/bright.css" rel="stylesheet" type="text/css"/>
		<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0"><!-- --></meta>
		<script type="text/javascript" src="resources/js/jquery-1.10.2.min.js"><!-- --></script>
		<script type="text/javascript" src="resources/js/jquery.color-2.1.2.js"><!-- --></script>
		<script type="text/javascript" src="resources/js/landingHandler.js"><!-- --></script>
	</head>
	<body class="landingPage">
		<div class="text-center statusBar">Automation Engine not reachable</div>
		<button id="lounge" class="text-center floatCenterScreen">Lounge/Kitchen</button>
		<button id="bedroom1" class="text-center floatCenterScreen">Rob's Room</button>
		<button id="bedroom2" class="text-center floatCenterScreen">Scat's Room</button>
		<div class="statusBlock">
			<p class="statusTitle">Apartment Status</p>
			<p id="flat-last-ocp"></p>
			<p id="front-door"></p>
			<p id="front-door-battery"></p>
			<p id="lounge-ocp"></p>
			<p id="lounge-temp"></p>
			<p id="lounge-lux"></p>
			<p id="lounge-humidity"></p>
			<p id="lounge-sensor-battery"></p>
			<p id="rob-door"></p>
			<p id="rob-ocp"></p>
			<p id="rob-temp"></p>
			<p id="rob-lux"></p>
			<p id="rob-humidity"></p>
			<p id="rob-sensor-battery"></p>
			<p id="rob-door-battery"></p>
		</div>
	</body>
</html>
</jsp:root>