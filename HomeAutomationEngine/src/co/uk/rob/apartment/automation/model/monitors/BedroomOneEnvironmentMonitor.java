package co.uk.rob.apartment.automation.model.monitors;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.devices.ElectricBlanket;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;
import co.uk.rob.apartment.automation.utilities.SMSHelper;

public class BedroomOneEnvironmentMonitor extends Thread {
	
	private Logger log = Logger.getLogger(BedroomOneEnvironmentMonitor.class);
	
	private List<ControllableDevice> devicesToControl;
	private ControllableDevice lamp;
	private ControllableDevice dehumidifier;
	private ControllableDevice ledRod;
	private ControllableDevice electricBlanket;
	private ReportingDevice motionSensor;
	private ReportingDevice doorSensor;
	private ReportingDevice windowSensor;
	private ReportingDevice outsideMotionSensor;
	private Random randomLightsOff;
	private int randomMinuteLightsOff;
	
	public BedroomOneEnvironmentMonitor() {
		devicesToControl = DeviceListManager.getControllableDeviceByLocation(Zone.ROB_ROOM);
		lamp = devicesToControl.get(0);
		dehumidifier = devicesToControl.get(2);
		ledRod = devicesToControl.get(3);
		electricBlanket = devicesToControl.get(4);
		
		motionSensor = DeviceListManager.getReportingDeviceByLocation(Zone.ROB_ROOM).get(0);
		doorSensor = DeviceListManager.getReportingDeviceByLocation(Zone.ROB_ROOM).get(1);
		windowSensor = DeviceListManager.getReportingDeviceByLocation(Zone.ROB_ROOM).get(2);
		outsideMotionSensor = DeviceListManager.getReportingDeviceByLocation(Zone.PATIO).get(0);
		
		randomLightsOff = new Random();
		randomMinuteLightsOff = randomLightsOff.nextInt(60);
		
		log.info("Rob room environment monitor started");
	}
	
