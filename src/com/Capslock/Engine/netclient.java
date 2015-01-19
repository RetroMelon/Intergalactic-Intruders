package com.Capslock.Engine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;


public class netclient extends basesocket {
	
	//variables required to function that are specific to the client class
	private String ip = "localhost";
	
	public netclient(String ip, int portnumber){
		super(portnumber);
		this.ip=ip;
	}
	
	public boolean connect(){
		try{
			mainsocket = new Socket(ip, port);
			try{mainsocket.setSoTimeout(1);}catch(Exception e){}
			writer = new PrintStream(mainsocket.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(mainsocket.getInputStream()));
		}catch(Exception e){disconnect(); return false;}
		
		try{Thread.sleep(100);}catch(Exception e){}
		
		//return testconnection();
		connected = true;
		return true;
	}

}