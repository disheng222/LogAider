package analysis.spatialcorr;

import analysis.spatialcorr.kmeans.KMeansSet;
import analysis.spatialcorr.kmeans.Point;

public class MidplaneTorusElem implements Point{
	
	public static boolean equalTypeAllowDuplication = false;
	
	private int id;
	private int x; //x index
	private int y; //y index
	private int z; //midplane id : 0 or 1
	private int i;
	private int j;
	private int k;
	private int t;
	
	private KMeansSet kset = null;
	private int count = 1;
	
	public MidplaneTorusElem(int id, int x, int y, int z, int i, int j, int k,
			int t) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.i = i;
		this.j = j;
		this.k = k;
		this.t = t;
	}
	public MidplaneTorusElem(int id, int x, int y, int z) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.i = y/8;
		this.j = x;
		int y_mod8 = y % 8;
		switch(y_mod8/2)
		{
		case 0:
			this.k = 0;
			break;
		case 1:
			this.k = 3;
			break;
		case 2:
			this.k = 1;
			break;
		case 3:
			this.k = 2;
			break;
		}
		if(y_mod8%2==0 && z==0)
			t = 0;
		else if(y_mod8%2==0 && z==1)
			t = 1;
		else if(y_mod8%2==1 && z==1)
			t = 2;
		else if(y_mod8%2==1 && z==0)
			t = 3;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public float getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public float getJ() {
		return j;
	}
	public void setJ(int j) {
		this.j = j;
	}
	public float getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}	
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
		this.z = z;
	}
	public float getT() {
		return t;
	}
	public void setT(int t) {
		this.t = t;
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
	
	public String getMidplaneName()
	{
		return "R"+x+Integer.toHexString(y).toUpperCase()+"-M"+z;
	}
	public KMeansSet getKset() {
		return kset;
	}
	public void setKset(KMeansSet kset) 
	{
		this.kset = kset;
	}
	public String toString()
	{
		return getMidplaneName();
	}
	public boolean equals(Object other)
	{
		MidplaneTorusElem o = (MidplaneTorusElem)other;
		if(id == o.id)
			return true;
		else
			return false;
	}
	public MidplaneTorusElem clone()
	{
		return new MidplaneTorusElem(id,x,y,z,i,j,k,t);
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}

