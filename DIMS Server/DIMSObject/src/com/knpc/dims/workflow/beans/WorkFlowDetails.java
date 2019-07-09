package com.knpc.dims.workflow.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="WorkFlowDetails")
public class WorkFlowDetails {

	private String workflowID;
	private String workflowName;
	private String workflowSubject;
	private String workflowDeadline;
	private String workflowReminderDate;
	private int workflowPriority;
	private String workflowLaunchedBy;
	private String workflowLaunchedOnBehalf;
	private String workflowBeginDate;
	private String workflowEndDate;
	private String workflow_CompletedBy;
	private String workflow_CompletedOnBehalf;
	private String workflowStatus;
	private String workflowPrimaryDocument;
	private WorkItemDetails	workItemDetails;
	private WorkflowAttachments	workflowAttachment;
	public String getWorkflowID() {
		return workflowID;
	}
	public void setWorkflowID(String workflowID) {
		this.workflowID = workflowID;
	}
	public String getWorkflowName() {
		return workflowName;
	}
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}
	public String getWorkflowSubject() {
		return workflowSubject;
	}
	public void setWorkflowSubject(String workflowSubject) {
		this.workflowSubject = workflowSubject;
	}
	public String getWorkflowDeadline() {
		return workflowDeadline;
	}
	public void setWorkflowDeadline(String workflowDeadline) {
		this.workflowDeadline = workflowDeadline;
	}
	public String getWorkflowReminderDate() {
		return workflowReminderDate;
	}
	public void setWorkflowReminderDate(String workflowReminderDate) {
		this.workflowReminderDate = workflowReminderDate;
	}
	public int getWorkflowPriority() {
		return workflowPriority;
	}
	public void setWorkflowPriority(int workflowPriority) {
		this.workflowPriority = workflowPriority;
	}
	public String getWorkflowLaunchedBy() {
		return workflowLaunchedBy;
	}
	public void setWorkflowLaunchedBy(String workflowLaunchedBy) {
		this.workflowLaunchedBy = workflowLaunchedBy;
	}
	public String getWorkflowLaunchedOnBehalf() {
		return workflowLaunchedOnBehalf;
	}
	public void setWorkflowLaunchedOnBehalf(String workflowLaunchedOnBehalf) {
		this.workflowLaunchedOnBehalf = workflowLaunchedOnBehalf;
	}
	public String getWorkflowBeginDate() {
		return workflowBeginDate;
	}
	public void setWorkflowBeginDate(String workflowBeginDate) {
		this.workflowBeginDate = workflowBeginDate;
	}
	public String getWorkflowEndDate() {
		return workflowEndDate;
	}
	public void setWorkflowEndDate(String workflowEndDate) {
		this.workflowEndDate = workflowEndDate;
	}
	public String getWorkflow_CompletedBy() {
		return workflow_CompletedBy;
	}
	public void setWorkflow_CompletedBy(String workflow_CompletedBy) {
		this.workflow_CompletedBy = workflow_CompletedBy;
	}
	public String getWorkflow_CompletedOnBehalf() {
		return workflow_CompletedOnBehalf;
	}
	public void setWorkflow_CompletedOnBehalf(String workflow_CompletedOnBehalf) {
		this.workflow_CompletedOnBehalf = workflow_CompletedOnBehalf;
	}
	public String getWorkflowStatus() {
		return workflowStatus;
	}
	public void setWorkflowStatus(String workflowStatus) {
		this.workflowStatus = workflowStatus;
	}
	public String getWorkflowPrimaryDocument() {
		return workflowPrimaryDocument;
	}
	public void setWorkflowPrimaryDocument(String workflowPrimaryDocument) {
		this.workflowPrimaryDocument = workflowPrimaryDocument;
	}
	
	public WorkItemDetails getWorkItemDetails() {
		return workItemDetails;
	}
	public void setWorkItemDetails(WorkItemDetails workItemDetails) {
		this.workItemDetails = workItemDetails;
	}
	public WorkflowAttachments getWorkflowAttachment() {
		return workflowAttachment;
	}
	public void setWorkflowAttachment(WorkflowAttachments workflowAttachment) {
		this.workflowAttachment = workflowAttachment;
	}
	
	
	
}
