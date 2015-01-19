package com.Capslock.IntergalacticIntruders;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Random;

import com.Capslock.Engine.entity;
import com.Capslock.Engine.peffect;
import com.Capslock.Engine.peffectfactory;
import com.Capslock.Engine.screen;

public class spscoredisplay extends menu{
	
	Random random = new Random(System.currentTimeMillis());
	
	//variables related to creating fireworks
	protected peffectfactory fireworksfactory;
	protected int fireworkfrequency = 3; // approximately how any fireworks we want ot display every second
	protected int xmargin = 100;
	protected int ymargin= 150;
	protected int yoffset = -50;//how much the fireworks are offset, aiming to move them up the screen
	
	//values to do with drawing of the stats on screen
	protected int labeloffsetx = 200;// distance from the centre for the labels
	protected int valueoffsetx = 200;
	protected int statbegin = 150;
	protected int stattextsize = 14;
	protected int statspacing = 18;//the amount of space left between each line of stats
	
	//the arrays that store the values for the labels
	private ArrayList<String> labelsarray = new ArrayList<String>();
	private ArrayList<String> valuesarray = new ArrayList<String>();
	
	public spscoredisplay(screen thescreen, String[] menuitems) {
		super(thescreen, "", menuitems);
		//changing the position of the buttons to make them lower so we can fit in all of the text easier.
		this.BUTTON_Y = mainscreen.getsizey()-250;
		
		//setting up the firework factory
		fireworksfactory = new peffectfactory(this);
		fireworksfactory.setcolor( Color.white, Color.red, Color.blue, Color.cyan, Color.pink, Color.yellow, Color.LIGHT_GRAY, Color.green);
		fireworksfactory.setcolorsingleparticle(true);
		fireworksfactory.setforce(200);
		fireworksfactory.setgravity(150);
		fireworksfactory.setlifespan(3000);
		fireworksfactory.setmaxsize(6);
		fireworksfactory.setminsize(3);
		fireworksfactory.setparticles(60);
		
		//adding all the stats to the arrays
		addstat("Total Score:", Integer.toString(I_Intruders.SP_TOTAL_SCORE));
		addstat("Total Deaths:", Integer.toString(I_Intruders.SP_TOTAL_DEATHS));
		addstat("Total Aliens Killed:", Integer.toString(I_Intruders.SP_TOTAL_ALIENS_KILLED));
		addstat("Total Shots Fired:", Integer.toString(I_Intruders.SP_TOTAL_SHOTS_FIRED));
		float percentageaccuracy = ((float)I_Intruders.SP_TOTAL_ALIENS_KILLED/(float)I_Intruders.SP_TOTAL_SHOTS_FIRED)*100;
		addstat("Percentage Accuracy", "%"+Float.toString(percentageaccuracy));
		addstat("Total Distance Travelled:", Double.toString(I_Intruders.SP_TOTAL_DISTANCE_TRAVELLED)+" Km");
		addstat("Total Time Played:", Long.toString(I_Intruders.SP_TOTAL_TIME_PLAYED/1000)+"Seconds");
		addstat("Total Alien Spending on Cloning:", "$"+Integer.toString(Math.abs(random.nextInt()%9999))+" Billion");
		addstat("Total Lives Ruined:", Integer.toString((int)(I_Intruders.SP_TOTAL_ALIENS_KILLED*1.2)));
		addstat("Total n00bz pwnd:", Integer.toString((int)(I_Intruders.SP_TOTAL_ALIENS_KILLED*0.7)));
	}
	
	private void addstat(String label, String value){
		labelsarray.add(label);
		valuesarray.add(value);
	}
	
	public void draw(){
		//drawing the paris background
		I_Intruders.parisbackground.draw(mainscreen.screengraphics, 0, I_Intruders.PARIS_BACKGROUND_Y_OFFSET);
		
		//drawing the particleffects first, then all other entities
		for(int i = 0; i<entities.size(); i++){
			entity tempent = entities.get(i);
			if(tempent instanceof peffect){tempent.draw();}
		}
		
		//drawing everything else
		for(int i = 0; i<entities.size(); i++){
			entity tempent = entities.get(i);
			if(tempent instanceof peffect)continue;
			if(tempent instanceof bullet)((bullet)tempent).draw(this);
			else entities.get(i).draw();
		}
		
		//drawing the wave reached by the player
		mainscreen.screengraphics.setColor(Color.white);
		String message = "You Reached Wave "+I_Intruders.SP_WAVES_COMPLETED;
		Font f = new Font(Font.DIALOG_INPUT, Font.PLAIN, 60);
		mainscreen.screengraphics.setFont(f);
		int textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
		mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, 80);
		
