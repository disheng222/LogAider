package filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import util.NumericChecker;
import util.PVFile;

//Compute the distribution of categories based on messages
public class Summarize2 {

	static HashMap<String, Count> map = new HashMap<String, Count>();
	static List<Count> countList = new ArrayList<Count>();
	static int monthInterval = 0;
	static int dayInterval = 2;
	
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
				String category = s[2];
				
				Count c = map.get(category);
				if(c==null)
				{
					c = new Count(category);
					map.put(category, c);
					countList.add(c);
				}
				int number = Integer.parseInt(s[4]);
				String msgID = s[0];
				c.count += number;
				c.messageIDList.add(msgID);
			}
		}
		
		Collections.sort(countList);
		PVFile.print2File(countList, filePath+".cat");
		System.out.println("output: "+filePath+".cat");
		System.out.print("done.");
		
	}
}

class Count implements Comparable<Count>
{
	String type;
	public int count = 0;
	public List<String> messageIDList = new ArrayList<String>();
	
	public Count(String type) {
		this.type = type;
	}

	public String toString()
	{
		String s = type+"\t\t"+count+"\t\t";
		Iterator<String> iter = messageIDList.iterator();
		while(iter.hasNext())
		{
			s += "|"+iter.next();
		}
		return s;
	}
	
	public int compareTo(Count c)
	{
		if(count < c.count)
			return -1;
		else if(count > c.count)
			return 1;
		else
			return 0;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}