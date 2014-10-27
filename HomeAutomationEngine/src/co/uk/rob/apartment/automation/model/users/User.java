package co.uk.rob.apartment.automation.model.users;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = 1579184153393122582L;
	private String firstName;
	private String lastName;
	private String username;
	private String mobileNumber;
	private String email;
	private String passwordHash;
	private String cookieHash;
	private boolean activationTextSent;
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}
	
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public String getCookieHash() {
		return cookieHash;
	}
	
	public void setCookieHash(String cookieHash) {
		this.cookieHash = cookieHash;
	}
	
	public boolean isActivationTextSent() {
		return activationTextSent;
	}

	public void setActivationTextSent(boolean activationTextSent) {
		this.activationTextSent = activationTextSent;
	}
}
