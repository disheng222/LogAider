package plot.visionController;

public class DenseLayout extends LayoutController{

	private boolean rowMajor = true;
	
	public DenseLayout(int count, boolean rowMajor, float width, float height, int curLevel) {
		super(count, width, height, curLevel);
		this.rowMajor = rowMajor;
		computeRowColumn();
	}
	
	public void computeRowColumn()
	{
		if(count<4)
		{
			rowNum = 1;
			columnNum = count;
		}
		
		int s = (int)Math.sqrt(count);
		if(s*s == count)
		{
			rowNum = s;
			columnNum = s;
		}
		else
		{
			for(int i = 1;i<rowNum;i++)
			{
				int a = s - i;
				int b = (int)(count/a);
				if(a*b==count)
				{
					rowNum = a; //smaller
					columnNum = b; //bigger
					break;
				}
			}
		}
		if(!rowMajor)
		{
			int c = rowNum;
			rowNum = columnNum;
			columnNum = c;
		}
	}
	
	public boolean isRowMajor() {
		return rowMajor;
	}
	public void setRowMajor(boolean rowMajor) {
		this.rowMajor = rowMajor;
	}
	
}
