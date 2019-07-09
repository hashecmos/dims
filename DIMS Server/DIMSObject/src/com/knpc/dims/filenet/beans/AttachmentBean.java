package com.knpc.dims.filenet.beans;

import java.io.InputStream;

public class AttachmentBean {

	private InputStream inputStream;
	private String mimeType ;
	private String fileName ;
	private String size ;
	private String elementSequenceNo;	
	
	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getMimeType() {
		return mimeType;
	}
	
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public String getElementSequenceNo() {
		return elementSequenceNo;
	}

	public void setElementSequenceNo(String elementSequenceNo) {
		this.elementSequenceNo = elementSequenceNo;
	}





		
	
}
