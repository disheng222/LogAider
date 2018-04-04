package analysis.Job;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import util.NumericChecker;
import util.PVFile;
import util.RecordSplitter;

public class SearchJobswithBreakWallClockFailure {

	static String jobLogFile = "";
	static String basicSchemaFile = "";
	static String outputDir = "";
	
	static float timeCompareErrThreshold = 5; //in seconds
	
	public static void main(String[] args)
	{
		if(args.length<3)
		{
			System.out.println("Usage: java SearchJobswithBreakWallClockFailure [jobLogFile] [basicSchema] [outputDir]");
			System.out.println("Example: java SearchJobswithBreakWallClockFailure /home/fti/Catalog-project/miralog/RAS-Job/Job/scrubbed-201410-data.csv /home/fti/Catalog-project/miralog/RAS-Job/Job/basicSchema/basicSchema.txt /home/fti/Catalog-project/miralog/RAS-Job/Job/lengthAnalysis");
			System.exit(0);
		}
		
		jobLogFile = args[0];
		basicSchemaFile = args[1];
		outputDir = args[2];
		
		int[] codeIndex = loadSchema(basicSchemaFile);
		int wallTimeIndex = codeIndex[3];
		int queuedTimeIndex = codeIndex[1];
		int startTimeIndex = codeIndex[2];
		int endTimeIndex = codeIndex[0];
		int exitCodeIndex = codeIndex[4];
		int nodesAlloIndex = codeIndex[5];
		int nodesRealIndex = codeIndex[6];
		
		List<String> breakWCJobList = new ArrayList<String>();
		List<String> allJobWithWCAndQLenList = new ArrayList<String>();
		List<String> allJobWithNodesCostvsQLenList = new ArrayList<String>();
		List<String> allJobWithNodesRealvsQLenList = new ArrayList<String>();
		List<NodeNumJobNumElement> generalNodeNumvsJobNumList = new ArrayList<NodeNumJobNumElement>();
		List<NodeNumJobNumElement> breakWCNodeNumvsJobNumList = new ArrayList<NodeNumJobNumElement>();
		List<NodeNumJobNumElement> allJobNodesCostvsAvgQLenList = new ArrayList<NodeNumJobNumElement>();
		List<WCJobNumElement> allJobWCvsAvgQLenList = new ArrayList<WCJobNumElement>();
		
		HashMap<String, NodeNumJobNumElement> generalNodesCostvsJobNumMap = new HashMap<String, NodeNumJobNumElement>();
		HashMap<String, NodeNumJobNumElement> breakWCNodesCostvsJobNumMap = new HashMap<String, NodeNumJobNumElement>();
		HashMap<String, NodeNumJobNumElement> allJobNodesCostvsQTimeMap = new HashMap<String, NodeNumJobNumElement>();
		HashMap<String, WCJobNumElement> allJobWCvsQTimeMap = new HashMap<String, WCJobNumElement>();
		
		List<String> lineList = PVFile.readFile(jobLogFile);
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.startsWith("#"))
				continue;
			String[] s = RecordSplitter.partition(line);
			
			String wallTimeKey = s[wallTimeIndex];
			float wallTime = Float.parseFloat(wallTimeKey);
			float queuedTime = getLongTime(s[queuedTimeIndex]);
			float startTime = getLongTime(s[startTimeIndex]);
			float endTime = getLongTime(s[endTimeIndex]);
			
			float queuingTime = startTime - queuedTime;
			
			int exitCode = 0;
			String exit = s[exitCodeIndex];
			if(!NumericChecker.isNumeric(exit))
				exitCode = -1;
			else
				exitCode = Integer.parseInt(exit);
			
			String nodesCostKey = s[nodesAlloIndex];
			int nodesCost = (int)Float.parseFloat(nodesCostKey);
			float nodesReal = Float.parseFloat(s[nodesRealIndex]);
			
			NodeNumJobNumElement element = generalNodesCostvsJobNumMap.get(nodesCostKey);
			if(element == null)
			{
				element = new NodeNumJobNumElement(nodesCost);
				generalNodeNumvsJobNumList.add(element);
				generalNodesCostvsJobNumMap.put(nodesCostKey, element);
			}
			element.setTotalJobNum(element.getTotalJobNum()+1);
			
