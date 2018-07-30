package co.uk.rob.apartment.automation.controller.alexa;

import java.util.List;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.devices.Blind;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

/**
 * @author Rob
 *
 */
public class LoungeAlexaController {

	private Logger log = Logger.getLogger(LoungeAlexaController.class);

	private List<ControllableDevice> allDevicesInLoungeAndKitchen;

  private ControllableDevice lampOneLounge;
  private ControllableDevice ledRodLounge;
  private ControllableDevice lampTwoLounge;
  private ControllableDevice bobbyLoungeLamp;
  private ControllableDevice kitchenLedRod;
  private Blind loungeWindowBlind;
	private Blind loungePatioBlind;
	private ReportingDevice patioDoor;

	public LoungeAlexaController() {
		allDevicesInLoungeAndKitchen = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE);
		allDevicesInLoungeAndKitchen.addAll(DeviceListManager.getControllableDeviceByLocation(Zone.KITCHEN));

		this.lampOneLounge = allDevicesInLoungeAndKitchen.get(0);
		this.ledRodLounge = allDevicesInLoungeAndKitchen.get(1);
		this.lampTwoLounge = allDevicesInLoungeAndKitchen.get(2);
		this.bobbyLoungeLamp = allDevicesInLoungeAndKitchen.get(5);
		this.kitchenLedRod = allDevicesInLoungeAndKitchen.get(6);
		this.loungeWindowBlind = (Blind) allDevicesInLoungeAndKitchen.get(3);
		this.loungePatioBlind = (Blind) allDevicesInLoungeAndKitchen.get(4);
		this.patioDoor = DeviceListManager.getReportingDeviceByLocation(Zone.PATIO).get(1);
	}

	public String informLounge(final String action) {
		String spokenResponse = "none";

		if ("on".equals(action)) {
			this.lampOneLounge.turnDeviceOn(true, "99");
			this.lampOneLounge.resetAutoOverridden();

			this.ledRodLounge.turnDeviceOff(true);
			this.ledRodLounge.resetAutoOverridden();

			this.lampTwoLounge.turnDeviceOn(true, "99");
			this.lampTwoLounge.resetAutoOverridden();

			this.bobbyLoungeLamp.turnDeviceOn(true, "99");
			this.bobbyLoungeLamp.resetAutoOverridden();

			this.log.info("Alexa request: lights on in lounge");
		}
		else if ("off".equals(action)) {
			this.lampOneLounge.turnDeviceOff(true);
			this.lampOneLounge.resetAutoOverridden();

			this.ledRodLounge.turnDeviceOff(true);
			this.ledRodLounge.resetAutoOverridden();

			this.lampTwoLounge.turnDeviceOff(true);
			this.lampTwoLounge.resetAutoOverridden();

			this.bobbyLoungeLamp.turnDeviceOff(true);
			this.bobbyLoungeLamp.resetAutoOverridden();

			this.log.info("Alexa request: lights off in lounge");
		}
		else if ("down".equals(action) || "dim".equals(action)) {
			this.lampOneLounge.turnDeviceOn(true, "40");
			this.lampOneLounge.resetAutoOverridden();

			this.ledRodLounge.turnDeviceOff(true);
			this.ledRodLounge.resetAutoOverridden();

			this.lampTwoLounge.turnDeviceOn(true, "0");
			this.lampTwoLounge.resetAutoOverridden();

			this.bobbyLoungeLamp.turnDeviceOn(true, "30");
			this.bobbyLoungeLamp.resetAutoOverridden();

			this.log.info("Alexa request: lights dimmer in lounge");
		}
		else if ("up".equals(action) || "brighten".equals(action)) {
			this.lampOneLounge.turnDeviceOn(true, "99");
			this.lampOneLounge.resetAutoOverridden();

			this.ledRodLounge.turnDeviceOff(true);
			this.ledRodLounge.resetAutoOverridden();

			this.lampTwoLounge.turnDeviceOn(true, "99");
			this.lampTwoLounge.resetAutoOverridden();

			this.bobbyLoungeLamp.turnDeviceOn(true, "99");
			this.bobbyLoungeLamp.resetAutoOverridden();

			this.log.info("Alexa request: lights brighter in lounge");
		}
		else if ("largelampon".equals(action)) {
			this.lampOneLounge.turnDeviceOn(true, "99");
			this.lampOneLounge.resetAutoOverridden();

			this.log.info("Alexa request: large lamp on in lounge");
		}
		else if ("largelampoff".equals(action)) {
			this.lampOneLounge.turnDeviceOff(true);
			this.lampOneLounge.resetAutoOverridden();

			this.log.info("Alexa request: large lamp off in lounge");
		}
		else if ("kitchenoff".equals(action)) {
			if (this.kitchenLedRod.isDeviceOn() && !this.kitchenLedRod.isAutoOverridden() && !this.kitchenLedRod.isManuallyOverridden()) {
				this.kitchenLedRod.turnDeviceOff(true);
			}

			this.log.info("Alexa request: switching off kitchen LED rod");
		}
		else if ("door".equals(action)) {
			if ("0".equals(this.loungePatioBlind.getDeviceLevel()) || "40".equals(this.loungePatioBlind.getDeviceLevel()) ||
					"55".equals(this.loungePatioBlind.getDeviceLevel())) {
				this.loungePatioBlind.turnDeviceOn(true, "60");
				this.loungePatioBlind.resetAutoOverridden();
			}

			spokenResponse = "There you go.";
			this.log.info("Alexa request: moving patio blind so back door can be openend");
		}
		else if ("blindsup".equals(action) || "blindsopen".equals(action)) {
			boolean moved = false;
			if (!CommonQueries.isBrightnessBelow20()) {
				if (!this.patioDoor.isTriggered()) {
					if (!"80".equals(this.loungeWindowBlind.getDeviceLevel())) {
						moved = this.loungeWindowBlind.turnDeviceOff(true);
						this.loungeWindowBlind.resetAutoOverridden();
					}

					if (!"80".equals(this.loungePatioBlind.getDeviceLevel())) {
						moved = this.loungePatioBlind.turnDeviceOff(true);
						this.loungePatioBlind.resetAutoOverridden();
					}
				}
				else {
					if (!"80".equals(this.loungeWindowBlind.getDeviceLevel())) {
						moved = this.loungeWindowBlind.turnDeviceOff(true);
						this.loungeWindowBlind.resetAutoOverridden();
					}
				}

				if (moved) {
					this.log.info("Alexa request: moving blinds up to 80% (max)");
				}
			}
			else {
				spokenResponse = "It's too dark outside to do this really.";
			}
		}
		else if ("blindshalfway".equals(action)) {
			boolean moved = false;
			if (!this.patioDoor.isTriggered()) {
				if (!"55".equals(this.loungeWindowBlind.getDeviceLevel())) {
					moved = this.loungeWindowBlind.turnDeviceOn(true, "55");
					this.loungeWindowBlind.resetAutoOverridden();
				}

				if (!"55".equals(this.loungePatioBlind.getDeviceLevel())) {
					moved = this.loungePatioBlind.turnDeviceOn(true, "55");
					this.loungePatioBlind.resetAutoOverridden();
				}
			}
			else {
				if (!"55".equals(this.loungeWindowBlind.getDeviceLevel())) {
					moved = this.loungeWindowBlind.turnDeviceOn(true, "55");
					this.loungeWindowBlind.resetAutoOverridden();
				}
			}

			if (moved) {
				this.log.info("Alexa request: moving blinds to 55%");
			}
		}
		else if ("blindsclose".equals(action) || "blindsdown".equals(action)) {
			boolean moved = false;
			if (!this.patioDoor.isTriggered()) {
				if (!"0".equals(this.loungeWindowBlind.getDeviceLevel())) {
					moved = this.loungeWindowBlind.turnDeviceOn(true, "0");
					this.loungeWindowBlind.resetAutoOverridden();
				}

				if (!"0".equals(this.loungePatioBlind.getDeviceLevel())) {
					moved = this.loungePatioBlind.turnDeviceOn(true, "0");
					this.loungePatioBlind.resetAutoOverridden();
				}
			}
			else {
				if (!"0".equals(this.loungeWindowBlind.getDeviceLevel())) {
					moved = this.loungeWindowBlind.turnDeviceOn(true, "0");
					this.loungeWindowBlind.resetAutoOverridden();
				}
			}

			if (moved) {
				this.log.info("Alexa request: moving blinds to 0%");
			}
		}
		else if ("bedroommodeon".equals(action)) {
			HomeAutomationProperties.setOrUpdateProperty("LoungeBedroomMode", "true");
			spokenResponse = "Bedroom mode is now on, please wait a few minutes for the room to adjust.";
			this.log.info("Request for full bedroom mode in Lounge [alexa]");
		}
		else if ("bedroommodeoff".equals(action)) {
			HomeAutomationProperties.setOrUpdateProperty("LoungeBedroomMode", "false");
			spokenResponse = "Bedroom mode is now off, please wait a few minutes for the room to reset.";
			this.log.info("Request for normal bedroom mode in Lounge [alexa]");
		}
		else if ("outsidebrightness".equals(action)) {
			float currentBrightness = -1;

			Float[] outsideLux = CommonQueries.getCurrentBrightness();
			if (outsideLux != null && outsideLux.length == 3) {
				currentBrightness = outsideLux[2];
			}

			spokenResponse = "Outside brightness is about " + currentBrightness + " lux.";
			this.log.info("Alexa request: current outside brightness");
		}
		else if ("reset".equals(action)) {
			int index = 0;
			for (ControllableDevice device : this.allDevicesInLoungeAndKitchen) {
				if (device.isManuallyOverridden() && !device.isAutoOverridden()) {
					device.resetManuallyOverridden();
					index++;
				}
			}

			final String resetSpokenResponse = "I've reset %d %s in the lounge and kitchen for you.";

			if (index > 1) {
				spokenResponse = String.format(resetSpokenResponse, index, "devices");
			}
			else if (index == 1) {
				spokenResponse = String.format(resetSpokenResponse, 1, "device");
			}
			else {
				spokenResponse = "There were actually no devices to reset.";
			}

			this.log.info("Alexa request: reset all devices in lounge");
		}

		return spokenResponse;
	}

}
