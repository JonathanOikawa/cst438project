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
	    <input onkeypress="return onlyAlphabets(event,this);" type="text" name="guess" /></p>
	    <input style="display: none" type="text" name="time" value="0" />
	
	    <p>Submit button.
	    <input id="submit-button" type="submit" name="submit" value="submit" /></p>
	</form>
	<div id="timer">You have been playing this game for <% out.println(request.getAttribute("time")); %> seconds...</div>
	<form action="MyServlet" method="post">  
		<button name="logout" value="true">Logout</button>
	</form>
</body>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script>
	var time = <% out.println(request.getAttribute("time")); %> != null ? <% out.println(request.getAttribute("time")); %> : 0;
	$("#submit-button").prop("disabled", true);
	$("input[name=guess]").change(function() {
		if ($("input[name=guess]").val().length > 0) {
			$("#submit-button").prop("disabled", false);
		} else {
			$("#submit-button").prop("disabled", true);
		}
	});
	
	function onlyAlphabets(e, t) {
        try {
            if (window.event) {
                var charCode = window.event.keyCode;
            }
            else if (e) {
                var charCode = e.which;
            }
            else { return true; }
            if ((charCode > 64 && charCode < 91) || (charCode > 96 && charCode < 123))
                return true;
            else
                return false;
        }
        catch (err) {
            alert(err.Description);
        }
    }
</script>
<script src="MyServlet?js=timer.js"></script>
</html>