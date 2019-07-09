package com.knpc.dims.db;

import java.net.URLDecoder;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.knpc.dims.db.util.DBConfiguration;
import com.knpc.dims.db.util.DBUtil;
import com.knpc.dims.employee.beans.DelegateBean;
import com.knpc.dims.employee.beans.Employee;
import com.knpc.dims.employee.beans.EmployeeDepartment;
import com.knpc.dims.employee.beans.EmployeeDirectorate;
import com.knpc.dims.employee.beans.EmployeeDivision;
import com.knpc.dims.employee.beans.UserList;
import com.knpc.dims.filenet.beans.DocumentType;
import com.knpc.dims.user.preference.ColumnPreference;
import com.knpc.dims.workflow.beans.DIMSSite;
import com.knpc.dims.workflow.beans.DivisionWF;
import com.knpc.dims.workflow.beans.PendingWorkItemDetails;
import com.knpc.dims.workflow.beans.WorkFlowDetails;
import com.knpc.dims.workflow.beans.WorkFlowSearchBean;
import com.knpc.dims.workflow.beans.WorkItemDetails;
import com.knpc.dims.workflow.beans.WorkItemHistory;
import com.knpc.dims.workflow.beans.WorkflowAttachments;
import com.knpc.dims.workflow.beans.WorkflowRecipient;

public class DBAdaptor {
	private static final Logger logger = Logger.getLogger(DBAdaptor.class);
	
	private String escapeString(String inString)
	{
		if(inString == null)
			return null;
		return inString.replaceAll("'", "''");
	}
	
	private String escapeIntString(String inString)
	{
		if(inString == null)
			return "0";
		int nReturn = 0;
		try {
			nReturn = Integer.parseInt(inString);
		} catch (Exception e){
		}
		return Integer.toString(nReturn);
	}
	
	private String escapeIntString(String inString, int defValue)
	{
		int nReturn = defValue;
		if(inString != null)
		{
			try {
				nReturn = Integer.parseInt(inString);
			} catch (Exception e){
			}
		}
		return Integer.toString(nReturn);
	}
	
