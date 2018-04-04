package analysis.temporalcorr.fields;

import analysis.temporalcorr.Similarity;
import filter.RecordElement;

public class StartTimeField implements Similarity{
	private int fieldIndex;
	private float weight;
	public static float delay = 1500;
	
	public StartTimeField(Integer fieldIndex, Float weight) {
		this.fieldIndex = fieldIndex;
		this.weight = weight;
	}
	public int getFieldIndex() {
		return fieldIndex;
	}
	public void setFieldIndex(int fieldIndex) {
		this.fieldIndex = fieldIndex;
	}
	public float getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
	public float getSimilarity(String value1, String value2)
	{
		float similarity = 0;
		String time1String = value1.replace(";", " ");
		String time2String = value2.replace(";", " ");
		double time1 = RecordElement.computeDoubleTimeinSeconds(time1String);
		double time2 = RecordElement.computeDoubleTimeinSeconds(time2String);
		double diff = Math.abs(time1-time2);
		similarity = (float)(delay - diff)/delay;
		if(similarity<0)
			return 0;
		return similarity;
	}
}
