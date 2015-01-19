package com.Capslock.IntergalacticIntruders;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import com.Capslock.Engine.game;
import com.Capslock.Engine.peffectfactory;
import com.Capslock.Engine.screen;

//this is the game that the client score display and server score display will extend, all they will do is override the init function

public abstract class basempscoredisplay extends game{
	
	protected ArrayList<String> P1_titles = new ArrayList<String>();
	protected ArrayList<String> P1_values = new ArrayList<String>();
	
	protected ArrayList<String> P2_titles = new ArrayList<String>();
	protected ArrayList<String> P2_values = new ArrayList<String>();
	
	protected int statspacing = 20;
	
	protected int P1offset = -350;
	protected int P2offset = 100;
	protected int valueoffset = 200;
	
	protected int statstarty = 200;
	
	
	protected Font wavefont = new Font(Font.DIALOG_INPUT, Font.PLAIN, 60);
	protected Font scorefont = new Font(Font.DIALOG_INPUT, Font.PLAIN, 30);
	protected Font timefont = new Font(Font.DIALOG_INPUT, Font.PLAIN, 22);
	protected Font statsfont = new Font(Font.DIALOG_INPUT, Font.PLAIN, 20);
	protected Font pressenterfont = new Font(Font.DIALOG_INPUT, Font.PLAIN, 25);
	
	protected String wavetext = "You reached WAVE: ";
	protected String scoretext = "Total score: ";
	protected String pressentertext = "Press <ENTER> to return to the main menu...";
	
	protected boolean drawpressenter = true;
	
	protected boolean ignoreenter = false;

	public basempscoredisplay(screen thescreen) {
		super(thescreen);
		if(mainscreen.keyboard.enterpressed){ignoreenter=true;}
	}

	@Override
	public void init(){
		addP1stat("YOU:", "");
		addP1stat("", "");
		addP2stat("PLAYER 2:", "");
		addP2stat("", "");
		
		retrievedata();
		
		//starting firework and text flasher threads
		new Thread(new fireworkspawner(this)).start();
		new Thread(new textflasher()).start();
	}
	
	//the method that will be overwritten in the server and client so they can exchange data
	protected abstract void retrievedata();
	
	
	@Override
	public void input() {
		if(mainscreen.keyboard.enterpressed){
			if(!ignoreenter)quit=true;
		}else{ignoreenter=false;}		
	}

	@Override
	public void update() {
		
		statspacing = 20;                                                      
        
		P1offset = -350;                                                       
		P2offset = 100;                                                        
		valueoffset = 250;                                                     
		                                                                       
		statstarty = 200;                                                      
		                                                                       
		                                                                       
		wavefont = new Font(Font.DIALOG_INPUT, Font.PLAIN, 60);               
		scorefont = new Font(Font.DIALOG_INPUT, Font.PLAIN, 30);              
		statsfont = new Font(Font.DIALOG_INPUT, Font.PLAIN, 20);              
		pressenterfont = new Font(Font.DIALOG_INPUT, Font.PLAIN, 25);         
		                                                                      
		wavetext = "WAVE REACHED: ";                                    
		scoretext = "Total score: ";                                        
		pressentertext = "Press <ENTER> to return to the main menu...";     
		
	}

