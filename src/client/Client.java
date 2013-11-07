import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class Client {

	public static void main(String[] args) {

		//String urlParameters = "param1=a&param2=b&param3=c";
		//JSONObject json = new JSONObject();

		Frame myFrame = new Frame();
		try {
			//json.put("filename", "Server.txt");
			//json.put("param2", "gokak");

			//String payload = json.toString();
			
			String filename = "test1.txt";

			String request = "http://192.168.0.20:8080/GatorDrive/download/"+filename;
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("GET");
			//connection.setRequestProperty("Content-Type",
			//		"application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Type","application/json");
			connection.setRequestProperty("charset", "utf-8");
			
			//connection.setRequestProperty("Content-Length",
			//		"" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			/*
			OutputStream wr = connection.getOutputStream();
			wr.write(payload.toString().getBytes("UTF-8"));
			wr.flush();
			wr.close();

			int resp = connection.getResponseCode();

			System.out.println("Code = " + resp);

			InputStream is = connection.getInputStream();

			if (is != null) {
				System.out.println("IS is not null");
				String resString = "";
				try {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(is));
					String str = "";
					while ((str = br.readLine()) != null) {
						resString += str + "\n";
					}
				} catch (IOException io) {
					io.printStackTrace();
					resString = null;
				}
				System.out.println("Resp = "+resString);
			}else {
				System.out.println("IS is null");
			}
			*/
			int responseCode = connection.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
	 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	    
			FileDialog choo = new FileDialog(myFrame, "Choose your file destination",FileDialog.SAVE);
			choo.setDirectory(null);
			choo.setFile("enter file name here");
	        choo.setVisible(true);
	        
	        String targetFile = choo.getDirectory() + choo.getFile() + ".txt";
	        
	        BufferedWriter out = new BufferedWriter(new FileWriter(new File(targetFile)));
			out.write(response.toString());
			out.close();
			
			//print result
			//System.out.println(response.toString());
			// connection.disconnect();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		

		/*
		 * try {
		 * 
		 * String request =
		 * "http://192.168.0.20:8080/Test/test?param1=fhanb&param2=andv"; URL
		 * url = new URL(request); HttpURLConnection connection =
		 * (HttpURLConnection) url .openConnection();
		 * connection.setDoOutput(true); connection.setDoInput(true);
		 * connection.setInstanceFollowRedirects(false);
		 * connection.setRequestMethod("Get");
		 * connection.setRequestProperty("Content-Type",
		 * "application/x-www-form-urlencoded");
		 * connection.setRequestProperty("charset", "utf-8");
		 * 
		 * connection.setUseCaches(false);
		 * 
		 * OutputStream wr = connection.getOutputStream();
		 * wr.write(payload.toString().getBytes("UTF-8")); wr.flush();
		 * wr.close(); connection.disconnect(); } catch (Exception e) {
		 * System.out.println(e.getMessage()); }
		 */
	}
}
