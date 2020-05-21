package analysis.Job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import element.Field;
import util.ConversionHandler;
import util.NumericChecker;
import util.PVFile;
import util.RecordSplitter;

public class CollectErrorMessages {

	public static void main(String[] args)
	{
		if(args.length<4)
		{
			System.out.println("Usage: java CollectErrorMessages [schemaPath] [severity_index] [logDir] [log_extension]");
			System.out.println("Example: java CollectErrorMessages /home/fti/Catalog-project/miralog/RAS-Job/Job/basicSchema/basicSchema.txt 14 /home/fti/Catalog-project/miralog/RAS-Job/Job csv");
			System.exit(0);
		}
		
		String schemaPath = args[0];
		int sevIndex = Integer.parseInt(args[1]);
		String inputDir = args[2];
		String logExt = args[3];
		String outputDir = inputDir;
		
		System.out.println("Loading basic schema....");
		List<Field> fieldList = ExtractValueTypes4EachField.loadBasicSchema(schemaPath);
		Field[] fields = ConversionHandler.convertFieldList2FieldArray(fieldList);
		
		List<String> logList = PVFile.getFiles(inputDir, logExt);
		Collections.sort(logList);
		
		List<String> fatalList = new ArrayList<String>();
		
		Iterator<String> iter = logList.iterator();
		double initLogTime = System.currentTimeMillis()/1000.0;
		for(int i = 0;iter.hasNext();i++)
		{
			String logFile = iter.next();
			String logPath = inputDir+"/"+logFile;
			List<String> lineList = PVFile.readFile(logPath);
			
			Iterator<String> iter2 = lineList.iterator();
			for(int j = 0;iter2.hasNext();j++)
			{
				String line = iter2.next();
				if(line.startsWith("#"))
					continue;
				String[] data = RecordSplitter.partition(line);
				//ExtractValueTypes4EachField.fixData(data);
				//String[] data = line.split(",");
				//data = ExtractValueTypes4EachField.fixData(data, fields);
				//severity is data[4].trim() for MIRA
				String severity = data[sevIndex].trim();
				if(!NumericChecker.isNumeric(severity))
				{
					fatalList.add(line);
				}
				else
				{	
					if(Integer.parseInt(severity)!=0)
						fatalList.add(line);
				}

				if(j%3000==0)
					PVFile.showProgress(initLogTime, j, lineList.size(), severity);
			}
			PVFile.showProgress(initLogTime, i, logList.size(), logFile);
		}
		
		System.out.println("Writing results to "+outputDir);
		PVFile.print2File(fatalList, outputDir+"/totalFatalMsg.fat");
		
		System.out.println("done.");
	}
}
