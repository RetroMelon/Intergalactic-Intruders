package com.Capslock.IntergalacticIntruders;

import java.awt.Color;

import com.Capslock.Engine.entity;
import com.Capslock.Engine.game;
import com.Capslock.Engine.peffectfactory;
import com.Capslock.Engine.screen;

public class ship extends entity implements Runnable{
	
	private int playernumber = 1;
	private boolean canshoot = true;
	
	private static peffectfactory explodeffect;
	
	static{
		explodeffect = new peffectfactory();
		explodeffect.setcolor( Color.white, Color.white, Color.white, Color.white, Color.white, Color.white, Color.LIGHT_GRAY);
		explodeffect.setcolorsingleparticle(true);
		explodeffect.setforce(200);
		explodeffect.setgravity(150);
		explodeffect.setlifespan(2000);
		explodeffect.setmaxsize(20);
		explodeffect.setminsize(5);
		explodeffect.setparticles(60);
	}

	public ship(screen mainscreen, int pnum) {
		super(mainscreen);
		playernumber = pnum;

		if(playernumber == 1){
			addsprite(I_Intruders.shipsprite);
		}else{
			addsprite(I_Intruders.shipsprite_2);
		}

	}

	@Override
	public void setup() {
		boundsx=50;
		boundsy=30;
		x=I_Intruders.SHIP_PERSISTANT_X;
		y=I_Intruders.SHIP_Y;		
		maxdirx=400;
		frictionx=600;
		weight=0.15;
		spriteoffsety=I_Intruders.SHIP_OFFSET_Y;
		collisionmode=3;
	}
	
	public int getplayernumber(){return playernumber;}
	
	public void respawn(game maingame){//creates a particleffect and starts the respawn thread
		createpeffect(maingame);
		respawn();
	}
	
	public void respawn(){
		new Thread(this).start();
	}
	
	public void createpeffect(game maingame){
		I_Intruders.playerexplode.play();
		maingame.addentity(explodeffect.createnewpeffect(maingame, (int)getx()+(int)getsizex()/2, (int)gety()));
	}
	
	public void run() {
		collideable = false;
		canshoot=false;
		for (int x = 0; x < 10; x++) {
			drawable = false;
			try {Thread.sleep(150);} catch (InterruptedException e) {}
			drawable = true;
			try{Thread.sleep(150);}catch(Exception e){}
		}
		collideable = true;
		canshoot=true;
		drawable=true;
	}
	
	//we have to override the move method so we can calculate how far the player has moved
	public void move(int framerate){
		lastframerate=framerate;
		//applying acceleration
		dirx=dirx+((accelx/weight)/framerate);
		diry=diry+((accely/weight)/framerate);
		//applying friction for x axis
		//when applying a friction, we only do it if the accel'n is 0. this prevents the case where the object is unable to accelerate due to being stopped by friction.
		double frictionresult;
		if(accelx==0){
			frictionresult=frictionx/framerate;
			if(dirx<-frictionresult){
				dirx+=frictionresult;
			}else if(dirx>frictionresult){
				dirx-=frictionresult;
			}else{dirx=0;}
		}
		//applying friction for y axis
		if(accely==0){
			frictionresult=frictiony/framerate;
			if(diry<-frictionresult){
				diry+=frictionresult;
			}else if(diry>frictionresult){
				diry-=frictionresult;
			}else{diry=0;}
		}
		//making sure the speeds do not exceed the speed cap
		if(maxdirx!=0){if(dirx>maxdirx){dirx=maxdirx;}else if(dirx<-maxdirx){dirx=-maxdirx;}}
		if(maxdiry!=0){if(diry>maxdiry){diry=maxdiry;}else if(diry<-maxdiry){diry=-maxdiry;}}
		//applying the speed to the position of the entity
		double distancetravelledx = dirx/framerate;
		x=x+distancetravelledx;
		y=y+diry/framerate;
		
		//adding the distance travelled here to the variable in I_Intruders
		try{
			I_Intruders.SP_TOTAL_DISTANCE_TRAVELLED+=Math.abs(distancetravelledx);
			I_Intruders.MP_TOTAL_DISTANCE_TRAVELLED_P1+=Math.abs(distancetravelledx);
			I_Intruders.MP_TOTAL_DISTANCE_TRAVELLED_P2+=Math.abs(distancetravelledx);
		}catch(Exception e){}
	}
	
	public void setcollideable(boolean newcollideable){collideable = newcollideable;}
	
	public void setcanshoot(boolean newval){canshoot = newval;}
	public boolean getcanshoot(){return canshoot;}

	public void hideformillis(int milliseconds) {
		new Thread(new hider(milliseconds)).start();
		
	}
	
	private class hider implements Runnable{//the thread used to hide the player for a small time
		
		int interval = 0;
		
		public hider(int milliseconds){
			interval = milliseconds;
		}

		public void run(){
			try{
				setdrawable(false);
				Thread.sleep(interval);
				setdrawable(true);
			}catch(Exception e){}
		}

	}

}
