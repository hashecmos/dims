package com.knpc.dims.filenet.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Folder")
public class FolderBean extends CMObjectBean{
	private String folderPath;
	private String path;
	private String type = "Folder";
	private String isFilingAllowed;
	

	public String getType() {
		return type;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setIsFilingAllowed(String isFilingAllowed) {
		this.isFilingAllowed = isFilingAllowed;
	}

	public String getIsFilingAllowed() {
		return isFilingAllowed;
	}

	@Override
	public String toString() {
		return "FolderBean [folderpath=" + folderPath + ", path=" + path
				+ ", type=" + type + ", isFilingAllowed=" + isFilingAllowed
				+ "]";
	}



}
