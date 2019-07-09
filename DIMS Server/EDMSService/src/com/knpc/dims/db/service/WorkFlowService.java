package com.knpc.dims.db.service;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.json.java.JSONObject;
import com.knpc.dims.db.DBAdaptor;
import com.knpc.dims.employee.beans.Employee;
import com.knpc.dims.filenet.FileNetAdaptor;
import com.knpc.dims.filenet.beans.DocumentBean;
import com.knpc.dims.filenet.beans.DocumentTransferBean;
import com.knpc.dims.filenet.beans.FileObject;
import com.knpc.dims.filenet.beans.PropertyBean;
import com.knpc.dims.filenet.beans.SearchResultBean;
import com.knpc.dims.filenet.util.FilenetSystemConfiguration;
import com.knpc.dims.workflow.beans.DIMSSite;
import com.knpc.dims.workflow.beans.DivisionWF;
import com.knpc.dims.workflow.beans.PendingWorkItemDetails;
import com.knpc.dims.workflow.beans.WorkFlowDetails;
import com.knpc.dims.workflow.beans.WorkFlowSearchBean;
import com.knpc.dims.workflow.beans.WorkItemDetails;
import com.knpc.dims.workflow.beans.WorkItemHistory;
import com.knpc.dims.workflow.beans.WorkflowAttachments;
import com.knpc.dims.workflow.beans.WorkflowRecipient;

public class WorkFlowService {

	private HttpServletRequest req;
	private HttpServletResponse resp;
	
	public WorkFlowService(HttpServletRequest req, HttpServletResponse resp) {
		this.req = req;
		this.resp = resp;
	}

	public WorkFlowService() {

	}

