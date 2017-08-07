package co.uk.rob.apartment.automation.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.devices.Blind;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;

/**
 * @author Rob
 *
 */
public class CommonQueries {
	
	private static Logger log = Logger.getLogger(CommonQueries.class);
	
	private static String bankHolidays = "1/1/2014;18/4/2014;21/4/2014;5/5/2014;26/5/2014;25/8/2014;25/12/2014;26/12/2014;1/1/2015;3/4/2015;6/4/2015;4/5/2015;25/5/2015;31/8/2015;25/12/2015;28/12/2015";
	private static List<Calendar> bankHolidayDates;
	
	static {
		bankHolidayDates = new ArrayList<Calendar>();
		String[] dates = bankHolidays.split(";");
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		
		for (String date : dates) {
			Calendar parsedDate = Calendar.getInstance();
			try {
				parsedDate.setTime(format.parse(date));
				bankHolidayDates.add(parsedDate);
			} catch (ParseException e) {
				continue;
			}
		}
	}
	
	private static boolean isItABankHoliday() {
		Calendar now = Calendar.getInstance();
		
		for (Calendar bankHolidayDate : bankHolidayDates) {
			if (now.get(Calendar.YEAR) == bankHolidayDate.get(Calendar.YEAR)) {
				if (now.get(Calendar.MONTH) == bankHolidayDate.get(Calendar.MONTH)) {
					if (now.get(Calendar.DAY_OF_MONTH) == bankHolidayDate.get(Calendar.DAY_OF_MONTH)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public static boolean isApartmentAlarmEnabled() {
		boolean alarmEnabled = false;
		
		//manual alarm override takes precedence
		String continuousAlarmMode = HomeAutomationProperties.getProperty("ContinuousAlarmMode");
		String forceDisableAlarm = HomeAutomationProperties.getProperty("ForceDisableAlarm");
		if ("true".equals(continuousAlarmMode)) {
			alarmEnabled = true;
		}
		else if ("true".equals(forceDisableAlarm)) {
			alarmEnabled = false;
		}
		else {
			Calendar now = Calendar.getInstance();
			
			Calendar hourAgo = (Calendar) now.clone();
			hourAgo.add(Calendar.HOUR_OF_DAY, -1);
			
			Calendar halfFivePM = (Calendar) now.clone();
			halfFivePM.set(Calendar.HOUR_OF_DAY, 17);
			halfFivePM.set(Calendar.MINUTE, 30);
			
			Calendar halfFiveAM = (Calendar) now.clone();
			halfFiveAM.set(Calendar.HOUR_OF_DAY, 5);
			halfFiveAM.set(Calendar.MINUTE, 30);
			
			Calendar halfSevenAM = (Calendar) now.clone();
			halfSevenAM.set(Calendar.HOUR_OF_DAY, 7);
			halfSevenAM.set(Calendar.MINUTE, 30);
			
			Calendar loungeMultisensor = (Calendar) now.clone();
			loungeMultisensor.setTime(new Date(DeviceListManager.getReportingDeviceByLocation(Zone.LOUNGE).get(0).getLastUpdated()));

			Calendar robRoomMultisensor = (Calendar) now.clone();
			robRoomMultisensor.setTime(new Date(DeviceListManager.getReportingDeviceByLocation(Zone.ROB_ROOM).get(0).getLastUpdated()));
			
			Calendar bathroomMultisensor = (Calendar) now.clone();
			bathroomMultisensor.setTime(new Date(DeviceListManager.getReportingDeviceByLocation(Zone.BATHROOM).get(0).getLastUpdated()));
			
			//check whether it's between 00:00 and 05:30
			if (now.before(halfFiveAM)) {
				alarmEnabled = true;
			}
			//or check whether lounge or bathroom sensor has not been triggered yet today (away from home)
			//this means if sensor is triggered in the morning the alarm will be disabled after 17:30 (fall into below)
			else if (!loungeMultisensor.after(halfFiveAM) && !bathroomMultisensor.after(halfSevenAM)) {
				alarmEnabled = true;
			}
			//or if above isn't true, check whether it's before 5:30pm and lounge multisensor OR robs multisensor OR bathroom multisensor has not been triggered within the last hour
			else if (now.before(halfFivePM) && !loungeMultisensor.after(hourAgo) &&
					!robRoomMultisensor.after(hourAgo) && !bathroomMultisensor.after(hourAgo)) {
				alarmEnabled = true;
			}
		}
		
		return alarmEnabled;
	}
	
	public static Calendar getLastApartmentOccupancyTime() {
		Calendar loungeMultisensor = Calendar.getInstance();
		loungeMultisensor.setTime(new Date(DeviceListManager.getReportingDeviceByLocation(Zone.LOUNGE).get(0).getLastUpdated()));
		
		Calendar robRoomMultisensor = Calendar.getInstance();
		robRoomMultisensor.setTime(new Date(DeviceListManager.getReportingDeviceByLocation(Zone.ROB_ROOM).get(0).getLastUpdated()));
		
		Calendar bathroomMultisensor = Calendar.getInstance();
		bathroomMultisensor.setTime(new Date(DeviceListManager.getReportingDeviceByLocation(Zone.BATHROOM).get(0).getLastUpdated()));
		
		Calendar lastOccupancy = loungeMultisensor;
		
		if (robRoomMultisensor.after(lastOccupancy)) {
			lastOccupancy = robRoomMultisensor;
		}
		
		if (bathroomMultisensor.after(lastOccupancy)) {
			lastOccupancy = bathroomMultisensor;
		}
		
		return lastOccupancy;
	}
	
	public static boolean isApartmentOccupied() {
		Calendar lastOccupancy = getLastApartmentOccupancyTime();
		
		Calendar frontDoor = Calendar.getInstance();
		frontDoor.setTime(new Date(DeviceListManager.getReportingDeviceByLocation(Zone.HALLWAY).get(0).getLastUpdated()));
		
		Calendar patioDoor = Calendar.getInstance();
		patioDoor.setTime(new Date(DeviceListManager.getReportingDeviceByLocation(Zone.PATIO).get(1).getLastUpdated()));
		
		if (lastOccupancy != null && (frontDoor.after(lastOccupancy) || patioDoor.after(lastOccupancy))) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isItTheWeekendOrBankHoliday() {
		Calendar now = Calendar.getInstance();
		
		if (now.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && now.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && !isItABankHoliday()) {
			return false;
		}
		
		return true;
	}
	
	public static Float[] getCurrentBrightness() {
		ReportingDevice patioReportingDevice = DeviceListManager.getReportingDeviceByLocation(Zone.PATIO).get(0);
		Float[] outsideLux = patioReportingDevice.getLuminiscence();
		
		if (outsideLux == null || outsideLux.length != 3) {
			log.error("Lux array returned from patio sensor is null or not of length 3");
			return null;
		}
		
		return outsideLux;
	}
	
	public static boolean isBrightnessGreaterThan800() {
		Float[] outsideLux = getCurrentBrightness();
		if (outsideLux[0] > 800f &&
				outsideLux[1] > 800f &&
					outsideLux[2] > 800f) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isBrightnessBetweenXandY(float x, float y) {
		Float[] outsideLux = getCurrentBrightness();
		if ((outsideLux[0] < y && outsideLux[0] > x) &&
				(outsideLux[1] < y && outsideLux[1] > x) &&
					(outsideLux[2] < y && outsideLux[2] > x)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isBrightnessBelow20() {
		Float[] outsideLux = getCurrentBrightness();
		if (outsideLux[0] <= 20 &&
				outsideLux[1] <= 20 &&
					outsideLux[2] <= 20) {
			return true;
		}
		
		return false;
	}
	
	public static int calculateBlindMovementTime(Blind blind, String targetLevel) {
		String currentBlindLevel = blind.getDeviceLevel();
		int movementTime = 0;
		
		if ("0".equals(targetLevel)) {
			if ("80".equals(currentBlindLevel)) {
				movementTime = 22;
			}
			else if ("55".equals(currentBlindLevel)) {
				movementTime = 16;
			}
			else if ("40".equals(currentBlindLevel)) {
				movementTime = 12;
			}
		}
		
		if ("40".equals(targetLevel)) {
			if ("80".equals(currentBlindLevel)) {
				movementTime = 10;
			}
			else if ("55".equals(currentBlindLevel)) {
				movementTime = 7;
			}
			else if ("0".equals(currentBlindLevel)) {
				movementTime = 14;
			}
		}
		
		if ("55".equals(targetLevel)) {
			if ("80".equals(currentBlindLevel)) {
				movementTime = 7;
			}
			else if ("40".equals(currentBlindLevel)) {
				movementTime = 7;
			}
			else if ("0".equals(currentBlindLevel)) {
				movementTime = 18;
			}
		}
		
		if ("80".equals(targetLevel)) {
			if ("55".equals(currentBlindLevel)) {
				movementTime = 12;
			}
			else if ("40".equals(currentBlindLevel)) {
				movementTime = 16;
			}
			else if ("0".equals(currentBlindLevel)) {
				movementTime = 26;
			}
		}
		
		if ("100".equals(targetLevel)) {
			if ("55".equals(currentBlindLevel)) {
				movementTime = 15;
			}
			else if ("40".equals(currentBlindLevel)) {
				movementTime = 19;
			}
			else if ("0".equals(currentBlindLevel)) {
				movementTime = 29;
			}
		}
		
		return movementTime * 1000;
	}
	
	public static boolean hasDehumidifierBeenInStateForOverHour(Date lastUpdated) {
		Calendar lastDateOccupied = Calendar.getInstance();
		lastDateOccupied.setTime(lastUpdated);
		lastDateOccupied.add(Calendar.MINUTE, 60);
		
		Calendar now = Calendar.getInstance();
		
		if (now.after(lastDateOccupied)) {
			return true;
		}
		
		return false;
	}
}
