package co.uk.rob.apartment.automation.model.jsonapi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.uk.rob.apartment.automation.model.Zone;


public class RestRequest {
	
    private final static Pattern zoneRegex;
    private final Matcher zoneMatcher;
    private Zone zone = null;
    
    static {
    	zoneRegex = Pattern.compile("^([a-z_]{5,13})");
    }
    
    /**
     * @param pathInfo the pathInfo string from HttpServletRequest
     */
    public RestRequest(final String pathInfo) {
    	final String[] pathInfoTidied = pathInfo.split("/");
    	
    	if (pathInfoTidied != null && pathInfoTidied.length > 0) {
    		if (pathInfoTidied[1] != null && !"".equals(pathInfoTidied[1])) {
    			this.zoneMatcher = RestRequest.zoneRegex.matcher(pathInfoTidied[1]);
    	        if (this.zoneMatcher.find()) {
    	            String matchedZone = this.zoneMatcher.group();
    	            for (Zone listedZone : Zone.values()) {
        				if (listedZone.toString().equals(matchedZone)) {
        					zone = listedZone;
        				}
        			}
    	        }
    		}
    		else {
    			this.zoneMatcher = null;
    		}
    	}
    	else {
			this.zoneMatcher = null;
		}
    }

    /**
     * Get parsed zone
     * 
     * @return parsed zone
     */
    public Zone getZone() {
        return zone;
    }
}
