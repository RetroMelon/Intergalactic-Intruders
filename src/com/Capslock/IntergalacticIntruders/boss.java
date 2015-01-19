package com.Capslock.IntergalacticIntruders;

import java.awt.Color;
import java.util.Random;

import com.Capslock.Engine.entity;
import com.Capslock.Engine.game;
import com.Capslock.Engine.peffectfactory;
import com.Capslock.Engine.screen;

public abstract class boss extends entity{
	
	public boss(screen mainscreen) {
		super(mainscreen);
		// TODO Auto-generated constructor stub
	}

	//the target coords that the boss can be set to move towards
	protected int shootfrequency = 1000; //the number of milliseconds on average between each shooting
	protected int attack1frequency = 7000;
	protected int ramfrequency = 4000;
	protected String name = "BOSS";
	protected Color bombcolor = Color.red;
	protected boolean reachedtarget = false;
	protected boolean idle = true;// this is used todetermine whether the boss is in an attackloop or not
	protected int targetx = 0;
	protected int targety = 0;
	protected int standardspeedy = 300;
	protected int standardspeedx = 300;
	protected int totalhealth = 100;
	protected int level = 1; //this determines the score gained for killing the boss..... the score will be 2000*level
	protected int health = totalhealth;
	protected boolean alive = true;
	
	private int xmargin = 120;
	private int topymargin = 30;
	
	private peffectfactory firefactory;
	private int firelifespan = 250;

	@Override
	public void setup() {
		collisionmode = entity.COLLISIONMODE_CUSTOMBOUNDS;
		boundsx = 300;
		boundsy = 300;
		weight = 0;
		//dirx = standardspeedx;
		//diry =  standardspeedy;
		dirx = 300;
		diry = 300;
		maxdirx = 500;
		maxdiry = 500;
		
		//setting up the firefactory, which is used to create little pieces of fire on the boss to indicate that it has taken damage
		firefactory = new peffectfactory();
		firefactory.setcolor(Color.orange, Color.orange, Color.orange, Color.orange, Color.orange, Color.orange, Color.red, Color.yellow, Color.yellow);
		firefactory.setcolorsingleparticle(true);
		firefactory.setforce(70);
		firefactory.setgravity(-200);
		firefactory.setlifespan(firelifespan);
		firefactory.setmaxsize(5);
		firefactory.setminsize(2);
		firefactory.setparticles(3);
	}
	
	//sets a new target for the boss, that it will move towards when 
	public void setnewtarget(int x, int y){
		if(targetx<=0){targetx=1;}
		targetx = x;
		targety = y;
	}
	
	
	//moves the entity in the direction of the target.
	public void movetotarget(int currentframerate, int movementspeed){
		setdirx(movementspeed);
		setdiry(movementspeed);
		movetotarget(currentframerate);
	}
	
	public void movetotarget(int currentframerate){
		lastframerate = currentframerate;
		boolean reachedx = false;
		boolean reachedy = false;
		
		//moving on the x axis first
		if(dirx!=0){//checking if the direction is not 0 so as not to waste processing time
			double movedistancex = Math.abs(dirx)/currentframerate;
			//if the distance to the target is less than the speed after the framerate is taken in to consideration, then jump to the target
			if(Math.abs(targetx-x)<=Math.abs(movedistancex)){
				x = targetx;
				reachedx = true;
			}
			//we are further than the move distance from the target so we decide on the best direction to move
			else{
				if(targetx<x)x-=movedistancex;
				else x+=movedistancex;
			}
		}
		
		//moving on the y axis
		if(diry!=0){//checking if the direction is not 0 so as not to waste processing time
			double movedistancey = Math.abs(diry)/currentframerate;
			
			//if the distance to the target is less than the speed after the framerate is taken in to consideration, then jump to the target
			if(Math.abs(targety-y)<=Math.abs(movedistancey)){
				y = targety;
			}
			//we are further than the move distance from the target so we decide on the best direction to move
			else{
				if(targety<y)y-=movedistancey;
				else y+=movedistancey;
			}
		}
		
	}
	
