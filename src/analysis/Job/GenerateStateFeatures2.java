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

public class GenerateStateFeatures2 {

	public static int sortingMode = 0; //0:probability; 1:margin of error
	
	//public static int exit_status_index = 19;
	public static int exit_status_index = 57;
	//public static int exit_status_index = 59;
	public static double v1d6 = 1/6.0;
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
		if(args.length<6)
		{
			System.out.println("Usage: java GenerateStateFeatures2 [basicSchema] [fullSchemaDir] [schemaExt] [logDir] [logExt] [outputDir] [fields....]");
			System.out.println("Example: java GenerateStateFeatures2 /home/fti/Catalog-project/miralog/RAS-Job/Job/basicSchema.txt /home/fti/Catalog-project/miralog/fullSchema/fullSchema/withRatio fsr "
					+ "/home/fti/Catalog-project/miralog/RAS-Job/Job csvs /home/fti/Catalog-project/miralog/RAS-Job/Job/featureState"
					+ " other_fields ....");
			System.exit(0);
		}
		
		String schemaPath = args[0];
		
		String fullSchemaDir = args[1];
		String schemaExt = args[2];
		
		String logDir = args[3];
		String logExt = args[4];
		
		String outputDir = args[5];
	
		int wallTimeIndex = 12;
		int runTimeIndex = 13;
		int corehourIndex = 25;
		int numConsecutiveTaskIndex = 40;
		int numMultilocationtaskIndex = 41;
		
		
//		int[] wallTimeSumLog = new int[10];
//		int[] runTimeSumLog = new int[10];
//		int[] corehourSumLog = new int[10];
//		int[] numConsecutiveTasksSumLog = new int[10];
//		int[] numMultilocationTasksSumLog = new int[10];
//		
//		int[] wallTimeSumLin = new int[25];
//		int[] runTimeSumLin = new int[25];
//		int[] corehourSumLin = new int[101];
//		int[] numConsecutiveTasksSumLin = new int[12];
//		int[] numMultilocationTasksSumLin = new int[12];		
		
		//log scale
		
		int numExitStatus = 24;
		
		double[][] wallTimeErrLog = new double[10][numExitStatus]; //min: 1/6 --> 1/6, 1/6*2, 1/6*4, 1/6*8, ...., 1/6*512
		double[][] runTimeErrLog = new double[10][numExitStatus]; //min: 1/6 --> 1/6, 1/6*2, 1/6*4, 1/6*8, ...., 1/6*512
		double[][] corehourErrLog = new double[10][numExitStatus]; //1000, 2000, 4000, 8000, ....., 512,000
		
		double[][] numConsecutiveTasksLog = new double[10][numExitStatus]; //0, 1-10, 11-20, 21-40, ....
		double[][] numMultilocationTasksLog = new double[10][numExitStatus]; //0, 1-10, 11-20, 21-40, ....
		
		double[] runTime_CorehourSum = new double[10];
		int[] runTime_jobCount = new int[10];
		int[] runTime_sumCoreCount = new int[10];
		double[] runTime_sumExeTime = new double[10];
		
		//linear scale
		double[][] wallTimeErrLin = new double[25][numExitStatus]; //0-1, 1-2, 2-3, 3-4, 4-5, ....., 24+ 
		int wallTimeUnit = 3600;
		double[][] runTimeErrLin = new double[25][numExitStatus]; //0-1, 1-2, 2-3, 3-4, 4-5, ....., 24+
		int runTimeUnit = 3600;
		double[][] corehourErrLin = new double[101][numExitStatus]; //1000, 2000, 3000, 4000, ...., 100,000, 100,000+
		int corehourUnit = 1000;
		double[][] numConsecutiveTasksLin = new double[12][numExitStatus]; //0, 1-20, 21-40, 41-60, ....., 181-200, 200+
		int numConTaskUnit = 20;
		double[][] numMultilocationTasksLin = new double[12][numExitStatus]; //0, 1-20, 21-40, 41-60, ....., 181-200, 200+
		int numMulTaskUnit = 20;
		
		double[] exitStatusWasteCoreHours = new double[numExitStatus];
		
		List<String> inputFileList = PVFile.getFiles(logDir, logExt);
		Collections.sort(inputFileList);
		
		String[] exitStatusValues = new String[numExitStatus];
		HashMap<String, Integer> exitIndexMap = loadIndex(fullSchemaDir+"/EXIT_SIGNAL.fsr", exitStatusValues);
		//HashMap<String, Integer> exitIndexMap = loadIndex(fullSchemaDir+"/RAS_MSG_ID.fsr", exitStatusValues);
		
		Iterator<String> iter = inputFileList.iterator();
		
		double log2 = Math.log(2);
		
