package analysis.significance;

/**
 * @author fti
 *
 */
public class MarginOfErrorController {

	public static double computeMarginOfError(double confidenceLevel, int sampleSize)
	{
	//	return erf_1(confidenceLevel)/Math.sqrt(2*sampleSize);
		if(confidenceLevel==0.90)
			return 0.82/Math.sqrt(sampleSize);
		else if(confidenceLevel == 0.95)
			return 0.98/Math.sqrt(sampleSize);
		else if(confidenceLevel == 0.99)
			return 1.29/Math.sqrt(sampleSize);
		else
			return -1;
	}
	
	/**
	 * Error is too huge!
	 * @deprecated
	 * @param z
	 * @return
	 */
	private static double erf_1(double z)
	{
		double a1 = Math.sqrt(Math.PI)/2;
		double z3 = z*z*z;
		double z5 = z3*z*z;
		double z7 = z5*z*z;
		double z9 = z7*z*z;
		double z11 = z9*z*z;
		double a2 = z + 
				Math.PI/12*z3 +
				7*Math.PI*Math.PI/480*z5+
				127*Math.PI*Math.PI*Math.PI/40320*z7+
				4369*Math.PI*Math.PI*Math.PI*Math.PI/5806080*z9+
				34807*Math.PI*Math.PI*Math.PI*Math.PI*Math.PI/182476800*z11;
		return a1*a2;
	}
	
	public static void main(String[] args)
	{
		double confidenceLevel = 0.95;
		int sampleSize = 1;
		
		double result = computeMarginOfError(confidenceLevel,sampleSize);
		System.out.println("result="+result);
	}
}
