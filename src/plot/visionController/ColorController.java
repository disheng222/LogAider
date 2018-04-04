package plot.visionController;

public class ColorController {

	public static String[] generateGradientColors(String startCode, String endCode, int segNum)
	{
		String[] result = new String[segNum];
		int start = Integer.parseInt(startCode, 16);
		int end = Integer.parseInt(endCode, 16);
		int seg = 65536/segNum;
		int k = 0;
		for(int i = start; i>=end && k<result.length; i-=seg)
		{
			StringBuilder sb = new StringBuilder("'#");
			sb.append(Integer.toHexString(i)).append("'");
			result[k++] = sb.toString();
		}
		return result;
	}
	
	public static void main(String[] args)
	{
		String[] result = generateGradientColors("FFFF00", "FF0000", 64);
		for(int i = 0;i<64;i++)
			System.out.println(result[i]);
	}
}
