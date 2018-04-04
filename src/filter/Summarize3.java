package filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import util.NumericChecker;
import util.PVFile;

//Compute the distribution of components based on messages
public class Summarize3 {

	static HashMap<String, Count> map = new HashMap<String, Count>();
	static List<Count> countList = new ArrayList<Count>();
	
	public static void main(String[] args)
	{
		
		//String filePath = "/home/fti/Catalog-project/miralog/RAS-Job/RAS/FilterAndClassify/summarize/fatal-msg-count.txt";
		String filePath = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/FilterAndClassify/summarize/ts/fatal-msg-count.txt";
		
		List<String> lineList = PVFile.readFile(filePath);
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next().trim();
			String[] s = line.split("\\s+");
			
			
			//s: messageID severity category numberOfEvents
			if(s.length>0 && NumericChecker.isNumeric(s[4]))
			{
				String component = s[3];
				Count c = map.get(component);
				if(c==null)
				{
					c = new Count(component);
					map.put(component, c);
					countList.add(c);
				}
				int number = Integer.parseInt(s[4]);
				String msgID = s[0];
				c.count += number;
				c.messageIDList.add(msgID);
			}
		}
		
		Collections.sort(countList);
		PVFile.print2File(countList, filePath+".cmp");
		System.out.println("output: "+filePath+".cmp");
		System.out.print("done.");
		
	}
}
