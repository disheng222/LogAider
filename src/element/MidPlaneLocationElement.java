package element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tools.ComputeLocationBasedOnMIRCode;
import util.PVFile;

public class MidPlaneLocationElement implements Comparable<MidPlaneLocationElement>{

	private String code;
	private int rackRow;
	private int rackColumn;
	private int midplaneID;

	public NodeBoard[][] nodeboards = new NodeBoard[4][4];
	
	private int x, y, z, w;
	
	private int eventCount = 0;
	
	public MidPlaneLocationElement(String code, int rackRow, int rackColumn, int midplaneID) {
		this.code = code;
		this.rackRow = rackRow;
		this.rackColumn = rackColumn;
		this.midplaneID = midplaneID;
		for(int i = 0;i<4;i++)
			for(int j = 0;j<4;j++)
				nodeboards[i][j] = new NodeBoard(i,j,0);
	}
	
	public void resetNodeboradSelectMark()
	{
		for(int i = 0;i<4;i++)
			for(int j = 0;j<4;j++)
				nodeboards[i][j].setSelect(false);
	}
	
	public MidPlaneLocationElement(String code, int rackRow, int rackColumn,
			int midplaneID, int x, int y, int z, int w) {
		this(code, rackRow, rackColumn, midplaneID);
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public int getRackRow() {
		return rackRow;
	}
	public void setRackRow(int rackRow) {
		this.rackRow = rackRow;
	}
	public int getRackColumn() {
		return rackColumn;
	}
	public void setRackColumn(int rackColumn) {
		this.rackColumn = rackColumn;
	}
	public int getMidplaneID() {
		return midplaneID;
	}
	public void setMidplaneID(int midplaneID) {
		this.midplaneID = midplaneID;
	}
	public MidPlaneLocationElement getNext()
	{
		if(w!=12) //jump to the next small block (midplane)
		{
			int w_ = w + 4;
			MidPlaneLocationElement ee = ComputeLocationBasedOnMIRCode.computeMidplaneLocation(null, x, y, z, w_);
			return ee;
		}
		else //w==12
		{
			int w_ = 0;
			if(z==12) //jump to the next big block (8 racks)
			{
				int x_, y_, z_ = 0;
				if(x==0)
				{
					x_ = x + 4;
					y_ = y;
				}
				else //x==4
				{
					x_ = 0;
					y_ = y + 4;
				}
				MidPlaneLocationElement ee = ComputeLocationBasedOnMIRCode.computeMidplaneLocation(null, x_, y_, z_, w_);
				return ee;
			}
			else //jump to the next median-size block (a pair of rack)
			{
				int z_ = z+4;
				MidPlaneLocationElement ee = ComputeLocationBasedOnMIRCode.computeMidplaneLocation(null, x, y, z_, w_);
				return ee;
			}
		}
	}
	
//	public MidPlaneLocationElement clone()
//	{
//		MidPlaneLocationElement ee = new MidPlaneLocationElement(code, rackRow, rackColumn, midplaneID, x, y, z, w);
//		return ee;
//	}
	
	public String toString()
	{
		return buildString(rackRow, rackColumn, midplaneID);
	}
	
	public boolean equals(Object other)
	{
		if(other instanceof MidPlaneLocationElement)
		{
			MidPlaneLocationElement otherE = (MidPlaneLocationElement)other;
			if(rackRow==otherE.rackRow && rackColumn==otherE.rackColumn && midplaneID == otherE.midplaneID)
				return true;
			else
				return false;
		}
		else
			return false;
	}
	
	public static String buildString(int rackRow, int rackColumn, int midplaneID)
	{
		StringBuilder sb = new StringBuilder("R(");
		sb.append(rackRow).append(",").append(rackColumn).append("),M").append(midplaneID);
		return sb.toString();		
	}
	
	public static void main(String[] args)
	{
		String code = "040C0";

		List<MidPlaneLocationElement> list = new ArrayList<MidPlaneLocationElement>();
		MidPlaneLocationElement e = ComputeLocationBasedOnMIRCode.computeMidplaneLocation(code);
		System.out.println(e);
		for(int i = 0;i<20;i++)
		{
			MidPlaneLocationElement next = e.getNext();
			list.add(next);
			System.out.println(next.toString());
			e = next;
		}
	}

	public int getEventCount() {
		return eventCount;
	}

	public void setEventCount(int eventCount) {
		this.eventCount = eventCount;
	}
	
	public int compareTo(MidPlaneLocationElement other)
	{
		if(eventCount < other.eventCount)
			return -1;
		else if(eventCount > other.eventCount)
			return 1;
		else
			return 0;
	}
	
	public void increaseForNodeBoards()
	{
		for(int i = 0;i<4;i++)
			for(int j = 0;j<4;j++)
			{
				NodeBoard nb = nodeboards[i][j];
				if(nb.isSelect())
					nb.setCount(nb.getCount()+1);
			}
	}
	
	
	public String addCounter(
			Map<String, Counter> level1Map, 
			Map<String, Counter> level2Map)
	{
		StringBuilder sb = new StringBuilder("R");
		String rackColumn16 = Integer.toHexString(rackColumn);
		sb.append(rackRow).append(rackColumn16);
		String rackID = sb.toString();
		Counter le1Counter = level1Map.get(rackID);
		if(le1Counter == null)
		{	
			le1Counter = new Counter(rackID, 0);
			level1Map.put(rackID, le1Counter);
		}
		le1Counter.setCounter(le1Counter.getCounter()+eventCount);
		
		String midID = sb.append("-").append("M").append(midplaneID).toString();
		
		Counter le2Counter = level2Map.get(midID);
		if(le2Counter == null)
		{
			le2Counter = new Counter(midID, 0);
			level2Map.put(midID, le2Counter);
		}
		le2Counter.setCounter(le2Counter.getCounter()+eventCount);
		
		return midID;
	}
	
	public void addCounter(
			Map<String, Counter> level1Map, 
			Map<String, Counter> level2Map,
			Map<String, Counter> level3Map)
	{
		String midID = addCounter(level1Map, level2Map);
		for(int i = 0;i<4;i++)
			for(int j = 0;j<4;j++)
			{
				NodeBoard nb = nodeboards[i][j];
				int nid = i*4+j;
				int num = nb.getCount();
				if(num>0)
				{
					
					StringBuilder sb = new StringBuilder(midID);
					String nbID = sb.append("-N").append(PVFile.df2.format(nid)).toString();
					Counter counter = level3Map.get(nbID);
					if(counter==null)
					{
						counter = new Counter(nbID, 0);
						level3Map.put(nbID, counter);
					}
					counter.setCounter(counter.getCounter()+num);
				}
			}
	}
}
