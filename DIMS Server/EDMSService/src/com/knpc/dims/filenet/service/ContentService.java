package com.knpc.dims.filenet.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knpc.dims.db.DBAdaptor;
import com.knpc.dims.filenet.FileNetAdaptor;
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

public class ContentService {
	
	private HttpServletRequest req;
	private HttpServletResponse resp;
	public ContentService(HttpServletRequest req, HttpServletResponse resp) {
		this.req = req;
		this.resp = resp;
	}

	public ContentService() {
	}

	public List<FolderBean> getfolderTree(String folderPath, String osName)	throws Exception {
		FileNetAdaptor adaptor = new FileNetAdaptor();
		List<FolderBean> folderList = new ArrayList<FolderBean>();
		if(adaptor.validateRequest(req, resp)) {
			folderList = adaptor.getfolderTree(folderPath, osName);
		}
		return folderList;
	}

	public String addFolder(String parentPath, String folderName, String osName) throws Exception {
		FileNetAdaptor adaptor = new FileNetAdaptor();
		String folderId = ""; 
		if(adaptor.validateRequest(req, resp)) {
			folderId = adaptor.addFolder(parentPath,folderName,osName);
		}
		return folderId;
	}

	public String deleteFolder(String folderPath, String osName) throws Exception {
		FileNetAdaptor adaptor = new FileNetAdaptor();
		String folderName = "";
		if(adaptor.validateRequest(req, resp)) {
			folderName = adaptor.deleteFolder(folderPath,osName);
		}
		return folderName;
	}

	public List<FolderBean> getSubFolders(String folderId, String osName) throws Exception {
		FileNetAdaptor adaptor = new FileNetAdaptor();
		List<FolderBean> folderList = new ArrayList<FolderBean>();
		if(adaptor.validateRequest(req, resp)) {
			folderList = adaptor.getSubFolders(folderId,osName);
		}
		return folderList;
	}

	public List<DocumentBean> getDocumentsInFolder(String objectStore,String folderId, int pageNo, int pageSize) throws Exception {
		FileNetAdaptor adaptor = new FileNetAdaptor();
		List<DocumentBean> documentsList = new ArrayList<DocumentBean>();
		if(adaptor.validateRequest(req, resp)) {
			documentsList = adaptor.getDocumentsInFolder(objectStore,folderId, pageNo, pageSize);
			documentsList = updateDepartmentName(documentsList);
		}
		return documentsList;
	}

	private List<DocumentBean> updateDepartmentName(List<DocumentBean> documentsList) {
		DBAdaptor db = new DBAdaptor();
		for(DocumentBean document: documentsList) {
			String deptId = document.getDepartment();
			if(deptId == null || deptId.length() <= 0 || deptId.equalsIgnoreCase("0"))
				continue;
			
			String deptName = db.getNameFromOldDimsDepartmentId(deptId);	
			if(deptName.length() > 0)
				document.setDepartment(deptName);			
		}

		return documentsList;
	}
	
	public boolean checkoutDocument(String docId, String objectStore) throws Exception {
		FileNetAdaptor adaptor = new FileNetAdaptor();
		boolean isCheckedOut = false;
		if(adaptor.validateRequest(req, resp)) {
			isCheckedOut = adaptor.checkoutDocument(docId,objectStore);
		}
		return isCheckedOut;
	}

	public boolean cancelCheckOut(String docId, String objectStore) throws Exception {
		FileNetAdaptor adaptor = new FileNetAdaptor();
		boolean isCheckedin = false;
		if(adaptor.validateRequest(req, resp)) {
			isCheckedin = adaptor.cancelCheckOut(docId,objectStore);
		}
		return isCheckedin;
	}

	public List<DocumentClassBean> getDocumentClasses(String objectStore) throws Exception {
		FileNetAdaptor adaptor = new FileNetAdaptor();
		List<DocumentClassBean> docClassList = new ArrayList<DocumentClassBean>();
		if(adaptor.validateRequest(req, resp)) {
			docClassList = adaptor.getDocumentClasses(objectStore);
		}
		return docClassList;
	}

	public List<PropertyBean> getPropertyDefinitions(String docClassName,String objectStoreName) throws Exception {
		FileNetAdaptor adaptor = new FileNetAdaptor();
		List<PropertyBean> propertyList = new ArrayList<PropertyBean>();
		if(adaptor.validateRequest(req, resp)) {
			propertyList = adaptor.getPropertyDefinitions(docClassName,objectStoreName);
		}
		return propertyList;
	}


