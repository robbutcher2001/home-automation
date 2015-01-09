package co.uk.rob.apartment.automation.model.abstracts;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;
import co.uk.rob.apartment.automation.utilities.OneTimeUrlGenerator;
import co.uk.rob.apartment.automation.utilities.SMSHelper;
import co.uk.rob.apartment.automation.utilities.SpeechOrchestrationManager;


public abstract class AbstractExternalDoorActivityHandler extends AbstractActivityHandler {
	
	private Logger log = Logger.getLogger(AbstractExternalDoorActivityHandler.class);
	
	private ControllableDevice loungeLamp;
	private ControllableDevice stickLoungeLamp;
	protected String door;
	
	protected void welcomeHome() {
		Calendar now = Calendar.getInstance();
		
		Calendar lastApartmentOccupancyPlusHour = CommonQueries.getLastApartmentOccupancyTime();
		if (lastApartmentOccupancyPlusHour != null) {
			lastApartmentOccupancyPlusHour.add(Calendar.MINUTE, 60);
		}
		
		if (lastApartmentOccupancyPlusHour != null && now.after(lastApartmentOccupancyPlusHour)) {
			String played = HomeAutomationProperties.getProperty("ApartmentWelcomeHome");
			if (played != null && "false".equals(played)) {
				HomeAutomationProperties.setOrUpdateProperty("ApartmentWelcomeHome", "true");
				new SpeechOrchestrationManager("Welcome home!", false, false, false, null).start();
			}
			
			log.info(this.door + " door opened, apartment unoccupied for more than 1 hour, welcoming home");
			
			if (CommonQueries.isBrightnessBelow20() || CommonQueries.isBrightnessBetween20and200()) {
				boolean lampsTurnedOn = false;
				
				loungeLamp = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(0);
				stickLoungeLamp = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(2);
				
				if (!stickLoungeLamp.isDeviceOn() && !stickLoungeLamp.isAutoOverridden() && !stickLoungeLamp.isManuallyOverridden()) {
					lampsTurnedOn = true;
					log.info("Dark enough outside and stick lounge lamp isn't on, welcoming people home with this lamp");
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						//no op
					}
					
					stickLoungeLamp.turnDeviceOnAutoOverride("99");
				}
				
				if (!loungeLamp.isDeviceOn() && !loungeLamp.isAutoOverridden() && !loungeLamp.isManuallyOverridden()) {
					lampsTurnedOn = true;
					log.info("Dark enough outside and tall lounge lamp isn't on, welcoming people home with this lamp");
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						//no op
					}
					
					loungeLamp.turnDeviceOnAutoOverride("55");
				}
				
				if (lampsTurnedOn) {
					new SpeechOrchestrationManager("I've turned some lounge lights on for you.", false, false, false, null).start();
				}
			}
		}
	}
	
	protected void runUnexpectedOccupancyControl() {
		//handle unexpected occupancy
		HomeAutomationProperties.setOrUpdateProperty("ApartmentUnexpectedOccupancy", "true");
		
		final String alarmOneTimeUrl = OneTimeUrlGenerator.getOneTimeString();
		HomeAutomationProperties.setOrUpdateProperty("AlarmOneTimeUrl", alarmOneTimeUrl);
		final String smsText = "Apartment occupied from " + this.door.toLowerCase() + 
				" door. Alarm will trigger. Deactivate now: "
				+ "http://robsflat.noip.me/disableApartmentAlarm/" + alarmOneTimeUrl;
		SMSHelper.sendSMS("07965502960", smsText);
		SMSHelper.sendSMS("07875468023", smsText);
		
		//trigger outdoor AlarmUnit in 1 minute
		//TODO: http://examples.javacodegeeks.com/core-java/util/timer-util/java-timer-example/
		Timer timer = new Timer("Sound alarm in 1 minute");
		
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				
				final String alarmOneTimeUrl = HomeAutomationProperties.getProperty("AlarmOneTimeUrl");
    			if (!"".equals(alarmOneTimeUrl)) {
    				ControllableDevice outdoorAlarmUnit = DeviceListManager.getControllableDeviceByLocation(Zone.PATIO).get(0);
					outdoorAlarmUnit.turnDeviceOn(false);
					SMSHelper.sendSMS("07965502960", "Alarm now sounding @ 106dB in apartment");
					
					log.info("ALARM TRIGGERED - now sounding @ 106dB");
    			}
    			else {
    				log.info("Alarm successfully disarmed within one minute - siren cancelled");
    			}
			}
		};
		
		timer.schedule(task, 60000);
	}
}
