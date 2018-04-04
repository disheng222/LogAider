package test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.PVFile;
import util.RecordSplitter;

public class ConvertReservationRecordsToCorrectTimeFormat {

	public static String file = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/schema/COBALT_MACHINE_RESERVATION_2015-filters.csv";
	public static String outputFile = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/schema/COBALT_MACHINE_RESERVATION_2015-filters.csv.new";
	public static void main(String[] args)
	{
		List<String> resultList= new ArrayList<String>();
		List<String> lineList = PVFile.readFile(file);
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.startsWith("#"))
			{
				resultList.add(line);
				continue;
			}
			String[] s = RecordSplitter.partition(line);
			String startTimeString = s[1];
			String endTimeString = s[2];
			
			String correctStartTime = convertTimeFormat(startTimeString);
			String correctEndTime = convertTimeFormat(endTimeString);
			
			String result = s[0]+","+correctStartTime+","+correctEndTime;
			for(int i = 3;i<s.length-1;i++)
				result+=","+s[i];
			result+=",\""+s[s.length-1]+"\"";
			resultList.add(result);
		}
		PVFile.print2File(resultList, outputFile);
		System.out.println("Output: "+outputFile);
		System.out.println("Done.");
	}
	
	/**
	 * 12/16/2015 16:53 --> 2015-12-16 16:53:00
	 * @param time
	 * @return
	 */
	public static String convertTimeFormat(String time)
	{
		String[] s = time.split("\\s");
		String[] d = s[0].split("/");
		String[] m = s[1].split(":");
		
		String year = d[2];
		String month = PVFile.df2.format(Integer.parseInt(d[0]));
		String day = PVFile.df2.format(Integer.parseInt(d[1]));
		
		String hour = PVFile.df2.format(Integer.parseInt(m[0]));
		String minute = PVFile.df2.format(Integer.parseInt(m[1]));
		
		String result = year+"-"+month+"-"+day+" "+hour+":"+minute+":00";
		return result;
	}
}
