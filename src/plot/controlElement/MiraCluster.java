package plot.controlElement;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import plot.visionController.DenseLayout;
import plot.visionController.LayoutController;
import plot.visionController.LayoutSchema;
import util.PVFile;

public class MiraCluster {
	public static HashMap<String, Record> componentMap = new HashMap<String, Record>();
	public static int[] minRate = new int[5];
	public static int[] maxRate = new int[5];
	
	private Rack[] rack;
	private State state = null;
	private LayoutController layout = null;
	
	public MiraCluster(int rackNum, LayoutController layout)
	{
		this.layout = layout;
		rack = new Rack[rackNum];
		float[] cellSize = layout.computeCellSize(LayoutController.fieldBoxHeight);
		float cellWidth = cellSize[0];
		float cellHeight = cellSize[1];	
		
		LayoutController rackLayout = LayoutSchema.layoutTypes[1].loadLayout(cellWidth, cellHeight);
		//int curLevel = rackLayout.getCurrentLevel();
		//DenseLayout rackLayout = new DenseLayout(2, false, cellWidth, cellHeight, curLevel+1);
		
		for(int i = 0;i<rack.length;i++)
		{	
			String localID = rackLayout.computeLocalID(i);
			String globalID = localID;
			rack[i] = new Rack(globalID, localID, i, rackLayout);
			componentMap.put(globalID, rack[i]);
		}
		
		state = new State();
	}

	public Rack[] getRack() {
		return rack;
	}

	public void setRack(Rack[] rack) {
		this.rack = rack;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public LayoutController getLayout() {
		return layout;
	}

	public void setLayout(DenseLayout layout) {
		this.layout = layout;
	}
	
	public void loadData(String distributionDirPath, String extension)
	{
		List<String> fileList = PVFile.getFiles(distributionDirPath, extension);
		Collections.sort(fileList);
		Iterator<String> iter = fileList.iterator();
		while(iter.hasNext())
		{
			String fileName = iter.next();
			int level = Integer.parseInt(fileName.split("\\.")[0].replace("level", ""));
			String filePath = distributionDirPath+"/"+fileName;
			List<String> lineList = PVFile.readFile(filePath);
			
			int min = Integer.MAX_VALUE, max = 0;
			Iterator<String> iter2 = lineList.iterator();
			while(iter2.hasNext())
			{
				String line = iter2.next();
				String[] s = line.split("\\s");
				String key = s[0];
				Record r = componentMap.get(key);
				if(r!=null)
				{
					int rate = Integer.parseInt(s[1]);
					if(min > rate) min = rate;
					if(max < rate) max = rate;
					
					r.setFatalErrRate(rate);					
				}
			}
			
			minRate[level] = min;
			maxRate[level] = max;
		}
	}
	
	public State[] getStates()
	{
		State[] states = new State[rack.length];
		for(int i = 0;i<states.length;i++)
			states[i] = rack[i].getState();
		return states;
	}
}
