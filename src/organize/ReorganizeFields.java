package organize;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class ReorganizeFields {

	public static void main(String[] args)
	{
		if(args.length<3)
		{
			System.out.println("Usage: java ReorganizeFields [logDir] [logExt] [outputDir]");
			System.out.println("Example: java ReorganizeFields /home/fti/Catalog-project/miralog csvtmp(or csv) /home/fti/Catalog-project/miralog/reorganize");
			System.exit(0);
		}
		
		String logDir = args[0];
		String logExt = args[1];
		String outputDir = args[2];
		
		List<String> fileList = PVFile.getFiles(logDir, logExt);
		Iterator<String> iter = fileList.iterator();
		while(iter.hasNext())
		{
			String fileName = iter.next();
			System.out.println("Processing file: "+fileName);
			String filePath = logDir+"/"+fileName;
			List<String> lineList = PVFile.readFile(filePath);
			double initLogTime = System.currentTimeMillis()/1000.0;
			List<String> newLineList = new ArrayList<String>();
			Iterator<String> iter2 = lineList.iterator();
			for(int k = 0;iter2.hasNext();k++)
			{
				String line = iter2.next();
				String[] s = line.split(",");
				StringBuilder sb = new StringBuilder();
				sb.append(s[0].trim());// record id
				sb.append(",");
				sb.append(s[1].trim()); //message id
				sb.append(",");
				sb.append(s[5].trim());//time
				sb.append(",");
				sb.append(s[6].trim());//job id
				sb.append(",");
				sb.append(s[7].trim()).append(":").append(s[8].trim()); //block:location
				sb.append(s[13].trim());//message
				sb.append(",");
				sb.append(s[16].trim()); //template id of the message
				
				newLineList.add(sb.toString());
				
				if(k%50000==0)
					PVFile.showProgress(initLogTime, k++, lineList.size(), fileName);
			}
			
			String outputFile = outputDir+"/"+fileName+".rg";
			System.out.println("Writing results to "+outputFile);
			PVFile.print2File(newLineList, outputFile);
		}
		
		
		System.out.println("Done.");
	}
}
