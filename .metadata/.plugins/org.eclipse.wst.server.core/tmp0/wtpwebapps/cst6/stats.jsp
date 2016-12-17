<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	User: <% out.println(request.getAttribute("username")); %> <br>
	Current game? <% out.println(request.getAttribute("isGameInProgress")); %><br>
	Game stats: <br>
	Easy: <% out.println(request.getAttribute("easyWin")); %> wins out of <% out.println(request.getAttribute("easyTotal")); %> games. <br>
	Normal: <% out.println(request.getAttribute("normalWin")); %> wins out of <% out.println(request.getAttribute("normalTotal")); %> games. <br>
	Hard: <% out.println(request.getAttribute("hardWin")); %> wins out of <% out.println(request.getAttribute("hardTotal")); %> games. <br>
	
</body>
</html>