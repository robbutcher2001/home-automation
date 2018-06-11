package co.uk.rob.apartment.automation.model.devices;

import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractControllableDevice;

public class Lamp extends AbstractControllableDevice {
	
	public Lamp(String endpoint, Zone location) {
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
		int parsedLevel = 255;
		try {
			parsedLevel = Integer.parseInt(level);
			
			if (parsedLevel < 0 || parsedLevel > 255) {
				parsedLevel = 255;
			}
		}
		catch (NumberFormatException nfe) {
			parsedLevel = 255;
		}
		
		super.turnDeviceOn(manuallyOverride, Integer.toString(parsedLevel));
		
		return callParseResult(host + endpoint + ".Set(" + parsedLevel + ")");
	}

	@Override
	public Boolean turnDeviceOff(boolean manuallyOverride) {
		super.turnDeviceOff(manuallyOverride);
		setDeviceLevel("0");
		
		return callParseResult(host + endpoint + ".Set(0)");
	}

}
