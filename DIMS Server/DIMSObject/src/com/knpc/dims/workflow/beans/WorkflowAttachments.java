package com.knpc.dims.workflow.beans;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="WorkFlowAttachment")
public class WorkflowAttachments {

	private String workflowAttachmentId;
	private String workflowId;
	private String workflowDocumentId;
	private String workflowAttachmentType;
	private String documentId;
	
	public String getWorkflowAttachmentId() {
		return workflowAttachmentId;
	}
	public void setWorkflowAttachmentId(String workflowAttachmentId) {
		this.workflowAttachmentId = workflowAttachmentId;
	}
	public String getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	public String getWorkflowDocumentId() {
		return workflowDocumentId;
	}
	public void setWorkflowDocumentId(String workflowDocumentId) {
		this.workflowDocumentId = workflowDocumentId;
	}
	public String getWorkflowAttachmentType() {
		return workflowAttachmentType;
	}
	public void setWorkflowAttachmentType(String workflowAttachmentType) {
		this.workflowAttachmentType = workflowAttachmentType;
	}
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	
}
