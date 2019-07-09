package com.knpc.dims.filenet.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.eclipse.hyades.logging.core.Guid;

import com.filenet.api.constants.AccessRight;


public class Utils {
	
	static final Logger logger = Logger.getLogger(Utils.class);

	public static String formatDate(Date date){
	
	Format formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	
	//Date dateCreated = (Date)date;
	String fomattedDate = formatter.format(date);
	return fomattedDate;
	}
	public static String formatLongForQuery(String intstring)
	{
		try {
			long lValue = Long.parseLong(intstring);
			return Long.toString(lValue);
		} catch (Exception e)
		{
		}
		return "0";
	}
	public static String formatDateForUI(Date date)
	{
		if(date == null)
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String formattedDate = sdf.format(date);
		return formattedDate;
	}
	
	public static String formatDateForUIUpdated(Date date)
	{
		if(date == null)
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String formattedDate = sdf.format(date);
		return formattedDate;
	}
	
	public static String formatDateForQuery(String inputDate)
	{
		if(inputDate == null)
			return "";
		String outDate = "";
		String [] inputDates = inputDate.split("~!");
		for(String date: inputDates) {
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
			//newDate.toString().trim().concat("T040000Z");
		//T214614Z
			//T183000Z
			String fnDate = newDate.toString().trim().concat("T210000Z");
			if(outDate.length() > 0)
				outDate += "~!";
			outDate += fnDate;
		}
		return outDate;
	}
	
