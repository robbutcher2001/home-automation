package co.uk.rob.apartment.automation.model.users;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import co.uk.rob.apartment.automation.model.security.CookieManager;
import co.uk.rob.apartment.automation.model.security.Hash;

public class UserManager {

	private static Logger log = Logger.getLogger(UserManager.class);
	private static List<User> users;
	private static String usersStore = File.separator + "home" + File.separator + "pi" +
										File.separator + "HomeAutomationUsers" + File.separator + "users";
//	private static String usersStore = File.separator + "Users" + File.separator + "Rob" +
//										File.separator + "Documents" + File.separator + "HomeAutomationUsers" +
//										File.separator + "users";
	
	@SuppressWarnings("unchecked")
	public static synchronized boolean readUsers() {
		users = new ArrayList<User>();
        ObjectInput objectIn = null;
        
        try {
            InputStream fileIn = new FileInputStream(usersStore);
            InputStream bufferIn = new BufferedInputStream(fileIn);
            objectIn = new ObjectInputStream(bufferIn);
            
            users = (List<User>) objectIn.readObject();
            objectIn.close();
            
            return true;
        }
        catch (FileNotFoundException e) {
        	storeUsers();
        }
        catch (IOException e) {
        	e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
        	try {
        		if (objectIn != null) {
        			objectIn.close();
        		}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        return false;
    }
	
	public static synchronized boolean storeUsers() {
		if (users == null) {
			users = new ArrayList<User>();
		}
		
		ObjectOutput objectOut = null;
		
		try {
			OutputStream fileOut = new FileOutputStream(usersStore);
			OutputStream bufferOut = new BufferedOutputStream(fileOut);
			objectOut = new ObjectOutputStream(bufferOut);
			
			objectOut.writeObject(users);
			objectOut.close();
			
			return true;
			
		} catch(Exception ex) {
			log.info("Cannot write user to file");
		}
		finally {
			try {
				if (objectOut != null) {
					objectOut.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	public static synchronized boolean addNewUser(User newUser) {
		boolean usersRead = false;
		if (users == null) {
			usersRead = readUsers();
		}
		else {
			usersRead = true;
		}
		
		if (!usersRead) {
			log.error("AddNewUser: Error whilst reading users from file");
			
			return false;
		}
		
		boolean canAdd = true;
		for (User user : users) {
			if (newUser.getUsername().equals(user.getUsername())) {
				canAdd = false;
			}
		}
		
		if (canAdd) {
			users.add(newUser);
			
			return storeUsers();
		}
		
		return false;
	}
	
	public static synchronized boolean updateUser(User updateUser) {
		boolean usersRead = false;
		if (users == null) {
			usersRead = readUsers();
		}
		else {
			usersRead = true;
		}
		
		if (!usersRead) {
			log.error("UpdateUser: Error whilst reading users from file");
			
			return false;
		}
		
		Integer toUpdate = null;
		for (int index = 0; index < users.size(); index++) {
			if (updateUser.getUsername().equals(users.get(index).getUsername())) {
				toUpdate = index;
			}
		}
		
		if (toUpdate != null) {
			users.remove(toUpdate);
			users.add(updateUser);
			
			return storeUsers();
		}
		
		return false;
	}
	
	public static synchronized User getUser(Cookie cookie) {
		boolean usersRead = false;
		if (users == null) {
			usersRead = readUsers();
		}
		else {
			usersRead = true;
		}
		
		if (!usersRead) {
			log.error("GetUser: Error whilst reading users from file");
			
			return null;
		}
		
		if (cookie != null) {
			for (User user : users) {
				if (cookie.getValue().equals(user.getCookieHash())) {
					return user;
				}
			}
		}
		
		return null;
	}
	
	public static synchronized List<User> getUsers() {
		return users;
	}
	
	public static synchronized Cookie logUserIn(String username, String password, HttpSession session) {
		boolean usersRead = false;
		if (users == null) {
			usersRead = readUsers();
		}
		else {
			usersRead = true;
		}
		
		if (!usersRead) {
			log.error("LogUserIn: Error whilst reading users from file");
			
			return null;
		}
		
		Cookie cookie = null;
		
		if (username != null && password != null) {
			for (User user : users) {
				if (username.equals(user.getUsername())) {
					if (authenticateUser(user, password)) {
						cookie = CookieManager.createUserCookie();
						if (cookie != null) {
							user.setCookieHash(cookie.getValue());
							if (storeUsers()) {
								session.setAttribute("activeUser", user.getUsername());
								log.info(user.getFirstName() + " " + user.getLastName() + " successfully logged in, user's cookie updated and saved to file");
							}
							else {
								log.error(user.getFirstName() + " " + user.getLastName() + " successfully logged in, user's cookie updated but could not save to file");
							}
						}
						else {
							log.error(user.getFirstName() + " " + user.getLastName() + " unsuccessfully logged in, user's cookie could not be created");
						}
						
						return cookie;
					}
					else {
						log.info("Attempted login [" + username + "] but password incorrect");
						
						return null;
					}
				}
			}
		}
		
		log.info("Attempted login but user [" + username + "] not found");
		
		return null;
	}
	
	private static boolean authenticateUser(User user, String password) {
		String passwordHash = Hash.getHash(password);
		
		if (passwordHash != null) {
			if (passwordHash.equals(user.getPasswordHash())) {
				return true;
			}
		}
		
		return false;
	}
}
