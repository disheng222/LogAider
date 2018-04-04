package plot.controlElement;

import plot.visionController.DenseLayout;
import plot.visionController.LayoutController;

public class Node implements Record{
	private String globalID;
	private String localID;
	private MidPlane mp;
	private ComputeCard[] cc;
	private State state = null;
	private LayoutController layout = null;
	
	public Node(String globalID, int localID, MidPlane mp, LayoutController layout) {
		this.globalID = globalID;
		this.localID = layout.computeLocalID(localID);
		this.mp = mp;
		this.state = new State();
		this.layout = layout;
		if(LayoutController.maxLevel > 2)
		{
			cc = new ComputeCard[32];
			float[] cellSize = layout.computeCellSize(LayoutController.fieldBoxHeight);
			float cellWidth = cellSize[0];
			float cellHeight = cellSize[1];
			int curLevel = layout.getCurrentLevel();
			DenseLayout ccLayout = new DenseLayout(16, true, cellWidth, cellHeight, curLevel+1);
			
			for(int i = 0;i<cc.length;i++)
			{
				StringBuilder sb = new StringBuilder(globalID);
				String localID_ = ccLayout.computeLocalID(i);
				sb.append("-").append(localID_);
				String gID = sb.toString();
				cc[i] = new ComputeCard(gID, i, this, ccLayout);
				MiraCluster.componentMap.put(gID, cc[i]);
			}
		}
	}
	public String getGlobalID() {
		return globalID;
	}
	public void setGlobalID(String globalID) {
		this.globalID = globalID;
	}
	public String getLocalID() {
		return localID;
	}
	public void setLocalID(String localID) {
		this.localID = localID;
	}
	public MidPlane getMp() {
		return mp;
	}
	public void setMp(MidPlane mp) {
		this.mp = mp;
	}
	public ComputeCard[] getCc() {
		return cc;
	}
	public void setCc(ComputeCard[] cc) {
		this.cc = cc;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public LayoutController getLayout() {
		return layout;
	}
	public void setLayout(DenseLayout layout) {
		this.layout = layout;
	}
	public void setFatalErrRate(int value)
	{
		state.setFatalRate(value);
	}
	public int getFatalErrRate()
	{
		return state.getFatalRate();
	}	
	public void setWarnErrRate(int value)
	{
		state.setWarnRate(value);
	}
	public int getWarnErrRate()
	{
		return state.getWarnRate();
	}
	public State[] getStates()
	{
		State[] states = new State[cc.length];
		for(int i = 0;i<states.length;i++)
			states[i] = cc[i].getState();
		return states;
	}
	
}
