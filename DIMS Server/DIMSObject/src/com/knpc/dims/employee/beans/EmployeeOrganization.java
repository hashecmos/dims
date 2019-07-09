package com.knpc.dims.employee.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EmployeeOrganization {
	private String employeeOrg;
	private String employeeOrgCode;
	private String employeeOrgId;
	private String employeeOrgType;

	public String getEmployeeOrg() {
		return employeeOrg;
	}

	public void setEmployeeOrg(String employeeOrg) {
		this.employeeOrg = employeeOrg;
	}

	public String getEmployeeOrgCode() {
		return employeeOrgCode;
	}

	public void setEmployeeOrgCode(String employeeOrgCode) {
		this.employeeOrgCode = employeeOrgCode;
	}

	public String getEmployeeOrgId() {
		return employeeOrgId;
	}

	public void setEmployeeOrgId(String employeeOrgId) {
		this.employeeOrgId = employeeOrgId;
	}

	public String getEmployeeOrgType() {
		return employeeOrgType;
	}

	public void setEmployeeOrgType(String employeeOrgType) {
		this.employeeOrgType = employeeOrgType;
	}

	public EmployeeOrganization(String employeeOrg,
			String employeeOrgCode, String employeeOrgId, String employeeOrgType) {
		super();
		this.employeeOrg = employeeOrg;
		this.employeeOrgCode = employeeOrgCode;
		this.employeeOrgId = employeeOrgId;
		this.employeeOrgType = employeeOrgType;
	}

	public EmployeeOrganization() {

	}

}
