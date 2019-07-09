package com.knpc.dims.filenet.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FilenetSystemConfiguration {

	private static FilenetSystemConfiguration sysConfig;
	
	public String CE_URI;
	public String ADMIN_USER;
	public String ADMIN_PASSWORD;
	public String STANZA;
	public String OS_NAME;
	public String SMTP_HOST_NAME;
	public String SMTP_PORT;
	public String SMTP_AUTH_USER;
	public String SMTP_AUTH_PWD;
	public String FROM_ADDRESS;
	
	private FilenetSystemConfiguration()
	{
		try {
			loadProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static FilenetSystemConfiguration getInstance()
	{
		if (sysConfig == null)
		{
			sysConfig = new FilenetSystemConfiguration();
		}
		
		return sysConfig;
	}
	
	private void loadProperties() throws Exception
	{
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("DIMS.properties");
		Properties props = new Properties();
		try {
			props.load(is);
			CE_URI = props.getProperty("CE_URI");
			ADMIN_USER	= props.getProperty( "ADMIN_USER" ) ;
			ADMIN_PASSWORD	= props.getProperty( "ADMIN_PASSWORD" ) ;
			STANZA	= props.getProperty( "STANZA" ) ;
			OS_NAME	= props.getProperty( "OS_NAME" ) ;
			SMTP_HOST_NAME = props.getProperty( "SMTP_HOST_NAME" ) ;
			SMTP_PORT = props.getProperty( "SMTP_PORT" ) ;
			SMTP_AUTH_USER = props.getProperty( "SMTP_AUTH_USER" ) ;
			SMTP_AUTH_PWD = props.getProperty( "SMTP_AUTH_PWD" ) ;
			FROM_ADDRESS = props.getProperty( "FROM_ADDRESS" ) ;
			
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
