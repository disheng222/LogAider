package filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import element.MaintainancePeriod;
import element.ReservationPeriod;
import util.ConversionHandler;
import util.NumericChecker;
import util.PVFile;
import util.RecordSplitter;

public class TemporalSpatialFilter {

	public static int sameLocationMinInterval = 300; //seconds
	public static int sameLocationMaxInterval = 900; //seconds
	public static float sameAllocationInterval = 240;//10;//6 seconds?
	
	public static int timeIndex = 0;
	public static int allocationIndex = 0;
	public static int locationIndex = 0;
	
	public static String classifiedLogDir;
	public static String outputDir;
	
	public static int eventID = 0;
	public static String type = "";
	public static List<String> filterFullRecordList = new ArrayList<String>();
	public static List<MaintainancePeriod> maintList = new ArrayList<MaintainancePeriod>();
	public static List<ReservationPeriod> resList = new ArrayList<ReservationPeriod>(); 
	
	public static void main(String[] args)
	{
		if(args.length<6)
		{
			System.out.println("Usage: java TemporalSpatialFilter [-t/-s/-ts] [classifiedLogDir] [extension] [maintainance-time-file] [outputDir]");
			System.out.println("Example: java TemporalSpatialFilter -t /home/fti/Catalog-project/miralog/FilterAndClassify ori /home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/schema/maintainance-period.txt /home/fti/Catalog-project/miralog/FilterAndClassify");
			System.out.println("Example: java TemporalSpatialFilter -ts /home/sdi/Work/Catalog-project/Catalog-data/Compare-5years-1years/5years/FilterAndClassify ori /home/sdi/Work/Catalog-project/Catalog-data/miralog/one-year-data/ALCF-Data/RAS/schema/maintainance-period.txt /home/sdi/Work/Catalog-project/Catalog-data/Compare-5years-1years/5years/FilterAndClassify");
			System.exit(0);
		}
		
		//-t: only temporal filter (i.e., only consider the repeated/periodic duplicated msg
		//-s: only spatial filter (i.e., only consider the duplicated messages with different locations but "same" occurance time
		//-ts: both
		
		type = args[0];
		classifiedLogDir = args[1];
		String extension = args[2];
		String maintainanceTimeFile = args[3];
		String reservationFile = args[4];
		outputDir = args[5];
		
		//loading the maintainance periods
		maintList = loadMaintenancePeriods(maintainanceTimeFile);
		
		//loading the reservation periods
		resList = loadReservationPeriods_5years(reservationFile);
		//resList = new ArrayList<ReservationPeriod>(); //@deprecated
		
		List<EventElement> totalEventList = new ArrayList<EventElement>();
		
		List<String> fileList = PVFile.getFiles(classifiedLogDir, extension);
		Iterator<String> iter = fileList.iterator();
		while(iter.hasNext())
		{
			String fileName = iter.next();
			String filePath = classifiedLogDir+"/"+fileName;
			System.out.println("Processing "+fileName);
			
			List<String> lineList = PVFile.readFile(filePath);
			List<RecordElement> sortedRecordList = sortRecords(lineList);
	
		//	addResvRecordsToRecordList(sortedRecordList, resList);
			
			System.out.println("Sorting the recordList again....");
			Collections.sort(sortedRecordList);
			String msgID = fileName.split("\\.")[0];
			//String outputSortedRecordFilePath = classifiedLogDir+"/"+msgID+".sort";
			
			//System.out.println("Writing sorted records to "+outputSortedRecordFilePath);
			//PVFile.print2File(sortedRecordList, outputSortedRecordFilePath);
			
//			if(msgID.equals("00062001"))
//				System.out.println();
			
			System.out.println("Filtering records .....");
//			if(msgID.equals("00070219"))
//				System.out.println("");
			List<EventElement> eventList = filterRecords(sortedRecordList);
			totalEventList.addAll(eventList);
			PVFile.print2File(eventList, outputDir+"/"+msgID+".fltr");
			
			//clearResvList();
		}
		
		//Group reserved records to one event and add it to event list.
		addResvRecordsToEventList(totalEventList, resList);
		List<String> reservEventList = new ArrayList<String>();
		Iterator<ReservationPeriod> itt = resList.iterator();
		while(itt.hasNext())
		{
			ReservationPeriod rp = itt.next();
			if(rp.reList.size()>0)
			{
				Collections.sort(rp.reList);
				reservEventList.add(rp.reList.get(0).toString());
//				if(rp.reList.get(0).toString().contains("2015-07-21 23:08:09.000000"))
//					System.out.println("");
			}
		}
		PVFile.print2File(reservEventList, outputDir+"/reserveEventList.txt");
	
		Collections.sort(totalEventList);
//		PVFile.print2File(resList, outputDir+"/resvEvents.txt");
		
		if(!filterFullRecordList.isEmpty())
			PVFile.print2File(filterFullRecordList, outputDir+"/totalRecordList.txt");
		
		PVFile.print2File(totalEventList, outputDir+"/allEvents.txt");
		System.out.println("done.");
	}
	
