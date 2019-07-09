package com.knpc.dims.db.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBConfiguration {


	private static DBConfiguration sysConfig;

	public String JDBC_DRIVER;
	public String DB_URL;
	public String USER_NAME;
	public String PASSWORD;
	public String DATASOURCE_NAME;
	
	private DBConfiguration()
	{
		try {
			loadProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static DBConfiguration getInstance()
	{
		if (sysConfig == null)
		{
			sysConfig = new DBConfiguration();
		}
		
		return sysConfig;
	}
	
	private void loadProperties() throws Exception
	{
		String encKey = "B4C787C5D72EDD343EEC752B02FC5E80"; // Encryption Key
		
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("DIMS.properties");
		Properties props = new Properties();
		try {
			props.load(is);
			JDBC_DRIVER = props.getProperty("JDBC_DRIVER");
			DB_URL	= props.getProperty( "DB_URL" ) ;
			USER_NAME	= props.getProperty( "USER_NAME" ) ;
			PASSWORD = props.getProperty("PASSWORD");
			DATASOURCE_NAME = props.getProperty("DATASOURCE_NAME");
			
			ECMEncryption enc = new ECMEncryption();
			PASSWORD = enc.getDecryptedString(encKey, PASSWORD);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		finally
		{
			is.close();
		}
		
	}


}
