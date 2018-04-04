package filter;

/**
 * Evaluation methods: 
 * 1. run ClassifyLogBasedonMessageID.java
 * 2. run TemporalSpatialFilter.java
 * 3. run Summarize1.java
 * 4. run Summarize2.java
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import util.NumericChecker;
import util.PVFile;
import util.RecordSplitter;

public class ClassifyLogBasedonMessageID {

	static HashMap<String, List<String>> msgIDRecordMap = new HashMap<String, List<String>>();
	
	public static void main(String[] args)
	{
		if(args.length<2)
		{
			System.out.println("Usage: java ClassifyLogBasedonMessageID [inputLogFile] [outputDir]");
			System.out.println("Example: java ClassifyLogBasedonMessageID /home/fti/Catalog-project/miralog/totalFatalMsg.fat /home/fti/Catalog-project/miralog/FilterAndClassify");
			System.exit(0);
		}
		
		String inputLogFile = args[0];
		String outputDir = args[1];
	
		System.out.println("Reading RAS log file....");
		
		List<String> lineList = PVFile.readFile(inputLogFile);
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String messageID;
			String line = iter.next();

			//String[] s = line.split(",");
			String[] s = RecordSplitter.partition(line);
			String recID = s[0];
			if(!NumericChecker.isNumeric(recID))
				continue;
//			if(line.contains("\""))
//			{
//				messageID = removeDoubleQuotationMarkAndTrim(s[1]);
//			}
//			else
//			{
//				messageID = s[1];
//			}
			
			messageID = s[1];
			
			//String recordID = s[0];
			//String time = s[5];
			//String allocation = s[7].trim();
			//String location = s[8].trim();
			
			List<String> recordList = msgIDRecordMap.get(messageID);
			if(recordList == null)
			{
				recordList = new ArrayList<String>();
				msgIDRecordMap.put(messageID, recordList);
			}
			
			recordList.add(line);
		}
		
		System.out.println("Writing classified recoreds....");
		System.out.println("There are "+msgIDRecordMap.size()+" msgIDs to deal with....");

		Iterator iter2 = msgIDRecordMap.entrySet().iterator(); 
		while (iter2.hasNext()) {
		    Map.Entry entry = (Map.Entry) iter2.next(); 
		    String msgID = (String)entry.getKey();
		    List<String> recordList = (List<String>)entry.getValue();
		    
		    String outputFile = outputDir+"/"+msgID+".ori";
		    System.out.println("Writing records to "+outputFile);
		    PVFile.print2File(recordList, outputFile);
		} 
			
		System.out.println("done.");
	}
	
	public static String removeDoubleQuotationMarkAndTrim(String s)
	{
		if(s.startsWith("\"")&&s.endsWith("\""))
			return s.substring(1, s.length()-1).trim();
		else
			return s.trim();
	}
}
