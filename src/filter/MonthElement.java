package filter;

public class MonthElement implements Comparable<MonthElement>
{
	public MonthElement(int month, String severity, String category,
			String component, int num) {
		super();
		this.month = month;
		this.severity = severity;
		this.category = category;
		this.component = component;
		this.num = num;
	}

	private int month = 0;
	private String severity;
	private String category;
	private String component;
	private int num;
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getComponent() {
		return component;
	}
	public void setComponent(String component) {
		this.component = component;
	}
	public int compareTo(MonthElement other)
	{
		if(this.month<other.month)
			return -1;
		else if(this.month>other.month)
			return 1;
		else 
			return 0;
	}
	public String toString()
	{
		return month+" "+severity+" "+category+" "+component+" "+num;
	}
}