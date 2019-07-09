package com.knpc.dims.employee.beans;

public class DelegateBean {

	private String delegationId;
	private String delegateFrom;
	private String delegateTo;
	private String absentLogin;
	private String delegateLogin;
	private String DelegateStatus;
	
	public String getDelegationId() {
		return delegationId;
	}
	public void setDelegationId(String delegationId) {
		this.delegationId = delegationId;
	}
	public String getDelegateFrom() {
		return delegateFrom;
	}
	public void setDelegateFrom(String delegateFrom) {
		this.delegateFrom = delegateFrom;
	}
	public String getDelegateTo() {
		return delegateTo;
	}
	public void setDelegateTo(String delegateTo) {
		this.delegateTo = delegateTo;
	}
	public String getAbsentLogin() {
		return absentLogin;
	}
	public void setAbsentLogin(String absentLogin) {
		this.absentLogin = absentLogin;
	}
	public String getDelegateLogin() {
		return delegateLogin;
	}
	public void setDelegateLogin(String delegateLogin) {
		this.delegateLogin = delegateLogin;
	}
	public String getDelegateStatus() {
		return DelegateStatus;
	}
	public void setDelegateStatus(String delegateStatus) {
		DelegateStatus = delegateStatus;
	}
}
