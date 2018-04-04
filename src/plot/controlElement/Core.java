package plot.controlElement;

/**
 * @deprecated
 * @author fti
 *
 */
public class Core{
	private String globalID;
	private String localID;
	private ComputeCard cc;
	private State state = null;
	
	public Core(String globalID, int localID, ComputeCard cc) {
		this.globalID = globalID;
		this.localID = "C"+localID;
		this.cc = cc;
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
	public ComputeCard getCc() {
		return cc;
	}
	public void setCc(ComputeCard cc) {
		this.cc = cc;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
}
