package plot.controlElement;

import plot.visionController.DenseLayout;
import plot.visionController.LayoutController;

public class ComputeCard implements Record{
	private String globalID;
	private String localID;
	private Node node;
	//private Core[] core;
	private State state = null;
	private LayoutController layout = null;
	
	public ComputeCard(String globalID, int localID, Node node, LayoutController layout) {
		this.globalID = globalID;
		this.localID = layout.computeLocalID(localID);
		this.node = node;
		state = new State();
		this.layout = layout;
//		if(PlotMiraGraph.maxLevel > 3)
//		{
//			//core = new Core[16];
//			layout = new DenseLayout(16,layoutType);
//			for(int i = 0;i<core.length;i++)
//			{
//				StringBuilder sb = new StringBuilder(globalID);
//				sb.append(String.valueOf(i));
//				core[i] = new Core(sb.toString(), i, this);
//			}
//		}
		this.state = new State();
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
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
//	public Core[] getCore() {
//		return core;
//	}
//	public void setCore(Core[] core) {
//		this.core = core;
//	}

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

//	public boolean isLayoutType() {
//		return layoutType;
//	}
//	public void setLayoutType(boolean layoutType) {
//		this.layoutType = layoutType;
//	}
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
//	public State[] getStates()
//	{
//		State[] states = new State[core.length];
//		for(int i = 0;i<states.length;i++)
//			states[i] = core[i].getState();
//		return states;
//	}
}
