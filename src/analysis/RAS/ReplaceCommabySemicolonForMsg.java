package analysis.RAS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import element.Field;
import util.ConversionHandler;
import util.PVFile;

public class ReplaceCommabySemicolonForMsg {

	public static void main(String[] args)
	{
		if(args.length<3)
		{
			System.out.println("Usage: java ReplaceCommabySemicolonForMsg [schemaPath] [logDir] [log_extension]");
			System.out.println("Example: java ReplaceCommabySemicolonForMsg /home/fti/Catalog-project/miralog/schema/basicSchema.txt /home/fti/Catalog-project/miralog csvtmp");
			System.exit(0);
		}
		
		String schemaPath = args[0];
		String inputDir = args[1];
		String logExt = args[2];
		//String outputDir = inputDir;
		
		System.out.println("Loading basic schema....");
		List<Field> fieldList = ExtractValueTypes4EachField.loadBasicSchema(schemaPath);
		Field[] fields = ConversionHandler.convertFieldList2FieldArray(fieldList);
		
		List<String> logList = PVFile.getFiles(inputDir, logExt);
		Collections.sort(logList);
				
		Iterator<String> iter = logList.iterator();
		double initLogTime = System.currentTimeMillis()/1000.0;
		for(int i = 0;iter.hasNext();i++)
		{
			String logFile = iter.next();
			String logPath = inputDir+"/"+logFile;
			List<String> lineList = PVFile.readFile(logPath);
			List<String> newLogList = new ArrayList<String>();
	
			Iterator<String> iter2 = lineList.iterator();
			for(int j = 0;iter2.hasNext();j++)
			{
				String line = iter2.next();
				String[] data = line.split(",");
				data = ExtractValueTypes4EachField.fixData(data, fields);
				//severity is data[4].trim() for MIRA

				String newLine = connectStringArray(data);
				newLogList.add(newLine);
				
				if(j%3000==0)
					PVFile.showProgress(initLogTime, j, lineList.size(), "");
			}
			PVFile.print2File(newLogList, logPath);
			PVFile.showProgress(initLogTime, i, logList.size(), logFile);
		}
		
		System.out.println("done.");
	}
	
	private static String connectStringArray(String[] s)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(s[0]);
		for(int i= 1;i<s.length;i++)
			sb.append(","+s[i]);
		return sb.toString();
	}
}
