package co.uk.rob.apartment.automation.utilities;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * @author Rob
 *
 */
public class OneTimeUrlGenerator {

	public static void main(String[] a) {
		System.out.println(getOneTimeString());
		System.out.println(UUID.randomUUID().toString());
	}
	
	public static String getOneTimeString() {
		SecureRandom random = new SecureRandom();
    	
		return new BigInteger(30, random).toString();
	}
}
