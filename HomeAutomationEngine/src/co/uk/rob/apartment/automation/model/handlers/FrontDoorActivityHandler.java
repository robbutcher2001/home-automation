package co.uk.rob.apartment.automation.model.handlers;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.abstracts.AbstractExternalDoorActivityHandler;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;
import co.uk.rob.apartment.automation.utilities.OneTimeUrlGenerator;
import co.uk.rob.apartment.automation.utilities.SMSHelper;

public class FrontDoorActivityHandler extends AbstractExternalDoorActivityHandler {
	
	private Logger log = Logger.getLogger(FrontDoorActivityHandler.class);
	
	@Override
	public void run() {
		super.run();
		
		if (this.reportingDevice.isTriggered()) {
			log.info("Front door opened");
			
			//check false occupancy
			String unexpectedOccupancy = HomeAutomationProperties.getProperty("ApartmentUnexpectedOccupancy");
			String atHomeModeLounge = HomeAutomationProperties.getProperty("AtHomeTodayMode");
			if (!CommonQueries.expectedOccupancyInApartment() && "false".equals(unexpectedOccupancy) && "false".equals(atHomeModeLounge)) {
				final String alarmOneTimeUrl = OneTimeUrlGenerator.getOneTimeString();
				HomeAutomationProperties.setOrUpdateProperty("AlarmOneTimeUrl", alarmOneTimeUrl);
				SMSHelper.sendSMS("07965502960", "Apartment occupied from front door. Alarm will trigger. Deactivate now: "
						+ "http://robsflat.co.uk/disableApartmentAlarm/" + alarmOneTimeUrl);
				HomeAutomationProperties.setOrUpdateProperty("ApartmentUnexpectedOccupancy", "true");
			}
			else {
				welcomeHome("Front");
			}
		}
		else {
			log.info("Front door now closed");
		}
	}
}
