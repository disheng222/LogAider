/**
 * @author Sheng Di
 * @class ValueCountItem
 * @description  
 */

package analysis.inbetween.element;

public class ValueCountItem implements Comparable<ValueCountItem>
{
	private String value;
	private int count;
	
	public ValueCountItem(String value, int count) {
		this.value = value;
		this.count = count;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(value).append(",").append(count);
		return sb.toString();
	}
	public int compareTo(ValueCountItem other)
	{
		if(count < other.count)
			return 1;
		else if(count > other.count)
			return -1;
		else
			return 0;
	}
}