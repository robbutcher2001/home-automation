package co.uk.rob.apartment.automation.model.monitors;

import java.util.Calendar;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;
import co.uk.rob.apartment.automation.utilities.SMSHelper;

public class BedroomTwoEnvironmentMonitor extends Thread {
	
	private Logger log = Logger.getLogger(BedroomTwoEnvironmentMonitor.class);
	
	private ReportingDevice windowSensor;
	
	public BedroomTwoEnvironmentMonitor() {
		windowSensor = DeviceListManager.getReportingDeviceByLocation(Zone.SCARLETT_ROOM).get(0);
		
		log.info("Scarlett room environment monitor started");
	}
	
	@Override
	public void run() {
		while (!this.isInterrupted()) {
			Calendar now = Calendar.getInstance();
			Calendar tenPM = (Calendar) now.clone();
			
			tenPM.set(Calendar.HOUR_OF_DAY, 22);
			tenPM.set(Calendar.MINUTE, 00);
			
			String scarlettWindowWarningSent = HomeAutomationProperties.getProperty("ScarlettWindowWarningSent");
			if (scarlettWindowWarningSent == null || (scarlettWindowWarningSent != null && "false".equals(scarlettWindowWarningSent))) {
				if (now.after(tenPM) && CommonQueries.isBrightnessBelow20() && windowSensor.isTriggered()) {
					final String smsText = "Warning: other bedroom window is still open and it's now dark";
					SMSHelper.sendSMS("07965502960", smsText);
					HomeAutomationProperties.setOrUpdateProperty("ScarlettWindowWarningSent", "true");
					log.info("Sending warning text to Rob as it's dark, after 10pm and the other bedroom window is still open");
				}
			}
			
			try {
				int oneMinute = 60000;
				Thread.sleep(oneMinute);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
