/**
 * @author Sheng Di
 * @class ComputeLocationBasedOnMIRCode.java
 * @description  
 */


package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import element.MidPlaneLocationElement;
import element.NodeBoard;

public class ComputeLocationBasedOnMIRCode {
	
	public static HashMap<String, MidPlaneLocationElement> midplaneMap = new HashMap<String, MidPlaneLocationElement>();
	
	public static List<MidPlaneLocationElement> computeLocations(String code)
	{
		String[] s = code.split("-");
//		System.out.println("s[s.length-1]="+s[s.length-1]);
		int nodeCount = Integer.parseInt(s[s.length-1]);
		
		String firstMidPlaneCode = s[1];
		if(firstMidPlaneCode.startsWith("R"))
			firstMidPlaneCode = convertNodeLocToMidPlaneBlockCode(firstMidPlaneCode);
		MidPlaneLocationElement mp = computeMidplaneLocation(firstMidPlaneCode);
		
		List<MidPlaneLocationElement> eList = new ArrayList<MidPlaneLocationElement>();
		
		eList.add(mp);
		
		char[] cc = firstMidPlaneCode.toCharArray();
		//System.out.println("code="+code+":::"+firstMidPlaneCode);
		int x1 = Integer.parseInt(String.valueOf(cc[0]));
		int y1 = Integer.parseInt(String.valueOf(cc[1]), 16);
		int x1_mod = x1%4;
		int y1_mod = y1%4;
		
		int x1_num = 4 - x1_mod;
		int y1_num = 4 - y1_mod;
		
		int maxNum_1stMidplane = x1_num*y1_num*32;

		int restNBNumAfter1stMidplane = nodeCount - maxNum_1stMidplane;
		
		int restMidNum;
		if(nodeCount%512==0)
			restMidNum = restNBNumAfter1stMidplane/512;
		else
			restMidNum = restNBNumAfter1stMidplane/512 +1;
		for(int i = 0;i<restMidNum;i++)
		{
			MidPlaneLocationElement mpp = mp.getNext();
			eList.add(mpp);
			mp = mpp;
		}
	
		//TODO: fill out nodeboards
		
		String secondMidPlaneCode = s[2];

		if(secondMidPlaneCode.startsWith("R"))
			secondMidPlaneCode = convertNodeLocToMidPlaneBlockCode(secondMidPlaneCode);		
		cc = secondMidPlaneCode.toCharArray();

		
		int x2 = Integer.parseInt(String.valueOf(cc[0]));
		int y2= Integer.parseInt(String.valueOf(cc[1]), 16);
		int x2_mod = x2%4;
		int y2_mod = y2%4;
		
		Iterator<MidPlaneLocationElement> it = eList.iterator();
		MidPlaneLocationElement firstMP = it.next();
		firstMP.resetNodeboradSelectMark();
		if(nodeCount <= maxNum_1stMidplane)
		{
			for(int i = y1_mod;i<=y2_mod;i++)
				for(int j = x1_mod;j<=x2_mod;j++)
				{
					NodeBoard nb = firstMP.nodeboards[i][j];
					nb.setSelect(true);
				}
		}
		else
		{
			int totalRestNBNum = nodeCount;
			for(int i = x1_mod;i<4;i++)
				for(int j = y1_mod;j<4;j++)
				{
					NodeBoard nb = firstMP.nodeboards[i][j];
					nb.setSelect(true);
					totalRestNBNum -= 32;
				}
			
			while(it.hasNext())
			{
				MidPlaneLocationElement mp_ = it.next();
				mp_.resetNodeboradSelectMark();
				if(totalRestNBNum >= 512)
				{
					for(int i = 0;i<4;i++)
						for(int j = 0;j<4;j++)
						{
							NodeBoard nb = mp_.nodeboards[i][j];
							nb.setSelect(true);
							totalRestNBNum -= 32;
						}
				}
				else //<512
				{
					for(int i = 0;i<y2_mod;i++)
						for(int j = 0;j<x2_mod;j++)
						{
							NodeBoard nb = mp_.nodeboards[i][j];
							nb.setSelect(true);
							totalRestNBNum -= 32;
						}			
				}
			}
		}
		
		return eList;
	}
	
