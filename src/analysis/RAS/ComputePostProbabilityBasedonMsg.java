package analysis.RAS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import element.PostierProbabilityItem;
import util.ConversionHandler;
import util.PVFile;
import util.RecordSplitter;

public class ComputePostProbabilityBasedonMsg {

	static HashMap<String, Integer> fieldIDMap = new HashMap<String, Integer>();
	static HashMap<String, Integer> fieldValueCombinationCountMap = new HashMap<String, Integer>();
	static HashMap<String, List<String>> inevitableTgtFVToSrcFVMap = new HashMap<String, List<String>>();
	static List<String> inevitableTgtFVList = new ArrayList<String>();
	static int totalMsgCount = 0;
	static String[] fields = null;
	static int[] fieldIndex = null;
	
	public static double confidenceLevel = 0.95; //only three options: 0.9, 0.95, ,and 0.99
	
	int msgLineNumber = 0;
	String msg;
	
	List<PostierProbabilityItem> relatedPostProbList = new ArrayList<PostierProbabilityItem>();
	
	public ComputePostProbabilityBasedonMsg(int msgLineNumber, String msg) {
		this.msgLineNumber = msgLineNumber;
		this.msg = msg;
	}

	public static void main(String[] args)
	{
		if(args.length<5)
		{
			System.out.println("Usage: java ComputePostProbabilityBasedonMsg [fieldListFile] [vcCountHashMapFile] [inputMessageFile] [outputResultFile] [confidenceLevel]");
			System.out.println("Example: java ComputePostProbabilityBasedonMsg /home/fti/Catalog-project/miralog/fieldValueCombination/fieldList.txt "
					+ "/home/fti/Catalog-project/miralog/fieldValueCombination/vc.count \"/home/fti/Catalog-project/miralog/inputMsg.txt\" /home/fti/Catalog-project/miralog/analyzeMsg 0.95");
			System.exit(0);
		}
		
		String fieldListFile = args[0];
		String vcCountHashMapFile = args[1];
		String msgFile = args[2];
		String outputResultDir = args[3];
		confidenceLevel = Double.parseDouble(args[4]);
		
		System.out.println("Loading fieldListFile: "+fieldListFile);
		List<String> fieldList = new ArrayList<String>();
		List<String> lineList = PVFile.readFile(fieldListFile);
		Iterator<String> iter = lineList.iterator();
		int size = lineList.size()-1;
		fieldIndex = new int[size];
		for(int i = 0;iter.hasNext();)
		{
			String line = iter.next();
			if(!line.startsWith("#"))
			{
				String[] s = line.split("\\s");
				fieldList.add(line);
				fieldIndex[i] = Integer.parseInt(s[1]);
				i++;
			}
		}
		fields = ConversionHandler.convertStringList2StringArray(fieldList);
				
		totalMsgCount = loadVCCountHashMap(vcCountHashMapFile);
		
		List<String> msgList = PVFile.readFile(msgFile);
		Iterator<String> iter2 = msgList.iterator();
		for(int i = 0;iter2.hasNext();i++)
		{
			String msg = iter2.next();
			System.out.println("Processing msg "+i+" : "+msg);
			ComputePostProbabilityBasedonMsg cppbm = new ComputePostProbabilityBasedonMsg(i,msg);
			cppbm.computePostProbability(fieldIndex,  outputResultDir);			
		}

		System.out.println("done.");
	}

	public void computePostProbability(int[] fieldIndex, String outputResultDir)
	{
		analyzeMessage(msg, fieldIndex);
		
		String outputResultFile = outputResultDir+"/"+msgLineNumber+".prob";
		System.out.println("outputResultFile = "+outputResultFile);
		Collections.sort(relatedPostProbList);
		String fieldNamesIndices = "";
		for(int i = 0;i<fields.length;i++)
			fieldNamesIndices += fields[i]+" ; ";
		relatedPostProbList.add(0,new PostierProbabilityItem("# "+fieldNamesIndices, -1, 0, 0));
		relatedPostProbList.add(1,new PostierProbabilityItem("# "+msg, -1, 0, 0));
		PVFile.print2File(relatedPostProbList, outputResultFile);
	}
	
	/**
	 * 
	 * @param vcCountHashMapFile
	 * @return
	 */
	public static int loadVCCountHashMap(String vcCountHashMapFile)
	{
		List<String> lineList = PVFile.readFile(vcCountHashMapFile);
		Iterator<String> iter = lineList.iterator();
		String firstLine = iter.next();
		int totalMsgCount = Integer.parseInt(firstLine.split("\\s")[1].split("=")[1]);
		while(iter.hasNext())
		{
			String line = iter.next();
			String[] s = line.split("\\s");
			int count = Integer.parseInt(s[1]);
//			if(s[0].equals("2:Software_Error"))
//				System.out.println();
			fieldValueCombinationCountMap.put(s[0], count);
		}
		return totalMsgCount;
	}
	
