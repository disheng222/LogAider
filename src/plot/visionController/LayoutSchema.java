package plot.visionController;

import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class LayoutSchema {

	public static LayoutType[] layoutTypes = new LayoutType[5];
	
	public static LayoutType[] loadLayoutSchema(String layoutSchemaFile)
	{		
		List<String> lineList = PVFile.readFile(layoutSchemaFile);
		LayoutType lt = null;
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next().trim();
			if(line.startsWith("#") || !line.contains("="))
				continue;
			
			String[] s = line.split("=");
			String key = s[0].trim();
			if(key.equals("level")) //a new layoutType
			{
				int level = Integer.parseInt(s[1].trim());
				lt = new LayoutType(level);
				layoutTypes[level] = lt;
			}
			else
			{
				if(key.equals("fullName"))
					lt.setFullName(s[1]);
				if(key.equals("nickName"))
					lt.setNickname(s[1]);
				if(key.equals("customize"))
					lt.setCustomized(Boolean.parseBoolean(s[1]));
				if(key.equals("count"))
					lt.setCount(Integer.parseInt(s[1]));
				if(key.equals("row"))
					lt.setRow(Integer.parseInt(s[1]));
				if(key.equals("column"))
					lt.setColumn(Integer.parseInt(s[1]));
				if(key.equals("titleRepresentBase"))
					lt.setTitleRepresentBase(s[1]);
				if(key.equals("titleRepresentOffset"))
					lt.setOffset(Integer.parseInt(s[1]));
				if(key.equals("titleRowBase"))
					lt.setTitleRowBase(s[1]);
				if(key.equals("titleColumnBase"))
					lt.setTitleColumnBase(s[1]);
					
			}
		}
		
		return layoutTypes;
	}
}
