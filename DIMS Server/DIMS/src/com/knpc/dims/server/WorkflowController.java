package com.knpc.dims.server;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.knpc.dims.db.service.WorkFlowService;
import com.knpc.dims.filenet.beans.DocumentBean;
import com.knpc.dims.filenet.beans.PropertyBean;
import com.knpc.dims.filenet.beans.SearchResultBean;
import com.knpc.dims.response.ResponseObject;
import com.knpc.dims.workflow.beans.DIMSSite;
import com.knpc.dims.workflow.beans.WorkFlowDetails;
import com.knpc.dims.workflow.beans.WorkItemDetails;
import com.knpc.dims.workflow.beans.WorkItemHistory;
import com.knpc.dims.workflow.beans.WorkflowRecipient;
@Path("/WorkflowService")
@ApplicationPath("resources")
public class WorkflowController {
	
	// http://localhost:9080/DIMS/resources/WorkflowService/launchWorkFlow
	@POST
	@Path("/launchWorkFlow")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response launchWorkFlow(@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		String response = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			response = ws.launchWorkFlowUpdated(req);
			
			//return response;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(response).build();
		
	}
	
	
	//http://localhost:9080/DIMS/resources/WorkflowService/getInboxItems?user_login=alex
	@GET
	@Path("/getInboxItems")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInboxItems(@QueryParam("user_login") String user_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<WorkItemDetails> inboxList = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			inboxList = ws.getInboxItems(user_login);
			//return inboxList;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(inboxList).build();
	}
	//http://localhost:9080/DIMS/resources/WorkflowService/filterInboxItems?user_login=alex&filterType=All
	@GET
	@Path("/filterInboxItems")
	@Produces(MediaType.APPLICATION_JSON)
	public Response filterInbox(@QueryParam("user_login") String user_login, @QueryParam("filterType") String filterType,@QueryParam("folderFilter") String folderFilter,@QueryParam("sortOrder") String sortOrder,@QueryParam("sortColumn") String sortColumn,@QueryParam("page") String page,@QueryParam("perPage") String perPage,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<WorkItemDetails> filterInbox = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			filterInbox = ws.filterInboxItems(user_login,filterType,folderFilter,sortOrder,sortColumn,page,perPage);
			//return filterInbox;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(filterInbox).build();
		
	}
	
	//http://localhost:9080/DIMS/resources/WorkflowService/searchInboxItems?user_login=alex&filterType=All
	@GET
	@Path("/searchInboxItems")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchInbox(@QueryParam("user_login") String user_login, @QueryParam("filterType") String filterType,@QueryParam("searchCriteria") String searchCriteria,@Context HttpServletRequest req,@Context HttpServletResponse resp ) throws Exception{
		
		ArrayList<WorkItemDetails> filterInbox = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			filterInbox = ws.searchInbox(user_login,filterType,searchCriteria);
			//return filterInbox;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(filterInbox).build();
		
	}
	
	//http://localhost:9080/DIMS/resources/WorkflowService/getWorkItemDetails?witem_id=215048e3-6ce3-4de0-bd69-355250c34e5b
	@GET
	@Path("/getWorkItemDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWorkItemDetails(@QueryParam("witem_id") String witem_id,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		WorkItemDetails workItemDetails = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			workItemDetails = ws.getWorkItemDetails(witem_id);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(workItemDetails).build();
		
	}
	
	//http://localhost:9080/DIMS/resources/WorkflowService/getWorkitemParentType?witem_id=215048e3-6ce3-4de0-bd69-355250c34e5b
	@GET
	@Path("/getWorkitemParentType")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWorkitemParentType(@QueryParam("witem_id") String witem_id,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		String parentType = "NONE";
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			parentType = ws.getWorkitemParentType(witem_id);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(parentType).build();
	}
		
	//http://localhost:9080/DIMS/resources/WorkflowService/getAndReadWorkItemDetails?witem_id=215048e3-6ce3-4de0-bd69-355250c34e5b&user_login=alex
		@GET
		@Path("/getAndReadWorkItemDetails")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getAndReadWorkItemDetails(@QueryParam("witem_id") String witem_id,@QueryParam("user_login") String user_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
			
			WorkItemDetails workItemDetails = null;
			try{
				WorkFlowService ws = new WorkFlowService(req,resp);
				workItemDetails = ws.getAndReadWorkItemDetails(witem_id, user_login);
			}catch (Exception e) {
				ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
				return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
			}
			return Response.ok().entity(workItemDetails).build();
			
		}
		
