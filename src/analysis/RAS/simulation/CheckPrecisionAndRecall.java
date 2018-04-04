package analysis.RAS.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import util.PVFile;

/**
 * Evaluation: 
 * /home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/month7-12/analyzeMsg90-basedonMonth1-6
 * /home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/month7-12/analyzeMsg90-basedonMonth7-12 (based on vc.count of month7-12)
 * Then compare the above two directory, and compute precision and recall.
 * @author fti
 *
 */
public class CheckPrecisionAndRecall {

	
	public static void main(String[] args)
	{
		String probTrainingDir = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/month7-12/analyzeMsg90-basedonMonth1-6";
		String probRealityDir = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/month7-12/analyzeMsg90-basedonMonth7-12";
		String outputFile = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/month7-12/analyzeMsg90-basedonMonth7-12/precisionRecall.txt";
		String extension = "prob";
		
/*		String probTrainingDir = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/cobalt/month7-12/analyzeMsg-basedonMonth1-6";
		String probRealityDir = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/cobalt/month7-12/analyzeMsg-basedonMonth7-12";
		String outputFile = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/cobalt/month7-12/analyzeMsg-basedonMonth7-12/precisionRecall.txt";
		String extension = "prob";*/
		
		PRCheckingResult[] result = new PRCheckingResult[100];
		for(int i = 0;i<result.length;i++)
		{
			float threshold = 0.01f+0.01f*i;
			result[i] = new PRCheckingResult(threshold);
		}
		
		System.out.println("Loading file names....");
		
		List<String> trainingFileList = PVFile.getFiles(probTrainingDir, extension);
		List<String> realityFileList = PVFile.getFiles(probRealityDir, extension);
		
		System.out.println("Sorting file names....");
		
		Collections.sort(trainingFileList);
		Collections.sort(realityFileList);
		
		System.out.println("Processing files....");
		double initLogTime = System.currentTimeMillis()/1000.0;
		Iterator<String> iter = trainingFileList.iterator();
		Iterator<String> iter2 = realityFileList.iterator();
		
		for(int i = 0;iter.hasNext();i++)
		{
			String trainingFileName = iter.next();
			String trainingFilePath = probTrainingDir+"/"+trainingFileName;
			String realityFileName = iter2.next();
			String realityFilePath = probRealityDir+"/"+realityFileName;
			
			List<String> trainLineList = PVFile.readFile(trainingFilePath);
			List<String> realityLineList = PVFile.readFile(realityFilePath);
			
			Map<String, AcrossFieldResult> acrossFieldMap = convertRealityLineList2Map(realityLineList);
			List<AcrossFieldResult> acrossFieldList = convertTrainLineList2List(trainLineList);
			
			Iterator<AcrossFieldResult> it = acrossFieldList.iterator();
			while(it.hasNext())
			{
				AcrossFieldResult tr = it.next(); //training result
				String key = tr.getKey();
				AcrossFieldResult rr = acrossFieldMap.get(key); //reality result
			
				checkResult(tr, rr,result);
			}
			if(i%1000==0)
				PVFile.showProgress(initLogTime, i, trainingFileList.size(), trainingFileName);
		}
		
		List<String> finalResultList = new ArrayList<String>();
		for(int i = 0;i<result.length;i++)
		{
			System.out.println(result[i]);
			finalResultList.add(result[i].toString());
		}
		finalResultList.add(0, "#threshold precision recall FP FN TP TN");
		PVFile.print2File(finalResultList, outputFile);
		System.out.println("done");
	}
	
	public static void checkResult(AcrossFieldResult tr, AcrossFieldResult rr, PRCheckingResult[] r)
	{
		float testProb = tr.getProb();
		float realProb = 0;
		if(rr!=null)
			realProb = rr.getProb();
		else
			return;
		
		for(int i= 0;i<r.length;i++)
		{
			r[i].check(testProb, realProb);
		}
	}
	
