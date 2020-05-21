package analysis.Job2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import element.ExitCodeStatElement;
import element.JobStatElement;
import filter.RecordElement;
import util.PVFile;
import util.RecordSplitter;


public class JobDistributionAnalysis {

	public static double MAXWALLTIME = 50.0;
	public static int MAXNODETIME = 1179648;

	public static void main(String[] args)
	{
		String schemaFile = "/home/sdi/windows-xp/Work/Catalog-project/Catalog-data/miralog/RAS-Job/Job/basicSchema/basicSchema-job.txt";
		String dataDir = "/home/sdi/windows-xp/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job";
		String extension = "csvs";
		
		String errFile = dataDir+"/errorFile.txt";
		String normFile = dataDir+"/normalFile.txt";
		String totalFile = dataDir+"/totalFile.txt";
		
		//List<String> lineList = PVFile.getFiles(dataDir, extension);
		//Collections.sort(lineList);
		List<String> lineList = new ArrayList<String>();
		lineList.add("ANL-ALCF-DJC-MIRA_20130409_20131231.csvs");
		lineList.add("ANL-ALCF-DJC-MIRA_20140101_20141231.csvs");
		lineList.add("ANL-ALCF-DJC-MIRA_20150101_20151231.csvs");
		lineList.add("ANL-ALCF-DJC-MIRA_20160101_20161231.csvs");
		lineList.add("ANL-ALCF-DJC-MIRA_20170101_20171231.csvs");
		lineList.add("ANL-ALCF-DJC-MIRA_20180101_20180930.csvs");
		
		HashMap<String, JobStatElement> userMap = new HashMap<String, JobStatElement>();
		HashMap<String, JobStatElement> projectMap = new HashMap<String, JobStatElement>();
		HashMap<String, ExitCodeStatElement> exitCodeMap = new HashMap<String, ExitCodeStatElement>();
		
		List<JobStatElement> userJobList = new ArrayList<JobStatElement>();
		List<JobStatElement> projJobList = new ArrayList<JobStatElement>();
		List<ExitCodeStatElement> exitCodeList = new ArrayList<ExitCodeStatElement>();
		
		List<String> consecutiveTaskCountList = new ArrayList<String>();
		List<String> multilocationTaskCountList = new ArrayList<String>();
		List<String> totalList = new ArrayList<String>();		
		List<String> errorList = new ArrayList<String>();
		List<String> normalList = new ArrayList<String>();
		
		long singleTaskCount = 0;
		long consecutiveTaskCount = 0;
		long multilocationTaskCount = 0;
		
		long[] totalNodeJobCount = new long[97];
		long[] errorNodeJobCount = new long[97];
		long totalJobSum = 0, errorJobSum = 0;
		
		long[] totalExecTimeJobCount = new long[100];
		long[] errorExecTimeJobCount = new long[100];
		
		double maxWallTime = 0;
		
		long[] totalNodeTimeJobCount = new long[101];
		long[] errorNodeTimeJobCount = new long[101];
		double maxNodeTime = 0;
		
		//List<Double> allJobLengthList = new ArrayList<Double>();
		//List<Double> errJobLengthList = new ArrayList<Double>();
		
		int maxNodeTimeInteval = MAXNODETIME/100;
		
		double sum_core_hours = 0;
		
		int[] jobsubmissionCount = new int[2001]; //2001 days
		double initTime = RecordElement.computeDoubleTimeinSeconds("2013-04-09 00:00:00.000000");
		
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String fileName = iter.next();
			String filePath = dataDir+"/"+fileName;
			System.out.println("Processing "+filePath);
			
			List<String> lineList2 = PVFile.readFile(filePath);
			Iterator<String> iter2 = lineList2.iterator();
			iter2.next(); //remove the first metadata line
			
			while(iter2.hasNext())
			{
				String line = iter2.next();
				if(line.startsWith("#"))
					continue;
				String[] s = RecordSplitter.partition(line);
				//int exitCode = Integer.parseInt(s[19]);
				String exitSignal = s[57];
				
				double submitTime = RecordElement.computeDoubleTimeinSeconds(s[3].replace(";", " "));
				double start = RecordElement.computeDoubleTimeinSeconds(s[5].replace(";", " "));
				double end = RecordElement.computeDoubleTimeinSeconds(s[7].replace(";", " "));
				
				int day = (int)(submitTime - initTime)/86400;
				if(day>=0)
					jobsubmissionCount[day]++;
				
				
				double execTimeSecs = end - start;
				double execTimeHours = execTimeSecs/3600;
				if(execTimeHours > maxWallTime)
					maxWallTime = execTimeHours;
				
				//allJobLengthList.add(execTimeHours);
				
				int index2 = (int)(execTimeHours);
				float nodeCount = Float.parseFloat(s[14]);
				
				double nodeTime = nodeCount*execTimeHours;
				if(maxNodeTime < nodeTime)
					maxNodeTime = nodeTime;
				
				int index3 = (int)(nodeTime/maxNodeTimeInteval);
				
				double runtime = Double.parseDouble(s[13]);
				float coresCount = Float.parseFloat(s[16]);
				double core_hours = runtime/3600*coresCount;
				sum_core_hours += core_hours;
				
				double wallTime = Double.parseDouble(s[12])/3600.0;
				double queueTime = Double.parseDouble(s[22])/3600.0;
				int queueWaitFactor = Integer.parseInt(s[23]);
				String jobID = s[1];
				int nodesRequested = (int)Double.parseDouble(s[15]);
				String nbTasks = s[43];
				
				singleTaskCount+= Integer.parseInt(s[44]);
				consecutiveTaskCount+=Integer.parseInt(s[45]);
				multilocationTaskCount+=Integer.parseInt(s[46]);
				
				int nbConsecutiveTasks = Integer.parseInt(s[40]);
				int nbMultilocationTasks = Integer.parseInt(s[41]);
				if(nbConsecutiveTasks > 0)
					consecutiveTaskCountList.add(String.valueOf(nbConsecutiveTasks));
				if(nbMultilocationTasks > 0)
					multilocationTaskCountList.add(String.valueOf(nbMultilocationTasks));
				String ss = jobID+" "+nodeCount+" "+execTimeHours+" "+(float)queueTime+" "+nodesRequested+" "+queueWaitFactor + " "+queueTime/execTimeHours + " " +nbTasks+" "+wallTime;
				int index = nodesRequested/512;
				
				String userID = s[9];
				String projID = s[10];
				JobStatElement userJobElement = userMap.get(userID);
				if(userJobElement==null)
				{
					userJobElement = new JobStatElement(userID);
					userJobElement.setCorehours(core_hours);
					userMap.put(userID, userJobElement);
					userJobList.add(userJobElement);
				}
				else
				{
					userJobElement.setCorehours(userJobElement.getCorehours()+core_hours);
				}
				JobStatElement projJobElement = projectMap.get(projID);
				if(projJobElement==null)
				{
					projJobElement = new JobStatElement(projID);
					projJobElement.setCorehours(core_hours);
					projectMap.put(projID, projJobElement);
					projJobList.add(projJobElement);
				}
				else
				{
					projJobElement.setCorehours(projJobElement.getCorehours()+core_hours);
				}
				
				ExitCodeStatElement ecse = exitCodeMap.get(exitSignal);
				
				if(ecse==null)
				{
					ecse = new ExitCodeStatElement(exitSignal);
					exitCodeMap.put(exitSignal, ecse);
					exitCodeList.add(ecse);
				}
				
				Integer jobCount = ecse.jobMap.get(jobID);
				if(jobCount==null)
					ecse.jobMap.put(jobID, 0);
				else
					ecse.jobMap.put(jobID, jobCount+1);

				Integer userCount = ecse.userMap.get(userID);
				if(userCount==null)
					ecse.userMap.put(userID, 0);
				else
					ecse.userMap.put(userID, userCount+1);
				
				Integer projCount = ecse.projMap.get(projID);
				if(projCount==null)
					ecse.projMap.put(projID, 0);
				else
					ecse.projMap.put(projID, projCount+1);
								
				totalList.add(ss);
				if(exitSignal.equals("0"))
				{
					normalList.add(ss);
					userJobElement.incrementNormalJobs();
					projJobElement.incrementNormalJobs();
				}
				else
				{	
					errorList.add(ss);	
					errorNodeJobCount[index]++;
					errorExecTimeJobCount[index2]++;
					errorNodeTimeJobCount[index3]++;
					errorJobSum ++;
					userJobElement.incrementErrorJobs();
					projJobElement.incrementErrorJobs();
					//errJobLengthList.add(execTimeHours);
				}
				totalNodeJobCount[index]++;
				totalExecTimeJobCount[index2]++;
				totalNodeTimeJobCount[index3]++;
				totalJobSum ++;
			}
		}
		
		
		int max = 0, min = 3000000, avg = 0;
		for(int i = 0;i<2001;i++)
		{
			if(max < jobsubmissionCount[i])
				max = jobsubmissionCount[i];
			if(min > jobsubmissionCount[i])
				min = jobsubmissionCount[i];
			avg += jobsubmissionCount[i];
		}
		
