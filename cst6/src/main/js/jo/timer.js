/**
 * 
 */
var processingSubmit = false;

$(document).ready(function() {
	initializeTimer();
});

function initializeTimer() {
	setInterval(function(){
		time++;
		$("input[name=time]").val(time);
		if (!processingSubmit) {
			$("#timer").text("You have been playing this game for " + time + " seconds...");
		}
	}, 1000);
}

$("#submit-button").click(function() {
	processingSubmit = true;
});