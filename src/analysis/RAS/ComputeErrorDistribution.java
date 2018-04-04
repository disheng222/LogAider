package analysis.RAS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import element.LocDistributionElement;
import element.MaintainancePeriod;
import filter.RecordElement;
import filter.TemporalSpatialFilter;
import util.PVFile;
import util.RecordSplitter;

//Generate location distribution (to be plotted on graph)
public class ComputeErrorDistribution {

	static String initTimeStamp = "2015-01-01 00:00:00.000000";
	static long seconds = 86400; //checking interval
	static HashMap<String, LocDistributionElement>[] locMap = new HashMap[4];
	
	static List<FilterElement> filterList = new ArrayList<FilterElement>();
	boolean isAND = true; //the relation among the keys is AND (or OR)
	
	static List<String> dailyErrCountList = new ArrayList<String>();
	
	static String maintenanceFile = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/schema/maintainance-period.txt";
	
	public static void main(String[] args)
	{
		if(args.length<9 || args.length%2==0)
		{
			System.out.println("Usage: java ComputeErrorDistribution [[filterFieldIndex] [filterValue] ....] [logDir] [logExtension] [locationIndex] [separator] [outputDir] [merge/separate] [isAND (or OR)]");
			System.out.println("Example: java ComputeErrorDistribution 4 FATAL 13 END_JOB /home/fti/Catalog-project/miralog csvtmp 8 - /home/fti/Catalog-project/miralog/errLocDistribution merge true");
			System.out.println("Example: java ComputeErrorDistribution 4 FATAL 1 00062001 /home/fti/Catalog-project/miralog csvtmp 8 - /home/fti/Catalog-project/miralog/errLocDistribution/FATAL_MSGID_00062001 merge false");
			System.exit(0);
		}
		
		int filterPairLength = args.length-7;
		for(int i = 0;i<filterPairLength;i+=2)
		{
			int filterIndex = Integer.parseInt(args[i]);
			String filterValue = args[i+1];
			filterList.add(new FilterElement(filterIndex, filterValue));
		}

		//String locFullSchema = args[2];
		//int maxLevel = Integer.parseInt(args[3]);
		String logDir = args[filterPairLength];
		String extension = args[filterPairLength+1];
		int locationIndex = Integer.parseInt(args[filterPairLength+2]);
		String separator = args[filterPairLength+3];
		String outputDir = args[filterPairLength+4];
		String outputType = args[filterPairLength+5]; //merge or separate
		boolean isAND = Boolean.parseBoolean(args[filterPairLength+6]);
		
		System.out.println("Loading maintenance periods....");
		TemporalSpatialFilter.maintList = TemporalSpatialFilter.loadMaintenancePeriods(maintenanceFile);
		
		System.out.println("Loading location schema.....");
		for(int i = 0;i<4;i++)
			locMap[i] = new HashMap<String, LocDistributionElement>();
//		locMap = loadLocFullSchama(locFullSchema, maxLevel, separator);
		
		if(outputType.equals("merge"))
		{
			List<String> fileList = PVFile.getFiles(logDir, extension);
			Iterator<String> iter = fileList.iterator();
			while(iter.hasNext())
			{
				String fileName = iter.next();
				String filePath = logDir+"/"+fileName;
				System.out.println("Reading log file: "+filePath);
				List<String> lineList = PVFile.readFile(filePath);
				//TODO
				if(isAND)
					processOneFile_Merge_AND(locMap, lineList, locationIndex, separator);
				else
					processOneFile_Merge_OR(locMap, lineList, locationIndex, separator);
			}			
			System.out.println("Printing results.....");
			printResults(locMap, outputDir);
		}
		else if(outputType.equals("separate"))//separate
		{
			List<String> fileList = PVFile.getFiles(logDir, extension);
			Collections.sort(fileList);
			Iterator<String> iter = fileList.iterator();
			while(iter.hasNext())
			{
				String fileName = iter.next();
				String filePath = logDir+"/"+fileName;
				System.out.println("Reading log file: "+filePath);
				List<String> lineList = PVFile.readFile(filePath);
				//TODO
				HashMap<String, LocDistributionElement>[] locMap = null;
				if(isAND)
					locMap = processOneFile_Separate_AND(lineList, locationIndex, separator);
				else
					locMap = processOneFile_Separate_OR(lineList, locationIndex, separator);
				String outputDir2 = outputDir+"/"+fileName.split("\\.")[0];
				printResults(locMap, outputDir2);
			}	
		}
		else //separate2
		{
			float lastTimeStamp = (float)RecordElement.computeDoubleTimeinSeconds(initTimeStamp);
			
			List<String> fileList = PVFile.getFiles(logDir, extension);
			Collections.sort(fileList);
			Iterator<String> iter = fileList.iterator();
			
			int startTimeStamp = 0;
			
			List<String> bufferLineList = new ArrayList<String>();
			while(iter.hasNext())
			{
				String fileName = iter.next();
				String filePath = logDir+"/"+fileName;
				System.out.println("Reading log file: "+filePath);
				//startTimeStamp = 0;
				List<String> lineList = PVFile.readFile(filePath);
				
				boolean addLineOK = true;
				String lastLine = null;
				
				Iterator<String> iter2 = lineList.iterator();
				while(iter2.hasNext())
				{
					String line = null;
					if(addLineOK)
						line = iter2.next();
					else
						line = lastLine;
					if(line.startsWith("#"))
						continue;
					String[] s = RecordSplitter.partition(line);
					String timeString = s[5];
					float eventTime = (float)RecordElement.computeDoubleTimeinSeconds(timeString);
					if(eventTime<lastTimeStamp+seconds)
					{
						bufferLineList.add(line);
						addLineOK = true;
					}
					else
					{
						addLineOK = false;
						lastLine = line;
						HashMap<String, LocDistributionElement>[] locMap = null;
						if(isAND)
							locMap = processOneFile_Separate_AND(bufferLineList, locationIndex, separator);
						else
							locMap = processOneFile_Separate_OR(bufferLineList, locationIndex, separator);
						int dayNumber = (int)(startTimeStamp/seconds+1);
						String outputDir2 = outputDir+"/daily/"+fileName.split("\\.")[0]+"_"+PVFile.df2.format(dayNumber);
						printResults(locMap, outputDir2);
						lastTimeStamp += seconds;
						startTimeStamp += seconds;
						bufferLineList.clear();
						
						int totalCount = 0;
						Iterator iter3 = locMap[0].entrySet().iterator();
						while (iter3.hasNext()) 
						{
							Map.Entry entry = (Map.Entry) iter3.next();
							LocDistributionElement lde = (LocDistributionElement)entry.getValue();
							totalCount += lde.getCount();
						}
						dailyErrCountList.add(dayNumber+" "+totalCount);
						System.out.println("finish day "+(startTimeStamp/seconds));
					}
				}
			}	
			
			PVFile.print2File(dailyErrCountList, outputDir+"/daily/dailyCount.txt");
		}
		
		System.out.println("done.");
	}
	
