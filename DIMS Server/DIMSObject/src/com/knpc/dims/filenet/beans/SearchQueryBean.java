package com.knpc.dims.filenet.beans;

import java.util.List;

public class SearchQueryBean {
	
	private String objectStore;
	private List<String> docClassName;
	private List<String> columnName;
	private List<String> joiningCondition;
	private List<FilterBean> filter;
	private String folderName;
	private String content;
	private boolean includeSubClasses;
	private boolean includeSubFolders;
	private String searchType;
	private String searchBaseType;
	private String simpleSearchCount;
	private String globalFilterOperator;
	private boolean crossDepartment;
	private String departmentId;
	
	private int pageSize = 100;
	private int pageNo = 1;
	
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public boolean isCrossDepartment() {
		return crossDepartment;
	}
	public void setCrossDepartment(boolean crossDepartment) {
		this.crossDepartment = crossDepartment;
	}
	public String getSearchBaseType() {
		return searchBaseType;
	}
	public void setSearchBaseType(String searchBaseType) {
		this.searchBaseType = searchBaseType;
	}
	
	public String getObjectStore() {
		return objectStore;
	}
	public void setObjectStore(String objectStore) {
		this.objectStore = objectStore;
	}
	public List<String> getDocClassName() {
		return docClassName;
	}
	public void setDocClassName(List<String> docClassName) {
		this.docClassName = docClassName;
	}
	public List<String> getColumnName() {
		return columnName;
	}
	public void setColumnName(List<String> columnName) {
		this.columnName = columnName;
	}
	public List<FilterBean> getFilter() {
		return filter;
	}
	public void setFilter(List<FilterBean> filter) {
		this.filter = filter;
	}
	public void setJoiningCondition(List<String> joiningCondition) {
		this.joiningCondition = joiningCondition;
	}
	public List<String> getJoiningCondition() {
		return joiningCondition;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setIncludeSubClasses(boolean includeSubClasses) {
		this.includeSubClasses = includeSubClasses;
	}
	public boolean isIncludeSubClasses() {
		return includeSubClasses;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContent() {
		return content;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	public String getSearchType() {
		return searchType;
	}
	public void setIncludeSubFolders(boolean includeSubFolders) {
		this.includeSubFolders = includeSubFolders;
	}
	public boolean isIncludeSubFolders() {
		return includeSubFolders;
	}
	public void setSimpleSearchCount(String simpleSearchCount) {
		this.simpleSearchCount = simpleSearchCount;
	}
	public String getSimpleSearchCount() {
		return simpleSearchCount;
	}
	public void setGlobalFilterOperator(String globalFilterOperator) {
		this.globalFilterOperator = globalFilterOperator;
	}
	public String getGlobalFilterOperator() {
		return globalFilterOperator;
	}
	
	public int getPageSize() { return this.pageSize; }
	public int getPageNo() { return this.pageNo; }
	
	public void setPageSize(int pSize) { this.pageSize = pSize; }
	public void setPageNo(int pNo) { this.pageNo = pNo; }

}
