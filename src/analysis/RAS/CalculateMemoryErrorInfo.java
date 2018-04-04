package analysis.RAS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import filter.RecordElement;
import util.PVFile;
import util.RecordSplitter;

public class CalculateMemoryErrorInfo {

	static String logDir;
	static String extension;
	static String outputDir;
	static int interval;
	static int referencePeriod;
	
	public static void main(String[] args)
	{
		if(args.length<5)
		{
			System.out.println("java CalculateMemoryErrorInfo [logDir] [extension] [interval] [referencePeriod] [outputDir]");
			System.out.println("Example: java CalculateMemoryErrorInfo .../ALCF-Data/RAS csv 10 86400 .../ALCF-Data/RAS/meminfo");
			System.exit(0);
		}
		
		logDir = args[0];
		extension = args[1];
		interval = Integer.parseInt(args[2]);
		referencePeriod = Integer.parseInt(args[3]);
		outputDir = args[4];
		
		
		List<MemoryInfoElement> errList = new ArrayList<MemoryInfoElement>();
		HashMap<String, MemoryInfoElement> errMap = new HashMap<String, MemoryInfoElement>();
		long totalCCount = 0;//totalCorrectableCount = 0;
		long recordCount = 0;
		
		List<String> fileList = PVFile.getFiles(logDir, extension);
		Collections.sort(fileList);
		
		List<String> monthlyResultList = new ArrayList<String>();
		
		String firstTimeStampTmp = "2015-MONTH-01 00:00:00.000000";
		Iterator<String> iter = fileList.iterator();
		for(int i = 1;iter.hasNext();i++)
		{
			int totalMonthlyCount = 0;
			String month = PVFile.df2.format(i);
			String firstTimeStampString = firstTimeStampTmp.replace("MONTH", month);
			float firstTimeStamp = RecordElement.computeFloatTimeinSeconds(firstTimeStampString);
					
			String fileName = iter.next();
			String filePath = logDir+"/"+fileName;

			System.out.println("Reading "+filePath);
			List<String> lineList = PVFile.readFile(filePath);
			System.out.println("Processing data.... : # lines = "+lineList.size());

			int totalPeriod = 32; //days
			int[] countArray = new int[totalPeriod*86400/interval];
			
			Iterator<String> iter2 = lineList.iterator();
			while(iter2.hasNext())
			{
				String line = iter2.next();
				String[] s = RecordSplitter.partition(line);
				String msgID = s[1];
				if(msgID.equals("00080033")||msgID.equals("00080034"))
				{
					float rasEventTime = RecordElement.computeFloatTimeinSeconds(s[5]);
					String desc = s[13];
					String[] ss = desc.split("\\s+");
					int count = Integer.parseInt(ss[5].split("=")[1]);
					
					float timeStamp = rasEventTime - firstTimeStamp;
					int index = (int)(timeStamp/interval);
					countArray[index] += timeStamp;
					
					String key = "NULL";
					if(ss.length >= 9)
						key = ss[9];
					
//					if(!key.startsWith("["))
//					{
//						System.out.println("Error: key does not start with \"[\", i.e., it doesn't look like [ECC_ERROR_COUNTER_THRESHOLD_REACHED].");
//						System.exit(0);
//					}
					totalMonthlyCount += count;
					totalCCount += count;
					recordCount++;
					MemoryInfoElement mie = errMap.get(key);
					if(mie==null)
					{
						mie = new MemoryInfoElement(key);
						errMap.put(key, mie);
						errList.add(mie);
					}
					mie.setCount(mie.getCount()+count);
				}
			}
			
			String outputFilePath = outputDir+"/tmpAnalysis/month-"+i+"-"+interval+".txt";
			PVFile.print2File(countArray, interval, referencePeriod, outputFilePath);
			System.out.println("outputPath = "+outputFilePath);
			
			monthlyResultList.add(i+" "+totalMonthlyCount);
		}
		
		String monthlyResultOutputFile = outputDir+"/tmpAnalysis/monthlyCount.txt";
		PVFile.print2File(monthlyResultList, monthlyResultOutputFile);
		
		System.out.println("total record count = "+recordCount);
		System.out.println("total number of errs = "+totalCCount);
		
		String errStatusOutputFile = outputDir+"/errStatus.txt";
		Collections.sort(errList);

		MemoryInfoElement metaInfo = new MemoryInfoElement("# recordCount="+recordCount+" totalCCount="+totalCCount);
		metaInfo.setCount(-1);

		PVFile.print2File(errList, errStatusOutputFile);
		System.out.println("output dir = "+outputDir);
		System.out.println("done.");
	}
	
}
