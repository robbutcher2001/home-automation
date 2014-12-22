package co.uk.rob.apartment.automation.model.handlers;

import java.util.Calendar;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractExternalDoorActivityHandler;
import co.uk.rob.apartment.automation.model.devices.Blind;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;
import co.uk.rob.apartment.automation.utilities.OneTimeUrlGenerator;
import co.uk.rob.apartment.automation.utilities.SMSHelper;

public class PatioDoorActivityHandler extends AbstractExternalDoorActivityHandler {
	
	private Logger log = Logger.getLogger(PatioDoorActivityHandler.class);

	@Override
	public void run() {
		super.run();
		
		if (this.reportingDevice.isTriggered()) {
			log.info("Patio door opened");
			
			//tilt blinds
			Calendar nineAM = Calendar.getInstance();
			Calendar now = Calendar.getInstance();
			
			nineAM.set(Calendar.HOUR_OF_DAY, 9);
			nineAM.set(Calendar.MINUTE, 00);
			
			Blind loungeWindowBlind = (Blind) DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(3);
			Blind loungePatioBlind = (Blind) DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(4);
			if ((now.after(nineAM) || CommonQueries.isItTheWeekendOrBankHoliday()) && (CommonQueries.isBrightnessBetween600and800() || CommonQueries.isBrightnessGreaterThan800())) {
				boolean tilted = false;
				if (!"0".equals(loungeWindowBlind.getDeviceLevel()) && !loungeWindowBlind.isTilted()) {
					tilted = loungeWindowBlind.tiltBlindDown();
				}
				
				if (!"0".equals(loungePatioBlind.getDeviceLevel()) && !loungePatioBlind.isTilted()) {
					tilted = loungePatioBlind.tiltBlindDown();
				}
				
				if (tilted) {
					log.info("Blinds aren't tilted and it's light enough outside, tilting now someone's home");
				}
			}
			
			if (!"100".equals(loungePatioBlind.getDeviceLevel())) {
				loungePatioBlind.turnDeviceOn(false, "100");
				log.info("Patio door blinds are too low and door is open, moving blinds to up to max");
			}
			
			//check false occupancy
			String unexpectedOccupancy = HomeAutomationProperties.getProperty("ApartmentUnexpectedOccupancy");
			String atHomeModeLounge = HomeAutomationProperties.getProperty("AtHomeTodayMode");
			if (!CommonQueries.expectedOccupancyInApartment() && "false".equals(unexpectedOccupancy) && "false".equals(atHomeModeLounge)) {
				final String alarmOneTimeUrl = OneTimeUrlGenerator.getOneTimeString();
				HomeAutomationProperties.setOrUpdateProperty("AlarmOneTimeUrl", alarmOneTimeUrl);
				SMSHelper.sendSMS("07965502960", "Apartment occupied from patio door. Alarm will trigger. Deactivate now: "
						+ "http://robsflat.co.uk/disableApartmentAlarm/" + alarmOneTimeUrl);
				HomeAutomationProperties.setOrUpdateProperty("ApartmentUnexpectedOccupancy", "true");
			}
			else {
				welcomeHome("Patio");
			}
		}
		else {
			log.info("Patio door now closed");
		}
	}
}
