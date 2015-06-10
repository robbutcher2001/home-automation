package co.uk.rob.apartment.automation.model.abstracts;

import java.util.Date;

import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.ZwayResultSet;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.utilities.CallZwaveModule;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

public abstract class AbstractControllableDevice extends AbstractDevice implements ControllableDevice {
	
	protected boolean state = false;
	protected String level = "0";
	protected Zone zone;
	protected boolean manuallyOverridden = false;
	protected boolean autoOverridden = false;
	protected String host = HomeAutomationProperties.getProperty("host");
	protected String endpoint;
	protected long lastUpdated = System.currentTimeMillis();

	@Override
	public boolean isDeviceOn() {
		return this.state;
	}

	@Override
	public boolean isManuallyOverridden() {
		return this.manuallyOverridden;
	}

	@Override
	public void resetManuallyOverridden() {
		this.manuallyOverridden = false;
	}

	@Override
	public void resetAutoOverridden() {
		this.autoOverridden = false;
	}

	@Override
	public Zone getZone() {
		return this.zone;
	}

	@Override
	public Date getLastInteractedTime() {
		return new Date(lastUpdated);
	}
	
	@Override
	public void setDeviceLevel(String level) {
		this.level = level;
	}

	@Override
	public String getDeviceLevel() {
		return this.level;
	}

	@Override
	public Boolean turnDeviceOn(boolean manuallyOverride) {
		this.state = true;
		this.manuallyOverridden = manuallyOverride;
		this.lastUpdated = System.currentTimeMillis();
		
		return true;
	}

	@Override
	public Boolean turnDeviceOn(boolean manuallyOverride,
			String level) {
		this.state = true;
		this.manuallyOverridden = manuallyOverride;
		this.level = level;
		this.lastUpdated = System.currentTimeMillis();
		
		return true;
	}

	@Override
	public Boolean turnDeviceOff(boolean manuallyOverride) {
		this.state = false;
		this.manuallyOverridden = manuallyOverride;
		this.lastUpdated = System.currentTimeMillis();
		
		return true;
	}

	@Override
	public boolean isAutoOverridden() {
		return this.autoOverridden;
	}

	@Override
	public Boolean turnDeviceOnAutoOverride(String level) {
		this.state = true;
		this.level = level;
		this.autoOverridden = true;
		
		return turnDeviceOn(false, level);
	}

	@Override
	public Boolean turnDeviceOffAutoOverride() {
		this.state = false;
		this.autoOverridden = false;
		
		return turnDeviceOff(false);
	}

	protected boolean callParseResult(String path) {
		ZwayResultSet result = CallZwaveModule.speakToModule(path);
		
		if (result.getResponseCode() == 200) {
			return true;
		}
		
		return false;
	}
}