	private static void processOneFile_Merge_OR(HashMap<String, LocDistributionElement>[] locMap, 
			List<String> lineList, int locationIndex, String separator)
	{
		Iterator<String> iter2 = lineList.iterator();
		while(iter2.hasNext())
		{
			String line = iter2.next();
			if(line.startsWith("#"))
				continue;
			String[] s = RecordSplitter.partition(line);
			
			boolean match = false;
			Iterator<FilterElement> iter3 = filterList.iterator();
			while(iter3.hasNext())
			{
				FilterElement fe = iter3.next();
				int index = fe.getFilterIndex();
				//String curValue = removeQuotationMarks(s[index]).trim();
				String curValue = s[index];
				String tgtValue = fe.getFilterValue();
				if(curValue.contains(tgtValue))
				{
					match = true;
					break;
				}
			}
			if(match)
			{
				//String location = removeQuotationMarks(s[locationIndex]).trim();
				String location = s[locationIndex];
				increment(locMap, location, separator);
//				String desc = s[13];
//				String[] ss = desc.split("\\s+");
//				int count = Integer.parseInt(ss[5].split("=")[1]);
//				increment(locMap, location, separator, count);
			}
		}
	}
	
	private static void processOneFile_Merge_AND(HashMap<String, LocDistributionElement>[] locMap, 
			List<String> lineList, int locationIndex, String separator)
	{
		Iterator<String> iter2 = lineList.iterator();
		while(iter2.hasNext())
		{
			String line = iter2.next();
			if(line.startsWith("#"))
				continue;
			String[] s = RecordSplitter.partition(line);

			//Filter the messages in the maintenance periods.
			double eventTime = RecordElement.computeDoubleTimeinSeconds(s[5]); //time stamp
			boolean isMaint = TemporalSpatialFilter.isMaintenanceMsg(eventTime);
			if(isMaint)
				continue;
			
			boolean allMatch = true;
			Iterator<FilterElement> iter3 = filterList.iterator();
			while(iter3.hasNext())
			{
				FilterElement fe = iter3.next();
				int index = fe.getFilterIndex();
				//String curValue = removeQuotationMarks(s[index]).trim();
				String curValue = s[index];
				String tgtValue = fe.getFilterValue();
				if(!curValue.contains(tgtValue))
				{
					allMatch = false;
					break;
				}
				
			}
			if(allMatch)
			{
				//String location = removeQuotationMarks(s[locationIndex]).trim();
				String location = s[locationIndex];
				increment(locMap, location, separator);
			}
		}
	}
	
