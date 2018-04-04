package filter;

public class FieldValueCountElement implements Comparable<FieldValueCountElement>{

	//private int month;
	private String value;
	private int count;
	
	public FieldValueCountElement(String value, int count) {
		this.value = value;
		this.count = count;
	}
	public String getValue() {
		return value;
	}
	public String getSanitizedValue()
	{
		return value.replaceAll("_", "\\\\\\\\_");
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
	
	public int compareTo(FieldValueCountElement other)
	{
		return value.compareTo(other.value);
	}
	
	public String toString()
	{
		return String.valueOf(count);
	}
}
