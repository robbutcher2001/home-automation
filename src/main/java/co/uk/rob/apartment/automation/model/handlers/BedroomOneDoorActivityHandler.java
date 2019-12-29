package co.uk.rob.apartment.automation.model.handlers;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.abstracts.AbstractActivityHandler;
import co.uk.rob.apartment.automation.model.devices.Blind;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;

public class BedroomOneDoorActivityHandler extends AbstractActivityHandler {
	
	private Logger log = Logger.getLogger(BedroomOneDoorActivityHandler.class);
	
	private List<ControllableDevice> devicesToControl;
	private ControllableDevice lamp;
	private ControllableDevice ceilingLight;
	private ControllableDevice ledRodRobRoom;
	private ControllableDevice electricBlanket;
	private ControllableDevice loungeLamp;
	private ControllableDevice ledRodLounge;
	private ControllableDevice stickLoungeLamp;
	private ControllableDevice bobbyLoungeLamp;
	private Blind robWindowBlind;

	public BedroomOneDoorActivityHandler() {
		devicesToControl = DeviceListManager.getControllableDeviceByLocation(Zone.ROB_ROOM);
		
		lamp = devicesToControl.get(0);
		ceilingLight = devicesToControl.get(1);
		ledRodRobRoom = devicesToControl.get(3);
		electricBlanket = devicesToControl.get(4);
		robWindowBlind = (Blind) devicesToControl.get(5);
		
		loungeLamp = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(0);
		ledRodLounge = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(1);
		stickLoungeLamp = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(2);
		bobbyLoungeLamp = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE).get(5);
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
		
		if (now.after(halfSixPM) && now.before(halfSevenPM) &&
				!this.reportingDevice.isTriggered()) {
			if (!"0".equals(robWindowBlind.getDeviceLevel())) {
				robWindowBlind.turnDeviceOn(true, "0");
				log.info("Door closed between 18:30 and 19:30 so assuming Rob wants to get changed, closing blinds");
			}
		}
		else {
			if (robWindowBlind.isManuallyOverridden()) {
				robWindowBlind.resetManuallyOverridden();
			}
		}
		
		if (now.after(tenPM) || now.before(quarterToEightAM)) {
			//bedroom door is closed at night
			if (!this.reportingDevice.isTriggered()) {
				if (loungeLamp.isDeviceOn() && !loungeLamp.isManuallyOverridden() && loungeLamp.isAutoOverridden()) {
					loungeLamp.turnDeviceOff(true);
					loungeLamp.resetAutoOverridden();
					log.info("Lounge lamp auto off as everyone has gone to bed");
				}

				if (ledRodLounge.isDeviceOn() && !ledRodLounge.isManuallyOverridden() && ledRodLounge.isAutoOverridden()) {
					ledRodLounge.turnDeviceOff(true);
					ledRodLounge.resetAutoOverridden();
					log.info("Lounge LED auto off as everyone has gone to bed");
				}
				
				if (stickLoungeLamp.isDeviceOn() && !stickLoungeLamp.isManuallyOverridden() && stickLoungeLamp.isAutoOverridden()) {
					stickLoungeLamp.turnDeviceOff(true);
					stickLoungeLamp.resetAutoOverridden();
					log.info("Lounge stick lamp auto off as everyone has gone to bed");
				}
				
				if (bobbyLoungeLamp.isDeviceOn() && !bobbyLoungeLamp.isManuallyOverridden() && bobbyLoungeLamp.isAutoOverridden()) {
					bobbyLoungeLamp.turnDeviceOff(true);
					bobbyLoungeLamp.resetAutoOverridden();
					log.info("Lounge Bobby lamp auto off as everyone has gone to bed");
				}
				
				if (ledRodRobRoom.isDeviceOn()) {
					log.info("Rob room bed mode triggered, switching off LED rod");
					ledRodRobRoom.turnDeviceOff(false);
				}
				
				if (electricBlanket.isDeviceOn() && !electricBlanket.isManuallyOverridden()) {
					log.info("Rob room bed mode triggered, switching off electric blanket");
					electricBlanket.turnDeviceOffAutoOverride();
				}
				
				if (ceilingLight.isDeviceOn()) {
					log.info("Rob room bed mode triggered, switching off ceiling light and decrementing brightness of lamp");
					ceilingLight.turnDeviceOff(false);
				}
				
				if (!lamp.isDeviceOn()) {
					lamp.resetAutoOverridden();
					lamp.turnDeviceOn(false);
					
					delayLampsOff();
				}
			}
			else {
				//bedroom door is opened at night, turn on main light but give it 30 seconds
				Timer timer = new Timer("Main light timer");
				
				TimerTask wait30secs = new TimerTask() {
					@Override
					public void run() {
						if (reportingDevice.isTriggered()) {
							if (!ceilingLight.isDeviceOn()) {
								log.info("Rob room door opened, switching off bed mode and switching on ceiling light");
								ceilingLight.turnDeviceOn(false);
							}
							
							if (lamp.isDeviceOn()) {
								lamp.turnDeviceOff(false);
							}
						}
					}
				};
				
				timer.schedule(wait30secs, 30000);
			}
		}
	}
	
	private void delayLampsOff() {
		Timer timer = new Timer("Lamp delay timer");
		
		TimerTask delayLampsOff = new TimerTask() {
			@Override
			public void run() {
				ControllableDevice lamp = DeviceListManager.getControllableDeviceByLocation(Zone.ROB_ROOM).get(0);
				lamp.turnDeviceOn(false);
			}
		};
		
		timer.schedule(delayLampsOff, 90000);
	}
}
