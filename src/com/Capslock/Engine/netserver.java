package com.Capslock.Engine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;

public class netserver extends basesocket {

	//network related variables
	private ServerSocket mainserversocket = null;
	
	//variables required for correct operation of the class
	private int timeout = 20000; //the time that the server socket will wait for a connection before returning false
	
	public netserver(int portnumber) {
		super(portnumber);
	}
	public netserver(int portnumber, int timeout) {
		super(portnumber);
		this.timeout=timeout;
	}
	
	//when this method is called it will wait for the amount of time indicated by the timeout variable for a connection to be made
	public boolean listen(){
		return listen1(); // calls a serarate method, purely so that the class is neater
	}
	public boolean listen(int timeout){
		this.timeout=timeout;
		return listen1();
	}
	
	private boolean listen1(){
		try{
			mainserversocket = new ServerSocket(port);
			mainserversocket.setReuseAddress(true);
			mainserversocket.setSoTimeout(timeout);
			mainsocket = mainserversocket.accept();
			try{mainsocket.setSoTimeout(5);}catch(Exception e){}
			writer = new PrintStream(mainsocket.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(mainsocket.getInputStream()));
		}catch(Exception e){
			disconnect();
			return false;
		}
		try{Thread.sleep(100);}catch(Exception e){}
		
		//return testconnection();
		connected=true;
		System.out.println(this.toString()+" ACCEPTED CONNECTION TO IP: "+mainsocket.getInetAddress());
		return true;
	}
	
	public boolean disconnect(){
		try{
			mainsocket.close();
		}catch(Exception e){}
		mainsocket=null;
		mainserversocket=null;
		reader=null;
		writer=null;
		connected=false;
		return true;
	}
	
	public String getinternetip(){
		String theip = "";
		URL theurl;
		try{
			theurl = new URL("http://automation.whatismyip.com/n09230945.asp");//this webpage provides a tool for finding an internet ip.
			HttpURLConnection con = (HttpURLConnection) theurl.openConnection();
			InputStream stream = con.getInputStream();
			InputStreamReader reader = new InputStreamReader(stream);
			BufferedReader bReader = new BufferedReader(reader);
			theip = bReader.readLine();
			return theip;
		}catch(Exception e){System.out.println("COULD NOT CONTACT REMOTE IP VERIFICATION SERVER"); return "";}
		
	}
	
	public void settimeout(int timeoutmillis){
		timeout = timeoutmillis;
		try{mainserversocket.setSoTimeout(timeout);}catch(Exception e){}
	}

}
