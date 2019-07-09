package com.knpc.dims.server.util;



public class CryptoUtil {/*

	*//**
	 * @description private static variables 
	 *//*
	//private static Logger		logger			= Logger.getLogger(CryptoUtil.class);
	private static final String	CRYPTO_KEY		= "KNPC!@#$%^&*EBLA";
	private static final String	CRYPTO_ALGO		= "AES";
	private static Key			secretKeySpec	= null;
			
	*//**
	 * @description Static block to initialize SecretKeySpec and Cipher 
	 *//*
	static {
		try {
			secretKeySpec = new SecretKeySpec(CRYPTO_KEY.getBytes("UTF-8"), CRYPTO_ALGO);
		} catch (Exception e) {
			//logger.error("Error while Constructor : ", e);
		}
	}

	public static void main(String[] args) {
//		System.out.println(CryptoUtil.encryptString("Ebla1234"));
		
		System.out.println(CryptoUtil.encryptString("ecmadminP"));
		
		System.out.println(CryptoUtil.decryptString("nKWaAcyvCpkn4J5el7Js+g=="));
	}
	*//**
	 * @author naveen
	 * @param str
	 * @return 128 bit AES Encrypted string 
	 *//*
	public static String encryptString(String str) {
		try {
			//logger.debug("Initializing Cipher in Encrypt_Mode");
			Cipher eCipher = Cipher.getInstance(CRYPTO_ALGO);
			eCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			
			//logger.debug("Encrypting string to byte array");
			byte[] encrypted = eCipher.doFinal(str.getBytes("UTF-8"));
			
			String encryptedValue64 = Base64.encodeBase64String(encrypted);
			
			//logger.debug("Returning encrypted String");
			return encryptedValue64;
		} catch (Exception e) {
			//logger.error("Error while Decryption : " + e.getMessage());
		}
		
		return "";
	}
	
	*//**
	 * @author naveen
	 * @param str
	 * @return Decrypted 128 bit AES string
	 *//*
	public static String decryptString(String str) {
		try {
			//logger.debug("Initializing Cipher in Decrypt_Mode");
			Cipher dCipher = Cipher.getInstance(CRYPTO_ALGO);
			dCipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			
			//logger.debug("Converting string to byte array "+ str);
			byte [] decryptedValue64 = Base64.decodeBase64(str);
			
			//logger.debug("Decrypting String");
	        byte [] decryptedByteValue = dCipher.doFinal(decryptedValue64);
	        String decryptedValue = new String(decryptedByteValue,"utf-8");
			
	        //logger.debug("Returning decrypted String");
	        return decryptedValue;
		} catch (Exception e) {
			//logger.error("Error while Decryption : " + e.getMessage());
		}
		return "";
	}
*/}
