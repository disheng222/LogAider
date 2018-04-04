package test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class SplitRASLog {

	public static void main(String[] args)
	{
		String fileName = "/home/fti/ELSA-HELO/offline/release2.0/output/eventlog.20150401-20150408_l0c0";
		String outputDir = "/home/fti/ELSA-HELO/offline/release2.0/output";
		int splitNum = 100;
		List<String> lineList = PVFile.readFile(fileName);
		int interval = lineList.size()/splitNum;
		List<String> newList = new ArrayList<String>();
		Iterator<String> iter = lineList.iterator();
		int n = 0;
		for(int i = 0;iter.hasNext();i++)
		{
			String line = iter.next();
			newList.add(line);
			if(i%interval==interval-1)
			{
				PVFile.print2File(newList, outputDir+"/eventlog"+n+"_l0c0");
				n++;
				newList.clear();
			}
		}
		PVFile.print2File(newList, outputDir+"/eventlog"+n+"_l0c0");
		
		System.out.println("outputDir: "+outputDir);
		System.out.println("done.");
	}
}
