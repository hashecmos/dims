package com.knpc.dims.filenet.beans;

import java.util.List;

public class EventBean {
	
	private String eventName;
	private String eventDate;
	private String initiatedBy;
	private String eventStatus;
	private List<PropertyBean> modifiedPropertyList;
	
	
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getEventDate() {
		return eventDate;
	}
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}
	public String getInitiatedBy() {
		return initiatedBy;
	}
	public void setInitiatedBy(String initiatedBy) {
		this.initiatedBy = initiatedBy;
	}
	public String getEventStatus() {
		return eventStatus;
	}
	public void setEventStatus(String eventStatus) {
		this.eventStatus = eventStatus;
	}
	public void setModifiedPropertyList(List<PropertyBean> modifiedPropertyList) {
		this.modifiedPropertyList = modifiedPropertyList;
	}
	public List<PropertyBean> getModifiedPropertyList() {
		return modifiedPropertyList;
	}
	

}
