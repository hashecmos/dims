package com.knpc.dims.workflow.beans;

import java.util.ArrayList;

public class WorkflowStatistic {
	
	private String totWF;
	private String totNWF;
	private String totAWF;
	private String totOWF;	
	private ArrayList<DivisionWF> divWFList;
	private String divName;
	
	public String getTotWF() {
		return totWF;
	}
	public void setTotWF(String totWF) {
		this.totWF = totWF;
	}
	public String getTotNWF() {
		return totNWF;
	}
	public void setTotNWF(String totNWF) {
		this.totNWF = totNWF;
	}
	public String getTotAWF() {
		return totAWF;
	}
	public void setTotAWF(String totAWF) {
		this.totAWF = totAWF;
	}
	public String getTotOWF() {
		return totOWF;
	}
	public void setTotOWF(String totOWF) {
		this.totOWF = totOWF;
	}
	public ArrayList<DivisionWF> getDivWFList() {
		return divWFList;
	}
	public void setDivWFList(ArrayList<DivisionWF> divWFList) {
		this.divWFList = divWFList;
	}
	public String getDivName() {
		return divName;
	}
	public void setDivName(String divName) {
		this.divName = divName;
	}
	
	
}
