/**
 * @author Sheng Di
 * @class RASJobConnector
 * @description  
 */

package analysis.inbetween.element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tools.ComputeLocationBasedOnIOCode;
import tools.ComputeLocationBasedOnMIRCode;
import util.PVFile;
import util.RecordSplitter;
import filter.RecordElement;

public class RASJobConnector {

	public static int durationThreshold = 240; //or 300 seconds?
	private List<RASRecord> rasRecordList;
	private List<JobRecord> jobRecordList;
	private int latestJobRecLineNum = 0; //its represent the line number that the record time is nearest (but smaller) to the current checking time
	private int latestRASRecLineNum = 0;
	
	public RASJobConnector(List<RASRecord> rasRecordList,
			List<JobRecord> jobRecordList) {
		this.rasRecordList = rasRecordList;
		this.jobRecordList = jobRecordList;
	}

	private boolean checkBlockOverlaping(String rasBlockCode, String jobBlockCode)
	{
		if(rasBlockCode.startsWith("MIR"))
		{
			int checking = ComputeLocationBasedOnMIRCode.checkBlockOverlapBlock(rasBlockCode, jobBlockCode);
			if(checking==0)
				return false;
			else
				return true;
		}
		else if(rasBlockCode.startsWith("Q"))//start with Q
		{
			int checking = ComputeLocationBasedOnMIRCode.checkIOBlockOverlapBlock(rasBlockCode, jobBlockCode);
			if(checking==0)
				return false;
			else
				return true;
		}
		else
		{
			System.out.println("Error: wrong blockCode = "+rasBlockCode);
			//System.exit(0);
			return false;
		}
	}
	
	public List<JobRecord> getRASEventRelatedJobs(double curTimeStamp, String rasBlockCode)
	{	
		List<JobRecord> resultList = new ArrayList<JobRecord>();
		double startTimeStamp = curTimeStamp;
		double endTimeStamp = curTimeStamp + durationThreshold;
		Iterator<JobRecord> iter = updateJobIterator();
		int i;
		for(i = 0;iter.hasNext();i++)
		{
			JobRecord recordItem = iter.next();
			double timeend = recordItem.getTime();
			String jobBlockCode = recordItem.getBlockCode();
			if(timeend >= startTimeStamp && timeend <= endTimeStamp)
			{
				boolean overlap = false;
				if(rasBlockCode.startsWith("Q"))
				{
					overlap = ComputeLocationBasedOnIOCode.checkIODrawerCodeOverlapComputeBlockCode(rasBlockCode, jobBlockCode);
				}
				else//compute node location
				{
					if(rasBlockCode.startsWith("R"))//node
					{
						overlap = ComputeLocationBasedOnMIRCode.checkNodeOverlapBlock(rasBlockCode, jobBlockCode);
					}
					else if(rasBlockCode.startsWith("MIR"))
					{
						overlap = checkBlockOverlaping(rasBlockCode, jobBlockCode);
					}
				}
				if(overlap)
					resultList.add(recordItem);
			}
			
			if(timeend > endTimeStamp)
				break;
		}
		latestJobRecLineNum += i;
		return resultList;
	}
	
	public List<RASRecord> getJobRelatedRASEvents(double curTimeStamp, String jobBlockCode)
	{
		List<RASRecord> resultList = new ArrayList<RASRecord>();
		double startTimeStamp = curTimeStamp;
		double endTimeStamp = curTimeStamp + durationThreshold;
		Iterator<RASRecord> iter = updateRASIterator();
		int i = 0;
		for(;iter.hasNext();i++)
		{
			RASRecord recordItem = iter.next();
			double timeend = recordItem.getTime();
			String rasBlockCode = recordItem.getBlockCode();
			if(timeend >= startTimeStamp && timeend <= endTimeStamp)
			{
				boolean overlap = checkBlockOverlaping(rasBlockCode, jobBlockCode);
				if(overlap)
					resultList.add(recordItem);
			}
			
			if(timeend > endTimeStamp)
				break;
		}
		latestRASRecLineNum += i;
		return resultList;
		
	}
	
	Iterator<JobRecord> updateJobIterator()
	{
		Iterator<JobRecord> iter = jobRecordList.iterator();
		iter.next(); //filter out the field line
		for(int i = 0;i<latestJobRecLineNum&&iter.hasNext();i++)
			iter.next();
		return iter;
	}
	
