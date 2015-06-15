package co.uk.rob.apartment.automation.model.handlers;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.abstracts.AbstractExternalDoorActivityHandler;
import co.uk.rob.apartment.automation.utilities.CommonQueries;

/**
 * @author Rob
 *
 */
public class BedroomOneWindowActivityHandler extends AbstractExternalDoorActivityHandler {

	private Logger log = Logger.getLogger(BedroomOneWindowActivityHandler.class);
	
	/**
	 * Handle Rob's room window activity
	 */
	@Override
	public void run() {
		super.run();
		
		this.entrance = "Rob's window";
		
		if (this.reportingDevice.isTriggered()) {
			log.info("Rob's bedroom window opened");
			
			//check false occupancy
			if (CommonQueries.isApartmentOccupied() && CommonQueries.isApartmentAlarmEnabled()) {
				runUnexpectedOccupancyControl();
			}
		}
		else {
			log.info("Rob's bedroom window now closed");
		}
	}
}
