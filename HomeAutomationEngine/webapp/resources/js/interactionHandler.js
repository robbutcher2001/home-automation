$(document).ready(function(){
	Utilities.createPermGreenStatusBar('Contacting apartment..');
	
	$(".accordion").accordion({
		collapsible: true,
		heightStyle: "fill",
		active: 0,
		animate: 100
	});
	
	Location.getLocation();
	
	//bind action for all buttons
	$('.loungePage button').bind('click', function() {
		DeliveryHelper.issueCommand('lounge', '?action=' + $(this).attr('id') + '&', 'Done');
	});
	
	$('.bedroomOnePage button').bind('click', function() {
		DeliveryHelper.issueCommand('bedroom1', '?action=' + $(this).attr('id') + '&', 'Done');
	});
	
	$('.apartmentPage button').bind('click', function() {
		DeliveryHelper.issueCommand('lounge', '?action=' + $(this).attr('id') + '&', 'Done');
	});
	
	$('#loungeCamera').bind('click', function() {
		DeliveryHelper.refreshCamera();
	});
	
	window.setInterval(function() { DeliveryHelper.getDeviceStatuses(); }, 1000);
	
	window.setInterval(function() { DeliveryHelper.refreshCamera(); }, 3000);
	
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
//			dataType: "json",
			statusCode: {
				406: function() {
			    	window.location = 'login';
			    }
			},
			error: function(response){
				$('.button').attr('disabled', 'disabled');
				Utilities.createPermRedStatusBar('Currently offline');
			},
			success: function(response){
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
			url: 'deviceStatus?' + Location.addToRequest(),
			type: 'GET',
			statusCode: {
				406: function() {
			    	window.location = 'login';
			    }
			},
			error: function(response){
				$('.statusTitle').html('Apartment - not auto updating');
				$('.statusTitle').addClass('error-text');
				$('.button').attr('disabled', 'disabled');
			},
			success: function(response){
				$('.button').removeAttr('disabled');
				var parsedResponse = response.split(';');
				
				$('.statusTitle').html('Apartment');
				$('.statusTitle').removeClass('error-text');
				
				if (parsedResponse[0] != 'false') {
					$('#flat-last-ocp').html('Last occupancy ' + parsedResponse[0]);
					$('#flat-last-ocp').show();
					$('.statusTitle').html('Apartment | Unoccupied');
				}
				else {
					$('#flat-last-ocp').hide();
					$('.statusTitle').html('Apartment | Occupied');
				}
				
				if (parsedResponse[1] == 'true') {
					$('.statusTitle').html($('.statusTitle').html() + ' | Front door open');
					$('.statusTitle').addClass('error-text');
				}
				else {
					$('.statusTitle').html($('.statusTitle').html() + ' | Front door closed');
					$('.statusTitle').removeClass('error-text');
				}
				
				if (parsedResponse[3] == 'true') {
					$('.top-title').html('Lounge | Occupied');
				}
				else {
					$('.top-title').html('Lounge | Unoccupied');
				}
				
				$('.top-title').html($('.top-title').html() + ' | ' + parsedResponse[4] + '&deg;C');
				$('#lounge-lux').html('Lounge brightness is ' + parsedResponse[5]);
				$('#lounge-humidity').html('Lounge humidity is ' + parsedResponse[6]);
				
				if (parsedResponse[8] == 'true') {
					$('#rob-door').html('Rob room door open');
					$('#rob-door').addClass('success-text');
				}
				else {
					$('#rob-door').html('Rob room door closed');
					$('#rob-door').removeClass('success-text');
				}
				
				if (parsedResponse[9] == 'true') {
					$('.rob-room-title').html('Rob\'s Room | Occupied');
				}
				else {
					$('.rob-room-title').html('Rob\'s Room | Unoccupied');
				}
				
				$('.rob-room-title').html($('.rob-room-title').html() + ' | ' + parsedResponse[10] + '&deg;C');
				$('#rob-lux').html('Rob\'s room brightness is ' + parsedResponse[11]);
				
				if (parsedResponse[13] == 'true') {
					$('#rob-humidity').html('Rob\'s room humidity is ' + parsedResponse[12] + ', dehumidifying');
				}
				else {
					$('#rob-humidity').html('Rob\'s room humidity is ' + parsedResponse[12]);
				}
				
				if (parsedResponse[16] == 'true') {
					$('#bedroomModeLounge').html('Switch to lounge mode');
					$('#bedroomModeLounge').addClass('error');
				}
				else {
					$('#bedroomModeLounge').html('Switch to bedroom mode');
					$('#bedroomModeLounge').removeClass('error');
				}
				
				if (parsedResponse[17] == 'true') {
					$('#bedroomModeRobRoom').html('Switch to normal bedroom mode');
					$('#bedroomModeRobRoom').addClass('error');
				}
				else {
					$('#bedroomModeRobRoom').html('Switch to full bedroom mode');
					$('#bedroomModeRobRoom').removeClass('error');
				}
				
				if (parsedResponse[18] == 'true') {
					$('#atHomeModeLounge').html('Switch to \'Normal Occupancy\' mode');
					$('#atHomeModeLounge').addClass('error');
				}
				else {
					$('#atHomeModeLounge').html('Switch to \'At Home Today\' mode');
					$('#atHomeModeLounge').removeClass('error');
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
	}
};