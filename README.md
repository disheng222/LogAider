# LogAider
A tool for mining potential correlations in HPC system logs

This code corresponds to the paper published in CCGrid2017: "LogAider - A tool for mining potential correlations in HPC system logs". 

It contains different analysis algorithms, including across-field correlation analysis, temporal correlation analysis, and spatial correlation analysis (k-means). 

The algorithms are designed and implemented based on IBM-BlueGQ - Argonne MIRA RAS log (available upon request). 

In order to reproduce the results presented in the paper, the following executables need to be run. 

1. Filtering: 
-1. RAS: 
	(1) analysis.RAS.CollectWarnFatalMessages: Extract all warn and fatal messages from original log
		batch: batchCollectWarnFatalMessages.sh
	(2) filter.ClassifyLogBasedonMessageID: ClassifyLogBasedonMessageID
	(3) filter.TemporalSpatialFilter: filter the messags based on -t, -s, or -ts
-2. Cobalt:
	(1) analysis.Job.CollectErrorMessages: Extract all error messages (with non-exit code)
		generate totalFatalMsg.fat
	(2) CalculateFailuresBasedonUsers.java: 
	(3) CalculateFailuresBasedonScaleAndLength:

2. Across-Field correlation: 
-1. RAS:
	(1) analysis.RAS.ExtractValueTypes4EachField : Generate fullSchema directory.
	(2) analysis.RAS.GenerateStateFeatures (on blues): gererate state features.
	(3) analysis.RAS.BuildFieldValueCombination: build fieldValueCombination, i.e., the dir fieldValueCombination
		<1> component vs. severity
		<2> component vs. category
		<3> category vs. severity
	(4) analysis.RAS.CalculateCountsForValueCombinations:  Generate vc.count file in the dir fieldValueCombination
	(5) analysis.RAS.ComputePostProbabilityBasedonMsg: Generate analysis for inputMsg.txt 
	(6) analysis.RAS.ComputeErrorDistribution : Generate errLocDistribution directory
 
-2. Cobalt: (Note that the fields of one-year log are different from the three-months log)
	(1) analysis.Job.ExtractValueTypes4EachField : Generate fullSchema directory.
	(2) analysis.Job.GenerateStateFeatures : generate state features 
	(3) analysis.Job.CalculateCountsForValueCombinations (on blues):  Generate vc.count file in the dir fieldValueCombination
	(4) analysis.Job.ComputePostProbabilityBasedonMsg : Generate analysis for inputMsg.txt
	(5) analysis.Job.ComputeJobMessageCounts: Generate error distribution (This class is for generating/plotting the location distribution in the MIRA graph)
		generate locDistribution/err 

3. failure rate of components (coarse and fine granularity)
-1. RAS: 
	(1) filter.Summarize1: Generate fatal-msg-count.txt, and monthly errors
	(2) filter.Summarize2: Generate fatal-msg-count.txt.cat (Compute the distribution of categories based on messages)
	(3) filter.Summarize3: Generate fatal-msg-count.txt.cmp (Compute the distribution of components based on messages)
	(4) filter.Summarize4: Generate fatal-locationKey-count.txt
-2. Job: 
	(1) analysis.Job.SearchJobswithBreakWallClockFailure : Generate lengthAnalysis directory 

4. Plot error distribution (Preliminary: 2-1(6) or 2-2(5))
	(1) plot.PlotMiraGraph: generate gnuplot/errLocDistribution/dis_compute.p

5. Plot figures for state features and prior distribution
	(1) tools.GenerateGnuPlotFiles_TablePriorProb (on blues)
	(2) tools.GenerateGnuPlotFiles_TableFeatureState (on blues) 
 
6. Study temporal correlation
	(1) analysis.inbetween.RAS_Job ( on blues) : Generate temporal correlation between RAS and Job (from RAS to Job) 
	(2) analysis.inbetween.RAS_Job_Analysis (on blues) : Generate statistical analysis for temporal correlation between RAS and Job.

7. Monthly Results
	(1) analysis.RAS.ComputeErrorDistribution : use 'separate' mode to get monthly results
	(2) filter.Summarize_MonthlyFailureRate : Generate monthly data results for category and component 

8. Memory Analysis
	(1) DDR.warn and DDR.fatal contain the WARN and FATAL respectively.
	(2) analysis.RAS.CalculateMemoryErrorInfo: Temporal analysis of DDR correctable errors (monthly, daily, etc.)
	(3) analysis.RAS.CalculateSpatialMemError:
 
9. ComputeDailyCount: analysis.RAS.ComputeDailyFilteredCount

10. Analyze the error propagation: 
	(1) analysis.RAS.ComputeTmporalErrPropagation: Analyze the error propagation (with the same type): if a fatal event happens, it will probably happen again within x hours?

10. Analyze Spatial-correlation
	(1) Batch-Generate the spatial-correlation graph (/home/fti/Catalog-project/miralog/one-year-data/ALCF-Data/RAS/FilterAndClassify/batch-xxx.sh)
	(2) ChiSquared Sygnificance Test: analysis.spatialcorr.GenerateContingencyTableForSigAnalysis then analysis.significance.ChiSquareSingleTest
	(3) KMeans Clustering: analysis.spatialcorr.kmeans.KMeansSolution (2 versions of outputs) and analysis.spatialcorr.kmeans.KMeansOpt (4 versions of outputs)  
	(4) plot.PlotKMeansMidplanes: input (kmeans clustering matrix - output of KMeansSolution or KMeansOpt); output (gnuplot file)

