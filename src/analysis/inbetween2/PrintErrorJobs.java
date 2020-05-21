package analysis.inbetween2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import util.PVFile;
import util.RecordSplitter;

public class PrintErrorJobs {

	static String cobaltJobLogDir = "/home/sdi/windows-xp/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job";
	static String cobaltJobFiles_str = "ANL-ALCF-DJC-MIRA_20130409_20131231.csv ANL-ALCF-DJC-MIRA_20140101_20141231.csv ANL-ALCF-DJC-MIRA_20150101_20151231.csv ANL-ALCF-DJC-MIRA_20160101_20161231.csv ANL-ALCF-DJC-MIRA_20170101_20171231.csv ANL-ALCF-DJC-MIRA_20180101_20180930.csv";
	
	public static List<String> cobaltJobList = new ArrayList<String>();
	public static HashMap<String, String> cobaltJobIDMap = new HashMap<String, String>();
	
	public static void main(String[] args)
	{
		List<String> resultList = new ArrayList<String>();
		String[] cobaltJobFiles = cobaltJobFiles_str.split("\\s");
		for(int i = 0;i<cobaltJobFiles.length;i++)
		{
			String fileName = cobaltJobFiles[i];
			String jobFilePath = cobaltJobLogDir+"/"+fileName;
			System.out.println("Loading cobalt job list from "+jobFilePath);
			
			List<String> lineList = PVFile.readFile(jobFilePath);
			
			Iterator<String> iter = lineList.iterator();
			iter.next();
			while(iter.hasNext())
			{
				String line = iter.next();
				String[] s = RecordSplitter.partition(line);
				int exitStatus = Integer.parseInt(s[19]);
				if(exitStatus!=0)
				{
					resultList.add(line);
				}
				
			}
		}
		
		PVFile.print2File(resultList, cobaltJobLogDir+"/ANL-ALCF-DJC-MIRA_20130409_20180930-errorjob.csv");
	}
	
	public static HashMap<String, String> loadCobaltJobIDMap(String cobaltJobLogFile)
	{
		HashMap<String, String> cobaltJobIDMap = new HashMap<String, String>();
		List<String> lineList = PVFile.readFile(cobaltJobLogFile);
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] s = RecordSplitter.partition(line);
			String cobaltJobID = s[1];
			cobaltJobIDMap.put(cobaltJobID, line);
		}
		return cobaltJobIDMap;
	}
}
