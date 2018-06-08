#!/bin/bash

targetGnuplotDir=/home/shdi/Catalog-project/ALCF-data/RAS/gnuplot
tempFile=/home/shdi/Catalog-project/ALCF-data/RAS/gnuplot/temp.p
fullSchema=/home/shdi/Catalog-project/ALCF-data/RAS/schema/fullSchema/withRatio
extension=fsr

echo java -cp lib/catalog.jar -Xmx48000m tools.GenerateGnuPlotFiles_TablePriorProb $targetGnuplotDir $tempFile $fullSchema $extension
java -cp lib/catalog.jar -Xmx48000m tools.GenerateGnuPlotFiles_TablePriorProb $targetGnuplotDir $tempFile $fullSchema $extension
