package util;

import java.util.ArrayList;
import java.util.Random;

public class RdGenerator {
	public static long RANDOM_SEED = 123456789l;
	public final static RdGenerator RAN_SeedGen = new RdGenerator(RANDOM_SEED);
	
	private Random r;
	public RdGenerator(long seed) {
		r = new Random(seed);
	}

	public int generate_Int(int start, int end)
	{
		int a = (int)(start+(end-start+1)*r.nextFloat());
		if(a>end)
			a = end;
		return a;
	}
	
	public long generate_Long(long start, long end)
	{
		return (long)(start+(end-start+1)*r.nextDouble());
	}
	
	public float generate_Float(float start, float end)
	{
		return (float)(start+(end-start)*r.nextFloat());
	}

	public double generate_Double(double start, double end)
	{
		return (start+(end-start)*r.nextDouble());
	}
	
	public int[] generate_Int(int start, int rangeSize, int number)
	{
		if(rangeSize==0)
			return null;
		if(number>rangeSize)
			number = rangeSize;
		int[] result = new int[number];
		if(number>rangeSize)
		{			
			for(int i = 0;i<number;i++)
			{
				result[i] = generate_Int(start,rangeSize-1);
			}
		}
		else
		{
			int size = rangeSize;
			ArrayList<Integer> list = new ArrayList<Integer>(size);
			for(int i = start;i<=rangeSize;i++)
			{
				list.add(new Integer(i));
			}
			for(int j = 0;j<number;j++)
			{
				result[j] = list.remove(generate_Int(0,size-1));
				size--;
			}
		}		
		return result;
	}
	
	public static void main(String[] args)
	{
		RdGenerator rGen = new RdGenerator(1234567891);
		int[] a = rGen.generate_Int(0,10,6);
		for(int i = 0;i<a.length;i++)
		{
			System.out.print(a[i]+",");
		}
	}
}
