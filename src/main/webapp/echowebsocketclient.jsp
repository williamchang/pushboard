<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.*" %>
<%
	Date dtNow = new Date();
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
	<meta charset="utf-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no, shrink-to-fit=no" />
	<meta http-equiv="x-ua-compatible" content="ie=edge" />
	<title>Apache Tomcat WebSocket</title>
	<meta name="author" content="William Chang" />
</head>
<body>
	<div>
		<input type="text" id="message" />
		<input type="button" onclick="wsSendMessage();" value="Echo" />
		<input type="button" onclick="wsCloseConnection();" value="Disconnect" />
		<span style="padding-left:10px;">Page Loaded at <%= dtNow.toString() %></span>
	</div>
	<br />
	<textarea id="console" cols="80" rows="20"></textarea>
	<script type="text/javascript">
		var wsEcho = new WebSocket("ws://localhost:8080/echowebsocket");
		var eleMessageInput = document.getElementById("message");
		var eleConsole = document.getElementById("console");

		eleConsole.value = "";

		function wsSendMessage() {
			wsEcho.send(eleMessageInput.value);
			eleConsole.value += "Client sent message to the WebSocket server : " + eleMessageInput.value + "\n";
			eleMessageInput.value = "";
		}

		function wsCloseConnection() {
			wsEcho.close();
		}

		wsEcho.onopen = function(wsMessage) {wsOnOpen(wsMessage);};
		wsEcho.onmessage = function(wsMessage) {wsOnMessage(wsMessage);};
		wsEcho.onclose = function(wsMessage) {wsOnClose(wsMessage);};
		wsEcho.onerror = function(wsMessage) {wsOnError(wsMessage);};

		function wsOnOpen(wsMessage) {
			eleConsole.value += "Client connected to WebSocket server\n";
		}

		function wsOnMessage(wsMessage) {
			eleConsole.value += "Client received message from the WebSocket server : \n    " + wsMessage.data + "\n";
		}

		function wsOnClose(wsMessage) {
			eleConsole.value += "Client disconnected from WebSocket server\n";
		}

		function wsOnError(wsMessage) {
			eleConsole.value += "Client received error from the WebSocket server\n";
		}
	</script>
</body>
</html>
