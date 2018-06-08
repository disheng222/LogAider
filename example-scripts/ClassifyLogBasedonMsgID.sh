#!/bin/bash

echo java -Xmx40000m -cp lib/catalog.jar filter.ClassifyLogBasedonMessageID /lcrc/project/radix/shdi/Catalog-5-years-data/RASLog/totalFatalMsg.fat /lcrc/project/radix/shdi/Catalog-5-years-data/RASLog/FilterAndClassify
java -Xmx40000m -cp lib/catalog.jar filter.ClassifyLogBasedonMessageID /lcrc/project/radix/shdi/Catalog-5-years-data/RASLog/totalFatalMsg.fat /lcrc/project/radix/shdi/Catalog-5-years-data/RASLog/FilterAndClassify
