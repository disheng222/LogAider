package filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class Summarize_MonthlyFailureRate {

	
	public static void main(String[] args)
	{
		if(args.length<3)
		{
			System.out.println("Usage: java Summarize_MonthlyFailureRate [fullSchemaDir] [monthlyFailureRateDir_BaseOnMsgID] [extension]");
			System.out.println("Example: java Summarize_MonthlyFailureRate /home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/schema/withRatio /home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/FilterAndClassify/ts/fatalEventMonthDis mct");
			System.exit(0);
		}
		
		String fullSchemaDir = args[0];
		String monthlyLogDir = args[1];
		String extension = args[2];
		
		String categoryOutputFile = monthlyLogDir+"/categoryMonthlyFailRate.txt";
		String componentOutputFile = monthlyLogDir+"/componentMonthlyFailRate.txt";
		
		//build the data structure for component and category
		HashMap<String,FieldValueCountElement>[] categoryMap = new HashMap[12];
		List<FieldValueCountElement>[] categoryList = new List[12];
		HashMap<String,FieldValueCountElement>[] componentMap = new HashMap[12];
		List<FieldValueCountElement>[] componentList = new List[12];
		String categorySchemaFile = fullSchemaDir+"/CATEGORY.fsr";
		String componentSchemaFile = fullSchemaDir+"/COMPONENT.fsr";
		loadSchema(categorySchemaFile, componentSchemaFile, categoryMap, componentMap, categoryList, componentList);
		
		List<String> fileList = PVFile.getFiles(monthlyLogDir, extension);
		Iterator<String> iter = fileList.iterator();
		while(iter.hasNext())
		{
			String fileName = iter.next();
			String filePath = monthlyLogDir+"/"+fileName;
			List<String> lineList = PVFile.readFile(filePath);
			Iterator<String> iter2 = lineList.iterator();
			for(int i = 0;iter2.hasNext();i++)
			{
				String line = iter2.next();
				String[] s = line.split("\\s");
				String categoryName = s[2];
				int count = Integer.parseInt(s[4]);
				FieldValueCountElement fvce_cat = categoryMap[i].get(categoryName);
				fvce_cat.setCount(fvce_cat.getCount()+count);
				
				String componentName = s[3];
				FieldValueCountElement fvce_com = componentMap[i].get(componentName);
				fvce_com.setCount(fvce_com.getCount()+count);
			}
		}
		
		List<String> catResultList = new ArrayList<String>();
		List<String> comResultList = new ArrayList<String>();
		
		boolean writeFields = false;
		for(int i = 0;i<12;i++)
		{
			int month = i+1;
			Collections.sort(categoryList[i]);
			String categoryFields = "month";
				
			Collections.sort(componentList[i]);
			String componentFields = "month";
			
			String catString = String.valueOf(month);
			String comString = String.valueOf(month);
			
			Iterator<FieldValueCountElement> iter1 = categoryList[i].iterator();
			while(iter1.hasNext())
			{
				FieldValueCountElement fvce = iter1.next();
				if(!writeFields)
					categoryFields +=" "+fvce.getSanitizedValue();
				catString+=" "+fvce.getCount();
			}
			if(!writeFields)
				catResultList.add(categoryFields);
			catResultList.add(catString);
			
			Iterator<FieldValueCountElement> iter2 = componentList[i].iterator();
			while(iter2.hasNext())
			{
				FieldValueCountElement fvce = iter2.next();
				if(!writeFields)
					componentFields +=" "+fvce.getSanitizedValue();
				comString+=" "+fvce.getCount();
			}
			if(!writeFields)
				comResultList.add(componentFields);
			comResultList.add(comString);
			
			writeFields = true;
		}
		
		System.out.println("Writing results to "+categoryOutputFile);
		PVFile.print2File(catResultList, categoryOutputFile);
		System.out.println("Writing results to "+componentOutputFile);
		PVFile.print2File(comResultList, componentOutputFile);
	}
	
	public static void loadSchema(String categorySchemaFile, String componentSchemaFile, 
			HashMap<String,FieldValueCountElement>[] categoryMap, HashMap<String,FieldValueCountElement>[] componentMap, 
			List<FieldValueCountElement>[] categoryList, List<FieldValueCountElement>[] componentList)
	{
		for(int i = 0;i<12;i++)
		{
			categoryMap[i] = new HashMap<String,FieldValueCountElement>();
			componentMap[i] = new HashMap<String, FieldValueCountElement>();
			categoryList[i] = new ArrayList<FieldValueCountElement>();
			componentList[i] = new ArrayList<FieldValueCountElement>();
		}
		List<String> lineList = PVFile.readFile(categorySchemaFile);
		Iterator<String> iter = lineList.iterator();
		iter.next();// remove the field line
		while(iter.hasNext())
		{
			String line = iter.next();
			String value = line.split("\\s")[0];
			for(int i = 0;i<12;i++)
			{
				FieldValueCountElement fvce = new FieldValueCountElement(value, 0);
				categoryMap[i].put(value, fvce);
				categoryList[i].add(fvce);
			}
		}
		
		List<String> lineList2 = PVFile.readFile(componentSchemaFile);
		Iterator<String> iter2 = lineList2.iterator();
		iter2.next();// remove the field line
		while(iter2.hasNext())
		{
			String line = iter2.next();
			String value = line.split("\\s")[0];
			for(int i = 0;i<12;i++)
			{
				FieldValueCountElement fvce = new FieldValueCountElement(value, 0);
				componentMap[i].put(value, fvce);
				componentList[i].add(fvce);
			}
		}
	}
}
