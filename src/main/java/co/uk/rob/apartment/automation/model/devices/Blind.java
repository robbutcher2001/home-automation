package co.uk.rob.apartment.automation.model.devices;

import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractControllableDevice;

public class Blind extends AbstractControllableDevice {

	private enum LastDirection {
		UP(), DOWN();
	}
	
	private LastDirection lastDirection;
	private String switchBinaryEndpoint;
	private boolean tilted = false;
	
	public Blind(String endpoint, Zone location, String switchBinaryEndpoint) {
		this.lastDirection = LastDirection.DOWN;
		this.endpoint = endpoint;
		this.zone = location;
		this.switchBinaryEndpoint = switchBinaryEndpoint;
	}

	@Override
	public Boolean turnDeviceOn(boolean manuallyOverride) {
		calculateLastDirection(0);
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
		
		calculateLastDirection(parsedLevel);
		super.turnDeviceOn(manuallyOverride, Integer.toString(parsedLevel));
		tilted = false;
		
		return callParseResult(host + endpoint + ".Set(" + parsedLevel + ")");
	}

	//off is blinds at 80%, on is anything else
	@Override
	public Boolean turnDeviceOff(boolean manuallyOverride) {
		calculateLastDirection(80);
		super.turnDeviceOff(manuallyOverride);
		setDeviceLevel("80");
		tilted = false;
		
		return callParseResult(host + endpoint + ".Set(80)");
	}
	
	public Boolean tiltBlindOpen() {
		if (this.lastDirection.equals(LastDirection.DOWN)) {
			tilted = true;
			callParseResult(host + this.switchBinaryEndpoint + ".Set(255)");
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// no op
			}
			callParseResult(host + this.switchBinaryEndpoint + ".Set(0)");
		}
		else {
			tilted = true;
			callParseResult(host + this.switchBinaryEndpoint + ".Set(0)");
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// no op
			}
			callParseResult(host + this.switchBinaryEndpoint + ".Set(255)");
		}
		
		return true;
	}

	public Boolean tiltBlindClosed() {
		if (this.lastDirection.equals(LastDirection.DOWN)) {
			tilted = false;
			callParseResult(host + this.switchBinaryEndpoint + ".Set(0)");
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// no op
			}
			callParseResult(host + this.switchBinaryEndpoint + ".Set(255)");
		}
		else {
			tilted = false;
			callParseResult(host + this.switchBinaryEndpoint + ".Set(255)");
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// no op
			}
			callParseResult(host + this.switchBinaryEndpoint + ".Set(0)");
		}
		
		return true;
	}
	
	public boolean isTilted() {
		return tilted;
	}
	
	private void calculateLastDirection(int targetLevel) {
		int currentLevel = 0;
		try {
			currentLevel = Integer.parseInt(level);
			
			if (currentLevel < 0 || currentLevel > 100) {
				currentLevel = 0;
			}
		}
		catch (NumberFormatException nfe) {
			currentLevel = 0;
		}
		
		//if the level we plan to move to is greater than where we are, we are moving up, else down
		if (targetLevel > currentLevel) {
			this.lastDirection = LastDirection.UP;
		}
		else {
			this.lastDirection = LastDirection.DOWN;
		}
	}
}
