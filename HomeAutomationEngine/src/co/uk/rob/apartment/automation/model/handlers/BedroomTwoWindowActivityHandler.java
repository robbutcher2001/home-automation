package co.uk.rob.apartment.automation.model.handlers;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.abstracts.AbstractExternalDoorActivityHandler;
import co.uk.rob.apartment.automation.utilities.CommonQueries;

/**
 * @author Rob
 *
 */
public class BedroomTwoWindowActivityHandler extends AbstractExternalDoorActivityHandler {

	private Logger log = Logger.getLogger(BedroomTwoWindowActivityHandler.class);
	
	/**
	 * Handle Rob's room window activity
	 */
	@Override
	public void run() {
		super.run();
		
		this.entrance = "Scarlett's window";
		
		if (this.reportingDevice.isTriggered()) {
			log.info("Scarlett's bedrom window opened");
			
			//check false occupancy
			if (CommonQueries.isApartmentAlarmEnabled()) {
				runUnexpectedOccupancyControl();
			}
		}
		else {
			log.info("Scarlett's bedrom window now closed");
		}
	}
}
