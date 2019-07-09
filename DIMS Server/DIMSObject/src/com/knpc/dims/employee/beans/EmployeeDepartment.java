package com.knpc.dims.employee.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EmployeeDepartment {

	private int departmentCode;
	private String department;
	private String oldDimsDeptCode;
	
	
	public String getOldDimsDeptCode() {
		return oldDimsDeptCode;
	}
	public void setOldDimsDeptCode(String oldDimsDeptCode) {
		this.oldDimsDeptCode = oldDimsDeptCode;
	}
	public int getDepartmentCode() {
		return departmentCode;
	}
	public void setDepartmentCode(int departmentCode) {
		this.departmentCode = departmentCode;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public EmployeeDepartment(int departmentCode, String department) {
		super();
		this.departmentCode = departmentCode;
		this.department = department;
	}
	
	public EmployeeDepartment(){
		
	}
}
