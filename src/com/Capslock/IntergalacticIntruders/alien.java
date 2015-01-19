package com.Capslock.IntergalacticIntruders;

import java.awt.Color;

import com.Capslock.Engine.*;

public class alien extends entity{
	
	private boolean alive = true;

	//uysed to offset the aliens based on the location they are in the 2 dimensional array
	private int alienoffsetx = 0;
	private int alienoffsety = 0;
	
	public static int sizex = I_Intruders.aliensprite.getsizex();
	public static int sizey = I_Intruders.aliensprite.getsizey();
	
	private static peffectfactory explosioneffects;
	
	static{ //setting up the particle effect factory
		explosioneffects = new peffectfactory();
		explosioneffects.setcolor(new Color[]{ new Color(154, 217, 226), new Color(154, 217, 226),new Color(154, 217, 226),new Color(154, 217, 226), new Color(154, 217, 226), new Color(154, 217, 226), new Color(255, 238, 95), new Color(243, 105, 53)});
		explosioneffects.setforce(100);
		explosioneffects.setlifespan(500);
		explosioneffects.setcolorsingleparticle(true);
		explosioneffects.setgravity(150);
		explosioneffects.setminsize(2);
		explosioneffects.setmaxsize(15);
		explosioneffects.setparticles(30);
	}
	
	public alien(screen mainscreen) {
		super(mainscreen);
	}

	@Override
	public void setup() {
		collisionmode = entity.COLLISIONMODE_CUSTOMBOUNDS;
		boundsx = I_Intruders.ALIEN_SIZE_X;
		boundsy = I_Intruders.ALIEN_SIZE_X;
		drawmode = entity.DRAWMODE_CLIP;
		addsprite(I_Intruders.aliensprite);
	}
	
	public void kill(game maingame){
		//this will set it to non-drawable, and non-collidable, and also adds a particle effect entity to the game.
		I_Intruders.alienexplode.play();
		maingame.addentity(explosioneffects.createnewpeffect(maingame, (int)(this.getx()+this.getsizex()/2), (int)(this.gety()+(this.getsizey()/3)*2)));
		kill();
	}
	
	public void kill(){
		//this will set it to non-drawable, and non-collidable
		collideable = false;
		drawable = false;
		alive=false;
	}

	public void respawn(){
		collideable = true;
		drawable = true;
		alive=true;
	}
	
	public void shoot(game maingame){//this creates an alien bullet at the correct position in the game that is passed in to this method
		maingame.addentity(new bullet(maingame, (int)(this.getx()+this.getsizex()/2-1), (int)(this.gety()+this.getsizey()), true));
	}

	
	//getters and setters for the offsets
	public int getalienoffsetx() {return alienoffsetx;}
	public void setalienoffsetx(int alienoffsetx) {this.alienoffsetx = alienoffsetx;}
	
	public int getalienoffsety() {return alienoffsety;}
	public void setalienoffsety(int alienoffsety) {this.alienoffsety = alienoffsety;}
	
	public peffectfactory getparticlegenerator(){return explosioneffects;}
	
	public boolean isalive(){return alive;}
	
	public void setalienposition(entity e){//passes in an entity. the alien then positions itself relative to that entity, based on the "alienoffsetx" and "alienoffsety"
		setx(e.getx()+this.alienoffsetx);
		sety(e.gety()+this.alienoffsety);
	}
}
