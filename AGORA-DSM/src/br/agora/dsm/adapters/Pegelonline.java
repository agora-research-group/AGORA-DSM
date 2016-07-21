package br.agora.dsm.adapters;

import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.agora.dsm.sensormanagement.PublishObservationV2;
import br.agora.dsm.sensormanagement.RegisterSensorV2;
import br.agora.dsm.utils.Common;

public class Pegelonline extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {
				StationAdapter();
			}
		}.start();

		new Thread() {
			public void run() {
				MeasurementAdapter();
			}
		}.start();

	}

	@SuppressWarnings("unchecked")
	public static void StationAdapter() {

		while (true) {

			try {

				// Reading json ina web page
				Object jsonStation = Common
						.URLjsonToObject("http://www.pegelonline.wsv.de/webservices/rest-api/v2/stations.json");

				if (jsonStation != null) {
					JSONArray array = (JSONArray) jsonStation;

					int count = 0;

					// Intern loop for all the pluviomethers
					while (count < array.size() - 1) {
						/*
						 * ****************** PERFORMANCE TRACKING
						 * ******************
						 */
						// long startAGORADSM = System.nanoTime();

						JSONObject jsonAdapter = new JSONObject();

						JSONObject station = (JSONObject) array.get(count);

						jsonAdapter.put("message", "registerSensor");

						// station uuid
						if (station.get("uuid") != null)
							jsonAdapter.put("sensor_id", station.get("uuid"));

						// station number
						// if (station.containsKey("number"))
						// number =
						// Long.parseLong(station.get("number").toString());

						// station shortname
						if (station.get("longname") != null)
							jsonAdapter.put("sensor_name", station.get("longname"));

						// station water shortname and longname
						JSONObject water = (JSONObject) station.get("water");

						if (water.get("longname") != null) {
							jsonAdapter.put("sensor_place_name", water.get("longname"));
						}

						// if json station contains latitude and longitude
						if (station.containsKey("latitude") && station.containsKey("longitude")) {
							JSONObject coordinates = new JSONObject();
							coordinates.put("longitude", station.get("longitude"));
							coordinates.put("latitude", station.get("latitude"));
							jsonAdapter.put("coordinates", coordinates);
						}

						if (station.get("km") != null && station.get("agency") != null) {
							jsonAdapter.put("info", "km-" + station.get("km") + ";agency-" + station.get("agency"));
						}

						jsonAdapter.put("type", "numeric");

						jsonAdapter.put("unit", "cm");

						jsonAdapter.put("agency", "Pegelonline");

						jsonAdapter.put("property", "water_level");

						count++;

						RegisterSensorV2.send(jsonAdapter);

						/*
						 * ****************** PERFORMANCE TRACKING
						 * ******************
						 */
						/*
						 * long stopAGORADSM = System.nanoTime();
						 * 
						 * double x = Math.pow(10, -18); double a =
						 * ((Long.parseLong(String.valueOf(stopAGORADSM)) -
						 * Long.parseLong(String.valueOf(startAGORADSM))) / x);
						 * 
						 * a = a * Math.pow(10, -(Math.floor(Math.log10(a) -
						 * 18)));
						 * 
						 * SimpleDateFormat dt = new SimpleDateFormat(
						 * "yyyy-MM-dd hh:mm:ss"); String date =
						 * dt.format(Calendar.getInstance().getTime());
						 * 
						 * String line = "Pegelonline;" + date + ";" +
						 * startAGORADSM + ";" + stopAGORADSM + ";" +
						 * (stopAGORADSM-startAGORADSM) + ";" + a + ";Station;";
						 * 
						 * Common.updateAGORADSMPerformanceMeasurement(line);
						 */
					}

				} else
					System.out.println("Pegelonline Station Adapter - Connection Refused.");

				// waiting 60 minutes to run again
				Thread.sleep(3600000);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Error Pegelonline Station Adapter cycle - " + e);
			}

		}

	}

	@SuppressWarnings("unchecked")
	public static void MeasurementAdapter() {

		while (true) {

			try {

				Object jsonStation = Common
						.URLjsonToObject("http://www.pegelonline.wsv.de/webservices/rest-api/v2/stations.json");

				if (jsonStation != null) {
					JSONArray array = (JSONArray) jsonStation;

					Iterator<JSONObject> stations = array.listIterator();

					// variables initialization
					String URL_name_station, URL_encoded;

					while (stations.hasNext()) {

						try {

							/*
							 * ****************** PERFORMANCE TRACKING
							 * ******************
							 */
							// long startAGORADSM = System.nanoTime();

							JSONObject sensor = stations.next();

							/**************************
							 * READ JSON STATION MEASUREMENT PAGE
							 **************************/
							// define station name
							URL_name_station = sensor.get("shortname").toString();
							URL_encoded = URL_name_station.replaceAll(" ", "%20");
							URL_encoded = URL_encoded.replaceAll("�", "%C3%BC");
							URL_encoded = URL_encoded.replaceAll("�", "%C3%B6");
							URL_encoded = URL_encoded.replaceAll("�", "%C3%A4");
							URL_encoded = URL_encoded.replaceAll("-", "%2D");

							// transform URL json page into an jsonObject
							Object objectMeasurement = Common
									.URLjsonToObject("http://www.pegelonline.wsv.de/webservices/rest-api/v2/stations/"
											+ URL_encoded + "/W/currentmeasurement.json");

							if (objectMeasurement != null) {

								JSONObject jsonMeasurement = (JSONObject) objectMeasurement;

								JSONObject jsonAdapter = new JSONObject();

								jsonAdapter.put("message", "publishObservation");

								// if json measurement contains timestamp
								if (sensor.get("uuid") != null)
									jsonAdapter.put("sensor_id", sensor.get("uuid").toString());

								if (jsonMeasurement.get("timestamp") != null)
									jsonAdapter.put("timestamp", jsonMeasurement.get("timestamp"));

								// if json measurement contains value
								if (jsonMeasurement.get("value") != null)
									jsonAdapter.put("value", jsonMeasurement.get("value"));

								// if json measurement contains stateMnwMhw
								if (jsonMeasurement.get("stateMnwMhw") != null)
									jsonAdapter.put("level", jsonMeasurement.get("stateMnwMhw"));

								// if json measurement contains stateNswHsw
								/*
								 * if (jsonMeasurement.get("stateNswHsw") !=
								 * null) jsonMeasurement.get("stateNswHsw");
								 */

								// variables initialization
								jsonAdapter.put("unit", "cm");
								jsonAdapter.put("type", "numeric");
								jsonAdapter.put("agency", "PEGELONLINE");
								jsonAdapter.put("property", "water_level");

								PublishObservationV2.send(jsonAdapter);
							}

							/*
							 * ****************** PERFORMANCE TRACKING
							 * ******************
							 */
							/*
							 * long stopAGORADSM = System.nanoTime();
							 * 
							 * double x = Math.pow(10, -18); double a =
							 * ((Long.parseLong(String.valueOf(stopAGORADSM)) -
							 * Long.parseLong(String.valueOf(startAGORADSM))) /
							 * x);
							 * 
							 * a = a * Math.pow(10, -(Math.floor(Math.log10(a) -
							 * 18)));
							 * 
							 * SimpleDateFormat dt = new SimpleDateFormat(
							 * "yyyy-MM-dd hh:mm:ss"); String date =
							 * dt.format(Calendar.getInstance().getTime());
							 * 
							 * String line = "PEGELONLINE;" + date + ";" +
							 * startAGORADSM + ";" + stopAGORADSM + ";" +
							 * (stopAGORADSM-startAGORADSM) + ";" + a +
							 * ";measurements;";
							 * 
							 * Common.updateAGORADSMPerformanceMeasurement(line)
							 * ;
							 */

						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("Error Pegelonline Measurements Adapter - " + e);
						}
					}
				} else
					System.out.println("Pegelonline Measurement Adapter - Connection Refused!");

				// waiting 10 minutes to run again
				Thread.sleep(600000);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Error Pegelonline Measurement Adapter Cycle - " + e);
			}
		}
	}

}