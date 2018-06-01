$(document).ready(function(){
	
	//bind action for login button
	$('.loginPage button').bind('click', function() {
		LoginHelper.logUserIn();
	});
	
	$('.loginPage #uname').keypress(function(e) {
		if (e.which == 13) {
			LoginHelper.logUserIn();
			return false;
		}
	});
	
	$('.loginPage #pword').keypress(function(e) {
		if (e.which == 13) {
			LoginHelper.logUserIn();
			return false;
		}
	});
	
});

var LoginHelper = {
	logUserIn: function() {
		var form = $('#loginForm');
		$('.loginButton').attr('disabled', 'disabled');
		$.ajax({
			type: "POST",
			url: form.attr('action'),
			data: form.serialize(),
			complete: function() {
				$('.loginButton').removeAttr('disabled');
			},
			statusCode: {
				200: function(response) {
					$('.statusBar').html(response);
					$('.statusBar').removeClass('error');
					$('.statusBar').addClass('success');
					$('.statusBar').slideDown(200);
					setTimeout(function() {
						window.location = '/';
					}, 1500);
			    },
			    401: function(response) {
			    	$('.statusBar').html(response.responseText);
					$('.statusBar').removeClass('success');
					$('.statusBar').addClass('error');
					$('.statusBar').slideDown(200);
					$('.statusBar').delay(1200).slideToggle(200);
			    },
				404: function() {
			    	window.location = '/';
			    }
			},
		});
		return false;
	}
};