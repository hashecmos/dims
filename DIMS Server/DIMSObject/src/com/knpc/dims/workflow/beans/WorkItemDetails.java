package com.knpc.dims.workflow.beans;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import com.knpc.dims.filenet.beans.DocumentBean;
@XmlRootElement(name="WorkItem")
public class WorkItemDetails {

	private String workflowWorkItemID;
	private String workflowId;
	private String workflowSender;
	private ArrayList<WorkflowRecipient> workflowRecipientList;
	private String workflowWorkItemType;
	private int workflowStepNo;
	private String workflowParentItem ;
	private String workflowInstruction;
	private String workflowItemReceivedOn;
	private String workflowItemReceivedBy; // SK 24/7  
	private String workflowItemDeadline;
	private String workflowItemReminderDate;
	private String workflowItemRootSender;
	private String workflowItemStatus;
	private String workflowItemAction;
	private String workflowItemActionComment;
	private String workflowItemActionBy;
	private String workflowItemActionOnBehalf;
	private int workflowItemSenderDepartment;
	private int workflowItemReceiverDepartment;
	private int workflowItemSenderDiv;
	private int workflowItemReceiverDiv;
	private String workflowItemSysStatus;
	
	private String workflowItemSubject;
	private String workflowItemPriority;
	private String workflowSenderName;
	
	private ArrayList<WorkflowAttachments> workflowAttachments;
	private ArrayList<DocumentBean> documentBean;
	private String totalCount;
	private String workflowName;
	private String isOverdue;
	private String senderDepartment;
	private String senderDivision;
	
