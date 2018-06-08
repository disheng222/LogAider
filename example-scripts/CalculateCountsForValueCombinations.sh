#!/bin/bash

basicSchemaFile=/home/shdi/Catalog-project/ALCF-data/RAS/schema/basicSchema.txt
fullSchemaDir=/home/shdi/Catalog-project/ALCF-data/RAS/schema/fullSchema/withCount
fullSchemaExtension=fsc
logDir=/home/shdi/Catalog-project/ALCF-data/RAS
extension=csv
outputFieldValueCombinationDir=/home/shdi/Catalog-project/ALCF-data/RAS/fieldValueCombination
keyFields="CATEGORY COMPONENT CPU MSG_ID SEVERITY"

java -cp lib/catalog.jar -Xmx48000m analysis.RAS.CalculateCountsForValueCombinations $basicSchemaFile $fullSchemaDir $fullSchemaExtension $logDir $extension $outputFieldValueCombinationDir $keyFields
