package analysis.inbetween2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import analysis.inbetween.element.RASRecord;
import util.PVFile;
import util.RecordSplitter;

public class RAS_Fatal_Msg_Mapping_Jobs {

	//static String fatalRASFile = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/ANL-ALCF-RE-MIRA_20130409-20170831-fatal.csv";
	//static String cobaltJobFiles_str = "ANL-ALCF-DJC-MIRA_20130409_20131231.csv ANL-ALCF-DJC-MIRA_20140101_20141231.csv ANL-ALCF-DJC-MIRA_20150101_20151231.csv ANL-ALCF-DJC-MIRA_20160101_20161231.csv ANL-ALCF-DJC-MIRA_20170101_20171231.csv ANL-ALCF-DJC-MIRA_20180101_20180630.csv";	
	static String cobaltJobLogDir = "/home/sdi/windows-xp/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job";
	
	static String errorJobListFile = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/ANL-ALCF-DJC-MIRA_20130409_20180930-errorjob.csv";
	static String fatalTaskListFile = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/ANL-ALCF-RE-MIRA_20130409_20180930-fatal.csv.tasklist";
	static String taskLogFile = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/ANL-ALCF-TH-MIRA_20130409_20180930-total.csv";
	
	public static void main(String[] args)
	{
	
		//load fatal events
		PrintFatalTaskID.getFatalTaskList(fatalTaskListFile); //format: RAS log
		System.out.println("total # RAS fatal tasks: "+PrintFatalTaskID.fatalTaskList.size());
		
		//load cobalt job list
		HashMap<String, String>	errorCobaltJobIDMap = PrintErrorJobs.loadCobaltJobIDMap(errorJobListFile);
		System.out.println("total # cobalt error jobs: " + errorCobaltJobIDMap.size());
		
		//load th tasks ....
		LoadHashMapFromTaskJobLog.loadTasks(taskLogFile);
	
		System.out.println("Starting analysis ....");
		
		//TODO
		
		List<String> resultList = new ArrayList<String>();
		
		int rasErrorNormalJobsCount = 0;
		List<String> rasErrorNomralJobIDList = new ArrayList<String>();
		HashMap<String, String> rasErrorNormalJobIDMap = new HashMap<String, String>();
		int rasErrorFailedJobsCount = 0;
		List<String> missingJobID_taskList = new ArrayList<String>();
		int taskIDMissingJobID = 0;
		
		Iterator<RASRecord> iter = PrintFatalTaskID.fatalTaskList.iterator();
		while(iter.hasNext())
		{
			RASRecord record = iter.next();
			String taskID = String.valueOf(record.getTaskID());
			String jobID = LoadHashMapFromTaskJobLog.taskJobMap.get(taskID);
			if(jobID==null)
			{
				taskIDMissingJobID ++;
				missingJobID_taskList.add(taskID);
				continue;
			}
			String jobLine = errorCobaltJobIDMap.get(jobID);
			if(jobLine==null)
			{
				rasErrorNormalJobsCount++;
				if(rasErrorNormalJobIDMap.get(jobID)==null)
				{
					rasErrorNormalJobIDMap.put(jobID, jobID);
					rasErrorNomralJobIDList.add(jobID);
				}
				
				continue;
			}
			else
				rasErrorFailedJobsCount++;
			
			String[] s = RecordSplitter.partition(jobLine);
			int exitStatus = Integer.parseInt(s[19]);
			StringBuilder sb = new StringBuilder();
			sb.append(record.getMsgID()).append(",").append(record.getCategory()).append(",").append(record.getComponent()).append(",").append(record.getBlockCode()).append(",").append(exitStatus).append(",").append(jobLine);
			resultList.add(sb.toString());
		}
		
		System.out.println("rasErrorNomralJobIDList.size()="+rasErrorNomralJobIDList.size()+",rasErrorNormalJobsCount="+rasErrorNormalJobsCount+",rasErrorFailedJobsCount="+rasErrorFailedJobsCount);
		
		PVFile.print2File(resultList, cobaltJobLogDir+"/rasErrJobFile.csv");		
		System.out.println("output file: "+cobaltJobLogDir+"/rasErrJobFile.csv");
		
		PVFile.print2File(rasErrorNomralJobIDList, cobaltJobLogDir+"/rasErrorNomralJobIDList.txt");
		System.out.println("rasErrorNomralJobIDList file: "+cobaltJobLogDir+"/rasErrorNomralJobIDList.txt");
		
		PVFile.print2File(missingJobID_taskList, cobaltJobLogDir+"/missingJobID_taskList.txt");
		System.out.println("missingJobID_taskList file: "+cobaltJobLogDir+"/missingJobID_taskList.txt");
	}
}

