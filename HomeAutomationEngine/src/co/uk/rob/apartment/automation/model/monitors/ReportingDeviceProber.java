package co.uk.rob.apartment.automation.model.monitors;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.utilities.CallZwaveModule;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

public class ReportingDeviceProber extends Thread {
	
	private Logger log = Logger.getLogger(ReportingDeviceProber.class);
	
	private String triggerEndpoint;
	
	public ReportingDeviceProber() {
		log.info("Four minute loop probe of all reporting devices started");
	}
	
	@Override
	public void run() {
		while (!this.isInterrupted()) {
			
			triggerEndpoint = HomeAutomationProperties.getProperty("host");
			triggerEndpoint += HomeAutomationProperties.getProperty("multisensorLoungeUpdateEndpoint");
			CallZwaveModule.speakToModule(triggerEndpoint);
			
			triggerEndpoint = HomeAutomationProperties.getProperty("host");
			triggerEndpoint += HomeAutomationProperties.getProperty("multisensorRobUpdateEndpoint");
			CallZwaveModule.speakToModule(triggerEndpoint);
			
			triggerEndpoint = HomeAutomationProperties.getProperty("host");
			triggerEndpoint += HomeAutomationProperties.getProperty("multisensorPatioUpdateEndpoint");
			CallZwaveModule.speakToModule(triggerEndpoint);
			
			try {
				int fourMinutes = 60000 * 4;
				Thread.sleep(fourMinutes);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