		while(iter.hasNext())
		{
			String file = iter.next();
			List<String> lineList = PVFile.readFile(logDir+"/"+file);
			
			System.out.println("Processing file: "+logDir+"/"+file);
			Iterator<String> iter2 = lineList.iterator();
			iter2.next(); //remove the first meta data line
			while(iter2.hasNext())
			{
				String line = iter2.next();
				if(line.startsWith("#"))
					continue;
				String[] data = RecordSplitter.partition(line);
				
				double wallTime = Double.parseDouble(data[wallTimeIndex])/3600;
				double runTime = Double.parseDouble(data[runTimeIndex])/3600;
				double corehours = Double.parseDouble(data[corehourIndex]);
				int numConsecutiveTasks = Integer.parseInt(data[numConsecutiveTaskIndex]);
				int numMultilocationTasks = Integer.parseInt(data[numMultilocationtaskIndex]);
				
				String exit_status = data[exit_status_index]; 
				
				//log scale
				int wallTimeResultIndex_log = (int)(Math.log(wallTime*6)/log2+1);
				if(wallTimeResultIndex_log>9)
					wallTimeResultIndex_log = 9;
				if(wallTimeResultIndex_log<0)
					wallTimeResultIndex_log = 0;
				//wallTimeSumLog[wallTimeResultIndex_log]++;
				int runTimeResultIndex_log = (int)(Math.log(runTime*6)/log2+1);
				if(runTimeResultIndex_log>9)
					runTimeResultIndex_log = 9;
				if(runTimeResultIndex_log<0)
					runTimeResultIndex_log = 0;

			
				if(exit_status.equals("999"))
				{
					int coresUsed = (int)Double.parseDouble(data[16]);
					runTime_CorehourSum[runTimeResultIndex_log] += corehours;
					runTime_jobCount[runTimeResultIndex_log] ++;
					runTime_sumCoreCount[runTimeResultIndex_log] += coresUsed;
					runTime_sumExeTime[runTimeResultIndex_log] += runTime;									
				}
				
				//runTimeSumLog[runTimeResultIndex_log]++;
				int corehourResultIndex_log = (int)(Math.log(corehours/1000)/log2+1); 
				if(corehourResultIndex_log>9)
					corehourResultIndex_log = 9;
				if(corehourResultIndex_log<0)
					corehourResultIndex_log = 0;
				//corehourSumLog[corehourResultIndex_log]++;
				
				int numConsecutiveTasksResultIndex_log = computeIndex_log_numTasks(numConsecutiveTasks);

				//numConsecutiveTasksSumLog[numConsecutiveTasksResultIndex_log]++;

				int numMultilocationTasksResultIndex_log = computeIndex_log_numTasks(numMultilocationTasks);
				
				
				//numMultilocationTasksSumLog[numMultilocationTasksResultIndex_log]++;
				
				//linear scale
				int wallTimeResultIndex_lin =(int)(wallTime); 
				if(wallTimeResultIndex_lin>24)
					wallTimeResultIndex_lin = 24;
				//wallTimeSumLin[wallTimeResultIndex_lin]++;
				int runTimeResultIndex_lin = (int)(runTime);
				if(runTimeResultIndex_lin>24)
					runTimeResultIndex_lin = 24;
				if(runTimeResultIndex_lin<0)
					runTimeResultIndex_lin = 0;
				//runTimeSumLin[runTimeResultIndex_lin]++;
				int corehoursResultIndex_lin = (int)(corehours/1000);
				if(corehoursResultIndex_lin>100)
					corehoursResultIndex_lin = 100;
				if(corehoursResultIndex_lin<0)
					corehoursResultIndex_lin = 0;
				//wallTimeSumLin[corehoursResultIndex_lin]++;
				
				int numConsecutiveTasksResultIndex_lin = 0;
				if(numConsecutiveTasks == 0)
					numConsecutiveTasksResultIndex_lin = 0;
				else
					numConsecutiveTasksResultIndex_lin = (int)(numConsecutiveTasks/20)+1;
				if(numConsecutiveTasksResultIndex_lin>11)
					numConsecutiveTasksResultIndex_lin = 11;
				//numConsecutiveTasksSumLin[numConsecutiveTasksResultIndex_lin]++;
				
				int numMultilocationTasksResultIndex_lin = 0;
				if(numMultilocationTasks == 0)
					numMultilocationTasksResultIndex_lin = 0;
				else
					numMultilocationTasksResultIndex_lin = (int)(numMultilocationTasks/20)+1;
				if(numMultilocationTasksResultIndex_lin>11)
					numMultilocationTasksResultIndex_lin = 11;
				//numMultilocationTasksSumLin[numMultilocationTasksResultIndex_lin]++;
				
				int statusIndex = exitIndexMap.get(exit_status);
				//start calculation....
				wallTimeErrLog[wallTimeResultIndex_log][statusIndex]++;
				runTimeErrLog[runTimeResultIndex_log][statusIndex]++;
				corehourErrLog[corehourResultIndex_log][statusIndex]++;
				if(numConsecutiveTasksResultIndex_log>=0)
					numConsecutiveTasksLog[numConsecutiveTasksResultIndex_log][statusIndex]++;
				if(numMultilocationTasksResultIndex_log>=0)
					numMultilocationTasksLog[numMultilocationTasksResultIndex_log][statusIndex]++;
				
				exitStatusWasteCoreHours[statusIndex] += corehours;
				
				wallTimeErrLin[wallTimeResultIndex_lin][statusIndex]++;
				runTimeErrLin[runTimeResultIndex_lin][statusIndex]++;
				corehourErrLin[corehoursResultIndex_lin][statusIndex]++;
				numConsecutiveTasksLin[numConsecutiveTasksResultIndex_lin][statusIndex]++;
				numMultilocationTasksLin[numMultilocationTasksResultIndex_lin][statusIndex]++;
				
//				if(corehours<=1000 && corehourResultIndex_log != corehoursResultIndex_lin)
//					System.out.println();
				
				totalCount++;
				
			}
		}
		
		for(int i = 0;i<10;i++)
		{
			System.out.println(i+" "+runTime_jobCount[i]+" & "+runTime_CorehourSum[i]+" & "+(1.0*runTime_sumCoreCount[i]/runTime_jobCount[i])+" & "+(1.0*runTime_sumExeTime[i]/runTime_jobCount[i]));
		}
		
		List<String> exitWasteCoreHoursList = new ArrayList<String>();
		String s = "", ss = "";
		for(int i = 0;i<numExitStatus;i++)
		{
			s+=" "+exitStatusValues[i];
			ss+=" "+exitStatusWasteCoreHours[i];
		}
		s = s.trim();
		exitWasteCoreHoursList.add(s);
		exitWasteCoreHoursList.add(ss);
		
