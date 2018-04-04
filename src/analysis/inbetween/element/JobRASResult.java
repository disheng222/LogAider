/**
 * @author Sheng Di
 * @class JoBRASResult
 * @description  
 */

package analysis.inbetween.element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JobRASResult {
	private String jobID;
	private double timeStamp;
	public List<RASRecord> rasList;
	
	public JobRASResult(String jobID, double timeStamp, List<RASRecord> rasList) {
		this.jobID = jobID;
		this.timeStamp = timeStamp;
		this.rasList = rasList;
	}
	public String getJobID() {
		return jobID;
	}
	public void setJobID(String jobID) {
		this.jobID = jobID;
	}
	public double getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(double timeStamp) {
		this.timeStamp = timeStamp;
	}
	public List<String> toOutputList()
	{
		List<String> resultList = new ArrayList<String>();
		StringBuilder metaSB = new StringBuilder(jobID);
		metaSB.append(",").append(timeStamp);
		metaSB.append("::");
		Iterator<RASRecord> iter = rasList.iterator();
		if(iter.hasNext())
		{
			RASRecord firstRR = iter.next();
			metaSB.append(firstRR.getTime()).append(",").append(firstRR.getRecordID());
		}
		while(iter.hasNext())
		{
			RASRecord rr = iter.next();
			metaSB.append(";").append(rr.getTime()).append(",").append(rr.getRecordID());
		}
		resultList.add(metaSB.toString());
		
		iter = rasList.iterator();
		while(iter.hasNext())
		{
			RASRecord rr = iter.next();
			StringBuilder sb = new StringBuilder(rr.getRecordID());
			sb.append("#").append(rr.getRecord());
			resultList.add(sb.toString());
		}
		return resultList;
	}
}
