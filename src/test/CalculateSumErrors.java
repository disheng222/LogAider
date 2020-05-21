package test;

import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class CalculateSumErrors {

	public static void main(String[] args)
	{
		int total = 0;
		List<String> lineList = PVFile.readFile("/home/sdi/Development/Catalog-project/miralog/5-year-data/Job/rasAffectedJobs/errLocDistribution/level1.err");
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] s = line.split("\\s");
			int a = Integer.parseInt(s[1]);
			total += a;
		}
		System.out.println("total="+total);
	}
}
