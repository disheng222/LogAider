package analysis.RAS;

class MemoryInfoElement implements Comparable<MemoryInfoElement>
{
	private String errStatus;
	private long count;
	
	public MemoryInfoElement(String errStatus) {
		this.errStatus = errStatus;
		this.count = 0;
	}
	public String getErrStatus() {
		return errStatus;
	}
	public void setErrStatus(String errStatus) {
		this.errStatus = errStatus;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
	public String toString()
	{
		if(count!=-1)
			return errStatus+" "+count;
		else
			return errStatus;
	}
	
	public int compareTo(MemoryInfoElement other)
	{
		if(this.count < other.count)
			return -1;
		else if(this.count > other.count)
			return 1;
		else
			return 0;
	}
}