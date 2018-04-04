package analysis.temporalcorr;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import filter.EventElement;
import filter.RecordElement;
import util.PVFile;

public class SearchErrorPropagation {

	public static List<Similarity> similarityList;
	public static int sameIDDelay;
	public static int diffIDDelay;
	
	public static List<Float> similarityThresholdList = new ArrayList<Float>();
	
	public static void main(String[] args)
	{
		if(args.length<4)
		{
			System.out.println("java SearchErrorPropagation [allEventsFile] [sameIDDelayThreshold(sec)] [diffIDDelayThreshold(sec)] [parameter setting file]");
			System.out.println("Example: java SearchErrorPropagation /home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/FilterAndClassify/no-MaintResv-no-DIAGS-filter-interval=240s/ts/allEvents.txt 240 1500 keyIndexClass.conf");
			System.exit(0);
		}
		
		String allEventsFile = args[0];
		diffIDDelay = Integer.parseInt(args[1]);
		sameIDDelay = Integer.parseInt(args[2]);
		String keyIndexClassConfFile = args[3];
		
		System.out.println("Loading similarity classes from "+keyIndexClassConfFile);
		similarityList = loadSetting(keyIndexClassConfFile);
		
		List<String> allEventStringList = PVFile.readFile(allEventsFile);
		EventElement[] allEvents = new EventElement[allEventStringList.size()];
		Iterator<String> iter = allEventStringList.iterator();
		
		for(int i = 0;iter.hasNext();i++)
		{
			String eventString = iter.next();
			EventElement event = new EventElement(eventString);
			allEvents[i] = event;
		}
		
		//Start searching
		System.out.println("Computing similarity.....");
		for(int i = 0;i<allEvents.length;i++)
		{
			EventElement preEvent = allEvents[i];
			for(int j = i+1;j<allEvents.length;j++)
			{
				double curTime = preEvent.getLatestRecord().getDtime();
				EventElement postEvent = allEvents[j];
				double eventTime = postEvent.getFirstRecord().getDtime();
				double diff = eventTime - curTime;
//				if(preEvent.getEventID()==56&&postEvent.getEventID()==57)
//					System.out.println();
				if(diff>sameIDDelay)
					break;
				else
				{
					float similarity = -1;
					if(!preEvent.getMsgID().equals(postEvent.getMsgID()))
					{
						if(diff<=diffIDDelay)
							similarity = preEvent.computeSimilarity(postEvent, diffIDDelay);
					}
					else  //diff<=sameIDDelay definitely holds here
						similarity = preEvent.computeSimilarity(postEvent, sameIDDelay);
					
					if(similarity>=0)
					{
						preEvent.postSimilarityList.add(similarity);
						preEvent.postCorrelatedEventList.add(postEvent);
						
						postEvent.preSimilarityList.add(similarity);
						postEvent.preCorrelatedEventList.add(preEvent);
						if(similarity>=0f)
						{
							preEvent.setLatestRecord(postEvent.getLatestRecord());
						}
					}
				}
			}
		}
		
		//Merge events based on different thresholds
		System.out.println("Merging events.....");
		List<String> mergedResultList = new ArrayList<String>();
		Iterator<Float> it = similarityThresholdList.iterator();
		while(it.hasNext())
		{
			float threshold = it.next();
			List<EventElement> mergeList = mergeEvents(threshold, allEvents);
			Iterator<EventElement> itt = mergeList.iterator();
			while(itt.hasNext())
			{
				EventElement ee = itt.next();
				mergedResultList.addAll(ee.printFinalEventItems());
			}
			mergedResultList.add(0, "#count="+mergeList.size());
			String outputFile = allEventsFile+"-"+threshold+".cor";
			System.out.println("outputResultFile to "+outputFile);
			PVFile.print2File(mergedResultList, outputFile);
			//clear results for the next test
			for(int i=0;i<allEvents.length;i++)
				allEvents[i].clearFinalList();
			mergedResultList.clear();
		}
		
		//print correlation results
		/*int min = 1000000, sum = 0, max = 0;
		List<String> resultList = new ArrayList<String>();
		for(int i = 0;i<allEvents.length;i++)
		{
			List<String> rList = allEvents[i].printAllEventItems();
			if(min>rList.size())
				min = rList.size();
			if(max<rList.size())
				max = rList.size();
			sum+=rList.size();
			resultList.addAll(rList);
		}
		float avg = ((float)sum)/allEvents.length;
		resultList.add(0, "#min="+min+",avg="+avg+",max="+max);
		String outputFile = allEventsFile+"-all.cor";
		PVFile.print2File(resultList, outputFile);
		System.out.println("outputResultFile to "+outputFile);*/
		System.out.println("done.");
	}
	
	public static List<EventElement> mergeEvents(float threshold, EventElement[] allEvents)
	{
		List<EventElement> mergeList = new ArrayList<EventElement>();
	
		for(int i= 0;i<allEvents.length;i++)
		{
			float maxSimilarity = 0;
			EventElement maxEE = null;
			EventElement ee = allEvents[i];
			Iterator<EventElement> it = ee.preCorrelatedEventList.iterator();
			Iterator<Float> it2 = ee.preSimilarityList.iterator();
			while(it.hasNext())
			{
				EventElement preEE = it.next();
				float similarity = it2.next();
				if(similarity>threshold)
				{
					if(maxSimilarity < similarity)
					{
						maxSimilarity = similarity;
						maxEE = preEE;
					}
				}
			}
			if(maxEE!=null)
			{
				if(maxEE.mergedTo==null)
				{
					ee.mergedTo = maxEE;
					maxEE.finalPostSimilarityList.add(maxSimilarity);
					maxEE.finalPostCorrList.add(ee);					
				}
				else
				{
					ee.mergedTo = maxEE.mergedTo;
					maxEE.mergedTo.finalPostSimilarityList.add(maxSimilarity);
					maxEE.mergedTo.finalPostCorrList.add(ee);					
				}
			}
			else
			{
				mergeList.add(ee);
			}
		}
		return mergeList;
	}
	
	public static List<Similarity> loadSetting(String keyIndexClassConfFile)
	{
		boolean fieldKey = true;
		List<Similarity> list = new ArrayList<Similarity>();
		List<String> lineList = PVFile.readFile(keyIndexClassConfFile);
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.startsWith("#field"))
			{
				fieldKey = true;
				continue;
			}
			else if(line.startsWith("#similarity"))
			{
				fieldKey = false;
				continue;
			}
			if(fieldKey)
			{
				String[] s = line.split("\\s");
				int index = Integer.parseInt(s[0]);
				float weight = Float.parseFloat(s[1]);
				try {
					Class<Similarity> _tempClass = (Class<Similarity>) Class.forName(s[2]);
					Constructor<Similarity> ctor = _tempClass.getDeclaredConstructor(Integer.class, Float.class);
					Similarity sim = ctor.newInstance(index, weight);
					list.add(sim);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			else
			{
				similarityThresholdList.add(Float.parseFloat(line));
			}
		}
		return list;
	}
	
}
