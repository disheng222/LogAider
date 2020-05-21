package analysis.Job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import util.PVFile;
import util.RecordSplitter;

/**
 * This class is to check the correlation between time, #nodes,.... with the exit_status
 * I.e., contingency table for exploring the relationship between time or number metrics with exit_status
 * @author sdi
 *
 */

public class GenerateStateFeatures3 {

	public static int sortingMode = 0; //0:probability; 1:margin of error

	public static int exit_status_index = 174;
	public static long totalCount = 0;
	
	public static void printMatrix(String title, int numStates, int numExitStatus, String[] exitStatusValues, double[][] data, String outputFile)
	{
		List<String> wallTimeErrLogStringList = new ArrayList<String>();
		wallTimeErrLogStringList.add("# "+title);
		for(int i = 0;i<numExitStatus;i++)
		{
			StringBuilder sb = new StringBuilder(exitStatusValues[i]);
			for(int j = 0;j < numStates;j++)
				sb.append(" ").append((int)data[j][i]);
			wallTimeErrLogStringList.add(sb.toString().trim());
		}
		PVFile.print2File(wallTimeErrLogStringList, outputFile);
	}
	
	public static void main(String[] args)
	{
		if(args.length<3)
		{
			System.out.println("Usage: java GenerateStateFeatures3 [fullSchemaDir] [darshanLogFile] [outputDir]");
			System.out.println("Example: java GenerateStateFeatures3 /home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/darshan/fullSchema/withRatio /home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/darshan/ANL-ALCF-DARSHAN-MIRA_20140101_20180930.csvs /home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/darshan/analysis");
			System.exit(0);
		}
		
		String fullSchemaDir = args[0];
		String darshanLogFile = args[1];
		String outputDir = args[2];
	
		int maxReadByteIndex = 101;
		int totalReadByteIndex = 33;
		int maxWrittenByteIndex = 153;
		int totalWrittenByteIndex = 106;
		
		//log scale
		
		int numExitStatus = 11;
		
		double[][] maxReadByteErrLog = new double[16][numExitStatus]; //min: 0.032, 0.064, 0.128, 0.256, 0.512, 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024
		double[][] totalReadByteErrLog = new double[16][numExitStatus]; //min: 0.512, 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384
		double[][] maxWrittenByteErrLog = new double[16][numExitStatus]; //0.032, 0.064, 0.128, 0.256, 0.512, 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024
		double[][] totalWrittenByteErrLog = new double[16][numExitStatus]; //0.512, 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384
				
		String[] exitStatusValues = new String[numExitStatus];
		HashMap<String, Integer> exitIndexMap = loadIndex(fullSchemaDir+"/SIGNAL.fsr", exitStatusValues);
		
		List<String> lineList = PVFile.readFile(darshanLogFile);
		
		double log2 = Math.log(2);
		
		Iterator<String> iter = lineList.iterator();
		iter.next(); //remove the first meta data line
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.startsWith("#"))
				continue;
			String[] data = RecordSplitter.partition(line);
			
			double maxReadBytes = Double.parseDouble(data[maxReadByteIndex])/33554432; //MB: min: 32MB
			double totalReadBytes = Double.parseDouble(data[totalReadByteIndex])/536870912; //MB: min: 512MB
			double maxWrittenBytes = Double.parseDouble(data[maxWrittenByteIndex])/33554432; //MB min: 32MB
			double totalWrittenBytes = Double.parseDouble(data[totalWrittenByteIndex])/536870912; //MB min: 512MB
			
			String exit_status = data[exit_status_index]; 
			
