package com.knpc.dims.filenet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.log4j.Logger;

import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.admin.DocumentClassDefinition;
import com.filenet.api.admin.PropertyDefinition;
import com.filenet.api.admin.PropertyTemplate;
import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.collection.ClassDefinitionSet;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DateTimeList;
import com.filenet.api.collection.DocumentSet;
import com.filenet.api.collection.Float64List;
import com.filenet.api.collection.FolderSet;
import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.collection.Integer32List;
import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.collection.PropertyDefinitionList;
import com.filenet.api.collection.PropertyTemplateSet;
import com.filenet.api.collection.StringList;
import com.filenet.api.collection.VersionableSet;
import com.filenet.api.constants.AccessLevel;
import com.filenet.api.constants.AccessType;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;
import com.filenet.api.constants.TypeID;
import com.filenet.api.core.BatchItemHandle;
import com.filenet.api.core.Connection;
import com.filenet.api.core.ContentElement;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.IndependentlyPersistableObject;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.core.UpdatingBatch;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.core.Versionable;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.Properties;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.security.AccessPermission;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;
import com.knpc.dims.filenet.beans.DocumentBean;
import com.knpc.dims.filenet.beans.DocumentClassBean;
import com.knpc.dims.filenet.beans.DocumentDownloadBean;
import com.knpc.dims.filenet.beans.DocumentTransferBean;
import com.knpc.dims.filenet.beans.EmailBean;
import com.knpc.dims.filenet.beans.FileObject;
import com.knpc.dims.filenet.beans.FolderBean;
import com.knpc.dims.filenet.beans.PropertyBean;
import com.knpc.dims.filenet.beans.SearchQueryBean;
import com.knpc.dims.filenet.beans.SearchResultBean;
import com.knpc.dims.filenet.beans.VersionBean;
import com.knpc.dims.filenet.util.FilenetSystemConfiguration;
import com.knpc.dims.filenet.util.PropertyDataType;
import com.knpc.dims.filenet.util.Utils;

public class FileNetAdaptor {
	private static final Logger logger1 = Logger.getLogger(FileNetAdaptor.class);
	
	private String objStoreName;
	private Connection conn;
	private com.filenet.api.core.ObjectStore os;
	private String ceURI,stanzName;
	private UserContext uc;
    private String userId;
    private String pwd;
	
	public FileNetAdaptor(String userName,String password){
		
	}
	

	public FileNetAdaptor() {
		
	}
	
	public UserContext getUc() {
		return uc;
	}


	public void setUc(UserContext uc) {
		this.uc = uc;
	}

	private String escapeString(String inString)
	{
		if(inString == null)
			return null;
		return inString.replaceAll("'", "''");
	}
	
	public Connection getCEConnection() {
		if(Utils.isIntegratedLogin()) {
			String uri = FilenetSystemConfiguration.getInstance().CE_URI;
			Connection conn = Factory.Connection.getConnection(uri);
			//String stanza = FilenetSystemConfiguration.getInstance().STANZA;
			//Subject subject = null; 
			return conn;
		} else {
			String uri = "http://ecmdemo1:9080/wsi/FNCEWS40MTOM/";
			Connection conn = Factory.Connection.getConnection(uri);
			String stanza = "FileNetP8WSI";
			Subject subject = UserContext.createSubject(conn, "p8admin", "filenet",stanza);
			UserContext uc = UserContext.get();
			uc.pushSubject(subject); // End Local 
			return conn;
		}
	}

	public static Domain getDomain(Connection conn) {
		String domainName = null; 
		Domain domain = Factory.Domain.fetchInstance(conn, domainName, null);
		return domain;
	}

	public static ObjectStore getObjectStore(Domain domain, String objectStoreName) {
		ObjectStore store = Factory.ObjectStore.fetchInstance(domain,objectStoreName, null);
		return store;
	}
	
	public List<FolderBean> getfolderTree(String folderPath, String osName) throws Exception {
		logger1.info("Started Method : getFolderTree  Method parameter folderPath:"+folderPath+" osName:"+osName);
		try {
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, osName);

			List<FolderBean>  folderList = null;
			folderPath = folderPath.replaceAll("~~~", "&");
			Folder parentFolder = Factory.Folder.fetchInstance(os, folderPath, null);
			if(parentFolder != null) {
				FolderSet fs = parentFolder.get_SubFolders();
				Iterator<Folder> iter = fs.iterator();
				if (iter != null) {
					folderList = new ArrayList<FolderBean>();
				}
				while(iter.hasNext()){
					Folder row = (Folder) iter.next();
					if(row.getProperties().getBooleanValue("IsHiddenContainer"))
						continue;
					FolderBean folderDetailBean = new FolderBean();
					String path = row.get_PathName();
					String Id = row.get_Id().toString();
					folderDetailBean.setId(Id);
					folderDetailBean.setPath(path);
					
					folderList.add(folderDetailBean);
				}
			}
			logger1.info("Exit Method : getFolderTree");
			return folderList;
		} catch (Exception e) {
			e.printStackTrace();
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
	}

	public String addFolder(String parentPath, String folderName, String osName) throws Exception {
		logger1.info("Started Method : addFolder Method parameter folderName : "+folderName+" osName : "+osName);
		String folderId = null;
		try {
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, osName);
			System.out.println("parentPath ::"+parentPath);
			String parentFolderPath = null;
			Folder parentFolder = null;
			Folder folderCheck = null;
			
			folderName = folderName.replaceAll("~~~", "&");
			parentPath = parentPath.replaceAll("~~~", "&");
			
			if(parentPath != null){
				parentFolder = Factory.Folder.fetchInstance(os, new Id(parentPath), null);
			}else{
				parentFolder = Factory.Folder.fetchInstance(os, "/", null);
			}
			parentFolderPath = parentFolder.get_PathName();
			System.out.println("parentFolderPath:"+parentFolderPath);
			if(parentFolderPath.equalsIgnoreCase("/")){
				parentFolderPath = "";
			}
			
			String folderCheckPath = parentFolderPath.concat("/").concat(folderName);
			try {
				folderCheck = Factory.Folder.fetchInstance(os,folderCheckPath, null);
				//DepartmentID
			} catch (Exception e) {
				//e.printStackTrace();
			}
			if (folderCheck == null) {

				try {
					Folder folder = Factory.Folder.createInstance(os,"DIMS_Folders");
					
					if(parentFolder.getProperties()!=null&&folder.getProperties()!=null){					
						folder.getProperties().putValue("DepartmentID", parentFolder.getProperties().getInteger32Value("DepartmentID"));
					}
					folder.set_Parent(parentFolder);
					folder.set_FolderName(folderName);
					folder.save(RefreshMode.REFRESH);
					folderId = folder.get_Id().toString();
					logger1.info("Exit Method : addFolder");
					return folderId;
				} catch (Exception e) {
					logger1.error(e.getMessage(),e);
					throw new Exception(e.getMessage());
				}
			} else {
				folderId = "Folder '"
					+ folderName
					+ "' already exists. Please enter a different name.";
				logger1.info("Exit Method : addFolder");
				throw new Exception(folderId);
			}
		} catch (Exception e) {
			folderId = e.getMessage();
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
	}

	public String deleteFolder(String folderPath, String osName) throws Exception {
		logger1.info("Started Method : deleteFolder Method parameter folderPath : "+folderPath+" osName : "+osName);
		String folderName = null;
		try {
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, osName);
			
			String parentFolderPath="";
			String folderCheckPath = parentFolderPath.concat("/").concat(folderPath);
			Folder folder = Factory.Folder.fetchInstance(os, folderCheckPath,null);
			folderName = folder.get_FolderName();
			folder.delete();
			folder.save(RefreshMode.REFRESH);
		} catch (Exception e) {
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		logger1.info("Exit Method : deleteFolder");
		return folderName+" folder has been deleted..";
	}

	public List<FolderBean> getSubFolders(String folderId, String osName) throws Exception {
		logger1.info("Started Method : getSubFolders  Method parameter folderID:"+folderId+" osName:"+osName);
		try {
			if((folderId == null) || (folderId.equalsIgnoreCase("undefined")))
				return null;
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, osName);
			Id folderIDObj = new Id(folderId);
			List<FolderBean>  folderList = null;
			Folder parentFolder = Factory.Folder.fetchInstance(os, folderIDObj, null);
			if(parentFolder != null) {
				FolderSet fs = parentFolder.get_SubFolders();
				Iterator<Folder> iter = fs.iterator();
				if (iter != null) {
					folderList = new ArrayList<FolderBean>();
				}
				while(iter.hasNext()){
					Folder row = (Folder) iter.next();
					if(row.getProperties().getBooleanValue("IsHiddenContainer"))
						continue;
					FolderBean folderDetailBean = new FolderBean();
					String path = row.get_PathName();
					String Id = row.get_Id().toString();
					folderDetailBean.setId(Id);
					folderDetailBean.setPath(path);
					
					folderList.add(folderDetailBean);
				}
			}
			
			logger1.info("Exit Method : getSubFolders");
			return folderList;
		} catch (Exception e) {
			e.printStackTrace();
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
	}

	public List<FolderBean> getSubFoldersOld(String folderId, String osName) throws Exception {
		logger1.info("Started Method : getSubFolders Method parameter folderId : "+folderId+" osName : "+osName);
		List<FolderBean> folderList = null;
		try 
		{
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, osName);
			
			SearchScope scope = new SearchScope(os);
			SearchSQL sqlQuery = new SearchSQL("SELECT FolderName, Parent, PathName, Id, DateCreated, Creator, DateLastModified, LastModifier, Permissions FROM "
					+ "Folder" + " WHERE Parent = OBJECT('" + escapeString(folderId) + "') order by DateCreated DESC");
			logger1.info("SQL QUERY : "+sqlQuery);
			IndependentObjectSet rows = scope.fetchObjects(sqlQuery, null, null, null);

			if (rows != null) 
			{
				Iterator<IndependentObject> iter = rows.iterator();
				if (iter != null) 
				{
					folderList = new ArrayList<FolderBean>();
				}
				while (iter.hasNext()) 
				{
					Folder row = (Folder)iter.next();
					Properties properties = row.getProperties();
					FolderBean folderBean = new FolderBean();
					String id = properties.getIdValue("Id").toString();
					String symbolicName = properties.getStringValue("FolderName").toString();
					String fldrPath = properties.getStringValue("PathName");
					String creator = properties.getStringValue("Creator");
					String dateCreated = com.knpc.dims.filenet.util.Utils.formatDate(properties.getDateTimeValue("DateCreated"));
					String lastModifier = properties.getStringValue("LastModifier");
					String dateModified = com.knpc.dims.filenet.util.Utils.formatDate(properties.getDateTimeValue("DateLastModified"));
					
					folderBean.setObjectStoreName(os.get_Name());
					folderBean.setIsFilingAllowed(Boolean.toString(true));
					folderBean.setPath(fldrPath);
					folderBean.setFolderPath(fldrPath);
					folderBean.setParentPath(folderId);
					folderBean.setSymbolicName(symbolicName);
					folderBean.setDescriptiveText(symbolicName);
					folderBean.setDateCreated(dateCreated);
					folderBean.setCreatedBy(creator);
					folderBean.setClassName("Folder");
					folderBean.setClassDescription("DIMS Folder");
					folderBean.setDateModified(dateModified);
					folderBean.setLastModifiedBy(lastModifier);
					folderBean.setObjectType("Folder");
					folderBean.setId(id);
					folderList.add(folderBean);
				}
			}
		} 
		catch (Exception e) 
		{
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		logger1.info("Exit Method : getSubFolders");
		return folderList;
	}

