/**
 * @author Sheng Di
 * @class ComputeLocationBasedOnMIRCode.java
 * @description  
 */

package tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class GenerateGnuPlotFiles_TableFeatureState {
	
	public static void main(String[] args)
	{
		if(args.length<4)
		{
			System.out.println("Usage: java GenerateGnuPlotFiles_Table [workingDir] [templateFile] [srcDataFileDir] [extension]");
			System.out.println("Example: java GenerateGnuPlotFiles_Table /home/fti/Catalog-project/miralog/gnuplot /home/fti/Catalog-project/miralog/gnuplot/temp-featurestate.p /home/fti/Catalog-project/miralog/featureState pr");
			System.exit(0);
		}
		
		String workingDir = args[0];
		String templateFile = args[1];
		String srcDir = args[2];
		String ext = args[3];
		
		String[] s = srcDir.split("/");
		String featureStateDirName = s[s.length-1];
		String outputDir = workingDir+"/"+featureStateDirName; //is like: .../featureState
		
		System.out.println("Start running....");
		//load gnuplot's template file
		List<String> tmpLineList = PVFile.readFile(templateFile);
		
		List<String> fieldList = PVFile.getDir(srcDir);
		Iterator<String> fieldIter = fieldList.iterator();
		double initLogTime = System.currentTimeMillis()/1000.0;
		for(int i = 0;fieldIter.hasNext();i++)
		{
			String fieldDir = fieldIter.next();
			String fieldDirPath = srcDir+"/"+fieldDir;
			List<String> dataFileList = PVFile.getFiles(fieldDirPath, ext);
			Iterator<String> iter = dataFileList.iterator();
			while(iter.hasNext())
			{
				String fileName = iter.next();
				String dataFilePath = fieldDirPath+"/"+fileName;
				String fieldName = fileName.split("\\.")[0];
				List<String> pLineList = generatePLineList(tmpLineList, dataFilePath, fieldName);
				PVFile.print2File(pLineList, outputDir+"/"+fieldDir+"/"+fieldName+".p");
			}
			PVFile.showProgress(initLogTime, i, fieldList.size(), "");
		}
		System.out.println("outputDir: "+outputDir);
		System.out.println("done.");
	}
	
	public static List<String> generatePLineList(List<String> tempLinelist, 
			String dataFile, String fieldName)
	{
		List<String> resultList = new ArrayList<String>();
		
		Iterator<String> iter = tempLinelist.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			line = line.replace("EPSFILENAME", fieldName);
			resultList.add(line);
		}
		
		List<String> lineList = PVFile.readFile(dataFile);
		String fieldLine = lineList.get(0);
		String[] s = fieldLine.split("\\s");
		
		//build the plot line
		String plotLine = "plot";
		
		for(int i = 1;i < s.length;i++)
		{
			String itemS = "'"+dataFile+"' using "+(i+1)+":xtic(1) ti \""+s[i]+"\"";
			if(i!=s.length-1)
				itemS += ",";
			plotLine += itemS;
		}
		
		resultList.add(plotLine);
		
		return resultList;
	}
}