	@Override
	public void run() {
		while (!this.isInterrupted()) {
			Calendar now = Calendar.getInstance();
			Calendar midday = (Calendar) now.clone();
			Calendar eightAM = (Calendar) now.clone();
			Calendar twoPM = (Calendar) now.clone();
			Calendar halfThreePM = (Calendar) now.clone();
			Calendar ninePM = (Calendar) now.clone();
			Calendar tenPM = (Calendar) now.clone();
			Calendar elevenPM = (Calendar) now.clone();
			Calendar elevenPmRandomMinute = (Calendar) now.clone();
			
			midday.set(Calendar.HOUR_OF_DAY, 12);
			midday.set(Calendar.MINUTE, 00);
			
			eightAM.set(Calendar.HOUR_OF_DAY, 8);
			eightAM.set(Calendar.MINUTE, 00);
			
			twoPM.set(Calendar.HOUR_OF_DAY, 14);
			twoPM.set(Calendar.MINUTE, 00);
			
			halfThreePM.set(Calendar.HOUR_OF_DAY, 15);
			halfThreePM.set(Calendar.MINUTE, 30);
			
			ninePM.set(Calendar.HOUR_OF_DAY, 21);
			ninePM.set(Calendar.MINUTE, 00);
			
			tenPM.set(Calendar.HOUR_OF_DAY, 22);
			tenPM.set(Calendar.MINUTE, 00);
			
			elevenPM.set(Calendar.HOUR_OF_DAY, 23);
			elevenPM.set(Calendar.MINUTE, 00);
			
			elevenPmRandomMinute.set(Calendar.HOUR_OF_DAY, 23);
			elevenPmRandomMinute.set(Calendar.MINUTE, randomMinuteLightsOff);
			
			//bedroom mode not enabled
			String robRoomBedroomMode = HomeAutomationProperties.getProperty("RobRoomBedroomMode");
			if (robRoomBedroomMode == null || (robRoomBedroomMode != null && "false".equals(robRoomBedroomMode))) {
				//lighting
				if (now.after(twoPM) && now.before(elevenPM) && doorSensor.isTriggered()) {
					if (CommonQueries.isBrightnessGreaterThan800()) {
						if (!"0".equals(lamp.getDeviceLevel()) && !lamp.isManuallyOverridden()) {
							lamp.turnDeviceOffAutoOverride();
							log.info("Outside brightness has fallen into > 800 bucket, Rob's room lamp auto off");
						}
						
						if (ledRod.isDeviceOn() && !ledRod.isManuallyOverridden()) {
							ledRod.turnDeviceOffAutoOverride();
							log.info("Outside brightness has fallen into > 800 bucket, Rob's room LED rod auto off");
						}
					}
					else if (CommonQueries.isBrightnessBetween600and800()) {
						if (!"0".equals(lamp.getDeviceLevel()) && !lamp.isManuallyOverridden()) {
							lamp.turnDeviceOffAutoOverride();
							log.info("Outside brightness has fallen into 600-800 bucket, Rob's room lamp auto off");
						}
						
						if (ledRod.isDeviceOn() && !ledRod.isManuallyOverridden()) {
							ledRod.turnDeviceOffAutoOverride();
							log.info("Outside brightness has fallen into 600-800 bucket, Rob's room LED rod auto off");
						}
					}
					else if (CommonQueries.isBrightnessBetween400and600() && now.after(halfThreePM)) {
						if (!"40".equals(lamp.getDeviceLevel()) && !lamp.isManuallyOverridden()) {
							lamp.turnDeviceOnAutoOverride("40");
							log.info("Outside brightness has fallen into 400-600 bucket, Rob's room lamp auto up to 40%");
						}
						
						if (ledRod.isDeviceOn() && !ledRod.isManuallyOverridden()) {
							ledRod.turnDeviceOffAutoOverride();
							log.info("Outside brightness has fallen into 400-600 bucket, Rob's room LED rod auto off");
						}
					}
					else if (CommonQueries.isBrightnessBetween200and400() && now.after(halfThreePM)) {
						if (!"60".equals(lamp.getDeviceLevel()) && !lamp.isManuallyOverridden()) {
							lamp.turnDeviceOnAutoOverride("60");
							log.info("Outside brightness has fallen into 200-400 bucket, Rob's room lamp auto up to 60%");
						}
						
						if (ledRod.isDeviceOn() && !ledRod.isManuallyOverridden()) {
							ledRod.turnDeviceOffAutoOverride();
							log.info("Outside brightness has fallen into 200-400 bucket, Rob's room LED rod auto off");
						}
					}
					else if (CommonQueries.isBrightnessBetween20and200() && now.after(halfThreePM)) {
						if (!"60".equals(lamp.getDeviceLevel()) && !lamp.isManuallyOverridden()) {
							lamp.turnDeviceOnAutoOverride("60");
							log.info("Outside brightness has fallen into 20-200 bucket, Rob's room lamp auto up to 60%");
						}
						
						if (ledRod.isDeviceOn() && !ledRod.isManuallyOverridden()) {
							ledRod.turnDeviceOffAutoOverride();
							log.info("Outside brightness has fallen into 20-200 bucket, Rob's room LED rod auto off");
						}
					}
					else if (CommonQueries.isBrightnessBelow20() && now.after(halfThreePM)) {
						if (!"60".equals(lamp.getDeviceLevel()) && !lamp.isManuallyOverridden()) {
							lamp.turnDeviceOnAutoOverride("60");
							log.info("Outside brightness is below 20, Rob's room lamp auto up to 60%");
						}
						
						if (!ledRod.isDeviceOn() && !ledRod.isManuallyOverridden()) {
							ledRod.turnDeviceOnAutoOverride("99");
							log.info("Outside brightness is below 20, Rob's room LED rod auto on");
						}
					}
				}
				
				if (!CommonQueries.isApartmentOccupied() && now.after(elevenPmRandomMinute)) {
					if (lamp.isDeviceOn() && !lamp.isManuallyOverridden() && lamp.isAutoOverridden()) {
						lamp.turnDeviceOffAutoOverride();
						log.info("Rob's room lamp auto off at randomised 23:" + randomMinuteLightsOff + " as apartment is unoccupied");
					}
					
					if (ledRod.isDeviceOn() && !ledRod.isManuallyOverridden() && ledRod.isAutoOverridden()) {
						ledRod.turnDeviceOffAutoOverride();
						log.info("Rob's room LED rod auto off at randomised 23:" + randomMinuteLightsOff + " as apartment is unoccupied");
						
						//generate random minute for next day
						randomMinuteLightsOff = randomLightsOff.nextInt(60);
					}
				}
				
				//switch lights off at midnight if they're still on regardless - i.e. above randomised time didn't apply
				if (now.before(twoPM)) {
					if (lamp.isDeviceOn() && !lamp.isManuallyOverridden() && lamp.isAutoOverridden()) {
						lamp.turnDeviceOffAutoOverride();
						log.info("Rob's room lamp auto off at midnight");
					}
					
					if (ledRod.isDeviceOn() && !ledRod.isManuallyOverridden() && ledRod.isAutoOverridden()) {
						ledRod.turnDeviceOffAutoOverride();
						log.info("Rob's room LED rod auto off at midnight");
					}
				}
				
				//humidity
				Float[] humidity = motionSensor.getHumidity();
				
				if (humidity[0] > 65f && humidity[1] > 65f && humidity[2] > 65f && !dehumidifier.isDeviceOn()
						&& now.before(tenPM) && now.after(eightAM) && !dehumidifier.isManuallyOverridden() &&
						doorSensor.isTriggered() && (!CommonQueries.isItTheWeekendOrBankHoliday() || now.after(midday))) {
					log.info("Humidity in apartment is above 65%, initialising dehumidification");
					dehumidifier.turnDeviceOn(false);
				}
				else if (humidity[0] < 60f && humidity[1] < 60f && humidity[2] < 60f && dehumidifier.isDeviceOn()) {
					log.info("Humidity in apartment is below 60%, stopping dehumidification");
					dehumidifier.turnDeviceOff(false);
				}
				else if ((now.after(tenPM) || now.before(eightAM)) && dehumidifier.isDeviceOn()) {
					log.info("Humidity in apartment is still high, turning off for the evening and resuming in the morning");
					dehumidifier.turnDeviceOff(false);
				}
			}
			
			//turn on electric blanket if it's cold outside, after 10pm and apartment is occupied
			Float[] outsideTemperatures = outsideMotionSensor.getTemperature();
			if (now.after(ninePM) && doorSensor.isTriggered() && outsideTemperatures[0] < 5f &&
					CommonQueries.isApartmentOccupied() && !electricBlanket.isDeviceOn() &&
					!electricBlanket.isManuallyOverridden() && !electricBlanket.isAutoOverridden()) {
				log.info("Turning on electric blanket as it's cold outside, after 9pm and apartment is occupied");
				electricBlanket.turnDeviceOnAutoOverride("100");
			}
			
			//turn off blanket if it's after midnight and it's on
			if (now.before(twoPM) && electricBlanket.isDeviceOn() && !electricBlanket.isManuallyOverridden()) {
				log.info("Auto turning off electric blanket now it's been on for a couple of hours");
				electricBlanket.turnDeviceOffAutoOverride();
			}
			
			//check bedroom window is now shut
			String robWindowWarningSent = HomeAutomationProperties.getProperty("RobWindowWarningSent");
			if (robWindowWarningSent == null || (robWindowWarningSent != null && "false".equals(robWindowWarningSent))) {
				if (now.after(tenPM) && CommonQueries.isBrightnessBelow20() && windowSensor.isTriggered()) {
					final String smsText = "Warning: your bedroom window is still open and it's now dark";
					SMSHelper.sendSMS("07965502960", smsText);
					HomeAutomationProperties.setOrUpdateProperty("RobWindowWarningSent", "true");
					log.info("Sending warning text to Rob as it's dark, after 10pm and his window is still open");
				}
			}
			
			try {
				int oneMinute = 60000;
				Thread.sleep(oneMinute);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			checkOccupancyTimeout();
		}
	}
	
	private void checkOccupancyTimeout() {
		Calendar lastDateOccupied = Calendar.getInstance();
		lastDateOccupied.setTime(new Date(motionSensor.getLastUpdated()));
		lastDateOccupied.add(Calendar.MINUTE, 60);
		
		Calendar now = Calendar.getInstance();
		
		int index = 1;
		if (now.after(lastDateOccupied)) {
			for (ControllableDevice device : devicesToControl) {
				if (device.isManuallyOverridden() && !device.isAutoOverridden() && !(device instanceof ElectricBlanket)) {
					log.info("Rob room unoccupied for more than 1 hour, resetting overridden flags for device " + index);
					device.resetManuallyOverridden();
					device.turnDeviceOff(false);
				}
				
				index++;
			}
		}
		
		for (ControllableDevice device : devicesToControl) {
			if (device instanceof ElectricBlanket && device.isDeviceOn() && ((ElectricBlanket) device).isTimeToSwitchOff() && device.isManuallyOverridden()) {
				log.info("Switching electric blanket off now it's timed out");
				device.resetManuallyOverridden();
				device.turnDeviceOff(false);
			}
		}
	}
}
