#!/bin/bash

basicSchemaFile=/home/shdi/Catalog-project/ALCF-data/cobalt/basicSchema/basicSchema.txt
fullSchemaDir=/home/shdi/Catalog-project/ALCF-data/cobalt/fullSchema/withCount
fullSchemaExtension=fsc
logDir=/home/shdi/Catalog-project/ALCF-data/cobalt
extension=csv
outputFieldValueCombinationDir=/home/shdi/Catalog-project/ALCF-data/cobalt/fieldValueCombination
keyFields="WALLTIME_SECONDS REQUESTED_CORES USED_CORES REQUESTED_NODES USED_NODES COBALT_PROJECT_NAME_GENID COBALT_USER_NAME_GENID MACHINE_PARTITION EXIT_CODE QUEUE_GENID MODE DELETED_BY_GENID PROJECT_NAME_GENID"

java -cp lib/catalog.jar -Xmx48000m analysis.Job.CalculateCountsForValueCombinations $basicSchemaFile $fullSchemaDir $fullSchemaExtension $logDir $extension $outputFieldValueCombinationDir $keyFields
