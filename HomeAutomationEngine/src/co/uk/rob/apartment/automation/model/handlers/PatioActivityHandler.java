package co.uk.rob.apartment.automation.model.handlers;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractActivityHandler;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.utilities.SMSHelper;

public class PatioActivityHandler extends AbstractActivityHandler {
	
	private Logger log = Logger.getLogger(PatioActivityHandler.class);

	@Override
	public void run() {
		super.run();
		
		if (reportingDevice.isTriggered()) {
			log.info("PATIO OCCUPIED - bat: " + reportingDevice.getBatteryLevel() + "%, lux: " + reportingDevice.getLuminiscence()[0]);
		}
		
//		ReportingDevice patioDoor = DeviceListManager.getReportingDeviceByLocation(Zone.PATIO).get(1);
//		
//		if (reportingDevice.isTriggered() && !patioDoor.isTriggered()) {
//			log.info("Patio occupied but patio door closed - closing blinds for security measure and sending SMS");
//			ControllableDevice loungeWindowBlind = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(3);
//			loungeWindowBlind.turnDeviceOn(true);
//			SMSHelper.sendSMS("07965502960", "Patio has been occupied but patio door is shut, blinds have been closed.");
//		}
	}
}