			//log scale
			int maxReadBytesIndex_log = (int)(Math.log(maxReadBytes)/log2+1);
			if(maxReadBytesIndex_log>15)
				maxReadBytesIndex_log = 15;
			if(maxReadBytesIndex_log < 0)
				maxReadBytesIndex_log = 0;
			int totalReadBytesIndex_log = (int)(Math.log(totalReadBytes)/log2+1);
			if(totalReadBytesIndex_log>15)
				totalReadBytesIndex_log = 15;
			if(totalReadBytesIndex_log<0)
				totalReadBytesIndex_log = 0;
			int maxWrittenBytesIndex_log = (int)(Math.log(maxWrittenBytes)/log2+1); 
			if(maxWrittenBytesIndex_log>15)
				maxWrittenBytesIndex_log = 15;
			if(maxWrittenBytesIndex_log<0)
				maxWrittenBytesIndex_log = 0;
			int totalWrittenBytesIndex_log = (int)(Math.log(totalWrittenBytes)/log2+1);
			if(totalWrittenBytesIndex_log>15)
				totalWrittenBytesIndex_log = 15;
			if(totalWrittenBytesIndex_log<0)
				totalWrittenBytesIndex_log = 0;		
			
			int statusIndex = exitIndexMap.get(exit_status);
			//start calculation....
			maxReadByteErrLog[maxReadBytesIndex_log][statusIndex]++;
			totalReadByteErrLog[totalReadBytesIndex_log][statusIndex]++;
			maxWrittenByteErrLog[maxWrittenBytesIndex_log][statusIndex]++;
			totalWrittenByteErrLog[totalWrittenBytesIndex_log][statusIndex]++;