	private static HashMap<String, LocDistributionElement>[] processOneFile_Separate_OR( 
			List<String> lineList, int locationIndex, String separator)
	{
		HashMap<String, LocDistributionElement>[] locMap = new HashMap[4];
		for(int i = 0;i<4;i++)
			locMap[i] = new HashMap<String, LocDistributionElement>();
		
		Iterator<String> iter2 = lineList.iterator();
		while(iter2.hasNext())
		{
			String line = iter2.next();
			String[] s = RecordSplitter.partition(line);
			
			boolean match = false;
			Iterator<FilterElement> iter3 = filterList.iterator();
			while(iter3.hasNext())
			{
				FilterElement fe = iter3.next();
				int index = fe.getFilterIndex();
				//String curValue = removeQuotationMarks(s[index]).trim();
				String curValue = s[index];
				String tgtValue = fe.getFilterValue();
				if(curValue.contains(tgtValue))
				{
					match = true;
					break;
				}
			}
			if(match)
			{
				//String location = removeQuotationMarks(s[locationIndex]).trim();
				String location = s[locationIndex];
				//increment(locMap, location, separator);
				String desc = s[13];
				String[] ss = desc.split("\\s+");
				int count = Integer.parseInt(ss[5].split("=")[1]);
				increment(locMap, location, separator, count);
			}
		}
		return locMap;
	}
	
	private static HashMap<String, LocDistributionElement>[] processOneFile_Separate_AND( 
			List<String> lineList, int locationIndex, String separator)
	{
		HashMap<String, LocDistributionElement>[] locMap = new HashMap[4];
		for(int i = 0;i<4;i++)
			locMap[i] = new HashMap<String, LocDistributionElement>();
		
		Iterator<String> iter2 = lineList.iterator();
		while(iter2.hasNext())
		{
			String line = iter2.next();
			String[] s = RecordSplitter.partition(line);
			
			double eventTime = RecordElement.computeDoubleTimeinSeconds(s[5]); //time stamp
//			boolean isMaint = TemporalSpatialFilter.isMaintenanceMsg(eventTime);
//			if(isMaint)
//				continue;
			
			boolean allMatch = true;
			Iterator<FilterElement> iter3 = filterList.iterator();
			while(iter3.hasNext())
			{
				FilterElement fe = iter3.next();
				int index = fe.getFilterIndex();
				//String curValue = removeQuotationMarks(s[index]).trim();
				String curValue = s[index];
				String tgtValue = fe.getFilterValue();
				if(!curValue.contains(tgtValue))
				{
					allMatch = false;
					break;
				}
			}
			if(allMatch)
			{
				//String location = removeQuotationMarks(s[locationIndex]).trim();
				String location = s[locationIndex];
				increment(locMap, location, separator);
			}
		}
		return locMap;
	}
	
