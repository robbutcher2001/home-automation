$(document).ready(function(){
//	$('#button').click(function() {
//		var bgColour = $(this).css('background-color');
//		$(this).css('background-color','#CDFECD');
//		$(this).delay(100).animate({backgroundColor: bgColour}, 400);
//		$('.statusBar').slideToggle(200);
//	});
	
//	$('.landingPage').find('.button').removeClass('activeStepHeading');
//	$('.Content_C-TabbedWorkflow-Full').find('.oohMessage').addClass('activeStepHeading');
//	$('.Content_C-TabbedWorkflow-Full').find('.activeStepHeading').show();
//	$('.Content_C-TabbedWorkflow-Full .ooh').show();
	
	Location.getLocation();
	
	//bind action for all buttons
	$('.landingPage button').bind('click', function() {
		DeliveryHelper.navigate($(this));
	});
	
	window.setInterval(function() { DeliveryHelper.getDeviceStatuses(); }, 1000);
	
});

var lat = "";
var lng = "";

var DeliveryHelper = {
	//ensure this is 'false' when in Production
	online: false,
	
//	navigate: function(button) {
//		if (online == true) {
//			window.location = button.attr('id') + Location.addToRequest();
//		}
//	},
	
//	verifyEngineOnline: function() {
//		$.ajax({
//			url: 'verifyEngineOnline' + Location.addToRequest(),
//			type: 'GET',
//			dataType: 'json',
//			statusCode: {
//				406: function() {
//			    	window.location = 'login';
//			    }
//			},
//			error: function(response){
//				$('.statusBar').html('Backend not available');
//				$('.statusBar').addClass('error');
//				$('.statusBar').slideDown(200);
//			},
//			success: function(response){
//				if (response == true) {
//					online = true;
//					$('.statusBar').html('Automation Engine online');
//					$('.statusBar').addClass('success');
//					$('.statusBar').slideDown(200);
//					$('.statusBar').delay(1400).slideToggle(750);
//				}
//				else {
//					$('.statusBar').html('Automation Engine not reachable');
//					$('.statusBar').addClass('error');
//					$('.statusBar').slideDown(200);
//				}
//			}
//		});
//		return false;
//	},
	
//	getDeviceStatuses: function() {
//		$.ajax({
//			url: 'deviceStatus' + Location.addToRequest(),
//			type: 'GET',
//			statusCode: {
//				406: function() {
//			    	window.location = 'login';
//			    }
//			},
//			error: function(response){
//				$('.statusTitle').html('Apartment Status (?)');
//				$('.statusTitle').removeClass('success-text');
//				$('.statusTitle').addClass('error-text');
//			},
//			success: function(response){
//				var parsedResponse = response.split(';');
//				
//				$('.statusTitle').html('Apartment Status');
//				$('.statusTitle').removeClass('error-text');
//				$('.statusTitle').addClass('success-text');
//				
//				if (parsedResponse[0] != 'false') {
//					$('#flat-last-ocp').html('Last occupancy ' + parsedResponse[0]);
//					$('#flat-last-ocp').show();
//				}
//				else {
//					$('#flat-last-ocp').hide();
//				}
//				
//				if (parsedResponse[1] == 'true') {
//					$('#front-door').html('Front door open');
//					$('#front-door').removeClass('success-text');
//					$('#front-door').addClass('error-text');
//				}
//				else {
//					$('#front-door').html('Front door closed');
//					$('#front-door').removeClass('error-text');
//					$('#front-door').addClass('success-text');
//				}
//				
//				$('#front-door-battery').html('Front door batt is ' + parsedResponse[2] + '%');
//				
//				if (parsedResponse[3] == 'true') {
//					$('#lounge-ocp').html('Kitchen occupied');
//					$('#lounge-ocp').addClass('success-text');
//				}
//				else {
//					$('#lounge-ocp').html('Kitchen not occupied');
//					$('#lounge-ocp').removeClass('success-text');
//				}
//				
//				$('#lounge-temp').html('Lounge is ' + parsedResponse[4] + 'C');
//				$('#lounge-lux').html('Lounge brightness is ' + parsedResponse[5]);
//				$('#lounge-humidity').html('Lounge humidity is ' + parsedResponse[6]);
//				
//				$('#lounge-sensor-battery').html('Lounge sensor batt is ' + parsedResponse[7] + '%');
//				
//				if (parsedResponse[8] == 'true') {
//					$('#rob-door').html('Rob room door open');
//					$('#rob-door').addClass('success-text');
//				}
//				else {
//					$('#rob-door').html('Rob room door closed');
//					$('#rob-door').removeClass('success-text');
//				}
//				
//				if (parsedResponse[9] == 'true') {
//					$('#rob-ocp').html('Rob room occupied');
//					$('#rob-ocp').addClass('success-text');
//				}
//				else {
//					$('#rob-ocp').html('Rob room not occupied');
//					$('#rob-ocp').removeClass('success-text');
//				}
//				
//				$('#rob-temp').html('Rob\'s room is ' + parsedResponse[10] + 'C');
//				$('#rob-lux').html('Rob\'s room brightness is ' + parsedResponse[11]);
//				
//				if (parsedResponse[13] == 'true') {
//					$('#rob-humidity').html('Rob\'s room humidity is ' + parsedResponse[12] + ', dehumidifying');
//				}
//				else {
//					$('#rob-humidity').html('Rob\'s room humidity is ' + parsedResponse[12]);
//				}
//				
//				$('#rob-sensor-battery').html('Rob sensor batt is ' + parsedResponse[14] + '%');
//				$('#rob-door-battery').html('Rob door batt is ' + parsedResponse[15] + '%');
//			}
//		});
//		return false;
//	}
};

//var Location = {
//	getLocation: function() {
//		if (navigator.geolocation) {
//			navigator.geolocation.getCurrentPosition(Location.parseLocation);
//		}
//	},
//	
//	parseLocation: function(position) {
//		lat = position.coords.latitude;
//		lng = position.coords.longitude;
//		
//		DeliveryHelper.verifyEngineOnline();
//	},
//	
//	addToRequest: function() {
//		return "?lat=" + lat + "&lng=" + lng;
//	}
//};