package co.uk.rob.apartment.automation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import co.uk.rob.apartment.automation.model.devices.ElectricBlanket;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

/**
 * Servlet implementation class BedroomOneTabletViewController (Rob's room)
 */
@WebServlet("/BedroomOneTabletViewController")
public class BedroomOneTabletViewController extends HttpServlet {
	private Logger log = Logger.getLogger(BedroomOneTabletViewController.class);
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BedroomOneTabletViewController() {
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
		
		if (action == null) {
			RequestDispatcher dispatch = request.getRequestDispatcher("tablet.html");
			dispatch.forward(request, response);
		}
		else {
			List<ControllableDevice> devicesToControl = DeviceListManager.getControllableDeviceByLocation(Zone.ROB_ROOM);
			
			ControllableDevice lampRobEndpoint = devicesToControl.get(0);
			ControllableDevice ceilingLightRobEndpoint = devicesToControl.get(1);
			ControllableDevice dehumidifier = devicesToControl.get(2);
			ControllableDevice ledRodRobEndpoint = devicesToControl.get(3);
			ControllableDevice electricBlanket = devicesToControl.get(4);
			
			if (action == null || action.equals("")) {
				RequestDispatcher dispatch = request.getRequestDispatcher("index.html");
				dispatch.forward(request, response);
			} else if (action.equals("fullLighting")) {
				log.info("Request from tablet for full lights on in Rob's room [" + activeUser + "]");
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
				
				//set next toggle as lights on soft
				HomeAutomationProperties.setOrUpdateProperty("RobRoomNextLightingState", "soft");
			}
			else if (action.equals("subtleLighting")) {
				log.info("Request from tablet for soft mood in Rob's room [" + activeUser + "]");
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
				
				//set next toggle as lights off
				HomeAutomationProperties.setOrUpdateProperty("RobRoomNextLightingState", "off");
			}
			else if (action.equals("lightingOff")) {
				log.info("Request from tablet for all lights off in Rob's room [" + activeUser + "]");
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
				
				//set next toggle as lights on full
				HomeAutomationProperties.setOrUpdateProperty("RobRoomNextLightingState", "full");
			}
			else if (action.equals("blanket20")) {
				if (electricBlanket instanceof ElectricBlanket) {
					((ElectricBlanket) electricBlanket).turnDeviceOnFor20Minutes();
					out.print(((ElectricBlanket) electricBlanket).getCurrentStateText());
				}
				log.info("Electric blanket 20 minute toggle request for Rob's room [" + activeUser + "]");
			}
			else if (action.equals("blanketHour")) {
				if (electricBlanket instanceof ElectricBlanket) {
					((ElectricBlanket) electricBlanket).turnDeviceOnForOneHour();
					out.print(((ElectricBlanket) electricBlanket).getCurrentStateText());
				}
				log.info("Electric blanket one hour toggle request for Rob's room [" + activeUser + "]");
			}
			else if (action.equals("blanketAllNight")) {
				if (electricBlanket instanceof ElectricBlanket) {
					((ElectricBlanket) electricBlanket).turnDeviceOnUntilMorning();
					out.print(((ElectricBlanket) electricBlanket).getCurrentStateText());
				}
				log.info("Electric blanket all night toggle request for Rob's room [" + activeUser + "]");
			}
			else if (action.equals("blanketOff")) {
				if (electricBlanket instanceof ElectricBlanket) {
					((ElectricBlanket) electricBlanket).turnDeviceOff(true);
					out.print(((ElectricBlanket) electricBlanket).getCurrentStateText());
				}
				log.info("Electric blanket off toggle request for Rob's room [" + activeUser + "]");
			}
			else if (action.equals("dehumOff")) {
				if (dehumidifier.isDeviceOn() && CommonQueries.hasDehumidifierBeenInStateForOverHour(dehumidifier.getLastInteractedTime())) {
					log.info("Request from tablet for dehumidifier off in Rob's room - been on for over an hour so switching off [" + activeUser + "]");
					dehumidifier.turnDeviceOff(true);
					out.print("Dehumidifier is now off");
				}
				else if (!dehumidifier.isDeviceOn()) {
					log.info("Request from tablet for dehumidifier off in Rob's room - dehumidifier is off [" + activeUser + "]");
					out.print("Dehumidifier is already off");
				}
				else {
					log.info("Request from tablet for dehumidifier off in Rob's room - has not been on for over an hour so not switching off to protect compressor [" + activeUser + "]");
					DateFormat format = new SimpleDateFormat("HH:mm");
					String lastUpdated = format.format(dehumidifier.getLastInteractedTime());
					out.print("Dehumidifier only been on since " + lastUpdated);
				}
			}
			else if (action.equals("bedroomMode")) {
				String robRoomBedroomMode = HomeAutomationProperties.getProperty("RobRoomBedroomMode");
				if (robRoomBedroomMode != null && "false".equals(robRoomBedroomMode)) {
					HomeAutomationProperties.setOrUpdateProperty("RobRoomBedroomMode", "true");
					log.info("Request from tablet for full bedroom mode in Rob's room [" + activeUser + "]");
					out.print("Full bedroom mode now on");
				}
				else {
					HomeAutomationProperties.setOrUpdateProperty("RobRoomBedroomMode", "false");
					log.info("Request from tablet for normal bedroom mode in Rob's room [" + activeUser + "]");
					out.print("Normal bedroom mode now back on");
				}
			}
			else {
				out.print("[button not implemented]");
			}
		}
		
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatch = request.getRequestDispatcher("tablet.html");
		dispatch.forward(request, response);
	}
	
}
