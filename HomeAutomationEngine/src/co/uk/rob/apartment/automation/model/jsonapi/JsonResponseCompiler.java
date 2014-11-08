package co.uk.rob.apartment.automation.model.jsonapi;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

public class JsonResponseCompiler {
	
	private JSONObject jsonResponse;
	private String callbackWrapper;

	public JsonResponseCompiler() {
        this.jsonResponse = new JSONObject();
    }
	
	public void setWrapper(final String callbackWrapper) {
		this.callbackWrapper = callbackWrapper;
	}

    /**
     * @param statuses result list from devices
     * @return device status results as JSON object for browser
     */
    @SuppressWarnings("unchecked")
	public String convertResultsToJson(final List<ZoneStatusResultSet> allZoneStatuses) {
        if (allZoneStatuses != null && allZoneStatuses.size() > 0) {
        	this.jsonResponse.clear();
            this.jsonResponse.put("status", "success");
            
            for (ZoneStatusResultSet zone : allZoneStatuses) {
            	JSONObject resultList = new JSONObject();
            	Set<String> keys = zone.getSensors();
            	for (String key : keys) {
            		JSONObject statusJson = new JSONObject();
            		Map<String, String> statuses = zone.getStatuses(key);
            		for (Map.Entry<String, String> status : statuses.entrySet()) {
	                    statusJson.put(status.getKey(), status.getValue() != null ? status.getValue() : "");
            		}
            		
            		resultList.put(key, statusJson);
            	}
            	
            	this.jsonResponse.put(zone.getZone().toString(), resultList);
            }
            
            this.jsonResponse.put("errorText", null);
        } else {
            createFailAsJson("No results found");
        }

        return wrapWithCallback(this.jsonResponse.toString());
    }

    /**
     * @param failText text to display in browser, sends JSend 'fail' status
     *        http://labs.omniti.com/labs/jsend
     * @return failText as JSON object for browser
     */
    @SuppressWarnings("unchecked")
	public String createFailAsJson(final String failText) {
    	this.jsonResponse.clear();
        this.jsonResponse.put("status", "fail");
        JSONObject fail = new JSONObject();
        fail.put("errorText", !failText.equals("") ? failText : "");
        this.jsonResponse.put("data", fail);

        return wrapWithCallback(this.jsonResponse.toString());
    }

    /**
     * @param errorText text to display in browser, sends JSend 'error' status
     *        http://labs.omniti.com/labs/jsend
     * @return errorText as JSON object for browser
     */
    @SuppressWarnings("unchecked")
	public String createErrorAsJson(final String errorText) {
    	this.jsonResponse.clear();
        this.jsonResponse.put("status", "error");
        this.jsonResponse.put("message", !errorText.equals("") ? errorText : "");

        return wrapWithCallback(this.jsonResponse.toString());
    }

    /**
     * Wraps a string with this.callbackWrapper so cross-domain calls can receive this JSON
     * 
     * @param itemToWrap string to wrap with this.callbackWrapper
     * @return wrapper itemToWrap or unwrapped itemToWrap is this.callbackWrapper is null or empty
     */
    private String wrapWithCallback(final String itemToWrap) {
        String callbackStart = "(", callbackEnd = ");";
        if (this.callbackWrapper != null && !this.callbackWrapper.equals("")) {
            return this.callbackWrapper + callbackStart.concat(itemToWrap) + callbackEnd;
        }

        return itemToWrap;
    }
}
