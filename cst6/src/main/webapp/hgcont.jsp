<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>HG Cont</title>
</head>
<body>
	<image src="MyServlet?image=h<%out.print(request.getAttribute("image"));%>.gif">
	<h2 style=\"font-family:'Lucida Console', monospace\"> <% out.println(request.getAttribute("guess")); %></h2>
	<form action="MyServlet" method="post">
    <p>Guess a character    
    <input type="text" name="guess" /></p>

    <p>Submit button.
    <input type="submit" name="submit" value="submit" /></p>
</form>
<div id="timer">
	You have been playing this game for 0 seconds...
</div>
</body>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script>
	var time = 0;
	$(document).ready(function() {
		initializeTimer();
	})
	
	function initializeTimer() {
    	setInterval(function(){
    		time++;
    		$("#timer").text("You have been playing this game for " + time + " seconds...");
    	}, 1000);
	}

</script>
</html>