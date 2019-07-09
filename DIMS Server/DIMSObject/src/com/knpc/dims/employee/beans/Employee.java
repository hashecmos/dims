package com.knpc.dims.employee.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Employee {
	private int employeeID;
	private String employeeName;
	private String employeeLogin;
	private String employeeSupervisor;
	private String employeeEmail;
	private String employeeDesignation;
	private EmployeeOrganization employeeOrganization;
	private EmployeeDepartment employeeDepartment;
	private EmployeeDivision employeeDivision;
	private EmployeeDirectorate employeeDirectorate;
	private String employeeJobTitle;
	private boolean isAdmin;
	
	private int isHaveInbox;
	private int isHaveReports;
	
	
	public int getIsHaveInbox() {
		return isHaveInbox;
	}

	public void setIsHaveInbox(int isHaveInbox) {
		this.isHaveInbox = isHaveInbox;
	}
	
	public String getEmployeeJobTitle() {
		return employeeJobTitle;
	}

	public void setEmployeeJobTitle(String employeeJobTitle) {
		this.employeeJobTitle = employeeJobTitle;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public EmployeeDirectorate getEmployeeDirectorate() {
		return employeeDirectorate;
	}

	public void setEmployeeDirectorate(EmployeeDirectorate employeeDirectorate) {
		this.employeeDirectorate = employeeDirectorate;
	}

	public int getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeLogin() {
		return employeeLogin;
	}

	public void setEmployeeLogin(String employeeLogin) {
		this.employeeLogin = employeeLogin;
	}

	public String getEmployeeSupervisor() {
		return employeeSupervisor;
	}

	public void setEmployeeSupervisor(String employeeSupervisor) {
		this.employeeSupervisor = employeeSupervisor;
	}

	public String getEmployeeEmail() {
		return employeeEmail;
	}

	public void setEmployeeEmail(String employeeEmail) {
		this.employeeEmail = employeeEmail;
	}

	public String getEmployeeDesignation() {
		return employeeDesignation;
	}

	public void setEmployeeDesignation(String employeeDesignation) {
		this.employeeDesignation = employeeDesignation;
	}

	public EmployeeOrganization getEmployeeOrganization() {
		return employeeOrganization;
	}

	public void setEmployeeOrganization(
			EmployeeOrganization employeeOrganization) {
		this.employeeOrganization = employeeOrganization;
	}

	public EmployeeDepartment getEmployeeDepartment() {
		return employeeDepartment;
	}

	public void setEmployeeDepartment(EmployeeDepartment employeeDepartment) {
		this.employeeDepartment = employeeDepartment;
	}

	public EmployeeDivision getEmployeeDivision() {
		return employeeDivision;
	}

	public void setEmployeeDivision(EmployeeDivision employeeDivision) {
		this.employeeDivision = employeeDivision;
	}

	public int getIsHaveReports() {
		return isHaveReports;
	}

	public void setIsHaveReports(int isHaveReports) {
		this.isHaveReports = isHaveReports;
	}
}
