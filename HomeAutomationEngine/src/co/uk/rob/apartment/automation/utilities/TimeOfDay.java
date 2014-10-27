package co.uk.rob.apartment.automation.utilities;

import java.util.Calendar;

/**
 * @author Rob
 *
 */
public class TimeOfDay {

	public static boolean isTheWeekend() {
		Calendar now = Calendar.getInstance();
		
		if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			return true;
		}
		
		return false;
	}
}
