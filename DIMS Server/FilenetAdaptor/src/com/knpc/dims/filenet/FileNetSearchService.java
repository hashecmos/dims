package com.knpc.dims.filenet;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.openjpa.util.IntId;

import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.constants.TypeID;
import com.filenet.api.core.Document;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.Properties;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.knpc.dims.filenet.beans.FilterBean;
import com.knpc.dims.filenet.beans.SearchQueryBean;
import com.knpc.dims.filenet.beans.SearchResultBean;
import com.knpc.dims.filenet.util.Utils;


public class FileNetSearchService {

	private static final Logger logger1 = Logger.getLogger(FileNetSearchService.class);
	private FileNetAdaptor adaptor;

	public FileNetSearchService(FileNetAdaptor adaptor) {
		this.adaptor = adaptor;
	}

	public SearchResultBean getSearchResults(ObjectStore os,SearchQueryBean searchBean, int oldDepartmentId) throws Exception {
		SearchResultBean searchResultBean = null;
		String checkedOutBy = null;

		try {
			
			long startTime = System.currentTimeMillis();
			
			SearchScope searchScope = new SearchScope(os);
			SearchSQL sqlObject = new SearchSQL();
			
			String query = buildQuery(searchBean,oldDepartmentId);
			if (query != null && "".equals(query)) {
				return new SearchResultBean();
			}
			
			sqlObject.setQueryString(query);

			String queryOutString = new String(query.getBytes(), "UTF-8");
			System.out.println("############ Modified ###### : " + queryOutString);

			PropertyFilter pf = new PropertyFilter();
			FilterElement fe = new FilterElement(
					null,
					null,
					null,
					"SymbolicName Id DocumentTitle ClassDescription Name VersionSeries MimeType Creator DateCreated DateLastModified LastModifier IsReserved ContentSize MajorVersionNumber ContentElementsPresent Reservation FoldersFiledIn Versions PathName FolderName ContentElements ElementSequenceNumber RetrievalName ContentType DateCheckedIn DisplayName",
					null);
			pf.addIncludeProperty(fe);
			logger1.info("Start time ::" + System.currentTimeMillis());
			IndependentObjectSet rowSet = searchScope.fetchObjects(sqlObject,10, pf, new Boolean(false));
			logger1.info("End time ::" + System.currentTimeMillis());
			Iterator<IndependentObject> iter = rowSet.iterator();

			if (iter != null) {
				searchResultBean = new SearchResultBean();
			}

			List<HashMap<String, Object>> mapList = new ArrayList<HashMap<String, Object>>();

			int skipCount = (searchBean.getPageNo() - 1) * searchBean.getPageSize();
			if(skipCount < 0)
				skipCount = 0;
			int pageCount = 0;
			int resultCount = 0;
			while (iter.hasNext()) {
				IndependentObject row = iter.next();
				resultCount++;
				if(resultCount <= skipCount)
					continue;
				if(pageCount >= searchBean.getPageSize())
					break;
				pageCount++;
				
				HashMap<String, Object> map = new HashMap<String, Object>();
				try {
					Properties properties = row.getProperties();
					String id = properties.getIdValue("Id").toString();

					checkAndSetStringProperty(properties, "Creator", null, map);
					Date dateCreated = properties.getDateTimeValue("DateCreated");
					//String dateModified = Utils.formatDateForUI(properties.getDateTimeValue("DateLastModified"));
					//String lastModifiedBy = properties.getStringValue("LastModifier");
					checkAndSetStringProperty(properties, "Subject", "Subject", map);
					checkAndSetStringProperty(properties, "EmailSubject", "subject", map);
					//checkAndSetStringProperty(properties, "Name", "name", map);
					
					String symbolicName = null;
					checkAndSetStringProperty(properties, "MimeType", "mimeType", map);
					checkAndSetStringProperty(properties, "DocumentID", "documentID", map);
					checkAndSetStringProperty(properties, "ReferenceNo", "referenceNo", map);
					checkAndSetIntegerProperty(properties, "DepartmentID", "department", map);
					checkAndSetDateProperty(properties, "DocumentDate", "documentDate", map);
					
					Integer isLaunched = properties.getInteger32Value("IsLaunched")!=null?(Integer)properties.getInteger32Value("IsLaunched"):0;
					map.put("IsLaunched",isLaunched.intValue());
					Boolean isReserved = properties.getBooleanValue("IsReserved");
					if (isReserved) {
						Document reservedObject = (Document) properties.getObjectValue("Reservation");
						checkedOutBy = reservedObject.get_Creator();
						//map.put("checkedOutBy", checkedOutBy);
						map.put("reserved", isReserved.toString());
					}
					VersionSeries vs = (VersionSeries) properties.getObjectValue("VersionSeries");
					String vsId = vs.get_Id().toString();
					symbolicName = properties.getStringValue("DocumentTitle");
					map.put("objectType", "Document");

					map.put("versionSeriesId", vsId);
					map.put("id", id);
					checkAndSetStringProperty(properties, "Creator","createdBy", map);
					
					map.put("dateCreated", Utils.formatDateForUI(dateCreated));
					//map.put("dateModified", dateModified);
					//map.put("lastModifiedBy", lastModifiedBy);
					map.put("symbolicName", symbolicName);
					mapList.add(map);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			searchResultBean.setResultMapList(mapList);

			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			int seconds = (int) (totalTime / 1000) % 60 ;
			System.out.println("Total Time Taken for ********  getSearchResults() ::: "+seconds +" seconds");
			return searchResultBean;
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage().contains("ErrorCode: 1,460, Message: 'ORA-01460: unimplemented or unreasonable conversion requested")) {
				throw new Exception("Value entered is invalid,enter a new value and try again");
			}
			throw new Exception(e.getMessage());
		}
	}

	private void checkAndSetStringProperty(Properties properties,
			String propName, String propNameOverride,
			HashMap<String, Object> map) {
		if (properties.isPropertyPresent(propName)) {
			map.put(propNameOverride == null || propNameOverride.isEmpty() ? propName
					: propNameOverride, properties.getStringValue(propName));
		}
	}

	private void checkAndSetDateProperty(Properties properties,
			String propName, String propNameOverride,
			HashMap<String, Object> map) {
		if (properties.isPropertyPresent(propName)) {
			map.put(propNameOverride == null || propNameOverride.isEmpty() ? propName
					: propNameOverride, Utils.formatDateForUIUpdated(properties.getDateTimeValue(propName)));
		}
	}
	
	private void checkAndSetIntegerProperty(Properties properties,
			String propName, String propNameOverride,
			HashMap<String, Object> map) {
		if (properties.isPropertyPresent(propName)) {
			map.put(propNameOverride == null || propNameOverride.isEmpty() ? propName
					: propNameOverride, properties.getInteger32Value(propName).toString());
		}
	}
	
	private String escapeString(String inString)
	{
		if(inString == null)
			return null;
		return inString.replaceAll("'", "''");
	}
	
	public String buildQuery(SearchQueryBean searchQueryBean, int oldDepartmentId) throws Exception {

		StringBuilder criteria = new StringBuilder();
		StringBuilder contentSearchJoinCondition = new StringBuilder();
		StringBuilder contentSearchContainCondition = new StringBuilder();
		List<FilterBean> filterBean = searchQueryBean.getFilter();
		Iterator<FilterBean> iter = filterBean.iterator();
		int criteriaCount = 0;
		String newDocQuery = "";
		while (iter.hasNext()) {
			
			FilterBean _filterBean = iter.next();
			
			String propValue = "";
			if(_filterBean.getFilterDataType().trim().equalsIgnoreCase(TypeID.DATE.toString()) && (URLDecoder.decode(_filterBean.getFilterValue(),"UTF-8")!=null && !URLDecoder.decode(_filterBean.getFilterValue(),"UTF-8").equalsIgnoreCase(""))){
				propValue = Utils.formatDateForQuery(Utils.convertToUTC(URLDecoder.decode(_filterBean.getFilterValue(),"UTF-8"), _filterBean.getFilterCondition()));	
			}else if(_filterBean.getFilterDataType().trim().equalsIgnoreCase(TypeID.LONG.toString()) && (URLDecoder.decode(_filterBean.getFilterValue(),"UTF-8")!=null && !URLDecoder.decode(_filterBean.getFilterValue(),"UTF-8").equalsIgnoreCase(""))){
				if((_filterBean.getFilterCondition() != null) && ((!_filterBean.getFilterCondition().trim().equalsIgnoreCase("in")) && 
						(!_filterBean.getFilterCondition().trim().equalsIgnoreCase("not in"))))
					propValue = Utils.formatLongForQuery(URLDecoder.decode(_filterBean.getFilterValue(),"UTF-8"));
				else
					propValue = URLDecoder.decode(_filterBean.getFilterValue(),"UTF-8");
			}
			else{
				propValue = URLDecoder.decode(_filterBean.getFilterValue(),"UTF-8");
			}
			
			propValue = escapeString(URLDecoder.decode(propValue,"UTF-8"));
			if (criteria.length() > 0 && !propValue.equalsIgnoreCase("")) {
				criteria.append(" AND ");
			}
			
			if(!propValue.equalsIgnoreCase("")){
				criteria.append(appendFitervaule(_filterBean,propValue,searchQueryBean.getSearchType()));
				
				if((criteriaCount >= 1) || !_filterBean.getFilterName().equalsIgnoreCase("DocumentID"))
					newDocQuery = " AND [NewDoc] = True ";
				criteriaCount++;
			}
			
		}
		
		String query ="";

		if(searchQueryBean.getContent() != null && !searchQueryBean.getContent().equalsIgnoreCase("")){
			
			contentSearchJoinCondition.append(" INNER JOIN ContentSearch c	ON d.This = c.QueriedObject ");
			
			contentSearchContainCondition.append(" AND CONTAINS(d.*, '*" +searchQueryBean.getContent()+ "*') ");
			
		}else{
			contentSearchJoinCondition.append("");
			contentSearchContainCondition.append("");
		}
		
		if(searchQueryBean.isCrossDepartment()){
			System.out.println("newDocQuery ::: "+newDocQuery);
			if(criteria!=null && !criteria.toString().equalsIgnoreCase("")){
				
				query = "SELECT TOP 100 d.[This], d.[VersionSeries],d.[MimeType],d.[DocumentID], d.[DocumentDate],d.[DepartmentID],d.[IsReserved],d.[Creator], d.[DateCreated],d.[DocumentTitle], d.[EmailSubject],d.[ReferenceNo], d.[Reservation],d.[Id],d.[IsLaunched] FROM [Correspondence] d "+contentSearchJoinCondition.toString()+" WHERE "
						+ criteria + newDocQuery
						+ " AND [IsCurrentVersion] = true "+contentSearchContainCondition+" ORDER BY [DateCreated] DESC OPTIONS(TIMELIMIT 180)";
			}else{
				if(newDocQuery !=null && !newDocQuery.toString().equalsIgnoreCase("")){
					query = "SELECT TOP 100 d.[This], d.[VersionSeries],d.[MimeType],d.[DocumentID], d.[DocumentDate], d.[DepartmentID],d.[IsReserved],d.[Creator], d.[DateCreated],d.[DocumentTitle], d.[EmailSubject],d.[ReferenceNo], d.[Reservation],d.[Id],d.[IsLaunched] FROM [Correspondence] d "+contentSearchJoinCondition.toString()+" WHERE "
							+ newDocQuery + " AND [IsCurrentVersion] = true "+contentSearchContainCondition+" ORDER BY [DateCreated] DESC OPTIONS(TIMELIMIT 180)";
				}else{
					query = "SELECT TOP 100 d.[This], d.[VersionSeries],d.[MimeType],d.[DocumentID], d.[DocumentDate], d.[DepartmentID],d.[IsReserved],d.[Creator], d.[DateCreated],d.[DocumentTitle], d.[EmailSubject],d.[ReferenceNo], d.[Reservation],d.[Id],d.[IsLaunched] FROM [Correspondence] d "+contentSearchJoinCondition.toString()+" WHERE "
							+ " [IsCurrentVersion] = true "+contentSearchContainCondition+" ORDER BY [DateCreated] DESC OPTIONS(TIMELIMIT 180)";
				}
				
			}
		}else{
			int topCount = searchQueryBean.getPageNo() * searchQueryBean.getPageSize();
			if(topCount <= 0)
				topCount = 100;
			if(criteria!=null && !criteria.toString().equalsIgnoreCase("")){
				
				query = "SELECT TOP " + topCount + " d.[This], d.[VersionSeries],d.[MimeType],d.[DocumentID], d.[DocumentDate],d.[DepartmentID], d.[IsReserved],d.[Creator], d.[DateCreated],d.[DocumentTitle], d.[EmailSubject],d.[ReferenceNo], d.[Reservation],d.[Id],d.[IsLaunched] FROM [Correspondence] d "+contentSearchJoinCondition.toString()+" WHERE "
						+ criteria
						+ "  AND [DepartmentID] = "+oldDepartmentId+" AND [IsCurrentVersion] = true "+contentSearchContainCondition+" ORDER BY [DateCreated] DESC OPTIONS(TIMELIMIT 180)";
			}else{
				query = "SELECT TOP " + topCount + " d.[This], d.[VersionSeries],d.[MimeType],d.[DocumentID], d.[DocumentDate],d.[DepartmentID],d.[IsReserved],d.[Creator], d.[DateCreated],d.[DocumentTitle], d.[EmailSubject],d.[ReferenceNo], d.[Reservation],d.[Id],d.[IsLaunched] FROM [Correspondence] d "+contentSearchJoinCondition.toString()+" WHERE "
						+ "  [DepartmentID] = "+oldDepartmentId+" AND [IsCurrentVersion] = true "+contentSearchContainCondition+" ORDER BY [DateCreated] DESC OPTIONS(TIMELIMIT 180)";
			}
			
		}

		return query;

	}

	private String appendFitervaule(FilterBean _filterBean, String propValue, String searchType) throws Exception {
		String filterValue = "";
		if(propValue.length()>0){
			String condition = "";
			if(searchType.equalsIgnoreCase("SimpleSearch")){
				condition = getConditionforSimpleSearch(_filterBean.getFilterCondition(),propValue);
				if((_filterBean.getFilterName().equals("DocumentTitle") || _filterBean.getFilterName().equals("ReferenceNo")
						|| _filterBean.getFilterName().equals("EmailSubject")) 
						&& searchType.equalsIgnoreCase("SimpleSearch")){
					filterValue= "["+getQueryFilterName(_filterBean.getFilterName()) + "] like '%" + propValue+ "%'";
				} else if(_filterBean.getFilterName().equals("DocumentID") && searchType.equalsIgnoreCase("SimpleSearch")){
					filterValue= "["+getQueryFilterName(_filterBean.getFilterName()) + "] = '" + propValue+ "'";
				} else if(_filterBean.getFilterDataType().trim().equalsIgnoreCase(TypeID.DATE.toString())){
					filterValue=  "["+getQueryFilterName(_filterBean.getFilterName()) + "] >= " + propValue+ " AND ["+getQueryFilterName(_filterBean.getFilterName()) + "] < " + Utils.formatDateForQuery(Utils.addingDate(URLDecoder.decode(_filterBean.getFilterValue(),"UTF-8")))+ "";
				}else{
					filterValue=  "["+getQueryFilterName(_filterBean.getFilterName()) + "] "+condition+" '" + propValue+ "'";
				}
			}else if(searchType.equalsIgnoreCase("AdvanceSearch")){
				if((_filterBean.getFilterDataType().trim().equalsIgnoreCase(TypeID.DATE.toString())) && 
						(_filterBean.getFilterCondition() != null) && (_filterBean.getFilterCondition().trim().equalsIgnoreCase("="))){
					  //filterValue=  "["+getQueryFilterName(_filterBean.getFilterName()) + "] >= " + propValue+ " AND ["+getQueryFilterName(_filterBean.getFilterName()) + "] < " + Utils.formatDateForQuery(Utils.addingDate(URLDecoder.decode(_filterBean.getFilterValue(),"UTF-8")))+ "";
					if(getQueryFilterName(_filterBean.getFilterName()).equalsIgnoreCase("DateReceived")) {
						filterValue=  "["+"DateCreated"+ "] >= " + propValue+ " AND ["+"DateCreated" + "] < " + Utils.formatDateForQuery(Utils.addingDate(URLDecoder.decode(_filterBean.getFilterValue(),"UTF-8")))+ "";
					}
					else {
						filterValue=  "["+getQueryFilterName(_filterBean.getFilterName()) + "] >= " + propValue+ " AND ["+getQueryFilterName(_filterBean.getFilterName()) + "] < " + Utils.formatDateForQuery(Utils.addingDate(URLDecoder.decode(_filterBean.getFilterValue(),"UTF-8")))+ "";
					}
				} else {
					condition = getConditionforAdvanceSearch(_filterBean.getFilterCondition(),propValue,
							(getQueryFilterName(_filterBean.getFilterName())).equalsIgnoreCase("DateReceived")?"DateCreated":getQueryFilterName(_filterBean.getFilterName()));
					if(getQueryFilterName(_filterBean.getFilterName()).equalsIgnoreCase("DocumentTo")) {
						filterValue=  "( " + condition + "["+getQueryFilterName(_filterBean.getFilterName()) + "] ) ";
					}
					else {
						if((_filterBean.getFilterCondition() != null) && ((_filterBean.getFilterCondition().trim().equalsIgnoreCase("in")))
								|| (_filterBean.getFilterCondition() != null) && (_filterBean.getFilterCondition().trim().equalsIgnoreCase("not in")))
							filterValue=  " ( ["+getQueryFilterName(_filterBean.getFilterName()) + "] "+condition + ")";
						else {
							if(getQueryFilterName(_filterBean.getFilterName()).equalsIgnoreCase("DateReceived")) {
								filterValue=  "["+"DateCreated" + "] "+condition;
							}
							else {
								filterValue=  "["+getQueryFilterName(_filterBean.getFilterName()) + "] "+condition;
							}
						}
					}
				}
			}
			
		}
		return filterValue;
	}

	private String getQueryFilterName(String inName)
	{

		String propName = inName.trim();
		if(propName.equalsIgnoreCase("DocumentTitle"))
			return "DocumentTitle";
		else if(propName.equalsIgnoreCase("DocumentID"))
			return "DocumentID";
		else if(propName.equalsIgnoreCase("ReferenceNo"))
			return "ReferenceNo";
		else if(propName.equalsIgnoreCase("EmailSubject"))
			return "EmailSubject";
		else if(propName.equalsIgnoreCase("DocumentType"))
			return "DocumentType";
		else if(propName.equalsIgnoreCase("Confidentiality"))
			return "Confidentiality";
		else if(propName.equalsIgnoreCase("DocumentDate"))
			return "DocumentDate";
		else if(propName.equalsIgnoreCase("LinkedReference"))
			return "LinkedReference";
		else if(propName.equalsIgnoreCase("IsLaunched"))
			return "IsLaunched";
		else if(propName.equalsIgnoreCase("DepartmentID"))
			return "DepartmentID";
		else if(propName.equalsIgnoreCase("GUIDDocID"))
			return "GUIDDocID";
		else if(propName.equalsIgnoreCase("Ref_NO"))
			return "Ref_NO";
		else if(propName.equalsIgnoreCase("DocumentFrom"))
			return "DocumentFrom";
		else if(propName.equalsIgnoreCase("CorrespondenceType"))
			return "CorrespondenceType";
		else if(propName.equalsIgnoreCase("DateReceived"))
			return "DateReceived";
		else if(propName.equalsIgnoreCase("DocumentTo"))
			return "DocumentTo";
		else if(propName.equalsIgnoreCase("AddedByUser"))
			return "AddedByUser";
		else if(propName.equalsIgnoreCase("Creator"))
			return "Creator";
		else if(propName.equalsIgnoreCase("contentSearch"))
			return "contentSearch";
		else if(propName.equalsIgnoreCase("CreateDate"))
			return "CreateDate";
		else if(propName.equalsIgnoreCase("DocumentDate"))
			return "DocumentDate";
		else if(propName.equalsIgnoreCase("ECM_Stamp"))
			return "ECM_Stamp";
		else if(propName.equalsIgnoreCase("DateReceived"))
			return "DateReceived";
		else if(propName.equalsIgnoreCase("DateCreated"))
			return "DateCreated";
		
		return "DocumentTitle";
	
	}
	
	private String getConditionforSimpleSearch(String filterCondition, String propValue) {
		String [] operators = {"is equal to", "like","contains", "starts with","ends with",
				"=", ">", "<", "<=", ">=" };
		if(filterCondition == null)
			filterCondition = "=";
		String condition = filterCondition.trim().toLowerCase();
		String retCondition = " = ";
		if(condition!=null && !condition.equalsIgnoreCase("")){
			for(int i=0; i<operators.length; i++)
				if(condition.equals(operators[i]))
				{
					retCondition = " " + operators[i].toUpperCase() + " ";
					break;
				}
		}
		
		return retCondition;
	}
	
	private String getConditionforAdvanceSearch(String filterCondition, String propValue, String propLabel) {
		String condition = filterCondition.trim();
		if(condition!=null && !condition.equalsIgnoreCase("")){
			if(condition.equalsIgnoreCase("Like")){
				condition = " LIKE '%"+propValue+"%'";
			}else if(condition.equalsIgnoreCase("is equal to")){
				if(propLabel.equalsIgnoreCase("DocumentTo")) {
					return " "+propValue + " IN ";
				}
				condition = " = '"+propValue+"'";
			}else if(condition.equalsIgnoreCase("contains")){
				condition = " LIKE '%"+propValue+"%'";
			}else if(condition.equalsIgnoreCase("starts with")){
				condition = " LIKE '"+propValue+"%'";
			}else if(condition.equalsIgnoreCase("ends with")){
				condition = " LIKE '%"+propValue+"'";
			}else if (condition.equalsIgnoreCase("=")){
				if(propLabel.equalsIgnoreCase("DocumentTo")) {
					return " "+propValue + " IN ";
				}
				condition = " = "+propValue;
			}else if(condition.equalsIgnoreCase(">=")){
				condition = " >= "+propValue;
			}else if(condition.equalsIgnoreCase(">")){
				condition = " > "+propValue;
			}else if(condition.equalsIgnoreCase("<=")){
				condition = " <= "+propValue;
			}else if(condition.equalsIgnoreCase("<")){
				condition = " < "+propValue;
			}else if(condition.equalsIgnoreCase("in")){
				condition = getInCondition(propLabel, propValue, false);
			}else if(condition.equalsIgnoreCase("not in")){
				condition = getInCondition(propLabel, propValue, true);
			} else if((condition.equalsIgnoreCase("period")) || (condition.equalsIgnoreCase("between"))){
				String values[] = propValue.split("~!");
				if(values.length > 0)
					condition = " >= " + values[0];
				if(values.length > 1) {
					if((values[1] != null) && (values[1].trim().length() > 0)) {
						condition += (" AND [" + propLabel + "] < " + values[1]);
					}
				}
			} else
				condition = " = " + propValue;
		} else {
			if(propLabel.equalsIgnoreCase("DocumentTo")) {
				return " "+propValue + " IN ";
			}
			condition = " = " + propValue;
		}
		System.out.println("final Condn :: "+condition);
		return condition;
	}

	private String getInCondition(String propLabel, String propValue, Boolean notIn) {
		
		String inNotIn = " OR ";
		String notInOp = " ";
		String eqOp = " = ";
		if(notIn) {
			inNotIn = " AND ";
			notInOp = " NOT ";
			eqOp = " <> ";
		}
		
		String condition = "";
		String values[] = propValue.split(",");
		if(values.length <= 1) {
			if(propLabel.equalsIgnoreCase("DocumentTo")) {
				return notInOp +propValue + " IN ";
			}
			condition = eqOp + " "+propValue + " ";
		}
		else {
			if(propLabel.equalsIgnoreCase("DocumentTo")) {
				condition += " ";
				int iterCount = 0;
				for(String inValue: values) {
					if(iterCount > 0)
						condition += inNotIn;
					if(iterCount >= (values.length - 1))
						condition += (notInOp + inValue + " IN ");
					else
						condition += (notInOp + inValue + " IN [" + propLabel + "] " );
					iterCount++;
				}
				condition += " ";
			} else {
				condition += " ";
				int iterCount = 0;
				for(String inValue: values) {
					if(iterCount > 0)
						condition += inNotIn;
					if(iterCount <= 0)
						condition += (eqOp + inValue);
					else
						condition += ("[" + propLabel + "] " + eqOp + inValue);
					iterCount++;
				}
				condition += " ";
			}
		}
		return condition;
	}


public SearchResultBean getDailyDocumentItems(ObjectStore os,String supervisor,ArrayList assistanceList, String sortOrder, String sortColumn, int oldDepartmentId ) throws Exception {
		
		SearchResultBean searchResultBean = null;
		String checkedOutBy = null;
		
		try {
		SearchScope searchScope = new SearchScope(os);
		SearchSQL sqlObject = new SearchSQL();
		String coloumnName = getDocumentColoumnName(sortColumn);
		String sorting ="DESC";
		if(sortOrder != null){
		sorting =sortOrder;
		}
		String creatorSQL = "";
		if((supervisor != null) && (supervisor.trim().length() > 0)){
		creatorSQL = " AND ([Creator] = '"+escapeString(supervisor)+"'";
		}
		System.out.println("creatorSQL ::"+creatorSQL);
		if(assistanceList!=null){
		for(int i=0;i<assistanceList.size();i++){
		
		if(assistanceList.get(i)!=null){
		String assistance = null;
		assistance = assistanceList.get(i).toString();
		if((assistance != null) && (assistance.trim().length() > 0)){
		creatorSQL =creatorSQL+ " OR [Creator] = '"+escapeString(assistance)+"' ";
		System.out.println("creatorSQL ::"+creatorSQL);
		}
		}
		}
		}
		System.out.println("creatorSQL ::"+creatorSQL);
		/*
		if((supervisor != null) && (supervisor.trim().length() > 0) &&
		(assistance != null) && (assistance.trim().length() > 0))
		creatorSQL = " AND ([Creator] = '"+escapeString(supervisor)+"' OR [Creator] = '"+escapeString(assistance)+"' ) ";
		else if((supervisor != null) && (supervisor.trim().length() > 0))
		creatorSQL = " AND [Creator] = '"+escapeString(supervisor)+"' ";
		else if((assistance != null) && (assistance.trim().length() > 0))
		creatorSQL = " AND [Creator] = '"+escapeString(assistance)+"' ";*/
		
		String query = "SELECT TOP 100 [This], [VersionSeries],[MimeType], [IsReserved], [Reservation], [AddedByUser], [CorrespondenceType], [CreateDate], [Creator], [DateCreated], [DateLastModified], [DateReceived], [DepartmentID], [DocumentTitle], [EmailSubject],[DocumentID],[ReferenceNo],[DocumentDate],[DocumentType], [Id], [LastModifier], [Name], [Owner] FROM [Correspondence] WHERE "
		+ " [DMail] = '0' AND [IsLaunched] = 0 AND [IsCurrentVersion]=true AND [DepartmentID] = "+oldDepartmentId + creatorSQL + ") ORDER BY "+escapeString(coloumnName)+" "+sorting+" OPTIONS(TIMELIMIT 180)";
		if(creatorSQL.length() <= 0)
		query = "";
		
		logger1.info("Daily Document Query: " + query );
		sqlObject.setQueryString(query);
		
		System.out.println("############" + query);
		
		PropertyFilter pf = new PropertyFilter();
		FilterElement fe = new FilterElement(
		null,
		null,
		null,
		"SymbolicName Id DocumentTitle ClassDescription Name VersionSeries MimeType Creator DateCreated DateLastModified LastModifier IsReserved ContentSize MajorVersionNumber ContentElementsPresent Reservation FoldersFiledIn Versions PathName FolderName ContentElements ElementSequenceNumber RetrievalName ContentType DateCheckedIn DisplayName",
		null);
		pf.addIncludeProperty(fe);
		logger1.info("Start time ::" + System.currentTimeMillis());
		IndependentObjectSet rowSet = searchScope.fetchObjects(sqlObject,10, pf, new Boolean(false));
		logger1.info("End time ::" + System.currentTimeMillis());
		Iterator<IndependentObject> iter = rowSet.iterator();
		
		if (iter != null) {
		searchResultBean = new SearchResultBean();
		}
		
		List<HashMap<String, Object>> mapList = new ArrayList<HashMap<String, Object>>();
		
		int resultCount = 0;
		while (iter.hasNext()) {
		logger1.info("***** result count :" + resultCount);
		resultCount++;
		IndependentObject row = iter.next();
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
		Properties properties = row.getProperties();
		String id = properties.getIdValue("Id").toString();
		
		checkAndSetStringProperty(properties, "Creator", null, map);
		Date dateCreated = properties.getDateTimeValue("DateCreated");
		String dateModified = Utils.formatDateForUI(properties.getDateTimeValue("DateLastModified"));
		String lastModifiedBy = properties.getStringValue("LastModifier");
		checkAndSetStringProperty(properties, "Subject", "Subject", map);
		checkAndSetStringProperty(properties, "EmailSubject", "subject", map);
		checkAndSetStringProperty(properties, "Name", "name", map);
		String symbolicName = null;
		checkAndSetStringProperty(properties, "MimeType", "mimeType", map);
		checkAndSetStringProperty(properties, "DocumentID", "documentID", map);
		
		checkAndSetStringProperty(properties, "ReferenceNo", "referenceNo", map);
		//checkAndSetStringProperty(properties, "DocumentDate", "documentDate", map);
		
		Boolean isReserved = properties.getBooleanValue("IsReserved");
		if (isReserved) {
		Document reservedObject = (Document) properties.getObjectValue("Reservation");
		checkedOutBy = reservedObject.get_Creator();
		map.put("checkedOutBy", checkedOutBy);
		map.put("reserved", isReserved.toString());
		}
		VersionSeries vs = (VersionSeries) properties.getObjectValue("VersionSeries");
		String vsId = vs.get_Id().toString();
		symbolicName = properties.getStringValue("DocumentTitle");
		map.put("objectType", "Document");
		
		map.put("versionSeriesId", vsId);
		map.put("CorrespondenceType", properties.getInteger32Value("CorrespondenceType"));
		map.put("id", id);
		map.put("document_id", id);
		checkAndSetStringProperty(properties, "Creator","createdBy", map);
		
		map.put("dateCreated", Utils.formatDateForUI(dateCreated));
		map.put("dateModified", dateModified);
		map.put("lastModifiedBy", lastModifiedBy);
		map.put("symbolicName", symbolicName);
		mapList.add(map);
		} catch (Exception e) {
		e.printStackTrace();
		}
		}
		searchResultBean.setResultMapList(mapList);
		return searchResultBean;
		} catch (Exception e) {
		logger1.error("Daily Document Error: " + e.getMessage());
		e.printStackTrace();
		if (e.getMessage().contains("ErrorCode: 1,460, Message: 'ORA-01460: unimplemented or unreasonable conversion requested")) {
		throw new Exception("Value entered is invalid,enter a new value and try again");
		}
		throw new Exception(e.getMessage());
		}
}

