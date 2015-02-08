package co.uk.rob.apartment.automation.model.handlers;

import java.util.Calendar;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractActivityHandler;
import co.uk.rob.apartment.automation.model.devices.Blind;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;
import co.uk.rob.apartment.automation.utilities.SpeechOrchestrationManager;

public class LoungeActivityHandler extends AbstractActivityHandler {
	
	private Logger log = Logger.getLogger(LoungeActivityHandler.class);

	private ControllableDevice loungeLamp;
	private ControllableDevice stickLoungeLamp;
	private ControllableDevice kitchenLedRod;
	private Blind loungeWindowBlind;
	private Blind loungePatioBlind;
	
	@Override
	public void run() {
		loungeLamp = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(0);
		stickLoungeLamp = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(2);
		kitchenLedRod = DeviceListManager.getControllableDeviceByLocation(Zone.KITCHEN).get(0);
		loungeWindowBlind = (Blind) DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(3);
		loungePatioBlind = (Blind) DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(4);
		
		if (reportingDevice.isTriggered()) {
			//will now continue turning lights on, opening blinds and speaking if not in bedroom mode
			String loungeBedroomMode = HomeAutomationProperties.getProperty("LoungeBedroomMode");
			if (loungeBedroomMode == null || (loungeBedroomMode != null && "false".equals(loungeBedroomMode))) {
				
				Calendar fiveAM = Calendar.getInstance();
				Calendar nineAM = Calendar.getInstance();
				Calendar midday = Calendar.getInstance();
				Calendar threePM = Calendar.getInstance();
				Calendar now = Calendar.getInstance();
				
				fiveAM.set(Calendar.HOUR_OF_DAY, 5);
				fiveAM.set(Calendar.MINUTE, 00);
				
				nineAM.set(Calendar.HOUR_OF_DAY, 9);
				nineAM.set(Calendar.MINUTE, 00);
				
				midday.set(Calendar.HOUR_OF_DAY, 12);
				midday.set(Calendar.MINUTE, 00);
				
				threePM.set(Calendar.HOUR_OF_DAY, 15);
				threePM.set(Calendar.MINUTE, 00);
				
				boolean openBlinds = false;
				if (!"55".equals(loungeWindowBlind.getDeviceLevel()) && !"80".equals(loungeWindowBlind.getDeviceLevel()) &&
						!"55".equals(loungePatioBlind.getDeviceLevel()) && !"80".equals(loungePatioBlind.getDeviceLevel())) {
					openBlinds = true;
				}
				
				//only turn on lights if blinds are not open max
				if (loungeWindowBlind.isDeviceOn() && loungePatioBlind.isDeviceOn()) {
					if (!stickLoungeLamp.isDeviceOn() && !stickLoungeLamp.isAutoOverridden() && !stickLoungeLamp.isManuallyOverridden()) {
						stickLoungeLamp.turnDeviceOn(false, "99");
						log.info("Lounge occupancy detected, not auto overridden and blinds are closed: switching on stick lounge lamp");
					}
					else if ("40".equals(stickLoungeLamp.getDeviceLevel()) && stickLoungeLamp.isAutoOverridden() && !stickLoungeLamp.isManuallyOverridden()) {
						stickLoungeLamp.turnDeviceOn(false, "99");
						log.info("Lounge occupancy detected, auto overridden and blinds are closed: turning up stick lounge lamp");
					}
					
					if (!kitchenLedRod.isDeviceOn() && !kitchenLedRod.isAutoOverridden()) {
						kitchenLedRod.turnDeviceOn(false);
						log.info("Lounge occupancy detected, not auto overridden and blinds are closed: switching on kitchen LED rod");
					}
					
					if (!loungeLamp.isDeviceOn() && !loungeLamp.isAutoOverridden() && !loungeLamp.isManuallyOverridden() && now.after(fiveAM) &&
							(CommonQueries.isBrightnessBelow20() || CommonQueries.isBrightnessBetween20and200() || CommonQueries.isBrightnessBetween200and400())) {
						loungeLamp.turnDeviceOn(false);
						log.info("Lounge occupancy detected, not auto overridden or manually overridden, blinds are closed and it's dark outside: switching on tall lounge lamp");
					}
				}
				
				ReportingDevice robRoomDoorSensor = DeviceListManager.getReportingDeviceByLocation(Zone.ROB_ROOM).get(1);
				
				//if it's a weekday, speak and move blinds after 5am
				//if it's a weekend, speak and move blinds after 9am
				if ((now.after(fiveAM) && now.before(midday) && !CommonQueries.isItTheWeekendOrBankHoliday()) || 
						(now.after(nineAM) && now.before(midday) && CommonQueries.isItTheWeekendOrBankHoliday())) {
					String played = HomeAutomationProperties.getProperty("LoungeWelcomedRob");
					if (played != null && "false".equals(played) && robRoomDoorSensor.isTriggered()) {
						HomeAutomationProperties.setOrUpdateProperty("LoungeWelcomedRob", "true");
						String robWelcomeText = "Good morning Robert.";
						if (openBlinds) {
							robWelcomeText += " I'll open the blinds slightly for you.";
						}
						if (!CommonQueries.isItTheWeekendOrBankHoliday()) {
							log.info("Saying good morning to Rob, opening blinds, relaying current weather information, latest train information and M25 traffic report");
							new SpeechOrchestrationManager(robWelcomeText, true, true, true, "0815").start();
						}
						else {
							log.info("Saying generic good morning to Rob and opening blinds");
							new SpeechOrchestrationManager(robWelcomeText, false, false, false, null).start();
						}
						
						if (openBlinds) {
							runBlindControl();
						}
					}
					
					played = HomeAutomationProperties.getProperty("LoungeWelcomedScarlett");
					if (played != null && "false".equals(played) && !robRoomDoorSensor.isTriggered()) {
						HomeAutomationProperties.setOrUpdateProperty("LoungeWelcomedScarlett", "true");
						String scarlettWelcomeText = "Good morning Scarlett.";
						if (openBlinds) {
							scarlettWelcomeText += " I'll open the blinds slightly for you.";
						}
						if (!CommonQueries.isItTheWeekendOrBankHoliday()) {
							log.info("Saying good morning to Scarlett, opening blinds, relaying current weather information and latest train information");
							new SpeechOrchestrationManager(scarlettWelcomeText, true, true, false, "0745").start();
						}
						else {
							log.info("Saying generic good morning to Scarlett and opening blinds");
							new SpeechOrchestrationManager(scarlettWelcomeText, false, false, false, null).start();
						}
						
						if (openBlinds) {
							runBlindControl();
						}
					}
				}
				
				//tilt blinds if not tilted but on Friday, only tilt if it's between 09:00-12:00 or after 15:00
				if ((now.after(nineAM) && now.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) ||
						(now.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && (now.after(nineAM) && now.before(midday) || now.after(threePM)))) {
					boolean tilted = false;
					if (!loungeWindowBlind.isTilted()) {
						if ((CommonQueries.isBrightnessBetween600and800() || CommonQueries.isBrightnessGreaterThan800()) && !"0".equals(loungeWindowBlind.getDeviceLevel())) {
							tilted = loungeWindowBlind.tiltBlindOpen();
						}
					}
					
					if (!loungePatioBlind.isTilted()) {
						if ((CommonQueries.isBrightnessBetween600and800() || CommonQueries.isBrightnessGreaterThan800()) && !"0".equals(loungePatioBlind.getDeviceLevel())) {
							tilted = loungePatioBlind.tiltBlindOpen();
						}
					}
					
					if (tilted) {
						log.info("Blinds aren't tilted and it's light enough outside, tilting now someone's home");
					}
				}
			}
			else {
				stickLoungeLamp.turnDeviceOn(false, "30");
				log.info("Lounge occupancy detected, nobody has overridden but bedroom mode is on: switching on stick lounge lamp at 30%");
			}
		}
		else {
			if (stickLoungeLamp.isDeviceOn() && !stickLoungeLamp.isAutoOverridden() && !stickLoungeLamp.isManuallyOverridden()) {
				stickLoungeLamp.turnDeviceOff(false);
				log.info("Lounge not occupied and nobody has overridden and lamp not auto overridden: switching off stick lounge lamp");
			}
			else if ("99".equals(stickLoungeLamp.getDeviceLevel()) && stickLoungeLamp.isAutoOverridden()) {
				stickLoungeLamp.turnDeviceOn(false, "40");
				log.info("Lounge not occupied and nobody has overridden: switching down stick lounge lamp");
			}
			
			if (kitchenLedRod.isDeviceOn() && !kitchenLedRod.isAutoOverridden() && !kitchenLedRod.isManuallyOverridden()) {
				kitchenLedRod.turnDeviceOff(false);
				log.info("Lounge not occupied and nobody has overridden: switching off kitchen LED rod");
			}
			
			if (loungeLamp.isDeviceOn() && !loungeLamp.isAutoOverridden() && !loungeLamp.isManuallyOverridden()) {
				loungeLamp.turnDeviceOff(false);
				log.info("Lounge not occupied and nobody has overridden: switching off tall lounge lamp");
			}
		}
	}
	
	private void runBlindControl() {
		try {
			Thread.sleep(6500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		loungeWindowBlind.turnDeviceOn(false, "55");
		loungePatioBlind.turnDeviceOn(false, "55");
	}
}
