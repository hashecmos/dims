package com.knpc.dims.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import jxl.CellType;
import jxl.CellView;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.knpc.dims.db.DBAdaptor;
import com.knpc.dims.filenet.beans.ContaineeBean;
import com.knpc.dims.filenet.beans.DocumentBean;
import com.knpc.dims.filenet.beans.DocumentClassBean;
import com.knpc.dims.filenet.beans.DocumentDownloadBean;
import com.knpc.dims.filenet.beans.FolderBean;
import com.knpc.dims.filenet.beans.PropertyBean;
import com.knpc.dims.filenet.beans.VersionBean;
import com.knpc.dims.filenet.service.ContentService;
import com.knpc.dims.filenet.util.Utils;
import com.knpc.dims.pdf.headerfooter.HeaderFooterPageEvent;
import com.knpc.dims.response.ResponseObject;

@Path("/FilenetService")
@ApplicationPath("resources")
public class ContentController {
	
	// http://localhost:9080/DIMS/resources/FilenetService/getfolderTree?folderPath=/&osName=ECM
	@GET
	@Path("/getfolderTree")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getfolderTree( @QueryParam("folderPath") String folderPath,@QueryParam("osName") String osName,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		List<FolderBean> folderList = null;
		try {
			ContentService cs = new ContentService(req, resp);
			folderList = cs.getfolderTree(folderPath, osName);
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(folderList).build();
	 }
	
	// http://localhost:9080/DIMS/resources/FilenetService/addFolder?parentPath=/&folderName=Test&osName=ECM
	@GET
	@Path("/addFolder")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)	
	public Response addFolder(@QueryParam("parentPath") String parentPath,@QueryParam("folderName") String folderName,@QueryParam("osName") String osName,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		String folderId =null;
		try {
			ContentService cs = new ContentService(req,resp);
			folderId = cs.addFolder(parentPath,folderName,osName);
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			if(e.getMessage().contains("already exists. Please enter a different name"))
				return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
			else
				return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(folderId).build();
	}
	
	// http://localhost:9080/DIMS/resources/FilenetService/deleteFolder?parentPath=/&osName=ECM
	@GET
	@Path("/deleteFolder")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)	
	public Response deleteFolder(@QueryParam("folderPath") String folderPath,@QueryParam("osName") String osName,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		String folderName = null;
		try{
			ContentService cs = new ContentService(req,resp);
			folderName = cs.deleteFolder(folderPath,osName);
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(folderName).build();
	}
	
	// http://localhost:9080/DIMS/resources/FilenetService/getContainees?osName=ECM&folderId=
	@GET
	@Path("/getContainees")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)	
	public Response getContainees(@QueryParam("folderId") String folderId, @QueryParam("osName") String osName,
			@QueryParam("pageNo") String pageNo, @QueryParam("pageSize") String pageSize,
			@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception {
		ContaineeBean containees = null;
		try{
			ContentService cs = new ContentService(req,resp);

			List<FolderBean> folders = cs.getSubFolders(folderId,osName);
			List<DocumentBean> documents = cs.getDocumentsInFolder(osName,folderId,
					Utils.convertStringToInt(pageNo, 1), Utils.convertStringToInt(pageSize, 100));

			containees = new ContaineeBean() ;
			containees.setFolders( folders ) ;
			containees.setDocuments( documents ) ;						
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(containees).build();
	}
	
	// http://localhost:9080/DIMS/resources/FilenetService/getSubFolder?parentFolder=/&osName=ECM
	@GET
	@Path("/getSubFolder")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)	
	public Response getSubFolders(@QueryParam("folderId") String folderId, @QueryParam("osName") String osName,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception {
		List<FolderBean> folderList =null;
		try{
			ContentService cs = new ContentService(req,resp);
			folderList = cs.getSubFolders(folderId,osName);
		} catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(folderList).build();
	}
	
	// http://localhost:9080/DIMS/resources/FilenetService/getDocumentsInFolder?osName=ECM&folderId=Test
	@GET
	@Path("/getDocumentsInFolder")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)	
	public Response getDocumentsInFolder(@QueryParam("osName")String osName,@QueryParam("folderId")String folderId,
			@QueryParam("pageNo") String pageNo, @QueryParam("pageSize") String pageSize,
			@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		List<DocumentBean> documentsList = null;
		try{
			ContentService cs = new ContentService(req,resp);
			documentsList = cs.getDocumentsInFolder(osName,folderId,
					Utils.convertStringToInt(pageNo, 1), Utils.convertStringToInt(pageSize, 100));
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(documentsList).build();
	}
	
	// http://localhost:9080/DIMS/resources/FilenetService/checkoutDocument?docId={CC8576BF-CD15-45D6-816C-951509E4C1A2}&osName=ECM
	@GET
	@Path("/checkoutDocument")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)	
	public Response checkoutDocument(@QueryParam("docId") String docId, @QueryParam("osName")String objectStore,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception {
		boolean isCheckedOut =false;
		try{
			ContentService cs = new ContentService(req,resp);
			isCheckedOut = cs.checkoutDocument(docId,objectStore);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(isCheckedOut).build();
	}
	
	// http://localhost:9080/DIMS/resources/FilenetService/cancelCheckoutDocument?docId={CC8576BF-CD15-45D6-816C-951509E4C1A2}&osName=ECM
	@GET
	@Path("/cancelCheckoutDocument")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)	
	public Response cancelCheckOut(@QueryParam("docId") String docId, @QueryParam("osName") String objectStore,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		boolean isCheckedin = false;
		try{
			ContentService cs = new ContentService(req,resp);
			isCheckedin = cs.cancelCheckOut(docId,objectStore);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(isCheckedin).build();
	}
	
	// http://localhost:9080/DIMS/resources/FilenetService/getDocumentClasses?osName=ECM
	@GET
	@Path("/getDocumentClasses")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)		
	public Response getDocumentClasses(@QueryParam("osName") String objectStore,@Context HttpServletRequest req,@Context HttpServletResponse resp)	throws Exception {
		List<DocumentClassBean> docClassList = null;
		try{
			ContentService cs = new ContentService(req,resp);
			docClassList = cs.getDocumentClasses(objectStore);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(docClassList).build();
	}
	
	// http://localhost:9080/DIMS/resources/FilenetService/getPropertyDefinitions?docClassName=DBS_Project&osName=ECM
	@GET
	@Path("/getPropertyDefinitions")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)
	public Response getPropertyDefinitions(@QueryParam("docClassName") String docClassName,@QueryParam("osName") String objectStoreName,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		List<PropertyBean> propertyList = null;
		try{
			ContentService cs = new ContentService(req,resp);
			propertyList = cs.getPropertyDefinitions(docClassName,objectStoreName);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(propertyList).build();
	}
	
	// http://localhost:9080/DIMS/resources/FilenetService/getVersions?docId={3175DF62-D4AC-C3A1-8610-5A2553800000}&osName=ECM
	@GET
	@Path("/getVersions")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)
	public Response getVersions(@QueryParam("docId") String docId,@QueryParam("osName") String objectStoreName,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		List<VersionBean> versionList = null;
		try{
			ContentService cs = new ContentService(req,resp);
			versionList = cs.getVersions(docId,objectStoreName);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(versionList).build();
	}
	
	// http://localhost:9080/DIMS/resources/FilenetService/getFoldersFileIn?docId={3175DF62-D4AC-C3A1-8610-5A2553800000}&osName=ECM
	@GET
	@Path("/getFoldersFileIn")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)
	public Response getFoldersFileIn(@QueryParam("docId") String docId,@QueryParam("osName") String objectStoreName,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		List<FolderBean> folderBeanList = null;
		try{
			ContentService cs = new ContentService(req,resp);
			folderBeanList = cs.getFoldersFileIn(docId,objectStoreName);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(folderBeanList).build();
	}

	// http://localhost:9080/DIMS/resources/FilenetService/foldersFileIn?docId={3175DF62-D4AC-C3A1-8610-5A2553800000}&osName=ECM&destinationFolderId={202B3AEA-D419-45A8-8197-1324333C2F02}
	@GET
	@Path("/foldersFileIn")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)
	public Response foldersFileIn(@QueryParam("docId") String docId,@QueryParam("osName") String objectStoreName,@QueryParam("destinationFolderId") String destinationFolderId,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		boolean foldersFileIn = false;
		try{
			ContentService cs = new ContentService(req,resp);
			foldersFileIn = cs.foldersFileIn(docId,objectStoreName,destinationFolderId);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(foldersFileIn).build();
	}

	// http://localhost:9080/DIMS/resources/FilenetService/moveDocument?docId={3175DF62-D4AC-C3A1-8610-5A2553800000}&osName=ECM&destinationFolderId={69B0BD07-CA33-4BDA-80FD-C70BF256588E}&currentFolderId={202B3AEA-D419-45A8-8197-1324333C2F02}
	@GET
	@Path("/moveDocument")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)
	public Response moveDocument(@QueryParam("docId") String docId,@QueryParam("osName") String objectStoreName,@QueryParam("destinationFolderId") String destinationFolderId,@QueryParam("currentFolderId") String currentFolderId,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		boolean foldersFileIn = false;
		String responseMessage ="";
		try{
			ContentService cs = new ContentService(req,resp);
			foldersFileIn = cs.moveDocument(docId,objectStoreName,currentFolderId,destinationFolderId);
			if(foldersFileIn){
				responseMessage =" The selected document has been moved successfully";
			}else{
				responseMessage =" The selected document failed moved";
			}
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(responseMessage).build();
	}
	
	// http://localhost:9080/DIMS/resources/FilenetService/downloadDocument?docId={47AF833A-68BB-C7F3-87B9-5A0191B00000}&osName=ECM
	@GET
	@Path("/downloadDocument")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)//@Produces(MediaType.APPLICATION_XML)
	public Response downloadDocument(@QueryParam("docId") String docId,@QueryParam("osName") String objectStoreName,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		InputStream inputStream = null;
		String fileName = null;
		try{
			ContentService cs = new ContentService(req,resp);
			DocumentDownloadBean documentDownloadBean = cs.downloadDocument(docId,objectStoreName);
			fileName = documentDownloadBean.getFileName() ;
			if(fileName!=null){
				String ext = fileName.substring(fileName.indexOf('.')+1, fileName.length());
				fileName = fileName.substring(0, fileName.indexOf('.')+1) + ext.toUpperCase();
				inputStream = documentDownloadBean.getInputStream() ;
			}
			
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		if(fileName !=null){
		return Response.ok(inputStream, MediaType.APPLICATION_OCTET_STREAM)
		        .header("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(fileName,"UTF-8").replaceAll("\\+"," ")  + "\"")
		        .build();
		
		}else{
			return Response.ok(inputStream, MediaType.APPLICATION_OCTET_STREAM).build();
		}
	}
	
	
	// http://localhost:9080/DIMS/resources/FilenetService/viewDocument?docId={47AF833A-68BB-C7F3-87B9-5A0191B00000}&osName=ECM
	@GET
	@Path("/viewDocument")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)
	public Response viewDocument(@QueryParam("docId") String docId,@QueryParam("osName") String objectStoreName,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{

		InputStream inputStream = null;
		String fileName = null;
		try{
			ContentService cs = new ContentService(req,resp);
			DocumentDownloadBean documentDownloadBean = cs.downloadDocument(docId,objectStoreName);
			fileName = documentDownloadBean.getFileName() ;
			String ext = fileName.substring(fileName.indexOf('.')+1, fileName.length());
			fileName = fileName.substring(0, fileName.indexOf('.')+1) + ext.toUpperCase();
			inputStream = documentDownloadBean.getInputStream() ;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok(inputStream, MediaType.APPLICATION_OCTET_STREAM)
		        .header("Content-Disposition", "inline; filename=\"" + URLEncoder.encode(fileName,"UTF-8").replaceAll("\\+"," ")  + "\"")
		        .build();
	
	}
	
	// http://localhost:9080/DIMS/resources/FilenetService/getOutlookAttachment?docIds={3175DF62-D4AC-C3A1-8610-5A2553800000}&osName=ECM
	@GET
	@Path("/getOutlookAttachment")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)
	public Response getOutlookAttachment(@QueryParam("docIds") String docIds,@QueryParam("osName") String objectStoreName,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		String fileName = null;
		try{
			ContentService cs = new ContentService(req,resp);
			fileName = getOutlookAttachmentFile(docIds,objectStoreName, req, resp);
			if((fileName != null) && (fileName.trim().length() > 4) && (fileName.substring(0, 4).equalsIgnoreCase("http")))
				fileName = fileName.replace('\\',  '/');
			System.out.println("Outlook file name: " + fileName);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(fileName).build();
	}
	
	private String getOutlookAttachmentFile(String docIds, String osName, 
			HttpServletRequest req, HttpServletResponse resp) throws Exception {

		ArrayList<String> dList = new ArrayList<String>();
		try {
			String [] ndocIdList = docIds.split(",");
			for(String did: ndocIdList) {
				if(did.trim().length() > 0)
					dList.add(did.trim());
			}
		} catch (Exception e) {
		}
		if(dList.size() == 1) {
			ContentService cs = new ContentService(req, resp);
			return cs.getOutlookAttachment(dList.get(0),osName);
		}
		
		ArrayList<String> outFiles = Utils.getOutlookFolders();
		String zipFile = outFiles.get(0);
		String outFileName = outFiles.get(1);
		
		String zipFileName = null;
		int length = 0 ;
		try{
			ContentService cs = new ContentService(req,resp);
			List<DocumentDownloadBean>  documentDownloadBeanList = cs.downloadMultiDocuments(docIds,osName);
			
			
			if(Calendar.getInstance().get(Calendar.AM_PM) == 1){
				zipFileName = (Calendar.getInstance().get(Calendar.MONTH)+1)+"_"+Calendar.getInstance().get(Calendar.DATE)+
						"_"+Calendar.getInstance().get(Calendar.YEAR)+"_"+
						Calendar.getInstance().get(Calendar.HOUR)+"_"+Calendar.getInstance().get(Calendar.MINUTE)+"_"+
						Calendar.getInstance().get(Calendar.SECOND)+"_PM";
			}else{
				zipFileName = (Calendar.getInstance().get(Calendar.MONTH)+1)+"_"+Calendar.getInstance().get(Calendar.DATE)+
						"_"+Calendar.getInstance().get(Calendar.YEAR)+"_"+
						Calendar.getInstance().get(Calendar.HOUR)+"_"+Calendar.getInstance().get(Calendar.MINUTE)+"_"+
						Calendar.getInstance().get(Calendar.SECOND)+"_AM";
			}
			FileOutputStream fos = new FileOutputStream(zipFile+zipFileName +".zip");
			outFileName += (zipFileName +".zip");
			
			ZipOutputStream zip = new ZipOutputStream(fos);

			List<String> fileNamesList = new ArrayList<String>() ;			
			int sameFileNameCount = 0 ;
			InputStream inputStream = null;
			for (int i = 0; i < documentDownloadBeanList.size(); i++) {

				String fileName = documentDownloadBeanList.get(i).getFileName() ;

				String fileNameToBeDownloaded = fileName ;
				String extension = "" ;
				int indexOfDotForFileName = fileName.lastIndexOf('.');				
				if (indexOfDotForFileName > 0)
				{					
					fileNameToBeDownloaded = fileNameToBeDownloaded.substring(0, indexOfDotForFileName) ;
					extension = fileName.substring( indexOfDotForFileName ) ;
				}

				if( fileNamesList.contains( fileName ) ){
					sameFileNameCount++ ;
					fileName = fileNameToBeDownloaded + "(" + sameFileNameCount + ")" + extension ;
				}

				fileNamesList.add( fileName ) ;

				inputStream = documentDownloadBeanList.get(i).getInputStream() ;
				zip.putNextEntry(new ZipEntry( fileName ) ) ; 


				byte[] bbuf = new byte[1024];  
				while ( ( inputStream != null ) && ( ( length = inputStream.read( bbuf ) ) != -1 ) ) {
					zip.write( bbuf, 0, length );  
				}  

				zip.closeEntry();
				inputStream.close();  
			}

			zip.flush();  
			zip.close(); 
			
			fos.flush(); 
			fos.close();
		} catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
			throw e;
		}	
		return outFileName;
	}
	
	// http://localhost:9080/DIMS/resources/FilenetService/getObjectPropertyValues?docId={3175DF62-D4AC-C3A1-8610-5A2553800000}&osName=ECM
	@GET
	@Path("/getObjectPropertyValues")
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)
	public Response getObjectPropertyValues(@QueryParam("docId") String docId,@QueryParam("osName") String objectStoreName,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		List<PropertyBean> propertyBeanList = null;
		try{
			ContentService cs = new ContentService(req,resp);
			propertyBeanList = cs.getObjectPropertyValues(docId,objectStoreName);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(propertyBeanList).build();
	}
	
	// http://localhost:9080/DIMS/resources/FilenetService/checkInDocument
	@POST
	@Path("/checkInDocument")
	@Produces(MediaType.MULTIPART_FORM_DATA)//@Produces(MediaType.APPLICATION_XML)
	public Response checkInDocument(@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		String response = null;
		try{
			ContentService cs = new ContentService(req,resp);
			response = cs.checkInDocument(req);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(response).build();
	}
	
	// http://localhost:9080/DIMS/resources/FilenetService/updateDocumentProperties
	@POST
	@Path("/updateDocumentProperties")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)
	public Response updateDocumentProperties(String json,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		try{
			ContentService cs = new ContentService(req,resp);
			cs.updateDocumentProperties(json);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity("Document modified successfully").build();
	}
		
	// http://localhost:9080/DIMS/resources/FilenetService/getDocumentMimeType
	@POST
	@Path("/getDocumentMimeType")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)
	public Response getDocumentMimeType(String json,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		ArrayList<DocumentDownloadBean> documentDownloadBeanList = null;
		try{
			ContentService cs = new ContentService(req,resp);
			documentDownloadBeanList = cs.getDocumentMimeType(json);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(documentDownloadBeanList).build();
	}
	
	//http://localhost:9080/DIMS/resources/FilenetService/emailDocument
	@POST
	@Path("/emailDocument")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendEmail(String json,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		try{
			ContentService cs = new ContentService(req,resp);
			cs.sendEmail(json);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity("mail sent successfully").build();
	}
	
	
	// http://localhost:9080/DIMS/resources/FilenetService/updateDailyDocument?docId={3175DF62-D4AC-C3A1-8610-5A2553800000}&osName=ECM
	@POST
	@Path("/updateDailyDocument")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)
	public Response updateDailyDocument(String json,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		//List<PropertyBean> propertyBeanList = null;
		String response = null;
		try{
			ContentService cs = new ContentService(req,resp);
			response = cs.updateDailyDocument(json);
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(response).build();
	}
	
	//downloadMultiDocuments
	
	// http://localhost:9080/DIMS/resources/FilenetService/downloadMultiDocuments
	@GET
	@Path("/downloadMultiDocuments")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)//@Produces(MediaType.APPLICATION_XML)
	public Response downloadMultiDocuments(@QueryParam("docIds") String docIds,@QueryParam("osName") String osName,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
	{
		ArrayList<String> dList = new ArrayList<String>();
		try {
			String [] ndocIdList = docIds.split(",");
			for(String did: ndocIdList) {
				if(did.trim().length() > 0)
					dList.add(did.trim());
			}
		} catch (Exception e) {
		}
		if(dList.size() == 1) {
			return downloadDocument(dList.get(0), osName, req, resp);
		}
		
		String javaTemp = System.getProperty("java.io.tmpdir");
		if(javaTemp == null)
			javaTemp = "C:\\Temp";
		//File theDir = new File("C:/Temp/DIMS/Temp");
		File theDir = new File(javaTemp);
		String zipFile = "";
		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    zipFile = theDir.getAbsolutePath();
		    boolean result = false;

		    try{
		        theDir.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		        //handle it
		    }        
		    if(result) {    
		    }
		}else{
			zipFile = theDir.getAbsolutePath();
			
		}
		
		String zipFileName = null;
		int length = 0 ;
		try{
			ContentService cs = new ContentService(req,resp);
			List<DocumentDownloadBean>  documentDownloadBeanList = cs.downloadMultiDocuments(docIds,osName);
			
			
			if(Calendar.getInstance().get(Calendar.AM_PM) == 1){
				zipFileName = (Calendar.getInstance().get(Calendar.MONTH)+1)+"_"+Calendar.getInstance().get(Calendar.DATE)+
						"_"+Calendar.getInstance().get(Calendar.YEAR)+"_"+
						Calendar.getInstance().get(Calendar.HOUR)+"_"+Calendar.getInstance().get(Calendar.MINUTE)+"_"+
						Calendar.getInstance().get(Calendar.SECOND)+"_PM";
			}else{
				zipFileName = (Calendar.getInstance().get(Calendar.MONTH)+1)+"_"+Calendar.getInstance().get(Calendar.DATE)+
						"_"+Calendar.getInstance().get(Calendar.YEAR)+"_"+
						Calendar.getInstance().get(Calendar.HOUR)+"_"+Calendar.getInstance().get(Calendar.MINUTE)+"_"+
						Calendar.getInstance().get(Calendar.SECOND)+"_AM";
			}
			FileOutputStream fos = new FileOutputStream(zipFile+"\\"+zipFileName +".zip");

			ZipOutputStream zip = new ZipOutputStream(fos);

			List<String> fileNamesList = new ArrayList<String>() ;			
			int sameFileNameCount = 0 ;
			InputStream inputStream = null;
			for (int i = 0; i < documentDownloadBeanList.size(); i++) {

				String fileName = documentDownloadBeanList.get(i).getFileName() ;

				String fileNameToBeDownloaded = fileName ;
				String extension = "" ;
				int indexOfDotForFileName = fileName.lastIndexOf('.');				
				if (indexOfDotForFileName > 0)
				{					
					fileNameToBeDownloaded = fileNameToBeDownloaded.substring(0, indexOfDotForFileName) ;
					extension = fileName.substring( indexOfDotForFileName ) ;
				}

				if( fileNamesList.contains( fileName ) ){
					sameFileNameCount++ ;
					fileName = fileNameToBeDownloaded + "(" + sameFileNameCount + ")" + extension ;
				}

				fileNamesList.add( fileName ) ;

				inputStream = documentDownloadBeanList.get(i).getInputStream() ;
				zip.putNextEntry(new ZipEntry( fileName ) ) ; 


				byte[] bbuf = new byte[1024];  
				while ( ( inputStream != null ) && ( ( length = inputStream.read( bbuf ) ) != -1 ) ) {
					zip.write( bbuf, 0, length );  
				}  

				zip.closeEntry();
				inputStream.close();  
			}

			zip.flush();  
			zip.close(); 
			
			fos.flush(); 
			fos.close();
			InputStream targetStream = new FileInputStream(zipFile+"\\"+zipFileName +".zip");
			return Response.ok(targetStream, MediaType.APPLICATION_OCTET_STREAM)
			        .header("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(zipFileName +".zip","UTF-8").replaceAll("\\+"," ")  + "\"")
			        .build();
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		
		
	}
	
	
	public static void addToZipFile(String fileName, ZipOutputStream zos, InputStream inputStream) throws FileNotFoundException, IOException {

		  ZipEntry zipEntry = new ZipEntry(fileName);
		  zos.putNextEntry(zipEntry);

		  byte[] bytes = new byte[1024];
		  int length;
		  while ((length = inputStream.read(bytes)) >= 0) {
		   zos.write(bytes, 0, length);
		  }

		  zos.closeEntry();
		  inputStream.close();
		 }

	@GET
	@Path("/documentsScannedReport")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void documentsScannedReport(@QueryParam("dpt") String department, @QueryParam("div") String division, @QueryParam("user") String user, @QueryParam("from") String from,@QueryParam("to") String to, @QueryParam("exportType") String exportType, @Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception {
		String fileExtension = null;
		OutputStream os = resp.getOutputStream();
		ByteArrayOutputStream baos = null;

		try{
			ContentService cs = new ContentService();
			DBAdaptor db = new DBAdaptor();
			int oldDepartmentId = db.getOldDimsDepartmentId(department);
			List<HashMap<String, Object>> list = cs.documentsScanned(oldDepartmentId,division,user,from,to);

			if(exportType.equalsIgnoreCase("PDF")) {
				fileExtension = "pdf";
				Document doc = null;
				Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD, new BaseColor(0, 0, 0));
				Font bfBoldRed = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.RED);
				Font bfNormal = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, new BaseColor(0, 0, 0));
				Font font = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
				Paragraph paragraph = new Paragraph();
				doc = new Document(PageSize.A4.rotate());
				
				baos = new ByteArrayOutputStream();
				/*PdfWriter.getInstance(doc, baos);
				doc.open();*/
				
				PdfWriter writer = PdfWriter.getInstance(doc, baos);

		        // add header and footer
		        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
		        writer.setPageEvent(event);
				doc.open();
				PdfPTable table = new PdfPTable(12);
				table.setWidthPercentage(100f);

				Image image = Image.getInstance(req.getSession().getServletContext().getRealPath("images")+"\\logo-new.png");
				image.setAlignment(Image.LEFT);
				paragraph.add(new Chunk(image, 0, 0, true));
				PdfPCell cell = new PdfPCell();
				cell.addElement(paragraph);
				PdfPTable table1 = new PdfPTable(5);		
				table1.addCell(cell);
				table1.setLockedWidth(true);
				String departmentName = new DBAdaptor().getDepartmentName(Long.valueOf(department).longValue());
				if(departmentName==null){
					departmentName="";
				}
				PdfPCell hC1 = insertCell1(table, "Dept Name : "+departmentName, Element.ALIGN_LEFT, 12, bfBold12);
				//PdfPCell hC2 = insertCell1(table, "Last Snapshoot has been taken on:", Element.ALIGN_LEFT, 11, bfBold12);

				table.addCell(hC1);
				//table.addCell(hC2);	

				PdfPCell hC6 = insertCell1(table, "Total Docs Added : "+list.size(), Element.ALIGN_LEFT, 12, bfBold12);
				PdfPCell hC7 = insertCell1(table, "Through the period : "+from+" To "+ to, Element.ALIGN_RIGHT, 6, bfBold12);
				PdfPCell hC35 = insertCell1(table, "", Element.ALIGN_RIGHT, 8, bfBold12);
				PdfPCell hC8 = insertCell1(table, ""+list.size(), Element.ALIGN_RIGHT, 1, bfBoldRed);
				String div ="";
				if(division.equalsIgnoreCase("-1")){
					
					div = "All Division";
				}else{
					div = new DBAdaptor().getDivisionsName(division);
				}
				PdfPCell hC34 = insertCell1(table, " | "+div, Element.ALIGN_LEFT, 12, bfBold12);
				PdfPCell hC9 = insertCell1(table, "Docs", Element.ALIGN_RIGHT, 1, bfNormal);
				PdfPCell hC33 = insertCell1(table, "", Element.ALIGN_RIGHT, 12, bfBold12);

				table.addCell(hC6);
				table.addCell(hC7);
				table.addCell(hC35);
				table.addCell(hC8);	
				table.addCell(hC34);
				table.addCell(hC9);
				table.addCell(hC33);	
				HashMap<String, String> documentCount = new HashMap<String, String>();
				for (int j = 0; j < user.split(",").length; j++) {
					int count =0;
					String userFullName = DBAdaptor.getUserFullName(user.split(",")[j]);
					for (int i = 0; i < list.size(); i++) {
						HashMap<String, Object> mapObj = list.get(i);
						if(mapObj.get("Creator") != null && user.split(",")[j].equalsIgnoreCase(mapObj.get("Creator").toString())) {
							count++;
						}
					}
					documentCount.put(userFullName, Integer.toString(count));
				}
				for (int j = 0; j < user.split(",").length; j++) {
					String userFullName = DBAdaptor.getUserFullName(user.split(",")[j]);
					PdfPCell hC32 = insertCell(table,userFullName, Element.ALIGN_LEFT, 11, font);
					PdfPCell hC32eee = insertCell(table,documentCount.get(userFullName), Element.ALIGN_LEFT, 1, font);
					hC32.setBackgroundColor(new BaseColor(128, 128, 128));
					table.addCell(hC32);
					hC32eee.setBackgroundColor(new BaseColor(128, 128, 128));
					table.addCell(hC32eee);
					
					PdfPCell hC00 = insertCell(table, "Date Created", Element.ALIGN_LEFT, 1, bfBold12);					
					PdfPCell hC10 = insertCell(table, "Launched", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC11 = insertCell(table, "Ref No", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC12 = insertCell(table, "Subject", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC13 = insertCell(table, "ID", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC14 = insertCell(table, "Size", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC15 = insertCell(table, "Document Type", Element.ALIGN_LEFT, 1, bfBold12);						
					PdfPCell hC16 = insertCell(table, "Correspondence Type", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC17 = insertCell(table, "Document Date", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC18 = insertCell(table, "Date Received", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC19 = insertCell(table, "Document From", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC20 = insertCell(table, "Confidential", Element.ALIGN_LEFT, 1, bfBold12);

					hC00.setBackgroundColor(new BaseColor(128, 128, 128));
					hC10.setBackgroundColor(new BaseColor(128, 128, 128));
					hC11.setBackgroundColor(new BaseColor(128, 128, 128));
					hC12.setBackgroundColor(new BaseColor(128, 128, 128));
					hC13.setBackgroundColor(new BaseColor(128, 128, 128));
					hC14.setBackgroundColor(new BaseColor(128, 128, 128));
					hC15.setBackgroundColor(new BaseColor(128, 128, 128));
					hC16.setBackgroundColor(new BaseColor(128, 128, 128));
					hC17.setBackgroundColor(new BaseColor(128, 128, 128));
					hC18.setBackgroundColor(new BaseColor(128, 128, 128));
					hC19.setBackgroundColor(new BaseColor(128, 128, 128));
					hC20.setBackgroundColor(new BaseColor(128, 128, 128));

					table.addCell(hC00);
					table.addCell(hC10);
					table.addCell(hC11);
					table.addCell(hC12);
					table.addCell(hC13);
					table.addCell(hC14);
					table.addCell(hC15);
					table.addCell(hC16);
					table.addCell(hC17);
					table.addCell(hC18);
					table.addCell(hC19);
					table.addCell(hC20);
					
					for (int i = 0; i < list.size(); i++) {

						HashMap<String, Object> mapObj = list.get(i);

						if(mapObj.get("Creator") != null && user.split(",")[j].equalsIgnoreCase(mapObj.get("Creator").toString())) {
							
							if(i % 2 == 0) {
								
								
								PdfPCell hC200 = insertCell(table, ""+mapObj.get("dateCreated"), Element.ALIGN_LEFT, 1, bfBold12);
								PdfPCell hC21 = insertCell(table, ""+mapObj.get("IsLaunched"), Element.ALIGN_LEFT, 1, bfBold12);
								PdfPCell hC22 = insertCell(table, ""+mapObj.get("ReferenceNo"), Element.ALIGN_LEFT, 1, bfBold12);
								boolean isArabicDocTitle = isArabicText(mapObj.get("DocumentTitle").toString());
								
								PdfPCell hC23 = insertCell(table, ""+mapObj.get("DocumentTitle"), Element.ALIGN_LEFT, 1, bfBold12);
								if(isArabicDocTitle){
									hC23.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
									hC23.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
								}else{
									hC23.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
								}
								PdfPCell hC24 = insertCell(table, ""+mapObj.get("DocumentID"), Element.ALIGN_LEFT, 1, bfBold12);
								PdfPCell hC25 = insertCell(table, ""+mapObj.get("ContentSize"), Element.ALIGN_LEFT, 1, bfBold12);
								int docType = (Integer)mapObj.get("DocumentType");
								String docTypeName = new DBAdaptor().getDocTypeName(docType);
								PdfPCell hC26 = insertCell(table, docTypeName, Element.ALIGN_LEFT, 1, bfBold12);						
								PdfPCell hC27 = insertCell(table, ""+mapObj.get("CorrespondenceType"), Element.ALIGN_LEFT, 1, bfBold12);
								PdfPCell hC28 = insertCell(table, ""+mapObj.get("DocumentDate"), Element.ALIGN_LEFT, 1, bfBold12);
								PdfPCell hC29 = insertCell(table, ""+mapObj.get("DateReceived"), Element.ALIGN_LEFT, 1, bfBold12);
								int docFrom = (Integer)mapObj.get("DocumentFrom");
								String documentFrom = new DBAdaptor().getDocumentSiteName(docFrom);
								boolean isArabicDocFrom = isArabicText(documentFrom);
								PdfPCell hC30 = insertCell(table, ""+documentFrom, Element.ALIGN_LEFT, 1, bfBold12);
								if(isArabicDocFrom){
									hC30.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
									hC30.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
								}else{
									hC30.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
								}
								PdfPCell hC31 = insertCell(table, ""+mapObj.get("Confidentiality"), Element.ALIGN_LEFT, 1, bfBold12);

								table.addCell(hC200);
								table.addCell(hC21);
								table.addCell(hC22);
								table.addCell(hC23);
								table.addCell(hC24);
								table.addCell(hC25);
								table.addCell(hC26);
								table.addCell(hC27);
								table.addCell(hC28);
								table.addCell(hC29);
								table.addCell(hC30);
								table.addCell(hC31);
							}
							else {
								PdfPCell hC200 = insertCell(table, ""+mapObj.get("dateCreated"), Element.ALIGN_LEFT, 1, bfBold12);
								PdfPCell hC21 = insertCell(table, ""+mapObj.get("IsLaunched"), Element.ALIGN_LEFT, 1, bfBold12);
								PdfPCell hC22 = insertCell(table, ""+mapObj.get("ReferenceNo"), Element.ALIGN_LEFT, 1, bfBold12);
								boolean isArabicDocTitle = isArabicText(mapObj.get("DocumentTitle").toString());
								
								PdfPCell hC23 = insertCell(table, ""+mapObj.get("DocumentTitle"), Element.ALIGN_LEFT, 1, bfBold12);
								if(isArabicDocTitle){
									hC23.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
									hC23.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
								}else{
									hC23.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
								}
								PdfPCell hC24 = insertCell(table, ""+mapObj.get("DocumentID"), Element.ALIGN_LEFT, 1, bfBold12);
								PdfPCell hC25 = insertCell(table, ""+mapObj.get("ContentSize"), Element.ALIGN_LEFT, 1, bfBold12);
								int docType = (Integer)mapObj.get("DocumentType");
								String docTypeName = new DBAdaptor().getDocTypeName(docType);
								PdfPCell hC26 = insertCell(table, docTypeName, Element.ALIGN_LEFT, 1, bfBold12);						
								PdfPCell hC27 = insertCell(table, ""+mapObj.get("CorrespondenceType"), Element.ALIGN_LEFT, 1, bfBold12);
								PdfPCell hC28 = insertCell(table, ""+mapObj.get("DocumentDate"), Element.ALIGN_LEFT, 1, bfBold12);
								PdfPCell hC29 = insertCell(table, ""+mapObj.get("DateReceived"), Element.ALIGN_LEFT, 1, bfBold12);
								int docFrom = (Integer)mapObj.get("DocumentFrom");
								String documentFrom = new DBAdaptor().getDocumentSiteName(docFrom);
								boolean isArabicDocFrom = isArabicText(documentFrom);
								PdfPCell hC30 = insertCell(table, ""+documentFrom, Element.ALIGN_LEFT, 1, bfBold12);
								if(isArabicDocFrom){
									hC30.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
									hC30.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
								}else{
									hC30.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
								}
								PdfPCell hC31 = insertCell(table, ""+mapObj.get("Confidentiality"), Element.ALIGN_LEFT, 1, bfBold12);
								
								hC200.setBackgroundColor(new BaseColor(245, 245, 245));
								hC21.setBackgroundColor(new BaseColor(245, 245, 245));
								hC22.setBackgroundColor(new BaseColor(245, 245, 245));
								hC23.setBackgroundColor(new BaseColor(245, 245, 245));
								hC24.setBackgroundColor(new BaseColor(245, 245, 245));
								hC25.setBackgroundColor(new BaseColor(245, 245, 245));
								hC26.setBackgroundColor(new BaseColor(245, 245, 245));
								hC27.setBackgroundColor(new BaseColor(245, 245, 245));
								hC28.setBackgroundColor(new BaseColor(245, 245, 245));
								hC29.setBackgroundColor(new BaseColor(245, 245, 245));
								hC30.setBackgroundColor(new BaseColor(245, 245, 245));
								hC31.setBackgroundColor(new BaseColor(245, 245, 245));
								
								table.addCell(hC200);
								table.addCell(hC21);
								table.addCell(hC22);
								table.addCell(hC23);
								table.addCell(hC24);
								table.addCell(hC25);
								table.addCell(hC26);
								table.addCell(hC27);
								table.addCell(hC28);
								table.addCell(hC29);
								table.addCell(hC30);
								table.addCell(hC31);

							}
						}
					}
					PdfPCell hC61 = insertCell1(table, "", Element.ALIGN_LEFT, 12, bfBold12);
					/*PdfPCell hC71 = insertCell1(table, "", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC81 = insertCell1(table, "", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC91 = insertCell1(table, "", Element.ALIGN_LEFT, 1, bfBold12);				
					PdfPCell hC101= insertCell1(table, "", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC102 = insertCell1(table, "", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC103 = insertCell1(table, "", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC108 = insertCell1(table, "", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC109 = insertCell1(table, "", Element.ALIGN_LEFT, 1, bfBold12);				
					PdfPCell hC110= insertCell1(table, "", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC6111 = insertCell1(table, "", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC7112 = insertCell1(table, "", Element.ALIGN_LEFT, 1, bfBold12);*/


					table.addCell(hC61);
					/*table.addCell(hC71);
					table.addCell(hC81);
					table.addCell(hC91);
					table.addCell(hC101);*/
				}

				paragraph.add(table);
				doc.add(paragraph);
				doc.close();

				resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode("DocumentScanned."+fileExtension,"UTF-8").replaceAll("\\+"," ")  + "\"");
				resp.setContentType("application/pdf");
				resp.setContentLength(baos.size());

				baos.writeTo(os);
				os.flush();
				os.close();

			}

			else if(exportType.equalsIgnoreCase("Excel")) {

				Long startTime = System.currentTimeMillis();
				System.out.println("Start Time   :"+(startTime-startTime));

				fileExtension = "xls";
				WritableWorkbook docScannedWorkbook = null;

				try {
					resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode("DocumentScanned."+fileExtension,"UTF-8").replaceAll("\\+"," ")  + "\"");
					resp.setContentType("application/vnd.ms-excel");

					docScannedWorkbook = jxl.Workbook.createWorkbook(resp.getOutputStream());

					WritableSheet workSheet = docScannedWorkbook.createSheet("docsAdded", 0);

					java.io.File imageFile = new java.io.File(req.getSession().getServletContext().getRealPath("images")+"\\logo-new.png");
					BufferedImage input = ImageIO.read(imageFile);
					baos = new ByteArrayOutputStream();
					ImageIO.write(input, "PNG", baos);
					workSheet.addImage(new WritableImage(0,0,0.75,5.5,baos.toByteArray()));

					CellView cell = workSheet.getRowView(12);
					workSheet.setColumnView(0, 50);
					cell.setAutosize(true);
					workSheet.setColumnView(1, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(2, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(3, 50);
					cell.setAutosize(true);
					workSheet.setColumnView(4, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(5, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(6, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(7, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(8, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(9, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(10, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(11, cell);
					
					WritableCellFormat cFormat = null;

					cFormat = fontBold();			
					String departmentName = new DBAdaptor().getDepartmentName(Long.valueOf(department).longValue());
					if(departmentName==null){
						departmentName="";
					}
					Label label = new Label(0, 7, "Dept Name : "+departmentName, cFormat);
					workSheet.addCell(label);					
					label = new Label(0, 8, "Total Docs Added : "+list.size(), cFormat);
					workSheet.addCell(label);
					cFormat = fontBoldItalic();
					label = new Label(0, 9, "Through the period : "+from+" To "+ to, cFormat);
					workSheet.addCell(label);
					cFormat = fontBoldWithRed();
					String div ="";
					if(division.equalsIgnoreCase("-1")){
						
						div = "All Division";
					}else{
						div = new DBAdaptor().getDivisionsName(division);
						
					}
					label = new Label(0, 10, ""+list.size()+" | "+div, cFormat);
					workSheet.addCell(label);
					cFormat = fontNoBold();
					label = new Label(0, 11, "Docs", cFormat);
					workSheet.addCell(label);
					
					int rwNo = 12;
					HashMap<String, String> documentCount = new HashMap<String, String>();
					for (int j = 0; j < user.split(",").length; j++) {
						int count =0;
						String userFullName = DBAdaptor.getUserFullName(user.split(",")[j]);
						for (int i = 0; i < list.size(); i++) {
							HashMap<String, Object> mapObj = list.get(i);
							if(mapObj.get("Creator") != null && user.split(",")[j].equalsIgnoreCase(mapObj.get("Creator").toString())) {
								count++;
							}
						}
						documentCount.put(userFullName, Integer.toString(count));
					}
					for (int j = 0; j < user.split(",").length; j++) {
						rwNo = rwNo+1;
						cFormat = fontBoldAndBackGround();
						String userFullName = DBAdaptor.getUserFullName(user.split(",")[j]);
						label = new Label(0, rwNo, userFullName, cFormat);
						workSheet.addCell(label);
						label = new Label(1, rwNo, "", cFormat);
						workSheet.addCell(label);
						label = new Label(2, rwNo, "", cFormat);
						workSheet.addCell(label);
						label = new Label(3, rwNo, "", cFormat);
						workSheet.addCell(label);
						label = new Label(4, rwNo, "", cFormat);
						workSheet.addCell(label);
						label = new Label(5, rwNo, "", cFormat);
						workSheet.addCell(label);
						label = new Label(6, rwNo, "", cFormat);
						workSheet.addCell(label);
						label = new Label(7, rwNo, "", cFormat);
						workSheet.addCell(label);
						label = new Label(8, rwNo, "", cFormat);
						workSheet.addCell(label);
						label = new Label(9, rwNo, "", cFormat);
						workSheet.addCell(label);
						label = new Label(10, rwNo, "", cFormat);
						workSheet.addCell(label);
						label = new Label(11, rwNo, "", cFormat);
						workSheet.addCell(label);
						label = new Label(12, rwNo, documentCount.get(userFullName), cFormat);
						workSheet.addCell(label);
						
						rwNo = rwNo+1;
						int startRow = rwNo;
						cFormat = fontBoldWithColor();
						label = new Label(0, rwNo, "", cFormat);
						workSheet.addCell(label);
						label = new Label(1, rwNo, "Date Created", cFormat);
						workSheet.addCell(label);
						System.out.println(workSheet.getCell(0, rwNo).getContents());
						label = new Label(2, rwNo, "Launched", cFormat);
						workSheet.addCell(label);
						label = new Label(3, rwNo, "Ref No", cFormat);
						workSheet.addCell(label);
						label = new Label(4, rwNo, "Subject", cFormat);
						workSheet.addCell(label);
						label = new Label(5, rwNo, "ID", cFormat);
						workSheet.addCell(label);
						label = new Label(6, rwNo, "Size", cFormat);
						workSheet.addCell(label);
						label = new Label(7, rwNo, "Document Type", cFormat);
						workSheet.addCell(label);
						label = new Label(8, rwNo, "Correspondence Type", cFormat);
						workSheet.addCell(label);
						label = new Label(9, rwNo, "Document Date", cFormat);
						workSheet.addCell(label);
						label = new Label(10, rwNo, "Date Received", cFormat);
						workSheet.addCell(label);
						label = new Label(11, rwNo, "Document From", cFormat);
						workSheet.addCell(label);
						label = new Label(12, rwNo, "Confidential", cFormat);
						workSheet.addCell(label);
						
						for (int i = 0; i < list.size(); i++) {

							HashMap<String, Object> mapObj = list.get(i);

							if(mapObj.get("Creator") != null && user.split(",")[j].equalsIgnoreCase(mapObj.get("Creator").toString())) {
								
								/*if(i == 0) {
									rwNo = rwNo+1;
									cFormat = fontBoldWithColor();
									label = new Label(0, rwNo, "Is Launched", cFormat);
									workSheet.addCell(label);
									label = new Label(1, rwNo, "Ref. NO", cFormat);
									workSheet.addCell(label);
									label = new Label(2, rwNo, "Subject", cFormat);
									workSheet.addCell(label);
									label = new Label(3, rwNo, "ID", cFormat);
									workSheet.addCell(label);
									label = new Label(4, rwNo, "Size", cFormat);
									workSheet.addCell(label);
									label = new Label(5, rwNo, "Document Type", cFormat);
									workSheet.addCell(label);
									label = new Label(6, rwNo, "Correspondence Type", cFormat);
									workSheet.addCell(label);
									label = new Label(7, rwNo, "Document Date", cFormat);
									workSheet.addCell(label);
									label = new Label(8, rwNo, "Receive Date", cFormat);
									workSheet.addCell(label);
									label = new Label(9, rwNo, "From", cFormat);
									workSheet.addCell(label);
									label = new Label(10, rwNo, "Confidential", cFormat);
									workSheet.addCell(label);
								}*/
								rwNo = rwNo+1;
								if(i % 2 == 0) {
									cFormat = fontNoBold();
									label = new Label(0, rwNo, "", cFormat);
									workSheet.addCell(label);
									label = new Label(1, rwNo, ""+mapObj.get("dateCreated").toString(), cFormat);
									workSheet.addCell(label);
									label = new Label(2, rwNo, ""+mapObj.get("IsLaunched"), cFormat);
									workSheet.addCell(label);
									label = new Label(3, rwNo, ""+mapObj.get("ReferenceNo"), cFormat);
									workSheet.addCell(label);
									label = new Label(4, rwNo, ""+mapObj.get("DocumentTitle"), cFormat);
									workSheet.addCell(label);
									label = new Label(5, rwNo, ""+mapObj.get("DocumentID"), cFormat);
									workSheet.addCell(label);
									label = new Label(6, rwNo, ""+mapObj.get("ContentSize"), cFormat);
									workSheet.addCell(label);
									int docType = (Integer)mapObj.get("DocumentType");
									String docTypeName = new DBAdaptor().getDocTypeName(docType);
									if(docTypeName == null){
										docTypeName ="";
									}
									label = new Label(7, rwNo, docTypeName, cFormat);
									workSheet.addCell(label);
									label = new Label(8, rwNo, ""+mapObj.get("CorrespondenceType"), cFormat);
									workSheet.addCell(label);
									label = new Label(9, rwNo, ""+mapObj.get("DocumentDate"), cFormat);
									workSheet.addCell(label);
									label = new Label(10, rwNo, ""+mapObj.get("DateReceived"), cFormat);
									workSheet.addCell(label);
									int docFrom = (Integer)mapObj.get("DocumentFrom");
									String documentFrom = new DBAdaptor().getDocumentSiteName(docFrom);
									if(documentFrom == null){
										documentFrom="";
									}
									label = new Label(11, rwNo, ""+documentFrom, cFormat);
									workSheet.addCell(label);
									String cnf = (String) mapObj.get("Confidentiality");
									if(cnf == null){
										cnf="";
									}
									label = new Label(12, rwNo, ""+cnf, cFormat);
									workSheet.addCell(label);
								}
								else {
									cFormat = fontNoBoldAndBackGround();
									label = new Label(0, rwNo, "", cFormat);
									workSheet.addCell(label);
									label = new Label(1, rwNo, ""+mapObj.get("dateCreated").toString(), cFormat);
									workSheet.addCell(label);
									label = new Label(2, rwNo, ""+mapObj.get("IsLaunched"), cFormat);
									workSheet.addCell(label);
									label = new Label(3, rwNo, ""+mapObj.get("ReferenceNo"), cFormat);
									workSheet.addCell(label);
									label = new Label(4, rwNo, ""+mapObj.get("DocumentTitle"), cFormat);
									workSheet.addCell(label);
									label = new Label(5, rwNo, ""+mapObj.get("DocumentID"), cFormat);
									workSheet.addCell(label);
									label = new Label(6, rwNo, ""+mapObj.get("ContentSize"), cFormat);
									workSheet.addCell(label);
									int docType = (Integer)mapObj.get("DocumentType");
									String docTypeName = new DBAdaptor().getDocTypeName(docType);
									if(docTypeName == null){
										docTypeName ="";
									}
									label = new Label(7, rwNo, docTypeName, cFormat);
									workSheet.addCell(label);
									label = new Label(8, rwNo, ""+mapObj.get("CorrespondenceType"), cFormat);
									workSheet.addCell(label);
									label = new Label(9, rwNo, ""+mapObj.get("DocumentDate"), cFormat);
									workSheet.addCell(label);
									label = new Label(10, rwNo, ""+mapObj.get("DateReceived"), cFormat);
									workSheet.addCell(label);
									int docFrom = (Integer)mapObj.get("DocumentFrom");
									String documentFrom = new DBAdaptor().getDocumentSiteName(docFrom);
									if(documentFrom == null){
										documentFrom="";
									}
									label = new Label(11, rwNo, ""+documentFrom, cFormat);
									workSheet.addCell(label);
									String cnf = (String) mapObj.get("Confidentiality");
									if(cnf == null){
										cnf="";
									}
									label = new Label(12, rwNo, ""+cnf, cFormat);
									workSheet.addCell(label);
								}
								
							}
						}
						workSheet.setRowGroup(startRow,rwNo, true); 
						label = new Label(0, rwNo++, "" , cFormat);
						workSheet.addCell(label);
					}
					
					docScannedWorkbook.write();
					System.out.println("Total Time   :"+(System.currentTimeMillis()-startTime));

				} catch (WriteException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (docScannedWorkbook != null) {
						try {
							docScannedWorkbook.close();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (WriteException e) {
							e.printStackTrace();
						}
					}

				}
			}

		}catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static WritableCellFormat fontBold() {
		WritableCellFormat cFormat = new WritableCellFormat();
		WritableFont font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
		cFormat.setFont(font);
		return cFormat;
	}

	private static WritableCellFormat fontNoBold() {
		WritableCellFormat cFormat = new WritableCellFormat();
		WritableFont font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.NO_BOLD);
		cFormat.setFont(font);
		return cFormat;
	}
	
	private static WritableCellFormat fontBoldWithRed() {
		WritableCellFormat cFormat = new WritableCellFormat();
		WritableFont font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
		try {
			font.setColour(Colour.RED);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		cFormat.setFont(font);
		return cFormat;
	}

	private static WritableCellFormat fontBoldItalic() {
		WritableCellFormat cFormat = new WritableCellFormat();
		WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
		try {
			font.setColour(Colour.GRAY_50);
		} catch (WriteException e1) {
			e1.printStackTrace();
		}
		cFormat.setFont(font);
		try {
			font.setItalic(true);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return cFormat;
	}

	private static WritableCellFormat fontBoldAndBackGround() {
		WritableCellFormat cFormat = new WritableCellFormat();
		WritableFont font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
		cFormat.setFont(font);
		try {
			cFormat.setBackground(Colour.GREY_50_PERCENT);
			font.setColour(Colour.WHITE);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return cFormat;
	}

	private static WritableCellFormat fontBoldWithColor() {
		WritableCellFormat cFormat = new WritableCellFormat();
		WritableFont font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
		cFormat.setFont(font);
		try {
			cFormat.setBackground(Colour.GREY_50_PERCENT);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return cFormat;
	}

	private static WritableCellFormat fontNoBoldAndBackGround() {
		WritableCellFormat cFormat = new WritableCellFormat();
		WritableFont font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.NO_BOLD);
		cFormat.setFont(font);
		try {
			cFormat.setBackground(Colour.GREY_25_PERCENT);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return cFormat;
	}

	
	
	@GET
	@Path("/documentsScanned")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentsScanned(@QueryParam("dpt") String department, @QueryParam("div") String division, @QueryParam("user") String user, @QueryParam("from") String from,@QueryParam("to") String to, @QueryParam("exportType") String exportType, @Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception {
		try{
			ContentService cs = new ContentService(req,resp);
			DBAdaptor db = new DBAdaptor();
			int oldDepartmentId = db.getOldDimsDepartmentId(department);
			List<HashMap<String, Object>> map = cs.documentsScanned(oldDepartmentId,division,user,from,to);
			
			for (int i = 0; i < map.size(); i++) {

				HashMap<String, Object> mapObj = map.get(i);
				int docType = (Integer)mapObj.get("DocumentType");
				String docTypeName = new DBAdaptor().getDocTypeName(docType);
				mapObj.put("DocumentType", docTypeName);
				int docFrom = (Integer)mapObj.get("DocumentFrom");
				String documentFrom = new DBAdaptor().getDocumentSiteName(docFrom);
				mapObj.put("DocumentFrom", documentFrom);
			}
			return Response.ok().entity(map).build();
		}catch (Exception e) {
			System.out.println("Exceptoin  :; "+e.getLocalizedMessage());
			e.printStackTrace();
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		

	}
	
	private PdfPCell insertCell(PdfPTable table, String text, int align, int colspan, Font font) {
		if (text == null || text.trim().equalsIgnoreCase("NULL")) {
			text = "";
		}
		PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
		cell.setHorizontalAlignment(align);
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setNoWrap(false);
		cell.setColspan(colspan);
		if (text.trim().equalsIgnoreCase("")) {
			cell.setMinimumHeight(10f);
		}
		return cell;
	}
	
	private PdfPCell insertCell1(PdfPTable table, String text, int align, int colspan, Font font) {
		if (text == null || text.trim().equalsIgnoreCase("NULL")) {
			text = "";
		}
		PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
		cell.setHorizontalAlignment(align);
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setNoWrap(true);
		cell.setColspan(colspan);
		cell.setBorder(Rectangle.NO_BORDER);
		if (text.trim().equalsIgnoreCase("")) {
			cell.setMinimumHeight(10f);
		}
		return cell;
	}
	private static boolean isArabicText(String subject) {
		boolean isArabic = false;
		if(subject!=null){			
	        for (int a = 0; a < subject.length(); a++) {
	            char c = subject.charAt(a);
	            if (c > 31 && c < 127){
	            	isArabic = false;
	            }
	            else{
	            	isArabic = true;
	            	break;
	            }
	        }
		}
        	 
	 return isArabic;
}
	// http://localhost:9080/DIMS/resources/FilenetService/getObjectAccessValues?docId={3175DF62-D4AC-C3A1-8610-5A2553800000}&osName=ECM
		/*@GET
		@Path("/getObjectAccessValues")
		@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)
		public Response getObjectAccessValues(@QueryParam("docId") String docId,@QueryParam("osName") String objectStoreName,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception 
		{
			List<PropertyBean> propertyBeanList = null;
			try{
				ContentService cs = new ContentService(req,resp);
				cs.getObjectAccessValues(docId,objectStoreName);
			}catch (Exception e) {
				ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
				return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
			}
			return Response.ok().entity(propertyBeanList).build();
		}*/
}
