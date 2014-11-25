package co.uk.rob.apartment.automation.model.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.security.CookieManager;
import co.uk.rob.apartment.automation.model.users.User;
import co.uk.rob.apartment.automation.model.users.UserManager;
import co.uk.rob.apartment.automation.utilities.HomeAutomationProperties;

/**
 * Servlet Filter implementation class AuthenticationManagementFilter
 */
@WebFilter("/AuthenticationManagementFilter")
public class AuthenticationManagementFilter implements Filter {
	private Logger log = Logger.getLogger(AuthenticationManagementFilter.class);

    /**
     * Default constructor. 
     */
    public AuthenticationManagementFilter() {
    	
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		
	}

	/**
	 * Allows the following types of requests to reach the backend:
	 * - static content
	 * - within apartment + on Wifi
	 * - out of range of apartment + not on Wifi + valid cookie
	 *  
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse servletResponse = (HttpServletResponse) response;
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		String lat = request.getParameter("lat");
		String lng = request.getParameter("lng");
		boolean allowLogin = false;
		boolean hasInvalidCookie = false;
		
		//static content
		if (isStaticOrAllowedContent(servletRequest.getRequestURI())) {
			chain.doFilter(request, response);
			
			return;
		}
		
		if (lat != null && !"".equals(lat) && lng != null && !"".equals(lng)) {
			//always check cookies first to prevent implicitLogin being set even if you have cookie
			Cookie[] cookies = servletRequest.getCookies();
			
			if (cookies != null) {
				Cookie automationCookie = CookieManager.getCookie(cookies);
				User user = UserManager.getUser(automationCookie);
				
				if (user != null) {
					servletRequest.getSession().setAttribute("activeUser", user.getUsername());
					allowLogin = true;
				}
				else {
					hasInvalidCookie = true;
				}
			}
			else {
				hasInvalidCookie = true;
			}
			
			//if cookie login failed, check they aren't allowed in implicitly
			if (!allowLogin) {
				//within apartment + on Wifi
				if (isNearToApartment(lat, lng) && isClientOnWifi(request)) {
					servletRequest.getSession().setAttribute("activeUser", "implicitLogin");
					allowLogin = true;
				}
			}
		}
		
		if (allowLogin) {
			chain.doFilter(request, response);
		}
		else if (hasInvalidCookie) {
			log.info("Client does not have the correct cookie and not candidate for implicit login");
			servletResponse.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
		}
		else {
			servletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access denied: cannot implicitly authorise client");
		}
	}
	
	private boolean isStaticOrAllowedContent(String requestURI) {
		if ((requestURI.contains("/resources/styling") && requestURI.contains(".css")) ||
				(requestURI.contains("/resources/js") && requestURI.contains(".js")) ||
				(requestURI.contains("/resources/js") && requestURI.contains(".map")) ||
				(requestURI.contains("/resources/images")) ||
				(requestURI.equals("/") || requestURI.equals("/HomeAutomationEngine/")) ||
				(requestURI.contains(".html") && requestURI.contains(".htm")) ||
				(requestURI.contains("/login"))) {
			return true;
		}
		
		return false;
	}
	
	private boolean isNearToApartment(String lat, String lng) {
		//original co-ords: lat: 51.3927, lon:-0.0799
		Double latDouble = null;
		Double lngDouble = null;
		
		try {
			latDouble = Double.parseDouble(lat);
			lngDouble = Double.parseDouble(lng);
		}
		catch (Exception e) {
			return false;
		}
		
		if (latDouble != null && lngDouble != null && latDouble.compareTo(51.3917) > 0 && latDouble.compareTo(51.3937) < 0 &&
				lngDouble.compareTo(-0.0789) < 0 && lngDouble.compareTo(-0.0809) > 0) {
			return true;
		}
		
		return false;
	}
	
	private boolean isClientOnWifi(ServletRequest request) {
		String[] remoteHost = HomeAutomationProperties.getProperty("RouterIP").split("\\.");
		String[] ip = request.getRemoteAddr().split("\\.");
		if (ip.length == 4 && remoteHost[0].equals(ip[0]) &&
				remoteHost[1].equals(ip[1]) && remoteHost[2].equals(ip[2]) && remoteHost[3].equals(ip[3])) {
			return true;
		}
		
		return false;
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		
	}

}
