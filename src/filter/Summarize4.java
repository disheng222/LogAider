package filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import util.NumericChecker;
import util.PVFile;

//Compute the distribution of fine-grained components based on messages and location fields (such as Rxx-Ixx-Jxx-Uxx)
public class Summarize4 {

	static HashMap<String, Count> totalMap = new HashMap<String, Count>();
	static List<Count> countList = new ArrayList<Count>();
	
	static HashMap<String, Count>[] monthlyMap = new HashMap[12];
	static List<Count>[] monthlyList = new ArrayList[12];
	
	static String outputDir = "";
	
	public static void main(String[] args)
	{
		
		//String filePath = "/home/fti/Catalog-project/miralog/RAS-Job/RAS/FilterAndClassify/summarize/fatal-msg-count.txt";
		
		if(args.length<3)
		{
			System.out.println("Usage: java Summarize4 [classifyFilterDir] [extension] [outputFile]");
			System.out.println("Example: java Summarize4 /home/fti/Catalog-project/miralog/RAS-Job/RAS/FilterAndClassify fltr /home/fti/Catalog-project/miralog/RAS-Job/RAS/FilterAndClassify/summarize");
			System.exit(0);
		}
		String classFilterDir = args[0];
		String extension = args[1];
		outputDir = args[2];
		String outputFile = outputDir+"/fatal-locationKey-count.txt";	
		
		for(int i = 0;i<12;i++)
		{
			monthlyMap[i] = new HashMap<String, Count>();
			monthlyList[i] = new ArrayList<Count>();
		}
		
		List<String> fileNameList = PVFile.getFiles(classFilterDir, extension);
		Iterator<String> iter = fileNameList.iterator();
		while(iter.hasNext())
		{
			String fileName = iter.next();
			String filePath = classFilterDir+"/"+fileName;
			List<String> lineList = PVFile.readFile(filePath);
			Iterator<String> iter2 = lineList.iterator();
			while(iter2.hasNext())
			{
				String line = iter2.next().trim();
				String[] s = line.split("\\s+");

				//s: messageID severity category numberOfEvents
				if(s.length>0 && NumericChecker.isNumeric(s[7]))
				{
					String keyLocString = s[8];
					String[] ss = keyLocString.split(":");
					String key = ss[0];
					//int number = Integer.parseInt(ss[1]);
					
					int month = Integer.parseInt(s[5].split(";")[0].split("-")[1]);
					int i = month - 1;
					List<Count> monthList = monthlyList[i];
					
					Count c = totalMap.get(key); //key=componentID
					if(c==null)
					{
						c = new Count(key);
						totalMap.put(key, c);
						countList.add(c);
					}
					Count cc = monthlyMap[i].get(key);
					if(cc==null)
					{
						cc = new Count(key);
						monthlyMap[i].put(key, cc);
						monthlyList[i].add(cc);
					}
					
					String msgID = s[0];
					c.count += 1;
					cc.count += 1;
					c.messageIDList.add(msgID);
				}
			}
		}
		
		Collections.sort(countList);
		PVFile.print2File(countList, outputFile);
		System.out.println("output: "+outputFile);
		
		
		//TODO: print the monthly data
		List<String> resultList = printMonthlyData();
		String outputFile2 = outputDir+"/fatal-locationKey-count-monthly.txt";
		System.out.println("output2: "+outputFile2);
		PVFile.print2File(resultList, outputFile2);
		
		System.out.print("done.");
		
	}
	
	private static List<String> printMonthlyData()
	{
		List<String> resultList = new ArrayList<String>();
		//get all fields
		HashMap<String, Integer> keyIndexMap = new HashMap<String, Integer>();
		
		String fields = "month";
		Iterator iter = totalMap.entrySet().iterator(); 
		for (int i = 0;iter.hasNext();i++) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    String key = (String)entry.getKey(); 
		    keyIndexMap.put(key, i);
		    fields+=" "+key;
		} 
		
		resultList.add(fields);
		
		int componentSize = totalMap.size();
		for(int i = 0;i<12;i++)
		{
			int[] count = new int[componentSize];
			int month = i+1;
			Iterator<Count> iter2 = monthlyList[i].iterator();
			while(iter2.hasNext())
			{
				Count c = iter2.next();
				String key = c.getType();
				int num = c.getCount();
				int index = keyIndexMap.get(key);
				count[index] = num;
			}
			
			String result = String.valueOf(month);
			for(int a = 0;a<componentSize;a++)
				result+=" "+count[a];
			
			resultList.add(result);
		}
		return resultList;
	}
}
