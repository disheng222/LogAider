package element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import analysis.significance.MarginOfErrorController;

public class PostierProbabilityItem implements Comparable<PostierProbabilityItem>{
	private String target;
	private List<String> srcItemList = new ArrayList<String>();
	private float probValue = -1;
	private int sampleSize = 0;
	private float marginOfErr = 0;
	private float lowerProb = 0;
	
	public PostierProbabilityItem(String target, float probValue, int sampleSize, double confidenceLevel) {
		this.target = target;
		this.probValue = probValue;
		this.sampleSize = sampleSize;
		this.marginOfErr = (float)MarginOfErrorController.computeMarginOfError(confidenceLevel, sampleSize);
		lowerProb = probValue - marginOfErr;
	}
	
	public void addSrcItem(String item)
	{
		srcItemList.add(item);
	}
	
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public List<String> getSrcItemList() {
		return srcItemList;
	}
	public void setSrcItemList(List<String> srcItemList) {
		this.srcItemList = srcItemList;
	}
	public float getProbValue() {
		return probValue;
	}
	public void setProbValue(float probValue) {
		this.probValue = probValue;
	}
	
	public int compareTo(PostierProbabilityItem ppi)
	{
		/*if(lowerProb>ppi.lowerProb)
			return -1;
		else if(lowerProb<ppi.lowerProb)
			return 1;
		else
			return 0;*/
		if(probValue>ppi.probValue)
			return -1;
		else if(probValue<ppi.probValue)
			return 1;
		else
			return 0;
	}
	
	public String toString()
	{
		if(probValue<0)
		{
			return target;
		}
		else
		{
			StringBuilder sb = new StringBuilder();
			if(!srcItemList.isEmpty())
			{
				Iterator<String> iter = srcItemList.iterator();
				sb.append(iter.next());
				while(iter.hasNext())
					sb.append(",").append(iter.next());
			}

			sb.append(" ==> ").append(target).append(" : ").append(probValue).append(" (").append(sampleSize).append(",").append(marginOfErr).append(")");
			return sb.toString();			
		}

	}
}
