package co.uk.rob.apartment.automation.model.jsonapi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.devices.Blind;
import co.uk.rob.apartment.automation.model.devices.Dehumidifier;
import co.uk.rob.apartment.automation.model.devices.DoorSensor;
import co.uk.rob.apartment.automation.model.devices.Multisensor;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

public class SimplifiedDeviceStatusCompiler {
	
	//utilities
	private DateFormat dateFormat = null;
	private String user;
	
	public SimplifiedDeviceStatusCompiler() {
		this.dateFormat = new SimpleDateFormat("dd/MM HH:mm");
		this.user = "unknown";
	}
	
	@SuppressWarnings("unchecked")
	public void testZoneStatus(Zone zone, JSONObject allZoneStatuses) {
		JSONObject zoneStatuses = new JSONObject();
		
		//modes
		if (Zone.LOUNGE.equals(zone)) {
			String loungeBedroomMode = HomeAutomationProperties.getProperty("LoungeBedroomMode");
			if (loungeBedroomMode != null && "true".equals(loungeBedroomMode)) {
				zoneStatuses.put("bedroom_mode", "enabled");
			}
			else {
				zoneStatuses.put("bedroom_mode", "disabled");
			}
		}
		else if (Zone.ROB_ROOM.equals(zone)) {
			String robRoomBedroomMode = HomeAutomationProperties.getProperty("RobRoomBedroomMode");
			if (robRoomBedroomMode != null && "true".equals(robRoomBedroomMode)) {
				zoneStatuses.put("full_bedroom_mode", "enabled");
			}
			else {
				zoneStatuses.put("full_bedroom_mode", "disabled");
			}
		}
		else if (Zone.APARTMENT.equals(zone)) {
			String atHomeModeLounge = HomeAutomationProperties.getProperty("AtHomeTodayMode");
			if (atHomeModeLounge != null && "true".equals(atHomeModeLounge)) {
				zoneStatuses.put("at_home_today_mode", "enabled");
			}
			else {
				zoneStatuses.put("at_home_today_mode", "disabled");
			}
		}
		
		//sensors and devices
		if (!Zone.APARTMENT.equals(zone)) {
			List<ReportingDevice> sensors = DeviceListManager.getReportingDeviceByLocation(zone);
			List<ControllableDevice> devices = DeviceListManager.getControllableDeviceByLocation(zone);
			
			for (ReportingDevice sensor : sensors) {
				if (sensor != null) {
					JSONObject sensorStatuses = new JSONObject();
					Calendar lastOccupied = Calendar.getInstance();
					lastOccupied.setTime(new Date(sensor.getLastUpdated()));
					if (sensor instanceof Multisensor) {
						sensorStatuses.put("temperature", Float.toString(sensor.getTemperature()[2]));
						sensorStatuses.put("humidity", Float.toString(sensor.getHumidity()[2]));
						sensorStatuses.put("luminiscence", Float.toString(sensor.getLuminiscence()[2]));
						sensorStatuses.put("occupied", Boolean.toString(sensor.isTriggered()));
						sensorStatuses.put("last_occupied", dateFormat.format(lastOccupied.getTime()));
					}
					else {
						sensorStatuses.put("open", Boolean.toString(sensor.isTriggered()));
						sensorStatuses.put("last_triggered", dateFormat.format(lastOccupied.getTime()));
					}
					
					sensorStatuses.put("battery_level", Integer.toString(sensor.getBatteryLevel()));
					
					if (sensor instanceof Multisensor) {
						zoneStatuses.put("multisensor", sensorStatuses);
					}
					else if (sensor instanceof DoorSensor) {
						zoneStatuses.put("door_sensor", sensorStatuses);
					}
					else {
						zoneStatuses.put("unknown_sensor", sensorStatuses);
					}
				}
			}
			
			int blindCount = 1, dehumidifierCount = 1;
			for (ControllableDevice device : devices) {
				if (device instanceof Blind) {
					JSONObject deviceStatuses = new JSONObject();
					deviceStatuses.put("percent_open", device.getDeviceLevel());
					deviceStatuses.put("tilted", Boolean.toString(((Blind) device).isTilted()));
					zoneStatuses.put("blind" + blindCount++, deviceStatuses);
				}
				else if (device instanceof Dehumidifier) {
					JSONObject deviceStatuses = new JSONObject();
					deviceStatuses.put("dehumidifying", Boolean.toString(device.isDeviceOn()));
					zoneStatuses.put("dehumidifier" + dehumidifierCount++, deviceStatuses);
				}
			}
		}
		else {
			if ("rbutcher".equals(user)) {
				zoneStatuses.put("bedroom_to_render", "bedroomOne");
			}
			else if ("scat".equals(user)) {
				zoneStatuses.put("bedroom_to_render", "bedroomTwo");
			}
			else {
				zoneStatuses.put("bedroom_to_render", user);
			}
			
			zoneStatuses.put("unexpected_occupancy", HomeAutomationProperties.getProperty("ApartmentUnexpectedOccupancy"));
			zoneStatuses.put("occupied", Boolean.toString(CommonQueries.isApartmentOccupied()));
			Calendar lastOccupancy = CommonQueries.getLastApartmentOccupancyTime();
			if (lastOccupancy != null && !CommonQueries.isApartmentOccupied()) {
				zoneStatuses.put("last_occupied", dateFormat.format(lastOccupancy.getTime()));
			}
			else {
				zoneStatuses.put("last_occupied", null);
			}
		}
		
		allZoneStatuses.put(zone.toString(), zoneStatuses);
	}
	
	public void setUser(String user) {
		this.user = user;
	}
}
