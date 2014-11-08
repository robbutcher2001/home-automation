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
		int count = 0;
		List<ReportingDevice> apartmentReportingDevices = DeviceListManager.getReportingDevices();
		
		for (ReportingDevice device : apartmentReportingDevices) {
			if (device instanceof Multisensor) {
				if (((Multisensor) device).isNotOperational()) {
					if (this.message == null) {
						this.message = new StringBuilder();
					}
					
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
		
		if (this.message != null) {
			if (count > 1) {
				this.message.append(" multisensors appear to be offline");
			}
			else {
				this.message.append(" multisensor appears to be offline");
			}
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
