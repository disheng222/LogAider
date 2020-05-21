package analysis.Job2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import util.PVFile;

public class ExtractFrequentData {

	public static int selectColNum = 15;
	public static int selectRowNum = 10;
	
	public static void main(String[] args)
	{
		//String file = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/featureState/LOCATION/LOCATION-EXIT_STATUS.fs";
		//String file = "/home/sdi/windows-xp/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/rasAffectedJobs/featureState/USERNAME_GENID/USERNAME_GENID-RAS_MSG_ID.fs";
		//String file = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/featureState2/numMultilocationTasksLog.fsct";
		//String file = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/featureState/PROJECT_NAME_GENID/PROJECT_NAME_GENID-EXIT_SIGNAL.fs";
		//String file = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/featureState/USERNAME_GENID/USERNAME_GENID-EXIT_SIGNAL.fs";
		//String file = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/featureState/LOCATION/LOCATION-EXIT_SIGNAL.fs";
		//String file = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/darshan/analysis/totalReadByteErrLog.fsct";
		//String file = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/featureState2/runTimeErrLog.fsct";
		//String file = "/home/sdi/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/featureState/NODES_USED/NODES_USED-EXIT_SIGNAL.fs";
		String file = "/home/sdi/windows-xp/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/rasAffectedJobs/featureState2/corehourErrLog.fsct";
		
		//String file = "/home/sdi/windows-xp/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job/darshan/analysis/totalWrittenByteErrLog.fsct";
		List<String> lineList = PVFile.readFile(file);
		
		Iterator<String> iter = lineList.iterator();
		
		String fieldLine = iter.next();
		String[] fields = fieldLine.split("\\s");
		
		int totalSum = 0, freqSum = 0;
		int[] sum = new int[fields.length]; //starting from '1': index [0] is useless
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] s = line.split("\\s");
			for(int i = 1;i<sum.length;i++)
				sum[i] += Integer.parseInt(s[i]);
		}
		
		for(int i = 0;i<sum.length;i++)
			totalSum += sum[i];
		
		List<FrequentElement> columnList = new ArrayList<FrequentElement>();
		for(int i = 1;i < fields.length;i++)
		{
			FrequentElement e = new FrequentElement(fields[i], sum[i], i);
			columnList.add(e);
		}
		Collections.sort(columnList);
		
		int[] selectedIndex = new int[selectColNum];
		for(int i = 0;i<selectColNum;i++)
		{
			FrequentElement e = columnList.get(i);
			selectedIndex[i] = e.getIndex();
			freqSum += e.getSum();
		}
		
		List<FrequentElement> lineList2 = new ArrayList<FrequentElement>();
		iter = lineList.iterator();
		iter.next();//remove the meta line
		while(iter.hasNext())
		{
			String line = iter.next();
			int sum2 = 0;
			String[] s = line.split("\\s");
			for(int i = 0;i<selectedIndex.length;i++)
			{
				int index = selectedIndex[i];
				sum2 += Integer.parseInt(s[index]);
			}
			FrequentElement e = new FrequentElement(line, sum2, 0);
			lineList2.add(e);
		}
		
		Collections.sort(lineList2);
		
		List<String> resultList = new ArrayList<String>();
		String metaLine = "#";
		for(int i = 0;i<selectedIndex.length;i++)
		{
			metaLine += " "+fields[selectedIndex[i]];
		}
		resultList.add(metaLine);
		
		for(int i = 0;i<selectRowNum;i++)
		{
			String line = lineList2.get(i).getKey();
			String[] s = line.split("\\s");
			String newline = s[0];
			for(int j = 0;j<selectedIndex.length;j++)
			{
				int index = selectedIndex[j];
				String c = s[index];
				newline += " "+c;
			}
			resultList.add(newline);
		}
		PVFile.print2File(resultList, file+"2");
		System.out.println("output file = "+file+"2");
	
		//use index to sort again
		FrequentElement.sortingType = 1;
		Collections.sort(lineList2);
		
		resultList = new ArrayList<String>();
		
		for(int i = 0;i<selectRowNum;i++)
		{
			String line = lineList2.get(i).getKey();
			String[] s = line.split("\\s");
			String newline = s[0];
			for(int j = 0;j<selectedIndex.length;j++)
			{
				int index = selectedIndex[j];
				String c = s[index];
				newline += " "+c;
			}
			resultList.add(newline);
		}
		PVFile.print2File(resultList, file+"3");
		System.out.println("output file = "+file+"3");
		
		System.out.println("totalSum="+freqSum+",totalFraction="+(1.0*freqSum/totalSum));
	}
}

class FrequentElement implements Comparable<FrequentElement>
{
	private String key;
	private int sum = 0;
	private int index;
	public static int sortingType = 0; //0 means frequent element; 1 means index
	
	public FrequentElement(String key, int sum, int index) 
	{
		this.key = key;
		this.sum = sum;
		this.index = index;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public long getSum() {
		return sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int compareTo(FrequentElement e)
	{
		if(sortingType==1)
		{
			if(index<e.index)
				return -1;
			else if(index>e.index)
				return 1;
			else 
				return 0;
		}
		else
		{
			if(sum > e.sum)
				return -1;
			else if(sum < e.sum)
				return 1;
			else
				return 0;			
		}

	}
	
}