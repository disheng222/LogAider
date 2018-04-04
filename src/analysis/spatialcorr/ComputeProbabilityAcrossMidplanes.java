package analysis.spatialcorr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComputeProbabilityAcrossMidplanes {
	public static int i_size = 2;
	public static int j_size = 3;
	public static int k_size = 4;
	public static int t_size = 4;
	
	public MidplaneTorusElem[][][][] midplane;
	public List<MidplaneTorusElem> midplaneList = new ArrayList<MidplaneTorusElem>();
	public HashMap<String, Integer> midplaneIDMap = new HashMap<String, Integer>();
	public HashMap<String, MidplaneTorusElem> nameMidplaneMap = new HashMap<String, MidplaneTorusElem>();
	public byte[][] hopMatrix;
	
	public ComputeProbabilityAcrossMidplanes(int p, int m, int n)
	{
		int id = 0;
		MidplaneTorusElem[][][][] midplane = new MidplaneTorusElem[i_size][j_size][k_size][t_size];
		for(int x = 0;x<p;x++)
			for(int y=0;y<m;y++)
				for(int z=0;z<n;z++)
				{
					MidplaneTorusElem mp = new MidplaneTorusElem(id, x, y, z);
					int i = (int)mp.getI();
					int j = (int)mp.getJ();
					int k = (int)mp.getK();
					int t = (int)mp.getT();
					midplane[i][j][k][t] = mp;
					midplaneList.add(mp);
					nameMidplaneMap.put(mp.getMidplaneName(), mp);
					midplaneIDMap.put(mp.getMidplaneName(), id++);
				}
		this.midplane = midplane;
	}
	
	public byte[][] generateHopMatrix()
	{
		int totalSize = i_size*j_size*k_size*t_size;
		byte[][] matrix = new byte[totalSize][totalSize];
		
		for(int i = 0;i<i_size;i++)
			for(int j=0;j<j_size;j++)
				for(int k=0;k<k_size;k++)
					for(int t=0;t<t_size;t++)
					{
						for(int a = 0;a<i_size;a++)
							for(int b=0;b<j_size;b++)
								for(int c=0;c<k_size;c++)
									for(int d=0;d<t_size;d++)
									{
										int hops = (int)midplane[i][j][k][t].getDistance(midplane[a][b][c][d]);
										int id1 = midplane[i][j][k][t].getId();
										int id2 = midplane[a][b][c][d].getId();
										matrix[id1][id2] = (byte)hops;
									}						
					}

		this.hopMatrix = matrix;
		return matrix;
	}
	
	public float getDistance(String midplane1, String midplane2)
	{
		MidplaneTorusElem e1 = nameMidplaneMap.get(midplane1);
		MidplaneTorusElem e2 = nameMidplaneMap.get(midplane2);
		return e1.getDistance(e2);
	}
	
/*	public int getDistance(String midplane1, String midplane2)
	{
		int id1 = midplaneIDMap.get(midplane1);
		int id2 = midplaneIDMap.get(midplane2);
		
		int hops = hopMatrix[id1][id2];
		
		return hops;
	}*/
	
	public static void main(String[] args)
	{
		ComputeProbabilityAcrossMidplanes cpam = new ComputeProbabilityAcrossMidplanes(3, 16, 2);
		float distance = cpam.getDistance("R00-M0", "R05-M1");
		System.out.println("distance="+distance);
		
/*		byte[][] matrix = cpam.generateHopMatrix();
		for(int i=0;i<96;i++)
		{
			MidplaneTorusElem mte = cpam.midplaneList.get(i);
			System.out.print(mte.getMidplaneName()+" ");
			for(int j=0;j<96;j++)
				System.out.print(matrix[i][j]+" ");
			System.out.println();
		}	*/
	}
}
