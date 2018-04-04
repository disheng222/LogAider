package element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Field {

	public Field(String fieldName, String dataType) {
		this.fieldName = fieldName;
		this.dataType = dataType;
	}
	private int index;
	private String fieldName;
	private String schemaType;
	private String dataType;
	private int length;
	private int scale;
	private boolean couldBeNull; //yes or no
	private boolean select; //select as event focus?
	
	private List<Value> valueList = new ArrayList<Value>();
	private HashMap<String, Value> valueMap = new HashMap<String, Value>();
	private HashMap<String, List<String>> selectMap = new HashMap<String, List<String>>();

	public Field(String fieldName, String schemaType, String dataType,
			int length, int scale, boolean couldBeNull) {
		this.fieldName = fieldName;
		this.schemaType = schemaType;
		this.dataType = dataType;
		this.length = length;
		this.scale = scale;
		this.couldBeNull = couldBeNull;
	}
	
	public Field(String fieldName)
	{
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getSchemaType() {
		return schemaType;
	}

	public void setSchemaType(String schemaType) {
		this.schemaType = schemaType;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public boolean isCouldBeNull() {
		return couldBeNull;
	}

	public void setCouldBeNull(boolean couldBeNull) {
		this.couldBeNull = couldBeNull;
	}
	
	public List<Value> getValueList() {
		return valueList;
	}

	public void setValueList(List<Value> valueList) {
		this.valueList = valueList;
	}

	public HashMap<String, Value> getValueMap() {
		return valueMap;
	}

	public void setValueMap(HashMap<String, Value> valueMap) {
		this.valueMap = valueMap;
	}

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public void checkAndAdd(String value)
	{
		if(!valueMap.containsKey(value))
		{
			Value vObject = new Value(value);
			valueMap.put(value, vObject);
			valueList.add(vObject);
		}
		else
		{
			Value v = valueMap.get(value);
			v.setCount(v.getCount()+1);
		}
	}
	
	public void checkSelectAndAdd(String value, String line)
	{
		
	}
	
	public List toStringList()
	{
		List resultList = new ArrayList();
		resultList.add("# "+fieldName+" "+schemaType+" "+dataType+" "+length+" "+scale+" "+couldBeNull);
		
		if(fieldName.equals("RECI")||fieldName.equals("EVENT_TIME"))
			return null;
		
		Collections.sort(valueList);
		resultList.addAll(valueList);
		
		return resultList;
	}
	
	public List<Ratio> computeRatios()
	{
		List<Ratio> resultList = new ArrayList<Ratio>();
		float sum = 0;
		
		Iterator<Value> iter = valueList.iterator();
		while(iter.hasNext())
		{
			Value v = iter.next();
			sum += v.getCount();
		}
		
		iter = valueList.iterator();
		while(iter.hasNext())
		{
			Value v = iter.next();
			float ratio = (((float)v.getCount())/sum*100); //percentage
			Ratio r = new Ratio(v.getValue(), ratio);
			resultList.add(r);
		}
		return resultList;
	}
	
	public List<String> toStringList_ratio()
	{
		List resultList = new ArrayList();
		resultList.add("# "+fieldName+" "+schemaType+" "+dataType+" "+length+" "+scale+" "+couldBeNull);
		
		if(fieldName.equals("RECI")||fieldName.equals("EVENT_TIME"))
			return null;
		
		List<Ratio> ratioList = computeRatios();
		Collections.sort(ratioList);
		resultList.addAll(ratioList);
		
		return resultList;
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	public Field clone()
	{
		Field f = new Field(fieldName, schemaType, dataType,
				length, scale, couldBeNull);
		return f;
	}
}