	/**
	 * 
	 * @param s: values of the current msg
	 * @param fieldIndex
	 * @param i: the index (target) of the fieldIndex
	 */
	private void analyzeMessage(String[] s, int[] fieldIndex, int i)
	{
		System.out.println("Processing target state "+i+"/"+fieldIndex.length);
		int I = fieldIndex[i];
		String tgtValue = s[I].trim();
		StringBuilder tsb = new StringBuilder();
		String tgt = tsb.append(I).append(":").append(tgtValue).toString();
		
		//0 src item
//		System.out.println("tgt="+tgt);
		Integer keyCount = fieldValueCombinationCountMap.get(tgt);
		if(keyCount==null)
			keyCount=0;
		float prob = ((float)keyCount)/((float)totalMsgCount);
		PostierProbabilityItem ppi0 = new PostierProbabilityItem(tgt, prob, totalMsgCount, confidenceLevel);
		relatedPostProbList.add(ppi0);
		if(prob==1)
		{
			inevitableTgtFVList.add(tgt);
			return;
		}
	
		//1 src item
		for(int j = 0;j<fieldIndex.length;j++)
		{
			if(j!=i)
			{
				int J = fieldIndex[j];
				String srcValue = s[J].trim();
				StringBuilder ssb = new StringBuilder();
				String src = ssb.append(J).append(":").append(srcValue).toString();
				String srcTgt = null;
				if(I<J)
				{
					StringBuilder sb = new StringBuilder(tgt);
					srcTgt = sb.append(",").append(src).toString();
				}
				else
				{
					StringBuilder sb = new StringBuilder(src);
					srcTgt = sb.append(",").append(tgt).toString();
				}
				computePosteriorProb(srcTgt, src,tgt);
			}
		}
		
		//2 src items
		for(int j = 0;j<fieldIndex.length-1;j++)
			for(int k = j+1;k<fieldIndex.length;k++)
			{
				if(i!=j&&i!=k)
				{
					int J = fieldIndex[j];
					String srcValue1 = s[J].trim();
					int K = fieldIndex[k];
					String srcValue2 = s[K].trim();
					StringBuilder ssb = new StringBuilder();
					String src = ssb.append(J).append(":").append(srcValue1).append(",").append(K).append(":").append(srcValue2).toString();
					String srcTgt = null;
					if(I<J)
					{
						StringBuilder sb = new StringBuilder(tgt);
						srcTgt = sb.append(",").append(src).toString();
					}
					else if(J<I&&I<K)
					{
						StringBuilder sb = new StringBuilder();
						sb.append(J).append(":").append(srcValue1)
						.append(",").append(tgt).append(",")
						.append(K).append(":").append(srcValue2);
						srcTgt = sb.toString();
					}
					else //J<K<I
					{
						StringBuilder sb = new StringBuilder(src);
						srcTgt = sb.append(",").append(tgt).toString();
					}
					computePosteriorProb(srcTgt, src,tgt);
				}
			}
		
		//3 src items
		for(int j = 0;j<fieldIndex.length-2;j++)
			for(int k = j+1;k<fieldIndex.length-1;k++)
				for(int p = k+1;p<fieldIndex.length;p++)
				{
					if(i!=j&&i!=k&&i!=p)
					{
						int J = fieldIndex[j];
						String srcValue1 = s[J].trim();
						int K = fieldIndex[k];
						String srcValue2 = s[K].trim();
						int P = fieldIndex[p];
						String srcValue3 = s[P].trim();
						StringBuilder ssb = new StringBuilder();
						String src = ssb.append(J).append(":").append(srcValue1).append(",")
								.append(K).append(":").append(srcValue2).append(",")
								.append(P).append(":").append(srcValue3).toString();
						String srcTgt = null;
						if(I<J)
						{
							StringBuilder sb = new StringBuilder(tgt);
							srcTgt = sb.append(",").append(src).toString();
						}
						else if(J<I&&I<K)
						{
							StringBuilder sb = new StringBuilder();
							sb.append(J).append(":").append(srcValue1).append(",")
							.append(tgt).append(",").append(K).append(":").append(srcValue2).append(",")
							.append(P).append(":").append(srcValue3);
							srcTgt = sb.toString();
						}
						else if(K<I&&I<P)//J<K<I
						{
							StringBuilder sb = new StringBuilder();
							sb.append(J).append(":").append(srcValue1).append(",")
							.append(K).append(":").append(srcValue2).append(",").append(tgt).append(",")
							.append(P).append(":").append(srcValue3);
							srcTgt = sb.toString();
						}
						else //J<K<P<I
						{
							StringBuilder sb = new StringBuilder(src);
							srcTgt = sb.append(",").append(tgt).toString();
						}
						computePosteriorProb(srcTgt, src,tgt);
					}
				}
		//4 src items
		for(int j = 0;j<fieldIndex.length-3;j++)
			for(int k = j+1;k<fieldIndex.length-2;k++)
				for(int p = k+1;p<fieldIndex.length-1;p++)
					for(int q = p+1;q<fieldIndex.length;q++)
					{
						if(i!=j&&i!=k&&i!=p&&i!=q)
						{
							int J = fieldIndex[j];
							String srcValue1 = s[J].trim();
							int K = fieldIndex[k];
							String srcValue2 = s[K].trim();
							int P = fieldIndex[p];
							String srcValue3 = s[P].trim();
							int Q = fieldIndex[q];
							String srcValue4 = s[Q].trim();
							StringBuilder ssb = new StringBuilder();
							String src = ssb.append(J).append(":").append(srcValue1).append(",")
									.append(K).append(":").append(srcValue2).append(",")
									.append(P).append(":").append(srcValue3).append(",")
									.append(Q).append(":").append(srcValue4).toString();
							String srcTgt = null;
							if(I<J)
							{
								StringBuilder sb = new StringBuilder(tgt);
								srcTgt = sb.append(",").append(src).toString();
							}
							else if(J<I&&I<K)
							{
								StringBuilder sb = new StringBuilder();
								sb.append(J).append(":").append(srcValue1).append(",")
								.append(tgt).append(",").append(K).append(":").append(srcValue2).append(",")
								.append(P).append(":").append(srcValue3).append(",")
								.append(Q).append(":").append(srcValue4);
								srcTgt = sb.toString();
							}
							else if(K<I&&I<P)//J<K<I<P
							{
								StringBuilder sb = new StringBuilder();
								sb.append(J).append(":").append(srcValue1).append(",")
								.append(K).append(":").append(srcValue2).append(",").append(tgt).append(",")
								.append(P).append(":").append(srcValue3).append(",")
								.append(Q).append(":").append(srcValue4);
								srcTgt = sb.toString();
							}
							else if(P<I&&I<Q)
							{
								StringBuilder sb = new StringBuilder();
								sb.append(J).append(":").append(srcValue1).append(",")
								.append(K).append(":").append(srcValue2).append(",")
								.append(P).append(":").append(srcValue3).append(",")
								.append(tgt).append(",")
								.append(Q).append(":").append(srcValue4);
								srcTgt = sb.toString();								
							}
							else //J<K<P<I<Q
							{
								StringBuilder sb = new StringBuilder(src);
								srcTgt = sb.append(",").append(tgt).toString();
							}
							computePosteriorProb(srcTgt, src,tgt);
						}
					}
	}			
	
