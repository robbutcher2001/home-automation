package co.uk.rob.apartment.automation.controller.alexa;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletContext;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.amazonaws.util.Base64;
import com.amazonaws.util.IOUtils;

/**
 * @author Rob
 *
 */
public class DecryptAuthorisationHeader {
	
	private Cipher cipher;
	private KeyFactory keyFactory;
	
	public DecryptAuthorisationHeader() throws NoSuchAlgorithmException, NoSuchPaddingException {
		Security.addProvider(new BouncyCastleProvider());
		this.keyFactory = KeyFactory.getInstance("RSA");
		this.cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
	}
	
	public PrivateKey getPrivateKey(ServletContext context, String filename) {
		PrivateKey privateKey = null;
		InputStream is = context.getResourceAsStream(filename);
		
		try {
			byte[] fileContents = IOUtils.toByteArray(is);
			
			final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(fileContents);
			privateKey = this.keyFactory.generatePrivate(spec);
			
		}
		catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (InvalidKeySpecException ikse) {
			ikse.printStackTrace();
		}
		finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
		
		return privateKey;
	}
	
	public String getSymmetricKey(ServletContext context, String filename) {
		String symmetricKey = null;
		InputStream is = context.getResourceAsStream(filename);
		
		try {
			symmetricKey = IOUtils.toString(is);
			
		}
		catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
		
		return symmetricKey;
	}
	
	public String decryptText(String msg, PrivateKey key) throws InvalidKeyException,
			UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
		this.cipher.init(Cipher.DECRYPT_MODE, key);
		
		return new String(this.cipher.doFinal(Base64.decode(msg)), "UTF-8");
	}
}
