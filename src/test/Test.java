package test;

import java.text.DecimalFormat;

public class Test {

	public static void main(String[] args)
	{
		String pattern = "this is a good *. There are d+ friends n+";
		
		String str1 = "this is a good boy. There are 12 friends";
		String str2 = "this is a good girl. There are 0x12 friends";
		String str3 = "this is a good boy. There are R12-R12 friends";
		String str4 = "abc";
		
		System.out.println(matches(str1, pattern));
		System.out.println(matches(str2, pattern));
		System.out.println(matches(str3, pattern));
		System.out.println(matches(str4, pattern));
		
		 DecimalFormat df = new DecimalFormat("00");
		 int a = 4;
		 System.out.println(df.format(a));
		 
		 String s = Integer.toHexString(32);
		 if(s.length()==1)
			 System.out.println("0"+s.toUpperCase());
		 else
			 System.out.println(s.toUpperCase());
	}
	
	private static boolean isNum(String str)
	{
		if(str.startsWith("0x"))
		{
			return true;
		}
		else 
		{
			char c = str.toCharArray()[0];
			if(Character.isDigit(c))
				return true;
		}
		return false;
	}
	
	public static boolean matches(String descMsg, String pattern)
	{
		boolean match = true;
		descMsg = descMsg.toLowerCase();
		descMsg = descMsg.replace(".", "");
		pattern = pattern.replace(".", "");
		String[] p = pattern.split("\\s+");
		String[] d = descMsg.split("\\s+");
		for(int i = 0;i<p.length;i++)
		{
			String s = p[i];
			if(s.equals("*"))
			{
				//do nothing
			}
			else if(s.equals("d+"))
			{
				if(!isNum(d[i]))
				{
					match = false;
					break;
				}
			}
			else if(s.equals("n+"))
			{
				match = true;
				break;
			}
			else //compare two strings
			{
				if(!p[i].equals(d[i]))
				{
					match = false;
					break;
				}
			}
		}
		return match;
	}
}
