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
	private String nextStateText;
	private State blanketState;
	
	public ElectricBlanket(String endpoint, Zone location) {
		this.endpoint = endpoint;
		this.zone = location;
		this.nextStateText = "Warm the bed for 20 minutes";
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
		String confirmationString = "Oops, issue warming bed";
		
		DateFormat format = new SimpleDateFormat("HH:mm");
		
		if (blanketState.equals(State.OFF)) {
			result = turnDeviceOnFor20Minutes();
			nextStateText = "Warm the bed for one hour";
		}
		else if (blanketState.equals(State.TWENTY_MINS)) {
			result = turnDeviceOnForOneHour();
			nextStateText = "Warm the bed for three hours";
		}
		else if (blanketState.equals(State.ONE_HOUR)) {
			result = turnDeviceOnForThreeHours();
			nextStateText = "Warm the bed until morning";
		}
		else if (blanketState.equals(State.THREE_HOURS)) {
			result = turnDeviceOnUntilMorning();
			nextStateText = "Switch off bed warming";
		}
		else if (blanketState.equals(State.UNTIL_MORNING)) {
			result = turnDeviceOff(false);
			nextStateText = "Warm the bed for 20 minutes";
		}
		
		if (result) {
			if (!isDeviceOn()) {
				confirmationString = "Bed warming off";
			}
			else {
				String switchOffAtFormatted = format.format(switchOffAt.getTime());
				confirmationString = "Warming bed until " + switchOffAtFormatted;
			}
		}
		
		return confirmationString;
	}
	
	public String getNextStateText() {
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
	
	private boolean turnDeviceOnFor20Minutes() {
		this.blanketState = State.TWENTY_MINS;
		switchOffAt = Calendar.getInstance();
		switchOffAt.add(Calendar.MINUTE, 20);
		
		return turnDeviceOn(true);
	}
	
	private boolean turnDeviceOnForOneHour() {
		this.blanketState = State.ONE_HOUR;
		switchOffAt = Calendar.getInstance();
		switchOffAt.add(Calendar.HOUR_OF_DAY, 1);
		
		return turnDeviceOn(true);
	}
	
	private boolean turnDeviceOnForThreeHours() {
		this.blanketState = State.THREE_HOURS;
		switchOffAt = Calendar.getInstance();
		switchOffAt.add(Calendar.HOUR_OF_DAY, 3);
		
		return turnDeviceOn(true);
	}
	
	private boolean turnDeviceOnUntilMorning() {
		this.blanketState = State.UNTIL_MORNING;
		switchOffAt = Calendar.getInstance();
		switchOffAt.set(Calendar.HOUR_OF_DAY, 07);
		switchOffAt.set(Calendar.MINUTE, 30);
		
		return turnDeviceOn(true);
	}
}
