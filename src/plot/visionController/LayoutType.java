package plot.visionController;

/**
 * This class is only used for loading layoutschema from the schema file...
 * @author fti
 *
 */
public class LayoutType {

	private int level = 0;
	private boolean isCustomized = false;
	private int count = 0;
	//row and column here are only valid when isCustomized==true
	private int row = 0;
	private int column = 0;
	//The mark "rowMajor" is only valid when isCustomized == false 
	private boolean rowMajor = false;
	
	private String titleRepresentBase;//hex or decimal
	private String titleRowBase;
	private String titleColumnBase;
	private int offset = 0;
	
	private String fullName = "";
	private String nickname = "";
	
	public LayoutType(int level){
		this.level = level;
	}
	
	public LayoutType(boolean isCustomized, int row, int column,
			boolean rowMajor, String titleRepresentBase, String nickname) {
		this.isCustomized = isCustomized;
		this.row = row;
		this.column = column;
		this.rowMajor = rowMajor;
		this.titleRepresentBase = titleRepresentBase;
		this.nickname = nickname;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isCustomized() {
		return isCustomized;
	}

	public void setCustomized(boolean isCustomized) {
		this.isCustomized = isCustomized;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public boolean isRowMajor() {
		return rowMajor;
	}

	public void setRowMajor(boolean rowMajor) {
		this.rowMajor = rowMajor;
	}

	public String getTitleRepresentBase() {
		return titleRepresentBase;
	}

	public void setTitleRepresentBase(String titleRepresentBase) {
		this.titleRepresentBase = titleRepresentBase;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public LayoutController loadLayout(float width, float height)
	{
		LayoutController resultLayout = null;
		if(isCustomized())
		{
			int row = getRow();
			int column = getColumn();
			resultLayout = new StaticLayout(count, row, column, width, height, 0); //this layout is for midplane
		}
		else
		{
			boolean rowMajor = isRowMajor();
			resultLayout = new DenseLayout(count, rowMajor, width, height, 0);
		}
		resultLayout.setBase(titleRepresentBase);
		resultLayout.setTitleRowBase(titleRowBase);
		resultLayout.setTitleRowBase(titleRowBase);
		resultLayout.setTitleColumnBase(titleColumnBase);
		resultLayout.setOffset(offset);
		resultLayout.setFullName(fullName);
		resultLayout.setNickName(nickname);
		resultLayout.setCurrentLevel(level);
		return resultLayout;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public String getTitleRowBase() {
		return titleRowBase;
	}

	public void setTitleRowBase(String titleRowBase) {
		this.titleRowBase = titleRowBase;
	}

	public String getTitleColumnBase() {
		return titleColumnBase;
	}

	public void setTitleColumnBase(String titleColumnBase) {
		this.titleColumnBase = titleColumnBase;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
}