	public ArrayList<WorkItemDetails> getInboxItems(String user_login) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<WorkItemDetails> inboxList = new ArrayList<WorkItemDetails>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
		 if(adaptor.validateRequest(req, resp)) {
			 inboxList = db.getInboxItems(user_login);
		 }
		return inboxList;
	}

	public WorkItemDetails getWorkItemDetails(String witem_id) throws Exception {
		DBAdaptor db = new DBAdaptor();
		WorkItemDetails inboxList = new WorkItemDetails();
		FileNetAdaptor adaptor = new FileNetAdaptor();
		 if(adaptor.validateRequest(req, resp)) {
			 //inboxList = db.getWorkItemDetails(witem_id); SK
			 inboxList = db.getWorkItemDetailsNew(witem_id);
			// db.executeWorkItemReadSP(witem_id, null); // Temporary SK 26/5/2017
		}
		return inboxList;
	}
	
	public WorkItemDetails getAndReadWorkItemDetails(String witem_id, String userLogin) throws Exception {
		DBAdaptor db = new DBAdaptor();
		WorkItemDetails inboxList = new WorkItemDetails();
		FileNetAdaptor adaptor = new FileNetAdaptor();
		 if(adaptor.validateRequest(req, resp)) {
			 inboxList = db.getWorkItemDetailsNew(witem_id);
			 db.executeWorkItemReadSP(witem_id, userLogin);
		}
		return inboxList;
	}

	public ArrayList<WorkItemDetails> getSentItems(String user_login) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<WorkItemDetails> inboxList = new ArrayList<WorkItemDetails>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
		 if(adaptor.validateRequest(req, resp)) {
			 inboxList = db.getSentItems(user_login);
		 }
		return inboxList;
	}

	public ArrayList<String> getWorkitemActions(String witemId, String queue, String user) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<String> actionList = new ArrayList<String>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
		 if(adaptor.validateRequest(req, resp)) {
			 actionList = db.getWorkitemActions(witemId, queue, user);
		 }
		return actionList;
	}
	
	public ArrayList<WorkItemDetails> getWorkitemSentItems(String witemId) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<WorkItemDetails> inboxList = new ArrayList<WorkItemDetails>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
		 if(adaptor.validateRequest(req, resp)) {
			 inboxList = db.getWorkitemSentItems(witemId);
		 }
		return inboxList;
	}
	
	public ArrayList<WorkItemDetails> getInboxItemsFrom(String user_login,String sender_login) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<WorkItemDetails> inboxList = new ArrayList<WorkItemDetails>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
		 if(adaptor.validateRequest(req, resp)) {
			 inboxList = db.getInboxItemsFrom(user_login,sender_login);
		 }
		return inboxList;
	}

	public ArrayList<WorkItemDetails> filterInboxItems(String user_login,String filterType, String folderFilter, String sortOrder, String sortColumn, String page, String perPage) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<WorkItemDetails> filterInboxItemsList = new ArrayList<WorkItemDetails>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
		 if(adaptor.validateRequest(req, resp)){
			 filterInboxItemsList = db.filterInboxItems(user_login,filterType,folderFilter,sortOrder,sortColumn,page,perPage);
		 }
		return filterInboxItemsList;
	}

	public ArrayList<WorkItemDetails> searchInbox(String user_login,String filterType, String searchCriteria) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<WorkItemDetails> filterInboxItemsList = new ArrayList<WorkItemDetails>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
		 if(adaptor.validateRequest(req, resp)) {
			 filterInboxItemsList = db.searchInbox(user_login,filterType,searchCriteria);
		 }
		return filterInboxItemsList;
	}

	public String forwardWorkItem(HttpServletRequest req) throws Exception {
		WorkFlowDetails workFlowDetails= null;
		DocumentBean documentBean = null;
		FileObject fileObject = null;
		DocumentTransferBean objectTransferBean = new DocumentTransferBean();
		ArrayList<FileObject> fileObjectList = new ArrayList<FileObject>();
		ArrayList<WorkflowAttachments> WorkflowAttachmentsList = new ArrayList<WorkflowAttachments>();
		ServletFileUpload uploader = new ServletFileUpload(new DiskFileItemFactory());
		DBAdaptor db = new DBAdaptor();
		FileNetAdaptor adaptor = new FileNetAdaptor();
	    try {
	        List<FileItem> parseRequest = uploader.parseRequest(req);
	        for (FileItem fileItem : parseRequest) {
	            if (fileItem.isFormField()) {
	            	if(fileItem.getFieldName().equalsIgnoreCase("workflowJSONString")){
	            		ObjectMapper mapper = new ObjectMapper();
	            		workFlowDetails = mapper.readValue(fileItem.getString(), new TypeReference<WorkFlowDetails>(){});
	            	}if(fileItem.getFieldName().equalsIgnoreCase("documentJSONString")){
	            		ObjectMapper mapper = new ObjectMapper();
	            		documentBean = mapper.readValue(fileItem.getString(), new TypeReference<DocumentBean>(){});
	            	}if(fileItem.getFieldName().equalsIgnoreCase("folderPath")){
	            		objectTransferBean.setFolderPath(fileItem.getString());
	            	}if(fileItem.getFieldName().equalsIgnoreCase("documentClass")){
	            		objectTransferBean.setDocClass(fileItem.getString());
	            	}
	            }else{
	            	fileObject = new FileObject();
	            
	            	if(fileItem.getName().contains("\\")){
	            		String fullPath = fileItem.getName();
	            		int index = fileItem.getName().lastIndexOf("\\");
		            	String fileName = fullPath.substring(index + 1);
		            	fileObject.setFileName(URLDecoder.decode(fileName,"UTF-8"));
	            	}else{
	            		fileObject.setFileName(URLDecoder.decode(fileItem.getName(),"UTF-8"));
	            	}
	            	
	            	fileObject.setInputStream(fileItem.getInputStream());
	            	fileObjectList.add(fileObject);
	            }
	        }
	        String[] recipientArray =  getRecipient(workFlowDetails,db);
        	String[] existingRecipientArray =  getExistingRecipient(db.getSentItemRecipients(workFlowDetails.getWorkItemDetails().getWorkflowWorkItemID()),db);
        	String[] finalList = new String[recipientArray.length+existingRecipientArray.length+1];
        	int count = 0;
            
            for(int i = 0; i<recipientArray.length; i++) { 
            	finalList[i] = recipientArray[i];
               count++;
            } 
            for(int j = 0;j<existingRecipientArray.length;j++) { 
            	finalList[count++] = existingRecipientArray[j];
            } 
        	
            finalList[count++] = workFlowDetails.getWorkItemDetails().getWorkflowItemRootSender();
        	
	        if(fileObjectList.size()>0){
	        	
		        for (int i = 0; i < fileObjectList.size(); i++) {
		        	
		        	ArrayList<PropertyBean> pbList = new ArrayList<PropertyBean>();
			   		 if(adaptor.validateRequest(req, resp)) {
			   			PropertyBean pb1 = new PropertyBean();
		            	pb1.setPropertyName("EmailSubject");
		            	pb1.setPropertyValue(fileObjectList.get(i).getFileName());
		            	
		            	PropertyBean pb2 = new PropertyBean();
		            	pb2.setPropertyName("DocumentTitle");
		            	pb2.setPropertyValue(fileObjectList.get(i).getFileName());
		            	pbList.add(pb1);
		            	pbList.add(pb2);
		            	documentBean = new DocumentBean();
		            	documentBean.setProperties(pbList);
		            	
			   			String documentId = adaptor.addDocument(documentBean,objectTransferBean,fileObjectList.get(i),FilenetSystemConfiguration.getInstance().OS_NAME,finalList);
						
			        	WorkflowAttachments workflowAttachment = new WorkflowAttachments();
			        	workflowAttachment.setWorkflowDocumentId(documentId); 
			        	workflowAttachment.setWorkflowAttachmentType("Attachment");
			        	WorkflowAttachmentsList.add(workflowAttachment);
			   		 }
		        
				}
	        }

	        if(workFlowDetails.getWorkItemDetails().getWorkflowAttachments().size()>0){
	        	ArrayList<WorkflowAttachments> WorkflowAttachmentsList1 = workFlowDetails.getWorkItemDetails().getWorkflowAttachments();
	        	 for (int i = 0; i < WorkflowAttachmentsList1.size(); i++) {
	        		 adaptor.setDocumentSecurity(WorkflowAttachmentsList1.get(i).getWorkflowDocumentId(), finalList);
	        		 WorkflowAttachmentsList.add(WorkflowAttachmentsList1.get(i));
				}
	        }
			db.forwardWorkItemFromSP(workFlowDetails,WorkflowAttachmentsList); // SK 27-May-2017
			
	    }catch (FileUploadException e) {
	        e.printStackTrace();
	        throw new Exception(e.getMessage());
	    }
	return "Work Item is forwarded successfully.";
	}

	private String[] getExistingRecipient(ArrayList<WorkflowRecipient> sentItemRecipients, DBAdaptor db) throws Exception {
		ArrayList<WorkflowRecipient> workflowRecipientList =  sentItemRecipients;
		 String[] recipientArray = null;
		 recipientArray = new String[workflowRecipientList.size()];
		 for (int j = 0; j < workflowRecipientList.size(); j++) {
			 String groupName = db.getDocumentSecurityfromTable(workflowRecipientList.get(j).getWorkflowRecipient());
			 if(groupName!=null && !groupName.equalsIgnoreCase("")){
				 recipientArray[j] = groupName;
			 }else{
				 recipientArray[j] = workflowRecipientList.get(j).getWorkflowRecipient();
			 }
		 }
		 return recipientArray;
	}

	private String[] getRecipient(WorkFlowDetails workFlowDetails, DBAdaptor db) throws Exception {
		ArrayList<WorkflowRecipient> workflowRecipientList =  workFlowDetails.getWorkItemDetails().getWorkflowRecipientList();
		 String[] recipientArray = null;
		 recipientArray = new String[workflowRecipientList.size()];
		 for (int j = 0; j < workflowRecipientList.size(); j++) {
			 String groupName = db.getDocumentSecurityfromTable(workflowRecipientList.get(j).getWorkflowRecipient());
			 if(groupName!=null && !groupName.equalsIgnoreCase("")){
				 recipientArray[j] = groupName;
			 }else{
				 recipientArray[j] = workflowRecipientList.get(j).getWorkflowRecipient();
			 }
		 }
		 return recipientArray;
	}
	
	public ArrayList<WorkItemDetails> filterSentItems(String user_login,String filterType, String folderFilter, String sortOrder, String sortColumn, String page, String perPage) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<WorkItemDetails> filterInboxItemsList = new ArrayList<WorkItemDetails>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			filterInboxItemsList = db.filterSentItems(user_login,filterType,folderFilter,sortOrder,sortColumn,page,perPage);
  		}
		return filterInboxItemsList;
	}

	public ArrayList<WorkItemDetails> searchSentItems(String user_login,String filterType, String searchCriteria) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<WorkItemDetails> filterInboxItemsList = new ArrayList<WorkItemDetails>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			filterInboxItemsList = db.searchSentItems(user_login,filterType,searchCriteria);
  		}
		return filterInboxItemsList;
	}

	public ArrayList<WorkItemDetails> getArchiveItems(String user_login, String page, String perPage, String folderFilter, String sortOrder, String sortColumn) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<WorkItemDetails> filterInboxItemsList = new ArrayList<WorkItemDetails>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			filterInboxItemsList = db.getArchiveItems(user_login,page,perPage,folderFilter,sortOrder,sortColumn);
  		}
		return filterInboxItemsList;
	}

	public ArrayList<WorkflowAttachments> getDocumentId(String witem_id) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<WorkflowAttachments> workflowAttachmentsList = new ArrayList<WorkflowAttachments>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			workflowAttachmentsList = db.getDocumentId(witem_id);
  		}
		return workflowAttachmentsList;
	}

	public String getWorkitemParentType(String witem_id) {
		DBAdaptor db = new DBAdaptor();
  		return db.getWorkitemParentType(witem_id);
	}
	
	public String addUserWorkItem(String jsonString) throws Exception {
		String response = "";
		try {
			ObjectMapper mapper = new ObjectMapper();
			WorkFlowDetails workFlowDetails = mapper.readValue(jsonString, new TypeReference<WorkFlowDetails>(){});
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
	  		if(adaptor.validateRequest(req, resp)) {
	  			//response = db.addUserWorkItem(workFlowDetails);
	  			response = db.addUserWorkItemFromSP(workFlowDetails);
	  			String[] recipientArray = null;
				ArrayList<WorkflowAttachments> wfAtth = null;
	  			if(response.equalsIgnoreCase("User added successfully."))
	  			{
					 ArrayList<WorkflowRecipient> workflowRecipientList =  workFlowDetails.getWorkItemDetails().getWorkflowRecipientList();
					 wfAtth = workFlowDetails.getWorkItemDetails().getWorkflowAttachments();	
					 recipientArray = new String[workflowRecipientList.size()];
					 for (int j = 0; j < workflowRecipientList.size(); j++) {
						 String groupName = db.getDocumentSecurityfromTable(workflowRecipientList.get(j).getWorkflowRecipient());
						 if(groupName!=null && !groupName.equalsIgnoreCase("")){
							 recipientArray[j] = groupName;
						 }else{
							 recipientArray[j] = workflowRecipientList.get(j).getWorkflowRecipient();
						 }
					}
					 for (int i = 0; i < wfAtth.size(); i++) {
						 adaptor.setDocumentSecurity(wfAtth.get(i).getWorkflowDocumentId(),recipientArray);	
					}
					
	  			}
	  		}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
		return response;
		
	}

	public String reassignWorkItem(String jsonString) throws Exception {
		
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode node = objectMapper.readValue(jsonString, JsonNode.class);
		
		JsonNode workItemNode = node.get("workItemDetails");
		JsonNode selectedRecipientNode = node.get("selectedRecipient");
		
		WorkFlowDetails workFlowDetails = objectMapper.readValue("{\"workItemDetails\": "+workItemNode.toString()+"}", new TypeReference<WorkFlowDetails>(){});
		DBAdaptor db = new DBAdaptor();
		String response = "";
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			//response = db.reassignWorkItem(workFlowDetails);
  			response = db.reassignWorkItemFromSP(workFlowDetails,selectedRecipientNode.get("workflowRecipient").asText());
  			String[] recipientArray = null;
			ArrayList<WorkflowAttachments> wfAtth = null;
  			if(response.equalsIgnoreCase("Work Item reassigned successfully.")){

				 ArrayList<WorkflowRecipient> workflowRecipientList =  workFlowDetails.getWorkItemDetails().getWorkflowRecipientList();
				 wfAtth = workFlowDetails.getWorkItemDetails().getWorkflowAttachments();	
				 recipientArray = new String[workflowRecipientList.size()];
				 for (int j = 0; j < workflowRecipientList.size(); j++) {
					 String groupName = db.getDocumentSecurityfromTable(workflowRecipientList.get(j).getWorkflowRecipient());
					 if(groupName!=null && !groupName.equalsIgnoreCase("")){
						 recipientArray[j] = groupName;
					 }else{
						 recipientArray[j] = workflowRecipientList.get(j).getWorkflowRecipient();
					 }
				}
				 for (int i = 0; i < wfAtth.size(); i++) {
					 adaptor.setDocumentSecurity(wfAtth.get(i).getWorkflowDocumentId(),recipientArray);	
				}
				
			 
  			}
  		}
		return response;
	}

	public String archiveWorkItem(String user_login, String workItemID, String actionBy) throws Exception {
		DBAdaptor db = new DBAdaptor();
		String response = "";
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			//response = db.archiveWorkItem(actionBy,workItemID);
  			response = db.archiveWorkItemFromSP(user_login,workItemID,actionBy);
  		}
		return response;
	}

	public String completeWorkFlow(String workItemId, String user_login) throws Exception {
		DBAdaptor db = new DBAdaptor();
		String response = "";
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			//response = db.completeWorkFlow(workItemId,user_login);
  			response = db.completeWorkFlowFromSP(workItemId,user_login); //SK 27/5/17
  		}
		return response;
	}

	public String doneWorkItem(HttpServletRequest req) throws Exception {

		WorkFlowDetails workFlowDetails= null;
		DocumentBean documentBean = null;
		FileObject fileObject = null;
		DBAdaptor db = new DBAdaptor();
		DocumentTransferBean objectTransferBean = new DocumentTransferBean();
		ArrayList<FileObject> fileObjectList = new ArrayList<FileObject>();
		ArrayList<WorkflowAttachments> WorkflowAttachmentsList = new ArrayList<WorkflowAttachments>();
		ServletFileUpload uploader = new ServletFileUpload(new DiskFileItemFactory());
		FileNetAdaptor adaptor = new FileNetAdaptor();
	    try {
	        List<FileItem> parseRequest = uploader.parseRequest(req);
	        for (FileItem fileItem : parseRequest) {
	            if (fileItem.isFormField()) {
	            	if(fileItem.getFieldName().equalsIgnoreCase("workflowJSONString")){
	            		ObjectMapper mapper = new ObjectMapper();
	            		workFlowDetails = mapper.readValue(fileItem.getString(), new TypeReference<WorkFlowDetails>(){});
	            	}/*if(fileItem.getFieldName().equalsIgnoreCase("documentJSONString")){
	            		ObjectMapper mapper = new ObjectMapper();
	            		documentBean = mapper.readValue(fileItem.getString(), new TypeReference<DocumentBean>(){});
	            	}*/if(fileItem.getFieldName().equalsIgnoreCase("DocumentClass")){
	            		objectTransferBean.setDocClass(fileItem.getString());
	            	}
	            }else{
	            	fileObject = new FileObject();
	            	
	            	if(fileItem.getName().contains("\\")){
	            		String fullPath = fileItem.getName();
	            		int index = fileItem.getName().lastIndexOf("\\");
		            	String fileName = fullPath.substring(index + 1);
		            	fileObject.setFileName(URLDecoder.decode(fileName,"UTF-8"));
	            	}else{
	            		fileObject.setFileName(URLDecoder.decode(fileItem.getName(),"UTF-8"));
	            	}
	            	
	            	fileObject.setInputStream(fileItem.getInputStream());
	            	fileObjectList.add(fileObject);
	            }
	        }
	        
	        String[] recipientArray =  getRecipient(workFlowDetails,db);
        	String sender =  db.getSentItemSender(workFlowDetails.getWorkItemDetails().getWorkflowWorkItemID());
        	String[] finalList = new String[recipientArray.length+1];
        	int count = 0;
            
            for(int i = 0; i<recipientArray.length; i++) { 
            	finalList[i] = recipientArray[i];
               count++;
            } 
           	finalList[count++] = sender;
           	
			if (fileObjectList.size() > 0) {

				/*WorkflowAttachments wfAtth = workFlowDetails.getWorkflowAttachment();
				String[] recipientArray = new String[1];
				String groupName = db.getDocumentSecurityfromTable(workFlowDetails.getWorkItemDetails().getWorkflowItemRootSender());
				if (groupName != null && !groupName.equalsIgnoreCase("")) {
					recipientArray[0] = groupName;
				} else {
					recipientArray[0] = workFlowDetails.getWorkItemDetails().getWorkflowItemRootSender();
				}
				if(wfAtth!=null){
					adaptor.setDocumentSecurity(wfAtth.getWorkflowAttachmentId(),recipientArray);
				}
				*/
				
				for (int i = 0; i < fileObjectList.size(); i++) {
					ArrayList<PropertyBean> pbList = new ArrayList<PropertyBean>();
			   		 if(adaptor.validateRequest(req, resp)) {
			   			PropertyBean pb1 = new PropertyBean();
		            	pb1.setPropertyName("EmailSubject");
		            	pb1.setPropertyValue(fileObjectList.get(i).getFileName());
		            	
		            	PropertyBean pb2 = new PropertyBean();
		            	pb2.setPropertyName("DocumentTitle");
		            	pb2.setPropertyValue(fileObjectList.get(i).getFileName());
		            	pbList.add(pb1);
		            	pbList.add(pb2);
		            	documentBean = new DocumentBean();
		            	documentBean.setProperties(pbList);
						String documentId = adaptor.addDocument(documentBean, objectTransferBean,fileObjectList.get(i), FilenetSystemConfiguration.getInstance().OS_NAME,finalList);
						WorkflowAttachments workflowAttachment = new WorkflowAttachments();
						workflowAttachment.setWorkflowDocumentId(documentId);
						workflowAttachment.setWorkflowAttachmentType("Attachment");
						WorkflowAttachmentsList.add(workflowAttachment);
					}
				}
			}
			
			if(workFlowDetails.getWorkItemDetails().getWorkflowAttachments().size()>0){
	        	ArrayList<WorkflowAttachments> WorkflowAttachmentsList1 = workFlowDetails.getWorkItemDetails().getWorkflowAttachments();
	        	 for (int i = 0; i < WorkflowAttachmentsList1.size(); i++) {
	        		 adaptor.setDocumentSecurity(WorkflowAttachmentsList1.get(i).getWorkflowDocumentId(), finalList);
	        		 WorkflowAttachmentsList.add(WorkflowAttachmentsList1.get(i));
				}
	        }
			//String responseString = db.doneWorkItem(workFlowDetails,WorkflowAttachmentsList);
			db.doneWorkItemFromSP(workFlowDetails,WorkflowAttachmentsList); // SK 27 May
	    } catch (FileUploadException e) {
	        e.printStackTrace();
	        throw new Exception(e.getMessage());
	    }
	return "Work Item is done successfully.";
	
	}

	public ArrayList<WorkItemHistory> getWorkFlowHistory(String workItemId, String workflowId) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<WorkItemHistory> response = new ArrayList<WorkItemHistory>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			response = db.getWorkFlowHistory(workItemId,workflowId);
  		}
		return response;
	}
	
	public ArrayList<WorkItemHistory> getWorkFlowHistoryByDept(String workItemId, String workflowId, String user_login) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<WorkItemHistory> response = new ArrayList<WorkItemHistory>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			response = db.getWorkFlowHistoryByDept(workItemId,workflowId,user_login);
  		}
		return response;
	}

	public ArrayList<WorkItemDetails> searchWorkFlow(String jsonString) throws Exception {
		ArrayList<WorkItemDetails> response=new ArrayList<WorkItemDetails>();
		try {
			ObjectMapper mapper = new ObjectMapper();
           JSONObject objJson = new JSONObject();
           JSONObject js = objJson.parse(jsonString);
           js.remove("recieveDate");
			WorkFlowSearchBean workFlowSearchBean = mapper.readValue(js.toString(), new TypeReference<WorkFlowSearchBean>(){});
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
	  		if(adaptor.validateRequest(req, resp)) {
	  			response = db.searchWorkFlow(workFlowSearchBean);
	  		}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		
		return response;
	}

	public ArrayList<String> getWorkflowInstructions() throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<String> response = new ArrayList<String>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			response = db.getWorkflowInstructions();
  		}
		return response;
	}

	public ArrayList<WorkflowRecipient> getSentItemRecipients(String witmID) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<WorkflowRecipient> response = new ArrayList<WorkflowRecipient>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			response = db.getSentItemRecipients(witmID);
  		}
		return response;
	}
	
	public ArrayList<WorkItemDetails> quickSearchWorkFlow(String subject, String user_login) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<WorkItemDetails> response = new ArrayList<WorkItemDetails>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			response = db.quickSearchWorkFlow(subject,user_login);
  		}
		return response;
	}

	public ArrayList<WorkFlowDetails> getDocumentHistory(String docId) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<WorkFlowDetails> response = new ArrayList<WorkFlowDetails>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			response = db.getDocumentHistory(docId);
  		}
		return response;
	}

	public String createWorkflowFolder(String type, String folderName,String user_name) throws Exception {
		DBAdaptor db = new DBAdaptor();
		String userName = "";
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			userName = db.createWorkflowFolder(type,folderName,user_name);
  		}
		return userName;
	}

	public ArrayList<String> getWorkflowFolder(String type, String user_name) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<String> folderList = new ArrayList<String>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			folderList = db.getWorkflowFolder(type,user_name);
  		}
		return folderList;
	}

	public String launchWorkFlowUpdated(HttpServletRequest req2) throws Exception {
		
		ArrayList<WorkFlowDetails> workFlowDetails = null;
		FileObject fileObject = null;
		DocumentTransferBean objectTransferBean = new DocumentTransferBean();
		ArrayList<FileObject> fileObjectList = new ArrayList<FileObject>();
		ArrayList<WorkflowAttachments> WorkflowAttachmentsList = new ArrayList<WorkflowAttachments>();
		ServletFileUpload uploader = new ServletFileUpload(new DiskFileItemFactory());
		DBAdaptor db = new DBAdaptor();
		FileNetAdaptor adaptor = new FileNetAdaptor();
		
		try {
	        List<FileItem> parseRequest = uploader.parseRequest(req);
	        for (FileItem fileItem : parseRequest) {
	            if (fileItem.isFormField()) {
	            	if(fileItem.getFieldName().equalsIgnoreCase("workflowJSONString")){
	            		ObjectMapper mapper = new ObjectMapper();
	        			workFlowDetails = mapper.readValue(fileItem.getString(), new TypeReference<ArrayList<WorkFlowDetails>>(){});
	            	}/*if(fileItem.getFieldName().equalsIgnoreCase("documentJSONString")){
	            		ObjectMapper mapper = new ObjectMapper();
	            		documentBean = mapper.readValue(fileItem.getString(), new TypeReference<DocumentBean>(){});
	            	}*/if(fileItem.getFieldName().equalsIgnoreCase("DocumentClass")){
	            		objectTransferBean.setDocClass(fileItem.getString());
	            	}
	            }else{
	            	fileObject = new FileObject();
	            	
	            	if(fileItem.getName().contains("\\")){
	            		String fullPath = fileItem.getName();
	            		int index = fileItem.getName().lastIndexOf("\\");
		            	String fileName = fullPath.substring(index + 1);
		            	fileObject.setFileName(URLDecoder.decode(fileName,"UTF-8"));
	            	}else{
	            		fileObject.setFileName(URLDecoder.decode(fileItem.getName(),"UTF-8"));
	            	}
	            	
	            	fileObject.setInputStream(fileItem.getInputStream());
	            	fileObjectList.add(fileObject);
	            }
	        }
	       
	        if(fileObjectList.size()>0){
	        	
	        	String[] recipientArray = getWorkflowRecipient(workFlowDetails,db);
		        for (int i = 0; i < fileObjectList.size(); i++) {
		        	ArrayList<PropertyBean> pbList = new ArrayList<PropertyBean>();
			   		 if(adaptor.validateRequest(req, resp)) {
			   			PropertyBean pb1 = new PropertyBean();
		            	pb1.setPropertyName("EmailSubject");
		            	pb1.setPropertyValue(fileObjectList.get(i).getFileName());
		            	
		            	PropertyBean pb2 = new PropertyBean();
		            	pb2.setPropertyName("DocumentTitle");
		            	pb2.setPropertyValue(fileObjectList.get(i).getFileName());
		            	pbList.add(pb1);
		            	pbList.add(pb2);
		            	DocumentBean documentBean = new DocumentBean();
		            	documentBean.setProperties(pbList);
			   			String documentId = adaptor.addDocument(documentBean,objectTransferBean,fileObjectList.get(i),FilenetSystemConfiguration.getInstance().OS_NAME,recipientArray);
			        	WorkflowAttachments workflowAttachment = new WorkflowAttachments();
			        	workflowAttachment.setWorkflowDocumentId(documentId); 
			        	workflowAttachment.setWorkflowAttachmentType("Attachment");
			        	WorkflowAttachmentsList.add(workflowAttachment);
			   		 }
				}
	        }
        	
			 if(adaptor.validateRequest(req, resp)) {
				
				 String response = db.launchWorkFlowWithSPUpdated(workFlowDetails,WorkflowAttachmentsList);
				 
				 String[] recipientArray = getWorkflowRecipient(workFlowDetails,db);
				 WorkflowAttachments wfAtth = null;
				 if(response.equalsIgnoreCase("success")){
					 for (int i = 0; i < workFlowDetails.size(); i++) {
						 wfAtth = workFlowDetails.get(i).getWorkflowAttachment();	
						 adaptor.setDocumentSecurity(wfAtth.getWorkflowDocumentId(),recipientArray);
						}
				 }
			 }
			
	    }catch (FileUploadException e) {
	        e.printStackTrace();
	        throw new Exception(e.getMessage());
	    }catch (Exception e) {
	        e.printStackTrace();
	        throw new Exception(e.getMessage());
	    }
		return "Workflow Launch Successfully";
	}

	private String[] getWorkflowRecipient(ArrayList<WorkFlowDetails> workFlowDetails, DBAdaptor db) throws Exception {
		 
		 String[] recipientArray = null;
		 ArrayList<WorkflowRecipient> workflowRecipientList =  workFlowDetails.get(0).getWorkItemDetails().getWorkflowRecipientList();
		 recipientArray = new String[workflowRecipientList.size()];
		 for (int j = 0; j < workflowRecipientList.size(); j++) {
			 String groupName = db.getDocumentSecurityfromTable(workflowRecipientList.get(j).getWorkflowRecipient());
			 if(groupName!=null && !groupName.equalsIgnoreCase("")){
				 recipientArray[j] = groupName;
			 }else{
				 recipientArray[j] = workflowRecipientList.get(j).getWorkflowRecipient();
			 }
		}
		
		return recipientArray;
	}

	public String getSupervisor(String user_login) throws Exception {
		DBAdaptor db = new DBAdaptor();
		String supervisor=null;
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			supervisor = db.getSupervisor(user_login);
  		}
		return supervisor;
	}

	public String getAssistance(String user_login) throws Exception {
		DBAdaptor db = new DBAdaptor();
		String assistance=null;
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			assistance = db.getAssistance(user_login);
  		}
		return assistance;
	}
	public ArrayList getAssistanceList(String user_login) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList assistnceList=null;
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			assistnceList = db.getAssistanceList(user_login);
  		}
		return assistnceList;
	}
	public SearchResultBean getDailyDocumentItems(String supervisor,ArrayList assistanceList, String sortOrder, String sortColumn, String user_login) throws Exception {
		SearchResultBean srb = new SearchResultBean();
		DBAdaptor db = new DBAdaptor();
		Employee emp = db.getUserDetails(user_login);
		int oldDepartmentId = db.getOldDimsDepartmentId(String.valueOf(emp.getEmployeeDepartment().getDepartmentCode()));
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			srb = adaptor.getDailyDocumentItems(supervisor,assistanceList, sortOrder, sortColumn,oldDepartmentId);
  		}
		return srb;
	}

	public String deleteFolder(String folderId) throws Exception {
		DBAdaptor db = new DBAdaptor();
		String response=null;
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			response = db.deleteFolder(folderId);
  		}
		return response;
	}

	public ArrayList<DIMSSite> getSiteItems(String dept_code, String siteDesc) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<DIMSSite> dimsSite= new ArrayList<DIMSSite>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			dimsSite = db.getSiteItems(dept_code,siteDesc);
  		}
		return dimsSite;
	}

	public ArrayList<DIMSSite> addSiteItems(String dept_code, String siteType,String siteDesc) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<DIMSSite> dimsSite= new ArrayList<DIMSSite>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			dimsSite = db.addSiteItems(dept_code,siteType,siteDesc);
  		}
		return dimsSite;
	}

	public ArrayList<DIMSSite> updateSiteItems(String siteId,String siteType, String siteDesc, String dept_code) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<DIMSSite> dimsSite= new ArrayList<DIMSSite>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
  		if(adaptor.validateRequest(req, resp)) {
  			dimsSite = db.updateSiteItems(siteId,siteType,siteDesc,dept_code);
  		}
		return dimsSite;
	}
	//Added on 17-07-2017
		public ArrayList<DivisionWF> workflowStatistics(String user_login, String department, String division, String from, String to) throws Exception {
			DBAdaptor db = new DBAdaptor();
			ArrayList<DivisionWF> workflowStatisticsList = new ArrayList<DivisionWF>();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			 if(adaptor.validateRequest(req, resp)){
				 workflowStatisticsList = db.workflowStatistics(user_login, department, division, from, to);
			 }
			return workflowStatisticsList;
		}
		public ArrayList<PendingWorkItemDetails> pendingWorkflows(String sender, String status, String overdue, String recipient, String from, String to) throws Exception {
			DBAdaptor db = new DBAdaptor();
			ArrayList<PendingWorkItemDetails> pendingWorkflowsList = new ArrayList<PendingWorkItemDetails>();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			 if(adaptor.validateRequest(req, resp)){
				 pendingWorkflowsList = db.pendingWorkflows(sender, status, overdue, recipient, from, to);
			 }
			return pendingWorkflowsList;
		}
		
		public ArrayList<PendingWorkItemDetails> pendingWorkflowsReport(String sender, String status, String overdue, String recipient, String from, String to) throws Exception {
			DBAdaptor db = new DBAdaptor();
			ArrayList<PendingWorkItemDetails> pendingWorkflowsList = new ArrayList<PendingWorkItemDetails>();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			 if(adaptor.validateRequest(req, resp)){
				 pendingWorkflowsList = db.pendingWorkflowsReport(sender, status, overdue, recipient, from, to);
			 }
			return pendingWorkflowsList;
		}
		
		public ArrayList<PendingWorkItemDetails> pendingWorkflowsFullHistory(String sender, String status, String recipient, String from, String to) throws Exception {
			DBAdaptor db = new DBAdaptor();
			ArrayList<PendingWorkItemDetails> pendingWorkflowsFullHistoryList = new ArrayList<PendingWorkItemDetails>();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			 if(adaptor.validateRequest(req, resp)){
				 pendingWorkflowsFullHistoryList = db.pendingWorkflowsFullHistory(sender, status, recipient, from, to);
			 }
			return pendingWorkflowsFullHistoryList;
		}
		
		public ArrayList<PendingWorkItemDetails> pendingWorkflowsSpecificHistory(String sender, String status, String recipient, String from, String to) throws Exception {
			DBAdaptor db = new DBAdaptor();
			ArrayList<PendingWorkItemDetails> pendingWorkflowsSpecificHistoryList = new ArrayList<PendingWorkItemDetails>();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			 if(adaptor.validateRequest(req, resp)){
				 pendingWorkflowsSpecificHistoryList = db.pendingWorkflowsSpecificHistory(sender, status, recipient, from, to);
			 }
			return pendingWorkflowsSpecificHistoryList;
		}
		
		public ArrayList<WorkItemDetails> documentsScanned(String department, String division, String user, String from, String to) throws Exception {
			DBAdaptor db = new DBAdaptor();
			ArrayList<WorkItemDetails> documentsScannedList = new ArrayList<WorkItemDetails>();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			 if(adaptor.validateRequest(req, resp)){
				 documentsScannedList = db.documentsScanned(department, division, user, from, to);
			 }
			return documentsScannedList;
		}

		public ArrayList<DivisionWF> exporWorkflowStatistics(String userLogin,String department, String division, String from, String to) throws Exception {
			DBAdaptor db = new DBAdaptor();
			ArrayList<DivisionWF> workflowStatisticsList = new ArrayList<DivisionWF>();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			 if(adaptor.validateRequest(req, resp)){
				 workflowStatisticsList = db.workflowStatistics(userLogin, department, division, from, to);
			 }
			return workflowStatisticsList;
		}
}
