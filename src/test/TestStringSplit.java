package test;

public class TestStringSplit {

	public static void main(String[] args)
	{
		String s = "a,b, ";
		String[] ss = s.split(",");
		System.out.println(ss[2]);
		System.out.println("done.");
		System.out.println();
		
		String b = "\"	test		\"";
		System.out.println("bb="+removeDoubleQuotationMark(b).trim());
		
		String c = "ab";
		String[] cc = c.split(",");
		System.out.println(cc.length);
		
		String sss = "33940823-00010001::";
		String[] aaa = sss.split("::");
		System.out.println();
	}
	
	public static String removeDoubleQuotationMark(String s)
	{
		if(s.startsWith("\"")&&s.endsWith("\""))
			return s.substring(1, s.length()-1);
		else
			return s;
	}
}
