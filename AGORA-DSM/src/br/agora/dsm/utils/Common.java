package br.agora.dsm.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class Common 
{

	
	/**
	 * 
	 */
	public final static String SERVER_PATH = "/home/suporte/AGORA-DSM/";
	
	public final static String SOS_PATH = "http://localhost:8080/52n-sos-webapp/service";	
	
	public final static int SOS_52n_VERSION = 3;
	
	public final static String IMAGES_PATH = "/home/ftpuser/";
	
	public final static String AGORADSMTrackingFile = "/home/suporte/AGORA-DSM/AGORA-DSM-performance-tracking.csv";
    
	public final static String uavsTrackingFile = "/home/ftpagora/AgoraDSM/uavs-performance-tracking.csv";
	
	public final static String twitterTrackingFile = "/home/ftpagora/AgoraDSM/twitter-performance-tracking.csv";
	
	/**
	 * 
	 * @param line
	 */
	
	public static class SOS_request {
	    
		private JSONObject request;
	    private int result;

		// constructor
		public SOS_request(JSONObject request, int result) 
		{
		   this.request = request;
		   this.result = result;
		}

	    // getter
	    public JSONObject getRequest() { return request; }
	    public int getResult() { return result; }
	    
	    // setter
	    public void setRequest(JSONObject request) { this.request = request; }
	    public void setResult(int result) { this.result = result; }
	    
	}
	
	public static void updateAGORADSMPerformanceMeasurement (String line) {
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(AGORADSMTrackingFile, true));
			
			writer.write(line);
			writer.newLine();
			writer.flush();			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void updateUavsPerformanceMeasurement (String line) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(uavsTrackingFile, true));
			
			writer.write(line);
			writer.newLine();
			writer.flush();			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * @param line
	 */
	public static void updateTwitterPerformanceMeasurement (String line) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(twitterTrackingFile, true));
			
			writer.write(line);
			writer.newLine();
			writer.flush();			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public static Document readXML(String url) {

		Document doc = null;

		try {

			// reading XML file
			File xmlFile = new File(url);

			// get the DOM Builder Factory
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();

			// ignoring comments
			// dbFactory.setIgnoringComments(true);

			// get the DOM Builder
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			// load and parse the XML document
			doc = dBuilder.parse(xmlFile);

			// normalization of the XML document
			// doc.getDocumentElement().normalize();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return doc;

	}

	/**
	 * 
	 * @param doc
	 * @throws TransformerException
	 */
	public static void printXML(Document doc) throws TransformerException {

		// A TransformerFactory instance can be used to create Transformer and Templates objects. 
		TransformerFactory tf = TransformerFactory.newInstance();
		
		// An instance of transformer can be obtained with the TransformerFactory.newTransformer method. This instance may then be used to process XML from a variety of sources and write the transformation output to a variety of sinks.
		Transformer transformer = tf.newTransformer();
		
		// Set an output property that will be in effect for the transformation.
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		
		// A character stream that collects its output in a string buffer, which can then be used to construct a string.
		StringWriter writer = new StringWriter();
		
		// Transform the XML Source to a Result. Specific transformation behavior is determined by the settings of the TransformerFactory in effect when the Transformer was instantiated and any modifications made to the Transformer instance.
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		
		// Return the string buffer itself.
		String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
		
		// print output
		System.out.println(output);

	}

	/**
	 * 
	 * @param input
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String httpPost(String xml) throws HttpException, IOException {
		// prepare HTTP post
		PostMethod post = new PostMethod(Common.SOS_PATH);

		// request content will be retrieved directly from the input stream. Per
		// default, the request content needs to be buffered in order to
		// determine its length. Request body buffering can be avoided when
		// content length is explicitly specified
		// post.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(input), input.length()));
		post.setRequestEntity(new StringRequestEntity(xml, "application/xml", "UTF-8"));

		// specify content type and encoding. If content encoding is not
		// explicitly specified, ISO-8859-1 is assumed
		post.setRequestHeader("Content-type", "application/xml; charset=UTF-8");

		// get HTTP client
		HttpClient httpclient = new HttpClient();

		// execute request
		try {

			int result = httpclient.executeMethod(post);

			// display status code
			System.out.println("Response status code: " + result);

			// display response
			System.out.println("Response body: ");
			System.out.println(post.getResponseBodyAsString());
			
			return post.getResponseBodyAsString();

		} finally {
			// release current connection to the connection pool once you are
			// done
			post.releaseConnection();
		}

	}
	
	/**
	 * 
	 * @param doc
	 * @return
	 * @throws TransformerException
	 */
	public static String getStringFromDocument(Document doc) throws TransformerException {
		
		// Acts as a holder for a transformation Source tree in the form of a Document Object Model (DOM) tree.
		DOMSource domSource = new DOMSource(doc);
	    
		// A character stream that collects its output in a string buffer, which can then be used to construct a string.
		StringWriter writer = new StringWriter();
	    
		// Acts as an holder for a transformation result, which may be XML, plain Text, HTML, or some other form of markup.
		StreamResult result = new StreamResult(writer);
	    
		// A TransformerFactory instance can be used to create Transformer and Templates objects.
		TransformerFactory tf = TransformerFactory.newInstance();
	    
		// An instance of transformer can be obtained with the TransformerFactory.newTransformer method. This instance may then be used to process XML from a variety of sources and write the transformation output to a variety of sinks.
		Transformer transformer = tf.newTransformer();
		
		// Transform the XML Source to a Result. Specific transformation behavior is determined by the settings of the TransformerFactory in effect when the Transformer was instantiated and any modifications made to the Transformer instance.
		transformer.transform(domSource, result);			
		
		return writer.toString();
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	public static String ImageToBin(String file) {
		
		byte[] imageInByte;
		
		String binaryChain = "";

		try {

			BufferedImage originalImage = ImageIO.read(new File(file));

			// convert BufferedImage to byte array
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(originalImage, "png", baos);
			ImageIO.write(originalImage, "jpeg", baos);
			ImageIO.write(originalImage, "jpg", baos);
			ImageIO.write(originalImage, "bmp", baos);
			ImageIO.write(originalImage, "gif", baos);
			
			baos.flush();
			imageInByte = baos.toByteArray();

			binaryChain = imageInByte.toString();
			
			/*// convert byte array back to BufferedImage
			InputStream in = new ByteArrayInputStream(imageInByte);
			BufferedImage bImageFromConvert = ImageIO.read(in);

			ImageIO.write(bImageFromConvert, "jpg", new File(
								"c:/new-darksouls.jpg"));*/

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		return binaryChain;

	}

	/**
	 * 
	 * @param xml
	 * @param sosUrl
	 * @return
	 */
	public static String request(StringBuilder xml, String sosUrl) {
		String response = new String();

		String yourString = xml.toString();
		String result = yourString.replaceAll("^([\\W]+)<", "<");

		CloseableHttpClient httpClient = HttpClients.createDefault();

		try {
			HttpPost httpPost = new HttpPost(sosUrl);
			httpPost.addHeader("Content-Type", "text/xml");

			StringEntity entity = new StringEntity(result, HTTP.UTF_8);
			entity.setContentType("text/xml");
			entity.setChunked(true);

			httpPost.setEntity(entity);

			CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent(), "ISO-8859-1"));
			StringBuffer newData = new StringBuffer();
			String data = new String();

			while ((data = br.readLine()) != null) {
				newData.append(data);
			}

			response = newData.toString();

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return response;
	}
	
	public static Connection dbConnection(String url, String user, String password) throws ClassNotFoundException, SQLException
	{
		
		Class.forName("org.postgresql.Driver");
				
		return DriverManager.getConnection(url, user, password);
		
	}
	
	public static Object URLjsonToObject(String urlPage)
	{
		
		String jsonString = null;
		
		try
		{
			// Class URL represents a Uniform Resource Locator, a pointer to a "resource" on the World Wide Web		
			URL jsonPage = new URL(urlPage);
			HttpURLConnection connection = (HttpURLConnection) jsonPage.openConnection();
						
			// not found - 400
			// response ok - 200
			if (connection.getResponseCode() == 200)
			{				
				// Reads text from a character-input stream, buffering characters so as to provide for the efficient reading of characters, arrays, and lines.
				BufferedReader in = new BufferedReader(new InputStreamReader(jsonPage.openStream()));
				
		        // Get the contents of a byte[] as a String using the default character encoding of the platform.
		        jsonString = IOUtils.toString(in);
		        
		        return JSONValue.parse(jsonString);
		    }			
	        
			return null;
			
		}  catch(java.net.SocketException e) {
			// TODO Auto-generated catch block
			System.out.println("Error SocketException URLjsonToObject - Common - "+e);
			return null;						
		}	catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error URLjsonToObject - Common - "+e);
			return null;
		}		
				
	}
	
	public static SOS_request httpPost_JSON(JSONObject json)
	{
		
		PostMethod post = null;
		int result = 0;
		JSONObject request = null;
		SOS_request sosRequest = null;
		
		try {
			
			// prepare HTTP post
			post = new PostMethod(SOS_PATH);
			
			// request content will be retrieved directly from the input stream. Per
			// default, the request content needs to be buffered in order to
			// determine its length. Request body buffering can be avoided when
			// content length is explicitly specified
			// post.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(input), input.length()));
			post.setRequestEntity(new StringRequestEntity(json.toString(), "application/json", "UTF-8"));
		
			// specify content type and encoding. If content encoding is not
			// explicitly specified, ISO-8859-1 is assumed
			post.setRequestHeader("Content-type", "application/json; charset=UTF-8");
						
			// get HTTP client
			HttpClient httpclient = new HttpClient();	
			
			// execute request
			result = httpclient.executeMethod(post);
						
			JSONParser parser = new JSONParser();
			request = (JSONObject) parser.parse(post.getResponseBodyAsString());
			sosRequest = new SOS_request(request, result);
			
			// display status code
			System.out.println("Response status code: " +sosRequest.result);

			// display response
			System.out.println("Response body: ");
			System.out.println(sosRequest.request.toString());
			
			/*if (request.containsKey("observations") == true)
			{
				JSONArray json_array = (JSONArray) json1.get("observations");
				
				if (json_array.size() == 0)
					result = 0;
			}*/
			
			/*if (json1.containsKey("exceptions") == true)
			{				
				JSONArray a = (JSONArray) json1.get("exceptions");			
				System.out.println("exceptions");
				JSONObject j = (JSONObject) a.get(0);
				System.out.println(j.toString());
				if (j.containsKey("locator"))
				{					
					System.out.println(j.get("locator").toString());					
					if (j.get("locator").toString().equals("observedProperty"))
					{
						System.out.println("observedProperty is invalid");
						result = 1;
						UpdateSensorV2.send(json);
					}					
				}
			}*/		
						
			
		} catch (Exception e) {
			System.out.println("Error http_post json- Common - "+e);			
		} finally {
			// release current connection to the connection pool once you are
			// done
			post.releaseConnection();
		}	
		
		return sosRequest;
		
	}
	
	public static String DocumentToString(Document doc) throws TransformerException 
	{
		// document to string
		TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        
        // below code to remove XML declaration
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        String output = writer.getBuffer().toString();
        
        return output;

	}
	
	public static Document StringToDocument(String str) throws ParserConfigurationException, SAXException, IOException 
	{

		// string to document
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		//domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		//Document SensorML = builder.parse(strSensorML);    
		Document doc = builder.parse(new InputSource(new StringReader(str)));
		
		return doc;
		
	}

}