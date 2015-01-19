package com.Capslock.Engine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Random;

public abstract class game {
	
	/*
	 * GAME CLASS
	 * 
	 * the game class is designed to be extended by a more specific class, which should declare it's own methods based on it's desired functionality.
	 * it includes a set of methods which are private and non-abstract. these methods are designed to do some of the basic features
	 * that the user almost always wants, so they are not required to implement the functionality themselves. 
	 * game classes should ideally not store any permanent resources (sounds or sprites), as these will be duplicated if multiple instances of the games are created.
	 * 
	 * the main elements inside the main loop (input, update, draw) are available to be overridden, and are called input, update and draw.
	 * however, the functions that are actually called are named gameinput, gameupdate and gamedraw. which are private methods which add in some extra necessary code, so the user is not required to add it in.
	 * 
	 * the pausing of the game is handled by the input method and must be activated using the "pause()" method, however the unpausing of the game is handled by the loop that executes while the game is paused
	 *  
	 */
	
	//necessary objects for the game class
	public screen mainscreen;
	protected int desiredframerate = 100;
	protected framerate frametimer = new framerate(desiredframerate);
	protected ArrayList<entity> entities = new ArrayList<entity>();
	protected ArrayList<entity> removelist = new ArrayList<entity>();
	protected Random random= new Random(System.currentTimeMillis());//random numbers are always useful
	
	//properties of the game, such as it's current state
	protected boolean quit = false;
	protected int quittype = 0; // can be used if the game is going to do some kind of animation on quitting
	protected int pausequittype = 0; // this is the quit type that will be returned by the default action of the pause function
	protected boolean paused = false;
	protected boolean breakpause = false;//used by the pauseaction method if it wants to break the pause cycle
	
	
	public game(screen thescreen){
		mainscreen = thescreen;
	}
	public game(screen thescreen, boolean startoncreate){//gives the option to start the game as soon as it is created. this does not allow for the returning of a quittype integer. so should only be used for basic game types
		mainscreen = thescreen;
		if(startoncreate)run();
	}
	
	//this is the method that contains the main game loop. it calls all of the methods that are not accessible from outside
	//the class such as the "gamedraw" function as opposed to the ones intended to be overridden by classes that inherit from game.
	//it returns the integer which is returned when the loop quits using the "quit" 
	public int run(){
		frametimer.pause();
		gameinit();
		frametimer.unpause();
		while(!quit){
			if(paused){
				while(mainscreen.keyboard.escpressed && !breakpause){pauseaction();}//the key will still be pressed down.
				while(!mainscreen.keyboard.escpressed && !breakpause){pauseaction();try{Thread.sleep(2);}catch(Exception e){}}//the key is now no longer pressed down
				while(mainscreen.keyboard.escpressed && !breakpause){pauseaction(); try{Thread.sleep(2);}catch(Exception e){}}//the key has been pressed down again, meaning it is about to be released.  
				breakpause=false;
				unpause();
			}
			else if(frametimer.checkframe()){
				gameinput();
				gameupdate();
				gamedraw();
			}
		}
		return quit();
	}
	
	//a method that is used to initialise/clear all variables, by the game. this should not be changed in any other game classes.
	//it also calls the init method, which IS changed between game classes. This method basically sets up all variables used within the game
	private void gameinit(){
		desiredframerate=100;
		entities.clear();
		random = new Random(System.currentTimeMillis());
		init();//this init goes first incase we change the value of desiredframerate
		frametimer = new framerate(desiredframerate);
	}
	//the init class should contain the init/clearing of variables that the user creates when they extend the game class.
	public abstract void init();
	
	//**********************************************************************************//
	//***************************all methods related to input***************************//
	//**********************************************************************************//
	
	//the input method that the game uses. it has no real purpose other than to remain consistent with the other classes
	private void gameinput(){input();}
	
	//this is the method that is overridden by the subclass. that will contain ifs to check if certain keys are pressed
	public abstract void input();
	
	//**********************************************************************************//
	//**************************all methods related to updating*************************//
	//**********************************************************************************//
	
	//the gameupdate method does normal updates, then if any entites are to be destroyed, it destroys them, then clears the "removelist" list
	private void gameupdate(){
		update();
		updateremoveentities();
	}
	
	//this is the method that the sub class will override. it will be used to move entities, and update scores, etc
	public abstract void update();
	
	//**********************************************************************************//
	//**************************all methods related to drawing**************************//
	//**********************************************************************************//
	
	//calls the draw method which will contain all of the drawing method calls after being overridden by the user.
	//this function will then 
	private void gamedraw(){
		draw();
		mainscreen.update();
	}
	
	//this method will contain all of the drawing stuffs as specified by the sub class of this. this will NOT require to call the screen.update() function when finished, the gamedraw method does that. :P
	public abstract void draw();
	
	//**********************************************************************************//
	//***************************some other important methods***************************//
	//**********************************************************************************//
	
	//the method that is called when the game loop should start. this returns an integer called the quittype, which can be used to determine the next action that the main class should take
	public abstract int quit();
	
	//adds the entity to a list, and after the update phase, the entity will be remove from the entities array.
	public void destroyentity(entity e){removelist.add(e);}
	
	//this method is used to actually remove the entities. in case the game needs to do this at a time other than in the update loop.
	protected void updateremoveentities(){
		try{entities.removeAll(removelist);}catch(Exception e){return;}
		removelist.clear();
	}
	
	//**********************************************************************************//
	//**************************all methods related to pausing**************************//
	//**********************************************************************************//
	//
	//to do with pausing
	public void pause(){frametimer.pause(); paused=true;}
	public void unpause(){frametimer.unpause(); paused=false;}
	//what we do while paused. this is executed repeatedly in a loop
	protected void pauseaction(){
		draw();
		mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.72));
		mainscreen.screengraphics.setColor(Color.DARK_GRAY);
		mainscreen.screengraphics.fillRect(0, 0, mainscreen.getsizex(), mainscreen.getsizey());
		mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)1));
		mainscreen.screengraphics.setColor(Color.white);
		String message = "GAME PAUSED!";
		Font f = new Font(Font.DIALOG_INPUT, Font.BOLD, 23);
		mainscreen.screengraphics.setFont(f);
		int textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
		mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2);
		message= "Press ESC to unpause, or ENTER to return to the previous screen.";
		f = new Font(Font.DIALOG_INPUT, Font.BOLD, 18);
		mainscreen.screengraphics.setFont(f);
		textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
		mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2+40);
		if(mainscreen.keyboard.enterpressed){quittype = pausequittype; quit=true; breakpause=true; while(mainscreen.keyboard.enterpressed){try{Thread.sleep(2);}catch(Exception e){}}}
		mainscreen.update();
	}
	public void addentity(entity newent) {entities.add(newent);}

}
