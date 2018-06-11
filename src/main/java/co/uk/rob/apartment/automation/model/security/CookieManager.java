package co.uk.rob.apartment.automation.model.security;

import javax.servlet.http.Cookie;

public class CookieManager
{
	private static int cookieBestBeforeDays = 30;
	
	/**
	 * Get the cookies belonging to our domain and choose the correct one
	 * we set and return this.
	 * 
	 * @param request
	 * @return
	 */
	public static Cookie getCookie(Cookie[] cookies) {
		if (cookies != null) {
			for (Cookie crumb : cookies) {
				if ("RobsApartment".compareTo(crumb.getName()) == 0) {
					return crumb;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Create new cookie with a session hash + salt
	 * and set the expiry to cookieExpiry.
	 * 
	 * @return Cookie or null
	 */
	//TODO: once this cookie is returned, you need to write this to a file - DONE but check no ConcurrentModificationException will be called when this happens
	public static Cookie createUserCookie() {
		int cookieExpiry = 60 * 60 * (24 * cookieBestBeforeDays);
		
		Long salt = System.currentTimeMillis();
		String hash = Hash.getHash(Hash.generateRandomHash() + salt.toString());
		Cookie cookie = null;
		
		if (hash != null) {
			cookie = new Cookie("RobsApartment", hash);
	    	cookie.setMaxAge(cookieExpiry);
	    	cookie.setHttpOnly(true);
	    	cookie.setSecure(true);
		}
		
    	return cookie;
	}
}