	public List<VersionBean> getVersions(String docId, String objectStoreName) throws Exception {
		FileNetAdaptor adaptor = new FileNetAdaptor();
		List<VersionBean> versionList = new ArrayList<VersionBean>();
		if(adaptor.validateRequest(req, resp)) {
			versionList = adaptor.getVersions(docId,objectStoreName);
		}
		return versionList;
	}

	public List<FolderBean> getFoldersFileIn(String docId,String objectStoreName) throws Exception {
		FileNetAdaptor adaptor = new FileNetAdaptor();
		List<FolderBean> folderBeanList = new ArrayList<FolderBean>();
		if(adaptor.validateRequest(req, resp)) {
			folderBeanList = adaptor.getFoldersFileIn(docId,objectStoreName);
		}
		return folderBeanList;
	}

	public boolean foldersFileIn(String docId, String objectStoreName,String destinationFolderId) throws Exception {
		FileNetAdaptor adaptor = new FileNetAdaptor();
		boolean foldersFileIn = false;
		if(adaptor.validateRequest(req, resp)) {
			foldersFileIn = adaptor.foldersFileIn(docId,objectStoreName,destinationFolderId);
		}
		return foldersFileIn;
	}

	public boolean moveDocument(String docId, String objectStoreName,String currentFolderId, String destinationFolderId) throws Exception {
		FileNetAdaptor adaptor = new FileNetAdaptor();
		boolean moveDocument = false;
		if(adaptor.validateRequest(req, resp)) {
			moveDocument = adaptor.moveDocument(docId,objectStoreName,currentFolderId,destinationFolderId);
		}
		return moveDocument;
	}

	public DocumentDownloadBean downloadDocument(String docId,String objectStoreName) throws Exception {
		FileNetAdaptor adaptor = new FileNetAdaptor();
		DocumentDownloadBean documentDownloadBean = new DocumentDownloadBean();
		if(adaptor.validateRequest(req, resp)) {
			documentDownloadBean = adaptor.downloadDocument(docId,objectStoreName);
		}
		return documentDownloadBean;
	}

	public String getOutlookAttachment(String docId,String objectStoreName) throws Exception {
		FileNetAdaptor adaptor = new FileNetAdaptor();
		return adaptor.getOutlookAttachment(docId,objectStoreName);
	}
	
	public List<PropertyBean> getObjectPropertyValues(String docId,String objectStoreName) throws Exception {
		FileNetAdaptor adaptor = new FileNetAdaptor();
		List<PropertyBean> propertyBeanList = new ArrayList<PropertyBean>();
		if(adaptor.validateRequest(req, resp)) {
			propertyBeanList = adaptor.getObjectPropertyValues(docId,objectStoreName);
		}
		return propertyBeanList;
	}

	public String updateDocumentProperties(String jsonString) throws Exception {
		DocumentBean documentBean = null;
		ObjectMapper mapper = new ObjectMapper();
		documentBean = mapper.readValue(jsonString, new TypeReference<DocumentBean>(){});
		FileNetAdaptor adaptor = new FileNetAdaptor();
		adaptor.updateDocumentProperties(documentBean);
		return "Document updated successfully";
	}
	
