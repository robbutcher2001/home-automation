$(document).ready(function(){
	var firstOpen = new Date().getTime();
	var firstOpenParsed = new Date().toTimeString();
	window.localStorage.setItem('lastOpened', firstOpen);
	window.localStorage.setItem('lastOpenedParsed', firstOpenParsed);

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
		$(this).attr('disabled', 'disabled');
		DeliveryHelper.issueCommand('lounge', '?action=' + $(this).attr('id') + '&', 'Done');
	});

	$('.bedroomOnePage button').bind('click', function() {
		$(this).attr('disabled', 'disabled');
		DeliveryHelper.issueCommand('bedroom1', '?action=' + $(this).attr('id') + '&', 'Done');
	});

	$('.apartmentPage button').bind('click', function() {
		$(this).attr('disabled', 'disabled');
		DeliveryHelper.issueCommand('lounge', '?action=' + $(this).attr('id') + '&', 'Done');
	});

	$('#loungeCamera').bind('click', function() {
		DeliveryHelper.refreshCamera();
	});

	window.setTimeout(function() { DeliveryHelper.checkMSO(); }, 4000);

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

		checkMSO: function() {
			$.ajax({
				url: "msoMessage?" + Location.addToRequest(),
				type: "GET",
				dataType: "json",
				success: function(response) {
					if (response.data.mso_message != null) {
						$('.msoBar').html(response.data.mso_message.replace("'", "\'"));
						$('.msoBar').slideDown(600);
					}
					else if (response.data.mso_message == null) {
						$('.msoBar').slideUp(200);
					}

					if (response.data.mso_message == 'Alarm system triggered. Click to disarm.') {
						$('#mso').bind('click', function() {
							DeliveryHelper.issueCommand('appDisableAlarm', '?', 'Disarmed');
							$('.msoBar').slideUp(200);
						});
					}
				}
			});
			return false;
		},

		issueCommand: function(servlet, action, completedMessage) {
			$.ajax({
				url: servlet + action + Location.addToRequest(),
				type: "GET",
//				dataType: "json",
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
			Utilities.manageRefreshNotification();

			$.ajax({
				url: 'deviceStatus/apartment?' + Location.addToRequest(),
				type: 'GET',
				dataType: "json",
				statusCode: {
					406: function() {
						window.location = 'login';
					}
				},
				error: function(response) {
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
				success: function(response) {
					if ($('.statusBar').html() == 'Can\'t reach apartment - retrying') {
						//Reset title bars
						$('.top-title').removeClass('error-text');
						$('.rob-room-title').removeClass('error-text');
						$('.statusTitle').removeClass('error-text');

						//Remove dropdown via temp green dropdown
						Utilities.createTempGreenStatusBar('Back online');

						//Enable buttons
						$('.button').removeAttr('disabled');
					}

					if ($('.statusBar').html().indexOf('refreshing') > -1) {
						//Reset title bars
						$('.top-title').removeClass('error-text');
						$('.rob-room-title').removeClass('error-text');
						$('.statusTitle').removeClass('error-text');

						//Remove dropdown
						Utilities.removeBar(200);

						//Enable buttons
						$('.button').removeAttr('disabled');
					}

					if (response.apartment.bedroom_to_render == 'bedroomOne') {
						$('.rob-room-title').html('Rob\'s Room');
						$('.rob-room-title').removeClass('grey-text');
					}
					else if (response.apartment.bedroom_to_render == 'bedroomTwo') {
						$('.rob-room-title').html('Scat\'s Room');
						$('.rob-room-title').removeClass('grey-text');
						$('.bedroomButtons').hide();
					}
					else {
						$('.rob-room-title').html('Bedroom (login to activate)');
						$('.rob-room-title').addClass('grey-text');
						$('.bedroomButtons').attr('disabled', 'disabled');
					}

					if (response.apartment.unexpected_occupancy == 'true') {
						$('#flat-unexp-ocp').html('Unexpected occupancy triggered');
						$('#flat-unexp-ocp').show();
					}
					else {
						$('#flat-unexp-ocp').html('');
						$('#flat-unexp-ocp').hide();
					}

					if (response.apartment.alarm_system == true) {
						if (response.apartment.continuous_alarm_mode == 'enabled') {
							$('#flat-alarm-system').html('Alarm system continuously monitoring');
						}
						else {
							$('#flat-alarm-system').html('Alarm system automatically enabled');
						}
						$('#flat-alarm-system').addClass('success-text');
						$('#flat-alarm-system').removeClass('error-text');
						$('#flat-alarm-system').show();
					}
					else if (response.apartment.force_disabled == true) {
						$('#flat-alarm-system').html('Alarm system forcibly disabled');
						$('#flat-alarm-system').removeClass('success-text');
						$('#flat-alarm-system').addClass('error-text');
						$('#flat-alarm-system').show();
					}
					else {
						$('#flat-alarm-system').html('');
						$('#flat-alarm-system').hide();
					}

					if (response.apartment.occupied == false) {
						$('#flat-last-ocp').html('Last occupancy ' + response.apartment.last_occupied);
						$('#flat-last-ocp').show();
						$('.statusTitle').html('Apartment | Unoccupied');

						$('#forceDisableAlarm').hide();
					}
					else {
						$('#flat-last-ocp').hide();
						$('.statusTitle').html('Apartment | Occupied');

						if (response.apartment.force_disabled == true) {
							$('#forceDisableAlarm').html('Resume normal alarm operation');
							$('#forceDisableAlarm').removeClass('error');
						}
						else {
							$('#forceDisableAlarm').html('Force alarm system off');
							$('#forceDisableAlarm').addClass('error');
						}
						$('#forceDisableAlarm').show();
					}

					if (response.hallway.door_sensor.open == true) {
						if (response.patio.door_sensor.open == true) {
							$('.statusTitle').html($('.statusTitle').html() + ' | Both outside doors open');
							$('.statusTitle').addClass('error-text');
						}
						else {
							$('.statusTitle').html($('.statusTitle').html() + ' | Front door open');
							$('.statusTitle').addClass('error-text');
						}
					}
					else {
						if (response.patio.door_sensor.open == true) {
							$('.statusTitle').html($('.statusTitle').html() + ' | Patio door open');
							$('.statusTitle').addClass('error-text');
						}
						else {
							$('.statusTitle').html($('.statusTitle').html() + ' | Both outside doors closed');
							$('.statusTitle').removeClass('error-text');
						}
					}

					if (response.lounge.multisensor.occupied == true) {
						$('.top-title').html('Lounge | Occupied');
					}
					else {
						$('.top-title').html('Lounge | Unoccupied');
					}

					if (response.lounge.blind2.percent_open == '100') {
						$('.top-title').html($('.top-title').html() + ' | Patio blind open');
					}
					else {
						if (response.lounge.blind1.percent_open == '0') {
							$('.top-title').html($('.top-title').html() + ' | Blinds closed');
						}
						else {
							$('.top-title').html($('.top-title').html() + ' | Blinds open ' + response.lounge.blind1.percent_open + '%');
						}
					}

					$('.top-title').html($('.top-title').html() + ' | ' + response.lounge.multisensor.temperature + '&deg;C');

					$('#flat-door-last-ocp').html('Front door last opened ' + response.hallway.door_sensor.last_triggered);
					$('#patio-door-last-ocp').html('Patio door last opened ' + response.patio.door_sensor.last_triggered);

					if (response.patio.multisensor.occupied == true) {
						$('#patio-last-ocp').html('Patio occupied');
						$('#patio-last-ocp').addClass('error-text');
					}
					else {
						$('#patio-last-ocp').html('Patio last occupied ' + response.patio.multisensor.last_occupied);
						$('#patio-last-ocp').removeClass('error-text');
					}
					if (response.patio.shock_sensor.shock_detected == true) {
						$('#patio-door-shock').html('Patio door shock detected');
						$('#patio-door-shock').show();
					}
					else {
						$('#patio-door-shock').hide();
						$('#patio-door-shock').html('');
					}
					$('#patio-lux').html('Outside brightness is ' + response.patio.multisensor.luminiscence);
					$('#patio-humidity').html('Outside humidity is ' + response.patio.multisensor.humidity + '%');
					$('#patio-temp').html('Outside temperature is ' + response.patio.multisensor.temperature + '&deg;C');

					if (response.apartment.announcements_muted == true) {
						$('#lounge-announcements-muted').html('Morning announcements are muted');
						$('#lounge-announcements-muted').show();
					}
					else {
						$('#lounge-announcements-muted').html('');
						$('#lounge-announcements-muted').hide();
					}

					$('#lounge-last-ocp').html('Lounge last occupied ' + response.lounge.multisensor.last_occupied);
					$('#lounge-lux').html('Lounge brightness is ' + response.lounge.multisensor.luminiscence);
					$('#lounge-humidity').html('Lounge humidity is ' + response.lounge.multisensor.humidity + '%');

					if (response.rob_room.electric_blanket1.is_warming == true) {
						$('#rob-bed-warming').html('Bed is warming up');
						$('#rob-bed-warming').show();
					}
					else {
						$('#rob-bed-warming').html('');
						$('#rob-bed-warming').hide();
					}

					$('#rob-last-ocp').html('Room last occupied ' + response.rob_room.multisensor.last_occupied);

					if (response.rob_room.door_sensor.open == true) {
						$('#rob-door-last-ocp').html('Room door opened at ' + response.rob_room.door_sensor.last_triggered);
					}
					else {
						$('#rob-door-last-ocp').html('Room door closed at ' + response.rob_room.door_sensor.last_triggered);
					}

					if (response.rob_room.window_sensor.open == true) {
						$('#rob-window-last-ocp').html('Room window opened at ' + response.rob_room.window_sensor.last_triggered);
					}
					else {
						$('#rob-window-last-ocp').html('Room window closed at ' + response.rob_room.window_sensor.last_triggered);
					}

					if (response.scarlett_room.window_sensor.open == true) {
						$('#scat-window-last-ocp').html('Scat window opened at ' + response.scarlett_room.window_sensor.last_triggered);
					}
					else {
						$('#scat-window-last-ocp').html('Scat window closed at ' + response.scarlett_room.window_sensor.last_triggered);
					}

					$('#bathroom-lux').html('Bathroom brightness is ' + response.bathroom.multisensor.luminiscence);
					$('#bathroom-humidity').html('Bathroom humidity is ' + response.bathroom.multisensor.humidity + '%');
					$('#bathroom-temp').html('Bathroom temperature is ' + response.bathroom.multisensor.temperature + '&deg;C');

					if (response.apartment.bedroom_to_render == 'bedroomOne') {
						if (response.rob_room.multisensor.occupied == true) {
							$('.rob-room-title').html($('.rob-room-title').html() + ' | Occupied');
						}
						else {
							$('.rob-room-title').html($('.rob-room-title').html() + ' | Unoccupied');
						}

						if (response.rob_room.blind1.percent_open == '0') {
							$('.rob-room-title').html($('.rob-room-title').html() + ' | Blinds closed');
						}
						else {
							$('.rob-room-title').html($('.rob-room-title').html() + ' | Blinds open ' + response.rob_room.blind1.percent_open + '%');
						}

						$('.rob-room-title').html($('.rob-room-title').html() + ' | ' + response.rob_room.multisensor.temperature + '&deg;C');
					}

					$('#rob-lux').html('Room brightness is ' + response.rob_room.multisensor.luminiscence);

					if (response.rob_room.dehumidifier1.dehumidifying == true) {
						$('#rob-humidity').html('Room humidity is ' + response.rob_room.multisensor.humidity + '%, dehumidifying');
					}
					else {
						$('#rob-humidity').html('Room humidity is ' + response.rob_room.multisensor.humidity + '%');
					}

					if (response.rob_room.next_lighting_state == 'soft') {
						$('#lightToggleRobRoom').html('Lights on Soft');
					}
					else if (response.rob_room.next_lighting_state == 'off') {
						$('#lightToggleRobRoom').html('Lights off');
					}
					else if (response.rob_room.next_lighting_state == 'full') {
						$('#lightToggleRobRoom').html('Lights on Full');
					}

					$('#toggleBlanket').html(response.rob_room.electric_blanket1.next_state);

					if (response.rob_room.blind1.tilted == false) {
						$('#blindTiltToggleRobRoom').html('Tilt blinds');
					}
					else {
						$('#blindTiltToggleRobRoom').html('Tilt blinds back up');
					}

					if (response.lounge.blind1.tilted == false) {
						$('#blindTiltToggleLounge').html('Tilt blinds');
					}
					else {
						$('#blindTiltToggleLounge').html('Tilt blinds back up');
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

					if (response.apartment.continuous_alarm_mode == 'enabled') {
						$('#continuousAlarmMode').html('Switch to \'Normal Alarm Mode\' mode');
						$('#continuousAlarmMode').addClass('error');
					}
					else {
						$('#continuousAlarmMode').html('Switch to \'Continuous Alarm Mode\' mode');
						$('#continuousAlarmMode').removeClass('error');
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
		},

		manageRefreshNotification: function() {
			var now = new Date().getTime();

			if (window.localStorage.getItem('lastOpened')) {
				var lastOpened = window.localStorage.getItem('lastOpened');
				var lastOpenedParsed = window.localStorage.getItem('lastOpenedParsed');

				//tab hasn't been active for 5 seconds, telling user it's refreshing until first response
				var difference = now - lastOpened;
				if(difference > 5000) {
					if (difference < 60000) {
						Utilities.createPermGreenStatusBar('Updated just now, refreshing..');
					}
					else {
						var hh = lastOpenedParsed.split(':')[0];
						var mm = lastOpenedParsed.split(':')[1];
						Utilities.createPermGreenStatusBar('Last updated ' + hh + ':' + mm + ', refreshing..');
					}

					//Change title bars
					$('.top-title').addClass('error-text');
					$('.rob-room-title').addClass('error-text');
					$('.statusTitle').addClass('error-text');

					DeliveryHelper.checkMSO();
				}
			}

			window.localStorage.setItem('lastOpened', now);
			window.localStorage.setItem('lastOpenedParsed', new Date().toTimeString());
		}
};
