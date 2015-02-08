package co.uk.rob.apartment.automation.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;
import co.uk.rob.apartment.automation.utilities.SMSHelper;

/**
 * Servlet implementation class LoungeKitchenCameraController
 */
@WebServlet("/LoungeKitchenCameraController")
public class LoungeKitchenCameraController extends HttpServlet {
	private Logger log = Logger.getLogger(LoungeKitchenCameraController.class);
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoungeKitchenCameraController() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		URL url = new URL("http://192.168.1.56/image/jpeg.cgi");
		OutputStream outputStream = new ByteArrayOutputStream();
		InputStream is = null;
		
		try {
			is = url.openStream();
			
			byte[] bytes = new byte[2048];
			int n;
			
			while ((n = is.read(bytes)) > 0) {
				outputStream.write(bytes, 0, n);
			}
		}
		catch (IOException ioe) {
			log.error("Cannot read camera input stream, it may be switched off at wall. Sending warning SMS.");
			
			final String cameraOffAlertSent = HomeAutomationProperties.getProperty("CameraOffAlertSent");
			if (cameraOffAlertSent != null && !"false".equals(cameraOffAlertSent)) {
				HomeAutomationProperties.setOrUpdateProperty("CameraOffAlertSent", "true");
				SMSHelper.sendSMS("07965502960", "Camera appears to have been switched off in lounge.");
			}
		}
		finally {
			if (is != null) {
				is.close();
			}
		}
		
		byte[] imageBytes = ((ByteArrayOutputStream) outputStream).toByteArray();

		response.setContentType("image/jpeg");
		response.setContentLength(imageBytes.length);
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.getOutputStream().write(imageBytes);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
