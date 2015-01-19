package com.Capslock.Engine;

import java.io.BufferedReader;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.net.Socket;



public abstract class basesocket {
	
	//network related objects
	protected Socket mainsocket = null;
	protected BufferedReader reader = null;
	protected PrintStream writer = null;
	
	//variables required for operation
	protected int port = 15000;
	protected boolean connected = false;
	public static final String CONNECTION_TEST_DATA = "§"; //the character sent when the connection is being tested

	public basesocket(int portnumber){
		port=portnumber;
	}
	
	//closes the socket and resets everything
	public boolean disconnect(){
		try{mainsocket.close();}catch(Exception e){}
		mainsocket=null;
		reader=null;
		writer=null;
		connected=false;
		return true;
	}
	
	//reads the oldest line of data on the input buffer
	public String read(){
		if(!connected)return "";
		try{
			String data = "";
			do{
				data=reader.readLine();
				if(data.equals(CONNECTION_TEST_DATA))continue;
				if(data.equals("")){return "";}//checking that the data recieved wasn't used to test the connection
				else{return data;}
			}while(reader.ready());
			
			return "";//if the loop ends with no valid data, then return an empty string.
			
		}catch(InterruptedIOException e){return "";}catch(Exception e){System.out.println("Disconnecting - exception in read():   "+e.getMessage()); disconnect(); return "";}// disconnect(); return "";}//catch(Exception e){System.out.println("this exception"); return "";}//catch(Exception e){System.out.println("exception in read():   "+e.getMessage()); disconnect(); return "";}
	}
	
	//writes a new line of data to the socket
	public boolean println(String message){
		try{
			writer.println(message);
		}catch(Exception e){disconnect(); return false;}
		return true;
	}
	
	public boolean testconnection(){
		try{writer.println(CONNECTION_TEST_DATA); connected=true; return true;}
		catch(Exception e){disconnect(); return false;}
	}
	
	//get and set methods
	public boolean isconnected(){return connected;}
	public int getport(){return port;}
	
	//sets
	public void settimeout(int millis){
		try{mainsocket.setSoTimeout(millis);}catch(Exception e){}
	}

}
