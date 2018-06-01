package co.uk.rob.apartment.automation.model.interfaces;

import co.uk.rob.apartment.automation.model.Zone;

public interface ReportingDevice extends BatteryOperable, Inoperable {
	
	public void requestNewReport();
	
	public boolean applyNewReport(String resultSet);
	
	public boolean isTriggered();
	
	public void setTriggered(boolean motion);
	
	public Float[] getTemperature();
	
	public Float[] getLuminiscence();
	
	public Float[] getHumidity();
	
	public long getLastUpdated();
	
	public Zone getZone();
	
}
