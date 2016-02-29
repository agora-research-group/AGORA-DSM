package br.agora.dsm.services;

import javax.servlet.http.HttpServlet;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import br.agora.dsm.sensormanagement.PublishObservationV2;
import br.agora.dsm.sensormanagement.RegisterSensorV2;
import br.agora.dsm.sensormanagement.UpdateSensorV2;

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
	
}