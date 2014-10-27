package co.uk.rob.apartment.automation.model.monitors;

import java.util.List;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.ZwayResultSet;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.utilities.CallZwaveModule;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

public class ApartmentActivityManager extends Thread {
	
	private Logger log = Logger.getLogger(ApartmentActivityManager.class);
	
	private List<ReportingDevice> reportingDevices;
	private String zWayUpdateRequest;
	private int lastUpdate = 0;
	
	public ApartmentActivityManager() {
		reportingDevices = DeviceListManager.getReportingDevices();
				
		zWayUpdateRequest = HomeAutomationProperties.getProperty("host");
		zWayUpdateRequest += HomeAutomationProperties.getProperty("zWayUpdateRequest");
		
		lastUpdate = (int) (System.currentTimeMillis()/1000);
		
		log.info("Apartment monitor started - listening to all reporting devices");
	}
	
	@Override
	public void run() {
		while (!this.isInterrupted()) {
			String updateURI = zWayUpdateRequest + lastUpdate;
			ZwayResultSet resultSet = CallZwaveModule.speakToModule(updateURI);
			//-12 is to put request one second behind actual time - is this correct?
			//removing for Production
			//if this thread dies, whole flat is unmonitored - maybe poll to see if alive regularly
			lastUpdate = (int) (System.currentTimeMillis()/1000);
			
			for (ReportingDevice device : reportingDevices) {
				device.applyNewReport(resultSet.getJsonResponse());
			}
		
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
