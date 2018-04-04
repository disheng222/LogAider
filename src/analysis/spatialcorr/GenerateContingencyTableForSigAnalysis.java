package analysis.spatialcorr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import util.PVFile;
import util.RecordSplitter;

public class GenerateContingencyTableForSigAnalysis {

	private static int row = 6;
	private static int col = 16;
	
	public static HashMap<String, int[]> midPNameLocMap = new HashMap<String, int[]>();
	

	private static void fillOutMapRow(int x, int m)
	{
		int i = 2*x+m;
		for(int j = 0;j<16;j++)
		{
			String name = "R"+x+Integer.toHexString(j).toUpperCase()+"-M"+m;
			midPNameLocMap.put(name, new int[]{i,j});	
		}
	}
	
	public static void buildMidPNameLocMap()
	{
		fillOutMapRow(0,0);
		fillOutMapRow(0,1);
		fillOutMapRow(1,0);
		fillOutMapRow(1,1);
		fillOutMapRow(2,0);
		fillOutMapRow(2,1);
	}
	
	public static int[][] generateTable(String[] midplanes)
	{
		int[][] matrix = new int[row][col];
		for(int a = 0;a<midplanes.length;a++)
		{
			String midplane = midplanes[a];
			int[] loc = midPNameLocMap.get(midplane);
			if(loc==null)
			{
				return null;
			}
			int i = loc[0];
			int j = loc[1];
			matrix[i][j]++;
		}
		return matrix;
	}
	
	public static void printMatrix(int[][] matrix, String outputFilePath)
	{
		List<String> lineList = new ArrayList<String>();
		lineList.add("#");
		for(int i = 0;i<matrix.length;i++)
		{
			StringBuilder sb = new StringBuilder("R"+i+" ");
			for(int j = 0;j<matrix[0].length;j++)
				sb.append(matrix[i][j]).append(" ");
			lineList.add(sb.toString());
		}
		PVFile.print2File(lineList, outputFilePath);
	}
	
	public static void main(String[] args)
	{

		buildMidPNameLocMap();
		
		String filterDataDir = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/FilterAndClassify";
		String extension = "ori";
		//construct the midplane string first
		List<String> fileList = PVFile.getFiles(filterDataDir, extension);
		Iterator<String> iter = fileList.iterator();
		while(iter.hasNext())
		{
			String fileName = iter.next();
			String filePath = filterDataDir+"/"+fileName;
			System.out.println("Processing "+fileName);
			String input = getMidplanes(filePath);
			int[][] matrix = generateTable(input.split("\\s"));
			if(matrix==null)
			{
				System.out.println(fileName+" is inconsistent with ComputeRack model.");
				continue;
			}
			String outputFilePath = filterDataDir+"/spatialcorr/"+fileName.split("\\.")[0]+".tab";
			printMatrix(matrix, outputFilePath);
			System.out.println("output: "+outputFilePath);
		}
/*		
		String midplaneString = "R01-M0 R00-M0 R10-M0 R22-M0 R21-M0 R16-M0 R16-M0";
		String[] midplanes = midplaneString.split("\\s");
		int[][] matrix = generateTable(midplanes);
		printMatrix(matrix, );*/
	}
	
	public static String getMidplanes(String filePath)
	{
		StringBuilder sb = new StringBuilder();
		List<String> lineList = PVFile.readFile(filePath);
		Iterator<String> iter2 = lineList.iterator();
		while(iter2.hasNext())
		{
			String line = iter2.next();
			String[] s = RecordSplitter.partition(line);
			String location = s[8];
			if(!location.startsWith("R"))
				continue;
			String[] ss = location.split("-");
			sb.append(ss[0]).append("-").append(ss[1]).append(" ");
		}
		String input = sb.toString().trim();
		return input;
	}
}
