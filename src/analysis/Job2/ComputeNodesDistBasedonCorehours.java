package analysis.Job2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.PVFile;
import util.RecordSplitter;

public class ComputeNodesDistBasedonCorehours {

	static String fileName = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/rasAffectedJobs.csv";
	static String outputFile = fileName+".chn"; //core-hours-nodes
	static String outputFile2 = fileName+".chn.cnt";
	public static void main(String[] args) {
		
		double[][] contTable = new double[10][8];
		List<String> resultList = new ArrayList<String>();	
		List<String> resultList2 = new ArrayList<String>();
		List<String> lineList = PVFile.readFile(fileName);
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] s = RecordSplitter.partition(line);
			double coreHours = Double.parseDouble(s[25]);
			double nodes = Double.parseDouble(s[14]);
			double hours = Double.parseDouble(s[13])/3600.0;
			
			int corehourIndex = corehoursToIndex(coreHours);
			int nbNodes = intNodes(nodes);
			if(nbNodes<0)
			{
				System.out.println("Error: negative number of nodes...");
				System.exit(0);
			}
			contTable[corehourIndex][nbNodes]++;
			resultList.add(coreHours+" "+nodes+" "+hours);
		}
		
		PVFile.print2File(resultList, outputFile);
		System.out.println("outputFile="+outputFile);
		
		for(int i = 0;i<10;i++)
		{
			String s = "";
			int sum = 0;
			for(int j = 0;j<8;j++)
			{
				System.out.print(contTable[i][j]+" ");
				sum += contTable[i][j];
			}
			System.out.println();
			
			for(int j = 0;j<8;j++)
			{
				contTable[i][j]/=sum;
				s += contTable[i][j] + " ";
			}
			resultList2.add(s.trim());
		}
		
		PVFile.print2File(resultList2, outputFile2);
		System.out.println("outputFile2="+outputFile2);
	}
	
	static String index2CorehoursString_log(int i)
	{
		if(i==0)
			return "[0,1000]";
		else
		{
			return "["+1000*Math.pow(2, i-1)+","+1000*Math.pow(2, i)+"]";
		}
	}
	
	static int corehoursToIndex(double corehours)
	{
		if(corehours<=1000)
			return 0;
		else if(corehours<=2000)
			return 1;
		else if(corehours<=4000)
			return 2;
		else if(corehours<=8000)
			return 3;
		else if(corehours<=16000)
			return 4;
		else if(corehours<=32000)
			return 5;
		else if(corehours<=64000)
			return 6;
		else if(corehours<=128000)
			return 7;
		else if(corehours<=256000)
			return 8;
		else
			return 9;
	}
	
	static int intNodes(double nodes)
	{
		int d = (int)nodes;
		if(d==512)
			return 0;
		else if(d==1024)
			return 1;
		else if(d==2048)
			return 2;
		else if(d==4096)
			return 3;
		else if(d==8192)
			return 4;
		else if(d>8192 && d<=16384)
			return 5;
		else if(d>16384 && d<=32768)
			return 6;
		else if(d>32768 && d<=65536)
			return 7;
		else 
			return -1;
			
	}
}