		PVFile.print2File(exitWasteCoreHoursList, outputDir+"/exitWasteCoreHours.txt");
		System.out.println("exit waste core hours output file: "+outputDir+"/exitWasteCoreHours.txt");
		
		printMatrix("[10m,20m),[20m,40m),...., [42.67h,85.3h], [85.3h,∞]", 10, numExitStatus, exitStatusValues, wallTimeErrLog, outputDir+"/wallTimeErrLog.fsc");
		printMatrix("[10m,20m),[20m,40m), · · · , [42.67h,85.3h], [85.3h,∞]", 10, numExitStatus, exitStatusValues, runTimeErrLog, outputDir+"/runTimeErrLog.fsc");
		printMatrix("[0,1000), [1000,2000),[2000,4000), · · · , [512,000, ∞)", 10, numExitStatus, exitStatusValues, corehourErrLog, outputDir+"/corehourErrLog.fsc");
		printMatrix("[1,10], [11,20], [21,80],· · · , [641,1320], [1320,∞)", 10, numExitStatus, exitStatusValues, numConsecutiveTasksLog, outputDir+"/numConsecutiveTasksLog.fsc");
		printMatrix("[1,10], [11,20], [21,80],· · · , [641,1320], [1320,∞)", 10, numExitStatus, exitStatusValues, numMultilocationTasksLog, outputDir+"/numMultilocationTasksLog.fsc");
		
		printMatrix("[0,1h), [1h,2h), [2h,3h), · · · , [23h,24h), [24h,∞)", 25, numExitStatus, exitStatusValues, wallTimeErrLin, outputDir+"/wallTimeErrLin.fsc");
		printMatrix("[0,1h), [1h,2h), [2h,3h), · · · , [23h,24h), [24h,∞)", 25, numExitStatus, exitStatusValues, runTimeErrLin, outputDir+"/runTimeErrLin.fsc");
		printMatrix("[0,1000),[1000,2000), · · · , [9000,10000), [10000,∞)", 101, numExitStatus, exitStatusValues, corehourErrLin, outputDir+"/corehourErrLin.fsc");
		printMatrix("[0],[1,20],[21,40],[41,60], · · · , [181,200], [200,∞)", 12, numExitStatus, exitStatusValues, numConsecutiveTasksLin, outputDir+"/numConsecutiveTasksLin.fsc");
		printMatrix("[0],[1,20],[21,40],[41,60], · · · , [181,200], [200,∞)", 12, numExitStatus, exitStatusValues, numMultilocationTasksLin, outputDir+"/numMultilocationTasksLin.fsc");
		
		//Compute the contingency table
		double[][] wallTimeErrLog_contingency = new double[10][numExitStatus]; //min: 1/6 --> 1/6, 1/6*2, 1/6*4, 1/6*8, ...., 1/6*512
		double[][] runTimeErrLog_contingency = new double[10][numExitStatus]; //min: 1/6 --> 1/6, 1/6*2, 1/6*4, 1/6*8, ...., 1/6*512
		double[][] corehourErrLog_contingency = new double[10][numExitStatus]; //1000, 2000, 4000, 8000, ....., 512,000
		
		double[][] numConsecutiveTasksLog_contingency = new double[10][numExitStatus]; //1-10, 11-20, 21-40, 41-80, 81-160, 161-320, 321-640, 641-1320, 1320+.....
		double[][] numMultilocationTasksLog_contingency = new double[10][numExitStatus]; //1-10, 11-20, 21-40, 41-80, 81-160, 161-320, 321-640, 641-1320, 1320+.....
		
		//linear scale
		double[][] wallTimeErrLin_contingency = new double[25][numExitStatus]; //0-1, 1-2, 2-3, 3-4, 4-5, ....., 24+ 
		double[][] runTimeErrLin_contingency = new double[25][numExitStatus]; //0-1, 1-2, 2-3, 3-4, 4-5, ....., 24+
		double[][] corehourErrLin_contingency = new double[101][numExitStatus]; //1000, 2000, 3000, 4000, ...., 100,000, 100,000+
		double[][] numConsecutiveTasksLin_contingency = new double[12][numExitStatus]; //0, 1-20, 21-40, 41-60, ....., 181-200, 200+
		double[][] numMultilocationTasksLin_contingency = new double[12][numExitStatus]; //0, 1-20, 21-40, 41-60, ....., 181-200, 200+
		
		for(int i = 0;i<10;i++)
		{
			for(int j = 0;j<numExitStatus;j++)
			{
				wallTimeErrLog_contingency[i][j] = wallTimeErrLog[i][j]/totalCount;
				runTimeErrLog_contingency[i][j] = runTimeErrLog[i][j]/totalCount;
				corehourErrLog_contingency[i][j] = corehourErrLog[i][j]/totalCount;
				numConsecutiveTasksLog_contingency[i][j] = numConsecutiveTasksLog[i][j]/totalCount;
				numMultilocationTasksLog_contingency[i][j] = numMultilocationTasksLog[i][j]/totalCount;
				
				//System.out.print(wallTimeErrLog_contingency[i][j]+" ");
			}
			//System.out.println();
		}
		
		for(int i = 0;i<25;i++)
			for(int j = 0;j<numExitStatus;j++)
			{
				wallTimeErrLin_contingency[i][j] = wallTimeErrLin[i][j]/totalCount;
				runTimeErrLin_contingency[i][j] = runTimeErrLin[i][j]/totalCount;
			}
		
