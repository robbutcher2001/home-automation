package co.uk.rob.apartment.automation.model.interfaces;

import co.uk.rob.apartment.automation.model.Zone;

public interface ReportingDevice {
	
	public void requestNewReport();
	
	public void requestNewBatteryReport();
	
	public boolean applyNewReport(String resultSet);
	
	public boolean isTriggered();
	
	public void setTriggered(boolean motion);
	
	public Float[] getTemperature();
	
	public Float[] getLuminiscence();
	
	public Float[] getHumidity();
	
	public Integer getBatteryLevel();
	
	public long getLastUpdated();
	
	public Zone getZone();
	
}