			if(exitCode!=0)
			{
				element.setTotalFailJobNum(element.getTotalFailJobNum()+1);
				float realRunTime = endTime - startTime;
				if(wallTime - realRunTime <= timeCompareErrThreshold || realRunTime > wallTime)
				{
					//TODO build a string and put it in breakWCJobList
					breakWCJobList.add(line);
					//TODO update the breakWCNodesCostvsJobNumMap
					if(!breakWCNodesCostvsJobNumMap.containsKey(nodesCostKey))
					{
						breakWCNodeNumvsJobNumList.add(element);
						breakWCNodesCostvsJobNumMap.put(nodesCostKey, element);
					}
					
					element.setWcFailJobNum(element.getWcFailJobNum()+1);
				}
			}
			
			element = allJobNodesCostvsQTimeMap.get(nodesCostKey);
			if(element==null)
			{
				element = new NodeNumJobNumElement(nodesCost);
				allJobNodesCostvsAvgQLenList.add(element);
				allJobNodesCostvsQTimeMap.put(nodesCostKey, element);
			}
			element.setWcFailJobNum(element.getWcFailJobNum()+1);
			element.setTotalWCFailTime(element.getTotalWCFailTime()+queuingTime);
			
			WCJobNumElement element2 = allJobWCvsQTimeMap.get(wallTimeKey);
			if(element2==null)
			{
				element2 = new WCJobNumElement(wallTime, 0);
				allJobWCvsAvgQLenList.add(element2);
				allJobWCvsQTimeMap.put(wallTimeKey,  element2);
			}
			element2.setJobNum(element2.getJobNum()+1);
			element2.setTotalTime(element2.getTotalTime()+queuingTime);
			
			//TODO update allJobWithWCAndQLenList
			StringBuilder sb1 = new StringBuilder();
			String ss = sb1.append(wallTime).append(" ").append(queuingTime).toString();
			allJobWithWCAndQLenList.add(ss);
			
			//TODO update allJobWithNodesCostvsQLenList
			StringBuilder sb2 = new StringBuilder();
			String ss2 = sb2.append(nodesCost).append(" ").append(queuingTime).toString();
			allJobWithNodesCostvsQLenList.add(ss2);
			
			//TODO update allJobWithNodesRealvsQLenList
			StringBuilder sb3 = new StringBuilder();
			String ss3 = sb3.append(nodesReal).append(" ").append(queuingTime).toString();
			allJobWithNodesRealvsQLenList.add(ss3);
		}
		
		//write results to file
		Collections.sort(breakWCNodeNumvsJobNumList);
		Collections.sort(allJobNodesCostvsAvgQLenList);
		Collections.sort(allJobWCvsAvgQLenList);
		PVFile.print2File(breakWCNodeNumvsJobNumList, outputDir+"/breakWCNodeNumvsJobNumList.txt");
		PVFile.print2File(allJobWithWCAndQLenList, outputDir+"/allJobWithWCAndQLenList.ori");
		PVFile.print2File(allJobWithNodesCostvsQLenList, outputDir+"/allJobWithNodesCostvsQLenList.ori");
		PVFile.print2File(allJobWithNodesRealvsQLenList, outputDir+"/allJobWithNodesRealvsQLenList.ori");
		PVFile.print2File(allJobWCvsAvgQLenList, outputDir+"/allJobWCvsAvgQLenList.txt");
		PVFile.print2File(breakWCJobList, outputDir+"/breakWCJobList.ori");
		
		NodeNumJobNumElement.printAvgTime = true;
		PVFile.print2File(allJobNodesCostvsAvgQLenList, outputDir+"/allJobNodesCostvsAvgQLenList.txt");
		
		System.out.println("outputDir="+outputDir);
		System.out.println("done.");
		
	}
	
	public static float getLongTime(String time)
	{
		Timestamp ts = Timestamp.valueOf(time);
		float ftime = ts.getTime()/1000f;
		return ftime;
	}
	
	/**
	 * 
	 * nodes_cost means the allocated # nodes; 
	 * nodes_real means the real # nodes in use.
	 * @param basicSchemaFile
	 * @return index of time_end, time_queued, time_start, walltime, exit_code, nodes_cost, nodes_real
	 * 
	 */
	public static int[] loadSchema(String basicSchemaFile)
	{
		int[] result = new int[7];
		List<String> lineList = PVFile.readFile(basicSchemaFile);
		Iterator<String> iter = lineList.iterator();
		for(int i = 0;iter.hasNext();i++)
		{
			String s = iter.next();
			if(s.equalsIgnoreCase("time_end") || s.equalsIgnoreCase("END_TIMESTAMP"))
				result[0] = i;
			if(s.equalsIgnoreCase("time_queued") || s.equalsIgnoreCase("QUEUED_TIMESTAMP"))
				result[1] = i;
			if(s.equalsIgnoreCase("time_start") || s.equalsIgnoreCase("START_TIMESTAMP"))
				result[2] = i;
			if(s.equalsIgnoreCase("walltime") || s.equalsIgnoreCase("WALLTIME_SECONDS"))
				result[3] = i;
			if(s.equalsIgnoreCase("exit_code"))
				result[4] = i;
			if(s.equalsIgnoreCase("nodes_cost") || s.equalsIgnoreCase("REQUESTED_NODES"))
				result[5] = i;
			if(s.equalsIgnoreCase("nodes_real") || s.equalsIgnoreCase("USED_NODES"))
				result[6] = i;
		}
		
		return result;
		
	}
}

