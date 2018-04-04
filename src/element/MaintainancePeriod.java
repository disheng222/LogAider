package element;

import filter.RecordElement;

public class MaintainancePeriod implements Comparable<MaintainancePeriod>{

	private double startTime;
	private double endTime;
	
	public MaintainancePeriod(double startTime, double endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}
	public double getStartTime() {
		return startTime;
	}
	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}
	public double getEndTime() {
		return endTime;
	}
	public void setEndTime(double endTime) {
		this.endTime = endTime;
	}
	
	public int compareTo(MaintainancePeriod other)
	{
		if(startTime < other.startTime)
			return -1;
		else if(startTime > other.startTime)
			return 1;
		else
			return 0;
	}
	
	public boolean containRecord(RecordElement record)
	{
		if(record.getDtime()>=startTime&&record.getDtime()<=endTime)
			return true;
		else
			return false;
	}
	
	public boolean containRecord(double eventTime)
	{
		if(eventTime>=startTime&&eventTime<=endTime)
			return true;
		else
			return false;
	}
	
}
