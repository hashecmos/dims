package com.knpc.dims.filenet.beans;

import java.util.ArrayList;

public class EmailBean {
	private String from;
	private String body;
	private String[] objectId;
	private String subject;
	private String osName;
	private ArrayList<EmailRecipient>emailRecipientList;
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	public String[] getObjectId() {
		return objectId;
	}
	public void setObjectId(String[] objectId) {
		this.objectId = objectId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getOsName() {
		return osName;
	}
	public void setOsName(String osName) {
		this.osName = osName;
	}
	public ArrayList<EmailRecipient> getEmailRecipientList() {
		return emailRecipientList;
	}
	public void setEmailRecipientList(ArrayList<EmailRecipient> emailRecipientList) {
		this.emailRecipientList = emailRecipientList;
	}
	
	
}
