package analysis.Job2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class TransposeMatrix {

	public static void main(String[] args)
	{
		//String file = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/featureState2/numMultilocationTasksLog.fsc";
		//String file = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/darshan/analysis/totalReadByteErrLog.fsc";
		//String file = "/home/sdi/windows-xp/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/featureState2/corehourErrLin.fsc";
		String file = "/home/sdi/windows-xp/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/rasAffectedJobs/featureState2/corehourErrLog.fsc";
		//String file = "/home/sdi/windows-xp/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/featureState2/runTimeErrLog.fsc";
		List<String> lineList = PVFile.readFile(file);
		
		Iterator<String> iter = lineList.iterator();
		
		iter.next(); //filter the meta data
		List<String> fieldList = new ArrayList<String>();
		
		int row = lineList.get(1).split("\\s").length-1;
		int col = lineList.size()-1;
		int[][] result = new int[row][col];
		for(int i = 0;iter.hasNext();i++) 
		{
			String line = iter.next();
			String[] s = line.split("\\s");
			String field = s[0];
			fieldList.add(field);
			for(int j = 1;j<s.length;j++)
			{
				result[j-1][i] = Integer.parseInt(s[j]);
			}
		}
		
		List<String> resultList = new ArrayList<String>();
		String fields = "#";
		Iterator<String> iter2 = fieldList.iterator();
		while(iter2.hasNext())
		{
			fields += " "+iter2.next();
		}
		resultList.add(fields);
		
		for(int i = 0;i<row;i++)
		{
			String s = "-"+i+"-";
			for(int j = 0;j<col;j++)
			{
				s+=" "+result[i][j];
			}
			resultList.add(s);
		}
		
		PVFile.print2File(resultList, file+"t");
		System.out.println("outputfile = "+file+"t");
	}
}
