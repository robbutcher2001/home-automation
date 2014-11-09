package co.uk.rob.apartment.automation.model.exceptions;

/**
 * @author Rob
 *
 */
public class ZwaveModuleResponseException extends Exception {

	private static final long serialVersionUID = 5446615204027401287L;

	/**
	 * Thrown when Zwave Module is offline
	 */
	public ZwaveModuleResponseException() {
		super();
	}

	/**
	 * Thrown when Zwave Module is offline
	 * 
	 * @param message message
	 */
	public ZwaveModuleResponseException(String message) {
		super(message);
	}

}
