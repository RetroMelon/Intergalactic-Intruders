package com.Capslock.IntergalacticIntruders;

import java.awt.Color;

import com.Capslock.Engine.entity;
import com.Capslock.Engine.game;
import com.Capslock.Engine.peffectfactory;
import com.Capslock.Engine.screen;

public class bullet extends entity{
	
	private boolean alien = false;
	private Color bulletcolor = Color.white;
	
	private game maingame;
	
	private peffectfactory bullettraileffect;
	
	public bullet(game maingame, int x, int y, boolean alien) {
		super(maingame.mainscreen);
		I_Intruders.alienshoot.play();
		this.maingame = maingame;
		this.x = x; 
		this.y = y;
		this.alien = alien;
		
		//initialising bullet trail particle effects factory
		bullettraileffect = new peffectfactory(maingame);
		bullettraileffect.setforce(30);
		bullettraileffect.setminsize(2);
		bullettraileffect.setmaxsize(4);
		bullettraileffect.setlifespan(100);
		
		//if a true is passed, then the bullet is coloured green, it is shot in the downwards direction, and the varable "alien" is set to true
		if(alien){//is alien
			I_Intruders.alienshoot.play();
			this.diry = I_Intruders.ALIEN_BULLET_SPEED;
			bulletcolor = Color.GREEN;
			
			//setting up particle effects factory for alien bullets
			bullettraileffect.setparticles(4);
			bullettraileffect.setgravity(-50);
			bullettraileffect.setlifespan(200);
			bullettraileffect.setforce(40);
			bullettraileffect.setcolor(Color.green);
			
		}else{//is player's
			//I_Intruders.playershoot.play();    -- got rid of the sound effect since it was causing the game to seize up
			diry = -I_Intruders.PLAYER_BULLET_SPEED;
			
			//setting up particle effects factory for player bullets
			bullettraileffect.setparticles(2);
			bullettraileffect.setgravity(-100);
			bullettraileffect.setcolor(Color.white);
		}
		
	}

	@Override
	public void setup() {
		boundsx = I_Intruders.BULLET_BOUNDS_X;
		boundsy = I_Intruders.BULLET_BOUNDS_Y;
		collisionmode = entity.COLLISIONMODE_CUSTOMBOUNDS;
		
	}
	
	public void draw(game maingame){
		maingame.addentity(bullettraileffect.createnewpeffect(maingame, (int)x+2, (int)y+(int)this.getsizey()-2));
		this.draw();
	}
	
	public void draw(){
		mainscreen.screengraphics.setColor(bulletcolor);
		mainscreen.screengraphics.fillRect((int)x, (int)y, (int)boundsx, (int)boundsy);
	}
	
	public boolean isalien(){return alien;}

}