	public static void clearResvList()
	{
		Iterator<ReservationPeriod> iter = resList.iterator();
		while(iter.hasNext())
		{
			ReservationPeriod rp = iter.next();
			rp.reList.clear();
		}
	}
	
	public static void addResvRecordsToRecordList(List<RecordElement> sortedRecordList, List<ReservationPeriod> resvList)
	{
		Iterator<ReservationPeriod> iter = resvList.iterator();
		while(iter.hasNext())
		{
			ReservationPeriod rp = iter.next();
			if(rp.reList.size()>0)
				sortedRecordList.add(rp.reList.get(0));
		}
	}
	
	public static void addResvRecordsToEventList(List<EventElement> eventList, List<ReservationPeriod> resvList)
	{
		Iterator<ReservationPeriod> iter = resvList.iterator();
		while(iter.hasNext())
		{
			ReservationPeriod rp = iter.next();
			if(rp.reList.size()>0)
			{
				Collections.sort(rp.reList);
				RecordElement firstElement = rp.reList.get(0);
				EventElement ee = new EventElement(eventID++, firstElement.getMsgID(), firstElement.getSeverity(), firstElement.getCategory(), 
						firstElement.getLocation(), firstElement, firstElement.getLocation());
				ee.componentList.add(firstElement.getComponent());
				ee.recordElementList.addAll(rp.reList);
				eventList.add(ee);
			}	
		}		
	}	
	
	public static List<EventElement> filterRecords(List<RecordElement> recordList)
	{
		List<EventElement> pastEventList = new ArrayList<EventElement>();		
		if(recordList.isEmpty())
			return pastEventList;
		
		List<EventElement> latestEventList = new ArrayList<EventElement>();

		Iterator<RecordElement> iter = recordList.iterator();
		RecordElement firstElement = iter.next();
		EventElement ee = new EventElement(eventID++, firstElement.getMsgID(), firstElement.getSeverity(), firstElement.getCategory(), 
				firstElement.getLocation(),firstElement, firstElement.getLocation());
		ee.componentList.add(firstElement.getComponent());
		latestEventList.add(ee);
		while(iter.hasNext())
		{
			RecordElement re = iter.next();
			List<EventElement> toLoadEventList = check(re, latestEventList); //toLoadEventList is the old list.
			removeLoadEventFromLatestList(toLoadEventList, latestEventList);
			pastEventList.addAll(toLoadEventList);
		}
		
		if(!latestEventList.isEmpty())
		{
			pastEventList.addAll(latestEventList);
		}
		
		return pastEventList;
	}
	
	private static void removeLoadEventFromLatestList(List<EventElement> toLoadEventList, List<EventElement> latestEventList)
	{
		Iterator<EventElement> iter = toLoadEventList.iterator();
		while(iter.hasNext())
		{
			EventElement ee = iter.next();
			latestEventList.remove(ee);
		}
	}
	
