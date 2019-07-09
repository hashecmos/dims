package com.knpc.dims.db.service;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knpc.dims.db.DBAdaptor;
import com.knpc.dims.employee.beans.DelegateBean;
import com.knpc.dims.employee.beans.Employee;
import com.knpc.dims.employee.beans.UserList;

public class EmployeeService {

	public String createUserList(String json) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		UserList userList = mapper.readValue(json, new TypeReference<UserList>(){});
		DBAdaptor db = new DBAdaptor();
		 String responseString = db.createUserList(userList);
		 return responseString;
	}
	
	

	public ArrayList<UserList> getUserList(String user_name, String type) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<UserList> userList = db.getUserList(user_name,type);
		return userList;
	}

	public ArrayList<Employee> getUserListMembers(String listId) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<Employee> empList = db.getUserListMembers(listId);
		return empList;
	}

	public String modifyUserList(String json) throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		UserList userList = mapper.readValue(json, new TypeReference<UserList>(){});
		DBAdaptor db = new DBAdaptor();
		String test = db.modifyUserList(userList);
		return test;
	}
	
	public String modifyUserListPriority(String json) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		List<UserList> userList = mapper.readValue(json, new TypeReference<List<UserList>>(){});
		DBAdaptor db = new DBAdaptor();
		String test = db.modifyUserListPriority(userList.get(0).getListId(),userList.get(1).getListId());
		return test;
	}

	public String deleteUserList(String listId) throws Exception {
		DBAdaptor db = new DBAdaptor();
		String test = db.deleteUserList(listId);
		return test;
	}

	public String addDelegateUsers(String json) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		DelegateBean delegateBean = mapper.readValue(json, new TypeReference<DelegateBean>(){});
		DBAdaptor db = new DBAdaptor();
		String test = db.addDelegateUsers(delegateBean);
		return test;
	}

	public String modifyDelegateUsers(String json) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		DelegateBean delegateBean = mapper.readValue(json, new TypeReference<DelegateBean>(){});
		DBAdaptor db = new DBAdaptor();
		String test = db.modifyDelegateUsers(delegateBean);
		return test;
	}

	public String deleteDelegateUsers(String delegationId) throws Exception {
		DBAdaptor db = new DBAdaptor();
		String test = db.deleteDelegateUsers(delegationId);
		return test;
	}

	public ArrayList<DelegateBean> getDelegatedUserList(String user_login) throws Exception {
		DBAdaptor db = new DBAdaptor();
		ArrayList<DelegateBean> test = db.getDelegatedUserList(user_login);
		return test;
	}

}
