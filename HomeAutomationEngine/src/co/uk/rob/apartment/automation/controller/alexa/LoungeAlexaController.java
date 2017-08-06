package co.uk.rob.apartment.automation.controller.alexa;

import java.util.List;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.devices.Blind;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;

/**
 * @author Rob
 *
 */
public class LoungeAlexaController {
	
	private Logger log = Logger.getLogger(LoungeAlexaController.class);
	
    private ControllableDevice lampOneLounge;
    private ControllableDevice ledRodLounge;
    private ControllableDevice lampTwoLounge;
    private ControllableDevice bobbyLoungeLamp;
    private ControllableDevice kitchenLedRod;
    private Blind loungePatioBlind;
    
	public LoungeAlexaController() {
		List<ControllableDevice> devicesInLoungeAndKitchen = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE);
		devicesInLoungeAndKitchen.addAll(DeviceListManager.getControllableDeviceByLocation(Zone.KITCHEN));
		
		this.lampOneLounge = devicesInLoungeAndKitchen.get(0);
		this.ledRodLounge = devicesInLoungeAndKitchen.get(1);
		this.lampTwoLounge = devicesInLoungeAndKitchen.get(2);
		this.bobbyLoungeLamp = devicesInLoungeAndKitchen.get(5);
		this.kitchenLedRod = devicesInLoungeAndKitchen.get(6);
		this.loungePatioBlind = (Blind) devicesInLoungeAndKitchen.get(4);
	}
	
	public boolean informLounge(final String action) {
		boolean successfulCall = false;
		
		if ("on".equals(action)) {
			successfulCall = lampOneLounge.turnDeviceOn(true, "99");
			lampOneLounge.resetAutoOverridden();
			if (successfulCall) {
				successfulCall = ledRodLounge.turnDeviceOff(true);
				ledRodLounge.resetAutoOverridden();
				if (successfulCall) {
					successfulCall = lampTwoLounge.turnDeviceOn(true, "99");
					lampTwoLounge.resetAutoOverridden();
					if (successfulCall) {
						successfulCall = bobbyLoungeLamp.turnDeviceOn(true, "99");
						bobbyLoungeLamp.resetAutoOverridden();
					}
				}
			}
		}
		else if ("off".equals(action)) {
			successfulCall = lampOneLounge.turnDeviceOff(true);
			lampOneLounge.resetAutoOverridden();
			if (successfulCall) {
				successfulCall = ledRodLounge.turnDeviceOff(true);
				ledRodLounge.resetAutoOverridden();
				if (successfulCall) {
					successfulCall = lampTwoLounge.turnDeviceOff(true);
					lampTwoLounge.resetAutoOverridden();
					if (successfulCall) {
						successfulCall = bobbyLoungeLamp.turnDeviceOff(true);
						bobbyLoungeLamp.resetAutoOverridden();
					}
				}
			}
		}
		else if ("down".equals(action)) {
			successfulCall = lampOneLounge.turnDeviceOn(true, "40");
			lampOneLounge.resetAutoOverridden();
			if (successfulCall) {
				successfulCall = ledRodLounge.turnDeviceOff(true);
				ledRodLounge.resetAutoOverridden();
				if (successfulCall) {
					successfulCall = lampTwoLounge.turnDeviceOn(true, "0");
					lampTwoLounge.resetAutoOverridden();
					if (successfulCall) {
						successfulCall = bobbyLoungeLamp.turnDeviceOn(true, "30");
						bobbyLoungeLamp.resetAutoOverridden();
					}
				}
			}
		}
		else if ("up".equals(action)) {
			successfulCall = lampOneLounge.turnDeviceOn(true, "99");
			lampOneLounge.resetAutoOverridden();
			if (successfulCall) {
				successfulCall = ledRodLounge.turnDeviceOff(true);
				ledRodLounge.resetAutoOverridden();
				if (successfulCall) {
					successfulCall = lampTwoLounge.turnDeviceOn(true, "99");
					lampTwoLounge.resetAutoOverridden();
					if (successfulCall) {
						successfulCall = bobbyLoungeLamp.turnDeviceOn(true, "99");
						bobbyLoungeLamp.resetAutoOverridden();
					}
				}
			}
		}
		else if ("kitchenoff".equals(action)) {
			if (this.kitchenLedRod.isDeviceOn() && !this.kitchenLedRod.isAutoOverridden() && !this.kitchenLedRod.isManuallyOverridden()) {
				successfulCall = this.kitchenLedRod.turnDeviceOff(false);
				this.log.info("Alexa request: switching off kitchen LED rod");
			}
		}
		else if ("backdoor".equals(action)) {
			if ("0".equals(this.loungePatioBlind.getDeviceLevel()) || "40".equals(this.loungePatioBlind.getDeviceLevel()) ||
					"55".equals(this.loungePatioBlind.getDeviceLevel())) {
				successfulCall = this.loungePatioBlind.turnDeviceOn(false, "60");
				this.log.info("Alexa request: moving patio blind so back door can be openend");
			}
		}
		
		
		return successfulCall;
	}

}