	public List<DocumentBean> getDocumentsInFolder(String objectStore,String folderId, int pageNo, int pageSize) throws Exception {
		logger1.info("Started Method : getDocumentsInFolder Method parameter objectStore : "+objectStore+" folderId : "+folderId);
		/*if((folderId == null) || (folderId.equalsIgnoreCase("undefined")))
			return null;
		*/
		System.out.println("pageNo :"+pageNo+":pageSize:"+pageSize);
		List<DocumentBean> documentsList = null;
		try 
		{
			int skipCount = (pageNo - 1) * pageSize;
			System.out.println("skipCount ::"+skipCount);
			if(skipCount < 0)
				skipCount = 0;
			int pageCount = 0;
			int resultCount = 0; 
			String maxDocs = "2000";
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, objectStore);

			Folder curFolder = Factory.Folder.fetchInstance(os, new Id(folderId), null);
			DocumentSet ds = curFolder.get_ContainedDocuments();
			Iterator <Document> iter = ds.iterator();
			
			if (iter != null){
				documentsList = new ArrayList<DocumentBean>();
			}
			while (iter.hasNext()){
				Document document = iter.next();
				resultCount++;
				System.out.println("resultCount ::"+resultCount+":skipCount:"+skipCount+":pageCount:"+pageCount+":pageSize:"+pageSize);
				if(resultCount <= skipCount)
					continue;
				if(pageCount >= pageSize)
					break;
				pageCount++;
				
				DocumentBean documentBean = getObject(document);
				documentBean.setParentPath(folderId);
				documentsList.add(documentBean);
			}
		} catch (Exception e) {
			if(!e.getMessage().contains("Requested item not found"))
			{
				logger1.error(e.getMessage(), e);
				throw new Exception(e.getMessage());
			}else{
				logger1.error(e.getMessage(), e);
			}
		}
		logger1.info("Exit Method : getDocumentsInFolder");
		return documentsList;
	}

	private String getSubject(Properties properties) {
		String subject = "";
		try {
			try {
				subject = properties.getStringValue("EmailSubject");
			} catch (Exception e) {
				subject = properties.getStringValue("DocumentTitle");
			}
		} catch(Exception ex) {}
		return subject;
	}
	
	private DocumentBean getObject(Document row) throws Exception {
		logger1.info("Started Method : getObject");
		DocumentBean documentBean = null;
		boolean hasMultipleAttachments= false;
		try 
		{
			Properties properties = row.getProperties();
			documentBean = new DocumentBean();
			ClassDescription cd = ((ClassDescription)properties.getObjectValue("ClassDescription"));
			String classSymbolicName = cd.get_SymbolicName();
			String classDisplayName = cd.get_DisplayName();
			int majorVersion = properties.getInteger32Value("MajorVersionNumber");
			Double contentSize = properties.getFloat64Value("ContentSize");
			double size = 0;
			if (contentSize != null){
				size = contentSize.doubleValue() / 1024;
				size = Math.round(size);
			}
			if (contentSize != null){
				documentBean.setSize(size);
			}
			
			StringList contentList = properties.getStringListValue("ContentElementsPresent");
			
			if(contentList!=null && contentList.size()>1){
				hasMultipleAttachments = true;
			}
			
			String mimeType = properties.getStringValue("MimeType");
			String emailSubject = getSubject(properties); 
			boolean isReserved = properties.getBooleanValue("IsReserved");
			
			if (isReserved){
				Document reservedObject = (Document)properties.getObjectValue("Reservation");
				String checkedOutBy = reservedObject.get_Creator();
				documentBean.setCheckedOutBy(checkedOutBy);
				documentBean.setReserved(isReserved);
			}
			
			VersionSeries vs = (VersionSeries)properties.getObjectValue("VersionSeries");
			String vsId = vs.get_Id().toString();
			String documentId = properties.getIdValue("Id").toString();
			String creator = properties.getStringValue("Creator");
			//String dateCreated =com.knpc.dims.filenet.util.Utils.formatDate( properties.getDateTimeValue("DateCreated"));
			String dateCreated =Utils.formatDateForUI( properties.getDateTimeValue("DateCreated"));
			String dateModified =com.knpc.dims.filenet.util.Utils.formatDate( properties.getDateTimeValue("DateLastModified"));
			String lastModifiedBy = properties.getStringValue("LastModifier");
			if(properties.isPropertyPresent("DepartmentID")){
				Integer deptId = properties.getInteger32Value("DepartmentID");
				if(deptId != null)
					documentBean.setDepartment(deptId.toString());
			}
			if(properties.isPropertyPresent("DocumentDate")){
				String documentDate = Utils.formatDateForUIUpdated(properties.getDateTimeValue("DocumentDate"));
				documentBean.setDocumentDate(documentDate);
			}
			String documentName = properties.getStringValue("DocumentTitle");
			if(properties.isPropertyPresent("ReferenceNo")){
				String referenceNo = properties.getStringValue("ReferenceNo");
				documentBean.setReferenceNo(referenceNo);
			}
			
			if(properties.isPropertyPresent("DocumentID")){
				String documentID = properties.getStringValue("DocumentID");
				documentBean.setDocumentID(documentID);
			}
			documentBean.setClassName(classSymbolicName);
			documentBean.setMajorVersion(majorVersion);	
			documentBean.setHasMultipleAttachments(hasMultipleAttachments);
			documentBean.setMimeType(mimeType);
			documentBean.setReserved(isReserved);
			documentBean.setVersionSeriesId(vsId);
			documentBean.setId(documentId);
			documentBean.setCreatedBy(creator);
			documentBean.setDateCreated(dateCreated);
			documentBean.setDateModified(dateModified);
			documentBean.setLastModifiedBy(lastModifiedBy);
			documentBean.setSymbolicName(documentName);
			documentBean.setClassDescription(classDisplayName);
			documentBean.setObjectType("Document");
			documentBean.setSubject(emailSubject);
			
			documentBean.setObjectStoreName(FilenetSystemConfiguration.getInstance().OS_NAME);
		} catch (Exception e){
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		logger1.info("Exit Method : getObject");
		return documentBean;
	}

	public boolean checkoutDocument(String docId, String objectStore) throws Exception {
		logger1.info("Started Method : checkoutDocument Method parameter docId : "+docId +" objectStore : "+objectStore);
		boolean isCheckedOut = false;
		try 
		{
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, objectStore);
			Document document = Factory.Document.fetchInstance(os, new Id(docId), null);
			VersionSeries vs = document.get_VersionSeries();
			Document doc = (Document) vs.get_CurrentVersion();
			//Check if the document is not already checked out
			if (!(doc.get_IsReserved().booleanValue())){
				doc.checkout(ReservationType.EXCLUSIVE, null, null, null);
				doc.save(RefreshMode.REFRESH);
				isCheckedOut = true;
			}
		} catch (Exception e){
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		logger1.info("Exit Method : checkoutDocument");
		return isCheckedOut;
	}

	public boolean cancelCheckOut(String docId, String objectStore) throws Exception {
		logger1.info("Started Method : cancelCheckOut Method parameter docId : "+docId +"objectStore : "+objectStore);
		boolean isCheckedin = false;

		try 
		{
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, objectStore);
			Document document = Factory.Document.fetchInstance(os, new Id(docId), null);
			VersionSeries vs = document.get_VersionSeries();
			
			Document doc = (Document) vs.get_CurrentVersion();

			// Check if the document is currently checked out
			if (doc.get_IsReserved().booleanValue()){
				Document versionableObj = (Document) doc.cancelCheckout();
				versionableObj.save(RefreshMode.REFRESH);
				isCheckedin = true;
			}
		} catch (Exception e) {
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		logger1.info("Exit Method : cancelCheckOut");
		return isCheckedin;
	}

	public List<DocumentClassBean> getDocumentClasses(String objectStore) throws Exception {
		logger1.info("Started Method : getDocumentClasses Method parameter objectStore : "+objectStore);
		List<DocumentClassBean> docClassList = null;
		try 
		{
			DocumentClassBean dcb = getBaseDocumentClass();
			
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, objectStore);
			
			SearchScope searchScope = new SearchScope(os);
			SearchSQL sqlObject = new SearchSQL();
			String query = "SELECT ID, DisplayName,SymbolicName, Permissions,ImmediateSubclassDefinitions FROM DocumentClassDefinition WHERE " +
							" SuperclassDefinition = Object('"+dcb.getId()+"') AND " +
							"IsHidden = FALSE AND IsSystemOwned = FALSE and AllowsInstances=TRUE";
			
			sqlObject.setQueryString(query);
			IndependentObjectSet rowSet = searchScope.fetchObjects(sqlObject, null,null, new Boolean(true));
			Iterator<RepositoryRow> iter = rowSet.iterator();
			if (iter != null){
				docClassList = new ArrayList<DocumentClassBean>();
			}
			while (iter.hasNext()){
				DocumentClassDefinition row = (DocumentClassDefinition) iter.next();
				DocumentClassBean docClassBean = new DocumentClassBean();
				String id = row.getProperties().getIdValue("ID").toString();
				String symbolicName = row.getProperties().getStringValue("SymbolicName").toString();
				String descriptiveText = row.getProperties().getStringValue("DisplayName").toString();
				docClassBean.setSymbolicName(symbolicName);
				docClassBean.setDescriptiveText(descriptiveText);
				docClassBean.setId(id);
				docClassList.add(docClassBean);
				
				ClassDefinitionSet subClassSet = row.get_ImmediateSubclassDefinitions();
				if (!(subClassSet.isEmpty())){
					List<DocumentClassBean> subDocClassList = getSubClasses(subClassSet);
					docClassList.addAll(subDocClassList);
				}
			}

		} catch (Exception e) {
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		logger1.info("Exit Method : getDocumentClasses");
		return docClassList;
	}
	private List<DocumentClassBean> getSubClasses(ClassDefinitionSet subClassSet) {
		logger1.info("Started Method : getSubClasses");
		List<DocumentClassBean> docClassList = new ArrayList<DocumentClassBean>();
		Iterator<ClassDefinition> cdIter =  subClassSet.iterator();
		while(cdIter.hasNext())
		{
			ClassDefinition cd = cdIter.next();
			DocumentClassBean docClassBean = new DocumentClassBean();
			String id = cd.get_Id().toString();
			String symbolicName = cd.get_SymbolicName();
			String descriptiveText = cd.get_DisplayName();
			docClassBean.setSymbolicName(symbolicName);
			docClassBean.setDescriptiveText(descriptiveText);
			docClassBean.setId(id);
			docClassList.add(docClassBean);
			
			ClassDefinitionSet cdSet = cd.get_ImmediateSubclassDefinitions();
			if (!(cdSet.isEmpty())){
				List<DocumentClassBean> subDocClasses = getSubClasses(cdSet);
				docClassList.addAll(subDocClasses);
			}
		}
		logger1.info("Exit Method : getSubClasses");
		return docClassList;
	}
	public DocumentClassBean getBaseDocumentClass() throws Exception {
		logger1.info("Started Method : getBaseDocumentClass");
		DocumentClassBean dcb = null;
		try
		{
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, FilenetSystemConfiguration.getInstance().OS_NAME);
			dcb = new DocumentClassBean();
			DocumentClassDefinition dcd = Factory.DocumentClassDefinition.fetchInstance(os, "Document", null);
			dcb.setClassDescription(dcd.get_SymbolicName());
			dcb.setSymbolicName(dcd.get_SymbolicName());
			dcb.setDescriptiveText(dcd.get_DisplayName());
			dcb.setId(dcd.get_Id().toString());
		}catch (Exception e){
			 logger1.error(e.getMessage(), e);
             throw new Exception(e.getMessage());
		}
		logger1.info("Exit Method : getBaseDocumentClass");
		return dcb;		
	}

	public List<PropertyBean> getPropertyDefinitions(String docClassName,String objectStoreName) throws Exception {
		logger1.info("Started Method : getPropertyDefinitions Method parameter docClassName : "+docClassName+" objectStoreName : "+objectStoreName);
		List<PropertyBean> propertyList = null;

		try 
		{			
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, objectStoreName);

			ClassDefinition classDef = Factory.ClassDefinition.fetchInstance(os, docClassName, null);
			PropertyDefinitionList pdList = classDef.get_PropertyDefinitions();

			if (pdList != null){
				propertyList = new ArrayList<PropertyBean>();
				for (int i = 0; i < pdList.size(); i++){
					boolean removeProp = true;
					PropertyDefinition pd = (PropertyDefinition) pdList.get(i);
					//Ignore certain properties as they are not used in the app
					if (pd.get_SymbolicName().equalsIgnoreCase("SourceDocument") || pd.get_SymbolicName().equalsIgnoreCase("RecordInformation") || pd.get_SymbolicName().equalsIgnoreCase("DestinationDocuments")){
						removeProp = false;
					}
					PropertyBean propertyBean;
					//Ignore all system owned properties and the security object
					if (!pd.get_IsSystemOwned() && !pd.get_IsHidden() && removeProp){	
						propertyBean = new PropertyBean();
						if (pd.get_SymbolicName().equalsIgnoreCase("DocumentTitle")){
							propertyBean.setDescriptiveText("Document Title");
						}else{
							if (pd.get_DescriptiveText() == null || pd.get_DescriptiveText().equals("")){
								propertyBean.setDescriptiveText(pd.get_SymbolicName());
							}else{
								propertyBean.setDescriptiveText(pd.get_DescriptiveText());
							}
						}
						String defaultPropertyValue = getDefaultValuesForPropDef(pd);
						if ( pd.get_DataType().toString().equalsIgnoreCase("String")){
							int maxLength = pd.getProperties().getInteger32Value("MaximumLengthString");
							propertyBean.setMaxLength(maxLength);
						}

						propertyBean.setPropertyName(pd.get_SymbolicName());
						propertyBean.setPropertyValue(defaultPropertyValue);
						propertyBean.setDatatype(pd.get_DataType().toString());
						propertyBean.setCardinality(pd.get_Cardinality().toString());
						propertyBean.setDocClassName(docClassName);
						propertyBean.setMandatory(pd.get_IsValueRequired());
						propertyBean.setLookupAvailable(false);
						
						if(propertyBean.getPropertyName().equalsIgnoreCase("EmailSubject"))
							propertyList.add(0, propertyBean);
						else
							propertyList.add(propertyBean);	
					}else{
						if (pd.get_SymbolicName().equalsIgnoreCase("FolderName")){
							propertyBean = new PropertyBean();
							propertyBean.setDescriptiveText("Subject");
							String defaultPropertyValue = getDefaultValuesForPropDef(pd);
							if ( pd.get_DataType().toString().equalsIgnoreCase("String")){
								int maxLength = pd.getProperties().getInteger32Value("MaximumLengthString");
								propertyBean.setMaxLength(maxLength);
							}

							propertyBean.setPropertyName(pd.get_SymbolicName());
							propertyBean.setPropertyValue(defaultPropertyValue);
							propertyBean.setDatatype(pd.get_DataType().toString());
							propertyBean.setCardinality(pd.get_Cardinality().toString());
							propertyBean.setDocClassName(docClassName);
							propertyBean.setMandatory(pd.get_IsValueRequired());
							propertyList.add(propertyBean);
						}
					}
				}
			}
			logger1.info("Exit Method : getPropertyDefinitions");
			return propertyList;
		} 
		catch (Exception e) 
		{
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		
	}
	
	private String getDefaultValuesForPropDef(PropertyDefinition pd) throws Exception{
		logger1.info("Started Method : getDefaultValuesForPropDef");
		String defaultPropertyValue = null;
		try{
			String dataType = pd.get_DataType().toString();
			if(dataType.equalsIgnoreCase("String")){
				if(pd.getProperties().getObjectValue("PropertyDefaultString")!=null)
					defaultPropertyValue = pd.getProperties().getObjectValue("PropertyDefaultString").toString();
			}else if(dataType.equalsIgnoreCase("Date")){	
				if(pd.getProperties().getObjectValue("PropertyDefaultDateTime")!=null){
					Date date = pd.getProperties().getDateTimeValue("PropertyDefaultDateTime");
					defaultPropertyValue =com.knpc.dims.filenet.util.Utils.formatDate(date);
				}
			}else if(dataType.equalsIgnoreCase("boolean")){
				if(pd.getProperties().getObjectValue("PropertyDefaultBoolean")!=null)
					defaultPropertyValue = pd.getProperties().getObjectValue("PropertyDefaultBoolean").toString();
			}else if(dataType.equalsIgnoreCase("Integer")){	
				if(pd.getProperties().getObjectValue("PropertyDefaultInteger32")!=null)
					defaultPropertyValue = pd.getProperties().getObjectValue("PropertyDefaultInteger32").toString();
			}else if(dataType.equalsIgnoreCase("ID")){	
				if(pd.getProperties().getObjectValue("PropertyDefaultId")!=null)
					defaultPropertyValue = pd.getProperties().getObjectValue("PropertyDefaultId").toString();
			}
		}catch(Exception e){
			logger1.error(e.getMessage(), e);
			throw new Exception(e);			
		}
		logger1.info("Exit Method : getDefaultValuesForPropDef");
		return defaultPropertyValue;
	}

	private PropertyFilter getDocumentsPropertyFilter()
	{
		logger1.info("Started Method : getDocumentsPropertyFilter");
		PropertyFilter pf = new PropertyFilter();
		FilterElement fe = new FilterElement(2, null, null, "SymbolicName Id DocumentTitle ClassDescription Name VersionSeries MimeType Creator DateCreated DateLastModified LastModifier IsReserved ContentSize MajorVersionNumber ContentElementsPresent Reservation FoldersFiledIn Versions PathName FolderName ContentElements ElementSequenceNumber RetrievalName ContentType DateCheckedIn DisplayName", null);
		pf.addIncludeProperty(fe);
		logger1.info("Exit Method : getDocumentsPropertyFilter");
		return pf;
	}
	
	private PropertyFilter getLimitedDocumentsPropertyFilter()
	{
		logger1.info("Started Method : getDocumentsPropertyFilter");
		PropertyFilter pf = new PropertyFilter();
		FilterElement fe = new FilterElement(2, null, null, "SymbolicName Id DocumentTitle MimeType", null);
		pf.addIncludeProperty(fe);
		logger1.info("Exit Method : getDocumentsPropertyFilter");
		return pf;
	}

	public SearchResultBean getSearchResults(SearchQueryBean searchQueryBean, int oldDepartmentId) throws Exception {
		logger1.info("Started Method : getSearchResults");
		System.out.println("Started Method : getSearchResults");
		try{
			
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, FilenetSystemConfiguration.getInstance().OS_NAME);
			
			SearchResultBean searchResultBean= null;
			
			FileNetSearchService searchResult = new FileNetSearchService(this);
			if(searchResult!=null){
				searchResultBean = new SearchResultBean();
			}
			searchResultBean = searchResult.getSearchResults(os, searchQueryBean,oldDepartmentId);
			
			logger1.info("Exit Method : getSearchResults");
			return searchResultBean;
		}
		catch (Exception e) {
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
	}
	
	public HashMap<String, PropertyBean> getPropertyTemplates(String objectStore) throws Exception {
		logger1.info("Started Method : getPropertyTemplates Method parameter objectStore : "+objectStore);
		HashMap<String, PropertyBean> propertyMap = null;
		try
		{
			if (PropertyDataType.propertyMap == null){
				propertyMap = new HashMap<String, PropertyBean>();
				Connection conn = getCEConnection();
				Domain domain = FileNetAdaptor.getDomain(conn);
				ObjectStore os = FileNetAdaptor.getObjectStore(domain, FilenetSystemConfiguration.getInstance().OS_NAME);

				ClassDefinition cd = Factory.DocumentClassDefinition.fetchInstance(os,"Document", null);
				PropertyDefinitionList pdList = cd.get_PropertyDefinitions();
				Iterator<PropertyDefinition> pdIter = pdList.iterator();
				while(pdIter.hasNext()){
					PropertyDefinition pd = pdIter.next();
					PropertyBean pb = new PropertyBean();
					if (pd.get_DataType() == TypeID.BOOLEAN || pd.get_DataType() == TypeID.STRING || pd.get_DataType() == TypeID.DATE || pd.get_DataType()==TypeID.LONG || pd.get_DataType()==TypeID.GUID )
						pb.setDatatype(pd.get_DataType().toString());
					pb.setSymbolicName(pd.get_SymbolicName());
					if(pd.get_DisplayName()!=null){
						pb.setDescriptiveText(pd.get_DisplayName());
					}else{
						pb.setDescriptiveText(pd.get_SymbolicName());	
					}
					propertyMap.put(pd.get_SymbolicName(),pb);
				}
				
				cd = Factory.ClassDefinition.fetchInstance(os, "Document", null);
				pdList = cd.get_PropertyDefinitions();
				pdIter = pdList.iterator();
				while(pdIter.hasNext()){
					PropertyDefinition pd = pdIter.next();
					PropertyBean pb = new PropertyBean();
					if (pd.get_DataType() == TypeID.BOOLEAN || pd.get_DataType()==TypeID.GUID || pd.get_DataType() == TypeID.STRING || pd.get_DataType() == TypeID.DATE || pd.get_DataType()==TypeID.LONG)
						pb.setDatatype(pd.get_DataType().toString());
					pb.setSymbolicName(pd.get_SymbolicName());
					if(pd.get_DisplayName()!=null){
						pb.setDescriptiveText(pd.get_DisplayName());
					}else{
						pb.setDescriptiveText(pd.get_SymbolicName());	
					}
					propertyMap.put(pd.get_SymbolicName(),pb);
				}

				PropertyTemplateSet ptSet = os.get_PropertyTemplates();
				Iterator<PropertyTemplate> ptIter = ptSet.iterator();
				while(ptIter.hasNext()){
					PropertyTemplate pt = ptIter.next();
					PropertyBean pb = new PropertyBean();
					
					if (pt.get_DataType() == TypeID.BOOLEAN || pt.get_DataType()==TypeID.GUID || pt.get_DataType() == TypeID.STRING || pt.get_DataType() == TypeID.DATE || pt.get_DataType()==TypeID.LONG)
						;
					pb.setDatatype(pt.get_DataType().toString());
					pb.setSymbolicName(pt.get_SymbolicName());

					if(pt.get_DisplayName()!=null){
						pb.setDescriptiveText(pt.get_DisplayName());
					}else{
						pb.setDescriptiveText(pt.get_SymbolicName());	
					}

					propertyMap.put(pt.get_SymbolicName(), pb);
				}
				PropertyDataType.propertyMap = propertyMap;
			}else{
				propertyMap = PropertyDataType.propertyMap;
			}							
		}catch(Exception e){
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		logger1.info("Exit Method : getPropertyTemplates");
		return propertyMap;
	}

	public String addDocument(DocumentBean documentBean,DocumentTransferBean transferBean, FileObject fileObject, String ObjStore, String[] recipientArray) throws Exception {
		logger1.info("Started Method : addDocument Method parameter objStore : "+ObjStore);
		Folder securityObject = null;
		String documentId = null;
		try 
		{
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore objectStore = FileNetAdaptor.getObjectStore(domain, FilenetSystemConfiguration.getInstance().OS_NAME);
			ReferentialContainmentRelationship relationship = null;
			UpdatingBatch ub = UpdatingBatch.createUpdatingBatchInstance(domain, RefreshMode.REFRESH);
			String selectedDocClassName =null;
			String fileName = fileObject.getFileName();
			Document doc = null;
			String guid=null;
			//For add document we are passing the document class name. In case of an entry template, document class Id is being passed.
			// That is handled in the exception condition. 
			try{
				doc = Factory.Document.createInstance(objectStore, transferBean.getDocClass());
			}catch (Exception e){	
				DocumentClassDefinition docClass = Factory.DocumentClassDefinition.fetchInstance(objectStore, new Id( transferBean.getDocClass()) ,null );
				selectedDocClassName = docClass.get_SymbolicName();
				transferBean.setDocClass(selectedDocClassName);
				doc = Factory.Document.createInstance(objectStore, transferBean.getDocClass());
			}
			//Create the content element list and set the content, mimetype, attachment name
			ContentElementList ceList = Factory.ContentElement.createList();
			ContentTransfer content = Factory.ContentTransfer.createInstance();
			content.setCaptureSource(fileObject.getInputStream());
			content.set_RetrievalName(fileName);
			String mimeType = Utils.getMimeType(fileName);
			content.set_ContentType(mimeType);
			ceList.add(content);
			doc.set_ContentElements(ceList);
			
			//Set the properties for the document
			List<PropertyBean> propertyList = documentBean.getProperties();
			Properties properties = doc.getProperties();
			for (int j = 0; j < propertyList.size(); j++){
				PropertyBean propertyBean = propertyList.get(j);
				String propertyName = propertyBean.getPropertyName();
				String propertyValue = propertyBean.getPropertyValue();

				String propertyType = getPropType(propertyName,	transferBean.getDocClass(), ObjStore);
				String propCard = getPropCardinality(propertyName,transferBean.getDocClass(),ObjStore);

				if (propCard.equalsIgnoreCase("SINGLE")	|| propCard.equalsIgnoreCase("")){
					if ((propertyType.equalsIgnoreCase("Integer") || propertyType.equalsIgnoreCase("Long"))){
						properties.putValue(propertyName,Integer.parseInt(propertyValue));
					}else if (propertyType.equalsIgnoreCase("Double")){
						properties.putValue(propertyName,Double.parseDouble(propertyValue));
					}else if (propertyType.equalsIgnoreCase("String")){
						properties.putValue(propertyName, propertyValue);
					}else if (propertyType.equalsIgnoreCase("Float")){
						properties.putValue(propertyName,Float.parseFloat(propertyValue));
					}else if (propertyType.equalsIgnoreCase("Boolean")){
						properties.putValue(propertyName,	Boolean.parseBoolean(propertyValue));
					}else if (propertyType.equalsIgnoreCase("ID")){
						properties.putValue(propertyName, new Id(propertyValue));
					}else if (propertyType.equalsIgnoreCase("Date")){
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						Date dt = null;
						if(!(propertyValue.trim().equalsIgnoreCase(""))){
							dt = sdf.parse(propertyValue);
						}
						properties.putValue(propertyName, dt);
					}
				}else if ((propCard.equalsIgnoreCase("LIST"))){
					if (propertyType.equalsIgnoreCase("String")){
						StringList sl = Factory.StringList.createList();
						List<String> list = Utils.setMultipleValues(propertyValue);

						for (int x = 0; x < list.size(); x++){
							sl.add(list.get(x));
						}
						properties.putValue(propertyName, sl);
					}else if ((propertyType.equalsIgnoreCase("Integer") || propertyType.equalsIgnoreCase("Long"))){
						Integer32List integer_list = Factory.Integer32List.createList();
						List<String> list_string = Utils.setMultipleValues(propertyValue);

						for (int i = 0; i < list_string.size(); i++) 
						{
							integer_list.add(Integer.parseInt(list_string.get(i)));
						}
						properties.putValue(propertyName, integer_list);
					}else if (propertyType.equalsIgnoreCase("Double")) {
						Float64List float_list = Factory.Float64List.createList();
						List<String> list_string = Utils.setMultipleValues(propertyValue);
						for (int i = 0; i < list_string.size(); i++){
							float_list.add(Double.parseDouble((list_string.get(i))));
						}
						properties.putValue(propertyName, float_list);
					}else if (propertyType.equalsIgnoreCase("Date")) {
						DateTimeList dateTimeList = Factory.DateTimeList.createList();
						List<String> list = Utils.setMultipleValues(propertyValue);
						for (int x = 0; x < list.size(); x++){
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							Date dt = sdf.parse(list.get(x));
							dateTimeList.add(dt);
						}
						properties.putValue(propertyName, dateTimeList);
					}
				}
			}

			if (mimeType != null){
				doc.set_MimeType(mimeType);
			}
			doc.checkin(AutoClassify.AUTO_CLASSIFY,	CheckinType.MAJOR_VERSION);
			
			//Create a batch operation and add the checkin operation 
			PropertyFilter pf=getDocumentsPropertyFilter();
			BatchItemHandle docHandle=ub.add(doc,pf );
			
			//If the document is being filed in a folder, get the folder details, and file the document to the folder.
			if (transferBean.getFolderPath() != null) {
				String folderPath = transferBean.getFolderPath();
				if (folderPath != null ){
					if(!folderPath.trim().equalsIgnoreCase("")){
						Folder fldr = Factory.Folder.fetchInstance(objectStore,folderPath, null);
						relationship = fldr.file(doc,AutoUniqueName.AUTO_UNIQUE,null,DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
						ub.add(relationship, null);
					}	
				}
			}
			//Commit the batch
			ub.updateBatch();
			Document tempDoc=(Document) docHandle.getObject();
			documentId=tempDoc.get_Id().toString();
			if(recipientArray!=null){
				setDocumentSecurity(documentId, recipientArray);
			}
		} catch (Exception e) {
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		logger1.info("Exit Method : addDocument");
		return documentId;
	}

	private String getPropCardinality(String propertyName, String docClass,	String objStore) throws Exception {
		logger1.info("Started Method : getPropCardinality Method parameter propertyName : "+propertyName+" docClass : "+docClass+" objStore"+objStore);
		String propType = "";
		List<PropertyBean> propList = getPropertyDefinitions(docClass,objStore);
		Iterator<PropertyBean> propItr = propList.iterator();
		while (propItr.hasNext()){
			PropertyBean propertyBean = propItr.next();
			if (propertyBean.getPropertyName().equalsIgnoreCase(propertyName)){
				propType = propertyBean.getCardinality();
				break;
			}
		}
		logger1.info("Exit Method : getPropCardinality");
		return propType;
	}

	private String getPropType(String propertyName, String docClass,String objStore) throws Exception {
		logger1.info("Started Method : getPropType Method parameter propertyName : "+propertyName+" docClass : "+docClass+" objStore : "+objStore);
		String propType = "";
		List<PropertyBean> propList = getPropertyDefinitions(docClass,objStore);
		Iterator<PropertyBean> propItr = propList.iterator();

		while (propItr.hasNext()){
			PropertyBean propertyBean = propItr.next();
			if (propertyBean.getPropertyName().equalsIgnoreCase(propertyName)){
				propType = propertyBean.getDatatype();
				break;
			}
		}
		logger1.info("Exit Method : getPropType");
		return propType;
	}

	public List<VersionBean> getVersions(String docId, String objectStoreName) throws Exception {
		logger1.info("Started Method : getVersions Method parameter docId : "+docId+" objectStoreName : "+objectStoreName);
		try 
		{
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, objectStoreName);
			Document document = Factory.Document.fetchInstance(os, new Id(docId), null);
			
			VersionSeries versionSeries = document.get_VersionSeries();
			VersionableSet vSet = versionSeries.get_Versions();
			ArrayList<VersionBean> versionBeanList = new ArrayList<VersionBean>();
			Iterator<Versionable> verIter = vSet.iterator();
			while (verIter.hasNext()){
				Document versionable = (Document) verIter.next();
				VersionBean versionBean = new VersionBean();
				versionBean.setId(versionable.get_Id().toString());
				versionBean.setVersionStatus(versionable.get_VersionStatus().toString());
				versionBean.setMajorVersion((versionable.get_MajorVersionNumber()));				
				versionBean.setCreatedBy(versionable.get_LastModifier());
				if (versionable.get_DateCheckedIn() != null){
					versionBean.setDateCreated(Utils.formatDate(versionable.get_DateCheckedIn()));
				}
				versionBean.setClassName(versionable.get_ClassDescription().get_SymbolicName());
				versionBean.setObjectStoreName(objectStoreName);
				versionBeanList.add(versionBean);
			}
			logger1.info("Exit Method : getVersions");
			return versionBeanList;
		}catch (Exception e){
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
	}

	public List<FolderBean> getFoldersFileIn(String docId,String objectStoreName) throws Exception {
		logger1.info("Started Method : getFoldersFileIn Method parameter docId "+docId+" objectStoreName : "+objectStoreName);
		try {
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, objectStoreName);
			ArrayList<FolderBean> folderBeanList = null;
			Document document = Factory.Document.fetchInstance(os, new Id(docId), null);
			VersionSeries vs = document.get_VersionSeries();
			Document doc = (Document) vs.get_CurrentVersion();

			FolderSet folderSet = doc.get_FoldersFiledIn();

			Iterator<Folder> folderItr = folderSet.iterator();
			if (folderItr != null) {
				folderBeanList = new ArrayList<FolderBean>();
			}
			while (folderItr.hasNext()) {
				Folder folder = folderItr.next();
				String id = folder.get_Id().toString();
				FolderBean folderBean = new FolderBean();
				folderBean.setId(id);
				folderBean.setSymbolicName(folder.get_FolderName());
				folderBean.setDateCreated(Utils.formatDate(folder.get_DateCreated()));
				folderBean.setCreatedBy(folder.get_Creator());
				folderBean.setPath(folder.get_PathName());
				folderBean.setObjectStoreName( objectStoreName ) ;
				folderBeanList.add(folderBean);
			}
			logger1.info("Exit Method : getFoldersFileIn");
			return folderBeanList;
		} catch (Exception e) {
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
	}

	public boolean foldersFileIn(String docId, String objectStoreName,String destinationFolderId) throws Exception {
		logger1.info("Started Method : foldersFileIn Method parameter docId : "+docId+" objectStoreName : "+objectStoreName+" destinationFolderId : "+destinationFolderId);
		boolean isFiled = false;
		try {
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, objectStoreName);
			Document document = Factory.Document.fetchInstance(os, new Id(docId), null);
			
			VersionSeries vs = document.get_VersionSeries();
			Document doc = (Document) vs.get_CurrentVersion();
			Folder folder = Factory.Folder.fetchInstance(os, new Id(destinationFolderId), null);

			// Check if the document is already filed in the folder.
			FolderSet foldersFiledIn = doc.get_FoldersFiledIn();
			Iterator<Folder> fldrIterator = foldersFiledIn.iterator();
			if (fldrIterator != null) {
				while (fldrIterator.hasNext()) {
					Folder fldr = fldrIterator.next();
					if (fldr.get_Id().toString()
							.equalsIgnoreCase(folder.get_Id().toString())) {
						//logger1.error("Document is already filed in the selected folder");
						throw new Exception("Document is already filed in the selected folder");
					}
				}

			}

			ReferentialContainmentRelationship rel = null;
			rel = folder.file(doc, AutoUniqueName.AUTO_UNIQUE, null,DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);

			rel.save(RefreshMode.REFRESH);
			if (rel != null) {
				isFiled = true;
			}
		} catch (Exception e) {
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		logger1.info("Exit Method : foldersFileIn");
		return isFiled;
	
	}

	public boolean moveDocument(String docId, String objectStoreName,String currentFolderId, String destinationFolderId) throws Exception {

		logger1.info("Started Method : moveDocument Method parameter docId : "+docId+" objectStoreName : "+objectStoreName+" currentFolderId : "+currentFolderId+" destinationFolderId "+destinationFolderId);
		boolean move = true;
		boolean isMoved = false;
		boolean isRemoved = false;
		try {
				try
				{
					List<FolderBean> folderBeanList = null;
					if(currentFolderId!=null && (currentFolderId.equalsIgnoreCase("") || currentFolderId.equalsIgnoreCase("undefined"))){
						folderBeanList = getFoldersFileIn(docId, objectStoreName);
					}
					isMoved = foldersFileIn(docId, objectStoreName,destinationFolderId);
					
					if(folderBeanList!=null){
						for (FolderBean folderBean : folderBeanList) {
							isRemoved = unFileDocumentFromFolder(docId,objectStoreName, folderBean.getId());
						}
					}else{
						isRemoved = unFileDocumentFromFolder(docId,objectStoreName, currentFolderId);
					}

				}
				catch (Exception e)
				{
					// If the document is filed into destination folder but not unfiled from source
					// then unfile it from destination folder
					if (isMoved == true && isRemoved == false)
					{
						isRemoved = unFileDocumentFromFolder(docId,objectStoreName, destinationFolderId);
					}

					Connection conn = getCEConnection();
					Domain domain = FileNetAdaptor.getDomain(conn);
					ObjectStore os = FileNetAdaptor.getObjectStore(domain, objectStoreName);
					Document document = Factory.Document.fetchInstance(os, new Id(docId), null);
					VersionSeries vs = document.get_VersionSeries();
					//VersionSeries vs = Factory.VersionSeries.fetchInstance(os, new Id(docId), null);
					Document doc = (Document) vs.get_CurrentVersion();
					String docName = doc.getProperties().getStringValue("DocumentTitle");
					move = false;
					logger1.error(e.getMessage(), e);
					throw new Exception("access deined to move the document");
					
				}
			
		} catch (Exception e) {
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		logger1.info("Exit Method : moveDocument");
		return move;
	
	}
	
	public boolean unFileDocumentFromFolder(String docId, String objectStore,String folderId) throws Exception {
		logger1.info("Started Method : unFileDocumentFromFolder Method parameter docId : "+docId+" objectStore : "+objectStore+" folderId : "+folderId);
		boolean docRemoved = false;
		try {
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, objectStore);
			Document document = Factory.Document.fetchInstance(os, new Id(docId), null);
			VersionSeries vs = document.get_VersionSeries();

			Document doc = (Document) vs.get_CurrentVersion();
			
				Folder folder = Factory.Folder.fetchInstance(os, new Id(folderId),null);
				ReferentialContainmentRelationship rel = null;
				rel = folder.unfile(doc);
				rel.save(RefreshMode.REFRESH);
				if (rel != null) {
					docRemoved = true;
				}
		} catch (Exception e) {
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		logger1.info("Exit Method : unFileDocumentFromFolder");
		return docRemoved;

	}

	public DocumentDownloadBean downloadDocument(String docId,String objectStoreName) throws Exception {
		logger1.info("Started Method : downloadDocument Method parameter docId : "+docId+" objectStoreName : "+objectStoreName);
		DocumentDownloadBean documentDownloadBean = null;
		try {
			
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, objectStoreName);
			Document documents = Factory.Document.fetchInstance(os, new Id(docId), null);
			VersionSeries vs = documents.get_VersionSeries();
			Document document = (Document) vs.get_CurrentVersion();
			List<String> elementSeqNos = new ArrayList<String>();
			elementSeqNos.add("0");
			List<DocumentDownloadBean> documentDownloadList = downloadObjects(document, elementSeqNos);
			documentDownloadBean = documentDownloadList.get(0);
		} catch (Exception e) {
			logger1.error(e.getMessage(), e);
			if(e.getMessage().contains("The requested item was not found")){
				documentDownloadBean = new DocumentDownloadBean();
			}else{
				throw new Exception(e.getMessage());
			}
			
		}
		logger1.info("Exit Method : downloadDocument");
		return documentDownloadBean;	
	}
	
	public String getOutlookAttachment(String docId,String objectStoreName) throws Exception {
		logger1.info("Started Method : downloadOutlookAttachment Method parameter docId : "+docId+" objectStoreName : "+objectStoreName);
		String outFileName = "";
		try {
			DocumentDownloadBean documentDownloadBean = null;
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, objectStoreName);
			Document documents = Factory.Document.fetchInstance(os, new Id(docId), null);
			VersionSeries vs = documents.get_VersionSeries();
			Document document = (Document) vs.get_CurrentVersion();
			List<String> elementSeqNos = new ArrayList<String>();
			elementSeqNos.add("0");
			List<DocumentDownloadBean> documentDownloadList = downloadObjects(document, elementSeqNos);
			documentDownloadBean = documentDownloadList.get(0);
			
			ArrayList<String> folderCfgs = Utils.getOutlookFolders();
			//String serverFileName = folderCfgs.get(0) + documentDownloadBean.getFileName();
			//String serverFileName = folderCfgs.get(0) + documentDownloadBean.getFileName().replace("/", "_");
			String fileName = documentDownloadBean.getFileName().replaceAll("\\\\", "_").replaceAll("/", "_");
			String serverFileName = folderCfgs.get(0) + fileName;
			outFileName = folderCfgs.get(1) + fileName;
			System.out.println("Server File: " + serverFileName + ", Client File: " + outFileName);
			File outFile = new File(serverFileName);
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			logger1.info("Outlook Attachment File: " + outFile);
			OutputStream outStream = new FileOutputStream(outFile);
			logger1.info("Created outStream");
			
			byte[] buf = new byte[4096];
			int len = -1;
			while ((len = documentDownloadBean.getInputStream().read(buf)) != -1) {
			    outStream.write(buf, 0, len);
			}
			outStream.flush();
			outStream.close();
			logger1.info("Wrote to outStream");
			return outFileName;
		} catch (Exception e) {
			logger1.error(e.getMessage(), e);
			if(e.getMessage().contains("The requested item was not found")){
			}else{
				throw new Exception(e.getMessage());
			}
			
		}
		logger1.info("Exit Method : downloadDocument");
		return outFileName;	
	}
	
	private List<DocumentDownloadBean> downloadObjects(IndependentlyPersistableObject object, List<String> elementSeqNos) throws Exception {
		logger1.info("Started Method : downloadObjects");
		List<DocumentDownloadBean> documentDownloadsList = null;
		try 
		{
			Document document = (Document) object;
			String docName = getSubject(document.getProperties()); 
			if((docName == null) || (docName.trim().length() <= 0))
				docName = document.getProperties().getStringValue("DocumentTitle");
			ContentElementList ceListOld = document.get_ContentElements();
			Iterator<ContentElement> ceItr = ceListOld.iterator();
			documentDownloadsList = new ArrayList<DocumentDownloadBean>();
			while (ceItr.hasNext()){
				ContentTransfer ce = (ContentTransfer) ceItr.next();
				if (elementSeqNos.contains(ce.get_ElementSequenceNumber().toString())){
					DocumentDownloadBean documentDownloadBean = new DocumentDownloadBean();
					InputStream is = ce.accessContentStream();
					documentDownloadBean.setInputStream(is);
					documentDownloadBean.setMimeType(ce.get_ContentType());
					documentDownloadBean.setFileName(ce.get_RetrievalName());
					String docFileName = getTitleFileName(docName, documentDownloadBean.getFileName());
					documentDownloadBean.setFileName(docFileName);
					documentDownloadsList.add(documentDownloadBean);
				}
			}
		} catch (Exception e){
			logger1.error(e.getMessage(), e);
			if(e.getMessage().contains("The file does not exist")){
				documentDownloadsList.add(new DocumentDownloadBean());
			}else{
				throw new Exception(e.getMessage());
			}
		}
		logger1.info("Exit Method : downloadObjects");
		return documentDownloadsList;
	}

	private String getTitleFileName(String docName, String fileName) {
		/*String retFile = docName;
		if((docName == null) || (docName.length() <= 0))
			retFile = fileName;
		
		try {
			int nDot = docName.lastIndexOf('.');
			if(nDot <= 0)
				nDot = docName.length();
			String justDoc = docName.substring(0, nDot);
			justDoc = justDoc.replaceAll("\\\\", "_").replaceAll("/", "_");
			if(nDot > 50)
				justDoc = justDoc.substring(0, 100);
			
			String fext = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
			if((fext != null) && (fext.length() > 0) && (fext.length() < fileName.length() -1 ))
				return (justDoc + "." + fext);
			
			String ext = docName.substring(docName.lastIndexOf('.') + 1, docName.length());
			if((ext != null) && (ext.length() > 0) && (ext.length() < docName.length() -1 ))
				return (justDoc + "." + ext);
		} catch (Exception e) {
		}
		return retFile.replaceAll("\\\\", "_").replaceAll("/", "_");*/
		

		String returnFileName = ""; 
		System.out.println("docName  ::: "+docName +" fileName ::: "+fileName);
		try {
			if(docName !=null && docName.length()>0){
				int nDot = docName.length();
				if(nDot > 100)
					docName = docName.substring(0, 100);
				
				docName = docName.replaceAll("\\\\", "_").replaceAll("/", "_");
				
				String fext = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
				
				if((fext != null) && (fext.length() > 0) && (fext.length() < fileName.length() -1 ))
					returnFileName = docName + "." + fext;
				else 
					returnFileName = docName+ ".PDF";
			}else{
				returnFileName = fileName;
			}
			
		} catch (Exception e) {
			logger1.error(e.getMessage(), e);
		}
		
		return returnFileName;
		
	}
	
	public List<PropertyBean> getObjectPropertyValues(String docId,String objectStoreName) throws Exception {
		logger1.info("Started Method : getObjectPropertyValues Method parameter docId : "+docId+" objectStoreName : "+objectStoreName);
		try {
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, objectStoreName);
			Document documents = Factory.Document.fetchInstance(os, new Id(docId), null);
			VersionSeries vs = documents.get_VersionSeries();
			Document doc = (Document) vs.get_CurrentVersion();
			logger1.info("Exit Method : getObjectPropertyValues");
			return getPropertyValues(objectStoreName, doc);
		} catch (Exception e) {
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}

	}
	
	public List<PropertyBean> getPropertyValues(String objectStore,IndependentlyPersistableObject doc) throws Exception {
		logger1.info("Started Method : getPropertyValues");
		List<PropertyBean> propertyList = null;

		try 
		{			
			String docClassName = doc.get_ClassDescription().get_SymbolicName();
			List<PropertyBean> docClassPropertyList = getPropertyDefinitions(docClassName, objectStore);
			Properties properties = doc.getProperties();			
			if (docClassPropertyList != null){
				propertyList = new ArrayList<PropertyBean>();
				PropertyBean propertyBean = new PropertyBean();
				propertyBean.setPropertyName("Document Class");
				propertyBean.setPropertyValue(docClassName);
				propertyBean.setDescriptiveText(doc.get_ClassDescription().get_DescriptiveText());
				propertyList.add(propertyBean);
				for (int i = 0; i < docClassPropertyList.size(); i++){
					PropertyBean pd = docClassPropertyList.get(i);
					String propName = pd.getPropertyName();
					propertyBean = new PropertyBean();
					StringBuilder values = new StringBuilder();
					String propCardinality = pd.getCardinality();
					String propertyType = pd.getDatatype();
					if(propertyType.equalsIgnoreCase("STRING")){
						int maxLen = 0;
						maxLen =pd.getMaxLength();
						propertyBean.setMaxLength(maxLen);
					}
					if (propCardinality.equalsIgnoreCase("Single")){
						if ((propertyType.equalsIgnoreCase("Integer") || propertyType.equalsIgnoreCase("Long"))) {
							Integer intValue = properties.getInteger32Value(propName);
							if ((intValue != null)){
								propertyBean.setPropertyValue(intValue.toString());
							}
						}else if (propertyType.equalsIgnoreCase("Double")){
							Double doubleValue = properties.getFloat64Value(propName);
							if (doubleValue != null){
								propertyBean.setPropertyValue(doubleValue.toString());
							}
						}else if (propertyType.equalsIgnoreCase("String")){
							String stringValue = properties.getStringValue(propName);
							if (stringValue != null){
								propertyBean.setPropertyValue(stringValue.toString());
							}
						}else if (propertyType.equalsIgnoreCase("Float")){
							
						}else if (propertyType.equalsIgnoreCase("Boolean")){
							Boolean booleanValue = properties.getBooleanValue(propName);
							if (booleanValue != null){
								propertyBean.setPropertyValue(booleanValue.toString());
							}
						}else if (propertyType.equalsIgnoreCase("ID")) {
							
						}else if (propertyType.equalsIgnoreCase("Date")){
							Date date = properties.getDateTimeValue(propName);
							if (date != null){
								//String formattedDate = Utils.formatDateForUI(date);
								String formattedDate = Utils.formatDateForUIUpdated(date);
								propertyBean.setPropertyValue(formattedDate);
							}
						}
					}else if (propCardinality.equalsIgnoreCase("List")){
						if ((propertyType.equalsIgnoreCase("Integer") || propertyType.equalsIgnoreCase("Long"))){
							Integer32List integer_list = properties.getInteger32ListValue(propName);
							String value = null;
							for (int j = 0; j < integer_list.size(); j++){
								value = integer_list.get(j).toString();
								values.append(value).append(';');
							}

							if ((values != null)){
								propertyBean.setPropertyValue(values.toString());
							}
						}else if (propertyType.equalsIgnoreCase("Double")|| propertyType.equalsIgnoreCase("Float")){
							Float64List float_list = properties.getFloat64ListValue(propName);
							String value = null;
							for (int j = 0; j < float_list.size(); j++){
								value = float_list.get(j).toString();
								values.append(value).append(';');
							}

							if (!(values.equals(null))){
								propertyBean.setPropertyValue(values.toString());
							}
						}else if (propertyType.equalsIgnoreCase("String")){
							StringList sl = properties.getStringListValue(propName);
							String value = null;
							for (int j = 0; j < sl.size(); j++){
								value = (String) sl.get(j);
								values.append(value).append(';');
							}
							if (values != null){
								propertyBean.setPropertyValue(values.toString());
							}
						}else if (propertyType.equalsIgnoreCase("Boolean")) {
							
						}else if (propertyType.equalsIgnoreCase("ID")) {
							
						}else if (propertyType.equalsIgnoreCase("Date")){
							DateTimeList dateTimeList = properties.getDateTimeListValue(propName);
							String formattedDate = null;

							for (int j = 0; j < dateTimeList.size(); j++){
								Date date = (Date) dateTimeList.get(j);
								formattedDate = Utils.formatDateForUI(date);
								values.append(formattedDate).append(';');
							}

							if ((values != null)) {
								propertyBean.setPropertyValue(values.toString());
							}
						}
					}
					if (pd.getPropertyName().equalsIgnoreCase("DocumentTitle")){
						propertyBean.setDescriptiveText("Document Title");
					}else{
						if (pd.getDescriptiveText() == null || pd.getDescriptiveText().equals("")){
							propertyBean.setDescriptiveText(pd.getPropertyName());
						}else{
							propertyBean.setDescriptiveText(pd.getDescriptiveText());
						}
					}
					propertyBean.setPropertyName(pd.getPropertyName());
					propertyBean.setMandatory(pd.isMandatory());
					propertyBean.setCardinality(pd.getCardinality());
					propertyBean.setIsSettable(pd.getIsSettable());
					propertyBean.setDatatype(pd.getDatatype());
					propertyBean.setIsSettable("true");
					propertyBean.setDocClassName(docClassName);
					
					propertyList.add(propertyBean);				
				}

				PropertyBean docPropertyBean1 = new PropertyBean();
				docPropertyBean1.setPropertyName("Creator");
				docPropertyBean1.setDescriptiveText("Added by");
				docPropertyBean1.setPropertyValue(doc.getProperties().getObjectValue("Creator").toString());
				docPropertyBean1.setIsSettable("false");

				PropertyBean docPropertyBean2 = new PropertyBean();
				docPropertyBean2.setPropertyName("DateCreated");
				docPropertyBean2.setDescriptiveText("Added on");
				Date addedDate = (Date) doc.getProperties().getObjectValue("DateCreated");
				Date modifiedOn = (Date) doc.getProperties().getObjectValue("DateLastModified");
				docPropertyBean2.setPropertyValue(Utils.formatDate(addedDate));
				docPropertyBean2.setIsSettable("false");

				PropertyBean docPropertyBean3 = new PropertyBean();
				docPropertyBean3.setPropertyName("LastModifier");
				docPropertyBean3.setDescriptiveText("Modified by");
				docPropertyBean3.setPropertyValue(doc.getProperties().getObjectValue("LastModifier").toString());
				docPropertyBean3.setIsSettable("false");

				PropertyBean docPropertyBean4 = new PropertyBean();
				docPropertyBean4.setPropertyName("DateLastModified");
				docPropertyBean4.setDescriptiveText("Modified on");
				docPropertyBean4.setPropertyValue(Utils.formatDate(modifiedOn));
				docPropertyBean4.setIsSettable("false");

				PropertyBean docPropertyBean5 = new PropertyBean();
				docPropertyBean5.setPropertyName("IsReserved");
				docPropertyBean5.setDescriptiveText("Is checked out");
				docPropertyBean5.setPropertyValue(doc.getProperties().getObjectValue("IsReserved").toString());
				docPropertyBean5.setIsSettable("false");
				
				PropertyBean docPropertyBean6 = new PropertyBean();
				docPropertyBean6.setPropertyName("ID");
				docPropertyBean6.setDescriptiveText("ID");
				docPropertyBean6.setPropertyValue(doc.getProperties().getObjectValue("ID").toString());
				docPropertyBean6.setIsSettable("false");

				PropertyBean docPropertyBean7 = new PropertyBean();
				docPropertyBean7.setPropertyName("ContentSize");
				docPropertyBean7.setDescriptiveText("Content Size");
				//Added by Ravi Boni on 17-11-2017
				Double d = (Double)doc.getProperties().getObjectValue("ContentSize")/1024;
				Double roundOff = Math.round(d * 100.0) / 100.0;
				docPropertyBean7.setPropertyValue(roundOff.toString()+" KB");
				//docPropertyBean7.setPropertyValue(doc.getProperties().getObjectValue("ContentSize").toString());
				//end
				docPropertyBean7.setIsSettable("false");
				
				propertyList.add(docPropertyBean1);
				propertyList.add(docPropertyBean2);
				propertyList.add(docPropertyBean3);
				propertyList.add(docPropertyBean4);
				propertyList.add(docPropertyBean5);
				propertyList.add(docPropertyBean6);
				propertyList.add(docPropertyBean7);
			}			
		} catch (Exception e) {
			logger1.error(e.getMessage(),e);
			throw new Exception(e.getMessage());
		}
		logger1.info("Exit Method : getPropertyValues");
		return propertyList;
	}

	public String checkIn(DocumentBean documentBean,DocumentTransferBean objectTransferBean, FileObject fileObject,String osName) throws Exception {
		logger1.info("Started Method : checkIn");
		try 
		{
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, osName);
			Document document = Factory.Document.fetchInstance(os, new Id(objectTransferBean.getObjectId()), null);
			
			VersionSeries vs = document.get_VersionSeries();
			Document doc = (Document) vs.get_CurrentVersion();

			List<InputStream> contentList = new ArrayList<InputStream>();
			contentList.add(fileObject.getInputStream());
			documentBean.setContentList(contentList);

			//Check if the document is currently checked out
			if (doc.get_IsReserved().booleanValue()){
				Document reservedDoc = (Document) doc.get_Reservation();
				setContent(documentBean, objectTransferBean.getFolderId(),reservedDoc, os, null,osName,fileObject.getFileName());
			}else{
				throw new Exception("Document cannot be checked in as it is not checked out");
			}
		} catch (Exception e) {
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		logger1.info("Exit Method : checkIn");
	 return "";
	}
	
	private void setContent(DocumentBean documentBean, String folder,	Document document, ObjectStore os, Folder securityObject,String objectStore,String uploadFileName) throws Exception {
		logger1.info("Started Method : setContent");
		String mimeType = Utils.getMimeType(uploadFileName);
		try {
			List<InputStream> isList = documentBean.getContentList();

			if (isList != null){
				ContentElementList ceList = document.get_ContentElements();

				for (int i = 0; i < isList.size(); i++){
					InputStream is = isList.get(i);
					ContentTransfer content = Factory.ContentTransfer.createInstance();
					content.set_ContentType(mimeType);
					content.set_RetrievalName(uploadFileName);
					content.setCaptureSource(is);
					ceList.add(content);
				}
				String documentClass = document.getClassName();
				document.set_ContentElements(ceList);
				document.checkin(AutoClassify.AUTO_CLASSIFY,CheckinType.MAJOR_VERSION);
				List<PropertyBean> propertyList = documentBean.getProperties();
				Properties properties = document.getProperties();
				for (int j = 0; j < propertyList.size(); j++){
					PropertyBean propertyBean = propertyList.get(j);
					String propertyName = propertyBean.getPropertyName();
					String propertyValue = propertyBean.getPropertyValue();
					String propertyType = getPropType(propertyName,	documentClass, objectStore);
					String propCard = getPropCardinality(propertyName,documentClass, objectStore);

					if (propCard.equalsIgnoreCase("SINGLE") || propCard.equalsIgnoreCase("")){
						if (propertyValue != null && !propertyValue.equals("")){
							if ((propertyType.equalsIgnoreCase("Integer") || propertyType.equalsIgnoreCase("Long"))){
								properties.putValue(propertyName,Integer.parseInt(propertyValue));
	
							}else if (propertyType.equalsIgnoreCase("Double")){
								properties.putValue(propertyName,Double.parseDouble(propertyValue));
							}else if (propertyType.equalsIgnoreCase("String")){
								properties.putValue(propertyName, propertyValue);
							}else if (propertyType.equalsIgnoreCase("Float")) 
							{
								properties.putValue(propertyName,Float.parseFloat(propertyValue));
							}else if (propertyType.equalsIgnoreCase("Boolean")){
								properties.putValue(propertyName,Boolean.parseBoolean(propertyValue));
							}else if (propertyType.equalsIgnoreCase("ID")){
								properties.putValue(propertyName, new Id(propertyValue));
							}else if (propertyType.equalsIgnoreCase("Date")){
								Date dt = null;
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								if(!(propertyValue.trim().equalsIgnoreCase(""))){
									dt = sdf.parse(propertyValue);
								}
								properties.putValue(propertyName, dt);
							}
						}
					}else if ((propCard.equalsIgnoreCase("LIST"))){
						if (propertyType.equalsIgnoreCase("String")){
							StringList sl = Factory.StringList.createList();
							List<String> list = Utils.setMultipleValues(propertyValue);

							for (int x = 0; x < list.size(); x++){
								sl.add(list.get(x));
							}
							properties.putValue(propertyName, sl);

						}else if ((propertyType.equalsIgnoreCase("Integer") || propertyType.equalsIgnoreCase("Long"))){
							Integer32List integer_list = Factory.Integer32List.createList();
							List<String> list_string = Utils.setMultipleValues(propertyValue);

							for (int i = 0; i < list_string.size(); i++){
								integer_list.add(Integer.parseInt(list_string.get(i)));
							}
							properties.putValue(propertyName, integer_list);
						}else if (propertyType.equalsIgnoreCase("Double")){
							Float64List float_list = Factory.Float64List.createList();
							List<String> list_string = Utils.setMultipleValues(propertyValue);

							for (int i = 0; i < list_string.size(); i++){
								float_list.add(Double.parseDouble((list_string.get(i))));
							}
							properties.putValue(propertyName, float_list);
						}else if (propertyType.equalsIgnoreCase("Date")){
							DateTimeList dateTimeList = Factory.DateTimeList.createList();
							List<String> list = Utils.setMultipleValues(propertyValue);

							for (int x = 0; x < list.size(); x++){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								Date dt = sdf.parse(list.get(x));
								dateTimeList.add(dt);
							}
							properties.putValue(propertyName, dateTimeList);
						}
					}
				}
				properties.putValue("mimeType", mimeType);
				document.save(RefreshMode.REFRESH);
			}
		} catch (Exception e) {
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		logger1.info("Exit Method : setContent");
	}

	public DocumentDownloadBean getDocumentMimeType(String docId, String osName) throws Exception {
		DocumentDownloadBean documentDownloadBean = null;
		try {
			
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, osName);
			Document documents = Factory.Document.fetchInstance(os, new Id(docId), null);
			VersionSeries vs = documents.get_VersionSeries();
			Document document = (Document) vs.get_CurrentVersion();
			List<String> elementSeqNos = new ArrayList<String>();
			elementSeqNos.add("0");
			List<DocumentDownloadBean> documentDownloadList = getDocumentObject(document, elementSeqNos);
			documentDownloadBean = documentDownloadList.get(0);
			documentDownloadBean.setDocId(docId);
		} catch (Exception e) {
			logger1.error(e.getMessage(), e);
			if(e.getMessage().contains("The requested item was not found.")){
				documentDownloadBean = new DocumentDownloadBean();
				documentDownloadBean.setFileName("Document not available");
			}else{
				throw new Exception(e.getMessage());
			}
		}
		logger1.info("Exit Method : downloadDocument");

		return documentDownloadBean;
	}
	
	private List<DocumentDownloadBean> getDocumentObject(IndependentlyPersistableObject object, List<String> elementSeqNos) throws Exception {
		logger1.info("Started Method : downloadObjects");
		List<DocumentDownloadBean> documentDownloadsList = null;
		try 
		{
			Document document = (Document) object;
			ContentElementList ceListOld = document.get_ContentElements();
			Iterator<ContentElement> ceItr = ceListOld.iterator();
			documentDownloadsList = new ArrayList<DocumentDownloadBean>();
			while (ceItr.hasNext()){
				ContentTransfer ce = (ContentTransfer) ceItr.next();
				if (elementSeqNos.contains(ce.get_ElementSequenceNumber().toString())){
					DocumentDownloadBean documentDownloadBean = new DocumentDownloadBean();
					
					documentDownloadBean.setMimeType(ce.get_ContentType());
					documentDownloadBean.setFileName(document.get_Name());
					documentDownloadsList.add(documentDownloadBean);
				}
			}
		} catch (Exception e){
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		logger1.info("Exit Method : downloadObjects");
		return documentDownloadsList;
	}

	public void sendEmail(EmailBean emailBean) throws Exception {
		
		java.util.Properties p = new java.util.Properties();
		try {
			
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("DIMS.properties");
			p.load(is);
			String SMTP_HOST_NAME = p.getProperty("SMTP_HOST_NAME");
			String SMTP_PORT = p.getProperty("SMTP_PORT");
			String from = p.getProperty("FROM_ADDRESS");
			String user = p.getProperty("SMTP_AUTH_USER");
			String pass = p.getProperty("SMTP_AUTH_PWD");
			
			HtmlEmail email = new HtmlEmail();
			email.setHostName(SMTP_HOST_NAME);
			email.setSmtpPort(parseSMTPPort(SMTP_PORT));
			if((from == null) || (from.length() <= 0))
				from = emailBean.getFrom();
			email.setFrom(from);
			if(pass== null)
				pass="";
			if((user != null) && (user.length() > 0))
				email.setAuthenticator(new DefaultAuthenticator(user,pass));

			StringBuffer tosb = new StringBuffer();
			StringBuffer ccsb = new StringBuffer();
			for (int i = 0; i < emailBean.getEmailRecipientList().size(); i++) {
				if(emailBean.getEmailRecipientList().get(i).getEmailRecipientType().equalsIgnoreCase("to")){
					tosb.append(emailBean.getEmailRecipientList().get(i).getEmailRecipient()).append(";");
				}else if(emailBean.getEmailRecipientList().get(i).getEmailRecipientType().equalsIgnoreCase("cc")){
					ccsb.append(emailBean.getEmailRecipientList().get(i).getEmailRecipient()).append(";");
				}
			}
			String[] toemailArray = tosb.toString().split(";");
			String[] ccemailArray = ccsb.toString().split(";");
			
			
			
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, emailBean.getOsName());
			for (int i = 0; i < emailBean.getObjectId().length; i++) {
				Document documents = Factory.Document.fetchInstance(os, new Id(emailBean.getObjectId()[i]), null);
				VersionSeries vs = documents.get_VersionSeries();
				//VersionSeries vs = Factory.VersionSeries.fetchInstance(os, new Id(vsId), null);
				Document document = (Document) vs.get_CurrentVersion();
				List<String> elementSeqNos = new ArrayList<String>();
				elementSeqNos.add("0");
				
				ContentElementList ceListOld = document.get_ContentElements();
				Iterator<ContentElement> ceItr = ceListOld.iterator();
				List<InputStream> ltInputStream = new ArrayList<InputStream>();
				List<String> ltDocNames = new ArrayList<String>();
				String docName = getSubject(document.getProperties()); 
				if((docName == null) || (docName.trim().length() <= 0))
					docName = document.getProperties().getStringValue("DocumentTitle");
				while (ceItr.hasNext()){
					ContentTransfer contentTransfer = (ContentTransfer) ceItr.next();
					if (elementSeqNos.contains(contentTransfer.get_ElementSequenceNumber().toString())){
						InputStream ist=null;
						ist = contentTransfer.accessContentStream();
						String contentType = contentTransfer.get_ContentType();
						String fileName = contentTransfer.get_RetrievalName();
						String docFileName = getTitleFileName(docName, fileName);
						
						DataSource source = new ByteArrayDataSource(ist, contentType);  
						// add the attachment
						email.attach(source, docFileName, docFileName);
					}
				}
			}
			
			email.setSubject(emailBean.getSubject());
			email.setMsg(emailBean.getBody());
			for(String address:toemailArray) {
				if(address.trim().length() > 0)
					email.addTo(address.trim());
			}
			for(String address:ccemailArray) {
				if(address.trim().length() > 0)
					email.addCc(address.trim());
			}
			System.out.println("*Email*:" + email.toString() + "\n" + getMailLogger(email));
			email.send();
		} catch (Exception e) {
			System.out.println("##Error##: " + e.getMessage());
			throw new Exception(e.getMessage());
		}
		
	}

	private String getMailLogger(HtmlEmail email) {
		String emailLog = "";
		try {
			emailLog = email.toString();
			
			emailLog += ("From: " + email.getFromAddress().toString() + "\n");
			emailLog += ("To: " + email.getToAddresses().toString() + "\n");
			emailLog += ("Subject: " + email.getSubject() + "\n");
			emailLog += ("Message: " + email.getMimeMessage().toString() + "\n");
			return emailLog;
			
		} catch (Exception e) {
		}
		return emailLog;
	}
	
	public boolean validateRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		/*boolean foundCookie = false;
		boolean authUser = false;
		Cookie[] cookies = req.getCookies(); 
		String userName = req.getParameter("user_login");
		String password = req.getParameter("password");
		String cookiesUserId=null;
		String cookiesPassword = null;
		
		if(cookies!=null)
		{
			for(int i = 0; i < cookies.length; i++)
			{ 			
				Cookie c = cookies[i];
				
				if (c.getName()!=null && c.getName().equals("token1"))
				{
					cookiesUserId = CryptoUtil.decryptString(c.getValue());		
				}else if(c.getName()!=null && c.getName().equals("token2"))
				{	
					cookiesPassword = CryptoUtil.decryptString(c.getValue());	
				}
				
				if(cookiesUserId!=null && cookiesPassword!=null)
				{
					foundCookie = true;
				}
			}
		}
		
		
		if(!foundCookie){
			if(password!=null){
				boolean flag = loadProperties(userName,password);
				if(flag){
					boolean validUser = authenticateUser();
					if(validUser){						
						Cookie userNameCookie = new Cookie("token1", CryptoUtil.encryptString(userName));
						Cookie passwordCookie = new Cookie("token2",CryptoUtil.encryptString(password));
						userNameCookie.setMaxAge(30*24*60*60);
						passwordCookie.setMaxAge(30*24*60*60);
						userNameCookie.setPath("/");
						passwordCookie.setPath("/");
					    resp.addCookie(userNameCookie); 
					    resp.addCookie(passwordCookie);
					    authUser = true;
					}else{
						authUser = false;
					}
				}
				}else{
					authUser = false;
				}
			
		}else{		 
			boolean flag = loadProperties(cookiesUserId,cookiesPassword);
			if(flag){
				boolean validUser = authenticateUser();
				if(validUser){
					authUser = true;
				}else{
					authUser = false;
				}
			}
		}
		return authUser;*/
		return true;
	}

	private boolean loadProperties(String userName, String password) throws Exception {
		try{
				
				ceURI = FilenetSystemConfiguration.getInstance().CE_URI;
				stanzName = FilenetSystemConfiguration.getInstance().STANZA;
				objStoreName = FilenetSystemConfiguration.getInstance().OS_NAME;
				userId = userName;
				pwd = password;
				conn = getSessionConnection(ceURI,stanzName,objStoreName,userId,pwd);
				return true;
					
		}catch(Exception e){
			e.printStackTrace();
			logger1.info("Exception caused at loadProperties() method  :: "+e.getMessage(),e);
		}
		return false;
	}
	private Connection getSessionConnection(String ceURI, String stanzName, String objStoreName, String userId, String pwd) throws Exception {
		try {
			logger1.info("Entering GetSession");
			//System.setProperty("java.security.auth.login.config", "C:\\IBM\\FileNet\\WebClient\\CE_API\\config\\jaas.conf.Websphere");
			conn = Factory.Connection.getConnection(ceURI);
			UserContext uc = UserContext.get();
			
			uc.pushSubject(UserContext.createSubject(conn, userId, pwd, stanzName));
			logger1.info("Exiting GetSession");
			
		} catch (Exception e) {
			logger1.info("Exception caused at getSessionConnection() method  :: "+e.getMessage(),e);
		}
		return conn;
	}
	
	private boolean authenticateUser() {
		boolean validUser = false;
		try {
			logger1.info(" Conn url  "+conn.getURI()+"     "+conn.toString());
			//System.setProperty("java.security.auth.login.config", "C:\\IBM\\FileNet\\WebClient\\CE_API\\config\\jaas.conf.Websphere");
			conn = Factory.Connection.getConnection(ceURI);
			com.filenet.api.core.Domain domain = com.filenet.api.core.Factory.Domain.fetchInstance(conn, null, null);
			os = com.filenet.api.core.Factory.ObjectStore.fetchInstance(domain, objStoreName, null);
			logger1.info("OS - " + os.get_Name());
			validUser = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger1.info("Exception at authorizeUser method :"+e.getMessage(), e);
		}
		return validUser;
	}


	public void setDocumentSecurity(String workflowDocumentId, String[] recipientArray) {
		try {
			for (int i=0;i<recipientArray.length;i++) {
				if(recipientArray[i] == null)
					continue;
				
				try {
					Connection conn = getCEConnection();
					Domain domain = FileNetAdaptor.getDomain(conn);
					ObjectStore os = FileNetAdaptor.getObjectStore(domain,FilenetSystemConfiguration.getInstance().OS_NAME);
					Document documents = Factory.Document.fetchInstance(os, new Id(workflowDocumentId), null);
					VersionSeries vs = documents.get_VersionSeries();
					//VersionSeries vs = Factory.VersionSeries.fetchInstance(os, new Id(vsId), null);
					Document document = (Document) vs.get_CurrentVersion();
					
					AccessPermissionList permissions =  document.get_Permissions(); 
	
					AccessPermission permission = Factory.AccessPermission.createInstance();
					permission.set_GranteeName(recipientArray[i]);
	
					permission.set_AccessMask(new Integer(394675));
					permission.set_AccessType(AccessType.ALLOW);
					permissions.add(permission);		
					
					document.set_Permissions(permissions);
					
					Properties properties = document.getProperties();
					properties.putValue("IsLaunched", 1);
					
					((Document) document).save(RefreshMode.REFRESH);
				} catch (Exception e) {
					e.printStackTrace();
					/*if(e.getMessage().contains("changed or deleted because it was modified one or more times in the repository")){
						setDocumentSecurity(workflowDocumentId, recipientArray);
					}*/
				}
			} 	
		} catch (Exception ex) {}
	}


