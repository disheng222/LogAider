package plot.controlElement;

import plot.visionController.BaseController;
import plot.visionController.DenseLayout;
import plot.visionController.LayoutController;
import plot.visionController.LayoutSchema;

public class MidPlane implements Record{

	private String globalID;
	private String localID;
	private Rack rack;
	private Node[] node;
	private State state = null;
	private LayoutController layout = null;
	
	public MidPlane(String globalID, int localID, Rack rack, LayoutController layout) {
		this.globalID = globalID;
		this.localID = layout.computeLocalID(localID);
		this.rack = rack;
		state = new State();
		this.layout = layout;
		int nodeNum = layout.getCount();
		if(LayoutController.maxLevel > 1)
		{
			node = new Node[nodeNum];
			float[] cellSize = layout.computeCellSize(LayoutController.fieldBoxHeight);
			float cellWidth = cellSize[0];
			float cellHeight = cellSize[1];
			//int curLevel = layout.getCurrentLevel();
			
			LayoutController nodeLayout = LayoutSchema.layoutTypes[layout.getCurrentLevel()+1].loadLayout(cellWidth, cellHeight);
			//DenseLayout nodeLayout = new DenseLayout(32, true, cellWidth, cellHeight, curLevel+1);
			
			for(int i = 0;i<node.length;i++)
			{	
				StringBuilder sb = new StringBuilder(globalID);
				String localID_ = nodeLayout.computeLocalID(i);
				sb.append("-").append(localID_);
				String gID = sb.toString();
				node[i] = new Node(gID, i, this, nodeLayout);
				MiraCluster.componentMap.put(gID, node[i]);
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
	public Rack getRack() {
		return rack;
	}
	public void setRack(Rack rack) {
		this.rack = rack;
	}
	public Node[] getNode() {
		return node;
	}
	public void setNode(Node[] node) {
		this.node = node;
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
		State[] states = new State[node.length];
		for(int i = 0;i<states.length;i++)
			states[i] = node[i].getState();
		return states;
	}
}