		//drawing the stat text to the screen
		drawstats();
	}
	
	
	public void update(){
		doallmenufunctions();//does all the standard functions for the menu
		
		//now we create some random fireworks :D
		if(random.nextInt()%(int)(frametimer.getactualframerate()/fireworkfrequency) == 0){entities.add( fireworksfactory.createnewpeffect(xmargin+Math.abs(random.nextInt())%(mainscreen.getsizex()-2*xmargin)  , yoffset+ymargin+Math.abs(random.nextInt()) % (mainscreen.getsizey()-2*ymargin)));}
	}
	
	//this method will go through the arrays of stat labels and values, and 
	private void drawstats(){
		mainscreen.screengraphics.setColor(Color.yellow);
		mainscreen.screengraphics.setFont(new Font(Font.DIALOG, Font.PLAIN, stattextsize));
		
		mainscreen.screengraphics.drawString("statistics:", mainscreen.getsizex()/2-labeloffsetx, statbegin-30);
		
		//drawing the labels
		for(int i = 0; i<labelsarray.size(); i++){
			mainscreen.screengraphics.drawString(labelsarray.get(i), mainscreen.getsizex()/2-labeloffsetx, statbegin+i*statspacing);
		}
		
		mainscreen.screengraphics.setColor(Color.white);
		
		//drawing the values
		for(int i = 0; i<valuesarray.size(); i++){
			mainscreen.screengraphics.drawString(valuesarray.get(i), mainscreen.getsizex()/2+valueoffsetx, statbegin+i*statspacing);
		}
	}

	//does all the stuff that the basic menu does
	private void doallmenufunctions(){
		for(int x = 0; x<entities.size(); x++){
			entity tempent = entities.get(x);
			
			
			//acting if the entity is the player's ship
			if(tempent instanceof ship){
				
				//acting upone the keyboard inputs
				if(right)tempent.setaccelx(I_Intruders.SHIP_SPEED);
				else if(left)tempent.setaccelx(-I_Intruders.SHIP_SPEED);
				else tempent.setaccelx(0);
				
				if(shoot && System.currentTimeMillis()>lastshoottime+I_Intruders.SHOOT_TIME_GAP){addbullet(); lastshoottime=System.currentTimeMillis();}
				
				//moving the ship, the checking to make sure it does not go outside any bounds, eg the bounds of the screen.
				tempent.move(frametimer.getactualframerate());
				if(tempent.getx()<=0){
					tempent.setx(0);
					tempent.setdirx(0);
				}else if(tempent.getx()+tempent.getsizex()>=mainscreen.getsizex()){
					tempent.setx(mainscreen.getsizex()-tempent.getsizex());
					tempent.setdirx(0);
				}
			}
		
			
			//acting if the entity is a button
			else if(tempent instanceof spacebutton && !quit){
				
				for(int x1 = 0; x1<entities.size(); x1++){
					if(entities.get(x1) instanceof bullet && entities.get(x1).checkcols(tempent)){ // checking collisions with all bullets
						//there has been a collision
						destroyentity(entities.get(x1));//destroying the bullet entity
						destroyentity(tempent);
						
						//creating a particle effect, and initialising a loop to display the particle effect before changing screens
						entities.add(buttoneffects.createnewpeffect((int)(tempent.getx()+tempent.getsizex()/2), BUTTON_Y+BUTTON_SIZE_Y/2));
						quittype = ((spacebutton) tempent).getnumber();
						quit=true;
						break;
					}
				}
			}
			
			
			
			
			//acting if the entity is a bullet
			else if(tempent instanceof bullet){
				tempent.move(frametimer.getactualframerate());
				if(tempent.gety()<-16){destroyentity(tempent);}
			}
		
		}
	}
}
