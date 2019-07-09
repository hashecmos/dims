package com.knpc.dims.filenet.beans;


public class CMObjectBean{
	private String symbolicName;
	private String className;
	private String parentClassName;
	private String classDescription;
	private String dateCreated;
	private String id;
	private String createdBy;
	private String descriptiveText;
	private String objectStoreName;	
	private String dateModified;
	private String lastModifiedBy;
	private String objectType;
	private String parentPath;
	private String securityPolicyId;

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public String getObjectStoreName() {
		return objectStoreName;
	}

	public void setObjectStoreName(String objectStoreName) {
		this.objectStoreName = objectStoreName;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getDescriptiveText() {
		return descriptiveText;
	}

	public void setDescriptiveText(String descriptiveText) {
		this.descriptiveText = descriptiveText;
	}

	public String getDateModified() {
		return dateModified;
	}

	public void setDateModified(String dateModified) {
		this.dateModified = dateModified;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	@Override
	public String toString() {
		return "CMObjectBean [symbolicName=" + symbolicName + ", className="
				+ className + ", dateCreated=" + dateCreated + ", id=" + id
				+ ", createdBy=" + createdBy + ", descriptiveText="
				+ descriptiveText + ", objectStoreName=" + objectStoreName
				+ ", dateModified=" + dateModified + ", lastModifiedBy="
				+ lastModifiedBy + ", objectType=" + objectType
				+ ", parentPath=" + parentPath + "]";
	}

	public String getClassDescription() {
		return classDescription;
	}

	public void setClassDescription(String classDescription) {
		this.classDescription = classDescription;
	}

	public void setParentClassName(String parentClassName) {
		this.parentClassName = parentClassName;
	}

	public String getParentClassName() {
		return parentClassName;
	}

	public void setSecurityPolicyId(String securityPolicyId) {
		this.securityPolicyId = securityPolicyId;
	}

	public String getSecurityPolicyId() {
		return securityPolicyId;
	}
	
	

}
