#!/bin/bash

RAS_log=/home/shdi/Catalog-project/ALCF-data/RAS
RAS_EXT=csv
Job_log=/home/shdi/Catalog-project/ALCF-data/cobalt
Job_EXT=csv
outputFile=/home/shdi/Catalog-project/ALCF-data/RAS-Job.txt


echo java -cp lib/catalog.jar -Xmx48000m analysis.inbetween.RAS_Job $RAS_log $RAS_EXT $Job_log $Job_EXT $outputFile
java -cp lib/catalog.jar -Xmx48000m analysis.inbetween.RAS_Job $RAS_log $RAS_EXT $Job_log $Job_EXT $outputFile
