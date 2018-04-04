package analysis.significance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class ChiSquareBatchTest2 {

	public static float[] alpha = {0.001f, 0.002f, 0.005f, 0.01f, 0.02f, 0.025f, 0.05f, 0.1f, 0.2f};
	
	public static void main(String[] args)
	{
		if(args.length < 2)
		{
			System.out.println("Usage: java ChiSquaredTest [dir_path_of_contingency_table_dir_path] [extension] [outputDirPath]");
			System.out.println("Example: java ChiSquaredTest /home/fti/Catalog-project/miralog/RAS-Job/Job/featureState fs /home/fti/Catalog-project/miralog/RAS-Job/Job/featureState/chiSquaredCorrelation");
			System.exit(0);
		}
		String contingTableDirPath = args[0];
		String extension = args[1];
		
		String outputFile = contingTableDirPath+".ctst";
		processOneContingTableDir(contingTableDirPath, extension, outputFile);
		
		System.out.println("done.");
	}
	
	public static void processOneContingTableDir(String contingTableDirPath, String extension, 
			String outputFile)
	{
		List<String> resultList = new ArrayList<String>();
		
		List<String> fileNameList = PVFile.getFiles(contingTableDirPath, extension);
		Iterator<String> iter = fileNameList.iterator();
		while(iter.hasNext())
		{
			String fileName = iter.next();
//			if(fileName.startsWith("00070219"))
//				System.out.println();
			String filePath = contingTableDirPath+"/"+fileName;
			System.out.println("Processing "+fileName);
			ChiSquaredSingleTest cst = new ChiSquaredSingleTest(filePath);
			
			ChiSquaredTestResult cstr = null;
			for(int i = 0;i<alpha.length;i++)
			{
				float cp = alpha[i];
				cstr = cst.checkCorrelation(cp);
				if(cstr==null)
					break;
				if(cstr.isCorrelate())
				{
					break;
				}
			}
			String result;
			if(cstr==null)
				result = fileName.split("\\.")[0]+" : "+null;
			else
				result = fileName.split("\\.")[0]+" : "+cstr.toString();
			resultList.add(result);	
		}
		
		if(!resultList.isEmpty())
			PVFile.print2File(resultList, outputFile);
		System.out.println("outputFile: "+outputFile);
	}
}