	// http://localhost:9080/DIMS/resources/WorkflowService/getSentItems?user_login=p8admin
	@GET
	@Path("/getSentItems")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSentItems(@QueryParam("user_login") String user_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<WorkItemDetails> inboxList = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			inboxList = ws.getSentItems(user_login);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(inboxList).build();
	}
	
	// http://localhost:9080/DIMS/resources/WorkflowService/getWorkitemSentItems?id=XXXXX
	@GET
	@Path("/getWorkitemSentItems")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWorkitemSentItems(@QueryParam("id") String witemId,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<WorkItemDetails> inboxList = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			inboxList = ws.getWorkitemSentItems(witemId);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(inboxList).build();
	}
	
	//http://localhost:9080/DIMS/resources/WorkflowService/filterSentItems?user_login=alex&filterType=All
	@GET
	@Path("/filterSentItems")
	@Produces(MediaType.APPLICATION_JSON)
	public Response filterSentItems(@QueryParam("user_login") String user_login, @QueryParam("filterType") String filterType,@QueryParam("folderFilter") String folderFilter,@QueryParam("sortOrder") String sortOrder,@QueryParam("sortColumn") String sortColumn,@QueryParam("page") String page,@QueryParam("perPage") String perPage,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<WorkItemDetails> filterInbox = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			filterInbox = ws.filterSentItems(user_login,filterType,folderFilter,sortOrder,sortColumn,page,perPage);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(filterInbox).build();
		
	}
	
	//http://localhost:9080/DIMS/resources/WorkflowService/searchSentItems?user_login=alex&filterType=All
	@GET
	@Path("/searchSentItems")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchSentItems(@QueryParam("user_login") String user_login, @QueryParam("filterType") String filterType,@QueryParam("searchCriteria") String searchCriteria,@Context HttpServletRequest req,@Context HttpServletResponse resp ) throws Exception{
		
		ArrayList<WorkItemDetails> filterInbox = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			filterInbox = ws.searchSentItems(user_login,filterType,searchCriteria);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(filterInbox).build();
		
	}
	//http://localhost:9080/DIMS/resources/WorkflowService/getArchiveItems?user_login=alex&filterType=All
	@GET
	@Path("/getArchiveItems")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getArchiveItems(@QueryParam("sortOrder") String sortOrder,@QueryParam("sortColumn") String sortColumn,@QueryParam("user_login") String user_login,@QueryParam("page") String page,@QueryParam("perPage") String perPage,@QueryParam("folderFilter") String folderFilter,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<WorkItemDetails> filterInbox = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			filterInbox = ws.getArchiveItems(user_login,page,perPage,folderFilter,sortOrder,sortColumn);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(filterInbox).build();
	}
	
	//http://localhost:9080/DIMS/resources/WorkflowService/forwardWorkItem?user_login=alex&jsonString=All
	@POST
	@Path("/forwardWorkItem")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response forwardWorkItem(@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		String response = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			response = ws.forwardWorkItem(req);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(response).build();
	}
	
	//http://localhost:9080/DIMS/resources/WorkflowService/forwardWorkItem?user_login=alex&jsonString=All
	@POST
	@Path("/doneWorkItem")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response doneWorkItem(@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		String response = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			response = ws.doneWorkItem(req);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(response).build();
	}
	
	//http://localhost:9080/DIMS/resources/WorkflowService/addUserWorkItem
	@POST
	@Path("/addUserWorkItem")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addUserWorkItem(String jsonString,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		String response = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			response = ws.addUserWorkItem(jsonString);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(response).build();
		
	}
	
	//http://localhost:9080/DIMS/resources/WorkflowService/reassignWorkItem
	@POST
	@Path("/reassignWorkItem")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response reassignWorkItem(String jsonString,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		String response = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			response = ws.reassignWorkItem(jsonString);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(response).build();
		
	}
	
	//http://localhost:9080/DIMS/resources/WorkflowService/archiveWorkItem?user_login=alex&workItemId=jhhjdsaj
	@GET
	@Path("/archiveWorkItem")
	public Response archiveWorkItem(@QueryParam("actionBy") String actionBy,@QueryParam("user_login") String user_login, @QueryParam("workItemID") String workItemId,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		String response = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			response = ws.archiveWorkItem(user_login,workItemId,actionBy);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(response).build();
	}
	
