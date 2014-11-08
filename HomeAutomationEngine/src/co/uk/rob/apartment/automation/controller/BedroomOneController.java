package co.uk.rob.apartment.automation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

/**
 * Servlet implementation class BedroomOneController (Rob's room)
 */
@WebServlet("/BedroomOneController")
public class BedroomOneController extends HttpServlet {
	private Logger log = Logger.getLogger(BedroomOneController.class);
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BedroomOneController() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		Boolean successfulCall = true;
		String activeUser = (String) request.getSession().getAttribute("activeUser");
		if (activeUser == null) {
			activeUser = "";
		}
		
		PrintWriter out = response.getWriter();
		
		if (activeUser.equalsIgnoreCase("rbutcher")) {
			List<ControllableDevice> devicesToControl = DeviceListManager.getControllableDeviceByLocation(Zone.ROB_ROOM);
			
			ControllableDevice lampRobEndpoint = devicesToControl.get(0);
			ControllableDevice ceilingLightRobEndpoint = devicesToControl.get(1);
			ControllableDevice dehumidifier = devicesToControl.get(2);
			ControllableDevice ledRodRobEndpoint = devicesToControl.get(3);
			
			if (action == null || action.equals("")) {
				RequestDispatcher dispatch = request.getRequestDispatcher("index.html");
				dispatch.forward(request, response);
			}
			else if (action.equals("fullOnRobRoom")) {
				log.info("Request for full lights on in Rob's room [" + activeUser + "]");
				successfulCall = lampRobEndpoint.turnDeviceOff(true);
				lampRobEndpoint.resetAutoOverridden();
				if (successfulCall == true) {
					successfulCall = ledRodRobEndpoint.turnDeviceOff(true);
					ledRodRobEndpoint.resetAutoOverridden();
					if (successfulCall == true) {
						successfulCall = ceilingLightRobEndpoint.turnDeviceOn(true, "99");
						ceilingLightRobEndpoint.resetAutoOverridden();
					}
				}
				
				if (successfulCall) {
					out.print("Lights now on full");
				}
				else {
					out.print("Issue turning all lights on full");
				}
			}
			else if (action.equals("softMoodRobRoom")) {
				log.info("Request for soft mood in Rob's room [" + activeUser + "]");
				successfulCall = ceilingLightRobEndpoint.turnDeviceOff(true);
				ceilingLightRobEndpoint.resetAutoOverridden();
				if (successfulCall == true) {
					successfulCall = lampRobEndpoint.turnDeviceOn(true, "35");
					lampRobEndpoint.resetAutoOverridden();
					if (successfulCall == true) {
						successfulCall = ledRodRobEndpoint.turnDeviceOn(true);
						ledRodRobEndpoint.resetAutoOverridden();
					}
				}
				
				if (successfulCall) {
					out.print("Light now on soft");
				}
				else {
					out.print("Issue turning all lights on soft");
				}
			}
			else if (action.equals("offRobRoom")) {
				log.info("Request for all lights off in Rob's room [" + activeUser + "]");
				successfulCall = ceilingLightRobEndpoint.turnDeviceOff(true);
				ceilingLightRobEndpoint.resetAutoOverridden();
				if (successfulCall == true) {
					successfulCall = lampRobEndpoint.turnDeviceOff(true);
					lampRobEndpoint.resetAutoOverridden();
					if (successfulCall == true) {
						successfulCall = ledRodRobEndpoint.turnDeviceOff(true);
						ledRodRobEndpoint.resetAutoOverridden();
					}
				}
				
				if (successfulCall) {
					out.print("Lights now off");
				}
				else {
					out.print("Issue turning all lights off");
				}
			}
			else if (action.equals("dehumRobRoom")) {
				if (dehumidifier.isDeviceOn() && hasDehumidifierBeenInStateForOverHour(dehumidifier.getLastInteractedTime())) {
					log.info("Request for dehumidifier off in Rob's room - been on for over an hour so switching off [" + activeUser + "]");
					dehumidifier.turnDeviceOff(true);
					out.print("Dehumidifier is now off");
				}
				else if (!dehumidifier.isDeviceOn()) {
					log.info("Request for dehumidifier off in Rob's room - dehumidifier is off [" + activeUser + "]");
					out.print("Dehumidifier is already off");
				}
				else {
					log.info("Request for dehumidifier off in Rob's room - has not been on for over an hour so not switching off to protect compressor [" + activeUser + "]");
					DateFormat format = new SimpleDateFormat("HH:mm");
					String lastUpdated = format.format(dehumidifier.getLastInteractedTime());
					out.print("Dehumidifier only been on since " + lastUpdated);
				}
			}
			else if (action.equals("bedroomModeRobRoom")) {
				String robRoomBedroomMode = HomeAutomationProperties.getProperty("RobRoomBedroomMode");
				if (robRoomBedroomMode != null && "false".equals(robRoomBedroomMode)) {
					HomeAutomationProperties.setOrUpdateProperty("RobRoomBedroomMode", "true");
					log.info("Request for full bedroom mode in Rob's room [" + activeUser + "]");
					out.print("Full bedroom mode now on");
				}
				else {
					HomeAutomationProperties.setOrUpdateProperty("RobRoomBedroomMode", "false");
					log.info("Request for normal bedroom mode in Rob's room [" + activeUser + "]");
					out.print("Normal bedroom mode now back on");
				}
			}
		}
		else {
			out.print("Tut tut, you have to be logged in as Rob");
		}
		
		//response.setContentType("application/json");
		//response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		//String json = "{online: 'true'}";
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatch = request.getRequestDispatcher("index.html");
		dispatch.forward(request, response);
	}
	
	private boolean hasDehumidifierBeenInStateForOverHour(Date lastUpdated) {
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