		System.out.println("min="+min+",max="+max+",avg="+(1.0*avg/2001));
		
		System.out.println("single task acount = "+singleTaskCount+"; consecutive task acount = "+consecutiveTaskCount+"; multilocation task count = "+multilocationTaskCount);
		System.out.println("total core hours = "+sum_core_hours);
		System.out.println("totalSum = "+totalJobSum+" , errorSum = "+errorJobSum + " ; ratio = "+1.0*errorJobSum/totalJobSum+", maxWallTime = "+maxWallTime+" , maxNodeTime = "+maxNodeTime); 
		
		List<String> errorStatNodeJobList = new ArrayList<String>();
		for(int i = 0;i<97;i++)
			errorStatNodeJobList.add(i+" "+totalNodeJobCount[i]+" "+errorNodeJobCount[i]+" "+(1.0*errorNodeJobCount[i]/totalNodeJobCount[i]));
		
		List<String> errorStatTimeJobList = new ArrayList<String>();
		for(int i = 0;i<=50;i++)
			errorStatTimeJobList.add(i+" "+totalExecTimeJobCount[i]+" "+errorExecTimeJobCount[i]+" "+(1.0*errorExecTimeJobCount[i]/totalExecTimeJobCount[i]));
			
		List<String> errorNodeTimeJobList = new ArrayList<String>();
		for(int i = 0;i<=100;i++)
			errorNodeTimeJobList.add(i+" "+totalNodeTimeJobCount[i]+" "+errorNodeTimeJobCount[i]+" "+(1.0*errorNodeTimeJobCount[i]/totalNodeTimeJobCount[i]));

