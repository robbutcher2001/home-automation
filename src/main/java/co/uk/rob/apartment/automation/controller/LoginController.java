package co.uk.rob.apartment.automation.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.users.User;
import co.uk.rob.apartment.automation.model.users.UserManager;

/**
 * Servlet implementation class LoginController
 */
@WebServlet("/LoginController")
public class LoginController extends HttpServlet {
	private Logger log = Logger.getLogger(LoginController.class);
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginController() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatch = request.getRequestDispatcher("login.html");
		dispatch.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("uname");
		String password = request.getParameter("pword");
		Cookie cookie = null;
		
		if (username != null && password != null) {
			cookie = UserManager.logUserIn(username, password);
		}
		
		PrintWriter out = response.getWriter();
		if (cookie != null) {
			response.addCookie(cookie);
			User user = UserManager.getUser(cookie);
			
			if (user != null && user.getFirstName() != null && user.getLastName() != null) {
				out.write("Hi, " + user.getFirstName() + " " + user.getLastName());
			}
			else {
				out.write("Welcome back");
			}
			response.setStatus(HttpServletResponse.SC_OK);
		}
		else {
			log.info("User not successfully logged in, cookie not created");
			out.write("Incorrect details, try again");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		
		//response.setContentType("application/json");
		//response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		//String json = "{online: 'true'}";
		out.flush();
	}

}
