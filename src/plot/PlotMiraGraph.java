package plot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import plot.controlElement.MidPlane;
import plot.controlElement.MiraCluster;
import plot.controlElement.Node;
import plot.controlElement.Rack;
import plot.visionController.LayoutController;
import plot.visionController.LayoutSchema;
import util.PVFile;

public class PlotMiraGraph {

	public static int width;
	public static int height;
	
	public static void main(String[] args)
	{	
		
		if(args.length<6)
		{
			System.out.println("Usage: java PlotMiraGraph [gnuplotTemplateFile] [distributionDir] [extension] [layoutSchemaFile] [maxLevel] [outputFileName]");
			System.out.println("Example: java PlotMiraGraph /home/fti/Catalog-project/miralog/gnuplot/temp-layout.p /home/fti/Catalog-project/miralog/errLocDistribution/fatal err "
					+ "/home/fti/Catalog-project/miralog/gnuplot/computeRackLayoutSchema.txt 2 /home/fti/Catalog-project/miralog/gnuplot/errdis_fatal_compute.p");
			System.out.println("Example: java PlotMiraGraph /home/fti/Catalog-project/miralog/gnuplot/temp-layout.p /home/fti/Catalog-project/miralog/errLocDistribution/fatal err "
					+ "/home/fti/Catalog-project/miralog/gnuplot/ioRackLayoutSchema.txt 2 /home/fti/Catalog-project/miralog/gnuplot/errDis_fatal_io.p");
			System.out.println("Example: java PlotMiraGraph /home/fti/Catalog-project/miralog/gnuplot/temp-layout.p /home/fti/Catalog-project/miralog/errLocDistribution/FATAL_MSGID_00062001 err /home/fti/Catalog-project/miralog/gnuplot/computeRackLayoutSchema.txt 2 /home/fti/Catalog-project/miralog/errLocDistribution/FATAL_MSGID_00062001/gnuplot/errdis_fatal_compute.p");
			System.out.println("Exmaple: java PlotMiraGraph /home/fti/Catalog-project/miralog/gnuplot/temp-layout.p /home/fti/Catalog-project/miralog/Adam-job-log/err err /home/fti/Catalog-project/miralog/gnuplot/computeRackLayoutSchema.txt 2 /home/fti/Catalog-project/miralog/Adam-job-log/err/dis_compute.p");
			System.out.println("Example: java PlotMiraGraph /home/fti/Catalog-project/miralog/gnuplot/temp-layout.p /home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/errLocDistribution err /home/fti/Catalog-project/miralog/gnuplot/computeRackLayoutSchema.txt 2 /home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/gnuplot/errLocDistribution/dis_compute.p");
			System.exit(0);
		}
		
		String gnuplotTemplateFile = args[0];
		String distributionDir = args[1];
		String extension = args[2];
		String layoutSchemaFile = args[3];
		LayoutController.maxLevel = Integer.parseInt(args[4]);
		String outputFilePath = args[5];
		
		LayoutSchema.loadLayoutSchema(layoutSchemaFile);
		
		List<String> plotCodeList_ = PVFile.readFile(gnuplotTemplateFile);
		loadWidthAndHeight(plotCodeList_);
		
		List<String> plotCodeList = new ArrayList<String>();
		Iterator<String> iter_ = plotCodeList_.iterator();
		
		String[] ss = outputFilePath.split("/");
		String workDir = "";
		for(int i = 0;i<ss.length-1;i++)
			workDir+="/"+ss[i];
		String epsFileName = ss[ss.length-1].split("\\.")[0];

		String date = epsFileName.replace("ras_event_mira_", "");
		String framePath, frameName;
		String[] s_ = date.split("_");
		if(s_.length==3)
		{
			int year = Integer.parseInt(s_[0]);
			int month = Integer.parseInt(s_[1]);
			int day = Integer.parseInt(s_[2]);
			frameName = "ras_event_mira_"+year+"_day"+computeDays(year, month, day);			
			framePath = workDir+"/"+frameName+".p";
		}
		else
		{
			frameName = epsFileName;
			framePath = outputFilePath;
		}
		
		while(iter_.hasNext())
		{
			String line = iter_.next();
			if(line.contains("EPSFILENAME"))
			{
				line = line.replace("EPSFILENAME", frameName);
			}
			plotCodeList.add(line);
		}
				
		System.out.println("Constructing objects for mira cluster.....");

		LayoutController clusterLayout = LayoutSchema.layoutTypes[0].loadLayout(width, height);
		int rackNum = clusterLayout.getCount();
		
		MiraCluster mc = new MiraCluster(rackNum, clusterLayout);
		
		//load distribution data into the miraCluster objects....
		System.out.println("Start loading data from distributionDir to the miraCluster objects....");
		mc.loadData(distributionDir, extension);
		
		System.out.println("Generating gnuplot codes related to architecture....");
		List<float[]> coordList0 = new ArrayList<float[]>();
		List<String> clusterBoxCodes = clusterLayout.buildVisionCode(0, 225, clusterLayout.getFullName(), coordList0,
				0, MiraCluster.maxRate[1], mc.getStates());
		plotCodeList.addAll(clusterBoxCodes);
		
		Rack[] rack = mc.getRack();
		Iterator<float[]> iter0 = coordList0.iterator();
		for(int i = 0;i<rack.length;i++)
		{
			List<float[]> coordList = new ArrayList<float[]>();
			
			float[] cd = iter0.next();
			float cdX = cd[0];
			float cdY = cd[1];
			
			Rack r = rack[i];
			LayoutController rl = r.getLayout();
			List<String> rackBoxCodes = rl.buildVisionCode(cdX, cdY, r.getLocalID(), coordList, 
					0, MiraCluster.maxRate[2], r.getStates());
			plotCodeList.add("#"+r.getGlobalID());
			plotCodeList.addAll(rackBoxCodes);
			
			if(LayoutController.maxLevel<=1)
				continue;
			
			MidPlane[] mp = r.getMp();
			Iterator<float[]> iter = coordList.iterator();
			for(int j = 0;j<mp.length;j++)
			{
				float[] cellCoord = iter.next();
				float cellX = cellCoord[0];
				float cellY = cellCoord[1];
				MidPlane m = mp[j];
				LayoutController ml = m.getLayout();
				
				List<float[]> coordList2 = new ArrayList<float[]>();
				List<String> mpBoxCodes = ml.buildVisionCode(cellX, cellY, m.getLocalID(), coordList2, 
						0, MiraCluster.maxRate[3], m.getStates());
				plotCodeList.addAll(mpBoxCodes);
				
				if(LayoutController.maxLevel<=2)
					continue;
				
				Node[] node = m.getNode();
				Iterator<float[]> iter2 = coordList2.iterator();
				for(int k = 0;k<node.length;k++)
				{
					float[] cellCoord2 = iter2.next();
					float cellX2 = cellCoord2[0];
					float cellY2 = cellCoord2[1];
					Node n = node[k];
					LayoutController nl = n.getLayout();
					
					List<float[]> coordList3 = new ArrayList<float[]>();
					List<String> nodeBoxCodes = nl.buildVisionCode(cellX2, cellY2, n.getLocalID(), coordList3, 
							0, MiraCluster.maxRate[4], n.getStates());
					plotCodeList.addAll(nodeBoxCodes);
					
					if(LayoutController.maxLevel <= 3)
						continue;
					
					//TODO
				}
			}
		}

		//plot color key
		System.out.println("Plotting color key....");
		int leftEdge = 1500;

		List<String> keyList = LayoutController.generateKey(leftEdge, LayoutSchema.layoutTypes, date.replaceAll("_","."));
		plotCodeList.addAll(keyList);
		
		plotCodeList.add("plot 'DATA'");
		
		PVFile.print2File(plotCodeList, framePath);
		System.out.println("output file: "+framePath);
		//System.out.println("Run \"gnuplot "+framePath+"\" to generate eps file.");
		
		//TODO generate DATA file....
		List<String> dList = new ArrayList<String>();
		dList.add("0 -0.1");
		String[] sss = outputFilePath.split("/");
		String DATAPATH = "";
		for(int i= 0;i<sss.length-1;i++)
			DATAPATH += "/"+sss[i];
		PVFile.print2File(dList, DATAPATH+"/DATA");
		
		System.out.println("done.");
	}
	
