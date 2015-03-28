package co.uk.rob.apartment.automation.controller;

import java.io.IOException;
import java.io.PrintWriter;

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
 * Servlet implementation class AppApartmentAlarmController
 */
@WebServlet("/AppApartmentAlarmController")
public class AppApartmentAlarmController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(AppApartmentAlarmController.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AppApartmentAlarmController() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("Alarm shut off triggered from MSO click");
		HomeAutomationProperties.setOrUpdateProperty("AlarmOneTimeUrl", "");
		HomeAutomationProperties.setOrUpdateProperty("ApartmentUnexpectedOccupancy", "false");
		
		AlarmUnit outdoorAlarmUnit = (AlarmUnit) DeviceListManager.getControllableDeviceByLocation(Zone.PATIO).get(0);
		AlarmUnit indoorAlarmUnit = (AlarmUnit) DeviceListManager.getControllableDeviceByLocation(Zone.HALLWAY).get(0);
		
		//leave actual switch off command after check in case device is on but we think it isn't
		if (outdoorAlarmUnit.isDeviceOn() || indoorAlarmUnit.isDeviceOn()) {
			log.info("Alarm is activated - forcing deactivation now");
		}
		outdoorAlarmUnit.turnDeviceOff(false);
		indoorAlarmUnit.turnDeviceOff(false);
		indoorAlarmUnit.setToStrobeOnlyMode();
		
		PrintWriter out = response.getWriter();
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		out.print(true);
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
