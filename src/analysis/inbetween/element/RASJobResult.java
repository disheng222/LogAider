/**
 * @author Sheng Di
 * @class RASJobResult
 * @description  
 */

package analysis.inbetween.element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RASJobResult {

	private String rasID; //should be recordID-msgID
	private double timeStamp; //actually, long integer starting from  01/01/1970
	public List<JobRecord> jobList;
	
	public RASJobResult(String rasID, double timeStamp, List<JobRecord> jobList) {
		this.rasID = rasID;
		this.timeStamp = timeStamp;
		this.jobList = jobList;
	}
	public String getRasID() {
		return rasID;
	}
	public void setRasID(String rasID) {
		this.rasID = rasID;
	}
	public double getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(float timeStamp) {
		this.timeStamp = timeStamp;
	}
	public List<String> toOutputList()
	{
		List<String> resultList = new ArrayList<String>();
		StringBuilder metaSB = new StringBuilder(rasID);
		metaSB.append(",").append(timeStamp);
		metaSB.append("::");
		Iterator<JobRecord> iter = jobList.iterator();
		if(iter.hasNext())
		{
			JobRecord firstJR = iter.next();
			metaSB.append(firstJR.getTime()).append(",").append(firstJR.getJobID());
		}
		while(iter.hasNext())
		{
			JobRecord jr = iter.next();
			metaSB.append(";").append(jr.getTime()).append(",").append(jr.getJobID());
		}
		resultList.add(metaSB.toString());
		
		iter = jobList.iterator();
		while(iter.hasNext())
		{
			JobRecord jr = iter.next();
			StringBuilder sb = new StringBuilder(jr.getJobID());
			sb.append("#").append(jr.getRecord());
			resultList.add(sb.toString());
		}
		return resultList;
	}
}
