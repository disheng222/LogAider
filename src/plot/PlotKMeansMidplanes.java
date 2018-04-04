package plot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class PlotKMeansMidplanes {
	public static int id = 1;
	
	public static String[] color = new String[]{"white", "yellow", "green", "red", "orange", "brown", "blue", "cyan", "gray", "pink", "purple", "indigo"};
	
	/**
	 * Explanation for the input files: 
	 * oxtrue (allowing duplicated messages, opt number of clusters), 
	 * oxfalse (no duplicated messages, opt number of clusters).
	 * fxtrue: optimized clustering results with multiple initial sample points (allowing duplicated messages).
	 * fxfalse: optimized clustering results with multiple initial sample points (no duplicated messages).
	 * kxtrue: classic clustering results with only one initial random sample points (allowing duplication).
	 * kxfalse:classic clustering results with only one initial random sample points (no duplication).
	 * @param args
	 */
	public static void main(String[] args)
	{
		if(args.length<1)
		{
			System.out.println("java PlotMeansMidplanes [gnuplotTemplateFile] [inputFilePath]");
			System.out.println("java PlotMeansMidplanes /home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/FilterAndClassify/gnuplot/template.p /home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/FilterAndClassify/00090210.ori.fxtrue");
			System.exit(0);
		}
		
		String gnuplotTemplateFile = args[0];
		String inputFilePath = args[1];
		String outputFilePath = inputFilePath+".p";
		
		String[] ss = inputFilePath.split("/");
		String hiddenDataFile = "";
		for(int i = 0;i<ss.length-1;i++)
			hiddenDataFile+=ss[i]+"/";
		hiddenDataFile+="DATA";
		
		String targetFileName = ss[ss.length-1];
		
		List<String> lineList = PVFile.readFile(gnuplotTemplateFile);
		
		List<String> plotCodeList = new ArrayList<String>();
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String s = iter.next();
			plotCodeList.add(s.replace("EPSFILENAME", targetFileName));
		}
		
		//String frame = printOneRec(122.5f, 47.5f, 245, 95, "white");
		//plotCodeList.add(frame);
		
		int[][] kmeansMatrix = PVFile.readFileTo2DIntArray(inputFilePath);
		
		displayMatrix(plotCodeList, kmeansMatrix, hiddenDataFile);
		
		PVFile.print2File(plotCodeList, outputFilePath);
		
		System.out.println("outputFile: "+outputFilePath);
		System.out.println("done.");
	}
	
	private static String printOneLabel(int x, int y, String txt)
	{
		StringBuilder sb = new StringBuilder("set label ");
		sb.append(id++);
		sb.append(" \"");
		sb.append(txt);
		sb.append("\" at ");
		sb.append(x).append(",").append(y);
		sb.append(" font \"Arial,120\" right norotate back nopoint");
		return sb.toString();	
	}
	
	
	private static void displayMatrix(List<String> plotCodeList, int[][] kmeansMatrix, String hiddenDataFile)
	{
		for(int i = 0;i<kmeansMatrix.length;i++)
		{
			String text = "R"+i/2+"X-M"+i%2;
			String label = printOneLabel(18, 12+11*i, text);
			plotCodeList.add(label);
			for(int j = 0;j<kmeansMatrix[0].length;j++)
			{
				int y = 12+11*i;
				int x = 24+14*j;
				int w = 8;
				int h = 8;
				String c = color[kmeansMatrix[i][j]];
				String s = printOneRec(x,y,w,h,c);
				plotCodeList.add(s);
			}
		}
		plotCodeList.add("plot 'DATA'");
		List<String> tmpList = new ArrayList<String>();
		tmpList.add("0 -0.1");
		PVFile.print2File(tmpList, hiddenDataFile);
	}
	
	private static String printOneRec(float x, float y, float width, float height, String color)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("set object ");
		sb.append(id++);
		sb.append(" rect center ");
		sb.append(x).append(",").append(y);
		sb.append(" size ").append(width).append(",").append(height);
		sb.append(" front fc rgb '");
		sb.append(color);
		sb.append("' fillstyle solid 1");
		return sb.toString();
	}
}
