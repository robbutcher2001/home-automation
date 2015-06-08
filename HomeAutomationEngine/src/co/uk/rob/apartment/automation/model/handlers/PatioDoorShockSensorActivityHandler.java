package co.uk.rob.apartment.automation.model.handlers;

import java.util.Calendar;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractActivityHandler;
import co.uk.rob.apartment.automation.model.devices.AlarmUnit;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.SMSHelper;

/**
 * @author Rob
 *
 */
public class PatioDoorShockSensorActivityHandler extends AbstractActivityHandler {

	private Logger log = Logger.getLogger(PatioDoorShockSensorActivityHandler.class);
	private Calendar alarmOnTrigger;
	
	public PatioDoorShockSensorActivityHandler() {
		alarmOnTrigger = Calendar.getInstance();
        //prevents button being accepted on first class instantiation
		alarmOnTrigger.add(Calendar.MINUTE, -6);
	}
	
	@Override
	public void run() {
		if (CommonQueries.isApartmentAlarmEnabled()) {
			super.run();
			
			AlarmUnit outdoorAlarmUnit = (AlarmUnit) DeviceListManager.getControllableDeviceByLocation(Zone.PATIO).get(0);
			
			Calendar now = Calendar.getInstance();
			now.add(Calendar.MINUTE, -5);
			if (now.before(alarmOnTrigger)) {
				outdoorAlarmUnit.turnDeviceOn(false);
				log.info("PATIO DOOR STRONG VIBRATION DETECTED - second vibration within 5 minutes, sounding outside siren");
				SMSHelper.sendSMS("07965502960", "Patio door vibration detected again, sounding outside siren");
			}
			else {
				alarmOnTrigger = Calendar.getInstance();
				outdoorAlarmUnit.setToStrobeOnlyMode();
				outdoorAlarmUnit.turnDeviceOn(false);
				
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					//no op
				}
				
				outdoorAlarmUnit.turnDeviceOff(false);
				outdoorAlarmUnit.setToStrobeSirenMode();
				
				log.info("PATIO DOOR STRONG VIBRATION DETECTED - visual outside alarm run for 10 seconds");
				SMSHelper.sendSMS("07965502960", "Patio door vibration detected, flashing outside light");
			}
		}
	}

}
