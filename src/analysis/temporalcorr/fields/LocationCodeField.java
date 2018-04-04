package analysis.temporalcorr.fields;

import analysis.temporalcorr.Similarity;

public class LocationCodeField implements Similarity{
	private int fieldIndex;
	private float weight;
	public LocationCodeField(Integer fieldIndex, Float weight) {
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
			if(value1.equals(""))
				return 0.5f;
			else
				return 1f;
		}
		if(value1.equals("")||value2.equals(""))
			return 0f;
		String[] s1 = value1.split("-");
		String[] s2 = value2.split("-");
		int size = Math.min(s1.length,s2.length);
		if(s1[0].equals(s2[0]))
			similarity += 0.8f;
		else
			return 0f;
		if(size==1)
			return similarity;
		if(s1[1].equals(s2[1]))
			similarity += 0.1f;
		else 
			return similarity;
		if(size==2)
			return similarity;
		if(s1[2].equals(s2[2]))
			similarity += 0.1f;
		return similarity;
	}
}
