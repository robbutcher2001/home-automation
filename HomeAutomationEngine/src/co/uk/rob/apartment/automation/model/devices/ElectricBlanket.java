package co.uk.rob.apartment.automation.model.devices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractControllableDevice;

public class ElectricBlanket extends AbstractControllableDevice {

	private enum State {
		OFF(), TWENTY_MINS(), ONE_HOUR(), THREE_HOURS(), UNTIL_MORNING();
	}
	
	private Calendar switchOffAt;
	private State blanketState;
	
	public ElectricBlanket(String endpoint, Zone location) {
		this.endpoint = endpoint;
		this.zone = location;
		this.blanketState = State.OFF;
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
		this.blanketState = State.OFF;
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
		this.blanketState = State.OFF;
		super.turnDeviceOffAutoOverride();
		
		return turnDeviceOff(false);
	}
	
	public String toggleNextState() {
		boolean result = false;
		String confirmationString = "";
		
		if (this.blanketState.equals(State.OFF)) {
			result = turnDeviceOnFor20Minutes();
		}
		else if (this.blanketState.equals(State.TWENTY_MINS)) {
			result = turnDeviceOnForOneHour();
		}
		else if (this.blanketState.equals(State.ONE_HOUR)) {
			result = turnDeviceOnForThreeHours();
		}
		else if (this.blanketState.equals(State.THREE_HOURS)) {
			result = turnDeviceOnUntilMorning();
		}
		else if (this.blanketState.equals(State.UNTIL_MORNING)) {
			result = turnDeviceOff(true);
		}
		
		if (result) {
			confirmationString = getCurrentStateText();
		}
		
		return confirmationString;
	}
	
	public String getCurrentStateText() {
		String currentState = "Oops, issue warming bed";
		DateFormat format = new SimpleDateFormat("HH:mm");
		
		if (!isDeviceOn()) {
			currentState = "Bed warming off";
		}
		else {
			String switchOffAtFormatted = format.format(switchOffAt.getTime());
			currentState = "Warming bed until " + switchOffAtFormatted;
		}
		
		return currentState;
	}
	
	public String getNextStateText() {
		String nextStateText = "";
		
		if (this.blanketState.equals(State.OFF)) {
			nextStateText = "Warm the bed for 20 minutes";
		}
		else if (this.blanketState.equals(State.TWENTY_MINS)) {
			nextStateText = "Warm the bed for one hour";
		}
		else if (this.blanketState.equals(State.ONE_HOUR)) {
			nextStateText = "Warm the bed for three hours";
		}
		else if (this.blanketState.equals(State.THREE_HOURS)) {
			nextStateText = "Warm the bed until morning";
		}
		else if (this.blanketState.equals(State.UNTIL_MORNING)) {
			nextStateText = "Switch off bed warming";
		}
		
		return nextStateText;
	}
	
	public boolean isTimeToSwitchOff() {
		boolean switchOff = false;
		Calendar now = Calendar.getInstance();
		
		if (now.after(switchOffAt)) {
			switchOff = true;
		}
		
		return switchOff;
	}
	
	public boolean turnDeviceOnFor20Minutes() {
		this.blanketState = State.TWENTY_MINS;
		this.switchOffAt = Calendar.getInstance();
		this.switchOffAt.add(Calendar.MINUTE, 20);
		
		return turnDeviceOn(true);
	}
	
	public boolean turnDeviceOnForOneHour() {
		this.blanketState = State.ONE_HOUR;
		this.switchOffAt = Calendar.getInstance();
		this.switchOffAt.add(Calendar.HOUR_OF_DAY, 1);
		
		return turnDeviceOn(true);
	}
	
	public boolean turnDeviceOnForThreeHours() {
		this.blanketState = State.THREE_HOURS;
		this.switchOffAt = Calendar.getInstance();
		this.switchOffAt.add(Calendar.HOUR_OF_DAY, 3);
		
		return turnDeviceOn(true);
	}
	
	public boolean turnDeviceOnUntilMorning() {
		this.blanketState = State.UNTIL_MORNING;
		this.switchOffAt = Calendar.getInstance();
		this.switchOffAt.set(Calendar.HOUR_OF_DAY, 07);
		this.switchOffAt.set(Calendar.MINUTE, 30);
		
		Calendar now = Calendar.getInstance();
		
		//if after 7:30am today, change switch off date to tomorrow
		if (now.after(this.switchOffAt)) {
			this.switchOffAt.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		return turnDeviceOn(true);
	}
}