		for(int i = 0;i<101;i++)
			for(int j = 0;j<numExitStatus;j++)
			{
				corehourErrLin_contingency[i][j] = corehourErrLin[i][j]/totalCount;
			}
		
		for(int i = 0;i<12;i++)
			for(int j = 0;j<numExitStatus;j++)
			{
				numConsecutiveTasksLin_contingency[i][j] = numConsecutiveTasksLin[i][j]/totalCount;
				numMultilocationTasksLin_contingency[i][j] = numMultilocationTasksLin[i][j]/totalCount;
			}

		System.out.println("Linear Computing aggregated probability for x-> exit_status....");
		
		double[] wallTimeErrLin_contingency_i = new double[25];
		double[] wallTimeErrLin_contingency_j = new double[numExitStatus];
		double[] runTimeErrLin_contingency_i = new double[25];
		double[] runTimeErrLin_contingency_j = new double[numExitStatus];
		double[] corehourErrLin_contingency_i = new double[101];
		double[] corehourErrLin_contingency_j = new double[numExitStatus];
		double[] numConsecutiveTasksLin_contingency_i = new double[12];
		double[] numConsecutiveTasksLin_contingency_j = new double[numExitStatus];
		double[] numMultilocationTasksLin_contingency_i = new double[12];
		double[] numMultilocationTasksLin_contingency_j = new double[numExitStatus];
		
		for(int i = 0;i<25;i++)
			for(int j = 0;j<numExitStatus;j++)
			{
				wallTimeErrLin_contingency_i[i] += wallTimeErrLin_contingency[i][j];
				runTimeErrLin_contingency_i[i] += runTimeErrLin_contingency[i][j];
			}		
		for(int j = 0;j<numExitStatus;j++)
			for(int i = 0;i<25;i++)
			{
				wallTimeErrLin_contingency_j[j] += wallTimeErrLin_contingency[i][j];
				runTimeErrLin_contingency_j[j] += runTimeErrLin_contingency[i][j];
			}	
		
		
		for(int i = 0;i<101;i++)
			for(int j = 0;j<numExitStatus;j++)
				corehourErrLin_contingency_i[i] += corehourErrLin_contingency[i][j];
		for(int j = 0;j<numExitStatus;j++)
			for(int i = 0;i<101;i++)
				corehourErrLin_contingency_j[j] += corehourErrLin_contingency[i][j];
		
		
		for(int i = 0;i<12;i++)
			for(int j = 0;j<numExitStatus;j++)
			{
				numConsecutiveTasksLin_contingency_i[i] += numConsecutiveTasksLin_contingency[i][j];
				numMultilocationTasksLin_contingency_i[i] += numMultilocationTasksLin_contingency[i][j];		
			}
		for(int j = 0;j<numExitStatus;j++)
			for(int i = 0;i<12;i++)
			{
				numConsecutiveTasksLin_contingency_j[j] += numConsecutiveTasksLin_contingency[i][j];
				numMultilocationTasksLin_contingency_j[j] += numMultilocationTasksLin_contingency[i][j];		
			}		
		
		List<ContinProbResult> continProbList_walltime_exitstatus_lin = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_runtime_exitstatus_lin = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_corehours_exitstatus_lin = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_numConTasks_exitstatus_lin = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_numMulTasks_exitstatus_lin = new ArrayList<ContinProbResult>();
		
		long size = 0;
		for(int i = 0;i<25;i++)
			for(int j = 0;j<numExitStatus;j++)
			{
				double prob = wallTimeErrLin_contingency[i][j]/wallTimeErrLin_contingency_i[i];
				size = (long)wallTimeErrLin[i][j];
				ContinProbResult r = new ContinProbResult("WALLTIME", "EXIT_STATUS", index2TimeString_lin(i), exitStatusValues[j], prob, size);
				continProbList_walltime_exitstatus_lin.add(r);
				
				prob = runTimeErrLin_contingency[i][j]/runTimeErrLin_contingency_i[i];
				size = (long)runTimeErrLin[i][j];
				r = new ContinProbResult("RUNTIME", "EXIT_STATUS", index2TimeString_lin(i), exitStatusValues[j], prob, size);
				continProbList_runtime_exitstatus_lin.add(r);
			}
		
		for(int i = 0;i<101;i++)
			for(int j = 0;j<numExitStatus;j++)
			{
				double prob = corehourErrLin_contingency[i][j]/corehourErrLin_contingency_i[i];
				size = (long)corehourErrLin[i][j];
				ContinProbResult r = new ContinProbResult("COREHOURS", "EXIT_STATUS", index2CorehoursString_lin(i), exitStatusValues[j], prob, size);
				continProbList_corehours_exitstatus_lin.add(r);				
			}

		for(int i = 0;i<12;i++)
			for(int j = 0;j<numExitStatus;j++)
			{
				double prob = numConsecutiveTasksLin_contingency[i][j]/numConsecutiveTasksLin_contingency_i[i];
				size = (long)numConsecutiveTasksLin[i][j];
				ContinProbResult r = new ContinProbResult("NUMCONTASKS", "EXIT_STATUS", index2NumTasksString_lin(i), exitStatusValues[j], prob, size);
				continProbList_numConTasks_exitstatus_lin.add(r);

				prob = numMultilocationTasksLin_contingency[i][j]/numMultilocationTasksLin_contingency_i[i];
				size = (long)numMultilocationTasksLin[i][j];
				r = new ContinProbResult("NUMMULTASKS", "EXIT_STATUS", index2NumTasksString_lin(i), exitStatusValues[j], prob, size);
				continProbList_numMulTasks_exitstatus_lin.add(r);				
			}

