package co.uk.rob.apartment.automation.controller.alexa;

import java.util.List;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;

/**
 * @author Rob
 *
 */
public class LoungeAlexaController {
	
    private ControllableDevice lampOneLounge;
    private ControllableDevice ledRodLounge;
    private ControllableDevice lampTwoLounge;
    private ControllableDevice bobbyLoungeLamp;
    
	public LoungeAlexaController() {
		List<ControllableDevice> devicesInLoungeAndKitchen = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE);
		devicesInLoungeAndKitchen.addAll(DeviceListManager.getControllableDeviceByLocation(Zone.KITCHEN));
		
		this.lampOneLounge = devicesInLoungeAndKitchen.get(0);
		this.ledRodLounge = devicesInLoungeAndKitchen.get(1);
		this.lampTwoLounge = devicesInLoungeAndKitchen.get(2);
		this.bobbyLoungeLamp = devicesInLoungeAndKitchen.get(5);
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
		
		
		return successfulCall;
	}

}