	public static String convertStreamToString(InputStream is)	throws IOException {


		if (is != null) {

			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(
				new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}

			} finally {
				is.close();
			}

			return writer.toString();
		} else {
			return "";
		}
	}
	public static List<String> setMultipleValues(String input){
		
		StringTokenizer st =new StringTokenizer(input,";");
		List<String> list = new ArrayList<String>();
		while(st.hasMoreTokens()){
		String value = st.nextToken();
			list.add(value);
			
		}
		return list;
		
	}
	public static int getAccessRightsIntValue(List<String> accessRights){
		
		List<Integer> summation = new ArrayList<Integer>();
		for (int i = 0; i < accessRights.size(); i++) {
			
			if(accessRights.get(i).equalsIgnoreCase("DELETE")){
				
				int value = 65536;
				summation.add(new Integer(value));
				
			}
			else if(accessRights.get(i).equalsIgnoreCase("LINK")){
				int value = 16;
				summation.add(new Integer(value));
				
			}
			else if(accessRights.get(i).equalsIgnoreCase("UNLINK")){
				int value = 32;
				summation.add(new Integer(value));
				
			}
			else if(accessRights.get(i).equalsIgnoreCase("WRITE_ACL")){
				
				int value = AccessRight.WRITE_ACL_AS_INT;
				summation.add(new Integer(value));
			}
			else if(accessRights.get(i).equalsIgnoreCase("VIEW_CONTENT")){
				
				int value = 128;
				summation.add(new Integer(value));
			}
			else if(accessRights.get(i).equalsIgnoreCase("READ")){
	
				int value = 1;
					summation.add(new Integer(value));
			}
			else if(accessRights.get(i).equalsIgnoreCase("READ_ACL")){
	
				int value = 131072;
				summation.add(new Integer(value));
			}
			else if(accessRights.get(i).equalsIgnoreCase("MAJOR_VERSION")){
				
				int value = 4;
				summation.add(new Integer(value));
			}
			else if(accessRights.get(i).equalsIgnoreCase("CREATE_INSTANCE")){
				
				int value = 256;
				summation.add(new Integer(value));
			}
			else if(accessRights.get(i).equalsIgnoreCase("NONE")){
				
				int value = 0;
				summation.add(new Integer(value));
			}
			else if(accessRights.get(i).equalsIgnoreCase("WRITE_OWNER")){
				
				int value = 524288;
				summation.add(new Integer(value));
			}
			else if(accessRights.get(i).equalsIgnoreCase("WRITE")){
				
				int value = 2;
				summation.add(new Integer(value));
			}
			else if(accessRights.get(i).equalsIgnoreCase("CREATE_CHILD")){
				
				int value = 512;
				summation.add(new Integer(value));
			}
			
		}
		
		
		return add(summation);
		}
		
	private static int add(List<Integer> summation){
		int sum = 0;
		for (int i = 0; i < summation.size(); i++) {
			
			sum = summation.get(i)+sum;
			//logger.info("summmmmmm"+sum);
		}
		return sum;
		
	}
	public static List<String> getExcludeDocClassList(String docClassString)
	{
		List<String> docClassList = new ArrayList<String>();
		docClassList = setMultipleValues(docClassString);
		return docClassList;
	}
	
	public static String getFavoritesQuery(List<String> vsList)
	{
		StringBuilder favQueryBuilder = new StringBuilder();
		String favQuery = null;
		int counter = 0;
		for (int i = 0; i < vsList.size(); i++) {
			
			favQueryBuilder = favQueryBuilder.append("[VersionSeries] = Object('" +vsList.get(i) + "')" );
			
			if(counter < vsList.size()-1 )
				{
				favQueryBuilder.append(" OR ");
				}
			counter++;	
		}
		favQuery = " ( " + favQueryBuilder.toString() + " ) " ;
		return favQuery;
	}
	
	/*private static String getWeekDates() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		String sun = formatDateForUI(c.getTime());
		c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		String sat = formatDateForUI(c.getTime());
		System.out.println("Week   :"+sun + "~!" + sat);
		return sun + "~!" + sat;
	}*/
	
	private static String getWeekDates() {
		Calendar c = Calendar.getInstance();
		String currentDate = formatDateForUI(c.getTime());
		c.add(Calendar.DATE, -6);
		Calendar c1 = Calendar.getInstance();
		String lastWeek = formatDateForUI(c.getTime());
		//System.out.println("currentDate   :"+currentDate+"   LastWeek   :"+lastWeek);
		return lastWeek + "~!" + currentDate;
	}
	
	/*private static String getMonthDates() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, 1);
		String first = formatDateForUI(c.getTime());
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH)); 
		String last = formatDateForUI(c.getTime());
		System.out.println("Month   :"+first + "~!" + last);
		return first + "~!" + last;
	}*/
	private static String getMonthDates() {
		Calendar c = Calendar.getInstance();
		String currentDate = formatDateForUI(c.getTime());
		c.add(Calendar.DATE, -30);
		Calendar c1 = Calendar.getInstance();
		String lastMonth = formatDateForUI(c.getTime());
		//System.out.println("currentDate   :"+currentDate+"   lastMonth  :"+lastMonth);
		return lastMonth + "~!" + currentDate;
	}
	
	private static String getTodayDates() {
		Calendar c = Calendar.getInstance();
		String first = formatDateForUI(c.getTime());
		//c.add(Calendar.DATE,  1);
		//String last = formatDateForUI(c.getTime());
		System.out.println("Today   :"+first + "~!" + first);
		return first + "~!" + first;
	}
	
	private static String convertPeriodToDates(String inPeriod) {
		if(inPeriod == null)
			return "";
		if(inPeriod.equalsIgnoreCase("TODAY"))
			return getTodayDates();
		else if(inPeriod.equalsIgnoreCase("THIS WEEK"))
			return getWeekDates();
		else if(inPeriod.equalsIgnoreCase("THIS MONTH"))
			return getMonthDates();
		
		return inPeriod;
	}
	
	public static String convertToUTC(String inputDates, String filterCondition) throws ParseException
	{
		String filter = filterCondition;
		String outDate = "";
		if((filter == null) || (filter.length() <= 0))
			filter = "=";
		filter = filter.trim();
		String [] splitDates = convertPeriodToDates(inputDates).split("~!");
		if(filter.equalsIgnoreCase("period"))
			filter = "between";
		int nCount = 0;
		for(String presentDate:splitDates) {
			String date = presentDate;
			if (presentDate != null && !presentDate.equalsIgnoreCase("")) {
				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				Calendar c = Calendar.getInstance();
				c.setTime(df.parse(date));
				if(filter.equalsIgnoreCase("=") || filter.equalsIgnoreCase(">=")
						|| filter.equalsIgnoreCase("<") || (filter.equalsIgnoreCase("between") && (nCount <= 0)))
					c.add(Calendar.DATE, -1); // how many days you want to add like here 1
					
				String presentUTCDate = df.format(c.getTime());
				if(outDate.length() > 0)
					outDate += "~!";
				outDate += presentUTCDate;
				nCount++;
			}
		}
		return outDate;
	}
	
	public static String addingDate(String presentDate) throws ParseException
	{
		 
		 String date = presentDate; 
		 SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		 Calendar c = Calendar.getInstance();
		 c.setTime(df.parse(date));
		 //c.add(Calendar.DATE, 1);  // how many days you want to add like here 1
		 String addeddate = df.format(c.getTime()); 
		 
		 
		 return addeddate;
	}
	public static String getMimeType(String fileName){
		  
		  String mimeType =null;
		  StringTokenizer stringTokenizer = new  StringTokenizer(fileName,".");
		  while ( stringTokenizer.hasMoreElements() ) {
			  mimeType = stringTokenizer.nextToken();
			  logger.info("mimetype :"+mimeType);	 
		  }
		  
		  
			if (mimeType.equalsIgnoreCase("pdf")) {
				mimeType = "application/pdf";
			} else if (mimeType.equalsIgnoreCase("ogg") ) {
				mimeType = "application/ogg";
			} else if (mimeType.equalsIgnoreCase("dtd") ) {
				mimeType = "application/xml-dtd";
			} else if (mimeType.equalsIgnoreCase("xhtml") ) {
				mimeType = "application/xhtml+xml";
			} else if (mimeType.equalsIgnoreCase("gzip") ) {
				mimeType = "application/x-gzip";
			} else if (mimeType.equalsIgnoreCase("zip") ) {
				mimeType = "application/zip";
			} else if (mimeType.equalsIgnoreCase("doc")	|| mimeType.equalsIgnoreCase("docx")) {
				mimeType = "application/msword";
			} else if (mimeType.equalsIgnoreCase("xls") || mimeType.equalsIgnoreCase("xlsx")) {
				mimeType = "application/vnd.ms-excel";
			} else if (mimeType.equalsIgnoreCase("bmp")) {
				mimeType = "image/bmp";
			} else if (mimeType.equalsIgnoreCase("ico")) {
				mimeType = "image/vnd.microsoft.icon";
			} else if (mimeType.equalsIgnoreCase("tiff")) {
				mimeType = "image/tiff";
			} else if (mimeType.equalsIgnoreCase("gif")) {
				mimeType = "image/gif";
			} else if (mimeType.equalsIgnoreCase("tif") || mimeType.equalsIgnoreCase("tiff")) {
				mimeType = "image/tiff";
			} else if (mimeType.equalsIgnoreCase("jpg") || mimeType.equalsIgnoreCase("jpe") || mimeType.equalsIgnoreCase("jpeg")) {
				mimeType = "image/jpeg";
			} else if (mimeType.equalsIgnoreCase("png")) {
				mimeType = "image/png";
			} else if (mimeType.equalsIgnoreCase("txt")) {
				mimeType = "text/plain";
			} else if (mimeType.equalsIgnoreCase("xml")) {
				mimeType = "text/xml";
			} else if (mimeType.equalsIgnoreCase("js")) {
				mimeType = "text/javascript";
			} else if (mimeType.equalsIgnoreCase("html")) {
				mimeType = "text/html";
			} else if (mimeType.equalsIgnoreCase("css")) {
				mimeType = "text/css";
			} else if (mimeType.equalsIgnoreCase("csv")) {
				mimeType = "text/csv";
			} else if (mimeType.equalsIgnoreCase("ai")) {
				mimeType = "application/ai";
			} else if (mimeType.equalsIgnoreCase("as")) {
				mimeType = "application/as";
			} else if (mimeType.equalsIgnoreCase("avi")) {
				mimeType = "application/avi";
			} else if (mimeType.equalsIgnoreCase("emf")) {
				mimeType = "application/emf";
			} else if (mimeType.equalsIgnoreCase("eps")) {
				mimeType = "application/eps";
			} else if (mimeType.equalsIgnoreCase("exe")) {
				mimeType = "application/exe";
			} else if (mimeType.equalsIgnoreCase("fla")) {
				mimeType = "application/fla";
			} else if (mimeType.equalsIgnoreCase("fon")) {
				mimeType = "application/fon";
			} else if (mimeType.equalsIgnoreCase("pot")) {
				mimeType = "application/pot";
			} else if (mimeType.equalsIgnoreCase("psd")) {
				mimeType = "application/psd";
			} else if (mimeType.equalsIgnoreCase("ttf")) {
				mimeType = "application/ttf";
			} else if (mimeType.equalsIgnoreCase("wav")) {
				mimeType = "application/wav";
			} else if (mimeType.equalsIgnoreCase("wmf")) {
				mimeType = "application/wmf";
			} else if (mimeType.equalsIgnoreCase("ppt") || mimeType.equalsIgnoreCase("pptx") ) {
				mimeType = "application/ppt";
			} else if (mimeType.equalsIgnoreCase("xlsx")) {
				mimeType = "application/xlsx";
			} else if (mimeType.equalsIgnoreCase("bin") || mimeType.equalsIgnoreCase("class") || mimeType.equalsIgnoreCase("dll")) {
				mimeType = "application/octet-stream";
			} else {
				mimeType = "application/octet-stream";
			}

			return mimeType;
		  
	  }
	
	public static String getFileNetSystemPropertyName(String prop) {
		String filenetName = prop;
		if (prop.equals("majorVersion"))
			return "MajorVersionNumber";
		else if (prop.equals("dateCreated"))
			return "DateCreated";
		else if (prop.equals("createdBy"))
			return "Creator";
		else
		return filenetName;
	}

	public static Boolean isIntegratedLogin() {
		java.util.Properties p = new java.util.Properties();
		try {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("DIMS.properties");
			p.load(is);
			String integrated = p.getProperty("LOGIN_MODE");
			if((integrated != null) && (integrated.trim().equalsIgnoreCase("STANDALONE")))
				return false;
		} catch (Exception e) {
		}
		return true;
	}
	
	public static ArrayList<String> getOutlookFolders() {
		ArrayList<String> configList = new ArrayList<String>();
		java.util.Properties p = new java.util.Properties();
		try {
			
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("DIMS.properties");
			p.load(is);
			String subFolderName = Guid.generate().toString() + '\\';
			String serverPath = createSubFolder(p.getProperty("ATTACH_PATH"), subFolderName);
			String clientPath = (appendChar(p.getProperty("ATTACH_CLIENT_PATH"), '\\') + subFolderName).replace('/', '\\');
					
			if((clientPath == null) || (clientPath.trim().length() <= 0))
				clientPath = serverPath;
			configList.add(serverPath);
			configList.add(clientPath);
		} catch (Exception e) {
			System.out.println("Error reading Outlook properties: " + e.getMessage());
		}
		return configList;
	}
	
	private static String createFolder(String folderPath) {
		try {
			File theDir = new File(folderPath);	
			// if the directory does not exist, create it
			if (!theDir.exists()) {
				if(!theDir.mkdir())
					throw new Exception("Can't create directory");
			} 
			return appendChar(folderPath, '\\');
		} catch(Exception e) {
			return "\\";
		}
	}
	private static String createSubFolder(String inFolder, String subFolderName) {
		return(createFolder(appendChar((inFolder.replace('/', '\\')), '\\') + subFolderName));
	}
	
	public static String appendChar(String inString, char ch)
    {
		if(inString == null)
			inString = "C:\\Temp";
        String outString = "C:\\Temp" + ch;
        if (inString.length() > 0)
        {
            outString = inString;
            if (inString.charAt(inString.length() - 1) != ch)
                outString += ch;
        }
        return outString;
    }
	
	public static int convertStringToInt(String intString, int nDefault) {
		try {
			return Integer.parseInt(intString);
		} catch (Exception e) {}
		return nDefault;
	}
}
