package analysis.spatialcorr;

public class RackTorusElem {

	public static int i_size = 2;
	public static int j_size = 3;
	public static int k_size = 8;	
	
	private int id;
	private int x; //x index
	private int y; //y index
	private int i;
	private int j;
	private int k;
	
	public RackTorusElem(int id, int i, int j, int k) {
		this.id = id;
		this.i = i;
		this.j = j;
		this.k = k;
		this.x = j;
		this.y = 8*i+k;
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
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public int getJ() {
		return j;
	}
	public void setJ(int j) {
		this.j = j;
	}
	public int getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}
	public int getDistance(RackTorusElem rack)
	{
		int dis = Math.abs(this.i - rack.getI());
		int dis_ = i_size - dis;
		int dis_i = Math.min(dis, dis_);
		dis = Math.abs(this.j - rack.getJ());
		dis_ = j_size - dis;
		int dis_j = Math.min(dis, dis_);
		dis = Math.abs(this.k - rack.getK());
		dis_ = k_size - dis;
		int dis_k = Math.min(dis, dis_);
		return dis_i+dis_j+dis_k;
	}
	
	public String getRackName()
	{
		return "R"+x+Integer.toHexString(y).toUpperCase();
	}
}