		System.out.println("Sorting ....");
		sortingMode = 0;
		Collections.sort(continProbList_walltime_exitstatus_lin);
		Collections.sort(continProbList_runtime_exitstatus_lin);
		Collections.sort(continProbList_corehours_exitstatus_lin);
		Collections.sort(continProbList_numConTasks_exitstatus_lin);
		Collections.sort(continProbList_numMulTasks_exitstatus_lin);
		
		System.out.println("writing lin-scale results....");
		PVFile.print2File(continProbList_walltime_exitstatus_lin, outputDir+"/walltime_exitstatus_lin.prb");
		PVFile.print2File(continProbList_runtime_exitstatus_lin, outputDir+"/runtime_exitstatus_lin.prb");
		PVFile.print2File(continProbList_corehours_exitstatus_lin, outputDir+"/corehours_exitstatus_lin.prb");
		PVFile.print2File(continProbList_numConTasks_exitstatus_lin, outputDir+"/numConTasks_exitstatus_lin.prb");
		PVFile.print2File(continProbList_numMulTasks_exitstatus_lin, outputDir+"/numMulTasks_exitstatus_lin.prb");
		
		sortingMode = 1;
		Collections.sort(continProbList_walltime_exitstatus_lin);
		Collections.sort(continProbList_runtime_exitstatus_lin);
		Collections.sort(continProbList_corehours_exitstatus_lin);
		Collections.sort(continProbList_numConTasks_exitstatus_lin);
		Collections.sort(continProbList_numMulTasks_exitstatus_lin);
		
		System.out.println("writing lin-scale results....");
		PVFile.print2File(continProbList_walltime_exitstatus_lin, outputDir+"/walltime_exitstatus_lin2.prb");
		PVFile.print2File(continProbList_runtime_exitstatus_lin, outputDir+"/runtime_exitstatus_lin2.prb");
		PVFile.print2File(continProbList_corehours_exitstatus_lin, outputDir+"/corehours_exitstatus_lin2.prb");
		PVFile.print2File(continProbList_numConTasks_exitstatus_lin, outputDir+"/numConTasks_exitstatus_lin2.prb");
		PVFile.print2File(continProbList_numMulTasks_exitstatus_lin, outputDir+"/numMulTasks_exitstatus_lin2.prb");
		
		System.out.println("Linear Computing aggregated probability for exit status -> x");
		
		List<ContinProbResult> continProbList_exitstatus_walltime_lin = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_exitstatus_runtime_lin = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_exitstatus_corehours_lin = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_exitstatus_numConTasks_lin = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_exitstatus_numMulTasks_lin = new ArrayList<ContinProbResult>();
		
		for(int i = 0;i<25;i++)
			for(int j = 0;j<numExitStatus;j++)
			{
				double prob = wallTimeErrLin_contingency[i][j]/wallTimeErrLin_contingency_j[j];
				size = (long)wallTimeErrLin[i][j];
				ContinProbResult r = new ContinProbResult("EXIT_STATUS", "WALLTIME", index2TimeString_lin(i), exitStatusValues[j], prob, size);
				continProbList_exitstatus_walltime_lin.add(r);
				
				prob = runTimeErrLin_contingency[i][j]/runTimeErrLin_contingency_j[j];
				size = (long)runTimeErrLin[i][j];
				r = new ContinProbResult("EXIT_STATUS", "RUNTIME", index2TimeString_lin(i), exitStatusValues[j], prob, size);
				continProbList_exitstatus_runtime_lin.add(r);
			}
		
		for(int i = 0;i<101;i++)
			for(int j = 0;j<numExitStatus;j++)
			{
				double prob = corehourErrLin_contingency[i][j]/corehourErrLin_contingency_j[j];
				size = (long)corehourErrLin[i][j];
				ContinProbResult r = new ContinProbResult("EXIT_STATUS", "COREHOURS", index2CorehoursString_lin(i), exitStatusValues[j], prob, size);
				continProbList_exitstatus_corehours_lin.add(r);				
			}

		for(int i = 0;i<12;i++)
			for(int j = 0;j<numExitStatus;j++)
			{
				double prob = numConsecutiveTasksLin_contingency[i][j]/numConsecutiveTasksLin_contingency_j[j];
				size = (long)numConsecutiveTasksLin[i][j];
				ContinProbResult r = new ContinProbResult("EXIT_STATUS", "NUMCONTASKS", index2NumTasksString_lin(i), exitStatusValues[j], prob, size);
				continProbList_exitstatus_numConTasks_lin.add(r);

				prob = numMultilocationTasksLin_contingency[i][j]/numMultilocationTasksLin_contingency_j[j];
				size = (long)numMultilocationTasksLin[i][j];
				r = new ContinProbResult("EXIT_STATUS", "NUMMULTASKS", index2NumTasksString_lin(i), exitStatusValues[j], prob, size);
				continProbList_exitstatus_numMulTasks_lin.add(r);				
			}

		System.out.println("Sorting ....");
		sortingMode = 0;
		Collections.sort(continProbList_exitstatus_walltime_lin);
		Collections.sort(continProbList_exitstatus_runtime_lin);
		Collections.sort(continProbList_exitstatus_corehours_lin);
		Collections.sort(continProbList_exitstatus_numConTasks_lin);
		Collections.sort(continProbList_exitstatus_numMulTasks_lin);
		
