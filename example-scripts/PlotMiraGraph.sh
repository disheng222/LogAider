#!/bin/bash

errLogDir=/home/shdi/Catalog-project/ALCF-data/RAS/errLocDistribution/daily
templayoutDir=/home/shdi/Catalog-project/ALCF-data/RAS/gnuplot
for file in `cd $errLogDir;ls | grep ras_event_mira`
do
	echo Processing $errLogDir/$file
	java -Xmx32000m -cp lib/catalog.jar plot.PlotMiraGraph $templayoutDir/temp-layout.p $errLogDir/$file err $templayoutDir/computeRackLayoutSchema.txt 2 $errLogDir/$file.p
done