	private String getDocumentColoumnName(String sortColumn) {
		if(sortColumn == null || sortColumn.equalsIgnoreCase(""))
			return "[DateCreated]";
		
		String colName = "";
		if(sortColumn.equalsIgnoreCase("Document Title")){
			colName = "[DocumentTitle]";
		}else if(sortColumn.equalsIgnoreCase("Id")){
			colName = "[DocumentID]";
		}else if(sortColumn.equalsIgnoreCase("Reference Number")){
			colName = "[ReferenceNo]";
		}else if(sortColumn.equalsIgnoreCase("Created By")){
			colName = "[Creator]";
		}else if(sortColumn.equalsIgnoreCase("Correspondence Type")){
			colName = "[CorrespondenceType]";
		}else if(sortColumn.equalsIgnoreCase("Date Created")){
			colName = "[DateCreated]";
		}
		return colName;
	}

	
	public List<HashMap<String, Object>> documentsScanned(ObjectStore os, int department,String division, String recipient, String from, String to) throws Exception {
		try {
			SearchScope searchScope = new SearchScope(os);
			SearchSQL sqlObject = new SearchSQL();
			String appendQuery = "";
			if(recipient != null) {
				appendQuery = "[Creator]";
				String recipients [] = recipient.split(",");

				for (int i = 0; i < recipients.length; i++) {
					if(recipients.length == 1) {
						appendQuery = appendQuery+" = '"+escapeString(recipients[i])+"'";
					}
					else {						
						if(i == recipients.length-1) {
							appendQuery = appendQuery+" = '"+escapeString(recipients[i])+"'";
						}
						else {
							appendQuery = appendQuery+" ='"+escapeString(recipients[i])+"' OR [Creator]";
						}
					}
				}
			}
			
			
			String fromDate = Utils.formatDateForQuery(Utils.convertToUTC(URLDecoder.decode(from,"UTF-8"),">="));
			String todate = Utils.formatDateForQuery(Utils.convertToUTC(URLDecoder.decode(to,"UTF-8"), "<="));
			
			/*String query = "SELECT TOP 100 [This], [IsLaunched], [DocumentFrom], [Confidentiality], [ContentSize], [VersionSeries],[MimeType], [IsReserved], [Reservation], [AddedByUser], [CorrespondenceType], [CreateDate], [Creator], [DateCreated], [DateLastModified], [DateReceived], [DepartmentID], [DocumentTitle], [EmailSubject],[DocumentID],[ReferenceNo],[DocumentDate],[DocumentType], [Id], [LastModifier], [Name], [Owner] FROM [Correspondence] WHERE "
					+ " [DepartmentID] = "+department+" AND ("+appendQuery+") AND [DateCreated] >= "+fromDate+" AND [DateCreated] <= "+todate+" AND [IsCurrentVersion] = true OPTIONS(TIMELIMIT 180)";
*/
			
			/*String query = "SELECT TOP 100 [This],[IsLaunched], [DocumentFrom], [Confidentiality], [ContentSize], [VersionSeries],[MimeType], [IsReserved], [Reservation], [CorrespondenceType], [Creator], [DateCreated], [DocumentTitle], [EmailSubject],[DocumentID],[ReferenceNo], [Id], [LastModifier], [Name], [Owner] FROM [Correspondence] WHERE "
					+ " [DepartmentID] = "+department+" AND ("+appendQuery+") AND [DateCreated] >= "+fromDate+" AND [DateCreated] <= "+todate+" AND [IsCurrentVersion] = true OPTIONS(TIMELIMIT 180)";
*/
			/*Added By Rameshwar -- added document type , DateReceived, DocumentDate*/
			String query = "SELECT TOP 100 [This],[IsLaunched], [DocumentFrom], [Confidentiality], [ContentSize], [VersionSeries],[MimeType], [IsReserved], [Reservation], [CorrespondenceType], [Creator], [DateCreated], [DateReceived],[DocumentTitle], [EmailSubject],[DocumentID],[DocumentDate],[DocumentType],[ReferenceNo], [Id], [LastModifier], [Name], [Owner] FROM [Correspondence] WHERE "
					+ " [DepartmentID] = "+department+" AND ("+appendQuery+") AND [DateCreated] >= "+fromDate+" AND [DateCreated] <= "+todate+" AND [IsCurrentVersion] = true OPTIONS(TIMELIMIT 180)";

			
			
			sqlObject.setQueryString(query);

			System.out.println("############" + query);

			PropertyFilter pf = new PropertyFilter();
			FilterElement fe = new FilterElement(
					null,
					null,
					null,
					"SymbolicName Id DocumentTitle ClassDescription Name VersionSeries MimeType Creator DateCreated DateLastModified LastModifier IsReserved ContentSize MajorVersionNumber ContentElementsPresent Reservation FoldersFiledIn Versions PathName FolderName ContentElements ElementSequenceNumber RetrievalName ContentType DateCheckedIn DisplayName",
					null);
			pf.addIncludeProperty(fe);
			logger1.info("Start time ::" + System.currentTimeMillis());
			IndependentObjectSet rowSet = searchScope.fetchObjects(sqlObject,10, pf, new Boolean(false));
			logger1.info("End time ::" + System.currentTimeMillis());
			Iterator<IndependentObject> iter = rowSet.iterator();


			List<HashMap<String, Object>> mapList = new ArrayList<HashMap<String, Object>>();

			int resultCount = 0;
			while (iter.hasNext()) {
				resultCount++;
				IndependentObject row = iter.next();
				HashMap<String, Object> map = new HashMap<String, Object>();
				try {
					Properties properties = row.getProperties();
					String id = properties.getIdValue("Id").toString();
					checkAndSetStringProperty(properties, "Creator", null, map);
					Date dateCreated = properties.getDateTimeValue("DateCreated");
					map.put("dateCreated", Utils.formatDateForUI(dateCreated));
					map.put("ReferenceNo", properties.getStringValue("ReferenceNo")!=null ?properties.getStringValue("ReferenceNo"):"");
					map.put("DocumentTitle", properties.getStringValue("DocumentTitle")!=null ? properties.getStringValue("DocumentTitle"):"");
					map.put("DocumentID", properties.getStringValue("DocumentID")!=null ?  properties.getStringValue("DocumentID") : "");
					if(properties.getFloat64Value("ContentSize")!=null){
						Double contentSize = properties.getFloat64Value("ContentSize")/(1024*1000);
						double roundOff = (double) Math.round(contentSize * 100) / 100;
						map.put("ContentSize", roundOff+" MB");
					}else{
						map.put("ContentSize", "0 MB");
					}
					
					int docType = properties.getInteger32Value("DocumentType")!=null ? properties.getInteger32Value("DocumentType"):0;
					map.put("DocumentType", docType);
					if(properties.getInteger32Value("CorrespondenceType")!=null && properties.getInteger32Value("CorrespondenceType") == 1) {
						map.put("CorrespondenceType", "Internal Incoming");
					}
					else if(properties.getInteger32Value("CorrespondenceType")!=null && properties.getInteger32Value("CorrespondenceType") == 2) {
						map.put("CorrespondenceType", "Internal Outgoing");
					}
					else if(properties.getInteger32Value("CorrespondenceType")!=null && properties.getInteger32Value("CorrespondenceType") == 3) {
						map.put("CorrespondenceType", "External Incoming");
					}
					else if(properties.getInteger32Value("CorrespondenceType")!=null && properties.getInteger32Value("CorrespondenceType") == 4) {
						map.put("CorrespondenceType", "External Outgoing");
					}else{
						map.put("CorrespondenceType", "");
					}
					Date documentDate = properties.getDateTimeValue("DocumentDate");
					Date dateReceived = properties.getDateTimeValue("DateReceived");
					map.put("DocumentDate", Utils.formatDateForUI(documentDate));
					map.put("DateReceived", Utils.formatDateForUI(dateReceived));
					if(properties.getInteger32Value("IsLaunched")!=null && properties.getInteger32Value("IsLaunched") == 1) {
						map.put("IsLaunched", "Yes");
					}
					else if(properties.getInteger32Value("IsLaunched")!=null && properties.getInteger32Value("IsLaunched") == 0) {
						map.put("IsLaunched", "No");
					}else{
						map.put("IsLaunched", "");
					}
					map.put("DocumentFrom", properties.getInteger32Value("DocumentFrom")!=null ? properties.getInteger32Value("DocumentFrom"):0);
					if(properties.getInteger32Value("Confidentiality") !=null && properties.getInteger32Value("Confidentiality") == 1) {
						map.put("Confidentiality", "Non Confidential");
					}
					else if(properties.getInteger32Value("Confidentiality") !=null && properties.getInteger32Value("Confidentiality") == 2) {
						map.put("Confidentiality", "Confidential");
					}else{
						map.put("Confidentiality", "");
					}
					
					mapList.add(map);
					System.out.println("END OF documentsScanned()");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("END 123 OF documentsScanned()");
			return mapList;
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage().contains("ErrorCode: 1,460, Message: 'ORA-01460: unimplemented or unreasonable conversion requested")) {
				throw new Exception("Value entered is invalid,enter a new value and try again");
			}
			throw new Exception(e.getMessage());
		}
	}
}