	@Override
	public void draw() {
		//drawing background
		I_Intruders.parisbackground.draw(mainscreen.screengraphics, 0, I_Intruders.PARIS_BACKGROUND_Y_OFFSET);
		
		//drawing fireworks
		for(int i = 0; i<entities.size(); i++){
			entities.get(i).draw();
		}
		
		//drawing logo
		I_Intruders.scaledlogo.draw(mainscreen.screengraphics, 10, mainscreen.getsizey()-78);
		
		//drawing press enter text
		if(drawpressenter){
			mainscreen.screengraphics.setFont(pressenterfont);
			mainscreen.screengraphics.setColor(Color.YELLOW);
			mainscreen.screengraphics.drawString(pressentertext, getcentredx(pressentertext, pressenterfont), mainscreen.getsizey()-40);
		}
		
		//drawing wave reached
		mainscreen.screengraphics.setFont(wavefont);
		mainscreen.screengraphics.setColor(Color.WHITE);
		mainscreen.screengraphics.drawString(wavetext+(I_Intruders.MP_WAVES_COMPLETED+1), getcentredx(wavetext, wavefont), 90);
		
		//drawing total score
			mainscreen.screengraphics.setFont(scorefont);
			mainscreen.screengraphics.setColor(Color.WHITE);
			mainscreen.screengraphics.drawString(scoretext+I_Intruders.MP_SCORE, getcentredx(scoretext, scorefont), 120);
			
			//drawing total score
			mainscreen.screengraphics.setFont(timefont);
			mainscreen.screengraphics.setColor(Color.WHITE);
			mainscreen.screengraphics.drawString("Time in-game:   "+(float)(I_Intruders.MP_TOTAL_TIME_PLAYED/1000)+" Seconds", getcentredx(("Time in-game:   "+(float)(I_Intruders.MP_TOTAL_TIME_PLAYED/1000)+" Seconds"), timefont), 150);
		
		//drawing p1 stats
		for(int i = 0; i<P1_titles.size(); i++){
			mainscreen.screengraphics.setFont(statsfont);
			mainscreen.screengraphics.setColor(Color.YELLOW);
			mainscreen.screengraphics.drawString(P1_titles.get(i), mainscreen.getsizex()/2+P1offset, statstarty+i*statspacing);
			
			mainscreen.screengraphics.setFont(statsfont);
			mainscreen.screengraphics.setColor(Color.WHITE);
			mainscreen.screengraphics.drawString(P1_values.get(i), mainscreen.getsizex()/2+P1offset+valueoffset, statstarty+i*statspacing);
		}
		
		
		//drawing p2 stats
		for(int i = 0; i<P2_titles.size(); i++){
			mainscreen.screengraphics.setFont(statsfont);
			mainscreen.screengraphics.setColor(Color.YELLOW);
			mainscreen.screengraphics.drawString(P2_titles.get(i), mainscreen.getsizex()/2+P2offset, statstarty+i*statspacing);
			
			mainscreen.screengraphics.setFont(statsfont);
			mainscreen.screengraphics.setColor(Color.WHITE);
			mainscreen.screengraphics.drawString(P2_values.get(i), mainscreen.getsizex()/2+P2offset+valueoffset, statstarty+i*statspacing);
		}
		
		
	}
	
	protected int getcentredx(String text, Font font){
		Font tempfont = mainscreen.screengraphics.getFont();
		
		mainscreen.screengraphics.setFont(font);
		int textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(text);
		
		mainscreen.screengraphics.setFont(tempfont);
		
		int x = mainscreen.getsizex()/2 - textlength/2;
		
		return x;

	}

	@Override
	public int quit() {
		disconnect();
		fadeout();
		return 0;
	}
	
	private void fadeout(){
		
	}
	
	protected void addP1stat(String label, String value){
		P1_titles.add(label);
		P1_values.add(value);
	}
	
	protected void addP2stat(String label, String value){
		P2_titles.add(label);
		P2_values.add(value);
	}
	
	public abstract void disconnect();//used to disconnect the socket

	
	private class fireworkspawner implements Runnable{

		@SuppressWarnings("unused")
		private game maingame;
		
		private peffectfactory fireworksfactory;
		
		//some values to do with the firework spawning
		private int xmargin = 100;
		private int ymargin = 150;
		private int yoffset = -50;
		private int fireworkfrequency = 2;
		
		public fireworkspawner(game maingame){
			this.maingame = maingame;
			
			//setting up the fireworkfactory
			fireworksfactory = new peffectfactory(maingame);
			fireworksfactory.setcolor( Color.white, Color.red, Color.blue, Color.cyan, Color.pink, Color.yellow, Color.LIGHT_GRAY, Color.green);
			fireworksfactory.setcolorsingleparticle(true);
			fireworksfactory.setforce(200);
			fireworksfactory.setgravity(150);
			fireworksfactory.setlifespan(3000);
			fireworksfactory.setmaxsize(6);
			fireworksfactory.setminsize(3);
			fireworksfactory.setparticles(60);
		}

		public void run() {
			try{//we "try" this in case the game ends, so that we do not throw an exception because we can't find the entities array or something
				while(!quit){
					if(random.nextInt()%(int)(100/fireworkfrequency) == 0){entities.add(fireworksfactory.createnewpeffect(xmargin+Math.abs(random.nextInt())%(mainscreen.getsizex()-2*xmargin)  , yoffset+ymargin+Math.abs(random.nextInt()) % (mainscreen.getsizey()-2*ymargin)));}
					Thread.sleep(10);
				}
			}catch(Exception e){}
		}
		
	}
	
	private class textflasher implements Runnable{
		
		private int delay = 400;
		
		public textflasher(){}

		public void run() {
			while(!quit){
				drawpressenter=true;
				
				try{Thread.sleep(delay);}catch(Exception e){}
				
				drawpressenter=false;
				
				try{Thread.sleep(delay);}catch(Exception e){}
			}			
			
			drawpressenter=true;
		}
	}
}
