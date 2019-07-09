package com.knpc.dims.workflow.beans;

public class PendingWorkItemDetails {
	
	private String totalCount;
	private String workflowBeginDate;
	private String receiveDate;
	private String workitemStatus;
	
	private String sender;
	private String recipient;
	private String deadLine;
	private String Action;
	
	private String actionBy;
	private String senderDepartment;
	private String receiverDepartment;
	private String senderDiv;
	private String receiverDiv;
	
	private String subject;
	
	//fullHistory
	private String actionDate;
	private String systemStatus;
	
	private String workflowID;
	private String comments;
	
	private String isOverdue;
	private String fromDate;
	private String toDate;
	
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getIsOverdue() {
		return isOverdue;
	}
	public void setIsOverdue(String isOverdue) {
		this.isOverdue = isOverdue;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getWorkflowID() {
		return workflowID;
	}
	public void setWorkflowID(String workflowID) {
		this.workflowID = workflowID;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getActionDate() {
		return actionDate;
	}
	public void setActionDate(String actionDate) {
		this.actionDate = actionDate;
	}
	public String getSystemStatus() {
		return systemStatus;
	}
	public void setSystemStatus(String systemStatus) {
		this.systemStatus = systemStatus;
	}
	public String getAction() {
		return Action;
	}
	public void setAction(String action) {
		Action = action;
	}
	
	public String getWorkitemStatus() {
		return workitemStatus;
	}
	public void setWorkitemStatus(String workitemStatus) {
		this.workitemStatus = workitemStatus;
	}
	public String getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}
	public String getWorkflowBeginDate() {
		return workflowBeginDate;
	}
	public void setWorkflowBeginDate(String workflowBeginDate) {
		this.workflowBeginDate = workflowBeginDate;
	}
	public String getReceiveDate() {
		return receiveDate;
	}
	public void setReceiveDate(String receiveDate) {
		this.receiveDate = receiveDate;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getRecipient() {
		return recipient;
	}
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	public String getDeadLine() {
		return deadLine;
	}
	public void setDeadLine(String deadLine) {
		this.deadLine = deadLine;
	}
	public String getActionBy() {
		return actionBy;
	}
	public void setActionBy(String actionBy) {
		this.actionBy = actionBy;
	}
	public String getSenderDepartment() {
		return senderDepartment;
	}
	public void setSenderDepartment(String senderDepartment) {
		this.senderDepartment = senderDepartment;
	}
	public String getReceiverDepartment() {
		return receiverDepartment;
	}
	public void setReceiverDepartment(String receiverDepartment) {
		this.receiverDepartment = receiverDepartment;
	}
	public String getSenderDiv() {
		return senderDiv;
	}
	public void setSenderDiv(String senderDiv) {
		this.senderDiv = senderDiv;
	}
	public String getReceiverDiv() {
		return receiverDiv;
	}
	public void setReceiverDiv(String receiverDiv) {
		this.receiverDiv = receiverDiv;
	}
	

}
