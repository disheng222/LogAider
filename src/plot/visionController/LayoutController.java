package plot.visionController;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import plot.controlElement.MiraCluster;
import plot.controlElement.State;

public class LayoutController {
	public static int oID = 0; //object ID for gnuplot
	public static int lID = 0; //label ID for gnuplot
	public static int aID = 0; //arrow ID
	public static int pad = 7;
	public static int fieldBoxHeight = 25;
	public static int maxLevel = 2;
	
	public static DecimalFormat df = new DecimalFormat("00");
	
	public static String[] colors = ColorController.generateGradientColors("FFFF00", "FF0000", 64);
	
	int count;
	int currentLevel;
	
	int rowNum = 0;
	int columnNum = 0;
	
	float width = 0;
	float height = 0;
	
	private String fullName;
	private String nickName;
	private String base = null;
	private String titleRowBase;
	private String titleColumnBase;
	private int offset = 0;
	
	public LayoutController(int count, float width, float height, int curLevel) {
		this.count = count;
		this.width = width;
		this.height = height;
		this.currentLevel = curLevel;
	}

	public List<String> buildVisionBlock(
			float x1, float y1, 
			int rowNum, int columnNum, 
			float cellWidth, float cellHeight, List<float[]> coordList, 
			int min, int max, State[] state)
	{
		List<String> resultList = new ArrayList<String>();
		int k = 0;
		for(int j = 0;j<rowNum;j++)
			for(int i = 0;i<columnNum;i++)
			{
				float centerX = x1+(i+1)*pad+i*cellWidth+cellWidth/2;
				float centerY = y1+(j+1)*pad+j*cellHeight+cellHeight/2;
				String s = drawRect(centerX, centerY, cellWidth, cellHeight, min, max, state[k++].getFatalRate());
				if(coordList!=null)
				{
					float bottomLeftX = centerX - cellWidth/2;
					float bottomLeftY = centerY - cellHeight/2;
					coordList.add(new float[]{bottomLeftX, bottomLeftY});					
				}
				resultList.add(s);
			}
		return resultList;
	}
	
	public static String drawRect(int centerX, int centerY, int cellWidth, int cellHeight,
			String color)
	{
		StringBuilder sb = new StringBuilder("set object ");
		sb.append(String.valueOf(++DenseLayout.oID));
		sb.append(" rect center ");
		sb.append(centerX);
		sb.append(",");
		sb.append(centerY);
		sb.append(" size ");
		sb.append(cellWidth);
		sb.append(",");
		sb.append(cellHeight);
		sb.append(" front fc rgb ");
		sb.append(color);
		sb.append(" fillstyle solid 1");
		return sb.toString();
	}
	
	public static String drawRect(float centerX, float centerY, float cellWidth, 
			float cellHeight, int min, int max, int curValue)
	{
		StringBuilder sb = new StringBuilder("set object ");
		sb.append(String.valueOf(++LayoutController.oID));
		sb.append(" rect center ");
		sb.append(centerX);
		sb.append(",");
		sb.append(centerY);
		sb.append(" size ");
		sb.append(cellWidth);
		sb.append(",");
		sb.append(cellHeight);
		String color = getColor(false, min, max, curValue);
		sb.append(" front fc rgb ");
		sb.append(color);
		sb.append(" fillstyle solid 1");
		return sb.toString();
	}
	
	public static String[] drawRect(float centerX, float centerY, float cellWidth, 
			float cellHeight, String label
			)
	{
		StringBuilder sb = new StringBuilder("set object ");
		sb.append(String.valueOf(++LayoutController.oID));
		sb.append(" rect center ");
		sb.append(centerX);
		sb.append(",");
		sb.append(centerY);
		sb.append(" size ");
		sb.append(cellWidth);
		sb.append(",");
		sb.append(cellHeight);
		String color = "'white'";
		sb.append(" front fc rgb ");
		sb.append(color);
		sb.append(" fillstyle solid 1");
		String rect = sb.toString();
		
		if(label!=null)
		{	
			StringBuilder sb2 = new StringBuilder("set label ");
			sb2.append(++LayoutController.lID);
			sb2.append(" \"").append(label).append("\" ");
			sb2.append("at ");
			sb2.append(centerX).append(",").append(centerY);
			sb2.append(" centre font \"Arial,90\" norotate front nopoint offset character 0,0,0");
			
			String lab = sb2.toString();	
			return new String[]{rect, lab};
		}
		else
			return new String[]{rect, null};
		

	}
	
