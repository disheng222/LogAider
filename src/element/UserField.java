package element;

public class UserField implements Comparable<UserField>{

	private String fieldName;
	private int fieldIndex;
	
	public UserField(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public int getFieldIndex() {
		return fieldIndex;
	}
	public void setFieldIndex(int fieldIndex) {
		this.fieldIndex = fieldIndex;
	}
	
	public int compareTo(UserField other)
	{
		if(fieldIndex < other.fieldIndex)
			return -1;
		else if(fieldIndex > other.fieldIndex)
			return 1;
		else 
			return 0;
	}
	
	public String toString()
	{
		if(fieldIndex!=-1)
			return fieldName+" "+fieldIndex;
		else
			return fieldName;
	}
}
