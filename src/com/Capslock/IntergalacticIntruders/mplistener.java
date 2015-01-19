package com.Capslock.IntergalacticIntruders;

import java.awt.Color;
import java.awt.Font;

import com.Capslock.Engine.game;
import com.Capslock.Engine.netserver;
import com.Capslock.Engine.screen;

//the game mode that starts the process of listening for a client to connect

//if successful, return 1, else return 2

public class mplistener extends game{
	
	private int timeout = 30;
	       
	private double timeremaining = timeout;
	       
	private boolean successful = false;

	public mplistener(screen thescreen) {
		super(thescreen);
	}

	@Override
	public void init() {
		
		System.out.println("MP LISTENER STARTED...");
		
		//starting the listener
		new Thread(new mainlistenerthread()).start();
		
		//starting the timer
		new Thread(new timer()).start();
	}

	@Override
	public void input() {
		if(mainscreen.keyboard.enterpressed){quit=true; successful = false;}		
	}

	@Override
	public void update(){}

	@Override
	public void draw() {
		
		//drawing the background
		I_Intruders.parisbackground.draw(mainscreen.screengraphics, 0, I_Intruders.PARIS_BACKGROUND_Y_OFFSET);
		
		I_Intruders.scaledlogo.draw(mainscreen.screengraphics, 10, mainscreen.getsizey()-78);
				
		//drawing the "listening" text
		mainscreen.screengraphics.setColor(Color.white);
		String message1 = "LISTENING...";
		Font f1 = new Font(Font.DIALOG_INPUT, Font.PLAIN, 65);
		mainscreen.screengraphics.setFont(f1);
		int textlength1 = mainscreen.screengraphics.getFontMetrics().stringWidth(message1);
		mainscreen.screengraphics.drawString(message1, mainscreen.getsizex()/2-textlength1/2, mainscreen.getsizey()/3+75);
		
		
		//drawing the "timeout in:   seconds" text
		mainscreen.screengraphics.setColor(Color.white);
		message1 = "TIMEOUT IN:   "+(Math.ceil(timeremaining*100)/100)+"s";
		f1 = new Font(Font.DIALOG_INPUT, Font.PLAIN, 35);
		mainscreen.screengraphics.setFont(f1);
		textlength1 = mainscreen.screengraphics.getFontMetrics().stringWidth("TIMEOUT IN:   12.12s");
		mainscreen.screengraphics.drawString(message1, mainscreen.getsizex()/2-textlength1/2, mainscreen.getsizey()/3 +175);
		
		
		//drawing the "press enter to stop at any time" text
		mainscreen.screengraphics.setColor(Color.yellow);
		message1 = "PRESS   <ENTER>    TO QUIT AT ANY TIME...";
		f1 = new Font(Font.DIALOG_INPUT, Font.PLAIN, 25);
		mainscreen.screengraphics.setFont(f1);
		textlength1 = mainscreen.screengraphics.getFontMetrics().stringWidth(message1);
		mainscreen.screengraphics.drawString(message1, mainscreen.getsizex()/2-textlength1/2, mainscreen.getsizey()-30);
		
		
	}

	@Override
	public int quit() {
		
		if(successful){
			return 1;
		}else{
			
			//disconnecting the socket so it's possible to try again
			I_Intruders.MP_SERVER.disconnect();
			return 2;
		}
	}
	
	private class mainlistenerthread implements Runnable{
		
		public mainlistenerthread(){}
		
		public void run(){
			I_Intruders.MP_SERVER = new netserver(I_Intruders.MP_SOCKET);
			I_Intruders.MP_SERVER.settimeout(timeout*1000);
			
			if(I_Intruders.MP_SERVER.listen()){
				successful = true;
				quit = true;
			}else{
				successful=false;
				quit=true;
			}
		}
		
		
	}
	
	
	private class timer implements Runnable{
		
		int resolution = 10;//the number of milliseconds subtracted each time
		
		public timer(){}
		
		public void run(){
			
			while(!quit && timeremaining>0){
				timeremaining -= (double)resolution/1000;
				try{Thread.sleep(resolution);}catch(Exception e){quit=true; return;}
			}
			
			successful = false;
			quit=true;
			
		}
		
	}
	

}
