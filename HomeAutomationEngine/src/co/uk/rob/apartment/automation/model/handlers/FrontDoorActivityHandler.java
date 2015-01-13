package co.uk.rob.apartment.automation.model.handlers;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.abstracts.AbstractExternalDoorActivityHandler;
import co.uk.rob.apartment.automation.utilities.CommonQueries;

public class FrontDoorActivityHandler extends AbstractExternalDoorActivityHandler {
	
	private Logger log = Logger.getLogger(FrontDoorActivityHandler.class);
	
	@Override
	public void run() {
		super.run();
		
		this.door = "Front";
		
		if (this.reportingDevice.isTriggered()) {
			log.info("Front door opened");
			
			//check false occupancy
			if (CommonQueries.isApartmentAlarmEnabled()) {
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
