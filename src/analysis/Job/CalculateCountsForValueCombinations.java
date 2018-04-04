package analysis.Job;

/**
 * Calculate the number of value combinations by brute-force method
 * valid for both RAS and Job log
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import element.Field;
import element.UserField;
import util.ConversionHandler;
import util.PVFile;
import util.RecordSplitter;

public class CalculateCountsForValueCombinations {

	static HashMap<String, Integer> fieldIDMap = new HashMap<String, Integer>();
	static HashMap<String, Integer> fieldValueCombinationCountMap = new HashMap<String, Integer>();
	static String[] fields;
	static int[] fieldIndex;
	static List<String>[] fieldValues;
	
	public static void main(String[] args) {

		if (args.length < 6) {
			System.out
					.println("Usage: java CalculateCountsForValueCombinations [basicSchemaFile] [fullSchemaDir] [fullSchemaExt] [logDir] [extension] [outputFile] [fields....]");
			System.out
					.println("Example: java CalculateCountsForValueCombinations /home/fti/Catalog-project/miralog/RAS-Job/Job/basicSchema/basicSchema.txt /home/fti/Catalog-project/miralog/fullSchema/fullSchema/withRatio fsr /home/fti/Catalog-project/miralog/RAS-Job/Job csv "
							+ "/home/fti/Catalog-project/miralog/fieldValueCombination capability exit_code major_project mode nodes_cost percentile prod_queue project_name queue science_field science_field_short size_buckets3 size_cost user");
			System.exit(0);
		}

		String basicSchamaFile = args[0];
		String fullSchemaDir = args[1];
		String fullSchemaExt = args[2];
		String logDir = args[3];
		String logExt = args[4];
		String outputDir = args[5];
		
		List<Field> completeFieldList = ExtractValueTypes4EachField.loadBasicSchema(basicSchamaFile);
		Field[] completeFields = ConversionHandler.convertFieldList2FieldArray(completeFieldList);
		for(int i = 0;i<completeFields.length;i++)
			fieldIDMap.put(completeFields[i].getFieldName(), completeFields[i].getIndex());
		
		List<UserField> fieldList = new ArrayList<UserField>();
		for(int i = 0;i<args.length-6;i++)
		{
			String fieldName = args[i+6];
			UserField uf = new UserField(fieldName);
			fieldList.add(uf);
			int fieldIndex = fieldIDMap.get(fieldName);
			uf.setFieldIndex(fieldIndex);
		}
		Collections.sort(fieldList);
		
		fields = new String[fieldList.size()];
		fieldIndex = new int[fields.length];
		Iterator<UserField> uit = fieldList.iterator();
		for(int i = 0;uit.hasNext();i++)
		{
			UserField uf = uit.next();
			fields[i] = uf.getFieldName();
			fieldIndex[i] = uf.getFieldIndex();
		}
		
		fieldValues = new ArrayList[fields.length];
		for(int i = 0;i<fieldValues.length;i++)
		{
			fieldValues[i] = new ArrayList<String>();
			String schemaFilePath = fullSchemaDir+"/"+fields[i]+"."+fullSchemaExt;
			List<String> lineList = PVFile.readFile(schemaFilePath);
			Iterator<String> iter = lineList.iterator();
			while(iter.hasNext())
			{
				String line = iter.next();
				if(!line.startsWith("#"))
				{
					fieldValues[i].add(line.split("\\s")[0]);
				}
			}
		}
		
		List<String> logFileList = PVFile.getFiles(logDir, logExt);
		Iterator<String> iter = logFileList.iterator();
		int totalMsgCount = 0;
		double initLogTime = System.currentTimeMillis()/1000.0;
		while(iter.hasNext())
		{
			String logFileName = iter.next();
			System.out.println("Processing "+logFileName);
			String logFilePath = logDir+"/"+logFileName;
			List<String> lineList = PVFile.readFile(logFilePath);
			totalMsgCount += lineList.size();
			Iterator<String> iter2 = lineList.iterator();
			for(int i = 0;iter2.hasNext();i++)
			{
				String line = iter2.next();
				analyzeMessage(line);
				if(i%1000==0)
					PVFile.showProgress(initLogTime, i, lineList.size(), "processing "+logFileName);
			}
		}
		
		System.out.println("Sorting the list....");
		List<String> vcCountList = new ArrayList<String>();
		Iterator iter3 = fieldValueCombinationCountMap.entrySet().iterator(); 
		while (iter3	.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter3.next(); 
		    String key = (String)entry.getKey(); 
		    Integer count = (Integer)entry.getValue(); 
		    vcCountList.add(key+" "+count);
		} 
		Collections.sort(vcCountList);
		vcCountList.add(0, "# totalMsgCount="+totalMsgCount);
		
		List<String> fieldWithIndexList = new ArrayList<String>(fieldList.size());
		fieldWithIndexList.add("# fieldName fieldIndex");
		Iterator<UserField> itt = fieldList.iterator();
		while(itt.hasNext())
		{
			String fieldNameIndex = itt.next().toString();
			fieldWithIndexList.add(fieldNameIndex);
		}
		PVFile.print2File(fieldWithIndexList, outputDir+"/fieldList.txt");
		
		System.out.println("Writing results to outputDir = "+outputDir);
		PVFile.print2File(vcCountList, outputDir+"/vc.count");
		System.out.println("done.");
	}
	
	public static void analyzeMessage(String line)
	{
		String[] s = RecordSplitter.partition(line);
		ExtractValueTypes4EachField.fixData(s);
		
		//String[] s = line.split(",");
		
		//1 item
		for(int i = 0;i<fieldIndex.length;i++)
		{
			int index = fieldIndex[i];
			StringBuilder sb = new StringBuilder();
			sb.append(index).append(":").append(s[index].trim());
			String vc = sb.toString();
			Integer sum = fieldValueCombinationCountMap.get(vc);
			if(sum==null)
				sum = new Integer(0);
			fieldValueCombinationCountMap.put(vc, sum+1);
		}
		
		//2 items
		for(int i = 0;i<fieldIndex.length-1;i++)
			for(int j = i+1;j<fieldIndex.length;j++)
			{
				int index1 = fieldIndex[i];
				int index2 = fieldIndex[j];
				StringBuilder sb = new StringBuilder();
				sb.append(index1).append(":").append(s[index1].trim()).append(",");
				sb.append(index2).append(":").append(s[index2].trim());
				String vc = sb.toString();
				Integer sum = fieldValueCombinationCountMap.get(vc);
				if(sum==null)
					sum = new Integer(0);
				fieldValueCombinationCountMap.put(vc, sum+1);
			}
		
		//3 items
		for(int i = 0;i<fieldIndex.length-2;i++)
			for(int j = i+1;j<fieldIndex.length-1;j++)
				for(int k = j+1;k<fieldIndex.length;k++)
				{
					int index1 = fieldIndex[i];
					int index2 = fieldIndex[j];
					int index3 = fieldIndex[k];
					StringBuilder sb = new StringBuilder();
					sb.append(index1).append(":").append(s[index1].trim()).append(",");
					sb.append(index2).append(":").append(s[index2].trim()).append(",");
					sb.append(index3).append(":").append(s[index3].trim());
					
					String vc = sb.toString();
					Integer sum = fieldValueCombinationCountMap.get(vc);
					if(sum==null)
						sum = new Integer(0);
					fieldValueCombinationCountMap.put(vc, sum+1);
				}
		
		//4 items
		for(int i = 0;i<fieldIndex.length-2;i++)
			for(int j = i+1;j<fieldIndex.length-1;j++)
				for(int k = j+1;k<fieldIndex.length;k++)
					for(int p = k+1;p<fieldIndex.length;p++)
					{
						int index1 = fieldIndex[i];
						int index2 = fieldIndex[j];
						int index3 = fieldIndex[k];
						int index4 = fieldIndex[p];
						StringBuilder sb = new StringBuilder();
						sb.append(index1).append(":").append(s[index1].trim()).append(",");
						sb.append(index2).append(":").append(s[index2].trim()).append(",");
						sb.append(index3).append(":").append(s[index3].trim()).append(",");
						sb.append(index4).append(":").append(s[index4].trim());
						
						String vc = sb.toString();
						Integer sum = fieldValueCombinationCountMap.get(vc);
						if(sum==null)
							sum = new Integer(0);
						fieldValueCombinationCountMap.put(vc, sum+1);
					}
		
		//5 items
		for(int i = 0;i<fieldIndex.length-2;i++)
			for(int j = i+1;j<fieldIndex.length-1;j++)
				for(int k = j+1;k<fieldIndex.length;k++)
					for(int p = k+1;p<fieldIndex.length;p++)
						for(int q = p+1;q<fieldIndex.length;q++)
						{
							int index1 = fieldIndex[i];
							int index2 = fieldIndex[j];
							int index3 = fieldIndex[k];
							int index4 = fieldIndex[p];
							int index5 = fieldIndex[q];
							StringBuilder sb = new StringBuilder();
							sb.append(index1).append(":").append(s[index1].trim()).append(",");
							sb.append(index2).append(":").append(s[index2].trim()).append(",");
							sb.append(index3).append(":").append(s[index3].trim()).append(",");
							sb.append(index4).append(":").append(s[index4].trim()).append(",");
							sb.append(index5).append(":").append(s[index5].trim());
							
							String vc = sb.toString();
							Integer sum = fieldValueCombinationCountMap.get(vc);
							if(sum==null)
								sum = new Integer(0);
							fieldValueCombinationCountMap.put(vc, sum+1);
						}		
	}
}
