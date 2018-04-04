package filter;

import java.sql.Timestamp;

public class RecordElement implements Comparable<RecordElement>{

	private String rID;
	private String msgID;
	private String time;
	private double dtime; //in seconds, precision: millisecond
	private String allocation;
	private String location;
	private String severity;
	private String category;
	private String component;
	
	private String fullRecord;
	
	public RecordElement(String msgID, String time, String severity, String category, String component, String location)
	{
		this.msgID = msgID;
		this.time = time.replace(";", " ");
		dtime = computeDoubleTimeinSeconds(this.time);
		this.severity = severity;
		this.category = category;
		this.component = component;
		this.location = location;
	}
	
	public RecordElement(String rID, String msgID, String time,
			String allocation, String location, 
			String severity, String category, String component,
			String fullRecord) {
		this.rID = rID;
		this.msgID = msgID;
		this.time = time;
		//System.out.println(time);
		dtime = computeDoubleTimeinSeconds(time);
		this.allocation = allocation;
		this.location = location;
		this.severity = severity;
		this.category = category;
		this.component = component;
		this.fullRecord = fullRecord;
	}

	//time format: example: 2015-04-20-21.37.23.448917 or 2015-04-20 21:37:23.448917
	public static float computeFloatTimeinSeconds(String time)
	{
		float ftime;
		StringBuilder sb = new StringBuilder();
		String[] s = time.split("-");
		if(s.length==4)
		{
			sb.append(s[0]).append("-");
			sb.append(s[1]).append("-");
			sb.append(s[2]).append(" ");
			
			String[] b = s[3].split("\\.");
			sb.append(b[0]).append(":");
			sb.append(b[1]).append(":");
			sb.append(b[2]).append(".").append(b[3]);
			
			String timeString = sb.toString();

			//System.out.println("timeString="+timeString);
			Timestamp ts = Timestamp.valueOf(timeString);
			ftime = ts.getTime()/1000.0f;
		}
		else //==3
		{
			Timestamp ts = Timestamp.valueOf(time);
			ftime = ts.getTime()/1000.0f;
		}
		return ftime;
	}
	
	
	//time format: example: 2015-04-20-21.37.23.448917 or 2015-04-20 21:37:23.448917
	public static double computeDoubleTimeinSeconds(String time)
	{
		double dtime;
		StringBuilder sb = new StringBuilder();
		String[] s = time.split("-");
		if(s.length==4)
		{
			sb.append(s[0]).append("-");
			sb.append(s[1]).append("-");
			sb.append(s[2]).append(" ");
			
			String[] b = s[3].split("\\.");
			sb.append(b[0]).append(":");
			sb.append(b[1]).append(":");
			sb.append(b[2]).append(".").append(b[3]);
			
			String timeString = sb.toString();

			//System.out.println("timeString="+timeString);
			Timestamp ts = Timestamp.valueOf(timeString);
			dtime = ts.getTime()/1000.0;
		}
		else //==3
		{
			Timestamp ts = Timestamp.valueOf(time);
			dtime = ts.getTime()/1000.0;
		}
		return dtime;
	}
	
	public String getrID() {
		return rID;
	}

	public void setrID(String rID) {
		this.rID = rID;
	}

	public String getMsgID() {
		return msgID;
	}

	public void setMsgID(String msgID) {
		this.msgID = msgID;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAllocation() {
		return allocation;
	}

	public void setAllocation(String allocation) {
		this.allocation = allocation;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getFullRecord() {
		return fullRecord;
	}

	public void setFullRecord(String fullRecord) {
		this.fullRecord = fullRecord;
	}
	
	public double getDtime() {
		return dtime;
	}

	public void setDtime(double ftime) {
		this.dtime = ftime;
	}
	
	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
 
	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public int compareTo(RecordElement other)
	{
		if(dtime < other.dtime)
			return -1;
		else if(dtime > other.dtime)
			return 1;
		else
			return 0;
	}
	
	public String toString()
	{
		return fullRecord;
	}
	
}
