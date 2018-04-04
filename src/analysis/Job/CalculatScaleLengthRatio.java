package analysis.Job;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import filter.RecordElement;
import util.PVFile;
import util.RecordSplitter;

public class CalculatScaleLengthRatio {

	public static void main(String[] args)
	{
		String jobLogFilePath = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/cobalt/cobalt_machine_job_mira_2015.csv";
		String outputFilePath = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/cobalt/generalStat/scaleLength.txt";
		String outputFilePath2 = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/cobalt/generalStat/scaleLength-failure.txt";
		
		List<String> lineList = PVFile.readFile(jobLogFilePath);
		
		List<String> scaleLengthList = new ArrayList<String>();
		List<String> scaleLengthList2 = new ArrayList<String>();
		
		Iterator<String> iter = lineList.iterator();
		
		float bigScaleLengthCount = 0;
		int counter = 0;
		int counter2 = 0;
		int offset = 300;
		int lowerBound = 1024;
		int upperBound = 1024;
		
		int bucketSize = 24;
		int interval = 24/bucketSize;
		
		int[] hoursTotalCount = new int[bucketSize];
		int[] hoursFailureCount = new int[bucketSize];
		
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.startsWith("#"))
				continue;
			String[] s = RecordSplitter.partition(line);
			float nodeCount = Float.parseFloat(s[11]);
			float startTime = RecordElement.computeFloatTimeinSeconds(s[1]);
			float finishTime = RecordElement.computeFloatTimeinSeconds(s[2]);
			float executionLength = (finishTime - startTime)/3600; //in seconds
			int exit_code = Integer.parseInt(s[19]);
			if(nodeCount>=lowerBound && nodeCount<=upperBound)
			{
				counter ++;
				int bucket = (int)(executionLength/interval);
				if(bucket>=bucketSize)
					bucket = bucketSize-1;
				hoursTotalCount[bucket] ++;
				
				if(exit_code!=0)
				{
					//scaleLengthList2.add((nodeCount-offset)+" "+executionLength);

					hoursFailureCount[bucket] ++;
					counter2 ++;
				}
				else
				{
					scaleLengthList.add((nodeCount+offset)+" "+executionLength);
				}
				
			}
//			if(nodeCount>512 && nodeCount<10000)
//			{
//				bigScaleLengthCount+=executionLength;
//				counter++;
//			}
		}
		
		float[] ratios = new float[bucketSize];
		for(int i = 0;i<bucketSize;i++)
		{
			ratios[i] = ((float)hoursFailureCount[i])/hoursTotalCount[i];
			System.out.println((i*interval+(float)interval/2)+" "+ratios[i]*100);
		}
		
		
		//float avg = bigScaleLengthCount/counter;
		float ratio = ((float)counter2)/counter;
		System.out.println("ratio="+ratio);
		//System.out.println("avg hours for big-scale jobs = "+avg);
		System.out.println("outputFile= "+outputFilePath);
		//PVFile.print2File(scaleLengthList, outputFilePath);
		//PVFile.print2File(scaleLengthList2, outputFilePath2);
		System.out.println("done.");
	}
}
