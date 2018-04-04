package util;

import java.util.ArrayList;
import java.util.List;

public class RecordSplitter {

	
	public static String[] partition(String recordString, String separator)
	{
		if(!recordString.contains("\""))
			return recordString.split(separator);
		
		List<String> itemList = new ArrayList<String>();
		String[] s = recordString.split("\"");
		int i = 0;
		if(recordString.startsWith("\""))
			i = 1; //the 0th one would be ""
		for(;i<s.length;i++)
		{
			String v = s[i];
			if(v.equals(separator))
			{
				//do nothing
			}
			else if(v.startsWith(separator)&&v.endsWith(separator))
			{
				String vv = v.substring(1, v.length()-1);
				String[] vs = vv.split(separator);
				for(int j = 0;j<vs.length;j++)
				{
					itemList.add(vs[j].trim());
				}
			}
			else if(!v.startsWith(separator) && !v.endsWith(separator))
			{
				itemList.add(v.trim());
			}
			else
			{
				if(v.startsWith(separator))
				{
					v = v.substring(1,v.length());
				}
				else if(v.endsWith(separator))
				{
					v = v.substring(0, v.length()-1);
				}
				String[] vs = v.split(separator);
				for(int j = 0;j<vs.length;j++)
				{
					itemList.add(vs[j].trim());
				}
			}
		}
		
		if(recordString.startsWith(separator))
			itemList.add(0, "");
		if(recordString.endsWith(separator))
			itemList.add("");
		String[] result = ConversionHandler.convertStringList2StringArray(itemList);
		return result;
	}
	
	public static String[] partition(String recordString)
	{
		if(!recordString.contains("\""))
			return recordString.split(",");
		
		List<String> itemList = new ArrayList<String>();
		String[] s = recordString.split("\"");
		int i = 0;
		if(recordString.startsWith("\""))
			i = 1; //the 0th one would be ""
		for(;i<s.length;i++)
		{
			String v = s[i];
			if(v.equals(","))
			{
				//do nothing
			}
			else if(v.startsWith(",")&&v.endsWith(","))
			{
				String vv = v.substring(1, v.length()-1);
				String[] vs = vv.split(",");
				for(int j = 0;j<vs.length;j++)
				{
					itemList.add(vs[j].trim());
				}
			}
			else if(!v.startsWith(",") && !v.endsWith(","))
			{
				itemList.add(v.trim());
			}
			else
			{
				if(v.startsWith(","))
				{
					v = v.substring(1,v.length());
				}
				else if(v.endsWith(","))
				{
					v = v.substring(0, v.length()-1);
				}
				String[] vs = v.split(",");
				for(int j = 0;j<vs.length;j++)
				{
					itemList.add(vs[j].trim());
				}
			}
		}
		
		if(recordString.startsWith(","))
			itemList.add(0, "");
		if(recordString.endsWith(","))
			itemList.add("");
		String[] result = ConversionHandler.convertStringList2StringArray(itemList);
		return result;
	}
}
