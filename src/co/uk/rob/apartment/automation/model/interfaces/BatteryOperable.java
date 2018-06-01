package co.uk.rob.apartment.automation.model.interfaces;

/**
 * Used to represent a device that is battery powered.
 * 
 * @author Rob
 *
 */
public interface BatteryOperable {

	public boolean applyNewReport(String resultSet);
	
	public void requestNewBatteryReport();
	
	public Integer getBatteryLevel();
}
