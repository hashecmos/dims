package com.knpc.dims.workflow.beans;

public class DIMSSite {

	private String departmentCode;
	private String siteName;
	private String siteType;
	private String siteId;
	private String isExisted;
	

	public String getIsExisted() {
		return isExisted;
	}
	public void setIsExisted(String isExisted) {
		this.isExisted = isExisted;
	}
	public String getDepartmentCode() {
		return departmentCode;
	}
	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getSiteType() {
		return siteType;
	}
	public void setSiteType(String siteType) {
		this.siteType = siteType;
	}
	public String getSiteId() {
		return siteId;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
}
