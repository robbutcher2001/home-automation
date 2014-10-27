package co.uk.rob.apartment.automation.model.devices;

import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractControllableDevice;

public class Blind extends AbstractControllableDevice {
	
	private String switchBinaryEndpoint;
	private boolean tilted = false;
	
	public Blind(String endpoint, Zone location, String switchBinaryEndpoint) {
		this.endpoint = endpoint;
		this.zone = location;
		this.switchBinaryEndpoint = switchBinaryEndpoint;
	}

	@Override
	public Boolean turnDeviceOn(boolean manuallyOverride) {
		super.turnDeviceOn(manuallyOverride, "0");
		tilted = false;
		
		return callParseResult(host + endpoint + ".Set(0)");
	}

	@Override
	public Boolean turnDeviceOn(boolean manuallyOverride, String level) {
		int parsedLevel = 0;
		try {
			parsedLevel = Integer.parseInt(level);
			
			if (parsedLevel < 0 || parsedLevel > 100) {
				parsedLevel = 0;
			}
		}
		catch (NumberFormatException nfe) {
			parsedLevel = 0;
		}
		
		super.turnDeviceOn(manuallyOverride, Integer.toString(parsedLevel));
		tilted = false;
		
		return callParseResult(host + endpoint + ".Set(" + parsedLevel + ")");
	}

	@Override
	public Boolean turnDeviceOff(boolean manuallyOverride) {
		super.turnDeviceOff(manuallyOverride);
		setDeviceLevel("80");
		tilted = false;
		
		return callParseResult(host + endpoint + ".Set(80)");
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
	
	public Boolean tiltBlindDown() {
		tilted = true;
		callParseResult(host + this.switchBinaryEndpoint + ".Set(255)");
		return callParseResult(host + this.switchBinaryEndpoint + ".Set(0)");
	}

	public Boolean tiltBlindUp() {
		tilted = false;
		callParseResult(host + this.switchBinaryEndpoint + ".Set(0)");
		return callParseResult(host + this.switchBinaryEndpoint + ".Set(255)");
	}
	
	public boolean isTilted() {
		return tilted;
	}
}
