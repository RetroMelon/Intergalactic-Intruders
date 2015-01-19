package com.Capslock.Engine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.util.Random;

public class peffect extends entity implements Runnable{

	private game maingame; // the game is here so the particle effect can remove itself from the entities arraylist ocne finished
	private Color[] colours = {new Color(154, 217, 226)};
	private int force = 90; 
	private int gravity = 150;
	private int lifespan = 1000;
	private int particles = 30;
	private int minsize = 2;
	private int maxsize = 6;
	private float initialtransparency = 1;
	private int percentagetimebeforefade = 75;
	
	//other variables
	private float transparency = 1; //this is used later, when the particles effect fades out.
	private Random random = new Random(System.currentTimeMillis());
	public boolean finished = false; //this is here so that the game can manually check if the particle effect has finished, in case it freezes or stops working, it can be removed manually
	
	//this entity requires an array of entities, because each particle is an entity... this is an example of entity-ception :P
	private entity[] entitiesarray;
	
	
	public peffect(game maingame, int x , int y, int lifespan, int particles, int force, int gravity, int minsize, int maxsize, float initialtransparency, int percentagetimebeforefade, Color[] colours) {
		super(maingame.mainscreen, x, y);
		this.maingame = maingame;
		this.particles = particles;
		this.lifespan = lifespan;
		this.force = force;
		this.gravity = gravity;
		this.minsize = minsize;
		this.maxsize = maxsize;
		this.initialtransparency = initialtransparency;
		transparency = initialtransparency;
		this.percentagetimebeforefade = percentagetimebeforefade;
		this.colours = colours;
		setup();
	}
	
	public peffect(game maingame){//this is only used to make a non-existant peffect for debugging
		
		super(maingame.mainscreen, 0, 0);
		this.maingame = maingame;
		this.particles = 0;
		this.lifespan = 100;
		this.force = 0;
		this.gravity = 0;
		this.minsize = 10;
		this.maxsize = 20;
		this.initialtransparency = 1;
		transparency = 1;
		this.percentagetimebeforefade = 100;
		this.colours = new Color[]{Color.blue};
		
	}
	
	
	private Color getparticlecol(){//this method simply returns a random one of the colours from the list
		return colours[Math.abs(random.nextInt())%colours.length];
	}
	private int getparticlesize(){//returns a random size inkeeping with the minsize and maxsize values
		return Math.abs(random.nextInt())%(maxsize-minsize)+minsize;
	}

	//this method overrides the draw method in entity sets the transparency then calls the draw method on all of the sub-particles
	public void draw(){
		if(drawable){
			
			//setting the transparency of all objects drawn
			mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)transparency));
			
			//cycles through each particle and draws it
			mainscreen.screengraphics.setColor(getparticlecol());
			for(int i = 0; i<entitiesarray.length; i++){entitiesarray[i].draw();}
			
			//setting the transparency back to it's original - fully opaque - so as not to cause any problems elsewhere
			mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)1));
		}
	}

	public void setup() {
		if(!finished){
			entitiesarray = new entity[particles];
			//filling the array with particle entities
			for(int i = 0; i<particles; i++){
				//creating the particle
				particle tempent = new particle(maingame.mainscreen, x, y, getparticlecol());
				//setting up size and direction
				int size = getparticlesize();
				tempent.setboundsx(size);
				tempent.setboundsy(size);
				tempent.setaccely(gravity);
				int maxforce = (Math.abs(random.nextInt())%force) + 1;
				int powerx = random.nextInt()%maxforce;
				if(maxforce !=0 && random.nextBoolean())powerx++;
				int powery = (int) Math.sqrt(Math.pow(maxforce, 2) - Math.pow(powerx, 2));
				if(random.nextBoolean())powery *= -1;
				tempent.setdirx(powerx);
				tempent.setdiry(powery);
				//adding it to the entities array that belongs to this peffect
				entitiesarray[i] = tempent;
			}
			
			//everything has been set up, now we will run the thread that moves the particles
			Thread t = new Thread(this);
			t.start();
		}
	}
	
	public void run(){//this is the run method that is called when this is created as a thread. it moves the particles, and fades them out near the end of their lives
		
		//this thread creates a subthread called fader. it is used to fade the particles out taking in to consideration the percentagetimebeforefade value
		new Thread(new fader()).start();
		
		//setting up some values that we will find useful when cycling through the moving stages
		int framerate = 100;
		int timeinterval = 1000/framerate;
		int iterations = framerate*lifespan/1000;
		
		
			//cycling the correct number of times to take the correct length of time.
			//each time we are moving every particle in the entitiesarray, then sleeping the rest of the time
			for(int i = 0; i<iterations; i++){
				          
				for(int e = 0; e<entitiesarray.length; e++){
					try{    
						entitiesarray[e].move(framerate);
					}catch(Exception ex){}//this will quite regularly throw exceptions... i do not understand why, but i've made it so it doesn't harm the other operations
				}
				
				try{Thread.sleep(timeinterval);}catch(Exception e){}
			}
			
			//setting these variables as a precaution so we don't have the particle effect freeze in mid air if it is unable to be removed from the game
			drawable = false;
			finished = true;
		
		//removing the peffect from the entities list in game, however, we try/catch this, because these is a chance it could already have been removed
		try{maingame.destroyentity(this);}catch(Exception e){System.out.println("exception while removing self from maingame.entities in peffect:  "+this.toString());}
		
	}
	
	public int getparticles(){
		return particles;
	}
	
	//a private inner class used by run
	private class fader implements Runnable{
		//this class is used to fade the partiucles out close to the end of their lives
		@Override
		public void run() {
			try{
				if(percentagetimebeforefade==100){return;}//this is just an efficiency thing - if there is no fade required, this will not run
				
				//setting up some values that are useful for coordinating fade
				int framerate = 100;
				int timeinterval = 1000/framerate;
				//							calculaing iterations for full lifespan				taking in to account the percentage of the lifespan to fade
				int iterations = (int)(  (double)lifespan   /   (double)timeinterval   *   (double)(100-percentagetimebeforefade)/100   );
				
				//sleeping until we are required to start fading
				try{Thread.sleep((int)(((double)percentagetimebeforefade/100)*(double)lifespan));}catch(Exception e){}
				
				//now we should start fading
				for(int i = 0; i<iterations; i++){
					transparency = (float)(initialtransparency - ((double)i / (double)iterations)*initialtransparency);
					try{Thread.sleep(timeinterval);}catch(Exception e){}
				}
				//we have finished fading. this thread simply ends
			}catch(Exception e){}
		}
		
	}
	
	
}
