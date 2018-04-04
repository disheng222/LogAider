package element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import filter.RecordElement;
import tools.ComputeLocationBasedOnMIRCode;
import util.ConversionHandler;

public class ReservationPeriod {
	private double startTime;
	private double endTime;
	public List<String> blockCodeList = new ArrayList<String>(6);
	public List<RecordElement> reList = new ArrayList<RecordElement>();
	
	public ReservationPeriod(double startTime, double endTime, List<String> blockCodeList) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.blockCodeList = blockCodeList;
	}

	public double getStartTime() {
		return startTime;
	}
	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}
	public double getEndTime() {
		return endTime;
	}
	public void setEndTime(double endTime) {
		this.endTime = endTime;
	}
	
	public int compareTo(ReservationPeriod other)
	{
		if(startTime < other.startTime)
			return -1;
		else if(startTime > other.startTime)
			return 1;
		else
			return 0;
	}

	public boolean checkBlockCodeIntersection(String blockCode, String locationCode)
	{
		//if(blockCode.startsWith("Q")||blockCode.startsWith("Default"))
		//if(blockCode.startsWith("Q"))
		//	return false;
		
		Iterator<String> iter = blockCodeList.iterator();
		while(iter.hasNext())
		{
			String blockCodeItem = iter.next();
			if(blockCode.startsWith("MIR")&&blockCode.contains("-"))
			{
				int overlap = ComputeLocationBasedOnMIRCode.checkBlockOverlapBlock(blockCodeItem, blockCode);
				if(overlap!=0)
					return true;				
			}

			boolean contains = ComputeLocationBasedOnMIRCode.checkMidplaneOverlapBlock(locationCode, blockCodeItem);
			if(contains)
				return true;
		}
		
		return false;
	}
	
	public static void main(String[] args)
	{
		String blockCode = "MIR-008C0-33BF1-512,MIR-408C0-73BF1-512";
		String locationCode1 = "R0F-M0-N05";
		String locationCode2 = "R07-M0-N03-J06";
		
		String[] ss = blockCode.split(",");
		List<String> blockList = ConversionHandler.convertStringArray2StringList(ss);
		ReservationPeriod rp = new ReservationPeriod(0, 0, blockList);		
		boolean a1 = rp.checkBlockCodeIntersection(blockCode, locationCode1);
		boolean a2 = rp.checkBlockCodeIntersection(blockCode, locationCode2);
		
		System.out.println("a1="+a1+"; a2="+a2);
		
	}
	
	public String toString()
	{
		String s = String.valueOf(reList.size())+","+startTime+","+endTime+"-----------------------\n";
		Iterator<RecordElement> iter = reList.iterator();
		while(iter.hasNext())
		{
			RecordElement re = iter.next();
			s+=re.toString()+"\n";
		}
		return s;
	}
	
}
