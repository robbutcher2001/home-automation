package co.uk.rob.apartment.automation.model.jsonapi;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.Zone;


public class RestRequest {
	
	private Logger log = Logger.getLogger(RestRequest.class);
	
	/*
     * Account number attributes
     */
//    private final Pattern accountTypeRegex;
//    private final Matcher accountTypeMatcher;
    private final Zone zone;

    /*
     * Phone number attributes
     */
//    private final Pattern phoneNumberRegex;
//    private final Matcher phoneNumberMatcher;
//    private final String level;

    /**
     * @param pathInfo the pathInfo string from HttpServletRequest
     */
    public RestRequest(final String pathInfo) {
    	final String[] pathInfoTidied = pathInfo.split("/");
    	
    	if (pathInfoTidied != null && pathInfoTidied.length > 0) {
    		if (pathInfoTidied[1] != null && !"".equals(pathInfoTidied[1])) {
    			if ("all".equals(pathInfoTidied[1])) {
    				zone = Zone.APARTMENT;
    			}
    			else if ("lounge".equals(pathInfoTidied[1])) {
    				zone = Zone.LOUNGE;
    			}
    			else if ("patio".equals(pathInfoTidied[1])) {
    				zone = Zone.PATIO;
    			}
    			else if ("robsroom".equals(pathInfoTidied[1])) {
    				zone = Zone.ROB_ROOM;
    			}
    			else {
    				zone = null;
    			}
    		}
    		else {
				zone = null;
			}
    	}
    	else {
			zone = null;
		}
    	
    	//TODO: convert to parsing URL with regex
//        final String pathInfoTidied = pathInfo.replace("/", "").toLowerCase();
//
//        this.accountTypeRegex = Pattern.compile("^([a-z]*)");
//        this.accountTypeMatcher = this.accountTypeRegex.matcher(pathInfoTidied);
//
//        if (this.accountTypeMatcher.find()) {
//            this.accountType = this.accountTypeMatcher.group();
//        } else {
//            this.accountType = null;
//        }
//
//        final String remainingUnmatchedPathInfo = this.accountTypeMatcher.replaceFirst("");
//        this.phoneNumberRegex = Pattern.compile("^([0-9]{1,11})");
//        this.phoneNumberMatcher = this.phoneNumberRegex.matcher(remainingUnmatchedPathInfo);
//
//        if (this.phoneNumberMatcher.find()) {
//            this.phoneNumber = this.phoneNumberMatcher.group();
//        } else {
//            this.phoneNumber = null;
//        }
    }

    public Zone getZone() {
        return zone;
    }

    /**
     * @return the first number sequence found in passed string matching 1 to 11 digits or null
     */
//    public String getLevel() {
//    	if (this.level != null) {
//            return this.level.toLowerCase();
//        } else {
//            return this.level;
//        }
//    }
}
