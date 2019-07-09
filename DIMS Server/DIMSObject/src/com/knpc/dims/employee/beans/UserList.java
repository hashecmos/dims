package com.knpc.dims.employee.beans;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserList {
	
	private String listId;
	private String listName;
	private String loginUser;
	private String listType;
	private ArrayList<String> listMember;
	private String listPriority;
	
	public String getListId() {
		return listId;
	}
	public void setListId(String listId) {
		this.listId = listId;
	}
	public String getListName() {
		return listName;
	}
	public void setListName(String listName) {
		this.listName = listName;
	}
	public String getLoginUser() {
		return loginUser;
	}
	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}
	public String getListType() {
		return listType;
	}
	public void setListType(String listType) {
		this.listType = listType;
	}
	public ArrayList<String> getListMember() {
		return listMember;
	}
	public void setListMember(ArrayList<String> listMember) {
		this.listMember = listMember;
	}
	public String getListPriority() {
		return listPriority;
	}
	public void setListPriority(String listPriority) {
		this.listPriority = listPriority;
	}
}