	private static void loadWidthAndHeight(List<String> lineList)
	{
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next().trim();
			if(line.startsWith("set xrange"))
			{
				String xrange = line.split("\\s")[2].replace("]", "");
				width = Integer.parseInt(xrange.split(":")[1]);
			}
			if(line.startsWith("set yrange"))
			{
				String yrange = line.split("\\s")[2].replace("]", "");
				height = Integer.parseInt(yrange.split(":")[1])-225;
			}
		}
	}
	
	static String computeDays(int Year, int Month, int Day)
	{
		int Total = 0, Leap = 0;
		 switch(Month)
		 {
		  case 1:Total=0;break;
		  case 2:Total=31;break;
		  case 3:Total=59;break;
		  case 4:Total=90;break;
		  case 5:Total=120;break;
		  case 6:Total=151;break;
		  case 7:Total=181;break;
		  case 8:Total=212;break;
		  case 9:Total=242;break;
		  case 10:Total=273;break;
		  case 11:Total=303;break;
		  case 12:Total=334;break;
		  default:System.out.println("error");break;
		 }
		 Total=Total+Day;
		 if( Month > 2 && ( (Year%4 == 0 && Year%100 != 0) || (Year%400) == 0 ) )
		 {
		  Leap=1; 
		 }
		 Total=Total+Leap;
		 return PVFile.df3.format(Total);
	}
}
