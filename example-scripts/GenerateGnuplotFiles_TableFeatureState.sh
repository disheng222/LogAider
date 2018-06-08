#!/bin/bash

targetGnuplotDir=/home/shdi/Catalog-project/ALCF-data/RAS/gnuplot
tempFile=/home/shdi/Catalog-project/ALCF-data/RAS/gnuplot/temp-featurestate.p
featureStateDir=/home/shdi/Catalog-project/ALCF-data/RAS/featureState
extension=pr

echo java -cp lib/catalog.jar -Xmx48000m tools.GenerateGnuPlotFiles_TableFeatureState $targetGnuplotDir $tempFile $featureStateDir $extension
java -cp lib/catalog.jar -Xmx52000m tools.GenerateGnuPlotFiles_TableFeatureState $targetGnuplotDir $tempFile $featureStateDir $extension
