

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;

import com.cloud.gatordrive.ApplicationInfo;

import pojo.HttpClientExample;

@MultipartConfig()
/**
 * Servlet implementation class FileUploadServlet
 */
@WebServlet("/FileUploadServlet")
public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileUploadServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		//System.out.println(request.getParameter("nameoffile"));
		
		
		final Part filePart = request.getPart("file");
		String result = printNames(filePart);
		
		System.out.println("result: " + result);
		PrintWriter out = response.getWriter();
		
		boolean fileUploadStatus = false;
		
		if(result.trim().replaceAll("\\s+","").compareToIgnoreCase("success=1") == 0)
			fileUploadStatus = true;
		
		//forward to login page to login
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/FileUpload.jsp");		
		
		if(fileUploadStatus)
			out.println("<font color=green>File upload successful.<br> Use the below uploader to upload more files.</font>");
		else
			out.println("<font color=red>File upload failed. Please try again.</font>");		
		
		rd.include(request, response);
		
		
		
		/*
		if(filePart != null) {
			final String fileName = getFileName(filePart);
			System.out.println("filename " + fileName);
		}
		else
			System.out.println("File part is null");
	    */
		
	//	HttpClientExample httpClientExample = new HttpClientExample();
		//httpClientExample.uploadFile();
	}

	/*
	 private String getFileName(final Part part) {
         final String partHeader = part.getHeader("content-disposition");
         System.out.println("Header = " + partHeader);
       //  LOGGER.log(Level.INFO, "Part Header = {0}", partHeader);
         for (String content : part.getHeader("content-disposition").split(";")) {
             if (content.trim().startsWith("filename")) {
                 return content.substring(
                         content.indexOf('=') + 1).trim().replace("\"", "");
             }
         }
         return null;
     }
     */
	
	public String printNames(final Part part) throws IllegalStateException, IOException, ServletException{
		String fileName = null;
		
		final String partHeader = part.getHeader("content-disposition");
        System.out.println("Header = " + partHeader);
      //  LOGGER.log(Level.INFO, "Part Header = {0}", partHeader);
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                fileName = content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
            
        }
        InputStream in = part.getInputStream();
        File file = stream2file(in, fileName);
        System.out.println("FileName = " + fileName);
        HttpClientExample client = new HttpClientExample();
        return client.executeMultiPartRequest("192.168.0.20", file, fileName, ApplicationInfo.userName);
	    /*for(Part part : request.getParts()){
	        System.out.println("PN: "+ part.getName());
	        Collection<String> headers = part.getHeaders("content-disposition");
	        if (headers == null)
	            continue;
	        for(String header : headers){
	            System.out.println("CDH: " + header);   
	            fileName = header;
	        } 
	        
	    }*/
        
	   
	}
	
	 public static File stream2file (InputStream in, String fileName) throws IOException {
	        final File tempFile = File.createTempFile(fileName,null);
	        tempFile.deleteOnExit();
	        try (FileOutputStream out = new FileOutputStream(tempFile)) {
	            IOUtils.copy(in, out);
	        }
	        return tempFile;
	    }
}
