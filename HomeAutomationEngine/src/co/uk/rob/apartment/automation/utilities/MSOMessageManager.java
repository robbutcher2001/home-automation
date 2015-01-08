package co.uk.rob.apartment.automation.utilities;

import java.util.List;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.devices.AlarmUnit;
import co.uk.rob.apartment.automation.model.devices.Multisensor;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
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
		List<ControllableDevice> apartmentControllableDevices = DeviceListManager.getControllableDevices();
		
		//call methods in order of precedence - lowest first
		testForMultisensorLowBatteries(apartmentReportingDevices);
		testForOfflineSensors(apartmentReportingDevices);
		testForAlarmUnitLowBatteries(apartmentControllableDevices);
		testForUnexpectedOccupancy();
		testForAlarmTrigger();
		
		//TODO: if windows are open and apartment marked as unoccupied, display as MSO (+ send text)
	}
	
	/**
	 * Will only show one Multisensor battery level if more than one have low batteries.
	 * 
	 * @param apartmentReportingDevices devices to check
	 */
	private void testForMultisensorLowBatteries(List<ReportingDevice> apartmentReportingDevices) {
		for (ReportingDevice device : apartmentReportingDevices) {
			if (device instanceof Multisensor) {
				if (device.getBatteryLevel() <= 5) {
					this.message = new StringBuilder();
					this.message.append(device.getZone().toString().replace("_", " ") + " wall sensor battery at " + 
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
		int offlineDeviceCount = 0;
		for (ReportingDevice device : apartmentReportingDevices) {
			if (device.isNotOperational()) {
				if (offlineDeviceCount == 0) {
					this.message = new StringBuilder();
				}
				
				if (offlineDeviceCount > 0) {
					this.message.append(", " + device.getZone().toString().replace("_", " "));
				}
				else {
					this.message.append(device.getZone().toString().replace("_", " "));
				}
				offlineDeviceCount++;
			}
		}
		
		//only wall sensors correctly implement the isNotOperational() method
		if (this.message != null && offlineDeviceCount > 0) {
			if (offlineDeviceCount > 1) {
				this.message.append(" wall sensors appear to be offline");
			}
			else {
				this.message.append(" wall sensor appears to be offline");
			}
		}
	}
	
	/**
	 * Will only show one AlarmUnit battery level if more than one have low batteries.
	 * 
	 * @param apartmentControllableDevices devices to check
	 */
	private void testForAlarmUnitLowBatteries(List<ControllableDevice> apartmentControllableDevices) {
		for (ControllableDevice device : apartmentControllableDevices) {
			if (device instanceof AlarmUnit) {
				if (((AlarmUnit) device).getBatteryLevel() <= 10) {
					this.message = new StringBuilder();
					this.message.append(device.getZone().toString().replace("_", " ") + " alarm unit battery at " + 
							((AlarmUnit) device).getBatteryLevel() + "%");
				}
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
	
	/**
	 * Creates MSO if alarm is triggered.
	 */
	private void testForAlarmTrigger() {
		final String alarmOneTimeUrl = HomeAutomationProperties.getProperty("AlarmOneTimeUrl");
		if (alarmOneTimeUrl != null && !"".equals(alarmOneTimeUrl)) {
			//overwrite unexpected occupancy as this takes precedence
			this.message = new StringBuilder();
			this.message.append("Alarm system triggered. Click to disarm.");
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
