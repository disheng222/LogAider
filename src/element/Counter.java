package element;

public class Counter implements Comparable<Counter>{

	private String id;
	private int counter;
	
	public Counter(String id, int counter) {
		this.id = id;
		this.counter = counter;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	public String toString()
	{
		return id.toUpperCase()+" "+counter;
	}
	
	public int compareTo(Counter other)
	{
		if(counter < other.counter)
			return 1;
		else if(counter > other.counter)
			return -1;
		else
			return 0;
	}
}
