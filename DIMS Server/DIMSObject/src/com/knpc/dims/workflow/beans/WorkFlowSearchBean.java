package com.knpc.dims.workflow.beans;


public class WorkFlowSearchBean {
	

	private String referenceNumber;
	private String documentId;
	private String recieveDate;
	private String fromUser;
	private String toUser;
	private String subject;
	private String instruction;
	private String comments;
	private String loginUser;
	private String workFlowSearchType;
	private int pageSize = 0;
	private int pageNo = 0;
	private String dateType;
	private String filterBy;
	
	
	public String getFilterBy() {
		return filterBy;
	}
	public void setFilterBy(String filterBy) {
		this.filterBy = filterBy;
	}
	public String getReferenceNumber() {
		return referenceNumber;
	}
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public String getRecieveDate() {
		return recieveDate;
	}
	public void setRecieveDate(String recieveDate) {
		this.recieveDate = recieveDate;
	}
	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	public String getToUser() {
		return toUser;
	}
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getInstruction() {
		return instruction;
	}
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getLoginUser() {
		return loginUser;
	}
	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}
	public String getWorkFlowSearchType() {
		return workFlowSearchType;
	}
	public void setWorkFlowSearchType(String workFlowSearchType) {
		this.workFlowSearchType = workFlowSearchType;
	}
	public int getPageSize() { return this.pageSize; }
	public int getPageNo() { return this.pageNo; }
	public void setPageSize(int pSize) { this.pageSize = pSize; }
	public void setPageNp(int pNo) { this.pageNo = pNo; }
	public String getDateType() {
		return dateType;
	}
	public void setDateType(String dateType) {
		this.dateType = dateType;
	}
	
}
