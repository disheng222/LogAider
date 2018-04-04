/**
 * @author Sheng Di
 * @class RAS_Job.java
 * @description  
 */

package analysis.inbetween;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import analysis.inbetween.element.JobRecord;
import analysis.inbetween.element.RASJobConnector;
import analysis.inbetween.element.RASJobResult;
import analysis.inbetween.element.RASRecord;
import filter.RecordElement;
import util.PVFile;
import util.RecordSplitter;
public class RAS_Job {

	public static void main(String[] args)
	{
		
		if(args.length < 5)
		{
			System.out.println("Usage: java RAS_Job [RAS log dir] [RAS_log_extension] [Job log dir] [Job_log_file_extension] [outputFile]");
			System.out.println("Example: java RAS_Job /home/fti/Catalog-project/miralog/RAS-Job/RAS csv /home/fti/Catalog-project/miralog/RAS-Job/Job csv /home/fti/Catalog-project/miralog/RAS-Job/RAS-Job.txt");
			System.exit(0);
		}
		
		String rasLogDir = args[0];
		String rasExt = args[1];
		String jobLogDir = args[2];
		String jobExt = args[3];
		String outputFile = args[4];
		

		System.out.println("loading the ras records....");
		//getRASRecordList2 is used for filtered file
		List<RASRecord> rasList = RASJobConnector.getRASRecordList2(rasLogDir, rasExt);
		System.out.println("loading the job records....");
		List<JobRecord> jobList = RASJobConnector.getJobRecordList(jobLogDir, jobExt);
		
		System.out.println("Sorting ras and job list....");
		Collections.sort(rasList);
		Collections.sort(jobList);
		
		System.out.println("Processing....");
		List<String> resultList = new ArrayList<String>();
		RASJobConnector connector = new RASJobConnector(rasList, jobList);
		
		Iterator<RASRecord> iter = rasList.iterator();
		while(iter.hasNext())
		{
			RASRecord rasRecord = iter.next();
			double time = rasRecord.getTime();
/*			String recordString = rasRecord.getRecord();
			String[] s = RecordSplitter.partition(recordString);
			String recordID = s[0];
			String msgID = s[1]; //msgID
			//String category = s[2];
			//String component = s[3];
			String blockCode = s[7];*/
/*			if(blockCode.startsWith("M"))
			{	
				//This event happened on compute rack
				String[] ss = blockCode.split("-");
				int nodeCount = Integer.parseInt(ss[ss.length-1]);
				List<RASJobRecord> relatedJobList = connector.getRASEventRelatedJobs(time);
				
			}
			else 
			{
				//This event happened on IO rack
			}*/
			//for filtered records
			String recordID = rasRecord.getRecordID();
			String msgID = rasRecord.getMsgID();
			String blockCode = rasRecord.getBlockCode();
			List<JobRecord> relateJobList = connector.getRASEventRelatedJobs(time, blockCode);
			RASJobResult result = new RASJobResult(recordID+"-"+msgID, time, relateJobList);
			resultList.addAll(result.toOutputList());
		}
		
		System.out.println("Wrinting results to "+outputFile);
		PVFile.print2File(resultList, outputFile);
		System.out.println("Done.");
	}
	
}
