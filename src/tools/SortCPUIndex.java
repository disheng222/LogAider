package tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class SortCPUIndex {

	public static void main(String[] args)
	{
		String filePath = "/home/fti/Catalog-project/miralog/featureState/CPU/CPU-SEVERITY.pr";
		List<String> lineList = PVFile.readFile(filePath);
		List<CPUIndexItem> cList = new ArrayList<CPUIndexItem>();
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.startsWith("#"))
			{
				cList.add(new CPUIndexItem(0,line));
				continue;
			}
			String cpuIndex = line.split("\\s")[0];
			int cIndex = Integer.parseInt(cpuIndex);
			CPUIndexItem cii = new CPUIndexItem(cIndex, line);
			cList.add(cii);
		}
		
		Collections.sort(cList);
		PVFile.print2File(cList, filePath+".sort");
		System.out.println("outputFile="+filePath+".sort");
		System.out.println("Done.");
	}
}

class CPUIndexItem implements Comparable<CPUIndexItem>
{
	private int cpuIndex;
	private String line;
	
	public CPUIndexItem(int cpuIndex, String line) {
		this.cpuIndex = cpuIndex;
		this.line = line;
	}

	public int getCpuIndex() {
		return cpuIndex;
	}

	public void setCpuIndex(int cpuIndex) {
		this.cpuIndex = cpuIndex;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public int compareTo(CPUIndexItem other)
	{
		if(cpuIndex < other.cpuIndex)
			return -1;
		else if(cpuIndex > other.cpuIndex)
			return 1;
		else
			return 0;
	}
	
	public String toString()
	{
		return line;
	}
}
