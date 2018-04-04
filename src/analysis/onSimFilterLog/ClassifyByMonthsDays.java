package analysis.onSimFilterLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class ClassifyByMonthsDays {
	
	static String inputDir = "/home/sdi/Work/Catalog-project/Catalog-data/Compare-5years-1years/5years/FilterAndClassify/240_300_900_fltr/ts_600_7200";
	static String inputExt = "cor";
	static String outputExt = "cnt";
	
	public static void main(String[] args)
	{
		if(args.length<3)
		{
			System.out.println("Usage: java ClassifyByMonths [startTime] [inputDir] [input_extension] [output_extension]");
			System.out.println("Example: java ClassifyByMonths (2013-04-01-00:00:00.000000) /home/sdi/Work/Catalog-project/Catalog-data/Compare-5years-1years/5years/FilterAndClassify/240_300_900_fltr/ts_600_7200"
					+ " cor cnt"); //crm means correlation files by months			
			System.out.println("Example: java ClassifyByMonths (2016-01-01-00.00.00.000000) /home/sdi/Work/Catalog-project/Catalog-data/Compare-5years-1years/5years/FilterAndClassify/240_300_900_fltr/ts_600_7200"
					+ " cor cnt"); //crm means correlation files by months
			System.exit(0);
		}
		inputDir = args[0];
		inputExt = args[1];
		outputExt = args[2];
		
		List<String> fileNameList = PVFile.getFiles(inputDir, inputExt);
		Iterator<String> iter = fileNameList.iterator();
		while(iter.hasNext())
		{
			List<CountElement> monthCountList = new ArrayList<CountElement>();
			HashMap<String, CountElement> monthCountMap = new HashMap<String, CountElement>();
			List<CountElement> dayCountList = new ArrayList<CountElement>();
			HashMap<String, CountElement> dayCountMap = new HashMap<String, CountElement>();			
			
			String fileName = iter.next();
			String filePath = inputDir+"/"+fileName;
			List<String> lineList = PVFile.readFile(filePath);
			Iterator<String> iter2 = lineList.iterator();
			while(iter2.hasNext())
			{
				String line = iter2.next();
				if(line.startsWith("#"))
					continue;
				if(line.startsWith("event"))
				{
					String[] s = line.split("\\s");
					String startTime = s[7].replaceAll(";", " ");
					String day = startTime.split("\\s")[0];
					CountElement dayCount = dayCountMap.get(day);
					if(dayCount == null)
					{
						dayCount = new CountElement(day, 0);
						dayCountMap.put(day, dayCount);
						dayCountList.add(dayCount);
					}
					dayCount.setCount(dayCount.getCount()+1);
					
					String[] ss = day.split("-");
					String month = ss[0]+"-"+ss[1];
					CountElement monthCount = monthCountMap.get(month);
					if(monthCount == null)
					{
						monthCount = new CountElement(month, 0);
						monthCountMap.put(month, monthCount);
						monthCountList.add(monthCount);
					}
					monthCount.setCount(monthCount.getCount()+1);			
				}
			}
			Collections.sort(dayCountList);
			Collections.sort(monthCountList);
			
			PVFile.print2File(monthCountList, filePath+".mcnt");
			PVFile.print2File(dayCountList, filePath+".dcnt");			
			System.out.println("Printing the results to "+filePath+".cnt");
		}
		
		System.out.println("Done.");
	}

	
}
