package br.agora.dsm.sensormanagement;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import br.agora.dsm.utils.Common;


public class PublishObservationV2 {

	/**
	 * Constructor
	 */
	public PublishObservationV2 () {
		
	}
	
	/**
	 * 
	 * @param message
	 * @return
	 * @throws org.apache.http.HttpException 
	 */
	@SuppressWarnings({ "unused", "unchecked" })	
	public static boolean send(JSONObject message) throws org.apache.http.HttpException 
	{
		
		Object obj, obj1, obj2;
		
		try {
			
			/******************************** parsing message json *************************************/
			
			String sensor_id = null, sensor_name = null, timestamp = null, value = null, nivel = null, unit = null, type = null, altitude = null, longitude = null, latitude = null, sensor_place_name = null, info = null;
				
			RegisterSensorV2.send(message);			

			// reading get observation
			JSONParser parser2 = new JSONParser();		
			
			obj2 = parser2.parse(new FileReader(Common.SERVER_PATH+"getObservation.json"));
			
			JSONObject jsonObject2 = (JSONObject) obj2;
			
			jsonObject2.put("observation", message.get("sensor_id").toString()+"_"+message.get("timestamp").toString()+"_"+message.get("property").toString());		
			
			Common.SOS_request getObservation = Common.httpPost_JSON(jsonObject2);
			
			if(getObservation.getResult() != 200 || getObservation.getRequest().get("observations") != null)
			{
				
				// reading SOS operation json
				JSONParser parser = new JSONParser();	
				
				obj = parser.parse(new FileReader(Common.SERVER_PATH+"insertObservation.json"));
					 
				JSONObject jsonObject = (JSONObject) obj;		
				
				JSONObject observation = (JSONObject) jsonObject.get("observation");
				
				// update procedure description and
				if (message.get("sensor_id") != null)
				{
					jsonObject.put("offering", message.get("sensor_id")+"_"+message.get("property"));					
					observation.put("procedure", message.get("sensor_id").toString());
				}
				
				// update observable property
				/*if (message.get("property") != null)
					observation.put("observedProperty", message.get("property").toString());*/
				
				// update unit of measurement
				if (message.get("timestamp") != null)
				{
					observation.put("resultTime", message.get("timestamp").toString());
					observation.put("phenomenonTime", message.get("timestamp").toString());
				}
				
				JSONObject result = (JSONObject) observation.get("result");
				
				// insert observation numeric value
				if (message.get("type").equals("numeric"))
				{
					observation.put("type", "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement");
					
					// update result unit of measurement
					if (message.get("unit") != null)
						result.put("uom", message.get("unit").toString());			
					
					// update result value
					if (message.get("value") != null)
						result.put("value", message.get("value"));			
				}
				else // insert observation numeric value
					if (message.get("type").equals("text"))
					{
						observation.put("type", "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TextObservation");
						String binaryChain = Common.ImageToBin(Common.IMAGES_PATH+message.get("value").toString());
						observation.put("result", binaryChain);
					}	
				
				JSONObject identifier = (JSONObject) observation.get("identifier");
				
				// update observation id
				if (message.get("sensor_id") != null && message.get("timestamp") != null && message.get("property") != null)
					identifier.put("value", message.get("sensor_id").toString()+"_"+message.get("timestamp").toString()+"_"+message.get("property").toString());
				
				// update feature of interest id
				if (message.get("sensor_place_name") != null)
				{
					JSONObject featureOfInterest = (JSONObject) observation.get("featureOfInterest");
					
					JSONObject foi_id = (JSONObject) featureOfInterest.get("identifier");					
					
					foi_id.put("value", message.get("sensor_place_name").toString());
					
					JSONArray foi_name_array = (JSONArray) featureOfInterest.get("name");
					
					JSONObject foi_name = (JSONObject) foi_name_array.get(0);
					
					// update feature of interest name
					foi_name.put("value", message.get("sensor_place_name"));
					
					JSONArray sampled_foi_array = (JSONArray) featureOfInterest.get("sampledFeature");
					
					sampled_foi_array.set(0, message.get("sensor_place_name").toString());
					
				}				
				
				Common.SOS_request insertObservation = Common.httpPost_JSON(jsonObject);
				
				// sending http post request using a json as parameter				
				/*if (insertObservation.getResult() == 200)					
					System.out.println("After Insert Observation");
				else
					if (insertObservation.getRequest().containsKey("exceptions") == true)
					{				
						JSONArray a = (JSONArray) insertObservation.getRequest().get("exceptions");			
						System.out.println("exceptions");
						
						JSONObject j = (JSONObject) a.get(0);
						System.out.println(j.toString());
						
						if (j.containsKey("locator"))
						{					
							System.out.println(j.get("locator").toString());					
							if (j.get("locator").toString().equals("observedProperty"))
							{
								System.out.println("observedProperty is invalid");
								RegisterSensorV2.send(message);
								PublishObservationV2.send(message);
							}					
						}
					}
					else
						if (insertObservation.getRequest().containsKey("exceptions") == true)
						{				
							JSONArray a = (JSONArray) insertObservation.getRequest().get("exceptions");			
							System.out.println("exceptions");
							
							JSONObject j = (JSONObject) a.get(0);
							System.out.println(j.toString());
							
							if (j.containsKey("locator"))
							{					
								System.out.println(j.get("locator").toString());					
								if (j.get("locator").toString().equals("offering"))
								{
									System.out.println("offering is invalid");
									RegisterSensorV2.send(message);
									PublishObservationV2.send(message);
								}
							}
						}*/
						
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Publish Observation error - "+e);
		}
					
		return true;	
		
	}
}