	public String getWorkflowName() {
		return workflowName;
	}
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}
	public String getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}
	public String getWorkflowWorkItemID() {
		return workflowWorkItemID;
	}
	public void setWorkflowWorkItemID(String workflowWorkItemID) {
		this.workflowWorkItemID = workflowWorkItemID;
	}
	public String getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	public String getWorkflowSender() {
		return workflowSender;
	}
	public void setWorkflowSender(String workflowSender) {
		this.workflowSender = workflowSender;
	}
	public String getWorkflowReceivedBy() {
		return workflowItemReceivedBy;
	}
	public void setWorkflowReceivedBy(String workflowReceiver) {
		this.workflowItemReceivedBy = workflowReceiver;
	}
	public int getWorkflowStepNo() {
		return workflowStepNo;
	}
	public void setWorkflowStepNo(int workflowStepNo) {
		this.workflowStepNo = workflowStepNo;
	}
	public String getWorkflowParentItem() {
		return workflowParentItem;
	}
	public void setWorkflowParentItem(String workflowParentItem) {
		this.workflowParentItem = workflowParentItem;
	}
	public String getWorkflowInstruction() {
		return workflowInstruction;
	}
	public void setWorkflowInstruction(String workflowInstruction) {
		this.workflowInstruction = workflowInstruction;
	}
	public String getWorkflowItemReceivedOn() {
		return workflowItemReceivedOn;
	}
	public void setWorkflowItemReceivedOn(String workflowItemReceivedOn) {
		this.workflowItemReceivedOn = workflowItemReceivedOn;
	}
	public String getWorkflowItemDeadline() {
		return workflowItemDeadline;
	}
	public void setWorkflowItemDeadline(String workflowItemDeadline) {
		this.workflowItemDeadline = workflowItemDeadline;
	}
	public String getWorkflowItemReminderDate() {
		return workflowItemReminderDate;
	}
	public void setWorkflowItemReminderDate(String workflowItemReminderDate) {
		this.workflowItemReminderDate = workflowItemReminderDate;
	}
	public String getWorkflowItemRootSender() {
		return workflowItemRootSender;
	}
	public void setWorkflowItemRootSender(String workflowItemRootSender) {
		this.workflowItemRootSender = workflowItemRootSender;
	}
	public String getWorkflowItemStatus() {
		return workflowItemStatus;
	}
	public void setWorkflowItemStatus(String workflowItemStatus) {
		this.workflowItemStatus = workflowItemStatus;
	}
	public String getWorkflowItemAction() {
		return workflowItemAction;
	}
	public void setWorkflowItemAction(String workflowItemAction) {
		this.workflowItemAction = workflowItemAction;
	}
	public String getWorkflowItemActionComment() {
		return workflowItemActionComment;
	}
	public void setWorkflowItemActionComment(String workflowItemActionComment) {
		this.workflowItemActionComment = workflowItemActionComment;
	}
	public String getWorkflowItemActionBy() {
		return workflowItemActionBy;
	}
	public void setWorkflowItemActionBy(String workflowItemActionBy) {
		this.workflowItemActionBy = workflowItemActionBy;
	}
	public String getWorkflowItemActionOnBehalf() {
		return workflowItemActionOnBehalf;
	}
	public void setWorkflowItemActionOnBehalf(String workflowItemActionOnBehalf) {
		this.workflowItemActionOnBehalf = workflowItemActionOnBehalf;
	}
	public int getWorkflowItemSenderDepartment() {
		return workflowItemSenderDepartment;
	}
	public void setWorkflowItemSenderDepartment(int workflowItemSenderDepartment) {
		this.workflowItemSenderDepartment = workflowItemSenderDepartment;
	}
	public int getWorkflowItemReceiverDepartment() {
		return workflowItemReceiverDepartment;
	}
	public void setWorkflowItemReceiverDepartment(int workflowItemReceiverDepartment) {
		this.workflowItemReceiverDepartment = workflowItemReceiverDepartment;
	}
	public int getWorkflowItemSenderDiv() {
		return workflowItemSenderDiv;
	}
	public void setWorkflowItemSenderDiv(int workflowItemSenderDiv) {
		this.workflowItemSenderDiv = workflowItemSenderDiv;
	}
	public int getWorkflowItemReceiverDiv() {
		return workflowItemReceiverDiv;
	}
	public void setWorkflowItemReceiverDiv(int workflowItemReceiverDiv) {
		this.workflowItemReceiverDiv = workflowItemReceiverDiv;
	}
	public String getWorkflowItemSubject() {
		return workflowItemSubject;
	}
	public void setWorkflowItemSubject(String workflowItemSubject) {
		this.workflowItemSubject = workflowItemSubject;
	}
	public String getWorkflowItemPriority() {
		return workflowItemPriority;
	}
	public void setWorkflowItemPriority(String workflowItemPriority) {
		this.workflowItemPriority = workflowItemPriority;
	}
	public String getWorkflowItemSysStatus() {
		return workflowItemSysStatus;
	}
	public void setWorkflowItemSysStatus(String workflowItemSysStatus) {
		this.workflowItemSysStatus = workflowItemSysStatus;
	}
	public String getWorkflowSenderName() {
		return workflowSenderName;
	}
	public void setWorkflowSenderName(String workflowSenderName) {
		this.workflowSenderName = workflowSenderName;
	}
	public ArrayList<WorkflowRecipient> getWorkflowRecipientList() {
		return workflowRecipientList;
	}
	public void setWorkflowRecipientList(
			ArrayList<WorkflowRecipient> workflowRecipientList) {
		this.workflowRecipientList = workflowRecipientList;
	}
	public String getWorkflowWorkItemType() {
		return workflowWorkItemType;
	}
	public void setWorkflowWorkItemType(String workflowWorkItemType) {
		this.workflowWorkItemType = workflowWorkItemType;
	}
	public ArrayList<WorkflowAttachments> getWorkflowAttachments() {
		return workflowAttachments;
	}
	public void setWorkflowAttachments(
			ArrayList<WorkflowAttachments> workflowAttachments) {
		this.workflowAttachments = workflowAttachments;
	}
	public ArrayList<DocumentBean> getDocumentBean() {
		return documentBean;
	}
	public void setDocumentBean(ArrayList<DocumentBean> documentBean) {
		this.documentBean = documentBean;
	}
	public String getWorkflowItemReceivedBy() {
		return workflowItemReceivedBy;
	}
	public void setWorkflowItemReceivedBy(String workflowItemReceivedBy) {
		this.workflowItemReceivedBy = workflowItemReceivedBy;
	}
	public String getIsOverdue() {
		return isOverdue;
	}
	public void setIsOverdue(String isOverdue) {
		this.isOverdue = isOverdue;
	}
	public String getSenderDepartment() {
		return senderDepartment;
	}
	public void setSenderDepartment(String senderDepartment) {
		this.senderDepartment = senderDepartment;
	}
	public String getSenderDivision() {
		return senderDivision;
	}
	public void setSenderDivision(String senderDivision) {
		this.senderDivision = senderDivision;
	}
	
	
}
