package element;

public class JobStatElement implements Comparable<JobStatElement>
{
	private String ID;
	private int normalJobs = 0;
	private int errorJobs = 0;
	private double corehours = 0;
	
	public JobStatElement(String iD) {
		ID = iD;
	}

	public int getNormalJobs()
	{
		return normalJobs;
	}

	public void setNormalJobs(int normalJobs)
	{
		this.normalJobs = normalJobs;
	}
	
	public void incrementNormalJobs()
	{
		normalJobs++;
	}
	
	public void incrementErrorJobs()
	{
		errorJobs++;
	}
	
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public int getErrorJobs() {
		return errorJobs;
	}

	public void setErrorJobs(int errorJobs) {
		this.errorJobs = errorJobs;
	}
	
	public int getTotalJobs()
	{
		return normalJobs + errorJobs;
	}
	
	public double getErrorJobRatio()
	{
		return 1.0*errorJobs/getTotalJobs();
	}
	
	public double getCorehours() {
		return corehours;
	}

	public void setCorehours(double corehours) {
		this.corehours = corehours;
	}

	public int compareTo(JobStatElement other)
	{
		/*if(this.getErrorJobRatio()<other.getErrorJobRatio())
			return 1;
		else if(this.getErrorJobRatio()>other.getErrorJobRatio())
			return -1;
		else 
			return 0;*/
		if(this.getCorehours()<other.getCorehours())
			return -1;
		else if(this.getCorehours()>other.getCorehours())
			return 1;
		else
			return 0;
	}
	
	public String toString()
	{
		return ID+" "+normalJobs+" "+errorJobs+" "+getTotalJobs()+" "+getErrorJobRatio()+" "+corehours;
	}
}
