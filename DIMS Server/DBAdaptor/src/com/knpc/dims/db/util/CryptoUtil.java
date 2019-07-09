package com.knpc.dims.db.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class CryptoUtil {

	/**
	 * @description private static variables 
	 */
	private static Logger		logger2			= Logger.getLogger(CryptoUtil.class);
	private static final String	CRYPTO_KEY		= "KNPC!@#$%^&*EBLA";
	private static final String	CRYPTO_ALGO		= "AES";
	private static Key			secretKeySpec	= null;
			
	/**
	 * @description Static block to initialize SecretKeySpec and Cipher 
	 */
	static {
		try {
			secretKeySpec = new SecretKeySpec(CRYPTO_KEY.getBytes("UTF-8"), CRYPTO_ALGO);
		} catch (Exception e) {
			//logger.error("Error while Constructor : ", e);
		}
	}

	
	/**
	 * @param str
	 * @return 128 bit AES Encrypted string 
	 */
	public static String encryptString(String str) {
		try {
			//logger.debug("Initializing Cipher in Encrypt_Mode");
			Cipher eCipher = Cipher.getInstance(CRYPTO_ALGO);
			eCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			
			//logger.debug("Encrypting string to byte array");
			byte[] encrypted = eCipher.doFinal(str.getBytes("UTF-8"));
			
			byte[] encryptedByteValue64 = Base64.encodeBase64(encrypted);
			String encryptedValue64 = new String(encryptedByteValue64);
			//logger.debug("Returning encrypted String");
			return encryptedValue64;
		} catch (Exception e) {
			//logger.error("Error while Decryption : " + e.getMessage());
		}
		
		return "";
	}
	
	/**
	 * @param str
	 * @return Decrypted 128 bit AES string
	 */
	public static String decryptString(String str) {
		try {
			logger2.debug("Initializing Cipher in Decrypt_Mode");
			Cipher dCipher = Cipher.getInstance(CRYPTO_ALGO);
			dCipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			
			logger2.debug("Converting string to byte array "+ str);
			byte [] decryptedValue64 = Base64.decodeBase64(str.getBytes());
			
			logger2.debug("Decrypting String");
	        byte [] decryptedByteValue = dCipher.doFinal(decryptedValue64);
	        String decryptedValue = new String(decryptedByteValue,"utf-8");
			
	        logger2.debug("Returning decrypted String");
	        return decryptedValue;
		} catch (Exception e) {
			logger2.error("Error while Decryption : " + e.getMessage());
		}
		return "";
	}

	/*public static void main(String[] args) {
		
		System.out.println(CryptoUtil.encryptString("NewDim$_10op"));
		
		System.out.println(CryptoUtil.decryptString("Mm9OTGSeUukO+Zg2Yb+Lnw=="));
		System.out.println(CryptoUtil.decryptString("0k5+gnqvLzcVgWF87fdDzA=="));
	}*/

}
