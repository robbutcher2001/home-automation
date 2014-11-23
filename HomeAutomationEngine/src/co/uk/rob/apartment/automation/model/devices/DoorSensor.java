package co.uk.rob.apartment.automation.model.devices;

import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractReportingDevice;
import co.uk.rob.apartment.automation.model.interfaces.ActivityHandler;

public class DoorSensor extends AbstractReportingDevice {

	public DoorSensor(String batteryUpdateEndpoint, String dataEndpoint, ActivityHandler handler, Zone zone) {
		super(batteryUpdateEndpoint, dataEndpoint, handler, zone);
		this.setTriggered(false);
	}

	@Override
	public synchronized boolean applyNewReport(String resultSet) {
		boolean applied = false;
		super.parseBatteryValue(resultSet);
		applied = super.parseMotionValue(resultSet);
		
		if (applied) {
			this.handler.handleActivity(this);
		}
		
		return applied;
	}
	
	@Override
	public void setTriggered(boolean motion) {
		super.setTriggered(motion);
		
		if (this.isTriggered() == false) {
			this.lastUpdated = System.currentTimeMillis();
		}
	}

	//As these devices are battery powered so can go offline but until now
	//there is no way to detect this so adding stub below
	@Override
	public boolean isNotOperational() {
		return false;
	}
}
