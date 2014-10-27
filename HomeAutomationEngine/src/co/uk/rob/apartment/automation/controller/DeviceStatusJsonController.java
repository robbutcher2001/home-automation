package co.uk.rob.apartment.automation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.jsonapi.DeviceStatusCompiler;
import co.uk.rob.apartment.automation.model.jsonapi.JsonResponseCompiler;
import co.uk.rob.apartment.automation.model.jsonapi.RestRequest;
import co.uk.rob.apartment.automation.model.jsonapi.SimplifiedDeviceStatusCompiler;
import co.uk.rob.apartment.automation.model.jsonapi.ZoneStatusResultSet;

/**
 * Servlet implementation class DeviceStatusJsonController
 */
@WebServlet("/DeviceStatusJsonController")
public class DeviceStatusJsonController extends HttpServlet {
	private Logger log = Logger.getLogger(DeviceStatusJsonController.class);
	private static final long serialVersionUID = 1L;
	private JsonResponseCompiler responseCompiler = null;
	private SimplifiedDeviceStatusCompiler statusCompiler = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeviceStatusJsonController() {
        super();
        this.responseCompiler = new JsonResponseCompiler();
        this.statusCompiler = new SimplifiedDeviceStatusCompiler();
    }

    /**
     * GET method for a status update of all devices throughout apartment
     * 
     * @param planType - the type of account to search against
     * @param number - the phone number up to 11 numbers
     * @return a formated JSON response
     */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = null;
        String wrapper = request.getParameter("callback");
        this.responseCompiler.setWrapper(wrapper);

        try {
            out = response.getWriter();
            String pathInfo = request.getPathInfo();

            if (pathInfo != null && !"".equals(pathInfo)) {
                RestRequest restUrl = new RestRequest(pathInfo);
                final Zone zone = restUrl.getZone();
                if (zone != null) {
                	List<ZoneStatusResultSet> allZoneStatuses = new ArrayList<ZoneStatusResultSet>();
                	if (Zone.APARTMENT.equals(zone)) {
                		JSONObject rootObject = new JSONObject();
                		rootObject.put("status", "success");
                		rootObject.put("errorText", null);
                		this.statusCompiler.testZoneStatus(Zone.APARTMENT, rootObject);
                		this.statusCompiler.testZoneStatus(Zone.LOUNGE, rootObject);
                		this.statusCompiler.testZoneStatus(Zone.ROB_ROOM, rootObject);
                		this.statusCompiler.testZoneStatus(Zone.HALLWAY, rootObject);
                		this.statusCompiler.testZoneStatus(Zone.PATIO, rootObject);
                		out.print(rootObject);
                	}
                	else {
                		//temp - delete this if above works
                		DeviceStatusCompiler statusCompiler = new DeviceStatusCompiler();
                		allZoneStatuses.add(statusCompiler.getZoneStatus(Zone.APARTMENT));
                		out.print(this.responseCompiler.convertResultsToJson(allZoneStatuses));
                	}
                } else {
                    out.print(this.responseCompiler.createFailAsJson("Incorrect params"));
                }
            } else {
                out.print(this.responseCompiler.createFailAsJson("Incorrect params"));
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            out.print(this.responseCompiler.createErrorAsJson("Server error"));
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            log.error(e.getMessage());
        } catch (Error e) {
            out.print(this.responseCompiler.createErrorAsJson("Server error"));
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            log.error(e.getMessage());
        }

        response.setContentType("application/json");
        if (out != null) {
            out.flush();
            out.close();
        }
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
