package element;

public class DailyElement implements Comparable<DailyElement> 
{
	private int day;
	private int count;
	
	public DailyElement(int day, int count) {
		super();
		this.day = day;
		this.count = count;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public int compareTo(DailyElement other)
	{
		if(day<other.day)
			return -1;
		else if(day>other.day)
			return 1;
		else
			return 0;
	}
	
	public String toString()
	{
		return day+" "+count;
	}
}
