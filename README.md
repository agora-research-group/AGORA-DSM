# AGORA-DSM

AGORA-DSM is a service-oriented middleware for dynamic, real-time management of heterogeneous geosensors in flood management. The approach is called AGORA - Dynamic Sensor Management (AGORA-DSM) and can be seen as an extension of <a href="http://52north.org/communities/sensorweb/incubation/sensorBus/">Sensor Bus</a>. 

## Instructions

### Pre-requisites:

Follow its respectives guides to complete the installation:

- <a href="http://git-scm.com/">Git</a>.

- <a href="http://www.oracle.com/technetwork/pt/java/javase/downloads/index.html">Java JDK</a>.

- <a href="https://eclipse.org/">Eclipse</a>.

- <a href="http://www.apache.org/">Apache</a>.

- <a href="http://www.postgresql.org/">PostgreSQL</a>.

- <a href="http://52north.org/communities/sensorweb/sos/">SOS</a>.

### Build Parameters

- Clone the project: <code>git clone https://github.com/agora-research-group/AGORA-DSM</code>.

- Open Eclipse, go to File - New Project and choose Java Project Options. Uncheck 'Use default location' and insert the location of the git project.

- Export the Java project into a .war file (need to install Maven Integration for Eclipse) inside the webapps folder of the Apache Tomcat.

### Creating the database

- Open PGAdmin3 and run the script (datamodel) to create the database, where the data are stored.

### Running the middleware

- After starting the Tomcat Apache, sensor data from <a href="http://www.cemaden.gov.br/">CEMADEN</a>, <a href="https://www.pegelonline.wsv.de/gast/start">PEGELONLINE</a> and configured wireless sensor networks adapters are stored in the SOS repository using SOS services.

## Reporting Bugs

Any problem should be reported to group-agora@googlegroups.com.

For more information on AGORA-DSM, please, visit its main web page at: http://www.agora.icmc.usp.br/site/.
