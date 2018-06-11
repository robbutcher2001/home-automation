package co.uk.rob.apartment.automation.model.abstracts;

import java.util.Calendar;
import java.util.Timer;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.ApartmentSirenTrigger;
import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.devices.AlarmUnit;
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
	protected String entrance;

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
				new SpeechOrchestrationManager("<prosody pitch=\"+20%\">Welcome home!</prosody> How <prosody pitch=\"+20%\">are you?</prosody>",
						false, false, false, null, null, null).start();
			}

			log.info(this.entrance + " opened, apartment unoccupied for more than 1 hour, welcoming home");

			if (CommonQueries.isBrightnessBelow20() || CommonQueries.isBrightnessBetweenXandY(20f, 200f)) {
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
					new SpeechOrchestrationManager("</prosody>I've turned some lounge lights <prosody pitch=\"-15%\">on for you.</prosody>",
							false, false, false, null, null, null).start();
				}
			}
		}
	}

	protected void runUnexpectedOccupancyControl() {
		String alarmOneTimeUrl = HomeAutomationProperties.getProperty("AlarmOneTimeUrl");
		if (alarmOneTimeUrl == null || "".equals(alarmOneTimeUrl)) {
			//start indoor alarm as strobe
			AlarmUnit indoorAlarmUnit = (AlarmUnit) DeviceListManager.getControllableDeviceByLocation(Zone.HALLWAY).get(0);
			indoorAlarmUnit.turnDeviceOn(false);

			//handle unexpected occupancy
			HomeAutomationProperties.setOrUpdateProperty("ApartmentUnexpectedOccupancy", "true");

			alarmOneTimeUrl = OneTimeUrlGenerator.getOneTimeString();
			HomeAutomationProperties.setOrUpdateProperty("AlarmOneTimeUrl", alarmOneTimeUrl);
			final String smsText = "Apartment occupied from " + this.entrance.toLowerCase() +
					". Alarm will trigger. Deactivate now: "
					+ "https://robsflat.co.uk/disableApartmentAlarm/" + alarmOneTimeUrl;
			SMSHelper.sendSMS("07965502960", smsText);
			SMSHelper.sendSMS("07909522243", smsText);

			//trigger outdoor AlarmUnit in 1 minute
			//TODO: http://examples.javacodegeeks.com/core-java/util/timer-util/java-timer-example/
			Timer timer = new Timer("Sound alarm in 1 minute");

			ApartmentSirenTrigger alarmOne = new ApartmentSirenTrigger("Alarm now sounding @ 106dB in apartment");
			ApartmentSirenTrigger alarmTwo = new ApartmentSirenTrigger("Alarm now sounding @ 106dB in apartment again (2)");
			ApartmentSirenTrigger alarmThree = new ApartmentSirenTrigger("Alarm now sounding @ 106dB in apartment again (3)");
			ApartmentSirenTrigger alarmFour = new ApartmentSirenTrigger("Alarm now sounding @ 106dB in apartment again (4)");
			ApartmentSirenTrigger alarmFive = new ApartmentSirenTrigger("Alarm now sounding @ 106dB in apartment again (5)");

			timer.schedule(alarmOne, 60000); //1 min
			timer.schedule(alarmTwo, 240000); //4 mins - wait for first siren then wait 2 mins
			timer.schedule(alarmThree, 420000); //7 mins - wait for second siren then wait 2 mins
			timer.schedule(alarmFour, 600000); //10 mins - wait for third siren then wait 2 mins
			timer.schedule(alarmFive, 780000); //13 mins - wait for fourth siren then wait 2 mins
		}
	}
}
