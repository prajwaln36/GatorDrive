package pojo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.commons.logging.LogFactory;

public class HttpClientExample {
	 /**
     * A generic method to execute any type of Http Request and constructs a response object
     * @param requestBase the request that needs to be exeuted
     * @return server response as <code>String</code>
     */
    private static String executeRequest(HttpRequestBase requestBase){
        String responseString = "" ;
 
        InputStream responseStream = null ;
        HttpClient client = HttpClientBuilder.create().build();
        try{
            HttpResponse response = client.execute(requestBase) ;
            if (response != null){
                HttpEntity responseEntity = response.getEntity() ;
 
                if (responseEntity != null){
                    responseStream = responseEntity.getContent() ;
                    if (responseStream != null){
                        BufferedReader br = new BufferedReader (new InputStreamReader (responseStream)) ;
                        String responseLine = br.readLine() ;
                        String tempResponseString = "" ;
                        while (responseLine != null){
                            tempResponseString = tempResponseString + responseLine + System.getProperty("line.separator") ;
                            responseLine = br.readLine() ;
                        }
                        br.close() ;
                        if (tempResponseString.length() > 0){
                            responseString = tempResponseString ;
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if (responseStream != null){
                try {
                    responseStream.close() ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        client.getConnectionManager().shutdown() ;
 
        return responseString ;
    }
 
    /**
     * Method that builds the multi-part form data request
     * @param urlString the urlString to which the file needs to be uploaded
     * @param file the actual file instance that needs to be uploaded
     * @param fileName name of the file, just to show how to add the usual form parameters
     * @param fileDescription some description for the file, just to show how to add the usual form parameters
     * @return server response as <code>String</code>
     */
    public String executeMultiPartRequest(String serverAddress, File file, String fileName, String username) {
    	
    	String urlString = "http://"+serverAddress+":8080/GatorDrive/upload";
        HttpPost postRequest = new HttpPost (urlString) ;
        try{
 
            MultipartEntity multiPartEntity = new MultipartEntity () ;
 
            //The usual form parameters can be added this way
            //multiPartEntity.addPart("fileDescription", new StringBody(fileDescription != null ? fileDescription : "")) ;
            multiPartEntity.addPart("fileName", new StringBody(fileName != null ? fileName : file.getName())) ;
            //multiPartEntity.addPart("fileDescriptor", new StringBody(fd+""));
            //multiPartEntity.addPart("partitionNum", new StringBody(partitionNum+""));
            //multiPartEntity.addPart("numOfParts", new StringBody(numOfParts+""));
            multiPartEntity.addPart("username", new StringBody(username+""));
            /*Need to construct a FileBody with the file that needs to be attached and specify the mime type of the file. Add the fileBody to the request as an another part.
            This part will be considered as file part and the rest of them as usual form-data parts*/
            FileBody fileBody = new FileBody(file, "application/octect-stream") ;
            multiPartEntity.addPart("attachment", fileBody) ;
 
            postRequest.setEntity(multiPartEntity) ;
        }catch (UnsupportedEncodingException ex){
            ex.printStackTrace() ;
        }
 
        return executeRequest (postRequest) ;
    }
    /*
    public int sendPartition(String serverAddress, File filePartition, String filename, String username) {
    	
    	String response = executeMultiPartRequest(serverAddress, filePartition, 
    			filePartition.getName(), username) ;
    	
    	String[] tokens = response.split("=");
    	return Integer.parseInt(tokens[1]);
    	
    }
   */ 
    
    public void uploadFile() {
    	
    	HttpClientExample fileUpload = new HttpClientExample() ;
       // File file = new File ("a.txt") ;
 
//        String response = fileUpload.executeMultiPartRequest("http://192.168.0.20:8080/GatorDrive/upload", file, file.getName(), "username") ;
     //   String response = fileUpload.executeMultiPartRequest("192.168.0.20", file, file.getName(), "gators") ;
     //   System.out.println("Response : "+response);
    }   
    
}