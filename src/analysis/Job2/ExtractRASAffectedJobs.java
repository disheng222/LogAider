package analysis.Job2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import util.PVFile;
import util.RecordSplitter;

public class ExtractRASAffectedJobs {

	
	
	public static void main(String[] args)
	{
		String rasFatalLogFile = "/home/sdi/windows-xp/Work/Catalog-project/Catalog-data/miralog/5-year-data/ANL-ALCF-RE-MIRA_20130409_20180930-fatal.csv";
		String cobaltLogDir = "/home/sdi/windows-xp/Work/Catalog-project/Catalog-data/miralog/5-year-data/Job";
		
		HashMap<String, JobElement> rasRecordJobMap = new HashMap<String, JobElement>(); 
		List<JobElement> jobList = new ArrayList<JobElement>();
		List<String> fileList = PVFile.getFiles(cobaltLogDir, "csvs");
		Collections.sort(fileList);
		Iterator<String> iterf = fileList.iterator();
		while(iterf.hasNext())
		{
			String fileName = iterf.next();
			String filePath = cobaltLogDir+"/"+fileName;
			List<String> lineList = PVFile.readFile(filePath);
			
			Iterator<String> iter = lineList.iterator();
			while(iter.hasNext())
			{
				String line = iter.next();
				String[] s = RecordSplitter.partition(line);
				String signal = (s[57]);
				if(signal.equals("35"))
				{
					String rasRecord = s[58];
					JobElement e = new JobElement(line, rasRecord);
					rasRecordJobMap.put(rasRecord, e);
					jobList.add(e);
				}
			}
				
		}
		
		
		List<String> rasList = new ArrayList<String>();
		
		double initLogTime = System.currentTimeMillis()/1000.0;
		List<String> lineList = PVFile.readFile(rasFatalLogFile);
		Iterator<String> iter = lineList.iterator();
		for(int i = 0;iter.hasNext();i++)
		{
			if(i%100000==0)
				PVFile.showProgress(initLogTime, i, lineList.size(), "");
			String line = iter.next();
			String[] s = RecordSplitter.partition(line);
			String fatalRecord = s[0];
			String msgID = s[1];
			String category = s[2];
			String component = s[3];
			String severity = s[4];
			String description = s[13];
			JobElement e = rasRecordJobMap.get(fatalRecord);
			if(e!=null)
			{
				e.setRasCategory(category);
				e.setRasComponent(component);
				e.setRasMsgID(msgID);
				e.setRasRecord(fatalRecord);
				e.setRasMsg(description);
				e.setRasSeverity(severity);
				rasList.add(line);
			}
		}
		
		PVFile.print2File(rasList, cobaltLogDir+"/rasAffectedJobs/jobAffectedRASList.csvr");
		System.out.println("Output: "+cobaltLogDir+"/rasAffectedJobs/jobAffectedRASList.csvr");
		
		//check jobs
		Iterator<JobElement> iterj = jobList.iterator();
		int nullCounter = 0 ;
		while(iterj.hasNext())
		{
			JobElement e = iterj.next();
			if(e.getRasMsgID()==null)
			{
				System.out.println((nullCounter++)+",null msgID: "+e);
			}
		}
		
		PVFile.print2File(jobList, cobaltLogDir+"/rasAffectedJobs.csv");
		System.out.println("output file: "+ cobaltLogDir+"/rasAffectedJobs.csv");
		
	}
}

class JobElement
{
	String cobaltMsg; 
	String rasRecord;
	String rasMsgID = null;
	String rasComponent = null;
	String rasCategory = null;
	String rasMsg = null;
	String rasSeverity = null;
	
	public JobElement(String cobaltMsg, String rasRecord)
	{
		this.cobaltMsg = cobaltMsg;
		this.rasRecord = rasRecord;
	}	
	
	public String getCobaltMsg() {
		return cobaltMsg;
	}
	public void setCobaltMsg(String cobaltMsg) {
		this.cobaltMsg = cobaltMsg;
	}
	public String getRasRecord() {
		return rasRecord;
	}
	public void setRasRecord(String rasRecord) {
		this.rasRecord = rasRecord;
	}
	public String getRasMsgID() {
		return rasMsgID;
	}
	public void setRasMsgID(String rasMsgID) {
		this.rasMsgID = rasMsgID;
	}
	public String getRasComponent() {
		return rasComponent;
	}
	public void setRasComponent(String rasComponent) {
		this.rasComponent = rasComponent;
	}
	public String getRasCategory() {
		return rasCategory;
	}
	public void setRasCategory(String rasCategory) {
		this.rasCategory = rasCategory;
	}
	public String getRasMsg() {
		return rasMsg;
	}
	public void setRasMsg(String rasMsg) {
		this.rasMsg = rasMsg;
	}
	public String getRasSeverity() {
		return rasSeverity;
	}
	public void setRasSeverity(String rasSeverity) {
		this.rasSeverity = rasSeverity;
	}

	public String toString()
	{
		return cobaltMsg+","+rasMsgID+","+rasCategory+","+rasComponent+",\""+rasMsg+"\"";
	}
}
