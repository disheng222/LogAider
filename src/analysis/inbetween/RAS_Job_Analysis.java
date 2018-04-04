/**
 * @author Sheng Di
 * @class RAS_Job_Analysis.java
 * @description  
 */

package analysis.inbetween;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import analysis.inbetween.element.JobField;
import analysis.inbetween.element.MsgJobMappingItem;
import analysis.inbetween.element.ValueCountItem;
import filter.RecordElement;
import util.PVFile;
import util.RecordSplitter;

public class RAS_Job_Analysis {

	public static List<JobField> jobFieldList = null; 

	public static void main(String[] args)
	{
		if(args.length<3)
		{
			System.out.println("Usage: java RAS_Job_Analysis [jobFieldSchemaFile] [RAS_job_connect_log_file] [outputDir]");
			System.out.println("Example: java RAS_Job_Analysis /home/fti/Catalog-project/miralog/RAS-Job/Job/basicSchema/analysisSchema.txt /home/fti/Catalog-project/miralog/RAS-Job/RAS-Job.txt /home/fti/Catalog-project/miralog/RAS-Job/RAS-Job-analysis");
			System.exit(0);			
		}
		String jobFieldSchemaFile = args[0];
		String RASJobConnectLogFile = args[1];
		String outputDir = args[2];
		
		System.out.println("Loading job field schema ....");
		jobFieldList = loadJobSchema(jobFieldSchemaFile);
		
		System.out.println("Processing the analysis ....");
		HashMap<String, MsgJobMappingItem> msgJobMap = new HashMap<String, MsgJobMappingItem>();
		List<MsgJobMappingItem> msgJobList = new ArrayList<MsgJobMappingItem>();
		
		MsgJobMappingItem mappingItem = null;
		
		List<String> lineList = PVFile.readFile(RASJobConnectLogFile);
		boolean validRecord = false;
		float rasTimeStamp = 0;
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.contains("::")&&!line.contains("#"))
			{
				String[] s = line.split("::");
				if(s.length > 1)
				{
					validRecord = true;
					//get the msg id
					String[] ss = s[0].split(",");
					String msgID = ss[0].split("-")[1];
					rasTimeStamp = Float.parseFloat(ss[1]);
					//String[] jobRecords = s[1].split("\\s");
					//Construct the job records
					
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
				}
				else
				{
					validRecord = false;
					continue;
				}
			}
			else //job records
			{
				String recordString = line.split("#")[1];
				String[] data = RecordSplitter.partition(recordString);
				Iterator iter2 = mappingItem.map.entrySet().iterator(); 
				while (iter2.hasNext()) { 
				    Map.Entry entry = (Map.Entry) iter2.next(); 
				    int index = (Integer)entry.getKey(); 
				    JobField jobField = (JobField)entry.getValue(); 
				    String value = data[index];
				    ValueCountItem vcItem = jobField.valueCountMap.get(value);
				    if(vcItem==null)
				    {
				    	vcItem = new ValueCountItem(value, 0);
				    	jobField.valueCountMap.put(value, vcItem);
				    	jobField.valueCountList.add(vcItem);
				    }
				    vcItem.setCount(vcItem.getCount()+1);
				}
				//get the delay
				String timeString = data[2]; //for old log, it's [43]
				float jobFinishTime = RecordElement.computeFloatTimeinSeconds(timeString);
				float delay = jobFinishTime - rasTimeStamp;
				mappingItem.updateDelayInfo(delay);
			}
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
	
	public static List<JobField> loadJobSchema(String jobFieldSchemaFile)
	{
		List<JobField> resultList = new ArrayList<JobField>();
		List<String> jobFieldList = PVFile.readFile(jobFieldSchemaFile);
		Iterator<String> iter = jobFieldList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] s = line.split("\\s");
			String keyName = s[0];
			int index = Integer.parseInt(s[1]);
			resultList.add(new JobField(index, keyName));
		}
		return resultList;
	}
}

