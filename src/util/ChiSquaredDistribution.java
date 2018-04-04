package util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;


public class ChiSquaredDistribution {

	private HashMap<Integer, float[]> table = new HashMap<Integer, float[]>();
	private ArrayList<Integer> kList = new ArrayList<Integer>();
	public String chiTableFilePath = "ChiSquaredTable.dat";
	private float[] prob = new float[11];
	
	public ChiSquaredDistribution(String chiTableFilePath)
	{
		this.chiTableFilePath = chiTableFilePath;
		loadTable();
	}
	
	public void loadTable()
	{
		List<String> lineList = PVFile.readFile(chiTableFilePath);
		Iterator<String> iter = lineList.iterator();
		String firstLine = iter.next();
		String[] s = firstLine.split("\\s");
		for(int i = 0;i<prob.length;i++)
			prob[i] = Float.parseFloat(s[i+1]);
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] ss = line.split("\\s+");
			int df = Integer.parseInt(ss[0]); //df is also known as k
			float[] vector = new float[prob.length];
			table.put(df, vector);
			if(df>=250)
				kList.add(df);
			for(int i = 0;i<vector.length;i++)
				vector[i] = Float.parseFloat(ss[i+1]);
		}
		Collections.sort(kList);
	}
	
	public float getProbPoint(int k, float p)
	{
		int pIndex = -1;
		for(int i = 0;i<prob.length;i++)
		{
			if(prob[i] == p)
				pIndex = i;
		}
		if(pIndex==-1)
			return -1;
		
		float[] vector = table.get(k);
		if(vector==null)
		{
			int prevEdgePoint = 0;
			int currEdgePoint = 0;
			Iterator<Integer> iter = kList.iterator();
			while(iter.hasNext())
			{
				currEdgePoint = iter.next();
				if(k <= currEdgePoint)
					break;
				prevEdgePoint = currEdgePoint;
			}
			int kk;
			if(k - prevEdgePoint < currEdgePoint - k)
				kk = prevEdgePoint;
			else
				kk = currEdgePoint;
			vector = table.get(kk);
		}
					
		return vector[pIndex];
	}
}
