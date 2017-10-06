package co.uk.rob.apartment.automation.model.handlers;

import java.util.Calendar;
import java.util.Random;

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
	private ControllableDevice bobbyLoungeLamp;
	private Blind loungeWindowBlind;
	private Blind loungePatioBlind;
	private ReportingDevice patioDoor;
	private static final Random random;
	
	private static final String[] aliceNames =
		{"Alice", "Bum", "Slawice", "Ali-bum-bum", "Ali", "Slice", "Slices", "Queso", "Suplemento de Queso"};
	
	static {
		random = new Random();
	}
	
	@Override
	public void run() {
		loungeLamp = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(0);
		stickLoungeLamp = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(2);
		kitchenLedRod = DeviceListManager.getControllableDeviceByLocation(Zone.KITCHEN).get(0);
		bobbyLoungeLamp = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(5);
		loungeWindowBlind = (Blind) DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(3);
		loungePatioBlind = (Blind) DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(4);
		patioDoor = DeviceListManager.getReportingDeviceByLocation(Zone.PATIO).get(1);
		
		Calendar now = Calendar.getInstance();
		Calendar eightPM = (Calendar) now.clone();
		
		eightPM.set(Calendar.HOUR_OF_DAY, 20);
		eightPM.set(Calendar.MINUTE, 00);
		
		if (reportingDevice.isTriggered()) {
			//will now continue turning lights on, opening blinds and speaking if not in bedroom mode
			String loungeBedroomMode = HomeAutomationProperties.getProperty("LoungeBedroomMode");
			if (loungeBedroomMode == null || (loungeBedroomMode != null && "false".equals(loungeBedroomMode))) {
				
				Calendar fiveAM = (Calendar) now.clone();
				Calendar eightAM = (Calendar) now.clone();
				Calendar nineAM = (Calendar) now.clone();
				Calendar elevenAM = (Calendar) now.clone();
				Calendar midday = (Calendar) now.clone();
				Calendar threePM = (Calendar) now.clone();
				
				fiveAM.set(Calendar.HOUR_OF_DAY, 5);
				fiveAM.set(Calendar.MINUTE, 00);
				
				eightAM.set(Calendar.HOUR_OF_DAY, 8);
				eightAM.set(Calendar.MINUTE, 00);
				
				nineAM.set(Calendar.HOUR_OF_DAY, 9);
				nineAM.set(Calendar.MINUTE, 00);
				
				elevenAM.set(Calendar.HOUR_OF_DAY, 11);
				elevenAM.set(Calendar.MINUTE, 00);
				
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
					else if ("40".equals(stickLoungeLamp.getDeviceLevel()) && !"99".equals(stickLoungeLamp.getDeviceLevel()) &&
							stickLoungeLamp.isAutoOverridden() && !stickLoungeLamp.isManuallyOverridden()) {
						stickLoungeLamp.turnDeviceOn(false, "99");
						log.info("Lounge occupancy detected, auto overridden and blinds are closed: turning up stick lounge lamp");
					}
					
					if (!kitchenLedRod.isDeviceOn() && !kitchenLedRod.isAutoOverridden() && !kitchenLedRod.isManuallyOverridden()) {
						kitchenLedRod.turnDeviceOn(false);
						log.info("Lounge occupancy detected, not auto overridden and blinds are closed: switching on kitchen LED rod");
					}
					
					if (!loungeLamp.isDeviceOn() && !loungeLamp.isAutoOverridden() && !loungeLamp.isManuallyOverridden() && now.after(fiveAM) && !patioDoor.isTriggered() &&
							(CommonQueries.isBrightnessBelow20() || CommonQueries.isBrightnessBetweenXandY(20f, 200f) || CommonQueries.isBrightnessBetweenXandY(200f, 400f))) {
						loungeLamp.turnDeviceOn(false);
						log.info("Lounge occupancy detected, not auto overridden or manually overridden, blinds are closed and it's dark outside: switching on tall lounge lamp");
					}
					
					if (now.after(eightPM) || now.before(nineAM)) {
						if (!"60".equals(bobbyLoungeLamp.getDeviceLevel()) && !bobbyLoungeLamp.isManuallyOverridden() && !patioDoor.isTriggered()) {
							bobbyLoungeLamp.turnDeviceOn(false, "60");
							log.info("Lounge occupancy detected, not auto overridden and blinds are closed: turning up Bobby lounge lamp");
						}
					}
				}
				
				//if it's a weekday, speak and move blinds after 5am
				if (now.after(fiveAM) && now.before(midday) && !CommonQueries.isItTheWeekendOrBankHoliday()) {
					if (openBlinds) {
						runBlindControl();
					}
					
					String played = HomeAutomationProperties.getProperty("LoungeWelcomedSlice");
					if (played != null && "false".equals(played)) {
						HomeAutomationProperties.setOrUpdateProperty("LoungeWelcomedSlice", "true");
						
						int nameToUse = random.nextInt(aliceNames.length);
						String sliceWelcomeText = "Good morning <prosody pitch=\"-25%\">" + aliceNames[nameToUse] + ". </prosody>";
						if (openBlinds) {
							sliceWelcomeText += "I've opened the blinds a <prosody pitch=\"-15%\">little for </prosody>you. ";
						}
						
						log.info("Saying good morning to Slice, opening blinds, relaying current weather information, latest train information and BBC News headlines");
						new SpeechOrchestrationManager(sliceWelcomeText, true, true, true, "0750", "NWD/VIC").start();
					}
					
					played = HomeAutomationProperties.getProperty("LoungeWelcomedRob");
					if (played != null && "false".equals(played)) {
						HomeAutomationProperties.setOrUpdateProperty("LoungeWelcomedRob", "true");
						
						String robWelcomeText = "Good morning <prosody pitch=\"+25%\">Robert. </prosody>";
						if (openBlinds) {
							robWelcomeText += "I've opened the blinds a <prosody pitch=\"-15%\">little for </prosody>you. ";
						}
						
						log.info("Saying good morning to Rob, opening blinds, relaying current weather information, latest train information and BBC News headlines");
						new SpeechOrchestrationManager(robWelcomeText, true, true, true, "0845", "NWD/LBG").start();
					}
				}
				
				//if it's a weekend, speak and move blinds after 9am
				if (now.after(nineAM) && now.before(midday) && CommonQueries.isItTheWeekendOrBankHoliday()) {
					if (openBlinds) {
						runBlindControl();
					}
					
					String played = HomeAutomationProperties.getProperty("LoungeWelcomedSlice");
					played = HomeAutomationProperties.getProperty("LoungeWelcomedRob");
					
					if (played != null && "false".equals(played)) {
						HomeAutomationProperties.setOrUpdateProperty("LoungeWelcomedSlice", "true");
						HomeAutomationProperties.setOrUpdateProperty("LoungeWelcomedRob", "true");
						
						String welcomeText = "Thank <prosody pitch=\"+40%\" volume=\"+10dB\">fuck</prosody> it's the weekend. ";
						
						if (now.after(elevenAM)) {
							welcomeText += "Although getting up after 11 is taking the piss a bit. LOL.";
						}
						else {
							welcomeText += "<break time=\"1s\"/> Sick. ";
						}
						
						if (openBlinds) {
							welcomeText += "Anyway, I've opened the blinds a <prosody pitch=\"-15%\">little for </prosody>you both. ";
						}
						
						log.info("Saying generic good morning to Slib and opening blinds");
						new SpeechOrchestrationManager(welcomeText, true, false, true, null, null).start();
					}
				}
				
				//tilt blinds if not tilted but on Friday, only tilt if it's between 09:00-12:00 or after 15:00
				if ((now.after(nineAM) && now.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) ||
						(now.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && (now.after(nineAM) && now.before(midday) || now.after(threePM)))) {
					boolean tilted = false;
					if (!loungeWindowBlind.isTilted() && !"100".equals(loungeWindowBlind.getDeviceLevel())) {
						if ((CommonQueries.isBrightnessBetweenXandY(600f, 800f) || CommonQueries.isBrightnessGreaterThan800()) && !"0".equals(loungeWindowBlind.getDeviceLevel())) {
							tilted = loungeWindowBlind.tiltBlindOpen();
						}
					}
					
					if (!loungePatioBlind.isTilted() && !"100".equals(loungePatioBlind.getDeviceLevel())) {
						if ((CommonQueries.isBrightnessBetweenXandY(600f, 800f) || CommonQueries.isBrightnessGreaterThan800()) && !"0".equals(loungePatioBlind.getDeviceLevel())) {
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
			
			if (bobbyLoungeLamp.isDeviceOn() && !bobbyLoungeLamp.isAutoOverridden() && !bobbyLoungeLamp.isManuallyOverridden()) {
				bobbyLoungeLamp.turnDeviceOff(false);
				log.info("Lounge not occupied and nobody has overridden and lamp not auto overridden: switching off Bobby lounge lamp");
			}
			else if ("60".equals(bobbyLoungeLamp.getDeviceLevel()) && bobbyLoungeLamp.isAutoOverridden() && now.after(eightPM)) {
				bobbyLoungeLamp.turnDeviceOn(false, "20");
				log.info("Lounge not occupied and nobody has overridden: switching down Bobby lounge lamp");
			}
		}
	}
	
	private void runBlindControl() {
		loungeWindowBlind.turnDeviceOn(false, "55");
		loungePatioBlind.turnDeviceOn(false, "55");
	}
}
