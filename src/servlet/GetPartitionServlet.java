package com.cloud.gatordrive.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.json.JSONObject;

import com.cloud.gatordrive.RequestHandler;

public class GetPartitionServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		
		try {
			
			FileUpload fup = new FileUpload();
			boolean isMultipart = FileUpload.isMultipartContent(req);

			// Create a new file upload handler
			System.out.println(isMultipart);
			DiskFileUpload upload = new DiskFileUpload();

			// Parse the request
			List /* FileItem */items = upload.parseRequest(req);
			int fd = 0;
			String replybackIP = "";
			//int partitionNumber = 0;
			int numOfParts = 0;
			String filename = "";
			String username = null;
			Iterator iter = items.iterator();
			//InputStream is;
			//String operation = "";
			while (iter.hasNext()) {

				FileItem item = (FileItem) iter.next();

				if (item.isFormField()) {
					System.out.println("its a field");
					if(item.getFieldName().contentEquals("fileDescriptor")){
						fd = Integer.parseInt(item.getString());
					}
					if(item.getFieldName().contentEquals("numOfParts")){
						numOfParts = Integer.parseInt(item.getString());
					}
					if(item.getFieldName().contentEquals("fileName")){
						filename = item.getString();
					}
					if(item.getFieldName().contentEquals("username")){
						username = item.getString();
					}
					if(item.getFieldName().contentEquals("replyBackIP")){
						replybackIP = item.getString();
					}
				}
			}
			
			RequestHandler reqHandler = new RequestHandler(username);
			int success = reqHandler.getPartition(fd, filename, replybackIP, numOfParts);
			
			res.setContentType("text/plain");
            try {
                    JSONObject json = new JSONObject();
                    json.put("success", success);
                    //res.getWriter().println(json.toString());
                    res.getWriter().println("success="+success);
            } catch (IOException e1) {
                    e1.printStackTrace();
            }

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