			totalCount++;
			
		}		
		
		printMatrix("0.032, 0.064, 0.128, 0.256, 0.512, 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024", 16, numExitStatus, exitStatusValues, maxReadByteErrLog, outputDir+"/maxReadByteErrLog.fsc");
		printMatrix("0-0.512, 0.512-1, 1-2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 8192-...", 16, numExitStatus, exitStatusValues, totalReadByteErrLog, outputDir+"/totalReadByteErrLog.fsc");
		printMatrix("0.032, 0.064, 0.128, 0.256, 0.512, 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024", 16, numExitStatus, exitStatusValues, maxWrittenByteErrLog, outputDir+"/maxWrittenByteErrLog.fsc");
		printMatrix("0-0.512, 0.512-1, 1-2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 8192-...", 16, numExitStatus, exitStatusValues, totalWrittenByteErrLog, outputDir+"/totalWrittenByteErrLog.fsc");

		//TODO
		
		//Compute the contingency table
		double[][]maxReadByteErrLog_contingency = new double[16][numExitStatus]; 
		double[][] totalReadByteErrLog_contingency = new double[16][numExitStatus]; 
		double[][] maxWrittenByteErrLog_contingency = new double[16][numExitStatus]; 
		double[][] totalWrittenByteErrLog_contingency = new double[16][numExitStatus]; 

		for(int i = 0;i<16;i++)
		{
			for(int j = 0;j<numExitStatus;j++)
			{
				maxReadByteErrLog_contingency[i][j] = maxReadByteErrLog[i][j]/totalCount;
				totalReadByteErrLog_contingency[i][j] = totalReadByteErrLog[i][j]/totalCount;
				maxWrittenByteErrLog_contingency[i][j] = maxWrittenByteErrLog[i][j]/totalCount;
				totalWrittenByteErrLog_contingency[i][j] = totalWrittenByteErrLog[i][j]/totalCount;
			}
		}
						
		System.out.println("Log Computing aggregated probability for x -> exit_status....");
		
		double[] maxReadByteErrLog_contingency_i = new double[16];
		double[] maxReadByteErrLog_contingency_j = new double[numExitStatus];
		double[] totalReadByteErrLog_contingency_i = new double[16];
		double[] totalReadByteErrLog_contingency_j = new double[numExitStatus];
		double[] maxWrittenByteErrLog_contingency_i = new double[16];
		double[] maxWrittenByteErrLog_contingency_j = new double[numExitStatus];
		double[] totalWrittenByteErrLog_contingency_i = new double[16];
		double[] totalWrittenByteErrLog_contingency_j = new double[numExitStatus];
		
		for(int i = 0;i<16;i++)
			for(int j = 0;j<numExitStatus;j++)
			{
				maxReadByteErrLog_contingency_i[i] += maxReadByteErrLog_contingency[i][j];
				totalReadByteErrLog_contingency_i[i] += totalReadByteErrLog_contingency[i][j];
				maxWrittenByteErrLog_contingency_i[i] += maxWrittenByteErrLog_contingency[i][j];
				totalWrittenByteErrLog_contingency_i[i] += totalWrittenByteErrLog_contingency[i][j];
			}
		
		for(int j = 0;j<numExitStatus;j++)
			for(int i = 0;i<16;i++)
			{
				maxReadByteErrLog_contingency_j[j] += maxReadByteErrLog_contingency[i][j];
				totalReadByteErrLog_contingency_j[j] += totalReadByteErrLog_contingency[i][j];
				maxWrittenByteErrLog_contingency_j[j] += maxWrittenByteErrLog_contingency[i][j];
				totalWrittenByteErrLog_contingency_j[j] += totalWrittenByteErrLog_contingency[i][j];
			}
		
		List<ContinProbResult> continProbList_maxReadByte_exitstatus_log = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_totalReadByte_exitstatus_log = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_maxWrittenByte_exitstatus_log = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_totalWrittenByte_exitstatus_log = new ArrayList<ContinProbResult>();
		
		long size = 0;
		for(int i = 0;i<10;i++)
			for(int j = 0;j<numExitStatus;j++)
			{
				double prob = maxReadByteErrLog_contingency[i][j]/maxReadByteErrLog_contingency_i[i];
				size = (long)maxReadByteErrLog[i][j];
				ContinProbResult r = new ContinProbResult("maxReadBytes", "EXIT_STATUS", index2MaxBytesString_log(i), exitStatusValues[j], prob, size);
				continProbList_maxReadByte_exitstatus_log.add(r);
				
				prob = totalReadByteErrLog_contingency[i][j]/totalReadByteErrLog_contingency_i[i];
				size = (long)totalReadByteErrLog[i][j];
				r = new ContinProbResult("totalReadBytes", "EXIT_STATUS", index2TotalBytesString_log(i), exitStatusValues[j], prob, size);
				continProbList_totalReadByte_exitstatus_log.add(r);
				
				prob = maxWrittenByteErrLog_contingency[i][j]/maxWrittenByteErrLog_contingency_i[i];
				size = (long)maxWrittenByteErrLog[i][j];
				r = new ContinProbResult("maxWrittenBytes", "EXIT_STATUS", index2MaxBytesString_log(i), exitStatusValues[j], prob, size);
				continProbList_maxWrittenByte_exitstatus_log.add(r);
				
				prob = totalWrittenByteErrLog_contingency[i][j]/totalWrittenByteErrLog_contingency_i[i];
				size = (long)totalWrittenByteErrLog[i][j];
				r = new ContinProbResult("totalWrittenBytes", "EXIT_STATUS", index2TotalBytesString_log(i), exitStatusValues[j], prob, size);
				continProbList_totalWrittenByte_exitstatus_log.add(r);
			}
		
		System.out.println("Sorting ....");
		sortingMode = 0;
		Collections.sort(continProbList_maxReadByte_exitstatus_log);
		Collections.sort(continProbList_totalReadByte_exitstatus_log);
		Collections.sort(continProbList_maxWrittenByte_exitstatus_log);
		Collections.sort(continProbList_totalWrittenByte_exitstatus_log);
		
		System.out.println("writing log-scale results....");
		PVFile.print2File(continProbList_maxReadByte_exitstatus_log, outputDir+"/continProbList_maxReadByte_exitstatus_log.prb");
		PVFile.print2File(continProbList_totalReadByte_exitstatus_log, outputDir+"/continProbList_totalReadByte_exitstatus_log.prb");
		PVFile.print2File(continProbList_maxWrittenByte_exitstatus_log, outputDir+"/continProbList_maxWrittenByte_exitstatus_log.prb");
		PVFile.print2File(continProbList_totalWrittenByte_exitstatus_log, outputDir+"/continProbList_totalWrittenByte_exitstatus_log.prb");
		
		sortingMode = 1;
		Collections.sort(continProbList_maxReadByte_exitstatus_log);
		Collections.sort(continProbList_totalReadByte_exitstatus_log);
		Collections.sort(continProbList_maxWrittenByte_exitstatus_log);
		Collections.sort(continProbList_totalWrittenByte_exitstatus_log);
		
		System.out.println("writing log-scale results....");
		PVFile.print2File(continProbList_maxReadByte_exitstatus_log, outputDir+"/continProbList_maxReadByte_exitstatus_log2.prb");
		PVFile.print2File(continProbList_totalReadByte_exitstatus_log, outputDir+"/continProbList_totalReadByte_exitstatus_log2.prb");
		PVFile.print2File(continProbList_maxWrittenByte_exitstatus_log, outputDir+"/continProbList_maxWrittenByte_exitstatus_log2.prb");
		PVFile.print2File(continProbList_totalWrittenByte_exitstatus_log, outputDir+"/continProbList_totalWrittenByte_exitstatus_log2.prb");
		
		System.out.println("Computing aggregated probability for exit status -> x");
		
		List<ContinProbResult> continProbList_exitstatus_maxReadBytes_log = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_exitstatus_totalReadBytes_log = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_exitstatus_maxWrittenBytes_log = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_exitstatus_totalWrittenBytes_log = new ArrayList<ContinProbResult>();
		
		for(int i = 0;i<10;i++)
			for(int j = 0;j<numExitStatus;j++)
			{
				double prob = maxReadByteErrLog_contingency[i][j]/maxReadByteErrLog_contingency_j[j];
				size = (long)maxReadByteErrLog[i][j];
				ContinProbResult r = new ContinProbResult("EXIT_STATUS", "maxReadBytes", index2MaxBytesString_log(i), exitStatusValues[j], prob, size);
				continProbList_exitstatus_maxReadBytes_log.add(r);
				
				prob = totalReadByteErrLog_contingency[i][j]/totalReadByteErrLog_contingency_j[j];
				size = (long)totalReadByteErrLog[i][j];
				r = new ContinProbResult("EXIT_STATUS", "totalReadBytes", index2TotalBytesString_log(i), exitStatusValues[j], prob, size);
				continProbList_exitstatus_totalReadBytes_log.add(r);
				
				prob = maxWrittenByteErrLog_contingency[i][j]/maxWrittenByteErrLog_contingency_j[j];
				size = (long)maxWrittenByteErrLog[i][j];
				r = new ContinProbResult("EXIT_STATUS", "maxWrittenBytes", index2MaxBytesString_log(i), exitStatusValues[j], prob, size);
				continProbList_exitstatus_maxWrittenBytes_log.add(r);
				
				prob = totalWrittenByteErrLog_contingency[i][j]/totalWrittenByteErrLog_contingency_j[j];
				size = (long)totalWrittenByteErrLog[i][j];
				r = new ContinProbResult("EXIT_STATUS", "totalWrittenBytes", index2TotalBytesString_log(i), exitStatusValues[j], prob, size);
				continProbList_exitstatus_totalWrittenBytes_log.add(r);
			}
		
		System.out.println("Sorting ....");
		sortingMode = 0;
		Collections.sort(continProbList_exitstatus_maxReadBytes_log);
		Collections.sort(continProbList_exitstatus_totalReadBytes_log);
		Collections.sort(continProbList_exitstatus_maxWrittenBytes_log);
		Collections.sort(continProbList_exitstatus_totalWrittenBytes_log);
		
		System.out.println("writing log-scale results....");
		PVFile.print2File(continProbList_exitstatus_maxReadBytes_log, outputDir+"/continProbList_exitstatus_maxReadBytes_log.prb");
		PVFile.print2File(continProbList_exitstatus_totalReadBytes_log, outputDir+"/continProbList_exitstatus_totalReadBytes_log.prb");
		PVFile.print2File(continProbList_exitstatus_maxWrittenBytes_log, outputDir+"/continProbList_exitstatus_maxWrittenBytes_log.prb");
		PVFile.print2File(continProbList_exitstatus_totalWrittenBytes_log, outputDir+"/continProbList_exitstatus_totalWrittenBytes_log.prb");
		
		sortingMode = 1;
		Collections.sort(continProbList_exitstatus_maxReadBytes_log);
		Collections.sort(continProbList_exitstatus_totalReadBytes_log);
		Collections.sort(continProbList_exitstatus_maxWrittenBytes_log);
		Collections.sort(continProbList_exitstatus_totalWrittenBytes_log);
		
		System.out.println("writing log-scale results....");
		PVFile.print2File(continProbList_exitstatus_maxReadBytes_log, outputDir+"/continProbList_exitstatus_maxReadBytes_log2.prb");
		PVFile.print2File(continProbList_exitstatus_totalReadBytes_log, outputDir+"/continProbList_exitstatus_totalReadBytes_log2.prb");
		PVFile.print2File(continProbList_exitstatus_maxWrittenBytes_log, outputDir+"/continProbList_exitstatus_maxWrittenBytes_log2.prb");
		PVFile.print2File(continProbList_exitstatus_totalWrittenBytes_log, outputDir+"/continProbList_exitstatus_totalWrittenBytes_log2.prb");
		
	}
	
	static HashMap<String, Integer> loadIndex(String fieldFileName, String[] exitStatusValues)
	{
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		List<String> lineList = PVFile.readFile(fieldFileName);
		Iterator<String> iter = lineList.iterator();
		int i = 0;
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.startsWith("#"))
				continue;
			String[] s = line.split("\\s");
			String key = s[0];
			result.put(key, i);
			exitStatusValues[i] = key;
			i++;
		}
		return result;
	}
	
	static String index2MaxBytesString_log(int i)
	{
		switch(i)
		{
		case 0: 
			return "[0,32MB)";
		case 1:
			return "[32MB,64MB)";
		case 2:
			return "[64MB,128MB)";
		case 3:
			return "[128MB,256MB)";
		case 4:
			return "[256MB,512MB)";
		case 5:
			return "[512MB,1GB)";
		case 6:
			return "[1GB,2GB)";
		case 7:
			return "[2GB,4GB)";
		case 8:
			return "[4GB,8GB)";
		case 9:
			return "[8GB,16GB)";
		case 10:
			return "[16GB,32GB)";
		case 11:
			return "[32GB,64GB)";
		case 12:
			return "[64GB,128GB)";
		case 13:
			return "[128GB,256GB)";
		case 14:
			return "[256GB,512GB)";
		case 15:
			return "[512GB,...)";
		}
		return "null";
	}

	static String index2TotalBytesString_log(int i)
	{
		switch(i)
		{
		case 0: 
			return "[0,512MB)";
		case 1:
			return "[512MB,1GB)";
		case 2:
			return "[1GB,2GB)";
		case 3:
			return "[2GB,4GB)";
		case 4:
			return "[4GB,8GB)";
		case 5:
			return "[8GB,16GB)";
		case 6:
			return "[16GB,32GB)";
		case 7:
			return "[32GB,64GB)";
		case 8:
			return "[64GB,128GB)";
		case 9:
			return "[128GB,256GB)";
		case 10:
			return "[256GB,512GB)";
		case 11:
			return "[512GB,1TB)";
		case 12:
			return "[1TB,2TB)";
		case 13:
			return "[2TB,4TB)";
		case 14:
			return "[4TB,8TB)";
		case 15:
			return "[8TB,...)";
		}
		return "null";
	}
	
}
