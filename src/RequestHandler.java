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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.omg.PortableServer.Servant;

import com.cloud.gatordrive.client.Client;

public class RequestHandler {

	private final static String SERVER_TABLE = "/tmp/ServerTable.txt";
	
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

	public int partitionFile(InputStream fis, String filename) {
		
		int FD = master.fileDescGenerator();
		
		File orgFile = createFile(fis, filename);
		System.out.println("Original file created");
		long size = orgFile.length();
		
		try {
			fis.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File repFile = replicateFile(fis, filename);
		System.out.println("Replicated file created");
		
		File[] orgPartitions = split.splitFile(orgFile, size, 1024, "Original");
		System.out.println("Original file partitions created");
		size = repFile.length();
		File[] repPartitions = split.splitFile(repFile, size, 1024, "Replicated");
		System.out.println("Replicated file partitions created");
		
		return distributePartitions(filename, FD, orgPartitions, repPartitions);
	}
	
	public int getFile(String filename){
		
		int fd = master.getFileDescriptor(filename);
		
		if(fd == 0){
			System.out.println("File not found on the server");
			return 0;
		}
		
		//check if the fd is in this server
		File file = new File(SERVER_TABLE);
		//boolean updated = false;
		int result = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			String line;
			String[] tokens;
			while((line = br.readLine()) != null){
				//if line is not null, there should be  3 tokens
				tokens = line.split("\t");
				System.out.println("tokenssize = "+tokens.length);
				for(String s : tokens){
					System.out.println("s = "+s);
				}
				if(tokens[0] != null && fd == Integer.parseInt(tokens[0])){
				
					result = this.getLocalPartition(fd, filename, tokens[2], Integer.parseInt(tokens[1]));
					break;
				}
			}
			br.close();
			
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		if(result == 1){
			return 1;
		}else {
			try {
				String localIP = InetAddress.getLocalHost().getHostAddress();
				return master.getPartition(fd, filename, localIP);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return 0;
			}		
		}
	}
	
	public int getLocalPartition(int fd, String filename, String partitions, int totalNumOfParts){

		String[] tokens1 = partitions.split(" ");
		// partitions will be of the format 1,O 2,O 0,R
		int numOfParts = tokens1.length;
		// File[] fileParts = new File[numOfParts];
		int result = 0;
		for (String s : tokens1) {
			String[] tokens2 = s.split(",");
			if (tokens2[1].contentEquals("O")) {
				// retrive the partition
				int partitionNum = Integer.parseInt(tokens2[1]);
				String path = "/tmp/" + username + "/" + partitionNum
						+ "original" + filename;
				File cfile = new File(path);

				InputStream is = null;
				try {
					is = new FileInputStream(cfile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				result = this.addPartition(fd, cfile.getName(), is, partitionNum,
						totalNumOfParts);
				//count++;
			} else {

			}
		}

		return result;

	}
	
	public int getPartition(int fd, String filename, String ip, int totalNumOfParts){

		File file = new File(SERVER_TABLE);
		// boolean updated = false;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			String line;
			String[] tokens;
			while ((line = br.readLine()) != null) {
				// if line is not null, there should be 3 tokens
				tokens = line.split("\t");
				System.out.println("tokenssize = " + tokens.length);
				for (String s : tokens) {
					System.out.println("s = " + s);
				}
				if (tokens[0] != null && fd == Integer.parseInt(tokens[0])) {

					String[] tokens1 = tokens[2].split(" ");
					// partitions will be of the format 1,O 2,O 0,R
					int numOfParts = tokens1.length;
					// File[] fileParts = new File[numOfParts];
					int count = 0;
					int result;
					for (String s : tokens1) {
						String[] tokens2 = s.split(",");
						if (tokens2[1].contentEquals("O")) {
							// retrive the partition
							int partitionNum = Integer.parseInt(tokens2[1]);
							String path = "/tmp/" + username + "/"
									+ partitionNum + "original" + filename;
							// fileParts[count] = new File(path);
							File cfile = new File(path);

							// InputStream is = new FileInputStream(cfile);
							// this.addPartition(fd, cfile.getName(), is,
							// partitionNum, totalNumOfParts);
							Client client = new Client();
							result = client.sendPartitionRead(fd, ip, cfile,
									partitionNum, totalNumOfParts, username,
									"readOp");
							if (result == 1) {
								System.out
										.println("Successfully sent the partition "
												+ partitionNum + " to " + ip);
							} else {
								System.out
										.println("Failed to send the partition "
												+ partitionNum + " to " + ip);
							}

							count++;
						} else {

						}
					}
				}
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
		/*
		 * if(count == totalNumOfParts){ return 1; }else{ return
		 * master.getFile(filename); }
		 */
	}
	
	//return values
	//1 - to indicate addPartition call was successful for (local/remote) server
	//0 - to indicate all the partitions are found in the same local server
	public int addPartition(int FD, String filePartition, InputStream is, int partitionNumber, int numOfParts) {
		
		ApplicationInfo.map.put(partitionNumber, is);
		
		if(ApplicationInfo.map.size() == numOfParts){
			//map has all required inputstream
			InputStream[] files = new InputStream[numOfParts];
			for(int i = 0; i < ApplicationInfo.map.size(); i++){
				 files[i] = ApplicationInfo.map.get(Integer.valueOf(i));
			}
			merge.mergeFiles(files);
			
			return 0;
		}
		return 1;
	}
	

	public File createFile(InputStream fis, String filename) {

		//OutputStream outputStream = null;
		File file = null;
		StringBuilder sb = new StringBuilder();
		try {
			//file = new File("/tmp/" + filename + "-" + ApplicationInfo.userID
			//		+ "original" + ".txt");
			file = new File("/tmp/original" + filename);
			// outputStream = new FileOutputStream(file);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			int read = 0;
			/*
			 * byte[] bytes = new byte[1024];
			 * 
			 * while ((read = fis.read(bytes, 0, read)) != -1) {
			 * outputStream.write(bytes); }
			 */

			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String str = "";
			while ((str = br.readLine()) != null) {
				sb.append(str);
				sb.append("\n");
			}
			bw.write(sb.toString());
			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			sb = null;
		} finally {/*
					 * if (outputStream != null) { try { //
					 * outputStream.flush(); outputStream.close(); } catch
					 * (IOException e) { e.printStackTrace(); }
					 * 
					 * }
					 */
		}

		return file;
	}

	public File replicateFile(InputStream fis, String filename) {

		//OutputStream outputStream = null;
		File file = null;
		StringBuilder sb = new StringBuilder();
		try {
			
			//file = new File("/tmp/" + filename
			//		+ "-" + ApplicationInfo.userID + "replicated" + ".txt");
			file = new File("/tmp/replicated" + filename);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			//outputStream = new FileOutputStream(file);
			int read = 0;
			/*
			byte[] bytes = new byte[1024];

			while ((read = fis.read(bytes, 0, read)) != -1) {
				outputStream.write(bytes);
			}
			*/
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String str = "";
			while ((str = br.readLine()) != null) {
				sb.append(str);
				sb.append("\n");
			}
			bw.write(sb.toString());
			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {/*
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			*/
		}

		return file;
	}

	public int distributePartitions(String filename, int fd, File[] orgPartitions, File[] repPartitions) {
		
		List<String> serverList = master.getDistribution(filename, fd);
		
		List<String> orgServerList = new ArrayList<String>();
		List<String> repServerList = new ArrayList<String>();
		
		System.out.println("FD = "+fd);
		for(String s : serverList){
			System.out.println("Server = "+s);
		}
		
		int num = orgPartitions.length;
		int perServer = (int)(num / serverList.size());
		
		int remaining = (num % serverList.size());
		
		Client client = new Client();
		int i;
		int count = 0;
		int result;
		
		System.out.println("AAAA");
		
		//distribute orgPartitions
		for(i = 0; i < serverList.size(); i++){
			int high = count + perServer;
			System.out.println("XXX");
			for(int j = count; j < high; j++,count++){
				result = client.sendPartition(fd, serverList.get(i),orgPartitions[j],j,num, username);
				if(result == 1){
					System.out.println("Storing Original partition "+j+" on "+serverList.get(i)+" success");
					//update master table
					orgServerList.add(serverList.get(i));
				}else{
					System.out.println("Storing Original partition "+j+" on "+serverList.get(i)+" failed");
				}
			}
		}
		System.out.println("BBBB");
		for(int j = count; j < num; j++){
			result = client.sendPartition(fd, serverList.get(i-1),orgPartitions[j],j,num, username);
			if(result == 1){
				System.out.println("Storing Original partition "+j+" on "+serverList.get(i-1)+" success");
				orgServerList.add(serverList.get(i-1));
			}else{
				System.out.println("Storing Original partition "+j+" on "+serverList.get(i-1)+" failed");
			}
		}
		
		//distribute repPartitions
		System.out.println("CCCC");
		count = 0;
		for(i = serverList.size() - 1; i >= 0; i--){
			System.out.println("11111111");
			int high = count + perServer;
			for(int j = count; j < high; j++,count++){
				System.out.println("22222222");
				result = client.sendPartition(fd, serverList.get(i),repPartitions[j],j,num, username);
				if(result == 1){
					System.out.println("Storing replicated partition "+j+" on "+serverList.get(i)+" success");
					//update master table
					repServerList.add(serverList.get(i));
				}else{
					System.out.println("Storing replicated partition "+j+" on "+serverList.get(i)+" failed");
				}
			}
		}
		System.out.println("i = "+i);
		System.out.println("count = "+count);
		System.out.println("DDDD");
		for(int j = count; j < num; j++){
			result = client.sendPartition(fd, serverList.get(i+1),repPartitions[j],j,num, username);
			if(result == 1){
				System.out.println("Storing replicated partition "+j+" on "+serverList.get(i+1)+" success");
				repServerList.add(serverList.get(i+1));
			}else{
				System.out.println("Storing replicated partition "+j+" on "+serverList.get(i+1)+" failed");
			}
		}
		
		master.addEntry(fd, orgServerList, repServerList);
		return 1;
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
			//BufferedWriter bw = new BufferedWriter(new FileWriter(file,false));
		    
			StringBuilder sb = new StringBuilder();
			/*
			sb.append(br.readLine());
			sb.append("\n");
			sb.append(br.readLine());
			sb.append("\n");
			*/
			String line;
			String[] tokens;
			while((line = br.readLine()) != null){
				//if line is not null, there should be  3 tokens
				tokens = line.split("\t");
				System.out.println("tokenssize = "+tokens.length);
				for(String s : tokens){
					System.out.println("s = "+s);
				}
				if(tokens[0] != null && FD == Integer.parseInt(tokens[0])){
					
					String currPartitions = tokens[2];
					System.out.println("tokens2 = "+tokens[2]);
					if(filename.contains("Original")){
						currPartitions = currPartitions + " "+ partitionNumber+",O";
					}else{
						currPartitions = currPartitions + " "+ partitionNumber+",R";
					}
					String entry = tokens[0]+"\t"+tokens[1]+"\t"+currPartitions;
					sb.append(entry);
					sb.append("\n");
					//bw.write(sb.toString());
					updated = true;
					break;
				}else{
					sb.append(line);
					sb.append("\n");
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
				sb.append(entry);
				sb.append("\n");
				//bw.write(sb.toString());
				
			}
			System.out.println("SB = "+sb.toString());
			br.close();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file,false));
			bw.write(sb.toString());
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