		ExitCodeStatElement.mode = 0;
		Collections.sort(exitCodeList);
		PVFile.print2File(exitCodeList, dataDir+"/exitCodeStat/IDs-sorted.txt");
		
		ExitCodeStatElement.mode = 1;
		Collections.sort(exitCodeList);
		PVFile.print2File(exitCodeList, dataDir+"/exitCodeStat/users-sorted.txt");
		
		ExitCodeStatElement.mode = 2;
		Collections.sort(exitCodeList);
		PVFile.print2File(exitCodeList, dataDir+"/exitCodeStat/projs-sorted.txt");	
		
		ExitCodeStatElement.mode = 3;
		Collections.sort(exitCodeList);
		PVFile.print2File(exitCodeList, dataDir+"/exitCodeStat/job-count-sorted.txt");
		
		String errorStatNodeJobFile = dataDir+"/errorStat_nodeJob.txt";
		String errorStatTimeJobFile = dataDir+"/errorStat_timeJob.txt";
		String errorStateNodeTimeJobFile = dataDir+"/errorStat_nodetimeJob.txt";
		String userJobStatFile = dataDir+"/userJobStatFile.txt";
		String projJobStatFile = dataDir+"/projJobStatFile.txt";
		String consecutiveTaskCountFile = dataDir+"/consecutiveTaskCountFile.txt";
		String multilocationTaskCountFile = dataDir+"/multilocationTaskCountFile.txt";
		
		//String allJobLengthListFile = dataDir+"/allJobLengthList.txt";
		//String errJobLengthListFile = dataDir+"/errJobLengthList.txt";
		
		//PVFile.print2File(allJobLengthList, allJobLengthListFile);
		//PVFile.print2File(errJobLengthList, errJobLengthListFile);
		PVFile.print2File(errorStatNodeJobList, errorStatNodeJobFile);
		PVFile.print2File(errorStatTimeJobList, errorStatTimeJobFile);
		PVFile.print2File(errorNodeTimeJobList, errorStateNodeTimeJobFile);
		PVFile.print2File(normalList, normFile);
		PVFile.print2File(errorList, errFile);
		PVFile.print2File(totalList, totalFile);
		PVFile.print2File(consecutiveTaskCountList, consecutiveTaskCountFile);
		PVFile.print2File(multilocationTaskCountList, multilocationTaskCountFile);
		
		
		Collections.sort(userJobList);
		Collections.sort(projJobList);
		PVFile.print2File(userJobList, userJobStatFile);
		PVFile.print2File(projJobList, projJobStatFile);
		
		System.out.println("errorStatNodeJobFile = "+errorStatNodeJobFile);
		System.out.println("errorStatTimeJobFile = "+errorStatTimeJobFile);
		System.out.println("errorStateNodeTimeJobFile = "+errorStateNodeTimeJobFile);
		System.out.println("errfile = "+errFile);
		System.out.println("normfile = "+normFile);
		System.out.println("totalFile = "+totalFile);
		System.out.println("userJobFile= "+userJobStatFile);
		System.out.println("projJobFile= "+projJobStatFile);
		System.out.println("exit code stats = "+dataDir+"/exitCodeStat/");
		System.out.println("Done");
	}
}
