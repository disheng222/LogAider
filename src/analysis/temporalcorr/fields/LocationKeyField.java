package analysis.temporalcorr.fields;

import analysis.temporalcorr.Similarity;

public class LocationKeyField implements Similarity{
	private int fieldIndex;
	private float weight;
	public LocationKeyField(Integer fieldIndex, Float weight) {
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
		{
			if(value1.equals("NULL"))
				return 0.5f;
			else
				return 1f;
		}
		char[] v1 = value1.toCharArray();
		char[] v2 = value2.toCharArray();
		int size = Math.min(v1.length, v2.length);
		for(int i = 0;i<size;i++)
		{
			if(v1[i]==v2[i])
				similarity+=0.25f;
		}
		if(similarity>1)
			similarity=1;
		return similarity;
	}

}
