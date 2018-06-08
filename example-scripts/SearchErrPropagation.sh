#!/bin/bash

shortFilterPeriod=1800
longFilterPeriod=43200
echo java -Xmx40000m -cp lib/catalog.jar analysis.temporalcorr.SearchErrorPropagation /lcrc/project/radix/shdi/Catalog-5-years-data/RASLog/FilterAndClassify/allEvents.txt $shortFilterPeriod $longFilterPeriod /lcrc/project/radix/shdi/Catalog-5-years-data/RASLog/keyIndexClass_1.conf
java -Xmx40000m -cp lib/catalog.jar analysis.temporalcorr.SearchErrorPropagation /lcrc/project/radix/shdi/Catalog-5-years-data/RASLog/FilterAndClassify/allEvents.txt $shortFilterPeriod $longFilterPeriod /lcrc/project/radix/shdi/Catalog-5-years-data/RASLog/keyIndexClass_2.conf
