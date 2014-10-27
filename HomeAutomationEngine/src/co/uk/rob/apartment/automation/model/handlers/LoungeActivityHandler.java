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

	private ControllableDevice stickLoungeLamp;
	private ControllableDevice kitchenLedRod;
	private Blind loungeWindowBlind;
	
	@Override
	public void run() {
		stickLoungeLamp = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(2);
		kitchenLedRod = DeviceListManager.getControllableDeviceByLocation(Zone.KITCHEN).get(0);
		loungeWindowBlind = (Blind) DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(3);
		
		if (reportingDevice.isTriggered()) {
			//will now continue turning lights on, opening blinds and speaking if not in bedroom mode
			String loungeBedroomMode = HomeAutomationProperties.getProperty("LoungeBedroomMode");
			if (loungeBedroomMode == null || (loungeBedroomMode != null && "false".equals(loungeBedroomMode))) {
				
				boolean openBlinds = false;
				if (!"55".equals(loungeWindowBlind.getDeviceLevel()) && !"80".equals(loungeWindowBlind.getDeviceLevel())) {
					openBlinds = true;
				}
				
				//only turn on lights if blinds are closed
				if (loungeWindowBlind.isDeviceOn()) {
					if (!stickLoungeLamp.isDeviceOn() && !stickLoungeLamp.isAutoOverridden()) {
						stickLoungeLamp.turnDeviceOn(false, "99");
						log.info("Lounge occupancy detected, not auto overridden and blinds are closed: switching on tall lounge lamp");
					}
					else if ("40".equals(stickLoungeLamp.getDeviceLevel()) && stickLoungeLamp.isAutoOverridden()) {
						stickLoungeLamp.turnDeviceOn(false, "99");
						log.info("Lounge occupancy detected, auto overridden and blinds are closed: turning up tall lounge lamp");
					}
					
					if (!kitchenLedRod.isDeviceOn() && !kitchenLedRod.isAutoOverridden()) {
						kitchenLedRod.turnDeviceOn(false);
						log.info("Lounge occupancy detected, not auto overridden and blinds are closed: switching on kitchen LED rod");
					}
				}
				
				Calendar fiveAM = Calendar.getInstance();
				Calendar nineAM = Calendar.getInstance();
				Calendar midday = Calendar.getInstance();
				Calendar now = Calendar.getInstance();
				
				fiveAM.set(Calendar.HOUR_OF_DAY, 5);
				fiveAM.set(Calendar.MINUTE, 00);
				
				nineAM.set(Calendar.HOUR_OF_DAY, 9);
				nineAM.set(Calendar.MINUTE, 00);
				
				midday.set(Calendar.HOUR_OF_DAY, 12);
				midday.set(Calendar.MINUTE, 00);
				
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
				else if (now.after(midday)) {
					//tilt blinds if not tilted
					if (!loungeWindowBlind.isTilted()) {
						if (CommonQueries.isBrightnessGreaterThan800() && ("40".equals(loungeWindowBlind.getDeviceLevel()) ||
								"55".equals(loungeWindowBlind.getDeviceLevel()) ||
								"80".equals(loungeWindowBlind.getDeviceLevel()))) {
							log.info("Blinds aren't tilted and it's light enough outside, tilting now someone's home");
							loungeWindowBlind.tiltBlindDown();
						}
					}
				}
			}
			else {
				stickLoungeLamp.turnDeviceOn(false, "30");
				log.info("Lounge occupancy detected, nobody has overridden but bedroom mode is on: switching on tall lounge lamp at 30%");
			}
		}
		else {
			if (stickLoungeLamp.isDeviceOn() && !stickLoungeLamp.isAutoOverridden() && !stickLoungeLamp.isManuallyOverridden()) {
				stickLoungeLamp.turnDeviceOff(false);
				log.info("Lounge not occupied and nobody has overridden and lamp not auto overridden: switching off tall lounge lamp");
			}
			else if ("99".equals(stickLoungeLamp.getDeviceLevel()) && stickLoungeLamp.isAutoOverridden()) {
				stickLoungeLamp.turnDeviceOn(false, "40");
				log.info("Lounge not occupied and nobody has overridden: switching down tall lounge lamp");
			}
			
			if (kitchenLedRod.isDeviceOn() && !kitchenLedRod.isAutoOverridden() && !kitchenLedRod.isManuallyOverridden()) {
				kitchenLedRod.turnDeviceOff(false);
				log.info("Lounge not occupied and nobody has overridden: switching off kitchen LED rod");
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
	}
}