package analysis.darshan;

import java.util.ArrayList;
import java.util.List;

import util.PVFile;

public class ReplaceCommabyNewLine {

	public static void main(String[] args)
	{
		List<String> lineList = PVFile.readFile("/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/schema/darshanSchema.txt");
		String s = lineList.get(0);
		
		List<String> lineList2 = new ArrayList<String>();
		String[] fields = s.split(",");
		for(int i = 0;i<fields.length;i++)
		{
			lineList2.add(fields[i]);
		}
		PVFile.print2File(lineList2, "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/schema/darshanSchema.txt");
	}
}
