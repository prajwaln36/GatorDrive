package com.cloud.gatordrive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class RequestHandler {

	private Split split;
	private Merge merge;

	public RequestHandler() {

		split = new Split();
		merge = new Merge();

	}

	public void partitionFile(FileInputStream fis, String filename) {

		File orgFile = createFile(fis, filename);
		long size = orgFile.length();
		
		try {
			fis.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File repFile = replicateFile(fis, filename);
		
		split.splitFile(orgFile, size, 1024, "Original");
		size = repFile.length();
		split.splitFile(repFile, size, 1024, "Replicated");

	}

	public File createFile(FileInputStream fis, String filename) {

		OutputStream outputStream = null;
		File file = null;
		
		try {
			file = new  File("/tmp/" + filename
					+ "-" + ApplicationInfo.userID + "original" + ".txt");
			outputStream = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = fis.read(bytes, 0, read)) != -1) {
				outputStream.write(bytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		return file;
	}

	public File replicateFile(FileInputStream fis, String filename) {

		OutputStream outputStream = null;
		File file = null;
		
		try {
			
			file = new File("/tmp/" + filename
					+ "-" + ApplicationInfo.userID + "replicated" + ".txt");
			outputStream = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = fis.read(bytes, 0, read)) != -1) {
				outputStream.write(bytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		return file;
	}

	public void distributePartitions() {

	}

	public void mergePartitions() {

	}
	
	public void storePartition(int FD, String filename, FileInputStream fis) {
		
	}
	
	public void deletePartition(int FD) {
		
	}
	
	public void addEntry(int FD, String filename, int numOfPartitions, int partitionNumber){
		
	}
	
	public void updateEntry(int FD, String filename, int numOfPartitions, int partitionNumber) {
		
	}
}
