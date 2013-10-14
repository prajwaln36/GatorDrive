package com.cloud.gatordrive;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestJava {

	public static List<FileDescriptor> fileDesList = new ArrayList<FileDescriptor>();
	//int count = 0;

	public void addFile(int count) {
		while (count < 5) {
			File outfile = new File("/tmp/text"+count+".txt");
			try {
				FileOutputStream file = new FileOutputStream(outfile);
				fileDesList.add(file.getFD());
				file.write("ravindra".toString().getBytes());
				file.close();
				count++;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void readFiles(int count){
		while (count < 5) {
			File infile = new File("/tmp/text"+count+".txt");
			try {
				FileInputStream file = new FileInputStream(infile);
				//fileDesList.add(file.getFD());
				//file.write("ravindra".toString().getBytes());
				System.out.println("RFD = "+file.getFD().toString());
				file.close();
				count++;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String ar[]) {
		TestJava tj = new TestJava();
		
		System.out.println("Write");
		tj.addFile(0);
		
		for(FileDescriptor fd : fileDesList){
			System.out.println("WFD = "+fd.toString());
		}
		
		System.out.println("Read");
		tj.readFiles(0);
		
	}

}
