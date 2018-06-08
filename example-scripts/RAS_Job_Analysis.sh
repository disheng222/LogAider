#!/bin/bash

jobAnalysisSchema=/home/shdi/Catalog-project/ALCF-data/cobalt/basicSchema/analysisSchema.txt
RASJobFile=/home/shdi/Catalog-project/ALCF-data/RAS-Job.txt
outputDir=/home/shdi/Catalog-project/ALCF-data/RAS-Job-analysis

echo java -cp lib/catalog.jar -Xmx48000m analysis.inbetween.RAS_Job_Analysis $jobAnalysisSchema $RASJobFile $outputDir
java -cp lib/catalog.jar -Xmx48000m analysis.inbetween.RAS_Job_Analysis $jobAnalysisSchema $RASJobFile $outputDir
