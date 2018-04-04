package element;

public class RASTmporalPairElement implements Comparable<RASTmporalPairElement>{

	private int count = 0;
	private String idPair; //msgID-msgID
	private double sumMinutes = 0;
	
	public RASTmporalPairElement(String idPair) {
		this.idPair = idPair;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getIDPair() {
		return idPair;
	}
	public void setIDPair(String iDPair) {
		this.idPair = iDPair;
	}
	public double getSumMinutes() {
		return sumMinutes;
	}
	public void setSumMinutes(double sumMinutes) {
		this.sumMinutes = sumMinutes;
	}
	public double getAvgMinutes()
	{
		return sumMinutes/count;
	}
	
	public int compareTo(RASTmporalPairElement other)
	{
		if(count < other.count)
			return 1;
		else if(count > other.count)
			return -1;
		else
			return 0;
	}
	
	public String toString()
	{
		return idPair+":"+count+","+getAvgMinutes();
	}
	
}
