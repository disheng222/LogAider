package analysis.Job2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class ComputePercentageforExitSignals {

	public static void main(String[] args)
	{
		String file = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/fullschema/withCount/EXIT_SIGNAL.fsc";
		List<String> lineList = PVFile.readFile(file);
		Iterator<String> iter = lineList.iterator();
		
		HashMap<String, SignalElement> signalMap = new HashMap<String, SignalElement>();
		List<SignalElement> signalList = new ArrayList<SignalElement>();
		iter.next(); //remove the first line (meta data)
		int total = 0;
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] s = line.split("\\s");
			String[] signals = s[0].split("-");
			int value = Integer.parseInt(s[1]);
			for(int i = 0;i<signals.length;i++)
			{
				String key = signals[i];
				SignalElement e = signalMap.get(key);
				if(e==null)
				{
					e = new SignalElement(key, 0);
					signalMap.put(key, e);
					signalList.add(e);
				}
				e.setNumber(e.getNumber()+value);
			}
			total += value;
		}
		
		Collections.sort(signalList);
		
		String outputFile1 = file+"list";
		PVFile.print2File(signalList, outputFile1);
		System.out.println("output: "+outputFile1);
		
		List<String> resultList = new ArrayList<String>();
		List<String> lineList2 = PVFile.readFile(outputFile1);
		Iterator<String> iter2 = lineList2.iterator();
		while(iter2.hasNext())
		{
			String line = iter2.next();
			String[] s = line.split("\\s");
			String key = s[0];
			Integer value = Integer.parseInt(s[1]);
			float percent = (float)1.0*value/total;
			resultList.add(key+" "+percent);
		}
		
		PVFile.print2File(resultList, file+"perc");
		System.out.println("done");
	}
}

class SignalElement implements Comparable<SignalElement>
{
	private String key;
	private int number;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public SignalElement(String key, int number) {
		this.key = key;
		this.number = number;
	}
	
	public int compareTo(SignalElement other)
	{
		if(number>other.number)
			return -1;
		else if(number<other.number)
			return 1;
		else
			return 0;
	}
	
	public String toString()
	{
		return key+" "+number;
	}
}