package co.uk.rob.apartment.automation.utilities;

import java.util.List;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.devices.Multisensor;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;

/**
 * Manages a MSO messages for the webapp, deciding order of precedence.
 * 
 * @author Rob
 *
 */
public class MSOMessageManager {
	
	private StringBuilder message = null;

	public MSOMessageManager() {
		List<ReportingDevice> apartmentReportingDevices = DeviceListManager.getReportingDevices();
		
		//call methods in order of precedence - lowest first
		testForLowBatteries(apartmentReportingDevices);
		testForOfflineSensors(apartmentReportingDevices);
		testForUnexpectedOccupancy();
		
		//TODO: if windows are open and apartment marked as unoccupied, display as MSO (+ send text)
	}
	
	/**
	 * Will only show one Multisensor battery level if more than one have low batteries.
	 * 
	 * @param apartmentReportingDevices devices to check
	 */
	private void testForLowBatteries(List<ReportingDevice> apartmentReportingDevices) {
		for (ReportingDevice device : apartmentReportingDevices) {
			if (device instanceof Multisensor) {
				if (device.getBatteryLevel() <= 10) {
					this.message = new StringBuilder();
					this.message.append(device.getZone().toString() + " multisensor battery " + 
							device.getBatteryLevel() + "%");
				}
			}
		}
	}
	
	/**
	 * List all Multisensors currently offline.
	 * 
	 * @param apartmentReportingDevices devices to check
	 */
	private void testForOfflineSensors(List<ReportingDevice> apartmentReportingDevices) {
		int count = 0;
		for (ReportingDevice device : apartmentReportingDevices) {
			if (device instanceof Multisensor) {
				if (((Multisensor) device).isNotOperational()) {
					this.message = new StringBuilder();
					
					if (count > 0) {
						this.message.append(", " + device.getZone().toString().replace("_", " "));
					}
					else {
						this.message.append(device.getZone().toString().replace("_", " "));
					}
					count++;
				}
			}
		}
		
		if (this.message != null && count > 0) {
			if (count > 1) {
				this.message.append(" multisensors appear to be offline");
			}
			else {
				this.message.append(" multisensor appears to be offline");
			}
		}
	}
	
	/**
	 * Creates MSO for unexpected occupancy.
	 */
	private void testForUnexpectedOccupancy() {
		String unexpectedOccupancy = HomeAutomationProperties.getProperty("ApartmentUnexpectedOccupancy");
		
		if ("true".equals(unexpectedOccupancy)) {
			//overwrite Multisensors as this takes precedence
			this.message = new StringBuilder();
			this.message.append("Unexpected occupancy in apartment");
		}
	}
	
	public String getMessage() {
		if (this.message != null) {
			String formatted = this.message.toString();
			formatted = formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
			return formatted;
		}
		
		return null;
	}
}
