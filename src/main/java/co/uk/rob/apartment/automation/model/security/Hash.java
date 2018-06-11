package co.uk.rob.apartment.automation.model.security;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Hash
{
    public static final String getHash(String nameToHash) {
        String hashString = null;
        
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.reset();
			digest.update(nameToHash.getBytes("UTF-8"));
			byte[] hash = digest.digest();
	        
			hashString = "";
	        for (Byte b : hash) {
	            hashString += b.toString();
	        }
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        
        return hashString;
    }
    
    /**
     * Generate a secure random hash to identify user from their cookie.
     * 
     * @return
     */
    public static String generateRandomHash() {
    	//source: http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string-in-java
    	SecureRandom random = new SecureRandom();
    	
	    return new BigInteger(130, random).toString(32);
    }
}
