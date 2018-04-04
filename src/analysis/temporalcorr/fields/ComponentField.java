package analysis.temporalcorr.fields;

import analysis.temporalcorr.Similarity;

public class ComponentField implements Similarity{

	private int fieldIndex;
	private float weight;
	
	public ComponentField(Integer fieldIndex, Float weight) {
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
		if(value1.equals(value2))
			similarity = 1f;
		return similarity;
	}
}
