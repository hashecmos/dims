package com.knpc.dims.filenet.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchResultBean {
	
	private List<PropertyBean> propertyBeanList;
	
	List<HashMap<String, Object>> resultMapList = new ArrayList<HashMap<String, Object>>();

	

	public List<HashMap<String, Object>> getResultMapList() {
		return resultMapList;
	}

	public void setResultMapList(List<HashMap<String, Object>> resultMapList) {
		this.resultMapList = resultMapList;
	}

	public void setPropertyBeanList(List<PropertyBean> propertyBeanList) {
		this.propertyBeanList = propertyBeanList;
	}

	public List<PropertyBean> getPropertyBeanList() {
		return propertyBeanList;
	}

	

}
