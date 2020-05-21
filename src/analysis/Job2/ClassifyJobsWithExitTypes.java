package analysis.Job2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import util.PVFile;
import util.RecordSplitter;

public class ClassifyJobsWithExitTypes {

	public static void main(String[] args)
	{
		String dataDir = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job";
		String ext = "csvs";
		
		HashMap<String, List<String>> fileMap = new HashMap<String, List<String>>();
		
		
		List<String> fileList = PVFile.getFiles(dataDir, ext);
		Collections.sort(fileList);
		Iterator<String> iterf = fileList.iterator();
		while(iterf.hasNext())
		{
			String fileName = iterf.next();
			String filePath = dataDir+"/"+fileName;
			List<String> lineList = PVFile.readFile(filePath);
			Iterator<String> iter = lineList.iterator();
			while(iter.hasNext())
			{
				String line = iter.next();
				String[] ss = RecordSplitter.partition(line);
				String recordID = ss[1];
				double runTime = Double.parseDouble(ss[13])/3600.0; //in hours
				String exitstatus = ss[57];
				String key = exitstatus;
				List<String> recordList = fileMap.get(key);
				if(recordList==null)
				{
					recordList = new ArrayList<String>();
					fileMap.put(key, recordList);
				}
				if(ss.length==59)
					recordList.add(recordID+" "+runTime+" "+ss[58]);
				else
					recordList.add(recordID+" "+runTime);	
			}
		}
		Iterator iter = fileMap.entrySet().iterator(); 
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    String key = (String)entry.getKey(); 
		    List<String> recordList = (List<String>)entry.getValue();
			PVFile.print2File(recordList, dataDir+"/exitRecords/exit_"+key+".csv");
			System.out.println("Writing records to "+dataDir+"/exitRecords/exit_"+key+".csv");
		} 
		
		System.out.println("done");
	}
}