	private int getIntValue(String inString)
	{
		int nReturn = 0;
		try {
			nReturn = Integer.parseInt(inString);
		} catch (Exception e){
		}
		return nReturn;
	}
	public Employee getUserDetails(String user_login) throws Exception {
		 
		Employee emp= new Employee();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery= null;
		try {
			
			logger.info("Started Method : getUserDetails  Method parameter user_login:"+user_login);
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery ="SELECT ECM_EMPLOYEE.ECM_USER_LOGIN,ECM_EMPLOYEE.ECM_USER_NAME,ECM_EMPLOYEE.ECM_SUPERVISOR_LOGIN,"
						+"ECM_EMPLOYEE.ECM_DEPT_CODE,ECM_EMPLOYEE.ECM_DEPARTMENT,ECM_EMPLOYEE.ECM_DIVISION_CODE,"
						+"ECM_EMPLOYEE.ECM_DIVISION,ECM_EMPLOYEE.ECM_DIR_CODE,ECM_EMPLOYEE.ECM_DIRECTORATE,"
						+"ECM_EMPLOYEE.ECM_JOB_TITLE,ECM_EMPLOYEE.ISADMIN,ECM_EMPLOYEE.ECM_USER_EMAIL,ECM_EMPLOYEE.ECM_ISHAVEINBOX,ECM_EMPLOYEE.ECM_ISHAVEREPORTS FROM ECM_EMPLOYEE WHERE ECM_EMPLOYEE.ECM_USER_LOGIN= ?";
			stmt = conn.prepareStatement(sqlQuery);
			stmt.setString(1, escapeString(user_login));
			rs = stmt.executeQuery();

			while (rs.next()) {

				emp.setEmployeeLogin(rs.getString("ECM_USER_LOGIN").trim());
				emp.setEmployeeName(rs.getString("ECM_USER_NAME").trim());
				emp.setEmployeeSupervisor(rs.getString("ECM_SUPERVISOR_LOGIN").trim());
				
				EmployeeDepartment empDept= new EmployeeDepartment();
				empDept.setDepartment(rs.getString("ECM_DEPARTMENT").trim());
				empDept.setDepartmentCode(rs.getInt("ECM_DEPT_CODE"));
				
				emp.setEmployeeDepartment(empDept);
				
				EmployeeDivision empDiv = new EmployeeDivision();
				empDiv.setEmpDivision(rs.getString("ECM_DIVISION").trim());
				empDiv.setEmpDivisionCode(rs.getInt("ECM_DIVISION_CODE"));
				
				emp.setEmployeeDivision(empDiv);
				
				EmployeeDirectorate empDir = new EmployeeDirectorate();
				empDir.setEmployeeDirectorateCode(rs.getString("ECM_DIR_CODE"));
				empDir.setEmployeeDirectorate(rs.getString("ECM_DIRECTORATE").trim());
				
				emp.setEmployeeDirectorate(empDir);
				String jobTitle = rs.getString("ECM_JOB_TITLE");
				if(jobTitle != null) // SK 23/7
					jobTitle = jobTitle.trim();
				else
					jobTitle = "ENGR"; // SK 23/7
				emp.setEmployeeJobTitle(jobTitle);
				emp.setAdmin(rs.getBoolean("ISADMIN"));
				emp.setEmployeeEmail(rs.getString("ECM_USER_EMAIL"));
				emp.setIsHaveInbox(rs.getInt("ECM_ISHAVEINBOX"));
				emp.setIsHaveReports(rs.getInt("ECM_ISHAVEREPORTS"));
				
			}

		} catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
			if(rs != null){
				rs.close();
			}
			if(stmt != null){
				stmt.close();			
			}
			if(conn != null){
				conn.close();
			}
		 }
		logger.info("Exit Method : getUserDetails");
		return emp;
	}

	public ArrayList<Employee> getDivisionUsers(String division_code, String user_login, String searchCrtieria) throws Exception {
		logger.info("Started Method : getDivisionUsers  Method parameter division_code:"+division_code);
		ArrayList<Employee> empList = new ArrayList<Employee>();
		Connection conn = null;
		 ResultSet rs = null;
		 CallableStatement callableStatement =null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
			
			String getuserlistSql = "{call get_user_dept_div_list(?,?,?)}";

			callableStatement = conn.prepareCall(getuserlistSql);
			callableStatement.setInt(1, getIntValue(division_code));
			callableStatement.setInt(2, 0);
			if(searchCrtieria.equalsIgnoreCase("undefined")){
				callableStatement.setString(3,null);
			}else{
				callableStatement.setString(3,searchCrtieria);
			}
			
			rs = callableStatement.executeQuery();

			while (rs.next()) {
				Employee emp = new Employee();
				emp.setEmployeeLogin(rs.getString("ECM_USER_LOGIN"));
				emp.setEmployeeName(rs.getString("ECM_USER_NAME"));
				emp.setEmployeeDesignation(rs.getString("ECM_USER_TITLE"));
				emp.setEmployeeEmail(rs.getString("ECM_USER_EMAIL"));
				empList.add(emp);
			}

		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		 	callableStatement.close();
		    conn.close();
		 }
		logger.info("Exit Method : getDivisionUsers ");
		return empList;
	}

	public ArrayList<WorkItemDetails> getInboxItems(String user_login) throws Exception {
		logger.info("Started Method : getInboxItems  Method parameter user_login:"+user_login);
		ArrayList<WorkItemDetails> workItemDetailsList = new ArrayList<WorkItemDetails>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "SELECT A.WFL_WITM_ID,B.WFL_SUBJECT,A.WFL_SENDER,"
					+ "C.ECM_USER_NAME AS SENDER_NAME, A.WFL_WITM_TYPE, "
					+ "A.WFL_STEP_NO, A.WFL_WITM_DEADLINE,  A.WFL_WITM_STATUS, "
					+ "B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_SYS_STATUS "
					+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
					+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN "
					+ "WHERE A.WFL_RECIPIENT = ? AND A.WFL_WITM_STATUS Not in ('Completed' ,'Archive') "
					+ "AND A.WFL_WITM_SYS_STATUS = 'ACTIVE'";
			stmt = conn.prepareStatement(sqlQuery);
			stmt.setString(1, escapeString(user_login));
			rs = stmt.executeQuery();

			while (rs.next()) {
				WorkItemDetails workItemDetails = new WorkItemDetails();
				workItemDetails.setWorkflowWorkItemID(rs.getString("WFL_WITM_ID"));
				workItemDetails.setWorkflowItemSubject(rs.getString("WFL_SUBJECT"));
				workItemDetails.setWorkflowSender(rs.getString("WFL_SENDER"));
				String senderName = rs.getString("SENDER_NAME");
				workItemDetails.setWorkflowSenderName(senderName);
				workItemDetails.setWorkflowWorkItemType(rs.getString("WFL_WITM_TYPE"));
				workItemDetails.setWorkflowStepNo(rs.getInt("WFL_STEP_NO"));
				workItemDetails.setWorkflowItemDeadline(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_DEADLINE")));
				workItemDetails.setWorkflowItemStatus(rs.getString("WFL_WITM_STATUS"));
				workItemDetails.setWorkflowItemPriority(rs.getString("WFL_WITM_PRIORITY"));
				if(workItemDetails.getWorkflowItemPriority() == null)
					workItemDetails.setWorkflowItemPriority(rs.getString("WFL_PRIORITY"));
				workItemDetails.setWorkflowItemReceivedOn(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_RECEIVEDON")));
				workItemDetails.setWorkflowItemSysStatus(rs.getString("WFL_WITM_SYS_STATUS"));
				
				workItemDetailsList.add(workItemDetails);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getInboxItems");
		return workItemDetailsList;
	}

	public ArrayList<WorkItemDetails> getSentItems(String user_login) throws Exception {
		logger.info("Started Method : getSentItems  Method parameter user_login:"+user_login);
		ArrayList<WorkItemDetails> workItemDetailsList = new ArrayList<WorkItemDetails>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);
			
			sqlQuery = "SELECT A.WFL_WITM_ID,B.WFL_SUBJECT,A.WFL_SENDER,"
					+ "C.ECM_USER_NAME AS RECIPIENT_NAME, A.WFL_WITM_TYPE, "
					+ "A.WFL_STEP_NO, A.WFL_WITM_DEADLINE,  A.WFL_WITM_STATUS, "
					+ "B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_SYS_STATUS "
					+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
					+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN "
					+ "WHERE A.WFL_SENDER = ?";
			
			stmt = conn.prepareStatement(sqlQuery);
			stmt.setString(1, escapeString(user_login));
			rs = stmt.executeQuery();

			while (rs.next()) {
				WorkItemDetails workItemDetails = new WorkItemDetails();
				workItemDetails.setWorkflowWorkItemID(rs.getString("WFL_WITM_ID"));
				workItemDetails.setWorkflowItemSubject(rs.getString("WFL_SUBJECT"));
				workItemDetails.setWorkflowSender(rs.getString("WFL_SENDER"));
				//String recipientName = rs.getString("RECIPIENT_NAME");
				//workItemDetails.setWorkflowRecipientName(recipientName);// List of name
				workItemDetails.setWorkflowWorkItemType(rs.getString("WFL_WITM_TYPE"));
				workItemDetails.setWorkflowStepNo(rs.getInt("WFL_STEP_NO"));
				workItemDetails.setWorkflowItemDeadline(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_DEADLINE")));
				workItemDetails.setWorkflowItemStatus(rs.getString("WFL_WITM_STATUS"));
				workItemDetails.setWorkflowItemPriority(rs.getString("WFL_WITM_PRIORITY"));
				if(workItemDetails.getWorkflowItemPriority() == null)
					workItemDetails.setWorkflowItemPriority(rs.getString("WFL_PRIORITY"));
				workItemDetails.setWorkflowItemReceivedOn(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_RECEIVEDON")));
				workItemDetails.setWorkflowItemSysStatus(rs.getString("WFL_WITM_SYS_STATUS"));
				
				workItemDetailsList.add(workItemDetails);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getSentItems");
		return workItemDetailsList;
	
	}

	public ArrayList<WorkItemDetails> getWorkitemSentItems(String witemID) throws Exception {
		logger.info("Started Method : getWorkitemSentItems  Method parameter Workitem ID:"+witemID);
		ArrayList<WorkItemDetails> workItemDetailsList = new ArrayList<WorkItemDetails>();
		Connection conn = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);
			
			sqlQuery = "{call GET_SENTITEM_WORKITEMS(?)}";
			
			stmt = conn.prepareCall(sqlQuery);
			stmt.setString(1, escapeString(witemID));
			rs = stmt.executeQuery();

			while (rs.next()) {
				WorkItemDetails workItemDetails = new WorkItemDetails();
				workItemDetails.setWorkflowWorkItemID(rs.getString("WFL_WITM_ID"));
				workItemDetails.setWorkflowItemSubject(rs.getString("WFL_SUBJECT"));
				workItemDetails.setWorkflowSender(rs.getString("WFL_SENDER"));
				String recipientName = rs.getString("RECIPIENT_NAME");
				workItemDetails.setWorkflowReceivedBy(recipientName);
				workItemDetails.setWorkflowWorkItemType(rs.getString("WFL_WITM_TYPE"));
				workItemDetails.setWorkflowStepNo(rs.getInt("WFL_STEP_NO"));
				workItemDetails.setWorkflowItemDeadline(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_DEADLINE")));
				workItemDetails.setWorkflowItemStatus(rs.getString("WFL_WITM_STATUS"));
				workItemDetails.setWorkflowItemPriority(rs.getString("WFL_WITM_PRIORITY"));
				if(workItemDetails.getWorkflowItemPriority() == null)
					workItemDetails.setWorkflowItemPriority(rs.getString("WFL_PRIORITY"));
				workItemDetails.setWorkflowItemReceivedOn(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_RECEIVEDON")));
				workItemDetails.setWorkflowItemSysStatus(rs.getString("WFL_WITM_SYS_STATUS"));
				
				workItemDetailsList.add(workItemDetails);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getAllSentItems");
		return workItemDetailsList;
	
	}
	
	public WorkItemDetails getWorkItemDetailsNew(String witem_id) throws Exception {
		logger.info("Started Method : getWorkItemDetailsNew  Method parameter witem_id:"+witem_id);
		WorkItemDetails workItemDetails = new WorkItemDetails();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		String actionBy = null;
		String workFlowUUID = null;
		String workFlowItemStatus = null;
		String parentWitm = null;
		PreparedStatement psSt = null;
		//CallableStatement callableStatement = null;
		try {
			
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);
			
			sqlQuery = "SELECT WFL_RECIPIENT,WFL_ID,WFL_WITM_STATUS, WFL_PARENT_WITM FROM DIMS_WORKITEM WHERE WFL_WITM_ID = ?";
			stmt = conn.prepareStatement(sqlQuery);
			stmt.setString(1, escapeString(witem_id));
			rs = stmt.executeQuery();
			while(rs.next()){
				actionBy = rs.getString("WFL_RECIPIENT");
				workFlowUUID = rs.getString("WFL_ID");
				workFlowItemStatus = rs.getString("WFL_WITM_STATUS");
				parentWitm = rs.getString("WFL_PARENT_WITM");
			}
			rs.close();
			stmt.close();
						
			
			sqlQuery = "SELECT A.WFL_WITM_ID,A.WFL_SENDER,A.WFL_ID,B.WFL_SUBJECT,B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, "
					+ "C.ECM_USER_NAME AS SENDER_NAME,A.WFL_WITM_TYPE, "
					+ "A.WFL_STEP_NO, A.WFL_PARENT_WITM,A.WFL_INSTRUCTIONS,"
					+ "A.WFL_WITM_RECEIVEDON,A.WFL_WITM_DEADLINE,A.WFL_WITM_REMINDER_DATE,"
					+ "A.WFL_WITM_ROOT_SENDER,  A.WFL_WITM_STATUS,"
					+ "A.WFL_WITM_ACTION,A.WFL_WITM_ACTION_COMMENT,"
					+ "A.WFL_WITM_ACTION_BY,A.WFL_WITM_ACTION_ONBEHALF,"
					+ "A.WFL_WITM_SENDER_DEPT,A.WFL_WITM_RECEIVER_DEPT,"
					+ "A.WFL_WITM_SENDER_DIV,A.WFL_WITM_RECEIVER_DIV,A.WFL_WITM_SYS_STATUS "
					+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
					+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN "
					+ "WHERE A.WFL_WITM_ID = ?";
			
			stmt = conn.prepareStatement(sqlQuery);
			stmt.setString(1, escapeString(witem_id));
			rs = stmt.executeQuery();
			
			//ArrayList<String> recipientList = new ArrayList<String>();
			
			while (rs.next()) {
				
				workItemDetails.setWorkflowWorkItemID(rs.getString("WFL_WITM_ID"));
				workItemDetails.setWorkflowId(rs.getString("WFL_ID"));
				workItemDetails.setWorkflowItemSubject(rs.getString("WFL_SUBJECT"));
				workItemDetails.setWorkflowItemPriority(rs.getString("WFL_WITM_PRIORITY"));
				if(workItemDetails.getWorkflowItemPriority() == null)
					workItemDetails.setWorkflowItemPriority(rs.getString("WFL_PRIORITY"));
				workItemDetails.setWorkflowSender(rs.getString("WFL_SENDER"));
				String senderName = rs.getString("SENDER_NAME");
				workItemDetails.setWorkflowSenderName(senderName);
				workItemDetails.setWorkflowWorkItemType(rs.getString("WFL_WITM_TYPE"));
				workItemDetails.setWorkflowStepNo(rs.getInt("WFL_STEP_NO"));
				workItemDetails.setWorkflowParentItem(rs.getString("WFL_PARENT_WITM"));
				workItemDetails.setWorkflowInstruction(rs.getString("WFL_INSTRUCTIONS"));
				workItemDetails.setWorkflowItemReceivedOn(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_RECEIVEDON")));
				workItemDetails.setWorkflowItemDeadline(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_DEADLINE")));
				workItemDetails.setWorkflowItemReminderDate(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_REMINDER_DATE")));
				workItemDetails.setWorkflowItemRootSender(rs.getString("WFL_WITM_ROOT_SENDER"));
				workItemDetails.setWorkflowItemStatus(rs.getString("WFL_WITM_STATUS"));
				workItemDetails.setWorkflowItemAction(rs.getString("WFL_WITM_ACTION"));
				String wiComments = rs.getString("WFL_WITM_ACTION");
				if((wiComments == null) || (wiComments.equalsIgnoreCase("undefined")) || (wiComments.trim().length() <= 0))
					wiComments = "There are no comments";
				workItemDetails.setWorkflowItemActionComment(wiComments);
				workItemDetails.setWorkflowItemActionBy(rs.getString("WFL_WITM_ACTION_BY"));
				workItemDetails.setWorkflowItemActionOnBehalf(rs.getString("WFL_WITM_ACTION_ONBEHALF"));
				workItemDetails.setWorkflowItemSenderDepartment(rs.getInt("WFL_WITM_SENDER_DEPT"));
				workItemDetails.setWorkflowItemReceiverDepartment(rs.getInt("WFL_WITM_RECEIVER_DEPT"));
				workItemDetails.setWorkflowItemSenderDiv(rs.getInt("WFL_WITM_SENDER_DIV"));
				workItemDetails.setWorkflowItemReceiverDiv(rs.getInt("WFL_WITM_RECEIVER_DIV"));
				workItemDetails.setWorkflowItemSysStatus(rs.getString("WFL_WITM_SYS_STATUS"));
			}
			rs.close();
			stmt.close();
			
			sqlQuery = "SELECT A.WFL_RECIPIENT,A.WFL_WITM_TYPE,C.ECM_USER_NAME AS RECIPIENT_NAME"
						+" FROM DIMS_WORKITEM A JOIN DIMS_WORKFLOW B ON A.WFL_ID = B.WFL_ID"
						+" JOIN ECM_EMPLOYEE C ON A.WFL_RECIPIENT = C.ECM_USER_LOGIN"
						+" WHERE A.WFL_ID IN (select WFL_ID from DIMS_WORKITEM where WFL_WITM_ID= ? )"
						+ " AND A.WFL_WITM_STATUS <> 'Reassigned'";
			if(parentWitm != null) {
				sqlQuery += " AND A.WFL_PARENT_WITM = ?";
				stmt = conn.prepareStatement(sqlQuery);
				stmt.setString(1, escapeString(witem_id));
				stmt.setString(2, escapeString(parentWitm));
			} else {
				sqlQuery += " AND A.WFL_PARENT_WITM IS NULL";
				stmt = conn.prepareStatement(sqlQuery);
				stmt.setString(1, escapeString(witem_id));
			}
			
			rs = stmt.executeQuery();
			ArrayList<WorkflowRecipient> workflowRecipientList = new ArrayList<WorkflowRecipient>();
			while(rs.next()){
				WorkflowRecipient wfRep = new WorkflowRecipient();
				wfRep.setWorkflowRecipient(rs.getString("WFL_RECIPIENT"));
				String recipientName = rs.getString("RECIPIENT_NAME");
				wfRep.setWorkflowRecipientName(recipientName);
				wfRep.setWorkflowWorkItemType(rs.getString("WFL_WITM_TYPE"));
				workflowRecipientList.add(wfRep);
			}
			workItemDetails.setWorkflowRecipientList(workflowRecipientList);
			
			rs.close();
			stmt.close();
			
			// SUSANTH 17-JULY-2017
			CallableStatement callableStatement = null;
			String getAttachSql = "{call GET_WORKITEM_ATTACHMENTS(?)}";
			callableStatement = conn.prepareCall(getAttachSql);
			callableStatement.setString(1, escapeString(witem_id));
			rs = callableStatement.executeQuery();
			// End SUSANTH 17-JULY-2017
			
			ArrayList<WorkflowAttachments> workflowAttachmentsList = new ArrayList<WorkflowAttachments>();
			while (rs.next()) {
				WorkflowAttachments workflowAttachments = new WorkflowAttachments();
				workflowAttachments.setWorkflowDocumentId(rs.getString("WFL_DOCUMENT_ID"));
				workflowAttachments.setWorkflowAttachmentType(rs.getString("WFL_ATTACHMENT_TYPE"));
				workflowAttachments.setDocumentId(rs.getString("DocumentID"));
				workflowAttachmentsList.add(workflowAttachments);
			}
			workItemDetails.setWorkflowAttachments(workflowAttachmentsList);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			if(rs != null){
				rs.close();
			}
			/*if(callableStatement!=null){
				callableStatement.close();
			}*/
			if(stmt !=null){
				stmt.close();
			}
			if(conn !=null){
				conn.close();
			}
		}
		logger.info("Exit Method : getWorkItemDetailsNew");
		return workItemDetails;
	}

	public void executeWorkItemReadSP(String witmID, String userLogin) throws Exception {
		CallableStatement callableStatement = null;
		Connection conn = null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			
			String getDBUSERSql = "{call APP_DIMS_READ_WORKITEM(?,?)}";
			callableStatement = conn.prepareCall(getDBUSERSql);
			callableStatement.setString(1, escapeString(witmID));
			callableStatement.setString(2, escapeString(userLogin));
			
			callableStatement.executeUpdate();
			
			callableStatement.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if(callableStatement != null)
				callableStatement.close();
			throw new Exception(e.getMessage());
		}finally{
			if(callableStatement != null)
				callableStatement.close();
			conn.close();
		 }
	}
	
	public ArrayList<WorkItemDetails> getInboxItemsFrom(String user_login,String sender_login) throws Exception {
		logger.info("Started Method : getInboxItemsFrom  Method parameter witem_id:"+user_login+" sender_login"+sender_login);
		ArrayList<WorkItemDetails> workItemDetailsList = new ArrayList<WorkItemDetails>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);
	
			sqlQuery = "SELECT A.WFL_WITM_ID,B.WFL_SUBJECT,A.WFL_SENDER,A.WFL_RECIPIENT,"
					+ "C.ECM_USER_NAME AS RECIPIENT_NAME, A.WFL_WITM_TYPE, "
					+ "A.WFL_STEP_NO, A.WFL_WITM_DEADLINE,  A.WFL_WITM_STATUS, "
					+ "B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_SYS_STATUS "
					+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
					+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN "
					+ "WHERE A.WFL_SENDER = ? AND WFL_RECIPIENT= ?";

			stmt = conn.prepareStatement(sqlQuery);
			stmt.setString(1, escapeString(user_login));
			stmt.setString(2, escapeString(sender_login));
			rs = stmt.executeQuery();

			while (rs.next()) {
				WorkItemDetails workItemDetails = new WorkItemDetails();
				workItemDetails.setWorkflowWorkItemID(rs.getString("WFL_WITM_ID"));
				workItemDetails.setWorkflowItemSubject(rs.getString("WFL_SUBJECT"));
				workItemDetails.setWorkflowSender(rs.getString("WFL_SENDER"));
				
				//String recipientName = rs.getString("RECIPIENT_NAME");
				//workItemDetails.setWorkflowRecipientName(recipientName);
				//workItemDetails.setWorkflowRecipient(rs.getString("WFL_RECIPIENT"));//// List of name
				workItemDetails.setWorkflowWorkItemType(rs.getString("WFL_WITM_TYPE"));
				workItemDetails.setWorkflowStepNo(rs.getInt("WFL_STEP_NO"));
				workItemDetails.setWorkflowItemDeadline(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_DEADLINE")));
				workItemDetails.setWorkflowItemStatus(rs.getString("WFL_WITM_STATUS"));
				workItemDetails.setWorkflowItemPriority(rs.getString("WFL_WITM_PRIORITY"));
				if(workItemDetails.getWorkflowItemPriority() == null)
					workItemDetails.setWorkflowItemPriority(rs.getString("WFL_PRIORITY"));
				workItemDetails.setWorkflowItemReceivedOn(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_RECEIVEDON")));
				workItemDetails.setWorkflowItemSysStatus(rs.getString("WFL_WITM_SYS_STATUS"));
				
				workItemDetailsList.add(workItemDetails);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getInboxItemsFrom");
		return workItemDetailsList;
	}

	public ArrayList<Employee> getDepartmentUsers(String dept_code, String searchCrtieria) throws Exception {
		logger.info("Started Method : getDepartmentUsers  Method parameter witem_id:"+dept_code);
		ArrayList<Employee> empList = new ArrayList<Employee>();
		Connection conn = null;
		ResultSet rs = null;
		CallableStatement callableStatement = null;
		try {
			
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
			
			String getuserlistSql = "{call get_user_dept_div_list(?,?,?)}";

			callableStatement = conn.prepareCall(getuserlistSql);
			callableStatement.setInt(1, 0);
			callableStatement.setInt(2, getIntValue(dept_code));
			if(searchCrtieria.equalsIgnoreCase("undefined")){
				callableStatement.setString(3,null);
			}else{
				callableStatement.setString(3,escapeString(searchCrtieria));
			}
			rs = callableStatement.executeQuery();

			while (rs.next()) {
				Employee emp = new Employee();
				emp.setEmployeeLogin(rs.getString("ECM_USER_LOGIN"));
				emp.setEmployeeName(rs.getString("ECM_USER_NAME"));
				emp.setEmployeeDesignation(rs.getString("ECM_USER_TITLE"));
				emp.setEmployeeEmail(rs.getString("ECM_USER_EMAIL"));
				empList.add(emp);
			}

		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		 	callableStatement.close();
		    conn.close();
		 }
		logger.info("Exit Method : getDepartmentUsers");
		return empList;
	}
	
	public ArrayList<Employee> getGlobalDepartmentUsers(String dept_code, String searchCrtieria) throws Exception {
		logger.info("Started Method : getGlobalDepartmentUsers  Method parameter witem_id:"+dept_code);
		ArrayList<Employee> empList = new ArrayList<Employee>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try {
			
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
			
			String getuserlistSql = "SELECT ECM_EMPLOYEE.ECM_USER_LOGIN,ECM_EMPLOYEE.ECM_USER_NAME,ECM_EMPLOYEE.ECM_USER_EMAIL, "
									+" ECM_EMPLOYEE.ECM_DESIGNATION,ECM_EMPLOYEE.ECM_USER_TITLE FROM ECM_EMPLOYEE "
									+" WHERE (ECM_EMPLOYEE.ECM_ACTIVE_USER='yes' OR ECM_EMPLOYEE.ECM_ACTIVE_USER=1) "
									+" AND (ECM_JOB_TITLE = 'TL' OR ECM_JOB_TITLE = 'MGR' OR ECM_JOB_TITLE = 'DCEO') ";
			
			if(dept_code != null) {
				getuserlistSql = getuserlistSql+" AND ECM_EMPLOYEE.ECM_DEPT_CODE in (";
				String deptCodes [] = dept_code.split("~");

				for (int i = 0; i < deptCodes.length; i++) {
					if(deptCodes.length == 1) {
						getuserlistSql = getuserlistSql+"'"+escapeString(deptCodes[i])+"'"+")";
					}
					else {						
						if(i == deptCodes.length-1) {
							getuserlistSql = getuserlistSql+"'"+escapeString(deptCodes[i])+"'"+")";
						}
						else {
							getuserlistSql = getuserlistSql+"'"+escapeString(deptCodes[i])+"'"+", ";
						}
					}
				}
			}
			//added by ravi boni on 12-12-2017
			if(searchCrtieria.equalsIgnoreCase("undefined") || searchCrtieria.equalsIgnoreCase("")) {
				getuserlistSql = getuserlistSql + " order by ECM_EMPLOYEE.ECM_USER_NAME ASC";
				stmt = conn.prepareStatement(getuserlistSql);
				
				System.out.println("Final query   :"+getuserlistSql);
			}
			else {
				getuserlistSql = getuserlistSql + " AND (ECM_USER_NAME LIKE ? OR ECM_USER_LOGIN LIKE ? OR ECM_DESIGNATION LIKE ? OR ECM_USER_TITLE LIKE ?)";
				System.out.println("searchCrtieria   :"+searchCrtieria);
				getuserlistSql = getuserlistSql + " order by ECM_EMPLOYEE.ECM_USER_NAME ASC";
				System.out.println("Final Query   :"+getuserlistSql);
				stmt = conn.prepareStatement(getuserlistSql);	
				stmt.setString(1, "%" + escapeString(searchCrtieria) + "%");
				stmt.setString(2, "%" + escapeString(searchCrtieria) + "%");
				stmt.setString(3, "%" + escapeString(searchCrtieria) + "%");
				stmt.setString(4, "%" + escapeString(searchCrtieria) + "%");
			}	
			//end
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				Employee emp = new Employee();
				emp.setEmployeeLogin(rs.getString("ECM_USER_LOGIN"));
				emp.setEmployeeName(rs.getString("ECM_USER_NAME"));
				emp.setEmployeeDesignation(rs.getString("ECM_USER_TITLE"));
				emp.setEmployeeEmail(rs.getString("ECM_USER_EMAIL"));
				empList.add(emp);
			}

		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
			 rs.close();
			 stmt.close();
			 conn.close();
		 }
		logger.info("Exit Method : getGlobalDepartmentUsers");
		return empList;
	}

	public ArrayList<Employee> getDirectorateUsers(String dir_code) throws Exception {
		logger.info("Started Method : getDirectorateUsers  Method parameter dir_code:"+dir_code);
		ArrayList<Employee> empList = new ArrayList<Employee>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery= null;
		try {
			
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "SELECT ECM_EMPLOYEE.ECM_USER_LOGIN,ECM_EMPLOYEE.ECM_USER_NAME,ECM_EMPLOYEE.ECM_USER_EMAIL,ECM_EMPLOYEE.ECM_DESIGNATION "
						+"FROM ECM_EMPLOYEE" 
							+" WHERE ECM_EMPLOYEE.ECM_DIR_CODE = ? OR ECM_EMPLOYEE.ECM_USER_LOGIN !='N/A'" ;

			stmt = conn.prepareStatement(sqlQuery);
			stmt.setString(1, escapeIntString(dir_code));
			rs = stmt.executeQuery();

			while (rs.next()) {
				Employee emp = new Employee();
				emp.setEmployeeLogin(rs.getString("ECM_USER_LOGIN"));
				emp.setEmployeeName(rs.getString("ECM_USER_NAME"));
				emp.setEmployeeEmail(rs.getString("ECM_USER_EMAIL"));
				emp.setEmployeeDesignation(rs.getString("ECM_DESIGNATION"));
				empList.add(emp);
			}

		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		    stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : getDirectorateUsers");
		return empList;
	}

	public String getSecretaries(String user_login) throws Exception {
		logger.info("Started Method : getSecretaries  Method parameter user_login : "+user_login);
		 Connection conn = null;
		 PreparedStatement stmt = null;
		 ResultSet rs = null;
		 String sqlQuery= null;
		 String asstEmpLogin="";
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "ECM_ASSISTANT_EMPLOYEE.ECM_ASST_EMP_LOGIN "
						+"FROM ECM_ASSISTANT_EMPLOYEE" 
							+"ECM_ASSISTANT_EMPLOYEE.ECM_ASST_EMP_SUPERVISOR = ?";

			stmt = conn.prepareStatement(sqlQuery);
			stmt.setString(1, escapeString(user_login));
			rs = stmt.executeQuery();

			while (rs.next()) {
				asstEmpLogin = rs.getString("ECM_ASST_EMP_LOGIN");
			}

		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		    stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : getSecretaries");
		return asstEmpLogin;
	}

	public String getManagerofSecretary(String user_login) throws Exception {
		logger.info("Started Method : getManagerofSecretary  Method parameter user_login : "+user_login);
		Connection conn = null;
		 PreparedStatement stmt = null;
		 ResultSet rs = null;
		 String sqlQuery= null;
		 String managerSec="";
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "SELECT ECM_ASSISTANT_EMPLOYEE.ECM_ASST_EMP_SUPERVISOR "
						+"FROM ECM_ASSISTANT_EMPLOYEE" 
							+" WHERE ECM_ASSISTANT_EMPLOYEE.ECM_ASST_EMP_LOGIN = ?";

			stmt = conn.prepareStatement(sqlQuery);
			stmt.setString(1, escapeString(user_login));
			rs = stmt.executeQuery();

			while (rs.next()) {
				managerSec = rs.getString("ECM_ASST_EMP_SUPERVISOR");
			}

		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		    stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : getManagerofSecretary");
		return managerSec;
	}

	public boolean addSecretary(String user_login, String supervisor_login) throws Exception {
		/*Connection conn = null;
		 Statement stmt = null;
		 ResultSet rs = null;
		 String sqlQuery= null;
		 int count = 0 ;
		 boolean isInserted = false;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
			stmt = DBUtil.getStatement(conn);

			sqlQuery = "INSERT INTO ECM_ASSISTANT_EMPLOYEE VALUES('','');";

			count = stmt.executeUpdate(sqlQuery);
			if(count>0){
				isInserted = true;
			}

		} catch(Exception e){
		    //Handle errors for Class.forName
		    e.printStackTrace();
		 }finally{
		 	rs.close();
		    stmt.close();
		    conn.close();
		 }
		return isInserted;*/
		return false;
	}

	public ArrayList<EmployeeDepartment> getDepartments(String dir_code) throws Exception {
		logger.info("Started Method : getDepartments  Method parameter dir_code : "+dir_code);
		ArrayList<EmployeeDepartment> empDeptList = new ArrayList<EmployeeDepartment>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery= null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "SELECT ECM_DEPARTMENT.ECM_DEPT_CODE,ECM_DEPARTMENT.ECM_DEPARTMENT FROM ECM_DEPARTMENT WHERE ECM_DEPARTMENT.ECM_DIR_CODE= ?";

			stmt = conn.prepareStatement(sqlQuery);
			stmt.setString(1, escapeIntString(dir_code));
			rs = stmt.executeQuery();

			while (rs.next()) {
				EmployeeDepartment empDept = new EmployeeDepartment();
				empDept.setDepartment(rs.getString("ECM_DEPARTMENT"));
				empDept.setDepartmentCode(rs.getInt("ECM_DEPT_CODE"));
				empDeptList.add(empDept);
			}

		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		    stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : getDepartments");
		return empDeptList;
	}

	public ArrayList<EmployeeDivision> getDivisions(String dept_code) throws Exception {
		logger.info("Started Method : getDivisions  Method parameter dir_code : "+dept_code);
		ArrayList<EmployeeDivision> empDivtList = new ArrayList<EmployeeDivision>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery= null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "SELECT ECM_DIVISION_CODE,ECM_DIVISION FROM ECM_DIVISION WHERE ECM_DEPT_CODE=? ORDER BY ECM_DIVISION DESC";

			stmt = conn.prepareStatement(sqlQuery);
			stmt.setString(1, escapeIntString(dept_code));
			rs = stmt.executeQuery();

			while (rs.next()) {
				EmployeeDivision empDiv = new EmployeeDivision();
				empDiv.setEmpDivision(rs.getString("ECM_DIVISION"));
				empDiv.setEmpDivisionCode(rs.getInt("ECM_DIVISION_CODE"));
				empDivtList.add(empDiv);
			}

		} catch(Exception e){
		    logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		    stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : getDivisions");
		return empDivtList;
	}

	
private String executeCreateWorkflowSP(WorkFlowDetails wf, Connection conn) throws Exception
{
	String returnValue = null;
	CallableStatement callableStatement = null;
	try {
		System.out.println("wf.getWorkflowPriority() :: "+wf.getWorkflowPriority());
		String getDBUSERSql = "{call APP_DIMS_CREATE_WORKFLOW(?,?,?,?,?,?,?,?)}";
		callableStatement = conn.prepareCall(getDBUSERSql);
		callableStatement.setString(1, escapeString(URLDecoder.decode(wf.getWorkflowSubject(),"UTF-8")));
		callableStatement.setString(2, escapeString(wf.getWorkflowDeadline()));
		callableStatement.setString(3, escapeString(wf.getWorkflowReminderDate()));
		callableStatement.setInt(4, wf.getWorkflowPriority());
		callableStatement.setString(5, escapeString(wf.getWorkflowLaunchedBy()));
		callableStatement.setString(6, escapeString(wf.getWorkflowLaunchedOnBehalf()));
		callableStatement.setString(7, escapeString(wf.getWorkflowPrimaryDocument()));
		callableStatement.registerOutParameter(8, java.sql.Types.NVARCHAR);
		
		callableStatement.executeUpdate();

		returnValue = callableStatement.getString(8);
		
		callableStatement.close();
	} catch (Exception e) {
		logger.error(e.getMessage(), e);
		if(callableStatement != null)
			callableStatement.close();
		throw new Exception(e.getMessage());
	}finally{
		if(callableStatement != null)
			callableStatement.close();
	 }
	return returnValue;
}

private void executeAddWorkitemAttachmentSP(String wiID, String docID, Connection conn) throws Exception
{
	CallableStatement callableStatement = null;
	try {
		String getDBUSERSql = "{call APP_DIMS_ADD_ATTACHMENT_WORKITEM(?,?)}";
		callableStatement = conn.prepareCall(getDBUSERSql);
		callableStatement.setString(1, escapeString(wiID));
		callableStatement.setString(2, escapeString(docID));
				
		callableStatement.executeUpdate();
		
		callableStatement.close();
	} catch (Exception e) {
		logger.error(e.getMessage(), e);
		if(callableStatement != null)
			callableStatement.close();
		if(!(e.getMessage().contains("Error number 101 in the THROW statement"))){
			throw new Exception(e.getMessage());			
		}
	}finally{
		if(callableStatement != null)
			callableStatement.close();
	 }
}

private String executeCreateWorkitemSP(String wfID, WorkItemDetails wi, WorkflowRecipient wr, Connection conn) throws Exception
{
	String returnValue = null;
	CallableStatement callableStatement = null;
	try {
		String getDBUSERSql = "{call APP_DIMS_CREATE_WORKITEM(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		callableStatement = conn.prepareCall(getDBUSERSql);
		callableStatement.setString(1, escapeString(wfID));
		callableStatement.setString(2, escapeString(wi.getWorkflowSender()));
		callableStatement.setString(3, escapeString(wr.getWorkflowRecipient()));
		callableStatement.setString(4, escapeString(wr.getWorkflowWorkItemType()));
		callableStatement.setString(5, null);
		callableStatement.setString(6, escapeString(wi.getWorkflowInstruction()));
		callableStatement.setString(7, escapeString(wi.getWorkflowItemDeadline()));
		callableStatement.setString(8, escapeString(wi.getWorkflowItemReminderDate()));
		callableStatement.setString(9, escapeString(wi.getWorkflowItemActionOnBehalf()));
		callableStatement.setString(10, "New");
		callableStatement.setString(11, escapeString(URLDecoder.decode(wi.getWorkflowItemActionComment(),"UTF-8")));
		callableStatement.setString(12, escapeString(wi.getWorkflowItemActionBy()));
		callableStatement.registerOutParameter(13, java.sql.Types.NVARCHAR);
		
		callableStatement.executeUpdate();

		returnValue = callableStatement.getString(13);
		
		callableStatement.close();
	} catch (Exception e) {
		logger.error(e.getMessage(), e);
		if(callableStatement != null)
			callableStatement.close();
		throw new Exception(e.getMessage());
	}finally{
		if(callableStatement != null)
			callableStatement.close();
	 }
	return returnValue;
}

	public ArrayList<WorkItemDetails> filterInboxItems(String user_login,String filterType, String folderFilter, String sortOrder, String sortColumn, String page, String perPage) throws Exception {
		logger.info("Started Method : filterInboxItems  Method parameter user_login : "+user_login+" filterType : "+filterType);
		ArrayList<WorkItemDetails> workItemDetailsList = new ArrayList<WorkItemDetails>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		CallableStatement callableStatement = null;
		// SK Change 23/5
		if(filterType == null)
			filterType = "ALL"; 
		String sortColumnDef = getColumnName(sortColumn);
		// End SK 23/7
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);
			
			String getDBUSERCursorSql = "{call GET_INBOX_WORKITEM_DETAILS(?,?,?,?,?,?,?,?)}";
			callableStatement = conn.prepareCall(getDBUSERCursorSql);
			callableStatement.setString(1, escapeString(user_login));
			callableStatement.setString(2, escapeIntString(page, 1));
			callableStatement.setString(3, escapeIntString(perPage, 15));
			callableStatement.setString(4, "1");
			callableStatement.setString(5, escapeString(filterType)); // Sk 23/7
			callableStatement.setString(6, escapeString(folderFilter));
			if(sortOrder!=null && !sortOrder.equalsIgnoreCase("")){
				if((sortOrder.toUpperCase().trim().equals("ASC")) && (sortOrder.toUpperCase().trim().equals("DESC")))
					sortOrder = "DESC";
				callableStatement.setString(7, escapeString(sortOrder));
			}else{
				if(sortOrder == null){
					callableStatement.setString(7,"DESC");
				}else{
					callableStatement.setString(7,sortOrder);
				}
				
			}
			
			if(sortColumnDef!=null && !sortColumnDef.equalsIgnoreCase("")){ // SK 23/7
				callableStatement.setString(8, escapeString(sortColumnDef)); // SK 23/7
			}else{
				callableStatement.setString(8, "A.WFL_WITM_RECEIVEDON");
			}
			
			rs = callableStatement.executeQuery();
			while (rs.next()) {
				WorkItemDetails workItemDetails = new WorkItemDetails();
				workItemDetails.setWorkflowWorkItemID(rs.getString("WFL_WITM_ID"));
				workItemDetails.setWorkflowItemSubject(rs.getString("WFL_SUBJECT"));
				workItemDetails.setWorkflowItemActionComment(rs.getString("WFL_WITM_ACTION_COMMENT"));
				workItemDetails.setWorkflowSender(rs.getString("WFL_SENDER"));
				String senderName = rs.getString("SENDER_NAME");
				workItemDetails.setWorkflowSenderName(senderName);
				workItemDetails.setWorkflowWorkItemType(rs.getString("WFL_WITM_TYPE"));
				workItemDetails.setWorkflowStepNo(rs.getInt("WFL_STEP_NO"));
				if(rs.getDate("WFL_WITM_DEADLINE") != null)
					workItemDetails.setWorkflowItemDeadline(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_DEADLINE")));
				workItemDetails.setWorkflowItemStatus(rs.getString("WFL_WITM_STATUS"));
				workItemDetails.setWorkflowItemPriority(rs.getString("WFL_WITM_PRIORITY"));
				if(workItemDetails.getWorkflowItemPriority() == null)
					workItemDetails.setWorkflowItemPriority(rs.getString("WFL_PRIORITY"));
				
				if(rs.getDate("WFL_WITM_RECEIVEDON") != null)
					workItemDetails.setWorkflowItemReceivedOn(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_RECEIVEDON")));
				workItemDetails.setWorkflowItemSysStatus(rs.getString("WFL_WITM_SYS_STATUS"));
				workItemDetails.setWorkflowInstruction(rs.getString("WFL_INSTRUCTIONS"));
				workItemDetails.setTotalCount(rs.getString("totalRecordsNumber"));
				workItemDetails.setWorkflowName(rs.getString("WFL_NAME"));
				workItemDetails.setIsOverdue(DBUtil.getOverdue(rs.getTimestamp("WFL_WITM_DEADLINE")));
				workItemDetails.setWorkflowItemRootSender(rs.getString("WFL_WITM_ROOT_SENDER"));
				workItemDetails.setWorkflowItemSenderDepartment(rs.getInt("WFL_WITM_SENDER_DEPT"));
				workItemDetails.setWorkflowItemSenderDiv(rs.getInt("WFL_WITM_SENDER_DIV"));
				workItemDetails.setSenderDepartment(rs.getString("ECM_DEPARTMENT"));
				workItemDetails.setSenderDivision(rs.getString("ECM_DIVISION"));
				workItemDetailsList.add(workItemDetails);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			if(rs != null){
				rs.close();
			}
			if(callableStatement!=null){
				callableStatement.close();
			}
			if(stmt !=null){
				stmt.close();
			}
			if(conn !=null){
				conn.close();
			}
		}
		logger.info("Exit Method : filterInboxItems");
		return workItemDetailsList;
	}

	

	private String getColumnName(String sortColumn) {
		if(sortColumn == null || sortColumn.equalsIgnoreCase(""))
			return "A.WFL_WITM_RECEIVEDON";
		
		String colName = "";
		if(sortColumn.equalsIgnoreCase("Subject")){
			colName = "B.WFL_SUBJECT";
		}else if(sortColumn.equalsIgnoreCase("Sender Name")){
			colName = "C.ECM_USER_NAME";
		}else if(sortColumn.equalsIgnoreCase("Received Date")){
			colName = "A.WFL_WITM_RECEIVEDON";
		}else if(sortColumn.equalsIgnoreCase("Workflow Deadline")){
			colName = "A.WFL_WITM_DEADLINE";
		}else if(sortColumn.equalsIgnoreCase("Recipient Name")){
			colName = "C.ECM_USER_NAME";
		}else if(sortColumn.equalsIgnoreCase("Sent Date")){
			colName = "A.WFL_WITM_RECEIVEDON";
		}
		return colName;		
	}

	public ArrayList<WorkItemDetails> searchInbox(String user_login,String filterType, String searchCriteria) throws Exception {
		logger.info("Started Method : searchInbox  Method parameter user_login : "+user_login+" filterType : "+filterType+" searchCriteria : "+searchCriteria);
		ArrayList<WorkItemDetails> workItemDetailsList = new ArrayList<WorkItemDetails>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = buildInboxSearchQuery(user_login,filterType,searchCriteria);
			stmt = conn.prepareStatement(sqlQuery);
			stmt.setString(1, escapeString(user_login));
			stmt.setString(2, "%" + escapeString(searchCriteria) + "%");
			rs = stmt.executeQuery();

			while (rs.next()) {
				WorkItemDetails workItemDetails = new WorkItemDetails();
				workItemDetails.setWorkflowWorkItemID(rs.getString("WFL_WITM_ID"));
				workItemDetails.setWorkflowItemSubject(rs.getString("WFL_SUBJECT"));
				workItemDetails.setWorkflowSender(rs.getString("WFL_SENDER"));
				String senderName = rs.getString("SENDER_NAME");
				workItemDetails.setWorkflowSenderName(senderName);
				workItemDetails.setWorkflowWorkItemType(rs.getString("WFL_WITM_TYPE"));
				workItemDetails.setWorkflowStepNo(rs.getInt("WFL_STEP_NO"));
				workItemDetails.setWorkflowItemDeadline(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_DEADLINE")));
				workItemDetails.setWorkflowItemStatus(rs.getString("WFL_WITM_STATUS"));
				workItemDetails.setWorkflowItemPriority(rs.getString("WFL_WITM_PRIORITY"));
				if(workItemDetails.getWorkflowItemPriority() == null)
					workItemDetails.setWorkflowItemPriority(rs.getString("WFL_PRIORITY"));
				workItemDetails.setWorkflowItemReceivedOn(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_RECEIVEDON")));
				workItemDetails.setWorkflowItemSysStatus(rs.getString("WFL_WITM_SYS_STATUS"));
				
				workItemDetailsList.add(workItemDetails);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : searchInbox");
		return workItemDetailsList;
	
	}

	private String buildInboxSearchQuery(String user_login, String filterType, String searchCriteria) {
		logger.info("Started Method : buildInboxSearchQuery  Method parameter user_login : "+user_login+" filterType : "+filterType+" searchCriteria : "+searchCriteria);
		String sqlQuery = null;
		if(filterType.equalsIgnoreCase("All")){
			sqlQuery = "SELECT A.WFL_WITM_ID,B.WFL_SUBJECT,A.WFL_SENDER,"
					+ "C.ECM_USER_NAME AS SENDER_NAME, A.WFL_WITM_TYPE, "
					+ "A.WFL_STEP_NO, A.WFL_WITM_DEADLINE,  A.WFL_WITM_STATUS, "
					+ "B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_SYS_STATUS "
					+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
					+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN "
					+ "WHERE A.WFL_RECIPIENT = ? AND B.WFL_SUBJECT=? ";
		}else if(filterType.equalsIgnoreCase("Active")){
			sqlQuery = "SELECT A.WFL_WITM_ID,B.WFL_SUBJECT,A.WFL_SENDER,"
					+ "C.ECM_USER_NAME AS SENDER_NAME, A.WFL_WITM_TYPE, "
					+ "A.WFL_STEP_NO, A.WFL_WITM_DEADLINE,  A.WFL_WITM_STATUS, "
					+ "B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_SYS_STATUS "
					+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
					+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN "
					+ "WHERE A.WFL_RECIPIENT = ? AND A.WFL_WITM_TYPE='TO' AND B.WFL_SUBJECT= ?";
		}else if(filterType.equalsIgnoreCase("CC")){
			sqlQuery = "SELECT A.WFL_WITM_ID,B.WFL_SUBJECT,A.WFL_SENDER,"
					+ "C.ECM_USER_NAME AS SENDER_NAME, A.WFL_WITM_TYPE, "
					+ "A.WFL_STEP_NO, A.WFL_WITM_DEADLINE,  A.WFL_WITM_STATUS, "
					+ "B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_SYS_STATUS "
					+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
					+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN "
					+ "WHERE A.WFL_RECIPIENT = ? AND A.WFL_WITM_TYPE='CC' AND B.WFL_SUBJECT=?";
		}else if(filterType.equalsIgnoreCase("Done by sub")){
			sqlQuery = "SELECT A.WFL_WITM_ID,B.WFL_SUBJECT,A.WFL_SENDER,"
					+ "C.ECM_USER_NAME AS SENDER_NAME, A.WFL_WITM_TYPE, "
					+ "A.WFL_STEP_NO, A.WFL_WITM_DEADLINE,  A.WFL_WITM_STATUS, "
					+ "B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_SYS_STATUS "
					+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
					+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN "
					+ "WHERE A.WFL_RECIPIENT = ? AND A.WFL_WITM_TYPE = 'Reply' "
					+ "AND A.WFL_WITM_STATUS NOT IN('Done','Forward','Reassign') AND B.WFL_SUBJECT=? ";
		}else if(filterType.equalsIgnoreCase("Overdue")){
			sqlQuery = "SELECT A.WFL_WITM_ID,B.WFL_SUBJECT,A.WFL_SENDER,"
					+ "C.ECM_USER_NAME AS SENDER_NAME, A.WFL_WITM_TYPE, "
					+ "A.WFL_STEP_NO, A.WFL_WITM_DEADLINE,  A.WFL_WITM_STATUS, "
					+ "B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_SYS_STATUS "
					+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
					+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN "
					+ "WHERE A.WFL_RECIPIENT = ? AND A.WFL_WITM_TYPE <> 'CC' "
					+ "AND A.WFL_WITM_STATUS NOT IN('Done','Forward','Reassign') AND A.WFL_WITM_DEADLINE < getDate() AND B.WFL_SUBJECT=?";

		}
		logger.info("Exit Method : buildInboxSearchQuery");
		return sqlQuery;
	}

	public ArrayList<WorkItemDetails> filterSentItems(String user_login,String filterType, String folderFilter, String sortOrder, String sortColumn, String page, String perPage) throws Exception {
		logger.info("Started Method : filterSentItems  Method parameter user_login : "+user_login+" filterType : "+filterType);

		ArrayList<WorkItemDetails> workItemDetailsList = new ArrayList<WorkItemDetails>();
		Connection conn = null;
		Statement stmt = null;
		CallableStatement callableStatement = null;
		ResultSet rs = null;
		// Begin change SK 23/5/17
		if(filterType == null)
			filterType = "ALL"; 
		
		String sortColumnDef = getColumnName(sortColumn);
		
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);
			
			String getDBUSERCursorSql = "{call GET_SENT_WORKITEM_DETAILS(?,?,?,?,?,?,?,?)}";
			callableStatement = conn.prepareCall(getDBUSERCursorSql);
			callableStatement.setString(1, escapeString(user_login));
			callableStatement.setString(2, escapeIntString(page, 1));
			callableStatement.setString(3, escapeIntString(perPage, 15));
			callableStatement.setInt(4, 1);
			callableStatement.setString(5, escapeString(filterType)); // SK 23/7
			callableStatement.setString(6, escapeString(folderFilter));
			if(sortOrder!=null && !sortOrder.equalsIgnoreCase("")){
				if((sortOrder.toUpperCase().trim().equals("ASC")) && (sortOrder.toUpperCase().trim().equals("DESC")))
					sortOrder = "DESC";
				callableStatement.setString(7, sortOrder);
			}else{
				if(sortOrder == null){
					callableStatement.setString(7,"DESC");
				}else{
					callableStatement.setString(7,sortOrder);
				}
			}
			
			if(sortColumnDef!=null && !sortColumnDef.equalsIgnoreCase("")){ // SK 23/7 
				callableStatement.setString(8, escapeString(sortColumnDef )); // SK 23/7
			}else{
				callableStatement.setString(8, "A.WFL_WITM_RECEIVEDON");
			}
			rs = callableStatement.executeQuery();
			
			HashSet<String> wfset=new HashSet<String>();  
			while (rs.next()) {
				String wflowID = rs.getString("WFL_ID");
				/*if(wfset.contains(wflowID)){
					
					continue;
				}*/
				wfset.add(wflowID);
				WorkItemDetails workItemDetails = new WorkItemDetails();
				workItemDetails.setWorkflowWorkItemID(rs.getString("WFL_WITM_ID"));
				workItemDetails.setWorkflowItemSubject(rs.getString("WFL_SUBJECT"));
				workItemDetails.setWorkflowItemActionComment(rs.getString("WFL_WITM_ACTION_COMMENT"));
				workItemDetails.setWorkflowSender(rs.getString("WFL_SENDER"));
				String recipientName = rs.getString("RECIPIENT_NAME");
				workItemDetails.setWorkflowReceivedBy(recipientName);// List of name
				workItemDetails.setWorkflowSender(recipientName); // SK Issues with setting recipient. So work around
				workItemDetails.setWorkflowWorkItemType(rs.getString("WFL_WITM_TYPE"));
				workItemDetails.setWorkflowStepNo(rs.getInt("WFL_STEP_NO"));
				if(rs.getDate("WFL_WITM_DEADLINE") != null)
					workItemDetails.setWorkflowItemDeadline(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_DEADLINE")));
				workItemDetails.setWorkflowItemStatus(rs.getString("WFL_WITM_STATUS"));
				workItemDetails.setWorkflowItemPriority(rs.getString("WFL_WITM_PRIORITY"));
				if(workItemDetails.getWorkflowItemPriority() == null)
					workItemDetails.setWorkflowItemPriority(rs.getString("WFL_PRIORITY"));

				if(rs.getDate("WFL_WITM_RECEIVEDON") != null)
					workItemDetails.setWorkflowItemReceivedOn(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_RECEIVEDON")));
				workItemDetails.setWorkflowItemSysStatus(rs.getString("WFL_WITM_SYS_STATUS"));
				workItemDetails.setWorkflowItemRootSender(rs.getString("WFL_WITM_ROOT_SENDER"));
				workItemDetails.setIsOverdue(DBUtil.getOverdue(rs.getDate("WFL_WITM_DEADLINE")));
				workItemDetails.setWorkflowItemSenderDepartment(rs.getInt("WFL_WITM_SENDER_DEPT"));
				workItemDetails.setWorkflowItemSenderDiv(rs.getInt("WFL_WITM_SENDER_DIV"));
				workItemDetails.setTotalCount(rs.getString("totalRecordsNumber"));
				workItemDetails.setSenderDepartment(rs.getString("ECM_DEPARTMENT"));
				workItemDetails.setSenderDivision(rs.getString("ECM_DIVISION"));
				workItemDetailsList.add(workItemDetails);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			if(rs != null){
				rs.close();
			}
			if(callableStatement!=null){
				callableStatement.close();
			}
			if(stmt !=null){
				stmt.close();
			}
			if(conn !=null){
				conn.close();
			}
		}
		logger.info("Exit Method : filterSentItems");
		return workItemDetailsList;
	
	
	}

	public ArrayList<WorkItemDetails> searchSentItems(String user_login,String filterType, String searchCriteria) throws Exception {
		logger.info("Started Method : searchSentItems  Method parameter user_login : "+user_login+" filterType : "+filterType+" searchCriteria : "+searchCriteria);
		ArrayList<WorkItemDetails> workItemDetailsList = new ArrayList<WorkItemDetails>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);
			
			sqlQuery = buildSentSearchQuery(user_login,filterType,searchCriteria);
			stmt = conn.prepareStatement(sqlQuery);
			stmt.setString(1, escapeString(user_login));
			rs = stmt.executeQuery();

			while (rs.next()) {
				WorkItemDetails workItemDetails = new WorkItemDetails();
				workItemDetails.setWorkflowWorkItemID(rs.getString("WFL_WITM_ID"));
				workItemDetails.setWorkflowItemSubject(rs.getString("WFL_SUBJECT"));
				workItemDetails.setWorkflowSender(rs.getString("WFL_SENDER"));
				//String recipientName = rs.getString("RECIPIENT_NAME");
				//workItemDetails.setWorkflowRecipientName(recipientName);// List of name
				workItemDetails.setWorkflowWorkItemType(rs.getString("WFL_WITM_TYPE"));
				workItemDetails.setWorkflowStepNo(rs.getInt("WFL_STEP_NO"));
				workItemDetails.setWorkflowItemDeadline(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_DEADLINE")));
				workItemDetails.setWorkflowItemStatus(rs.getString("WFL_WITM_STATUS"));
				workItemDetails.setWorkflowItemPriority(rs.getString("WFL_WITM_PRIORITY"));
				if(workItemDetails.getWorkflowItemPriority() == null)
					workItemDetails.setWorkflowItemPriority(rs.getString("WFL_PRIORITY"));
				workItemDetails.setWorkflowItemReceivedOn(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_RECEIVEDON")));
				workItemDetails.setWorkflowItemSysStatus(rs.getString("WFL_WITM_SYS_STATUS"));
				
				workItemDetailsList.add(workItemDetails);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : searchSentItems");
		return workItemDetailsList;
	}

	private String buildSentSearchQuery(String user_login, String filterType,String searchCriteria) {
		logger.info("Started Method : buildSentSearchQuery  Method parameter user_login : "+user_login+" filterType : "+filterType+" searchCriteria : "+searchCriteria);
		String sqlQuery = "";
		if(filterType.equalsIgnoreCase("All")){
			sqlQuery = "SELECT A.WFL_WITM_ID,B.WFL_SUBJECT,A.WFL_SENDER,"
					+ "C.ECM_USER_NAME AS RECIPIENT_NAME, A.WFL_WITM_TYPE, "
					+ "A.WFL_STEP_NO, A.WFL_WITM_DEADLINE,  A.WFL_WITM_STATUS, "
					+ "B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_SYS_STATUS "
					+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
					+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN "
					+ "WHERE A.WFL_SENDER = ? ";
		}else if(filterType.equalsIgnoreCase("Active")){
			sqlQuery = "SELECT A.WFL_WITM_ID,B.WFL_SUBJECT,A.WFL_SENDER,"
					+ "C.ECM_USER_NAME AS RECIPIENT_NAME, A.WFL_WITM_TYPE, "
					+ "A.WFL_STEP_NO, A.WFL_WITM_DEADLINE,  A.WFL_WITM_STATUS, "
					+ "B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_SYS_STATUS "
					+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
					+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN "
					+ "WHERE A.WFL_SENDER = ? AND A.WFL_WITM_SYS_STATUS = 'ACTIVE' ";
		}else if(filterType.equalsIgnoreCase("CC")){
			sqlQuery = "SELECT A.WFL_WITM_ID,B.WFL_SUBJECT,A.WFL_SENDER,"
					+ "C.ECM_USER_NAME AS RECIPIENT_NAME, A.WFL_WITM_TYPE, "
					+ "A.WFL_STEP_NO, A.WFL_WITM_DEADLINE,  A.WFL_WITM_STATUS, "
					+ "B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_SYS_STATUS "
					+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
					+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN AND A.WFL_WITM_TYPE='CC' "
					+ "WHERE A.WFL_SENDER = ? ";
		}else if(filterType.equalsIgnoreCase("Completed by Sub")){
			sqlQuery = "SELECT A.WFL_WITM_ID,B.WFL_SUBJECT,A.WFL_SENDER,"
					+ "C.ECM_USER_NAME AS RECIPIENT_NAME, A.WFL_WITM_TYPE, "
					+ "A.WFL_STEP_NO, A.WFL_WITM_DEADLINE,  A.WFL_WITM_STATUS, "
					+ "B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_SYS_STATUS "
					+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
					+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN AND A.WFL_WITM_TYPE = 'Reply' "
					+ "WHERE A.WFL_SENDER = ? ";
		}else if(filterType.equalsIgnoreCase("Overdue")){
			sqlQuery = "SELECT A.WFL_WITM_ID,B.WFL_SUBJECT,A.WFL_SENDER,"
					+ "C.ECM_USER_NAME AS RECIPIENT_NAME, A.WFL_WITM_TYPE, "
					+ "A.WFL_STEP_NO, A.WFL_WITM_DEADLINE,  A.WFL_WITM_STATUS, "
					+ "B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_SYS_STATUS "
					+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
					+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN "
					+ "WHERE A.WFL_SENDER = ? AND A.WFL_WITM_SYS_STATUS = 'ACTIVE' AND "
					+ "A.WFL_WITM_TYPE <>('Reply')";
		}
		logger.info("Exit Method : buildSentSearchQuery");
		return sqlQuery;
	}

	
	private String getArchiveColumnName(String sortColumn) {
		
		if(sortColumn == null || sortColumn.equalsIgnoreCase(""))
			return "A.WFL_WITM_RECEIVEDON";
		
		String colName = "";
		if(sortColumn.equalsIgnoreCase("Sender Name")){
			colName = "C.ECM_USER_NAME";
		}else if(sortColumn.equalsIgnoreCase("Workflow Deadline")){
			colName = "A.WFL_WITM_DEADLINE";
		}else if(sortColumn.equalsIgnoreCase("Workflow Name")){
			colName = "B.WFL_SUBJECT";
		}
		return colName;		
	}
	
	public ArrayList<WorkItemDetails> getArchiveItems(String user_login, String page, String perPage, String folderFilter, String sortOrder, String sortColumn) throws Exception {
		logger.info("Started Method : getArchiveItems  Method parameter user_login : "+user_login);
		ArrayList<WorkItemDetails> workItemDetailsList = new ArrayList<WorkItemDetails>();
		Connection conn = null;
		Statement stmt = null;
		CallableStatement callableStatement = null;
		ResultSet rs = null;
		try {
			String sortColName = getArchiveColumnName(sortColumn);
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);
		
			String getDBUSERCursorSql = "{call GET_ARCHIVE_WORKITEM_DETAILS(?,?,?,?,?,?,?,?)}";
			callableStatement = conn.prepareCall(getDBUSERCursorSql);
			callableStatement.setString(1, escapeString(user_login));
			callableStatement.setString(2, escapeIntString(page, 1));
			callableStatement.setString(3, escapeIntString(perPage, 15));
			callableStatement.setInt(4, 1);
			callableStatement.setString(5, null);
			callableStatement.setString(6, escapeString(folderFilter));
			
			if(sortOrder!=null && !sortOrder.equalsIgnoreCase("")){
				if((sortOrder.toUpperCase().trim().equals("ASC")) && (sortOrder.toUpperCase().trim().equals("DESC")))
					sortOrder = "DESC";
				callableStatement.setString(7, sortOrder);
			}else{
				if(sortOrder == null){
					callableStatement.setString(7,"DESC");
				}else{
					callableStatement.setString(7,sortOrder);
				}
			}
			
			if(sortColName!=null && !sortColName.equalsIgnoreCase("")){ 
				callableStatement.setString(8, escapeString(sortColName)); 
			}else{
				callableStatement.setString(8, "A.WFL_WITM_RECEIVEDON");
			}
			
			rs = callableStatement.executeQuery();

			while (rs.next()) {
				WorkItemDetails workItemDetails = new WorkItemDetails();
				workItemDetails.setWorkflowWorkItemID(rs.getString("WFL_WITM_ID"));
				workItemDetails.setWorkflowItemSubject(rs.getString("WFL_SUBJECT"));
				workItemDetails.setWorkflowSender(rs.getString("WFL_SENDER"));
				workItemDetails.setWorkflowItemActionComment(rs.getString("WFL_WITM_ACTION_COMMENT"));
				String senderName = rs.getString("SENDER_NAME");
				workItemDetails.setWorkflowSenderName(senderName);
				workItemDetails.setWorkflowWorkItemType(rs.getString("WFL_WITM_TYPE"));
				workItemDetails.setWorkflowStepNo(rs.getInt("WFL_STEP_NO"));
				workItemDetails.setWorkflowItemDeadline(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_DEADLINE")));
				workItemDetails.setWorkflowItemStatus(rs.getString("WFL_WITM_STATUS"));
				workItemDetails.setWorkflowItemPriority(rs.getString("WFL_WITM_PRIORITY"));
				if(workItemDetails.getWorkflowItemPriority() == null)
					workItemDetails.setWorkflowItemPriority(rs.getString("WFL_PRIORITY"));
				workItemDetails.setWorkflowItemReceivedOn(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_RECEIVEDON")));
				workItemDetails.setWorkflowItemSysStatus(rs.getString("WFL_WITM_SYS_STATUS"));
				workItemDetails.setTotalCount(rs.getString("totalRecordsNumber"));
				
				workItemDetailsList.add(workItemDetails);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			if(rs != null){
				rs.close();
			}
			if(callableStatement!=null){
				callableStatement.close();
			}
			if(stmt !=null){
				stmt.close();
			}
			if(conn !=null){
				conn.close();
			}
		}
		logger.info("Exit Method : getArchiveItems");
		return workItemDetailsList;
	
	}

	public ArrayList<WorkflowAttachments> getDocumentId(String witem_id) throws Exception {
		logger.info("Started Method : getDocumentId  Method parameter user_login : "+witem_id);
		ArrayList<WorkflowAttachments> workflowAttachmentsList = new ArrayList<WorkflowAttachments>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "SELECT A.WFL_DOCUMENT_ID, A.WFL_ATTACHMENT_TYPE	"
					+ "FROM DIMS_WORKFLOW_ATTACHMENT A "
					+ "WHERE A.WFL_ID IN (SELECT B.WFL_ID FROM DIMS_WORKITEM B WHERE B.WFL_WITM_ID= ? )";

			stmt = conn.prepareStatement(sqlQuery);
			stmt.setString(1, escapeString(witem_id));
			rs = stmt.executeQuery();

			while (rs.next()) {
				WorkflowAttachments workflowAttachments = new WorkflowAttachments();
				workflowAttachments.setWorkflowDocumentId(rs.getString("WFL_DOCUMENT_ID"));
				workflowAttachments.setWorkflowAttachmentType(rs.getString("WFL_ATTACHMENT_TYPE"));
				workflowAttachmentsList.add(workflowAttachments);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getDocumentId");
		return workflowAttachmentsList;
	}

public String forwardWorkItemFromSP(WorkFlowDetails workFlowDetails, ArrayList<WorkflowAttachments> workflowAttachmentsList) throws Exception {
		logger.info("Started Method : forwardWorkItemFromSP ");
		Connection conn = null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);
			
			ArrayList<WorkflowRecipient> workflowRecipientList =  workFlowDetails.getWorkItemDetails().getWorkflowRecipientList();
			Collections.sort(workflowRecipientList,new WorkflowRecipientComp());
			ArrayList<String> newWorkItemList = new ArrayList<String>();
			ArrayList<String> oldNewList = new ArrayList<String>();
			for (int j = 0; j < workflowRecipientList.size(); j++) {
				WorkItemDetails workItemDetails = workFlowDetails.getWorkItemDetails();
				String witId = executeWorkItemForwardSP(workItemDetails.getWorkflowWorkItemID(), 
						workItemDetails.getWorkflowItemActionBy(),
						URLDecoder.decode(workItemDetails.getWorkflowItemActionComment(),"UTF-8"),
						workItemDetails.getWorkflowItemActionOnBehalf(),
						workflowRecipientList.get(j).getWorkflowRecipient(),
						workflowRecipientList.get(j).getWorkflowWorkItemType(),
						workItemDetails.getWorkflowInstruction(),
						workItemDetails.getWorkflowItemDeadline(),
						workItemDetails.getWorkflowItemReminderDate(), 
						workItemDetails.getWorkflowItemPriority(), conn);
				newWorkItemList.add(witId);
				//workFlowDetails.getWorkItemDetails().setWorkflowWorkItemID(witId);
				String oldNewString = workFlowDetails.getWorkItemDetails().getWorkflowWorkItemID() + ";" + witId;
				oldNewList.add(oldNewString);
			}
			if(workflowAttachmentsList.size()>0){
				for (int i = 0; i < newWorkItemList.size(); i++) {
					for (int j = 0; j < workflowAttachmentsList.size(); j++) {
						WorkflowAttachments wfAtth = workflowAttachmentsList.get(j);
						executeAddWorkitemAttachmentSP(newWorkItemList.get(i),wfAtth.getWorkflowDocumentId(), conn);
					}
				}
			}
			// Execute Email Notification Stored Procedure
			for(String onString: oldNewList) {
				String[] onSplits = onString.split(";");
				String oWid = "";
				String nWid = "";
				if(onSplits.length > 0)
					oWid = onSplits[0];
				if(onSplits.length > 1)
					nWid = onSplits[1];
				executeEmailNotificationSP(workFlowDetails.getWorkflowID(), nWid, oWid, conn);
			}
			conn.close();	

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			conn.close();
		}
		logger.info("Exit Method : forwardWorkItemFromSP ");
		return "Forwarded the work item";
	}

	private String executeAddUserWorkitemSP(String wiID, WorkItemDetails wi, WorkflowRecipient wr, Connection conn) throws Exception
	{
		String returnValue = null;
		CallableStatement callableStatement = null;
		try {
			String getDBUSERSql = "{call APP_DIMS_ADDUSER_WORKITEM(?,?,?,?,?,?,?,?,?,?,?)}";
			callableStatement = conn.prepareCall(getDBUSERSql);
			callableStatement.setString(1, escapeString(wiID));
			callableStatement.setString(2, escapeString(wi.getWorkflowSender()));
			callableStatement.setString(3, escapeString(wr.getWorkflowRecipient()));
			callableStatement.setString(4, escapeString(wr.getWorkflowWorkItemType()));
			callableStatement.setString(5, escapeString(wi.getWorkflowInstruction()));
			callableStatement.setString(6, escapeString(wi.getWorkflowItemDeadline()));
			callableStatement.setString(7, escapeString(wi.getWorkflowItemReminderDate()));
			callableStatement.setString(8, escapeString(wi.getWorkflowItemActionOnBehalf()));
			callableStatement.setString(9, "New");
			callableStatement.setString(10, escapeString(URLDecoder.decode(wi.getWorkflowItemActionComment(),"UTF-8")));
			callableStatement.registerOutParameter(11, java.sql.Types.NVARCHAR);
			
			callableStatement.executeUpdate();
			returnValue = callableStatement.getString(11);
			callableStatement.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if(callableStatement != null)
				callableStatement.close();
			if(!(e.getMessage().contains("Error number 1002 in the THROW")))
				throw new Exception(e.getMessage());
		}finally{
			if(callableStatement != null)
				callableStatement.close();
		 }
		return returnValue;
	}

	
	public String addUserWorkItemFromSP(WorkFlowDetails workFlowDetails) throws Exception {
		logger.info("Started Method : addUserWorkItemFromSP ");
		Connection conn = null;
		try {
			
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
				DBConfiguration.getInstance().USER_NAME,
				DBConfiguration.getInstance().PASSWORD,
				DBConfiguration.getInstance().JDBC_DRIVER);
		
				ArrayList<WorkflowRecipient> workflowRecipientList =  workFlowDetails.getWorkItemDetails().getWorkflowRecipientList();
				Collections.sort(workflowRecipientList,new WorkflowRecipientComp());
				ArrayList<String> newWorkItemList = new ArrayList<String>();
				ArrayList<String> oldNewList = new ArrayList<String>();
				
				for (int j = 0; j < workflowRecipientList.size(); j++) {
					try {
						String wiID = executeAddUserWorkitemSP(workFlowDetails.getWorkItemDetails().getWorkflowWorkItemID(), 
								workFlowDetails.getWorkItemDetails(),
								workflowRecipientList.get(j), conn);	
						newWorkItemList.add(wiID);
						
						String oldNewString = workFlowDetails.getWorkItemDetails().getWorkflowWorkItemID() + ";" + wiID;
						oldNewList.add(oldNewString);
						
					} catch (Exception e) {
						
					}
					//workFlowDetails.getWorkItemDetails().setWorkflowWorkItemID(wiID);
				}
				ArrayList<WorkflowAttachments> workflowAttachmentsList = workFlowDetails.getWorkItemDetails().getWorkflowAttachments();
				if(workflowAttachmentsList.size()>0){
					for (int i = 0; i < newWorkItemList.size(); i++) {
						for (int j = 0; j < workflowAttachmentsList.size(); j++) {
							WorkflowAttachments wfAtth = workflowAttachmentsList.get(j);
							executeAddWorkitemAttachmentSP(newWorkItemList.get(i),wfAtth.getWorkflowDocumentId(), conn);
						}
					}
				}
				
				// Execute Email Notification Stored Procedure
				for(String onString: oldNewList) {
					String[] onSplits = onString.split(";");
					String oWid = "";
					String nWid = "";
					if(onSplits.length > 0)
						oWid = onSplits[0];
					if(onSplits.length > 1)
						nWid = onSplits[1];
					executeEmailNotificationSP(workFlowDetails.getWorkflowID(), nWid, oWid, conn);
				}
					
		}catch (Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}finally {
			conn.close();
		}
		logger.info("Exit Method : addUserWorkItem ");
		return "User added successfully.";
	}

	private String executeReassignWorkitemSP(String wiID, WorkItemDetails wi, String recipientUser, Connection conn, String recipientName) throws Exception
	{
		String returnValue = null;
		CallableStatement callableStatement = null;
		try {
			String getDBUSERSql = "{call APP_DIMS_REASSIGN_WORKITEM(?,?,?,?,?,?,?)}";
			callableStatement = conn.prepareCall(getDBUSERSql);
			callableStatement.setString(1, escapeString(wiID));
			callableStatement.setString(2, escapeString(recipientUser));
			callableStatement.setString(3, escapeString(wi.getWorkflowItemActionBy()));
			callableStatement.setString(4, escapeString(wi.getWorkflowItemActionOnBehalf()));
			callableStatement.setString(5, escapeString(URLDecoder.decode(wi.getWorkflowItemActionComment(),"UTF-8")));
			callableStatement.setString(6, escapeString(recipientName));
			callableStatement.registerOutParameter(7, java.sql.Types.NVARCHAR);
			
			callableStatement.executeUpdate();
			returnValue = callableStatement.getString(7);
			callableStatement.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if(callableStatement != null)
				callableStatement.close();
			throw new Exception(e.getMessage());
		}finally{
			if(callableStatement != null)
				callableStatement.close();
		}
		return returnValue;
	}

	public String reassignWorkItemFromSP(WorkFlowDetails workFlowDetails, String recipientName) throws Exception {
		logger.info("Started Method : reassignWorkItemFromSP ");
		Connection conn = null;
		try {
			
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			ArrayList<WorkflowRecipient> workflowRecipientList =  workFlowDetails.getWorkItemDetails().getWorkflowRecipientList();
			Collections.sort(workflowRecipientList,new WorkflowRecipientComp());
			ArrayList<String> newWorkItemList = new ArrayList<String>();
			ArrayList<String> oldNewList = new ArrayList<String>();
			for (int j = 0; j < workflowRecipientList.size(); j++) {
				String wiID = executeReassignWorkitemSP(workFlowDetails.getWorkItemDetails().getWorkflowWorkItemID(), 
					workFlowDetails.getWorkItemDetails(), 
					workflowRecipientList.get(j).getWorkflowRecipient(), conn,recipientName);
				
				newWorkItemList.add(wiID);
				//workFlowDetails.getWorkItemDetails().setWorkflowWorkItemID(wiID);
				String oldNewString = workFlowDetails.getWorkItemDetails().getWorkflowWorkItemID() + ";" + wiID;
				oldNewList.add(oldNewString);
			}
			ArrayList<WorkflowAttachments> workflowAttachmentsList = workFlowDetails.getWorkItemDetails().getWorkflowAttachments();
			if(workflowAttachmentsList.size()>0){
				for (int i = 0; i < newWorkItemList.size(); i++) {
					for (int j = 0; j < workflowAttachmentsList.size(); j++) {
						WorkflowAttachments wfAtth = workflowAttachmentsList.get(j);
						executeAddWorkitemAttachmentSP(newWorkItemList.get(i),wfAtth.getWorkflowDocumentId(), conn);
					}
				}
			}
			
			// Execute Email Notification Stored Procedure
			for(String onString: oldNewList) {
				String[] onSplits = onString.split(";");
				String oWid = "";
				String nWid = "";
				if(onSplits.length > 0)
					oWid = onSplits[0];
				if(onSplits.length > 1)
					nWid = onSplits[1];
				executeEmailNotificationSP(workFlowDetails.getWorkflowID(), nWid, oWid, conn);
			}
			
		}catch (Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}finally {
			conn.close();
		}
		logger.info("Exit Method : reassignWorkItemFromSP ");
		return "Work Item reassigned successfully.";
	}

	private void executeArchiveWorkItemSP(String wiID, String actionOnBehalf, String actionBy, Connection conn) throws Exception 
	{
		CallableStatement callableStatement = null;
		try {
			String getDBUSERSql = "{call APP_DIMS_ARCHIVE_WORKITEM(?,?,?)}";
			callableStatement = conn.prepareCall(getDBUSERSql);
			callableStatement.setString(1, escapeString(wiID));
			callableStatement.setString(2, escapeString(actionBy));
			callableStatement.setString(3, escapeString(actionOnBehalf));
			
			callableStatement.executeUpdate();
			
			callableStatement.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if(callableStatement != null)
				callableStatement.close();
			throw new Exception(e.getMessage());
		}
		finally{
			if(callableStatement != null)
				callableStatement.close();
		 }
	}
	
	public String archiveWorkItemFromSP(String user_login, String workItemID, String actionBy) throws Exception {
		logger.info("Started Method : archiveWorkItem Method parameter actionBy : "+actionBy+"workItemID : "+workItemID);
		Connection conn = null;
		try {
			
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			executeArchiveWorkItemSP(workItemID, user_login, actionBy, conn);
			
		}catch (Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}finally {
			conn.close();
		}
		logger.info("Exit Method : archiveWorkItem");
		return "Archived workitem";
	
	}
	
	public String completeWorkFlowFromSP(String workItemId, String user_login) throws Exception {
		logger.info("Started Method : completeWorkFlowFromSP Method parameter workItemId : "+
					workItemId+"user_login : "+user_login);
		CallableStatement callableStatement = null;
		Connection conn = null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			
			String getDBUSERSql = "{call APP_DIMS_COMPLETE_WORKITEM(?,?,?)}";
			callableStatement = conn.prepareCall(getDBUSERSql);
			callableStatement.setString(1, escapeString(workItemId));
			callableStatement.setString(2, escapeString(user_login));
			callableStatement.setString(3, escapeString(user_login));
			
			callableStatement.executeUpdate();
			
			callableStatement.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if(callableStatement != null)
				callableStatement.close();
			throw new Exception(e.getMessage());
		}finally{
			if(callableStatement != null)
				callableStatement.close();
			conn.close();
		 }
		
		logger.info("Exit Method : completeWorkFlow");
		return "Workflow completed";
	}

	private String executeWorkItemForwardSP(String wiID, String actionUser, String actionComments, 
			String actionOnBehalf, String recipient, String wiType, String instructions,
			String deadline, String reminder, String priority, Connection conn) throws Exception 
	{
		String returnValue =null;
		if(recipient == null)
			return "";
		if(wiType == null)
			wiType = "to";
		CallableStatement callableStatement = null;
		try {
			String getDBUSERSql = "{call APP_DIMS_FORWARD_WORKITEM(?,?,?,?,?,?,?,?,?,?,?)}";
			callableStatement = conn.prepareCall(getDBUSERSql);
			callableStatement.setString(1, escapeString(wiID));
			callableStatement.setString(2, escapeString(actionUser));
			callableStatement.setString(3, escapeString(actionComments));
			callableStatement.setString(4, escapeString(actionOnBehalf));
			callableStatement.setString(5, escapeString(recipient));
			callableStatement.setString(6, escapeString(wiType));
			callableStatement.setString(7, escapeString(instructions));
			callableStatement.setString(8, escapeString(deadline));
			callableStatement.setString(9, escapeString(reminder));
			callableStatement.setInt(10, DBUtil.convertStringToInt(priority));
			
			callableStatement.registerOutParameter(11, java.sql.Types.NVARCHAR);
			
			callableStatement.executeUpdate();

			returnValue = callableStatement.getString(11);
			
			callableStatement.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if(callableStatement != null)
				callableStatement.close();
			throw new Exception(e.getMessage());
		}
		finally{
			if(callableStatement != null)
				callableStatement.close();
		 }
		
		return returnValue;
	}
	
	private String executeWorkItemDoneSP(String wiID, String actionUser, String actionComments, String actionOnBehalf, Connection conn) throws Exception 
	{
		CallableStatement callableStatement = null;
		String returnValue ="";
		try {
			String getDBUSERSql = "{call APP_DIMS_DONE_WORKITEM(?,?,?,?,?)}";
			callableStatement = conn.prepareCall(getDBUSERSql);
			callableStatement.setString(1, escapeString(wiID));
			callableStatement.setString(2, escapeString(actionUser));
			callableStatement.setString(3, escapeString(actionComments));
			callableStatement.setString(4, escapeString(actionOnBehalf));
			
			callableStatement.registerOutParameter(5, java.sql.Types.NVARCHAR);
			
			callableStatement.executeUpdate();

			returnValue = callableStatement.getString(5);
			callableStatement.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if(callableStatement != null)
				callableStatement.close();
			throw new Exception(e.getMessage());
		}
		finally{
			if(callableStatement != null)
				callableStatement.close();
		 }
		
		return returnValue;
	}
	
	public String doneWorkItemFromSP(WorkFlowDetails workFlowDetails, ArrayList<WorkflowAttachments> workflowAttachmentsList) throws Exception {
		
		logger.info("Started Method : doneWorkItemFromSP");
		Connection conn = null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);

			
			
			WorkItemDetails workItemDetails = workFlowDetails.getWorkItemDetails();
			String nwitid = executeWorkItemDoneSP(workFlowDetails.getWorkItemDetails().getWorkflowWorkItemID(),
					workItemDetails.getWorkflowItemActionBy(),
					URLDecoder.decode(workItemDetails.getWorkflowItemActionComment(),"UTF-8"),
					workItemDetails.getWorkflowItemActionOnBehalf(), conn);
			
			if(workflowAttachmentsList.size()>0){
				for (int i = 0; i < workflowAttachmentsList.size(); i++) {
					WorkflowAttachments wfAtth = workflowAttachmentsList.get(i);
					executeAddWorkitemAttachmentSP(nwitid,wfAtth.getWorkflowDocumentId(), conn);
				}
			}
			
			// Execute Email Notification Stored Procedure
			executeEmailNotificationSP(workFlowDetails.getWorkflowID(), nwitid, workFlowDetails.getWorkItemDetails().getWorkflowWorkItemID(), conn);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			conn.close();
		}
		logger.info("Exit Method : doneWorkItemFromSP");
		return "Done the work item";
	}
	

	public ArrayList<WorkItemHistory> getWorkFlowHistoryByDept(String workItemId, String workflowId, String user_login) throws Exception {
		logger.info("Started Method : getWorkFlowHistory method parameter workitemId : "+workItemId);
		Connection conn = null;
		ResultSet rs = null;
		Statement st = null;
		CallableStatement callableStatement = null;
		String user_department_code = null;
		ArrayList<WorkItemHistory> workItemHistoryList= new ArrayList<WorkItemHistory>();
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);
			st = DBUtil.getStatement(conn);
			
			String sqlQuery = "SELECT ECM_DEPT_CODE FROM ECM_EMPLOYEE WHERE ECM_USER_LOGIN = '"+user_login+"'";

			rs = st.executeQuery(sqlQuery);
			
			while (rs.next()) {
				user_department_code = rs.getString("ECM_DEPT_CODE");
			}
			System.out.println("user_department_code :: "+user_department_code);
			if(user_department_code != null && user_department_code.trim().length()>0){
				String getWorkFlowHistorySql = "{call GET_WORKFLOW_HISTORY_BYDEPT(?,?,?)}";
				callableStatement = conn.prepareCall(getWorkFlowHistorySql);
				System.out.println("user_department_code indide :: "+user_department_code);
				callableStatement.setString(1, escapeString(user_department_code));
				callableStatement.setString(2, escapeString(workItemId));
				callableStatement.setString(3, escapeString(workflowId));
							
				rs = callableStatement.executeQuery();
				while (rs.next()) {
					WorkItemHistory workItemHistory = new WorkItemHistory();
					workItemHistory.setRecipientName(rs.getString("RECIPIENT_NAME"));
					workItemHistory.setSenderName(rs.getString("SENDER_NAME"));
					if(rs.getString("WFL_ACTION_STATUS")!=null){
						workItemHistory.setWorkItemStatus(rs.getString("WFL_ACTION_STATUS").trim());
					}else{
						workItemHistory.setWorkItemStatus("");
					}
					
					workItemHistory.setWorkItemType(rs.getString("WFL_WITM_TYPE"));
					workItemHistory.setWorkItemReceivedOn(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_RECEIVEDON")));
					workItemHistory.setWorkItemDeadLine(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_DEADLINE")));
					workItemHistory.setWorkflowInstruction(rs.getString("WFL_INSTRUCTIONS"));
					workItemHistory.setWorkItemActionComment(rs.getString("WFL_WITM_ACTION_COMMENT"));
					workItemHistory.setWorkflowActionUser(rs.getString("WFL_ACTION_USER"));
					workItemHistory.setWorkflowActionTime(DBUtil.formatDateForUI(rs.getTimestamp("WFL_ACTION_TIMESTAMP")));
					workItemHistory.setWorkflowId(rs.getString("WFL_ID"));
					workItemHistory.setWorkflowActionItemId(rs.getString("WFL_WITM_ID"));
					workItemHistoryList.add(workItemHistory);
				}
			}
			
		} catch(Exception e){
		    //Handle errors for Class.forName
		    e.printStackTrace();
		    logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
			if(rs != null){
				rs.close();
			}
			if(callableStatement!=null){
				callableStatement.close();
			}
			if(conn !=null){
				conn.close();
			}
		 }
		logger.info("Exit Method : getWorkFlowHistory");
		return workItemHistoryList;
	}


	public ArrayList<WorkItemHistory> getWorkFlowHistory(String workItemId, String workflowId) throws Exception {
		logger.info("Started Method : getWorkFlowHistory method parameter workitemId : "+workItemId);
		Connection conn = null;
		ResultSet rs = null;
		CallableStatement callableStatement = null;
		ArrayList<WorkItemHistory> workItemHistoryList= new ArrayList<WorkItemHistory>();
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);
			
			String getWorkFlowHistorySql = "{call GET_WORKFLOW_HISTORY(?,?)}";
			callableStatement = conn.prepareCall(getWorkFlowHistorySql);
			callableStatement.setString(1, escapeString(workItemId));
			callableStatement.setString(2, escapeString(workflowId));
						
			rs = callableStatement.executeQuery();
			while (rs.next()) {
				WorkItemHistory workItemHistory = new WorkItemHistory();
				workItemHistory.setRecipientName(rs.getString("RECIPIENT_NAME"));
				workItemHistory.setSenderName(rs.getString("SENDER_NAME"));
				if(rs.getString("WFL_ACTION_STATUS")!=null){
					workItemHistory.setWorkItemStatus(rs.getString("WFL_ACTION_STATUS").trim());
				}else{
					workItemHistory.setWorkItemStatus("");
				}
				
				workItemHistory.setWorkItemType(rs.getString("WFL_WITM_TYPE"));
				workItemHistory.setWorkItemReceivedOn(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_RECEIVEDON")));
				workItemHistory.setWorkItemDeadLine(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_DEADLINE")));
				workItemHistory.setWorkflowInstruction(rs.getString("WFL_INSTRUCTIONS"));
				workItemHistory.setWorkItemActionComment(rs.getString("WFL_WITM_ACTION_COMMENT"));
				workItemHistory.setWorkflowActionUser(rs.getString("WFL_ACTION_USER"));
				workItemHistory.setWorkflowActionTime(DBUtil.formatDateForUI(rs.getTimestamp("WFL_ACTION_TIMESTAMP")));
				workItemHistory.setWorkflowId(rs.getString("WFL_ID"));
				workItemHistory.setWorkflowActionItemId(rs.getString("WFL_WITM_ID"));
				workItemHistoryList.add(workItemHistory);
			}
		} catch(Exception e){
		    //Handle errors for Class.forName
		    e.printStackTrace();
		    logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
			if(rs != null){
				rs.close();
			}
			if(callableStatement!=null){
				callableStatement.close();
			}
			if(conn !=null){
				conn.close();
			}
		 }
		logger.info("Exit Method : getWorkFlowHistory");
		return workItemHistoryList;
	}

	public ArrayList<WorkItemDetails> searchWorkFlow(WorkFlowSearchBean workFlowSearchBean) throws Exception {
		logger.info("Started Method : searchWorkFlow ");
		ArrayList<WorkItemDetails> workItemDetailsList= new ArrayList<WorkItemDetails>();
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		StringBuffer sqlQuery= new StringBuffer();
		String sqlWhereClause = null;
		sqlQuery.append("SELECT * FROM (SELECT ROW_NUMBER() OVER(ORDER BY A.WFL_WITM_RECEIVEDON DESC) as RowNum, COUNT(*) over() AS totalRecordsNumber, ");
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
			
			String searchType = workFlowSearchBean.getWorkFlowSearchType();
			if(searchType == null)
				searchType = "Inbox";
			Boolean hasArgs = false;
			if((workFlowSearchBean.getReferenceNumber()!= null && workFlowSearchBean.getReferenceNumber().trim().length()>0) || 
					(workFlowSearchBean.getDocumentId()!=null && workFlowSearchBean.getDocumentId().trim().length()>0)){
				if(searchType.equalsIgnoreCase("Inbox")){
				sqlQuery.append(" A.WFL_WITM_ID,A.WFL_ID,B.WFL_SUBJECT,B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, "
						+ "C.ECM_USER_NAME AS SENDER_NAME,A.WFL_WITM_TYPE, "
						+ "A.WFL_STEP_NO, A.WFL_PARENT_WITM,A.WFL_INSTRUCTIONS,"
						+ "A.WFL_WITM_RECEIVEDON,A.WFL_WITM_DEADLINE,A.WFL_WITM_REMINDER_DATE,"
						+ "A.WFL_WITM_ROOT_SENDER,  A.WFL_WITM_STATUS,"
						+ "A.WFL_WITM_ACTION,A.WFL_WITM_ACTION_COMMENT,"
						+ "A.WFL_WITM_ACTION_BY,A.WFL_WITM_ACTION_ONBEHALF,"
						+ "A.WFL_WITM_SENDER_DEPT,A.WFL_WITM_RECEIVER_DEPT,"
						+ "A.WFL_WITM_SENDER_DIV,A.WFL_WITM_RECEIVER_DIV,A.WFL_WITM_SYS_STATUS "
						+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
						+ "INNER JOIN dbo.DIMS_WORKITEM_ATTACHMENT D ON A.WFL_WITM_ID = D.WFL_WITM_ID INNER JOIN "
						+ "dbo.FileNet_Docs E ON D.WFL_DOCUMENT_ID = E.object_id "
						+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN "
						+ "WHERE A.WFL_WITM_SYS_STATUS='ACTIVE' AND A.WFL_RECIPIENT=? AND A.WFL_WITM_STATUS != 'Archive' ");
				hasArgs = true;
			} 
			else if(searchType.equalsIgnoreCase("Sent")){
				sqlQuery.append(" A.WFL_WITM_ID,A.WFL_ID,B.WFL_SUBJECT,B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, "
						+ "C.ECM_USER_NAME AS SENDER_NAME,A.WFL_WITM_TYPE, "
						+ "A.WFL_STEP_NO, A.WFL_PARENT_WITM,A.WFL_INSTRUCTIONS,"
						+ "A.WFL_WITM_RECEIVEDON,A.WFL_WITM_DEADLINE,A.WFL_WITM_REMINDER_DATE,"
						+ "A.WFL_WITM_ROOT_SENDER,  A.WFL_WITM_STATUS,"
						+ "A.WFL_WITM_ACTION,A.WFL_WITM_ACTION_COMMENT,"
						+ "A.WFL_WITM_ACTION_BY,A.WFL_WITM_ACTION_ONBEHALF,"
						+ "A.WFL_WITM_SENDER_DEPT,A.WFL_WITM_RECEIVER_DEPT,"
						+ "A.WFL_WITM_SENDER_DIV,A.WFL_WITM_RECEIVER_DIV,A.WFL_WITM_SYS_STATUS "
						+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
						+ "INNER JOIN dbo.DIMS_WORKITEM_ATTACHMENT D ON A.WFL_WITM_ID = D.WFL_WITM_ID INNER JOIN "
						+ "dbo.FileNet_Docs E ON D.WFL_DOCUMENT_ID = E.object_id "
						+ "JOIN ECM_EMPLOYEE C ON A.WFL_RECIPIENT=C.ECM_USER_LOGIN "
						+ "WHERE A.WFL_WITM_SEQNO = "
						+ "(select min(wfl_witm_seqno) from dims_workitem where wfl_id = a.wfl_id and wfl_sender = a.wfl_sender and (WFL_WITM_SENT_STATUS IS NULL OR WFL_WITM_SENT_STATUS = 'ACTIVE'))"
						+ "AND A.WFL_SENDER=? ");
				hasArgs = true;
			} else if(searchType.equalsIgnoreCase("Archive")){
				sqlQuery.append(" A.WFL_WITM_ID,A.WFL_ID,B.WFL_SUBJECT,B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, "
						+ "C.ECM_USER_NAME AS SENDER_NAME,A.WFL_WITM_TYPE, "
						+ "A.WFL_STEP_NO, A.WFL_PARENT_WITM,A.WFL_INSTRUCTIONS,"
						+ "A.WFL_WITM_RECEIVEDON,A.WFL_WITM_DEADLINE,A.WFL_WITM_REMINDER_DATE,"
						+ "A.WFL_WITM_ROOT_SENDER,  A.WFL_WITM_STATUS,"
						+ "A.WFL_WITM_ACTION,A.WFL_WITM_ACTION_COMMENT,"
						+ "A.WFL_WITM_ACTION_BY,A.WFL_WITM_ACTION_ONBEHALF,"
						+ "A.WFL_WITM_SENDER_DEPT,A.WFL_WITM_RECEIVER_DEPT,"
						+ "A.WFL_WITM_SENDER_DIV,A.WFL_WITM_RECEIVER_DIV,A.WFL_WITM_SYS_STATUS "
						+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
						+ "INNER JOIN dbo.DIMS_WORKITEM_ATTACHMENT D ON A.WFL_WITM_ID = D.WFL_WITM_ID INNER JOIN "
						+ "dbo.FileNet_Docs E ON D.WFL_DOCUMENT_ID = E.object_id "
						+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN "
						+ "WHERE ((A.WFL_RECIPIENT=? AND A.WFL_WITM_STATUS = 'Archive') OR "
						+ "(A.WFL_SENDER=? AND A.WFL_WITM_SENT_STATUS = 'Archive')) AND A.WFL_WITM_TYPE='CC' ");
				hasArgs = true;
			}
				
			}else{
					if(searchType.equalsIgnoreCase("Inbox")){
				sqlQuery.append(" A.WFL_WITM_ID,A.WFL_ID,B.WFL_SUBJECT,B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, "
						+ "C.ECM_USER_NAME AS SENDER_NAME,A.WFL_WITM_TYPE, "
						+ "A.WFL_STEP_NO, A.WFL_PARENT_WITM,A.WFL_INSTRUCTIONS,"
						+ "A.WFL_WITM_RECEIVEDON,A.WFL_WITM_DEADLINE,A.WFL_WITM_REMINDER_DATE,"
						+ "A.WFL_WITM_ROOT_SENDER,  A.WFL_WITM_STATUS,"
						+ "A.WFL_WITM_ACTION,A.WFL_WITM_ACTION_COMMENT,"
						+ "A.WFL_WITM_ACTION_BY,A.WFL_WITM_ACTION_ONBEHALF,"
						+ "A.WFL_WITM_SENDER_DEPT,A.WFL_WITM_RECEIVER_DEPT,"
						+ "A.WFL_WITM_SENDER_DIV,A.WFL_WITM_RECEIVER_DIV,A.WFL_WITM_SYS_STATUS "
						+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
						+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN "
						+ "WHERE A.WFL_WITM_SYS_STATUS='ACTIVE' AND A.WFL_RECIPIENT=? AND A.WFL_WITM_STATUS != 'Archive' ");
				hasArgs = true;
			} 
			else if(searchType.equalsIgnoreCase("Sent")){
				sqlQuery.append(" A.WFL_WITM_ID,A.WFL_ID,B.WFL_SUBJECT,B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, "
						+ "C.ECM_USER_NAME AS SENDER_NAME,A.WFL_WITM_TYPE, "
						+ "A.WFL_STEP_NO, A.WFL_PARENT_WITM,A.WFL_INSTRUCTIONS,"
						+ "A.WFL_WITM_RECEIVEDON,A.WFL_WITM_DEADLINE,A.WFL_WITM_REMINDER_DATE,"
						+ "A.WFL_WITM_ROOT_SENDER,  A.WFL_WITM_STATUS,"
						+ "A.WFL_WITM_ACTION,A.WFL_WITM_ACTION_COMMENT,"
						+ "A.WFL_WITM_ACTION_BY,A.WFL_WITM_ACTION_ONBEHALF,"
						+ "A.WFL_WITM_SENDER_DEPT,A.WFL_WITM_RECEIVER_DEPT,"
						+ "A.WFL_WITM_SENDER_DIV,A.WFL_WITM_RECEIVER_DIV,A.WFL_WITM_SYS_STATUS "
						+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
						+ "JOIN ECM_EMPLOYEE C ON A.WFL_RECIPIENT=C.ECM_USER_LOGIN "
						+ "WHERE A.WFL_WITM_SEQNO = "
						+ "(select min(wfl_witm_seqno) from dims_workitem where wfl_id = a.wfl_id and wfl_sender = a.wfl_sender and (WFL_WITM_SENT_STATUS IS NULL OR WFL_WITM_SENT_STATUS = 'ACTIVE'))"
						+ "AND A.WFL_SENDER=? AND WFL_WITM_STATUS != 'Complete' ");
				hasArgs = true;
			} else if(searchType.equalsIgnoreCase("Archive")){
				sqlQuery.append(" A.WFL_WITM_ID,A.WFL_ID,B.WFL_SUBJECT,B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, "
						+ "C.ECM_USER_NAME AS SENDER_NAME,A.WFL_WITM_TYPE, "
						+ "A.WFL_STEP_NO, A.WFL_PARENT_WITM,A.WFL_INSTRUCTIONS,"
						+ "A.WFL_WITM_RECEIVEDON,A.WFL_WITM_DEADLINE,A.WFL_WITM_REMINDER_DATE,"
						+ "A.WFL_WITM_ROOT_SENDER,  A.WFL_WITM_STATUS,"
						+ "A.WFL_WITM_ACTION,A.WFL_WITM_ACTION_COMMENT,"
						+ "A.WFL_WITM_ACTION_BY,A.WFL_WITM_ACTION_ONBEHALF,"
						+ "A.WFL_WITM_SENDER_DEPT,A.WFL_WITM_RECEIVER_DEPT,"
						+ "A.WFL_WITM_SENDER_DIV,A.WFL_WITM_RECEIVER_DIV,A.WFL_WITM_SYS_STATUS "
						+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
						+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN "
						+ "WHERE ((A.WFL_RECIPIENT=? AND A.WFL_WITM_STATUS = 'Archive') OR "
						+ "(A.WFL_SENDER =? AND A.WFL_WITM_SENT_STATUS = 'Archive')) AND A.WFL_WITM_TYPE='CC' ");
				hasArgs = true;
			}
			
			}
			
			
			sqlWhereClause = buildSQL(workFlowSearchBean,sqlQuery);
			int nPage = workFlowSearchBean.getPageNo();
			int nPageSize = workFlowSearchBean.getPageSize();
			if(nPage <= 0)
				nPage = 1;
			if(nPageSize <= 0)
				nPageSize = 25;
			//sqlWhereClause = sqlWhereClause+" ORDER BY A.WFL_WITM_RECEIVEDON DESC";
			sqlWhereClause = sqlWhereClause+ ") as RESULTS where RowNum between " + (((nPage - 1) * nPageSize)+ 1) + 
						" and  " + nPage * nPageSize;
			System.out.println("Final query  ::::::::"+sqlWhereClause);
			st = conn.prepareStatement(sqlWhereClause);
			if(hasArgs) {
				st.setString(1, escapeString(workFlowSearchBean.getLoginUser()));
				if(searchType.equalsIgnoreCase("Archive"))
					st.setString(2, escapeString(workFlowSearchBean.getLoginUser()));
			}
			rs = st.executeQuery();

			while (rs.next()) {
				WorkItemDetails workItemDetails = new WorkItemDetails();
				int nTotalCount = rs.getInt("totalRecordsNumber");
				workItemDetails.setTotalCount(nTotalCount + "");
				workItemDetails.setWorkflowWorkItemID(rs.getString("WFL_WITM_ID"));
				workItemDetails.setWorkflowItemSubject(rs.getString("WFL_SUBJECT"));
				//workItemDetails.setWorkflowSender(rs.getString("WFL_SENDER"));
				String senderName = rs.getString("SENDER_NAME");
				workItemDetails.setWorkflowSenderName(senderName);
				workItemDetails.setWorkflowWorkItemType(rs.getString("WFL_WITM_TYPE"));
				workItemDetails.setWorkflowStepNo(rs.getInt("WFL_STEP_NO"));
				workItemDetails.setWorkflowItemDeadline(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_DEADLINE")));
				workItemDetails.setWorkflowItemStatus(rs.getString("WFL_WITM_STATUS"));
				workItemDetails.setWorkflowItemPriority(rs.getString("WFL_WITM_PRIORITY"));
				if(workItemDetails.getWorkflowItemPriority() == null)
					workItemDetails.setWorkflowItemPriority(rs.getString("WFL_PRIORITY"));
				workItemDetails.setWorkflowItemReceivedOn(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_RECEIVEDON")));
				workItemDetails.setWorkflowItemSysStatus(rs.getString("WFL_WITM_SYS_STATUS"));
				
				workItemDetailsList.add(workItemDetails);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}finally{
		 	rs.close();
		    st.close();
		    conn.close();
		 }
		logger.info("Exit Method : searchWorkFlow ");
		return workItemDetailsList;
	}

	
	

    private String buildSQL(WorkFlowSearchBean workFlowSearchBean, StringBuffer sqlQuery) throws Exception {
        logger.info("Started Method : buildSQL ");
        StringBuffer sb = sqlQuery;
        
        if(workFlowSearchBean.getReferenceNumber()!= null && !workFlowSearchBean.getReferenceNumber().trim().equals("") ){
            if(!workFlowSearchBean.getReferenceNumber().contains("###")){
            sqlQuery.append(" AND E.referenceno like N'%"+escapeString(workFlowSearchBean.getReferenceNumber())+"%'");
            }
            else{
                            String[] referenceNumber = workFlowSearchBean.getReferenceNumber().split("###");
                            String inUsers =" AND ( ";
                            for(int i=0; i < referenceNumber.length; i++){
                                inUsers = inUsers+"E.referenceno like N'%"+referenceNumber[i]+"%' OR ";
                            }   
                            inUsers = inUsers.substring(0, inUsers.length() - 3);
                              inUsers = inUsers+" ) ";
                            sqlQuery.append(inUsers);
            }
        }
        if(workFlowSearchBean.getDocumentId()!= null && !workFlowSearchBean.getDocumentId().trim().equals("") ){
            if(!workFlowSearchBean.getDocumentId().contains("###")){
            sqlQuery.append(" AND E.documentid = '"+escapeString(workFlowSearchBean.getDocumentId())+"'");
            }
            else{
                            String[] documentId = workFlowSearchBean.getDocumentId().split("###");
                            String inUsers ="'";
                              for(int i=0; i < documentId.length; i++){
                               	if(documentId.length-1 == i) {
                               		inUsers = inUsers+documentId[i]+"'";
                               	}
                               	else {
                               		inUsers = inUsers+documentId[i]+"','";
                               	}
                               } 
                              inUsers = " AND E.documentid IN("+inUsers+") ";
                              
                            sqlQuery.append(inUsers);
            }
        }
        
        
        if(workFlowSearchBean.getDateType()!= null && !workFlowSearchBean.getDateType().trim().equals("") ){
        				System.out.println("workFlowSearchBean.getDateType()  ::: "+workFlowSearchBean.getDateType());
        				String qry = "";
        				
                        if(escapeString(workFlowSearchBean.getDateType()).split("~")[0].equalsIgnoreCase("today") || 
                		escapeString(workFlowSearchBean.getDateType()).split("~")[0].equalsIgnoreCase("specific")) {
                        	qry = " AND ( A.WFL_WITM_RECEIVEDON > '"+DBUtil.convertStringtoDateWF(escapeString(workFlowSearchBean.getDateType()))+"' AND " +
                    			"A.WFL_WITM_RECEIVEDON < '"+DBUtil.convertStringtoSpecificDateWF(escapeString(workFlowSearchBean.getDateType()))+"') ";
            			} 
            			else if(escapeString(workFlowSearchBean.getDateType()).split("~")[0].equalsIgnoreCase("lastWeek") || 
                        		escapeString(workFlowSearchBean.getDateType()).split("~")[0].equalsIgnoreCase("lastMonth") ||
                        		escapeString(workFlowSearchBean.getDateType()).split("~")[0].equalsIgnoreCase("lastYear")) {
            				qry = " AND ( A.WFL_WITM_RECEIVEDON < '"+DBUtil.convertStringtoDateWF(escapeString(workFlowSearchBean.getDateType()))+"' AND " +
            						"A.WFL_WITM_RECEIVEDON > '"+DBUtil.convertStringtoSpecificDateWF(escapeString(workFlowSearchBean.getDateType()))+"') ";
            				 
            			}
            			else if(escapeString(workFlowSearchBean.getDateType()).split("~")[0].equalsIgnoreCase("before")) {
            				qry = " AND ( A.WFL_WITM_RECEIVEDON < '"+DBUtil.convertStringtoDateWF(escapeString(workFlowSearchBean.getDateType()))+"') ";
            				 
            			}
            			else if(escapeString(workFlowSearchBean.getDateType()).split("~")[0].equalsIgnoreCase("after")) {
            				qry = " AND ( A.WFL_WITM_RECEIVEDON > '"+DBUtil.convertStringtoDateWF(escapeString(workFlowSearchBean.getDateType()))+"') ";
            				 
            			}
            			else if(escapeString(workFlowSearchBean.getDateType()).split("~")[0].equalsIgnoreCase("range")) {
            				qry = " AND ( A.WFL_WITM_RECEIVEDON > '"+DBUtil.convertStringtoDateWF(escapeString(workFlowSearchBean.getDateType()))+"' AND " +
            							"A.WFL_WITM_RECEIVEDON < '"+DBUtil.convertStringtoDateRange(escapeString(workFlowSearchBean.getDateType()))+"') ";
            				 
            			}
            			
                        sqlQuery.append(qry);
        }
        
        if(workFlowSearchBean.getFilterBy()!= null && !workFlowSearchBean.getFilterBy().trim().equals("") ){
            if(workFlowSearchBean.getFilterBy().equalsIgnoreCase("ACTIVE")){
            	//sqlQuery.append(" AND A.WFL_WITM_SYS_STATUS = '"+escapeString(workFlowSearchBean.getFilterBy())+"' AND A.WFL_WITM_TYPE = 'TO'");
            	sqlQuery.append(" AND A.WFL_WITM_TYPE = 'TO'");
            }
            if(workFlowSearchBean.getFilterBy().equalsIgnoreCase("New")){
            	sqlQuery.append(" AND A.WFL_WITM_STATUS = '"+escapeString(workFlowSearchBean.getFilterBy())+"'");
            	
            }
			if(workFlowSearchBean.getFilterBy().equalsIgnoreCase("cc")){
				sqlQuery.append(" AND A.WFL_WITM_TYPE = '"+escapeString(workFlowSearchBean.getFilterBy())+"'");
				
			}
			if(workFlowSearchBean.getFilterBy().equalsIgnoreCase("Reply")){
				sqlQuery.append(" AND A.WFL_WITM_TYPE = '"+escapeString(workFlowSearchBean.getFilterBy())+"'");
			}
        }

        if(workFlowSearchBean.getFromUser()!= null && !workFlowSearchBean.getFromUser().trim().equals("") ){
                        if(!workFlowSearchBean.getFromUser().contains("###")){
                        sqlQuery.append(" AND A.WFL_SENDER = '"+escapeString(workFlowSearchBean.getFromUser())+"'");
        }
                        else{
                                        String[] from = workFlowSearchBean.getFromUser().split("###");
                                        String inUsers ="'";
                                        for(int i=0; i < from.length; i++){
                                         	if(from.length-1 == i) {
                                         		inUsers = inUsers+from[i]+"'";
                                         	}
                                         	else {
                                         		inUsers = inUsers+from[i]+"','";
                                         	}
                                         } 
                                        inUsers = " AND A.WFL_SENDER IN("+inUsers+") ";
                                        sqlQuery.append(inUsers);
                        }
        }

                    if(workFlowSearchBean.getToUser()!= null && !workFlowSearchBean.getToUser().trim().equals("") ){
                                    if(!workFlowSearchBean.getToUser().contains("###")){
                                    sqlQuery.append(" AND A.WFL_RECIPIENT = '"+escapeString(workFlowSearchBean.getToUser())+"'");
                    }
                                    else{
                                                    String to[] = workFlowSearchBean.getToUser().split("###");
                                                    String inUsers ="'";
                                                    for(int i=0; i < to.length; i++){
                                                     	if(to.length-1 == i) {
                                                     		inUsers = inUsers+to[i]+"'";
                                                     	}
                                                     	else {
                                                     		inUsers = inUsers+to[i]+"','";
                                                     	}
                                                     } 
                                                    inUsers = " AND A.WFL_RECIPIENT IN("+inUsers+") ";
                                                    sqlQuery.append(inUsers);
                                                    
                                    }
                    }

                    if(workFlowSearchBean.getSubject()!= null && !workFlowSearchBean.getSubject().trim().equals("") ){
                                    if(!workFlowSearchBean.getSubject().contains("###")){
                                    sqlQuery.append(" AND B.WFL_SUBJECT LIKE N'%"+escapeString(workFlowSearchBean.getSubject())+"%'");
                    }
                                    else{
                                                    String subject[] = workFlowSearchBean.getSubject().split("###");
                                                    String inUsers =" AND ( ";
                                                    for(int i=0; i < subject.length; i++){
                                                        inUsers = inUsers+"B.WFL_SUBJECT like N'%"+escapeString(subject[i])+"%' OR ";
                                                    }   
                                                    inUsers = inUsers.substring(0, inUsers.length() - 3);
                                                      inUsers = inUsers+" ) ";
                                                    sqlQuery.append(inUsers);
                                    }
                    }

                    if(workFlowSearchBean.getInstruction()!= null && !workFlowSearchBean.getInstruction().trim().equals("") ){
                                    sqlQuery.append(" AND A.WFL_INSTRUCTIONS = '"+escapeString(workFlowSearchBean.getInstruction())+"'");
                    }

                    if(workFlowSearchBean.getComments()!= null && !workFlowSearchBean.getComments().trim().equals("") ){
                                    if(!workFlowSearchBean.getComments().contains("###")){
                                    sqlQuery.append(" AND A.WFL_WITM_ACTION_COMMENT LIKE N'%"+escapeString(workFlowSearchBean.getComments())+"%'");
                    }
                                    else{
                                                    String comments[] = workFlowSearchBean.getComments().split("###");
                                                    String inUsers =" AND ( ";
                                                    for(int i=0; i < comments.length; i++){
                                                        inUsers = inUsers+"A.WFL_WITM_ACTION_COMMENT like N'%"+escapeString(comments[i])+"%' OR ";
                                                    }   
                                                    inUsers = inUsers.substring(0, inUsers.length() - 3);
                                                      inUsers = inUsers+" ) ";
                                                    sqlQuery.append(inUsers);
                                                    }
                                    }

                    logger.info("Exit Method : buildSQL ");
                    return sb.toString();
    }

	
	public String getWorkitemParentType(String witmID) {
		logger.info("Started Method : getWorkitemParentType ");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String parentType = "NONE";
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			
			String querySQL = "SELECT UPPER(WFL_WITM_TYPE) FROM DIMS_WORKITEM "
					+ "WHERE WFL_WITM_ID = (SELECT WFL_PARENT_WITM FROM DIMS_WORKITEM WHERE WFL_WITM_ID = ?)";
			ps = conn.prepareStatement(querySQL);
			ps.setString(1, escapeString(witmID));
			
			rs = ps.executeQuery();

			if((rs != null) && (rs.next())) {
				parentType = rs.getString(1);
				if(parentType != null)
					parentType = parentType.trim();
				if((parentType == null) || (parentType.length() <= 0))
					parentType = "NONE";
			}
		 	rs.close();
		    ps.close();
		    conn.close();
		} catch(Exception e){
		    //Handle errors for Class.forName
		    e.printStackTrace();
		    logger.error(e.getMessage(), e);
		 }
		logger.info("Exit Method : getWorkitemParentType ");
		return parentType;
	}
	
	public ArrayList<WorkflowRecipient> getSentItemRecipients(String witmID) throws Exception {
		logger.info("Started Method : getWorkflowRecipients ");
		Connection conn = null;
		CallableStatement callableStatement = null;
		ResultSet rs = null;
		ArrayList<WorkflowRecipient> wrList= new ArrayList<WorkflowRecipient>();
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			
			String getDBUSERCursorSql = "{call GET_SENT_ITEM_RECIPIENTS(?)}";
			callableStatement = conn.prepareCall(getDBUSERCursorSql);
			callableStatement.setString(1, escapeString(witmID));
			
			rs = callableStatement.executeQuery();

			while (rs.next()) {
				WorkflowRecipient wr = new WorkflowRecipient();
				wr.setWorkflowRecipient(rs.getString("WFL_RECIPIENT"));
				wr.setWorkflowRecipientName(rs.getString("ECM_USER_NAME"));
				wr.setWorkflowWorkItemType(rs.getString("WFL_WITM_TYPE"));
				wr.setWorkflowWorkItemStatus(rs.getString("WFL_WITM_STATUS"));
				wrList.add(wr);
			}
		} catch(Exception e){
		    //Handle errors for Class.forName
		    e.printStackTrace();
		    logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		    callableStatement.close();
		    conn.close();
		 }
		logger.info("Exit Method : getSentItemRecipients ");
		return wrList;
	}
	
	public ArrayList<String> getWorkitemActions(String witmID, String queue, String user) throws Exception {
		logger.info("Started Method : getWorkitemActions ");
		Connection conn = null;
		CallableStatement callableStatement = null;
		ResultSet rs = null;
		ArrayList<String> aList = new ArrayList<String>();
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			
			String qName = "INBOX";
			if(queue == null)
				qName = "INBOX";
			else if(!queue.trim().toUpperCase().equalsIgnoreCase("INBOX") &&
					!queue.trim().toUpperCase().equalsIgnoreCase("SENT") &&
					!queue.trim().toUpperCase().equalsIgnoreCase("ARCHIVE"))
				qName = "INBOX";
			else
				qName = queue.trim().toUpperCase();
			
			String wiList [] = witmID.split(",");
			for(String wiID: wiList) {
				if(wiID.length() <= 0)
					continue;
				ArrayList<String> curList = new ArrayList<String>();
				String getDBUSERCursorSql = "{call GET_WORKITEM_ACTIONS(?,?,?)}";
				callableStatement = conn.prepareCall(getDBUSERCursorSql);
				callableStatement.setString(1, escapeString(wiID));
				callableStatement.setString(2, escapeString(qName));
				callableStatement.setString(3, escapeString(user));
				
				rs = callableStatement.executeQuery();
	
				if(rs != null && rs.next()) {
					String actions = rs.getString(1);
					String [] actionList = actions.split(";");
					for(String action:actionList)
						if((action != null) && (action.length() > 0))
							curList.add(action);
				}
				rs.close();
			    callableStatement.close();
			    aList = intersectLists(aList, curList);
			}
		    conn.close();
		    
		    if(wiList.length > 1) {
		    	ArrayList<String> newAList = new ArrayList<String>();
		    	for(String action: aList) {
		    		if(!(action.equalsIgnoreCase("Add Users") || action.equalsIgnoreCase("Launch") || action.equalsIgnoreCase("Reassign"))) {
		    			newAList.add(action);
		    		}
		    	}
		    	aList = newAList;
		    }
		} catch(Exception e){
		    //Handle errors for Class.forName
		 }
		logger.info("Exit Method : getWorkitemActions ");
		return aList;
	}
	
	private ArrayList<String> intersectLists(ArrayList<String> aList, ArrayList<String> curList) {
		if(aList.size() <= 0)
			return curList;
		ArrayList<String> returnList = new ArrayList<String>();
		for(String action:aList) {
			for(String curAction: curList) {
				if(curAction.equalsIgnoreCase(action)) {
					returnList.add(curAction);
					break;
				}
			}
		}
		return returnList;
	}
	
	public String getSentItemSender(String witmID) throws Exception {
		logger.info("Started Method : getWorkflowRecipients ");
		Connection conn = null;
		CallableStatement callableStatement = null;
		String returnValue = "";
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			
			String getDBUSERCursorSql = "{call GET_SENT_ITEM_SENDER(?,?)}";
			callableStatement = conn.prepareCall(getDBUSERCursorSql);
			callableStatement.setString(1, escapeString(witmID));
			callableStatement.registerOutParameter(2, java.sql.Types.NVARCHAR);
			
			callableStatement.executeUpdate();

			returnValue = callableStatement.getString(2);

			
		} catch(Exception e){
		    //Handle errors for Class.forName
		    e.printStackTrace();
		    logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		    callableStatement.close();
		    conn.close();
		 }
		logger.info("Exit Method : getSentItemRecipients ");
		return returnValue;
	}
	
	public ArrayList<String> getWorkflowInstructions() throws Exception {
		logger.info("Started Method : getWorkflowInstructions ");
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String sqlQuery= null;
		String instruction = null;
		ArrayList<String> instructionList= new ArrayList<String>();
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
			st = DBUtil.getStatement(conn);
			
			sqlQuery = "SELECT WFL_INSTRUCTION"
					+ " FROM DIMS_WORKFLOW_INSTRUCTIONS";

			rs = st.executeQuery(sqlQuery);

			while (rs.next()) {
				instruction = rs.getString("WFL_INSTRUCTION");
				instructionList.add(instruction);
			}
		} catch(Exception e){
		    //Handle errors for Class.forName
		    e.printStackTrace();
		    logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		    st.close();
		    conn.close();
		 }
		logger.info("Exit Method : getWorkflowInstructions ");
		return instructionList;
	}

	public ArrayList<EmployeeDirectorate> getDirectorate() throws Exception {
		logger.info("Started Method : getDirectorate ");
		ArrayList<EmployeeDirectorate> employeeDirectorateList = new ArrayList<EmployeeDirectorate>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sqlQuery= null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
			stmt = DBUtil.getStatement(conn);

			sqlQuery = "SELECT distinct(ECM_EMPLOYEE.ECM_DIR_CODE), ECM_EMPLOYEE.ECM_DIRECTORATE FROM ECM_EMPLOYEE WHERE ECM_EMPLOYEE.ECM_DIR_CODE IS NOT NULL";

			rs = stmt.executeQuery(sqlQuery);

			while (rs.next()) {
				EmployeeDirectorate employeeDirectorate = new EmployeeDirectorate();
				employeeDirectorate.setEmployeeDirectorate(rs.getString("ECM_DIRECTORATE"));
				employeeDirectorate.setEmployeeDirectorateCode(rs.getString("ECM_DIR_CODE"));
				employeeDirectorateList.add(employeeDirectorate);
			}

		} catch(Exception e){
		    e.printStackTrace();
		    logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		    stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : getDirectorate ");
		return employeeDirectorateList;
	
	}

	public ArrayList<WorkItemDetails> quickSearchWorkFlow(String subject, String user_login) throws Exception {
		logger.info("Started Method : getDirectorate Method parameter subject : "+subject+" user_login"+user_login);
		ArrayList<WorkItemDetails> workItemDetailsList = new ArrayList<WorkItemDetails>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "SELECT A.WFL_WITM_ID,B.WFL_SUBJECT,A.WFL_SENDER,"
					+ "C.ECM_USER_NAME AS SENDER_NAME, A.WFL_WITM_TYPE, "
					+ "A.WFL_STEP_NO, A.WFL_WITM_DEADLINE,  A.WFL_WITM_STATUS, "
					+ "B.WFL_PRIORITY, A.WFL_WITM_PRIORITY, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_SYS_STATUS "
					+ "FROM dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID=B.WFL_ID "
					+ "JOIN ECM_EMPLOYEE C ON A.WFL_SENDER=C.ECM_USER_LOGIN "
					+ "WHERE C.ECM_USER_NAME LIKE ? AND (A.WFL_RECIPIENT = ? OR A.WFL_SENDER = ?)";

			stmt = conn.prepareStatement(sqlQuery);			
			stmt.setString(1, "%" + escapeString(subject) + "%");
			stmt.setString(2, escapeString(user_login));
			stmt.setString(3, escapeString(user_login));
			rs = stmt.executeQuery();

			while (rs.next()) {
				WorkItemDetails workItemDetails = new WorkItemDetails();
				workItemDetails.setWorkflowWorkItemID(rs.getString("WFL_WITM_ID"));
				workItemDetails.setWorkflowItemSubject(rs.getString("WFL_SUBJECT"));
				workItemDetails.setWorkflowSender(rs.getString("WFL_SENDER"));
				String senderName = rs.getString("SENDER_NAME");
				workItemDetails.setWorkflowSenderName(senderName);
				workItemDetails.setWorkflowWorkItemType(rs.getString("WFL_WITM_TYPE"));
				workItemDetails.setWorkflowStepNo(rs.getInt("WFL_STEP_NO"));
				workItemDetails.setWorkflowItemDeadline(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_DEADLINE")));
				workItemDetails.setWorkflowItemStatus(rs.getString("WFL_WITM_STATUS"));
				workItemDetails.setWorkflowItemPriority(rs.getString("WFL_WITM_PRIORITY"));
				if(workItemDetails.getWorkflowItemPriority() == null)
					workItemDetails.setWorkflowItemPriority(rs.getString("WFL_PRIORITY"));
				workItemDetails.setWorkflowItemReceivedOn(DBUtil.formatDateForUI(rs.getTimestamp("WFL_WITM_RECEIVEDON")));
				workItemDetails.setWorkflowItemSysStatus(rs.getString("WFL_WITM_SYS_STATUS"));
				
				workItemDetailsList.add(workItemDetails);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getDirectorate");
		return workItemDetailsList;
	
	}

	
	public ArrayList<String> getDelegatedUsers(String user_login) throws Exception {

		logger.info("Started Method : getDelegatedUsers Method parameter user_login"+user_login);
		ArrayList<String> delegatedUserList = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "SELECT WFL_ABSENTEE_LOGIN,WFL_DELEGATION_ACCESSFORREPORTS,(SELECT ECM_JOB_TITLE FROM ECM_EMPLOYEE where ECM_USER_LOGIN = WFL_ABSENTEE_LOGIN) As Emp_job_Title,(SELECT ECM_ISHAVEREPORTS FROM ECM_EMPLOYEE where ECM_USER_LOGIN = WFL_ABSENTEE_LOGIN) As Emp_isHaveReports,(SELECT ECM_USER_NAME FROM ECM_EMPLOYEE WHERE ECM_USER_LOGIN = WFL_ABSENTEE_LOGIN) As Full_Name"
					+ " FROM DIMS_DELEGATION "
					+ " WHERE DIMS_DELEGATION.WFL_DELEGATE_TO > (getDate()-1) "
					+ " AND DIMS_DELEGATION.WFL_DELEGATE_LOGIN in( select ECM_USER_LOGIN from ecm_Employee where ECM_USER_LOGIN = ?) "
							+ "AND WFL_DELEGATION_STATUS ='ACTIVE'";

			stmt = conn.prepareStatement(sqlQuery);			
			stmt.setString(1, escapeString(user_login));
			rs = stmt.executeQuery();

			while (rs.next()) {
				//
				StringBuffer delegateUserDetail = new StringBuffer();
				String userLogin = rs.getString("WFL_ABSENTEE_LOGIN");
				String userHaveAccessForReports = rs.getString("WFL_DELEGATION_ACCESSFORREPORTS");
				String userFullName = rs.getString("Full_Name");
				String userJobTitle = rs.getString("Emp_job_Title");
				String userIsHaveReports = rs.getString("Emp_isHaveReports");
				delegateUserDetail.append(userLogin).append("!~").append(userFullName).append("!~").append(userJobTitle).append("!~").append(userIsHaveReports).append("!~").append(userHaveAccessForReports);
				
				delegatedUserList.add(delegateUserDetail.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getDelegatedUsers");
		return delegatedUserList;
	}

	

	public ArrayList<WorkFlowDetails> getDocumentHistory(String docId) throws Exception {
		logger.info("Started Method : getDocumentHistory Method parameter docId"+docId);
		ArrayList<WorkFlowDetails> workFlowDetailsList = new ArrayList<WorkFlowDetails>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);

/*			sqlQuery = "SELECT A.WFL_ID, B.WFL_SUBJECT, B.WFL_LAUNCHEDBY,B.WFL_BEGINDATE,"
					+ "B.WFL_DEADLINE FROM DIMS_WORKFLOW_ATTACHMENT A "
					+ "JOIN DIMS_WORKFLOW B on A.WFL_ID = B.WFL_ID	"
					+ "WHERE A.WFL_DOCUMENT_ID = ?";*/
			
			sqlQuery = "SELECT A.WFL_ID, B.WFL_SUBJECT,(Select ECM_USER_NAME from [DIMS].[dbo].ECM_EMPLOYEE where ECM_USER_LOGIN = B.WFL_LAUNCHEDBY) AS WFL_LAUNCHEDBY,B.WFL_BEGINDATE,"
					+ "B.WFL_DEADLINE FROM DIMS_WORKFLOW_ATTACHMENT A "
					+ "JOIN DIMS_WORKFLOW B on A.WFL_ID = B.WFL_ID	"
					+ "WHERE A.WFL_DOCUMENT_ID = ?";
			
			
					//+ "WHERE A.WFL_DOCUMENT_ID = '"+docId+"' and A.WFL_ATTACHMENT_TYPE ='Primary'";
			stmt = conn.prepareStatement(sqlQuery);			
			stmt.setString(1, escapeString(docId));
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				WorkFlowDetails wfd = new WorkFlowDetails();
				wfd.setWorkflowID(rs.getString("WFL_ID"));
				wfd.setWorkflowSubject(rs.getString("WFL_SUBJECT"));
				wfd.setWorkflowLaunchedBy(rs.getString("WFL_LAUNCHEDBY"));
				wfd.setWorkflowBeginDate(DBUtil.formatDateForUI(rs.getTimestamp("WFL_BEGINDATE")));
				wfd.setWorkflowDeadline(DBUtil.formatDateForUI(rs.getTimestamp("WFL_DEADLINE")));
				workFlowDetailsList.add(wfd);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getDocumentHistory");
		return workFlowDetailsList;
	
	}

	public ArrayList<Employee> getEmailIds(String email) throws Exception {
		logger.info("Started Method : getEmailIds Method parameter email"+email);
		ArrayList<Employee> emailIdsList = new ArrayList<Employee>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "SELECT ECM_USER_EMAIL,ECM_USER_NAME,ECM_USER_LOGIN FROM ECM_EMPLOYEE WHERE ECM_USER_NAME LIKE ? OR ECM_USER_LOGIN LIKE ? OR ECM_DESIGNATION LIKE ?";

			stmt = conn.prepareStatement(sqlQuery);			
			stmt.setString(1, "%" + escapeString(email) + "%");
			stmt.setString(2, "%" + escapeString(email) + "%");
			stmt.setString(3, "%" + escapeString(email) + "%");
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				Employee emp = new Employee();
				emp.setEmployeeEmail(rs.getString("ECM_USER_EMAIL"));
				emp.setEmployeeName(rs.getString("ECM_USER_NAME"));
				emp.setEmployeeLogin(rs.getString("ECM_USER_LOGIN"));
				emailIdsList.add(emp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getEmailIds");
		return emailIdsList;
	}

	public String createWorkflowFolder(String folderType, String folderName, String user_name) throws Exception {
		logger.info("Started Method : createWorkflowFolder Method parameter folderName : "+folderName+" folderType : "+folderType);
		Connection conn = null;
		PreparedStatement preStmt = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		String userName = null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			

			sqlQuery = "INSERT INTO DIMS_WORKFLOW_FOLDERS"
						+"(FOLDER_TYPE,LOGIN_USER,FOLDER_NAME) "
						+ "VALUES(?,?,?)";
			
			preStmt = DBUtil.getPreparedStatement(conn, sqlQuery);
			preStmt.setString(1,escapeString(folderType));
			preStmt.setString(2,escapeString(user_name));
			preStmt.setString(3,escapeString(folderName));
			int rowCount = preStmt.executeUpdate();
			
			preStmt.close();
			if(rowCount>0){
				
				sqlQuery = "SELECT ECM_USER_LOGIN FROM ECM_EMPLOYEE WHERE ECM_USER_NAME =?";
				stmt = conn.prepareStatement(sqlQuery);			
				stmt.setString(1, escapeString(folderName));
				rs = stmt.executeQuery();
				
				while(rs.next()){
					userName = rs.getString("ECM_USER_LOGIN");
				}
				stmt.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			preStmt.close();
			conn.close();
		}
		return userName;
	}

	public ArrayList<String> getWorkflowFolder(String type, String user_name) throws Exception {
		logger.info("Started Method : getWorkflowFolder Method parameter type : "+type+" user_name : "+user_name);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		ArrayList<String> folderList = new ArrayList<String>();
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			
			sqlQuery = "SELECT A.FOLDER_ID,A.FOLDER_TYPE,A.FOLDER_NAME, B.ECM_USER_NAME,A.LOGIN_USER "
					+ "FROM DIMS_WORKFLOW_FOLDERS A JOIN ECM_EMPLOYEE B "
					+ "ON A.FOLDER_NAME = B.ECM_USER_LOGIN WHERE A.LOGIN_USER = ? AND A.FOLDER_TYPE = ?";

			stmt = conn.prepareStatement(sqlQuery);			
			stmt.setString(1, escapeString(user_name));
			stmt.setString(2, escapeString(type));
			rs = stmt.executeQuery();
			
			while(rs.next()){
				StringBuffer folderDetail = new StringBuffer();
				String folderId = rs.getString("FOLDER_ID");
				String folderType = rs.getString("FOLDER_TYPE");
				String folderName = rs.getString("FOLDER_NAME");
				String folderFullName = rs.getString("ECM_USER_NAME");
				folderDetail.append(folderId).append("!~").append(folderType).append("!~").append(folderName).append("!~").append(folderFullName);
				
				folderList.add(folderDetail.toString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			stmt.close();
			rs.close();
			conn.close();
		}
		return folderList;
	}

	public String createUserList(UserList userList) throws Exception {
		//logger.info("Started Method : getWorkflowFolder Method parameter type : "+type+" user_name : "+user_name);
		Connection conn = null;
		PreparedStatement preStmt = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		int userListID = 0;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			String sql = "SELECT COUNT(*) AS count from DIMS_USER_LIST where WFL_USER_LOGIN = '"+escapeString(userList.getLoginUser())+"'";
			Statement st = conn.createStatement();
			ResultSet rs1 = st.executeQuery(sql);
			rs1.next();
			int x = rs1.getInt("count");
			
			if(x>0){
				sqlQuery = "INSERT INTO DIMS_USER_LIST"
						+"(WFL_LIST_NAME,WFL_USER_LOGIN,WFL_LIST_TYPE,WFL_LIST_PRIORITY) "
						+ "VALUES(?,?,?,((SELECT MAX(WFL_LIST_PRIORITY) from DIMS_USER_LIST where WFL_USER_LOGIN = ?)+1))";
			
				preStmt = DBUtil.getPreparedStatement(conn, sqlQuery);
				preStmt.setString(1,escapeString(userList.getListName()));
				preStmt.setString(2,escapeString(userList.getLoginUser()));
				preStmt.setString(3,escapeString(userList.getListType()));
				preStmt.setString(4,escapeString(userList.getLoginUser()));
				int rowCount = preStmt.executeUpdate();
				preStmt.close();
				if(rowCount>0){
					
					sqlQuery = "SELECT WFL_LIST_ID FROM DIMS_USER_LIST WHERE WFL_LIST_NAME=?";
					stmt = conn.prepareStatement(sqlQuery);			
					stmt.setString(1, escapeString(userList.getListName()));
					rs = stmt.executeQuery();
					
					while(rs.next()){
						userListID = rs.getInt("WFL_LIST_ID");
					}
					stmt.close();
					rs.close();
					
					ArrayList<String> memberList = userList.getListMember();
					for (int i = 0; i < memberList.size(); i++) {
						
						sqlQuery = "INSERT INTO DIMS_USER_LIST_DETAIL"
									+"(WFL_LIST_ID,WFL_USER_LOGIN) "
									+ "VALUES(?,?)";
						preStmt = DBUtil.getPreparedStatement(conn, sqlQuery);
						preStmt.setInt(1,userListID);
						preStmt.setString(2,escapeString(memberList.get(i)));
						int rowCreated = preStmt.executeUpdate();
						if(rowCreated>0){
							preStmt.close();
						}
					}
				}
				preStmt.close();
			}else{
				sqlQuery = "INSERT INTO DIMS_USER_LIST"
						+"(WFL_LIST_NAME,WFL_USER_LOGIN,WFL_LIST_TYPE,WFL_LIST_PRIORITY) "
						+ "VALUES(?,?,?,?)";
			
				preStmt = DBUtil.getPreparedStatement(conn, sqlQuery);
				preStmt.setString(1,escapeString(userList.getListName()));
				preStmt.setString(2,escapeString(userList.getLoginUser()));
				preStmt.setString(3,escapeString(userList.getListType()));
				preStmt.setString(4,"1");
				int rowCount = preStmt.executeUpdate();
				preStmt.close();
				if(rowCount>0){
					
					sqlQuery = "SELECT WFL_LIST_ID FROM DIMS_USER_LIST WHERE WFL_LIST_NAME=?";
					stmt = conn.prepareStatement(sqlQuery);			
					stmt.setString(1, escapeString(userList.getListName()));
					rs = stmt.executeQuery();
					
					while(rs.next()){
						userListID = rs.getInt("WFL_LIST_ID");
					}
					stmt.close();
					rs.close();
					
					ArrayList<String> memberList = userList.getListMember();
					for (int i = 0; i < memberList.size(); i++) {
						
						sqlQuery = "INSERT INTO DIMS_USER_LIST_DETAIL"
									+"(WFL_LIST_ID,WFL_USER_LOGIN) "
									+ "VALUES(?,?)";
						preStmt = DBUtil.getPreparedStatement(conn, sqlQuery);
						preStmt.setInt(1,userListID);
						preStmt.setString(2,escapeString(memberList.get(i)));
						int rowCreated = preStmt.executeUpdate();
						if(rowCreated>0){
							preStmt.close();
						}
					}
				}
				preStmt.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			stmt.close();
			rs.close();
			conn.close();
		}
		return Integer.toString(userListID) ;
	}

	public ArrayList<UserList> getUserList(String user_name, String type) throws Exception {
		logger.info("Started Method : getWorkflowFolder Method parameter type : "+type+" user_name : "+user_name);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		ArrayList<UserList> uList = new ArrayList<UserList>();
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			
			sqlQuery = "SELECT WFL_LIST_NAME,WFL_LIST_ID,WFL_LIST_PRIORITY "
					+ "FROM DIMS_USER_LIST "
					+ "WHERE WFL_USER_LOGIN = ? AND WFL_LIST_TYPE = ? ORDER BY [WFL_LIST_PRIORITY] ASC";
			
			stmt = conn.prepareStatement(sqlQuery);			
			stmt.setString(1, escapeString(user_name));
			stmt.setString(2, escapeString(type));
			rs = stmt.executeQuery();

			while(rs.next()){
				UserList userList = new UserList();
				userList.setListId(rs.getString("WFL_LIST_ID"));
				userList.setListName(rs.getString("WFL_LIST_NAME"));
				userList.setListPriority(rs.getString("WFL_LIST_PRIORITY"));
				uList.add(userList);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			stmt.close();
			rs.close();
			conn.close();
		}
		return uList;
	}

	public ArrayList<Employee> getUserListMembers(String listId) throws Exception {
		//logger.info("Started Method : getWorkflowFolder Method parameter type : "+type+" user_name : "+user_name);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		ArrayList<Employee> empList = new ArrayList<Employee>();
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			
			sqlQuery = "SELECT A.WFL_USER_LOGIN,B.ECM_USER_NAME,B.ECM_USER_EMAIL,"
					+ "B.ECM_DESIGNATION,B.ECM_USER_TITLE FROM DIMS_USER_LIST_DETAIL A "
					+ "JOIN ECM_EMPLOYEE B ON A.WFL_USER_LOGIN = B.ECM_USER_LOGIN "
					+ "WHERE A.WFL_LIST_ID =?";
			
			stmt = conn.prepareStatement(sqlQuery);			
			stmt.setString(1, escapeString(listId));
			rs = stmt.executeQuery();

			while(rs.next()){
				Employee emp = new Employee();
				emp.setEmployeeLogin(rs.getString("WFL_USER_LOGIN"));
				emp.setEmployeeName(rs.getString("ECM_USER_NAME"));
				emp.setEmployeeDesignation(rs.getString("ECM_USER_TITLE"));
				emp.setEmployeeEmail(rs.getString("ECM_USER_EMAIL"));
				empList.add(emp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			stmt.close();
			rs.close();
			conn.close();
		}
		return empList;
	}

	public String modifyUserList(UserList userList) throws Exception {
		//logger.info("Started Method : getWorkflowFolder Method parameter type : "+type+" user_name : "+user_name);
		Connection conn = null;
		PreparedStatement stmt = null;
		String sqlQuery = null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);

			String sql = "SELECT WFL_LIST_PRIORITY from DIMS_USER_LIST where WFL_LIST_ID = "+userList.getListId()+" AND WFL_USER_LOGIN = '"+escapeString(userList.getLoginUser())+"'";
			Statement st = conn.createStatement();
			ResultSet rs1 = st.executeQuery(sql);
			rs1.next();
			String previousPriority = rs1.getString("WFL_LIST_PRIORITY");
			sqlQuery = "DELETE A FROM DIMS_USER_LIST A JOIN DIMS_USER_LIST_DETAIL B ON B.WFL_LIST_ID = A.WFL_LIST_ID WHERE A.WFL_LIST_ID =?";
			stmt = conn.prepareStatement(sqlQuery);			
			stmt.setString(1, escapeString(userList.getListId()));
			int count = stmt.executeUpdate();
			stmt.close();
			if(count>0){
				createUserListForModify(userList,previousPriority);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			stmt.close();
			conn.close();
		}
		return "Modified the userList";
	}
	

	public String createUserListForModify(UserList userList,String previousPriority) throws Exception {
		//logger.info("Started Method : getWorkflowFolder Method parameter type : "+type+" user_name : "+user_name);
		Connection conn = null;
		PreparedStatement preStmt = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		int userListID = 0;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			
			
			
				sqlQuery = "INSERT INTO DIMS_USER_LIST"
					+"(WFL_LIST_NAME,WFL_USER_LOGIN,WFL_LIST_TYPE,WFL_LIST_PRIORITY) "
					+ "VALUES(?,?,?,?)";
			
				preStmt = DBUtil.getPreparedStatement(conn, sqlQuery);
				preStmt.setString(1,escapeString(userList.getListName()));
				preStmt.setString(2,escapeString(userList.getLoginUser()));
				preStmt.setString(3,escapeString(userList.getListType()));
				preStmt.setString(4,previousPriority);
				int rowCount = preStmt.executeUpdate();
				preStmt.close();
				if(rowCount>0){
					
					sqlQuery = "SELECT WFL_LIST_ID FROM DIMS_USER_LIST WHERE WFL_LIST_NAME=?";
					stmt = conn.prepareStatement(sqlQuery);			
					stmt.setString(1, escapeString(userList.getListName()));
					rs = stmt.executeQuery();
					
					while(rs.next()){
						userListID = rs.getInt("WFL_LIST_ID");
					}
					stmt.close();
					rs.close();
					
					ArrayList<String> memberList = userList.getListMember();
					for (int i = 0; i < memberList.size(); i++) {
						
						sqlQuery = "INSERT INTO DIMS_USER_LIST_DETAIL"
									+"(WFL_LIST_ID,WFL_USER_LOGIN) "
									+ "VALUES(?,?)";
						preStmt = DBUtil.getPreparedStatement(conn, sqlQuery);
						preStmt.setInt(1,userListID);
						preStmt.setString(2,escapeString(memberList.get(i)));
						int rowCreated = preStmt.executeUpdate();
						if(rowCreated>0){
							preStmt.close();
						}
					}
				}
				preStmt.close();
				
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			stmt.close();
			rs.close();
			conn.close();
		}
		return Integer.toString(userListID) ;
	}
	
	public String modifyUserListPriority(String source, String target) throws Exception {
		//logger.info("Started Method : getWorkflowFolder Method parameter type : "+type+" user_name : "+user_name);
		Connection conn = null;
		PreparedStatement stmt = null;
		String sqlQuery = null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "UPDATE DIMS_USER_LIST SET WFL_LIST_PRIORITY = ( SELECT SUM(WFL_LIST_PRIORITY) FROM"
					+ " DIMS_USER_LIST WHERE WFL_LIST_ID IN (?,?) ) - WFL_LIST_PRIORITY WHERE WFL_LIST_ID IN (?,?)";
			stmt = conn.prepareStatement(sqlQuery);			
			stmt.setString(1,source);
			stmt.setString(2,target);
			stmt.setString(3,source);
			stmt.setString(4,target);
			int count = stmt.executeUpdate();
			stmt.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			stmt.close();
			conn.close();
		}
		return "swaped userList";
	}

	public String deleteUserList(String listId) throws Exception {

		//logger.info("Started Method : getWorkflowFolder Method parameter type : "+type+" user_name : "+user_name);
		Connection conn = null;
		PreparedStatement stmt = null;
		String sqlQuery = null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
 
			//sqlQuery = "DELETE A FROM DIMS_USER_LIST A JOIN DIMS_USER_LIST_DETAIL B ON B.WFL_LIST_ID = A.WFL_LIST_ID WHERE A.WFL_LIST_ID ='"+listId+"'";
			sqlQuery ="Delete from DIMS_USER_LIST where WFL_LIST_ID = ?; Delete from DIMS_USER_LIST_DETAIL where WFL_LIST_ID = ?";

			stmt = conn.prepareStatement(sqlQuery);			
			stmt.setString(1, escapeString(listId));
			stmt.setString(2, escapeString(listId));
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			stmt.close();
			conn.close();
		}
		return "Deleted the userList";
	}

	public String addDelegateUsers(DelegateBean delegateBean) throws Exception {
		//logger.info("Started Method : getWorkflowFolder Method parameter type : "+type+" user_name : "+user_name);
		Connection conn = null;
		PreparedStatement st = null;
		String sqlQuery = null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			
			sqlQuery =  "INSERT INTO DIMS_DELEGATION"
					+"(WFL_DELEGATE_FROM,WFL_DELEGATE_TO,WFL_ABSENTEE_LOGIN,WFL_DELEGATE_LOGIN,"
					+ "WFL_DELEGATION_STATUS) "
					+ "VALUES(?,?,?,?,?)";
			st = DBUtil.getPreparedStatement(conn, sqlQuery);
			st.setTimestamp(1, DBUtil.convertStringtoDate(delegateBean.getDelegateFrom()));
			st.setTimestamp(2, DBUtil.convertStringtoDate(delegateBean.getDelegateTo()));
			st.setString(3, escapeString(delegateBean.getAbsentLogin()));
			st.setString(4, escapeString(delegateBean.getDelegateLogin()));
			st.setString(5, escapeString(delegateBean.getDelegateStatus()));
			st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			st.close();
			conn.close();
		}
		return "Delegated the user";
	}

	public String modifyDelegateUsers(DelegateBean delegateBean) throws Exception {
		//logger.info("Started Method : getWorkflowFolder Method parameter type : "+type+" user_name : "+user_name);
		Connection conn = null;
		PreparedStatement stmt = null;
		String sqlQuery = null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			
			sqlQuery = "UPDATE DIMS_DELEGATION SET"
					   +" WFL_DELEGATION_STATUS = ?"
					   +" WHERE WFL_DELEGATION_ID = ?";

			stmt = conn.prepareStatement(sqlQuery);			
			stmt.setString(1, escapeString(delegateBean.getDelegateStatus()));
			stmt.setString(2, escapeString(delegateBean.getDelegationId()));
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			stmt.close();
			conn.close();
		}
		return "modified Delegated the user";
	}

	public String deleteDelegateUsers(String delegationId) throws Exception {
		//logger.info("Started Method : getWorkflowFolder Method parameter type : "+type+" user_name : "+user_name);
		Connection conn = null;
		PreparedStatement stmt = null;
		String sqlQuery = null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			
			sqlQuery = "UPDATE DIMS_DELEGATION SET WFL_DELEGATION_STATUS ='INACTIVE' WHERE WFL_DELEGATION_ID=?";
			
			stmt = conn.prepareStatement(sqlQuery);			
			stmt.setString(1, escapeString(delegationId));
			stmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			stmt.close();
			conn.close();
		}
		return "deleted Delegated the user";
	}

	public ArrayList<DelegateBean> getDelegatedUserList(String user_login) throws Exception {
		logger.info("Started Method : getDelegatedUsers Method parameter user_login"+user_login);
		ArrayList<DelegateBean> delegatedUserList = new ArrayList<DelegateBean>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "SELECT WFL_ABSENTEE_LOGIN,WFL_DELEGATE_LOGIN,WFL_DELEGATION_STATUS,WFL_DELEGATE_TO,WFL_DELEGATE_FROM,WFL_DELEGATION_ID "
					+ "FROM DIMS_DELEGATION "
					+ "WHERE DIMS_DELEGATION.WFL_ABSENTEE_LOGIN = ?"
							+ "AND WFL_DELEGATION_STATUS ='ACTIVE'";

			stmt = conn.prepareStatement(sqlQuery);			
			stmt.setString(1, escapeString(user_login));
			rs = stmt.executeQuery();

			while (rs.next()) {
				DelegateBean delegateBean = new DelegateBean();
				delegateBean.setAbsentLogin(rs.getString("WFL_ABSENTEE_LOGIN"));
				delegateBean.setDelegateFrom(rs.getString("WFL_DELEGATE_FROM"));
				delegateBean.setDelegateLogin(rs.getString("WFL_DELEGATE_LOGIN"));
				delegateBean.setDelegateStatus(rs.getString("WFL_DELEGATION_STATUS"));
				delegateBean.setDelegateTo(rs.getString("WFL_DELEGATE_TO"));
				delegateBean.setDelegationId(rs.getString("WFL_DELEGATION_ID"));
				delegatedUserList.add(delegateBean);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getDelegatedUsers");
		return delegatedUserList;
	}

	public ArrayList<EmployeeDivision> getUserDivision(String division_code, String user_login) throws Exception {
		logger.info("Started Method : getUserDivision Method parameter user_login"+division_code);
		ArrayList<EmployeeDivision> employeeDivisionList = new ArrayList<EmployeeDivision>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "SELECT ECM_DIVISION_CODE,ECM_DIVISION FROM ECM_EMPLOYEE WHERE ECM_DIVISION_CODE= ? AND ECM_USER_LOGIN =?";
			stmt = conn.prepareStatement(sqlQuery);	
			stmt.setString(1, escapeIntString(division_code));
			stmt.setString(2, escapeString(user_login));
			rs = stmt.executeQuery();

			while (rs.next()) {
				EmployeeDivision employeeDivision = new EmployeeDivision();
				employeeDivision.setEmpDivision(rs.getString("ECM_DIVISION"));
				employeeDivision.setEmpDivisionCode(rs.getInt("ECM_DIVISION_CODE"));
				employeeDivisionList.add(employeeDivision);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getDelegatedUsers");
		return employeeDivisionList;
	}

	public ArrayList<EmployeeDepartment> getUserDepartment(String dept_code,String user_login) throws Exception {
		
		logger.info("Started Method : getUserDivision Method parameter user_login"+dept_code);
		ArrayList<EmployeeDepartment> employeeDepartmentList = new ArrayList<EmployeeDepartment>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "SELECT ECM_DEPT_CODE,ECM_DEPARTMENT FROM ECM_EMPLOYEE WHERE ECM_DEPT_CODE= ? AND ECM_USER_LOGIN = ?";
			stmt = conn.prepareStatement(sqlQuery);	
			stmt.setString(1, escapeIntString(dept_code));
			stmt.setString(2, escapeString(user_login));
			rs = stmt.executeQuery();

			while (rs.next()) {
				EmployeeDepartment employeeDepartment = new EmployeeDepartment();
				employeeDepartment.setDepartment(rs.getString("ECM_DEPARTMENT"));
				employeeDepartment.setDepartmentCode(rs.getInt("ECM_DEPT_CODE"));
				employeeDepartmentList.add(employeeDepartment);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getDelegatedUsers");
		return employeeDepartmentList;
	}

	public String getDocumentSecurityfromTable(String workflowRecipient) throws Exception {
		
		logger.info("Started Method : getDocumentSecurityfromTable Method parameter workflowRecipient ::"+workflowRecipient);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		String groupName = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
										DBConfiguration.getInstance().USER_NAME,
										DBConfiguration.getInstance().PASSWORD,
										DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "SELECT AD_GROUP FROM DIMS_CONFIDENTIALITY WHERE USER_LOGIN=?";
			stmt = conn.prepareStatement(sqlQuery);	
			stmt.setString(1, escapeString(workflowRecipient));
			rs = stmt.executeQuery();

			while (rs.next()) {
				groupName = rs.getString("AD_GROUP");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getDocumentSecurityfromTable");
		return groupName;
	}

	public ArrayList<Employee> getEmployeeList(String flag, String filter, String code) throws Exception {
		logger.info("Started Method : getEmployeeList Method parameter flag::"+flag+"   filter"+filter);
		ArrayList<Employee> emailIdsList = new ArrayList<Employee>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);

			//division,department
			if(flag.equalsIgnoreCase("division")){
				sqlQuery = "SELECT ECM_USER_LOGIN,ECM_USER_NAME,ECM_USER_EMAIL,ECM_DESIGNATION FROM ECM_EMPLOYEE WHERE ECM_DEPT_CODE = convert(varchar(10),?) AND (ECM_ACTIVE_USER='true' OR ECM_ACTIVE_USER=1) AND (ECM_USER_NAME LIKE ? OR ECM_USER_LOGIN LIKE ? OR ECM_DESIGNATION LIKE ?)";
			}else if(flag.equalsIgnoreCase("department")){
				sqlQuery = "SELECT ECM_USER_LOGIN,ECM_USER_NAME,ECM_USER_EMAIL,ECM_DESIGNATION FROM ECM_EMPLOYEE WHERE ECM_DIVISION_CODE = convert(varchar(10),?) AND (ECM_ACTIVE_USER='true' OR ECM_ACTIVE_USER=1) AND (ECM_USER_NAME LIKE ? OR ECM_USER_LOGIN LIKE ? OR ECM_DESIGNATION LIKE ?)";
			}
			
			stmt = conn.prepareStatement(sqlQuery);	
			stmt.setString(1, escapeString(code));
			stmt.setString(2, "%" + escapeString(filter) + "%");
			stmt.setString(3, "%" + escapeString(filter) + "%");
			stmt.setString(4, "%" + escapeString(filter) + "%");
			rs = stmt.executeQuery();

			while (rs.next()) {
				Employee emp = new Employee();
				emp.setEmployeeEmail(rs.getString("ECM_USER_EMAIL"));
				emp.setEmployeeName(rs.getString("ECM_USER_NAME"));
				emp.setEmployeeLogin(rs.getString("ECM_USER_LOGIN"));
				emailIdsList.add(emp);
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getEmployeeList");
		return emailIdsList;
	}

	public ArrayList<EmployeeDepartment> getKNPCHierarchy() throws Exception {
		logger.info("Started Method : getKNPCHierarchy ");
		ArrayList<EmployeeDepartment> empDeptList = new ArrayList<EmployeeDepartment>();
		Connection conn = null;
		 PreparedStatement stmt = null;
		 ResultSet rs = null;
		 String sqlQuery= null;
		try {
			
			
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "SELECT ECM_DEPARTMENT.ECM_DEPT_CODE,ECM_DEPARTMENT.ECM_DEPARTMENT,ECM_DEPARTMENT.OLD_DIMS_DEPARTMENT_ID FROM ECM_DEPARTMENT ORDER BY ECM_DEPARTMENT.ECM_DEPARTMENT ASC";
			stmt = conn.prepareStatement(sqlQuery);	
			rs = stmt.executeQuery();

			while (rs.next()) {
				EmployeeDepartment empDept = new EmployeeDepartment();
				empDept.setDepartment(rs.getString("ECM_DEPARTMENT"));
				empDept.setDepartmentCode(rs.getInt("ECM_DEPT_CODE"));
				empDept.setOldDimsDeptCode(rs.getString("OLD_DIMS_DEPARTMENT_ID"));
				empDeptList.add(empDept);
			}

		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		    stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : getKNPCHierarchy");
		return empDeptList;
	}

	public Map<String,ColumnPreference> getColumnPreference(String user_login) throws Exception {
		logger.info("Started Method : getColumnPreference ");
		Map<String,ColumnPreference> columnPreferenceList = new HashMap<String,ColumnPreference>();
		
		Connection conn = null;
		 PreparedStatement stmt = null;
		 ResultSet rs = null;
		 String sqlQuery= null;
		try {
			
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
			
			sqlQuery = "SELECT CONFIG_KEY,CONFIG_VALUE FROM DIMS_CONFIGURATION WHERE CONFIG_KEY LIKE ? OR CONFIG_KEY LIKE 'default~~%'";
			
			stmt = conn.prepareStatement(sqlQuery);	
			stmt.setString(1, escapeString(user_login) + "~~workflow");
			rs = stmt.executeQuery();
		
			while (rs.next()) {
				if(rs.getString("CONFIG_KEY").equalsIgnoreCase(user_login+"~~workflow")){
					System.out.println("into Workflow");
					ColumnPreference columnPreference = new ColumnPreference();
					columnPreference.setColoumnPref(rs.getString("CONFIG_VALUE"));
					columnPreferenceList.put("workflow",columnPreference);
				}
				
				if(rs.getString("CONFIG_KEY").equalsIgnoreCase("default~~workflow")){
					System.out.println("into Default Doc");
					ColumnPreference columnPreference = new ColumnPreference();
					columnPreference.setColoumnPref(rs.getString("CONFIG_VALUE"));
					columnPreferenceList.put("default",columnPreference);
				}
				
				
			}

		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		    stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : getColumnPreference");
		return columnPreferenceList;
	}

	public ArrayList<Employee> getCrossDepartmentUsers(String searchCrtieria) throws Exception {
		logger.info("Started Method : getCrossDepartmentUsers  Method parameter division_code:");
		ArrayList<Employee> empList = new ArrayList<Employee>();
		Connection conn = null;
		 PreparedStatement stmt = null;
		 ResultSet rs = null;
		 String sqlQuery= null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
			Boolean bArgs = false;
			
			if(searchCrtieria.equalsIgnoreCase("undefined")){
				sqlQuery ="SELECT ECM_USER_LOGIN,ECM_USER_NAME,ECM_USER_EMAIL,"
						+ "ECM_DESIGNATION,ECM_USER_TITLE FROM ECM_EMPLOYEE "
						+ "WHERE (ECM_JOB_TITLE = 'TL' OR ECM_JOB_TITLE = 'MGR' OR ECM_JOB_TITLE = 'DCEO' OR ECM_JOB_TITLE ='CRDEP' OR ECM_JOB_TITLE = 'CEO')"
						+ "AND (ECM_ACTIVE_USER='true' OR ECM_ACTIVE_USER=1) AND ECM_ISHAVEINBOX = 1 order by ECM_USER_NAME ASC";
			}else{
				sqlQuery ="SELECT ECM_USER_LOGIN,ECM_USER_NAME,ECM_USER_EMAIL,"
						+ "ECM_DESIGNATION,ECM_USER_TITLE FROM ECM_EMPLOYEE "
						+ "WHERE (ECM_JOB_TITLE = 'TL' OR ECM_JOB_TITLE = 'MGR' OR ECM_JOB_TITLE = 'DCEO' OR ECM_JOB_TITLE ='CRDEP' OR ECM_JOB_TITLE = 'CEO')"
						+ "AND (ECM_ACTIVE_USER='true' OR ECM_ACTIVE_USER=1)  AND (ECM_USER_NAME LIKE ?  "
								+ "OR ECM_USER_LOGIN LIKE ? OR ECM_USER_TITLE LIKE ?)  AND ECM_ISHAVEINBOX = 1 order by ECM_USER_NAME ASC";
				bArgs = true;
			}
			
			stmt = conn.prepareStatement(sqlQuery);	
			if(bArgs) {
				stmt.setString(1, "%" + escapeString(searchCrtieria) + "%");
				stmt.setString(2, "%" + escapeString(searchCrtieria) + "%");
				stmt.setString(3, "%" + escapeString(searchCrtieria) + "%");
			}
			rs = stmt.executeQuery();


			while (rs.next()) {
				Employee emp = new Employee();
				emp.setEmployeeLogin(rs.getString("ECM_USER_LOGIN"));
				emp.setEmployeeName(rs.getString("ECM_USER_NAME"));
				emp.setEmployeeDesignation(rs.getString("ECM_USER_TITLE"));
				emp.setEmployeeEmail(rs.getString("ECM_USER_EMAIL"));
				empList.add(emp);
			}

		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		 	stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : getCrossDepartmentUsers ");
		return empList;
	}

	public ArrayList<Employee> loadDeafultUserList(String searchCrtieria, String listId) throws Exception {
		logger.info("Started Method : loadDeafultUserList  Method parameter :"+searchCrtieria);
		ArrayList<Employee> empList = new ArrayList<Employee>();
		Connection conn = null;
		 PreparedStatement stmt = null;
		 ResultSet rs = null;
		 String sqlQuery= null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
			
			Boolean bArgs = false;

			
			if(searchCrtieria.equalsIgnoreCase("undefined")){
				sqlQuery ="SELECT A.WFL_USER_LOGIN,B.ECM_USER_LOGIN,B.ECM_USER_NAME,B.ECM_USER_EMAIL,"
						+ "B.ECM_DESIGNATION,B.ECM_USER_TITLE FROM DIMS_USER_LIST_DETAIL A JOIN ECM_EMPLOYEE B ON A.WFL_USER_LOGIN = B.ECM_USER_LOGIN "
						+ "WHERE A.WFL_LIST_ID = ? order by B.ECM_USER_NAME ASC";
			}else{
				sqlQuery ="SELECT A.WFL_USER_LOGIN,B.ECM_USER_LOGIN,B.ECM_USER_NAME,B.ECM_USER_EMAIL,"
						+ "B.ECM_DESIGNATION,B.ECM_USER_TITLE FROM DIMS_USER_LIST_DETAIL A JOIN ECM_EMPLOYEE B ON A.WFL_USER_LOGIN = B.ECM_USER_LOGIN "
						+ "WHERE A.WFL_LIST_ID = ? AND (ECM_USER_NAME LIKE ?  "
								+ "OR ECM_USER_LOGIN LIKE ? OR ECM_USER_TITLE LIKE ?) order by B.ECM_USER_NAME ASC";
				bArgs = true;
			}
			
			stmt = conn.prepareStatement(sqlQuery);	
			stmt.setString(1, escapeString(listId));
			if(bArgs) {
				stmt.setString(2, "%" + escapeString(searchCrtieria) + "%");
				stmt.setString(3, "%" + escapeString(searchCrtieria) + "%");
				stmt.setString(4, "%" + escapeString(searchCrtieria) + "%");
			}
			rs = stmt.executeQuery();

			while (rs.next()) {
				Employee emp = new Employee();
				emp.setEmployeeLogin(rs.getString("ECM_USER_LOGIN"));
				emp.setEmployeeName(rs.getString("ECM_USER_NAME"));
				emp.setEmployeeDesignation(rs.getString("ECM_USER_TITLE"));
				emp.setEmployeeEmail(rs.getString("ECM_USER_EMAIL"));
				empList.add(emp);
			}

		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		 	stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : loadDeafultUserList ");
		return empList;
	}

	public ArrayList<Employee> getKNPCHierarchyUsersForDepartment(String searchCrtieria, String dept_code) throws Exception {
		logger.info("Started Method : getKNPCHierarchyUsersForDepartment  Method parameter witem_id:"+dept_code);
		ArrayList<Employee> empList = new ArrayList<Employee>();
		Connection conn = null;
		 PreparedStatement stmt = null;
		 ResultSet rs = null;
		 String sqlQuery= null;
		try {
			
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
			Boolean bArgs = false;
			
			if(searchCrtieria.equalsIgnoreCase("undefined")){
				sqlQuery ="SELECT ECM_EMPLOYEE.ECM_USER_LOGIN,ECM_EMPLOYEE.ECM_USER_NAME,"
						+ "ECM_EMPLOYEE.ECM_USER_EMAIL,ECM_EMPLOYEE.ECM_DESIGNATION,ECM_EMPLOYEE.ECM_USER_TITLE FROM "
						+ "ECM_EMPLOYEE WHERE ECM_EMPLOYEE.ECM_DEPT_CODE = convert(varchar(10),?) "
						+ "AND (ECM_EMPLOYEE.ECM_ACTIVE_USER='true' OR ECM_EMPLOYEE.ECM_ACTIVE_USER=1) "
						+ "AND (ECM_JOB_TITLE = 'TL' OR ECM_JOB_TITLE = 'MGR' OR ECM_JOB_TITLE = 'DCEO' OR ECM_JOB_TITLE = 'CEO') AND ECM_ISHAVEINBOX = 1 order by ECM_EMPLOYEE.ECM_USER_NAME ASC";
			}else{
				sqlQuery ="SELECT ECM_EMPLOYEE.ECM_USER_LOGIN,ECM_EMPLOYEE.ECM_USER_NAME,"
						+ "ECM_EMPLOYEE.ECM_USER_EMAIL,ECM_EMPLOYEE.ECM_DESIGNATION,ECM_EMPLOYEE.ECM_USER_TITLE FROM "
						+ "ECM_EMPLOYEE WHERE ECM_EMPLOYEE.ECM_DEPT_CODE = convert(varchar(10),?) "
						+ "AND (ECM_EMPLOYEE.ECM_ACTIVE_USER='true' OR ECM_EMPLOYEE.ECM_ACTIVE_USER=1) "
						+ "AND (ECM_JOB_TITLE = 'TL' OR ECM_JOB_TITLE = 'MGR' OR ECM_JOB_TITLE = 'DCEO' OR ECM_JOB_TITLE = 'CEO') "
						+ "AND (ECM_USER_NAME LIKE ? OR ECM_USER_LOGIN LIKE ? OR ECM_USER_TITLE LIKE ?) AND ECM_ISHAVEINBOX = 1 order by ECM_EMPLOYEE.ECM_USER_NAME ASC";
				bArgs = true;
			}
			stmt = conn.prepareStatement(sqlQuery);	
			stmt.setString(1, escapeString(dept_code));
			if(bArgs) {
				stmt.setString(2, "%" + escapeString(searchCrtieria) + "%");
				stmt.setString(3, "%" + escapeString(searchCrtieria) + "%");
				stmt.setString(4, "%" + escapeString(searchCrtieria) + "%");
			}
			rs = stmt.executeQuery();

			while (rs.next()) {
				Employee emp = new Employee();
				emp.setEmployeeLogin(rs.getString("ECM_USER_LOGIN"));
				emp.setEmployeeName(rs.getString("ECM_USER_NAME"));
				emp.setEmployeeDesignation(rs.getString("ECM_USER_TITLE"));
				emp.setEmployeeEmail(rs.getString("ECM_USER_EMAIL"));
				empList.add(emp);
			}

		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		 	stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : getKNPCHierarchyUsersForDepartment");
		return empList;
	}
	
	public ArrayList<Employee> getKNPCHierarchyUsers(String searchCrtieria) throws Exception {
		logger.info("Started Method : getKNPCHierarchyUsers  Method parameter witem_id:");
		ArrayList<Employee> empList = new ArrayList<Employee>();
		Connection conn = null;
		 PreparedStatement stmt = null;
		 ResultSet rs = null;
		 String sqlQuery= null;
		try {
			
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
			Boolean bArgs = false;
			
			if(searchCrtieria.equalsIgnoreCase("undefined") || searchCrtieria.equalsIgnoreCase("")){
				sqlQuery ="SELECT ECM_EMPLOYEE.ECM_USER_LOGIN,ECM_EMPLOYEE.ECM_USER_NAME,"
						+ "ECM_EMPLOYEE.ECM_USER_EMAIL,ECM_EMPLOYEE.ECM_DESIGNATION,ECM_EMPLOYEE.ECM_USER_TITLE, ECM_EMPLOYEE.ECM_DEPT_CODE, ECM_EMPLOYEE.ECM_DEPARTMENT FROM "
						+ "ECM_EMPLOYEE WHERE "
						+ " (ECM_EMPLOYEE.ECM_ACTIVE_USER='true' OR ECM_EMPLOYEE.ECM_ACTIVE_USER=1) "
						+ "AND (ECM_JOB_TITLE = 'TL' OR ECM_JOB_TITLE = 'MGR' OR ECM_JOB_TITLE = 'DCEO' OR ECM_JOB_TITLE = 'CEO')  AND ECM_ISHAVEINBOX = 1 order by ECM_EMPLOYEE.ECM_USER_NAME ASC";
			}else{
				sqlQuery ="SELECT ECM_EMPLOYEE.ECM_USER_LOGIN,ECM_EMPLOYEE.ECM_USER_NAME,"
						+ "ECM_EMPLOYEE.ECM_USER_EMAIL,ECM_EMPLOYEE.ECM_DESIGNATION,ECM_EMPLOYEE.ECM_USER_TITLE, ECM_EMPLOYEE.ECM_DEPT_CODE, ECM_EMPLOYEE.ECM_DEPARTMENT FROM "
						+ "ECM_EMPLOYEE WHERE "
						+ " (ECM_EMPLOYEE.ECM_ACTIVE_USER='true' OR ECM_EMPLOYEE.ECM_ACTIVE_USER=1) "
						+ "AND (ECM_JOB_TITLE = 'TL' OR ECM_JOB_TITLE = 'MGR' OR ECM_JOB_TITLE = 'DCEO' OR ECM_JOB_TITLE = 'CEO') "
						+ "AND (ECM_USER_NAME LIKE ? OR ECM_USER_LOGIN LIKE ? OR ECM_USER_TITLE LIKE ?)  AND ECM_ISHAVEINBOX = 1 order by ECM_EMPLOYEE.ECM_USER_NAME ASC";
				bArgs = true;
			}
			stmt = conn.prepareStatement(sqlQuery);	
			//stmt.setString(1, escapeString(dept_code));
			if(bArgs) {
				stmt.setString(1, "%" + escapeString(searchCrtieria) + "%");
				stmt.setString(2, "%" + escapeString(searchCrtieria) + "%");
				stmt.setString(3, "%" + escapeString(searchCrtieria) + "%");
			}
			rs = stmt.executeQuery();

			while (rs.next()) {
				Employee emp = new Employee();
				emp.setEmployeeLogin(rs.getString("ECM_USER_LOGIN"));
				emp.setEmployeeName(rs.getString("ECM_USER_NAME"));
				emp.setEmployeeDesignation(rs.getString("ECM_USER_TITLE"));
				emp.setEmployeeEmail(rs.getString("ECM_USER_EMAIL"));
				//emp.setEmpDept(rs.getString("ECM_DEPARTMENT"));
				empList.add(emp);
			}

		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		 	stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : getKNPCHierarchyUsers");
		return empList;
	}
	
	public ArrayList<EmployeeDepartment> getKNPCHierarchyUsersForDepartment_1(String searchCrtieria) throws Exception {
		logger.info("Started Method : getKNPCHierarchyUsersForDepartment_1  Method parameter witem_id:"+searchCrtieria);
		//ArrayList<Employee> empList = new ArrayList<Employee>();
		ArrayList<EmployeeDepartment> empDeptList = new ArrayList<EmployeeDepartment>();
		Connection conn = null;
		 PreparedStatement stmt = null;
		 ResultSet rs = null;
		 String sqlQuery= null;
		try {
			/*
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
			Boolean bArgs = false;
			
			if(searchCrtieria.equalsIgnoreCase("undefined") || searchCrtieria.equalsIgnoreCase("")){
				sqlQuery ="SELECT ECM_EMPLOYEE.ECM_USER_LOGIN,ECM_EMPLOYEE.ECM_USER_NAME,"
						+ "ECM_EMPLOYEE.ECM_USER_EMAIL,ECM_EMPLOYEE.ECM_DESIGNATION,ECM_EMPLOYEE.ECM_USER_TITLE, ECM_EMPLOYEE.ECM_DEPT_CODE, ECM_EMPLOYEE.ECM_DEPARTMENT FROM "
						+ "ECM_EMPLOYEE WHERE "
						+ " (ECM_EMPLOYEE.ECM_ACTIVE_USER='true' OR ECM_EMPLOYEE.ECM_ACTIVE_USER=1) "
						+ "AND (ECM_JOB_TITLE = 'TL' OR ECM_JOB_TITLE = 'MGR' OR ECM_JOB_TITLE = 'DCEO')";
			}else{
				sqlQuery ="SELECT ECM_EMPLOYEE.ECM_USER_LOGIN,ECM_EMPLOYEE.ECM_USER_NAME,"
						+ "ECM_EMPLOYEE.ECM_USER_EMAIL,ECM_EMPLOYEE.ECM_DESIGNATION,ECM_EMPLOYEE.ECM_USER_TITLE, ECM_EMPLOYEE.ECM_DEPT_CODE, ECM_EMPLOYEE.ECM_DEPARTMENT FROM "
						+ "ECM_EMPLOYEE WHERE "
						+ " (ECM_EMPLOYEE.ECM_ACTIVE_USER='true' OR ECM_EMPLOYEE.ECM_ACTIVE_USER=1) "
						+ "AND (ECM_JOB_TITLE = 'TL' OR ECM_JOB_TITLE = 'MGR' OR ECM_JOB_TITLE = 'DCEO') "
						+ "AND (ECM_USER_NAME LIKE ? OR ECM_USER_LOGIN LIKE ? OR ECM_USER_TITLE LIKE ?)";
				bArgs = true;
			}
			stmt = conn.prepareStatement(sqlQuery);	
			//stmt.setString(1, escapeString(dept_code));
			if(bArgs) {
				stmt.setString(1, "%" + escapeString(searchCrtieria) + "%");
				stmt.setString(2, "%" + escapeString(searchCrtieria) + "%");
				stmt.setString(3, "%" + escapeString(searchCrtieria) + "%");
			}
			rs = stmt.executeQuery();

			while (rs.next()) {
				Employee emp = new Employee();
				emp.setEmployeeLogin(rs.getString("ECM_USER_LOGIN"));
				emp.setEmployeeName(rs.getString("ECM_USER_NAME"));
				emp.setEmployeeDesignation(rs.getString("ECM_USER_TITLE"));
				emp.setEmployeeEmail(rs.getString("ECM_USER_EMAIL"));
				emp.setEmpDept(rs.getString("ECM_DEPARTMENT"));
				empList.add(emp);
			}*/




			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);

			if(searchCrtieria.equalsIgnoreCase("") || searchCrtieria.equalsIgnoreCase("undefined")) {
				sqlQuery = "SELECT ECM_DEPARTMENT.ECM_DEPT_CODE,ECM_DEPARTMENT.ECM_DEPARTMENT,ECM_DEPARTMENT.OLD_DIMS_DEPARTMENT_ID FROM"
						+ " ECM_DEPARTMENT "
						+ " ORDER BY ECM_DEPARTMENT.ECM_DEPARTMENT ASC";
				stmt = conn.prepareStatement(sqlQuery);	
			}
			else {			
				sqlQuery = "SELECT ECM_DEPARTMENT.ECM_DEPT_CODE,ECM_DEPARTMENT.ECM_DEPARTMENT,ECM_DEPARTMENT.OLD_DIMS_DEPARTMENT_ID FROM"
						+ " ECM_DEPARTMENT WHERE ECM_DEPARTMENT IN ((SELECT DISTINCT ECM_DEPARTMENT FROM [DIMS].[dbo].[ECM_EMPLOYEE] WHERE ECM_ISHAVEINBOX = 1 AND"
						+ " (ECM_USER_NAME like ? OR ECM_USER_LOGIN LIKE ? OR ECM_USER_TITLE LIKE ?) AND (ECM_JOB_TITLE = 'TL' OR ECM_JOB_TITLE = 'MGR' OR ECM_JOB_TITLE = 'DCEO' OR ECM_JOB_TITLE = 'CEO'))) ORDER BY ECM_DEPARTMENT.ECM_DEPARTMENT ASC";
				stmt = conn.prepareStatement(sqlQuery);	
				stmt.setString(1, "%" + escapeString(searchCrtieria) + "%");
				stmt.setString(2, "%" + escapeString(searchCrtieria) + "%");
				stmt.setString(3, "%" + escapeString(searchCrtieria) + "%");
			}
			rs = stmt.executeQuery();

			while (rs.next()) {
				EmployeeDepartment empDept = new EmployeeDepartment();
				empDept.setDepartment(rs.getString("ECM_DEPARTMENT"));
				empDept.setDepartmentCode(rs.getInt("ECM_DEPT_CODE"));
				empDept.setOldDimsDeptCode(rs.getString("OLD_DIMS_DEPARTMENT_ID"));
				empDeptList.add(empDept);
			}
		} catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		 	stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : getKNPCHierarchyUsersForDepartment_1");
		return empDeptList;
	}
	

	
	class WorkflowRecipientComp implements Comparator<WorkflowRecipient>{
		 
	    @Override
	    public int compare(WorkflowRecipient wr1, WorkflowRecipient wr2) {
	    	 return wr2.getWorkflowWorkItemType().compareTo(wr1.getWorkflowWorkItemType());
	    }
	}
	
	
	public String launchWorkFlowWithSPUpdated(ArrayList<WorkFlowDetails> workFlowDetails,ArrayList<WorkflowAttachments> WorkflowAttachmentsList) throws Exception {
		
		//logger.info("Started Method : launchWorkFlow  Method parameter workFlowDetails : "+workFlowDetails);
				String response="";
				Connection conn = null;
				try {
					
					conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
							DBConfiguration.getInstance().USER_NAME,
							DBConfiguration.getInstance().PASSWORD,
							DBConfiguration.getInstance().JDBC_DRIVER);
					
					if(workFlowDetails.size() > 0)
					{
						WorkFlowDetails curWorkflow = workFlowDetails.get(0);				
						
						String wfID = executeCreateWorkflowSP(curWorkflow, conn);
						curWorkflow.setWorkflowID(wfID);
						ArrayList<String> workItemId = new ArrayList<String>();
						ArrayList<WorkflowRecipient> workflowRecipientList =  curWorkflow.getWorkItemDetails().getWorkflowRecipientList();
						Collections.sort(workflowRecipientList,new WorkflowRecipientComp());

						for (int j = 0; j < workflowRecipientList.size(); j++) {
							String wiID = executeCreateWorkitemSP(wfID, curWorkflow.getWorkItemDetails(),workflowRecipientList.get(j), conn);
							workItemId.add(wiID);
						}	
						for (int j = 0; j < workItemId.size(); j++) {
							for (int i = 0; i < workFlowDetails.size(); i++) {	
								executeAddWorkitemAttachmentSP(workItemId.get(j).toString(),workFlowDetails.get(i).getWorkflowAttachment().getWorkflowDocumentId(),conn);
								}
						}
						for (int j = 0; j < workItemId.size(); j++) {
							for (int i = 0; i < WorkflowAttachmentsList.size(); i++) {
								executeAddWorkitemAttachmentSP(workItemId.get(j).toString(),WorkflowAttachmentsList.get(i).getWorkflowDocumentId(),conn);
								}
						}
						
						// Execute Email Notification Stored Procedure
						for(String onString: workItemId) {
							executeEmailNotificationSP(wfID, onString, "", conn);
						}
									
					}
					response = "success";
				} catch (Exception e) {
					response = "failure";
					//logger.error(e.getMessage(), e);
					throw new Exception(e.getMessage());
				}finally{
				    conn.close();
				 }
				//logger.info("Exit Method : launchWorkFlow");
				return response;
		
	}

	public String isSecretary(String user_login) throws Exception {
		System.out.println("Started Method : isSecretary  Method parameter user_login:"+user_login);
		String isSecretary = "false";
		Connection conn = null;
		 PreparedStatement stmt = null;
		 ResultSet rs = null;
		 String sqlQuery= null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
			

			sqlQuery ="select ECM_ASST_EMP_LOGIN from ECM_ASSISTANT_EMPLOYEE WHERE ECM_ASST_EMP_SUPERVISOR=?";
			
			stmt = conn.prepareStatement(sqlQuery);	
			stmt.setString(1, escapeString(user_login));
			rs = stmt.executeQuery();

			while (rs.next()) {
				if(rs.getString("ECM_ASST_EMP_LOGIN")!=null && !rs.getString("ECM_ASST_EMP_LOGIN").equalsIgnoreCase("") ){
					isSecretary = "true";					
				}
			}

		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		 	stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : isSecretary ");
		return isSecretary;
	}

	public String getSupervisor(String user_login) throws Exception {
		logger.info("Started Method : getSupervisor  Method parameter user_login:"+user_login);
		String supervisor = "";
		Connection conn = null;
		 PreparedStatement stmt = null;
		 ResultSet rs = null;
		 String sqlQuery= null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery ="SELECT ECM_ASST_EMP_SUPERVISOR FROM ECM_ASSISTANT_EMPLOYEE WHERE ECM_ASST_EMP_LOGIN=?";
			
			stmt = conn.prepareStatement(sqlQuery);	
			stmt.setString(1, escapeString(user_login));
			rs = stmt.executeQuery();

			while (rs.next()) {
				supervisor = rs.getString("ECM_ASST_EMP_SUPERVISOR");
			}
			
		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		 	stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : getSupervisor ");
		return supervisor;
	}

	public String getAssistance(String user_login) throws Exception {
		logger.info("Started Method : getAssistance  Method parameter user_login:"+user_login);
		String assistance = "";
		Connection conn = null;
		 PreparedStatement stmt = null;
		 ResultSet rs = null;
		 String sqlQuery= null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
			

			sqlQuery ="SELECT ECM_ASST_EMP_LOGIN FROM ECM_ASSISTANT_EMPLOYEE WHERE ECM_ASST_EMP_SUPERVISOR =?";
			
			stmt = conn.prepareStatement(sqlQuery);	
			stmt.setString(1, escapeString(user_login));
			rs = stmt.executeQuery();

			while (rs.next()) {
				assistance = rs.getString("ECM_ASST_EMP_LOGIN");
			}

		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		 	stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : getAssistance ");
		return assistance;
	}
	
	// Added For Getting List of Secratries
	public ArrayList getAssistanceList(String user_login) throws Exception {
		logger.info("Started Method : getAssistance Method parameter user_login:"+user_login);
		String assistance = "";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery= null;
		ArrayList userList = null;
		try {
		conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
	
		sqlQuery ="SELECT ECM_ASST_EMP_LOGIN FROM ECM_ASSISTANT_EMPLOYEE WHERE ECM_ASST_EMP_SUPERVISOR =?";
	
		stmt = conn.prepareStatement(sqlQuery);
		stmt.setString(1, escapeString(user_login));
		rs = stmt.executeQuery();
		userList = new ArrayList();
		while (rs.next()) {
		//assistance = rs.getString("ECM_ASST_EMP_LOGIN");
		userList.add(rs.getString("ECM_ASST_EMP_LOGIN"));
	
		}
	
		} catch(Exception e){
		logger.error(e.getMessage(), e);
		throw new Exception(e.getMessage());
		}finally{
		rs.close();
		stmt.close();
		conn.close();
		}
		logger.info("Exit Method : getAssistance ");
		return userList;
	}

	public String deleteFolder(String folderId) throws Exception {
		logger.info("Started Method : deleteFolder Method parameter folderid : "+folderId);
		Connection conn = null;
		PreparedStatement stmt = null;
		String sqlQuery = null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery ="Delete from DIMS_WORKFLOW_FOLDERS where FOLDER_ID = ?";
			stmt = conn.prepareStatement(sqlQuery);	
			stmt.setString(1, escapeString(folderId));
			stmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getAssistance ");
		return "true";
	}

	public ArrayList<DIMSSite> getSiteItems(String dept_code, String siteDesc) throws Exception {
		logger.info("Started Method : getSiteItems Method parameter folderid : "+dept_code);
		long startTime = System.currentTimeMillis();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		ArrayList<DIMSSite> dimsSiteList= new ArrayList<DIMSSite>();
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
			
			
			sqlQuery ="SELECT TOP 1000 SITE_ID,DEPT_CODE,SITE_NAME,SITE_TYPE FROM DIMS_SITE WHERE SITE_NAME LIKE ? AND DEPT_CODE = convert(varchar(10),?)";
			
			stmt = conn.prepareStatement(sqlQuery);	
			stmt.setString(1, "%" +escapeString(URLDecoder.decode(siteDesc,"UTF-8"))+ "%");
			stmt.setString(2, escapeString(dept_code));
			rs = stmt.executeQuery();

			while (rs.next()) {
				DIMSSite dimsSite = new DIMSSite();
				dimsSite.setSiteId(rs.getString("SITE_ID"));
				dimsSite.setSiteName(rs.getString("SITE_NAME"));
				dimsSite.setSiteType(rs.getString("SITE_TYPE"));
				dimsSite.setDepartmentCode(rs.getString("DEPT_CODE"));
				dimsSiteList.add(dimsSite);
			}
			
			/*if(dimsSiteList.size() == 0) {
				DIMSSite dimsSite = new DIMSSite();
				dimsSite.setIsExisted("SEARCH");
				dimsSiteList.add(dimsSite);
			}*/
			
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			int seconds = (int) (totalTime / 1000) % 60 ;
			System.out.println("Total Time Taken for *****@@@@@@@@@@@***   getSiteItems() ::: "+seconds +" seconds");

		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		 	stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : getSiteItems ");
		return dimsSiteList;
	}

	public ArrayList<DIMSSite> addSiteItems(String dept_code, String siteType,String siteDesc) throws Exception {
		logger.info("Started Method : addSiteItems");
		Connection conn = null;
		PreparedStatement preStmt = null;
		String sqlQuery = null;
		ArrayList<DIMSSite> dimsSiteList= new ArrayList<DIMSSite>();
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			
			//added Ravi Boni
			int rowCount = 0;
			String siteName = null;
			preStmt = DBUtil.getPreparedStatement(conn, "SELECT SITE_NAME FROM DIMS_SITE WHERE DEPT_CODE = ? AND SITE_NAME=?");
			preStmt.setString(1,escapeString(dept_code));
			preStmt.setString(2,escapeString(URLDecoder.decode(siteDesc.trim(),"UTF-8")));
			ResultSet rs = preStmt.executeQuery();
			while(rs.next()) {
				siteName = rs.getString("SITE_NAME");
			}
			preStmt.close();
			if(siteName == null && siteDesc != null && siteDesc.trim().length()>0) {
			
				sqlQuery = "INSERT INTO DIMS_SITE"
							+"(DEPT_CODE,SITE_NAME,SITE_TYPE) "
							+ "VALUES(?,?,?)";
				
				preStmt = DBUtil.getPreparedStatement(conn, sqlQuery);
				preStmt.setString(1,escapeString(dept_code));
				preStmt.setString(2,escapeString(URLDecoder.decode(siteDesc.trim(),"UTF-8")));
				preStmt.setString(3,escapeString(siteType));
				rowCount = preStmt.executeUpdate();
			
			preStmt.close();
			}
			
			if(rowCount>0){
				dimsSiteList = getSiteItems(dept_code,"");
			}
			else {
				DIMSSite dimsSite = new DIMSSite();
				dimsSite.setIsExisted("EXIST");
				dimsSiteList.add(dimsSite);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			preStmt.close();
			conn.close();
		}
		logger.info("Exit Method : addSiteItems");
		return dimsSiteList;
	}


	public ArrayList<DIMSSite> updateSiteItems(String siteId,String siteType, String siteDesc, String dept_code) throws Exception {
		logger.info("Started Method : addSiteItems");
		Connection conn = null;
		PreparedStatement preStmt = null;
		String sqlQuery = null;
		ArrayList<DIMSSite> dimsSiteList= new ArrayList<DIMSSite>();
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			
			//added Ravi Boni
			int rowCount = 0;
			String siteName = null;
			String existSiteType = null;
			preStmt = DBUtil.getPreparedStatement(conn, "SELECT SITE_NAME, SITE_TYPE FROM DIMS_SITE WHERE DEPT_CODE = ? AND SITE_NAME=? AND SITE_TYPE=?");
			preStmt.setString(1,escapeString(dept_code));
			preStmt.setString(2,escapeString(URLDecoder.decode(siteDesc.trim(),"UTF-8")));
			preStmt.setString(3,escapeString(siteType));
			ResultSet rs = preStmt.executeQuery();
			while(rs.next()) {
				siteName = rs.getString("SITE_NAME");
				existSiteType = rs.getString("SITE_TYPE");
			}
			preStmt.close();
			if(siteName == null && siteDesc != null && siteDesc.trim().length() > 0) {

				sqlQuery = "UPDATE DIMS_SITE SET SITE_NAME=?, SITE_TYPE=? WHERE SITE_ID =?";
				
				preStmt = DBUtil.getPreparedStatement(conn, sqlQuery);
				preStmt.setString(1,escapeString(URLDecoder.decode(siteDesc.trim(),"UTF-8")));
				preStmt.setString(2,escapeString(siteType));
				preStmt.setString(3,escapeString(siteId));
				rowCount = preStmt.executeUpdate();
				
				preStmt.close();
			}
			else if(siteName != null && !(siteName.trim().equalsIgnoreCase(siteDesc.trim())) && siteType != null && !(siteType.trim().equalsIgnoreCase(existSiteType.trim()))) {

				sqlQuery = "UPDATE DIMS_SITE SET SITE_NAME=?, SITE_TYPE=? WHERE SITE_ID =?";
				
				preStmt = DBUtil.getPreparedStatement(conn, sqlQuery);
				preStmt.setString(1,escapeString(URLDecoder.decode(siteDesc.trim(),"UTF-8")));
				preStmt.setString(2,escapeString(siteType));
				preStmt.setString(3,escapeString(siteId));
				rowCount = preStmt.executeUpdate();
				
				preStmt.close();
			}
			if(rowCount>0){
				dimsSiteList = getSiteItems(dept_code,"");
			}
			else {
				DIMSSite dimsSite = new DIMSSite();
				dimsSite.setIsExisted("EXIST");
				dimsSiteList.add(dimsSite);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			preStmt.close();
			conn.close();
		}
		logger.info("Exit Method : addSiteItems");
		return dimsSiteList;
	}
	
	public ColumnPreference addColumnPreference(String jsonString, String user_login, String prefFor) throws Exception {
		logger.info("Started Method : addColumnPreference ");
		ColumnPreference columnPreference = new ColumnPreference();
		Connection conn = null;
		PreparedStatement preStmt = null;
		 ResultSet rs = null;
		 String sqlQuery= null;
		 String configKey = null;
		try {
			if(prefFor.equalsIgnoreCase("workflow")){
				configKey = user_login+"~~workflow";
			}else{
				configKey = user_login+"~~document";
			}
			
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
			
			//insert into table_name values();
			sqlQuery = "SELECT CONFIG_KEY FROM DIMS_CONFIGURATION WHERE CONFIG_KEY = ?"; 
			preStmt = DBUtil.getPreparedStatement(conn, sqlQuery);
			preStmt.setString(1, escapeString(configKey));
			rs = preStmt.executeQuery();
			String key =null;
			while(rs.next()){
				key= rs.getString("CONFIG_KEY");
			}
			rs.close();
			preStmt.close();
			if(key!=null){
				
				sqlQuery = "DELETE FROM DIMS_CONFIGURATION WHERE CONFIG_KEY =?";
				preStmt = DBUtil.getPreparedStatement(conn, sqlQuery);
				preStmt.setString(1, escapeString(key));
				preStmt.execute();
				preStmt.close();
			}
			
			sqlQuery = "INSERT INTO DIMS_CONFIGURATION VALUES(?,?)"; 
			preStmt = DBUtil.getPreparedStatement(conn, sqlQuery);
			preStmt.setString(1, escapeString(configKey));
			preStmt.setString(2, escapeString(jsonString));
			preStmt.executeUpdate();
		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		 	preStmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : addColumnPreference");
		return columnPreference;
	}

	/*
	 * this method gets workflow statistics details...
	 * 
	 */
	public ArrayList<DivisionWF> workflowStatistics(String user_login, String department, String division, String from, String to) throws Exception {
		logger.info("Started Method : workflowStatistics ");
		ArrayList<DivisionWF> divisionWFList = new ArrayList<DivisionWF>();
		Connection conn = null;
		PreparedStatement preStmt = null;
		 ResultSet rs = null;
		 String sqlQuery= null;
		try {
			
			if(from.equalsIgnoreCase("undefined")) {
				from = null;
			}
			if(to.equalsIgnoreCase("undefined")) {
				to = null;				
			}
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);
			sqlQuery = "{call DIMS_REPORT_WORKFLOW_STATISTIC(?,?,?,?)}"; 
			preStmt = DBUtil.getPreparedStatement(conn, sqlQuery);
			preStmt.setString(1, escapeString(department));
			preStmt.setString(2, escapeString(division));
			preStmt.setString(3, escapeString(from));
			preStmt.setString(4, escapeString(to));
			rs = preStmt.executeQuery();
			while(rs.next()){
				DivisionWF divWF = new DivisionWF();
				divWF.setDivName(rs.getString("ECM_DIVISION"));
				divWF.setTotAWF(rs.getString("ActiveWorkflow"));
				divWF.setTotNWF(rs.getString("NewWorkflow"));
				divWF.setTotOWF(rs.getString("OverDueWorkflow"));
				divWF.setTotWF(rs.getString("totalWorkflow"));
				divWF.setUserName(rs.getString("ECM_USER_NAME"));
				divisionWFList.add(divWF);
				
			}
			rs.close();
			preStmt.close();
			
		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		 	preStmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : workflowStatistics");
		return divisionWFList;
	}

	
	public ArrayList<PendingWorkItemDetails> pendingWorkflows(String sender, String status, String overdue, String recipient, String from, String to) throws Exception{

		//to do
		logger.info("Started Method : pendingWorkflows Method parameter user_login:"+sender);
		ArrayList<PendingWorkItemDetails> pendingWorkItemsList = new ArrayList<PendingWorkItemDetails>();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);

			
			sqlQuery = "select A.WFL_WITM_RECEIVEDON, A.WFL_WITM_STATUS, E.ECM_USER_NAME as SENDER, C.ECM_USER_NAME as RECIPIENT, A.WFL_WITM_DEADLINE, A.WFL_WITM_ACTION,A.WFL_WITM_DEADLINE, A.WFL_WITM_ACTION_BY, A.WFL_WITM_SENDER_DEPT, "
					+ " A.WFL_WITM_RECEIVER_DEPT, A.WFL_WITM_SENDER_DIV, A.WFL_WITM_RECEIVER_DIV, B.WFL_SUBJECT from dims_workitem A join DIMS_WORKFLOW B ON A.WFL_ID = B.WFL_ID join ECM_EMPLOYEE E ON A.WFL_SENDER = E.ECM_USER_LOGIN join ECM_EMPLOYEE C ON A.WFL_RECIPIENT = C.ECM_USER_LOGIN where A.wfl_sender = "+"'"+escapeString(sender)+"'";

			if(escapeString(status).equalsIgnoreCase("All")) {
				sqlQuery = sqlQuery+" AND A.WFL_WITM_STATUS in ('Done', 'New', 'Read','Forward','Reassign')"; 
			}
			else if(escapeString(status).equalsIgnoreCase("Done")) {
				sqlQuery = sqlQuery+" AND A.wfl_witm_status in ('Done')";
			}
			else if(escapeString(status).equalsIgnoreCase("Not Done")) {
				sqlQuery = sqlQuery+" AND A.wfl_witm_status in ('New', 'Read','Forward','Reassign')";
			}

			//DBUtil.convertStringtoDate(from);
			if(!escapeString(from).equalsIgnoreCase("undefined") && !escapeString(to).equalsIgnoreCase("undefined")){
				sqlQuery = sqlQuery+" AND A.WFL_WITM_RECEIVEDON >"+"'"+DBUtil.convertStringtoDate(from)+"'"+" AND A.WFL_WITM_RECEIVEDON <="+"'"+DBUtil.addOneDay(to)+"'";
			}
			else if(!escapeString(from).equalsIgnoreCase("undefined") && escapeString(to).equalsIgnoreCase("undefined")){
				sqlQuery = sqlQuery+" AND A.WFL_WITM_RECEIVEDON >"+"'"+DBUtil.convertStringtoDate(from)+"'";
			}
			else if(escapeString(from).equalsIgnoreCase("undefined") && !escapeString(to).equalsIgnoreCase("undefined")){
				sqlQuery = sqlQuery+" AND A.WFL_WITM_RECEIVEDON <="+"'"+DBUtil.addOneDay(to)+"'";
			}
			else if(escapeString(from).equalsIgnoreCase("undefined") && escapeString(to).equalsIgnoreCase("undefined")){
				sqlQuery = sqlQuery;
			}			
			//sqlQuery.append(" AND A.WFL_WITM_RECEIVEDON = "+DBUtil.convertStringtoDate(workFlowSearchBean.getRecieveDate()));

			if(escapeString(overdue) != null && escapeString(overdue).equalsIgnoreCase("True")) {

				java.util.Date date = new java.util.Date();
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

				//for getting overdue workitems.
				sqlQuery = sqlQuery+" AND A.WFL_WITM_DEADLINE < "+"'"+DBUtil.convertStringtoDate(sdf.format(date))+"'";
			}

			if(escapeString(recipient) != null) {
				sqlQuery = sqlQuery+" AND A.WFL_RECIPIENT in (";
				String recipients [] = recipient.split(",");

				for (int i = 0; i < recipients.length; i++) {
					if(recipients.length == 1) {
						sqlQuery = sqlQuery+"'"+escapeString(recipients[i])+"'"+") AND A.WFL_WITM_TYPE <> 'cc' order by A.WFL_RECIPIENT";
					}
					else {						
						if(i == recipients.length-1) {
							sqlQuery = sqlQuery+"'"+escapeString(recipients[i])+"'"+") AND A.WFL_WITM_TYPE <> 'cc' order by A.WFL_RECIPIENT";
						}
						else {
							sqlQuery = sqlQuery+"'"+escapeString(recipients[i])+"'"+", ";
						}
					}
				}
			}
			System.out.println("pending workflow Sql Query :"+sqlQuery);

			stmt = conn.prepareStatement(sqlQuery);
			rs = stmt.executeQuery();

			while (rs.next()) {
				PendingWorkItemDetails pendingWorkItemDetails = new PendingWorkItemDetails();
				//pendingWorkItemDetails.setRecipient(rs.getString("WFL_RECIPIENT"));
				pendingWorkItemDetails.setRecipient(rs.getString("RECIPIENT"));
				String receivedDate = rs.getString("WFL_WITM_RECEIVEDON").substring(0, 10);
				pendingWorkItemDetails.setWorkflowBeginDate(receivedDate);
				pendingWorkItemDetails.setWorkitemStatus(rs.getString("WFL_WITM_STATUS"));
				pendingWorkItemDetails.setReceiveDate(receivedDate);
				pendingWorkItemDetails.setDeadLine(rs.getString("WFL_WITM_DEADLINE").substring(0, 10));
				//pendingWorkItemDetails.setSender(rs.getString("WFL_SENDER"));
				pendingWorkItemDetails.setSender(rs.getString("SENDER"));
				pendingWorkItemDetails.setSubject(rs.getString("WFL_SUBJECT"));
				pendingWorkItemDetails.setIsOverdue(DBUtil.getOverdue(rs.getDate("WFL_WITM_DEADLINE")));
				//pendingWorkItemDetails.setAction(rs.getString("WFL_WITM_ACTION"));
				//pendingWorkItemDetails.setActionBy(rs.getString("WFL_WITM_ACTION_BY"));
				//pendingWorkItemDetails.setSenderDepartment(rs.getString("WFL_WITM_SENDER_DEPT"));
				//pendingWorkItemDetails.setReceiverDepartment(rs.getString("WFL_WITM_RECEIVER_DEPT"));
				//pendingWorkItemDetails.setSenderDiv(rs.getString("WFL_WITM_SENDER_DIV"));
				//pendingWorkItemDetails.setReceiverDiv(rs.getString("WFL_WITM_RECEIVER_DIV"));
				if(!from.equalsIgnoreCase("undefined")) {
					pendingWorkItemDetails.setFromDate(from);
				}
				if(!to.equalsIgnoreCase("undefined")) {
					pendingWorkItemDetails.setToDate(to);
				}
				pendingWorkItemsList.add(pendingWorkItemDetails);
			}

			//pendingWorkItemDetailsList.add(pendingWorkItemsList);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : pendingWorkflows");
		return pendingWorkItemsList;

	}
	
	public ArrayList<PendingWorkItemDetails> pendingWorkflowsReport(String sender, String status, String overdue, String recipient, String from, String to) throws Exception{

		//to do
		logger.info("Started Method : pendingWorkflows Method parameter user_login:"+sender);
		ArrayList<PendingWorkItemDetails> pendingWorkItemsList = new ArrayList<PendingWorkItemDetails>();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "select WFL_WITM_RECEIVEDON, WFL_WITM_STATUS, WFL_SENDER, WFL_RECIPIENT, WFL_WITM_DEADLINE, WFL_WITM_ACTION, WFL_WITM_ACTION_BY, WFL_WITM_SENDER_DEPT, "
					+ " WFL_WITM_RECEIVER_DEPT, WFL_WITM_SENDER_DIV, WFL_WITM_RECEIVER_DIV from dbo.dims_workitem where wfl_sender = "+"'"+escapeString(sender)+"'";

			if(escapeString(status).equalsIgnoreCase("All")) {
				sqlQuery = sqlQuery+" AND WFL_WITM_STATUS in ('Done', 'New', 'Read')";
			}
			else if(escapeString(status).equalsIgnoreCase("Done")) {
				sqlQuery = sqlQuery+" AND wfl_witm_status in ('Done')";
			}
			else if(escapeString(status).equalsIgnoreCase("Not Done")) {
				sqlQuery = sqlQuery+" AND wfl_witm_status in ('New', 'Read')";
			}

			//DBUtil.convertStringtoDate(from);
			if(!escapeString(from).equalsIgnoreCase("undefined") && !escapeString(to).equalsIgnoreCase("undefined")){
				sqlQuery = sqlQuery+" AND WFL_WITM_RECEIVEDON >="+"'"+DBUtil.minusOneDay(from)+"'"+" AND WFL_WITM_RECEIVEDON <="+"'"+DBUtil.addOneDay(to)+"'";
			}
			else if(!escapeString(from).equalsIgnoreCase("undefined") && escapeString(to).equalsIgnoreCase("undefined")){
				sqlQuery = sqlQuery+" AND WFL_WITM_RECEIVEDON >="+"'"+DBUtil.minusOneDay(from)+"'";
			}
			else if(escapeString(from).equalsIgnoreCase("undefined") && !escapeString(to).equalsIgnoreCase("undefined")){
				sqlQuery = sqlQuery+" AND WFL_WITM_RECEIVEDON <="+"'"+DBUtil.addOneDay(to)+"'";
			}
			else if(escapeString(from).equalsIgnoreCase("undefined") && escapeString(to).equalsIgnoreCase("undefined")){
				sqlQuery = sqlQuery;
			}			
			//sqlQuery.append(" AND A.WFL_WITM_RECEIVEDON = "+DBUtil.convertStringtoDate(workFlowSearchBean.getRecieveDate()));

			if(escapeString(overdue) != null && escapeString(overdue).equalsIgnoreCase("True")) {

				java.util.Date date = new java.util.Date();
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

				//for getting overdue workitems.
				sqlQuery = sqlQuery+" AND WFL_WITM_DEADLINE <= "+"'"+DBUtil.convertStringtoDate(sdf.format(date))+"'";
			}

			if(escapeString(recipient) != null) {
				sqlQuery = sqlQuery+" AND WFL_RECIPIENT in (";
				String recipients [] = recipient.split(",");

				for (int i = 0; i < recipients.length; i++) {
					if(recipients.length == 1) {
						sqlQuery = sqlQuery+"'"+escapeString(recipients[i])+"'"+") AND WFL_WITM_TYPE <> 'cc' order by WFL_RECIPIENT";
					}
					else {						
						if(i == recipients.length-1) {
							sqlQuery = sqlQuery+"'"+escapeString(recipients[i])+"'"+") AND A.WFL_WITM_TYPE <> 'cc' order by WFL_RECIPIENT";
						}
						else {
							sqlQuery = sqlQuery+"'"+escapeString(recipients[i])+"'"+", ";
						}
					}
				}
			}
			//System.out.println("Sql Query :"+sqlQuery);

			stmt = conn.prepareStatement(sqlQuery);
			rs = stmt.executeQuery();

			while (rs.next()) {
				PendingWorkItemDetails pendingWorkItemDetails = new PendingWorkItemDetails();
				pendingWorkItemDetails.setRecipient(rs.getString("WFL_RECIPIENT"));
				String receivedDate = rs.getString("WFL_WITM_RECEIVEDON").substring(0, 10);
				pendingWorkItemDetails.setWorkflowBeginDate(receivedDate);
				pendingWorkItemDetails.setWorkitemStatus(rs.getString("WFL_WITM_STATUS"));
				pendingWorkItemDetails.setReceiveDate(receivedDate);
				pendingWorkItemDetails.setDeadLine(rs.getString("WFL_WITM_DEADLINE").substring(0, 10));				
				pendingWorkItemDetails.setSender(rs.getString("WFL_SENDER"));
				//pendingWorkItemDetails.setAction(rs.getString("WFL_WITM_ACTION"));
				//pendingWorkItemDetails.setActionBy(rs.getString("WFL_WITM_ACTION_BY"));
				//pendingWorkItemDetails.setSenderDepartment(rs.getString("WFL_WITM_SENDER_DEPT"));
				//pendingWorkItemDetails.setReceiverDepartment(rs.getString("WFL_WITM_RECEIVER_DEPT"));
				//pendingWorkItemDetails.setSenderDiv(rs.getString("WFL_WITM_SENDER_DIV"));
				//pendingWorkItemDetails.setReceiverDiv(rs.getString("WFL_WITM_RECEIVER_DIV"));

				pendingWorkItemsList.add(pendingWorkItemDetails);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getInboxItems");
		return pendingWorkItemsList;

	}

	public ArrayList<PendingWorkItemDetails> pendingWorkflowsFullHistory(String sender, String status, String recipient, String from, String to) throws Exception{
		//to do
		ArrayList<PendingWorkItemDetails> pendingWorkItemFullHistoryList = new ArrayList<PendingWorkItemDetails>();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);

			/*sqlQuery = "select A.WFL_WITM_STATUS, A.WFL_SENDER, A.WFL_RECIPIENT, A.WFL_WITM_SYS_STATUS, B.WFL_ACTION_TIMESTAMP, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_DEADLINE"
					+ " from dbo.dims_workitem A join DIMS_WORKFLOW_HISTORY B ON A.WFL_WITM_ID=B.WFL_ACTION_WITM_ID where wfl_sender = "+"'"+escapeString(sender)+"'";
			 */
			sqlQuery = "SELECT DISTINCT WFL_ID FROM DIMS_WORKITEM WHERE wfl_sender = "+"'"+escapeString(sender)+"'";

			if(escapeString(status).equalsIgnoreCase("All")) {
				sqlQuery = sqlQuery+" AND WFL_WITM_STATUS in ('Done', 'New', 'Read','Forward','Reassign')";
			}
			else if(escapeString(status).equalsIgnoreCase("Done")) {
				sqlQuery = sqlQuery+" AND wfl_witm_status in ('Done')";
			}
			else if(escapeString(status).equalsIgnoreCase("Not Done")) {
				sqlQuery = sqlQuery+" AND wfl_witm_status in ('New', 'Read','Forward','Reassign')";
			}
			
			if(!escapeString(from).equalsIgnoreCase("undefined") && !escapeString(to).equalsIgnoreCase("undefined")){
				if(escapeString(from).equalsIgnoreCase(escapeString(to))) {
					sqlQuery = sqlQuery+" AND WFL_WITM_RECEIVEDON >"+"'"+DBUtil.convertStringtoDate(escapeString(from))+"'"+" AND WFL_WITM_RECEIVEDON <"+"'"+DBUtil.addOneDay(escapeString(to))+"'";
				}
				else {
					sqlQuery = sqlQuery+" AND WFL_WITM_RECEIVEDON >"+"'"+DBUtil.convertStringtoDate(escapeString(from))+"'"+" AND WFL_WITM_RECEIVEDON <"+"'"+DBUtil.addOneDay(escapeString(to))+"'";
				}
			}
			else if(!from.equalsIgnoreCase("undefined") && to.equalsIgnoreCase("undefined")){
				sqlQuery = sqlQuery+" AND WFL_WITM_RECEIVEDON >"+"'"+DBUtil.convertStringtoDate(escapeString(from))+"'";
			}
			else if(from.equalsIgnoreCase("undefined") && !to.equalsIgnoreCase("undefined")){
				sqlQuery = sqlQuery+" AND WFL_WITM_RECEIVEDON <"+"'"+DBUtil.addOneDay(escapeString(to))+"'";
			}
			else if(from.equalsIgnoreCase("undefined") && to.equalsIgnoreCase("undefined")){
				sqlQuery = sqlQuery;
			}			

			if(recipient != null) {
				sqlQuery = sqlQuery+" AND WFL_RECIPIENT in (";
				String recipients [] = recipient.split(",");

				for (int i = 0; i < recipients.length; i++) {
					if(recipients.length == 1) {
						sqlQuery = sqlQuery+"'"+escapeString(recipients[i])+"'"+")";
					}
					else {						
						if(i == recipients.length-1) {
							sqlQuery = sqlQuery+"'"+escapeString(recipients[i])+"'"+")";
						}
						else {
							sqlQuery = sqlQuery+"'"+escapeString(recipients[i])+"'"+", ";
						}
					}
				}
			}
			System.out.println("pendingWorkflowsFullHistory Sql Query :"+sqlQuery);

			stmt = conn.prepareStatement(sqlQuery);
			rs = stmt.executeQuery();
			
			ArrayList<PendingWorkItemDetails> pendingWorkItemFullHistoryList1 = new ArrayList<PendingWorkItemDetails>();
			while (rs.next()) {
				PendingWorkItemDetails pendingWorkItemDetails = new PendingWorkItemDetails();
				String workFlowId = rs.getString("WFL_ID");
				pendingWorkItemDetails.setWorkflowID(workFlowId);
				pendingWorkItemFullHistoryList1.add(pendingWorkItemDetails);
			}

			for (int i = 0; i < pendingWorkItemFullHistoryList1.size(); i++) {
				/*String sqlQuery1 = "SELECT  A.WFL_ID, C.WFL_SUBJECT,A.WFL_WITM_ACTION_COMMENT, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_STATUS, E.ECM_USER_NAME  as SENDER, D.ECM_USER_NAME as RECIPIENT, A.WFL_WITM_SYS_STATUS, A.WFL_WITM_TYPE, B.WFL_ACTION_TIMESTAMP, A.WFL_WITM_DEADLINE, B.WFL_ACTION_STATUS "
						+ " FROM DIMS_WORKFLOW_HISTORY B, DIMS_WORKFLOW C, DIMS_WORKITEM A join ECM_EMPLOYEE E ON A.WFL_SENDER = E.ECM_USER_LOGIN join ECM_EMPLOYEE D ON A.WFL_RECIPIENT = D.ECM_USER_LOGIN WHERE B.WFL_ID = "+"'"+pendingWorkItemFullHistoryList1.get(i).getWorkflowID()+"'"+ " AND "
						+ "B.WFL_ACTION_WITM_ID = A.WFL_WITM_ID AND A.WFL_ID = C.WFL_ID ORDER BY B.WFL_ACTION_TIMESTAMP ASC";*/
				//Added by Ravi Boni 0n 17-11-2017
				String sqlQuery1 = "SELECT  A.WFL_ID, C.WFL_SUBJECT,A.WFL_WITM_ACTION_COMMENT, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_STATUS, E.ECM_USER_NAME  as SENDER, D.ECM_USER_NAME as RECIPIENT, A.WFL_WITM_SYS_STATUS, A.WFL_WITM_TYPE, B.WFL_ACTION_TIMESTAMP, A.WFL_WITM_DEADLINE, B.WFL_ACTION_STATUS "
						+ " FROM DIMS_WORKFLOW_HISTORY B, DIMS_WORKFLOW C, DIMS_WORKITEM A join ECM_EMPLOYEE E ON A.WFL_SENDER = E.ECM_USER_LOGIN join ECM_EMPLOYEE D ON A.WFL_RECIPIENT = D.ECM_USER_LOGIN WHERE B.WFL_ID = "+"'"+pendingWorkItemFullHistoryList1.get(i).getWorkflowID()+"'"+ " AND "
						+ "B.WFL_ACTION_WITM_ID = A.WFL_WITM_ID AND A.WFL_ID = C.WFL_ID AND A.WFL_WITM_TYPE <> 'cc' ORDER BY B.WFL_ACTION_TIMESTAMP ASC";
				
				
				//System.out.println("Final Query   :"+sqlQuery1);
				stmt = conn.prepareStatement(sqlQuery1);
				rs = stmt.executeQuery();
				while(rs.next()) {

					PendingWorkItemDetails pendingWorkItemDetails1 = new PendingWorkItemDetails();
					String receiveDate = rs.getString("WFL_WITM_RECEIVEDON").substring(0, 10);
					pendingWorkItemDetails1.setWorkflowBeginDate(receiveDate);
					pendingWorkItemDetails1.setReceiveDate(receiveDate);
					pendingWorkItemDetails1.setDeadLine(rs.getString("WFL_WITM_DEADLINE").substring(0, 10));
					
					pendingWorkItemDetails1.setSender(rs.getString("SENDER"));
					pendingWorkItemDetails1.setRecipient(rs.getString("RECIPIENT"));
					pendingWorkItemDetails1.setWorkitemStatus(rs.getString("WFL_ACTION_STATUS"));
					pendingWorkItemDetails1.setSystemStatus(rs.getString("WFL_WITM_TYPE"));
					pendingWorkItemDetails1.setActionDate(rs.getString("WFL_ACTION_TIMESTAMP").substring(0, 10));
					pendingWorkItemDetails1.setWorkflowID(rs.getString("WFL_ID"));
					pendingWorkItemDetails1.setSubject(rs.getString("WFL_SUBJECT"));
					pendingWorkItemDetails1.setComments(rs.getString("WFL_WITM_ACTION_COMMENT"));
					
					pendingWorkItemFullHistoryList.add(pendingWorkItemDetails1);
				}

			} //for loop close

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : pendingWorkflowsFullHistory");
		return pendingWorkItemFullHistoryList;

	}


	public ArrayList<PendingWorkItemDetails> pendingWorkflowsSpecificHistory(String sender, String status, String recipient, String from, String to) throws Exception {

		//to do
		ArrayList<PendingWorkItemDetails> pendingWorkItemSpecificHistoryList = new ArrayList<PendingWorkItemDetails>();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "SELECT DISTINCT WFL_ID FROM DIMS_WORKITEM WHERE wfl_sender = "+"'"+escapeString(sender)+"'";

			if(status.equalsIgnoreCase("All")) {
				sqlQuery = sqlQuery+" AND WFL_WITM_STATUS in ('Done', 'New', 'Read','Forward','Reassign')";
			}
			else if(status.equalsIgnoreCase("Done")) {
				sqlQuery = sqlQuery+" AND wfl_witm_status in ('Done')";
			}
			else if(status.equalsIgnoreCase("Not Done")) {
				sqlQuery = sqlQuery+" AND wfl_witm_status in ('New', 'Read','Forward','Reassign')";
			}

			if(!from.equalsIgnoreCase("undefined") && !to.equalsIgnoreCase("undefined")){
				if(from.equalsIgnoreCase(to)) {
					sqlQuery = sqlQuery+" AND WFL_WITM_RECEIVEDON >"+"'"+DBUtil.convertStringtoDate(escapeString(from))+"'"+" AND WFL_WITM_RECEIVEDON <"+"'"+DBUtil.addOneDay(escapeString(to))+"'";
				}
				else {
					sqlQuery = sqlQuery+" AND WFL_WITM_RECEIVEDON >"+"'"+DBUtil.convertStringtoDate(escapeString(from))+"'"+" AND WFL_WITM_RECEIVEDON <"+"'"+DBUtil.addOneDay(escapeString(to))+"'";
				}
			}
			else if(!from.equalsIgnoreCase("undefined") && to.equalsIgnoreCase("undefined")){
				sqlQuery = sqlQuery+" AND WFL_WITM_RECEIVEDON >"+"'"+DBUtil.convertStringtoDate(escapeString(from))+"'";
			}
			else if(from.equalsIgnoreCase("undefined") && !to.equalsIgnoreCase("undefined")){
				sqlQuery = sqlQuery+" AND WFL_WITM_RECEIVEDON <"+"'"+DBUtil.addOneDay(escapeString(to))+"'";
			}
			else if(from.equalsIgnoreCase("undefined") && to.equalsIgnoreCase("undefined")){
				sqlQuery = sqlQuery;
			}			

			if(recipient != null) {
				sqlQuery = sqlQuery+" AND WFL_RECIPIENT in (";
				String recipients [] = recipient.split(",");

				for (int i = 0; i < recipients.length; i++) {
					if(recipients.length == 1) {
						sqlQuery = sqlQuery+"'"+escapeString(recipients[i])+"'"+")";
					}
					else {						
						if(i == recipients.length-1) {
							sqlQuery = sqlQuery+"'"+escapeString(recipients[i])+"'"+")";
						}
						else {
							sqlQuery = sqlQuery+"'"+escapeString(recipients[i])+"'"+", ";
						}
					}
				}
			}
			System.out.println("pendingWorkflowsSpecificHistory Sql Query :"+sqlQuery);

			stmt = conn.prepareStatement(sqlQuery);
			rs = stmt.executeQuery();
			
			ArrayList<PendingWorkItemDetails> pendingWorkItemFullHistoryList1 = new ArrayList<PendingWorkItemDetails>();
			while (rs.next()) {
				PendingWorkItemDetails pendingWorkItemDetails = new PendingWorkItemDetails();
				String workFlowId = rs.getString("WFL_ID");
				pendingWorkItemDetails.setWorkflowID(workFlowId);
				pendingWorkItemFullHistoryList1.add(pendingWorkItemDetails);
			}

			for (int i = 0; i < pendingWorkItemFullHistoryList1.size(); i++) {
				
				/*String sqlQuery1 = "SELECT  A.WFL_ID, C.WFL_SUBJECT, A.WFL_WITM_ACTION_COMMENT, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_STATUS, E.ECM_USER_NAME as SENDER, D.ECM_USER_NAME as RECIPIENT, A.WFL_WITM_SYS_STATUS, A.WFL_WITM_TYPE, B.WFL_ACTION_TIMESTAMP, A.WFL_WITM_DEADLINE, B.WFL_ACTION_STATUS "
						+ " FROM DIMS_WORKFLOW_HISTORY B, DIMS_WORKFLOW C, DIMS_WORKITEM A join ECM_EMPLOYEE E ON A.WFL_SENDER = E.ECM_USER_LOGIN join ECM_EMPLOYEE D ON A.WFL_RECIPIENT = D.ECM_USER_LOGIN WHERE B.WFL_ID = "+"'"+pendingWorkItemFullHistoryList1.get(i).getWorkflowID()+"'"+ " AND "
						+ "B.WFL_ACTION_WITM_ID = A.WFL_WITM_ID AND A.WFL_ID = C.WFL_ID AND B.WFL_ACTION_STATUS in ('New', 'Read', 'Done', 'Forward', 'Reassign') "
						+ "AND B.WFL_ACTION_USER = "+"'"+escapeString(recipient)+"'"+" ORDER BY B.WFL_ACTION_TIMESTAMP ASC";*/
				
				//Added by Ravi Boni on 17-11-2017
				String sqlQuery1 = "SELECT  A.WFL_ID, C.WFL_SUBJECT, A.WFL_WITM_ACTION_COMMENT, A.WFL_WITM_RECEIVEDON, A.WFL_WITM_STATUS, E.ECM_USER_NAME as SENDER, D.ECM_USER_NAME as RECIPIENT, A.WFL_WITM_SYS_STATUS, A.WFL_WITM_TYPE, B.WFL_ACTION_TIMESTAMP, A.WFL_WITM_DEADLINE, B.WFL_ACTION_STATUS "
						+ " FROM DIMS_WORKFLOW_HISTORY B, DIMS_WORKFLOW C, DIMS_WORKITEM A join ECM_EMPLOYEE E ON A.WFL_SENDER = E.ECM_USER_LOGIN join ECM_EMPLOYEE D ON A.WFL_RECIPIENT = D.ECM_USER_LOGIN WHERE B.WFL_ID = "+"'"+pendingWorkItemFullHistoryList1.get(i).getWorkflowID()+"'"+ " AND "
						+ "B.WFL_ACTION_WITM_ID = A.WFL_WITM_ID AND A.WFL_ID = C.WFL_ID AND B.WFL_ACTION_STATUS in ('New', 'Red', 'Done', 'Forwarded', 'Reassign') "
						+ "AND B.WFL_ACTION_USER = "+"'"+escapeString(recipient)+"'"+" AND A.WFL_WITM_TYPE <> 'cc' ORDER BY B.WFL_ACTION_TIMESTAMP ASC";
				
				System.out.println("Final Query   :"+sqlQuery1);
				stmt = conn.prepareStatement(sqlQuery1);
				rs = stmt.executeQuery();
				while(rs.next()) {

					PendingWorkItemDetails pendingWorkItemDetails1 = new PendingWorkItemDetails();
					String receiveDate = rs.getString("WFL_WITM_RECEIVEDON").substring(0, 10);
					pendingWorkItemDetails1.setWorkflowBeginDate(receiveDate);
					pendingWorkItemDetails1.setReceiveDate(receiveDate);
					pendingWorkItemDetails1.setDeadLine(rs.getString("WFL_WITM_DEADLINE").substring(0, 10));
					
					String sqlQuery2 = "SELECT ECM_USER_NAME from ECM_EMPLOYEE where ECM_USER_LOGIN ='"+escapeString(sender)+"'";
					stmt1 = conn.prepareStatement(sqlQuery2);
					rs1 = stmt1.executeQuery();
					while(rs1.next()) {
						pendingWorkItemDetails1.setSender(rs1.getString("ECM_USER_NAME"));
					}
					rs1.close();
					stmt1.close();
					
					String sqlQuery3 = "SELECT ECM_USER_NAME from ECM_EMPLOYEE where ECM_USER_LOGIN ='"+escapeString(recipient)+"'";
					stmt1 = conn.prepareStatement(sqlQuery3);
					rs1 = stmt1.executeQuery();
					while(rs1.next()) {
						pendingWorkItemDetails1.setRecipient(rs1.getString("ECM_USER_NAME"));
					}
					rs1.close();
					stmt1.close();
					
					pendingWorkItemDetails1.setWorkitemStatus(rs.getString("WFL_ACTION_STATUS"));
					pendingWorkItemDetails1.setSystemStatus(rs.getString("WFL_WITM_TYPE"));
					pendingWorkItemDetails1.setActionDate(rs.getString("WFL_ACTION_TIMESTAMP").substring(0, 10));
					//WFL_D for differenciation of excel sheet creation
					pendingWorkItemDetails1.setWorkflowID(rs.getString("WFL_ID"));
					pendingWorkItemDetails1.setSubject(rs.getString("WFL_SUBJECT"));
					pendingWorkItemDetails1.setComments(rs.getString("WFL_WITM_ACTION_COMMENT"));

					pendingWorkItemSpecificHistoryList.add(pendingWorkItemDetails1);
				}


			} //for loop close

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			
			conn.close();
		}
		logger.info("Exit Method : pendingWorkflowsSpecificHistory");
		return pendingWorkItemSpecificHistoryList;

	}

	public ArrayList<WorkItemDetails> documentsScanned(String json, String division, String user, String from, String to){

		//to do
		return null;
	}
	
	public String getDepartmentName(Long dptCode) throws Exception {
		String result = "";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {
			if(dptCode != 5){
				conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
						DBConfiguration.getInstance().USER_NAME,
						DBConfiguration.getInstance().PASSWORD,
						DBConfiguration.getInstance().JDBC_DRIVER);

				sqlQuery = "SELECT ECM_DEPARTMENT FROM ECM_DEPARTMENT WHERE ECM_DEPT_CODE=?";

				stmt = conn.prepareStatement(sqlQuery);			
				stmt.setLong(1, dptCode);
				rs = stmt.executeQuery();

				if (rs.next()) {
					result = rs.getString("ECM_DEPARTMENT");
				}
				rs.close();
				stmt.close();
				conn.close();

			}else{
				result = "Top Management";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		logger.info("Exit Method : getDelegatedUsers");
		return result;
	}
	
	public ArrayList<Employee> getFilterUsers(String filter, String deptCode) throws Exception {
		logger.info("Started Method : getDepartmentUsers  Method parameter witem_id:"+filter);
		ArrayList<Employee> empList = new ArrayList<Employee>();
		Connection conn = null;
		ResultSet rs = null;
		CallableStatement callableStatement = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);

			String getuserlistSql = "{call GET_USER_FILTER(?,?)}";

			callableStatement = conn.prepareCall(getuserlistSql);
			callableStatement.setString(1, escapeString(filter));
			callableStatement.setString(2, deptCode);

			rs = callableStatement.executeQuery();


			while (rs.next()) {
				Employee emp = new Employee();
				emp.setEmployeeLogin(rs.getString("ECM_USER_LOGIN"));
				emp.setEmployeeName(rs.getString("ECM_USER_NAME"));
				empList.add(emp);
			}

		} catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}finally{
			rs.close();
			callableStatement.close();
			conn.close();
		}
		logger.info("Exit Method : getDepartmentUsers");
		return empList;
	}

	public int getOldDimsDepartmentId(String departmentId) throws Exception {
		int oldDepartmentId =0;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "SELECT OLD_DIMS_DEPARTMENT_ID FROM ECM_DEPARTMENT WHERE ECM_DEPT_CODE=?";

			stmt = conn.prepareStatement(sqlQuery);			
			stmt.setString(1, escapeIntString(departmentId));
			rs = stmt.executeQuery();

			if (rs.next()) {
				oldDepartmentId = rs.getInt("OLD_DIMS_DEPARTMENT_ID");
			}


		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getDelegatedUsers");
		return oldDepartmentId;
	}

	public String getNameFromOldDimsDepartmentId(String oldDepartmentId) {
		String deptName = "";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {
			if(!oldDepartmentId.equalsIgnoreCase("5")){
				conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
						DBConfiguration.getInstance().USER_NAME,
						DBConfiguration.getInstance().PASSWORD,
						DBConfiguration.getInstance().JDBC_DRIVER);

				sqlQuery = "SELECT ECM_DEPARTMENT FROM ECM_DEPARTMENT WHERE OLD_DIMS_DEPARTMENT_ID=" + oldDepartmentId;

				stmt = conn.prepareStatement(sqlQuery);			
				rs = stmt.executeQuery();

				if (rs.next()) {
					deptName = rs.getString("ECM_DEPARTMENT");
				}
				rs.close();
				stmt.close();
				conn.close();

			}else{
				deptName = "Top Management";
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		} 
		logger.info("Exit Method : getNameFromOldDimsDepartmentId");
		return deptName;
	}
	
	public ArrayList<DocumentType> getDocumentType() throws Exception {
		logger.info("Start Method : getDocumentType");
		ArrayList<DocumentType> docTypeList = new ArrayList<DocumentType>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "SELECT DOCTYPE_ID, DOCTYPE_NAME FROM DIMS_DOCUMENT_TYPE";

			stmt = conn.prepareStatement(sqlQuery);			
			rs = stmt.executeQuery();

			while (rs.next()) {
				
				DocumentType documentType = new DocumentType();
				documentType.setDocType(rs.getString("DOCTYPE_NAME"));
				documentType.setId(rs.getString("DOCTYPE_ID"));
				docTypeList.add(documentType);
			}


		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getDocumentType");
		return docTypeList;
	}

	public ArrayList<Employee> getUsersForDivisionReports(String division_code, String dept_code) throws Exception {
		logger.info("Start Method : getUsersForDivisionReports");
		ArrayList<Employee> empList = new ArrayList<Employee>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			if(division_code!=null && division_code.equalsIgnoreCase("-1")){
				/*sqlQuery = "SELECT ECM_USER_LOGIN,ECM_USER_NAME,ECM_USER_EMAIL,"
						+ " ECM_DESIGNATION,ECM_USER_TITLE,ECM_DIVISION_CODE,ECM_DIVISION "
						+ " FROM ECM_EMPLOYEE "
						+ " WHERE ECM_DIVISION_CODE is not null "
						+ " AND (ECM_ACTIVE_USER='true' OR ECM_ACTIVE_USER=1)"
						+ " AND ECM_USER_LOGIN IN "
						+ "(Select ECM_ASST_EMP_LOGIN from ECM_ASSISTANT_EMPLOYEE where ECM_ASST_EMP_SUPERVISOR "
						+ "IN(SELECT ECM_USER_LOGIN FROM ECM_EMPLOYEE where ECM_DEPT_CODE = ?) )";*/
				sqlQuery = "SELECT ECM_USER_LOGIN,ECM_USER_NAME,ECM_USER_EMAIL,ECM_DESIGNATION,ECM_USER_TITLE,ECM_DIVISION_CODE,ECM_DIVISION FROM ECM_EMPLOYEE"
						+ " WHERE ECM_DEPT_CODE = ? AND (ECM_ACTIVE_USER='yes' OR ECM_ACTIVE_USER=1) AND ECM_JOB_TITLE = 'SEC'";
			}else{
				
				/*sqlQuery = "SELECT ECM_USER_LOGIN,ECM_USER_NAME,ECM_USER_EMAIL,"
						+ " ECM_DESIGNATION,ECM_USER_TITLE,ECM_DIVISION_CODE,ECM_DIVISION "
						+ " FROM ECM_EMPLOYEE "
						+ " WHERE ECM_DIVISION_CODE = ? "
						+ " AND (ECM_ACTIVE_USER='true' OR ECM_ACTIVE_USER=1)"
						+ " AND ECM_USER_LOGIN IN "
						+ "(Select ECM_ASST_EMP_LOGIN from ECM_ASSISTANT_EMPLOYEE where ECM_ASST_EMP_SUPERVISOR "
						+ "IN(SELECT ECM_USER_LOGIN FROM ECM_EMPLOYEE where ECM_DEPT_CODE = ?) )";*/
				sqlQuery = "SELECT ECM_USER_LOGIN,ECM_USER_NAME,ECM_USER_EMAIL,ECM_DESIGNATION,ECM_USER_TITLE,ECM_DIVISION_CODE,ECM_DIVISION FROM ECM_EMPLOYEE"
						+ " WHERE ECM_DEPT_CODE = ? AND (ECM_ACTIVE_USER='yes' OR ECM_ACTIVE_USER=1) AND ECM_DIVISION_CODE = ? AND ECM_JOB_TITLE = 'SEC'";
			}
			

			stmt = conn.prepareStatement(sqlQuery);	
			if(division_code!=null && division_code.equalsIgnoreCase("-1")){
				stmt.setString(1, escapeString(dept_code));
			}else{
				stmt.setString(1, escapeString(dept_code));
				stmt.setString(2, escapeString(division_code));
			}
			
			rs = stmt.executeQuery();

			while (rs.next()) {
				
				Employee emp = new Employee();
				emp.setEmployeeLogin(rs.getString("ECM_USER_LOGIN"));
				emp.setEmployeeName(rs.getString("ECM_USER_NAME"));
				emp.setEmployeeDesignation(rs.getString("ECM_USER_TITLE"));
				emp.setEmployeeEmail(rs.getString("ECM_USER_EMAIL"));
				EmployeeDivision empDiv = new EmployeeDivision();
				empDiv.setEmpDivision(rs.getString("ECM_DIVISION"));
				empDiv.setEmpDivisionCode(rs.getInt("ECM_DIVISION_CODE"));
				emp.setEmployeeDivision(empDiv);
				empList.add(emp);
			}


		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getUsersForDivisionReports");
		return empList;
	}

	public String getDocTypeName(int docValue) throws Exception {

		logger.info("Start Method : getDocTypeName");
		String docTypeName = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			sqlQuery = "SELECT DOCTYPE_NAME FROM DIMS_DOCUMENT_TYPE WHERE DOCTYPE_ID = ?";
			

			stmt = conn.prepareStatement(sqlQuery);	
			stmt.setInt(1, docValue);
			
			rs = stmt.executeQuery();

			while (rs.next()) {
				docTypeName = rs.getString("DOCTYPE_NAME");
			}


		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		logger.info("Exit Method : getUsersForDivisionReports");
		return docTypeName;
	}
	
	public static String getUserFullName(String user_login) throws Exception{
		String userFullName = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			sqlQuery = "SELECT ECM_USER_NAME FROM ECM_EMPLOYEE WHERE ECM_USER_LOGIN =?";
			
			stmt = conn.prepareStatement(sqlQuery);	
			stmt.setString(1, user_login);
			
			rs = stmt.executeQuery();

			while (rs.next()) {
				userFullName = rs.getString("ECM_USER_NAME");
			}


		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		
		return userFullName;
	}

	public String getDocumentSiteName(int docFrom) throws Exception {
		String siteName = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,
					DBConfiguration.getInstance().USER_NAME,
					DBConfiguration.getInstance().PASSWORD,
					DBConfiguration.getInstance().JDBC_DRIVER);
			sqlQuery = "SELECT SITE_NAME FROM DIMS_SITE WHERE SITE_ID =?";
			
			stmt = conn.prepareStatement(sqlQuery);	
			stmt.setInt(1, docFrom);
			
			rs = stmt.executeQuery();

			while (rs.next()) {
				siteName = rs.getString("SITE_NAME");
			}


		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		
		return siteName;
	}

	public String getDivisionsName(String division) throws Exception {
		logger.info("Started Method : getDivisions  Method parameter division : "+division);
		String divisionName = null;
		Connection conn = null;
		 PreparedStatement stmt = null;
		 ResultSet rs = null;
		 String sqlQuery= null;
		try {
			
			
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery = "SELECT ECM_DIVISION FROM ECM_DIVISION WHERE ECM_DIVISION_CODE=? ORDER BY ECM_DIVISION DESC";

			stmt = conn.prepareStatement(sqlQuery);
			stmt.setString(1, escapeIntString(division));
			rs = stmt.executeQuery();

			while (rs.next()) {
				divisionName = rs.getString("ECM_DIVISION");
			}

		} catch(Exception e){
		    logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		    stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : getDivisions");
		return divisionName;
	}
	
	private void executeEmailNotificationSP(String wiID, String newWitemID, String oldWitemID, Connection conn) 
	{
		try {
			CallableStatement callableStatement = null;
			try {
				String getDBUSERSql = "{call DIMS_NOTIFICATION(?,?,?)}";
				callableStatement = conn.prepareCall(getDBUSERSql);
				callableStatement.setString(1, escapeString(wiID));
				callableStatement.setString(2, escapeString(newWitemID));
				callableStatement.setString(3, escapeString(oldWitemID));
				
				callableStatement.executeUpdate();
				
				callableStatement.close();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				if(callableStatement != null)
					callableStatement.close();
			}
			finally{
				if(callableStatement != null)
					callableStatement.close();
			}
		} catch (Exception oe) {
			logger.error("Error while sending email notification: " + oe.getMessage());
		}
	}
	
	public String getSupervisorDetails(String user_login) throws Exception {
		logger.info("Started Method : getSupervisor  Method parameter user_login:"+user_login);
		String supervisor = "";
		Connection conn = null;
		 PreparedStatement stmt = null;
		 ResultSet rs = null;
		 String sqlQuery= null;
		try {
			conn = DBUtil.getConnection(DBConfiguration.getInstance().DB_URL,DBConfiguration.getInstance().USER_NAME,DBConfiguration.getInstance().PASSWORD,DBConfiguration.getInstance().JDBC_DRIVER);

			sqlQuery ="SELECT ECM_ASST_EMP_SUPERVISOR FROM ECM_ASSISTANT_EMPLOYEE WHERE ECM_ASST_EMP_LOGIN=?";
			
			stmt = conn.prepareStatement(sqlQuery);	
			stmt.setString(1, escapeString(user_login));
			rs = stmt.executeQuery();

			while (rs.next()) {
				supervisor = rs.getString("ECM_ASST_EMP_SUPERVISOR");
			}
			
		} catch(Exception e){
			logger.error(e.getMessage(), e);
		    throw new Exception(e.getMessage());
		 }finally{
		 	rs.close();
		 	stmt.close();
		    conn.close();
		 }
		logger.info("Exit Method : getSupervisor ");
		return supervisor;
	}
}
