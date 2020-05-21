package analysis.inbetween2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import analysis.inbetween.element.RASRecord;
import filter.RecordElement;
import util.PVFile;
import util.RecordSplitter;

public class PrintFatalTaskID {

	public static HashMap<String, RASRecord> fatalTaskMap = new HashMap<String, RASRecord>();
	public static List<RASRecord> fatalTaskList = new ArrayList<RASRecord>();	
	
	static String fatalRASFile = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/ANL-ALCF-RE-MIRA_20130409_20180930-fatal.csv";
	public static void main(String[] args)
	{
		
		getFatalTaskList(fatalRASFile);
		
		String outputFile = fatalRASFile+".tasklist";
		PVFile.print2File(fatalTaskList, outputFile);
		System.out.println("output the events with nonzero-taskid to the file: "+outputFile);
	}
	
	public static void getFatalTaskList(String inputFile)
	{
		//load fatal events
		//System.out.println("Loading Fatal Event List from "+inputFile);
		List<String> fatalEventList = PVFile.readFile(inputFile);
		//System.out.println("total # fatal events: " + fatalEventList.size());
		
		Iterator<String> iter = fatalEventList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] s = RecordSplitter.partition(line);
			String taskIDs = s[6];
			if(!taskIDs.equals(""))
			{
				RASRecord record = fatalTaskMap.get(taskIDs);
				
				if(record==null)
				{
					String timeString = s[5];
					timeString = timeString.replace(";", " ");
					String recordID = s[0];
					String msgID = s[1];
					String catagory = s[2];
					String component = s[3];
					String blockCode = s[8];
					int taskID = Integer.parseInt(taskIDs);
					double time = RecordElement.computeDoubleTimeinSeconds(timeString);				
					record = new RASRecord(time, recordID, msgID, catagory, component, blockCode, taskID, line);	
					fatalTaskList.add(record);
				}
				else
				{
					record.setTaskIDCount(record.getTaskIDCount()+1);
				}
			}
		}		
		
	}
	
}
