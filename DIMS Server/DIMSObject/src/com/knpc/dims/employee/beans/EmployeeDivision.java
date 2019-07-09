package com.knpc.dims.employee.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EmployeeDivision {
	
	private int empDivisionCode;
	private String empDivision;
	public int getEmpDivisionCode() {
		return empDivisionCode;
	}
	public void setEmpDivisionCode(int empDivisionCode) {
		this.empDivisionCode = empDivisionCode;
	}
	public String getEmpDivision() {
		return empDivision;
	}
	public void setEmpDivision(String empDivision) {
		this.empDivision = empDivision;
	}
	public EmployeeDivision(int empDivisionCode, String empDivision) {
		super();
		this.empDivisionCode = empDivisionCode;
		this.empDivision = empDivision;
	}
	
	public EmployeeDivision(){
		
	}
}
