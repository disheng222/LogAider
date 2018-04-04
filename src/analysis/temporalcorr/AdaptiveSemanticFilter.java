package analysis.temporalcorr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import filter.EventElement;
import util.PVFile;

/**
 * This version considers only msg ID, actually considering the description field. 
 * @author fti
 *
 */
public class AdaptiveSemanticFilter {

	static String allEventsFile = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/FilterAndClassify/no-MaintResv-no-DIAGS-filter-interval=60s/s/"
			+ "allEvents.txt";
	
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
			performSTF(events, delay_threshold[i]);
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
	
	public static void performSTF(EventElement[] events, double delay_threshold)
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
				if(postEvent.getEventID()==preEvent.getEventID())
				{
					preEvent.postCorrelatedEventList.add(postEvent);
					postEvent.preCorrelatedEventList.add(preEvent);		
					postEvent.preSimilarityList.add(1f);					
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
		
		String outputFile = allEventsFile+"-"+delay_threshold+".stf";
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
				event.mergedTo = event.preCorrelatedEventList.get(0);
				event.mergedTo.finalPostCorrList.add(event);
				event.mergedTo.finalPostSimilarityList.add(1f);
			}
		}
		return mergedList;
	}
	
}
