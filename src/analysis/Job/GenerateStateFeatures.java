package analysis.Job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import element.FeatureStateField;
import element.Field;
import util.ConversionHandler;
import util.PVFile;
import util.RecordSplitter;

public class GenerateStateFeatures {
	public static void main(String[] args)
	{
		if(args.length<8)
		{
			System.out.println("Usage: java GenerateStateFeatures [basicSchema] [fullSchemaDir] [schemaExt] [logDir] [logExt] [outputDir] [fields....]");
			System.out.println("Example: java GenerateStateFeatures /home/fti/Catalog-project/miralog/RAS-Job/Job/basicSchema/basicSchema.txt /home/fti/Catalog-project/miralog/fullSchema/fullSchema/withRatio fsr "
					+ "/home/fti/Catalog-project/miralog/RAS-Job/Job csv /home/fti/Catalog-project/miralog/RAS-Job/Job/featureState"
					+ " capability exit_code major_project mode nodes_cost percentile prod_queue project_name queue science_field science_field_short size_buckets3 size_cost user");
			System.exit(0);
		}
		
		String schemaPath = args[0];
		
		String fullSchemaDir = args[1];
		String schemaExt = args[2];
		
		String logDir = args[3];
		String logExt = args[4];
		
		String outputDir = args[5];
		
		String[] focusFieldNames = new String[args.length-6];
		for(int i = 0;i<focusFieldNames.length;i++)
			focusFieldNames[i] = args[i+6];
		
		System.out.println("Loading basic schema....");
		List<Field> fieldList = ExtractValueTypes4EachField.loadBasicSchema(schemaPath);
		Field[] fields = ConversionHandler.convertFieldList2FieldArray(fieldList);
		
		System.out.println("Loading full schama.....");
		HashMap<String, List<String>> fieldValuesMap = loadFullSchema(fullSchemaDir, schemaExt);
		
		HashMap<String, HashMap<String, Integer>> fieldValueIndexMap = getFieldValueIndexMap(fieldValuesMap);
		
		Field[] focusFields = buildFocusFields(focusFieldNames, fields);
		//featureStateFields are used to store the results....
		FeatureStateField[][] featureStateFields = buildFeatureStateFields(focusFields, fieldValuesMap); 
		
		List<String> inputFileList = PVFile.getFiles(logDir, logExt);
		Collections.sort(inputFileList);
		
		Iterator<String> iter = inputFileList.iterator();
		while(iter.hasNext())
		{
			String logFileName = iter.next();
			String logFilePath = logDir+"/"+logFileName;
			System.out.println("Reading file "+logFilePath);
			List<String> lineList = PVFile.readFile(logFilePath);
			
			System.out.println("Processing file "+logFilePath);
			double initLogTime = System.currentTimeMillis()/1000.0;
			Iterator<String> iter2 = lineList.iterator();
			for(int a = 0;iter2.hasNext();a++)
			{
				String line = iter2.next();
				if(line.startsWith("#"))
					continue;
				String[] data = RecordSplitter.partition(line);
				ExtractValueTypes4EachField.fixData(data);
				//String[] data = line.split(",");
				//data = ExtractValueTypes4EachField.fixData(data, fields);
				
				processData(data, featureStateFields, fieldValueIndexMap);
				if(a%5000==0)
					PVFile.showProgress(initLogTime, a, lineList.size(), logFileName);
			}	
		}
		
		System.out.println("Writing results to "+outputDir);
		writeResults(featureStateFields, outputDir);
		
		System.out.println("done.");
	}
	
	public static void writeResults(FeatureStateField[][] featureStateFields, String outputDir)
	{
		for(int i = 0;i<featureStateFields.length;i++)
		{
			String featureField = featureStateFields[i][0].getFeatureFieldName();
			String featureFieldDir = outputDir+"/"+featureField;
			for(int j = 0;j<featureStateFields[0].length;j++)
			{
				FeatureStateField fsf = featureStateFields[i][j];
				String fieldNames = fsf.getFieldNames();
				String outputFilePath = featureFieldDir+"/"+fieldNames+".fs";
				List<String> lineList = fsf.toPlainText();
				PVFile.print2File(lineList, outputFilePath);
				
				String outputFilePath2 = featureFieldDir+"/"+fieldNames+".pr";
				List<String> lineList2 = fsf.toPlainText_prob();
				PVFile.print2File(lineList2, outputFilePath2);
				
				String outputFilePath3 = featureFieldDir+"/"+fieldNames+".pe99";
				List<String> lineList3 = fsf.toPlainText_probMarginOfErr(0.99);
				PVFile.print2File(lineList3, outputFilePath3);

				String outputFilePath4 = featureFieldDir+"/"+fieldNames+".pe95";
				List<String> lineList4 = fsf.toPlainText_probMarginOfErr(0.95);
				PVFile.print2File(lineList4, outputFilePath4);

				String outputFilePath5 = featureFieldDir+"/"+fieldNames+".pe90";
				List<String> lineList5 = fsf.toPlainText_probMarginOfErr(0.90);
				PVFile.print2File(lineList5, outputFilePath5);
			}
			System.out.println("Finish writing results for "+featureFieldDir);
		}
	}
	
