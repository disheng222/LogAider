/**
 * @author Sheng Di
 * @class Job_RAS_Analysis.java
 * @description  
 */

package analysis.inbetween;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import filter.RecordElement;
import util.PVFile;
import util.RecordSplitter;
import analysis.inbetween.element.JobField;
import analysis.inbetween.element.MsgJobMappingItem;
import analysis.inbetween.element.ValueCountItem;

public class Job_RAS_Analysis {

	static List<JobField> jobFieldList = null; 

	public static void main(String[] args)
	{
		if(args.length<4)
		{
			System.out.println("Usage: java Job_RAS_Analysis [jobFieldSchemaFile] [Job_RAS_connect_log_file] [jobLogFile] [outputDir]");
			System.out.println("Example: java Job_RAS_Analysis /home/fti/Catalog-project/miralog/RAS-Job/Job/basicSchema/analysisSchema.txt /home/fti/Catalog-project/miralog/RAS-Job/Job-RAS.txt /home/fti/Catalog-project/miralog/RAS-Job/Job/scrubbed-201410-data.csv /home/fti/Catalog-project/miralog/RAS-Job/Job-RAS-analysis");
			System.exit(0);			
		}
		String jobFieldSchemaFile = args[0];
		String JobRASConnectLogFile = args[1];
		String jobLog = args[2];
		String outputDir = args[3];
		
		System.out.println("Loading job field schema ....");
		jobFieldList = RAS_Job_Analysis.loadJobSchema(jobFieldSchemaFile);
		
		System.out.println("Processing the analysis ....");
		HashMap<String, MsgJobMappingItem> msgJobMap = new HashMap<String, MsgJobMappingItem>();
		List<MsgJobMappingItem> msgJobList = new ArrayList<MsgJobMappingItem>();
		
		List<String> jobIDEventRecordIDList = new ArrayList<String>();
		HashMap<String, String> jobIDMsgCorMap = new HashMap<String, String>();
		HashMap<String, String> jobIDJobRecordMap = new HashMap<String, String>();
		MsgJobMappingItem mappingItem = null;
		
		List<String> lineList = PVFile.readFile(JobRASConnectLogFile);
		String jobID = null;
		float jobTimeStamp = 0;
		
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.contains("::")&&!line.contains("#"))
			{
				String[] s = line.split("::");
				if(s.length > 1)
				{
					String[] ss = s[0].split(",");
					jobID = ss[0];
					jobTimeStamp = Float.parseFloat(ss[1]);
				}
			}
			else //RAS record
			{
				String[] ss = line.split("#");
				//String recID = ss[0];
				String rasRecord = ss[1];
				String[] s = RecordSplitter.partition(rasRecord);
				String msgID = s[1];
				float rasEventTime = RecordElement.computeFloatTimeinSeconds(s[5]);
				float delay = rasEventTime - jobTimeStamp;
				StringBuilder sb = new StringBuilder();
				
				sb.append(jobID).append(" ").append(jobTimeStamp).append(" ").append(msgID).append(" ").append(delay);
				
				jobIDEventRecordIDList.add(sb.toString());
				jobIDMsgCorMap.put(jobID, sb.toString());
			}
		}
		
		PVFile.print2File(jobIDEventRecordIDList, outputDir+"/jobIDEventReocrdID.txt");
		
		//Reading job cobalt log
		List<String> jobLogLineList = PVFile.readFile(jobLog);
		iter = jobLogLineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] s = RecordSplitter.partition(line);
			jobID = s[22];
//			if(jobID.equals("341031"))
//				System.out.println();
			String rasMsgInfo = jobIDMsgCorMap.get(jobID);
			
			if(rasMsgInfo!=null)
			{
				jobIDJobRecordMap.put(jobID, line);
			}
		}

		//Traverse job cor list
		Iterator<String> iter3 = jobIDEventRecordIDList.iterator();
		while(iter3.hasNext())
		{
			String line = iter3.next();
			String[] data = line.split("\\s");
			jobID = data[0];
			String msgID = data[2];
			float delay = Float.parseFloat(data[3]);
			
			mappingItem = msgJobMap.get(msgID);
			if(mappingItem==null)
			{
				mappingItem = new MsgJobMappingItem(msgID);
				Iterator<JobField> iter2 = jobFieldList.iterator();
				while(iter2.hasNext())
				{
					JobField jf = iter2.next().clone();
					mappingItem.map.put(jf.getIndex(), jf);
				}
				msgJobMap.put(msgID, mappingItem);
				msgJobList.add(mappingItem);
			}
			

			String jobRecord = jobIDJobRecordMap.get(jobID);
			
			Iterator<JobField> iter2 = jobFieldList.iterator();
			while(iter2.hasNext())
			{
				JobField jField = iter2.next();
				int index = jField.getIndex();
				//String fieldName = jobField.getFieldName();
				
				JobField jobField = mappingItem.map.get(index);
				
				String[] s = RecordSplitter.partition(jobRecord);
			    String value = s[index];
			    ValueCountItem vcItem = jobField.valueCountMap.get(value);
			    if(vcItem==null)
			    {
			    	vcItem = new ValueCountItem(value, 0);
			    	jobField.valueCountMap.put(value, vcItem);
			    	jobField.valueCountList.add(vcItem);
			    }
			    vcItem.setCount(vcItem.getCount()+1);
			}
			
			mappingItem.updateDelayInfo(delay);
		}
		
		//print results
		Iterator<MsgJobMappingItem> iter2 = msgJobList.iterator();
		while(iter2.hasNext())
		{
			MsgJobMappingItem item = iter2.next();
			List<String> resultList = item.toStringList(jobFieldList);
			String msgIDFile = outputDir+"/"+item.getMsgID()+".cor";
			PVFile.print2File(resultList, msgIDFile);
		}
		
		System.out.println("outputDir="+outputDir);
		System.out.println("Done.");
	}
}
