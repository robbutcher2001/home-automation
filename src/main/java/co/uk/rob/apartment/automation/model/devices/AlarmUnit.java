package co.uk.rob.apartment.automation.model.devices;

import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractControllableDevice;
import co.uk.rob.apartment.automation.model.interfaces.BatteryOperable;

/**
 * @author Rob
 *
 */
public class AlarmUnit extends AbstractControllableDevice implements BatteryOperable {

	private String batteryUpdateEndpoint;
	private String strobeOnlyEndpoint;
	private String strobeSirenEndpoint;
	
	public AlarmUnit(String batteryUpdateEndpoint, String dataEndpoint, String endpoint, Zone location,
			String strobeOnlyEndpoint, String strobeSirenEndpoint) {
		this.batteryUpdateEndpoint = batteryUpdateEndpoint;
		this.dataEndpoint = dataEndpoint;
		this.endpoint = endpoint;
		this.zone = location;
		this.strobeOnlyEndpoint = strobeOnlyEndpoint;
		this.strobeSirenEndpoint = strobeSirenEndpoint;
	}

	@Override
	public Boolean turnDeviceOn(boolean manuallyOverride) {
		super.turnDeviceOn(manuallyOverride, "100");
		
		return callParseResult(host + endpoint + ".Set(100)");
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
	public boolean applyNewReport(String resultSet) {
		return parseBatteryValue(resultSet);
	}

	@Override
	public synchronized void requestNewBatteryReport() {
		if (batteryUpdateEndpoint != null) {
			callParseResult(host + batteryUpdateEndpoint);
		}
	}

	@Override
	public Integer getBatteryLevel() {
		return this.battery;
	}
	
	public boolean setToStrobeOnlyMode() {
		return callParseResult(host + strobeOnlyEndpoint);
	}
	
	public boolean setToStrobeSirenMode() {
		return callParseResult(host + strobeSirenEndpoint);
	}
}
