package analysis.Job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import util.PVFile;
import util.RecordSplitter;

public class CalculateFailuresBasedonUsers {

	static String wlLengthFailureFile;
	static String proj_exit_file_fs;
	static String proj_exit_file_pe; 
	static String user_exit_file_fs;
	static String user_exit_file_pe; 	
	static String proj_outputFile;
	static String user_outputFile;
	
	public static void main(String[] args)
	{
		if(args.length < 7)
		{
			System.out.println("Usage: java CalculateFailuresBasedonUsers [wlLengthFailureFile] "
					+ "[proj_exit_file_fs] [proj_exit_file_pe] [proj_outputFile] "
					+ "[user_exit_file_fs] [user_exit_file_pe] [user_outputFile]");
			System.out.println("Example: java CalculateFailuresBasedonUsers "
					+ "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/cobalt/lengthAnalysis/breakWCJobList.ori "
					+ "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/cobalt/featureState/COBALT_PROJECT_NAME_GENID/COBALT_PROJECT_NAME_GENID-EXIT_CODE.fs "
					+ "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/cobalt/featureState/COBALT_PROJECT_NAME_GENID/COBALT_PROJECT_NAME_GENID-EXIT_CODE.pe90 "
					+ "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/cobalt/projFailure.out "
					+ "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/cobalt/featureState/COBALT_USER_NAME_GENID/COBALT_USER_NAME_GENID-EXIT_CODE.fs "
					+ "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/cobalt/featureState/COBALT_USER_NAME_GENID/COBALT_USER_NAME_GENID-EXIT_CODE.pe90 "
					+ "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/cobalt/userFailure.out");
			System.exit(0);
		}

		wlLengthFailureFile = args[0];
		
		proj_exit_file_fs = args[1];
		proj_exit_file_pe = args[2];
		proj_outputFile = args[3];
		
		user_exit_file_fs = args[4];
		user_exit_file_pe = args[5];
		user_outputFile = args[6];
		
		HashMap<String, Integer> projMap = new HashMap<String, Integer>();
		HashMap<String, Integer> userMap = new HashMap<String, Integer>();
		
		computeBreakWLLengthCount(projMap, userMap);
		
		process(proj_exit_file_fs, proj_exit_file_pe, projMap, proj_outputFile);
		System.out.println("OutputFile= "+proj_outputFile);
		
		UserFailureElement.userid = 0;
		process(user_exit_file_fs, user_exit_file_pe, userMap, user_outputFile);
		System.out.println("OutputFile= "+user_outputFile);
		System.out.println("done.");
	
	}

	public static void process(String fsFile, String peFile, HashMap<String, Integer> map, String outputFile)
	{
		List<String> resultList = new ArrayList<String>();
		List<String> lineList_fs = PVFile.readFile(fsFile);
		List<String> lineList_pe = PVFile.readFile(peFile);
		Iterator<String> iter_fs = lineList_fs.iterator();
		Iterator<String> iter_pe = lineList_pe.iterator();
		while(iter_pe.hasNext())
		{
			String line_fs = iter_fs.next();
			String line_pe = iter_pe.next();
			
			if(line_fs.startsWith("#"))
				continue;
			String[] fs = line_fs.split("\\s");
			String userID = fs[0];
			
			String[] pe = line_pe.split("\\s");
			String[] ss = pe[0].split("\\[");
			int totalEventCount = Integer.parseInt(ss[1].split(",")[0]);
			if(totalEventCount<10)
				continue;
			float normalEventCount = Integer.parseInt(fs[54]);
			
			Integer breakWLCount = map.get(userID);
			if(breakWLCount==null)
				breakWLCount = 0;
			UserFailureElement ufe = new UserFailureElement(userID, totalEventCount, normalEventCount, breakWLCount);
			resultList.add(ufe.toString());
		}
		
		PVFile.print2File(resultList, outputFile);
	}
	
	public static void computeBreakWLLengthCount(HashMap<String, Integer> projectMap, HashMap<String, Integer> userMap)
	{
		List<String> lineList = PVFile.readFile(wlLengthFailureFile);
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] s = RecordSplitter.partition(line);
			if(projectMap!=null)
			{
				String projName = s[16];
				Integer count = projectMap.get(projName);
				if(count==null)
					projectMap.put(projName, 1);
				else
					projectMap.put(projName, count+1);
			}
			if(userMap!=null)
			{
				String userName = s[17];
				Integer count = userMap.get(userName);
				if(count==null)
					userMap.put(userName, 1);
				else
					userMap.put(userName, count+1);
			}
		}
	}
}
