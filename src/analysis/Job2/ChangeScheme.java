package analysis.Job2;

import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class ChangeScheme {

	public static void main(String[] argv)
	{
		List<String> lineList = PVFile.readFile("/home/sdi/windows-xp/Work/Catalog-project/Catalog-data/miralog/RAS-Job/Job/basicSchema/basicSchema-job.txt");
		Iterator<String> iter = lineList.iterator();
		int j = 0;
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] data = line.split(","); 
			for(int i = 0;i<data.length;i++)
				System.out.println((j++)+" "+data[i]);
		}	
	}
}
