#!/bin/bash

fieldListFile=/home/shdi/Catalog-project/ALCF-data/RAS/fieldValueCombination/fieldList.txt
vcCountFile=/home/shdi/Catalog-project/ALCF-data/RAS/fieldValueCombination/vc.count
inputMsgFile=/home/shdi/Catalog-project/ALCF-data/RAS/inputMsg.txt
outputDir=/home/shdi/Catalog-project/ALCF-data/RAS/analyzeMsg90
confidenceLevel=0.90

echo java -cp lib/catalog.jar -Xmx48000m analysis.RAS.ComputePostProbabilityBasedonMsg $fieldListFile $vcCountFile $inputMsgFile $outputDir $confidenceLevel
java -cp lib/catalog.jar -Xmx48000m analysis.RAS.ComputePostProbabilityBasedonMsg $fieldListFile $vcCountFile $inputMsgFile $outputDir $confidenceLevel
