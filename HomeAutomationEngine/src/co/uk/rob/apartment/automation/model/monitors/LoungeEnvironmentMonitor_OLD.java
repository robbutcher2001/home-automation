package co.uk.rob.apartment.automation.model.monitors;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;

public class LoungeEnvironmentMonitor_OLD extends Thread {
	
	private Logger log = Logger.getLogger(LoungeEnvironmentMonitor_OLD.class);
	
	private List<ControllableDevice> devicesToControl;
	private ControllableDevice smallLoungeLamp;
	private ControllableDevice tallLoungeLamp;
	private ReportingDevice loungeReportingDevice;
	
	public LoungeEnvironmentMonitor_OLD() {
		devicesToControl = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE);
		devicesToControl.addAll(DeviceListManager.getControllableDeviceByLocation(Zone.KITCHEN));
		
		smallLoungeLamp = devicesToControl.get(0);
		tallLoungeLamp = devicesToControl.get(2);
		
		loungeReportingDevice = DeviceListManager.getReportingDeviceByLocation(Zone.LOUNGE).get(0);
		
		log.info("Lounge room environment monitor started");
	}
	
	@Override
	public void run() {
		while (!this.isInterrupted()) {
			Calendar sevenPM = Calendar.getInstance();
			Calendar now = Calendar.getInstance();
			
			sevenPM.set(Calendar.HOUR_OF_DAY, 19);
			sevenPM.set(Calendar.MINUTE, 00);
			
			if (now.after(sevenPM) && !smallLoungeLamp.isDeviceOn() && !smallLoungeLamp.isManuallyOverridden() && !smallLoungeLamp.isAutoOverridden()) {
				smallLoungeLamp.turnDeviceOnAutoOverride("55");
				log.info("Lounge small lamp auto on from 7pm - will remove when lux control takes over");
			}
			else if (now.before(sevenPM) && smallLoungeLamp.isDeviceOn() && !smallLoungeLamp.isManuallyOverridden() && smallLoungeLamp.isAutoOverridden()) {
				smallLoungeLamp.turnDeviceOffAutoOverride();
				log.info("Lounge small lamp auto off at midnight");
			}
			
			if (now.after(sevenPM) && !tallLoungeLamp.isDeviceOn() && !tallLoungeLamp.isManuallyOverridden() && !tallLoungeLamp.isAutoOverridden()) {
				tallLoungeLamp.turnDeviceOnAutoOverride("40");
				log.info("Lounge tall lamp auto on low from 7pm - will remove when lux control takes over");
			}
			else if (now.before(sevenPM) && tallLoungeLamp.isDeviceOn() && !tallLoungeLamp.isManuallyOverridden() && tallLoungeLamp.isAutoOverridden()) {
				tallLoungeLamp.turnDeviceOffAutoOverride();
				log.info("Lounge tall lamp auto off at midnight");
			}
			
			checkOccupancyTimeout();
			
			try {
				int oneMinute = 60000;
				Thread.sleep(oneMinute);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void checkOccupancyTimeout() {
		Calendar lastDateOccupied = Calendar.getInstance();
		lastDateOccupied.setTime(new Date(loungeReportingDevice.getLastUpdated()));
		lastDateOccupied.add(Calendar.MINUTE, 60);
		
		Calendar now = Calendar.getInstance();
		
		int index = 1;
		if (now.after(lastDateOccupied)) {
			for (ControllableDevice device : devicesToControl) {
				if (device.isManuallyOverridden() && !device.isAutoOverridden()) {
					log.info("Lounge room unoccupied for more than 1 hour, switching off and resetting flags for lamp " + index);
					device.resetManuallyOverridden();
					device.turnDeviceOff(false);
				}
				index++;
			}
		}
	}
}
