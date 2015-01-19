package com.Capslock.Engine;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;


//the particle effect factory generates oldpeffect entities of the given specification
public class peffectfactory {
	
	public static final boolean DEBUG_EFFECTS = false; 	// effectively switches the particle effects off... no particle effect is added... this does not effect the "createnewpeffect" method... only the ones which actually add it straight to the game
														//true means no effects, false means effects as normal...
	
	//the oldpeffect entities need a game in order to remove themselves automatically once they complete
	private game maingame;
	//particle effects can have multiple colors, so this stores all possible colours
	private ArrayList<Color> colors = new ArrayList<Color>();
	//this variable decides whether each particle EFFECT should be a diferent color, or whether each PARTICLE should be a different colour.
	private boolean colorsingleparticles = false;
	
	//some physical properties of the particles
	private int launchforce = 90; //how fast/powerfully the particles are launched from the origin
	private int gravity = 150;
	private int lifespan = 1000;
	private int particles = 20;
	private int minsize = 2;
	private int maxsize = 6;
	private float initialtransparency = 1;
	private int percentagetimebeforefade = 75; //this is the percentage of the time in which the particles are opaque, after this they will start to fade
	
	
	//other objects/variables
	Random random = new Random(System.currentTimeMillis());
	
	
	
	//constructor
	public peffectfactory(game maingame){
		this.maingame = maingame;
	}
	
	public peffectfactory(){}
	
	//all set methods
	public void setcolor(Color... colours){
		colors.clear();
		for(int i = 0; i<colours.length; i++){
			colors.add(colours[i]);
		}
	}
	public void setcolorsingleparticle(boolean newval){colorsingleparticles = newval;}
	public void setforce(int newforce){launchforce = newforce;}
	public void setgravity(int newgravity){gravity = newgravity;}
	public void setlifespan(int newlifespan){lifespan = newlifespan;}
	public void setparticles(int newparticles){particles = newparticles;}
	public void setminsize(int newminsize){minsize = newminsize;}
	public void setmaxsize(int newmaxsize){maxsize = newmaxsize;}
	public void setpercentagetimebeforefade(int newpercentagetimebeforefade){percentagetimebeforefade = newpercentagetimebeforefade;}
	public void setinitialtransparency(float newval){initialtransparency = newval;}
	
	public peffect createnewpeffect(int x, int y){
		if(DEBUG_EFFECTS){return new peffect(maingame);}
		if(maxsize<=minsize){maxsize=minsize+1;}
		if(colorsingleparticles){
			return new peffect(maingame, x, y, lifespan, particles, launchforce, gravity, minsize, maxsize, initialtransparency, percentagetimebeforefade, colors.toArray(new Color[colors.size()]));
		}
		else return new peffect(maingame, x, y, lifespan, particles, launchforce, gravity, minsize, maxsize, initialtransparency, percentagetimebeforefade, getrandomcolor());
	}
	
	public peffect createnewpeffect(game maingame, int x, int y){
		this.maingame = maingame;
		return createnewpeffect(x, y);
	}
	private Color[] getrandomcolor() {
		Color[] tempcol = {colors.get(Math.abs(random.nextInt())%colors.size())};
		return tempcol;
	}

	public entity createnewpeffect(game maingame2, double d, double e) {
		return createnewpeffect(maingame2, (int)d, (int)e);
	}
}