	//http://localhost:9080/DIMS/resources/WorkflowService/completeWorkItem?user_login=p8admin&workItemId=
	@GET
	@Path("/completeWorkItem")
	public Response completeWorkItem(@QueryParam("workItemId") String workItemId,@QueryParam("user_login") String user_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		String response = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			response = ws.completeWorkFlow(workItemId,user_login);
			//return response;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(response).build();
	}

	//http://localhost:9080/DIMS/resources/WorkflowService/getWorkItemHistory?workItemId=gdgdgd
	@GET
	@Path("/getWorkFlowHistory")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWorkFlowHistory(@QueryParam("workItemId") String workItemId,@QueryParam("workflowId") String workflowId,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<WorkItemHistory> response = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			response = ws.getWorkFlowHistory(workItemId,workflowId);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(response).build();
	}
	

	//http://localhost:9080/DIMS/resources/WorkflowService/getWorkItemHistory?workItemId=gdgdgd
		@GET
		@Path("/getWorkFlowHistoryByDept")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getWorkFlowHistoryByDept(@QueryParam("workItemId") String workItemId,@QueryParam("workflowId") String workflowId,@QueryParam("user_login") String user_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
			
			ArrayList<WorkItemHistory> response = null;
			try{
				WorkFlowService ws = new WorkFlowService(req,resp);
				response = ws.getWorkFlowHistoryByDept(workItemId,workflowId,user_login);
			}catch (Exception e) {
				ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
				return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
			}
			return Response.ok().entity(response).build();
		}
	
	
	//http://localhost:9080/DIMS/resources/WorkflowService/getInboxItemsFrom?user_login=p8admin&sender_login=alex
	@GET
	@Path("/getInboxItemsFrom")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInboxItemsFrom(@QueryParam("user_login") String user_login, @QueryParam("sender_login") String sender_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<WorkItemDetails> inboxList = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			inboxList = ws.getInboxItemsFrom(user_login,sender_login);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(inboxList).build();
	}
	
	
		
	//http://localhost:9080/DIMS/resources/WorkflowService/getWorkflowInstructions
	@GET
	@Path("/getWorkflowInstructions")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWorkflowInstructions(@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<String> instructionList = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			instructionList = ws.getWorkflowInstructions();
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(instructionList).build();
		
	}
	
	//http://localhost:9080/DIMS/resources/WorkflowService/getWorkitemActions
	@GET
	@Path("/getWorkitemActions")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWorkitemActions(@QueryParam("witemid") String witemId, @QueryParam("queue") String queue,
			@QueryParam("userid") String userid, @Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<String> actionList = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			actionList = ws.getWorkitemActions(witemId, queue, userid);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(actionList).build();
		
	}
		
	//http://localhost:9080/DIMS/resources/WorkflowService/getSentItemRecipients?witmId=394A9999-2D56-49E6-9E14-5812B7FFD0CB
	@GET
	@Path("/getSentItemRecipients")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSentItemRecipients(@QueryParam("witmId") String witmId,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<WorkflowRecipient> workFlowRecipientList = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			workFlowRecipientList = ws.getSentItemRecipients(witmId);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(workFlowRecipientList).build();
	}
		
	//http://localhost:9080/DIMS/resources/WorkflowService/getDocumentHistory?docId={47AF833A-68BB-C7F3-87B9-5A0191B00000}
	@GET
	@Path("/getDocumentHistory")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDocumentHistory(@QueryParam("docId") String docId,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<WorkFlowDetails> workFlowDetailsList = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			workFlowDetailsList = ws.getDocumentHistory(docId);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(workFlowDetailsList).build();
	}
	
	//http://localhost:9080/DIMS/resources/WorkflowService/createWorkflowFolder?type=inbox&folderName=Fatima Muneer Mohammad&user_name=Maryam
	@GET
	@Path("/createWorkflowFolder")
	public Response createWorkflowFolder(@QueryParam("type") String type,@QueryParam("folderName") String folderName,@QueryParam("user_name") String user_name,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		String userName = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			userName = ws.createWorkflowFolder(type,folderName,user_name);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(userName).build();
	}
	
