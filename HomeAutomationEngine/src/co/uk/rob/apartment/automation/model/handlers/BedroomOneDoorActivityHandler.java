package co.uk.rob.apartment.automation.model.handlers;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractActivityHandler;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;

public class BedroomOneDoorActivityHandler extends AbstractActivityHandler {
	
	private Logger log = Logger.getLogger(BedroomOneDoorActivityHandler.class);
	
	private List<ControllableDevice> devicesToControl;
	private ControllableDevice lamp;
	private ControllableDevice ceilingLight;
	private ControllableDevice ledRodRobRoom;
	private ControllableDevice loungeLamp;
	private ControllableDevice stickLoungeLamp;

	public BedroomOneDoorActivityHandler() {
		devicesToControl = DeviceListManager.getControllableDeviceByLocation(Zone.ROB_ROOM);
		
		lamp = devicesToControl.get(0);
		ceilingLight = devicesToControl.get(1);
		ledRodRobRoom = devicesToControl.get(3);
		
		loungeLamp = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(0);
		stickLoungeLamp = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(2);
	}
	
	@Override
	public void run() {
		super.run();
		
		Calendar now = Calendar.getInstance();
		
		Calendar halfSixPM = Calendar.getInstance();
		Calendar halfSevenPM = Calendar.getInstance();
		
		Calendar tenPM = Calendar.getInstance();
		Calendar quarterToEightAM = Calendar.getInstance();
		
		halfSixPM.set(Calendar.HOUR_OF_DAY, 18);
		halfSixPM.set(Calendar.MINUTE, 30);
		
		halfSevenPM.set(Calendar.HOUR_OF_DAY, 19);
		halfSevenPM.set(Calendar.MINUTE, 30);
		
		tenPM.set(Calendar.HOUR_OF_DAY, 22);
		tenPM.set(Calendar.MINUTE, 00);
		
		quarterToEightAM.set(Calendar.HOUR_OF_DAY, 7);
		quarterToEightAM.set(Calendar.MINUTE, 45);
		
		if (now.after(halfSixPM) && now.before(halfSevenPM) && !this.reportingDevice.isTriggered()) {
			//TODO: Add closure of blinds now door is shut and it's after work
			log.info("Door closed between 18:30 and 19:30 so assuming Rob wants to get changed, closing blinds");
		}
		
		if (now.after(tenPM) || now.before(quarterToEightAM)) {
			//bedroom door is closed at night
			if (!this.reportingDevice.isTriggered()) {
				if (loungeLamp.isDeviceOn() && !loungeLamp.isManuallyOverridden() && loungeLamp.isAutoOverridden()) {
					loungeLamp.turnDeviceOff(true);
					loungeLamp.resetAutoOverridden();
					log.info("Lounge lamp auto off as everyone has gone to bed");
				}
				
				if (stickLoungeLamp.isDeviceOn() && !stickLoungeLamp.isManuallyOverridden() && stickLoungeLamp.isAutoOverridden()) {
					stickLoungeLamp.turnDeviceOff(true);
					stickLoungeLamp.resetAutoOverridden();
					log.info("Lounge stick lamp auto off as everyone has gone to bed");
				}
				
				if (ledRodRobRoom.isDeviceOn()) {
					log.info("Rob room bed mode triggered, switching off LED rod");
					ledRodRobRoom.turnDeviceOff(false);
				}
				
				if (ceilingLight.isDeviceOn()) {
					log.info("Rob room bed mode triggered, switching off ceiling light and decrementing brightness of lamp");
					ceilingLight.turnDeviceOff(false);
				}
				
				if (!lamp.isDeviceOn()) {
					lamp.resetAutoOverridden();
					lamp.turnDeviceOn(false, "99");
					
					try {
						Thread.sleep(60000);
						lamp.turnDeviceOn(false, "60");
						Thread.sleep(30000);
						lamp.turnDeviceOn(false, "40");
						Thread.sleep(30000);
						lamp.turnDeviceOn(false, "20");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			else {
				//bedroom door is opened at night
				if (!ceilingLight.isDeviceOn()) {
					log.info("Rob room door opened, switching off bed mode and switching on ceiling light");
					ceilingLight.turnDeviceOn(false);
				}
				
				if (lamp.isDeviceOn()) {
					lamp.turnDeviceOff(false);
				}
			}
		}
	}
}
