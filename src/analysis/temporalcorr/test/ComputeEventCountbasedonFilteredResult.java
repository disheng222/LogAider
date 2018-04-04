package analysis.temporalcorr.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import element.DailyElement;
import filter.RecordElement;
import util.PVFile;

public class ComputeEventCountbasedonFilteredResult {

	static String eventLogDir = "/home/sdi/windows-xp/my-projects/Catalog-project/miralog/one-year-data/"
			+ "ALCF-Data/RAS/FilterAndClassify/no-MaintResv-no-DIAGS-filter-interval=240s/ts/cor-240-43200";
	static String eventFilePath = eventLogDir+"/allEvents.txt-0.8.cor";
	public static List<DailyElement> dailyList = new ArrayList<DailyElement>();
	public static HashMap<String, DailyElement> dailyMap = new HashMap<String, DailyElement>();
	
	public static void main(String[] args)
	{
		float initTime = RecordElement.computeFloatTimeinSeconds("2015-01-01 00:00:00.000000");
		
		List<String> lineList = PVFile.readFile(eventFilePath);
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.startsWith("#"))
				continue;
			if(line.startsWith("event"))
			{
				String[] s = line.split("\\s");
				String occrTime = s[7].replace(";", " ");
				float time = RecordElement.computeFloatTimeinSeconds(occrTime);
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
		
		String[] ss = eventFilePath.split("/");
		String eventFileName = ss[ss.length-1];
		String outputFile = eventLogDir+"/"+eventFileName+".daily";
		System.out.println("writing results to "+outputFile);
		PVFile.print2File(count, outputFile);
		
		System.out.println("done");
	}
}
