package analysis.RAS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import util.PVFile;
import element.DailyElement;
import filter.RecordElement;

public class ComputeDailyFilteredCount 
{
	
	static String filterLogDir = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/FilterAndClassify/no-Maint-no-DIAGS-filter-interval=240s/ts";
	static String extension;
	
	public static HashMap<String, DailyElement> dailyMap = new HashMap<String, DailyElement>();
	public static List<DailyElement> dailyList = new ArrayList<DailyElement>();
	public static void main(String[] args)
	{
		if(args.length<2)
		{
			System.out.println("java ComputeDailyFilteredCount [filterLogDir] [extension]");
			System.out.println("Example: java ComputeDailyFilteredCount "+filterLogDir+" fltr");
			System.exit(0);
		}
		
		filterLogDir = args[0];
		extension = args[1];
		
		float initTime = RecordElement.computeFloatTimeinSeconds("2015-01-01 00:00:00.000000");
		
		List<String> fileList = PVFile.getFiles(filterLogDir, extension);
		Iterator<String> iter = fileList.iterator();
		while(iter.hasNext())
		{
			String fileName = iter.next();
			System.out.println("Processing "+fileName);
			String filePath = filterLogDir+"/"+fileName;
			List<String> lineList = PVFile.readFile(filePath);
			Iterator<String> iter2 = lineList.iterator();
			while(iter2.hasNext())
			{
				String line = iter2.next();
				String[] s = line.split("\\s");
				String eventTime = s[5].replace(";", " ");
				float time = RecordElement.computeFloatTimeinSeconds(eventTime);
				int seconds = (int)(time - initTime);
				int day = seconds/86400+1;
				DailyElement de = dailyMap.get(String.valueOf(day));
				if(de == null)
				{
					de = new DailyElement(day, 0);
					dailyMap.put(String.valueOf(day), de);
					dailyList.add(de);
				}
				de.setCount(de.getCount()+1);
			}
		}
		Collections.sort(dailyList);
		
		int[] count = new int[366];
		Iterator<DailyElement> itt = dailyList.iterator();
		while(itt.hasNext())
		{
			DailyElement de = itt.next();
			int i = de.getDay();
			int v = de.getCount();
			count[i] = v;
		}
		
		String outputFile = filterLogDir+"/dailyCount-filter.txt";
		System.out.println("writing results to "+outputFile);
		PVFile.print2File(count, outputFile);
	}
}
