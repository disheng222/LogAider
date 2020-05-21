package analysis.darshan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import util.PVFile;
import util.RecordSplitter;

public class AddSignalToDarshanLog {

	public static void main(String[] args)
	{
		String darshanLogFile = "/home/sdi/windows-xp/Work/Catalog-project/Catalog-data/miralog/5-year-data/ANL-ALCF-DARSHAN-MIRA_20140101_20180930.csv";
		String cobaltLogDir = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/darshan";
		
		//Build the signal map based on cobalt log
		HashMap<String, String> signalMap = new HashMap<String, String>();
		List<String> fileList = PVFile.getFiles(cobaltLogDir, "csvs");
		Iterator<String> iterf = fileList.iterator();
		while(iterf.hasNext())
		{
			String fileName = iterf.next();
			System.out.println("Loading signals from "+fileName);
			String filePath = cobaltLogDir + "/" + fileName;
			List<String> lineList = PVFile.readFile(filePath);
			Iterator<String> iter = lineList.iterator();
			while(iter.hasNext())
			{
				String line = iter.next();
				if(line.startsWith("#"))
					continue;
				String[] s = RecordSplitter.partition(line);
				String jobID = s[1];
				String signal = s[57];
				if(signal.equals("35"))
					signal += ","+s[58];
				if(!signal.equals("0"))
					signalMap.put(jobID, signal);
			}
		}
		
		//Scan Darshan Log
		
		List<String> resultList = new ArrayList<String>();
		
		List<String> lineList = PVFile.readFile(darshanLogFile);
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] s = RecordSplitter.partition(line);
			String cobaltID = s[123];
			String signal = signalMap.get(cobaltID);
			if(signal==null)
				signal = "0";
			resultList.add(line+","+signal);
		}
		
		PVFile.print2File(resultList, darshanLogFile+"s");
		System.out.println("output file: "+darshanLogFile+"s");
		System.out.println("Done");
	}
}