	private void computePosteriorProb(String srcTgt, String src, String tgt)
	{
		//check if the inevitable list are completely included in src (all of the conditions...)
		if(containInevitableProbItem(tgt,src))
			return;
		
		Integer tgtCombProb = fieldValueCombinationCountMap.get(srcTgt);
		if(tgtCombProb==null)
			tgtCombProb = 0;
		Integer srcProb = fieldValueCombinationCountMap.get(src);
		float prob;
		if(srcProb==null)
		{
			prob = -1;
			srcProb = 0;
		}
		else
			prob = tgtCombProb/srcProb;
		PostierProbabilityItem ppi = new PostierProbabilityItem(tgt, prob, (int)srcProb, confidenceLevel);
		String[] s = src.split(",");
		for(int i = 0;i<s.length;i++)
			ppi.addSrcItem(s[i]);
		relatedPostProbList.add(ppi);
		if(prob==1)
		{
			List<String> srcList = inevitableTgtFVToSrcFVMap.get(tgt);
			if(srcList==null)
			{
				srcList = new ArrayList<String>();
				inevitableTgtFVToSrcFVMap.put(tgt, srcList);
			}
			srcList.add(src);
		}
	}
	
	private static boolean containInevitableProbItem(String tgt, String src)
	{
		if(inevitableTgtFVList.contains(tgt))
			return true;
		
		String[] srcItems = src.split(",");
		List<String> srcItemList = ConversionHandler.convertStringArray2StringList(srcItems);
		
		boolean existIneviProb = false;
		List<String> srcInevList = inevitableTgtFVToSrcFVMap.get(tgt);
		
		if(srcInevList == null)
			return false;
		
		Iterator<String> iter = srcInevList.iterator();
		while(iter.hasNext())
		{
			boolean inevitableProb = true;
			String inevProbItems = iter.next();
			String[] ss = inevProbItems.split(",");
			for(int i = 0;i<ss.length;i++)
			{
				if(!srcItemList.contains(ss[i]))
				{
					inevitableProb = false;
					break;
				}
			}
			if(inevitableProb)
			{
				existIneviProb = true;
				break;
			}
		}
		return existIneviProb;
	}
	
	public void analyzeMessage(String line, int[] fieldIndex)
	{
		//String[] s = line.split(",");
		String[] s = RecordSplitter.partition(line);
		
		double initLogTime = System.currentTimeMillis()/1000.0;
		for(int i = 0;i<fieldIndex.length;i++)
		{	
			analyzeMessage(s, fieldIndex, i);
			PVFile.showProgress(initLogTime, i, fieldIndex.length, "");
		}
	}
}
