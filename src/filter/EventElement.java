package filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import util.PVFile;
import analysis.temporalcorr.SearchErrorPropagation;
import analysis.temporalcorr.Similarity;
import analysis.temporalcorr.fields.StartTimeField;

public class EventElement implements Comparable<EventElement>{

	private int eventID;
	private String recID;
	private String msgID;
	private String severity;
	private String category;
	public List<String> componentList = new ArrayList<String>();
	public String firstLocation;
	private int recordElementSize;
	public List<RecordElement> recordElementList = new ArrayList<RecordElement>();
	private int blockSize;
	private RecordElement firstRecord;
	private RecordElement latestRecord;
	public String locationKey;
	public HashMap<String, Integer> locationKeyMap = new HashMap<String, Integer>();
	private String eventString;
	
	public List<Float> preSimilarityList = new ArrayList<Float>();
	public List<EventElement> preCorrelatedEventList = new ArrayList<EventElement>();
	
	public List<Float> postSimilarityList = new ArrayList<Float>();
	public List<EventElement> postCorrelatedEventList = new ArrayList<EventElement>();
	
	public EventElement mergedTo;
	
	public List<Float> finalPostSimilarityList = new ArrayList<Float>();
	public List<EventElement> finalPostCorrList = new ArrayList<EventElement>();
	
	public EventElement(String eventString)
	{
		this.eventString = eventString;
		String[] s = eventString.split("\\s");
		eventID = Integer.parseInt(s[0]);
		msgID = s[1];
		recID = s[2];
		severity = s[3];
		category = s[4];
		componentList.add(s[5]);
		firstRecord = new RecordElement(msgID, s[6], severity, category, s[5], s[10]);
		latestRecord = new RecordElement(msgID, s[7], severity, category, s[5], s[10]);
		locationKey = s[9];
		
		try {
			blockSize = Integer.parseInt(s[11]);
		} catch (Exception e) {
			System.out.println("blockSize is not integer: "+s[11]);
		}
		recordElementSize = Integer.parseInt(s[8]);
	}
	
	public EventElement(int eventID, String msgID, String severity, String category, 
			String location, 
			RecordElement record, String firstLocation) {
		this.eventID = eventID;
		this.msgID = msgID;
		this.severity = severity;
		this.category = category;
		String keyLocation = TemporalSpatialFilter.getLocationKeyName(location);
		locationKeyMap.put(keyLocation, new Integer(1));
		this.firstRecord = record;
		this.latestRecord = record;
		recordElementList.add(record);
		this.firstLocation = firstLocation;
	}
	public int getEventID() {
		return eventID;
	}
	public void setEventID(int eventID) {
		this.eventID = eventID;
	}
	public String getMsgID() {
		return msgID;
	}
	public void setMsgID(String msgID) {
		this.msgID = msgID;
	}
	public RecordElement getLatestRecord() {
		return latestRecord;
	}
	public void setLatestRecord(RecordElement latestRecord) {
		this.latestRecord = latestRecord;
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
	
	public boolean equals(Object o)
	{
		EventElement e = (EventElement)o;
		if(eventID == e.eventID)
			return true;
		else
			return false;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(eventID).append(" ").append(msgID).append(" ").append(firstRecord.getrID()).append(" ");
		sb.append(severity).append(" ").append(category).append(" ");
		if(componentList.size()==1)
			sb.append(componentList.get(0)).append(" ");
		else //>1
		{
			Iterator<String> iter = componentList.iterator();
			String firstComp = iter.next();
			sb.append(firstComp);
			while(iter.hasNext())
			{
				String comp = iter.next();
				sb.append(",").append(comp);
			}
			sb.append(" ");
		}
		latestRecord = recordElementList.get(recordElementList.size()-1);
		sb.append(firstRecord.getTime().replace(" ", ";")).append(" ").append(latestRecord.getTime().replace(" ", ";")).append(" ").append(recordElementList.size());
		sb.append(" ");
		Iterator iter = locationKeyMap.entrySet().iterator();
		if(iter.hasNext())
		{
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    String key = (String)entry.getKey(); 
		    sb.append(key);
		}
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    String key = (String)entry.getKey(); 
		    int count = (Integer)entry.getValue(); 
		    //sb.append(key).append(":").append(count).append(" ");
		    sb.append(",").append(key);
		}
		sb.append(" ");
		sb.append(firstLocation);
		sb.append(" ").append(blockSize);
		return sb.toString().trim();
	}
	public int getBlockSize() {
		return blockSize;
	}
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	public int compareTo(EventElement ee)
	{
		if(this.firstRecord.getDtime()<ee.firstRecord.getDtime())
			return -1;
		else if(this.firstRecord.getDtime()>ee.firstRecord.getDtime())
			return 1;
		else
			return 0;
	}
	public int getRecordElementSize() {
		return recordElementSize;
	}
	public void setRecordElementSize(int recordElementSize) {
		this.recordElementSize = recordElementSize;
	}
	public String getEventString() {
		return eventString;
	}
	public void setEventString(String eventString) {
		this.eventString = eventString;
	}
	public RecordElement getFirstRecord() {
		return firstRecord;
	}
	public void setFirstRecord(RecordElement firstRecord) {
		this.firstRecord = firstRecord;
	}
	
	public float computeSimilarity(EventElement otherEvent, float delay)
	{
//		if(this.getEventID()==145&&otherEvent.getEventID()==146)
//			System.out.println();
		StartTimeField.delay = delay;
		float similarity = 0;
		String[] s1 = eventString.split("\\s");
		String[] s2 = otherEvent.eventString.split("\\s");
		Iterator<Similarity> iter = SearchErrorPropagation.similarityList.iterator();
		while(iter.hasNext())
		{
			Similarity sim = iter.next();
			int index = sim.getFieldIndex();
			float weight = sim.getWeight();
			float simValue = sim.getSimilarity(s1[index], s2[index]);
			similarity += weight*simValue;
		}
		return similarity;
	}
	
	public List<String> printAllEventItems()
	{
		List<String> resultList = new ArrayList<String>();
		resultList.add("event "+eventString);
		Iterator<EventElement> iter = postCorrelatedEventList.iterator();
		Iterator<Float> iter2 = postSimilarityList.iterator();
		while(iter.hasNext())
		{
			EventElement ee = iter.next();
			float similarity = iter2.next();
			resultList.add("\t"+similarity+" "+ee.eventString);
		}
		return resultList;
	}
	
	public List<String> printFinalEventItems()
	{
		List<String> resultList = new ArrayList<String>();
		resultList.add("event "+eventString);
		Iterator<EventElement> iter = finalPostCorrList.iterator();
		Iterator<Float> iter2 = finalPostSimilarityList.iterator();
		while(iter.hasNext())
		{
			EventElement ee = iter.next();
			float similarity = iter2.next();
			resultList.add("\t"+similarity+" "+ee.eventString);
		}
		return resultList;	
	}
	
	public void clearPrePostList()
	{
		this.preSimilarityList.clear();
		this.preCorrelatedEventList.clear();
		this.postSimilarityList.clear();
		this.postCorrelatedEventList.clear();
	}
	
	public void clearFinalList()
	{
		mergedTo = null;
		this.finalPostSimilarityList.clear();
		this.finalPostCorrList.clear();
	}
}
