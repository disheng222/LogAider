package analysis.Job2;

import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class CheckParetoPrinciple {

	public static void main(String[] args)
	{
		//String file = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/fullschema/withCount/USERNAME_GENID.fsc";
		String file = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/userJobStatFile.txt";
		int dataIndex = 5; //1 (for fullSchema or 5 for corehours (userStat.txt)
		
		List<String> lineList = PVFile.readFile(file);
		
		int nbKeys = lineList.size();
		double t80 = nbKeys*0.8;
		double t85 = nbKeys*0.85;
		double t90 = nbKeys*0.9;
		double t95 = nbKeys*0.95;
		
		double totalValueCount = 0;
		Iterator<String> iter = lineList.iterator();
		for(int i = 0;iter.hasNext();i++)
		{
			String line = iter.next();
			if(line.startsWith("#"))
				continue;
			String[] s = line.split("\\s");
			//String keyID = s[0];
			double count = Double.parseDouble(s[dataIndex]);
			totalValueCount+=count;
		}
		
		System.out.println("nbKeys = "+nbKeys+", totalValueCount = "+totalValueCount);
		
		boolean printT80 = false, printT85 = false, printT90 = false, printT95 = false;
		double sumValue = 0; 
		iter = lineList.iterator();
		for(int i = 0; iter.hasNext(); i++)
		{
			String line = iter.next();
			if(line.startsWith("#"))
				continue;
			String[] s = line.split("\\s");
			double count = Double.parseDouble(s[dataIndex]);
			
			if(i>=t80 && printT80 == false)
			{
				System.out.println("t80: sumValue = "+sumValue+", 80% keys' value takes the ratio of "+(sumValue*1.0/totalValueCount)+", and 20% takes "+(1-(sumValue*1.0/totalValueCount)));
				printT80 = true;
			}
			if(i>=t85 && printT85 == false)
			{
				System.out.println("t85: sumValue = "+sumValue+", 85% keys' value takes the ratio of "+(sumValue*1.0/totalValueCount)+", and 15% takes "+(1-(sumValue*1.0/totalValueCount)));
				printT85 = true;
			}
			if(i>=t90 && printT90 == false)
			{
				System.out.println("t90: sumValue = "+sumValue+", ratio = "+(sumValue*1.0/totalValueCount)+", 10% takes "+(1-(sumValue*1.0/totalValueCount)));
				printT90 = true;				
			}
			if(i>=t95 && printT95 == false)
			{
				System.out.println("t95: sumValue = "+sumValue+", ratio = "+(sumValue*1.0/totalValueCount)+", 5% tasks "+(1-(sumValue*1.0/totalValueCount)));
				printT95 = true;
				break;
			}
			sumValue += count;
		}
	}
}
