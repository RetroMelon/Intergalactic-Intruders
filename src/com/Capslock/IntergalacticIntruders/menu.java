package com.Capslock.IntergalacticIntruders;

import java.awt.Color;
import java.awt.Font;

import com.Capslock.Engine.*;

public class menu extends game{
	
	protected int BUTTON_Y = 250;
	protected static final int TEXT_Y = 150;
	protected static final int BUTTON_SIZE_Y = 100;
	
	protected String text = "Shoot the option you wish to select...";
	protected Color textcolor = Color.WHITE;
	protected int fontsize = 30;
	protected String[] menuitems;
	protected final int margins = 30; //the number of pixels on either side that will not be considered
	protected final int gap = 20; // the gap left between each button
	
	//some variables for keeping track of input.
	protected boolean left = false;
	protected boolean right = false;
	protected boolean shoot = false;
	protected long lastshoottime = 0;
	
	//used to decide which animation to play
	protected int[] animlist; //this array stores which animation to use for which
	public static final int ANIM_NONE = 0; // does not play any animation, quits immediately
	public static final int ANIM_STAY = 1; // allows the particle effect of the button to play, and sets the playerpersistantx to the current value
	public static final int ANIM_CENTRE = 2; //moves the player to the centre, then quits
	
	//creating the ship as a separate entity, to make it easier to update
	ship player;
	
	//the particle effects used in the bullet and buttons
	peffectfactory playerbulleteffects;
	peffectfactory buttoneffects;
	
	//relating to the volume toggling
	private boolean preventtoggle = false;

	public menu(screen thescreen, String menutext, String... menuitems) {
		super(thescreen);
		this.text = menutext;
		this.menuitems = menuitems;
		
		//creating and populating the animationlist
		animlist = new int[menuitems.length];
		for(int i = 0; i<animlist.length; i++){
			animlist[i]=0;
		}
		
		
	}

	@Override
	public void init() {
		//setting up bullet and button particle effects
		playerbulleteffects = new peffectfactory(this);
		playerbulleteffects.setcolor(new Color[]{ Color.white});
		playerbulleteffects.setforce(20);
		playerbulleteffects.setlifespan(100);
		playerbulleteffects.setgravity(150);
		playerbulleteffects.setparticles(2);
		
		buttoneffects = new peffectfactory(this);
		buttoneffects.setcolor(new Color[]{ Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.GRAY, Color.white});
		buttoneffects.setcolorsingleparticle(true);
		buttoneffects.setminsize(25);
		buttoneffects.setmaxsize(35);
		buttoneffects.setforce(300);
		buttoneffects.setlifespan(500);
		buttoneffects.setgravity(150);
		buttoneffects.setparticles(20);
		buttoneffects.setinitialtransparency((float)0.5);
		
		
		//creating a ship
		player = new ship(mainscreen, 1);
		entities.add(player);
		
		//finding out the width of each button
		int buttonwidth = (mainscreen.getsizex() - 2*margins - (menuitems.length-1)*gap)/menuitems.length;
		
		//creating the buttons and adding them to the entities list
		for(int i=0; i<menuitems.length; i++){
			int xposition = margins + i*(buttonwidth+gap);
			entities.add(new spacebutton(mainscreen, menuitems[i], xposition, BUTTON_Y, buttonwidth, BUTTON_SIZE_Y, i));
		}
		
		//adding a musictoggle button to the top corner
		entities.add(new musictoggle(mainscreen, mainscreen.getsizex()-50-15, 15));
	}

	@Override
	public void input() {
		shoot=mainscreen.keyboard.spacepressed;
		left=mainscreen.keyboard.leftpressed;
		right=mainscreen.keyboard.rightpressed;
	}

