package element;

import java.util.Collection;

public class TmpValue implements Comparable<TmpValue>{

	private String fieldValueName;
	private int count;
	
	public TmpValue(String fieldValueName, int count) {
		this.fieldValueName = fieldValueName;
		this.count = count;
	}
	public String getFieldValueName() {
		return fieldValueName;
	}
	public void setFieldValueName(String fieldValueName) {
		this.fieldValueName = fieldValueName;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	/**
	 * invert sorting
	 */
	public int compareTo(TmpValue v)
	{
		if(count > v.count)
			return -1;
		else if(count < v.count)
			return 1;
		else
			return 0;
	}
	
	public String toString()
	{
		return fieldValueName+":"+count;
	}
}
