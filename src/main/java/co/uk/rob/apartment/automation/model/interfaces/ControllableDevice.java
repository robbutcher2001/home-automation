package co.uk.rob.apartment.automation.model.interfaces;

import java.util.Date;

import co.uk.rob.apartment.automation.model.Zone;

public interface ControllableDevice {
	
	public boolean isDeviceOn();
	
	public boolean isManuallyOverridden();
	
	public boolean isAutoOverridden();
	
	public void resetManuallyOverridden();
	
	public void resetAutoOverridden();
	
	public Zone getZone();
	
	public Date getLastInteractedTime();
	
	public void setDeviceLevel(String level);
	
	public String getDeviceLevel();
	
	public Boolean turnDeviceOn(boolean manuallyOverride);
	
	public Boolean turnDeviceOn(boolean manuallyOverride, String level);
	
	public Boolean turnDeviceOff(boolean manuallyOverride);
	
	public Boolean turnDeviceOnAutoOverride(String level);
	
	public Boolean turnDeviceOffAutoOverride();
}
