package element;

import analysis.Job.ExtractValueTypes4EachField;

public class Value implements Comparable<Value>{
	public static boolean withCount = true;
	
	private String value;
	private int count;
	public int[] tmpCount;
	
	public Value(String value) {
		this.value = value;
		this.count = 1;
	}
	
	public Value(String value, int tmpSize)
	{
		tmpCount = new int[tmpSize];
		for(int i = 0;i<tmpCount.length;i++)
			tmpCount[i] = 0;
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public String toString()
	{
		if(withCount)
		{
			if(value==null||value.equals(""))
				value = "NULL";
			return value + ExtractValueTypes4EachField.separator+count;
		}
		else
			return value;
	}
	
	public int compareTo(Value v)
	{
		if(this.count<v.count)
			return -1;
		else if(this.count>v.count)
			return 1;
		else
			return 0;
	}
}
