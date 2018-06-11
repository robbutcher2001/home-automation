package co.uk.rob.apartment.automation.model.abstracts;

import co.uk.rob.apartment.automation.model.interfaces.ActivityHandler;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;

public abstract class AbstractActivityHandler extends Thread implements ActivityHandler {

	protected ReportingDevice reportingDevice;
	
	@Override
	public void handleActivity(ReportingDevice reportingDevice) {
		this.reportingDevice = reportingDevice;
		this.run();
	}
	
}