		System.out.println("writing lin-scale results....");
		PVFile.print2File(continProbList_exitstatus_walltime_lin, outputDir+"/exitstatus_walltime_lin.prb");
		PVFile.print2File(continProbList_exitstatus_runtime_lin, outputDir+"/exitstatus_runtime_lin.prb");
		PVFile.print2File(continProbList_exitstatus_corehours_lin, outputDir+"/exitstatus_corehours_lin.prb");
		PVFile.print2File(continProbList_exitstatus_numConTasks_lin, outputDir+"/exitstatus_numConTasks_lin.prb");
		PVFile.print2File(continProbList_exitstatus_numMulTasks_lin, outputDir+"/exitstatus_numMulTasks_lin.prb");		

		sortingMode = 1;
		Collections.sort(continProbList_exitstatus_walltime_lin);
		Collections.sort(continProbList_exitstatus_runtime_lin);
		Collections.sort(continProbList_exitstatus_corehours_lin);
		Collections.sort(continProbList_exitstatus_numConTasks_lin);
		Collections.sort(continProbList_exitstatus_numMulTasks_lin);
		
		System.out.println("writing lin-scale results....");
		PVFile.print2File(continProbList_exitstatus_walltime_lin, outputDir+"/exitstatus_walltime_lin2.prb");
		PVFile.print2File(continProbList_exitstatus_runtime_lin, outputDir+"/exitstatus_runtime_lin2.prb");
		PVFile.print2File(continProbList_exitstatus_corehours_lin, outputDir+"/exitstatus_corehours_lin2.prb");
		PVFile.print2File(continProbList_exitstatus_numConTasks_lin, outputDir+"/exitstatus_numConTasks_lin2.prb");
		PVFile.print2File(continProbList_exitstatus_numMulTasks_lin, outputDir+"/exitstatus_numMulTasks_lin2.prb");
		
		System.out.println("Log Computing aggregated probability for x -> exit_status....");
		
		double[] wallTimeErrLog_contingency_i = new double[10];
		double[] wallTimeErrLog_contingency_j = new double[numExitStatus];
		double[] runTimeErrLog_contingency_i = new double[10];
		double[] runTimeErrLog_contingency_j = new double[numExitStatus];
		double[] corehourErrLog_contingency_i = new double[10];
		double[] corehourErrLog_contingency_j = new double[numExitStatus];
		double[] numConsecutiveTasksLog_contingency_i = new double[10];
		double[] numConsecutiveTasksLog_contingency_j = new double[numExitStatus];
		double[] numMultilocationTasksLog_contingency_i = new double[10];
		double[] numMultilocationTasksLog_contingency_j = new double[numExitStatus];
		
		for(int i = 0;i<10;i++)
			for(int j = 0;j<numExitStatus;j++)
			{
				wallTimeErrLog_contingency_i[i] += wallTimeErrLog_contingency[i][j];
				runTimeErrLog_contingency_i[i] += runTimeErrLog_contingency[i][j];
				corehourErrLog_contingency_i[i] += corehourErrLog_contingency[i][j];
				numConsecutiveTasksLog_contingency_i[i] += numConsecutiveTasksLog_contingency[i][j];
				numMultilocationTasksLog_contingency_i[i] += numMultilocationTasksLog_contingency[i][j];
			}
		
		for(int j = 0;j<numExitStatus;j++)
			for(int i = 0;i<10;i++)
			{
				wallTimeErrLog_contingency_j[j] += wallTimeErrLog_contingency[i][j];
				runTimeErrLog_contingency_j[j] += runTimeErrLog_contingency[i][j];
				corehourErrLog_contingency_j[j] += corehourErrLog_contingency[i][j];
				numConsecutiveTasksLog_contingency_j[j] += numConsecutiveTasksLog_contingency[i][j];
				numMultilocationTasksLog_contingency_j[j] += numMultilocationTasksLog_contingency[i][j];
			}
		
		List<ContinProbResult> continProbList_walltime_exitstatus_log = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_runtime_exitstatus_log = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_corehours_exitstatus_log = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_numConTasks_exitstatus_log = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_numMulTasks_exitstatus_log = new ArrayList<ContinProbResult>();
		
		for(int i = 0;i<10;i++)
			for(int j = 0;j<numExitStatus;j++)
			{
				double prob = wallTimeErrLog_contingency[i][j]/wallTimeErrLog_contingency_i[i];
				size = (long)wallTimeErrLog[i][j];
				ContinProbResult r = new ContinProbResult("WALLTIME", "EXIT_STATUS", index2TimeString_log(i), exitStatusValues[j], prob, size);
				continProbList_walltime_exitstatus_log.add(r);
				
				prob = runTimeErrLog_contingency[i][j]/runTimeErrLog_contingency_i[i];
				size = (long)runTimeErrLog[i][j];
				r = new ContinProbResult("RUNTIME", "EXIT_STATUS", index2TimeString_log(i), exitStatusValues[j], prob, size);
				continProbList_runtime_exitstatus_log.add(r);
				
				prob = corehourErrLog_contingency[i][j]/corehourErrLog_contingency_i[i];
				size = (long)corehourErrLog[i][j];
				r = new ContinProbResult("COREHOURS", "EXIT_STATUS", index2CorehoursString_log(i), exitStatusValues[j], prob, size);
				continProbList_corehours_exitstatus_log.add(r);
				
				prob = numConsecutiveTasksLog_contingency[i][j]/numConsecutiveTasksLog_contingency_i[i];
				size = (long)numConsecutiveTasksLog[i][j];
				r = new ContinProbResult("NUMCONTASKS", "EXIT_STATUS", index2NumTasksString_log(i), exitStatusValues[j], prob, size);
				continProbList_numConTasks_exitstatus_log.add(r);

				prob = numMultilocationTasksLog_contingency[i][j]/numMultilocationTasksLog_contingency_i[i];
				size= (long)numMultilocationTasksLog[i][j];
				r = new ContinProbResult("NUMMULTASKS", "EXIT_STATUS", index2NumTasksString_log(i), exitStatusValues[j], prob, size);
				continProbList_numMulTasks_exitstatus_log.add(r);
			}
		
