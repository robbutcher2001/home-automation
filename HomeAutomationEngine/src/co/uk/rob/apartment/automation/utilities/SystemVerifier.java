package co.uk.rob.apartment.automation.utilities;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.ZwayResultSet;

public class SystemVerifier {

	private static Logger log = Logger.getLogger(SystemVerifier.class);
	
	public static boolean doCheck() {
		boolean online = false;
		
		String host = HomeAutomationProperties.getProperty("host");
		String zwaydata = HomeAutomationProperties.getProperty("zwaydata");
		ZwayResultSet zwayresponse = CallZwaveModule.speakToModule(host + zwaydata + System.currentTimeMillis()/1000);
		
		if (zwayresponse != null && zwayresponse.getResponseCode() == 200) {
			online = true;
		}
		else {
			log.error("Automation Engine unavailable");
		}
		
		return online;
	}
}
