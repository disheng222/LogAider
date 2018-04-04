package analysis.RAS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import element.Field;
import util.ConversionHandler;
import util.PVFile;

/**
 * The last field should not be trimmed if it is a "space", otherwise, .split() will ignore it.
 * @depcreated
 * @author fti
 *
 */
public class TrimAllFields {

	public static void main(String[] args)
	{
		if(args.length!=4)
		{
			System.out.println("Usage: java TrimAllFields [schema] [inputDir] [extension]");
			System.out.println("Usage: java TrimAllFields /home/fti/CFEFIES-project/miralog/schema/basicSchema.txt /home/fti/CFEFIES-project/miralog csv /home/fti/CFEFIES-project/miralog/schema/fullSchema");
			System.exit(0);
		}
		
		String schemaPath = args[0];
		String inputDir = args[1];
		String extension = args[2];
		String outputDir = inputDir;
		
		List<Field> fieldList = ExtractValueTypes4EachField.loadBasicSchema(schemaPath);
		Field[] fields = ConversionHandler.convertFieldList2FieldArray(fieldList);
		
		List<String> inputFileList = PVFile.getFiles(inputDir, extension);
		Collections.sort(inputFileList);
		Iterator<String> iter = inputFileList.iterator();
		double initLogTime = System.currentTimeMillis()/1000.0;
		for(int s = 1;iter.hasNext();s++)
		{
			String fileName = iter.next();
			String filePath = inputDir+"/"+fileName;
			List<String> lineList = PVFile.readFile(filePath);
			List<String> newLineList = new ArrayList<String>();
			Iterator<String> iter2 = lineList.iterator();
			while(iter2.hasNext())
			{
				String line = iter2.next();
				String[] data = line.split(",");
				for(int i = 0;i<data.length;i++)
				{
					data = fixData(data, fields);
					String newDataLine = connectStringArray(data);
					newLineList.add(newDataLine);
				}
			}
			PVFile.print2File(newLineList, filePath);
			PVFile.showProgress(initLogTime, s, inputFileList.size(), fileName);
		}
	}
	
	public static String[] fixData(String[] data, Field[] fields)
	{
		if(data.length <= fields.length)
			return data;
		else
		{
			//This means the the description field has "," (fields[13])
			int startIndex = 13;
			int endIndex = data.length-3;
			String[] correctData = new String[fields.length];
			for(int i = 0; i < startIndex;i++)
				correctData[i] = data[i];
			String message = data[13];
			for(int i = 14;i<=endIndex;i++)
				message += " ; "+data[i];
			correctData[13] = message;
			for(int i = 0; i < 2;i++)
				correctData[14+i] = data[data.length-2+i];
			
			return correctData;
		}
	}
	
	private static String connectStringArray(String[] s)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(s[0]);
		for(int i= 1;i<s.length;i++)
			sb.append(","+s[i]);
		return sb.toString();
	}
	
	public static void trim(String[] data)
	{
		for(int i= 0;i<data.length;i++)
			data[i] = data[i].trim();
	}
}
