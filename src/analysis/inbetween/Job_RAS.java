/**
 * @author Sheng Di
 * @class Job_RAS.java
 * @description  
 */

package analysis.inbetween;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import analysis.inbetween.element.JobRASResult;
import analysis.inbetween.element.JobRecord;
import analysis.inbetween.element.RASJobConnector;
import analysis.inbetween.element.RASRecord;
import util.PVFile;
import util.RecordSplitter;

public class Job_RAS {

	public static void main(String[] args)
	{
		if(args.length < 5)
		{
			System.out.println("Usage: java Job_RAS [RAS log dir] [RAS_log_extension] [Job log dir] [Job_log_file_extension] [outputFile]");
			System.out.println("Example: java Job_RAS /home/fti/Catalog-project/miralog/RAS-Job/RAS csv /home/fti/Catalog-project/miralog/RAS-Job/Job csv /home/fti/Catalog-project/miralog/RAS-Job/Job-RAS.txt");
			System.exit(0);
		}
		
		String rasLogDir = args[0];
		String rasExt = args[1];
		String jobLogDir = args[2];
		String jobExt = args[3];
		String outputFile = args[4];
		

		System.out.println("loading the ras records....");
		List<RASRecord> rasList = RASJobConnector.getRASRecordList(rasLogDir, rasExt);
		System.out.println("loading the job records....");
		List<JobRecord> jobList = RASJobConnector.getJobRecordList(jobLogDir, jobExt);
		
		System.out.println("Sorting ras and job list....");
		Collections.sort(rasList);
		Collections.sort(jobList);
		
		System.out.println("Processing....");
		List<String> resultList = new ArrayList<String>();
		RASJobConnector connector = new RASJobConnector(rasList, jobList);
		
		Iterator<JobRecord> iter = jobList.iterator();
		while(iter.hasNext())
		{
			JobRecord jobRecord = iter.next();
			double time = jobRecord.getTime();
			String jobID = jobRecord.getJobID();
			String jobBlockCode = jobRecord.getBlockCode();
//			String recordString = jobRecord.getRecord();
//			String[] s = RecordSplitter.partition(recordString);
//			String jobID = s[22];

			List<RASRecord> relateRASList = connector.getJobRelatedRASEvents(time, jobBlockCode);
			JobRASResult result = new JobRASResult(jobID, time, relateRASList);
			resultList.addAll(result.toOutputList());
		}
		
		System.out.println("Wrinting results to "+outputFile);
		PVFile.print2File(resultList, outputFile);
		System.out.println("Done.");
	}
}
