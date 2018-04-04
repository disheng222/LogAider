package element;

public class FieldCombination implements Comparable<FieldCombination>
{
	private String fieldName;
	private int fieldID;
	private String value;
	
	public FieldCombination(String fieldName, int fieldID, String value) {
		this.fieldName = fieldName;
		this.fieldID = fieldID;
		this.value = value;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public int getFieldID() {
		return fieldID;
	}

	public void setFieldID(int fieldID) {
		this.fieldID = fieldID;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int compareTo(FieldCombination other)
	{
		if(fieldID < other.fieldID)
			return -1;
		else if(fieldID > other.fieldID)
			return 1;
		else 
			return 0;
	}
}
