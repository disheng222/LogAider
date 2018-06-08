#!/bin/bash

basicSchemaFile=/home/shdi/Catalog-project/ALCF-data/RAS/schema/basicSchema.txt
fullSchemaDir=/home/shdi/Catalog-project/ALCF-data/RAS/schema/fullSchema/withCount
fullSchemaExtension=fsc
logDir=/home/shdi/Catalog-project/ALCF-data/RAS
extension=csv
outputStateFeatureDir=/home/shdi/Catalog-project/ALCF-data/RAS/featureState
keyFields="CATEGORY COMPONENT CPU MSG_ID SEVERITY"

echo java -cp lib/catalog.jar -Xmx48000m analysis.RAS.GenerateStateFeatures $basicSchemaFile $fullSchemaDir $fullSchemaExtension $logDir $extension $outputStateFeatureDir $keyFields
java -cp lib/catalog.jar -Xmx48000m analysis.RAS.GenerateStateFeatures $basicSchemaFile $fullSchemaDir $fullSchemaExtension $logDir $extension $outputStateFeatureDir $keyFields
