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
		<title>Rob's Apartment | Explicit Login</title>
		<link rel="shortcut icon" href="resources/images/favicon.ico" />
		<link href="resources/styling/bright.css" rel="stylesheet" type="text/css"/>
		<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0"><!-- --></meta>
		<script type="text/javascript" src="resources/js/jquery-1.10.2.min.js"><!-- --></script>
	</head>
	<body class="loginPage">
		<form action="login" autocomplete="on" method="post">
			<input type="text" id="uname" name="uname" placeholder="Name" autofocus="" tabindex="1" class="floatCenterScreen"></input>
			<input type="password" id="pword" name="pword" placeholder="Password" tabindex="2" class="floatCenterScreen"></input>
			<button type="submit" tabindex="3" class="text-center floatCenterScreen"><span>Login (one month)</span></button>
		</form>
	</body>
</html>
</jsp:root>