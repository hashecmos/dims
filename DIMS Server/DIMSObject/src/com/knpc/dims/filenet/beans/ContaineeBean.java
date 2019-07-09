package com.knpc.dims.filenet.beans;

import java.io.Serializable;
import java.util.List;

public class ContaineeBean implements Serializable {
	
	private List<DocumentBean> documents ;
	private List<FolderBean> folders ;
	public List<DocumentBean> getDocuments() {
		return documents;
	}
	public void setDocuments(List<DocumentBean> documents) {
		this.documents = documents;
	}
	public List<FolderBean> getFolders() {
		return folders;
	}
	public void setFolders(List<FolderBean> folders) {
		this.folders = folders;
	}
	

			
}
