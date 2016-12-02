<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>HG Win</title>
</head>
<body>
	<image src="MyServlet?image=h<%out.print(request.getAttribute("image"));%>.gif">
	<h2>Congratulations you win!</h2>
</body>
</html>