$(document).ready(function(){
	Location.getLocation();
	//bind action for all buttons
	$('.bedroomOnePage button').bind('click', function() {
		DeliveryHelper.issueCommand($(this).attr('id'));
	});
	
});

var lat = "";
var lng = "";

var DeliveryHelper = {
	
	issueCommand: function(action) {
		$.ajax({
			url: "bedroom1?action=" + action + Location.addToRequest(),
			type: "GET",
			//dataType: "json",
			statusCode: {
				406: function() {
			    	window.location = 'login';
			    }
			},
			error: function(response){
				$('.statusBar').html("Backend not available");
				$('.statusBar').removeClass('success');
				$('.statusBar').addClass('error');
				$('.statusBar').slideDown(200);
			},
			success: function(response){
				if (response == 'true') {
					$('.statusBar').html("Done");
					$('.statusBar').removeClass('error');
					$('.statusBar').addClass('success');
					$('.statusBar').slideDown(200);
					$('.statusBar').delay(1400).slideToggle(750);
				}
				else if (response == 'false') {
					$('.statusBar').html("Automation Engine not reachable");
					$('.statusBar').removeClass('success');
					$('.statusBar').addClass('error');
					$('.statusBar').slideDown(200);
				}
				else {
					$('.statusBar').html(response);
					$('.statusBar').removeClass('error');
					$('.statusBar').addClass('success');
					$('.statusBar').slideDown(200);
					$('.statusBar').delay(1400).slideToggle(750);
				}
			}
		});
		return false;
	}
};

var Location = {
	getLocation: function() {
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(Location.parseLocation);
		}
	},
	
	parseLocation: function(position) {
		lat = position.coords.latitude;
		lng = position.coords.longitude;
		
		DeliveryHelper.verifyEngineOnline();
	},
	
	addToRequest: function() {
		return "&lat=" + lat + "&lng=" + lng;
	}
};