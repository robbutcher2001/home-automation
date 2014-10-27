package co.uk.rob.apartment.automation.model.devices;

import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractControllableDevice;

public class ColouredLedRod extends AbstractControllableDevice {
	
	public ColouredLedRod(String endpoint, Zone location) {
		this.endpoint = endpoint;
		this.zone = location;
	}

	@Override
	public Boolean turnDeviceOn(boolean manuallyOverride) {
		super.turnDeviceOn(manuallyOverride, "100");
		
		return callParseResult(host + endpoint + ".Set(255)");
	}

	@Override
	public Boolean turnDeviceOn(boolean manuallyOverride, String level) {
		
		return turnDeviceOn(manuallyOverride);
	}

	@Override
	public Boolean turnDeviceOff(boolean manuallyOverride) {
		super.turnDeviceOff(manuallyOverride);
		setDeviceLevel("0");
		
		return callParseResult(host + endpoint + ".Set(0)");
	}
	
	@Override
	public Boolean turnDeviceOnAutoOverride(String level) {
		super.turnDeviceOnAutoOverride(level);
		
		return turnDeviceOn(false, level);
	}

	@Override
	public Boolean turnDeviceOffAutoOverride() {
		super.turnDeviceOffAutoOverride();
		
		return turnDeviceOff(false);
	}

}