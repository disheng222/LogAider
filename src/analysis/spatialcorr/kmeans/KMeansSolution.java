package analysis.spatialcorr.kmeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import util.PVFile;
import util.RdGenerator;
import analysis.spatialcorr.ComputeProbabilityAcrossMidplanes;
import analysis.spatialcorr.GenerateContingencyTableForSigAnalysis;
import analysis.spatialcorr.MidplaneTorusElem;

public class KMeansSolution {

	private float totalWCSD = 0;
	
	public List<KMeansSet> kmeansSetList = new ArrayList<KMeansSet>();
	public List<Center> centerList = new ArrayList<Center>();
	public List<MidplaneTorusElem> allPointList = new ArrayList<MidplaneTorusElem>();
	public List<MidplaneTorusElem> uniqPointList = new ArrayList<MidplaneTorusElem>();
	
	public KMeansSolution(String[] sampleMidPlanes, ComputeProbabilityAcrossMidplanes cpam)
	{
		for(int i= 0;i<sampleMidPlanes.length;i++)
		{
			String midplane = sampleMidPlanes[i];
			MidplaneTorusElem mte = cpam.nameMidplaneMap.get(midplane);		
			allPointList.add(mte);
			mte.setKset(null);
		}
	}
	
	public boolean mergeNearbyCenters(float threshold)
	{
		boolean merged = false;
		List<CenterPair> cpList = new ArrayList<CenterPair>();
		for(int i = 0;i<centerList.size();i++)
			for(int j = i+1;j<centerList.size();j++)
			{
				Center a = centerList.get(i);
				Center b = centerList.get(j);
				float dis = a.getDistance(b);
				if(dis < threshold)
				{
					merged = true;
					CenterPair cp = new CenterPair(a,b);
					centerList.remove(a);
					centerList.remove(b);
					cpList.add(cp);
					i--;
					break;
				}
			}
		
		Iterator<CenterPair> iter = cpList.iterator();
		while(iter.hasNext())
		{
			CenterPair cp = iter.next();
			Center merge = cp.getMergeCenter(this);
			centerList.add(merge);
		}
		totalWCSD = 0;
		return merged;
	}
	
	public float computeAvgCenterDistance()
	{
		float totalDis = 0;
		for(int i = 0;i<centerList.size();i++)
			for(int j = i+1;j<centerList.size();j++)
			{
				Center a = centerList.get(i);
				Center b = centerList.get(j);
				float dis = a.getDistance(b);
				totalDis += dis;
			}
		return totalDis/(centerList.size()*(centerList.size()-1)/2);
	}
	
	public void genUniquePoints()
	{
		HashMap<String, MidplaneTorusElem> map = new HashMap<String, MidplaneTorusElem>();
		Iterator<MidplaneTorusElem> iter = allPointList.iterator();
		while(iter.hasNext())
		{
			MidplaneTorusElem elem = iter.next();
			String name = elem.getMidplaneName();
			MidplaneTorusElem elem2 = map.get(name);
			if(elem2==null)
			{
				map.put(name, elem);
				uniqPointList.add(elem);
				elem2 = elem;
				if(MidplaneTorusElem.equalTypeAllowDuplication)
					elem2.setCount(0);
			}
			if(MidplaneTorusElem.equalTypeAllowDuplication)
				elem2.setCount(elem2.getCount()+1);
		}
	}
	
	public KMeansSolution executeWithCenters()
	{
		for(int i = 0;i<1000;i++)
		{
			boolean changed = false;
			Iterator<MidplaneTorusElem> iter = uniqPointList.iterator();
			while(iter.hasNext())
			{
				MidplaneTorusElem mte = iter.next();
/*				if(mte.getMidplaneName().equals("R24-M0"))
				{
					System.out.println("R24-M0");
					if(mte.getKset()!=null)
					{
						int a = 0;
						Iterator<MidplaneTorusElem> iter2 = mte.getKset().midplaneList.iterator();
						while(iter2.hasNext())
						{
							MidplaneTorusElem mte2 = iter2.next();
							if(mte2.getMidplaneName().equals("R24-M0"))
								a++;
						}
						if(a==0)
							System.out.println("a="+0);
						System.out.println("R24-M0="+a);	
					}
				}
*/				
				KMeansSet kset = mte.getKset();
				Center center = getClosestCenter(mte);
				if(kset==null)
				{
					center.getKMeansSet().add(mte);
					mte.setKset(center.getKMeansSet());
					changed = true;
				}
				else if(!center.getKMeansSet().midplaneList.contains(mte))
				{
					kset.remove(mte);
					center.getKMeansSet().add(mte);
					mte.setKset(center.getKMeansSet());
					changed = true;
				}
			}
			if(!changed)
				break;
			else
				updateCenters();
		}
		return this;
	}
	
