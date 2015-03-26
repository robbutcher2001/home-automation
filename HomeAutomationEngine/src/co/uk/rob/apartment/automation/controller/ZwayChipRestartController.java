package co.uk.rob.apartment.automation.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.ZwayResultSet;
import co.uk.rob.apartment.automation.utilities.CallZwaveModule;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

/**
 * Servlet implementation class ZwayChipRestartController
 */
@WebServlet("/ZwayChipRestartController")
public class ZwayChipRestartController extends HttpServlet {
	private Logger log = Logger.getLogger(ZwayChipRestartController.class);
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ZwayChipRestartController() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean successfulRestart = false;
		String activeUser = (String) request.getSession().getAttribute("activeUser");
		if (activeUser == null) {
			activeUser = "";
		}
		
		if (activeUser.equals("rbutcher")) {
			String host = HomeAutomationProperties.getProperty("host");
			String chipRestart = HomeAutomationProperties.getProperty("chipRestart");
			ZwayResultSet zwayResponse = CallZwaveModule.speakToModule(host + chipRestart);
			
			if (zwayResponse != null && zwayResponse.getResponseCode() == 200) {
				successfulRestart = true;
			}
			else {
				log.error("Couldn't restart Zway chip [" + activeUser + "]");
			}
		}
		else {
			log.info("Request to restart chip but by unauthorised user [" + activeUser + "]");
		}
		
		if (successfulRestart) {
			response.sendRedirect("/chipRestarted.html");
		}
		else {
			response.sendRedirect("/index.html");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatch = request.getRequestDispatcher("index.html");
		dispatch.forward(request, response);
	}

}
