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
			url: 'deviceStatusJsonTest/all?' + Location.addToRequest(),
			type: 'GET',
			dataType: "json",
			statusCode: {
				406: function() {
			    	window.location = 'login';
			    }
			},
			error: function(response){
				//Change title bars
				$('.top-title').html('Lounge - not auto updating');
				$('.top-title').addClass('error-text');
				$('.rob-room-title').html('Bedroom - not auto updating');
				$('.rob-room-title').addClass('error-text');
				$('.statusTitle').html('Apartment - not auto updating');
				$('.statusTitle').addClass('error-text');
				
				//Add dropdown
				Utilities.createPermRedStatusBar('Can\'t reach apartment - retrying');
				
				//Disable buttons
				$('.button').attr('disabled', 'disabled');
			},
			success: function(response){
				
				if ($('.statusBar').html() == 'Can\'t reach apartment - retrying') {
					//Reset title bars
					$('.top-title').removeClass('error-text');
					$('.rob-room-title').removeClass('error-text');
					$('.statusTitle').removeClass('error-text');
					
					//Remove dropdown
					Utilities.createTempGreenStatusBar('Back online');
					
					//Enable buttons
					$('.button').removeAttr('disabled');
				}
				
				if (response.apartment.bedroom_to_render == 'bedroomOne') {
					$('.rob-room-title').html('Rob\'s Room');
				}
				else if (response.apartment.bedroom_to_render == 'bedroomTwo') {
					$('.rob-room-title').html('Scat\'s Room');
					$('.bedroomButtons').hide();
				}
				else {
					$('.rob-room-title').html('Bedroom');
					$('.bedroomButtons').attr('disabled', 'disabled');
				}
				
				if (response.apartment.unexpected_occupancy == 'true') {
					$('#flat-unexp-ocp').html('Unexpected occupancy triggered');
					$('#flat-unexp-ocp').show();
				}
				
				if (response.apartment.occupied == 'false') {
					$('#flat-last-ocp').html('Last occupancy ' + response.apartment.last_occupied);
					$('#flat-last-ocp').show();
					$('.statusTitle').html('Apartment | Unoccupied');
				}
				else {
					$('#flat-last-ocp').hide();
					$('.statusTitle').html('Apartment | Occupied');
				}
				
				if (response.hallway.door_sensor.open == 'true') {
					if (response.patio.door_sensor.open == 'true') {
						$('.statusTitle').html($('.statusTitle').html() + ' | Both outside doors open');
						$('.statusTitle').addClass('error-text');
					}
					else {
						$('.statusTitle').html($('.statusTitle').html() + ' | Front door open');
						$('.statusTitle').addClass('error-text');
					}
				}
				else {
					if (response.patio.door_sensor.open == 'true') {
						$('.statusTitle').html($('.statusTitle').html() + ' | Patio door open');
						$('.statusTitle').addClass('error-text');
					}
					else {
						$('.statusTitle').html($('.statusTitle').html() + ' | Both outside doors closed');
						$('.statusTitle').removeClass('error-text');
					}
				}
				
				if (response.lounge.multisensor.occupied == 'true') {
					$('.top-title').html('Lounge | Occupied');
				}
				else {
					$('.top-title').html('Lounge | Unoccupied');
				}
				
				if (response.lounge.blind1.percent_open == '0') {
					$('.top-title').html($('.top-title').html() + ' | Blinds closed');
				}
				else {
					$('.top-title').html($('.top-title').html() + ' | Blinds open ' + response.lounge.blind1.percent_open + '%');
				}
				$('.top-title').html($('.top-title').html() + ' | ' + response.lounge.multisensor.temperature + '&deg;C');
				
				$('#flat-door-last-ocp').html('Front door last opened ' + response.hallway.door_sensor.last_triggered);
				$('#patio-door-last-ocp').html('Patio door last opened ' + response.patio.door_sensor.last_triggered);
				
				if (response.patio.multisensor.occupied == 'true') {
					$('#patio-last-ocp').html('Patio occupied');
					$('#patio-last-ocp').addClass('error-text');
				}
				else {
					$('#patio-last-ocp').html('Patio last occupied ' + response.patio.multisensor.last_occupied);
					$('#patio-last-ocp').removeClass('error-text');
				}
				$('#patio-lux').html('Outside brightness is ' + response.patio.multisensor.luminiscence);
				$('#patio-humidity').html('Outside humidity is ' + response.patio.multisensor.humidity);
				$('#patio-temp').html('Outside temperature is ' + response.patio.multisensor.temperature + '&deg;C');
				
				$('#lounge-last-ocp').html('Lounge last occupied ' + response.lounge.multisensor.last_occupied);
				$('#lounge-lux').html('Lounge brightness is ' + response.lounge.multisensor.luminiscence);
				$('#lounge-humidity').html('Lounge humidity is ' + response.lounge.multisensor.humidity);
				
				$('#rob-last-ocp').html('Room last occupied ' + response.rob_room.multisensor.last_occupied);
				
				if (response.rob_room.door_sensor.open == 'true') {
					$('#rob-door-last-ocp').html('Room door opened at ' + response.rob_room.door_sensor.last_triggered);
				}
				else {
					$('#rob-door-last-ocp').html('Room door closed at ' + response.rob_room.door_sensor.last_triggered);
				}
				
				if (response.rob_room.multisensor.occupied == 'true') {
					$('.rob-room-title').html($('.rob-room-title').html() + ' | Occupied');
				}
				else {
					$('.rob-room-title').html($('.rob-room-title').html() + ' | Unoccupied');
				}
				
				$('.rob-room-title').html($('.rob-room-title').html() + ' | ' + response.rob_room.multisensor.temperature + '&deg;C');
				$('#rob-lux').html('Room brightness is ' + response.rob_room.multisensor.luminiscence);
				
				if (response.rob_room.dehumidifier1.dehumidifying == 'true') {
					$('#rob-humidity').html('Room humidity is ' + response.rob_room.multisensor.humidity + ', dehumidifying');
				}
				else {
					$('#rob-humidity').html('Room humidity is ' + response.rob_room.multisensor.humidity);
				}
				
				if (response.lounge.blind1.tilted == 'false') {
					$('#blindTiltToggle').html('Tilt blinds');
				}
				else {
					$('#blindTiltToggle').html('Tilt blinds back up');
				}
				
				if (response.lounge.bedroom_mode == 'enabled') {
					$('#bedroomModeLounge').html('Switch to lounge mode');
					$('#bedroomModeLounge').addClass('error');
				}
				else {
					$('#bedroomModeLounge').html('Switch to bedroom mode');
					$('#bedroomModeLounge').removeClass('error');
				}
				
				if (response.rob_room.full_bedroom_mode == 'enabled') {
					$('#bedroomModeRobRoom').html('Switch to normal bedroom mode');
					$('#bedroomModeRobRoom').addClass('error');
				}
				else {
					$('#bedroomModeRobRoom').html('Switch to full bedroom mode');
					$('#bedroomModeRobRoom').removeClass('error');
				}
				
				if (response.apartment.at_home_today_mode == 'enabled') {
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
