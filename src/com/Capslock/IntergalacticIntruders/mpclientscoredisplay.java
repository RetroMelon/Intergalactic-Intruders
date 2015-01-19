package com.Capslock.IntergalacticIntruders;

import com.Capslock.Engine.screen;

/*
 * procedure:
 * 
 * 		this waits (300)
 * 		server sends
 * 		server waits
 * 		this retrieves
 * 		this sends
 * 		server recieves
 */

public class mpclientscoredisplay extends basempscoredisplay{

	public mpclientscoredisplay(screen thescreen) {
		super(thescreen);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void retrievedata() {
		recievestats();
		
		sendstats();
		
	}
	
	private void sendstats(){
		I_Intruders.MP_CLIENT.println(new Integer((int)I_Intruders.MP_TOTAL_DISTANCE_TRAVELLED_P1).toString());
	}
	
	private void recievestats(){
		//sleeping for a little while to make sure the server has enough time to send all the data
		try{Thread.sleep(300);}catch(Exception e){}
		
		//retrieveing the total time played
		I_Intruders.MP_TOTAL_TIME_PLAYED=Long.parseLong(I_Intruders.MP_CLIENT.read()); //read: TIME PLAYED
		
		//retrieveing the server's stats
		addP2stat("Score contribution:", I_Intruders.MP_CLIENT.read());         //read: SERVER SCORE
		addP2stat("Deaths:", I_Intruders.MP_CLIENT.read());                     //read:	SERVER DEATHS
		                                                                        
		int kills = Integer.parseInt(I_Intruders.MP_CLIENT.read());             //read:	SERVER KILLS
		int shotsfired = Integer.parseInt(I_Intruders.MP_CLIENT.read());        //read:	SERVER SHOTS FIRED
		int percentageaccuracy = (kills+1)/(shotsfired+1);                      
		                                                                        
		addP2stat("Kills:", ""+kills);                                          
		addP2stat("Shots fired:", ""+shotsfired);                               
		addP2stat("Percentage accuracy:", ""+percentageaccuracy);               
		addP2stat("Distance travelled:", I_Intruders.MP_CLIENT.read());         //read:	SERVER DISTANCE TRAVELLED
		                                                                        
		                                                                        
		//retrieveing the our own                                               
		addP1stat("Score contribution:", I_Intruders.MP_CLIENT.read());         //read:	THIS SCORE 
		addP1stat("Deaths:", I_Intruders.MP_CLIENT.read());                     //read:	THIS DEATHS
		                                                                        
		int ourkills = Integer.parseInt(I_Intruders.MP_CLIENT.read());          //read:	THIS KILLS	
		int ourshotsfired = Integer.parseInt(I_Intruders.MP_CLIENT.read());     //read:	THIS SHOTS FIRED
		int ourpercentageaccuracy = 100*(ourkills+1)/(ourshotsfired+1);				
		
		addP1stat("Kills:", ""+ourkills);
		addP1stat("Shots fired:", ""+ourshotsfired);
		addP1stat("Percentage accuracy:", ""+ourpercentageaccuracy);
		
		addP1stat("Distance travelled:", ""+Math.round(I_Intruders.MP_TOTAL_DISTANCE_TRAVELLED_P1));
		
		
	}

	@Override
	public void disconnect() {
		try{I_Intruders.MP_CLIENT.disconnect();}catch(Exception e){}		
	}

}