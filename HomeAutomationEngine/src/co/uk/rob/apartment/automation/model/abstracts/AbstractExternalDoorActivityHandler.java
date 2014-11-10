package co.uk.rob.apartment.automation.model.abstracts;

import java.util.Calendar;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;
import co.uk.rob.apartment.automation.utilities.SpeechOrchestrationManager;


public abstract class AbstractExternalDoorActivityHandler extends AbstractActivityHandler {
	
	private Logger log = Logger.getLogger(AbstractExternalDoorActivityHandler.class);
	
	private ControllableDevice loungeLamp;
	private ControllableDevice stickLoungeLamp;
	
	protected void welcomeHome(String door) {
		Calendar now = Calendar.getInstance();
		
		Calendar lastApartmentOccupancyPlusHour = CommonQueries.getLastApartmentOccupancyTime();
		if (lastApartmentOccupancyPlusHour != null) {
			lastApartmentOccupancyPlusHour.add(Calendar.MINUTE, 60);
		}
		
		if (lastApartmentOccupancyPlusHour != null && now.after(lastApartmentOccupancyPlusHour)) {
			String played = HomeAutomationProperties.getProperty("ApartmentWelcomeHome");
			if (played != null && "false".equals(played)) {
				HomeAutomationProperties.setOrUpdateProperty("ApartmentWelcomeHome", "true");
				new SpeechOrchestrationManager("Welcome home!", false, false, false, null).start();
			}
			
			log.info(door + " door opened, apartment unoccupied for more than 1 hour, welcoming home");
			
			if (CommonQueries.isBrightnessAt0()) {
				boolean lampsTurnedOn = false;
				
				loungeLamp = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(0);
				stickLoungeLamp = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(2);
				
				if (!stickLoungeLamp.isDeviceOn() && !stickLoungeLamp.isAutoOverridden() && !stickLoungeLamp.isManuallyOverridden()) {
					lampsTurnedOn = true;
					log.info("Dark enough outside and stick lounge lamp isn't on, welcoming people home with this lamp");
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						//no op
					}
					
					stickLoungeLamp.turnDeviceOnAutoOverride("99");
				}
				
				if (!loungeLamp.isDeviceOn() && !loungeLamp.isAutoOverridden() && !loungeLamp.isManuallyOverridden()) {
					lampsTurnedOn = true;
					log.info("Dark enough outside and tall lounge lamp isn't on, welcoming people home with this lamp");
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						//no op
					}
					
					loungeLamp.turnDeviceOnAutoOverride("55");
				}
				
				if (lampsTurnedOn) {
					new SpeechOrchestrationManager("I've turned some lounge lamps on for you.", false, false, false, null).start();
				}
			}
		}
	}
}
