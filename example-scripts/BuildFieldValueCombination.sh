#!/bin/bash

basicSchemaFile=/home/shdi/Catalog-project/ALCF-data/RAS/schema/basicSchema.txt
fullSchemaDir=/home/shdi/Catalog-project/ALCF-data/RAS/schema/fullSchema/withCount
fullSchemaExtension=fsc
featureStateDir=/home/shdi/Catalog-project/ALCF-data/RAS/featureState
extension=pr
outputFieldValueCombinationDir=/home/shdi/Catalog-project/ALCF-data/RAS/fieldValueCombination
keyFields="CATEGORY COMPONENT CPU MSG_ID SEVERITY"

maxElementCount=5

java -cp lib/catalog.jar -Xmx48000m analysis.RAS.BuildFieldValueCombination $maxElementCount $basicSchemaFile $fullSchemaDir $fullSchemaExtension $featureStateDir $extension $outputFieldValueCombinationDir $keyFields
