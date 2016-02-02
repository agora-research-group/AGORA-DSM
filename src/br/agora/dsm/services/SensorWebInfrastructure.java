package br.agora.dsm.services;

import javax.servlet.http.HttpServlet;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import br.agora.dsm.sensormanagement.PublishObservationV2;
import br.agora.dsm.sensormanagement.RegisterSensorV2;
import br.agora.dsm.sensormanagement.UpdateSensorV2;

/**
 * @author 
 *
 */
public class SensorWebInfrastructure extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 * @param message
	 * @return
	 */
	public static String traditionalSensor(String request) {
				
		//System.out.println("traditionalSensor");
		//System.out.println(request);
				
		try {
			
			
			Object obj = JSONValue.parse(request);
			
			JSONObject observation = (JSONObject)obj;
			
			String operation = observation.get("message").toString();
		    
			if (operation.equals("registerSensor"))
				RegisterSensorV2.send(observation);
			else if (operation.equals("publishObservation"))
				PublishObservationV2.send(observation);
			else if (operation.equals("updateSensor"))
				UpdateSensorV2.send(observation);

			return operation;
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	/*public static void main(String[] args) {
		
		System.out.println("init");
		
		//String teste = "{\"timestamp\":\"2015-12-06T18:45:15+00:00\",\"message\":\"publishObservation\",\"sensor_name\":\"foi3\",\"value\":\"inundacao.jpeg\",\"property\":\"photo\",\"sensor_id\":\"sensor-13\",\"type\":\"text\",\"coordinates\":{\"longitude\":-47.899989,\"latitude\":-22.000368}}";
				
		String teste = "{\"timestamp\":\"2015-12-07T18:45:15+00:00\",\"message\":\"publishObservation\",\"unit\":\"cm\",\"sensor_name\":\"foi3\",\"value\":30,\"property\":\"nivel_agua\",\"sensor_id\":\"sensor-13\",\"type\":\"numeric\",\"coordinates\":{\"longitude\":-47.899989,\"latitude\":-22.000368}}";
		
		traditionalSensor(teste);
		
	}*/

}