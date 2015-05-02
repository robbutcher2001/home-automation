$(document).ready(function(){
	Location.getLocation();

	//bind action for all buttons
	$('.button').bind('click', function() {
		$(this).attr('disabled', 'disabled');
		DeliveryHelper.issueCommand('bedroomOneTabletView', '?action=' + $(this).attr('id') + '&', 'Done');
	});

	$('#loungeCamera').bind('click', function() {
		DeliveryHelper.refreshCamera();
	});

	window.setInterval(function() { DeliveryHelper.getDeviceStatuses(); }, 1000);

	window.setInterval(function() { DeliveryHelper.refreshCamera(); }, 2000);
	
});

var lat = "";
var lng = "";

var DeliveryHelper = {
		//ensure this is 'false' when in Production
		online: false,

		verifyEngineOnline: function() {
			DeliveryHelper.issueCommand('verifyEngineOnline', '?', 'Online');
		},
		
		issueCommand: function(servlet, action, completedMessage) {
			$.ajax({
				url: servlet + action + Location.addToRequest(),
				type: "GET",
				statusCode: {
					406: function() {
						window.location = 'login';
					}
				},
				error: function(response) {
					$('.button').attr('disabled', 'disabled');
					Utilities.createPermRedStatusBar('Currently offline');
				},
				success: function(response) {
					$('.button').removeAttr('disabled');
					if (response == 'true' || response == true) {
						Utilities.createTempGreenStatusBar(completedMessage);
					}
					else if (response == 'false' || response == false) {
						Utilities.createPermRedStatusBar('Automation Engine not reachable - offline');
					}
					else {
						Utilities.createTempGreenStatusBar(response);
					}
				}
			});
			return false;
		},

		refreshCamera: function() {
			$('#loungeCamera').attr('src', '/loungeCamera?' + Location.addToRequest());

			return false;
		},

		getDeviceStatuses: function() {
			$.ajax({
				url: 'deviceStatus/rob_room?' + Location.addToRequest(),
				type: 'GET',
				dataType: "json",
				statusCode: {
					406: function() {
						window.location = 'login';
					}
				},
				error: function(response) {
					//Change title bars
					$('.lighting').html('Lighting - not auto updating');
					$('.lighting').addClass('error-text');
					$('.blinds').html('Blinds - not auto updating');
					$('.blinds').addClass('error-text');
					$('.bedHeating').html('Bed Heating - not auto updating');
					$('.bedHeating').addClass('error-text');
					$('.misc').html('Miscellaneous - not auto updating');
					$('.misc').addClass('error-text');

					//Add dropdown
					Utilities.createPermRedStatusBar('Can\'t reach apartment - retrying');

					//Disable buttons
					$('.button').attr('disabled', 'disabled');
				},
				success: function(response) {
					if ($('.statusBar').html() == 'Can\'t reach apartment - retrying') {
						//Reset title bars
						$('.lighting').removeClass('error-text');
						$('.blinds').removeClass('error-text');
						$('.bedHeating').removeClass('error-text');
						$('.misc').removeClass('error-text');

						//Remove dropdown via temp green dropdown
						Utilities.createTempGreenStatusBar('Back online');

						//Enable buttons
						$('.button').removeAttr('disabled');
					}
					
					$('.top-title').html('Lighting');

					if (response.rob_room.electric_blanket1.is_warming == true) {
						$('.bedHeating').html('Bed Heating | Currently Warming');
					}
					else {
						$('.bedHeating').html('Bed Heating');
					}
					
					if (response.rob_room.full_bedroom_mode == 'enabled') {
						$('#bedroomMode').html('Switch to normal mode');
						$('#bedroomMode').addClass('error');
					}
					else {
						$('#bedroomMode').html('Switch to full bedroom mode');
						$('#bedroomMode').removeClass('error');
					}
				}
			});
			return false;
		}
};

//build in 401 front end display message
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
			return "lat=" + lat + "&lng=" + lng;
		}
};

var Utilities = {
		createTempGreenStatusBar: function(message) {
			$('.statusBar').html(message);
			$('.statusBar').removeClass('error');
			$('.statusBar').addClass('success');
			$('.statusBar').slideDown(200);
			$('.statusBar').delay(1200).slideToggle(200);
		},

		createPermGreenStatusBar: function(message) {
			$('.statusBar').html(message);
			$('.statusBar').removeClass('error');
			$('.statusBar').addClass('success');
			$('.statusBar').slideDown(200);
		},

		createTempRedStatusBar: function(message) {
			$('.statusBar').html(message);
			$('.statusBar').removeClass('success');
			$('.statusBar').addClass('error');
			$('.statusBar').slideDown(200);
			$('.statusBar').delay(1200).slideToggle(200);
		},

		createPermRedStatusBar: function(message) {
			$('.statusBar').html(message);
			$('.statusBar').removeClass('success');
			$('.statusBar').addClass('error');
			$('.statusBar').slideDown(200);
		},

		removeBar: function(delay) {
			$('.statusBar').delay(delay).slideUp();
		}
};