public SearchResultBean getDailyDocumentItems(String supervisor,ArrayList assistanceList, String sortOrder, String sortColumn, int oldDepartmentId) throws Exception {
		
		logger1.info("Started Method : getDailyDocumentItems");
		try{
			
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, FilenetSystemConfiguration.getInstance().OS_NAME);
			
			SearchResultBean searchResultBean= null;
			
			FileNetSearchService searchResult = new FileNetSearchService(this);
			if(searchResult!=null){
				searchResultBean = new SearchResultBean();
			}
			searchResultBean = searchResult.getDailyDocumentItems(os, supervisor,assistanceList, sortOrder, sortColumn,oldDepartmentId);
			
			logger1.info("Exit Method : getDailyDocumentItems");
			return searchResultBean;
		}
		catch (Exception e) {
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
	}


	public String updateDailyDocument(String removeDocId, String objectStoreName) throws Exception {
		logger1.info("Started Method : updateDailyDocument");
		try{
			
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, FilenetSystemConfiguration.getInstance().OS_NAME);
			Document documents = Factory.Document.fetchInstance(os, new Id(removeDocId), null);
			VersionSeries vs = documents.get_VersionSeries();
			Document document = (Document) vs.get_CurrentVersion();
			
			Properties properties = document.getProperties();
			
			properties.putValue("Dmail", "1");
			
			document.save(RefreshMode.REFRESH);
			
			logger1.info("Exit Method : updateDailyDocument");
			
		}
		catch (Exception e) {
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		return "";
	}

	public List<HashMap<String, Object>> documentsScanned(int department, String division,String user, String from, String to) throws Exception {
		logger1.info("Started Method : getDailyDocumentItems");
		try{
			
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, FilenetSystemConfiguration.getInstance().OS_NAME);
			
			SearchResultBean searchResultBean= null;
			
			FileNetSearchService searchResult = new FileNetSearchService(this);
			if(searchResult!=null){
				searchResultBean = new SearchResultBean();
			}
			List<HashMap<String, Object>> mapList = searchResult.documentsScanned(os, department,division,user,from,to);
			
			logger1.info("Exit Method : getDailyDocumentItems");
			return mapList;
		}
		catch (Exception e) {
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		
	}
	
	public void updateDocumentProperties(DocumentBean db) throws Exception {
		try {
			logger1.info("Enter update Document Properties");
			
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, db.getObjectStoreName());
			Document documents = Factory.Document.fetchInstance(os, new Id(db.getDocumentID()), null);
			VersionSeries vs = documents.get_VersionSeries();
			Document document = (Document) vs.get_CurrentVersion();
			Properties props = document.getProperties();
			putPropertyValues(db.getProperties(), props);		
			document.save(RefreshMode.REFRESH);
			
			logger1.info("Exit update Document Properties");
		} catch (Exception e) {
			logger1.error(e.getMessage(), e);
			throw new Exception("Error while updating document properties. " + e.getMessage());
		}
	}
	
	private void putSingleValues(PropertyBean pb, Properties props) 
	{
		logger1.info("Enter putSingleValues");
		if(pb.getPropertyValue() != null){
			if(pb.getDatatype().equalsIgnoreCase("STRING")) {
				props.putValue(pb.getPropertyName(), pb.getPropertyValue());
			} else if(pb.getDatatype().equalsIgnoreCase("INTEGER") || pb.getDatatype().equalsIgnoreCase("LONG")) {
				if(!pb.getPropertyValue().equalsIgnoreCase(""))
					props.putValue(pb.getPropertyName(),Integer.parseInt(pb.getPropertyValue()));
			} else if (pb.getDatatype().equalsIgnoreCase("Double")){
				if(!pb.getPropertyValue().equalsIgnoreCase(""))
					props.putValue(pb.getPropertyName(),Double.parseDouble(pb.getPropertyValue()));
			}else if (pb.getDatatype().equalsIgnoreCase("Float")) {
				if(!pb.getPropertyValue().equalsIgnoreCase(""))
					props.putValue(pb.getPropertyName(),Float.parseFloat(pb.getPropertyValue()));
			}else if (pb.getDatatype().equalsIgnoreCase("Boolean")){
				if(!pb.getPropertyValue().equalsIgnoreCase(""))
					props.putValue(pb.getPropertyName(),Boolean.parseBoolean(pb.getPropertyValue()));
			}else if (pb.getDatatype().equalsIgnoreCase("ID")){
				if(!pb.getPropertyValue().equalsIgnoreCase(""))
					props.putValue(pb.getPropertyName(), new Id(pb.getPropertyValue()));
			}else if (pb.getDatatype().equalsIgnoreCase("Date")){
				if(!pb.getPropertyValue().equalsIgnoreCase("")){
					Date dt = getDateFromString(pb.getPropertyValue());
					if(dt != null)
						props.putValue(pb.getPropertyName(), dt);
				}
			}
		}
		
		logger1.info("Exit putSingleValues");
	}
	private String[] convetStringToList(String multiValue){
		String[] listValues= null;
		try{
		listValues=	multiValue.split(";");
		}catch(Exception ex){}
		return listValues;
				
	}
	private void putMultipleValues(PropertyBean pb, Properties props)
	{
		logger1.info("Enter putMultipleValues ::");
		System.out.println("pb.getPropertyMultiValues() ::::"+pb.getPropertyMultiValues());
		System.out.println("pb.getPropertyValue() ::::"+pb.getPropertyValue());
		try{
		if((pb == null) || (pb.getPropertyValue() == null))
			return;
		
		if (pb.getDatatype().equalsIgnoreCase("String")){
			StringList sl = Factory.StringList.createList();

			for (int i = 0; i < pb.getPropertyMultiValues().size(); i++){
				sl.add(pb.getPropertyMultiValues().get(i));
			}
			props.putValue(pb.getPropertyName(), sl);

		}else if ((pb.getDatatype().equalsIgnoreCase("Integer") || pb.getDatatype().equalsIgnoreCase("Long"))){
			Integer32List integer_list = Factory.Integer32List.createList();
			//System.out.println("pb.getPropertyMultiValues().get(i) ::::"+pb.getPropertyMultiValues().get(0));
			System.out.println("pb.getDatatype()::"+pb.getDatatype());
			String[] ListValues = convetStringToList(pb.getPropertyValue());
			for (int i = 0; i < ListValues.length; i++){
				System.out.println("pb.getDatatype()::"+Integer.parseInt(ListValues[i]));
				integer_list.add(Integer.parseInt(ListValues[i]));
			}
			props.removeFromCache(pb.getPropertyName());
			props.putValue(pb.getPropertyName(), integer_list);
		}else if (pb.getDatatype().equalsIgnoreCase("Double")){
			com.filenet.api.collection.Float64List float_list = Factory.Float64List.createList();

			for (int i = 0; i < pb.getPropertyMultiValues().size(); i++){
				float_list.add(Double.parseDouble((pb.getPropertyMultiValues().get(i))));
			}
			props.putValue(pb.getPropertyName(), float_list);
		}else if (pb.getDatatype().equalsIgnoreCase("Date")){
			DateTimeList dateTimeList = Factory.DateTimeList.createList();

			for (int i = 0; i < pb.getPropertyMultiValues().size(); i++){
				Date dt = getDateFromString(pb.getPropertyMultiValues().get(i));
				if(dt != null)
					dateTimeList.add(dt);
			}
			props.putValue(pb.getPropertyName(), dateTimeList);
		}
		}catch(Exception exceton){
			exceton.printStackTrace();
		}
		logger1.info("Exit putMultipleValues");
	}
	
	private void putPropertyValues(List<PropertyBean> pbList, Properties props)
	{
		logger1.info("Enter ::::PutpropertyValue");
		if(pbList != null) {
			for(PropertyBean pb:pbList) {
				
				if((pb.getCardinality() == null) || (pb.getCardinality().equalsIgnoreCase("SINGLE"))){
					if(pb.getDatatype()!=null){
						System.out.println("::SINGLE::"+pb.getPropertyName());
					putSingleValues(pb, props);}}
				else{
					System.out.println("::Multi::"+pb.getPropertyName()+"  :  "+pb.getCardinality()+"::"+pb.getDatatype());
					if(pb.getDatatype()!=null){
					putMultipleValues(pb, props);}
					}
			}
		}
		logger1.info("Exit PutpropertyValue");	
	}
	
	private Date getDateFromString(String dateStr)
	{
		Date dt = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			if(!(dateStr.trim().equalsIgnoreCase(""))){
				dt = sdf.parse(dateStr);
			} 
		} catch (Exception e) {
		}
		return dt;
	}


	/*public void getObjectAccessValues(String docId, String objectStoreName) throws Exception {
		logger1.info("Started Method : getObjectPropertyValues Method parameter docId : "+docId+" objectStoreName : "+objectStoreName);
		try {
			Connection conn = getCEConnection();
			Domain domain = FileNetAdaptor.getDomain(conn);
			ObjectStore os = FileNetAdaptor.getObjectStore(domain, objectStoreName);
			Document documents = Factory.Document.fetchInstance(os, new Id(docId), null);
			VersionSeries vs = documents.get_VersionSeries();
			Document doc = (Document) vs.get_CurrentVersion();
			logger1.info("Exit Method : getObjectPropertyValues");
			getAccessPropertyValues(doc);
		} catch (Exception e) {
			logger1.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
	}


	private void getAccessPropertyValues(Document doc) {
		
		Iterator iter = doc.get_Permissions().iterator();
		while(iter.hasNext()) {
			AccessPermission ap = (AccessPermission)iter.next();
			if(ap.get_AccessType() == AccessType.ALLOW)
				System.out.println(" ACCESS TYPE :: ALLOW");
			else if(ap.get_AccessType() == AccessType.DENY)
				System.out.println(" ACCESS TYPE :: DENY");
			else
				System.out.println(" ACCESS TYPE :: UNKNOWN");
			System.out.println("Access Mask :: "+ap.get_AccessMask());
			System.out.println("Grantee Name :: "+ap.get_GranteeName());
			
		}
		
		
	}*/

	private int parseSMTPPort(String port) {
		try {
			return Integer.parseInt(port);
		} catch (Exception e) {
			return 25;
		}
	}
}
