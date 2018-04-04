package analysis.spatialcorr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComputeProbabilityAcrossRacks {

	public RackTorusElem[][][] rack;
	public List<RackTorusElem> rackList = new ArrayList<RackTorusElem>();
	public HashMap<String, Integer> rackIDMap = new HashMap<String, Integer>();
	public int[][] hopMatrix;
	
	public RackTorusElem[][][] initializeRackElements(int p, int m, int n)
	{
		int id = 0;
		RackTorusElem[][][] rack = new RackTorusElem[p][m][n];
		for(int i = 0;i<p;i++)
			for(int j=0;j<m;j++)
				for(int k=0;k<n;k++)
				{
					rack[i][j][k] = new RackTorusElem(id, i, j, k);
					rackList.add(rack[i][j][k]);
					rackIDMap.put(rack[i][j][k].getRackName(), id++);
				}
		return rack;
	}
	
	public int[][] generateHopMatrix(int p, int m, int n)
	{
		int[][] matrix = new int[p*m*n][p*m*n];
		rack = initializeRackElements(p,m,n);
		
		for(int i = 0;i<p;i++)
			for(int j=0;j<m;j++)
				for(int k=0;k<n;k++)
				{
					for(int a = 0;a<p;a++)
						for(int b=0;b<m;b++)
							for(int c=0;c<n;c++)
							{
								int hops = rack[i][j][k].getDistance(rack[a][b][c]);
								int id1 = rack[i][j][k].getId();
								int id2 = rack[a][b][c].getId();
								matrix[id1][id2] = hops;
							}

				}
		this.hopMatrix = matrix;
		return matrix;
	}
	
	public int getDistance(String rack1, String rack2)
	{
		int id1 = rackIDMap.get(rack1);
		int id2 = rackIDMap.get(rack2);
		
		int hops = hopMatrix[id1][id2];
		
		return hops;
	}
	
	public static void main(String[] args)
	{
		ComputeProbabilityAcrossRacks cpar = new ComputeProbabilityAcrossRacks();
		int[][] matrix = cpar.generateHopMatrix(RackTorusElem.i_size, RackTorusElem.j_size, RackTorusElem.k_size);
		for(int i=0;i<48;i++)
		{
			RackTorusElem rte = cpar.rackList.get(i);
			System.out.print(rte.getRackName()+" ");
			for(int j=0;j<48;j++)
				System.out.print(matrix[i][j]+" ");
			System.out.println();
		}
		
		int distance = cpar.getDistance("R00", "R05");
		System.out.println("distance="+distance);
		
	}
}
