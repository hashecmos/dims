package com.knpc.dims.filenet.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knpc.dims.db.DBAdaptor;
import com.knpc.dims.filenet.FileNetAdaptor;
import com.knpc.dims.filenet.beans.FilterBean;
import com.knpc.dims.filenet.beans.SearchQueryBean;
import com.knpc.dims.filenet.beans.SearchResultBean;

public class ContentSearchService {
	
	private HttpServletRequest req;
	private HttpServletResponse resp;
	
	public ContentSearchService(HttpServletRequest req,HttpServletResponse resp) {
		this.req = req;
		this.resp = resp;
	}

	public SearchResultBean simpleSearch(String jsonString) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		SearchQueryBean searchQueryBean = mapper.readValue(jsonString, new TypeReference<SearchQueryBean>(){});
		FileNetAdaptor adaptor = new FileNetAdaptor();
		DBAdaptor db = new DBAdaptor();
		int oldDepartmentId = 0;
		List<FilterBean> filteBeanList = searchQueryBean.getFilter();
		for (int i = 0; i < filteBeanList.size(); i++) {
			if(filteBeanList.get(i).getFilterName().equalsIgnoreCase("DepartmentID")){
				
				oldDepartmentId = db.getOldDimsDepartmentId(filteBeanList.get(i).getFilterValue());
				if(oldDepartmentId != 0){
					filteBeanList.get(i).setFilterValue(String.valueOf(oldDepartmentId));
					searchQueryBean.setFilter(filteBeanList);
				}
				
			}
		}
		if(oldDepartmentId == 0){
			oldDepartmentId = db.getOldDimsDepartmentId(searchQueryBean.getDepartmentId());
		}
		
		
		SearchResultBean searchResultBean = new SearchResultBean();
		if(adaptor.validateRequest(req, resp)) {
			searchResultBean = adaptor.getSearchResults(searchQueryBean,oldDepartmentId);
			searchResultBean = updateDepartmentName(db, searchQueryBean, searchResultBean);
		}
		return searchResultBean;
	}
	
	private SearchResultBean updateDepartmentName(DBAdaptor db, SearchQueryBean searchQueryBean, SearchResultBean searchResultBean) {
		if(searchQueryBean == null)
			return searchResultBean;
		
		String depid = "";
		String deptName = "";
		
		int nCount = 0;
		for(HashMap<String, Object> properties: searchResultBean.getResultMapList()) {
			if(properties.containsKey("department")) {
				if(searchQueryBean.isCrossDepartment() || (nCount <= 0)) {
					depid = (String)properties.get("department");
					deptName = db.getNameFromOldDimsDepartmentId(depid);
				}
				if(deptName.length() > 0)
					properties.put("department", deptName);
				nCount++;
			}
		}
		
		
		return searchResultBean;
	}
}