	public static List<AcrossFieldResult> convertTrainLineList2List(List<String> trainLineList)
	{
		List<AcrossFieldResult> list = new ArrayList<AcrossFieldResult>();
		Iterator<String> iter = trainLineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.startsWith("#"))
				continue;
			String[] s = line.split(" : ");
			if(s.length<2)
				continue;
			String key = s[0];
			String[] s2 = s[1].split("\\s");
			float prob = Float.parseFloat(s2[0]);
			String[] s3 = s2[1].replace("(", "").replace(")", "").split(",");
			int sampleSize = Integer.parseInt(s3[0]);
			float marginOfErr = Float.parseFloat(s3[1]);
			AcrossFieldResult r = new AcrossFieldResult(key, prob, sampleSize, marginOfErr);
			list.add(r);
		}
		return list;
	}	
	
	public static Map<String, AcrossFieldResult> convertRealityLineList2Map(List<String> trainLineList)
	{
		Map<String, AcrossFieldResult> map = new HashMap<String, AcrossFieldResult>();
		Iterator<String> iter = trainLineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.startsWith("#"))
				continue;
			String[] s = line.split(" : ");
			String key = s[0];
			String[] s2 = s[1].split("\\s");
			float prob = Float.parseFloat(s2[0]);
			String[] s3 = s2[1].replace("(", "").replace(")", "").split(",");
			int sampleSize = Integer.parseInt(s3[0]);
			float marginOfErr = Float.parseFloat(s3[1]);
			AcrossFieldResult r = new AcrossFieldResult(key, prob, sampleSize, marginOfErr);
			map.put(key, r);
		}
		return map;
	}
}

class PRCheckingResult
{
	private float threshold;
	private int FP = 0;
	private int FN = 0;
	private int TP = 0;
	private int TN = 0;
	
	public PRCheckingResult(float threshold) {
		this.threshold = threshold;
	}
	public int getFP() {
		return FP;
	}
	public void setFP(int fP) {
		FP = fP;
	}
	public int getFN() {
		return FN;
	}
	public void setFN(int fN) {
		FN = fN;
	}
	public int getTP() {
		return TP;
	}
	public void setTP(int tP) {
		TP = tP;
	}
	public int getTN() {
		return TN;
	}
	public void setTN(int tN) {
		TN = tN;
	}
	public float getPrecision()
	{
		return ((float)TP)/(TP+FP);
	}
	public float getRecall()
	{
		return ((float)TP)/(TP+FN);
	}
	public float getThreshold() {
		return threshold;
	}
	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}
	public void check(float trProb, float rrProb)
	{
		if(trProb>=threshold && rrProb>=threshold)
		{
			TP++;
			return;
		}
		if(trProb>=threshold && rrProb<threshold)
		{
			FP++;
			return;
		}
		if(trProb<threshold && rrProb>=threshold)
		{
			FN++;
			return;
		}
		if(trProb<threshold && rrProb<threshold)
			TN++;
	}
	public String toString()
	{
		return PVFile.df4.format(threshold)+" "+getPrecision()*100+" "+getRecall()*100+" "+FP+" "+FN+" "+TP+" "+TN;
	}
}

class AcrossFieldResult
{
	private String key;
	private float prob;
	private int sampleSize;
	private float marginOfErr;
	
	public AcrossFieldResult(String key, float prob, int sampleSize, float marginOfErr) {
		this.key = key;
		this.prob = prob;
		this.sampleSize = sampleSize;
		this.marginOfErr = marginOfErr;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public float getProb() {
		return prob;
	}
	public void setProb(float prob) {
		this.prob = prob;
	}
	public int getSampleSize() {
		return sampleSize;
	}
	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}
	public float getMarginOfErr() {
		return marginOfErr;
	}
	public void setMarginOfErr(float marginOfErr) {
		this.marginOfErr = marginOfErr;
	}
}