# AGORA-DSM

AGORA-DSM is a service-oriented middleware for dynamic, real-time management of heterogeneous geosensors in flood management. The approach is called AGORA - Dynamic Sensor Management (AGORA-DSM) and can be seen as an extension of <a href="http://52north.org/communities/sensorweb/incubation/sensorBus/">Sensor Bus</a>. 

## Pre-requisites:

Follow its respectives guides to complete the installation:

- <a href="http://git-scm.com/">Git</a>.

- <a href="http://www.oracle.com/technetwork/pt/java/javase/downloads/index.html">Java JDK</a>.

- <a href="https://eclipse.org/">Eclipse</a>.

- <a href="http://www.apache.org/">Apache</a>.

- <a href="http://www.postgresql.org/">PostgreSQL</a>.

- <a href="http://52north.org/communities/sensorweb/sos/">SOS</a>.

## Build Instructions

- Clone the project: <code>git clone https://github.com/agora-research-group/AGORA-DSM</code>.

- Open Eclipse, go to File - New Project and choose Java Project Options. Uncheck 'Use default location' and insert the location of the git project.



<b>NOTE:</b> Common class contains the path specification for folders, files and services. The following variable paths need to be updated. 

<ol>
  <li><b><i>SERVER_PATH</i></b> is the path for keeping files such as <i>SensorML</i>, <i>describeSensor.json</i>, <i>insertObservation.json</i>, <i>getObservation.json</i>, <i>updateSensor.json</i>;</li>
  <li><b><i>SOS_PATH</i></b> is the path for requesting data from the SOS repository;</li>
  <li><b><i>SOS_52n_VERSION</i></b> is the version of the SOS service used;</li>
  <li><b><i>IMAGES_PATH</i></b> is the path where observations images are stored;</li>
  <li><b><i>Tracking File</i></b> are the files to help tracking performance from the required activities.</li>
</ol>

- Export the Java project into a .war file (need to install Maven Integration for Eclipse) inside the webapps folder of the Apache Tomcat.

- After starting the Tomcat Apache, sensor data from <a href="http://www.cemaden.gov.br/">CEMADEN</a>, <a href="https://www.pegelonline.wsv.de/gast/start">PEGELONLINE</a> and configured wireless sensor networks adapters are stored in the SOS repository using SOS services.

## Reporting Bugs

Any problem should be reported to group-agora@googlegroups.com.

For more information on AGORA-DSM, please, visit its main web page at: http://www.agora.icmc.usp.br/site/, or read the following reference: <a href="http://www.agora.icmc.usp.br/site/wp-content/uploads/2016/02/dissertacao_luiz.pdf">PDF</a>.
