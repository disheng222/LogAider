package analysis.temporalcorr.test;

import java.util.Iterator;
import java.util.List;

import filter.EventElement;
import util.PVFile;

public class RemoveSingleEvents {
	
	public static void main(String[] args)
	{
		String filePath = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/FilterAndClassify/no-MaintResv-no-DIAGS-filter-interval=60s/s/allEvents.txt";
		List<String> lineList = PVFile.readFile(filePath);
		
		double prepreTime = 0;
		double preTime = 0;
		for(int i = 0;i<lineList.size();i++)
		{
			String line = lineList.get(i);
			EventElement e = new EventElement(line);
			double curDtime = e.getFirstRecord().getDtime();
			if(curDtime-preTime > 86400 && preTime-prepreTime>86400)
			{
				lineList.remove(i);
				System.out.println(e.getEventID());
				i--;
			}
			prepreTime = preTime;
			preTime = e.getLatestRecord().getDtime();
		}
		PVFile.print2File(lineList, filePath+".rmsing");
		System.out.println("outputfile: "+filePath+".rmsing");
	}
}
