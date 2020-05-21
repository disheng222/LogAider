The filtered-event files contain four years (2014-2017) of the duplication-filtered messages based on spatio-temporal filter for Argonne MIRA log (based on RAS log).

The data files can be found here: http://www.mcs.anl.gov/~shdi/download/filtered-events.tar.gz

The description to each column:
1. event ID (I defined this thing, because each event involves one or multiple msg records)
2. MSG ID
3. RECID
4. SEVERITY
5. CATEGORY
6. COMPONENT
7. EVENT_TIME
8. the number of message records involved with this event
9. the type of the component: e.g., the nodes are like R02-M1-N07-J27, so the x3 is RMNJ. RMNJ means a compute node's core. 
10. LOCATION
11. block size (please check my paper or IBM guide to see the detailed definition; I remember it refers to the number of nodes or cores? not sure)
