package analysis.significance;

import java.util.Iterator;
import java.util.List;

import util.ChiSquaredDistribution;
import util.PVFile;

public class ChiSquaredSingleTest {

	private int k = 0;
	private int N = 0; //total sample size (# of records)
	private float[] pi_; //fraction of row totals
	private float[] p_j; //fraction of column totals
	private float[][] Oij;
	private float[][] Eij;
	private int row, column;
	
	public static String chiSquareTableFilePath = "ChiSquaredTable.dat";
	public static ChiSquaredDistribution csd = new ChiSquaredDistribution(chiSquareTableFilePath);
	
	public ChiSquaredSingleTest(String contingencyTablePath)
	{
		float[][] table = loadContingencyTable(contingencyTablePath);
		initialize(table);
	}
	
	public ChiSquaredSingleTest(float[][] contingTable)
	{
		initialize(contingTable);
	}
	
	public void initialize(float[][] contingTable)
	{
		row = contingTable.length;
		column = contingTable[0].length;
		Oij = new float[row][column];
		pi_ = new float[row];
		p_j = new float[column];
		k = (row-1)*(column-1);
		for(int i = 0;i<contingTable.length;i++)
			for(int j = 0;j<contingTable[0].length;j++)
			{
				float value = contingTable[i][j];
				Oij[i][j] = value;
				pi_[i] += value;
				p_j[j] += value;
				N += value;
			}
		Eij = new float[row][column];
		for(int i = 0;i < pi_.length;i++)
			pi_[i] /= N;
		for(int i = 0;i < p_j.length;i++)
			p_j[i] /= N;
		
		for(int i = 0;i<row;i++)
			for(int j = 0;j<column;j++)
			{
				Eij[i][j] = N*pi_[i]*p_j[j];
			}
	}
	
	public float computeChiSquare()
	{
		float sum = 0;
		for(int i = 0;i<row;i++)
			for(int j = 0;j<column;j++)
			{
				if(Eij[i][j]!=0)
					sum += (Oij[i][j] - Eij[i][j])*(Oij[i][j] - Eij[i][j])/Eij[i][j];
			}
		return sum;
	}
	
	/**
	 * 
	 * @param alpha: complement of confidence interval (0.1,0.05,0.025,0.01, 0.005)
	 * @return
	 */
	public ChiSquaredTestResult checkCorrelation(float alpha)
	{
		float Chi_standard = csd.getProbPoint(k, alpha); 
		if(Chi_standard == -1)
			return null;
		
		float Chi_compute = computeChiSquare();
		if(Chi_compute <= Chi_standard)
		{
			ChiSquaredTestResult cstr = new ChiSquaredTestResult(false, Chi_compute, alpha, Chi_standard);
			return cstr; //reject (no correlation)
		}
		else
		{
			ChiSquaredTestResult cstr = new ChiSquaredTestResult(true, Chi_compute, alpha, Chi_standard);
			return cstr; //accept (i.e., correlated)
		}
	}
	
	public float[][] loadContingencyTable(String filePath)
	{
		float[][] cTable = null;
		List<String> lineList = PVFile.readFile(filePath);
		int row = lineList.size()-1;
		Iterator<String> iter = lineList.iterator();
		int i = 0;
		while(iter.hasNext())
		{
			String line = iter.next();
			if(!line.startsWith("#"))
			{
				String[] s = line.split("\\s");
				if(cTable==null)
				{
					int column = s.length-1;
					cTable = new float[row][column];
				}
				
				for(int j = 0;j<cTable[0].length;j++)
					cTable[i][j] = Float.parseFloat(s[j+1]);
				i++;
			}
		}
		return cTable;
	}

	public static void main(String[] args)
	{
		if(args.length < 1)
		{
			System.out.println("Usage: java ChiSquaredTest [contingency_table_path]");
			System.out.println("Example: java ChiSquaredTest /home/fti/Catalog-project/miralog/RAS-Job/Job/featureState/science_field_short/science_field_short-mode.fs");
			System.exit(0);
		}
		String contingTablePath = args[0];
		
		ChiSquaredSingleTest cst = new ChiSquaredSingleTest(contingTablePath);
		
		float alpha = 0.001f;
		ChiSquaredTestResult cstr = cst.checkCorrelation(alpha);
		System.out.println("result: "+cstr);
	}
}
