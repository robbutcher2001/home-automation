package co.uk.rob.apartment.automation.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import co.uk.rob.apartment.automation.model.Zone;
import co.uk.rob.apartment.automation.model.jsonapi.RestRequest;
import co.uk.rob.apartment.automation.model.jsonapi.DeviceStatusCompiler;

/**
 * Servlet implementation class DeviceStatusJsonController
 */
@WebServlet("/DeviceStatusJsonController")
public class DeviceStatusJsonController extends HttpServlet {
	private Logger log = Logger.getLogger(DeviceStatusJsonController.class);
	private static final long serialVersionUID = 1L;
	private DeviceStatusCompiler statusCompiler = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeviceStatusJsonController() {
        super();
        this.statusCompiler = new DeviceStatusCompiler();
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
		String activeUser = (String) request.getSession().getAttribute("activeUser");
		if (activeUser == null) {
			activeUser = "";
		}
		
        this.statusCompiler.setUser(activeUser);

        JSONObject rootObject = new JSONObject();
        try {
            out = response.getWriter();
            String pathInfo = request.getPathInfo();

            if (pathInfo != null && !"".equals(pathInfo)) {
                RestRequest restUrl = new RestRequest(pathInfo);
                final Zone zone = restUrl.getZone();
                if (zone != null) {
            		rootObject.put("status", "success");
            		rootObject.put("errorText", null);
            		
                	if (Zone.APARTMENT.equals(zone)) {
                		this.statusCompiler.getZoneStatus(Zone.APARTMENT, rootObject);
                		this.statusCompiler.getZoneStatus(Zone.LOUNGE, rootObject);
                		this.statusCompiler.getZoneStatus(Zone.ROB_ROOM, rootObject);
                		this.statusCompiler.getZoneStatus(Zone.SCARLETT_ROOM, rootObject);
                		this.statusCompiler.getZoneStatus(Zone.HALLWAY, rootObject);
                		this.statusCompiler.getZoneStatus(Zone.PATIO, rootObject);
                	}
                	else {
                		this.statusCompiler.getZoneStatus(zone, rootObject);
                	}
                	
                	out.print(rootObject);
                } else {
                    out.print(this.statusCompiler.createFailAsJson("Incorrect params", rootObject));
                }
            } else {
                out.print(this.statusCompiler.createFailAsJson("Incorrect params", rootObject));
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            out.print(this.statusCompiler.createErrorAsJson("Server error", rootObject));
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            log.error(e.getMessage());
        } catch (Error e) {
            out.print(this.statusCompiler.createErrorAsJson("Server error", rootObject));
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
