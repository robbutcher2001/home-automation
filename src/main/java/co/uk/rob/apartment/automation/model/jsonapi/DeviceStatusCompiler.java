package co.uk.rob.apartment.automation.model.jsonapi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.devices.AlarmUnit;
import co.uk.rob.apartment.automation.model.devices.Blind;
import co.uk.rob.apartment.automation.model.devices.Dehumidifier;
import co.uk.rob.apartment.automation.model.devices.DoorSensor;
import co.uk.rob.apartment.automation.model.devices.ElectricBlanket;
import co.uk.rob.apartment.automation.model.devices.Multisensor;
import co.uk.rob.apartment.automation.model.devices.ShockSensor;
import co.uk.rob.apartment.automation.model.devices.WindowSensor;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

public class DeviceStatusCompiler {
	
	//utilities
	private DateFormat dateFormat = null;
	private String user;
	
	public DeviceStatusCompiler() {
		this.dateFormat = new SimpleDateFormat("dd/MM HH:mm");
		this.user = "unknown";
	}
	
	@SuppressWarnings("unchecked")
	public void getZoneStatus(Zone zone, JSONObject rootObject) {
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
			
			zoneStatuses.put("next_lighting_state", HomeAutomationProperties.getProperty("RobRoomNextLightingState"));
		}
		else if (Zone.APARTMENT.equals(zone)) {
			String continuousAlarmMode = HomeAutomationProperties.getProperty("ContinuousAlarmMode");
			if (continuousAlarmMode != null && "true".equals(continuousAlarmMode)) {
				zoneStatuses.put("continuous_alarm_mode", "enabled");
			}
			else {
				zoneStatuses.put("continuous_alarm_mode", "disabled");
			}
			
			String forceDisableAlarm = HomeAutomationProperties.getProperty("ForceDisableAlarm");
			if (forceDisableAlarm != null && "true".equals(forceDisableAlarm)) {
				zoneStatuses.put("force_disabled", true);
			}
			else {
				zoneStatuses.put("force_disabled", false);
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
						sensorStatuses.put("occupied", sensor.isTriggered());
						sensorStatuses.put("last_occupied", dateFormat.format(lastOccupied.getTime()));
					}
					else if (sensor instanceof ShockSensor) {
						sensorStatuses.put("shock_detected", sensor.isTriggered());
						sensorStatuses.put("last_triggered", dateFormat.format(lastOccupied.getTime()));
					}
					else {
						sensorStatuses.put("open", sensor.isTriggered());
						sensorStatuses.put("last_triggered", dateFormat.format(lastOccupied.getTime()));
					}
					
					sensorStatuses.put("battery_level", Integer.toString(sensor.getBatteryLevel()));
					
					if (sensor instanceof Multisensor) {
						zoneStatuses.put("multisensor", sensorStatuses);
					}
					else if (sensor instanceof DoorSensor) {
						zoneStatuses.put("door_sensor", sensorStatuses);
					}
					else if (sensor instanceof WindowSensor) {
						zoneStatuses.put("window_sensor", sensorStatuses);
					}
					else if (sensor instanceof ShockSensor) {
						zoneStatuses.put("shock_sensor", sensorStatuses);
					}
					else {
						zoneStatuses.put("unknown_sensor", sensorStatuses);
					}
				}
			}
			
			int blindCount = 1, dehumidifierCount = 1, alarmUnitCount = 1, electricBlanket = 1;
			for (ControllableDevice device : devices) {
				if (device instanceof Blind) {
					JSONObject deviceStatuses = new JSONObject();
					deviceStatuses.put("percent_open", device.getDeviceLevel());
					deviceStatuses.put("tilted", ((Blind) device).isTilted());
					zoneStatuses.put("blind" + blindCount++, deviceStatuses);
				}
				else if (device instanceof Dehumidifier) {
					JSONObject deviceStatuses = new JSONObject();
					deviceStatuses.put("dehumidifying", device.isDeviceOn());
					zoneStatuses.put("dehumidifier" + dehumidifierCount++, deviceStatuses);
				}
				else if (device instanceof AlarmUnit) {
					JSONObject deviceStatuses = new JSONObject();
					deviceStatuses.put("battery_level", Integer.toString(((AlarmUnit) device).getBatteryLevel()));
					zoneStatuses.put("alarm_unit" + alarmUnitCount++, deviceStatuses);
				}
				else if (device instanceof ElectricBlanket) {
					JSONObject deviceStatuses = new JSONObject();
					deviceStatuses.put("next_state", ((ElectricBlanket) device).getNextStateText());
					deviceStatuses.put("is_warming", device.isDeviceOn());
					zoneStatuses.put("electric_blanket" + electricBlanket++, deviceStatuses);
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
			
			//check false occupancy
			if (CommonQueries.isApartmentAlarmEnabled()) {
				zoneStatuses.put("alarm_system", true);
			}
			else {
				zoneStatuses.put("alarm_system", false);
			}
			
			boolean isApartmentOccupied = CommonQueries.isApartmentOccupied();
			zoneStatuses.put("occupied", isApartmentOccupied);
			Calendar lastOccupancy = CommonQueries.getLastApartmentOccupancyTime();
			if (lastOccupancy != null && !isApartmentOccupied) {
				zoneStatuses.put("last_occupied", dateFormat.format(lastOccupancy.getTime()));
			}
			else {
				zoneStatuses.put("last_occupied", null);
			}
		}
		
		rootObject.put(zone.toString(), zoneStatuses);
	}
	
	/**
     * @param failText text to display in browser, sends JSend 'fail' status
     *        http://labs.omniti.com/labs/jsend
     * @return failText as JSON object for browser
     */
    @SuppressWarnings("unchecked")
	public String createFailAsJson(final String failText, JSONObject rootObject) {
    	rootObject.clear();
    	rootObject.put("status", "fail");
        JSONObject fail = new JSONObject();
        fail.put("errorText", !failText.equals("") ? failText : "");
        rootObject.put("data", fail);

        return rootObject.toJSONString();
    }

    /**
     * @param errorText text to display in browser, sends JSend 'error' status
     *        http://labs.omniti.com/labs/jsend
     * @return errorText as JSON object for browser
     */
    @SuppressWarnings("unchecked")
	public String createErrorAsJson(final String errorText, JSONObject rootObject) {
    	rootObject.clear();
    	rootObject.put("status", "error");
    	rootObject.put("message", !errorText.equals("") ? errorText : "");

        return rootObject.toJSONString();
    }
    
    public void setUser(String user) {
		this.user = user;
	}
}
