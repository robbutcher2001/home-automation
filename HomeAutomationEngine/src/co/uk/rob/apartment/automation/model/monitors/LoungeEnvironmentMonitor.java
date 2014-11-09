package co.uk.rob.apartment.automation.model.monitors;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.devices.Blind;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

public class LoungeEnvironmentMonitor extends Thread {
	
	private Logger log = Logger.getLogger(LoungeEnvironmentMonitor.class);
	
	private List<ControllableDevice> devicesToControl;
	private ControllableDevice loungeLamp;
	private ControllableDevice stickLoungeLamp;
	private Blind loungeWindowBlind;
	private ReportingDevice loungeReportingDevice;
	
	public LoungeEnvironmentMonitor() {
		devicesToControl = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE);
		devicesToControl.addAll(DeviceListManager.getControllableDeviceByLocation(Zone.KITCHEN));
		
		loungeLamp = devicesToControl.get(0);
		stickLoungeLamp = devicesToControl.get(2);
		loungeWindowBlind = (Blind) devicesToControl.get(3);
		
		loungeReportingDevice = DeviceListManager.getReportingDeviceByLocation(Zone.LOUNGE).get(0);
		
		log.info("Lounge room environment monitor started");
	}
	
	@Override
	public void run() {
		while (!this.isInterrupted()) {
			Calendar nineAM = Calendar.getInstance();
			Calendar halfThreePM = Calendar.getInstance();
			Calendar elevenPM = Calendar.getInstance();
			Calendar now = Calendar.getInstance();
			
			nineAM.set(Calendar.HOUR_OF_DAY, 9);
			nineAM.set(Calendar.MINUTE, 00);
			halfThreePM.set(Calendar.HOUR_OF_DAY, 15);
			halfThreePM.set(Calendar.MINUTE, 30);
			elevenPM.set(Calendar.HOUR_OF_DAY, 23);
			elevenPM.set(Calendar.MINUTE, 00);
			
			//if after 11pm and blinds are still not 0%, presume patio sensor is offline - close blinds
			if (now.after(elevenPM)) {
				if (!"0".equals(loungeWindowBlind.getDeviceLevel())) {
					loungeWindowBlind.turnDeviceOn(false);
					log.info("Patio sensor appears to be offline, closing blinds as precaution");
				}
			}
			
			//blinds
			String loungeBedroomMode = HomeAutomationProperties.getProperty("LoungeBedroomMode");
			if (loungeBedroomMode == null || (loungeBedroomMode != null && "true".equals(loungeBedroomMode))) {
				if (!"0".equals(loungeWindowBlind.getDeviceLevel())) {
					loungeWindowBlind.turnDeviceOn(false);
					log.info("Lounge bedroom mode is on, closing blinds");
				}
			}
			
			if (now.after(nineAM) && (loungeBedroomMode == null || (loungeBedroomMode != null && "false".equals(loungeBedroomMode)))) {
				if (CommonQueries.isBrightnessGreaterThan800()) {
					if (!"80".equals(loungeWindowBlind.getDeviceLevel()) && !loungeWindowBlind.isManuallyOverridden()) {
						loungeWindowBlind.turnDeviceOffAutoOverride();
						log.info("Outside brightness has fallen into > 800 bucket, moving blinds to 80% (max)");
					}
				}
				else if (CommonQueries.isBrightnessBetween600and800()) {
					if (!"80".equals(loungeWindowBlind.getDeviceLevel()) && !loungeWindowBlind.isManuallyOverridden()) {
						loungeWindowBlind.turnDeviceOffAutoOverride();
						log.info("Outside brightness has fallen into 600-800 bucket, moving blinds to 80% (max)");
					}
				}
				else if (CommonQueries.isBrightnessBetween400and600()) {
					if (!"80".equals(loungeWindowBlind.getDeviceLevel()) && !loungeWindowBlind.isManuallyOverridden()) {
						loungeWindowBlind.turnDeviceOffAutoOverride();
						log.info("Outside brightness has fallen into 400-600 bucket, moving blinds to 80% (max)");
					}
				}
				else if (CommonQueries.isBrightnessBetween200and400() && now.after(halfThreePM)) {
					if (!"55".equals(loungeWindowBlind.getDeviceLevel()) && !loungeWindowBlind.isManuallyOverridden()) {
						int movementTime = CommonQueries.calculateBlindMovementTime(loungeWindowBlind, "55");
						loungeWindowBlind.turnDeviceOnAutoOverride("55");
						log.info("Outside brightness has fallen into 200-400 bucket, moving blinds to 55%");
						
						if (CommonQueries.isApartmentOccupied()) {
							try {
								Thread.sleep(movementTime);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							loungeWindowBlind.tiltBlindDown();
						}
					}
				}
				else if (CommonQueries.isBrightnessBetween1and200() && now.after(halfThreePM)) {
					if (!"40".equals(loungeWindowBlind.getDeviceLevel()) && !loungeWindowBlind.isManuallyOverridden()) {
						int movementTime = CommonQueries.calculateBlindMovementTime(loungeWindowBlind, "40");
						loungeWindowBlind.turnDeviceOnAutoOverride("40");
						log.info("Outside brightness has fallen into 1-200 bucket, moving blinds to 40%");
						
						if (CommonQueries.isApartmentOccupied()) {
							try {
								Thread.sleep(movementTime);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							loungeWindowBlind.tiltBlindDown();
						}
					}
				}
				else if (CommonQueries.isBrightnessAt0() && now.after(halfThreePM)) {
					if (!"0".equals(loungeWindowBlind.getDeviceLevel()) && !loungeWindowBlind.isManuallyOverridden()) {
						loungeWindowBlind.turnDeviceOnAutoOverride("0");
						log.info("Outside brightness has fallen into < 1 bucket, moving blinds to 0%");
					}
				}
			}
			
			Calendar twoPM = Calendar.getInstance();
			
			twoPM.set(Calendar.HOUR_OF_DAY, 14);
			twoPM.set(Calendar.MINUTE, 00);
			
			//lights
			if (now.after(twoPM)) {
				if (CommonQueries.isBrightnessGreaterThan800()) {
					if (!"0".equals(loungeLamp.getDeviceLevel()) && !"0".equals(stickLoungeLamp.getDeviceLevel()) && !loungeLamp.isManuallyOverridden()) {
						loungeLamp.turnDeviceOffAutoOverride();
						stickLoungeLamp.turnDeviceOffAutoOverride();
						log.info("Outside brightness has fallen into > 800 bucket, lounge lamp and stick lamp auto off");
					}
				}
				else if (CommonQueries.isBrightnessBetween600and800()) {
					if (!"0".equals(loungeLamp.getDeviceLevel()) && !"0".equals(stickLoungeLamp.getDeviceLevel()) && !loungeLamp.isManuallyOverridden()) {
						loungeLamp.turnDeviceOffAutoOverride();
						stickLoungeLamp.turnDeviceOffAutoOverride();
						log.info("Outside brightness has fallen into 600-800 bucket, lounge lamp and stick lamp auto off");
					}
				}
				else if (CommonQueries.isBrightnessBetween400and600() && now.after(halfThreePM)) {
					if (!"0".equals(loungeLamp.getDeviceLevel()) && !"0".equals(stickLoungeLamp.getDeviceLevel()) && !loungeLamp.isManuallyOverridden()) {
						loungeLamp.turnDeviceOffAutoOverride();
						stickLoungeLamp.turnDeviceOffAutoOverride();
						log.info("Outside brightness has fallen into 400-600 bucket, lounge lamp and stick lamp auto off");
					}
					
					//TODO: Turn on lounge table lamp slightly once bought
				}
				else if (CommonQueries.isBrightnessBetween200and400() && now.after(halfThreePM)) {
					if (!"30".equals(loungeLamp.getDeviceLevel()) && !loungeLamp.isManuallyOverridden()) {
						loungeLamp.turnDeviceOnAutoOverride("30");
						log.info("Outside brightness has fallen into 200-400 bucket, lounge lamp auto on 30%");
					}
				}
				else if (CommonQueries.isBrightnessBetween1and200() && now.after(halfThreePM)) {
					if (!"40".equals(loungeLamp.getDeviceLevel()) && !loungeLamp.isManuallyOverridden()) {
						loungeLamp.turnDeviceOnAutoOverride("40");
						log.info("Outside brightness has fallen into 1-200 bucket, lounge lamp auto up to 40%");
					}
				}
				else if (CommonQueries.isBrightnessAt0() && now.after(halfThreePM)) {
					if (lampsOnFull()) {
						log.info("Outside brightness has fallen into < 1 bucket, lounge lamp auto up to 55% (max)");
						log.info("Outside brightness has fallen into < 1 bucket, lounge stick lamp auto up to 40%");
					}
				}
			}
			
			if (now.before(twoPM)) {
				if (loungeLamp.isDeviceOn() && !loungeLamp.isManuallyOverridden() && loungeLamp.isAutoOverridden()) {
					loungeLamp.turnDeviceOffAutoOverride();
					log.info("Lounge lamp auto off at midnight");
				}
				
				if (stickLoungeLamp.isDeviceOn() && !stickLoungeLamp.isManuallyOverridden() && stickLoungeLamp.isAutoOverridden()) {
					stickLoungeLamp.turnDeviceOffAutoOverride();
					log.info("Lounge stick lamp auto off at midnight");
				}
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
	
	private boolean lampsOnFull() {
		boolean switched = false;
		
		if (!"55".equals(loungeLamp.getDeviceLevel()) && !loungeLamp.isManuallyOverridden()) {
			loungeLamp.turnDeviceOnAutoOverride("55");
			
			switched = true;
		}
		
		if (!"99".equals(stickLoungeLamp.getDeviceLevel()) && !"40".equals(stickLoungeLamp.getDeviceLevel()) && !stickLoungeLamp.isManuallyOverridden()) {
			stickLoungeLamp.turnDeviceOnAutoOverride("40");
			
			switched = true;
		}
		
		return switched;
	}
	
	private void checkOccupancyTimeout() {
		Calendar lastDateOccupied = Calendar.getInstance();
		lastDateOccupied.setTime(new Date(loungeReportingDevice.getLastUpdated()));
		lastDateOccupied.add(Calendar.MINUTE, 60);
		
		Calendar now = Calendar.getInstance();
		
		int index = 1;
		if (now.after(lastDateOccupied)) {
			for (ControllableDevice device : devicesToControl) {
				if (device.isManuallyOverridden() && !device.isAutoOverridden() && !(device instanceof Blind)) {
					log.info("Lounge room unoccupied for more than 1 hour, switching off and resetting flags for lamp " + index);
					device.resetManuallyOverridden();
					device.turnDeviceOff(false);
				}
				index++;
			}
		}
	}
}