	private static String removeQuotationMarks(String s)
	{
		if(s.startsWith("\"") && s.endsWith("\""))
			return s.substring(1, s.length()-1);
		else 
			return s;
	}
	
	public static void printResults(HashMap<String, LocDistributionElement>[] locMap, 
			String outputDir)
	{
		for(int i = 0;i<locMap.length;i++)
		{
			List<LocDistributionElement> resultList = new ArrayList<LocDistributionElement>();
			Iterator iter = locMap[i].entrySet().iterator(); 
			while (iter.hasNext()) { 
			    Map.Entry entry = (Map.Entry) iter.next(); 
			    String key = (String)entry.getKey(); 
			    LocDistributionElement lde = (LocDistributionElement)entry.getValue(); 
			    resultList.add(lde);
			}
			System.out.println("Sorting the results ....");
			Collections.sort(resultList);
			String outputFile = outputDir+"/level"+(i+1)+".err";
			System.out.println("Writing results to "+outputFile+" .....");
			PVFile.print2File(resultList, outputFile);
		}
	}
	
	public static void increment(HashMap<String, LocDistributionElement>[] locMap, 
			String location, String separator, int count)
	{
		String[] ss = location.split(separator);
		
		StringBuilder sb = new StringBuilder();
		String key_;
		sb.append(ss[0]);
		for(int i = 0;i < ss.length;i++)
		{
			key_ = sb.toString();
			if(key_.equals(""))
				key_ = "NULL";
			LocDistributionElement lde = locMap[i].get(key_);
			if(lde==null)
			{
				lde = new LocDistributionElement(key_, 0);
				locMap[i].put(key_, lde);
			}
			lde.setCount(lde.getCount()+count);
			if(i+1<ss.length)
				sb.append("-").append(ss[i+1]);
		}
	}
	
	public static void increment(HashMap<String, LocDistributionElement>[] locMap, 
			String location, String separator)
	{
		String[] ss = location.split(separator);
		
		StringBuilder sb = new StringBuilder();
		String key_;
		sb.append(ss[0]);
		for(int i = 0;i < ss.length;i++)
		{
			key_ = sb.toString();
			if(key_.equals(""))
				key_ = "NULL";
			LocDistributionElement lde = locMap[i].get(key_);
			if(lde==null)
			{
				lde = new LocDistributionElement(key_, 0);
				locMap[i].put(key_, lde);
			}
			lde.setCount(lde.getCount()+1);
			if(i+1<ss.length)
				sb.append("-").append(ss[i+1]);
		}
	}
	
	public static HashMap<String, LocDistributionElement>[] loadLocFullSchama(String locFullSchemaPath, int maxLevel, String separator)
	{
		HashMap<String, LocDistributionElement>[] map = new HashMap[maxLevel];
		for(int i = 0;i<maxLevel;i++)
			map[i] = new HashMap<String, LocDistributionElement>();
		List<String> locList = PVFile.readFile(locFullSchemaPath);
		Iterator<String> iter = locList.iterator();
		while(iter.hasNext())
		{
			String s = iter.next();
			if(s.startsWith("#"))
				continue;
			String key = s.split(" ")[0];
			String[] ss = key.split(separator);
			StringBuilder sb = new StringBuilder();
			String key_;
			sb.append(ss[0]);
			for(int i = 0;i < ss.length;i++)
			{
				key_ = sb.toString();
				map[i].put(key_, new LocDistributionElement(key_, 0));
				if(i+1<ss.length)
					sb.append("-").append(ss[i+1]);
			}
		}
		return map;
	}
}

class FilterElement
{
	private int filterIndex;
	private String filterValue;
	
	public FilterElement(int filterIndex, String filterValue) {
		this.filterIndex = filterIndex;
		this.filterValue = filterValue;
	}
	public int getFilterIndex() {
		return filterIndex;
	}
	public void setFilterIndex(int filterIndex) {
		this.filterIndex = filterIndex;
	}
	public String getFilterValue() {
		return filterValue;
	}
	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}
	
	
}
