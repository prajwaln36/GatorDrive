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
import java.util.List;

import javax.swing.RepaintManager;

public class MasterNode {

	//A file to maintain mapping from filename to FD created using this node
	private final static String FILE_NAME = "MapperFile.txt";
	private final static String SEQ_FILE_NAME = "SequenceNumber.txt";
	private final static String MASTER_TABLE = "MasterTable.txt";
	
	//Master node assigns unique fileDesc to each of the files in the system
	
	public int fileDescGenerator(){
		//open the file and get the latest FD
		//File looks like this
		/*
		 * 
		 * text1.txt 1
		 * text2.txt 2
		 * text3.txt 3
		 * 
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
					BufferedWriter bw = new BufferedWriter(new FileWriter(file,false));
				    
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
	
}