		System.out.println("Sorting ....");
		sortingMode = 0;
		Collections.sort(continProbList_walltime_exitstatus_log);
		Collections.sort(continProbList_runtime_exitstatus_log);
		Collections.sort(continProbList_corehours_exitstatus_log);
		Collections.sort(continProbList_numConTasks_exitstatus_log);
		Collections.sort(continProbList_numMulTasks_exitstatus_log);
		
		System.out.println("writing log-scale results....");
		PVFile.print2File(continProbList_walltime_exitstatus_log, outputDir+"/walltime_exitstatus_log.prb");
		PVFile.print2File(continProbList_runtime_exitstatus_log, outputDir+"/runtime_exitstatus_log.prb");
		PVFile.print2File(continProbList_corehours_exitstatus_log, outputDir+"/corehours_exitstatus_log.prb");
		PVFile.print2File(continProbList_numConTasks_exitstatus_log, outputDir+"/numConTasks_exitstatus_log.prb");
		PVFile.print2File(continProbList_numMulTasks_exitstatus_log, outputDir+"/numMulTasks_exitstatus_log.prb");
		
		sortingMode = 1;
		Collections.sort(continProbList_walltime_exitstatus_log);
		Collections.sort(continProbList_runtime_exitstatus_log);
		Collections.sort(continProbList_corehours_exitstatus_log);
		Collections.sort(continProbList_numConTasks_exitstatus_log);
		Collections.sort(continProbList_numMulTasks_exitstatus_log);
		
		System.out.println("writing log-scale results....");
		PVFile.print2File(continProbList_walltime_exitstatus_log, outputDir+"/walltime_exitstatus_log2.prb");
		PVFile.print2File(continProbList_runtime_exitstatus_log, outputDir+"/runtime_exitstatus_log2.prb");
		PVFile.print2File(continProbList_corehours_exitstatus_log, outputDir+"/corehours_exitstatus_log2.prb");
		PVFile.print2File(continProbList_numConTasks_exitstatus_log, outputDir+"/numConTasks_exitstatus_log2.prb");
		PVFile.print2File(continProbList_numMulTasks_exitstatus_log, outputDir+"/numMulTasks_exitstatus_log2.prb");
		
		System.out.println("Computing aggregated probability for exit status -> x");
		
		List<ContinProbResult> continProbList_exitstatus_walltime_log = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_exitstatus_runtime_log = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_exitstatus_corehours_log = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_exitstatus_numConTasks_log = new ArrayList<ContinProbResult>();
		List<ContinProbResult> continProbList_exitstatus_numMulTasks_log = new ArrayList<ContinProbResult>();
		
		for(int i = 0;i<10;i++)
			for(int j = 0;j<numExitStatus;j++)
			{
				double prob = wallTimeErrLog_contingency[i][j]/wallTimeErrLog_contingency_j[j];
				size = (long)wallTimeErrLog[i][j];
				ContinProbResult r = new ContinProbResult("EXIT_STATUS", "WALLTIME", index2TimeString_log(i), exitStatusValues[j], prob, size);
				continProbList_exitstatus_walltime_log.add(r);
				
				prob = runTimeErrLog_contingency[i][j]/runTimeErrLog_contingency_j[j];
				size = (long)runTimeErrLog[i][j];
				r = new ContinProbResult("EXIT_STATUS", "RUNTIME", index2TimeString_log(i), exitStatusValues[j], prob, size);
				continProbList_exitstatus_runtime_log.add(r);
				
				prob = corehourErrLog_contingency[i][j]/corehourErrLog_contingency_j[j];
				size = (long)corehourErrLog[i][j];
				r = new ContinProbResult("EXIT_STATUS", "COREHOURS", index2CorehoursString_log(i), exitStatusValues[j], prob, size);
				continProbList_exitstatus_corehours_log.add(r);
				
				prob = numConsecutiveTasksLog_contingency[i][j]/numConsecutiveTasksLog_contingency_j[j];
				size = (long)numConsecutiveTasksLog[i][j];
				r = new ContinProbResult("EXIT_STATUS", "NUMCONTASKS", index2NumTasksString_log(i), exitStatusValues[j], prob, size);
				continProbList_exitstatus_numConTasks_log.add(r);

				prob = numMultilocationTasksLog_contingency[i][j]/numMultilocationTasksLog_contingency_j[j];
				size = (long)numMultilocationTasksLog[i][j];
				r = new ContinProbResult("EXIT_STATUS", "NUMMULTASKS", index2NumTasksString_log(i), exitStatusValues[j], prob, size);
				continProbList_exitstatus_numMulTasks_log.add(r);
			}
		
		System.out.println("Sorting ....");
		sortingMode = 0;
		Collections.sort(continProbList_exitstatus_walltime_log);
		Collections.sort(continProbList_exitstatus_runtime_log);
		Collections.sort(continProbList_exitstatus_corehours_log);
		Collections.sort(continProbList_exitstatus_numConTasks_log);
		Collections.sort(continProbList_exitstatus_numMulTasks_log);
		
		System.out.println("writing log-scale results....");
		PVFile.print2File(continProbList_exitstatus_walltime_log, outputDir+"/exitstatus_walltime_log.prb");
		PVFile.print2File(continProbList_exitstatus_runtime_log, outputDir+"/exitstatus_runtime_log.prb");
		PVFile.print2File(continProbList_exitstatus_corehours_log, outputDir+"/exitstatus_corehours_log.prb");
		PVFile.print2File(continProbList_exitstatus_numConTasks_log, outputDir+"/exitstatus_numConTasks_log.prb");
		PVFile.print2File(continProbList_exitstatus_numMulTasks_log, outputDir+"/exitstatus_numMulTasks_log.prb");
		
