package co.uk.rob.apartment.automation.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

/**
 * Servlet implementation class ApartmentAlarmController
 */
@WebServlet("/ApartmentAlarmController")
public class ApartmentAlarmController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
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
		String forward = "alarmNotDisabled.html";
		final String pathInfo = request.getPathInfo();

		if (pathInfo != null && !"".equals(pathInfo)) {
			final String[] pathInfoTidied = pathInfo.split("/");
	    	
	    	if (pathInfoTidied != null && pathInfoTidied.length > 0) {
	    		if (pathInfoTidied[1] != null && !"".equals(pathInfoTidied[1])) {
	    			final String alarmOneTimeUrl = HomeAutomationProperties.getProperty("AlarmOneTimeUrl");
	    			if (alarmOneTimeUrl != null && alarmOneTimeUrl.equals(pathInfoTidied[1])) {
	    				forward = "alarmDisabled.html";
	    				
	    				//TODO: Disable alarm
	    				//http://examples.javacodegeeks.com/core-java/util/timer-util/java-timer-example/
	    			}
	    		}
	    	}
		}
		
		RequestDispatcher dispatch = request.getRequestDispatcher(forward);
		dispatch.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
