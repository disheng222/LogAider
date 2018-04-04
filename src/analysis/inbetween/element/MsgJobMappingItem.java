/**
 * @author Sheng Di
 * @class MsgJobMappingItem
 * @description  
 */

package analysis.inbetween.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import analysis.inbetween.RAS_Job_Analysis;

public class MsgJobMappingItem
{
	private String msgID;
	float minJobDelay = 1000000000;
	float sumJobDelay = 0; 
	float maxJobDelay = 0;
	int jobCount = 0;
	
	public HashMap<Integer, JobField> map = new HashMap<Integer, JobField>(); //index, JobFieldItem
	public MsgJobMappingItem(String msgID) {
		this.msgID = msgID;
	}
	public String getMsgID() {
		return msgID;
	}
	public void setMsgID(String msgID) {
		this.msgID = msgID;
	}
	public float getMinJobDelay() {
		return minJobDelay;
	}
	public void setMinJobDelay(float minJobDelay) {
		this.minJobDelay = minJobDelay;
	}
	public float getMaxJobDelay() {
		return maxJobDelay;
	}
	public void setMaxJobDelay(float maxJobDelay) {
		this.maxJobDelay = maxJobDelay;
	}
	public float getAvgDelay()
	{
		return sumJobDelay/jobCount;
	}
	public void updateDelayInfo(float delay)
	{
		if(delay > maxJobDelay)
			maxJobDelay = delay;
		if(delay < minJobDelay)
			minJobDelay = delay;
		sumJobDelay += delay;
		jobCount++;
	}
	public List<String> toStringList(List<JobField> list)
	{
		List<String> resultList = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		sb.append(msgID).append(" minDelay=").append(minJobDelay).append(" avgDelay=").append(getAvgDelay()).append(" maxDelay=").append(maxJobDelay);
		resultList.add(sb.toString());
		Iterator<JobField> iter = list.iterator();
		while(iter.hasNext())
		{
			JobField jField = iter.next();
			int index = jField.getIndex();
			//String fieldName = jField.getFieldName();
			JobField jobField = map.get(index);
			resultList.add(jobField.toString());
		}
		return resultList;
	}
}