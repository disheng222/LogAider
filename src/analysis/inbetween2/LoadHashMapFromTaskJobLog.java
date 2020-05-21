package analysis.inbetween2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import util.PVFile;
import util.RecordSplitter;

public class LoadHashMapFromTaskJobLog {

	public static HashMap<String, String> taskJobMap = new HashMap<String, String> ();
	
	public static void loadTasks(String taskFile)
	{
		System.out.println("Loading tasks from "+taskFile);
		List<String> taskList = PVFile.readFile(taskFile);
		System.out.println("total # tasks: "+ taskList.size());
		
		Iterator<String> iter = taskList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] s = RecordSplitter.partition(line);
			String taskID = s[1];
			String cobaltJobID = s[7];
			taskJobMap.put(taskID, cobaltJobID);
		}
	}
	
}
