package com.knpc.dims.filenet.beans;

import java.io.InputStream;
import java.util.List;

public class DocumentBean extends CMObjectBean {

	private List<PropertyBean> properties;
	private List<InputStream> contentList;	
	private double size;
	private String mimeType;
	private int majorVersion;
	private String folderPath;
	private boolean hasMultipleAttachments;
	private String checkedOutBy;
	private boolean isReserved;
	private boolean hasLinks;
	private List<FolderBean> foldersFiledIn;
	private List<VersionBean> versions;
	private List<AttachmentBean> attachments;
	private List<AccessControlEntryBean> directPermissions;
	private List<EventBean> events;
	private String versionSeriesId;
	
	private String referenceNo;
	private String documentID;
	private String subject;
	private String documentDate;
	private String department;
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getReferenceNo() {
		return referenceNo;
	}
	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}
	public String getDocumentID() {
		return documentID;
	}
	public void setDocumentID(String documentID) {
		this.documentID = documentID;
	}
	public String getDocumentDate() {
		return documentDate;
	}
	public void setDocumentDate(String documentDate) {
		this.documentID = documentDate;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public List<PropertyBean> getProperties() {
		return properties;
	}
	public void setProperties(List<PropertyBean> properties) {
		this.properties = properties;
	}
	public List<InputStream> getContentList() {
		return contentList;
	}
	public void setContentList(List<InputStream> contentList) {
		this.contentList = contentList;
	}


	public boolean isReserved() {
		return isReserved;
	}
	public void setReserved(boolean isReserved) {
		this.isReserved = isReserved;
	}
	public boolean isHasLinks() {
		return hasLinks;
	}
	public void setHasLinks(boolean hasLinks) {
		this.hasLinks = hasLinks;
	}
	
	public void setHasMultipleAttachments(boolean hasMultipleAttachments) {
		this.hasMultipleAttachments = hasMultipleAttachments;
	}
	
	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}

	public int getMajorVersion() {
		return majorVersion;
	}
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	public String getFolderPath() {
		return folderPath;
	}
	
	public String getCheckedOutBy() {
		return checkedOutBy;
	}
	public void setCheckedOutBy(String checkedOutBy) {
		this.checkedOutBy = checkedOutBy;
	}
	public boolean isHasMultipleAttachments() {
		return hasMultipleAttachments;
	}
	public List<FolderBean> getFoldersFiledIn() {
		return foldersFiledIn;
	}
	public void setFoldersFiledIn(List<FolderBean> foldersFiledIn) {
		this.foldersFiledIn = foldersFiledIn;
	}
	public List<VersionBean> getVersions() {
		return versions;
	}
	public void setVersions(List<VersionBean> versions) {
		this.versions = versions;
	}
	public List<AttachmentBean> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<AttachmentBean> attachments) {
		this.attachments = attachments;
	}
	public List<AccessControlEntryBean> getDirectPermissions() {
		return directPermissions;
	}
	public void setDirectPermissions(List<AccessControlEntryBean> directPermissions) {
		this.directPermissions = directPermissions;
	}
	public List<EventBean> getEvents() {
		return events;
	}
	public void setEvents(List<EventBean> events) {
		this.events = events;
	}
	public String getVersionSeriesId() {
		return versionSeriesId;
	}
	public void setVersionSeriesId(String versionSeriesId) {
		this.versionSeriesId = versionSeriesId;
	}

	public String getPropertyValue(String propertyName)
	{
		for (PropertyBean property : properties) {
			if (property.getPropertyName().equals(propertyName))
			{
				return property.getPropertyValue();
			}
		}
		return "";
	}

}
