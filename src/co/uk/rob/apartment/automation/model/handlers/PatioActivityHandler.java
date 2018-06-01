package co.uk.rob.apartment.automation.model.handlers;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractActivityHandler;
import co.uk.rob.apartment.automation.model.devices.AlarmUnit;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

public class PatioActivityHandler extends AbstractActivityHandler {
	
	private Logger log = Logger.getLogger(PatioActivityHandler.class);

	@Override
	public void run() {
		super.run();
		
		if (reportingDevice.isTriggered()) {
			String continuousAlarmMode = HomeAutomationProperties.getProperty("ContinuousAlarmMode");
			if (continuousAlarmMode != null && "true".equals(continuousAlarmMode) && CommonQueries.isBrightnessBelow20()) {
				AlarmUnit outdoorAlarmUnit = (AlarmUnit) DeviceListManager.getControllableDeviceByLocation(Zone.PATIO).get(0);
				outdoorAlarmUnit.setToStrobeOnlyMode();
				outdoorAlarmUnit.turnDeviceOn(false);
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					//no op
				}
				
				outdoorAlarmUnit.turnDeviceOff(false);
				outdoorAlarmUnit.setToStrobeSirenMode();
				
				log.info("Patio occupied during Continuous Alarm Mode - flashing strobe on siren as warning");
			}
		}
	}
}
