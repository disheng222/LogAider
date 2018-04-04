package analysis.temporalcorr;

public interface Similarity {

	public int getFieldIndex();
	public float getWeight();
	public float getSimilarity(String value1, String value2);
}
