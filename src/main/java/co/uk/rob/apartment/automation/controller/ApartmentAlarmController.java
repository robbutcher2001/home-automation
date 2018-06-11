package co.uk.rob.apartment.automation.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.devices.AlarmUnit;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

/**
 * Servlet implementation class ApartmentAlarmController
 */
@WebServlet("/ApartmentAlarmController")
public class ApartmentAlarmController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(ApartmentAlarmController.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ApartmentAlarmController() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String redirect = "/alarmNotDisabled.html";
		final String pathInfo = request.getPathInfo();

		if (pathInfo != null && !"".equals(pathInfo)) {
			final String[] pathInfoTidied = pathInfo.split("/");
	    	
	    	if (pathInfoTidied != null && pathInfoTidied.length > 0) {
	    		if (pathInfoTidied[1] != null && !"".equals(pathInfoTidied[1])) {
	    			final String alarmOneTimeUrl = HomeAutomationProperties.getProperty("AlarmOneTimeUrl");
	    			if (alarmOneTimeUrl != null && !"".equals(alarmOneTimeUrl) && alarmOneTimeUrl.equals(pathInfoTidied[1])) {
	    				log.info("One time alarm URL recognised [" + pathInfoTidied[1] + "] - disabling alarm");
	    				redirect = "/alarmDisabled.html";
	    				HomeAutomationProperties.setOrUpdateProperty("AlarmOneTimeUrl", "");
	    				HomeAutomationProperties.setOrUpdateProperty("ApartmentUnexpectedOccupancy", "false");
	    				
	    				AlarmUnit outdoorAlarmUnit = (AlarmUnit) DeviceListManager.getControllableDeviceByLocation(Zone.PATIO).get(0);
	    				AlarmUnit indoorAlarmUnit = (AlarmUnit) DeviceListManager.getControllableDeviceByLocation(Zone.HALLWAY).get(0);
	    				
	    				//leave actual switch off command after check in case device is on but we think it isn't
	    				if (outdoorAlarmUnit.isDeviceOn() || indoorAlarmUnit.isDeviceOn()) {
	    					log.info("Alarm is activated - forcing deactivation now via one-time URL");
	    				}
						outdoorAlarmUnit.turnDeviceOff(false);
						indoorAlarmUnit.turnDeviceOff(false);
						indoorAlarmUnit.setToStrobeOnlyMode();
	    			}
	    			else {
	    				log.info("One time alarm URL not recognised [" + pathInfoTidied[1] + "]");
	    			}
	    		}
	    	}
		}
		
		//TODO: make RequestDispatcher but prevent infinite loop
		//RequestDispatcher dispatch = request.getRequestDispatcher(redirect);
		//dispatch.forward(request, response);
		
		response.sendRedirect(redirect);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
