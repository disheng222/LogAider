package plot.visionController;

public class StaticLayout extends LayoutController {

	public StaticLayout(int count, 
			int rowNum, int columnNum,
			float width, float height, int curLevel)
	{
		super(count, width, height, curLevel);
		super.rowNum = rowNum;
		super.columnNum = columnNum;
	}
}
