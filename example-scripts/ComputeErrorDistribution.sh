#!/bin/bash

logDir=/home/shdi/Catalog-project/ALCF-data/RAS
java -Xmx32000m -cp lib/catalog.jar analysis.RAS.ComputeErrorDistribution 4 FATAL $logDir csv 8 - $logDir/errLocDistribution separate2
