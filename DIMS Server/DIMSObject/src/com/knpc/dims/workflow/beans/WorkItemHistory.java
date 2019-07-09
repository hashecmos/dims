package com.knpc.dims.workflow.beans;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name="WorkFlowHistory")
public class WorkItemHistory {

	private String workflowHistoryId;
	private String workflowId;
	private String workflowActionUser;
	private String workflowActionTime;
	private String workflowActionItemId;
	private String workflowActionDetails;
	private String workflowActionDocument;
	
	private String senderName;
	private String recipientName;
	private String workItemType;
	private String workItemStatus;
	private String workItemReceivedOn;
	private String workItemDeadLine;
	private String workflowInstruction;
	private String workItemActionComment;
	
	public String getWorkItemReceivedOn() {
		return workItemReceivedOn;
	}
	public void setWorkItemReceivedOn(String workItemReceivedOn) {
		this.workItemReceivedOn = workItemReceivedOn;
	}
	public String getWorkItemDeadLine() {
		return workItemDeadLine;
	}
	public void setWorkItemDeadLine(String workItemDeadLine) {
		this.workItemDeadLine = workItemDeadLine;
	}
	public String getWorkflowInstruction() {
		return workflowInstruction;
	}
	public void setWorkflowInstruction(String workflowInstruction) {
		this.workflowInstruction = workflowInstruction;
	}

	public String getWorkItemActionComment() {
		return workItemActionComment;
	}
	public void setWorkItemActionComment(String workItemActionComment) {
		this.workItemActionComment = workItemActionComment;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getRecipientName() {
		return recipientName;
	}
	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}
	public String getWorkItemType() {
		return workItemType;
	}
	public void setWorkItemType(String workItemType) {
		this.workItemType = workItemType;
	}
	public String getWorkItemStatus() {
		return workItemStatus;
	}
	public void setWorkItemStatus(String workItemStatus) {
		this.workItemStatus = workItemStatus;
	}
	public String getWorkflowHistoryId() {
		return workflowHistoryId;
	}
	public void setWorkflowHistoryId(String workflowHistoryId) {
		this.workflowHistoryId = workflowHistoryId;
	}
	public String getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	public String getWorkflowActionUser() {
		return workflowActionUser;
	}
	public void setWorkflowActionUser(String workflowActionUser) {
		this.workflowActionUser = workflowActionUser;
	}
	public String getWorkflowActionTime() {
		return workflowActionTime;
	}
	public void setWorkflowActionTime(String workflowActionTime) {
		this.workflowActionTime = workflowActionTime;
	}
	public String getWorkflowActionItemId() {
		return workflowActionItemId;
	}
	public void setWorkflowActionItemId(String workflowActionItemId) {
		this.workflowActionItemId = workflowActionItemId;
	}
	public String getWorkflowActionDetails() {
		return workflowActionDetails;
	}
	public void setWorkflowActionDetails(String workflowActionDetails) {
		this.workflowActionDetails = workflowActionDetails;
	}
	public String getWorkflowActionDocument() {
		return workflowActionDocument;
	}
	public void setWorkflowActionDocument(String workflowActionDocument) {
		this.workflowActionDocument = workflowActionDocument;
	}
	
	

}
