package analysis.inbetween.element;

public class ItemCombination implements Comparable<ItemCombination>{

	private boolean countSorting = true;
	private String key;
	private int count = 1;
	
	public ItemCombination(String key) {
		this.key = key;
	}
	
	public ItemCombination(String key, int count, boolean countSorting)
	{
		this.key = key;
		this.count = count;
		this.countSorting = countSorting;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public int compareTo(ItemCombination ic)
	{
		if(countSorting)
		{
			if(this.count < ic.count)
				return 1;
			else if(this.count > ic.count)
				return -1;
			else 
				return 0;			
		}
		else
		{
			return this.key.compareTo(ic.key);
		}
	}
	
	public String toString()
	{
		return key+":"+count;
	}
}
