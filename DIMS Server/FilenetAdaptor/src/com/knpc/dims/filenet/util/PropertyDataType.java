package com.knpc.dims.filenet.util;

import java.util.HashMap;

import com.knpc.dims.filenet.FileNetAdaptor;
import com.knpc.dims.filenet.beans.PropertyBean;

public class PropertyDataType {
	
	public static HashMap<String, PropertyBean> propertyMap = null;
	
	private static PropertyDataType propDataType = null;
	static
	{
		if (propDataType == null)
		{
			try {
				propDataType = new PropertyDataType();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private PropertyDataType() throws Exception
	{
		
		FileNetAdaptor adaptor = new FileNetAdaptor();
		propertyMap = adaptor.getPropertyTemplates("ECM");
		
	}
	
}
