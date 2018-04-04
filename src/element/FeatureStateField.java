package element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import analysis.significance.MarginOfErrorController;

public class FeatureStateField {
	private String featureFieldName;
	private int featureFieldIndex;
	private String stateFieldName;
	private int stateFieldIndex;
	
	private List<String> featureValueList;
	private List<String> stateValueList;
	
	private int[][] count;
	public FeatureStateField(String featureFieldName, String stateFieldName,
			List<String> featureValueList, List<String> stateValueList) {
		this.featureFieldName = featureFieldName;
		this.stateFieldName = stateFieldName;
		count = new int[featureValueList.size()][stateValueList.size()];
		this.featureValueList = featureValueList;
		this.stateValueList = stateValueList;
	}
	public String getFeatureFieldName() {
		return featureFieldName;
	}
	public void setFeatureFieldName(String featureFieldName) {
		this.featureFieldName = featureFieldName;
	}
	public String getStateFieldName() {
		return stateFieldName;
	}
	public void setStateFieldName(String stateFieldName) {
		this.stateFieldName = stateFieldName;
	}
	public int getFeatureFieldIndex() {
		return featureFieldIndex;
	}
	public void setFeatureFieldIndex(int featureFieldIndex) {
		this.featureFieldIndex = featureFieldIndex;
	}
	public int getStateFieldIndex() {
		return stateFieldIndex;
	}
	public void setStateFieldIndex(int stateFieldIndex) {
		this.stateFieldIndex = stateFieldIndex;
	}
	public int[][] getCount() {
		return count;
	}
	public void setCount(int[][] count) {
		this.count = count;
	}
	
	public String getFieldNames()
	{
		return featureFieldName+"-"+stateFieldName;
	}
	
	public List<String> toPlainText()
	{
		List<String> lineList = new ArrayList<String>();
		
		StringBuilder fieldSB = new StringBuilder("#");
		Iterator<String> iter = stateValueList.iterator();
		while(iter.hasNext())
		{
			String state = iter.next();
			fieldSB.append(" ");
			fieldSB.append(state);
		}
		
		lineList.add(fieldSB.toString());
		
		for(int i = 0;i<featureValueList.size();i++)
		{
			String featureValueName = featureValueList.get(i);
			StringBuilder sb = new StringBuilder(featureValueName);
			for(int j = 0;j<count[0].length;j++)
			{
				sb.append(" ");
				sb.append(String.valueOf(count[i][j]));
			}
			lineList.add(sb.toString());
		}
		return lineList;
	}
	
	public List<String> toPlainText_probMarginOfErr(double confidenceLevel)
	{
		List<String> lineList = new ArrayList<String>();
		
		StringBuilder fieldSB = new StringBuilder("#");
		Iterator<String> iter = stateValueList.iterator();
		while(iter.hasNext())
		{
			String state = iter.next();
			fieldSB.append(" ");
			fieldSB.append(state);
		}
		
		lineList.add(fieldSB.toString());
		
		for(int i = 0;i<featureValueList.size();i++)
		{
			String featureValueName = featureValueList.get(i);
			StringBuilder sb = new StringBuilder(featureValueName);
			float sum = 0;
			for(int j = 0;j<count[0].length;j++)
				sum += count[i][j];
			float marginOfErr = (float)MarginOfErrorController.computeMarginOfError(confidenceLevel, (int)sum);
			sb.append("[").append((int)sum).append(",").append(marginOfErr).append("]");
			for(int j = 0;j<count[0].length;j++)
			{
				sb.append(" ");
				float prob = ((float)count[i][j])/sum*100; //percentage
				sb.append(String.valueOf(prob));
			}
			lineList.add(sb.toString());
		}
		return lineList;
	}
	
	public List<String> toPlainText_prob()
	{
		List<String> lineList = new ArrayList<String>();
		
		StringBuilder fieldSB = new StringBuilder("#");
		Iterator<String> iter = stateValueList.iterator();
		while(iter.hasNext())
		{
			String state = iter.next();
			fieldSB.append(" ");
			fieldSB.append(state);
		}
		
		lineList.add(fieldSB.toString());
		
		for(int i = 0;i<featureValueList.size();i++)
		{
			String featureValueName = featureValueList.get(i);
			StringBuilder sb = new StringBuilder(featureValueName);
			float sum = 0;
			for(int j = 0;j<count[0].length;j++)
				sum += count[i][j];
			for(int j = 0;j<count[0].length;j++)
			{
				sb.append(" ");
				float prob = ((float)count[i][j])/sum*100; //percentage
				sb.append(String.valueOf(prob));
			}
			lineList.add(sb.toString());
		}
		return lineList;
	}
}
