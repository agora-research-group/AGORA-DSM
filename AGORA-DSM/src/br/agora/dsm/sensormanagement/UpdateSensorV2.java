package br.agora.dsm.sensormanagement;

import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import br.agora.dsm.utils.Common;


public class UpdateSensorV2 {

	/**
	 * Constructor
	 */
	public UpdateSensorV2 () {
		
	}
	
	/**
	 * 
	 * @param message
	 * @return
	 * @throws org.apache.http.HttpException 
	 * @throws DOMException 
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	
	public static boolean send(JSONObject message) throws DOMException, org.apache.http.HttpException 
	{
		
		Object obj;
		
		try {
			
			/******************************** parsing message json *************************************/
			
			String sensor_id = null, sensor_name = null, agency = null, timestamp = null, value = null, nivel = null, unit = null, type = null, altitude = null, longitude = null, latitude = null, sensor_place_name = null, info = null;
			
			// reading SOS operation json
			JSONParser parser1 = new JSONParser();	
			
			Object obj1 = parser1.parse(new FileReader(Common.SERVER_PATH+"describeSensor.json"));
			
			JSONObject jsonObject1 = (JSONObject) obj1;	
			
			jsonObject1.put("procedure", message.get("sensor_id"));
			
			Common.SOS_request describeSensor = Common.httpPost_JSON(jsonObject1);
			
			if(describeSensor.getResult() == 200)
			{				
				// reading SOS operation json
				JSONParser parser = new JSONParser();	
				
				obj = parser.parse(new FileReader(Common.SERVER_PATH+"updateSensor.json"));
					 
				JSONObject jsonObject = (JSONObject) obj;		

				// reading SensorML
				String strSensorML = jsonObject.get("procedureDescription").toString();
				
				// converting string to xml
				Document SensorML = Common.StringToDocument(strSensorML);
			
				// setting sensor_id within SensorML
				if (message.get("sensor_id") != null)
				{
					sensor_id = message.get("sensor_id").toString();
					
					// changing SensorML sensor_id
					Node node0 = SensorML.getElementsByTagName("sml:value").item(0);
					node0.getFirstChild().setNodeValue(sensor_id);
					
					// changing SensorML offering id
					Node node3 = SensorML.getElementsByTagName("swe:value").item(0);
					node3.getFirstChild().setNodeValue("offering_"+sensor_id);				
					
				}			
	
				if (message.get("agency") != null)
				{
					agency = message.get("agency").toString();
					
					// changing SensorML parent Procedure
					Node node4 = SensorML.getElementsByTagName("swe:value").item(1);
					node4.getFirstChild().setNodeValue(agency);
				}
				
				// setting sensor_name within SensorML
				if (message.get("sensor_name") != null)
				{
					sensor_name = message.get("sensor_name").toString().replace(" ", "_");
					
					// changing SensorML location longname
					Node node1 = SensorML.getElementsByTagName("sml:value").item(1);
					node1.getFirstChild().setNodeValue(sensor_name);
					
					// changing SensorML location longname
					Node node2 = SensorML.getElementsByTagName("sml:value").item(2);
					node2.getFirstChild().setNodeValue(sensor_name);
				}		
				
				// setting sensor_place_name within SensorML
				if (message.get("sensor_place_name") != null)
				{
					sensor_place_name = message.get("sensor_place_name").toString().replace(" ","_");
					
					// changing SensorML feature of InterestID
					Node node5 = SensorML.getElementsByTagName("swe:value").item(2);
					node5.getFirstChild().setNodeValue(sensor_place_name);
				}
				
				// setting coordinates within SensorML
				if (message.get("coordinates") != null)
				{
					JSONObject coordinates = (JSONObject)message.get("coordinates");
									
					// if json station contains latitude
					if (coordinates.get("latitude") != null)
					{
						latitude = coordinates.get("latitude").toString();
						
						// changing SensorML latitude
						Node node7 = SensorML.getElementsByTagName("swe:value").item(3);
						node7.getFirstChild().setNodeValue(latitude);
					}
					
					// if json station contains longitude
					if (coordinates.get("longitude") != null)
					{
						longitude = coordinates.get("longitude").toString();
						
						// changing SensorML longitude
						Node node6 = SensorML.getElementsByTagName("swe:value").item(4);
						node6.getFirstChild().setNodeValue(longitude);
					}
					
					// if json station contains latitude
					if (coordinates.get("altitude") != null)
					{
						altitude = coordinates.get("altitude").toString();
						
						// changing SensorML altitude
						Node node8 = SensorML.getElementsByTagName("swe:value").item(5);
						node8.getFirstChild().setNodeValue(altitude);					
					}
					
				}	
				
				// converting xml to string
				String strSensorML_v2 = Common.DocumentToString(SensorML);
				
				// update procedure description
				jsonObject.put("procedureDescription", strSensorML_v2);
								
				// update observable property
				/*if (message.get("property") != null)
				{
					// cria um JSONArray e preenche com valores string 
					JSONArray observableProperty = (JSONArray) jsonObject.get("observableProperty");
					
					observableProperty.add(message.get("property").toString());
					
					jsonObject.put("observableProperty", observableProperty);
					
				}*/
				
				// sending http post request using a json as parameter
				Common.httpPost_JSON(jsonObject);
				
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
					
		return true;	
		
	}
	
}
