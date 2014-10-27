package co.uk.rob.apartment.automation.model.handlers;

import java.util.Calendar;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractActivityHandler;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;
import co.uk.rob.apartment.automation.utilities.SMSHelper;
import co.uk.rob.apartment.automation.utilities.SpeechOrchestrationManager;

public class FrontDoorActivityHandler extends AbstractActivityHandler {
	
	private Logger log = Logger.getLogger(FrontDoorActivityHandler.class);
	
	private ControllableDevice loungeLamp;
	private ControllableDevice stickLoungeLamp;
	
	@Override
	public void run() {
		if (this.reportingDevice.isTriggered()) {
			log.info("Front door opened");
			String unexpectedOccupancy = HomeAutomationProperties.getProperty("ApartmentUnexpectedOccupancy");
			String atHomeModeLounge = HomeAutomationProperties.getProperty("AtHomeTodayMode");
			if (!CommonQueries.expectedOccupancyInApartment() && "false".equals(unexpectedOccupancy) && "false".equals(atHomeModeLounge)) {
				SMSHelper.sendSMS("07965502960", "Apartment has been occupied during an unexpected time from front door.");
				HomeAutomationProperties.setOrUpdateProperty("ApartmentUnexpectedOccupancy", "true");
			}
			
			Calendar now = Calendar.getInstance();
			
			Calendar lastApartmentOccupancyPlusHour = CommonQueries.getLastApartmentOccupancyTime();
			if (lastApartmentOccupancyPlusHour != null) {
				lastApartmentOccupancyPlusHour.add(Calendar.MINUTE, 60);
			}
			
			if (lastApartmentOccupancyPlusHour != null && now.after(lastApartmentOccupancyPlusHour)) {
				boolean darkOutside = false;
				
				if (CommonQueries.isBrightnessBetween1and200()) {
					darkOutside = true;
					log.info("Front door opened, apartment unoccupied for more than 1 hour and it's dark outside - welcoming people home with all lamps if not already on");
					
					loungeLamp = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(0);
					stickLoungeLamp = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(2);
					
					if (!loungeLamp.isDeviceOn() && !loungeLamp.isAutoOverridden() && !loungeLamp.isManuallyOverridden()) {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							//no op
						}
						
						loungeLamp.turnDeviceOnAutoOverride("55");
					}
					
					if (!stickLoungeLamp.isDeviceOn() && !stickLoungeLamp.isAutoOverridden() && !stickLoungeLamp.isManuallyOverridden()) {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							//no op
						}
						
						stickLoungeLamp.turnDeviceOnAutoOverride("99");
					}
				}
				
				String played = HomeAutomationProperties.getProperty("ApartmentWelcomeHome");
				if (played != null && "false".equals(played)) {
					HomeAutomationProperties.setOrUpdateProperty("ApartmentWelcomeHome", "true");
					if (darkOutside) {
						new SpeechOrchestrationManager("Welcome home! I've turned some lights on for you.", false, false, false, null).start();
					}
					else {
						new SpeechOrchestrationManager("Welcome home!", false, false, false, null).start();
					}
				}
			}
		}
		else {
			log.info("Front door now closed");
		}
	}
}
