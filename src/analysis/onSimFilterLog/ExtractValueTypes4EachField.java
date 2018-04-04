package analysis.onSimFilterLog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import element.Field;
import util.ConversionHandler;
import util.PVFile;

public class ExtractValueTypes4EachField {

	/**
	 * Generate full schema based on the basic schema
	 * @param args
	 */
	public static void main(String[] args)
	{
		if(args.length!=3)
		{
			System.out.println("Usage: java ExtractValueType4EachField [schema] [inputFile] [outputDir]");
			System.out.println("Example: java ExtractValueType4EachField /home/sdi/Work/Catalog-project/Catalog-data/Compare-5years-1years/5years/FilterAndClassify/eventKeyIndex.conf "
					+ "/home/sdi/Work/Catalog-project/Catalog-data/Compare-5years-1years/5years/FilterAndClassify/allEvents.txt-0.3-onlyEvents3.cor "
					+ "/home/sdi/Work/Catalog-project/Catalog-data/Compare-5years-1years/5years/FilterAndClassify/withRatio-pie");
			System.exit(0);
		}
		
		String schemaPath = args[0];
		String inputFile = args[1];
		String outputDir = args[2];
		
		System.out.println("Loading basic schema....");
		List<Field> fieldList = loadBasicSchema(schemaPath);
		Field[] fields = ConversionHandler.convertFieldList2FieldArray(fieldList);
		
		System.out.println("Processing the data file....");
		
		String filePath = inputFile;
		System.out.println("Reading "+filePath);
		List<String> lineList = PVFile.readFile(filePath);
		System.out.println("Extracting value types .....");
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.startsWith("#"))
				continue;
			
			String[] data = line.split("\\s");
			fields[0].checkAndAdd(data[2].trim());
			fields[1].checkAndAdd(data[3].trim());
			fields[2].checkAndAdd(data[5].trim());
			fields[3].checkAndAdd(data[6].trim());
			fields[4].checkAndAdd(data[10].trim());
			fields[5].checkAndAdd(data[12].trim());
		}
		
		System.out.println("Outputing withcount results....");
		
		for(int i = 0;i<fields.length;i++)
		{
			List vlist = fields[i].toStringList();
			List<String> rList = fields[i].toStringList_ratio();
			if(vlist!=null)
			{	
				PVFile.print2File(vlist, outputDir+"/"+fields[i].getFieldName()+".fsc");
				PVFile.print2File(rList, outputDir+"/"+fields[i].getFieldName()+".fsr");
			}
		}
		
		System.out.println("full schema is outputted to: "+outputDir);
		System.out.println("done.");
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
			Field f = new Field(fieldName);
			f.setIndex(i++);
			fieldList.add(f);
		}
		
		return fieldList;
	}
}
