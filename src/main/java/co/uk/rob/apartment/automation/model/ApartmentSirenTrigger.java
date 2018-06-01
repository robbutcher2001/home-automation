package co.uk.rob.apartment.automation.model;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.devices.AlarmUnit;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;
import co.uk.rob.apartment.automation.utilities.SMSHelper;

/**
 * @author Rob
 *
 */
public class ApartmentSirenTrigger extends TimerTask {

	private Logger log = Logger.getLogger(ApartmentSirenTrigger.class);
	private String logMessage;
	
	public ApartmentSirenTrigger(String logMessage) {
		this.logMessage = logMessage;
	}

	@Override
	public void run() {
		
		final String alarmOneTimeUrl = HomeAutomationProperties.getProperty("AlarmOneTimeUrl");
		if (!"".equals(alarmOneTimeUrl)) {
			ControllableDevice outdoorAlarmUnit = DeviceListManager.getControllableDeviceByLocation(Zone.PATIO).get(0);
			outdoorAlarmUnit.turnDeviceOn(false);
			
			AlarmUnit indoorAlarmUnit = (AlarmUnit) DeviceListManager.getControllableDeviceByLocation(Zone.HALLWAY).get(0);
			indoorAlarmUnit.turnDeviceOff(false);
			indoorAlarmUnit.setToStrobeSirenMode();
			
			SMSHelper.sendSMS("07965502960", logMessage);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				//no op
			}
			
			indoorAlarmUnit.turnDeviceOn(false);
			
			log.info("ALARM TRIGGERED - now sounding @ 106dB");
		}
		else {
			log.info("Alarm disarmed - siren cancelled");
		}
	}

}