	private static String getColor(boolean isTitle, int min, int max, int cur)
	{
		if(isTitle)
		{
			return "'white'";
		}
		else
		{
			if(cur == min)
				return "'white'";
			float length = max - min;
			float curLength = cur-min;
			float ratio = curLength/length;
			
			int codeIndex = (int)(ratio*colors.length);
			if(codeIndex==64)
				codeIndex = 63;
			return colors[codeIndex];
		}
	}
	
	/**
	 * 
	 * @param x1: bottom left point (x)
	 * @param y1: bottom left point (y)
	 * @param List<float[]>: the coordinate list of the sub components....
	 * @return
	 */
	public List<String> buildVisionCode(float x1, float y1, String label, List<float[]> coordList, 
			int min, int max, State[] state)
	{
//		float x2 = x1+width;
//		float y2 = y1+height;
		
		float fieldBoxHeight = LayoutController.fieldBoxHeight;
		float[] cellSize = computeCellSize(fieldBoxHeight);
		float cellWidth = cellSize[0];
		float cellHeight = cellSize[1];
		
		int pad = LayoutController.pad;

		List<String> resultList = new ArrayList<String>();
		
		List<String> layoutList = buildVisionBlock(x1, y1, rowNum, columnNum, cellWidth, cellHeight, coordList, 
				min, max, state);
		
		resultList.addAll(layoutList);
		
		if(currentLevel <= maxLevel)
		{
			float fieldBoxWidth = width - 4*pad;
			float fieldCenterX = x1+width/2;
			float fieldCenterY = y1+height-pad-LayoutController.fieldBoxHeight/2;
			String[] titleBox = LayoutController.drawRect(fieldCenterX, fieldCenterY, fieldBoxWidth, fieldBoxHeight, label);

			resultList.add(titleBox[0]);
			resultList.add(titleBox[1]);
		}
		
		return resultList;
	}
	
	public float[] computeCellSize(float fieldBoxHeight)
	{
		int rowNum = getRowNum();
		float minHeight = fieldBoxHeight+(rowNum+2)*DenseLayout.pad;
		if(height <= minHeight)
		{
			System.out.println("Error: height < minHeight ("+minHeight+") !");
			System.exit(0);
		}
		
		int columnNum = getColumnNum();
		float minWidth = (columnNum+1)*DenseLayout.pad;
		if(width <= minWidth)
		{
			System.out.println("Error: width < minWidth ("+minWidth+") !");
			System.exit(0);			
		}
		
		float xLength = width - minWidth;
		float yLength = height - minHeight;
		
		float cellWidth = xLength/columnNum;
		float cellHeight = yLength/rowNum;
		
		return new float[]{cellWidth, cellHeight};
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getRowNum() {
		return rowNum;
	}
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}
	public int getColumnNum() {
		return columnNum;
	}
	public void setColumnNum(int columnNum) {
		this.columnNum = columnNum;
	}
	public int getCurrentLevel() {
		return currentLevel;
	}
	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	
	public static List<String> generateColorKey(int centerX_start, int centerY)
	{
		int startX = centerX_start;
		List<String> keyStringList = new ArrayList<String>();
		int PAD = pad*4;
		int n = colors.length;
		for(int j = 0;j<n;j++)
		{
			startX = startX + PAD + pad;
			int y = centerY;
			int width = PAD;
			int height = 4*pad;
			String s = drawRect(startX,y,width,height,colors[j]);
			keyStringList.add(s);
		}
		return keyStringList;
	}
	
	public static String generateLabel(int x, int y, String label)
	{
		StringBuilder sb = new StringBuilder("set label ");
		sb.append(++LayoutController.oID);
		sb.append(" \""+label+"\"");
		sb.append(" at ");
		sb.append(x).append(",").append(y);
		sb.append(" font \"Arial,100\" right norotate back nopoint");
		return sb.toString();
	}
	
	public static String generateArrow(int x1, int y1, int x2, int y2)
	{
		String s = "set arrow "+(++aID)+" from "+x1+","+y1+" to "+x2+","+y2+" as 1";
		return s;

	}
	
