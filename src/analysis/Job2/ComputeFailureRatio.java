package analysis.Job2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class ComputeFailureRatio {

	public static void main(String[] args)
	{
		//String dir = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/featureState2";
		//String dir = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/featureState/QUEUE_NAME";
		//String dir = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/featureState/PROJECT_NAME_GENID";
		//String dir = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/featureState/USERNAME_GENID";
		//String dir = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/darshan/analysis";
		String dir = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/featureState2";
		//String dir = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/rasAffectedJobs/featureState2";
		//String extension = "fs2";
		String extension = "fsct2";
		//String extension = "fsct";
		List<String> fileList = PVFile.getFiles(dir, extension);
		Iterator<String> iter = fileList.iterator();
		while(iter.hasNext())
		{
			String fileName = iter.next();
			String filePath = dir+"/"+fileName;
			List<String> lineList = PVFile.readFile(filePath);
			System.out.println(filePath);
			List<String> resultList = new ArrayList<String>();
			List<String> resultList2 = new ArrayList<String>();
			Iterator<String> iter2 = lineList.iterator();
			while(iter2.hasNext())
			{
				String line = iter2.next();
				String[] s = line.split("\\s");
				String newLine = "", newLine2 = "";
				if(line.startsWith("#"))
				{
					newLine2 = newLine = "#";
					for(int i = 1;i<s.length;i++)
						newLine2 = newLine += " "+s[i];
				}	
				else
				{
					
					newLine2 = newLine = s[0];
					int sum = 0, sum2 = 0;
					for(int i = 1;i<s.length;i++)
						sum += Integer.parseInt(s[i]);
					for(int i = 1;i<s.length;i++) //excluding the normal job state (with exit_code = 0)
						sum2 += Integer.parseInt(s[i]);
					for(int i = 1;i<s.length;i++) //excluding the normal job state (with exit_code = 0)
					{
						newLine+=" "+(1.0*Integer.parseInt(s[i])/sum);
						newLine2 += " "+(1.0*Integer.parseInt(s[i])/sum2);
					}
				}
				
/*				if(line.startsWith("#"))
				{
					newLine2 = newLine = "#";
					for(int i = 2;i<s.length;i++)
						newLine2 = newLine += " "+s[i];
				}	
				else
				{
					
					newLine2 = newLine = s[0];
					int sum = 0, sum2 = 0;
					for(int i = 1;i<s.length;i++)
						sum += Integer.parseInt(s[i]);
					for(int i = 2;i<s.length;i++) //excluding the normal job state (with exit_code = 0)
						sum2 += Integer.parseInt(s[i]);
					for(int i = 2;i<s.length;i++) //excluding the normal job state (with exit_code = 0)
					{
						newLine+=" "+(1.0*Integer.parseInt(s[i])/sum);
						newLine2 += " "+(1.0*Integer.parseInt(s[i])/sum2);
					}
				}*/
				
				resultList.add(newLine);
				resultList2.add(newLine2);
			}
			
			PVFile.print2File(resultList, filePath+"r1");
			PVFile.print2File(resultList2, filePath+"r2");
		}
	}
}
