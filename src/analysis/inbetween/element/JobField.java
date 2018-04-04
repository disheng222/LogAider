/**
 * @author Sheng Di
 * @class JobField
 * @description  
 */

package analysis.inbetween.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class JobField
{
	private int index;
	private String fieldName;
	public HashMap<String, ValueCountItem> valueCountMap = new HashMap<String, ValueCountItem>();
	public List<ValueCountItem> valueCountList = new ArrayList<ValueCountItem>();
	
	public JobField(int index, String fieldName) {
		this.index = index;
		this.fieldName = fieldName;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public JobField clone()
	{
		return new JobField(index, fieldName);
	}
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(index).append(":").append(fieldName);
		Iterator<ValueCountItem> iter = valueCountList.iterator();
		Collections.sort(valueCountList);
		while(iter.hasNext())
		{
			ValueCountItem vcItem = iter.next();
			sb.append(" ").append(vcItem.toString());
		}
		return sb.toString();
	}
}