	public KMeansSolution execute(int numOfSets)
	{
		genUniquePoints();
		int[] rdIndex = RdGenerator.RAN_SeedGen.generate_Int(0, uniqPointList.size(), numOfSets);
		
		//Initialization.
		for(int i = 0;i<rdIndex.length;i++)
		{
			int I = rdIndex[i];
			MidplaneTorusElem mte = uniqPointList.get(I);
			KMeansSet kset = new KMeansSet(mte);
			Center center = new Center(mte, kset);
			kset.setCenter(center);
			centerList.add(center);
			
			mte.setKset(kset);
			kmeansSetList.add(kset);
		}
		
		executeWithCenters();
		
		return this;
	}
		
	public Center getClosestCenter(MidplaneTorusElem mte)
	{
		Center closestCenter = null;
		float closestDis = 1000000;
		Iterator<Center> iter = centerList.iterator();
		while(iter.hasNext())
		{
			Center center = iter.next();
			float distance = mte.getDistance(center);
			if(closestDis>distance)
			{
				closestDis = distance;
				closestCenter = center;
			}
		}
		return closestCenter;
	}
	
	public boolean updateCenters()
	{
		boolean changeCenters = false;
		Iterator<KMeansSet> iter = kmeansSetList.iterator();
		while(iter.hasNext())
		{
			KMeansSet kset = iter.next();
			boolean changeCenter = kset.updateCenter();
			if(!changeCenters&&changeCenter)
				changeCenters = true; 
		}
		if(changeCenters)
			totalWCSD = 0;
		return changeCenters;
	}
	
	public void printKMeansSets()
	{
		Iterator<KMeansSet> iter  = kmeansSetList.iterator();
		while(iter.hasNext())
		{
			KMeansSet set = iter.next();
			System.out.println(set.toString());
		}
	}
	
	public void displayInMatrix(int[][] matrix)
	{
		GenerateContingencyTableForSigAnalysis.buildMidPNameLocMap();
		Iterator<KMeansSet> iter = kmeansSetList.iterator();
		int a = 1;
		while(iter.hasNext())
		{
			KMeansSet set = iter.next();
			Iterator<MidplaneTorusElem> iter2 = set.midplaneList.iterator();
			while(iter2.hasNext())
			{
				MidplaneTorusElem mte = iter2.next();
				String name = mte.getMidplaneName();
				int[] loc = GenerateContingencyTableForSigAnalysis.midPNameLocMap.get(name);
				int i = loc[0];
				int j = loc[1];
				matrix[i][j] = a;
			}
			a++;
		}
	}
	
	public void printMatrix(int[][] matrix, String outputFilePath)
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
	
	public float getTotalWCSD()
	{
		if(totalWCSD==0)
		{
			Iterator<KMeansSet> iter = kmeansSetList.iterator();
			while(iter.hasNext())
			{
				KMeansSet set = iter.next();
				totalWCSD += set.getWCSD();
			}
			return totalWCSD;			
		}
		else
			return totalWCSD;
	}
	
	public static void main(String[] args)
	{
		//String input = "R00-M0 R11-M1 R22-M0";
		
		String filePath = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/FilterAndClassify/00090210.ori";
		
		if(args.length<2)
		{
			System.out.println("java KMeansSolution [allowDuplicate?] [inputFilePath]");
			System.exit(0);
		}
		
		MidplaneTorusElem.equalTypeAllowDuplication = Boolean.parseBoolean(args[0]);
		filePath = args[1];
		
		String input = GenerateContingencyTableForSigAnalysis.getMidplanes(filePath);
		
		//System.out.println(input);
		
		String[] midplanes = input.split("\\s");
		
		System.out.println("# midplanes: "+midplanes.length);
		
		ComputeProbabilityAcrossMidplanes cpam = new ComputeProbabilityAcrossMidplanes(3, 16, 2);
		KMeansSolution kmeans = new KMeansSolution(midplanes, cpam);
		kmeans.execute(3);
		
		//kmeans.printKMeansSets();
		
		int[][] displayMatrix = new int[6][16];
		kmeans.displayInMatrix(displayMatrix);
	
		kmeans.printMatrix(displayMatrix, filePath+".kx"+MidplaneTorusElem.equalTypeAllowDuplication);
		System.out.println("done.");
	}
}
