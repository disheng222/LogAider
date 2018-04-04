package analysis.RAS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import element.RASTmporalPairElement;
import filter.RecordElement;
import analysis.inbetween.element.RASRecord;
import util.PVFile;

public class ComputeTmporalErrPropagation {

	static List<RASTmporalPairElement> msgIDList = new ArrayList<RASTmporalPairElement>();
	static HashMap<String, RASTmporalPairElement> msgIDMap = new HashMap<String, RASTmporalPairElement>();
	static List<RASTmporalPairElement> compoList = new ArrayList<RASTmporalPairElement>();
	static HashMap<String, RASTmporalPairElement> compoMap = new HashMap<String, RASTmporalPairElement>();
	static List<RASTmporalPairElement> categList = new ArrayList<RASTmporalPairElement>();
	static HashMap<String, RASTmporalPairElement> categMap = new HashMap<String, RASTmporalPairElement>();
	static int totalCount = 0;
	
	public static void main(String[] args)
	{
		if(args.length<3)
		{
			System.out.println("java ComputeTmporalErrPropagation [rootDir] [extension] [minutes]");
			System.out.println("Usage: java ComputeTmporalErrPropagation /home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/FilterAndClassify/no-MaintResv-no-DIAGS-filter-interval=240s/ts fltr 60");
			System.exit(0);
		}
		
		String rootDir = args[0];
		String extension = args[1];
		int minutes = Integer.parseInt(args[2]);
		String outputDir = rootDir+"/rasErrPropagation";
		
		List<RASRecord> rlist = new ArrayList<RASRecord>();
		List<String> fileList = PVFile.getFiles(rootDir, extension);
		Iterator<String> iter = fileList.iterator();
		while(iter.hasNext())
		{
			String fileName = iter.next();
			String msgID = fileName.split("\\.")[0];
			String filePath = rootDir+"/"+fileName;
			List<String> lineList = PVFile.readFile(filePath);
			Iterator<String> iter2 = lineList.iterator();
			while(iter2.hasNext())
			{
				String line = iter2.next();
				String[] s = line.split("\\s");
				double time = RecordElement.computeDoubleTimeinSeconds(s[5].replace(";", " "));
				String recID = s[0];
				String component = s[4];
				String category = s[3];
				String blockCode = s[9];
				RASRecord r = new RASRecord(time, recID, msgID, component, category, blockCode, line);
				rlist.add(r);
			}
		}
		Collections.sort(rlist);
		System.out.println("Finish sorting ....");
		
		PVFile.print2File(rlist, outputDir+"/total.fltr");
		
		RASRecord[] record = new RASRecord[rlist.size()];
		
		Iterator<RASRecord> iter3 = rlist.iterator();
		for(int i = 0;iter3.hasNext();i++)
		{
			RASRecord r = iter3.next();
			record[i] = r;
		}		
		
		for(int i= 0;i<record.length;i++)
		{
			RASRecord curRecord = record[i];
			double curTime = curRecord.getTime();
			double nextTime = curTime;
			boolean errFollowed = false;
			for(int j = i+1;j<record.length;j++)
			{
				RASRecord nextRecord = record[j];
				nextTime = nextRecord.getTime();
				if(nextTime - curTime > minutes*60)
					break;
				
				String curMsgID = curRecord.getMsgID();
				String nextMsgID = nextRecord.getMsgID();
				String curComponent = curRecord.getComponent();
				String nextComponent = nextRecord.getComponent();
				String curCategory = curRecord.getCategory();
				String nextCategory = nextRecord.getCategory();
				
				String msgIDPair = curMsgID+"-"+nextMsgID;
				String compoPair = curComponent+"-"+nextComponent;
				String categPair = curCategory+"-"+nextCategory;
				
				updateMapList(msgIDPair, nextTime - curTime, msgIDList, msgIDMap);
				updateMapList(compoPair, nextTime - curTime, compoList, compoMap);
				updateMapList(categPair, nextTime - curTime, categList, categMap);				
				
				errFollowed = true;
			}
			if(errFollowed)
				totalCount++;
		}

		Collections.sort(msgIDList);
		Collections.sort(compoList);
		Collections.sort(categList);
		
		PVFile.print2File(msgIDList, outputDir+"/msgIDList.txt");
		PVFile.print2File(compoList, outputDir+"/compoList.txt");
		PVFile.print2File(categList, outputDir+"/categList.txt");
		
		List<String> resultList = new ArrayList<String>();
		double ratio = ((double)totalCount)/(record.length-1);
		resultList.add("ratio="+ratio);
		PVFile.print2File(resultList, outputDir+"/stat.txt");
		System.out.println("ratio = "+ratio);
		System.out.println("Done.");
	}
	
	public static void updateMapList(String idPair, double timeInterval, List<RASTmporalPairElement> list, Map<String, RASTmporalPairElement> map)
	{
		RASTmporalPairElement element = map.get(idPair);
		if(element==null)
		{
			element = new RASTmporalPairElement(idPair);
			list.add(element);
			map.put(idPair, element);
		}
		element.setCount(element.getCount()+1);
		element.setSumMinutes(element.getSumMinutes()+timeInterval/60.0);
	}
}
