<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>HG Lose</title>
</head>
<body>
	<image src="MyServlet?image=h<%out.print(request.getAttribute("image"));%>.gif">
	<h2>You lost!  The word is <% out.println(request.getAttribute("word")); %></h2>
	<div>This session took you <% out.println(request.getAttribute("time")); %> seconds </div>
	<div>Start a new game.</div>
	<form action="MyServlet" method="post">  
		<button name="difficulty" value="easy">Easy</button>
	</form>
	<form action="MyServlet" method="post">  
		<button name="difficulty" value="normal">Normal</button>
	</form>
	<form action="MyServlet" method="post">  
		<button name="difficulty" value="hard">Hard</button>
	</form>
	<form action="MyServlet" method="post">  
		<button name="logout" value="true">Logout</button>
	</form>
	<form action="MyServlet" method="get">  
		<button name="stats" value="true">STATS</button>
	</form>
</body>
</html>