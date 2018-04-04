package analysis.temporalcorr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.PVFile;
import filter.EventElement;

public class CompareClusteringSolutionWithReality {

	public static void main(String[] args)
	{
		String rootDir = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/FilterAndClassify/no-MaintResv-no-DIAGS-filter-interval=60s/s";
		//String solFile = rootDir+"/allEvents.txt-0.8.cor";
		//String solFile = rootDir+"/allEvents.txt-1800.0.asf";
		String extension = "stf";//cor or asf
		String trueFile = rootDir+"/allEvents.txt.true";
		
		List<String> fileList = PVFile.getFiles(rootDir, extension);
		Iterator<String> iter3 = fileList.iterator();
		while(iter3.hasNext())
		{
			String fileName = iter3.next();
			String solFile = rootDir+"/"+fileName;
			List<ClusteringSet> solList = convertSolToClusteringSets(solFile);
			List<ClusteringSet> trueList = convertSolToClusteringSets(trueFile);
			//System.out.println("Start processing.....");
			Iterator<ClusteringSet> iter = solList.iterator();
			float sumSim = 0;
			while(iter.hasNext())
			{
				float maxSim = 0; //similarity
				ClusteringSet cset = iter.next();
				Iterator<ClusteringSet> iter2 = trueList.iterator();
				while(iter2.hasNext())
				{
					ClusteringSet tset = iter2.next();
					int[] c = cset.computeIntersectAndUnionCount(tset);	
					float interset = c[0];
					float union = c[1];
					float sim = interset/union;
					if(maxSim<sim)
						maxSim = sim;
				}
				sumSim += maxSim;
			}
			
			float avgSim = sumSim/solList.size();
			System.out.println(fileName+" "+avgSim);	
		}
	}
	
	public static List<ClusteringSet> convertSolToClusteringSets(String solFilePath)
	{
		List<ClusteringSet> resultList = new ArrayList<ClusteringSet>();
		ClusteringSet set = null;
		List<String> lineList = PVFile.readFile(solFilePath);
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.startsWith("#"))
				continue;
			if(line.startsWith("event"))
			{
				set = new ClusteringSet();
				resultList.add(set); 
				EventElement event = new EventElement(line.replace("event ", ""));
				set.eList.add(event);
			}
			else
			{
				StringBuilder sb = new StringBuilder();
				String[] s = line.split("\\s");
				for(int i = 2;i<s.length;i++)
					sb.append(s[i]).append(" ");
				String eventString = sb.toString().trim();
				EventElement event = new EventElement(eventString);
				set.eList.add(event);
			}
		}
		return resultList;
	}
}

class ClusteringSet
{
	private float distance;
	private float similarity;
	
	public List<EventElement> eList = new ArrayList<EventElement>();
	
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public float getSimilarity() {
		return similarity;
	}
	public void setSimilarity(float similarity) {
		this.similarity = similarity;
	}
	
	public int computeInterSectionCount(ClusteringSet set)
	{
		int count = 0;
		Iterator<EventElement> iter = eList.iterator();
		while(iter.hasNext())
		{
			EventElement ee = iter.next();
			if(set.eList.contains(ee))
				count++;
		}
		return count;
	}
	
	public int computeUnionCount(ClusteringSet set)
	{
		return eList.size()+set.eList.size()-computeInterSectionCount(set);
	}
	
	public int[] computeIntersectAndUnionCount(ClusteringSet set)
	{
		int interset = computeInterSectionCount(set);
		int union = eList.size()+set.eList.size()-interset;
		return new int[]{interset, union};
	}
}
