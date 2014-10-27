package co.uk.rob.apartment.automation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

/**
 * Servlet implementation class DeviceStatusController
 */
@WebServlet("/DeviceStatusController")
public class DeviceStatusController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private List<ReportingDevice> reportingDevices;
	private ControllableDevice dehumidifier;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeviceStatusController() {
        super();
        reportingDevices = DeviceListManager.getReportingDevices();
        dehumidifier = DeviceListManager.getControllableDeviceByLocation(Zone.ROB_ROOM).get(2);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    //TODO: Do not assume all devices return a value for temp, lux etc - some might be null
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String statuses = "";
		
		if (!CommonQueries.isApartmentOccupied()) {
			DateFormat format = new SimpleDateFormat("dd/MM HH:mm");
			
			Calendar lastOccupancy = CommonQueries.getLastApartmentOccupancyTime();
			
			if (lastOccupancy != null) {
				statuses += format.format(lastOccupancy.getTime()) + ";";
			}
			else {
				statuses += "<null>;";
			}
		}
		else {
			statuses += "false" + ";";
		}
		
		reportingDevices = DeviceListManager.getReportingDeviceByLocation(Zone.HALLWAY);
		for (ReportingDevice device : reportingDevices) {
			statuses += device.isTriggered() + ";";
			statuses += device.getBatteryLevel() == 255 ? "0;" : device.getBatteryLevel() + ";";
		}
		
		reportingDevices = DeviceListManager.getReportingDeviceByLocation(Zone.LOUNGE);
		statuses += reportingDevices.get(0).isTriggered() + ";";
		statuses += reportingDevices.get(0).getTemperature()[2] + ";";
		statuses += reportingDevices.get(0).getLuminiscence()[2] + ";";
		statuses += reportingDevices.get(0).getHumidity()[2] + ";";
		statuses += reportingDevices.get(0).getBatteryLevel() == 255 ? "0;" : reportingDevices.get(0).getBatteryLevel() + ";";
		
		reportingDevices = DeviceListManager.getReportingDeviceByLocation(Zone.ROB_ROOM);
		statuses += reportingDevices.get(1).isTriggered() + ";";
		statuses += reportingDevices.get(0).isTriggered() + ";";
		statuses += reportingDevices.get(0).getTemperature()[2] + ";";
		statuses += reportingDevices.get(0).getLuminiscence()[2] + ";";
		statuses += reportingDevices.get(0).getHumidity()[2] + ";";
		statuses += dehumidifier.isDeviceOn() + ";";
		statuses += reportingDevices.get(0).getBatteryLevel() == 255 ? "0;" : reportingDevices.get(0).getBatteryLevel() + ";";
		statuses += reportingDevices.get(1).getBatteryLevel() == 255 ? "0;" : reportingDevices.get(1).getBatteryLevel() + ";";
		
		//next element is index 16
		String loungeBedroomMode = HomeAutomationProperties.getProperty("LoungeBedroomMode");
		if (loungeBedroomMode != null && "true".equals(loungeBedroomMode)) {
			statuses += "true;";
		}
		else {
			statuses += "false;";
		}
		
		String robRoomBedroomMode = HomeAutomationProperties.getProperty("RobRoomBedroomMode");
		if (robRoomBedroomMode != null && "true".equals(robRoomBedroomMode)) {
			statuses += "true;";
		}
		else {
			statuses += "false;";
		}
		
		String atHomeModeLounge = HomeAutomationProperties.getProperty("AtHomeTodayMode");
		if (atHomeModeLounge != null && "true".equals(atHomeModeLounge)) {
			statuses += "true;";
		}
		else {
			statuses += "false;";
		}
		
		//response.setContentType("application/json");
		//response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		PrintWriter out = response.getWriter();
		//String json = "{online: 'true'}";
		out.print(statuses);
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatch = request.getRequestDispatcher("index.html");
		dispatch.forward(request, response);
	}

}
