package analysis.Job;

/**
 * This class is for generating/plotting the location distribution in the MIRA graph
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import element.MidPlaneLocationElement;
import element.Counter;
import tools.ComputeLocationBasedOnMIRCode;
import util.NumericChecker;
import util.PVFile;

public class ComputeJobMessageCounts {

	static String jobLog = "/home/fti/Catalog-project/miralog/RAS-Job/Job/scrubbed-201410-data.csv";
	static String outputDir = "/home/fti/Catalog-project/miralog/RAS-Job/Job/locDistribution/err";
	static boolean onlyCheckErr = true;
	
	public static void main(String[] args)
	{
		
		if(args.length<5)
		{
			System.out.println("Usage: java ComputeJobMessageCounts [onlyCheckErr?] [jobLog] [exitCodeIndex] [locationCodeIndexx] [outputDir]");
			System.out.println("Example: java ComputeJobMessageCounts true/false "+jobLog+" 14 23 "+outputDir);
			System.out.println("Example: java ComputeJobMessageCounts true /home/fti/Catalog-project/miralog/Adam-job-log/job-history-log-from-adam.txt 24 3 /home/fti/Catalog-project/miralog/Adam-job-log/err");
			System.out.println("Example: java ComputeJobMessageCounts false /home/fti/Catalog-project/miralog/Adam-job-log/ascovel_jobhistory.csv 24 3 /home/fti/Catalog-project/miralog/Adam-job-log/all");
			System.exit(0);
		}
		
		onlyCheckErr = Boolean.parseBoolean(args[0]);
		jobLog = args[1];
		int exitCodeIndex = Integer.parseInt(args[2]);
		int locCodeIndex = Integer.parseInt(args[3]);
		outputDir = args[4];
		
		System.out.println("joblog path = "+jobLog);
		System.out.println("reading job log....");
		List<String> lineList = PVFile.readFile(jobLog);
		System.out.println("parsing job log....");
		Iterator<String> iter = lineList.iterator();
		iter.next(); //filter out the field line
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] s = line.split(",");
			String exit_code = removeQuotationMarks(s[exitCodeIndex]);
			String locationCode = removeQuotationMarks(s[locCodeIndex]);
			
			boolean show = true;
			if(onlyCheckErr)
			{
				if(!NumericChecker.isNumeric(exit_code))
				{
					show = true;
				}
				else
				{
					int eCode = Integer.parseInt(exit_code);
					if(eCode!=0)
						show = true;
					else
						show = false;
				}				
			}

			if(show)
			{
				List<MidPlaneLocationElement> mList = ComputeLocationBasedOnMIRCode.computeLocations(locationCode);
				
				Iterator<MidPlaneLocationElement> iter2 = mList.iterator();
				while(iter2.hasNext())
				{
					MidPlaneLocationElement e = iter2.next();
					e.setEventCount(e.getEventCount()+1);
					e.increaseForNodeBoards();
				}	
			}
		}
		
		System.out.println("Constructing statistics....");
		HashMap<String, Counter> level1Map = new HashMap<String, Counter>();
		HashMap<String, Counter> level2Map = new HashMap<String, Counter>();
		HashMap<String, Counter> level3Map = new HashMap<String, Counter>();
		
		Iterator iter3 = ComputeLocationBasedOnMIRCode.midplaneMap.entrySet().iterator(); 
		while (iter3.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter3.next(); 
		    String key = (String)entry.getKey(); 
		    MidPlaneLocationElement element = (MidPlaneLocationElement)entry.getValue();
		    element.addCounter(level1Map, level2Map, level3Map);
		} 
		
		List<Counter> list1 = convertMap2List(level1Map);
		List<Counter> list2 = convertMap2List(level2Map);
		List<Counter> list3 = convertMap2List(level3Map);
		
		//Construct list3
		
		PVFile.print2File(list1, outputDir+"/level1.err");
		PVFile.print2File(list2, outputDir+"/level2.err");
		PVFile.print2File(list3, outputDir+"/level3.err");
		
		System.out.println("Output DIR = "+outputDir);
		System.out.println("Done.");
	}
	

	
	private static List<Counter> convertMap2List(Map<String, Counter> map)
	{
		List<Counter> list = new ArrayList<Counter>();
		Iterator it1 = map.entrySet().iterator();
		while(it1.hasNext())
		{
			Map.Entry entry = (Map.Entry) it1.next();
			Counter counter = (Counter)entry.getValue();
			list.add(counter);
		}
		Collections.sort(list);
		return list;
	}
	
	public static String removeQuotationMarks(String s)
	{
		if(s.startsWith("\"") && s.endsWith("\""))
		{
			return s.substring(0, s.length()-1).trim();
		}
		else
			return s.trim();
	}
}
