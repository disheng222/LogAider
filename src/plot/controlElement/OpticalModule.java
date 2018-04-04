package plot.controlElement;

public class OpticalModule implements Record {
	private String globalID;
	private String localID;
	private Node node;
	private State state = null;
	
	public OpticalModule(String globalID, int localID, Node node) {
		this.globalID = globalID;
		this.localID = "O"+localID;
		this.node = node;
		state = new State();
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
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
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
}
