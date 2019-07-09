package com.knpc.dims.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knpc.dims.db.DBAdaptor;
import com.knpc.dims.db.service.EmployeeService;
import com.knpc.dims.db.util.ECMEncryption;
import com.knpc.dims.employee.beans.DelegateBean;
import com.knpc.dims.employee.beans.Employee;
import com.knpc.dims.employee.beans.EmployeeDepartment;
import com.knpc.dims.employee.beans.EmployeeDirectorate;
import com.knpc.dims.employee.beans.EmployeeDivision;
import com.knpc.dims.employee.beans.UserList;
import com.knpc.dims.filenet.FileNetAdaptor;
import com.knpc.dims.filenet.beans.DocumentType;
import com.knpc.dims.response.ResponseObject;
import com.knpc.dims.user.preference.ColumnPreference;

@Path("/EmployeeService")
@ApplicationPath("resources")
public class EmployeeController extends Application{
	
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getUserDetails?user_login=p8admin
	@GET
	@Path("/getUserDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserDetails(@QueryParam("user_login") String user_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		Employee userDetails = new Employee();
		try{
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			String userName ="";
			if(req.getUserPrincipal()!=null){
				userName = req.getUserPrincipal().getName();
			}
			if(adaptor.validateRequest(req, resp)) {
				if(user_login.equalsIgnoreCase("undefined")){
					userDetails = db.getUserDetails(userName);
				}else{
					userDetails = db.getUserDetails(user_login);
				}
			}
			//return userDetails;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(userDetails).build();
	}
	// http://localhost:9080/DIMS/resources/EmployeeService/getDivisionUsers?division_code=p8admin
	@GET
	@Path("/getDivisionUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDivisionUsers(@QueryParam("division_code") String division_code,@QueryParam("user_login") String user_login,@QueryParam("searchCrtieria") String searchCrtieria,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		ArrayList<Employee> userDetailsList = new ArrayList<Employee>();
		try{
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			
			if(adaptor.validateRequest(req, resp)) {
				userDetailsList = db.getDivisionUsers(division_code,user_login,searchCrtieria);
			}
			//return userDetailsList;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(userDetailsList).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getDepartmentUsers?dept_code=196100
	@GET
	@Path("/getDepartmentUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDepartmentUsers(@QueryParam("dept_code") String dept_code,@QueryParam("searchCrtieria") String searchCrtieria,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 
		ArrayList<Employee> userDetailsList = new ArrayList<Employee>();
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				userDetailsList = db.getDepartmentUsers(dept_code,searchCrtieria);
			}
			//return userDetailsList;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(userDetailsList).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getDepartmentUsersForGlobal?dept_code=196100
		@GET
		@Path("/getDepartmentUsersForGlobal")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getDepartmentUsersForGlobal(@QueryParam("dept_code") String dept_code,@QueryParam("searchCrtieria") String searchCrtieria,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
			 
			ArrayList<Employee> userDetailsList = new ArrayList<Employee>();
			try {
				DBAdaptor db = new DBAdaptor();
				FileNetAdaptor adaptor = new FileNetAdaptor();
				if(adaptor.validateRequest(req, resp)) {
					userDetailsList = db.getGlobalDepartmentUsers(dept_code,searchCrtieria);
				}
				//return userDetailsList;
			} catch (Exception e) {
				ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
				return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
			}
			return Response.ok().entity(userDetailsList).build();
		}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getDirectorateUsers?dir_code=p8admin
	@GET
	@Path("/getDirectorateUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDirectorateUsers(@QueryParam("dir_code") String dir_code,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 
		ArrayList<Employee> userDetailsList = new ArrayList<Employee>();
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				userDetailsList = db.getDirectorateUsers(dir_code);
			}
			//return userDetailsList;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(userDetailsList).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getSecretaries?user_login=p8admin
	@GET
	@Path("/getSecretaries")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSecretaries(@QueryParam("user_login") String user_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		String asstEmpLogin = null;
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				asstEmpLogin = db.getSecretaries(user_login);
			}
			//return asstEmpLogin;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(asstEmpLogin).build();
	}
	// http://localhost:9080/DIMS/resources/EmployeeService/getManagerofSecretary?user_login=p8admin
	@GET
	@Path("/getManagerofSecretary")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getManagerofSecretary(@QueryParam("user_login") String user_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{

		String asstEmpLogin = null;
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				asstEmpLogin = db.getManagerofSecretary(user_login);
			}
			// return asstEmpLogin;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(asstEmpLogin).build();
	}
	// http://localhost:9080/DIMS/resources/EmployeeService/addSecretary?user_login=p8admin&supervisor_login=
	@GET
	@Path("/addSecretary")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addSecretary(@QueryParam("user_login") String user_login , @QueryParam("supervisor_login") String supervisor_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		boolean asstEmpLogin = false;
		try{
			 DBAdaptor db = new DBAdaptor();
			 FileNetAdaptor adaptor = new FileNetAdaptor();
			 if(adaptor.validateRequest(req, resp)) {
				 asstEmpLogin = db.addSecretary(user_login,supervisor_login);
			 }
			 //return asstEmpLogin;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(asstEmpLogin).build();
	}
	// http://localhost:9080/DIMS/resources/EmployeeService/getDirectorate
	@GET
	@Path("/getDirectorate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDirectorate(@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 
		ArrayList<EmployeeDirectorate> directorateList = new ArrayList<EmployeeDirectorate>();
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				directorateList = db.getDirectorate();
			}
			// return directorateList;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(directorateList).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getDepartments?dir_code=p8admin
	@GET
	@Path("/getDepartments")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDepartments(@QueryParam("dir_code") String dir_code,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<EmployeeDepartment> deptList = new ArrayList<EmployeeDepartment>();
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				deptList = db.getDepartments(dir_code);
			}
			//return deptList;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(deptList).build();
	}
	
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getUserDepartment?department_code=p8admin
	@GET
	@Path("/getUserDepartment")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserDepartment(@QueryParam("department_code") String dept_code,@QueryParam("user_login") String user_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<EmployeeDepartment> deptList = new ArrayList<EmployeeDepartment>();
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				deptList = db.getUserDepartment(dept_code,user_login);
			}
			//return deptList;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(deptList).build();
	}
	
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getDivisions?dept_code=p8admin
	@GET
	@Path("/getDivisions")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDivisions(@QueryParam("dept_code") String dept_code,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 
		ArrayList<EmployeeDivision> divList = new ArrayList<EmployeeDivision>();
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				divList = db.getDivisions(dept_code);
			}
			//return divList;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(divList).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getUserDivision?division_code=p8admin
	@GET
	@Path("/getUserDivision")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserDivision(@QueryParam("division_code") String division_code,@QueryParam("user_login") String user_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 
		ArrayList<EmployeeDivision> divList = new ArrayList<EmployeeDivision>();
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				divList = db.getUserDivision(division_code,user_login);
			}
			//return divList;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(divList).build();
	}
	// http://localhost:9080/DIMS/resources/EmployeeService/getDelegatedUsers?user_login=mark
	@GET
	@Path("/getDelegatedUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDelegatedUsers(@QueryParam("user_login") String user_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 
		ArrayList<String> delegatedUserList = new ArrayList<String>();
		try {
			DBAdaptor db = new DBAdaptor();

			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				delegatedUserList = db.getDelegatedUsers(user_login);
			}
			// return delegatedUserList;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(delegatedUserList).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getEmailIds?email=
	@GET
	@Path("/getEmailIds")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEmailIds(@QueryParam("email") String email,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 
		ArrayList<Employee> emailList = new ArrayList<Employee>();
		try {
			DBAdaptor db = new DBAdaptor();

			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				emailList = db.getEmailIds(email);
			}
			// return emailList;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(emailList).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/createUserList
	@POST
	@Path("/createUserList")
	public Response createUserList(String json,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		String responseString = null;	
		try{
			EmployeeService empService = new EmployeeService();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			 if(adaptor.validateRequest(req, resp)) {
				 responseString = empService.createUserList(json);	
			 }
			//return test;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(responseString).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/modifyUserList
	@POST
	@Path("/modifyUserList")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response modifyUserList(String json,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		String test = null;	
		try{
			EmployeeService empService = new EmployeeService();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			 if(adaptor.validateRequest(req, resp)) {
				 test = empService.modifyUserList(json);	
			}
			//return test;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(test).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/modifyUserListPriority
		@POST
		@Path("/modifyUserListPriority")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response modifyUserListPriority(String json,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
			String test = null;	
			try{
				EmployeeService empService = new EmployeeService();
				FileNetAdaptor adaptor = new FileNetAdaptor();
				 if(adaptor.validateRequest(req, resp)) {
					 test = empService.modifyUserListPriority(json);	
				}
				//return test;
			}catch (Exception e) {
				ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
				return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
			}
			return Response.ok().entity(test).build();
		}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/deleteUserList&listId=
	@GET
	@Path("/deleteUserList")
	public Response deleteUserList(@QueryParam("listId") String listId,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		String test = null;
		try {
			EmployeeService empService = new EmployeeService();

			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				test = empService.deleteUserList(listId);
			}
			// return test;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(test).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getUserList?user_name=&type=
	@GET
	@Path("/getUserList")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserList(@QueryParam("user_name") String user_name, @QueryParam("type") String type,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<UserList> userList = new ArrayList<UserList>();
		try{
			EmployeeService empService = new EmployeeService();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			 if(adaptor.validateRequest(req, resp)) {
				 userList = empService.getUserList(user_name,type);	
			 }
			//return userList;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(userList).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getUserListMembers?listId=
	@GET
	@Path("/getUserListMembers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserListMembers(@QueryParam("listId") String listId,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<Employee> employeeList = new ArrayList<Employee>();
		try{
			EmployeeService empService = new EmployeeService();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			 if(adaptor.validateRequest(req, resp)) {
				 employeeList = empService.getUserListMembers(listId);
			 }
			//return employeeList;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(employeeList).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/addDelegateUsers
	@POST
	@Path("/addDelegateUsers")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addDelegateUsers(String json,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		String delegateUser = null;
		try{
			EmployeeService empService = new EmployeeService();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			 if(adaptor.validateRequest(req, resp)) {
				 delegateUser = empService.addDelegateUsers(json);
			 }
			//return delegateUser;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(delegateUser).build();
		
	}
	// http://localhost:9080/DIMS/resources/EmployeeService/getDelegatedUserList?user_login=alex
	@GET
	@Path("/getDelegatedUserList")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDelegatedUserList(@QueryParam("user_login") String user_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<DelegateBean> delegatedUserList = new ArrayList<DelegateBean>();
		try{
			EmployeeService empService = new EmployeeService();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			 if(adaptor.validateRequest(req, resp)) {
				 delegatedUserList = empService.getDelegatedUserList(user_login);
			 }
			//return delegatedUserList;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(delegatedUserList).build();
	}
	// http://localhost:9080/DIMS/resources/EmployeeService/modifyDelegateUsers
	@POST
	@Path("/modifyDelegateUsers")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response modifyDelegateUsers(String json,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		String delegateUser = null;	
		try{
			EmployeeService empService = new EmployeeService();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			 if(adaptor.validateRequest(req, resp)) {
				 delegateUser = empService.modifyDelegateUsers(json);	
			 }
			//return delegateUser;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(delegateUser).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/deleteDelegateUsers
	@GET
	@Path("/deleteDelegateUsers")
	public Response deleteDelegateUsers(@QueryParam("delegationId") String delegationId,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		String delegateUser = null;
		try{
			EmployeeService empService = new EmployeeService();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			 if(adaptor.validateRequest(req, resp)) {
				 delegateUser = empService.deleteDelegateUsers(delegationId);		
			 }
			//return delegateUser;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(delegateUser).build();
	}
	// http://localhost:9080/DIMS/resources/EmployeeService/removeCookies
	@GET
	@Path("/removeCookies")
	public void deleteDelegateUsers(@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		Cookie[] cookies = req.getCookies();

	    if (cookies != null) {
	    	
	    	for(int i = 0; i < cookies.length; i++)
			{ 			
				Cookie c = cookies[i];
				
				if (c.getName()!=null && c.getName().equals("token1"))
				{
					c.setValue(null);
	                c.setMaxAge(0);
	                c.setPath("/");
	                resp.addCookie(c);	
				}else if(c.getName()!=null && c.getName().equals("token2"))
				{	
					c.setValue(null);
	                c.setMaxAge(0);
	                c.setPath("/");
	                resp.addCookie(c);	
				}
				
			}
	    }
		
	}
	// http://localhost:9080/DIMS/resources/EmployeeService/TestUserList
	@GET
	@Path("/TestUserList")
	@Produces(MediaType.APPLICATION_JSON)
	public DelegateBean getUserListUsers() throws Exception{
		/*UserList userList = new UserList();
		ArrayList<String> memberList = new ArrayList<String>();
		memberList.add("Fatima");
		memberList.add("Waleed");
		userList.setListMember(memberList);
		userList.setListName("Sample List Name");
		userList.setListType("Private/Public");
		userList.setLoginUser("Logged in user");
		userList.setListId("");
		return userList;*/
		
		DelegateBean delegateBean = new DelegateBean();
		delegateBean.setAbsentLogin("");
		delegateBean.setDelegateFrom("");
		delegateBean.setDelegateLogin("");
		delegateBean.setDelegateStatus("");
		delegateBean.setDelegateTo("");
		delegateBean.setDelegationId("");
		return delegateBean;
		
	}
	
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getEmployeeList?email=
	@GET
	@Path("/getEmployeeList")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEmployeeList(@QueryParam("code") String code,@QueryParam("flag") String flag,@QueryParam("filter") String filter,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 
		ArrayList<Employee> employeeList = new ArrayList<Employee>();
		try {
			DBAdaptor db = new DBAdaptor();

			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				employeeList = db.getEmployeeList(flag, filter,code);
			}
			// return emailList;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(employeeList).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getKNPCHierarchy
	@GET
	@Path("/getKNPCHierarchy")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getKNPCHierarchy(@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 
		ArrayList<EmployeeDepartment> deptList = new ArrayList<EmployeeDepartment>();
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				deptList = db.getKNPCHierarchy();
			}
			// return directorateList;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(deptList).build();
	}
	//getCrossDepartmentUsers
	// http://localhost:9080/DIMS/resources/EmployeeService/getCrossDepartmentUsers
	@GET
	@Path("/getCrossDepartmentUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCrossDepartmentUsers(@QueryParam("searchCrtieria") String searchCrtieria,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 
		ArrayList<Employee> userDetailsList = new ArrayList<Employee>();
		try{
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			
			if(adaptor.validateRequest(req, resp)) {
				userDetailsList = db.getCrossDepartmentUsers(searchCrtieria);
			}
			//return userDetailsList;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(userDetailsList).build();
	}
	
	//getKNPCHierarchyUsersForDepartment
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getCrossDepartmentUsers
	@GET
	@Path("/getKNPCHierarchyUsersForDepartment")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getKNPCHierarchyUsersForDepartment(@QueryParam("dept_code") String dept_code,@QueryParam("searchCrtieria") String searchCrtieria,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 
		ArrayList<Employee> userDetailsList = new ArrayList<Employee>();
		try{
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			
			if(adaptor.validateRequest(req, resp)) {
				userDetailsList = db.getKNPCHierarchyUsersForDepartment(searchCrtieria,dept_code);
			}
			//return userDetailsList;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(userDetailsList).build();
	}
	
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getCrossDepartmentUsers
		@GET
		@Path("/getKNPCHierarchyUsersForDepartment_1")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getKNPCHierarchyUsersForDepartment_1(@QueryParam("searchCrtieria") String searchCrtieria,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
			 
			ArrayList<EmployeeDepartment> userDetailsList = new ArrayList<EmployeeDepartment>();
			try{
				DBAdaptor db = new DBAdaptor();
				FileNetAdaptor adaptor = new FileNetAdaptor();
				
				if(adaptor.validateRequest(req, resp)) {
					userDetailsList = db.getKNPCHierarchyUsersForDepartment_1(searchCrtieria);
				}
				//return userDetailsList;
			}catch (Exception e) {
				ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
				return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
			}
			return Response.ok().entity(userDetailsList).build();
		}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/loadDeafultUserList
	@GET
	@Path("/loadDeafultUserList")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadDeafultUserList(@QueryParam("listId") String listId,@QueryParam("searchCrtieria") String searchCrtieria,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 
		ArrayList<Employee> userDetailsList = new ArrayList<Employee>();
		try{
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			
			if(adaptor.validateRequest(req, resp)) {
				userDetailsList = db.loadDeafultUserList(searchCrtieria,listId);
			}
			//return userDetailsList;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(userDetailsList).build();
	}
	
	
	
	/*// http://localhost:9080/DIMS/resources/EmployeeService/getUserPreference
	@GET
	@Path("/getUserPreference")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserPreference(@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 ss
		ArrayList<EmployeeDepartment> deptList = new ArrayList<EmployeeDepartment>();
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				deptList = db.getKNPCHierarchy();
			}
			// return directorateList;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(deptList).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/defaultUserPreference
	@GET
	@Path("/defaultUserPreference")
	@Produces(MediaType.APPLICATION_JSON)
	public Response defaultUserPreference(@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 ss
		ArrayList<EmployeeDepartment> deptList = new ArrayList<EmployeeDepartment>();
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				deptList = db.getKNPCHierarchy();
			}
			// return directorateList;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(deptList).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/updateUserPreference
	@GET
	@Path("/updateUserPreference")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUserPreference(@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 ss
		ArrayList<EmployeeDepartment> deptList = new ArrayList<EmployeeDepartment>();
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				deptList = db.getKNPCHierarchy();
			}
			// return directorateList;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(deptList).build();
	}*/
	
	// http://localhost:9080/DIMS/resources/EmployeeService/isSecretary?user_login=
	@GET
	@Path("/isSecretary")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)
	public Response isSecretary(@QueryParam("user_login") String user_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		String isSecretary = "false";
		try{
			DBAdaptor db = new DBAdaptor();
			isSecretary = db.isSecretary(user_login);
			//return userDetailsList;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(isSecretary).build();
	}
	
	@GET
	@Path("/getColumnPreference")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getColumnPreference(@QueryParam("user_login") String user_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		ArrayList<ColumnPreference> columnPreferenceList = new ArrayList<ColumnPreference>();
		
		Map<String,ColumnPreference> cPList = new HashMap<String,ColumnPreference>();
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				cPList = db.getColumnPreference(user_login);
				if(cPList!=null && cPList.containsKey("workflow")){
					ColumnPreference columnPreference = (ColumnPreference) cPList.get("workflow");
					ObjectMapper objectMapper = new ObjectMapper();
					JsonNode node = objectMapper.readValue(columnPreference.getColoumnPref(), JsonNode.class);
					System.out.println("node.size()  ::: "+node.size());
					for (int i = 0; i < node.size(); i++) {
						ColumnPreference colPref = new ColumnPreference();
						JsonNode colNode = node.get(i);
						colPref.setItemType(colNode.get("itemType").asText());
						colPref.setColumnName(colNode.get("columnName").asText());
						colPref.setColoumnEnabled(colNode.get("coloumnEnabled").asText());
						columnPreferenceList.add(colPref);
					}
				}else{
					ColumnPreference columnPreference = (ColumnPreference) cPList.get("default");
					ObjectMapper objectMapper = new ObjectMapper();
					JsonNode node = objectMapper.readValue(columnPreference.getColoumnPref(), JsonNode.class);
					System.out.println("node.size()  ::: "+node.size());
					for (int i = 0; i < node.size(); i++) {
						ColumnPreference colPref = new ColumnPreference();
						JsonNode colNode = node.get(i);
						colPref.setItemType(colNode.get("itemType").asText());
						colPref.setColumnName(colNode.get("columnName").asText());
						colPref.setColoumnEnabled(colNode.get("coloumnEnabled").asText());
						columnPreferenceList.add(colPref);
					}
				}
				
			}
			// return directorateList;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(columnPreferenceList).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/addColumnPreference?user_login=fatima
	@POST
	@Path("/addColumnPreference")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addColumnPreference(String jsonString,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 
		ColumnPreference columnPreference  = new ColumnPreference();
		try {
			
			ObjectMapper objectMapper = new ObjectMapper();
			
			JsonNode node = objectMapper.readValue(jsonString, JsonNode.class);
			
			JsonNode coloumnPref = node.get("coloumnPref");
			JsonNode userLogin = node.get("userLogin");
			JsonNode prefFor = node.get("prefFor");
			
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				columnPreference = db.addColumnPreference(coloumnPref.asText(),userLogin.asText(),prefFor.asText());
			}
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(columnPreference).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getDepartmentName?department_code=2
	@GET
	@Path("/getDepartmentName")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDepartmentName(@QueryParam("department_code") Long dept_code,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		String result ="";
		
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				 result = db.getDepartmentName(dept_code);
			}
			//return deptList;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(result).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getFilterUsers?filter=All
	@GET
	@Path("/getFilterUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFilterUsers(@QueryParam("filter") String filter, @QueryParam("deptCode") String deptCode, @Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 
		ArrayList<Employee> userDetailsList = new ArrayList<Employee>();
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				userDetailsList = db.getFilterUsers(filter, deptCode);
			}
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(userDetailsList).build();
	}
	//getDocumentType
	@GET
	@Path("/getDocumentType")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDocumentType(@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 
		ArrayList<DocumentType> documentType = new ArrayList<DocumentType>();
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				documentType = db.getDocumentType();
			}
			//return userDetailsList;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(documentType).build();
	}
	
	
	@GET
	@Path("/getUsersForDivisionReports")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsersForDivisionReports(@QueryParam("division_code") String division_code, @QueryParam("dept_code") String dept_code,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		 
		ArrayList<Employee> empList = new ArrayList<Employee>();
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				empList = db.getUsersForDivisionReports(division_code,dept_code);
			}
			//return userDetailsList;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(empList).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getSupervisorDetails?user_login=p8admin
	@GET
	@Path("/getSupervisorDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSupervisorDetails(@QueryParam("user_login") String user_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		Employee userDetails = new Employee();
		String asstEmpLogin = null;
		try {
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				asstEmpLogin = db.getSupervisorDetails(user_login);
			}
			userDetails = db.getUserDetails(asstEmpLogin);
			//return asstEmpLogin;
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(userDetails).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getUserDetails?user_login=p8admin
	@GET
	@Path("/getDelegatedUserDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDelegatedUserDetails(@QueryParam("user_login") String user_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		Employee userDetails = new Employee();
		try{
			DBAdaptor db = new DBAdaptor();
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
					userDetails = db.getUserDetails(user_login);
			}
			//return userDetails;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(userDetails).build();
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getEncryptionKey?pass=kNpcDim$k3y
	@GET
	@Path("/getEncryptionKey")
	@Produces(MediaType.APPLICATION_JSON)
	public Response zGetEncryptionKey(@QueryParam("pass") String pass, @Context HttpServletRequest req,
			@Context HttpServletResponse resp) throws Exception {
		try {
			if(!pass.equals("kNpcDim$k3y"))
				throw new Exception("Not authorized to use this service!");
			ECMEncryption enc = new ECMEncryption();
			String key = enc.getEncryptionKey();

			return Response.ok().entity(key).build();
		} catch (Exception e) {
			//logger.logException(e);
			ResponseObject responseObject = ResponseObject.getResponseObject(e
					.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject)
					.build();
		}
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/encryptString?pass=kNpcDim$k3y&key=keyvalue&string=stringvalue
	@GET
	@Path("/encryptString")
	@Produces(MediaType.APPLICATION_JSON)
	public Response zEncryptString(@QueryParam("pass") String pass, @QueryParam("key") String key, @QueryParam("string") String inputString,
			@Context HttpServletRequest req, @Context HttpServletResponse resp) throws Exception {
		try {
			if(!pass.equals("kNpcDim$k3y"))
				throw new Exception("Not authorized to use this service!");
			ECMEncryption enc = new ECMEncryption();
			String encString = enc.getEncryptedString(key, inputString);

			return Response.ok().entity(encString).build();
		} catch (Exception e) {
			//logger1.logException(e);
			ResponseObject responseObject = ResponseObject.getResponseObject(e
					.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject)
					.build();
		}
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/decryptString?pass=kNpcDim$k3y&key=keyvalue&string=stringvalue
	@GET
	@Path("/decryptString")
	@Produces(MediaType.APPLICATION_JSON)
	public Response zDecryptString(@QueryParam("pass") String pass, @QueryParam("key") String key, @QueryParam("string") String inputString,
			@Context HttpServletRequest req, @Context HttpServletResponse resp) throws Exception {
		try {
			if(!pass.equals("kNpcDim$k3y"))
				throw new Exception("Not authorized to use this service!");
			ECMEncryption enc = new ECMEncryption();
			String decString = enc.getDecryptedString(key, inputString);

			return Response.ok().entity(decString).build();
		} catch (Exception e) {
			//logger.logException(e);
			ResponseObject responseObject = ResponseObject.getResponseObject(e
					.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject)
					.build();
		}
	}
	
	// http://localhost:9080/DIMS/resources/EmployeeService/getCrossDepartmentUsers
		@GET
		@Path("/getKNPCHierarchyUsers")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getKNPCHierarchyUsers(@QueryParam("searchCrtieria") String searchCrtieria,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
			 
			ArrayList<Employee> userDetailsList = new ArrayList<Employee>();
			try{
				DBAdaptor db = new DBAdaptor();
				FileNetAdaptor adaptor = new FileNetAdaptor();
				
				if(adaptor.validateRequest(req, resp)) {
					userDetailsList = db.getKNPCHierarchyUsers(searchCrtieria);
				}
				//return userDetailsList;
			}catch (Exception e) {
				ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
				return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
			}
			return Response.ok().entity(userDetailsList).build();
		}
	
}