	public String checkInDocument(HttpServletRequest req) throws Exception {
		
		DocumentBean documentBean = null;
		FileObject fileObject = null;
		String osName = null;
		DocumentTransferBean objectTransferBean = new DocumentTransferBean();
		ArrayList<FileObject> fileObjectList = new ArrayList<FileObject>();

		ServletFileUpload uploader = new ServletFileUpload(new DiskFileItemFactory());
	    try {
	        List<FileItem> parseRequest = uploader.parseRequest(req);
	        for (FileItem fileItem : parseRequest) {
	            if (fileItem.isFormField()) {
	            	if(fileItem.getFieldName().equalsIgnoreCase("documentJSONString")){
	            		ObjectMapper mapper = new ObjectMapper();
	            		documentBean = mapper.readValue(fileItem.getString(), new TypeReference<DocumentBean>(){});
	            	}if(fileItem.getFieldName().equalsIgnoreCase("osName")){
	            		osName = fileItem.getString();
	            	}if(fileItem.getFieldName().equalsIgnoreCase("objectId")){
	            		String objectId = fileItem.getString();
	            		objectTransferBean.setObjectId(objectId);
	            		/*ObjectMapper mapper = new ObjectMapper();
	            		objectTransferBean = mapper.readValue(fileItem.getString(), new TypeReference<DocumentTransferBean>(){});*/
	            	}
	            }else{
	            	fileObject = new FileObject();
	            	fileObject.setFileName(fileItem.getName());
	            	fileObject.setInputStream(fileItem.getInputStream());
	            	fileObjectList.add(fileObject);
	            }
	        }
	        
	        if(fileObjectList.size()>0){
		        for (int i = 0; i < fileObjectList.size(); i++) {
		        	
		        	FileNetAdaptor adaptor = new FileNetAdaptor();
		        	//DocumentBean documentBean, DocumentTransferBean documentTransferBean,String objectStore
		        	if(adaptor.validateRequest(req, resp)) {
		        		adaptor.checkIn(documentBean,objectTransferBean,fileObject,osName);
		        	}
				}
	        }
	        
	    }catch(Exception e){
	    	 e.printStackTrace();
		     throw new Exception(e.getMessage());
	    }
	    return "checked in succesfully";
	}

	public ArrayList<DocumentDownloadBean> getDocumentMimeType(String json) throws Exception{
		ArrayList<DocumentDownloadBean> documentDownloadBeanList = new ArrayList<DocumentDownloadBean>();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode node = objectMapper.readValue(json, JsonNode.class);
		
		JsonNode brandNode = node.get("osName");
		String osName = brandNode.asText();
		
		FileNetAdaptor adaptor = new FileNetAdaptor();
		JsonNode array = node.get("documentIds");
		
		if(adaptor.validateRequest(req, resp)) {
			for (int i = 0; i < array.size(); i++) {
				JsonNode jsonNode = array.get(i);
				
				DocumentDownloadBean documentDownloadBean = adaptor.getDocumentMimeType(jsonNode.asText(),osName);
				documentDownloadBeanList.add(documentDownloadBean);
			}
		}
		
		return documentDownloadBeanList;
	}

	public void sendEmail(String json) throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		EmailBean emailBean = mapper.readValue(json, new TypeReference<EmailBean>(){});
	
		FileNetAdaptor adaptor = new FileNetAdaptor();
		adaptor.sendEmail(emailBean);

	}

	public String updateDailyDocument(String json) throws Exception {
		String response = null;
		FileNetAdaptor adaptor = new FileNetAdaptor();
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode node = objectMapper.readValue(json, JsonNode.class);
		
		JsonNode brandNode = node.get("osName");
		String osName = brandNode.asText();
		
		JsonNode array = node.get("itemIds");
		
		if(adaptor.validateRequest(req, resp)) {
			for (int i = 0; i < array.size(); i++) {
				JsonNode jsonNode = array.get(i);
				response = adaptor.updateDailyDocument(jsonNode.asText(),osName);
			}
		}
		return response;
	}

	public ArrayList<DocumentDownloadBean> downloadMultiDocuments(String docIds, String osName) throws Exception{
		ArrayList<DocumentDownloadBean> documentDownloadBeanList = new ArrayList<DocumentDownloadBean>();
		FileNetAdaptor adaptor = new FileNetAdaptor();
		
		String docId[] = docIds.split(",");
		
		if(adaptor.validateRequest(req, resp)) {
			for (int i = 0; i < docId.length; i++) {
				String id = docId[i];
				if(id.length() <= 0)
					continue;
				DocumentDownloadBean documentDownloadBean = adaptor.downloadDocument(id,osName);
				documentDownloadBeanList.add(documentDownloadBean);
			}
		}
		return documentDownloadBeanList;
	}

	public List<HashMap<String, Object>> documentsScanned(int department, String division,String user, String from, String to) throws Exception {
		
		FileNetAdaptor adaptor = new FileNetAdaptor();
		List<HashMap<String, Object>> map = adaptor.documentsScanned(department,division,user,from,to);
		return map;
	}

	/*public void getObjectAccessValues(String docId,	String objectStoreName) throws Exception {
		FileNetAdaptor adaptor = new FileNetAdaptor();
		List<PropertyBean> propertyBeanList = new ArrayList<PropertyBean>();
		if(adaptor.validateRequest(req, resp)) {
			adaptor.getObjectAccessValues(docId,objectStoreName);
		}
	}*/

	
}
