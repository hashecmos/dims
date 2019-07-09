package com.knpc.dims.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

import jxl.CellView;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

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
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.knpc.dims.db.DBAdaptor;
import com.knpc.dims.db.service.WorkFlowService;
import com.knpc.dims.filenet.FileNetAdaptor;
import com.knpc.dims.pdf.headerfooter.HeaderFooterPageEvent;
import com.knpc.dims.response.ResponseObject;
import com.knpc.dims.workflow.beans.DivisionWF;
import com.knpc.dims.workflow.beans.PendingWorkItemDetails;
import com.knpc.dims.workflow.beans.WorkItemDetails;
@Path("/WorkflowSearchService")
@ApplicationPath("resources")
public class WorkFlowSearchController {
	
	// http://localhost:9080/DIMS/resources/WorkflowSearchService/searchWorkFlow
	@POST
	@Path("/searchWorkFlow")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchWorkFlow(String jsonString,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{

		ArrayList<WorkItemDetails> workItemDetailsList = new ArrayList<WorkItemDetails>();
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			FileNetAdaptor adaptor = new FileNetAdaptor();
	  		if(adaptor.validateRequest(req, resp)) {
	  			workItemDetailsList = ws.searchWorkFlow(jsonString);
	  		}
		//	return workItemDetailsList;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(workItemDetailsList).build();
	}
	
	
	// http://localhost:9080/DIMS/resources/WorkflowSearchService/quickSearchWorkFlow?subject=
	@GET
	@Path("/quickSearchWorkFlow")
	@Produces(MediaType.APPLICATION_JSON)
	public Response quickSearchWorkFlow(@QueryParam("subject") String subject,@QueryParam("user_login") String user_login,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		
		ArrayList<WorkItemDetails> workItemDetailsList = new ArrayList<WorkItemDetails>();
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			FileNetAdaptor adaptor = new FileNetAdaptor();
	  		if(adaptor.validateRequest(req, resp)) {
	  			workItemDetailsList = ws.quickSearchWorkFlow(subject,user_login);
	  		}
			//return workItemDetailsList;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(workItemDetailsList).build();
	}
	
	@GET
	@Path("/workflowStatistics")
	@Produces(MediaType.APPLICATION_JSON)
	public Response workflowStatistics(@QueryParam("userLogin") String userLogin, @QueryParam("dpt") String department,@QueryParam("div") String division, @QueryParam("from") String from,@QueryParam("to") String to, @Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{

		ArrayList<DivisionWF> divisionWFList = null;
		HashMap<String , ArrayList<DivisionWF>> map = new HashMap<String, ArrayList<DivisionWF>>();
		Map<String, ArrayList<DivisionWF>> sortedMap = new TreeMap<String, ArrayList<DivisionWF>>(
                new Comparator<String>() {

                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }

                });

		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			divisionWFList = ws.workflowStatistics(userLogin, department, division, from, to);
			
			String key ="";
			for (int i = 0; i < divisionWFList.size(); i++) {
				ArrayList<DivisionWF> divWFList = new ArrayList<DivisionWF>();
				key = divisionWFList.get(i).getDivName();
				for (int j = 0; j < divisionWFList.size(); j++) {
					if(divisionWFList.get(j).getDivName().equalsIgnoreCase(divisionWFList.get(i).getDivName())){
						//key = divisionWFList.get(j).getDivName();
						divWFList.add(divisionWFList.get(j));
					}
				}
				map.put(key, divWFList);
			}
	        sortedMap.putAll(map);
			//return filterInbox;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(sortedMap).build();

	}
	
	@GET
	@Path("/exporWorkflowStatistics")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void exporWorkflowStatistics(@QueryParam("userLogin") String userLogin, @QueryParam("dpt") String department,@QueryParam("div") String division, @QueryParam("from") String from,@QueryParam("to") String to, @QueryParam("exportType") String exportType, @Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{

		ArrayList<DivisionWF> divisionWFList = null;
		HashMap<String , ArrayList<DivisionWF>> map = new HashMap<String, ArrayList<DivisionWF>>();
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			divisionWFList = ws.exporWorkflowStatistics(userLogin, department, division, from, to);
			
			String key ="";
			for (int i = 0; i < divisionWFList.size(); i++) {
				ArrayList<DivisionWF> divWFList = new ArrayList<DivisionWF>();
				key = divisionWFList.get(i).getDivName();
				for (int j = 0; j < divisionWFList.size(); j++) {
					//System.out.println("divisionWFList.get(j).getDivName() : Comparision   :::::>  "+divisionWFList.get(j).getDivName()); 
					if(divisionWFList.get(j).getDivName().equalsIgnoreCase(divisionWFList.get(i).getDivName())){
						divWFList.add(divisionWFList.get(j));
					}
				}
				map.put(key, divWFList);
			}
			Map<String, ArrayList<DivisionWF>> sortedMap = new TreeMap<String, ArrayList<DivisionWF>>(
	                new Comparator<String>() {

	                    @Override
	                    public int compare(String o1, String o2) {
	                        return o2.compareTo(o1);
	                    }

	                });

		  
	        sortedMap.putAll(map);
			exportToPdforExcel(sortedMap,exportType, req, resp);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void exportToPdforExcel(Map<String, ArrayList<DivisionWF>> sortedMap,String exportType, HttpServletRequest req, HttpServletResponse resp) throws Exception {

		OutputStream os = resp.getOutputStream();
		ByteArrayOutputStream baos = null;
		String fileExtension = null;

		if(exportType.equalsIgnoreCase("PDF")) {
			fileExtension = "pdf";
			Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD, new BaseColor(0, 0, 0));
			Font fontNormal = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, new BaseColor(0, 0, 0));
			Font font = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD, BaseColor.WHITE);
			Paragraph paragraph = new Paragraph();
			Document doc = new Document(PageSize.A4, 36, 36, 36, 72);
			baos = new ByteArrayOutputStream();
			
			
			PdfWriter writer = PdfWriter.getInstance(doc, baos);

	        // add header and footer
	        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
	        writer.setPageEvent(event);
	        
			/*PdfWriter.getInstance(doc, baos);
			MyFooter event = new MyFooter();
			PdfWriter.getInstance(doc, baos).setPageEvent(event);*/
			doc.open();
			PdfPTable table = new PdfPTable(5);// (columnWidths);
			table.setWidthPercentage(100f);

			Image image = Image.getInstance(req.getSession().getServletContext().getRealPath("images")+"\\logo-new.png");
			image.setAlignment(Image.LEFT);

			paragraph.add(new Chunk(image, 0, 0, true));
			PdfPCell cell = new PdfPCell();
			cell.addElement(paragraph);
			PdfPTable table1 = new PdfPTable(5);
			table1.addCell(cell);
			table1.setLockedWidth(true);

			table.setTotalWidth(new float[]{ 216,72,72,72,72 });
			table.setLockedWidth(true);

			int totalWF = 0;
			int totalNWF = 0;
			int totalAWF = 0;
			int totalOWF = 0;
			for (Map.Entry<String, ArrayList<DivisionWF>> entry : sortedMap.entrySet())
			{

				for (int i = 0; i < entry.getValue().size(); i++) {

					totalWF = totalWF+ Integer.parseInt(entry.getValue().get(i).getTotWF());
					totalNWF = totalNWF+ Integer.parseInt(entry.getValue().get(i).getTotNWF());
					totalAWF = totalAWF+ Integer.parseInt(entry.getValue().get(i).getTotAWF());
					totalOWF = totalOWF+ Integer.parseInt(entry.getValue().get(i).getTotOWF());

				}
			}

			PdfPCell hC21 = insertCell1(table, "Information Technology", Element.ALIGN_LEFT, 4, bfBold12);
			PdfPCell hC22 = insertCellnoWrap(table, "Total* WorkFlow:"+totalWF, Element.ALIGN_RIGHT, 1, bfBold12);

			PdfPCell hC23 = insertCell1(table, "Workflows of Information Technology", Element.ALIGN_LEFT, 4, bfBold12);
			PdfPCell hC24 = insertCellnoWrap(table, "New* WorkFlow:"+totalNWF, Element.ALIGN_RIGHT, 1, bfBold12);	

			PdfPCell hC25 = insertCellnoWrap(table, "Active* WorkFlow:"+totalAWF, Element.ALIGN_RIGHT, 5, bfBold12);
			PdfPCell hC26 = insertCellnoWrap(table, "Over Due WorkFlow:"+totalOWF, Element.ALIGN_RIGHT, 5, bfBold12);

			table.addCell(hC21);
			table.addCell(hC22);
			table.addCell(hC23);
			table.addCell(hC24);
			table.addCell(hC25);
			table.addCell(hC26);

			PdfPCell space = insertCell1(table, "", Element.ALIGN_RIGHT, 5, bfBold12);
			space.setFixedHeight(20);
			table.addCell(space);

			PdfPCell hC16 = insertCell1(table, "Division", Element.ALIGN_LEFT, 1, bfBold12);
			PdfPCell hC17 = insertCell1(table, "All", Element.ALIGN_LEFT, 1, bfBold12);
			PdfPCell hC18 = insertCell1(table, "New", Element.ALIGN_LEFT, 1, bfBold12);
			PdfPCell hC19 = insertCell1(table, "Active", Element.ALIGN_LEFT, 1, bfBold12);				
			PdfPCell hC20 = insertCell1(table, "Overdue", Element.ALIGN_LEFT, 1, bfBold12);

			table.addCell(hC16);
			table.addCell(hC17);
			table.addCell(hC18);
			table.addCell(hC19);
			table.addCell(hC20);

			for (Map.Entry<String, ArrayList<DivisionWF>> entry : sortedMap.entrySet())
			{
				System.out.println("Division Name   :"+entry.getKey());
				PdfPCell row1 = insertRow1(table, entry.getKey(), Element.ALIGN_LEFT, 1, font);
				table.addCell(row1);
				totalWF =0;
				totalNWF =0;
				totalAWF =0;
				totalOWF =0;
				int totCount = 0;
				System.out.println("entry.getValue().size()   :"+entry.getValue().size());
				for (int i = 0; i < entry.getValue().size(); i++) {

					totalWF = totalWF + Integer.parseInt(entry.getValue().get(i).getTotWF());
					totalNWF = totalNWF + Integer.parseInt(entry.getValue().get(i).getTotNWF());
					totalAWF = totalAWF + Integer.parseInt(entry.getValue().get(i).getTotAWF());
					totalOWF = totalOWF + Integer.parseInt(entry.getValue().get(i).getTotOWF());

				}

				PdfPCell row2 = insertRow1(table, ""+totalWF, Element.ALIGN_LEFT, 1, font);
				PdfPCell row3 = insertRow1(table, ""+totalNWF, Element.ALIGN_LEFT, 1, font);
				PdfPCell row4 = insertRow1(table, ""+totalAWF, Element.ALIGN_LEFT, 1, font);
				PdfPCell row5 = insertRow1(table, ""+totalOWF, Element.ALIGN_LEFT, 1, font);
				table.addCell(row2);
				table.addCell(row3);
				table.addCell(row4);
				table.addCell(row5);
				System.out.println("No. of recordds   :"+entry.getValue().size());
				for (int i = 0; i < entry.getValue().size(); i++) {

					PdfPCell hC1 = insertCell1(table, entry.getValue().get(i).getUserName(), Element.ALIGN_LEFT, 1, fontNormal);
					PdfPCell hC2 = insertCell1(table, entry.getValue().get(i).getTotWF(), Element.ALIGN_LEFT, 1, fontNormal);
					PdfPCell hC3 = insertCell1(table, entry.getValue().get(i).getTotNWF(), Element.ALIGN_LEFT, 1, fontNormal);
					PdfPCell hC4 = insertCell1(table, entry.getValue().get(i).getTotAWF(), Element.ALIGN_LEFT, 1, fontNormal);				
					PdfPCell hC5 = insertCell1(table, entry.getValue().get(i).getTotOWF(), Element.ALIGN_LEFT, 1, fontNormal);

					table.addCell(hC1);
					table.addCell(hC2);
					table.addCell(hC3);
					table.addCell(hC4);
					table.addCell(hC5);
					totCount++;
				}
				PdfPCell row6 = insertCell1(table, "Total Employee of "+entry.getKey()+" : "+totCount, Element.ALIGN_LEFT, 1, fontNormal);
				PdfPCell emptyCell = new PdfPCell(new Phrase("",font));
				emptyCell.setBorder(Rectangle.NO_BORDER);
				table.addCell(row6);
				table.addCell(emptyCell);
				table.addCell(emptyCell);
				table.addCell(emptyCell);
				table.addCell(emptyCell);
				
				PdfPCell row7 = insertCell1(table, "", Element.ALIGN_LEFT, 1, fontNormal);
				PdfPCell emptyCell1 = new PdfPCell(new Phrase("",font));
				emptyCell1.setBorder(Rectangle.NO_BORDER);
				table.addCell(row7);
				table.addCell(emptyCell);
				table.addCell(emptyCell);
				table.addCell(emptyCell);
				table.addCell(emptyCell);
			}
			paragraph.add(table);
			doc.add(paragraph);
			doc.close();

			resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode("Workflow Statistic."+fileExtension,"UTF-8").replaceAll("\\+"," ")  + "\"");
			resp.setContentType("application/pdf");
			resp.setContentLength(baos.size());

			baos.writeTo(os);
			os.flush();
			os.close();
			
			System.out.println("end pdf...!");

		}else if(exportType.equalsIgnoreCase("Excel")) {

			Long startTime = System.currentTimeMillis();
			System.out.println("Start Time   :"+(startTime-startTime));
			fileExtension = "xls";
			WritableWorkbook wfStatisticsWorkbook = null;

			try {
				resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode("Workflow Statistic."+fileExtension,"UTF-8").replaceAll("\\+"," ")  + "\"");
				resp.setContentType("application/vnd.ms-excel");

				wfStatisticsWorkbook = jxl.Workbook.createWorkbook(resp.getOutputStream());

				WritableSheet workSheet = wfStatisticsWorkbook.createSheet("PendingWFHFilter", 0);

				java.io.File imageFile = new java.io.File(req.getSession().getServletContext().getRealPath("images")+"\\logo-new.png");
				BufferedImage input = ImageIO.read(imageFile);
				baos = new ByteArrayOutputStream();
				ImageIO.write(input, "PNG", baos);
				workSheet.addImage(new WritableImage(0,0,0.45,5.5,baos.toByteArray()));
				
				
				CellView cell = workSheet.getRowView(12);
				cell.setAutosize(true);
				workSheet.setColumnView(0, cell);
				cell.setAutosize(true);
				workSheet.setColumnView(1, cell);
				cell.setAutosize(true);
				workSheet.setColumnView(2, cell);
				cell.setAutosize(true);
				workSheet.setColumnView(3, cell);
				cell.setAutosize(true);
				workSheet.setColumnView(4, cell);
				cell.setAutosize(true);
				workSheet.setColumnView(5, cell);

				WritableCellFormat cFormat = null;

				int totalWF = 0;
				int totalNWF = 0;
				int totalAWF = 0;
				int totalOWF = 0;
				for (Map.Entry<String, ArrayList<DivisionWF>> entry : sortedMap.entrySet())
				{

					for (int i = 0; i < entry.getValue().size(); i++) {

						totalWF = totalWF+ Integer.parseInt(entry.getValue().get(i).getTotWF());
						totalNWF = totalNWF+ Integer.parseInt(entry.getValue().get(i).getTotNWF());
						totalAWF = totalAWF+ Integer.parseInt(entry.getValue().get(i).getTotAWF());
						totalOWF = totalOWF+ Integer.parseInt(entry.getValue().get(i).getTotOWF());

					}
				}

				cFormat = fontMoreWithBold();						
				Label label = new Label(0, 7, "Information Technology", cFormat);
				workSheet.addCell(label);				
				cFormat = fontNoBold();
				label = new Label(3, 7, "Total* Workflow", cFormat);
				workSheet.addCell(label);
				cFormat = fontBold();
				label = new Label(4, 7, ""+totalWF, cFormat);
				workSheet.addCell(label);
				cFormat = fontNoBold();
				label = new Label(0, 8, "Workflows of Information Technology", cFormat);
				workSheet.addCell(label);
				cFormat = fontNoBold();
				label = new Label(3, 8, "New* Workflow", cFormat);
				workSheet.addCell(label);
				cFormat = fontBold();
				label = new Label(4, 8, ""+totalNWF, cFormat);
				workSheet.addCell(label);
				cFormat = fontNoBold();
				label = new Label(3, 9, "Active* Workflow", cFormat);
				workSheet.addCell(label);
				cFormat = fontBold();
				label = new Label(4, 9, ""+totalAWF, cFormat);
				workSheet.addCell(label);
				cFormat = fontNoBold();
				label = new Label(3, 10, "Over Due Workflow", cFormat);
				workSheet.addCell(label);
				cFormat = fontBold();
				label = new Label(4, 10, ""+totalOWF, cFormat);
				workSheet.addCell(label);
				
				cFormat = fontBold();
				label = new Label(0, 12, "Division", cFormat);
				workSheet.addCell(label);
				label = new Label(1, 12, "All", cFormat);
				workSheet.addCell(label);
				label = new Label(2, 12, "New", cFormat);
				workSheet.addCell(label);
				label = new Label(3, 12, "Active", cFormat);
				workSheet.addCell(label);
				label = new Label(4, 12, "Over Due", cFormat);
				workSheet.addCell(label);
				
				int rowNo = 14;
				int rwNumber = 0;
				for (Map.Entry<String, ArrayList<DivisionWF>> entry : sortedMap.entrySet()) {
					totalWF =0;
					totalNWF =0;
					totalAWF =0;
					totalOWF =0;
					int count = 0;
					int totCount = 0;
					System.out.println("entry.getValue().size()   :"+entry.getValue().size());
					for (int i = 0; i < entry.getValue().size(); i++) {
						
						totalWF = totalWF + Integer.parseInt(entry.getValue().get(i).getTotWF());
						totalNWF = totalNWF + Integer.parseInt(entry.getValue().get(i).getTotNWF());
						totalAWF = totalAWF + Integer.parseInt(entry.getValue().get(i).getTotAWF());
						totalOWF = totalOWF + Integer.parseInt(entry.getValue().get(i).getTotOWF());

						cFormat = fontNoBold();
						label = new Label(0, rowNo, entry.getValue().get(i).getUserName(), cFormat);
						System.out.println("Division Name in excel   :"+entry.getValue().get(i).getUserName());
						workSheet.addCell(label);
						label = new Label(1, rowNo, ""+Integer.parseInt(entry.getValue().get(i).getTotWF()), cFormat);
						workSheet.addCell(label);
						label = new Label(2, rowNo, ""+Integer.parseInt(entry.getValue().get(i).getTotNWF()), cFormat);
						workSheet.addCell(label);
						label = new Label(3, rowNo, ""+Integer.parseInt(entry.getValue().get(i).getTotAWF()), cFormat);
						workSheet.addCell(label);
						label = new Label(4, rowNo, ""+Integer.parseInt(entry.getValue().get(i).getTotOWF()), cFormat);
						workSheet.addCell(label);
						
						rowNo++;
						count++;
						totCount++;
					}
					
					rwNumber = rowNo - (count+1);
					
					cFormat = fontBoldAndBackGround();
					label = new Label(0, rwNumber, entry.getKey(), cFormat);
					workSheet.addCell(label);
					System.out.println("data in excel   :"+entry.getKey());
					label = new Label(1, rwNumber, ""+totalWF, cFormat);
					workSheet.addCell(label);
					label = new Label(2, rwNumber, ""+totalNWF, cFormat);
					workSheet.addCell(label);
					label = new Label(3, rwNumber, ""+totalAWF, cFormat);
					workSheet.addCell(label);
					label = new Label(4, rwNumber, ""+totalOWF, cFormat);
					workSheet.addCell(label);
					
					workSheet.setRowGroup(rwNumber+1,rwNumber+count, true); 
					
					cFormat = fontNoBold();
					label = new Label(0, rowNo, "Total Employee of "+entry.getKey()+" : "+totCount, cFormat);
					workSheet.addCell(label);
					label = new Label(0, (rowNo+1), "" , cFormat);
					workSheet.addCell(label);
					rowNo = rowNo+3;
					
				}

				wfStatisticsWorkbook.write();
				System.out.println("Total Time   :"+(System.currentTimeMillis()-startTime));

			} catch (WriteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (wfStatisticsWorkbook != null) {
					try {
						wfStatisticsWorkbook.close();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}

			}

		}
	}
	@GET
	@Path("/pendingWorkflows")
	@Produces(MediaType.APPLICATION_JSON)
	public Response pendingWorkflows(@QueryParam("sender") String sender, @QueryParam("status") String status,@QueryParam("overdue") String overdue,@QueryParam("recipient") String recipient,@QueryParam("from") String from,@QueryParam("to") String to, @Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{

		ArrayList<PendingWorkItemDetails> pendingWorkflowsList = null;
		HashMap<String, ArrayList<PendingWorkItemDetails>> map = new HashMap<String, ArrayList<PendingWorkItemDetails>>();
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			pendingWorkflowsList = ws.pendingWorkflows(sender, status, overdue, recipient, from, to);
			
			String key = null;

			for (int i = 0; i < pendingWorkflowsList.size(); i++) {
				ArrayList<PendingWorkItemDetails> pendingWFList = new ArrayList<PendingWorkItemDetails>();
				key = pendingWorkflowsList.get(i).getRecipient();
				for (int j = 0; j < pendingWorkflowsList.size(); j++) {
					if(pendingWorkflowsList.get(j).getRecipient().equalsIgnoreCase(pendingWorkflowsList.get(i).getRecipient())){
						pendingWFList.add(pendingWorkflowsList.get(j));
					}
				}
				map.put(key, pendingWFList);
			}
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(map).build();
	}

	// http://localhost:9080/DIMS/resources/WorkflowSearchService/pendingWorkflowsFullHistory
	@GET
	@Path("/pendingWorkflowsFullHistory")
	@Produces(MediaType.APPLICATION_JSON)
	public Response pendingWorkflowsFullHistory(@QueryParam("sender") String sender, @QueryParam("status") String status, @QueryParam("recipient") String recipient, @QueryParam("from") String from, @QueryParam("to") String to, @Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{

		ArrayList<PendingWorkItemDetails> pendingWorkItemDetailsList = new ArrayList<PendingWorkItemDetails>();
		HashMap<String, ArrayList<PendingWorkItemDetails>> map = new HashMap<String, ArrayList<PendingWorkItemDetails>>();
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				pendingWorkItemDetailsList = ws.pendingWorkflowsFullHistory(sender, status, recipient, from, to);
			}
			String key = null;

			for (int i = 0; i < pendingWorkItemDetailsList.size(); i++) {
				ArrayList<PendingWorkItemDetails> divWFList = new ArrayList<PendingWorkItemDetails>();
				key = pendingWorkItemDetailsList.get(i).getWorkflowID();
				for (int j = 0; j < pendingWorkItemDetailsList.size(); j++) {
					if(pendingWorkItemDetailsList.get(j).getWorkflowID().equalsIgnoreCase(pendingWorkItemDetailsList.get(i).getWorkflowID())){
						divWFList.add(pendingWorkItemDetailsList.get(j));
					}
				}
				map.put(key, divWFList);
			}
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(map).build();
	}

	// http://localhost:9080/DIMS/resources/WorkflowSearchService/pendingWorkflowsSpecificHistory
	@GET
	@Path("/pendingWorkflowsSpecificHistory")
	@Produces(MediaType.APPLICATION_JSON)
	public Response pendingWorkfplowsSpecificHistory(@QueryParam("sender") String sender, @QueryParam("status") String status, @QueryParam("recipient") String recipient, @QueryParam("from") String from, @QueryParam("to") String to, @Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{

		ArrayList<PendingWorkItemDetails> pendingWorkItemDetailsList = new ArrayList<PendingWorkItemDetails>();
		HashMap<String, ArrayList<PendingWorkItemDetails>> map = new HashMap<String, ArrayList<PendingWorkItemDetails>>();
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			FileNetAdaptor adaptor = new FileNetAdaptor();
			if(adaptor.validateRequest(req, resp)) {
				pendingWorkItemDetailsList = ws.pendingWorkflowsSpecificHistory(sender, status, recipient, from, to);

			}
			
			String key = null;
			for (int i = 0; i < pendingWorkItemDetailsList.size(); i++) {
				ArrayList<PendingWorkItemDetails> specificWFList = new ArrayList<PendingWorkItemDetails>();
				key = pendingWorkItemDetailsList.get(i).getWorkflowID();
				for (int j = 0; j < pendingWorkItemDetailsList.size(); j++) {
					if(pendingWorkItemDetailsList.get(j).getWorkflowID().equalsIgnoreCase(pendingWorkItemDetailsList.get(i).getWorkflowID())){
						specificWFList.add(pendingWorkItemDetailsList.get(j));
					}
				}
				map.put(key, specificWFList);
			}
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(map).build();
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
	

	@GET
	@Path("/pendingWorkflowsSpecificHistoryReport")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void pendingWorkflowsSpecificHistoryReport(@QueryParam("sender") String sender, @QueryParam("status") String status, @QueryParam("recipient") String recipient,@QueryParam("from") String from,@QueryParam("to") String to, @QueryParam("exportType") String exportType, @Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		ArrayList<PendingWorkItemDetails> pendingWorkflowsList = null;
		OutputStream os = resp.getOutputStream();
		ByteArrayOutputStream baos = null;
		String fileExtension = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			pendingWorkflowsList = ws.pendingWorkflowsSpecificHistory(sender, status, recipient, from, to);

			String key = null;
			HashMap<String, ArrayList<PendingWorkItemDetails>> map = new HashMap<String, ArrayList<PendingWorkItemDetails>>();
			for (int i = 0; i < pendingWorkflowsList.size(); i++) {
				ArrayList<PendingWorkItemDetails> specificWFHistoryList = new ArrayList<PendingWorkItemDetails>();
				key = pendingWorkflowsList.get(i).getWorkflowID();
				for (int j = 0; j < pendingWorkflowsList.size(); j++) {
					if(pendingWorkflowsList.get(j).getWorkflowID().equalsIgnoreCase(pendingWorkflowsList.get(i).getWorkflowID())){
						specificWFHistoryList.add(pendingWorkflowsList.get(j));
					}
				}
				map.put(key, specificWFHistoryList);
			}

			if(exportType.equalsIgnoreCase("PDF")) {
				fileExtension = "pdf";
				Font font = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD, BaseColor.WHITE);
				Font bfBold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, new BaseColor(0, 0, 0));
				Font italicFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC, new BaseColor(0, 0, 0));
				
				BaseFont unicodeFont = BaseFont.createFont(req.getSession().getServletContext().getRealPath("fonts")+"\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				Font utfFont = new Font(unicodeFont,10,Font.NORMAL); 
				Font utfFontItalic = new Font(unicodeFont,10,Font.ITALIC); 
				
				Paragraph paragraph = new Paragraph();
				Document doc = new Document(PageSize.A4, 36, 36, 36, 72);
				baos = new ByteArrayOutputStream();
				
				PdfWriter writer = PdfWriter.getInstance(doc, baos);

		        // add header and footer
		        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
		        writer.setPageEvent(event);
		        
				/*PdfWriter.getInstance(doc, baos);
				MyFooter event = new MyFooter();
				PdfWriter.getInstance(doc, baos).setPageEvent(event);*/
				doc.open();
				PdfPTable table = new PdfPTable(5);// (columnWidths);
				table.setWidthPercentage(100f);

				float[] columnWidths = new float[]{35f, 35f, 10f, 10f, 10f};
				table.setWidths(columnWidths);

				Image image = Image.getInstance(req.getSession().getServletContext().getRealPath("images")+"\\logo-new.png");
				image.setAlignment(Image.LEFT);

				paragraph.add(new Chunk(image, 0, 0, true));
				PdfPCell cell = new PdfPCell();
				cell.addElement(paragraph);
				PdfPTable table1 = new PdfPTable(5);
				table1.addCell(cell);
				table1.setLockedWidth(true);

				Set<String> wfIdSet = map.keySet();
				Iterator<String> it = wfIdSet.iterator();
				while (it.hasNext()) {
					String wfIDKey = it.next().toString();
					ArrayList<PendingWorkItemDetails> list = map.get(wfIDKey);

					paragraph.add(new Paragraph(""));
					
					boolean isArabic = isArabicText(list.get(0).getSubject());
					PdfPTable table3 = new PdfPTable(1);// (columnWidths);
					table3.setWidthPercentage(100f);
					PdfPCell cell3 = new PdfPCell(new Phrase(list.get(0).getSubject(),utfFont));
					cell3.setBorder(Rectangle.NO_BORDER);
					if(isArabic){
						cell3.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
						cell3.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
					}else{
						cell3.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
					}
					table3.addCell(cell3);
					paragraph.add(table3);
					paragraph.add(new Paragraph(""));
					paragraph.add("Workflow Begin Date");
					paragraph.add("       ");
					paragraph.add("Workflow Receive Date");
					paragraph.add("       ");
					paragraph.add("Workflow Deadline");
					paragraph.add(new Paragraph(""));
					paragraph.add(list.get(0).getWorkflowBeginDate());
					paragraph.add("                      ");
					paragraph.add(list.get(0).getReceiveDate());
					paragraph.add("                          ");
					paragraph.add(list.get(0).getDeadLine());
					paragraph.add(new Paragraph(""));
					
					PdfPTable table4 = new PdfPTable(1);// (columnWidths);
					table4.setWidthPercentage(100f);
					PdfPCell cell1 = new PdfPCell(new Phrase("History of "+list.get(0).getSubject(),utfFont));
					cell1.setColspan(1);
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setUseDescender(true);
					if(isArabic){
						cell1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
						cell1.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
					}else{
						cell1.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
					}
					table4.addCell(cell1);
					paragraph.add(table4);
					PdfPCell hC6 = insertCell1(table, "Sender", Element.ALIGN_LEFT, 1, font);
					PdfPCell hC7 = insertCell1(table, "Recipient", Element.ALIGN_LEFT, 1, font);
					PdfPCell hC8 = insertCell1(table, "Status", Element.ALIGN_LEFT, 1, font);
					PdfPCell hC9 = insertCell1(table, "Type", Element.ALIGN_LEFT, 1, font);				
					PdfPCell hC10= insertCell1(table, "Action Date", Element.ALIGN_LEFT, 1, font);

					hC6.setBackgroundColor(new BaseColor(128, 128, 128));
					hC7.setBackgroundColor(new BaseColor(128, 128, 128));
					hC8.setBackgroundColor(new BaseColor(128, 128, 128));
					hC9.setBackgroundColor(new BaseColor(128, 128, 128));
					hC10.setBackgroundColor(new BaseColor(128, 128, 128));

					table.addCell(hC6);
					table.addCell(hC7);
					table.addCell(hC8);
					table.addCell(hC9);
					table.addCell(hC10);

					for (int i = 0; i < list.size(); i++) {
						if(i % 2 == 0) {
							PdfPCell hC1 = insertCell1(table, list.get(i).getSender(), Element.ALIGN_LEFT, 1, bfBold);
							PdfPCell hC2 = insertCell1(table, list.get(i).getRecipient(), Element.ALIGN_LEFT, 1, bfBold);
							PdfPCell hC3 = insertCell1(table, list.get(i).getWorkitemStatus(), Element.ALIGN_LEFT, 1, bfBold);
							PdfPCell hC4 = insertCell1(table, list.get(i).getSystemStatus(), Element.ALIGN_LEFT, 1, bfBold);				
							PdfPCell hC5 = insertCell1(table, list.get(i).getActionDate(), Element.ALIGN_LEFT, 1, bfBold);

							PdfPCell comments = null;
							if(((list.get(i).getComments()) != null) && (!(list.get(i).getComments()).equalsIgnoreCase("undefined"))) {
								comments = insertCellAR(table, list.get(i).getSender()+" Comments: "+list.get(i).getComments(), Element.ALIGN_LEFT, 5, utfFontItalic);
							}
							else {
								comments = insertCellAR(table, "No Comments from the Sender", Element.ALIGN_LEFT, 5, utfFontItalic);
							}
							boolean isCommentArabic = isArabicText(list.get(0).getComments());
							if(isCommentArabic){
								comments.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
								comments.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
							}else{
								comments.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
							}
							table.addCell(hC1);
							table.addCell(hC2);
							table.addCell(hC3);
							table.addCell(hC4);
							table.addCell(hC5);
							table.addCell(comments);
						}
						else {
							PdfPCell hC1 = insertCell1(table, list.get(i).getSender(), Element.ALIGN_LEFT, 1, bfBold);
							PdfPCell hC2 = insertCell1(table, list.get(i).getRecipient(), Element.ALIGN_LEFT, 1, bfBold);
							PdfPCell hC3 = insertCell1(table, list.get(i).getWorkitemStatus(), Element.ALIGN_LEFT, 1, bfBold);
							PdfPCell hC4 = insertCell1(table, list.get(i).getSystemStatus(), Element.ALIGN_LEFT, 1, bfBold);				
							PdfPCell hC5 = insertCell1(table, list.get(i).getActionDate(), Element.ALIGN_LEFT, 1, bfBold);

							PdfPCell comments = null;
							if(((list.get(i).getComments()) != null) && (!(list.get(i).getComments()).equalsIgnoreCase("undefined"))) {
								comments = insertCellAR(table, list.get(i).getSender()+" Comments: "+list.get(i).getComments(), Element.ALIGN_LEFT, 5, utfFontItalic);
							}
							else {
								comments = insertCellAR(table, "No Comments from the Sender", Element.ALIGN_LEFT, 5, utfFontItalic);
							}
							boolean isCommentArabic = isArabicText(list.get(0).getComments());
							if(isCommentArabic){
								comments.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
								comments.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
							}else{
								comments.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
							}
							hC1.setBackgroundColor(new BaseColor(245, 245, 245));
							hC2.setBackgroundColor(new BaseColor(245, 245, 245));
							hC3.setBackgroundColor(new BaseColor(245, 245, 245));
							hC4.setBackgroundColor(new BaseColor(245, 245, 245));
							hC5.setBackgroundColor(new BaseColor(245, 245, 245));
							comments.setBackgroundColor(new BaseColor(245, 245, 245));

							table.addCell(hC1);
							table.addCell(hC2);
							table.addCell(hC3);
							table.addCell(hC4);
							table.addCell(hC5);
							table.addCell(comments);
						}
					}
					PdfPCell sapce1 = insertCell1(table, "", Element.ALIGN_LEFT, 5, bfBold);
					sapce1.setFixedHeight(50);
					table.addCell(sapce1);
					paragraph.add(table);
					doc.add(paragraph);
					table.flushContent();
					paragraph.clear();
				}
				doc.close();
				resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode("Pending Workflow Specific History."+fileExtension,"UTF-8").replaceAll("\\+"," ")  + "\"");
				resp.setContentType("application/pdf");
				resp.setContentLength(baos.size());

				baos.writeTo(os);
				os.flush();
				os.close();

			}

			else if(exportType.equalsIgnoreCase("Excel")) {

				Long startTime = System.currentTimeMillis();
				fileExtension = "xls";
				WritableWorkbook specificHistoryWorkbook = null;

				try {
					resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode("Pending Workflow Specific History."+fileExtension,"UTF-8").replaceAll("\\+"," ")  + "\"");
					resp.setContentType("application/vnd.ms-excel");

					specificHistoryWorkbook = jxl.Workbook.createWorkbook(resp.getOutputStream());

					WritableSheet workSheet = specificHistoryWorkbook.createSheet("PendingWFHFilter", 0);

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
					workSheet.setColumnView(3, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(4, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(5, cell);

					Set<String> wfIdSet = map.keySet();
					Iterator<String> it = wfIdSet.iterator();
					int rowNumber = 6;
					while (it.hasNext()) {
						String wfIDKey = it.next().toString();
						ArrayList<PendingWorkItemDetails> list = map.get(wfIDKey);

						WritableCellFormat cFormat = null;

						cFormat = fontNoBold();						
						Label label = new Label(0, rowNumber++, list.get(0).getSubject(), cFormat);
						workSheet.addCell(label);

						rowNumber++;						
						cFormat = fontBoldItalic();
						label = new Label(0, rowNumber, "Workflow Begin Date", cFormat);
						workSheet.addCell(label);
						label = new Label(1, rowNumber, "Workflow Receive Date", cFormat);
						workSheet.addCell(label);
						label = new Label(2, rowNumber, "Workflow Deadline", cFormat);
						workSheet.addCell(label);

						rowNumber++;						
						cFormat = fontNoBold();
						label = new Label(0, rowNumber, list.get(0).getWorkflowBeginDate(), cFormat);
						workSheet.addCell(label);
						label = new Label(1, rowNumber, list.get(0).getReceiveDate(), cFormat);
						workSheet.addCell(label);
						label = new Label(2, rowNumber, list.get(0).getDeadLine(), cFormat);
						workSheet.addCell(label);
						cFormat = fontBold();

						rowNumber = rowNumber+2;
						label = new Label(0, rowNumber, "History of "+list.get(0).getSubject(), cFormat);
						workSheet.addCell(label);

						rowNumber = rowNumber+1;
						cFormat = fontBoldAndBackGround();
						label = new Label(0, rowNumber, "Sender", cFormat);
						workSheet.addCell(label);
						label = new Label(1, rowNumber, "Recipient", cFormat);
						workSheet.addCell(label);
						label = new Label(2, rowNumber, "Status", cFormat);
						workSheet.addCell(label);
						label = new Label(3, rowNumber, "Type", cFormat);
						workSheet.addCell(label);
						label = new Label(4, rowNumber, "Action Date", cFormat);
						workSheet.addCell(label);
						label = new Label(5, rowNumber, "Comments", cFormat);
						workSheet.addCell(label);

						int noOfRows = rowNumber+1;
						for (int i = 0; i < list.size() ; i++) {

							cFormat = fontNoBoldAndBackGround();							
							if(i % 2 == 0) {
								label = new Label(0, noOfRows, list.get(i).getSender(), cFormat);
								workSheet.addCell(label);
								label = new Label(1, noOfRows, list.get(i).getRecipient(), cFormat);
								workSheet.addCell(label);
								label = new Label(2, noOfRows, list.get(i).getWorkitemStatus(), cFormat);
								workSheet.addCell(label);
								label = new Label(3, noOfRows, list.get(i).getSystemStatus(), cFormat);
								workSheet.addCell(label);
								label = new Label(4, noOfRows, list.get(i).getActionDate(), cFormat);
								workSheet.addCell(label);
								if(((list.get(i).getComments()) != null) && (!(list.get(i).getComments()).equalsIgnoreCase("undefined"))) {
									label = new Label(5, noOfRows, list.get(i).getSender()+" Comments: "+list.get(i).getComments(), cFormat);
									workSheet.addCell(label);
								}
								else {
									label = new Label(5, noOfRows, "No Comments from the Sender", cFormat);
									workSheet.addCell(label);
								}
							}

							else {
								cFormat = fontNoBold();
								label = new Label(0, noOfRows, list.get(i).getSender(), cFormat);
								workSheet.addCell(label);
								label = new Label(1, noOfRows, list.get(i).getRecipient(), cFormat);
								workSheet.addCell(label);
								label = new Label(2, noOfRows, list.get(i).getWorkitemStatus(), cFormat);
								workSheet.addCell(label);
								label = new Label(3, noOfRows, list.get(i).getSystemStatus(), cFormat);
								workSheet.addCell(label);
								label = new Label(4, noOfRows, list.get(i).getActionDate(), cFormat);
								workSheet.addCell(label);
								if(((list.get(i).getComments()) != null) && (!(list.get(i).getComments()).equalsIgnoreCase("undefined"))) {
									label = new Label(5, noOfRows, list.get(i).getSender()+" Comments: "+list.get(i).getComments(), cFormat);
									workSheet.addCell(label);
								}
								else {
									label = new Label(5, noOfRows, "No Comments from the Sender", cFormat);
									workSheet.addCell(label);
								}

							}

							noOfRows++;
						}
						rowNumber = noOfRows+2;
					}

					specificHistoryWorkbook.write();
					System.out.println("Total Time   :"+(System.currentTimeMillis()-startTime));

				} catch (WriteException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (specificHistoryWorkbook != null) {
						try {
							specificHistoryWorkbook.close();
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

	@GET
	@Path("/pendingWorkflowsFullHistoryReport")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)//@Produces(MediaType.APPLICATION_XML)
	public void pendingWorkflowsFullHistoryReport(@QueryParam("sender") String sender, @QueryParam("status") String status, @QueryParam("recipient") String recipient,@QueryParam("from") String from,@QueryParam("to") String to, @QueryParam("exportType") String exportType, @Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		ArrayList<PendingWorkItemDetails> pendingWorkflowsList = null;
		OutputStream os = resp.getOutputStream();
		ByteArrayOutputStream baos = null;
		String fileExtension = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			pendingWorkflowsList = ws.pendingWorkflowsFullHistory(sender, status, recipient, from, to);
			String userFullName = DBAdaptor.getUserFullName(recipient);
			String key = null;
			HashMap<String, ArrayList<PendingWorkItemDetails>> map = new HashMap<String, ArrayList<PendingWorkItemDetails>>();
			for (int i = 0; i < pendingWorkflowsList.size(); i++) {
				ArrayList<PendingWorkItemDetails> fullWFHistoryList = new ArrayList<PendingWorkItemDetails>();
				key = pendingWorkflowsList.get(i).getWorkflowID();
				for (int j = 0; j < pendingWorkflowsList.size(); j++) {
					if(pendingWorkflowsList.get(j).getWorkflowID().equalsIgnoreCase(pendingWorkflowsList.get(i).getWorkflowID())){
						fullWFHistoryList.add(pendingWorkflowsList.get(j));
					}
				}
				map.put(key, fullWFHistoryList);
			}

			if(exportType.equalsIgnoreCase("PDF")) {
				fileExtension = "pdf";
				Font bfBold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, new BaseColor(0, 0, 0));
				Font italicFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC, new BaseColor(0, 0, 0));
				Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.WHITE);
				
				BaseFont unicodeFont = BaseFont.createFont(req.getSession().getServletContext().getRealPath("fonts")+"\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				Font utfFont = new Font(unicodeFont,10,Font.NORMAL);
				Font utfFontItalic = new Font(unicodeFont,10,Font.ITALIC); 
				Paragraph paragraph = new Paragraph();
				Document doc = new Document(PageSize.A4, 36, 36, 36, 72);
				baos = new ByteArrayOutputStream();
				/*PdfWriter.getInstance(doc, baos);
				MyFooter event = new MyFooter();
				PdfWriter.getInstance(doc, baos).setPageEvent(event);
				*/
				
				PdfWriter writer = PdfWriter.getInstance(doc, baos);

		        // add header and footer
		        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
		        writer.setPageEvent(event);
				doc.open();
				
				Set<String> wfIdSet = map.keySet();
				Iterator<String> it = wfIdSet.iterator();

				while (it.hasNext()) {
					String wfIDKey = it.next().toString();
					ArrayList<PendingWorkItemDetails> list = map.get(wfIDKey);

					PdfPTable table = new PdfPTable(5);
					table.setWidthPercentage(100f);
					Image image = Image.getInstance(req.getSession().getServletContext().getRealPath("images")+"\\logo-new.png");
					image.setAlignment(Image.LEFT);

					paragraph.add(new Chunk(image, 0, 0, true));
					PdfPCell cell = new PdfPCell();
					cell.addElement(paragraph);
					PdfPTable table1 = new PdfPTable(5);			

					table1.addCell(cell);
					table1.setLockedWidth(true);
					float[] columnWidths = new float[]{35f, 35f, 10f, 10f, 10f};
					table.setWidths(columnWidths);
					
					paragraph.add(new Paragraph(""));
					paragraph.add(userFullName);
					
					paragraph.add(new Paragraph(""));
					boolean isArabic = isArabicText(list.get(0).getSubject());
					PdfPTable table3 = new PdfPTable(1);// (columnWidths);
					table3.setWidthPercentage(100f);
					PdfPCell cell3 = new PdfPCell(new Phrase(list.get(0).getSubject(),utfFont));
					cell3.setBorder(Rectangle.NO_BORDER);
					if(isArabic){
						cell3.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
						cell3.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
					}else{
						cell3.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
					}
					table3.addCell(cell3);
					paragraph.add(table3);			    
					
					paragraph.add(new Paragraph(""));
					paragraph.add(list.get(0).getWorkitemStatus().trim()+"("+list.get(0).getWorkitemStatus().trim()+")");
					
					paragraph.add(new Paragraph(""));
					paragraph.add("Workflow Begin Date");
					paragraph.add("       ");
					paragraph.add("Workflow Receive Date");
					paragraph.add("       ");
					paragraph.add("Workflow Deadline");
					paragraph.add(new Paragraph(""));
					paragraph.add(list.get(0).getWorkflowBeginDate());
					paragraph.add("                      ");
					paragraph.add(list.get(0).getReceiveDate());
					paragraph.add("                          ");
					paragraph.add(list.get(0).getDeadLine());

					paragraph.add(new Paragraph(""));
					paragraph.add("\n");
					PdfPTable table4 = new PdfPTable(1);// (columnWidths);
					table4.setWidthPercentage(100f);
					PdfPCell cell1 = new PdfPCell(new Phrase("History of "+list.get(0).getSubject(),utfFont));
					cell1.setColspan(1);
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setUseDescender(true);
					if(isArabic){
						cell1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
						cell1.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
					}else{
						cell1.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
					}
					table4.addCell(cell1);
					paragraph.add(table4);
					

					PdfPCell hC6 = insertCell1(table, "Sender", Element.ALIGN_LEFT, 1, font);
					PdfPCell hC7 = insertCell1(table, "Recipient", Element.ALIGN_LEFT, 1, font);
					PdfPCell hC8 = insertCell1(table, "Status", Element.ALIGN_LEFT, 1, font);
					PdfPCell hC9 = insertCell1(table, "Type", Element.ALIGN_LEFT, 1, font);				
					PdfPCell hC10= insertCell1(table, "Action Date", Element.ALIGN_LEFT, 1, font);

					hC6.setBackgroundColor(new BaseColor(128, 128, 128));
					hC7.setBackgroundColor(new BaseColor(128, 128, 128));
					hC8.setBackgroundColor(new BaseColor(128, 128, 128));
					hC9.setBackgroundColor(new BaseColor(128, 128, 128));
					hC10.setBackgroundColor(new BaseColor(128, 128, 128));

					table.addCell(hC6);
					table.addCell(hC7);
					table.addCell(hC8);
					table.addCell(hC9);
					table.addCell(hC10);

					for (int i = 0; i < list.size(); i++) {

						if ( i % 2 == 0 ) {
							PdfPCell hC1 = insertCell1(table, list.get(i).getSender(), Element.ALIGN_LEFT, 1, bfBold);
							PdfPCell hC2 = insertCell1(table, list.get(i).getRecipient(), Element.ALIGN_LEFT, 1, bfBold);
							PdfPCell hC3 = insertCell1(table, list.get(i).getWorkitemStatus(), Element.ALIGN_LEFT, 1, bfBold);
							PdfPCell hC4 = insertCell1(table, list.get(i).getSystemStatus(), Element.ALIGN_LEFT, 1, bfBold);				
							PdfPCell hC5 = insertCell1(table, list.get(i).getActionDate(), Element.ALIGN_LEFT, 1, bfBold);

							PdfPCell comments = null;
							if(((list.get(i).getComments()) != null) && (!(list.get(i).getComments()).equalsIgnoreCase("undefined"))) {
								comments = insertCellAR(table, list.get(i).getSender()+" Comments: "+list.get(i).getComments(), Element.ALIGN_LEFT, 5, utfFontItalic);
							}
							else {
								comments = insertCellAR(table, "No Comments from the Sender", Element.ALIGN_LEFT, 5, utfFontItalic);
							}
							boolean isCommentArabic = isArabicText(list.get(0).getComments());
							if(isCommentArabic){
								comments.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
								comments.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
							}else{
								comments.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
							}
							table.addCell(hC1);
							table.addCell(hC2);
							table.addCell(hC3);
							table.addCell(hC4);
							table.addCell(hC5);
							table.addCell(comments);

						}

						else {
							PdfPCell hC1 = insertCell1(table, list.get(i).getSender(), Element.ALIGN_LEFT, 1, bfBold);
							PdfPCell hC2 = insertCell1(table, list.get(i).getRecipient(), Element.ALIGN_LEFT, 1, bfBold);
							PdfPCell hC3 = insertCell1(table, list.get(i).getWorkitemStatus(), Element.ALIGN_LEFT, 1, bfBold);
							PdfPCell hC4 = insertCell1(table, list.get(i).getSystemStatus(), Element.ALIGN_LEFT, 1, bfBold);				
							PdfPCell hC5 = insertCell1(table, list.get(i).getActionDate(), Element.ALIGN_LEFT, 1, bfBold);

							PdfPCell comments = null;
							if(((list.get(i).getComments()) != null) && (!(list.get(i).getComments()).equalsIgnoreCase("undefined"))) {
								comments = insertCell1(table, list.get(i).getSender()+" Comments: "+list.get(i).getComments(), Element.ALIGN_LEFT, 5, utfFontItalic);
							}
							else {
								comments = insertCell1(table, "No Comments from the Sender", Element.ALIGN_LEFT, 5, utfFontItalic);
							}
							boolean isCommentArabic = isArabicText(list.get(0).getComments());
							if(isCommentArabic){
								comments.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
								comments.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
							}else{
								comments.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
							}
							hC1.setBackgroundColor(new BaseColor(245, 245, 245));
							hC2.setBackgroundColor(new BaseColor(245, 245, 245));
							hC3.setBackgroundColor(new BaseColor(245, 245, 245));
							hC4.setBackgroundColor(new BaseColor(245, 245, 245));
							hC5.setBackgroundColor(new BaseColor(245, 245, 245));
							comments.setBackgroundColor(new BaseColor(245, 245, 245));

							table.addCell(hC1);
							table.addCell(hC2);
							table.addCell(hC3);
							table.addCell(hC4);
							table.addCell(hC5);
							table.addCell(comments);

						}
					}
					
					paragraph.add(table);
					doc.add(paragraph);
					paragraph.clear();
					doc.newPage();
				}
				doc.close();
				resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode("Pending Workflow Full History."+fileExtension,"UTF-8").replaceAll("\\+"," ")  + "\"");
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
				WritableWorkbook fullHistoryWorkbook = null;

				try {
					resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode("Pending Workflow Full History."+fileExtension,"UTF-8").replaceAll("\\+"," ")  + "\"");
					resp.setContentType("application/vnd.ms-excel");

					fullHistoryWorkbook = jxl.Workbook.createWorkbook(resp.getOutputStream());
					Set<String> wfIdSet = map.keySet();
					Iterator<String> it = wfIdSet.iterator();
					int count = wfIdSet.size();
					while (it.hasNext()) {
						String wfIDKey = it.next().toString();
						ArrayList<PendingWorkItemDetails> list = map.get(wfIDKey);

						WritableSheet workSheet = fullHistoryWorkbook.createSheet("Sheet"+(wfIdSet.size()- count), (wfIdSet.size()- count));

						java.io.File imageFile = new java.io.File(req.getSession().getServletContext().getRealPath("images")+"\\logo-new.png");
						BufferedImage input = ImageIO.read(imageFile);
						baos = new ByteArrayOutputStream();
						ImageIO.write(input, "PNG", baos);
						workSheet.addImage(new WritableImage(0,0,0.95,5.6,baos.toByteArray()));

						CellView cell = workSheet.getRowView(12);
						workSheet.setColumnView(0, 50);
						cell.setAutosize(true);
						workSheet.setColumnView(1, cell);
						cell.setAutosize(true);
						workSheet.setColumnView(2, cell);
						cell.setAutosize(true);
						workSheet.setColumnView(3, cell);
						cell.setAutosize(true);
						workSheet.setColumnView(4, cell);
						
						cell.setAutosize(true);
						workSheet.setColumnView(5, cell);
						
						WritableCellFormat cFormat = null;

						cFormat = fontBold();						
						Label label = new Label(0, 7, userFullName, cFormat);
						workSheet.addCell(label);

						cFormat = fontNoBold();						
						label = new Label(0, 9, list.get(0).getSubject(), cFormat);
						workSheet.addCell(label);

						label = new Label(0, 10, list.get(0).getWorkitemStatus().trim()+"("+list.get(0).getWorkitemStatus().trim()+")", cFormat);
						workSheet.addCell(label);

						cFormat = fontBoldItalic();
						label = new Label(0, 12, "Workflow Begin Date", cFormat);
						workSheet.addCell(label);
						label = new Label(1, 12, "Workflow Receive Date", cFormat);
						workSheet.addCell(label);
						label = new Label(2, 12, "Workflow Deadline", cFormat);
						workSheet.addCell(label);

						cFormat = fontNoBold();
						label = new Label(0, 13, list.get(0).getWorkflowBeginDate(), cFormat);
						workSheet.addCell(label);
						label = new Label(1, 13, list.get(0).getReceiveDate(), cFormat);
						workSheet.addCell(label);
						label = new Label(2, 13, list.get(0).getDeadLine(), cFormat);
						workSheet.addCell(label);

						cFormat = fontBold();
						label = new Label(0, 15, "History of "+list.get(0).getSubject(), cFormat);
						workSheet.addCell(label);

						cFormat = fontBoldAndBackGround();
						label = new Label(0, 16, "Sender", cFormat);
						workSheet.addCell(label);
						label = new Label(1, 16, "Recipient", cFormat);
						workSheet.addCell(label);
						label = new Label(2, 16, "Status", cFormat);
						workSheet.addCell(label);
						label = new Label(3, 16, "Type", cFormat);
						workSheet.addCell(label);
						label = new Label(4, 16, "Action Date", cFormat);
						workSheet.addCell(label);
						label = new Label(5, 16, "Comments", cFormat);
						workSheet.addCell(label);

						int rowNumber = 17;
						for (int i = 0; i < list.size() ; i++) {

							cFormat = fontNoBoldAndBackGround();							
							if(i % 2 == 0) {
								label = new Label(0, rowNumber, list.get(i).getSender(), cFormat);
								workSheet.addCell(label);
								label = new Label(1, rowNumber, list.get(i).getRecipient(), cFormat);
								workSheet.addCell(label);
								label = new Label(2, rowNumber, list.get(i).getWorkitemStatus(), cFormat);
								workSheet.addCell(label);
								label = new Label(3, rowNumber, list.get(i).getSystemStatus(), cFormat);
								workSheet.addCell(label);
								label = new Label(4, rowNumber, list.get(i).getActionDate(), cFormat);
								workSheet.addCell(label);
								if(((list.get(i).getComments()) != null) && (!(list.get(i).getComments()).equalsIgnoreCase("undefined"))) {
									label = new Label(5, rowNumber, list.get(i).getSender()+" Comments: "+list.get(i).getComments(), cFormat);
									workSheet.addCell(label);
								}
								else {
									label = new Label(5, rowNumber, "No Comments from the Sender", cFormat);
									workSheet.addCell(label);
								}
							}

							else {
								cFormat = fontNoBold();
								label = new Label(0, rowNumber, list.get(i).getSender(), cFormat);
								workSheet.addCell(label);
								label = new Label(1, rowNumber, list.get(i).getRecipient(), cFormat);
								workSheet.addCell(label);
								label = new Label(2, rowNumber, list.get(i).getWorkitemStatus(), cFormat);
								workSheet.addCell(label);
								label = new Label(3, rowNumber, list.get(i).getSystemStatus(), cFormat);
								workSheet.addCell(label);
								label = new Label(4, rowNumber, list.get(i).getActionDate(), cFormat);
								workSheet.addCell(label);
								if(((list.get(i).getComments()) != null) && (!(list.get(i).getComments()).equalsIgnoreCase("undefined"))) {
									label = new Label(5, rowNumber, list.get(i).getSender()+" Comments: "+list.get(i).getComments(), cFormat);
									workSheet.addCell(label);
								}
								else {
									label = new Label(5, rowNumber, "No Comments from the Sender", cFormat);
									workSheet.addCell(label);
								}

							}

							rowNumber++;
						}
						count--;
					}

					fullHistoryWorkbook.write();
					System.out.println("Total Time   :"+(System.currentTimeMillis()-startTime));
				} catch (WriteException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (fullHistoryWorkbook != null) {
						try {
							fullHistoryWorkbook.close();
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
	
	private static WritableCellFormat fontMoreWithBold() {
		WritableCellFormat cFormat = new WritableCellFormat();
		WritableFont font = new WritableFont(WritableFont.ARIAL, 15, WritableFont.BOLD);
		cFormat.setFont(font);
		return cFormat;
	}

	private static WritableCellFormat fontNoBold() throws Exception {
		WritableCellFormat cFormat = new WritableCellFormat();
		WritableFont font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.NO_BOLD);
		cFormat.setFont(font);
		return cFormat;
	}
	private static WritableCellFormat strikeFont() throws Exception {
		WritableCellFormat cFormat = new WritableCellFormat();
		WritableFont font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
		font.setStruckout(true);
		cFormat.setFont(font);
		return cFormat;
	}
	private static WritableCellFormat redFont() throws Exception {
		WritableCellFormat cFormat = new WritableCellFormat();
		WritableFont font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.NO_BOLD);
		font.setColour(Colour.RED);
		cFormat.setFont(font);
		return cFormat;
	}
	private static WritableCellFormat fontBoldItalic() {
		WritableCellFormat cFormat = new WritableCellFormat();
		WritableFont font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
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
	class MyFooter extends PdfPageEventHelper {
        Font ffont = new Font(Font.FontFamily.UNDEFINED, 5, Font.ITALIC);
 
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase header = new Phrase(" header");
            Phrase footer = new Phrase("page " + document.getPageNumber(), ffont);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    header,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + 10, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
        }
    }
	
	@GET
	@Path("/pendingWorkflowsReport")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)//@Produces(MediaType.APPLICATION_XML)
	public void pendingWorkflowsReport(@QueryParam("sender") String sender, @QueryParam("status") String status,@QueryParam("overdue") String overdue,@QueryParam("recipient") String recipient,@QueryParam("from") String from,@QueryParam("to") String to, @QueryParam("exportType") String exportType, @Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		ArrayList<PendingWorkItemDetails> pendingWorkflowsList = null;
		String fileExtension = null;
		OutputStream os = resp.getOutputStream();
		ByteArrayOutputStream baos = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			pendingWorkflowsList = ws.pendingWorkflows(sender, status, overdue, recipient, from, to);

			int totalDoneWorkitems = 0;
			int totalNotDoneWorkitems = 0;
			int totalWItems = pendingWorkflowsList.size();

			for (int j = 0; j < pendingWorkflowsList.size(); j++) {

				if((pendingWorkflowsList.get(j).getWorkitemStatus()).equalsIgnoreCase("Done")) {
					totalDoneWorkitems++;
				}
				else {
					totalNotDoneWorkitems++;
				}
			}

			if(exportType.equalsIgnoreCase("PDF")) {
				fileExtension = "pdf";
				Document doc = null;
				Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD, new BaseColor(0, 0, 0));
				Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.WHITE);
				Font bfBold = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL, new BaseColor(0, 0, 0));
				Font strikeBold = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.STRIKETHRU, new BaseColor(0, 0, 0));
				Font redBold = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL, new BaseColor(255, 0, 0));
				Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
				Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.ORANGE);
				Paragraph paragraph = new Paragraph();
				doc = new Document();
				baos = new ByteArrayOutputStream();
				PdfWriter.getInstance(doc, baos);
				doc.open();
				PdfPTable table = new PdfPTable(5);// (columnWidths);
				table.setWidthPercentage(100f);

				float[] columnWidths = new float[]{40f, 20f, 15f, 15f, 15f};
				table.setWidths(columnWidths);

				Image image = Image.getInstance(req.getSession().getServletContext().getRealPath("images")+"\\logo-new.png");
				image.setAlignment(Image.LEFT);

				paragraph.add(new Chunk(image, 0, 0, true));
				PdfPCell cell = new PdfPCell();
				cell.addElement(paragraph);
				PdfPTable table1 = new PdfPTable(5);
				table1.addCell(cell);

				PdfPCell hC26 = insertCell1(table, "Workflows From", Element.	ALIGN_LEFT, 1, bfBold12);
				PdfPCell hC27 = insertCell1(table, pendingWorkflowsList.get(0).getSender(), Element.ALIGN_LEFT, 4, bfBold12);
				PdfPCell hC28 = insertCell1(table, "Workflows Begin Date", Element.ALIGN_LEFT, 1, font1);
				PdfPCell hC29 = null;
				if(pendingWorkflowsList.get(0).getFromDate() != null) {
					hC29 = insertCell1(table, pendingWorkflowsList.get(0).getFromDate(), Element.ALIGN_LEFT, 4, font1);
				}
				if(pendingWorkflowsList.get(0).getToDate() != null) {
					hC29 = insertCell1(table, pendingWorkflowsList.get(0).getToDate(), Element.ALIGN_LEFT, 4, font1);
				}
				if(pendingWorkflowsList.get(0).getFromDate() != null && pendingWorkflowsList.get(0).getToDate() != null) {
					hC29 = insertCell1(table, pendingWorkflowsList.get(0).getFromDate()+" Till "+pendingWorkflowsList.get(0).getToDate(), Element.ALIGN_LEFT, 4, font1);
				}
				if(pendingWorkflowsList.get(0).getFromDate() == null && pendingWorkflowsList.get(0).getToDate() == null) {
					hC29 = insertCell1(table, "All Workflows", Element.ALIGN_LEFT, 4, font1);
				}

				PdfPCell hC30 = insertCell1(table, "All Items (Done & Not Done)", Element.ALIGN_LEFT, 5, font2);

				PdfPCell sapce1 = insertCell1(table, "", Element.ALIGN_LEFT, 5, bfBold);
				sapce1.setFixedHeight(50);
				table.addCell(sapce1);

				table.addCell(hC26);
				table.addCell(hC27);
				table.addCell(hC28);
				table.addCell(hC29);
				table.addCell(hC30);

				PdfPCell hC16 = insertCell1(table, "Recipient", Element.	ALIGN_LEFT, 1, bfBold12);
				PdfPCell hC17 = insertCell1(table, "% Done", Element.ALIGN_LEFT, 1, bfBold12);
				PdfPCell hC18 = insertCell1(table, "Total", Element.ALIGN_LEFT, 1, bfBold12);
				PdfPCell hC19 = insertCell1(table, "Done", Element.ALIGN_LEFT, 1, bfBold12);				
				PdfPCell hC20 = insertCell1(table, "Not Done", Element.ALIGN_LEFT, 1, bfBold12);

				table.addCell(hC16);
				table.addCell(hC17);
				table.addCell(hC18);
				table.addCell(hC19);
				table.addCell(hC20);

				String key = null;
				HashMap<String, ArrayList<PendingWorkItemDetails>> map = new HashMap<String, ArrayList<PendingWorkItemDetails>>();
				for (int i = 0; i < pendingWorkflowsList.size(); i++) {
					ArrayList<PendingWorkItemDetails> pendingWFs = new ArrayList<PendingWorkItemDetails>();
					key = pendingWorkflowsList.get(i).getRecipient();
					for (int j = 0; j < pendingWorkflowsList.size(); j++) {
						if(pendingWorkflowsList.get(j).getRecipient().equalsIgnoreCase(pendingWorkflowsList.get(i).getRecipient())){
							pendingWFs.add(pendingWorkflowsList.get(j));
						}
					}
					map.put(key, pendingWFs);
				}
				Set<String> recipientName = map.keySet();
				Iterator<String> it = recipientName.iterator();
				while (it.hasNext()) {
					String recipienKey = it.next().toString();
					ArrayList<PendingWorkItemDetails> list = map.get(recipienKey);

					int doneWorkitems = 0;
					int notDoneWorkitems = 0;
					int totalWorkitems = list.size();
					int donePercentage = 0;

					for (int j = 0; j < list.size(); j++) {

						if((list.get(j).getWorkitemStatus()).equalsIgnoreCase("Done")) {
							doneWorkitems++;
						}
						else {
							notDoneWorkitems++;
						}
					}
					donePercentage = (doneWorkitems * 100)/totalWorkitems;

					PdfPCell hC11 = insertCell1(table, recipienKey, Element.ALIGN_LEFT, 1, font);
					PdfPCell hC12 = insertCell1(table, donePercentage+"%", Element.ALIGN_LEFT, 1, font);
					PdfPCell hC13 = insertCell1(table, ""+totalWorkitems, Element.ALIGN_LEFT, 1, font);
					PdfPCell hC14 = insertCell1(table, ""+doneWorkitems, Element.ALIGN_LEFT, 1, font);				
					PdfPCell hC15 = insertCell1(table, ""+notDoneWorkitems, Element.ALIGN_LEFT, 1, font);

					hC11.setBackgroundColor(BaseColor.DARK_GRAY);
					hC12.setBackgroundColor(BaseColor.DARK_GRAY);
					hC13.setBackgroundColor(BaseColor.DARK_GRAY);
					hC14.setBackgroundColor(BaseColor.DARK_GRAY);
					hC15.setBackgroundColor(BaseColor.DARK_GRAY);

					table.addCell(hC11);
					table.addCell(hC12);
					table.addCell(hC13);
					table.addCell(hC14);
					table.addCell(hC15);

					PdfPCell hC6 = insertCell1(table, "Subject", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC7 = insertCell1(table, "Begin Date", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC8 = insertCell1(table, "Status", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC9 = insertCell1(table, "Receive Date", Element.ALIGN_LEFT, 1, bfBold12);				
					PdfPCell hC10= insertCell1(table, "DeadLine", Element.ALIGN_LEFT, 1, bfBold12);

					hC6.setBackgroundColor(BaseColor.LIGHT_GRAY);
					hC7.setBackgroundColor(BaseColor.LIGHT_GRAY);
					hC8.setBackgroundColor(BaseColor.LIGHT_GRAY);
					hC9.setBackgroundColor(BaseColor.LIGHT_GRAY);
					hC10.setBackgroundColor(BaseColor.LIGHT_GRAY);

					table.addCell(hC6);
					table.addCell(hC7);
					table.addCell(hC8);
					table.addCell(hC9);
					table.addCell(hC10);

					for (int i = 0; i < list.size(); i++) {
						if(list.get(i).getWorkitemStatus().equalsIgnoreCase("Done")){
							PdfPCell hC1 = insertCell1(table, list.get(i).getSubject(), Element.ALIGN_LEFT, 1, strikeBold);
							PdfPCell hC2 = insertCell1(table, list.get(i).getWorkflowBeginDate(), Element.ALIGN_LEFT, 1, strikeBold);
							PdfPCell hC3 = insertCell1(table, list.get(i).getWorkitemStatus(), Element.ALIGN_LEFT, 1, strikeBold);
							PdfPCell hC4 = insertCell1(table, list.get(i).getReceiveDate(), Element.ALIGN_LEFT, 1, strikeBold);	
							PdfPCell hC5 = insertCell1(table, list.get(i).getDeadLine(), Element.ALIGN_LEFT, 1, strikeBold);
							
							table.addCell(hC1);
							table.addCell(hC2);
							table.addCell(hC3);
							table.addCell(hC4);
							table.addCell(hC5);
						}else {
							PdfPCell hC1 = insertCell1(table, list.get(i).getSubject(), Element.ALIGN_LEFT, 1, bfBold);
							PdfPCell hC2 = insertCell1(table, list.get(i).getWorkflowBeginDate(), Element.ALIGN_LEFT, 1, bfBold);
							PdfPCell hC3 = insertCell1(table, list.get(i).getWorkitemStatus(), Element.ALIGN_LEFT, 1, bfBold);
							PdfPCell hC4 = insertCell1(table, list.get(i).getReceiveDate(), Element.ALIGN_LEFT, 1, bfBold);	
							table.addCell(hC1);
							table.addCell(hC2);
							table.addCell(hC3);
							table.addCell(hC4);
							if(list.get(i).getIsOverdue().equalsIgnoreCase("true")){
								PdfPCell hC5 = insertCell1(table, list.get(i).getDeadLine(), Element.ALIGN_LEFT, 1, redBold);
								table.addCell(hC5);
							}else{
								PdfPCell hC5 = insertCell1(table, list.get(i).getDeadLine(), Element.ALIGN_LEFT, 1, bfBold);
								table.addCell(hC5);
							}
						}
					}
					paragraph.add(table);
					doc.add(paragraph);

					table.flushContent();
					paragraph.clear();

				}

				PdfPCell hC21 = insertCell(table, "Grand Total", Element.ALIGN_LEFT, 1, bfBold12);
				PdfPCell hC22 = insertCell(table, "", Element.ALIGN_LEFT, 1, bfBold12);
				PdfPCell hC23 = insertCell(table, ""+totalWItems, Element.ALIGN_LEFT, 1, bfBold12);
				PdfPCell hC24 = insertCell(table, ""+totalDoneWorkitems, Element.ALIGN_LEFT, 1, bfBold12);				
				PdfPCell hC25= insertCell(table, ""+totalNotDoneWorkitems, Element.ALIGN_LEFT, 1, bfBold12);

				table.addCell(hC21);
				table.addCell(hC22);
				table.addCell(hC23);
				table.addCell(hC24);
				table.addCell(hC25);

				paragraph.add(table);
				doc.add(paragraph);

				doc.close();

				resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode("Pending Workflow."+fileExtension,"UTF-8").replaceAll("\\+"," ")  + "\"");
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
				WritableWorkbook pendwiWorkbook = null;

				try {
					resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode("Pending Workflows."+fileExtension,"UTF-8").replaceAll("\\+"," ")  + "\"");
					resp.setContentType("application/vnd.ms-excel");

					pendwiWorkbook = jxl.Workbook.createWorkbook(resp.getOutputStream());

					WritableSheet workSheet = pendwiWorkbook.createSheet("PendingWFHFilter", 0);

					java.io.File imageFile = new java.io.File(req.getSession().getServletContext().getRealPath("images")+"\\logo-new.png");
					BufferedImage input = ImageIO.read(imageFile);
					baos = new ByteArrayOutputStream();
					ImageIO.write(input, "PNG", baos);
					workSheet.addImage(new WritableImage(0,0,0.75,5.5,baos.toByteArray()));

					CellView cell = workSheet.getRowView(12);
					cell.setAutosize(true);
					workSheet.setColumnView(0, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(1, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(2, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(3, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(4, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(5, cell);

					WritableCellFormat cFormat = null;

					cFormat = fontBold();
					Label label = new Label(0, 7, "Workflows From", cFormat);
					workSheet.addCell(label);					
					cFormat = fontNoBold();
					label = new Label(1, 7, pendingWorkflowsList.get(0).getSender(), cFormat);
					workSheet.addCell(label);

					cFormat = fontBold();
					label = new Label(0, 8, "Workflows Begin Date", cFormat);
					workSheet.addCell(label);					
					cFormat = fontNoBold();					
					if(pendingWorkflowsList.get(0).getFromDate() != null) {
						label = new Label(1, 8, pendingWorkflowsList.get(0).getFromDate(), cFormat);
					}
					if(pendingWorkflowsList.get(0).getToDate() != null) {
						label = new Label(1, 8, pendingWorkflowsList.get(0).getToDate(), cFormat);
					}
					if(pendingWorkflowsList.get(0).getFromDate() != null && pendingWorkflowsList.get(0).getToDate() != null) {
						label = new Label(1, 8, pendingWorkflowsList.get(0).getFromDate()+"  Till  "+pendingWorkflowsList.get(0).getToDate(), cFormat);
					}
					if(pendingWorkflowsList.get(0).getFromDate() == null && pendingWorkflowsList.get(0).getToDate() == null) {
						label = new Label(1, 8, "All Workflows", cFormat);
					}
					workSheet.addCell(label);

					cFormat = fontBold();
					label = new Label(0, 9, "All Items (Done & Not Done)", cFormat);
					workSheet.addCell(label);

					cFormat = fontBold();
					label = new Label(0, 11, "Recipient", cFormat);
					workSheet.addCell(label);
					label = new Label(1, 11, "% Done", cFormat);
					workSheet.addCell(label);
					label = new Label(2, 11, "Total", cFormat);
					workSheet.addCell(label);
					label = new Label(3, 11, "Done", cFormat);
					workSheet.addCell(label);
					label = new Label(4, 11, "Not Done", cFormat);
					workSheet.addCell(label);

					String key = null;
					HashMap<String, ArrayList<PendingWorkItemDetails>> map = new HashMap<String, ArrayList<PendingWorkItemDetails>>();
					for (int i = 0; i < pendingWorkflowsList.size(); i++) {
						ArrayList<PendingWorkItemDetails> pendingWFs = new ArrayList<PendingWorkItemDetails>();
						key = pendingWorkflowsList.get(i).getRecipient();
						for (int j = 0; j < pendingWorkflowsList.size(); j++) {
							if(pendingWorkflowsList.get(j).getRecipient().equalsIgnoreCase(pendingWorkflowsList.get(i).getRecipient())){
								pendingWFs.add(pendingWorkflowsList.get(j));
							}
						}
						map.put(key, pendingWFs);
					}
					Set<String> recipientName = map.keySet();
					Iterator<String> it = recipientName.iterator();
					int rowNumber = 12;
					while (it.hasNext()) {
						String recipienKey = it.next().toString();
						ArrayList<PendingWorkItemDetails> list = map.get(recipienKey);

						int doneWorkitems = 0;
						int notDoneWorkitems = 0;
						int totalWorkitems = list.size();
						int donePercentage = 0;

						for (int j = 0; j < list.size(); j++) {

							if((list.get(j).getWorkitemStatus()).equalsIgnoreCase("Done")) {
								doneWorkitems++;
							}
							else {
								notDoneWorkitems++;
							}
						}
						donePercentage = (doneWorkitems * 100)/totalWorkitems;

						cFormat = fontBoldAndBackGround();
						label = new Label(0, rowNumber, recipienKey, cFormat);
						workSheet.addCell(label);
						label = new Label(1, rowNumber, donePercentage+"%", cFormat);
						workSheet.addCell(label);
						label = new Label(2, rowNumber, ""+totalWorkitems, cFormat);
						workSheet.addCell(label);
						label = new Label(3, rowNumber, ""+doneWorkitems, cFormat);
						workSheet.addCell(label); 
						label = new Label(4, rowNumber, ""+notDoneWorkitems, cFormat);
						workSheet.addCell(label);

						rowNumber = rowNumber+1;
						cFormat = fontBoldWithColor();
						label = new Label(0, rowNumber, "Subject", cFormat);
						workSheet.addCell(label);
						label = new Label(1, rowNumber, "Workflow Begin Date", cFormat);
						workSheet.addCell(label);
						label = new Label(2, rowNumber, "Status", cFormat);
						workSheet.addCell(label);
						label = new Label(3, rowNumber, "Receive Date", cFormat);
						workSheet.addCell(label); 
						label = new Label(4, rowNumber, "DeadLine", cFormat);
						workSheet.addCell(label);

						int noOfRows = rowNumber+1;
						
						
						cFormat = fontNoBold();
						for (int i = 0; i < list.size(); i++) {
							if((list.get(i).getWorkitemStatus()).equalsIgnoreCase("Done")) {
								
								cFormat = strikeFont();
							}
							label = new Label(0, noOfRows, list.get(i).getSubject(), cFormat);
							workSheet.addCell(label);
							label = new Label(1, noOfRows, list.get(i).getWorkflowBeginDate(), cFormat);
							workSheet.addCell(label);
							label = new Label(2, noOfRows, list.get(i).getWorkitemStatus(), cFormat);
							workSheet.addCell(label);
							label = new Label(3, noOfRows, list.get(i).getReceiveDate(), cFormat);
							workSheet.addCell(label);
							if((list.get(i).getIsOverdue()).equalsIgnoreCase("true")){
								cFormat = redFont();
							}
							label = new Label(4, noOfRows, list.get(i).getDeadLine(), cFormat);
							workSheet.addCell(label);

							noOfRows++;

						}

						rowNumber = noOfRows;

					}

					cFormat = fontNoBoldAndBackGround();
					int rwNo = rowNumber;
					label = new Label(0, rwNo, "Grand Total", cFormat);
					
					workSheet.addCell(label);
					label = new Label(1, rwNo, "", cFormat);
					workSheet.addCell(label);
					label = new Label(2, rwNo, ""+totalWItems, cFormat);
					workSheet.addCell(label);
					label = new Label(3, rwNo, ""+totalDoneWorkitems, cFormat);
					workSheet.addCell(label);
					label = new Label(4, rwNo, ""+totalNotDoneWorkitems, cFormat);
					workSheet.addCell(label);

					pendwiWorkbook.write();
					System.out.println("Total Time   :"+(System.currentTimeMillis()-startTime));

				} catch (WriteException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (pendwiWorkbook != null) {
						try {
							pendwiWorkbook.close();
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
	private PdfPCell insertCellAR(PdfPTable table, String text, int align, int colspan, Font font) throws Exception, Exception {
		if (text == null || text.trim().equalsIgnoreCase("NULL")) {
			text = "";
		}
		PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
		cell.setHorizontalAlignment(align);
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setNoWrap(false);
		cell.setColspan(colspan);
		cell.setBorder(Rectangle.NO_BORDER);
		if (text.trim().equalsIgnoreCase("")) {
			cell.setMinimumHeight(10f);
		}
		return cell;
		
	}
	
	private PdfPCell insertCell1(PdfPTable table, String text, int align, int colspan, Font font) throws Exception, Exception {
		if (text == null || text.trim().equalsIgnoreCase("NULL")) {
			text = "";
		}
		PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
		cell.setHorizontalAlignment(align);
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setNoWrap(false);
		cell.setColspan(colspan);
		cell.setBorder(Rectangle.NO_BORDER);
		if (text.trim().equalsIgnoreCase("")) {
			cell.setMinimumHeight(10f);
		}
		return cell;
		
		
		/*Font font12 = FontFactory.getFont("C:\\Users\\p8admin\\ICMWorkSpace\\DIMS\\WebContent\\fonts\\ARABTYPE.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
	    Phrase phrase = new Phrase();
        phrase.add(new Chunk(text.trim(), font12));
        
		PdfPCell cell = new PdfPCell(phrase);
		cell.setHorizontalAlignment(align);
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setNoWrap(false);
		cell.setColspan(colspan);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setUseDescender(true);
        cell.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
		if (text.trim().equalsIgnoreCase("")) {
			cell.setMinimumHeight(10f);
		}
		
		Font f = FontFactory.getFont("C:\\Users\\p8admin\\ICMWorkSpace\\DIMS\\WebContent\\fonts\\ARABTYPE.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Phrase phrase = new Phrase();
        phrase.add(new Chunk(text.trim(), f));
        PdfPCell cell = new PdfPCell(phrase);
        //cell.setUseDescender(true);
        //cell.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        
		return cell;*/
	}
	
	private PdfPCell insertCellnoWrap(PdfPTable table, String text, int align, int colspan, Font font) throws Exception, Exception {
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
	
	private PdfPCell insertRow1(PdfPTable table, String text, int align, int colspan, Font font) {
		if (text == null || text.trim().equalsIgnoreCase("NULL")) {
			text = "";
		}
		PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
		cell.setHorizontalAlignment(align);
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setColspan(colspan);
		BaseColor myColor = WebColors.getRGBColor("#999");
		cell.setBackgroundColor(myColor);
		cell.setBorder(Rectangle.NO_BORDER);
		if (text.trim().equalsIgnoreCase("")) {
			cell.setMinimumHeight(10f);
		}
		return cell;
	}
	
	@POST
	@Path("/canvasImage1")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void canvasImage1(@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		ServletFileUpload uploader = new ServletFileUpload(new DiskFileItemFactory());
		String sender = null;
		String status = null;
		String overdue = null;  
		String recipient = null;
		String from = null;
		String to = null;
		String exportType = null;
		InputStream initialStream = null;
		String templateName = "template"+System.currentTimeMillis()+".png";

		@SuppressWarnings("unchecked")
		List<FileItem> parseRequest = uploader.parseRequest(req);

		for (FileItem fileItem : parseRequest) {

			if (fileItem.isFormField()) {

				if(fileItem.getFieldName().equalsIgnoreCase("sender")){
					sender = fileItem.getString();
				}
				if(fileItem.getFieldName().equalsIgnoreCase("status")){
					status = fileItem.getString();
				}
				if(fileItem.getFieldName().equalsIgnoreCase("overdue")){
					overdue = fileItem.getString();
				}
				if(fileItem.getFieldName().equalsIgnoreCase("recipient")){
					recipient = fileItem.getString();
				}
				if(fileItem.getFieldName().equalsIgnoreCase("from")){
					from = fileItem.getString();
				}
				if(fileItem.getFieldName().equalsIgnoreCase("to")){
					to = fileItem.getString();
				}
				if(fileItem.getFieldName().equalsIgnoreCase("exportType")){
					exportType = fileItem.getString();
				}  

			}else{
				initialStream = fileItem.getInputStream();
			}
		}

		ArrayList<PendingWorkItemDetails> pendingWorkflowsList = null;
		String fileExtension = null;

		
		OutputStream os = resp.getOutputStream();
		ByteArrayOutputStream baos = null;
		try{
			WorkFlowService ws = new WorkFlowService(req,resp);
			pendingWorkflowsList = ws.pendingWorkflows(sender, status, overdue, recipient, from, to);

			int totalDoneWorkitems = 0;
			int totalNotDoneWorkitems = 0;
			int totalWItems = pendingWorkflowsList.size();

			for (int j = 0; j < pendingWorkflowsList.size(); j++) {

				if((pendingWorkflowsList.get(j).getWorkitemStatus()).equalsIgnoreCase("Done")) {
					totalDoneWorkitems++;
				}
				else {
					totalNotDoneWorkitems++;
				}
			}
			
			byte[] buffer = new byte[initialStream.available()];
			initialStream.read(buffer);

			if(exportType.equalsIgnoreCase("PDF")) {
				fileExtension = "pdf";
				Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, new BaseColor(0, 0, 0));
				Font font = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD, BaseColor.WHITE);
				//Font bfBold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, new BaseColor(0, 0, 0));
				
				BaseFont unicodeFont = BaseFont.createFont(req.getSession().getServletContext().getRealPath("fonts")+"\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				Font strikeBold = new Font(unicodeFont,10,Font.STRIKETHRU);
				Font bfBold = new Font(unicodeFont,10,Font.NORMAL);
				Font redBold = new Font(unicodeFont, 10, Font.NORMAL, new BaseColor(255, 0, 0));
				Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL, BaseColor.BLACK);
				Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL, BaseColor.ORANGE);
				Paragraph paragraph = new Paragraph();
				Document doc = new Document(PageSize.A4, 36, 36, 36, 72);
				baos = new ByteArrayOutputStream();
				/*PdfWriter.getInstance(doc, baos);
				MyFooter event = new MyFooter();
				PdfWriter.getInstance(doc, baos).setPageEvent(event);*/
				PdfWriter writer = PdfWriter.getInstance(doc, baos);

		        // add header and footer
		        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
		        writer.setPageEvent(event);
				doc.open();
				PdfPTable table = new PdfPTable(5);// (columnWidths);
				table.setWidthPercentage(100f);

				float[] columnWidths = new float[]{40f, 15f, 10f, 15f, 15f};
				table.setWidths(columnWidths);
				
				Image image = Image.getInstance(req.getSession().getServletContext().getRealPath("images")+"\\logo-new.png");
				image.setAlignment(Image.LEFT);
				paragraph.add(new Chunk(image, 0, 0, true));
				PdfPCell cell = new PdfPCell();
				cell.addElement(paragraph);
				PdfPTable table1 = new PdfPTable(5);
				table1.addCell(cell);

				PdfPTable table5 = new PdfPTable(5);// (columnWidths);
				table5.setWidthPercentage(100f);
				PdfPCell hC26 = insertCell1(table5, "Workflows From", Element.	ALIGN_LEFT, 1, bfBold12);
				PdfPCell hC27 = insertCell1(table5, pendingWorkflowsList.get(0).getSender(), Element.ALIGN_LEFT, 4, bfBold12);
				PdfPCell hC28 = insertCell1(table5, "Workflows Begin Date", Element.ALIGN_LEFT, 1, font1);
				PdfPCell hC29 = null;
				if(pendingWorkflowsList.get(0).getFromDate() != null) {
					hC29 = insertCell1(table5, pendingWorkflowsList.get(0).getFromDate(), Element.ALIGN_LEFT, 4, font1);
				}
				if(pendingWorkflowsList.get(0).getToDate() != null) {
					hC29 = insertCell1(table5, pendingWorkflowsList.get(0).getToDate(), Element.ALIGN_LEFT, 4, font1);
				}
				if(pendingWorkflowsList.get(0).getFromDate() != null && pendingWorkflowsList.get(0).getToDate() != null) {
					hC29 = insertCell1(table5, pendingWorkflowsList.get(0).getFromDate()+" Till "+pendingWorkflowsList.get(0).getToDate(), Element.ALIGN_LEFT, 4, font1);
				}
				if(pendingWorkflowsList.get(0).getFromDate() == null && pendingWorkflowsList.get(0).getToDate() == null) {
					hC29 = insertCell1(table5, "All Workflows", Element.ALIGN_LEFT, 4, font1);
				}

				PdfPCell hC30 = insertCell1(table5, "All Items (Done & Not Done)", Element.ALIGN_LEFT, 5, font2);

				PdfPCell sapce1 = insertCell1(table5, "", Element.ALIGN_LEFT, 5, bfBold);
				sapce1.setFixedHeight(50);
				table5.addCell(sapce1);

				table5.addCell(hC26);
				table5.addCell(hC27);
				table5.addCell(hC28);
				table5.addCell(hC29);
				table5.addCell(hC30);
				
				paragraph.add(table5);
				
				/*Image image2 = Image.getInstance(buffer);
				image2.setAlignment(Image.LEFT);
				paragraph.add(new Chunk(image2, 0, 0, true));
				PdfPCell cell1 = new PdfPCell();
				cell1.addElement(paragraph);

				PdfPTable table2 = new PdfPTable(5);*/
				//table2.addCell(cell1);

				PdfPCell hC16 = insertCell1(table, "Recipient", Element.ALIGN_LEFT, 1, bfBold12);
				PdfPCell hC17 = insertCell1(table, "% Done", Element.ALIGN_LEFT, 1, bfBold12);
				PdfPCell hC18 = insertCell1(table, "Total", Element.ALIGN_LEFT, 1, bfBold12);
				PdfPCell hC19 = insertCell1(table, "Done", Element.ALIGN_LEFT, 1, bfBold12);				
				PdfPCell hC20 = insertCell1(table, "Not Done", Element.ALIGN_LEFT, 1, bfBold12);

				table.addCell(hC16);
				table.addCell(hC17);
				table.addCell(hC18);
				table.addCell(hC19);
				table.addCell(hC20);

				String key = null;
				HashMap<String, ArrayList<PendingWorkItemDetails>> map = new HashMap<String, ArrayList<PendingWorkItemDetails>>();
				for (int i = 0; i < pendingWorkflowsList.size(); i++) {
					ArrayList<PendingWorkItemDetails> pendingWFs = new ArrayList<PendingWorkItemDetails>();
					key = pendingWorkflowsList.get(i).getRecipient();
					for (int j = 0; j < pendingWorkflowsList.size(); j++) {
						if(pendingWorkflowsList.get(j).getRecipient().equalsIgnoreCase(pendingWorkflowsList.get(i).getRecipient())){
							pendingWFs.add(pendingWorkflowsList.get(j));
						}
					}
					map.put(key, pendingWFs);
				}
				Set<String> recipientName = map.keySet();
				Iterator<String> it = recipientName.iterator();
				while (it.hasNext()) {
					String recipienKey = it.next().toString();
					ArrayList<PendingWorkItemDetails> list = map.get(recipienKey);

					int doneWorkitems = 0;
					int notDoneWorkitems = 0;
					int totalWorkitems = list.size();
					int donePercentage = 0;

					for (int j = 0; j < list.size(); j++) {

						if((list.get(j).getWorkitemStatus()).equalsIgnoreCase("Done")) {
							doneWorkitems++;
						}
						else {
							notDoneWorkitems++;
						}
					}
					donePercentage = (doneWorkitems * 100)/totalWorkitems;

					PdfPCell hC11 = insertCell1(table, recipienKey, Element.ALIGN_LEFT, 1, font);
					PdfPCell hC12 = insertCell1(table, donePercentage+"%", Element.ALIGN_LEFT, 1, font);
					PdfPCell hC13 = insertCell1(table, ""+totalWorkitems, Element.ALIGN_LEFT, 1, font);
					PdfPCell hC14 = insertCell1(table, ""+doneWorkitems, Element.ALIGN_LEFT, 1, font);				
					PdfPCell hC15 = insertCell1(table, ""+notDoneWorkitems, Element.ALIGN_LEFT, 1, font);

					hC11.setBackgroundColor(BaseColor.DARK_GRAY);
					hC12.setBackgroundColor(BaseColor.DARK_GRAY);
					hC13.setBackgroundColor(BaseColor.DARK_GRAY);
					hC14.setBackgroundColor(BaseColor.DARK_GRAY);
					hC15.setBackgroundColor(BaseColor.DARK_GRAY);

					table.addCell(hC11);
					table.addCell(hC12);
					table.addCell(hC13);
					table.addCell(hC14);
					table.addCell(hC15);

					PdfPCell hC6 = insertCell1(table, "Subject", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC7 = insertCell1(table, "Begin Date", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC8 = insertCell1(table, "Status", Element.ALIGN_LEFT, 1, bfBold12);
					PdfPCell hC9 = insertCell1(table, "Receive Date", Element.ALIGN_LEFT, 1, bfBold12);				
					PdfPCell hC10= insertCell1(table, "DeadLine", Element.ALIGN_LEFT, 1, bfBold12);

					hC6.setBackgroundColor(BaseColor.LIGHT_GRAY);
					hC7.setBackgroundColor(BaseColor.LIGHT_GRAY);
					hC8.setBackgroundColor(BaseColor.LIGHT_GRAY);
					hC9.setBackgroundColor(BaseColor.LIGHT_GRAY);
					hC10.setBackgroundColor(BaseColor.LIGHT_GRAY);

					table.addCell(hC6);
					table.addCell(hC7);
					table.addCell(hC8);
					table.addCell(hC9);
					table.addCell(hC10);

					for (int i = 0; i < list.size(); i++) {
						if(list.get(i).getWorkitemStatus().equalsIgnoreCase("Done")){
							boolean isArabic = isArabicText(list.get(i).getSubject());
							
							PdfPCell hC1 = insertCell(table, list.get(i).getSubject(), Element.ALIGN_LEFT, 1, strikeBold);
							PdfPCell hC2 = insertCell(table, list.get(i).getWorkflowBeginDate(), Element.ALIGN_LEFT, 1, strikeBold);
							PdfPCell hC3 = insertCell(table, list.get(i).getWorkitemStatus(), Element.ALIGN_LEFT, 1, strikeBold);
							PdfPCell hC4 = insertCell(table, list.get(i).getReceiveDate(), Element.ALIGN_LEFT, 1, strikeBold);	
							PdfPCell hC5 = insertCell(table, list.get(i).getDeadLine(), Element.ALIGN_LEFT, 1, strikeBold);
							hC1.setArabicOptions(ColumnText.AR_COMPOSEDTASHKEEL);
							if(isArabic){
								hC1.setUseDescender(true);
								hC1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
								hC1.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
							}else{
								hC1.setUseDescender(true);
								hC1.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
							}
							table.addCell(hC1);
							table.addCell(hC2);
							table.addCell(hC3);
							table.addCell(hC4);
							table.addCell(hC5);
						}else {

							boolean isArabic = isArabicText(list.get(i).getSubject());
							
							PdfPCell hC1 = insertCell(table, list.get(i).getSubject(), Element.ALIGN_LEFT, 1, bfBold);
							PdfPCell hC2 = insertCell(table, list.get(i).getWorkflowBeginDate(), Element.ALIGN_LEFT, 1, bfBold);
							PdfPCell hC3 = insertCell(table, list.get(i).getWorkitemStatus(), Element.ALIGN_LEFT, 1, bfBold);
							PdfPCell hC4 = insertCell(table, list.get(i).getReceiveDate(), Element.ALIGN_LEFT, 1, bfBold);	
							hC1.setArabicOptions(ColumnText.AR_COMPOSEDTASHKEEL);
							if(isArabic){
								
								hC1.setUseDescender(true);
								hC1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
								hC1.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
							}else{
								hC1.setUseDescender(true);
								hC1.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
							}
							table.addCell(hC1);
							table.addCell(hC2);
							table.addCell(hC3);
							table.addCell(hC4);
							if(list.get(i).getIsOverdue().equalsIgnoreCase("true")){
								PdfPCell hC5 = insertCell(table, list.get(i).getDeadLine(), Element.ALIGN_LEFT, 1, redBold);
								table.addCell(hC5);
							}else{
								PdfPCell hC5 = insertCell(table, list.get(i).getDeadLine(), Element.ALIGN_LEFT, 1, bfBold);
								table.addCell(hC5);
							}
						}

					}
					
					PdfPCell hC61 = insertCell1(table, "", Element.ALIGN_LEFT, 1, bfBold);
					PdfPCell hC71 = insertCell1(table, "", Element.ALIGN_LEFT, 1, bfBold);
					PdfPCell hC81 = insertCell1(table, "", Element.ALIGN_LEFT, 1, bfBold);
					PdfPCell hC91 = insertCell1(table, "", Element.ALIGN_LEFT, 1, bfBold);				
					PdfPCell hC101= insertCell1(table, "", Element.ALIGN_LEFT, 1, bfBold);

					table.addCell(hC61);
					table.addCell(hC71);
					table.addCell(hC81);
					table.addCell(hC91);
					table.addCell(hC101);
					
					paragraph.add(table);
					doc.add(paragraph);

					table.flushContent();
					paragraph.clear();

				}

				PdfPCell hC21 = insertCell(table, "Grand Total", Element.ALIGN_LEFT, 1, bfBold12);
				PdfPCell hC22 = insertCell(table, "", Element.ALIGN_LEFT, 1, bfBold12);
				PdfPCell hC23 = insertCell(table, ""+totalWItems, Element.ALIGN_LEFT, 1, bfBold12);
				PdfPCell hC24 = insertCell(table, ""+totalDoneWorkitems, Element.ALIGN_LEFT, 1, bfBold12);				
				PdfPCell hC25= insertCell(table, ""+totalNotDoneWorkitems, Element.ALIGN_LEFT, 1, bfBold12);

				table.addCell(hC21);
				table.addCell(hC22);
				table.addCell(hC23);
				table.addCell(hC24);
				table.addCell(hC25);

				paragraph.add(table);
				
				doc.add(paragraph);
				doc.close();

				resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode("Pending Workflow."+fileExtension,"UTF-8").replaceAll("\\+"," ")  + "\"");
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
				WritableWorkbook pendwiWorkbook = null;

				try {
					
					resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode("Pending Workflows."+fileExtension,"UTF-8").replaceAll("\\+"," ")  + "\"");
					resp.setContentType("application/vnd.ms-excel");
					
					pendwiWorkbook = jxl.Workbook.createWorkbook(resp.getOutputStream());
					WritableSheet workSheet = pendwiWorkbook.createSheet("PendingWFHFilter", 0);

					java.io.File imageFile = new java.io.File(req.getSession().getServletContext().getRealPath("images")+"\\logo-new.png");
					BufferedImage input = ImageIO.read(imageFile);
					baos = new ByteArrayOutputStream();
					ImageIO.write(input, "PNG", baos);
					WritableImage im = new WritableImage(0,0,1.2,6,baos.toByteArray());
					im.setImageAnchor(WritableImage.NO_MOVE_OR_SIZE_WITH_CELLS);
					workSheet.addImage(im);

					CellView cell = workSheet.getRowView(12);
					workSheet.setColumnView(0, 50);
					cell.setAutosize(true);
					workSheet.setColumnView(1, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(2, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(3, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(4, cell);
					cell.setAutosize(true);
					workSheet.setColumnView(5, cell);

					WritableCellFormat cFormat = null;

					cFormat = fontBold();
					Label label = new Label(0, 7, "Workflows From", cFormat);
					workSheet.addCell(label);					
					cFormat = fontNoBold();
					label = new Label(1, 7, pendingWorkflowsList.get(0).getSender(), cFormat);
					workSheet.addCell(label);

					cFormat = fontBold();
					label = new Label(0, 8, "Workflows Begin Date", cFormat);
					workSheet.addCell(label);					
					cFormat = fontNoBold();					
					if(pendingWorkflowsList.get(0).getFromDate() != null) {
						label = new Label(1, 8, pendingWorkflowsList.get(0).getFromDate(), cFormat);
					}
					if(pendingWorkflowsList.get(0).getToDate() != null) {
						label = new Label(1, 8, pendingWorkflowsList.get(0).getToDate(), cFormat);
					}
					if(pendingWorkflowsList.get(0).getFromDate() != null && pendingWorkflowsList.get(0).getToDate() != null) {
						label = new Label(1, 8, pendingWorkflowsList.get(0).getFromDate()+"  Till  "+pendingWorkflowsList.get(0).getToDate(), cFormat);
					}
					if(pendingWorkflowsList.get(0).getFromDate() == null && pendingWorkflowsList.get(0).getToDate() == null) {
						label = new Label(1, 8, "All Workflows", cFormat);
					}
					workSheet.addCell(label);

					cFormat = fontBold();
					label = new Label(0, 9, "All Items (Done & Not Done)", cFormat);
					workSheet.addCell(label);

					File canvasImageFile = new File(templateName);
					String filePath = canvasImageFile.getAbsolutePath();
					OutputStream  outputStream = new FileOutputStream(canvasImageFile);
					outputStream.write(buffer);
					outputStream.close();
					
					/*java.io.File imageFile1 = new java.io.File(filePath);
					BufferedImage input1 = ImageIO.read(imageFile1);
					ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
					ImageIO.write(input1, "PNG", baos1);
					WritableImage im1 = new WritableImage(0,11,4.2,20,baos1.toByteArray());
					im1.setImageAnchor(WritableImage.NO_MOVE_OR_SIZE_WITH_CELLS);*/
					//workSheet.addImage(im1);
					
					cFormat = fontBold();
					label = new Label(0, 12, "Recipient", cFormat);
					workSheet.addCell(label);
					label = new Label(1, 12, "% Done", cFormat);
					workSheet.addCell(label);
					label = new Label(2, 12, "Total", cFormat);
					workSheet.addCell(label);
					label = new Label(3, 12, "Done", cFormat);
					workSheet.addCell(label);
					label = new Label(4, 12, "Not Done", cFormat);
					workSheet.addCell(label);

					String key = null;
					HashMap<String, ArrayList<PendingWorkItemDetails>> map = new HashMap<String, ArrayList<PendingWorkItemDetails>>();
					for (int i = 0; i < pendingWorkflowsList.size(); i++) {
						ArrayList<PendingWorkItemDetails> pendingWFs = new ArrayList<PendingWorkItemDetails>();
						key = pendingWorkflowsList.get(i).getRecipient();
						for (int j = 0; j < pendingWorkflowsList.size(); j++) {
							if(pendingWorkflowsList.get(j).getRecipient().equalsIgnoreCase(pendingWorkflowsList.get(i).getRecipient())){
								pendingWFs.add(pendingWorkflowsList.get(j));
							}
						}
						map.put(key, pendingWFs);
					}
					Set<String> recipientName = map.keySet();
					Iterator<String> it = recipientName.iterator();
					int rowNumber = 14;
					while (it.hasNext()) {
						String recipienKey = it.next().toString();
						ArrayList<PendingWorkItemDetails> list = map.get(recipienKey);

						int doneWorkitems = 0;
						int notDoneWorkitems = 0;
						int totalWorkitems = list.size();
						int donePercentage = 0;

						for (int j = 0; j < list.size(); j++) {

							if((list.get(j).getWorkitemStatus()).equalsIgnoreCase("Done")) {
								doneWorkitems++;
							}
							else {
								notDoneWorkitems++;
							}
						}
						donePercentage = (doneWorkitems * 100)/totalWorkitems;

						cFormat = fontBoldAndBackGround();
						label = new Label(0, rowNumber, recipienKey, cFormat);
						workSheet.addCell(label);
						label = new Label(1, rowNumber, donePercentage+"%", cFormat);
						workSheet.addCell(label);
						label = new Label(2, rowNumber, ""+totalWorkitems, cFormat);
						workSheet.addCell(label);
						label = new Label(3, rowNumber, ""+doneWorkitems, cFormat);
						workSheet.addCell(label); 
						label = new Label(4, rowNumber, ""+notDoneWorkitems, cFormat);
						workSheet.addCell(label);

						rowNumber = rowNumber+1;
						int collapseStartIndex = rowNumber;
						cFormat = fontBoldWithColor();
						label = new Label(0, rowNumber, "Subject", cFormat);
						workSheet.addCell(label);
						label = new Label(1, rowNumber, "Workflow Begin Date", cFormat);
						workSheet.addCell(label);
						label = new Label(2, rowNumber, "Status", cFormat);
						workSheet.addCell(label);
						label = new Label(3, rowNumber, "Receive Date", cFormat);
						workSheet.addCell(label); 
						label = new Label(4, rowNumber, "DeadLine", cFormat);
						workSheet.addCell(label);

						int noOfRows = rowNumber+1;
						
						for (int i = 0; i < list.size(); i++) {
							if((list.get(i).getWorkitemStatus()).equalsIgnoreCase("Done")) {
								
								cFormat = strikeFont();
							}else{
								cFormat = fontNoBold();
							}
							label = new Label(0, noOfRows, list.get(i).getSubject(), cFormat);
							workSheet.addCell(label);
							label = new Label(1, noOfRows, list.get(i).getWorkflowBeginDate(), cFormat);
							workSheet.addCell(label);
							label = new Label(2, noOfRows, list.get(i).getWorkitemStatus(), cFormat);
							workSheet.addCell(label);
							label = new Label(3, noOfRows, list.get(i).getReceiveDate(), cFormat);
							workSheet.addCell(label);
							if((list.get(i).getIsOverdue()).equalsIgnoreCase("true") && (list.get(i).getWorkitemStatus()).equalsIgnoreCase("Done")){
								cFormat = strikeFont();
							}else if((list.get(i).getIsOverdue()).equalsIgnoreCase("true")){
								cFormat = redFont();
							}else{
								cFormat = fontNoBold();
							}
							label = new Label(4, noOfRows, list.get(i).getDeadLine(), cFormat);
							workSheet.addCell(label);

							noOfRows++;

						}
						workSheet.setRowGroup(collapseStartIndex,noOfRows-1, true); 
						label = new Label(0, noOfRows++, "" , cFormat);
						workSheet.addCell(label);
						rowNumber = noOfRows;

					}

					cFormat = fontNoBoldAndBackGround();
					int rwNo = rowNumber;
					label = new Label(0, rwNo, "Grand Total", cFormat);
					workSheet.addCell(label);
					label = new Label(1, rwNo, "", cFormat);
					workSheet.addCell(label);
					label = new Label(2, rwNo, ""+totalWItems, cFormat);
					workSheet.addCell(label);
					label = new Label(3, rwNo, ""+totalDoneWorkitems, cFormat);
					workSheet.addCell(label);
					label = new Label(4, rwNo, ""+totalNotDoneWorkitems, cFormat);
					workSheet.addCell(label);


					pendwiWorkbook.write();
					System.out.println("Total Time   :"+(System.currentTimeMillis()-startTime));

				} catch (WriteException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (pendwiWorkbook != null) {
						try {
							File filePath = new File(templateName);
					        filePath.delete(); 
							pendwiWorkbook.close();
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

	 private static boolean isArabicText(String subject) {
		
	        boolean isArabic = false;
	        for (int a = 0; a < subject.length(); a++) {
	            char c = subject.charAt(a);
	            if ((int)c > 31 && (int)c < 127){
	            	isArabic = false;
	            }
	            else{
	            	isArabic = true;
	            	break;
	            }
	        }
		 
		 return isArabic;
	}


	public static String unicodeEscaped(char ch) {
	      if (ch < 0x10) {
	          return "\\u000" + Integer.toHexString(ch);
	      } else if (ch < 0x100) {
	          return "\\u00" + Integer.toHexString(ch);
	      } else if (ch < 0x1000) {
	          return "\\u0" + Integer.toHexString(ch);
	      }
	      return "\\u" + Integer.toHexString(ch);
	  }
	
	
}
