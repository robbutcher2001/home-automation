package co.uk.rob.apartment.automation.model.handlers;

import java.util.Calendar;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractActivityHandler;
import co.uk.rob.apartment.automation.model.devices.Blind;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;
import co.uk.rob.apartment.automation.utilities.SMSHelper;

public class PatioDoorActivityHandler extends AbstractActivityHandler {
	
	private Logger log = Logger.getLogger(PatioDoorActivityHandler.class);

	@Override
	public void run() {
		super.run();
		
		if (this.reportingDevice.isTriggered()) {
			log.info("Patio door opened");
			
			//tilt blinds
			Calendar midday = Calendar.getInstance();
			Calendar now = Calendar.getInstance();
			
			midday.set(Calendar.HOUR_OF_DAY, 12);
			midday.set(Calendar.MINUTE, 00);
			
			Blind loungeWindowBlind = (Blind) DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(3);
			if (!loungeWindowBlind.isTilted() && (now.after(midday) || CommonQueries.isItTheWeekendOrBankHoliday())) {
				if (CommonQueries.isBrightnessGreaterThan800() && ("40".equals(loungeWindowBlind.getDeviceLevel()) ||
						"55".equals(loungeWindowBlind.getDeviceLevel()) ||
						"80".equals(loungeWindowBlind.getDeviceLevel()))) {
					log.info("Blinds aren't tilted and it's light enough outside, tilting now someone's home");
					loungeWindowBlind.tiltBlindDown();
				}
			}
			
			//check false occupancy
			String unexpectedOccupancy = HomeAutomationProperties.getProperty("ApartmentUnexpectedOccupancy");
			String atHomeModeLounge = HomeAutomationProperties.getProperty("AtHomeTodayMode");
			if (!CommonQueries.expectedOccupancyInApartment() && "false".equals(unexpectedOccupancy) && "false".equals(atHomeModeLounge)) {
				SMSHelper.sendSMS("07965502960", "Apartment has been occupied during an unexpected time from patio door.");
				HomeAutomationProperties.setOrUpdateProperty("ApartmentUnexpectedOccupancy", "true");
			}
		}
		else {
			log.info("Patio door now closed");
		}
	}
}
