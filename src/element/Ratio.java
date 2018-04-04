package element;

public class Ratio implements Comparable<Ratio>{

	private String value;
	private float ratio;
	
	public Ratio(String value, float ratio) {
		this.value = value;
		this.ratio = ratio;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public float getRatio() {
		return ratio;
	}
	public void setRatio(float ratio) {
		this.ratio = ratio;
	}
	
	public int compareTo(Ratio other)
	{
		if(ratio < other.ratio)
			return -1;
		else if(ratio > other.ratio)
			return 1;
		else
			return 0;
	}
	
	public String toString()
	{
		if(value==null||value.equals(""))
			value = "NULL";
		return value + " "+ratio;
	}
}
