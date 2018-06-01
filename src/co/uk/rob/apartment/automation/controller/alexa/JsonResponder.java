package co.uk.rob.apartment.automation.controller.alexa;

import org.json.simple.JSONObject;

/**
 * @author Rob
 *
 */
public class JsonResponder {

	private final JSONObject rootObject;
	
	public JsonResponder() {
		this.rootObject = new JSONObject();
	}
	
	/**
     * @param dataObject to display in browser, sends JSend 'success' status
     *        http://labs.omniti.com/labs/jsend
     * @return dataObject as JSON object for browser
     */
	@SuppressWarnings("unchecked")
	public String createSuccessAsJson(final JSONObject dataObject) {
    	this.rootObject.clear();
    	this.rootObject.put("status", "success");
        this.rootObject.put("data", dataObject);

        return this.rootObject.toJSONString();
    }

	/**
     * @param failText text to display in browser, sends JSend 'fail' status
     *        http://labs.omniti.com/labs/jsend
     * @return failText as JSON object for browser
     */
	@SuppressWarnings("unchecked")
	public String createFailAsJson(final String failText) {
    	this.rootObject.clear();
    	this.rootObject.put("status", "fail");
        JSONObject errorText = new JSONObject();
        errorText.put("errorText", !failText.equals("") ? failText : "");
        this.rootObject.put("data", errorText);

        return this.rootObject.toJSONString();
    }
}
