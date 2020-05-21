package analysis.Job2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import analysis.inbetween.element.RASRecord;
import filter.RecordElement;
import util.PVFile;
import util.RecordSplitter;

public class ExtractFailureJobswithAbnormalTasks {

	public static void main(String[] args)
	{
		String taskFileName = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/ANL-ALCF-TH-MIRA_20130409_20180930-total.csv";
		String cobaltDir = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job";
		String cobaltExtension = "csv";
		
		//create failed cobalt ID map
		HashMap<String, TaskSignalElement> abNormalJobMap = new HashMap<String, TaskSignalElement>();
		List<String> lineList = PVFile.readFile(taskFileName);
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] s = RecordSplitter.partition(line);
			String jobID = s[7];

			String exitstatus = s[8];
			String msg = s[10];
			String[] ss = msg.split("\\s");
			if(exitstatus.equals(""))
			{
				if(msg.startsWith("normal termination"))
					exitstatus = "0";
				else 
				{
					exitstatus = "888";
					if(msg.startsWith("abnormal termination by"))
					{
						exitstatus = ss[4];
						if(msg.contains("timed out") && (exitstatus.equals("9") || exitstatus.equals("36")))
						{
							exitstatus = "333"; //time out tasks/jobs
						}
					}
				}
			}
			if(exitstatus.equals("35"))
			{
				exitstatus += " " + ss[15].replaceAll("\\.", ""); //RAS record ID
			}
			
			if(!exitstatus.equals("0") && (!msg.startsWith("normal termination")))
			{
				TaskSignalElement record = abNormalJobMap.get(jobID);
				
				if(record==null)
				{
					record = new TaskSignalElement(jobID, new StringBuilder());
					abNormalJobMap.put(jobID, record);
				}
				String str = record.getSignalStr().toString();
				if(str.equals(""))
					record.getSignalStr().append(exitstatus);
				else
				{
					if(!str.contains(" ") && !str.contains(exitstatus))
						record.getSignalStr().append("-").append(exitstatus);
				}
			}
		}
		
		List<String> nonzeroJobList = new ArrayList<String>();
		List<String> abnormalJobList = new ArrayList<String>(); 
		
		long totalTimeOutJobs = 0;
		long totalNonZeroExitJobCount = 0, abnormalJobCount = 0;
		//scan the cobalt log to get the jobs
		List<String> fileList = PVFile.getFiles(cobaltDir,  cobaltExtension);
		Collections.sort(fileList);
		Iterator<String> iterx = fileList.iterator();
		while(iterx.hasNext())
		{
			String fileName = iterx.next();
			System.out.println("processing "+fileName);
			String filePath = cobaltDir+"/"+fileName;
			List<String> lineList2 = PVFile.readFile(filePath);
			Iterator<String> iter2 = lineList2.iterator();
			iter2.next(); //remove the first metadata line
			
			while(iter2.hasNext())
			{
				String line = iter2.next();
				if(line.startsWith("#"))
					continue;
				String[] s = RecordSplitter.partition(line);
				int exitCode = Integer.parseInt(s[19]);
				if(exitCode!=0)
				{
					totalNonZeroExitJobCount++;
					nonzeroJobList.add(line);
				}
				
				String jobID = s[1];
				TaskSignalElement e = abNormalJobMap.get(jobID);
				if(e!=null)
				{
					e.setCobaltMsg(line);
					String codes = e.getSignalStr().toString();
					
					boolean bugJob = checkBugJob(codes);
					if(bugJob)
						e.setSignalStr(new StringBuilder("777"));
					
					if(!codes.equals("0") && e.checkTimeOut())
					{
						totalTimeOutJobs++;
						e.setSignalStr(new StringBuilder("999"));
					}
					abnormalJobList.add(e.toString());
					abnormalJobCount ++;
				}
			}
		}
		
		PVFile.print2File(nonzeroJobList, cobaltDir+"/abnormalJobs/nonzeroJobList.csv");
		PVFile.print2File(abnormalJobList, cobaltDir+"/abnormalJobs/abnormalJobList.csv");
		
		System.out.println("totalTimeOutJobs = " + totalTimeOutJobs);
		System.out.println("totalNonZeroExitJobCount = "+totalNonZeroExitJobCount);
		System.out.println("abnormalJobCount = "+abnormalJobCount);
		System.out.println("done");
		
	}
	
	public static boolean checkBugJob(String codes)
	{
		String[] ss = codes.split("-");
		for(int i = 0;i<ss.length;i++)
		{
			if(ss[i].equals("6") || ss[i].equals("11"))
				return true;
		}
		return false;
	}
}

class TaskSignalElement
{
	private String jobID;
	private StringBuilder signalStr;
	private String cobaltMsg = null;
	
	public TaskSignalElement(String jobID, StringBuilder signalStr) {
		this.jobID = jobID;
		this.signalStr = signalStr;
	}

	public String getJobID() {
		return jobID;
	}
	public void setJobID(String jobID) {
		this.jobID = jobID;
	}
	public StringBuilder getSignalStr() {
		return signalStr;
	}
	public void setSignalStr(StringBuilder signalStr) {
		this.signalStr = signalStr;
	}
	public String getCobaltMsg() {
		return cobaltMsg;
	}
	public void setCobaltMsg(String cobaltMsg) {
		this.cobaltMsg = cobaltMsg;
	}
	
	public boolean checkTimeOut()
	{
		String[] s = RecordSplitter.partition(cobaltMsg);
		double start = RecordElement.computeDoubleTimeinSeconds(s[5].replace(";", " "));
		double end = RecordElement.computeDoubleTimeinSeconds(s[7].replace(";", " "));
		
		double execTimeSecs = end - start;
		double wallTime = Double.parseDouble(s[12]);
		
		boolean timeout = execTimeSecs >= wallTime?true:false;
/*		if(timeout && !(signalStr.toString().contains("999")))
		{
			//System.out.println("Error: missing time out recoreds....: "+execTimeSecs +" -- "+wallTime+" -- " +cobaltMsg);
			signalStr
		}*/
		return timeout;
	}
	
	
	public String toString()
	{
		//sorting signalStr
		String s = signalStr.toString();
		String[] s_ = s.split(" ");
		String[] ss = s_[0].split("-");
		if(ss.length>1)
		{
			List<Integer> list = new ArrayList<Integer>();
			for(int i = 0;i<ss.length;i++)
			{
				list.add(Integer.parseInt(ss[i]));
			}
			Collections.sort(list);
			Iterator<Integer> iter = list.iterator();
			String sb = String.valueOf(iter.next());
			while(iter.hasNext())
			{
				sb+="-"+iter.next();
			}
			if(s_.length==1)
				return cobaltMsg + ","+sb;
			else
				return cobaltMsg + ","+sb+" "+s_[1]; //s_1[1] means the RAS record ID
		}
		else
			return cobaltMsg+","+s;
	}
	
}