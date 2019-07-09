package com.knpc.dims.filenet.beans;

public class FilterBean {
	
	private String filterName;
	private String filterValue;
	private String filterCondition;
	private String filterDataType;
	public String getFilterName() {
		return filterName;
	}
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
	public String getFilterValue() {
		return filterValue;
	}
	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}
	public String getFilterCondition() {
		return filterCondition;
	}
	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}
	public void setFilterDataType(String filterDataType) {
		this.filterDataType = filterDataType;
	}
	public String getFilterDataType() {
		return filterDataType;
	}

}
