package analysis.RAS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import element.Field;
import util.ConversionHandler;
import util.PVFile;

public class ExtractDescriptionField {

	public static void main(String[] args)
	{
		if(args.length<5)
		{
			System.out.println("Usage: java ExtractDescriptionField [schema] [descriptionFieldIndex] [inputDir] [extension] [outputDir]");
			System.out.println("Example: java ExtractDescriptionField /home/fti/eventlog/schema/basicSchema.txt 13 /home/fti/Catalog-project/miralog csv /home/fti/Catalog-project/miralog/desc");
			System.exit(0);
		}
		
		String schemaPath = args[0];
		int descIndex = Integer.parseInt(args[1]);
		String inputDir = args[2];
		String extension = args[3];
		String outputDir = args[4];
		
		System.out.println("Loading basic schema....");
		List<Field> fieldList = ExtractValueTypes4EachField.loadBasicSchema(schemaPath);
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
			System.out.println("Extracting the descprition ....");
			List<String> msgList = new ArrayList<String>();
			Iterator<String> iter2 = lineList.iterator();
			while(iter2.hasNext())
			{
				String line = iter2.next();
				String[] data = line.split(",");
				data = ExtractValueTypes4EachField.fixData(data, fields);
				String desc = data[descIndex].trim();
				msgList.add(desc);
			}
			PVFile.print2File(msgList, outputDir+"/"+fileName+"_l0c0");
			PVFile.showProgress(initLogTime, s, inputFileList.size(), fileName);
		}
		
		System.out.println("outputDir: "+outputDir);
		System.out.println("done.");
	}
}
