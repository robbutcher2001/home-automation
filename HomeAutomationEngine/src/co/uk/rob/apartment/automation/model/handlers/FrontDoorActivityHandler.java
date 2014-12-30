package co.uk.rob.apartment.automation.model.handlers;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.abstracts.AbstractExternalDoorActivityHandler;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

public class FrontDoorActivityHandler extends AbstractExternalDoorActivityHandler {
	
	private Logger log = Logger.getLogger(FrontDoorActivityHandler.class);
	
	@Override
	public void run() {
		super.run();
		
		this.door = "Front";
		
		if (this.reportingDevice.isTriggered()) {
			log.info("Front door opened");
			
			//check false occupancy
			String unexpectedOccupancy = HomeAutomationProperties.getProperty("ApartmentUnexpectedOccupancy");
			String atHomeModeLounge = HomeAutomationProperties.getProperty("AtHomeTodayMode");
			if (!CommonQueries.expectedOccupancyInApartment() && "false".equals(unexpectedOccupancy) && "false".equals(atHomeModeLounge)) {
				runUnexpectedOccupancyControl();
			}
			else {
				welcomeHome();
			}
		}
		else {
			log.info("Front door now closed");
		}
	}
}
