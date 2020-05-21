/**
 * @author Sheng Di
 * @class RASRecord
 * @description  
 */


package analysis.inbetween.element;

public class RASRecord implements Comparable<RASRecord>{

	private double time;
	private String recordID;
	private String blockCode;
	private String msgID;
	private String component;
	private String category;
	private String record;
	private int taskID = 0;
	private int taskIDCount = 0;
	
	public RASRecord(double time, String recordID,
			String msgID, String component, String category, String blockCode, int taskID, String record) {
		this.time = time;
		this.recordID = recordID;
		this.blockCode = blockCode;
		this.msgID = msgID;
		this.component = component;
		this.category = category;
		this.taskID = taskID;
		this.record = record;
	}
	public RASRecord(double time, String recordID, String blockCode, String msgID, String record) {
		this.time = time;
		this.recordID = recordID;
		this.blockCode = blockCode;
		this.msgID = msgID;
		this.record = record;
	}
	public String getBlockCode() {
		return blockCode;
	}

	public void setBlockCode(String blockCode) {
		this.blockCode = blockCode;
	}

	public String getRecordID() {
		return recordID;
	}
	public void setRecordID(String recordID) {
		this.recordID = recordID;
	}
	public String getMsgID() {
		return msgID;
	}
	public void setMsgID(String msgID) {
		this.msgID = msgID;
	}
	public double getTime() {
		return time;
	}
	public void setTime(float time) {
		this.time = time;
	}
	public String getRecord() {
		return record;
	}
	public void setRecord(String record) {
		this.record = record;
	}
	public String getComponent() {
		return component;
	}
	public void setComponent(String component) {
		this.component = component;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	public int getTaskIDCount() {
		return taskIDCount;
	}
	public void setTaskIDCount(int taskIDCount) {
		this.taskIDCount = taskIDCount;
	}
	
	public int getTaskID() {
		return taskID;
	}
	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}
	public int compareTo(RASRecord other)
	{
		if(time < other.time)
			return -1;
		else if(time > other.time)
			return 1;
		else
			return 0;
	}
	
	public String toString()
	{
		return record;
	}
}
