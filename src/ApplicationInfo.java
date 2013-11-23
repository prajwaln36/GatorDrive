package com.cloud.gatordrive;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.HashMap;

public class ApplicationInfo {

	public static String userName;
	public static int userID = 0;
	
	public static String IP_ADDRESS = "192.168.0.20"; //InetAddress.getLocalHost().getHostAddress();// "192.168.0.20";
	
	public static HashMap<Integer,InputStream> map = new HashMap<Integer,InputStream>();
	
}
