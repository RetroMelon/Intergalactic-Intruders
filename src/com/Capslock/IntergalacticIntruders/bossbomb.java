package com.Capslock.IntergalacticIntruders;

import java.awt.Color;

import com.Capslock.Engine.entity;
import com.Capslock.Engine.game;
import com.Capslock.Engine.peffect;
import com.Capslock.Engine.peffectfactory;
import com.Capslock.Engine.screen;

public class bossbomb extends entity{
	
	protected Color maincolor = Color.red;
	protected int radius = 30;
	protected final int speed = 300;
	
	//the particle effects used for the explosion and trail
	private peffectfactory traileffect;
	

	public bossbomb(screen mainscreen, int x, int y, Color bombcolor) {
		super(mainscreen);
		maincolor = bombcolor;
		this.x = x;
		this.y = y;
	}

	@Override
	public void setup() {
		//setting up the physical properties of the bomb
		this.diry = speed;
		
		//setting up the bomb trail
		traileffect = new peffectfactory();
		traileffect.setforce(160);
		traileffect.setminsize(15);
		traileffect.setmaxsize(18);
		traileffect.setlifespan(200);
		traileffect.setparticles(2);
		traileffect.setgravity(-300);
		traileffect.setcolor(Color.red);
	}
	
	public boolean checkcols(entity e){
		if(!collideable || !e.getcollideable()){return false;}
		
		//checking to see if this is too far in any direction
		if(x+radius<e.getx())return false;
		if(x-radius>e.getx()+e.getsizex())return false;
		if(y+radius<e.gety())return false;
		if(y-radius>e.gety()+e.getsizey())return false;
		
		//if we reach here then it must be colliding. returning true.
		return true;
	}
	
	public void draw(game maingame){
		if(!drawable)return;
		
		draw();
		
		//creating a new particle effect and adding it to game
		maingame.addentity(traileffect.createnewpeffect(maingame, (int)x-19, (int)y));
		maingame.addentity(traileffect.createnewpeffect(maingame, (int)x+10, (int)y));
	}
	public void draw(){
		if(!drawable)return;
		
		//drawing the ball
		mainscreen.screengraphics.setColor(maincolor);
		mainscreen.screengraphics.fillOval((int)x-radius, (int)y-radius, radius*2, radius*2);

	}
	
	//gets and sets
	public int getradius(){return radius;}
	
	public void explode(spbossfight maingame){
		traileffect.setforce(200);
		traileffect.setminsize(10);
		traileffect.setmaxsize(17);
		traileffect.setlifespan(400);
		traileffect.setparticles(40);
		traileffect.setgravity(200);
		
		maingame.addentity(traileffect.createnewpeffect(maingame, (int)x, (int)y));
		
		drawable = false;
		collideable = false;
		
	}

}
