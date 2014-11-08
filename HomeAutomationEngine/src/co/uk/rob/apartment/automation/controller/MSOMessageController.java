package co.uk.rob.apartment.automation.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import co.uk.rob.apartment.automation.utilities.MSOMessageManager;

/**
 * Servlet implementation class MSOMessageController
 */
@WebServlet("/MSOMessageController")
public class MSOMessageController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MSOMessageController() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject rootObject = new JSONObject();
		rootObject.put("status", "success");
		rootObject.put("errorText", null);
		
		MSOMessageManager msoMessenger = new MSOMessageManager();
		JSONObject msoAsJson = new JSONObject();
        msoAsJson.put("mso_message", msoMessenger.getMessage());
        rootObject.put("data", msoAsJson);
		
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		PrintWriter out = response.getWriter();
		out.print(rootObject);
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
