/**
 * @author Sheng Di
 * @class JobRecord
 * @description  
 */

package analysis.inbetween.element;

public class JobRecord implements Comparable<JobRecord>{
	private double time;
	private String jobID;
	private String blockCode;
	private String record;
	
	public JobRecord(double time, String jobID, String record, String blockCode) {
		this.time = time;
		this.jobID = jobID;
		this.blockCode = blockCode;
		this.record = record;
	}
	public String getJobID() {
		return jobID;
	}
	public void setJobID(String jobID) {
		this.jobID = jobID;
	}
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}
	public String getRecord() {
		return record;
	}
	public void setRecord(String record) {
		this.record = record;
	}
	public String getBlockCode() {
		return blockCode;
	}
	public void setBlockCode(String blockCode) {
		this.blockCode = blockCode;
	}
	public int compareTo(JobRecord other)
	{
		if(time < other.time)
			return -1;
		else if(time > other.time)
			return 1;
		else
			return 0;
	}
}
