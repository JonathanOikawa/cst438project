<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>HG ContBad</title>
<link rel="stylesheet" href="css/styles.css">
</head>
<body>
	<image src="MyServlet?image=h<%out.print(request.getAttribute("image"));%>.gif">
	<h2 style=\"font-family:'Lucida Console', monospace\"> <% out.println(request.getAttribute("guess")); %></h2>
	<form action="MyServlet" method="post">
    <p>There are no <% out.println(request.getAttribute("lastGuess")); %>'s.
    <input type="text" name="guess" /></p>

    <p>Submit button.
    <input type="submit" name="submit" value="submit" /></p>
</form>
</body>
</html>