package analysis.temporalcorr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import filter.EventElement;
import util.PVFile;

/**
 * This version does not consider the description field but uses different values to build the integer vector. 
 * @author fti
 *
 */
public class AdaptiveSemanticFilter2 {

	static String allEventsFile = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/FilterAndClassify/no-MaintResv-no-DIAGS-filter-interval=60s/s/"
			+ "allEvents.txt.rmsing";
	
	public static void main(String[] args)
	{
		double oneMin = 60;
		double[] delay_threshold = {1*oneMin, 2*oneMin, 4*oneMin, 8*oneMin, 15*oneMin, 30*oneMin, 60*oneMin, 120*oneMin, 240*oneMin, 480*oneMin, 960*oneMin};
		//double[] delay_threshold = {1800};
		
		List<String> lineList = PVFile.readFile(allEventsFile);
		
		EventElement[] events = new EventElement[lineList.size()];
		
		//Converting to string array
		Iterator<String> iter = lineList.iterator();
		for(int i= 0;iter.hasNext();i++)
		{
			String line = iter.next();
			if(line.startsWith("#"))
				continue;
			events[i] = new EventElement(line);
		}
		
		for(int i = 0;i<delay_threshold.length;i++)
		{
			performASF(events, delay_threshold[i]);
			clearEvents(events);
		}
		
		System.out.println("Done.");
	}
	
	public static void clearEvents(EventElement[] events)
	{
		for(int i = 0;i<events.length;i++)
		{
			events[i].clearPrePostList();
			events[i].clearFinalList();
		}
	}
	
	public static void performASF(EventElement[] events, double delay_threshold)
	{
		for(int i = 0;i<events.length;i++)
		{
			EventElement preEvent = events[i];
			double preTime = preEvent.getLatestRecord().getDtime();
			for(int j = i+1;j<events.length;j++)
			{
				EventElement postEvent = events[j];
				double postTime = postEvent.getFirstRecord().getDtime();
				double diff = postTime - preTime;
				if(diff>delay_threshold)
					break;
				double threshold = diff/delay_threshold;
				float similarity = (float)computeSimilarity(preEvent, postEvent);
				if(similarity>threshold)
				{
					preEvent.postCorrelatedEventList.add(postEvent);
					postEvent.preCorrelatedEventList.add(preEvent);		
					postEvent.preSimilarityList.add(similarity);
				}
			}
		}
		
		List<EventElement> mergeList = mergeEvents(delay_threshold, events);
		List<String> mergedResultList = new ArrayList<String>();
		mergedResultList.add(0, "#count="+mergeList.size());
		Iterator<EventElement> iter3 = mergeList.iterator();
		while(iter3.hasNext())
		{
			EventElement ee = iter3.next();
			mergedResultList.add("event "+ee.getEventString());
			Iterator<EventElement> iter4 = ee.finalPostCorrList.iterator();
			Iterator<Float> iter5 = ee.finalPostSimilarityList.iterator();
			while(iter4.hasNext())
			{
				EventElement postE = iter4.next();
				float sim = iter5.next();
				mergedResultList.add("\t"+sim+" "+postE.getEventString());
			}
		}
		
		String outputFile = allEventsFile+"-"+delay_threshold+".asf";
		System.out.println("outputResultFile to "+outputFile);
		PVFile.print2File(mergedResultList, outputFile);
		
	}
	
	private static List<EventElement> mergeEvents(double delay_threshold, EventElement[] allEvents)
	{
		List<EventElement> mergedList = new ArrayList<EventElement>();
		for(int i = 0;i<allEvents.length;i++)
		{
			EventElement event = allEvents[i];
			if(event.preCorrelatedEventList.isEmpty())
			{
				mergedList.add(event);
			}
			else
			{
				List<Float> aggSimilarityList = new ArrayList<Float>();
				List<EventElement> aggEventList = new ArrayList<EventElement>();
				for(int j = 0;j<event.preSimilarityList.size();j++)
				{
					float simi = event.preSimilarityList.get(j);
					EventElement e = event.preCorrelatedEventList.get(j);
					double diff = event.getFirstRecord().getDtime() - e.getLatestRecord().getDtime();
					double threshold = diff/delay_threshold;
					if(simi>threshold)
					{
						aggSimilarityList.add(simi);
						aggEventList.add(e);
					}
				}	
				
				float maxSim = 0;
				int maxIndex = 0;
				for(int j =0;j<aggSimilarityList.size();j++)
				{
					float sim = aggSimilarityList.get(j);
					if(maxSim<sim)
					{
						maxSim = sim;
						maxIndex = j;
					}
				}
				if(aggSimilarityList.isEmpty())
					mergedList.add(event);
				else
				{
					EventElement ee = aggEventList.get(maxIndex);
					event.mergedTo = ee;
					ee.finalPostCorrList.add(event);
					ee.finalPostSimilarityList.add(maxSim);
				}
			}
		}
		return mergedList;
	}
	
	private static double computeSimilarity(EventElement preEvent, EventElement postEvent)
	{
		double sum00 = 0, sum01=0, sum10=0, sum11=0;
		if(preEvent.getMsgID().equals(postEvent.getMsgID()))
		{
			sum00++;
			sum11++;
		}
		else
		{
			sum01++;
			sum10++;
		}
		if(preEvent.getCategory().equals(postEvent.getCategory()))
		{
			sum00++;
			sum11++;
		}
		else
		{
			sum01++;
			sum10++;
		}
		if(preEvent.componentList.get(0).equals(postEvent.componentList.get(0)))
		{
			sum00++;
			sum11++;
		}
		else
		{
			sum01++;
			sum10++;
		}
		if(preEvent.locationKey.equals(postEvent.locationKey))
		{
			sum00++;
			sum11++;
		}
		else
		{
			sum01++;
			sum10++;
		}
		if(preEvent.getFirstRecord().getLocation().equals(preEvent.getFirstRecord().getLocation()))
		{
			sum00++;
			sum11++;
		}
		else
		{
			sum01++;
			sum10++;
		}
		double sum_0=sum00+sum10;
		double sum0_=sum01+sum00;
		double sum_1=sum01+sum11;
		double sum1_=sum10+sum11;
		
		double phi = (sum00*sum11-sum01*sum10)/Math.sqrt(sum_0*sum0_*sum_1*sum1_);
		return phi;
	}
	
}
