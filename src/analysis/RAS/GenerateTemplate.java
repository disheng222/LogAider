package analysis.RAS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import element.Field;
import util.Cmd;
import util.ConversionHandler;
import util.PVFile;

/**
 * @deprecated
 * @author fti
 *
 */
public class GenerateTemplate {

	public static HashMap<String, List<String>> selFieldMap = new HashMap<String, List<String>>();
	public static String heloPath = "";
	
	public static void main(String[] args)
	{
		if(args.length!=5)
		{
			System.out.println("Usage: java GenerateTemplate [helopath] [schema] [inputDir] [extension] [outputDir]");
			System.out.println("Usage: java GenerateTemplate xxx/group /home/fti/CFEFIES-project/miralog/schema/basicSchema-s.txt /home/fti/CFEFIES-project/miralog csv output");
			System.exit(0);
		}
		
		heloPath = args[0];
		String schemaPath = args[1];
		String inputDir = args[2];
		String extension = args[3];
		String outputDir = args[4];
		
		List<Field> fullFieldList = new ArrayList<Field>();
		System.out.println("Loading basic schema...");
		List<Field> fieldList = loadBasicSchema(fullFieldList, schemaPath);
		Field[] fullFields = ConversionHandler.convertFieldList2FieldArray(fullFieldList);
		Field[] fields = ConversionHandler.convertFieldList2FieldArray(fieldList);
		int[] selFieldIndex = getSelectFieldIndex(fields);
		
		List<String> inputFileList = PVFile.getFiles(inputDir, extension);
		Collections.sort(inputFileList);
		
		System.out.println("Processing files....");
		Iterator<String> iter = inputFileList.iterator();
		double initLogTime = System.currentTimeMillis()/1000.0;
		for(int s = 1;iter.hasNext();s++)
		{
			String fileName = iter.next();
			String filePath = inputDir+"/"+fileName;
			List<String> lineList = PVFile.readFile(filePath);
			Iterator<String> iter2 = lineList.iterator();
			int k = 0;
			while(iter2.hasNext())
			{
				k++;
				String line = iter2.next();
				String[] data = line.split(",");
				for(int i = 0;i<data.length;i++)
				{
					data = ExtractValueTypes4EachField.fixData(data, fullFields);
					//TODO
					//fields[i].checkAndAdd(data[i].trim());
					String key = constructKey(selFieldIndex, data);
					List<String> list = selFieldMap.get(key);
					if(list==null)
					{
						list = new ArrayList<String>();
						selFieldMap.put(key, list);
					}
					String newLine = connectFieldData(data, selFieldIndex);
					list.add(newLine);
				}
			}
			PVFile.showProgress(initLogTime, s, inputFileList.size(), fileName);
		}
		
		//output the reorganized data
		writeReorganizedData(outputDir);
		System.out.println("outputDir= "+outputDir);
		System.out.println("done.");
	}
	
	public static void writeReorganizedData(String outputDir)
	{
		//TODO: write selFieldMap to a set of files (filename is the keys)
		Iterator iter = selFieldMap.entrySet().iterator(); 
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    String fileName = (String)entry.getKey(); 
		    String filePath = outputDir+"/"+fileName+"_l0c0";
		    List<String> list = (List<String>)entry.getValue(); 
		    PVFile.print2File(list, filePath);
		    
		    Cmd.CmdExec(heloPath+"/group -g50 -m0 "+fileName);
		}
	}
	
	private static String connectFieldData(String[] data, int[] excludeIndex)
	{
		int j = 0; //excluding index
		StringBuffer sb = new StringBuffer();
		for(int i = 0;i<data.length;i++)
		{
			if(j<excludeIndex.length&&i==excludeIndex[j])
			{
				j++;
				continue;
			}
			sb.append(data[i]);
			sb.append(",");
		}
		String result = sb.toString();
		return result.substring(0, result.length()-1);
	}
	
	private static int[] getSelectFieldIndex(Field[] fields)
	{
		int[] index = new int[fields.length];
		for(int i = 0 ; i<fields.length;i++)
		{
			index[i] = fields[i].getIndex();
		}
		return index;
	}
	
	private static String constructKey(int[] selIndex, String[] data)
	{
		StringBuffer sb = new StringBuffer();
		int I = selIndex[0];
		sb.append(data[I].trim());
		for(int i = 1;i<selIndex.length;i++)
		{
			I = selIndex[i];
			sb.append("~");
			sb.append(data[I].trim());
		}
		return sb.toString().replace("\"", "");
	}
	
	public static List<Field> loadBasicSchema(List <Field> fullFieldList, String basicSchemaFilePath)
	{
		List<Field> fieldList = new ArrayList<Field>();
		
		List<String> lineList = PVFile.readFile(basicSchemaFilePath);
		Iterator<String> iter = lineList.iterator();
		for(int i = 0;iter.hasNext();i++)
		{
			String line = iter.next().trim();
			if(line.startsWith("#"))
				continue;
			String[] data = line.split("\\s+");
			String fieldName = data[0];
			boolean select = false;
			if(fieldName.startsWith("*"))
			{
				fieldName = fieldName.replace("*", "");
				select = true;
			}
			String schemaType = data[1];
			String dataType = data[2];
			int length = Integer.parseInt(data[3]);
			int scale = Integer.parseInt(data[4]);
			boolean tag = true;
			if(data[5].equalsIgnoreCase("No"))
				tag = false;
			else if(data[5].equalsIgnoreCase("Yes"))
				tag = true;
			
			if(select)
			{
				Field f = new Field(fieldName, schemaType, dataType, length, scale, tag);
				f.setIndex(i);
				fieldList.add(f);	
				fullFieldList.add(f);
			}
			else
			{
				Field f = new Field(fieldName, schemaType, dataType, length, scale, tag);
				fullFieldList.add(f);
			}
		}
		
		return fieldList;
	}
}