	public void moverandomly(int currentframerate){
		if(!idle)return;
		if(hasreachedtarget()){
			setrandomtarget();
		}
		movetotarget(currentframerate);
	}
	
	public void setrandomtarget(){
		Random random = new Random(System.currentTimeMillis());
		int newx = (int)(Math.abs(random.nextInt())%(mainscreen.getsizex()-xmargin*2-getsizex())+xmargin);
		int newy =  (int)(Math.abs(random.nextInt())%(mainscreen.getsizey()-getsizey()-200-topymargin)+topymargin);
		setnewtarget(newx, newy);
	}
	
	public boolean hasreachedtarget(){
		reachedtarget = (x==targetx && y==targety);
		return reachedtarget;
	}
	
	public boolean isidle(){
		return idle; 
	}
	
	public String getname(){return name;}
	
	public int gethealth(){return health;}
	public void sethealth(int newhealth){
		health = newhealth;
		if(health<=0){
			health=0;
			alive=false;
		}
	}
	
	public int getshootfrequency(){return shootfrequency;}
	public int getattack1frequency(){return attack1frequency;}
	public int getramfrequency(){return ramfrequency;}
	public int getlevel(){return level;}
	
	public boolean isalive(){return alive;}
	
	public void damage(int i){
		I_Intruders.bosshit.play();
		health-=i;
		if(health<=0){
			health=0;
			alive=false;
		}
	}
	
	public void debugdraw(){
		if(hasreachedtarget()){mainscreen.screengraphics.setColor(Color.green);}else{mainscreen.screengraphics.setColor(Color.yellow);}
		mainscreen.screengraphics.drawRect((int)x, (int)y, (int)boundsx, (int)boundsy);
		
		int crosssize = 10;
		mainscreen.screengraphics.setColor(Color.red);
		mainscreen.screengraphics.drawLine(targetx-crosssize, targety, targetx+crosssize, targety);
		mainscreen.screengraphics.drawLine(targetx, targety+crosssize, targetx, targety-crosssize);
	}
	

	public void deathexplosion(game maingame, int time) {
		//creating an explosion thread that will play the explosion for the length of time indicated by "time"
		new Thread(new explodethread(maingame, time)).start();
		
	}
	
	public void damageparticle(game maingame, int x1, int y1){
		firefactory.setlifespan(800);
		firefactory.setgravity(200);
		firefactory.setparticles(15);
		maingame.addentity(firefactory.createnewpeffect(maingame, x1, y1));
		firefactory.setlifespan(firelifespan);
		firefactory.setgravity(-200);
		firefactory.setparticles(3);
	}
	
	public void shootbomb(spbossfight maingame){
		I_Intruders.bombshoot.play();
		maingame.bullets.add(new bossbomb(mainscreen, (int)x+(int)boundsx/2, (int)y+(int)boundsy, bombcolor));
	}
	
	public abstract void shoot(spbossfight spbossfight);
	

	public void attack1(spbossfight maingame){
		new Thread(new attack1thread(maingame)).start();
	}
	
	
	//a thread used to carry out the attacking
	private class attack1thread implements Runnable{
		
		spbossfight maingame;
		
		public attack1thread(spbossfight thegame){
			this.maingame = thegame;
		}

		@Override
		public void run() {
			idle = false;			
			
			setnewtarget(50, 50);
			while(!hasreachedtarget()){if(!isalive())return; try{Thread.sleep(10);}catch(Exception e){}}
			
			
			setnewtarget(mainscreen.getsizex()-200, 50);
			while(!hasreachedtarget()){if(!isalive())return; shootbomb(maingame); try{Thread.sleep(700);}catch(Exception e){}}
			
			
			idle = true;			
		}
	}
	
	
	public void ram(spbossfight maingame){
		new Thread(new ramthread(maingame)).start();
	}
	
	
	//a thread used to carry out the attacking
	private class ramthread implements Runnable{
		
