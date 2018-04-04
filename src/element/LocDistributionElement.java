package element;

public class LocDistributionElement implements Comparable<LocDistributionElement>{

	private String locID;
	private int count;
	
	public LocDistributionElement(String locID, int count) {
		this.locID = locID;
		this.count = count;
	}
	public String getLocID() {
		return locID;
	}
	public void setLocID(String locID) {
		this.locID = locID;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder(locID);
		return sb.append(" ").append(String.valueOf(count)).toString();
	}
	
	public int compareTo(LocDistributionElement other)
	{
		if(count < other.count)
			return 1;
		else if(count > other.count)
			return -1;
		else
			return 0;
	}
}