	@Override
	public void update() {
		
		for(int x = 0; x<entities.size(); x++){
			entity tempent = entities.get(x);
			
			
			//acting if the entity is the player's ship
			if(tempent instanceof ship){
				
				//acting upon the keyboard inputs
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
						I_Intruders.buttonsound.play();
						destroyentity(entities.get(x1));//destroying the bullet entity
						destroyentity(tempent);
						
						//creating a particle effect, and initiaing a loop to display the particle effect before changing screens
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
			
			
			else if(tempent instanceof musictoggle){
				if(mainscreen.getmousedown() && tempent.checkcols(mainscreen.getmousex(), mainscreen.getmousey())){//if we are clicking the button
					if(!preventtoggle){//making sure we are alloweds to toggle
						if(I_Intruders.musicplaying){
							I_Intruders.helixnebula.stop();
							I_Intruders.musicplaying=false;
						}else{
							I_Intruders.helixnebula.loop();
							I_Intruders.musicplaying=true;
						}
						preventtoggle=true;//setting so we are not allowed to toggle until we have atleast let go of the mouse button again
					}
				}else{preventtoggle=false;}
			}
			
		
		}
		
		
	}

	@Override
	public void draw() {
		I_Intruders.parisbackground.draw(mainscreen.screengraphics, 0, I_Intruders.PARIS_BACKGROUND_Y_OFFSET);
		
		//I_Intruders.scaledlogo.draw(mainscreen.screengraphics, 10, -10);
		//I_Intruders.scaledlogo.draw(mainscreen.screengraphics, mainscreen.getsizex()-170, -10);
		//I_Intruders.scaledlogo.draw(mainscreen.screengraphics, mainscreen.getsizex()-170, mainscreen.getsizey()-85);
		I_Intruders.scaledlogo.draw(mainscreen.screengraphics, 10, mainscreen.getsizey()-78);
		
		if(text.equals("<T>")){//drawing the title image rather than text
			I_Intruders.titlesprite.draw(mainscreen.screengraphics, mainscreen.getsizex()/2-I_Intruders.titlesprite.getsizex()/2, 20);
		}else{
			Font f = new Font(Font.DIALOG_INPUT, Font.PLAIN, fontsize);
			mainscreen.screengraphics.setFont(f);
			int textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(text);
			
			mainscreen.screengraphics.setColor(textcolor);
			mainscreen.screengraphics.drawString(text, (int)(mainscreen.getsizex()/2-textlength/2), TEXT_Y-fontsize);
		}
		
		//drawing the player, bullets, and buttons
		for(int x = 0; x<entities.size(); x++){
			entity tempent = entities.get(x);
			if(tempent instanceof bullet)((bullet)tempent).draw(this);
			else entities.get(x).draw();
		}
	}


	@Override
	public int quit() {
		//deciding which animation to play
		if(animlist[quittype] == this.ANIM_CENTRE){animcentre(); I_Intruders.SHIP_PERSISTANT_X=I_Intruders.SHIP_START_X;}
		
		else if(animlist[quittype] == this.ANIM_STAY){
			animstay(); I_Intruders.SHIP_PERSISTANT_X=(int)player.getx();}
		
		else{I_Intruders.SHIP_PERSISTANT_X=I_Intruders.SHIP_START_X;}
		
		return quittype;
	}
	
	private void animcentre(){
		boolean toofarleft;
		this.updateremoveentities();
		while(true){
			//resetting the input variables
			right=false;
			left=false;
			
			//telling which direction to move the player
			if(player.getx()==I_Intruders.SHIP_START_X){return;}
			else if(player.getx()<I_Intruders.SHIP_START_X){toofarleft=true;}
			else{toofarleft=false;}
			
			//setting the movement directions in accordance with the player's location
			if(toofarleft)right=true;
			else left=true;
			
			//updating (ie, calling the move function to the player)
			update();
			
			//now, telling whether the player has passed the point required
			if(toofarleft){//if it was too far left, and is now too far right
				if(player.getx()>=I_Intruders.SHIP_START_X){return;}
			}else{//if it was too far right, and is now too far left
				if(player.getx()<=I_Intruders.SHIP_START_X){return;}
			}
			
			//finally, drawing
			draw();
			mainscreen.update();
			
			//waiting a little bit, acting as a frame regulator
			try {Thread.sleep(10);} catch (InterruptedException e) {}
		}
	}
	
	private void animstay(){
		this.updateremoveentities();
		for(int i = 0; i<50; i++){
			
			input();
			update();
			draw();
			mainscreen.update();
			
			//waiting a little bit, acting as a frame regulator
			try {Thread.sleep(10);} catch (InterruptedException e) {}
		}
	}
	
	//adds a bullet to the screen.
	protected void addbullet(){
		entities.add(new bullet(this, (int)(entities.get(0).getx()+23), (int)(entities.get(0).gety()+entities.get(0).getspriteoffsetx()), false));
	}

	public void setanim(int menuitemnumber, int value) {//this allows the maker to assign an animation to each of the button values
		try{animlist[menuitemnumber] = value;}catch(Exception e){}
	}
	
}
