package analysis.spatialcorr.kmeans;

import analysis.spatialcorr.ComputeProbabilityAcrossMidplanes;
import analysis.spatialcorr.GenerateContingencyTableForSigAnalysis;
import analysis.spatialcorr.MidplaneTorusElem;

public class KMeansOpt {
	
	private ComputeProbabilityAcrossMidplanes cpam;
	private int initNumOfSets = 0;
	private String[] input;
	
	public KMeansOpt(int initNumOfSets, String[] input, ComputeProbabilityAcrossMidplanes cpam)
	{
		this.initNumOfSets = initNumOfSets;
		this.input = input;
		this.cpam = cpam;
	}
	
	public KMeansSolution execute_optClustering_optK(int checkTimes, float lambda)
	{
		KMeansSolution optSol = execute_optClustering_fixK(checkTimes);
		while(true)
		{
			float avgCenterDistance = optSol.computeAvgCenterDistance();
			float threshold = avgCenterDistance*lambda;
			boolean merged = optSol.mergeNearbyCenters(threshold);
			if(!merged || optSol.centerList.size()==2)
				break;
			optSol.executeWithCenters();
		}
		return optSol;
		
	}
	
	public KMeansSolution execute_optClustering_fixK(int checkTimes)
	{
		KMeansSolution optSolution = null;
		for(int i = 0;i<checkTimes;i++)
		{
			KMeansSolution kmeans = new KMeansSolution(input, cpam);
			kmeans.execute(initNumOfSets);
			
			if(optSolution==null)
				optSolution = kmeans;
			else
			{
				float oldTotalWCSD = optSolution.getTotalWCSD();
				float newTotalWCSD = kmeans.getTotalWCSD();
				if(oldTotalWCSD>newTotalWCSD)
					optSolution = kmeans;
			}
		}
		return optSolution;
	}
	
	public static void main(String[] args)
	{
		MidplaneTorusElem.equalTypeAllowDuplication = true;
		String kmeansSolType = "optK"; //fixK or optK
		String filePath = "/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/FilterAndClassify/00090210.ori";
		
		if(args.length<4)
		{
			System.out.println("java KMeansOpt [kmeansSolType (fixK or optK)] [initNumOfSets] [allowDuplicate?] [inputFilePath]");
			System.out.println("java KMeansOpt fixK 10 true "+filePath);
			System.exit(0);
		}
		
		kmeansSolType = args[0];
		int initNumOfSets = Integer.parseInt(args[1]);
		MidplaneTorusElem.equalTypeAllowDuplication = Boolean.parseBoolean(args[2]);
		filePath = args[3];
		
		String input = GenerateContingencyTableForSigAnalysis.getMidplanes(filePath);
		
		System.out.println(input);
		
		String[] midplanes = input.split("\\s");
		
		System.out.println("# midplanes: "+midplanes.length);
				
		ComputeProbabilityAcrossMidplanes cpam = new ComputeProbabilityAcrossMidplanes(3, 16, 2);
		KMeansOpt kmeans = new KMeansOpt(initNumOfSets, midplanes, cpam);
		
	
		if(kmeansSolType.equalsIgnoreCase("fixK"))
		{
			KMeansSolution sol = kmeans.execute_optClustering_fixK(10);
			
			//sol.printKMeansSets();
			
			int[][] displayMatrix = new int[6][16];
			sol.displayInMatrix(displayMatrix);
			sol.printMatrix(displayMatrix, filePath+".fx"+MidplaneTorusElem.equalTypeAllowDuplication);			
		}
		else
		{
			KMeansSolution sol = kmeans.execute_optClustering_optK(10, 0.8f);
			
			//sol.printKMeansSets();
			
			int[][] displayMatrix = new int[6][16];
			sol.displayInMatrix(displayMatrix);
			sol.printMatrix(displayMatrix, filePath+".ox"+MidplaneTorusElem.equalTypeAllowDuplication);
		}

		System.out.println("done.");
	}
}
