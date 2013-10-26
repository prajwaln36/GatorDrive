package com.cloud.gatordrive;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import org.omg.PortableServer.Servant;

import com.cloud.gatordrive.client.Client;

public class RequestHandler {

	private final static String SERVER_TABLE = "ServerTable.txt";
	
	private Split split;
	private Merge merge;
	private MasterNode master;
	private String username;
	
	public RequestHandler(String username) {

		split = new Split();
		merge = new Merge();
		master = new MasterNode();
		this.username = username;
	}

	public void partitionFile(InputStream fis, String filename) {

		int FD = master.fileDescGenerator();
		
		File orgFile = createFile(fis, filename);
		long size = orgFile.length();
		
		try {
			fis.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File repFile = replicateFile(fis, filename);
		
		File[] orgPartitions = split.splitFile(orgFile, size, 1024, "Original");
		size = repFile.length();
		File[] repPartitions = split.splitFile(repFile, size, 1024, "Replicated");
	
		distributePartitions(filename, FD, orgPartitions, repPartitions);
	}

	public File createFile(InputStream fis, String filename) {

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

	public File replicateFile(InputStream fis, String filename) {

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

	public void distributePartitions(String filename, int fd, File[] orgPartitions, File[] repPartitions) {
		
		List<String> serverList = master.getDistribution(filename, fd);
		
		int num = orgPartitions.length;
		int perServer = (int)(num / serverList.size());
		
		int remaining = (num % serverList.size());
		
		Client client = new Client();
		int i;
		int count = 0;
		int result;
		
		//distribute orgPartitions
		for(i = 0; i < serverList.size(); i++){
			int high = count + perServer;
			for(int j = count; j < high; j++,count++){
				result = client.sendPartition(fd, serverList.get(i),orgPartitions[j],j,num, username);
				if(result == 1){
					System.out.println("Storing Original partition "+j+" on "+serverList.get(i)+" success");
				}else{
					System.out.println("Storing Original partition "+j+" on "+serverList.get(i)+" failed");
				}
			}
		}
		
		for(int j = count; j < num; j++){
			result = client.sendPartition(fd, serverList.get(i-1),orgPartitions[j],j,num, username);
			if(result == 1){
				System.out.println("Storing Original partition "+j+" on "+serverList.get(i-1)+" success");
			}else{
				System.out.println("Storing Original partition "+j+" on "+serverList.get(i-1)+" failed");
			}
		}
		
		//distribute repPartitions
		count = 0;
		for(i = serverList.size() - 1; i <= 0; i--){
			int high = count + perServer;
			for(int j = count; j < high; j++,count++){
				result = client.sendPartition(fd, serverList.get(i),repPartitions[j],j,num, username);
				if(result == 1){
					System.out.println("Storing replicated partition "+j+" on "+serverList.get(i)+" success");
				}else{
					System.out.println("Storing replicated partition "+j+" on "+serverList.get(i)+" failed");
				}
			}
		}
		
		for(int j = count; j < num; j++){
			result = client.sendPartition(fd, serverList.get(i+1),repPartitions[j],j,num, username);
			if(result == 1){
				System.out.println("Storing replicated partition "+j+" on "+serverList.get(i+1)+" success");
			}else{
				System.out.println("Storing replicated partition "+j+" on "+serverList.get(i+1)+" failed");
			}
		}
		
	}

	public void mergePartitions() {

	}
	
	public int storePartition(int FD, String filePartition, InputStream is, int partitionNumber, int numOfParts) {
		
		String pathToStore = "/tmp/"+username+"/"+filePartition;
		File directory = new File("/tmp/"+username);
		if(!(directory.exists() && directory.isDirectory())){
			directory.mkdir();
		}
		try{
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(pathToStore)));
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				StringBuilder resString = new StringBuilder();
				String str = "";
				while ((str = br.readLine()) != null) {
					resString.append(str);
					resString.append("\n");
				}
				bw.write(resString.toString());
				br.close();
				bw.close();
		}catch (Exception e) {
			System.out.println(e.getMessage());
			return 0;
		}
		
		return addEntry(FD, filePartition, numOfParts, partitionNumber);
	}
	
	
	public void deletePartition(int FD) {
		
	}
	
	
	public int addEntry(int FD, String filename, int numOfPartitions, int partitionNumber){
		
		//Table looks like this
				/*				Server Table
				 * FD		NumOfParts			partitions currently in server
				 * 1			4						1,O 3,O 2,R
				 * 3			6						2,R 1,O 3,R				
				 * 
				 */
		
		File file = new File(SERVER_TABLE);
		boolean updated = false;
		try {
			//FileInputStream is = new FileInputStream();
			BufferedReader br = new BufferedReader(new FileReader(file));
			BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
		    
			String line;
			String[] tokens;
			while((line = br.readLine()) != null){
				//if line is not null, there should be  3 tokens
				tokens = line.split("\t");
				if(tokens[0] != null && FD == Integer.parseInt(tokens[0])){
					
					String currPartitions = tokens[2];
					if(filename.contains("Original")){
						currPartitions = currPartitions + " "+ partitionNumber+",O";
					}else{
						currPartitions = currPartitions + " "+ partitionNumber+",R";
					}
					updated = true;
					break;
				}
			}
			
			if(!updated){
				//this is a new entry, just append it
				
				String entry ="";
				if(filename.contains("Original")){
					entry = FD+"\t"+numOfPartitions+"\t"+partitionNumber+",O";
				}else{
					entry = FD+"\t"+numOfPartitions+"\t"+partitionNumber+",R";
				}
				
				bw.write(entry);
				
			}
	
			br.close();
		    bw.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}
	
	public void updateEntry(int FD, String filename, int numOfPartitions, int partitionNumber) {
		
	}
}
