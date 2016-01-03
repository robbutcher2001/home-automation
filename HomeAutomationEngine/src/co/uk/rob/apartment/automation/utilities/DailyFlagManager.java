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
			Calendar now = Calendar.getInstance();
			Calendar threeAM = (Calendar) now.clone();
			Calendar fourAM = (Calendar) now.clone();
			Calendar halfFiveAM = (Calendar) now.clone();
			Calendar halfSixAM = (Calendar) now.clone();
			Calendar midday = (Calendar) now.clone();
			Calendar onePM = (Calendar) now.clone();
			
			threeAM.set(Calendar.HOUR_OF_DAY, 3);
			threeAM.set(Calendar.MINUTE, 00);
			
			fourAM.set(Calendar.HOUR_OF_DAY, 4);
			fourAM.set(Calendar.MINUTE, 00);
			
			halfFiveAM.set(Calendar.HOUR_OF_DAY, 5);
			halfFiveAM.set(Calendar.MINUTE, 30);
			
			halfSixAM.set(Calendar.HOUR_OF_DAY, 6);
			halfSixAM.set(Calendar.MINUTE, 30);
			
			midday.set(Calendar.HOUR_OF_DAY, 12);
			midday.set(Calendar.MINUTE, 00);
			
			onePM.set(Calendar.HOUR_OF_DAY, 13);
			onePM.set(Calendar.MINUTE, 00);
			
			if (now.after(threeAM) && now.before(fourAM)) {
				HomeAutomationProperties.setOrUpdateProperty("LoungeWelcomedRob", "false");
				HomeAutomationProperties.setOrUpdateProperty("LoungeWelcomedScarlett", "false");
				HomeAutomationProperties.setOrUpdateProperty("ApartmentWelcomeHome", "false");
				HomeAutomationProperties.setOrUpdateProperty("ApartmentUnexpectedOccupancy", "false");
				HomeAutomationProperties.setOrUpdateProperty("RobWindowWarningSent", "false");
				HomeAutomationProperties.setOrUpdateProperty("ScarlettWindowWarningSent", "false");
				log.info("Daily flags have been reset");
				
				ControllableDevice loungeWindowBlind = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(3);
				if (loungeWindowBlind.isManuallyOverridden()) {
					loungeWindowBlind.resetManuallyOverridden();
					log.info("Lounge window blind manual override has been reset");
				}
				
				ControllableDevice loungePatioBlind = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(4);
				if (loungePatioBlind.isManuallyOverridden()) {
					loungePatioBlind.resetManuallyOverridden();
					log.info("Lounge patio blind manual override has been reset");
				}
				
				ControllableDevice robWindowBlind = DeviceListManager.getControllableDeviceByLocation(Zone.ROB_ROOM).get(5);
				if (robWindowBlind.isManuallyOverridden()) {
					robWindowBlind.resetManuallyOverridden();
					log.info("Rob room blind manual override has been reset");
				}
			}
			
			if (now.after(halfFiveAM) && now.before(halfSixAM)) {
				HomeAutomationProperties.setOrUpdateProperty("ContinuousAlarmMode", "false");
				log.info("Alarm has now been automatically disabled, resetting ContinuousAlarmMode flag");
			}
			
			if (now.after(midday) && now.before(onePM)) {
				String forceDisableAlarm = HomeAutomationProperties.getProperty("ForceDisableAlarm");
				if ("true".equals(forceDisableAlarm)) {
					HomeAutomationProperties.setOrUpdateProperty("ForceDisableAlarm", "false");
					log.info("'ForceDisableAlarm' is enabled, resetting flag");
				}
			}
			
			try {
				Thread.sleep(3600000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
