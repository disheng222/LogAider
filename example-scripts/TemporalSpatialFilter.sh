#!/bin/bash

echo java -Xmx20000m -cp lib/catalog.jar filter.TemporalSpatialFilter -ts /lcrc/project/radix/shdi/Catalog-5-years-data/RASLog/FilterAndClassify ori /lcrc/project/radix/shdi/Catalog-5-years-data/schema/maintenance_periods.txt /lcrc/project/radix/shdi/Catalog-5-years-data/RASLog/FilterAndClassify
java -Xmx20000m -cp lib/catalog.jar filter.TemporalSpatialFilter -ts /lcrc/project/radix/shdi/Catalog-5-years-data/RASLog/FilterAndClassify ori /lcrc/project/radix/shdi/Catalog-5-years-data/schema/maintenance_periods.txt /lcrc/project/radix/shdi/Catalog-5-years-data/RASLog/FilterAndClassify
