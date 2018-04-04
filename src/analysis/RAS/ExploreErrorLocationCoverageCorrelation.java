package analysis.RAS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import analysis.inbetween.element.ItemCombination;
import analysis.inbetween.element.TemporalGroup;
import filter.RecordElement;
import util.PVFile;
import util.RecordSplitter;

public class ExploreErrorLocationCoverageCorrelation {

	static HashMap<String, ItemCombination> totalMsgIDMap = new HashMap<String, ItemCombination>();
	static HashMap<String, ItemCombination> totalCompMap = new HashMap<String, ItemCombination>();
	static HashMap<String, ItemCombination> totalCateMap = new HashMap<String, ItemCombination>();
	
	static float threshold = 20;
	
	public static void main(String[] args)
	{
		if(args.length < 3)
		{
			System.out.println("Usage: java ExploreErrorLocationCoverageCorrelation [logDir] [extension] [outputDir]");
			System.exit(0);
		}
		
		String logDir = args[0];
		String extension = args[1];
		String outputDir = args[2];
		
		List<TemporalGroup> tgList = new ArrayList<TemporalGroup>();
		TemporalGroup curTG = null;
		double lastEventTime = -1;
		List<String> fileList = PVFile.getFiles(logDir, extension);
		Collections.sort(fileList);
		double initLogTime = System.currentTimeMillis()/1000.0;
		Iterator<String> iter = fileList.iterator();
		
		TemporalGroup.totalInitTime = RecordElement.computeDoubleTimeinSeconds("2015-01-01 0:00:00.000000");
		
		while(iter.hasNext())
		{
			String fileName = iter.next();
			int i = 0;
			System.out.println("Processing "+fileName);
			String filePath = logDir+"/"+fileName;
			List<String> lineList = PVFile.readFile(filePath);
			Iterator<String> iter2 = lineList.iterator();
			while(iter2.hasNext())
			{
				String line = iter2.next();
				String[] s = RecordSplitter.partition(line);
				String timeString = s[5];
				double curEventTime = RecordElement.computeDoubleTimeinSeconds(timeString);

				if(lastEventTime<0)
				{
					lastEventTime = curEventTime;
					TemporalGroup tg = new TemporalGroup(lastEventTime, lastEventTime);
					tg.eventList.add(s);
					tgList.add(tg);
					curTG = tg;
				}
				else
				{
					if(Math.abs(curEventTime - lastEventTime) >= threshold)
					{
						//construct a new group
						TemporalGroup tg = new TemporalGroup(curEventTime, curEventTime);
						tg.eventList.add(s);
						tgList.add(tg);
						curTG = tg;
					}
					else
					{
						curTG.eventList.add(s);
						curTG.setLastTime(curEventTime);
					}
					lastEventTime = curEventTime;
				}
				i++;
				if(i%100==0)
					PVFile.showProgress(initLogTime, i, lineList.size(), fileName);
			}
		}
		System.out.println("Scan all events to construct totalMsgIDMap, totalCompMap, and totalCateMap....."	);
		Iterator<TemporalGroup> it = tgList.iterator();
		while(it.hasNext())
		{
			TemporalGroup tg = it.next();
			tg.mergeICList(totalMsgIDMap, totalCompMap, totalCateMap);
		}
		
		System.out.println("Converting Maps to Lists");
		
		List<ItemCombination> totalMsgIDCombList = convertMap2List(totalMsgIDMap);
		List<ItemCombination> totalCompCombList = convertMap2List(totalCompMap);
		List<ItemCombination> totalCateCombList = convertMap2List(totalCateMap);
		
		System.out.println("Sorting lists....");
		
		Collections.sort(totalMsgIDCombList);
		Collections.sort(totalCompCombList);
		Collections.sort(totalCateCombList);
		
		System.out.println("printing results to "+outputDir);
		
		PVFile.print2File(tgList, outputDir+"/tgList.txt");
		PVFile.print2File(totalMsgIDCombList, outputDir+"/totalMsgIDCombList.txt");
		PVFile.print2File(totalCompCombList, outputDir+"/totalCompCombList.txt");
		PVFile.print2File(totalCateCombList, outputDir+"/totalCateCombList.txt");
		
		System.out.println("done.");
	}
	
	public static List<ItemCombination> convertMap2List(Map<String, ItemCombination> map)
	{
		List<ItemCombination> resultList = new ArrayList<ItemCombination>();
		Iterator entries = map.entrySet().iterator();  
		  
		while (entries.hasNext()) {  
		  
		    Map.Entry entry = (Map.Entry) entries.next();  
		    //String key = (String)entry.getKey();  
		    ItemCombination value = (ItemCombination)entry.getValue();  
		    resultList.add(value);
		}  
		
		return resultList;
	}
}
