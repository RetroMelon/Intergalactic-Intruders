package com.Capslock.Engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class framerate {
	//setting up variables required to perform framerate functions
	private int desiredframerate = 100; //the framerate that we want to achieve.
	private int actualframerate = 100; //the achieved framerate based on the last frame's elapsed time.
	private long lastframeelapsed = 10; //the elapsed time in milliseconds that the last frame took to perform.
	private long lastframetime = System.currentTimeMillis(); //the system time at which the last frame was rendered.
	private int desiredframeinterval = 1/desiredframerate; //the desired time between each frame. this variable exists to prevent the need for calculating this value constantly for checking frames
	
	//setting up variables that allow for pausing of the framerate timer
	private boolean paused = false; //whether the framerate timer should be paused
	private long pausetimedifference = 0; 	//when paused this is the time difference between the last frame and pausing. if the last frame were at 2000, and you paused at 2200, this would be 200
											//then, when you unpause, this is taken from the system time to give you the new lastframetime.
	
	//setting up variables that allow this to be drawn to the screen.
	private Font defaultfont = new Font(Font.DIALOG, Font.PLAIN, 12);
	private Color defaultcolor = Color.red;
	private int defaultdrawx = 5;
	private int defaultdrawy = 12;
		
	//constructor sets up new desired framerate, etc.
	public framerate(int desiredframerate){
		if(desiredframerate>1000){desiredframerate=1000;}
		else if(desiredframerate<=0){desiredframerate=100;}
		this.desiredframerate=desiredframerate;
		actualframerate=desiredframerate;
		desiredframeinterval=1000/desiredframerate;
		lastframeelapsed=desiredframeinterval;
		lastframetime=System.currentTimeMillis();
	}
	
	//checks if the desired length of time has passes since last frame, and if so, returns true. if paused=true then it automatically returns false;
	public boolean checkframe(){
		if(paused){return false;}
		if(System.currentTimeMillis()-lastframetime>=desiredframeinterval){
			lastframeelapsed=System.currentTimeMillis()-lastframetime; //calculating the total time taken to do the last frame
			try{actualframerate=(int) (1000/lastframeelapsed);}catch(Exception e){} //using the time taken to calculate the actual framerate
			lastframetime=System.currentTimeMillis(); //updating the lastframetime
			return true;
		}
		return false;
	}
	
	//if not already paused, measures how long the current frame has been running for, stores it in pausetimedifference, and sets paused to true.
	public void pause(){
		if(paused)return;//if paused, then just stop executing the method
		pausetimedifference=System.currentTimeMillis()-lastframetime;
		paused=true;
	}
	
	//if the game is paused, then set the new lastframetime equal to the current system time minus the pausetimedifference, and set paused=false
	public void unpause(){
		if(!paused)return; //if already unpaused then stop executing
		lastframetime=System.currentTimeMillis()-pausetimedifference;
		paused=false;
	}
	
	//draws the framerate on to a graphics2D object
	public void draw(Graphics2D image){
		Color tempcol=image.getColor();
		image.setColor(defaultcolor);
		image.setFont(defaultfont);
		image.drawString("Framerate: "+Integer.toString(actualframerate), defaultdrawx, defaultdrawy);
		image.setColor(tempcol);
	}
	
	//all get functions
	public int getdesiredframerate(){return desiredframerate;}
	public int getactualframerate(){if(actualframerate>0){return actualframerate;}else{return 1;}}
	public long getlastpauselength(){return pausetimedifference;}
	public int getlastframetime(){return (int)lastframeelapsed;}
	public boolean getpaused(){return paused;}
	
	//all set functions
	public void setdesiredframerate(int newframerate){
		if(newframerate>1000){desiredframerate=1000;}
		else if(newframerate<=0){desiredframerate=100;}
		else{desiredframerate=newframerate;}
		desiredframeinterval=1000/desiredframerate;
	}
	
	
}
