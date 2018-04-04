package analysis.spatialcorr.kmeans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import analysis.spatialcorr.ComputeProbabilityAcrossMidplanes;
import analysis.spatialcorr.MidplaneTorusElem;

public class KMeansSet {
	private Center center;
	public List<MidplaneTorusElem> midplaneList = new ArrayList<MidplaneTorusElem>();
	
	private int wcsd = -1;
	
	public KMeansSet(MidplaneTorusElem mte) 
	{
		center = new Center(mte.getI(), mte.getJ(), mte.getK(), mte.getT(), this);
		midplaneList.add(mte);
	}

	public void addAll(KMeansSet otherSet)
	{
		this.midplaneList.addAll(otherSet.midplaneList);
		
		Iterator<MidplaneTorusElem> iter = otherSet.midplaneList.iterator();
		while(iter.hasNext())
		{
			MidplaneTorusElem mte = iter.next();
			mte.setKset(this);
		}
		
		otherSet.midplaneList.clear();
		otherSet.center = null;
	}
	
	public boolean updateCenter()
	{
		int totalCount = 0;
		float center_i=0,center_j=0,center_k=0,center_t=0;
		Iterator<MidplaneTorusElem> iter = midplaneList.iterator();
		while(iter.hasNext())
		{
			MidplaneTorusElem mte = iter.next();
			float i = mte.getI();
			float j = mte.getJ();
			float k = mte.getK();
			float t = mte.getT();
			center_i += i*mte.getCount();
			center_j += j*mte.getCount();
			center_k += k*mte.getCount();
			center_t += t*mte.getCount();
			totalCount+=mte.getCount();
		}
		center_i/=totalCount;
		center_j/=totalCount;
		center_k/=totalCount;
		center_t/=totalCount;
		
		if(this.center==null)
		{
			this.center = new Center(center_i,center_j,center_k,center_t,this);
			return true;
		}
		else
		{
			float old_i = center.getI();
			float old_j = center.getJ();
			float old_k = center.getK();
			float old_t = center.getT();

			if(old_i==center_i&&old_j==center_j&&old_k==center_k&&old_t==center_t)
				return false;
			else 
			{
				this.center.setI(center_i);
				this.center.setJ(center_j);
				this.center.setK(center_k);
				this.center.setT(center_t);
				return true;
			}
		}
	}
	
	public float getWCSD()
	{
		float sum_distance = 0;
		int i_size = ComputeProbabilityAcrossMidplanes.i_size;
		int j_size = ComputeProbabilityAcrossMidplanes.j_size;
		int k_size = ComputeProbabilityAcrossMidplanes.k_size;
		int t_size = ComputeProbabilityAcrossMidplanes.t_size;
		
		if(wcsd==-1)
		{
			Iterator<MidplaneTorusElem> iter = midplaneList.iterator();
			while(iter.hasNext())
			{
				MidplaneTorusElem mte = iter.next();
				float i = mte.getI();
				float j = mte.getJ();
				float k = mte.getK();
				float t = mte.getT();
				float dis = Math.abs(center.getI() - i)*mte.getCount();
				float dis_ = i_size*mte.getCount() - dis;
				float dis_i = Math.min(dis, dis_);
				
				dis = Math.abs(center.getJ() - j)*mte.getCount();
				dis_ = j_size*mte.getCount() - dis;
				float dis_j = Math.min(dis, dis_);
				
				dis = Math.abs(center.getK() - k)*mte.getCount();
				dis_ = k_size*mte.getCount() - dis;
				float dis_k = Math.min(dis, dis_);
				
				dis = Math.abs(center.getT() - t)*mte.getCount();
				dis_ = t_size*mte.getCount() - dis;
				float dis_t = Math.min(dis, dis_);
				
				sum_distance+=(dis_i+dis_j+dis_k+dis_t);
			}
			return sum_distance;
		}
		else
			return wcsd;
	}

	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}
	
	public void add(MidplaneTorusElem mte)
	{
		midplaneList.add(mte);
	}
	
	public void remove(MidplaneTorusElem mte)
	{
		midplaneList.remove(mte);
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(").append(center.getI()).append(",").append(center.getJ()).append(",").append(center.getK()).append(",").append(center.getT()).append(")");
		sb.append(":");
		Iterator<MidplaneTorusElem> iter = midplaneList.iterator();
		while(iter.hasNext())
		{
			MidplaneTorusElem mte = iter.next();
			sb.append(mte.toString()).append(" ");
		}
		return sb.toString().trim();
	}
}
