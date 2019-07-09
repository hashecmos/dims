package com.knpc.dims.employee.beans;

public class EmployeeProfile {
	
	private String employeeLogin;
	private String employeeName;
	private String employeeSupervisor;
	private String employeeEmail;
	private String employeeDesignation;
	public String getEmployeeLogin() {
		return employeeLogin;
	}
	public void setEmployeeLogin(String employeeLogin) {
		this.employeeLogin = employeeLogin;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
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
	public EmployeeProfile(String employeeLogin, String employeeName,
			String employeeSupervisor, String employeeEmail,
			String employeeDesignation) {
		super();
		this.employeeLogin = employeeLogin;
		this.employeeName = employeeName;
		this.employeeSupervisor = employeeSupervisor;
		this.employeeEmail = employeeEmail;
		this.employeeDesignation = employeeDesignation;
	}
	
	public EmployeeProfile(){
		
	}
}
