package element;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ValueCombination {

	private String ID = "";
	private List<FieldCombination> fieldList = null;	
	
	public ValueCombination(String ID, List<FieldCombination> fcList)
	{
		this.ID = ID;
		fieldList = fcList;
		//Collections.sort(fieldList);
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		//suppose fieldList is already sorted.
		Iterator<FieldCombination> iter = fieldList.iterator();
		while(iter.hasNext())
		{
			FieldCombination fc = iter.next();
			sb.append(fc.getFieldID());
			sb.append(":");
			sb.append(fc.getValue());
			sb.append("--");
		}
		return sb.toString().trim();
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}
	
}
