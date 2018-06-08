#!/bin/bash

echo java -Xmx50000m -cp lib/catalog.jar analysis.RAS.CollectWarnFatalMessages /lcrc/project/radix/shdi/Catalog-5-years-data/schema/basicSchema.txt 4 -d /lcrc/project/radix/shdi/Catalog-5-years-data/RASLog csv
java -Xmx55000m -cp lib/catalog.jar analysis.RAS.CollectWarnFatalMessages /lcrc/project/radix/shdi/Catalog-5-years-data/schema/basicSchema.txt 4 -d /lcrc/project/radix/shdi/Catalog-5-years-data/RASLog csv
