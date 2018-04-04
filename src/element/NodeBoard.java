package element;

public class NodeBoard {

	private int i;
	private int j;
	private boolean select = false;
	private int count;
	public int getI() {
		return i;
	}
	public boolean isSelect() {
		return select;
	}
	public void setSelect(boolean select) {
		this.select = select;
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
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public NodeBoard(int i, int j, int count) {
		this.i = i;
		this.j = j;
		this.count = count;
	}
	
}
