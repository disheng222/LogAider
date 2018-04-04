package analysis.RAS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import element.Field;
import util.ConversionHandler;
import util.PVFile;
import util.RecordSplitter;

public class CollectWarnFatalMessages {

	static String inputDir = null;
	static String inputFile = null;
	static String outputDir = null;
	static String logExt = null;
	public static void main(String[] args)
	{
		if(args.length<5)
		{
			System.out.println("Usage: java CollectWarnFatalMessags [schemaPath] [severity_index] [file or directory: -f/-d] [logDir/logFile] [log_extension]");
			System.out.println("Example: java CollectWarnFatalMessags /home/fti/Catalog-project/miralog/schema/basicSchema.txt 4 -d /home/fti/Catalog-project/miralog csvtmp");
			System.exit(0);
		}
		
		String schemaPath = args[0];
		int sevIndex = Integer.parseInt(args[1]);
		if(args[2].equals("-f"))
		{	
			inputFile = args[3];
		}
		else
		{
			inputDir = args[3];
			outputDir = inputDir;
			logExt = args[4];
		}
		
		
		System.out.println("Loading basic schema....");
		List<Field> fieldList = ExtractValueTypes4EachField.loadBasicSchema(schemaPath);
		Field[] fields = ConversionHandler.convertFieldList2FieldArray(fieldList);
		
		List<String> infoList = new ArrayList<String>();
		List<String> warnList = new ArrayList<String>();
		List<String> fatalList = new ArrayList<String>();
		List<String> nodiagFatalList = new ArrayList<String>();
		
		double initLogTime = System.currentTimeMillis()/1000.0;
		if(args[2].equals("-d"))
		{
			List<String> logList = PVFile.getFiles(inputDir, logExt);
			Collections.sort(logList);
			
			Iterator<String> iter = logList.iterator();
			for(int i = 0;iter.hasNext();i++)
			{
				String logFile = iter.next();
				String logPath = inputDir+"/"+logFile;
				List<String> lineList = PVFile.readFile(logPath);
				
				Iterator<String> iter2 = lineList.iterator();
				for(int j = 0;iter2.hasNext();j++)
				{
					String line = iter2.next();
					//String[] data = line.split(",");
					//data = ExtractValueTypes4EachField.fixData(data, fields);
					String severity = process(line, sevIndex, infoList, warnList, fatalList, nodiagFatalList);
					if(j%3000==0)
						PVFile.showProgress(initLogTime, j, lineList.size(), severity);
				}
				PVFile.showProgress(initLogTime, i, logList.size(), logFile);
			}
		}
		else
		{
			String logPath = inputFile;
			List<String> lineList = PVFile.readFile(logPath);
			
			Iterator<String> iter2 = lineList.iterator();
			for(int j = 0;iter2.hasNext();j++)
			{
				String line = iter2.next();
				//String[] data = line.split(",");
				//data = ExtractValueTypes4EachField.fixData(data, fields);
				String severity = process(line, sevIndex, infoList, warnList, fatalList, nodiagFatalList);
				if(j%3000==0)
					PVFile.showProgress(initLogTime, j, lineList.size(), severity);
			}
		}
		
		if(args[2].equals("-d"))
		{
			System.out.println("Writing results to "+outputDir);
			PVFile.print2File(infoList, outputDir+"/totalInfoMsg.info");
			PVFile.print2File(warnList, outputDir+"/totalWarnMsg.wan");
			PVFile.print2File(fatalList, outputDir+"/totalFatalMsg.fat");
		}
		else
		{
			System.out.println("Writing results to "+inputFile+".inf");
			PVFile.print2File(infoList, inputFile+".inf");			
			System.out.println("Writing results to "+inputFile+".wan");
			PVFile.print2File(warnList, inputFile+".wan");
			System.out.println("Writing results to "+inputFile+".fat");
			PVFile.print2File(fatalList, inputFile+".fat");
			System.out.println("Writing results to "+inputFile+".nodiag");
			PVFile.print2File(nodiagFatalList, inputFile+".nodiag");
		}
		
		System.out.println("done.");
	}
	

	private static String process(String line, int sevIndex, List<String> infoList, List<String> warnList, List<String> fatalList, List<String> nodiagFatalList)
	{
		String[] data = RecordSplitter.partition(line);
		
		//severity is data[4].trim() for MIRA
		String severity = data[sevIndex];
		if(severity.equals("WARN"))
			warnList.add(line);
		else if(severity.equals("FATAL"))
		{
			fatalList.add(line);
			String component = data[3];
			if(!component.equals("DIAGS"))
				nodiagFatalList.add(line);
		}
		else
			infoList.add(line);
		return severity;
	}
}
