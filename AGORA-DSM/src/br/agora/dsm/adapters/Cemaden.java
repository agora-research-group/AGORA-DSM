package br.agora.dsm.adapters;

import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.agora.dsm.sensormanagement.PublishObservationV2;
import br.agora.dsm.utils.Common;

public class Cemaden extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {
				try {
					CemadenAdapter();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();

	}

	public static void CemadenAdapter() throws Exception {

		while (true) {

			try {

				StringTokenizer siglasEstados = new StringTokenizer(
						"AC AL AP AM BA CE DF ES GO MA MT MS MG PA PB PR PE PI RJ RN RS RO RR SC SP SE TO");

				// Main loop principal for all the Brazilian states
				while (siglasEstados.hasMoreTokens()) {

					// Reading json in a web page
					JSONObject jsonPluviometer = (JSONObject) Common.URLjsonToObject(
							"http://150.163.255.240/CEMADEN/resources/parceiros/" + siglasEstados.nextToken() + "/1");

					if (jsonPluviometer != null) {
						JSONArray array = (JSONArray) jsonPluviometer.get("cemaden");

						Iterator<JSONObject> all_measurements = array.listIterator();

						// Intern loop for all the pluviomethers
						while (all_measurements.hasNext()) {
							/*
							 * ****************** PERFORMANCE TRACKING
							 * ******************
							 */
							// long startAGORADSM = System.nanoTime();

							JSONObject pluviometer = all_measurements.next();

							JSONObject jsonAdapter = new JSONObject();

							jsonAdapter.put("message", "publishObservation");

							if (pluviometer.get("codestacao") != null)
								jsonAdapter.put("sensor_id", pluviometer.get("codestacao"));

							if (pluviometer.get("latitude") != null && pluviometer.get("longitude") != null) {
								JSONObject coordinates = new JSONObject();

								coordinates.put("longitude", pluviometer.get("longitude"));
								coordinates.put("latitude", pluviometer.get("latitude"));

								jsonAdapter.put("coordinates", coordinates);
							}

							if (pluviometer.get("cidade") != null && pluviometer.get("uf") != null)
								jsonAdapter.put("sensor_place_name",
										pluviometer.get("cidade") + "-" + pluviometer.get("uf"));

							if (pluviometer.get("nome") != null)
								jsonAdapter.put("sensor_name", pluviometer.get("nome"));

							jsonAdapter.put("type", "numeric");

							if (pluviometer.get("chuva") != null)
								jsonAdapter.put("value", pluviometer.get("chuva"));

							/*
							 * if(pluviometer.get("nivel") != null)
							 * jsonAdapter.put("level",
							 * pluviometer.get("nivel"));
							 */

							if (pluviometer.get("dataHora") != null) {
								String new_dataHora = pluviometer.get("dataHora").toString().replaceAll(" ", "T")
										.concat("+00:00");
								jsonAdapter.put("timestamp", new_dataHora);
							}

							if (pluviometer.get("tipo") != null)
								jsonAdapter.put("info", pluviometer.get("tipo"));

							jsonAdapter.put("property", "pluviometer");

							jsonAdapter.put("unit", "cm");

							jsonAdapter.put("agency", "CEMADEN");

							PublishObservationV2.send(jsonAdapter);

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
							 * String line = "CEMADEN;" + date + ";" +
							 * startAGORADSM + ";" + stopAGORADSM + ";" +
							 * (stopAGORADSM-startAGORADSM) + ";" + a +
							 * ";CEMADEN;";
							 * 
							 * Common.updateAGORADSMPerformanceMeasurement(line)
							 * ;
							 */

						}

					} else
						System.out.println("Cemaden - Connnection Refused");
				}

				// waiting 15 minutes to run again
				Thread.sleep(900000);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Error Cemaden measurement - " + e);
			}

		}

	}

}