	public static List<String> generateTextKey(int x, int y, LayoutType[] layouts)
	{
		List<String> keyList = new ArrayList<String>();
		
		int endTxtLoc = x+2300;
		
		int x_1 = x - pad*18;
		int y_1 = y - pad*6;
		String rackLabel = generateLabel(x_1,y_1, "Number of events on "+layouts[1].getFullName());
		keyList.add(rackLabel);
		
		int minRate = MiraCluster.minRate[1];
		int maxRate = MiraCluster.maxRate[1];
		if(maxRate == 0)
			minRate = 0;
		String minText = generateLabel(x + pad*6, y_1, String.valueOf(minRate));
		String maxText = generateLabel(endTxtLoc, y_1, String.valueOf(maxRate));
		keyList.add(minText);
		keyList.add(maxText);
		
		if(LayoutController.maxLevel>=1)
		{
			int x_2 = x_1;
			int y_2 = y_1 - pad*6;
			String mpLabel = generateLabel(x_2, y_2, "Number of events on "+layouts[2].getFullName());
			keyList.add(mpLabel);
			minRate = MiraCluster.minRate[2];
			maxRate = MiraCluster.maxRate[2];
			if(maxRate == 0)
				minRate = 0;
			
			minText = generateLabel(x + pad*6, y_2, String.valueOf(minRate));
			maxText = generateLabel(endTxtLoc, y_2, String.valueOf(maxRate));
			keyList.add(minText);
			keyList.add(maxText);
			
			if(LayoutController.maxLevel>=2)
			{
				int x_3 = x_1;
				int y_3 = y_2 - pad*6;
				String nodeLabel = generateLabel(x_3, y_3, "Number of events on "+layouts[3].getFullName());
				keyList.add(nodeLabel);
				minRate = MiraCluster.minRate[3];
				maxRate = MiraCluster.maxRate[3];
				if(maxRate == 0)
					minRate = 0;
				minText = generateLabel(x + pad*6, y_3, String.valueOf(minRate));
				maxText = generateLabel(endTxtLoc, y_3, String.valueOf(maxRate));
				keyList.add(minText);
				keyList.add(maxText);
			}
		}
		
		return keyList;
	}
	
	
	public static List<String> generateKey(int leftEdge, LayoutType[] layouts, String date)
	{
		List<String> plotCodeList = generateKey(leftEdge, layouts);
		String dateLabel = generateLabel(leftEdge-pad*18,180, date);
		plotCodeList.add(dateLabel);
		return plotCodeList;
	}
	
	public static List<String> generateKey(int leftEdge, LayoutType[] layouts)
	{
		List<String> plotCodeList = new ArrayList<String>();
		List<String> keyList = LayoutController.generateColorKey(leftEdge, 180);
		plotCodeList.addAll(keyList);
		
		List<String> textKeyList = LayoutController.generateTextKey(leftEdge, 180, layouts);
		plotCodeList.addAll(textKeyList);
		
		int startX = leftEdge+100;
		int startY = 137;
		int endX = leftEdge+2100;
		String arrow1 = LayoutController.generateArrow(startX, startY, endX, startY);
		plotCodeList.add(arrow1);
		if(LayoutController.maxLevel>=1)
		{
			String arrow2 = LayoutController.generateArrow(startX, startY-LayoutController.pad*6, endX, startY-LayoutController.pad*6);
			plotCodeList.add(arrow2);
			if(LayoutController.maxLevel>=2)
			{
				String arrow3 = LayoutController.generateArrow(startX, startY-LayoutController.pad*12, endX, startY-LayoutController.pad*12);
				plotCodeList.add(arrow3);
			}	 			
		}	
		return plotCodeList;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
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

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public String computeLocalID(int decimalID)
	{
		if(base!=null)
		{
			String localID = null;
			if(base.equals("hex"))
			{
				String id = BaseController.toHexFormat(decimalID,2);
				localID = nickName+id;
			}
			else if(base.equals("binary"))
			{
				String id = BaseController.toBinaryFormat(decimalID, 1); 
				localID = nickName+id;
			}
			else if(base.equals("oct"))
			{
				String id = BaseController.toOctalFormat(decimalID,  2);			
				localID = nickName+id;
			}
			else if(base.equals("decimal"))//decimal
			{
				localID = nickName+decimalID;
			}
			else if(base.equals("decimal2"))
			{
				localID = nickName+df.format(decimalID);
			}
			if(localID==null)
			{
				System.out.println("Error: The base setting is wrong in the schama.");
				System.exit(0);
			}
			return localID;
		}
		else if(titleRowBase==null || titleColumnBase==null)
		{
			System.out.println("Error in schama setting: titleRepresentBase and titleRowBase/titleColumnBase cannot be both null.");
			System.exit(0);
			return null;
		}
		else
		{
			String id = "";
			if(titleColumnBase.equals("binary"))
			{
				id = BaseController.toBinaryFormat(decimalID, 2);
				id = BaseController.setOffsetToLastLetter(id, decimalID, offset, titleColumnBase, titleRowBase);				
			}
			else
			{
				System.out.println("not implemented yet in this case.");
				System.exit(0);
			}
			String localID = nickName+id;
			return localID;
		}
	}
}
