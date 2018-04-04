/**
 * @author Sheng Di
 * @class TemporalGroup
 * @description  
 */

package analysis.inbetween.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TemporalGroup {
	
	public static double totalInitTime;

	public List<String[]> eventList = new ArrayList<String[]>();
	private double startTime;
	private double lastTime;
	
	public List<ItemCombination> idList = new ArrayList<ItemCombination>();
	public HashMap<String, ItemCombination> idMap = new HashMap<String, ItemCombination>();
	public List<ItemCombination> compList = new ArrayList<ItemCombination>();
	public HashMap<String, ItemCombination> compMap = new HashMap<String, ItemCombination>();
	public List<ItemCombination> catList = new ArrayList<ItemCombination>();
	public HashMap<String, ItemCombination> catMap = new HashMap<String, ItemCombination>();
	

	public TemporalGroup(double startTime, double lastTime) {
		this.startTime = startTime;
		this.lastTime = lastTime;
	}

	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public double getLastTime() {
		return lastTime;
	}
	public void setLastTime(double lastTime) {
		this.lastTime = lastTime;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(startTime-totalInitTime);
		sb.append("-");
		sb.append(lastTime-totalInitTime);
		sb.append(":");
		sb.append(eventList.size());
		return sb.toString();
	}

	public void constructMaps()
	{
		Iterator<String[]> iter = eventList.iterator();
		while(iter.hasNext())
		{
			String[] event = iter.next();
			String msgID = event[1];
			String categ = event[2];
			String compo = event[3];
			
			ItemCombination ic = idMap.get(msgID);
			if(ic==null)
			{
				ic = new ItemCombination(msgID, 0, false);
				idList.add(ic);
				idMap.put(msgID, ic);
			}
			
			ic.setCount(ic.getCount()+1);
			
			ic = compMap.get(compo);
			if(ic==null)
			{
				ic = new ItemCombination(compo, 0, false);
				compList.add(ic);
				compMap.put(compo, ic);
			}
			
			ic.setCount(ic.getCount()+1);
			
			ic = catMap.get(categ);
			
			if(ic==null)
			{
				ic = new ItemCombination(categ, 0, false);
				catList.add(ic);
				catMap.put(categ, ic);
			}
			
			ic.setCount(ic.getCount()+1);
		}
	}
	
	/**
	 * Item Combination
	 * @param totalMsgIDMap
	 * @param totalCompMap
	 * @param totalCateMap
	 */
	public void mergeICList(
			HashMap<String, ItemCombination> totalMsgIDMap, 
			HashMap<String, ItemCombination> totalCompMap, 
			HashMap<String, ItemCombination> totalCateMap)
	{	
		//scan maps...
		constructMaps();
				  
		List<String> msgCombList = generateCombinationsFromItems(idList);
		mergeICList(msgCombList, totalMsgIDMap);
		
		List<String> compCombList = generateCombinationsFromItems(compList);
		mergeICList(compCombList, totalCompMap);
		
		List<String> cateCombList = generateCombinationsFromItems(catList);
		mergeICList(cateCombList, totalCateMap);
	}
	
	public void mergeICList(List<String> combList, HashMap<String, ItemCombination> totalMap)
	{
		Iterator<String> iter = combList.iterator();
		while(iter.hasNext())
		{
			String s = iter.next();
			ItemCombination ic = totalMap.get(s);
			if(ic==null)
			{
				ic = new ItemCombination(s, 0, true);
				totalMap.put(s, ic);
			}
			ic.setCount(ic.getCount()+1);
		}
	}
	
	public List<String> generateCombinationsFromItems(List<ItemCombination> list)
	{
		List<String> resultList = new ArrayList<String>();
		Collections.sort(list);
		int size = list.size();
		for(int i = 0;i<size-1;i++)
		{
			ItemCombination ic1 = list.get(i);
			for(int j = i+1;j<size;j++)
			{
				ItemCombination ic2 = list.get(j);
				StringBuilder sb = new StringBuilder();
				sb.append(ic1.getKey()).append("-").append(ic2.getKey());
				String combKey = sb.toString();
				resultList.add(combKey);
			}
		}
		return resultList;
	}
	
}
