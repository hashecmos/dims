package com.knpc.dims.filenet.beans;

import java.io.Serializable;

public class AccessControlEntryBean implements Serializable{
	
	Integer accessControlId;	
	private String granteeName;
	private String granteeType;
	private String permissionSource;
	private String accessLevel;
	private String accessType;
	private int inheritableDepth;	
	
	public Integer getAccessControlId() {
		return accessControlId;
	}
	public void setAccessControlId(Integer accessControlId) {
		this.accessControlId = accessControlId;
	}
	public String getGranteeName() {
		return granteeName;
	}
	public void setGranteeName(String granteeName) {
		this.granteeName = granteeName;
	}
	public String getGranteeType() {
		return granteeType;
	}
	public void setGranteeType(String granteeType) {
		this.granteeType = granteeType;
	}
	public String getPermissionSource() {
		return permissionSource;
	}
	public void setPermissionSource(String permissionSource) {
		this.permissionSource = permissionSource;
	}
	public String getAccessLevel() {
		return accessLevel;
	}
	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}
	public String getAccessType() {
		return accessType;
	}
	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}
	public int getInheritableDepth() {
		return inheritableDepth;
	}
	public void setInheritableDepth(int inheritableDepth) {
		this.inheritableDepth = inheritableDepth;
	}
	
	
}
