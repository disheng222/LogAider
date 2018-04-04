package analysis.significance;

public class ChiSquaredTestResult {

	private float alpha;
	private boolean correlate = false;
	float chiSquareStatValue;
	float chiSquareCritPoint;
	public ChiSquaredTestResult(boolean correlate, float chiSquareStatValue,
			float alpha, float chiSquareCritPoint) {
		this.correlate = correlate;
		this.chiSquareStatValue = chiSquareStatValue;
		this.alpha = alpha;
		this.chiSquareCritPoint = chiSquareCritPoint;
	}
	public boolean isCorrelate() {
		return correlate;
	}
	public void setCorrelate(boolean correlate) {
		this.correlate = correlate;
	}
	public float getChiSquareStatValue() {
		return chiSquareStatValue;
	}
	public void setChiSquareStatValue(float chiSquareStatValue) {
		this.chiSquareStatValue = chiSquareStatValue;
	}
	public float getChiSquareCritPoint() {
		return chiSquareCritPoint;
	}
	public void setChiSquareCritPoint(float chiSquareCritPoint) {
		this.chiSquareCritPoint = chiSquareCritPoint;
	}
	
	public String toString()
	{
		return correlate+" "+chiSquareStatValue+" "+alpha+" "+chiSquareCritPoint;
	}
	
}
