package plot.visionController;

public class BaseController {

	public static String toHexFormat(int number, int expLength)
	{
		String s = Integer.toHexString(number);
		if(s.length()<expLength)
		{
			StringBuilder sb = new StringBuilder();
			int miss = expLength - s.length();
			for(int i = 0;i<miss;i++)
				sb.append(0);
			sb.append(s);
			return sb.toString().toUpperCase();
		}
		else
		{
			return s.toUpperCase();
		}
	}
	
	public static String toBinaryFormat(int number, int expLength)
	{
		String s = Integer.toBinaryString(number);
		if(s.length()<expLength)
		{
			StringBuilder sb = new StringBuilder();
			int miss = expLength - s.length();
			for(int i = 0;i<miss;i++)
				sb.append(0);
			sb.append(s);
			return sb.toString().toUpperCase();
		}
		else
		{
			return s.toUpperCase();
		}
	}
	
	public static String toOctalFormat(int number, int expLength)
	{
		String s = Integer.toOctalString(number);
		if(s.length()<expLength)
		{
			StringBuilder sb = new StringBuilder();
			int miss = expLength - s.length();
			for(int i = 0;i<miss;i++)
				sb.append(0);
			sb.append(s);
			return sb.toString().toUpperCase();
		}
		else
		{
			return s.toUpperCase();
		}
	}
	
	public static String setOffsetToLastLetter(String baseString, int decimalID, int offset, 
			String curBase, String rowBase)
	{
		if(curBase.equals(rowBase) && offset==0)
			return baseString;
		
		char[] c = baseString.toCharArray();
		
		char last = c[c.length-1];
		char newLast = (char)(last+offset+7);
		
		if(curBase.equals(rowBase) && offset!=0)
		{
			c[c.length-1] = newLast;
			return new String(c);
		}
		
		if(!curBase.equals(rowBase))
		{
			if(curBase.equals("binary"))
			{
				if(rowBase.equals("decimal"))
				{
					String rowString = String.valueOf(decimalID >> 1);
					return rowString+newLast;
				}
				else if(rowBase.equals("oct"))
				{
					String rowString = Integer.toOctalString(decimalID >> 1);
					return rowString+newLast;
				}
				else if(rowBase.equals("hex"))
				{
					String rowString = Integer.toHexString(decimalID >> 1);
					return rowString+newLast;
				}
			}
			else
			{
				System.out.println("not implemented yet in this situation.");
				System.exit(0);
			}
		}
		
		return null;
	}
	
}
