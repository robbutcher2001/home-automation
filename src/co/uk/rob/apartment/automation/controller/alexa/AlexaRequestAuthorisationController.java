package co.uk.rob.apartment.automation.controller.alexa;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

/**
 * Servlet implementation class AlexaRequestAuthorisationController
 */
@WebServlet("/AlexaRequestAuthorisationController")
public class AlexaRequestAuthorisationController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(AlexaRequestAuthorisationController.class);
	private DecryptAuthorisationHeader decrypter = null;
	private PrivateKey privateKey = null;
	private String symmetricKey;
	private JsonResponder responder;
	private final String defaultResponse;
    
	//controllers
	private final LoungeAlexaController loungeController;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AlexaRequestAuthorisationController() {
    	super();
    	
		try {
			this.decrypter = new DecryptAuthorisationHeader();
			this.responder = new JsonResponder();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
		
		this.defaultResponse = this.responder.createFailAsJson("Request could not be completed.");
		this.loungeController = new LoungeAlexaController();
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String responseMessage = this.defaultResponse;
		PrintWriter out = response.getWriter();
		
		if (this.privateKey == null) {
			this.privateKey = this.decrypter.getPrivateKey(request.getServletContext(), "/WEB-INF/alexa-private-key.der");
		}
		
		if (this.symmetricKey == null) {
			this.symmetricKey = this.decrypter.getSymmetricKey(request.getServletContext(), "/WEB-INF/symmetric_key");
		}
		
		String authHeader = request.getHeader("AutomationAuth");
		
		if (authHeader != null) {
			try {
				final String[] authHeaderTokenised =
						this.decrypter.decryptText(authHeader, this.privateKey).split(":");
				
				if (authHeaderTokenised.length == 2) {
					if (this.symmetricKey.equals(authHeaderTokenised[0])) {
						try {
							final Calendar oneMinuteAgo = Calendar.getInstance();
							oneMinuteAgo.add(Calendar.MINUTE, -1);
							
							final Calendar requestTime = Calendar.getInstance();
							requestTime.setTimeInMillis(Long.parseLong(authHeaderTokenised[1]));
							
							if (requestTime.after(oneMinuteAgo)) {
								String pathInfo = request.getPathInfo();
								if (pathInfo != null && !"".equals(pathInfo)) {
									final String[] pathInfoTidied = pathInfo.split("/");
									
									if (pathInfoTidied.length == 3 && "lounge".equals(pathInfoTidied[1])) {
										final String rsp = this.loungeController.informLounge(pathInfoTidied[2]);
										JSONObject message = new JSONObject();
										message.put("message", rsp);
										responseMessage = this.responder.createSuccessAsJson(message);
									}
									else {
										responseMessage = this.responder.createFailAsJson("Too many URL tokens passed.");
										this.log.info("Too many URL tokens passed " + Arrays.toString(pathInfoTidied));
									}
								}
							}
							else {
								responseMessage = this.responder.createFailAsJson("Request expired.");
								this.log.info("Request from Alexa failed: request expired [" + requestTime.toString() + "]");
							}
						}
						catch (NumberFormatException nfe) {
							this.log.error(nfe.getMessage());
						}
					}
					else {
						responseMessage = this.responder.createFailAsJson("Symmetric key mismatch.");
						this.log.info("Request from Alexa failed: symmetric key mismatch");
					}
				}
				
			} catch (InvalidKeyException ike) {
				this.log.error(ike.getMessage());
			} catch (IllegalBlockSizeException ibse) {
				this.log.error(ibse.getMessage());
			} catch (BadPaddingException bpe) {
				this.log.error(bpe.getMessage());
			}
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		out.write(responseMessage);
		out.flush();
        out.close();
	}

}
