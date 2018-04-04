package analysis.Job;

public class UserFailureElement {

	private String userID;
	public static int userid = 0;
	private int totalEventCount;
	private float normalEventCount;
	private float wlLengthBrokenCount;
	public UserFailureElement(String userID, int totalEventCount,
			float normalEventCount, float wlLengthBrokenCount) {
		this.userID = userID;
		this.totalEventCount = totalEventCount;
		this.normalEventCount = normalEventCount;
		this.wlLengthBrokenCount = wlLengthBrokenCount;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public int getTotalEventCount() {
		return totalEventCount;
	}
	public void setTotalEventCount(int totalEventCount) {
		this.totalEventCount = totalEventCount;
	}
	public float getNormalEventCount() {
		return normalEventCount;
	}
	public void setNormalEventCount(float normalEventCount) {
		this.normalEventCount = normalEventCount;
	}
	public float getWlLengthBrokenCount() {
		return wlLengthBrokenCount;
	}
	public void setWlLengthBrokenCount(float wlLengthBrokenCount) {
		this.wlLengthBrokenCount = wlLengthBrokenCount;
	}
	
	public String toString()
	{
		//userid totalEventCount failureRate wlFailRate normalEventCount wlLengthBrokenCount
		StringBuilder sb = new StringBuilder();
		sb.append(++userid).append(" ");
		sb.append(totalEventCount).append(" ");
		float totalFailNum = (float)(totalEventCount - normalEventCount);
		float failureRate = totalFailNum/((float)totalEventCount);
		sb.append(failureRate).append(" ");
		if(totalFailNum >= 0.9) //i.e., >=1
		{
			float wlFailRate = ((float)wlLengthBrokenCount)/totalFailNum;
			sb.append(wlFailRate).append(" ");
		}
		else
			sb.append("- ");
		
		sb.append(normalEventCount).append(" ").append(wlLengthBrokenCount);
		return sb.toString();
		
	}
}