	//http://localhost:9080/DIMS/resources/WorkflowService/getWorkflowFolder?type=inbox&user_name=Maryam
	@GET
	@Path("/getWorkflowFolder")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWorkflowFolder(@QueryParam("type") String type,@QueryParam("user_name") String user_name,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<String> folderList = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			folderList = ws.getWorkflowFolder(type,user_name);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(folderList).build();
		
	}
	
	
	//http://localhost:9080/DIMS/resources/WorkflowService/deleteFolder?folderId=
	@GET
	@Path("/deleteFolder")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteFolder(@QueryParam("folderId") String folderId,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		String response = "false";
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			response = ws.deleteFolder(folderId);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(response).build();
		
	}
	
	// http://localhost:9080/DIMS/resources/WorkflowService/getDailyDocumentItems?docId={3175DF62-D4AC-C3A1-8610-5A2553800000}&osName=ECM
		@GET
		@Path("/getDailyDocumentItems")
		@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)
		public Response getDailyDocumentItems(@QueryParam("user_login") String user_login,@QueryParam("isSecretary") String isSecretary,@QueryParam("filterType") String filterType,@QueryParam("folderFilter") String folderFilter,@QueryParam("sortOrder") String sortOrder,@QueryParam("sortColumn") String sortColumn,@QueryParam("page") String page,@QueryParam("perPage") String perPage,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
		{
			SearchResultBean srb = new SearchResultBean();
			try{
				WorkFlowService ws = new WorkFlowService(req,resp);
				String supervisor = null;
				String assistance = null;
				ArrayList assistanceList = null;
				/*if(isSecretary.equalsIgnoreCase("true")){
					assistance = ws.getAssistance(user_login);
				}
				if(isSecretary.equalsIgnoreCase("false")){
					supervisor = user_login;
				}
				*/
				if(isSecretary.equalsIgnoreCase("false")){
					supervisor = user_login;
				}
				if(isSecretary.equalsIgnoreCase("true")){
					supervisor = user_login;
					assistanceList = ws.getAssistanceList(user_login);
				}
				
				srb = ws.getDailyDocumentItems(supervisor,assistanceList, sortOrder, sortColumn,user_login);
				
			}catch (Exception e) {
				ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
				return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
			}
			return Response.ok().entity(srb).build();
		}
	
	
	
	// http://localhost:9080/DIMS/resources/WorkflowService/getSiteItems?docId={3175DF62-D4AC-C3A1-8610-5A2553800000}&osName=ECM
	@GET
	@Path("/getSiteItems")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)
	public Response getSiteItems(@QueryParam("dept_code") String dept_code,@QueryParam("siteDesc") String siteDesc,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		ArrayList<DIMSSite> dimsSiteList = new ArrayList<DIMSSite>();
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			dimsSiteList = ws.getSiteItems(dept_code,siteDesc);
			
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(dimsSiteList).build();
	}
	
	// http://localhost:9080/DIMS/resources/WorkflowService/addSiteItems?docId={3175DF62-D4AC-C3A1-8610-5A2553800000}&osName=ECM
	@GET
	@Path("/addSiteItems")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)
	public Response addSiteItems(@QueryParam("dept_code") String dept_code,@QueryParam("siteType") String siteType,@QueryParam("siteDesc") String siteDesc,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		ArrayList<DIMSSite> dimsSiteList = new ArrayList<DIMSSite>();
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			dimsSiteList = ws.addSiteItems(dept_code,siteType,siteDesc);
			
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(dimsSiteList).build();
	}
	
	// http://localhost:9080/DIMS/resources/WorkflowService/updateSiteItems?docId={3175DF62-D4AC-C3A1-8610-5A2553800000}&osName=ECM
	@GET
	@Path("/updateSiteItems")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)
	public Response updateSiteItems(@QueryParam("dept_code") String dept_code,@QueryParam("siteId") String siteId,@QueryParam("siteType") String siteType,@QueryParam("siteDesc") String siteDesc,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		ArrayList<DIMSSite> dimsSiteList = new ArrayList<DIMSSite>();
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			dimsSiteList = ws.updateSiteItems(siteId,siteType,siteDesc,dept_code);
			
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(dimsSiteList).build();
	}
	
	// http://localhost:9080/DIMS/resources/WorkflowService/getDocumentBean
	@GET
	@Path("/getDocumentBean")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)
	public Response getDocumentBean(@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		DocumentBean docBean = new DocumentBean();
		try{
			docBean.setProperties(new ArrayList<PropertyBean>());
			docBean.setObjectStoreName("ECM");
			docBean.setId("test id");
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(docBean).build();
	}
}
