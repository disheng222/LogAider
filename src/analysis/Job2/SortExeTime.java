package analysis.Job2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class SortExeTime {

	public static void main(String[] args)
	{
		String file = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/totalFile.txt";
		List<String> lineList = PVFile.readFile(file);
		
		List<SElement> list = new ArrayList<SElement>();
		HashMap<String, SElement> map = new HashMap<String, SElement>();
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] s = line.split("\\s");
			String exeTime_s = s[2];
			float exeTime = Float.parseFloat(exeTime_s);
			float queTime = Float.parseFloat(s[3]);
			SElement e = map.get(exeTime_s);
			if(e==null)
			{
				e = new SElement(0, exeTime);
				map.put(exeTime_s, e);
				list.add(e);
			}
			e.setCounter(e.getCounter()+1);
			e.setSumQueTime(e.getSumQueTime()+queTime);
		}
		
		Collections.sort(list);
		
		PVFile.print2File(list, file+".sorttime");
		
		System.out.println("output:"+file+".sorttime");
	}
}

class SElement implements Comparable<SElement>
{
	private int counter = 0;
	private double sum_queTime = 0;
	private float exeTime = 0;
	public SElement(float queTime, float exeTime) {
		this.sum_queTime = queTime;
		this.exeTime = exeTime;
	}
	public float getExeTime() {
		return exeTime;
	}
	public void setExeTime(float exeTime) {
		this.exeTime = exeTime;
	}
	public double getSumQueTime() {
		return sum_queTime;
	}
	public void setSumQueTime(double queTime) {
		this.sum_queTime = queTime;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public int compareTo(SElement e)
	{
		if(exeTime<e.exeTime)
			return -1;
		else if(exeTime>e.exeTime)
			return 1;
		else
			return 0;
	}
	
	public String toString()
	{
		return exeTime+" "+sum_queTime/counter;
	}
}