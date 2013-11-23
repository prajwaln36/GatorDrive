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
import java.util.ArrayList;
import java.util.List;

import javax.swing.RepaintManager;

import com.cloud.gatordrive.client.Client;

public class MasterNode {

	//A file to maintain mapping from filename to FD created using this node
	private final static String FILE_NAME = "/tmp/MapperFile.txt";
	private final static String SEQ_FILE_NAME = "/tmp/SequenceNumber.txt";
	private final static String MASTER_TABLE = "/tmp/MasterTable.txt";
	private final static String SERVERS_FILE = "/tmp/Server.txt";
	
	//Master node assigns unique fileDesc to each of the files in the system
	
	public int fileDescGenerator(){
		//open the file and get the latest FD
		//File looks like this
		/*
		 * 5 
		 */
		
		File file = new File(SEQ_FILE_NAME);
		//String latest;
		int latest = 0;
		try {
			//FileInputStream is = new FileInputStream();
			BufferedReader br = new BufferedReader(new FileReader(file));
		    String line = br.readLine();
		    latest = Integer.parseInt(line);
		    latest += 1;
		    br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return latest;
		
	}
	
	public int getFileDescriptor(String filename) {
		// open the file and get the fd for filename
		// File looks like this
		/*
		 * text1.txt 1 
		 * text2.txt 2 
		 * text3.txt 3
		 */

		File file = new File(FILE_NAME);
		// String latest;
		int fd = 0;
		try {
			// FileInputStream is = new FileInputStream();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line; // = br.readLine();
			String[] tokens;
			while((line = br.readLine()) != null){
				tokens = line.split(" ");
				if(tokens[0].contentEquals(filename)){
					fd = Integer.parseInt(tokens[1]);
					break;
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fd;
	}
	
	
	public int getPartition(int fd, String filename, String ip, int totalNumOfParts){
		
		//get master table data and send requests
		File file = new File(MASTER_TABLE);
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			String line;
			String[] tokens;
			while((line = br.readLine()) != null){
				tokens = line.split("\t");
				if(fd == Integer.parseInt(tokens[0])){
					String[] orgServers;
					orgServers = tokens[1].split(",");
					for(String serverAddress : orgServers){
						Client client = new Client();
						client.getPartition(fd, serverAddress, filename, ip, ApplicationInfo.userName, totalNumOfParts);
					}
				}
			}
			
		}catch(IOException e){
			
		}
		
		return 0;
		
	}
	
	public void writeSuccessfull(String filename,int FD) {
		
		File sfile = new File(SEQ_FILE_NAME);
		File file = new File(FILE_NAME);
		
		try {
			//FileInputStream is = new FileInputStream();
			//FileOutputStream os = new FileOutputStream(sfile);
			BufferedWriter sbw = new BufferedWriter(new FileWriter(sfile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
		    String line = filename + " " + FD;
		    bw.write(line);
		    sbw.write(FD);
		    bw.close();
		    sbw.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void updateTable(int FD, List<String> OrgServers,List<String> replicatedServers){
		
		//Table looks like this
		/*				Master Table
		 * FD		ServerList(Org partition)		ServerList(Replicated Partition)
		 * 1		s2,s4,s7						s3,s8
		 * 3		s6,s3							s9,s1
		 * 
		 */
		
		File file = new File(MASTER_TABLE);
		try {
			//FileInputStream is = new FileInputStream();
			BufferedReader br = new BufferedReader(new FileReader(file));
		    
			//skip first 2 lines
			br.readLine();
			br.readLine();
			String line;
			StringBuilder sb = new StringBuilder();
			String[] tokens;
			while((line = br.readLine()) != null){
				tokens = line.split("\t");
				int fd = Integer.parseInt(tokens[0]);
				if(fd == FD){
					StringBuilder sb1 = new StringBuilder();
					sb1.append(fd);
					sb1.append("\t");
					sb1.append(tokens[1]);
					for(String server : OrgServers){
						sb1.append(server);
						sb1.append(",");
					}
					sb1.append("\t");
					sb1.append(tokens[2]);
					for(String server : replicatedServers){
						sb1.append(server);
						sb1.append(",");
					}
					sb.append(sb1.toString());
					sb.append("\n");
				}else{
					sb.append(line);
					sb.append("\n");
				}
			}
		    br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void addEntry(int FD, List<String> OrgServers, List<String> replicatedServers){
		
		//Table looks like this
				/*				Master Table
				 * FD		ServerList(Org partition)		ServerList(Replicated Partition)
				 * 1		s2,s4,s7						s3,s8
				 * 3		s6,s3							s9,s1
				 * 
				 */
				
				File file = new File(MASTER_TABLE);
				try {
					//FileInputStream is = new FileInputStream();
					BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
				    
					StringBuilder sb = new StringBuilder();
					
					sb.append(FD);
					sb.append("\t");
					
					for(String server : OrgServers){
						sb.append(server);
						sb.append(",");
					}
					sb.append("\t");
					
					for(String server : replicatedServers){
						sb.append(server);
						sb.append(",");
					}
					
					bw.write(sb.toString());
					
				    bw.close();
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
	}
	
	public void deleteEntry(int FD){
		
		//Table looks like this
				/*				Master Table
				 * FD		ServerList(Org partition)		ServerList(Replicated Partition)
				 * 1		s2,s4,s7						s3,s8
				 * 3		s6,s3							s9,s1
				 * 
				 */
		File file = new File(MASTER_TABLE);
		try {
			//FileInputStream is = new FileInputStream();
			BufferedReader br = new BufferedReader(new FileReader(file));
		    
			//skip first 2 lines
			br.readLine();
			br.readLine();
			String line;
			StringBuilder sb = new StringBuilder();
			String[] tokens;
			while((line = br.readLine()) != null){
				tokens = line.split("\t");
				int fd = Integer.parseInt(tokens[0]);
				if(fd == FD){
					//don't add this line
				}else{
					sb.append(line);
					sb.append("\n");
				}
			}
		    br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
			
	}
	
	public List<String> getDistribution(String filename,int fd){
		
		//The file which stores all other servers ip address, and their server name looks like this
		/*
		 * server1	192.168.0.1
		 * server2	192.168.0.2
		 * server3	192.168.0.3
		 */
		
		File file = new File(SERVERS_FILE);
		List<String> ipList = new ArrayList<String>();
		String line;
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			while((line = br.readLine()) != null){
				ipList.add(line);
			}
			br.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ipList;
	}
	
}
