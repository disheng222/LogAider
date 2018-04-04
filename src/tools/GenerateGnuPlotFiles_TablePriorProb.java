package tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class GenerateGnuPlotFiles_TablePriorProb {

	public static void main(String[] args)
	{
		
		if(args.length<4)
		{
			System.out.println("Usage: java GenerateGnuPlotFiles_Table [workingDir] [templateFile] [srcDataFileDir] [extension]");
			System.out.println("Example: java GenerateGnuPlotFiles_Table /home/fti/Catalog-project/miralog/gnuplot /home/fti/Catalog-project/miralog/gnuplot/temp.p /home/fti/Catalog-project/miralog/schema/fullSchema/withRatio fsr");
			System.exit(0);
		}
		
		String workingDir = args[0];
		String templateFile = args[1];
		String srcDir = args[2];
		String ext = args[3];
		
		String[] s = srcDir.split("/");
		String outputDir = workingDir+"/"+s[s.length-1];
		
		//load gnuplot's template file
		List<String> tmpLineList = PVFile.readFile(templateFile);
		
		List<String> dataFileList = PVFile.getFiles(srcDir, ext);
		Iterator<String> iter = dataFileList.iterator();
		while(iter.hasNext())
		{
			String fileName = iter.next();
			String dataFilePath = srcDir+"/"+fileName;
			String fieldName = fileName.split("\\.")[0];
			List<String> pLineList = generatePLineList(tmpLineList, dataFilePath, fieldName);
			PVFile.print2File(pLineList, outputDir+"/"+fieldName+".p");
		}
		
		System.out.println("output dir: "+outputDir);
		System.out.println("done.");
	}
	
	public static List<String> generatePLineList(List<String> tempLinelist, String dataFile, String fieldName)
	{
		List<String> resultList = new ArrayList<String>();
		
		Iterator<String> iter = tempLinelist.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			line = line.replace("ITEM", fieldName);
			line = line.replace("VALUE", dataFile);
			resultList.add(line);
		}
		return resultList;
	}
}
