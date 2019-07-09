package com.knpc.dims.filenet.beans;

import java.io.Serializable;

public class DocumentTransferBean implements Serializable{
	String folderId;
	String folderPath;
	String docClass;
	String securityPolicyId;
	String uploadFileName;
	String fileGUID;
	String objectId;
	
	public String getFolderId() {
		return folderId;
	}
	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}
	public String getDocClass() {
		return docClass;
	}
	public void setDocClass(String docClass) {
		this.docClass = docClass;
	}
	public String getSecurityPolicyId() {
		return securityPolicyId;
	}
	public void setSecurityPolicyId(String securityPolicyId) {
		this.securityPolicyId = securityPolicyId;
	}
	public String getUploadFileName() {
		return uploadFileName;
	}
	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public String getFileGUID() {
		return fileGUID;
	}
	public void setFileGUID(String fileGUID) {
		this.fileGUID = fileGUID;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getFolderPath() {
		return folderPath;
	}
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
}
