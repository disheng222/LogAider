/**
 * @author Sheng Di
 * @class ComputeLocationBasedOnIOCode.java
 * @description  
 */

package tools;

import java.util.ArrayList;
import java.util.List;

import element.MidPlaneLocationElement;

public class ComputeLocationBasedOnIOCode {

	/**
	 * The code must be in the form of Qxy-Iz-Jw
	 * @param code
	 * @return
	 */
	public static List<MidPlaneLocationElement> computeLocations(String code)
	{
		if(!code.startsWith("Q"))
		{
			System.out.println("Error: code does not start with Q.: code: "+code);
			//System.exit(0);
			return null;
		}
		
		List<MidPlaneLocationElement> resultList = new ArrayList<MidPlaneLocationElement>();
		String[] s = code.split("-");
		if(s.length==1)
		{
			char[] c = s[0].toCharArray();
			int row = Integer.parseInt(String.valueOf(c[1]));
			int startColumnIndex = 0;
			if(c[2]=='H') //only two options: G or H (G means left(bottom), and H means right(top))
				startColumnIndex = 8;
			
			for(int i = 0;i<8;i++)
			{
				int column = startColumnIndex + i;
				MidPlaneLocationElement e = new MidPlaneLocationElement("", row, column, 0);
				resultList.add(e);
				e = new MidPlaneLocationElement("", row, column, 1);
				resultList.add(e);
			}
		}
		else if(s.length==2)
		{
			char[] c = s[0].toCharArray();
			int row = Integer.parseInt(String.valueOf(c[1]));
			int startColumnIndex = 0;
			if(c[2]=='H') //only two options: G or H (G means left(bottom), and H means right(top))
				startColumnIndex = 8;
			
			char[] c2 = s[1].toCharArray();
			int cindex = Integer.parseInt(String.valueOf(c2[1]));
			int column = startColumnIndex + cindex;
			MidPlaneLocationElement e = new MidPlaneLocationElement("", row, column, 0);
			resultList.add(e);
			e = new MidPlaneLocationElement("", row, column, 1);
			resultList.add(e);
		}
		else if(s.length==3)
		{
			char[] c = s[0].toCharArray();
			int row = Integer.parseInt(String.valueOf(c[1]));
			int startColumnIndex = 0;
			if(c[2]=='H') //only two options: G or H (G means left(bottom), and H means right(top))
				startColumnIndex = 8;
			
			char[] c2 = s[1].toCharArray();
			int cindex = Integer.parseInt(String.valueOf(c2[1]));
			int column = startColumnIndex + cindex;
			
			int midPlane = 0;
			int jIndex = Integer.parseInt(s[2].replace("J", ""));
			if(jIndex<=3)
				midPlane = 0;
			else
				midPlane = 1;
			MidPlaneLocationElement e = new MidPlaneLocationElement("", row, column, midPlane);
			resultList.add(e);
		}
		else
		{
			System.out.println("Error: code can't be three parts separated by -: code:"+code);
			System.exit(0);
		}
		
		return resultList;
	}
	
	public static String mapIODrawerCodeToComputeBlockCode(String ioDrawerCode)
	{
		String[] s = ioDrawerCode.split("-");
		String ioRack = s[0].replace("Q", ""); 
		String ioDrawer = s[1].replace("I", ""); //0-8
		
		if(ioRack.equals("0G"))
		{
			return "MIR-00000-33FF1-8192";
		}
		else if(ioRack.equals("0H"))
		{
			return "MIR-40000-73FF1-8192";
		}
		else if(ioRack.equals("1G"))
		{
			return "MIR-04000-37FF1-8192";
		}
		else if(ioRack.equals("1H"))
		{
			return "MIR-44000-77FF1-8192";
		}
		else if(ioRack.equals("2G"))
		{
			return "MIR-08000-3BFF1-8192";
		}
		else if(ioRack.equals("2H"))
		{
			return "MIR-48000-7BFF1-8192";
		}
		else
		{
			return "MIR-00000-7BFF1-8192";
		}
	}
	
	public static boolean checkIODrawerCodeOverlapComputeBlockCode(String ioDrawerCode, String computeBlockCode)
	{
		String ioBlockCode = mapIODrawerCodeToComputeBlockCode(ioDrawerCode);
		int sum = ComputeLocationBasedOnMIRCode.checkBlockOverlapBlock(ioBlockCode, computeBlockCode);
		if(sum==0)
			return false;
		else
			return true;
	}
}
