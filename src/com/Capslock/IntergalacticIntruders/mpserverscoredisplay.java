package com.Capslock.IntergalacticIntruders;

import com.Capslock.Engine.screen;

/*
 * 
 * 
 */



public class mpserverscoredisplay extends basempscoredisplay{

	public mpserverscoredisplay(screen thescreen) {
		super(thescreen);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void retrievedata() {
		sendstats();
		
		addlocalstats();
		
		recievestats();		
		
	}
	
	private void sendstats(){
		//sending the amount of time played in millis
		I_Intruders.MP_SERVER.println(""+I_Intruders.MP_TOTAL_TIME_PLAYED);
		
		//sending server info
		I_Intruders.MP_SERVER.println(""+I_Intruders.MP_SCORE_P1);
		I_Intruders.MP_SERVER.println(""+I_Intruders.MP_TOTAL_DEATHS_P1);
		I_Intruders.MP_SERVER.println(""+I_Intruders.MP_TOTAL_ALIENS_KILLED_P1);
		I_Intruders.MP_SERVER.println(""+I_Intruders.MP_TOTAL_SHOTS_FIRED_P1);
		I_Intruders.MP_SERVER.println(""+(int)I_Intruders.MP_TOTAL_DISTANCE_TRAVELLED_P1);

		//sending client info
		I_Intruders.MP_SERVER.println(""+I_Intruders.MP_SCORE_P2);
		I_Intruders.MP_SERVER.println(""+I_Intruders.MP_TOTAL_DEATHS_P2);
		I_Intruders.MP_SERVER.println(""+I_Intruders.MP_TOTAL_ALIENS_KILLED_P2);
		I_Intruders.MP_SERVER.println(""+I_Intruders.MP_TOTAL_SHOTS_FIRED_P2);
	}
	
	private void addlocalstats(){
		//adding all of the local server stats
		addP1stat("Score contribution", ""+I_Intruders.MP_SCORE_P1);
		addP1stat("Deaths:", ""+I_Intruders.MP_TOTAL_DEATHS_P1);
		addP1stat("Kills:", ""+I_Intruders.MP_TOTAL_ALIENS_KILLED_P1);
		addP1stat("Shots fired:", ""+I_Intruders.MP_TOTAL_SHOTS_FIRED_P1);
		addP1stat("Percentage accuracy", ""+(int)((I_Intruders.MP_TOTAL_ALIENS_KILLED_P1+1)/(I_Intruders.MP_TOTAL_SHOTS_FIRED_P1)));
		addP1stat("Distance travelled:", ""+(int)I_Intruders.MP_TOTAL_DISTANCE_TRAVELLED_P1);
		
		//now the same for player 2
		addP2stat("Score contribution", ""+I_Intruders.MP_SCORE_P2);
		addP2stat("Deaths:", ""+I_Intruders.MP_TOTAL_DEATHS_P2);
		addP2stat("Kills:", ""+I_Intruders.MP_TOTAL_ALIENS_KILLED_P2);
		addP2stat("Shots fired:", ""+I_Intruders.MP_TOTAL_SHOTS_FIRED_P2);
		addP2stat("Percentage accuracy", ""+(int)(100*(I_Intruders.MP_TOTAL_ALIENS_KILLED_P2+1)/(I_Intruders.MP_TOTAL_SHOTS_FIRED_P2)));
	}
	
	private void recievestats(){
		try{Thread.sleep(400);}catch(Exception e){}
		addP2stat("Distance travelled:", I_Intruders.MP_SERVER.read());
		
	}

	@Override
	public void disconnect() {
		try{I_Intruders.MP_SERVER.disconnect();}catch(Exception e){}		
	}

}

