package analysis.RAS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import element.Field;
import element.Value;
import util.ConversionHandler;
import util.PVFile;
import util.RecordSplitter;

public class ExtractValueTypes4EachField {

	/**
	 * Generate full schema based on the basic schema
	 * @param args
	 */
	public static void main(String[] args)
	{
		if(args.length!=4)
		{
			System.out.println("Usage: java ExtractValueType4EachField [schema] [inputDir] [extension] [outputDir]");
			System.out.println("Example: java ExtractValueType4EachField /home/fti/eventlog/schema/basicSchema.txt /home/fti/eventlog csv /home/fti/eventlog/schema/fullSchema");
			System.exit(0);
		}
		
		String schemaPath = args[0];
		String inputDir = args[1];
		String extension = args[2];
		String outputDir = args[3];
		
		boolean fltrMode = false;
		if(extension.equals("fltr"))
			fltrMode = true;
		
		System.out.println("Loading basic schema....");
		List<Field> fieldList = loadBasicSchema(schemaPath);
		Field[] fields = ConversionHandler.convertFieldList2FieldArray(fieldList);
		
		List<String> inputFileList = PVFile.getFiles(inputDir, extension);
		Collections.sort(inputFileList);
		
		System.out.println("Processing the files....");
		Iterator<String> iter = inputFileList.iterator();
		double initLogTime = System.currentTimeMillis()/1000.0;
		for(int s = 1;iter.hasNext();s++)
		{
			String fileName = iter.next();
			String filePath = inputDir+"/"+fileName;
			System.out.println("Reading "+filePath);
			List<String> lineList = PVFile.readFile(filePath);
			System.out.println("Extracting value types .....");
			Iterator<String> iter2 = lineList.iterator();
			while(iter2.hasNext())
			{
				String line = iter2.next();
				if(line.startsWith("#"))
					continue;
				
				String[] data = null;
				if(!fltrMode)
					data = RecordSplitter.partition(line, ",");
				else
					data = RecordSplitter.partition(line, " ");
//				String[] data = line.split(",");
				if(!fltrMode)
					data = fixData(data, fields);
				for(int i = 0;i<data.length;i++)
				{
					if(data[i]!=null)
						fields[i].checkAndAdd(data[i].trim());
					else
						fields[i].checkAndAdd(null);
				}
			}
			PVFile.showProgress(initLogTime, s, inputFileList.size(), fileName);
		}
		
		System.out.println("Outputing withcount results....");
		
		Value.withCount = true;
		for(int i = 0;i<fields.length;i++)
		{
			List vlist = fields[i].toStringList();
			List<String> rList = fields[i].toStringList_ratio();
			if(vlist!=null)
			{	
				PVFile.print2File(vlist, outputDir+"/withCount/"+fields[i].getFieldName()+".fsc");
				PVFile.print2File(rList, outputDir+"/withRatio/"+fields[i].getFieldName()+".fsr");
			}
		}
		
		System.out.println("Outputting withoutcount results....");
		Value.withCount = false;
		for(int i = 0;i<fields.length;i++)
		{
			List vlist = fields[i].toStringList();
			if(vlist!=null)
				PVFile.print2File(vlist, outputDir+"/withoutCount/"+fields[i].getFieldName()+".fsc");
		}
		
		System.out.println("full schema is outputted to: "+outputDir);
		System.out.println("done.");
	}
	
	public static String[] fixData(String[] data, Field[] fields)
	{
		if(data.length == fields.length)
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
	
	public static List<Field> loadBasicSchema(String basicSchemaFilePath)
	{
		List<Field> fieldList = new ArrayList<Field>();
		
		List<String> lineList = PVFile.readFile(basicSchemaFilePath);
		Iterator<String> iter = lineList.iterator();
		int i = 0;
		while(iter.hasNext())
		{
			String line = iter.next().trim();
			if(line.startsWith("#"))
				continue;
			String[] data = line.split("\\s+");
			String fieldName = data[0];
//			String schemaType = data[1];
//			String dataType = data[2];
//			int length = Integer.parseInt(data[3]);
//			int scale = Integer.parseInt(data[4]);
//			boolean tag = true;
//			if(data[5].equalsIgnoreCase("No"))
//				tag = false;
//			else if(data[5].equalsIgnoreCase("Yes"))
//				tag = true;
//			
//			Field f = new Field(fieldName, schemaType, dataType, length, scale, tag);
			Field f = new Field(fieldName);
			f.setIndex(i++);
			fieldList.add(f);
		}
		
		return fieldList;
	}
}
