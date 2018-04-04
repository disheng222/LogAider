package plot.controlElement;

import plot.visionController.DenseLayout;
import plot.visionController.LayoutController;
import plot.visionController.LayoutSchema;

public class Rack implements Record {
	private String globalID;
	private String localID;
	private MidPlane[] mp;
	private State state;
	private LayoutController layout = null;
	
	public Rack(String globalID, String localID, int rackID, LayoutController layout) {
		this.globalID = globalID;
		this.localID = localID;
		
		state = new State();
		int mpNum = layout.getCount();
		this.layout = layout;
		
		if(LayoutController.maxLevel > 0)
		{
			mp = new MidPlane[mpNum];
			float[] cellSize = layout.computeCellSize(LayoutController.fieldBoxHeight);
			float cellWidth = cellSize[0];
			float cellHeight = cellSize[1];
			//int curLevel = layout.getCurrentLevel();
		
			LayoutController mpLayout = LayoutSchema.layoutTypes[layout.getCurrentLevel()+1].loadLayout(cellWidth, cellHeight);
			//DenseLayout mpLayout = new DenseLayout(16, true, cellWidth, cellHeight, curLevel+1);
			
			for(int i = 0;i<mpNum;i++)
			{	
				StringBuilder sb = new StringBuilder(globalID);
				String localID_ = mpLayout.computeLocalID(i);
				sb.append("-").append(localID_);
				String gID = sb.toString();
				mp[i] = new MidPlane(gID, i, this, mpLayout);
				MiraCluster.componentMap.put(gID, mp[i]);
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

	public void setMp(MidPlane[] mp) {
		this.mp = mp;
	}

	public MidPlane[] getMp() {
		return mp;
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
		State[] states = new State[mp.length];
		for(int i = 0;i<states.length;i++)
			states[i] = mp[i].getState();
		return states;
	}
}
