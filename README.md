# AGORA-DSM

AGORA-DSM is a service-oriented middleware for dynamic, real-time management of heterogeneous geosensors in flood management. The approach is called AGORA - Dynamic Sensor Management (AGORA-DSM) and can be seen as an extension of <a href="http://52north.org/communities/sensorweb/incubation/sensorBus/">Sensor Bus</a>. 

## Instructions

### Pre-requisites:

- <a href="http://git-scm.com/">Git</a>.

- <a href="http://www.oracle.com/technetwork/pt/java/javase/downloads/index.html">Java JDK</a>.

- <a href="https://eclipse.org/">Eclipse</a>.

- <a href="http://www.apache.org/">Apache</a>.

- <a href="http://www.postgresql.org/">PostgreSQL and PGAdmin3</a>.

### Build Parameters

- Clone the project: <code>git clone https://github.com/agora-research-group/AGORA-DSM</code>.

- Open Eclipse, go to File - New Project and choose Java Project Options. Uncheck 'Use default location' and insert the location of the git project.

- Export project to a .war file (install Maven Integration for Eclipse).

- Insert the .war file into the webapps folder of the Apache Tomcat.

### Creating the database

- Open PGAdmin3 and run the script (datamodel) to create the database. The script also insert the catchments into the database. 

### Running the middleware

- After starting the Tomcat Apache, sensor data from <a href="http://www.cemaden.gov.br/">CEMADEN</a>, <a href="https://www.pegelonline.wsv.de/gast/start">PEGELONLINE</a> and configured wireless sensor networks adapters are stored in the database.

## Reporting Bugs

Any problem should be reported to group-agora@googlegroups.com.

For more information on AGORA-DSM, please, visit its main web page at: http://www.agora.icmc.usp.br/site/.
