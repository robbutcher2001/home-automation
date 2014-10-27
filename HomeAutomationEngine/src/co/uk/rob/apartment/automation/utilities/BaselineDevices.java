package co.uk.rob.apartment.automation.utilities;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.devices.Dehumidifier;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;

public class BaselineDevices {

	private static Logger log = Logger.getLogger(BaselineDevices.class);
	
	public static void trigger() {
		
		try {
			log.info("Running baseline across whole apartment");
			
			int index = 1;
			for (ControllableDevice device : DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE)) {
				if (device instanceof Dehumidifier) {
					log.info("Skipping baseline of dehumidifier in lounge to protect compressor");
					continue;
				}
				
				log.info("Switching device " + index + " on in Lounge");
				device.turnDeviceOn(false);
				
				Thread.sleep(2000);
				
				log.info("Switching device " + index + " off in Lounge");
				device.turnDeviceOff(false);
				
				index++;
				
				Thread.sleep(2000);
			}
			
			index = 1;
			for (ControllableDevice device : DeviceListManager.getControllableDeviceByLocation(Zone.KITCHEN)) {
				if (device instanceof Dehumidifier) {
					log.info("Skipping baseline of dehumidifier in kitchen to protect compressor");
					continue;
				}
				
				log.info("Switching device " + index + " on in Kitchen");
				device.turnDeviceOn(false);
				
				Thread.sleep(2000);
				
				log.info("Switching device " + index + " off in Kitchen");
				device.turnDeviceOff(false);
				
				index++;
				
				Thread.sleep(2000);
			}
			
			index = 1;
			for (ControllableDevice device : DeviceListManager.getControllableDeviceByLocation(Zone.BATHROOM)) {
				if (device instanceof Dehumidifier) {
					log.info("Skipping baseline of dehumidifier in bathroom to protect compressor");
					continue;
				}
				
				log.info("Switching device " + index + " on in Bathroom");
				device.turnDeviceOn(false);
				
				Thread.sleep(2000);
				
				log.info("Switching device " + index + " off in Bathroom");
				device.turnDeviceOff(false);
				
				index++;
				
				Thread.sleep(2000);
			}
			
			index = 1;
			for (ControllableDevice device : DeviceListManager.getControllableDeviceByLocation(Zone.HALLWAY)) {
				if (device instanceof Dehumidifier) {
					log.info("Skipping baseline of dehumidifier in hallway to protect compressor");
					continue;
				}
				
				log.info("Switching device " + index + " on in Hallway");
				device.turnDeviceOn(false);
				
				Thread.sleep(2000);
				
				log.info("Switching device " + index + " off in Hallway");
				device.turnDeviceOff(false);
				
				index++;
				
				Thread.sleep(2000);
			}
			
			index = 1;
			for (ControllableDevice device : DeviceListManager.getControllableDeviceByLocation(Zone.ROB_ROOM)) {
				if (device instanceof Dehumidifier) {
					log.info("Skipping baseline of dehumidifier in Rob's room to protect compressor");
					continue;
				}
				
				log.info("Switching device " + index + " on in Rob's room");
				device.turnDeviceOn(false);
				
				Thread.sleep(2000);
				
				log.info("Switching device " + index + " off in Rob's room");
				device.turnDeviceOff(false);
				
				index++;
				
				Thread.sleep(2000);
			}
			
			index = 1;
			for (ControllableDevice device : DeviceListManager.getControllableDeviceByLocation(Zone.SCARLETT_ROOM)) {
				if (device instanceof Dehumidifier) {
					log.info("Skipping baseline of dehumidifier in Scarlett's room to protect compressor");
					continue;
				}
				
				log.info("Switching device " + index + " on in Scarlett's room");
				device.turnDeviceOn(false);
				
				Thread.sleep(2000);
				
				log.info("Switching device " + index + " off in Scarlett's room");
				device.turnDeviceOff(false);
				
				index++;
				
				Thread.sleep(2000);
			}
			
			log.info("Baseline complete - all devices are off and status flags aligned");
		}
		catch (InterruptedException ie) {
			//no op
		}
	}
}