	public static MidPlaneLocationElement computeMidplaneLocation(String code)
	{
		String nodeBlock = code;
		if(code.startsWith("R"))
		{
			nodeBlock = convertNodeLocToMidPlaneBlockCode(code);  //return xxxxx-xxxxx
			nodeBlock = nodeBlock.split("-")[0];
		}
		char[] cc = nodeBlock.toCharArray();
		int x;
		int y;
		int z;
		int w;
		try {
			x = Integer.parseInt(String.valueOf(cc[0]));
			y = Integer.parseInt(String.valueOf(cc[1]), 16);
			z = Integer.parseInt(String.valueOf(cc[2]), 16);
			w = Integer.parseInt(String.valueOf(cc[3]), 16);
			
			//System.out.println("code="+code+",nodeBlock="+nodeBlock+" : x="+x+",y="+y);
			MidPlaneLocationElement ee = computeMidplaneLocation(nodeBlock, x, y, z, w);
			return ee;			
		} catch (Exception e) { //code is like: MIR-R23M0-R22M0-1024
			//System.out.println("code="+code+",nodeBlock="+nodeBlock);
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	public static MidPlaneLocationElement computeMidplaneLocation(String code, int x, int y, int z, int w)
	{
		int a = y/4;
		int b = 2*x + f(z/4,w/4);
		int c = C(w/4);
		
		String key = MidPlaneLocationElement.buildString(a, b, c);
		
		MidPlaneLocationElement e = midplaneMap.get(key);
		if(e == null)
		{
			e = new MidPlaneLocationElement(code, a,b,c, x, y, z, w);
			midplaneMap.put(key,  e);
		}

		return e;
	}
	
	private static int f(int a1, int a2)
	{
		return H(a1)+Q(a2);
	}
	
	private static int H(int z_4)
	{
		switch(z_4)
		{
		case 0: 
			return 0;
		case 1:
			return 4;
		case 2:
			return 6;
		case 3:
			return 2;
		default:
			System.out.println("Error: wrong z_4: z/4="+z_4);
			System.exit(0);
			return -1;
		}
	}
	
	private static int Q(int w_4)
	{
		if(w_4<=1)
			return 0;
		else
			return 1;
	}
	
	public static int C(int w_4)
	{
		switch(w_4)
		{
		case 0: 
			return 0;
		case 1:
			return 1;
		case 2:
			return 1;
		case 3:
			return 0;
		default:
			System.out.println("Error: wrong w_4: w/4="+w_4);
			System.exit(0);
			return -1;
		}
	}
	
	public static int checkBlockOverlapBlock(String code1, String code2)
	{
		List<MidPlaneLocationElement> list1 = computeLocations(code1);
		List<MidPlaneLocationElement> list2 = computeLocations(code2);
		Iterator<MidPlaneLocationElement> iter = list1.iterator();
		int sum = 0;
		while(iter.hasNext())
		{
			MidPlaneLocationElement m = iter.next();
			if(list2.contains(m))
				sum++;
		}
		return sum;
	}
	
	public static boolean checkMidplaneOverlapBlock(String midplaneCode, String blockCode)
	{
		String[] s = midplaneCode.split("-");
		if(s.length<2||!s[1].startsWith("M"))
			return false;
		char[] c = s[0].toCharArray();
		int row = Integer.parseInt(String.valueOf(c[1]));
		String columnS = String.valueOf(c[2]);
		int column = Integer.parseInt(columnS, 16);
		char[] cc = s[1].toCharArray();
			
		int mp = Integer.parseInt(String.valueOf(cc[1]));
		MidPlaneLocationElement midplane = new MidPlaneLocationElement(midplaneCode, row, column, mp);
		
		boolean result = checkMidplaneOverlapBlock(midplane, blockCode);
		return result;
	}
	
	public static boolean checkMidplaneOverlapBlock(MidPlaneLocationElement midplane, String blockCode)
	{
		List<MidPlaneLocationElement> list = computeLocations(blockCode);
		if(list.contains(midplane))
			return true;
		else
			return false;
	}
	
	public static String convertNodeLocToMidPlaneBlockCode(String nodeLocation)
	{
		String nodeLocation_ = nodeLocation;
		if(!nodeLocation.contains("-"))
		{
			char[] ss = nodeLocation.toCharArray();
			nodeLocation_ = "R"+ss[1]+ss[2]+"-"+ss[3]+ss[4];
		}
		StringBuilder sb1 = new StringBuilder(), sb2 = new StringBuilder();
		String[] s = nodeLocation_.split("-");
		String locR_16 = s[0].replace("R", "");
		int locR = Integer.parseInt(locR_16, 16);
		if(locR>=0&&locR<=7)
		{
			sb1.append("00");
			sb2.append("33");
		}
		else if(locR>=8&&locR<=15)
		{
			sb1.append("40");
			sb2.append("73");
		}
		else if(locR>=16&&locR<=23)
		{
			sb1.append("04");
			sb2.append("37");
		}
		else if(locR>=24&&locR<=31)
		{
			sb1.append("44");
			sb2.append("77");
		}
		else if(locR>=32&&locR<=39)
		{
			sb1.append("08");
			sb2.append("3B");
		}
		else if(locR>=40&&locR<=47)
		{
			sb1.append("48");
			sb2.append("7B");
		}		

		switch(locR%8)
		{
		case 0:
		case 1:
			sb1.append("0");
			sb2.append("3");
			break;
		case 4:
		case 5:
			sb1.append("4");
			sb2.append("7");	
			break;
		case 6:
		case 7:
			sb1.append("8");
			sb2.append("B");
			break;
		case 2:
		case 3:
			sb1.append("C");
			sb2.append("F");
			break;
		}
		
		if(s[1].equals("L"))
		{
			sb1.append("00");
			sb2.append("F1");
			return sb1.toString()+"-"+sb2.toString();
		}
		
		int locM = Integer.parseInt(s[1].replace("M", ""));
		if(locR%2==0)
		{
			if(locM==0)
			{
				sb1.append("00");
				sb2.append("31");
			}
			else //==1
			{
				sb1.append("40");
				sb2.append("71");
				
			}
			
		}
		else //==1
		{
			if(locM==1)
			{
				sb1.append("80");
				sb2.append("B1");
			}
			else //==0
			{
				sb1.append("C0");
				sb2.append("F1");
				
			}
		}
		
		return sb1.toString()+"-"+sb2.toString();
	}
	
	public static boolean checkNodeOverlapBlock(String nodeLocation, String blockCode)
	{
		String nodeBlock = convertNodeLocToMidPlaneBlockCode(nodeLocation);
		boolean result = checkMidplaneOverlapBlock(nodeBlock, blockCode);
		return result;	
	}
	
	/**
	 * Compute the number of overlapping elements between the two blocks
	 * @param midplaneList
	 * @param blockCode
	 * @return
	 */
	public static int checkBlockOverlapBlock(List<MidPlaneLocationElement> midplaneList, String blockCode)
	{
		int sum = 0;
		List<MidPlaneLocationElement> list = computeLocations(blockCode);
		Iterator<MidPlaneLocationElement> iter = midplaneList.iterator();
		while(iter.hasNext())
		{
			MidPlaneLocationElement mpe = iter.next();
			if(list.contains(mpe))
				sum++;
		}
		return sum;
	}
			
	public static int checkIOBlockOverlapBlock(String ioCode, String blockCode)
	{
		List<MidPlaneLocationElement> eList = ComputeLocationBasedOnIOCode.computeLocations(ioCode);
		int num = checkBlockOverlapBlock(eList, blockCode);
		return num;
	}
	
/*	public static void main(String[] args)
	{
		String codes = "MIR-00000-73FF1-16384";
		List<MidPlaneLocationElement> list = computeLocations(codes);
		for(int i = 0;i<list.size();i++)
			System.out.println(list.get(i));
		
		System.out.println("----------------");
		//String codes2 = "MIR-04000-77FF1-16384";
		String codes2 = "MIR-00C00-33FF1-2048";
		list = computeLocations(codes2);
		for(int i = 0;i<list.size();i++)
			System.out.println(list.get(i));
		
		int overlap = checkBlockOverlapBlock(codes, codes2);
		
		boolean check = checkNodeOverlapBlock("R1F-M1-N03-J00", "MIR-04000-77FF1-16384");
		System.out.println("check="+check);
		
		System.out.println("overlap="+overlap);
		System.out.println("done.");
	}*/
	
	public static void main(String[] args)
	{
		String blockCode = "MIR-00440-33771-512";
		String ioCode = "Q0G-I4-J02";
		
		int overlap = checkIOBlockOverlapBlock(ioCode, blockCode);
		System.out.println("overlap="+overlap);
		//String s = convertNodeLocToMidPlaneBlockCode("R23M0");
		//System.out.println(s);
		
	}
}