		sortingMode = 1;
		Collections.sort(continProbList_exitstatus_walltime_log);
		Collections.sort(continProbList_exitstatus_runtime_log);
		Collections.sort(continProbList_exitstatus_corehours_log);
		Collections.sort(continProbList_exitstatus_numConTasks_log);
		Collections.sort(continProbList_exitstatus_numMulTasks_log);
		
		System.out.println("writing log-scale results....");
		PVFile.print2File(continProbList_exitstatus_walltime_log, outputDir+"/exitstatus_walltime_log2.prb");
		PVFile.print2File(continProbList_exitstatus_runtime_log, outputDir+"/exitstatus_runtime_log2.prb");
		PVFile.print2File(continProbList_exitstatus_corehours_log, outputDir+"/exitstatus_corehours_log2.prb");
		PVFile.print2File(continProbList_exitstatus_numConTasks_log, outputDir+"/exitstatus_numConTasks_log2.prb");
		PVFile.print2File(continProbList_exitstatus_numMulTasks_log, outputDir+"/exitstatus_numMulTasks_log2.prb");
		
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
	
	static int computeIndex_log_numTasks(int numTasks)
	{
		if(numTasks==0)
			return -1;
		else if(numTasks>=1 && numTasks<=10)
			return 0;
		else if(numTasks>=11 && numTasks<=20)
			return 1;
		else if(numTasks>=21 && numTasks<=40)
			return 2;
		else if(numTasks>=41 && numTasks<=80)
			return 3;
		else if(numTasks>=81 && numTasks<=160)
			return 4;
		else if(numTasks>=161 && numTasks<=320)
			return 5;
		else if(numTasks>=321 && numTasks<=640)
			return 6;
		else if(numTasks>=641 && numTasks<=1280)
			return 7;
		else if(numTasks>=1281 && numTasks<=2560)
			return 8;
		else 
			return 9;
		
	}
	
	/**
	 * used for walltime and runtime
	 * @param i
	 * @return
	 */
	static String index2TimeString_log(int i)
	{
		if(i==0)
			return "[0,10min]";
		else
		{
			return "["+v1d6*Math.pow(2, i-1)+","+v1d6*Math.pow(2, i)+"]";
		}
	}
	
	static String index2CorehoursString_log(int i)
	{
		if(i==0)
			return "[0,1000]";
		else
		{
			return "["+1000*Math.pow(2, i-1)+","+1000*Math.pow(2, i)+"]";
		}
	}
	
	static String index2NumTasksString_log(int i)
	{
		switch(i)
		{
		case 0: 
			return "[0]";
		case 1:
			return "[1,20]";
		case 2:
			return "[21,40]";
		case 3:
			return "[41,80]";
		case 4:
			return "[81,160]";
		case 5:
			return "[161,320]";
		case 6:
			return "[321,640]";
		case 7:
			return "[641,1280]";
		case 8:
			return "[1281,2560]";
		case 9:
			return "[2561+]";
		}
		return "null";
	}
	
	static String index2TimeString_lin(int i)
	{
		if(i==24)
			return "[24+]";
		else
			return "["+i+","+(i+1)+"]";
	}
	
	static String index2CorehoursString_lin(int i)
	{
		if(i==100)
			return "[100+]";
		else
			return "["+i*1000+","+(i+1)*1000+"]";
	}
	
	static String index2NumTasksString_lin(int i)
	{
		if(i==0)
			return "[0]";
		else
			return "["+((i-1)*20+1)+","+i*20+"]";
	}
	
}


class ContinProbResult implements Comparable<ContinProbResult>
{
	private String source;
	private String target;

	private String featureValue;
	private String exitStatusValue;
	
	private double probability = 0;
	private long sampleSize = 0;
	
	public ContinProbResult(String source, String target, String featureValue, String exitStatusValue,
			double probability, long size) {
		this.source = source;
		this.target = target;
		this.featureValue = featureValue;
		this.exitStatusValue = exitStatusValue;
		this.probability = probability;
		sampleSize = size;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getFeatureValue() {
		return featureValue;
	}

	public void setFeatureValue(String featureValue) {
		this.featureValue = featureValue;
	}

	public String getExitStatusValue() {
		return exitStatusValue;
	}

	public void setExitStatusValue(String exitStatusValue) {
		this.exitStatusValue = exitStatusValue;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if(source.equals("EXIT_STATUS"))
		{
			sb.append("EXIT_STATUS(").append(exitStatusValue).append(")-").append(target).append("(").append(featureValue).append("): ").append(probability).append(" ").append(1.29/Math.sqrt(sampleSize));
		}
		else //target.equals("EXIT_STATUS")
		{
			sb.append(source).append("(").append(featureValue).append(")-").append("EXIT_STATUS(").append(exitStatusValue).append("): ").append(probability).append(" ").append(1.29/Math.sqrt(sampleSize));
		}
		return sb.toString();
	}
	
	public int compareTo(ContinProbResult other)
	{
		if(GenerateStateFeatures2.sortingMode==0)
		{
			if(probability>other.probability)
				return -1;
			else if(probability<other.probability)
				return 1;
			else
				return 0;
		}
		else
		{
			if(sampleSize>other.sampleSize)
				return -1;
			else if(sampleSize<other.sampleSize)
				return 1;
			else
				return 0;
		}
	}

	public long getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(long sampleSize) {
		this.sampleSize = sampleSize;
	}
}

