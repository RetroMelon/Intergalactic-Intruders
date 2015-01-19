package com.Capslock.IntergalacticIntruders;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;

import com.Capslock.Engine.game;
import com.Capslock.Engine.instructiontracker;
import com.Capslock.Engine.peffectfactory;
import com.Capslock.Engine.screen;

public class instructions extends game{
	
	///the tracker used to move the text
	private instructiontracker tracker;
	
	private int instructionsize = 0;//the size in pixels of all the text down to the end of the instructions
	private int totalsize = 0;//the size of all of the text including the press enter text and the bottom spacer
	private int bottomspacer = 40; // a little extra space left at the bottom of the screen
	
	
	//actual text
	private String title = "Instructions";
	private String pressenter = "Press <Enter> to return to the main menu...";
	private String[] instructions;//3 spaces between colons and names
	
	
	//text sizes
	private int titletextsize = 40;
	private int instructiontextsize = 20;
	private int pressentersize = 25;
	
		
	//text positions on the y axis from the textstarty
	private int titletexty = 100;//from the tracker to the title
	private int instructiontexty = 170;//from the tracker to the instructions
	private int pressentertexty = 0;//this distance will be calculated on starting the game
	
	private int pressenterspacing = 100;//the space between the bottom of the instructions and the bottom of the pressenter text
	private int instructionspacing = 25;
	
	//text colours
	private Color titlecolor = Color.yellow;
	private Color instructioncolor = Color.white;
	private Color pressentercolor = Color.yellow; // Color.LIGHT_GREY
	
	
	//text animation
	private int scrollspeed = 700; //number of pixels per second
	private boolean drawpressenter = false;
	
	
	
	public instructions(screen thescreen, String title, String[] body) {
		super(thescreen);
		this.title = title;
		instructions=body;
	}

	@Override
	public void init() {
		//start a firework spawner
		new Thread(new fireworkspawner(this)).start();
		
		new Thread(new textflasher()).start();
		
		tracker = new instructiontracker(mainscreen, 0, 0);
		tracker.setdebugbounds(true);
		//tracker.setdrawable(false)
		tracker.setfrictiony(100);
		
		
		//calculating some variables like the size of the text
		//checking whether the tracker is out of bounds.
				instructionsize = instructiontexty+instructionspacing*instructions.length;
				pressentertexty = instructionsize+pressenterspacing;
				totalsize = pressentertexty+bottomspacer;
				
				
		tracker.sety(0);
	}

	@Override
	public void input() {
		quit = mainscreen.keyboard.enterpressed;
		
		if(mainscreen.keyboard.uppressed){tracker.setdiry(scrollspeed);}
		else if(mainscreen.keyboard.downpressed){tracker.setdiry(-scrollspeed);}
	}

	@Override
	public void update() {
		//assigning the variables every frame so we can easily adjust when debugging
		
		titletextsize = 40;
		instructiontextsize = 20;
		pressentersize = 25;

		titletexty = 150;
		instructiontexty = 250;
		
		scrollspeed = 400;
		
		tracker.setfrictiony(600);
		
		instructionsize = instructiontexty+instructionspacing*instructions.length;
		pressentertexty = instructionsize+pressenterspacing;
		totalsize = pressentertexty+bottomspacer;
		
		
		//moving the tracker
		tracker.move(frametimer.getactualframerate());		
		
		//checking the tracker is not out of bounds
		if(tracker.gety()>0){//the text is at the top, it should not go anyhigher
			tracker.sety(0);
		}
		else if(tracker.gety()<-(totalsize-mainscreen.getsizey())){//the tracker has reached the top of the screen
			tracker.sety(-(totalsize-mainscreen.getsizey()));
		}
		
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
		
		mainscreen.screengraphics.drawString(title, x, (int)tracker.gety()+titletexty);
		
		
		//drawing the actual instructions
		
		for(int i = 0; i<instructions.length; i++){
			x = getcentre(instructions[i], instructiontextsize, Font.DIALOG_INPUT, Font.PLAIN);
			
			mainscreen.screengraphics.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, instructiontextsize));
			mainscreen.screengraphics.setColor(instructioncolor);
			
			mainscreen.screengraphics.drawString(instructions[i], x, (int)tracker.gety()+instructiontexty+i*instructionspacing);
		}
		
		
		//drawing the press enter text
		if(drawpressenter){
			x = getcentre(pressenter, pressentersize, Font.DIALOG_INPUT, Font.PLAIN);
			
			mainscreen.screengraphics.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, pressentersize));
			mainscreen.screengraphics.setColor(pressentercolor);
			
			mainscreen.screengraphics.drawString(pressenter, x, (int)tracker.gety()+pressentertexty);
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
