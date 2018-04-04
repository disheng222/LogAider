package analysis.RAS;

/**
 * This class is only used for RAS log
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import element.Field;
import element.TmpValue;
import element.Value;
import util.ConversionHandler;
import util.PVFile;

public class CalculateCountsBasedonMsgTemplates {

	public static void main(String[] args)
	{
		if(args.length<6)
		{
			System.out.println("Usage: java CalculateCountsBasedonMsgTemplates [schemaPath] [templateIDIndex] [logDir] [log_extension] [templateCountFilePath] [outputDir]");
			System.out.println("Example: java CalculateCountsBasedonMsgTemplates /home/fti/Catalog-project/miralog/schema/basicSchema.txt 13 /home/fti/Catalog-project/miralog csvtmp /home/fti/Catalog-project/miralog/eventlog.20150401-20150501.csv.tmpcnt /home/fti/Catalog-project/miralog/fieldTmpCount");
			System.exit(0);
		}
		
		String schemaPath = args[0];
		int descIndex = Integer.parseInt(args[1]);
		String inputDir = args[2];
		String logExt = args[3];
		String tmpCountFilePath = args[4];
		String outputDir = args[5];
		
		System.out.println("Loading basic schema....");
		List<Field> fieldList = ExtractValueTypes4EachField.loadBasicSchema(schemaPath);
		Field[] fields = ConversionHandler.convertFieldList2FieldArray(fieldList);
	
		List<String> templateList = PVFile.readFile(tmpCountFilePath);
		double[] tmpCount = ConversionHandler.convertStringList2DoubleArray(templateList, 1);		
		
		List<String> inputFileList = PVFile.getFiles(inputDir, logExt);
		Collections.sort(inputFileList);
		//List<String> templateFileList = PVFile.getFiles(tmpDir, tmpExt);
		//Collections.sort(templateFileList);
		
		System.out.println("Processing the files....");
	
		Iterator<String> iter = inputFileList.iterator();
		//Iterator<String> iter2 = templateFileList.iterator();
		double initLogTime = System.currentTimeMillis()/1000.0;
		for(int s = 1;iter.hasNext();s++)
		{
			String fileName = iter.next();
			String filePath = inputDir+"/"+fileName;
			
			List<String> lineList = PVFile.readFile(filePath);
			Iterator<String> iter2 = lineList.iterator();
			double initLogTime2 = System.currentTimeMillis()/1000.0;
			for(int k = 0;iter2.hasNext();k++)
			{
				String line = iter2.next();
				String[] data = line.split(",");
				data = fixData(data, fields);
				int tmpID = Integer.parseInt(data[data.length-1]);
				for(int i = 1;i<fields.length;i++)
				{
					if(i!=descIndex) //do not consider the description field
					{
						String key = data[i].trim();
						Map<String, Value> map = fields[i].getValueMap();
						Value countValue = map.get(key);
						if(countValue==null)
						{
							countValue = new Value(key,tmpCount.length);
							map.put(key, countValue);
						}
						countValue.tmpCount[tmpID]++;
					}
					
				}
				if(k%3000==0)
					PVFile.showProgress(initLogTime2, k, lineList.size(), fileName);
			}
			
			PVFile.showProgress(initLogTime, s, inputFileList.size(), fileName+"----------");
		}
		
		System.out.println("Writing the results to: "+outputDir);
		outputResults(outputDir, fields, tmpCount);
		
		System.out.println("done.");
	}
	
	public static void outputResults(String outputDir, Field[] fields, double[] tmpCount)
	{
		List<TmpValue>[] tmpList = new ArrayList[tmpCount.length];
		for(int i = 0;i<tmpCount.length;i++)
			tmpList[i] = new ArrayList<TmpValue>();
		
		for(int i = 0;i<fields.length;i++)
		{
			List<String> resultList = new ArrayList<String>();
			List<String> resultList2 = new ArrayList<String>();
			String fieldName = fields[i].getFieldName();
			String outputFilePath = outputDir+"/"+fieldName;
			Map<String, Value> vMap = fields[i].getValueMap();
			int[] totalCount = new int[tmpCount.length];
			Iterator iter = vMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String)entry.getKey(); // the value of fieldName
				Value val = (Value)entry.getValue();
				StringBuilder s = new StringBuilder();
				StringBuilder ss = new StringBuilder();
				int sum = 0;
				for(int j = 0;j<val.tmpCount.length;j++)
				{
					sum += val.tmpCount[j];
					//TODO
					//tmpList[j].add(new TmpValue(fieldName+":"+key, val.tmpCount[j]));
					totalCount[j] += val.tmpCount[j];
					s.append(String.valueOf((int)val.tmpCount[j]));
					s.append(" ");
					ss.append(String.valueOf(val.tmpCount[j]/tmpCount[j]));
					ss.append(" ");
				}
				resultList.add(key+" "+s.toString().trim()+" | "+sum);
				resultList2.add(key+" "+ss.toString().trim());
			}
			
			String totalCS = "#";
			for(int j = 0;j<totalCount.length;j++)
				totalCS += " "+totalCount[j];
			
			resultList.add(0, totalCS);
			PVFile.print2File(resultList, outputFilePath+".cnt");
			PVFile.print2File(resultList2, outputFilePath+".per");
			System.out.println("~~~~~~finish : "+fieldName);
		}
			
/*		System.out.println("Generating the aggregated results.....");
		List<String> resultList3 = new ArrayList<String>();
		for(int i = 0;i < tmpList.length;i++)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(String.valueOf(i));
			sb.append(" ");
			Collections.sort(tmpList[i]);
			Iterator<TmpValue> it = tmpList[i].iterator();
			while(it.hasNext())
			{
				TmpValue s = it.next();
				sb.append(s.toString()+" ");
			}
			resultList3.add(sb.toString().trim());
		}
		System.out.println("writing data....");
		PVFile.print2File(resultList3, outputDir+"/tmpFieldValueCount.txt");*/
	}
	
	public static String[] fixData(String[] data, Field[] fields)
	{
		if(data.length == fields.length + 1)
			return data;
		else
		{
			//This means the the description field has "," (fields[13])
			int startIndex = 13;
			int endIndex = data.length - 4;
			String[] correctData = new String[fields.length+1];
			for(int i = 0; i < startIndex;i++)
				correctData[i] = data[i];
			String message = data[13];
			for(int i = 14;i<=endIndex;i++)
				message += " ; "+data[i];
			correctData[13] = message;
			for(int i = 0; i < 3;i++)
				correctData[14+i] = data[data.length-3+i];
			
			return correctData;
		}
	}
}