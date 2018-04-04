package filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import util.PVFile;

//Compute the number of events for each messageID, based on the files xxxxxxxx.fltr
public class Summarize1 {

	public static HashMap<Integer, MonthElement> map = new HashMap<Integer, MonthElement>();			
	public static void main(String[] args)
	{
		if(args.length<3)
		{
			System.out.println("Usage: java Summarize1 [inputDir] [extension] [outputDir]");
			System.out.println("Example: java Summarize1 /home/fti/Catalog-project/miralog/RAS-Job/RAS/FilterAndClassify fltr /home/fti/Catalog-project/miralog/RAS-Job/RAS/FilterAndClassify/summarize/days");
			System.exit(0);
		}
		
		String inputDir = args[0];
		String extension = args[1];
		String outputDir = args[2];
		
		List<String> resultList = new ArrayList<String>();
		
		List<String> fileList = PVFile.getFiles(inputDir, extension);
		Iterator<String> iter = fileList.iterator();
		while(iter.hasNext())
		{
			String fileName = iter.next();
//			if(!NumericChecker.isNumeric(fileName.split("\\.")[0]))
//				continue;
			//System.out.println("fileName="+fileName);
			String filePath = inputDir+"/"+fileName;
			List<String> lineList = PVFile.readFile(filePath);
			int num = lineList.size();
			String firstItem = lineList.get(0);
			String[] s = firstItem.split("\\s+");
			String msgID = s[1];
			String severity = s[2];
			String category = s[3];
			String component = s[4];
			
			List<MonthElement> meList = new ArrayList<MonthElement>();
			
			for(int i = 1;i<=12;i++)
			{
				MonthElement me = new MonthElement(i, severity, category, component, 0);
				map.put(i, me);
				meList.add(me);
			}
			
			Iterator<String> iter2 = lineList.iterator();
			while(iter2.hasNext())
			{
				String line2 = iter2.next();
				String[] ss = line2.split("\\s+");
				int month = Integer.parseInt(ss[5].split(";")[0].split("-")[1]);
				MonthElement me = map.get(month);
				me.setNum(me.getNum()+1);
			}
			String msg = msgID+" "+severity+" "+category+" "+component+" "+num;
			resultList.add(msg);
			
			String outputFilePath = inputDir+"/fatalEventMonthDis/"+fileName.split("\\.")[0]+".mct";
			Collections.sort(meList);
			System.out.println("Writing results to "+outputFilePath);
			PVFile.print2File(meList, outputFilePath);
		}
		PVFile.print2File(resultList, outputDir+"/fatal-msg-count.txt");
		System.out.println("output: "+outputDir+"/fatal-msg-count.txt");
		System.out.println("done.");
	}
}

