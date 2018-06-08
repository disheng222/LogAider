#!/bin/bash

basicSchemaFile=/home/shdi/Catalog-project/ALCF-data/RAS/schema/basicSchema.txt
logDir=/home/shdi/Catalog-project/ALCF-data/RAS
extension=csv
fullSchemaDir=/home/shdi/Catalog-project/ALCF-data/RAS/schema/fullSchema

echo java -cp lib/catalog.jar analysis.RAS.ExtractValueTypes4EachField $basicSchemaFile $logDir csv $fullSchemaDir 
java -cp lib/catalog.jar -Xmx48000m analysis.RAS.ExtractValueTypes4EachField $basicSchemaFile $logDir csv $fullSchemaDir 
