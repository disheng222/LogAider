package test;

import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class Check {

	static String fileName = "/home/fti/ELSA-HELO/offline/release2.0/output/MESSAGE_l0c0";
	public static void main(String[] args)
	{
/*		System.out.println("start....");
		List<String> list = PVFile.readFile(fileName);
		Iterator<String> iter = list.iterator();
		double initLogTime = System.currentTimeMillis()/1000.0;
		for(int i = 0;iter.hasNext();i++)
		{
			String line = iter.next();
			if(line.equalsIgnoreCase("end_job"))
				System.out.println("line #: "+i);
			//if(i%100==0)
			//	PVFile.showProgress(initLogTime, i, list.size(), line);
		}
		System.out.println("done.");*/
		
		String s  = "\"disheng\"";
		String s_ = s.substring(1,s.length()-1);
		System.out.println(s+" "+s_);
	}
}
