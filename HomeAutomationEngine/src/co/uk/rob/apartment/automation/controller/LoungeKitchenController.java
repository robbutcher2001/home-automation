package co.uk.rob.apartment.automation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.DeviceListManager;
import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.devices.Blind;
import co.uk.rob.apartment.automation.model.interfaces.ControllableDevice;
import co.uk.rob.apartment.automation.model.interfaces.ReportingDevice;
import co.uk.rob.apartment.automation.utilities.CommonQueries;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

/**
 * Servlet implementation class LoungeKitchenController
 */
@WebServlet("/LoungeKitchenController")
public class LoungeKitchenController extends HttpServlet {
	private Logger log = Logger.getLogger(LoungeKitchenController.class);
	private static final long serialVersionUID = 1L;
    private Calendar forceAlarmOffTrigger;
    private ControllableDevice lampOneLounge;
    private ControllableDevice ledRodLounge;
    private ControllableDevice lampTwoLounge;
    private ControllableDevice bobbyLoungeLamp;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoungeKitchenController() {
        super();
        forceAlarmOffTrigger = Calendar.getInstance();
        //prevents button being accepted on first class instantiation
        forceAlarmOffTrigger.add(Calendar.SECOND, -10);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		boolean successfulCall = false;
		String activeUser = (String) request.getSession().getAttribute("activeUser");
		if (activeUser == null) {
			activeUser = "";
		}
		
		List<ControllableDevice> devicesInLoungeAndKitchen = DeviceListManager.getControllableDeviceByLocation(Zone.LOUNGE);
		devicesInLoungeAndKitchen.addAll(DeviceListManager.getControllableDeviceByLocation(Zone.KITCHEN));
		
		lampOneLounge = devicesInLoungeAndKitchen.get(0);
		ledRodLounge = devicesInLoungeAndKitchen.get(1);
		lampTwoLounge = devicesInLoungeAndKitchen.get(2);
		bobbyLoungeLamp = devicesInLoungeAndKitchen.get(5);
		Blind loungeWindowBlind = (Blind) devicesInLoungeAndKitchen.get(3);
		Blind loungePatioBlind = (Blind) devicesInLoungeAndKitchen.get(4);
		
		ReportingDevice patioDoor = DeviceListManager.getReportingDeviceByLocation(Zone.PATIO).get(1);
		
		PrintWriter out = response.getWriter();
		
		if (action == null || action.equals("")) {
			RequestDispatcher dispatch = request.getRequestDispatcher("index.html");
			dispatch.forward(request, response);
		}
		else if (action.equals("fullOnLounge")) {
			log.info("Request for all lights on in Lounge and Kitchen [" + activeUser + "]");
			successfulCall = lampOneLounge.turnDeviceOn(true, "99");
			lampOneLounge.resetAutoOverridden();
			if (successfulCall) {
				successfulCall = ledRodLounge.turnDeviceOff(true);
				ledRodLounge.resetAutoOverridden();
				if (successfulCall) {
					successfulCall = lampTwoLounge.turnDeviceOn(true, "99");
					lampTwoLounge.resetAutoOverridden();
					if (successfulCall) {
						successfulCall = bobbyLoungeLamp.turnDeviceOn(true, "99");
						bobbyLoungeLamp.resetAutoOverridden();
					}
				}
			}
			
			if (successfulCall) {
				out.print("Lights now on full in lounge");
			}
			else {
				out.print("Issue turning all lights on full");
			}
		}
		else if (action.equals("filmModeLounge")) {
			if (patioDoor.isTriggered()) {
				log.info("Request for film mode in Lounge and Kitchen but patio door is open [" + activeUser + "]");
				out.print("Close patio door");
			}
			else {
				log.info("Request for film mode in Lounge and Kitchen, turning lamps off, LED on and blinds closed [" + activeUser + "]");
				int windowBlindMovementTime = CommonQueries.calculateBlindMovementTime(loungeWindowBlind, "0");
				int patioBlindMovementTime = CommonQueries.calculateBlindMovementTime(loungePatioBlind, "0");
				
				if (patioBlindMovementTime > windowBlindMovementTime) {
					windowBlindMovementTime = patioBlindMovementTime;
				}
				
				if (!"0".equals(loungeWindowBlind.getDeviceLevel())) {
					successfulCall = loungeWindowBlind.turnDeviceOn(true);
					
					if (successfulCall) {
						successfulCall = loungePatioBlind.turnDeviceOn(true);
					}
				}

				TimerTask continueFilmModeTask = new TimerTask() {
					
					@Override
					public void run() {
						lampOneLounge.turnDeviceOff(true);
						lampOneLounge.resetAutoOverridden();
						
						lampTwoLounge.turnDeviceOff(true);
						lampTwoLounge.resetAutoOverridden();
						
						ledRodLounge.turnDeviceOn(true);
						ledRodLounge.resetAutoOverridden();
						
						bobbyLoungeLamp.turnDeviceOff(true);
						bobbyLoungeLamp.resetAutoOverridden();
					}
				};
				
				Timer timer = new Timer("Continue switching to film/TV mode");
				timer.schedule(continueFilmModeTask, windowBlindMovementTime);
				
				out.print("Switching to film mode..");
			}
		}
		else if (action.equals("offLounge")) {
			log.info("Request for all lights off and blinds up (resetting manual flag) in Lounge and Kitchen [" + activeUser + "]");
			for (ControllableDevice device : devicesInLoungeAndKitchen) {
				if (!(device instanceof Blind)) {
					successfulCall = device.turnDeviceOff(true);
					device.resetAutoOverridden();
					if (!successfulCall) {
						break;
					}
				}
			}
			
			if (loungeWindowBlind.isDeviceOn()) {
				loungeWindowBlind.resetManuallyOverridden();
			}
			
			if (loungePatioBlind.isDeviceOn()) {
				loungePatioBlind.resetManuallyOverridden();
			}
			
			boolean blindsToOpen = false;
			if (!CommonQueries.isBrightnessBelow20()) {
				blindsToOpen = true;
			}
			
			if (successfulCall) {
				if (blindsToOpen) {
					out.print("Lights now off, blinds will open soon");
				}
				else {
					out.print("Lights now off in lounge");
				}
			}
			else {
				out.print("Issue turning lights off / blinds up");
			}
		}
		else if (action.equals("blindToggle")) {
			if (loungeWindowBlind.isDeviceOn() || loungePatioBlind.isDeviceOn()) {
				log.info("Request for blinds fully open in lounge [" + activeUser + "]");
				successfulCall = loungeWindowBlind.turnDeviceOff(true);
				loungeWindowBlind.resetAutoOverridden();
				
				if (successfulCall) {
					successfulCall = loungePatioBlind.turnDeviceOff(true);
					loungePatioBlind.resetAutoOverridden();
				}
				
				if (successfulCall) {
					out.print("Blinds now opening in lounge");
				}
			}
			else {
				log.info("Request for blinds fully closed in lounge [" + activeUser + "]");
				successfulCall = loungeWindowBlind.turnDeviceOn(true);
				loungeWindowBlind.resetAutoOverridden();
				
				if (successfulCall) {
					successfulCall = loungePatioBlind.turnDeviceOn(true);
					loungePatioBlind.resetAutoOverridden();
				}
				
				if (successfulCall) {
					out.print("Blinds now closing in lounge");
				}
			}
		}
		else if (action.equals("blindTiltToggle")) {
			log.info("Request for blind tilt in lounge [" + activeUser + "]");
			if (!CommonQueries.isBrightnessBelow20()) {
				if (!loungeWindowBlind.isTilted() || !loungePatioBlind.isTilted()) {
					successfulCall = loungeWindowBlind.tiltBlindOpen();
					successfulCall = loungePatioBlind.tiltBlindOpen();
					if (successfulCall) {
						out.print("Blinds tilted down in lounge");
						log.info("Blinds tilted down in lounge [" + activeUser + "]");
					}
					else {
						out.print("Issue tilting blinds");
					}
				}
				else {
					successfulCall = loungeWindowBlind.tiltBlindClosed();
					successfulCall = loungePatioBlind.tiltBlindClosed();
					if (successfulCall) {
						out.print("Blinds tilted back up in lounge");
						log.info("Blinds tilted up in lounge [" + activeUser + "]");
					}
					else {
						out.print("Issue tilting blinds");
					}
				}
			}
			else {
				out.print("Too dark outside");
			}
		}
		else if (action.equals("bedroomModeLounge")) {
			String loungeBedroomMode = HomeAutomationProperties.getProperty("LoungeBedroomMode");
			if (loungeBedroomMode != null && "false".equals(loungeBedroomMode)) {
				HomeAutomationProperties.setOrUpdateProperty("LoungeBedroomMode", "true");
				log.info("Request for full bedroom mode in Lounge [" + activeUser + "]");
				out.print("Lounge now in bedroom mode");
			}
			else {
				HomeAutomationProperties.setOrUpdateProperty("LoungeBedroomMode", "false");
				log.info("Request for normal bedroom mode in Lounge [" + activeUser + "]");
				out.print("Lounge back to lounge mode");
			}
		}
		else if (action.equals("continuousAlarmMode")) {
			String continuousAlarmMode = HomeAutomationProperties.getProperty("ContinuousAlarmMode");
			if (continuousAlarmMode != null && "false".equals(continuousAlarmMode)) {
				HomeAutomationProperties.setOrUpdateProperty("ContinuousAlarmMode", "true");
				HomeAutomationProperties.setOrUpdateProperty("ForceDisableAlarm", "false");
				log.info("Request for 'Continuous Alarm Mode' mode for full apartment [" + activeUser + "]");
				out.print("Alarm now continuously on");
			}
			else {
				if (CommonQueries.isApartmentOccupied()) {
					HomeAutomationProperties.setOrUpdateProperty("ContinuousAlarmMode", "false");
					log.info("Request for 'Normal Alarm Mode' mode for full apartment [" + activeUser + "]");
					out.print("Normal alarm mode now resumed");
				}
				else {
					log.info("Request for 'Normal Alarm Mode' mode for full apartment but apartment has to be occupied [" + activeUser + "]");
					out.print("Apartment has to be occupied");
				}
			}
		}
		else if (action.equals("forceDisableAlarm")) {
			if (activeUser.equalsIgnoreCase("rbutcher") || activeUser.equalsIgnoreCase("scat")) {
				String forceDisableAlarm = HomeAutomationProperties.getProperty("ForceDisableAlarm");
				if (forceDisableAlarm != null && "false".equals(forceDisableAlarm)) {
					if (CommonQueries.isApartmentOccupied()) {
						Calendar now = Calendar.getInstance();
						now.add(Calendar.SECOND, -5);
						if (now.before(forceAlarmOffTrigger)) {
							HomeAutomationProperties.setOrUpdateProperty("ForceDisableAlarm", "true");
							HomeAutomationProperties.setOrUpdateProperty("ContinuousAlarmMode", "false");
							log.info("Request to permanently disable alarm in apartment, 'ForceDisableAlarm' flag set [" + activeUser + "]");
							out.print("Alarm now permanently disabled");
						}
						else {
							forceAlarmOffTrigger = Calendar.getInstance();
							out.print("Press again within 5 seconds..");
						}
					}
					else {
						log.info("Request to permanently disable alarm in apartment but apartment not occupied, 'ForceDisableAlarm' flag not set to true [" + activeUser + "]");
						out.print("Apartment has to be occupied");
					}
				}
				else {
					HomeAutomationProperties.setOrUpdateProperty("ForceDisableAlarm", "false");
					log.info("Request to re-enable alarm in apartment, 'ForceDisableAlarm' flag set to false [" + activeUser + "]");
					out.print("Alarm back to normal operating mode");
				}
			}
			else {
				log.info("Request to permanently disable alarm but user unknown [" + activeUser + "]");
				out.print("You have to login to do this, bitch!");
			}
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

}
