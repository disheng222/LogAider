LogAider
========

##Description
Today’s large-scale supercomputers are producing
a huge amount of log data. Exploring various potential correla-
tions of fatal events is crucial for understanding their causality
and improving the working efficiency for system administra-
tors. To this end, we developed a toolkit, named LogAider,
that can reveal three types of potential correlations: across-
field, spatial, and temporal. Across-field correlation refers to
the statistical correlation across fields within a log or across
multiple logs based on probabilistic analysis. For analyzing
the spatial correlation of events, we developed a generic, easy-
to-use visualizer that can view any events queried by users
on a system machine graph. LogAider can also mine spatial
correlations by an optimized K-meaning clustering algorithm
over a Torus network topology. It is also able to disclose the
temporal correlations (or error propagations) over a certain
period inside a log or across multiple logs, based on an effective
similarity analysis strategy. 

This code corresponds to the paper published in CCGrid2017: "LogAider - A tool for mining potential correlations in HPC system logs".

##Dependency

LogAider is coded in Java, so you need to install JDK 1.8+
(checking the version of JDK on your machine using 'java --version')

After installing JDK, you are ready to use LogAider by running the corresponding bash scripts or the java programs.

##How to use LogAider

LogAider provides a rich set of analysis functions as listed below, for mining the correlations of events in a Reliability, Availability and Serviablity (RAS) log.
In the following, we use the RAS log of MIRA supercomputer (BlueG/Q system) as an example. We provide flexible schema files for users to edit, in order to adapt to other systems. 

- **Extract all warn and fatal messages from original log**
> *script*: CollectWarnFatalMessages.sh  
*source code*: analysis.RAS.CollectWarnFatalMessages.java  
*Usage*: java CollectWarnFatalMessags [schemaPath] [severity_index] [file or directory: -f/-d] [logDir/logFile] [log_extension]  
*Example*: Example: java CollectWarnFatalMessags /home/fti/Catalog-project/miralog/schema/basicSchema.txt 4 -d /home/fti/Catalog-project/miralog csv  

*schema file* is used to specify the format of the log data. For example, in MIRA RAS log, the basicScheme.txt looks like this:  

```
#Column 		name    schema  	Data type name  Length
RECI			DSYSIBM INTEGER 	4       0       No
MSG_ID			SYSIBM  CHARACTER       8       0       Yes  
CATEGORY		SYSIBM  CHARACTER       16      0       Yes  
COMPONENT		SYSIBM  CHARACTER       16      0       Yes  
SEVERITY		SYSIBM  CHARACTER       8       0       Yes  
EVENT_TIME		SYSIBM  TIMESTAMP       10      6       No  
JOBID			SYSIBM  BIGINT  	8       0       Yes  
BLOCK			SYSIBM  CHARACTER       32      0       Yes  
LOCATION		SYSIBM  CHARACTER       64      0       Yes  
SERIALNUMBER		SYSIBM  CHARACTER       19      0       Yes  
CPU			SYSIBM  INTEGER 	4       0       Yes  
COUNT			SYSIBM  INTEGER 	4       0       Yes  
CTLACTION		SYSIBM  VARCHAR 	256     0       Yes
MESSAGE			SYSIBM  VARCHAR 	1024    0       Yes
DIAGS			SYSIBM  CHARACTER       1       0       No
QUALIFIER		SYSIBM  CHARACTER       32      0       Yes
```

In the above example, /home/fti/Catalog-project/miralog is the directory containing the original RAS log data files, whose extensions are csv.  
Some examples are showns below:  
```
[sdi@sdihost RasLog]$ ls
ANL-ALCF-RE-MIRA_20130409_20131231.csv  ANL-ALCF-RE-MIRA_20140101_20141231.csv  ANL-ALCF-RE-MIRA_20150101_20151231.csv  ANL-ALCF-RE-MIRA_20160101_20161231.csv  ANL-ALCF-RE-MIRA_20170101_20170831.csv  
[sdi@sdihost RasLog]$ cat ANL-ALCF-RE-MIRA_20130409_20131231.csv  
"RECID","MSG_ID","CATEGORY","COMPONENT","SEVERITY","EVENT_TIME","JOBID","BLOCK","LOCATION","SERIALNUMBER","CPU","COUNT","CTLACTION","MESSAGE","DIAGS","QUALIFIER","MACHINE_NAME"
13113415,"0008003C","Software_Error","FIRMWARE","INFO","2013-04-01 00:00:37.072514",180219,"MIR-40000-737F1-4096","R0D-M0-N05-J06","74Y9656YL1CK135701C",65,"","","DDR0 PHY was recalibrated(0):  taken = 196 usec. Previous cal was 209.5 seconds ago.","F","655148938                       ","mira"
13113416,"00080034","DDR","FIRMWARE","INFO","2013-04-01 00:00:42.804000",180268,"MIR-44400-77771-1024","R1C-M0-N12-J14","00E5870YL1FB227033C",8,8713,"","DDR  Correctable Error Summary : count=8713 MCFIR error status:  [POWERBUS_WRITE_BUFFER_CE] This bit is set when a PBUS ECC CE is detected on a PBus write buffer read op;","F","50419698                        ","mira"
13113417,"0008002F","BQC","FIRMWARE","INFO","2013-04-01 00:00:42.806127",180268,"MIR-44400-77771-1024","R1C-M0-N12-J14","00E5870YL1FB227033C",8,27355,"","L1P Correctable Error Summary : count=27355 cores=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15 L1P_ESR : [ERR_RELOAD_ECC_X2] correctable reload data ECC error;","F","50419698                        ","mira"
13113418,"00080030","BQC","FIRMWARE","INFO","2013-04-01 00:00:42.807463",180268,"MIR-44400-77771-1024","R1C-M0-N12-J14","00E5870YL1FB227033C",8,400,"","L2 Array Correctable Error Summary : count=400 slices=10 L2_INTERRUPT_STATE error status:  [EDR_CE] Coherence array correctable error;","F","50419698                        ","mira"
13113419,"0006100E","Optical_Module","MMCS","WARN","2013-04-01 00:00:51.477275","","","R21-M0-N09-O13","","","","","Health Check detected an abnormal condition for the optical module at location R21-M0-N09-O13.  The condition is related to POWER 11 .","F","","mira"

```








