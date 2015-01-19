package com.Capslock.IntergalacticIntruders;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;

import com.Capslock.Engine.game;
import com.Capslock.Engine.peffectfactory;
import com.Capslock.Engine.screen;

public class credits extends game{
	
	//actual text
	private String title = "CREDITS";
	private String pressenter = "Press <Enter> to return to the main menu...";
	private String[] credits = {	"Game + Engine:   Joe Frew  ",
									"     Graphics:   Ryan Steel",
									"",
									"",
									"        Music:   ???       "
												};//3 spaces between colons and names
	
	
	//text sizes
	private int titletextsize = 40;
	private int credittextsize = 20;
	private int pressentersize = 25;
	
		
	//text positions on the Y axis... from the top
	private int titletexty = 100;
	private int credittexty = 170;
	private int pressentertexty = 250;
	
	private int creditspacing = 25;
	
			
	
	//text colours
	private Color titlecolor = Color.yellow;
	private Color creditcolor = Color.white;
	private Color pressentercolor = Color.yellow; // Color.LIGHT_GREY
	
	
	//text animation
	private int scrollspeed = 700; //number of pixels per second
	private boolean drawpressenter = false;
	private boolean complete = false;
		
	
	///////////////////////////////////////////////////////////////////////////////////
	//variables that we should not need to edit
	private int currentglobaly = 700;
	
	
	public credits(screen thescreen) {
		super(thescreen);
	}

	@Override
	public void init() {
		//start a firework spawner
		new Thread(new fireworkspawner(this)).start();
		
		new Thread(new textflasher()).start();
	}

	@Override
	public void input() {
		quit = mainscreen.keyboard.enterpressed;
	}

	@Override
	public void update() {
		//assigning the variables every frame so we can easily adjust when debugging
		title = "CREDITS";
		pressenter = "Press <Enter> to return to the main menu...";
		credits[0] = "Game + Engine   :   Joe Frew     ";
		credits[1] = "   Background   :   Ryan Steel   ";
		credits[2] = "";
		credits[3] = "        Music   :   'Helix Nebula' ";
		credits[4] = "                    by Anamanaguchi";
		
		titletextsize = 40;
		credittextsize = 20;
		pressentersize = 25;

		titletexty = 150;
		credittexty = 250;
		pressentertexty = mainscreen.getsizey()-30;
		
		scrollspeed = 500;
		
		//doing the moving thing
		if(currentglobaly>=0){currentglobaly-=scrollspeed/frametimer.getactualframerate();}
		else{if(!complete){drawpressenter=true;} complete = true;}//setting "complete equal to true
		
	}

	@Override
	public void draw() {
		
		I_Intruders.parisbackground.draw(mainscreen.screengraphics, 0, I_Intruders.PARIS_BACKGROUND_Y_OFFSET);
		
		for(int i = 0; i<entities.size(); i++){
			entities.get(i).draw();
		}
		
		I_Intruders.scaledlogo.draw(mainscreen.screengraphics, 10, mainscreen.getsizey()-78);
		
		int x;
		//drawing the title
		x = getcentre(title, titletextsize, Font.DIALOG_INPUT, Font.PLAIN);
		
		mainscreen.screengraphics.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, titletextsize));
		mainscreen.screengraphics.setColor(titlecolor);
		
		mainscreen.screengraphics.drawString(title, x, currentglobaly+titletexty);
		
		
		//drawing the actual credits
		x = getcentre(credits[0], credittextsize, Font.DIALOG_INPUT, Font.PLAIN);
		
		for(int i = 0; i<credits.length; i++){
			mainscreen.screengraphics.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, credittextsize));
			mainscreen.screengraphics.setColor(creditcolor);
			
			mainscreen.screengraphics.drawString(credits[i], x, currentglobaly+credittexty+i*creditspacing);
		}
		
		
		//drawing the press enter text
		if(drawpressenter){
			x = getcentre(pressenter, pressentersize, Font.DIALOG_INPUT, Font.PLAIN);
			
			mainscreen.screengraphics.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, pressentersize));
			mainscreen.screengraphics.setColor(pressentercolor);
			
			mainscreen.screengraphics.drawString(pressenter, x, currentglobaly+pressentertexty);
		}
		
		
	}
	
	//gets the x position that the text should start at
	private int getcentre(String message, int textsize, String fonttype, int bolditalic){
		Font f = new Font(Font.DIALOG_INPUT, bolditalic, textsize);
		mainscreen.screengraphics.setFont(f);
		int textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
		
		int x = mainscreen.getsizex()/2 - textlength/2;
		
		return x;
	}

	@Override
	public int quit() {
		//do a fading animation
		int totalframes = 50;
		
		for(int i = 0; i<totalframes; i++){
			
			try{Thread.sleep(10);}catch(Exception e){}
			
			update();
			draw();
			
			mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)((float)i/(float)totalframes)));
			mainscreen.screengraphics.setColor(Color.black);
			mainscreen.screengraphics.fillRect(0, 0, mainscreen.getsizex(), mainscreen.getsizey());
			
			mainscreen.update();
			
		}
		try{Thread.sleep(60);}catch(Exception e){}
		return 0;
	}
	
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
		
		private int delay = 700;
		
		public textflasher(){}

		public void run() {
			while(!drawpressenter){try{Thread.sleep(50);}catch(Exception e){}} // waiting for the text to finish coming on to the screen
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
