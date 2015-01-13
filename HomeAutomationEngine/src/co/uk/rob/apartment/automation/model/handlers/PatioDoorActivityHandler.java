package co.uk.rob.apartment.automation.model.handlers;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractExternalDoorActivityHandler;
import co.uk.rob.apartment.automation.model.devices.Blind;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.utilities.CommonQueries;

public class PatioDoorActivityHandler extends AbstractExternalDoorActivityHandler {
	
	private Logger log = Logger.getLogger(PatioDoorActivityHandler.class);

	@Override
	public void run() {
		super.run();
		
		this.door = "Patio";
		
		if (this.reportingDevice.isTriggered()) {
			log.info("Patio door opened");
			
			ControllableDevice loungeLamp = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(0);
			Blind loungePatioBlind = (Blind) DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(4);
			
			if (loungeLamp.isDeviceOn()) {
				loungeLamp.turnDeviceOff(false);
			}
			
			if (!"100".equals(loungePatioBlind.getDeviceLevel())) {
				loungePatioBlind.turnDeviceOn(false, "100");
				log.info("Patio door blinds are too low and door is open, moving blinds to up to max");
			}
			
			//check false occupancy
			if (CommonQueries.isApartmentAlarmEnabled()) {
				runUnexpectedOccupancyControl();
			}
			else {
				welcomeHome();
			}
		}
		else {
			log.info("Patio door now closed");
		}
	}
}
