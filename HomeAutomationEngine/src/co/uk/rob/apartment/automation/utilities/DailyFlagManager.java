package co.uk.rob.apartment.automation.utilities;

import java.util.Calendar;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;

public class DailyFlagManager extends Thread {
	
	private Logger log = Logger.getLogger(DailyFlagManager.class);
	
	public DailyFlagManager() {
		log.info("Daily flag manager started - daily flags will be reset between 3 and 4am");
	}
	
	@Override
	public void run() {
		while (!this.isInterrupted()) {
			Calendar threeAM = Calendar.getInstance();
			Calendar fourAM = Calendar.getInstance();
			Calendar now = Calendar.getInstance();
			
			threeAM.set(Calendar.HOUR_OF_DAY, 3);
			threeAM.set(Calendar.MINUTE, 00);
			
			fourAM.set(Calendar.HOUR_OF_DAY, 4);
			fourAM.set(Calendar.MINUTE, 00);
			
			if (now.after(threeAM) && now.before(fourAM)) {
				HomeAutomationProperties.setOrUpdateProperty("LoungeWelcomedRob", "false");
				HomeAutomationProperties.setOrUpdateProperty("LoungeWelcomedScarlett", "false");
				HomeAutomationProperties.setOrUpdateProperty("ApartmentWelcomeHome", "false");
				HomeAutomationProperties.setOrUpdateProperty("ApartmentUnexpectedOccupancy", "false");
				HomeAutomationProperties.setOrUpdateProperty("AtHomeTodayMode", "false");
				log.info("Daily flags have been reset");
				
				ControllableDevice loungeWindowBlind = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(3);
				if (loungeWindowBlind.isManuallyOverridden()) {
					loungeWindowBlind.resetManuallyOverridden();
					log.info("Lounge window blind manual override has been reset");
				}
				
				//above is improved code for the following
//				String loungeBedroomMode = HomeAutomationProperties.getProperty("LoungeBedroomMode");
//				if (loungeBedroomMode == null || (loungeBedroomMode != null && "false".equals(loungeBedroomMode))) {
//					ControllableDevice loungeWindowBlind = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(3);
//					loungeWindowBlind.resetManuallyOverridden();
//					log.info("Lounge window blind manual override has been reset");
//				}
//				else {
//					log.info("Lounge window blind manual override has not been reset as lounge is in bedroom mode");
//				}
			}
			
			try {
				Thread.sleep(3600000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
