package com.knpc.dims.filenet.beans;

import java.util.List;

public class PropertyBean {
	private String propertyName;
	private String propertyValue;
	private String descriptiveText;
	private String cardinality;
	private boolean mandatory;
	private int maxLength;
	private String datatype;
	private boolean isChoiceList;
	private String  symbolicName;
	private String isSettable;
	private String dataStructure;
	private boolean isLookupAvailable;
	private List<String> lookupValues;
	private String docClassName;
	private String isHidden ;
	private boolean isMultiple;
	private List<String> propertyMultiValues;
	//Used by LookupMigrator
	//private LookupBean lookup; 
	
	public String getIsHidden() {
		return isHidden;
	}
	public void setIsHidden(String isHidden) {
		this.isHidden = isHidden;
	}
	public String getDocClassName() {
		return docClassName;
	}
	public void setDocClassName(String docClassName) {
		this.docClassName = docClassName;
	}
	public String getCardinality() {
		return cardinality;
	}
	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}
	
	public int getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public boolean isChoiceList() {
		return isChoiceList;
	}
	public void setChoiceList(boolean isChoiceList) {
		this.isChoiceList = isChoiceList;
	}
	public String getSymbolicName() {
		return symbolicName;
	}
	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}
	public String getIsSettable() {
		return isSettable;
	}
	public void setIsSettable(String isSettable) {
		this.isSettable = isSettable;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public String getPropertyValue() {
		return propertyValue;
	}
	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}
	public String getDescriptiveText() {
		return descriptiveText;
	}
	public void setDescriptiveText(String descriptiveText) {
		this.descriptiveText = descriptiveText;
	}
	
	public void setDataStructure(String dataStructure) {
		this.dataStructure = dataStructure;
	}
	public String getDataStructure() {
		return dataStructure;
	}
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	public boolean isMandatory() {
		return mandatory;
	}
	public void setLookupAvailable(boolean isLookupAvailable) {
		this.isLookupAvailable = isLookupAvailable;
	}
	public boolean isLookupAvailable() {
		return isLookupAvailable;
	}
	public void setLookupValues(List<String> lookupValues) {
		this.lookupValues = lookupValues;
	}
	public List<String> getLookupValues() {
		return lookupValues;
	}
	public boolean isMultiple() {
		return isMultiple;
	}
	public void setMultiple(boolean isMultiple) {
		this.isMultiple = isMultiple;
	}
	public List<String> getPropertyMultiValues() {
		return propertyMultiValues;
	}
	public void setPropertyMultiValues(List<String> propertyMultiValues) {
		this.propertyMultiValues = propertyMultiValues;
	}
	/*public void setLookup(LookupBean lookup) {
		this.lookup = lookup;
	}
	public LookupBean getLookup() {
		return lookup;
	}*/
	
}