	private static List<EventElement> check(RecordElement curRE, List<EventElement> latestEventList)
	{
		double curFTime = curRE.getDtime();
		String curAllocation = curRE.getAllocation();
		String curLocation = curRE.getLocation();
		
		List<EventElement> toLoadEventList = new ArrayList<EventElement>();
		
		boolean foundExist = false;
		
		Iterator<EventElement> iter = latestEventList.iterator();
		while(iter.hasNext())
		{
			EventElement ee = iter.next();
			RecordElement prevRE = ee.getLatestRecord();
			
			double prevFTime = prevRE.getDtime();
			String prevAllocation = prevRE.getAllocation();
			String prevLocation = prevRE.getLocation();
			
			double timeDiff = curFTime - prevFTime;
			
			if(type.equals("-ts"))
				if(timeDiff < sameAllocationInterval && 
						(curAllocation.equals(prevAllocation) || curAllocation.startsWith("Q")))
				{
					foundExist = processSpatialFilter(ee, curRE, curLocation);
					break;
				}
				else if(timeDiff>sameLocationMinInterval && 
						timeDiff<sameLocationMaxInterval && curLocation.equals(prevLocation))
				{
					foundExist = processTemporalFilter(ee, curRE, curLocation);
					break;
				}
				else //this is a different event
				{
/*					if(timeDiff>=sameAllocationInterval)
					{
						toLoadEventList.add(ee);
					}*/
					toLoadEventList.add(ee);
				}
			else if(type.equals("-t"))
			{
				if(timeDiff>sameLocationMinInterval && timeDiff<sameLocationMaxInterval && curLocation.equals(prevLocation))
				{
					foundExist = processTemporalFilter(ee, curRE, curLocation);
					break;
				}
				else //this is a different event
				{
/*					if(timeDiff>=sameAllocationInterval)
					{
						toLoadEventList.add(ee);
					}*/
					toLoadEventList.add(ee);
				}
			}
			else //type.equals("-s")
			{
				if(timeDiff < sameAllocationInterval && (curAllocation.equals(prevAllocation) || curAllocation.startsWith("Q")))
				{
					foundExist = processSpatialFilter(ee, curRE, curLocation);
					break;
				}
				else //this is a different event
				{
/*					if(timeDiff>=sameAllocationInterval)
					{
						toLoadEventList.add(ee);
					}*/
					toLoadEventList.add(ee);
				}
			}
		}
		if(!foundExist)
		{
			try {
				//all records with the same messageID has the same severity and category
				EventElement newEE = new EventElement(eventID++, curRE.getMsgID(), curRE.getSeverity(),
						curRE.getCategory(), curLocation, curRE, curRE.getLocation());
				if (curRE.getAllocation().startsWith("MIR")) {
					int blockSize = Integer.parseInt(curRE.getAllocation().split("-")[3]);
					newEE.setBlockSize(blockSize);
				}
				newEE.componentList.add(curRE.getComponent());
				latestEventList.add(newEE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return toLoadEventList; //old list
	}
	
	private static boolean processSpatialFilter(EventElement ee, RecordElement curRE, String curLocation)
	{
		ee.recordElementList.add(curRE);
		if(!ee.componentList.contains(curRE.getComponent()))
			ee.componentList.add(curRE.getComponent());
		String curLocationKey = getLocationKeyName(curLocation);
		Integer count = ee.locationKeyMap.get(curLocationKey);
		if(count == null)
			ee.locationKeyMap.put(curLocationKey, new Integer(1));
		else
			ee.locationKeyMap.put(curLocationKey, new Integer(count+1));
		ee.setLatestRecord(curRE);
		boolean foundExist = true;
		return foundExist;
	}
	
	private static boolean processTemporalFilter(EventElement ee, RecordElement curRE, String curLocation)
	{
		ee.recordElementList.add(curRE);
		if(!ee.componentList.contains(curRE.getComponent()))
			ee.componentList.add(curRE.getComponent());		
		String curLocationKey = getLocationKeyName(curLocation);
		Integer count = ee.locationKeyMap.get(curLocationKey);
		if(count == null)
			ee.locationKeyMap.put(curLocationKey, new Integer(1));
		else
			ee.locationKeyMap.put(curLocationKey, new Integer(count+1));
		boolean foundExist = true;
		return foundExist;
	}
	
	public static List<MaintainancePeriod> loadMaintenancePeriods(String maintainanceTimeFile)
	{
		List<MaintainancePeriod> maintList = new ArrayList<MaintainancePeriod>();
		List<String> maintLineList = PVFile.readFile(maintainanceTimeFile);
		Iterator<String> iter = maintLineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] s = line.split(",");
			double startTime = RecordElement.computeDoubleTimeinSeconds(s[0]);
			double endTime = RecordElement.computeDoubleTimeinSeconds(s[1]);
			maintList.add(new MaintainancePeriod(startTime, endTime));
		}
		return maintList;
	}
	
	public static List<ReservationPeriod> loadReservationPeriods_2015(String reservationFile)
	{
		List<ReservationPeriod> resList = new ArrayList<ReservationPeriod>();
		List<String> resrvLineList = PVFile.readFile(reservationFile);
		Iterator<String> iter = resrvLineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.startsWith("#"))
				continue;
			String[] s = RecordSplitter.partition(line);
			double startTime = RecordElement.computeDoubleTimeinSeconds(s[1]);
			double endTime = RecordElement.computeDoubleTimeinSeconds(s[2]);
			String[] ss = s[8].split(",");
			List<String> blockList = ConversionHandler.convertStringArray2StringList(ss);
			ReservationPeriod rp = new ReservationPeriod(startTime, endTime, blockList);
			resList.add(rp);
		}
		return resList;
	}
	
	public static List<ReservationPeriod> loadReservationPeriods_5years(String reservationFile)
	{
		List<ReservationPeriod> resList = new ArrayList<ReservationPeriod>();
		List<String> resrvLineList = PVFile.readFile(reservationFile);
		Iterator<String> iter = resrvLineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.startsWith("#"))
				continue;
			String[] s = RecordSplitter.partition(line);
			double startTime = RecordElement.computeDoubleTimeinSeconds(s[0]);
			double endTime = RecordElement.computeDoubleTimeinSeconds(s[1]);
			String[] ss = s[13].split(",");
			List<String> blockList = ConversionHandler.convertStringArray2StringList(ss);
			ReservationPeriod rp = new ReservationPeriod(startTime, endTime, blockList);
			resList.add(rp);
		}
		return resList;
	}	
	
	public static boolean isMaintenanceMsg(RecordElement re)
	{
		Iterator<MaintainancePeriod> iter = maintList.iterator();
		boolean contains = false;
		while(iter.hasNext())
		{
			MaintainancePeriod p = iter.next();
			contains = p.containRecord(re);
			if(contains)
				break;
		}
		return contains;
	}
	
	public static boolean isMaintenanceMsg(double eventTime)
	{
		Iterator<MaintainancePeriod> iter = maintList.iterator();
		boolean contains = false;
		while(iter.hasNext())
		{
			MaintainancePeriod p = iter.next();
			contains = p.containRecord(eventTime);
			if(contains)
				break;
		}
		return contains;
	}
	
	public static boolean isReservedMsg(RecordElement re)
	{
		String blockCode = re.getAllocation();
		String locationCode = re.getLocation();
		Iterator<ReservationPeriod> iter = resList.iterator();
		while(iter.hasNext())
		{
			ReservationPeriod rp = iter.next();
//			if(rp.getStartTime()==1.437528957E9)
//				System.out.println("");
			if(rp.getStartTime()<=re.getDtime()&&re.getDtime()<=rp.getEndTime())
			{
				boolean contains = rp.checkBlockCodeIntersection(blockCode, locationCode);
				if(contains)
				{
					rp.reList.add(re);
					return true;
				}
			}
			//if(rp.getEndTime()<re.getDtime())
			//	break;
		}
		//relaxed reservation period because reservation period may delay from an event (reservation was done by administrator based on some event).
		iter = resList.iterator();
		while(iter.hasNext())
		{
			ReservationPeriod rp = iter.next();
			if(rp.getStartTime()-1200<=re.getDtime()&&re.getDtime()<=rp.getEndTime())
			{
				boolean contains = rp.checkBlockCodeIntersection(blockCode, locationCode);
				if(contains)
				{
					rp.reList.add(re);
					return true;
				}
			}
			//if(rp.getEndTime()<re.getDtime())
			//	break;
		}
		return false;
	}
	
	public static boolean isCustomizedUserFiltering(RecordElement re)
	{
		if(re.getMsgID().equals("00030007"))
			return true;
		else
			return false;
	}
	
	
	public static List<RecordElement> sortRecords(List<String> recordList)
	{
		List<RecordElement> rList = new ArrayList<RecordElement>();
		Iterator<String> iter = recordList.iterator();
		while(iter.hasNext())
		{
			String record = iter.next();
			String[] s = RecordSplitter.partition(record);

			String recordID = s[0].trim();
//			if(recordID.equals("42461328"))
//				System.out.println();
			if(!NumericChecker.isNumeric(recordID))
				continue;
			String messageID = s[1].trim();
			String component = s[3].trim();
			String severity = s[4].trim();
			String category = s[2].trim();
			String time = s[5].trim();
			String allocation = s[7].trim();
			String location = s[8].trim();
			
//			if(time.equals("2015-07-21 20:27:31.793444"))
//				System.out.println();
			
			RecordElement re = new RecordElement(recordID, messageID, time, allocation, location, severity, category, component, record);
			
			//removing the maintainance related event records
			//TODO
//			if(re.getMsgID().equals("00070219")&&re.getTime().startsWith("2015-10-19 22:08:38"))				
//				System.out.println();
			boolean maint = isMaintenanceMsg(re);
			boolean resve = isReservedMsg(re);
			
			boolean custo = isCustomizedUserFiltering(re);
			
			if(!maint && !resve && !custo)
				rList.add(re);
			//if(!maint && !custo)
			//	rList.add(re);
		}
		
		Collections.sort(rList);
		return rList;
	}
	
	public static String getLocationKeyName(String location)
	{
		StringBuilder sb = new StringBuilder();
		if(location==null || location.equals(""))
			return "NULL";
		String[] s = location.split("-");
		for(int i = 0;i<s.length;i++)
		{
			char[] cc = s[i].toCharArray();
			sb.append(cc[0]);
		}
		return sb.toString();
	}
}
