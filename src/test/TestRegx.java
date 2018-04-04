package test;

import java.util.regex.Pattern;

public class TestRegx {

	public static void main(String[] args)
	{
		//String s = "(\\d+|(\\D\\d+)-(\\D\\d+)-(\\D\\d+)-(\\D\\d+))(\\.)?";
		String s = "the primarymc has detected that this subnetmc process has terminated. : subnet=*";
		System.out.println(s);
		Pattern.compile(s, Pattern.CASE_INSENSITIVE);
		String input = "The PrimaryMc has detected that this SubnetMc process has terminated. : Subnet=Subnet1";
		boolean b = matches(input, s);
		System.out.println(b);
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
			if(s.contains("*"))
			{
				if(s.contains("="))
				{
					if(!d[i].contains("="))
					{
						match = false;
						break;
					}
					else
					{
						String key = s.split("=")[0].trim();
						String key_ = d[i].split("=")[0].trim();
						if(!key.equals(key_))
						{
							match = false;
							break;
						}
					}
					
				}
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
