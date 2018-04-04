package analysis.onSimFilterLog;

public class CountElement implements Comparable<CountElement>{

	private String timeUnit;
	private int count = 0;
	
	public CountElement(String timeUnit, int count) {
		super();
		this.timeUnit = timeUnit;
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int compareTo(CountElement e)
	{
		return timeUnit.compareTo(e.timeUnit);
	}
	
	public String toString()
	{
		return timeUnit+" "+count;
	}
}
