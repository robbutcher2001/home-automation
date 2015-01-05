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
	
	public static boolean expectedExternalDoorActivity() {
		boolean expectedOccupancy = true;
		Calendar now = Calendar.getInstance();
		
		Calendar nineAM = (Calendar) now.clone();
		nineAM.set(Calendar.HOUR_OF_DAY, 9);
		nineAM.set(Calendar.MINUTE, 00);
		
		Calendar halfFivePM = (Calendar) now.clone();
		halfFivePM.set(Calendar.HOUR_OF_DAY, 17);
		halfFivePM.set(Calendar.MINUTE, 30);
		
		Calendar halfFiveAM = (Calendar) now.clone();
		halfFiveAM.set(Calendar.HOUR_OF_DAY, 5);
		halfFiveAM.set(Calendar.MINUTE, 30);
		
		if (!isItTheWeekendOrBankHoliday()) {
			if (now.after(nineAM) && now.before(halfFivePM)) {
				expectedOccupancy = false;
			}
		}
		
		//now check that it's morning hours and after midnight
		if (now.before(halfFiveAM)) {
			expectedOccupancy = false;
		}
		
		return expectedOccupancy;
	}
	
	public static Calendar getLastApartmentOccupancyTime() {
		Calendar lounge = Calendar.getInstance();
		lounge.setTime(new Date(DeviceListManager.getReportingDeviceByLocation(Zone.LOUNGE).get(0).getLastUpdated()));
		
		Calendar robRoom = Calendar.getInstance();
		robRoom.setTime(new Date(DeviceListManager.getReportingDeviceByLocation(Zone.ROB_ROOM).get(0).getLastUpdated()));
		
//		Calendar frontDoor = Calendar.getInstance();
//		frontDoor.setTime(new Date(DeviceListManager.getReportingDeviceByLocation(Zone.HALLWAY).get(0).getLastUpdated()));
		
		Calendar lastOccupancy = null;
		
		if (lounge.after(robRoom)) {
			lastOccupancy = lounge;
		}
		
		if (robRoom.after(lounge)) {
			lastOccupancy = robRoom;
		}
		
		//removed because as the door is opened as you come in, the last occupancy time is updated to the
		//time that happens so the FrontDoorActivityHandler cannot detect whether you've been out the flat for an hour
//		if (frontDoor.after(lounge) && frontDoor.after(robRoom)) {
//			lastOccupancy = frontDoor;
//		}
		
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
	
	private static Float[] getCurrentBrightness() {
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
	
	public static boolean isBrightnessBetween600and800() {
		Float[] outsideLux = getCurrentBrightness();
		if ((outsideLux[0] < 800f && outsideLux[0] > 600f) &&
				(outsideLux[1] < 800f && outsideLux[1] > 600f) &&
					(outsideLux[2] < 800f && outsideLux[2] > 600f)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isBrightnessBetween400and600() {
		Float[] outsideLux = getCurrentBrightness();
		if ((outsideLux[0] < 600f && outsideLux[0] > 400f) &&
				(outsideLux[1] < 600f && outsideLux[1] > 400f) &&
					(outsideLux[2] < 600f && outsideLux[2] > 400f)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isBrightnessBetween200and400() {
		Float[] outsideLux = getCurrentBrightness();
		if ((outsideLux[0] < 400f && outsideLux[0] > 200f) &&
				(outsideLux[1] < 400f && outsideLux[1] > 200f) &&
					(outsideLux[2] < 400f && outsideLux[2] > 200f)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isBrightnessBetween1and200() {
		Float[] outsideLux = getCurrentBrightness();
		if ((outsideLux[0] < 200f && outsideLux[0] > 0f) &&
				(outsideLux[1] < 200f && outsideLux[1] > 0f) &&
					(outsideLux[2] < 200f && outsideLux[2] > 0f)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isBrightnessAt0() {
		Float[] outsideLux = getCurrentBrightness();
		if (outsideLux[0] < 1 &&
				outsideLux[1] < 1 &&
					outsideLux[2] < 1) {
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
}