	private static HashMap<String, HashMap<String, Integer>> getFieldValueIndexMap(
			HashMap<String, List<String>> fieldValueMap)
	{
		HashMap<String, HashMap<String, Integer>> fieldValueIndexMap = new HashMap<String, HashMap<String, Integer>>();
		Iterator iter = fieldValueMap.entrySet().iterator();
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    String fieldName = (String)entry.getKey(); 
		    List<String> valueList = (List<String>)entry.getValue(); 
		    HashMap<String, Integer> valueIndexMap = new HashMap<String, Integer>();
		    Iterator<String> iter2 = valueList.iterator();
		    for(int i =0;iter2.hasNext();i++)
		    {
		    	String value = iter2.next();
		    	valueIndexMap.put(value, i);
		    }
		    fieldValueIndexMap.put(fieldName, valueIndexMap);
		} 
		return fieldValueIndexMap;
	}
	
	private static HashMap<String, List<String>> loadFullSchema(String fullSchemaDir, String schemaExt)
	{
		HashMap<String, List<String>> fieldValueMap = new HashMap<String, List<String>>();
		List<String> fileList = PVFile.getFiles(fullSchemaDir,  schemaExt);
		Iterator<String> iter = fileList.iterator();
		while(iter.hasNext())
		{
			String schemaFileName = iter.next();
			String schemaFilePath = fullSchemaDir+"/"+schemaFileName;
			String fieldName = schemaFileName.split("\\.")[0];

			List<String> valueList = new ArrayList<String>();
			List<String> lineList = PVFile.readFile(schemaFilePath);
			Iterator<String> iter2 = lineList.iterator();
			while(iter2.hasNext())
			{
				String line = iter2.next();
				if(!line.startsWith("#"))
				{
					String v = line.split("\\s")[0];
					valueList.add(v);
				}
			}
			fieldValueMap.put(fieldName, valueList);
		}
		return fieldValueMap;
	}
	
	public static void processData(String[] data, FeatureStateField[][] fsFields, 
			HashMap<String, HashMap<String, Integer>> fieldValueIndexMap)
	{
		for(int i = 0;i<fsFields.length;i++)
		{	
			for(int j = 0;j<fsFields[0].length;j++)
			{
				FeatureStateField fsField = fsFields[i][j];
				String featureFieldName = fsField.getFeatureFieldName();
				String stateFieldName = fsField.getStateFieldName();
				
				int ii = fsField.getFeatureFieldIndex();
				int jj = fsField.getStateFieldIndex();
				
				String featureValue = data[ii].trim();
				String stateValue = data[jj].trim();
				
				HashMap<String, Integer> featureIndexMap = fieldValueIndexMap.get(featureFieldName);
				if(featureValue.equals("")) featureValue = "NULL";
				
				int featureRecordIndex = featureIndexMap.get(featureValue);
				
				HashMap<String, Integer> stateIndexMap = fieldValueIndexMap.get(stateFieldName);
				if(stateValue.equals("")) stateValue = "NULL";
				int stateRecordIndex = stateIndexMap.get(stateValue);
				
				int[][] count = fsField.getCount();
				count[featureRecordIndex][stateRecordIndex]++;
			}
		}
	}
	
	private static Field[] buildFocusFields(String[] focusFieldNames, Field[] fields)
	{
		Field[] focusFields = new Field[focusFieldNames.length];
		for(int i = 0;i<focusFieldNames.length;i++)
		{
			String fieldName = focusFieldNames[i];
			Field field = getField(fieldName, fields);
			if(field==null)
			{
				System.out.println("Error: fieldName "+fieldName+" doesn't exist.");
				System.out.println("Please check the input arguments.....");
				System.exit(0);
			}
			else
				focusFields[i] = field;
		}
		return focusFields;
	}
	
	private static FeatureStateField[][] buildFeatureStateFields(Field[] focusFields, HashMap<String, List<String>> fieldValuesMap)
	{
		int size = focusFields.length;
		FeatureStateField[][] fsFields = new FeatureStateField[size][size];
		for(int i = 0;i<size;i++)
		{
			for(int j = 0;j<size;j++)
			{
				String featureFieldName = focusFields[i].getFieldName();
				String stateFieldName = focusFields[j].getFieldName();
				
				List<String> featureValueList = fieldValuesMap.get(featureFieldName);
				List<String> stateValueList = fieldValuesMap.get(stateFieldName);
				
				FeatureStateField fsf = new FeatureStateField(featureFieldName, stateFieldName,
						featureValueList, stateValueList);
				fsf.setFeatureFieldIndex(focusFields[i].getIndex());
				fsf.setStateFieldIndex(focusFields[j].getIndex());
				fsFields[i][j] = fsf;
			}
		}
		return fsFields;
	}
	
	public static Field getField(String fieldName, Field[] fields)
	{
		Field field = null;
		for(int i = 0;i<fields.length;i++)
		{
			if(fieldName.equalsIgnoreCase(fields[i].getFieldName()))
			{
				field = fields[i];
				break;
			}
		}
		return field;
	}
	
}
