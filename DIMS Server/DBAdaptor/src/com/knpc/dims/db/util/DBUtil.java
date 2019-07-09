package com.knpc.dims.db.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.UUID;

import org.apache.log4j.Logger;

public class DBUtil {
	private static final Logger logger = Logger.getLogger(DBUtil.class);
	
	public static Connection getConnection(String bduri, String userName,String password,String driverDetails) throws Exception{
		logger.info("Driver Details::"+driverDetails+" DB URL ::"+bduri+"  userName :"+userName);
		Connection conn=null;
		try {
			
			Class.forName(driverDetails);
			conn = DriverManager.getConnection(bduri,userName,password);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}	
		logger.info("getConnection method exit");
		return conn;
	}
	
	public static Statement getStatement(Connection conn) throws Exception{
		logger.info("getStatement method start");
		Statement st = null;
		try {
			st = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}	
		logger.info("getStatement method exit");
		return st;
	}
	
	public static String getGUID()
	{
		String guid = null;
		UUID uid = UUID.randomUUID();
		guid = uid.toString();
		return guid;
	}

	public static PreparedStatement getPreparedStatement(Connection conn, String sqlQuery) {
		PreparedStatement pstm = null;
		try {
			pstm = conn.prepareStatement(sqlQuery);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pstm;
	}
	
	public static String formatDateForQuery(String date)
	{
		StringTokenizer st =new StringTokenizer(date,"/");
		StringBuilder newDate = new StringBuilder();
		int i = 0;
		String selectedDate = null;
		String selectedMonth = null;
		String selectedYear = null;
		while(st.hasMoreTokens()){
		String value = st.nextToken();
		
		if(i ==0){
			selectedDate = value;
		}
		if(i ==1){
			 selectedMonth = value;
		}
		if(i ==2){
			 selectedYear = value;
		}
			i++;
		}
		newDate.append(selectedYear.trim()).append(selectedMonth.trim()).append(selectedDate.trim());
		return newDate.toString().trim().concat("T210000Z");
	}
	
	public static String convertDateToString(Date date){
		DateFormat df = new SimpleDateFormat("EEE d MMM yyyy hh:mm:ss a");
		String reportDate = df.format(date);
		return reportDate;
	}
	
	public static Timestamp convertStringtoDate(String stringDate) throws Exception{
		try {
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
			Date date = format.parse(stringDate);
			
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			java.sql.Timestamp timestamp = new java.sql.Timestamp(c.getTimeInMillis());
			return timestamp;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		
	}
	
	public static Timestamp convertStringtoDateAndAddOndeDay(String stringDate) throws Exception{
		try {
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
			Date date = format.parse(stringDate);
			
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DATE, 1);
			java.sql.Timestamp timestamp = new java.sql.Timestamp(c.getTimeInMillis());
			return timestamp;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		
	}
	

	public static Timestamp convertStringtoDateWF(String stringDate) throws Exception{
		java.sql.Timestamp timestamp = null;
		try {
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
			//Today
			if(stringDate.split("~")[0].equalsIgnoreCase("today")) {
				stringDate = format.format(new Date());
				Date date = format.parse(stringDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				timestamp = new java.sql.Timestamp(c.getTimeInMillis());
				System.out.println("First Date  :"+timestamp);
			} 
			//Specific Date~07/11/2017
			else if(stringDate.split("~")[0].equalsIgnoreCase("specific")) {
				Date date = format.parse(stringDate.split("~")[1]);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				timestamp = new java.sql.Timestamp(c.getTimeInMillis());
				System.out.println("First Date  :"+timestamp);
			}
			//Last Week
			else if(stringDate.split("~")[0].equalsIgnoreCase("lastWeek")) {
				stringDate = format.format(new Date());
				Date date = format.parse(stringDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				timestamp = new java.sql.Timestamp(c.getTimeInMillis());
				System.out.println("First Date  :"+timestamp);
			}
			//Last Month
			else if(stringDate.split("~")[0].equalsIgnoreCase("lastMonth")) {
				stringDate = format.format(new Date());
				Date date = format.parse(stringDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				timestamp = new java.sql.Timestamp(c.getTimeInMillis());
				System.out.println("First Date  :"+timestamp);
			}
			//Last Year
			else if(stringDate.split("~")[0].equalsIgnoreCase("lastYear")) {
				stringDate = format.format(new Date());
				Date date = format.parse(stringDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				timestamp = new java.sql.Timestamp(c.getTimeInMillis());
				System.out.println("First Date  :"+timestamp);
			}
			//All Dates Before~31/10/2017
			else if(stringDate.split("~")[0].equalsIgnoreCase("before")) {
				Date date = format.parse(stringDate.split("~")[1]);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				timestamp = new java.sql.Timestamp(c.getTimeInMillis());
				System.out.println("First Date  :"+timestamp);
			}
			//All Dates After~31/10/2017
			else if(stringDate.split("~")[0].equalsIgnoreCase("after")) {
				Date date = format.parse(stringDate.split("~")[1]);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				c.add(Calendar.DATE, 1);
				timestamp = new java.sql.Timestamp(c.getTimeInMillis());
				System.out.println("First Date  :"+timestamp);
			}
			//range
			else if(stringDate.split("~")[0].equalsIgnoreCase("range")){
				Date date = format.parse(stringDate.split("~")[1]);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				timestamp = new java.sql.Timestamp(c.getTimeInMillis());
				System.out.println("First Date  :"+timestamp);
			}


			return timestamp;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

	}
	

	public static Timestamp convertStringtoDateRange(String stringDate) throws Exception {
		java.sql.Timestamp timestamp = null;
		try {
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
			Date date = format.parse(stringDate.split("~")[2]);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DATE, 1);
			timestamp = new java.sql.Timestamp(c.getTimeInMillis());
			System.out.println("First Date  :"+timestamp);
			return timestamp;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

	}

	public static Timestamp convertStringtoSpecificDateWF(String stringDate) throws Exception{
		java.sql.Timestamp timestamp = null;
		try {
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
			if(stringDate.split("~")[0].equalsIgnoreCase("today")) {
				stringDate = format.format(new Date());
				Date date = format.parse(stringDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				c.add(Calendar.DATE, 1);
				timestamp = new java.sql.Timestamp(c.getTimeInMillis());
				System.out.println("Next Date   :"+timestamp);
			}
			else if(stringDate.split("~")[0].equalsIgnoreCase("specific")) {
				Date date = format.parse(stringDate.split("~")[1]);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				c.add(Calendar.DATE, 1);
				timestamp = new java.sql.Timestamp(c.getTimeInMillis());
				System.out.println("Next Date   :"+timestamp);
			}
			else if(stringDate.split("~")[0].equalsIgnoreCase("lastWeek")) {
				stringDate = format.format(new Date());
				Date date = format.parse(stringDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				c.add(Calendar.DATE, -7);
				timestamp = new java.sql.Timestamp(c.getTimeInMillis());
				System.out.println("Next Date   :"+timestamp);
			}
			else if(stringDate.split("~")[0].equalsIgnoreCase("lastMonth")) {
				stringDate = format.format(new Date());
				Date date = format.parse(stringDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				c.add(Calendar.DATE, -30);
				timestamp = new java.sql.Timestamp(c.getTimeInMillis());
				System.out.println("Next Date   :"+timestamp);
			}
			else if(stringDate.split("~")[0].equalsIgnoreCase("lastYear")) {
				stringDate = format.format(new Date());
				Date date = format.parse(stringDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				c.add(Calendar.DATE, -365);
				timestamp = new java.sql.Timestamp(c.getTimeInMillis());
				System.out.println("Next Date   :"+timestamp);
			}
			else if(stringDate.split("~")[0].equalsIgnoreCase("after")) {
				stringDate = format.format(new Date());
				Date date = format.parse(stringDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				c.add(Calendar.DATE, 1);
				timestamp = new java.sql.Timestamp(c.getTimeInMillis());
				System.out.println("Next Date   :"+timestamp);
			}

			return timestamp;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

	}
	
	public static String validateDateInput(String stringDate)
	{
		try {
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
			Date date = format.parse(stringDate);
			return formatDateForUI(date);
		} catch (Exception e) {
			return null;
		}
		
	}
	
	public static String formatDateForUI(Date date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm aaa");
		String formattedDate = sdf.format(date);
		return formattedDate;
	}
	
	public static Timestamp getCurrentTimestamp() throws Exception{
		try {
			Calendar c1 = Calendar.getInstance();
			c1.setTime(new java.util.Date()); // Now use today date.
			c1.add(Calendar.DATE, 0); 
			java.sql.Timestamp timestamp = new java.sql.Timestamp(c1.getTimeInMillis());
			return timestamp;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static Timestamp addDaystoDate(int noOfDays){
		Calendar c = Calendar.getInstance();
		c.setTime(new java.util.Date()); // Now use today date.
		c.add(Calendar.DATE, noOfDays); // Adding noOfDays
		java.sql.Timestamp timestamp = new java.sql.Timestamp(c.getTimeInMillis());
		return timestamp;
	}
	
	public static Timestamp minusOneDay(String dateString) throws Exception{
		DateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date date = sdf1.parse(dateString);
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		c1.add(Calendar.DATE, -1);
		java.sql.Timestamp timestamp = new java.sql.Timestamp(c1.getTimeInMillis());
		return timestamp;
	}
	public static Timestamp addOneDay(String dateString) throws Exception {
		DateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date date = sdf1.parse(dateString);
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		c1.add(Calendar.DATE, 1);
		java.sql.Timestamp timestamp = new java.sql.Timestamp(c1.getTimeInMillis());
		return timestamp;
	}
	public static String getOverdue(Date workitemOverdueDate) {

		logger.debug("Entering getOverdue method");
		String isOverdue="false";
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd/MM/yyyy hh:mm aaa");
		try {
			Date date = new Date();
			/*Date todayDate = simpleDateFormat.parse(simpleDateFormat.format(date));
			Date workItmDueDate = simpleDateFormat.parse(simpleDateFormat.format(workitemOverdueDate));
			
			if (todayDate.compareTo(workItmDueDate) > 0) {
				isOverdue="true";
	        }*/
			
			if (date.compareTo(workitemOverdueDate) > 0) {
				isOverdue="true";
	        }
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
		logger.debug("Exit from getOverdue");
		return isOverdue;
	
	}
	
	public static Integer convertStringToInt(String strInt) {
		try {
			return Integer.parseInt(strInt);
		} catch (Exception e) {}
		return null;
	}
}
