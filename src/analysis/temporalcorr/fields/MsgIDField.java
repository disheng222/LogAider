package analysis.temporalcorr.fields;

import analysis.temporalcorr.Similarity;

public class MsgIDField implements Similarity {
	private int fieldIndex;
	private float weight;
	public MsgIDField(Integer fieldIndex, Float weight) {
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
		char[] a = value1.toCharArray();
		char[] b = value2.toCharArray();
		if(a[0]==b[0]&&a[1]==b[1]&&a[2]==b[2]&&a[3]==b[3])
			similarity+=0.5f;
		if(a[4]==b[4]&&a[5]==b[5]&&a[6]==b[6]&&a[7]==b[7])
			similarity+=0.5f;
		return similarity;
	}
	
}