class NodeNumJobNumElement implements Comparable<NodeNumJobNumElement>
{
	static boolean printAvgTime = false;
	
	private int nodeNum;
	private int wcFailJobNum = 0;
	private int totalFailJobNum = 0;
	private int totalJobNum = 0;
	private float totalWCFailTime;  //used to compute the avg time
	
	public float getTotalWCFailTime() {
		return totalWCFailTime;
	}

	public void setTotalWCFailTime(float totalWCFailTime) {
		this.totalWCFailTime = totalWCFailTime;
	}

	public NodeNumJobNumElement(int nodeNum) {
		this.nodeNum = nodeNum;
	}
	
	public int getNodeNum() {
		return nodeNum;
	}

	public void setNodeNum(int nodeNum) {
		this.nodeNum = nodeNum;
	}

	public float getAvgTime()
	{
		return totalWCFailTime/wcFailJobNum;
	}
	
	public int getWcFailJobNum() {
		return wcFailJobNum;
	}

	public void setWcFailJobNum(int wcFailJobNum) {
		this.wcFailJobNum = wcFailJobNum;
	}

	public int getTotalFailJobNum() {
		return totalFailJobNum;
	}

	public void setTotalFailJobNum(int totalFailJobNum) {
		this.totalFailJobNum = totalFailJobNum;
	}

	public int getTotalJobNum() {
		return totalJobNum;
	}

	public void setTotalJobNum(int totalJobNum) {
		this.totalJobNum = totalJobNum;
	}

	public int compareTo(NodeNumJobNumElement other)
	{
		if(nodeNum<other.nodeNum)
			return -1;
		else if(nodeNum>other.nodeNum)
			return 1;
		else
			return 0;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if(!printAvgTime)
		{
			sb.append(nodeNum).append(" ").append(wcFailJobNum);
			sb.append(" ").append(totalFailJobNum);
			sb.append(" ").append(totalJobNum);
			float wcFailJobRatioOverAllFails = ((float)wcFailJobNum)/((float)totalFailJobNum);
			float wcFailJobRatioOverAllJobs = ((float)wcFailJobNum)/((float)totalJobNum);
			float failJobRatioOverAllJobs = ((float)totalFailJobNum)/((float)totalJobNum);
			sb.append(" ").append(wcFailJobRatioOverAllFails);
			sb.append(" ").append(wcFailJobRatioOverAllJobs);
			sb.append(" ").append(failJobRatioOverAllJobs);
			return sb.toString();
		}
		else
			return sb.append(nodeNum).append(" ").append(getAvgTime()).toString();
	}
}

class WCJobNumElement implements Comparable<WCJobNumElement>
{
	private float wallTime;
	private int jobNum;
	private float totalTime;
	
	public WCJobNumElement(float wallTime, int jobNum) {
		this.wallTime = wallTime;
		this.jobNum = jobNum;
	}

	public int getJobNum() {
		return jobNum;
	}

	public void setJobNum(int jobNum) {
		this.jobNum = jobNum;
	}
	
	public float getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(float totalTime) {
		this.totalTime = totalTime;
	}

	public float getAvgTime()
	{
		return totalTime/jobNum;
	}

	public int compareTo(WCJobNumElement other)
	{
		if(wallTime<other.wallTime)
			return -1;
		else if(wallTime>other.wallTime)
			return 1;
		else
			return 0;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		float avg = getAvgTime();
		return sb.append(wallTime).append(" ").append(avg).toString();
	}
}