		spbossfight maingame;
		
		public ramthread(spbossfight thegame){
			this.maingame = thegame;
		}

		@Override
		public void run() {
			idle = false;			
			setcurrentsprite(1);
			boolean reachedplayer = false;
			while(!reachedplayer){
				if(!isalive())return;
				if((maingame.getplayer().getx()>x+30 && maingame.getplayer().getx()<x+boundsx-50) || x<=1)reachedplayer = true;
				int locationx = (int)maingame.getplayer().getx()-(int)boundsx/2;
				setnewtarget(locationx,(int)y);
				if(x<=1){x=1;}
				try{Thread.sleep(10);}catch(Exception e){}
			}
			
			//has now found the player... shakes about for a second before smashing down quickly and letting off a bunch of particles perhaps
			
			int shakeferocity = 10;
			int originalx = (int)x;
			int originaly = (int)y;
			
			Random random = new Random(System.currentTimeMillis());
			//shaking
			for(int shakes = 0; shakes<10; shakes++){
				if(!isalive())return;
				while(!hasreachedtarget()){try{Thread.sleep(10);}catch(Exception e){}}
				setnewtarget(originalx+random.nextInt()%shakeferocity, originaly+random.nextInt()%shakeferocity);
			}
			//smashing
			diry=700;
			setnewtarget((int)x,(int)maingame.getplayer().gety()+30-(int)boundsy);
			while(!hasreachedtarget()){if(!isalive())return; try{Thread.sleep(10);}catch(Exception e){}}
			diry=standardspeedy;
			
			I_Intruders.bombshoot.play();
			try{Thread.sleep(100);}catch(Exception e){}
			
			setcurrentsprite(0);
			
			setnewtarget((int)x, 90);
			while(!hasreachedtarget()){if(!isalive())return; try{Thread.sleep(10);}catch(Exception e){}}
			
			
			
			idle = true;			
		}
	}
	
	private class explodethread implements Runnable{
		
		int timespan;
		game maingame;
		Random random;
		
		public explodethread(game maingame, int thetime){
			this.maingame = maingame;
			this.timespan = thetime;
			random = new Random(System.currentTimeMillis());
		}
		
		public void run(){
			setcurrentsprite(1);
			
			//using the "firefactory" particle effect generator since we won't be needing it again
			firefactory.setforce(120);
			firefactory.setgravity(200);
			firefactory.setlifespan(300);
			firefactory.setmaxsize(15);
			firefactory.setminsize(5);
			firefactory.setparticles(10);
			
			//time is the length of time the explosions should go on for before the big explosion
			long currenttime = System.currentTimeMillis();
			int playinterval = 3;
			int playcounter = 1;
			while(System.currentTimeMillis()<currenttime+timespan){
				playcounter++;
				if(playcounter%3 == 0){I_Intruders.alienexplode.play();}
				maingame.addentity(firefactory.createnewpeffect(maingame, x+Math.abs(random.nextInt()%boundsx), y+Math.abs(random.nextInt()%boundsy)));
				try{Thread.sleep(100);}catch(Exception e){}//this means approximately 10 every second will be added
			}
			
			//now making a big huge explosion and hiding the boss's image
			firefactory.setforce(400);
			firefactory.setgravity(400);
			firefactory.setlifespan(800);
			firefactory.setmaxsize(60);
			firefactory.setminsize(10);
			firefactory.setparticles(80);
			
			maingame.addentity(firefactory.createnewpeffect(maingame, x+boundsx/2, y+boundsy/2-25));		
			drawable=false;
			
			//making the explosion sounds
			I_Intruders.playerexplode.play();
			I_Intruders.bombshoot.play();
		}
		
		
	}
	 

}
