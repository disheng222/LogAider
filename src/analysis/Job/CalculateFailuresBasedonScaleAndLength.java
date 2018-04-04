package analysis.Job;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.PVFile;
import util.RecordSplitter;

public class CalculateFailuresBasedonScaleAndLength {

	static String inputFile = "";
	static String outputDir = "";
	
	public static void main(String[] args)
	{
		if(args.length<2)
		{
			System.out.println("Usage: java CalculateFailuresBasedonScaleAndLength [inputFile] [outputDir]");
			System.out.println("Example: java CalculateFailuresBasedonScaleAndLength /home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/cobalt/totalFatalMsg.fat "
					+ "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/cobalt/scaleLengthFailure");
			System.exit(0);
		}
		
		inputFile = args[0];
		outputDir = args[1];
		
		List<String> totalList = new ArrayList<String>();
		List<String> failureList = new ArrayList<String>();
		
		List<String> lineList = PVFile.readFile(inputFile);
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.startsWith("#"))
				continue;
			String[] s = RecordSplitter.partition(line);
			int exitCode = Integer.parseInt(s[19]);
			double wallTimeSecs = Double.parseDouble(s[7]);
			double wallTimeHours = wallTimeSecs/3600;
			float nodeCount = Float.parseFloat(s[11]);
			String jobID = s[25];
			String ss = jobID+" "+nodeCount+" "+wallTimeHours;
			totalList.add(ss);
			if(exitCode==0)
				continue;
			failureList.add(ss);
		}
		
		PVFile.print2File(totalList, outputDir+"/totalList.txt");
		PVFile.print2File(failureList, outputDir+"/failureList.txt");
		System.out.println("outputDir= "+outputDir);
		System.out.println("done.");
	}
}
