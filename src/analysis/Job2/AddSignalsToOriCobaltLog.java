package analysis.Job2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import util.PVFile;
import util.RecordSplitter;

public class AddSignalsToOriCobaltLog {

	public static void main(String[] args)
	{
		String signalFile = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/abnormalJobs/abnormalJobList.csv";
		String dataDir = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job";
		String ext = "csv";
		
		//Build job-signal map
		HashMap<String, String> signalMap = new HashMap<String, String>();
		List<String> sigLineList = PVFile.readFile(signalFile);
		Iterator<String> iter = sigLineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] ss = RecordSplitter.partition(line);
			String signal = ss[57];
			String jobID =ss[1];
			signalMap.put(jobID, signal);
		}
		
		System.out.println("Start scanning log");
		List<String> fileList = PVFile.getFiles(dataDir, ext);
		Iterator<String> iter2 = fileList.iterator();
		while(iter2.hasNext())
		{
			String fileName = iter2.next();
			System.out.println("Processing "+fileName);
			String filePath = dataDir+"/"+fileName;
			String filePath2 = dataDir+"/"+fileName+"s"; //s means signal
			List<String> lineList = PVFile.readFile(filePath);
			List<String> lineList2 = new ArrayList<String>();
			Iterator<String> iter3 = lineList.iterator();
			iter3.next(); //remove the first meta data line
			while(iter3.hasNext())
			{
				String line = iter3.next();
				if(line.startsWith("#"))
					continue;
				String[] ss = RecordSplitter.partition(line);
				String jobID = ss[1];
				String signal = signalMap.get(jobID);
				if(signal==null)
					lineList2.add(line+",0");
				else
				{
					if(signal.equals("9-888"))
						lineList2.add(line+",888");
					else if(signal.startsWith("35-888"))
						lineList2.add(line+",35,"+signal.split(" ")[1]); //35 means related to RAS event
					else if(signal.equals("9-36"))
						lineList2.add(line+",9");
					else if(signal.contains("333"))
					{
						String[] sss = signal.split("-");
						if(sss.length==1)
							lineList2.add("#"+line+","+signal);
						else
						{
							String newS = "";
							for(int i = 0;i<sss.length;i++)
							{
								if(sss[i].equals("333"))
									continue;
								newS += sss[i]+" ";
							}
							newS = newS.trim();
							newS = newS.replaceAll(" ", "-");
							if(newS.equals("9-888"))
								newS = "888";
							lineList2.add(line+","+newS);
						}
					}
					else
						lineList2.add(line+","+signal.replaceAll(" ", ","));
						//lineList2.add(line+","+signal);
				}
			}
			PVFile.print2File(lineList2, filePath2);
			System.out.println("output file: "+filePath2);
		}
		
		System.out.println("done");
	}
}
