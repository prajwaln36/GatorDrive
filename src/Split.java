package com.cloud.gatordrive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Split {

	 int FRG_FSIZE=1024;  
     
	    public File[] splitFile(File source,long length,int lsize,String type)  
	    {  
	    	
	    	//FRG_FSIZE = 1024;
	    	int noFile = (int)(length / FRG_FSIZE);
	          
	        File[] fileFragments = new File[noFile];  
	        String[] frgfName = new String[noFile];  
	        FileInputStream fis = null;
	            try{      
	                String sourceFName = source.getName();  
	                long sourceFSize = source.length();  
	                fis = new FileInputStream(source);  
	  
	                String Fileinfo = new String(sourceFName + "," + String.valueOf(sourceFSize));  
	                System.out.println(noFile);  
	                if (lsize != 0) {  
	                    noFile--;  
	                }  
	                System.out.println(noFile);  
	                sourceFName = sourceFName.substring(0, sourceFName.lastIndexOf("."));  
	                int j=0;  
	                for (int i = 1; i <= noFile; i++) {  
	                    frgfName[i-1] ="/tmp/temp"+sourceFName + String.valueOf(i)+type+".splt";  
	                    fileFragments[i-1] = new File(frgfName[i-1]);  
	                     
	                    FileOutputStream fos = new FileOutputStream(fileFragments[i - 1]);  
	                    byte[] data = new byte[FRG_FSIZE];  
	                    int count = fis.read(data);  
	                    fos.write(data);  
	                    fos.close();  
	                    String frgFileInfo = new String(frgfName[i-1] + "," + String.valueOf(FRG_FSIZE));  
	                }  
	                if (lsize != 0) {                      
	                    System.out.println(noFile);  
	                    frgfName[noFile] ="/tmp/temp"+sourceFName + String.valueOf(noFile+1)+type+".splt";  
	                    fileFragments[noFile] = new File(frgfName[noFile]);  
	                    FileOutputStream fos = new FileOutputStream(fileFragments[noFile]);  
	                    byte[] data = new byte[lsize];  
	                    int count = fis.read(data);  
	                    fos.write(data);  
	                    fos.close();  
	                    String frgFileInfo = new String(frgfName[noFile] + "," + String.valueOf(lsize));  
	                }  
	  
	               } catch (Exception e) {  
	                        
	                   System.out.println("Error in Splitting"+e);  
	                   JOptionPane.showMessageDialog(null, "Error in Splitting File \n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);  
	                     return null;  
	               }  finally {
	       			if (fis != null) {
	    				try {
	    					// outputStream.flush();
	    					fis.close();
	    				} catch (IOException e) {
	    					e.printStackTrace();
	    				}

	    			}
	    		}
	        return fileFragments;  
	  }  
	    
	  public static void main(String ar[])  
	  {  
	    Split sf=new Split();  
	    //sf.splitFile(new File("/home/ravindra/workspace3/FileSplitterAndMerger/src/Placement.txt"),5,1024);  
	  }  
	  
}