	Iterator<RASRecord> updateRASIterator()
	{
		Iterator<RASRecord> iter = rasRecordList.iterator();
		for(int i = 0;i<latestRASRecLineNum&&iter.hasNext();i++)
			iter.next();
		return iter;
	}
	
	public static List<JobRecord> getJobRecordList(String logDir, String extension)
	{
		List<String> fileList = PVFile.getFiles(logDir, extension);
		List<JobRecord> recordList = new ArrayList<JobRecord>();
		Iterator<String> iter = fileList.iterator();
		while(iter.hasNext())
		{
			String jobLogFile = iter.next();
			String jobLogPath = logDir+"/"+jobLogFile;
			List<String> lineList = PVFile.readFile(jobLogPath);
			Iterator<String> iter2 = lineList.iterator();
			while(iter2.hasNext())
			{
				String line = iter2.next();
				if(line.startsWith("#"))
					continue;
				String[] s = RecordSplitter.partition(line);
//				String timeString = s[43]; //only for old one-month job log
//				String jobID = s[22];      //only for old one-month job log
//				String blockCode = s[23];  //only for old one-month job log
				String timeString = s[2];
				String jobID = s[25];
				String blockCode = s[18];
				double time = RecordElement.computeFloatTimeinSeconds(timeString);
				JobRecord record = new JobRecord(time, jobID, line, blockCode); //cobalt job ID
				recordList.add(record);
			}
		}
		
		return recordList;
	}
	
	public static List<RASRecord> getRASRecordList2(String logDir, String extension)
	{
		List<String> fileList = PVFile.getFiles(logDir, extension);
		List<RASRecord> recordList = new ArrayList<RASRecord>();
		Iterator<String> iter = fileList.iterator();
		while(iter.hasNext())
		{
			String rasLogFile = iter.next();
			String rasLogPath = logDir+"/"+rasLogFile;
			List<String> lineList = PVFile.readFile(rasLogPath);
			Iterator<String> iter2 = lineList.iterator();
			while(iter2.hasNext())
			{
				String line = iter2.next();
				if(line.startsWith("#"))
					continue;
				//String[] s = RecordSplitter.partition(line);
				String[] s = line.split("\\s");
				String timeString = s[5];
				timeString = timeString.replace(";", " ");
				String recordID = s[0];
				String msgID = s[1];
				String blockCode = s[9];
				double time = RecordElement.computeDoubleTimeinSeconds(timeString);
				RASRecord record = new RASRecord(time, recordID, blockCode, msgID, line);
				recordList.add(record);
			}
		}
		
		return recordList;
	}
	
	public static List<RASRecord> getRASRecordList(String logDir, String extension)
	{
		List<String> fileList = PVFile.getFiles(logDir, extension);
		List<RASRecord> recordList = new ArrayList<RASRecord>();
		Iterator<String> iter = fileList.iterator();
		while(iter.hasNext())
		{
			String rasLogFile = iter.next();
			String rasLogPath = logDir+"/"+rasLogFile;
			List<String> lineList = PVFile.readFile(rasLogPath);
			Iterator<String> iter2 = lineList.iterator();
			while(iter2.hasNext())
			{
				String line = iter2.next();
				if(line.startsWith("#"))
					continue;
				String[] s = RecordSplitter.partition(line);
				String timeString = s[5];
				String recordID = s[0];
				String msgID = s[1];
				String blockCode = s[7];
				float time = RecordElement.computeFloatTimeinSeconds(timeString);
				RASRecord record = new RASRecord(time, recordID, blockCode, msgID, line);
				recordList.add(record);
			}
		}
		
		return recordList;
	}

	public int getLatestJobRecLineNum() {
		return latestJobRecLineNum;
	}

	public void setLatestJobRecLineNum(int latestJobRecLineNum) {
		this.latestJobRecLineNum = latestJobRecLineNum;
	}

	public int getLatestRASRecLineNum() {
		return latestRASRecLineNum;
	}

	public void setLatestRASRecLineNum(int latestRASRecLineNum) {
		this.latestRASRecLineNum = latestRASRecLineNum;
	}
}
