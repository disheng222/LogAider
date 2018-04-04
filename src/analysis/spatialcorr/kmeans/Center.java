package analysis.spatialcorr.kmeans;

import analysis.spatialcorr.ComputeProbabilityAcrossMidplanes;
import analysis.spatialcorr.MidplaneTorusElem;

public class Center implements Point{

	private float i;
	private float j;
	private float k;
	private float t;
	private KMeansSet cs;
	
	public Center(float i, float j, float k, float t, KMeansSet cs) {
		this.i = i;
		this.j = j;
		this.k = k;
		this.t = t;
		this.cs = cs;
	}
	
	public Center(MidplaneTorusElem mte, KMeansSet cs)
	{
		this(mte.getI(), mte.getJ(), mte.getK(), mte.getT(), cs);
	}
	
	public float getI() {
		return i;
	}
	public void setI(float i) {
		this.i = i;
	}
	public float getJ() {
		return j;
	}
	public void setJ(float j) {
		this.j = j;
	}
	public float getK() {
		return k;
	}
	public void setK(float k) {
		this.k = k;
	}
	public float getT() {
		return t;
	}
	public void setT(float t) {
		this.t = t;
	}
	
	public KMeansSet getKMeansSet()
	{
		return cs;
	}
	
	public float getDistance(Point point)
	{
		int i_size = ComputeProbabilityAcrossMidplanes.i_size;
		int j_size = ComputeProbabilityAcrossMidplanes.j_size;
		int k_size = ComputeProbabilityAcrossMidplanes.k_size;
		int t_size = ComputeProbabilityAcrossMidplanes.t_size;
		
		float dis = Math.abs(this.i - point.getI());
		float dis_ = i_size - dis;
		float dis_i = Math.min(dis, dis_);
		
		dis = Math.abs(this.j - point.getJ());
		dis_ = j_size - dis;
		float dis_j = Math.min(dis, dis_);
		
		dis = Math.abs(this.k - point.getK());
		dis_ = k_size - dis;
		float dis_k = Math.min(dis, dis_);
		
		dis = Math.abs(this.t - point.getT());
		dis_ = t_size - dis;
		float dis_t = Math.min(dis, dis_);
		
		return dis_i+dis_j+dis_k+dis_t;
	}	
	
	/*public boolean equals(Object obj)
	{
		Center other = (Center)obj;
		if(i==other.i&&j==other.j&&k==other.k&&t==other.t)
			return true;
		else
			return false;
	}*/
}
