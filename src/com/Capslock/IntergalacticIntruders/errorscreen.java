package com.Capslock.IntergalacticIntruders;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;

import com.Capslock.Engine.game;
import com.Capslock.Engine.screen;

public class errorscreen extends game{

	private String[] message;
	
	private boolean drawpressenter = false;
	
	private boolean quittable = true; // if true, we should be allowed to quit if enter is pressed
	
	public errorscreen(screen thescreen, String... message) {
		super(thescreen);
		this.message = message;
		System.out.println("Error screen with message:   "+message[0]);
	}

	@Override
	public void init() {
		new Thread(new textflasher()).start();
		quittable = !mainscreen.keyboard.enterpressed;
	}

	@Override
	public void input() {
		if(mainscreen.keyboard.enterpressed){
			
			quit = quittable && mainscreen.keyboard.enterpressed;
		
		}else{quittable=true;}
		
	}

	@Override
	public void update() {
		
	}

	@Override
	public void draw() {
		//drawing the background
		I_Intruders.parisbackground.draw(mainscreen.screengraphics, 0, I_Intruders.PARIS_BACKGROUND_Y_OFFSET);
		
		I_Intruders.scaledlogo.draw(mainscreen.screengraphics, 10, mainscreen.getsizey()-78);
		
		//drawing all the text
		
		int x = 0;
		Font f = new Font(Font.DIALOG_INPUT, Font.PLAIN, 38);
		
		//drawing the word "message"
		mainscreen.screengraphics.setFont(f);
		mainscreen.screengraphics.setColor(Color.yellow);
		String m = "MESSAGE";
		x = getcentre(m, f);
		mainscreen.screengraphics.drawString(m, x, 100);
		
		//drawing the actual error message
		for(int i = 0; i<message.length; i++){
			f = new Font(Font.DIALOG_INPUT, Font.PLAIN, 24);
			
			mainscreen.screengraphics.setFont(f);
			mainscreen.screengraphics.setColor(Color.white);
			m = message[i];
			x = getcentre(m, f);
			
			mainscreen.screengraphics.drawString(m, x, 280+i*30);
		}
		
		if(drawpressenter){
			f = new Font(Font.DIALOG_INPUT, Font.PLAIN, 20);
			
			mainscreen.screengraphics.setFont(f);
			mainscreen.screengraphics.setColor(Color.yellow);
			m = "Press <Enter> to continue...";
			x = getcentre(m, f);
			
			mainscreen.screengraphics.drawString(m, x, mainscreen.getsizey()-30);
		}
		
	}

	@Override
	public int quit() {
		//do a fading animation
		int totalframes = 50;
		
		for(int i = 0; i<totalframes; i++){
			
			try{Thread.sleep(10);}catch(Exception e){}
			draw();
			
			mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)((float)i/(float)totalframes)));
			mainscreen.screengraphics.setColor(Color.black);
			mainscreen.screengraphics.fillRect(0, 0, mainscreen.getsizex(), mainscreen.getsizey());
			
			mainscreen.update();
			
		}
		try{Thread.sleep(60);}catch(Exception e){}
		
		return 0;
	}
	
	//gets the x position that the text should start at
	private int getcentre(String message, Font f1){
		Font f = f1;
		mainscreen.screengraphics.setFont(f);
		int textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
		
		int x = mainscreen.getsizex()/2 - textlength/2;
		
		return x;
	}
	
	private class textflasher implements Runnable{
		
		private int delay = 700;
		
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
