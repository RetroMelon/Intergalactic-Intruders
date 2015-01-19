package com.Capslock.IntergalacticIntruders;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Random;

import com.Capslock.Engine.entity;
import com.Capslock.Engine.game;
import com.Capslock.Engine.peffectfactory;
import com.Capslock.Engine.screen;

public class spbossfight extends game{
	
	//entities such as the player and boss
	boss mainboss;
	ship player;
	//bullets are stored in a separate list from particle effects for efficiency and draw order
	public ArrayList<entity> bullets = new ArrayList<entity>();
	
	//variables affecting behavior of the boss
	private int bossshootfrequency;
	private int bossattack1frequency;
	private int bossramfrequency;
	private int bulletdamage = 5; //the amount of damage a single one of the player's bullets does on hitting the boss
	private final int bossstarty = 50;
	
	
	//variables needed while controlling the boss
	protected int bossstate = 1;
	
	
	//input variables
	boolean left, right, space = false;
	
	//life and score tracking variables
	private int deaths = 0;
	private int score = 0;
	private long gamestart;
	private int shotsfired = 0;
	
	private int lives = I_Intruders.SP_LIVES+2;
	
	
	//other necessary variables
	private ArrayList<entity> destroybulletslist = new ArrayList<entity>();
	private long lastshoottime = System.currentTimeMillis();
	private boolean respawning = false;
	private int respawncounter = 3;
	private boolean complete = false;
	private boolean lastpaused = false;
	
	//drawing vairables, mostly about the health bar.
	private final int totalbosshealth;
	private final int healthbaroffsety = 40;
	private final int healthbarsizey = 19;
	private final int healthbarsize = 200;
	private final int healthbarstart;
	

	public spbossfight(screen thescreen, boss mainboss) {
		super(thescreen);
		totalbosshealth = mainboss.gethealth();
		healthbarstart = mainscreen.getsizex()/2-healthbarsize/2;
		bossshootfrequency = mainboss.getshootfrequency();
		bossattack1frequency = mainboss.getattack1frequency();
		bossramfrequency = mainboss.getramfrequency();
		this.mainboss = mainboss;
	}

	@Override
	public void init() {
		//adding the player
		player = new ship(mainscreen, 1);
		player.setx(I_Intruders.SHIP_START_X);
		
		//starting the boss off outside the screen
		mainboss.setx(mainscreen.getsizex()/2-mainboss.getsizex()/2);
		mainboss.sety(-200);
		
		//drawing the intro fading screen
		dointroscreen();
		
		//playing an animation - eg, having the boss fly in from the top of the screen
		mainboss.setnewtarget((int)(mainboss.getx()), 100);
		while(!mainboss.hasreachedtarget()){
			mainboss.movetotarget(100);
			try{Thread.sleep(10);}catch(Exception e){}
			draw();
			mainscreen.update();
		}
		//noting the time that the game started - for use in the end stat screen
		gamestart = System.currentTimeMillis();
		
		
	}
	
	
	private void dointroscreen(){
		//doing the solid black screen first
		int totalframessolid = 300;
		for(int i = 0; i<totalframessolid; i++){
			if(i==200)break;
			double countdown = ((double)totalframessolid-i)/100;
			
			//drawing the actual gameplay stuff on the screen, eg. aliens, etc
			draw();
			
			//drawing the black background
			mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)(1)));
			mainscreen.screengraphics.setColor(Color.black);
			mainscreen.screengraphics.fillRect(0, 0, mainscreen.getsizex(), mainscreen.getsizey());
			
			drawintrotext(countdown);
			
			
			if(mainscreen.keyboard.escpressed){return;}
			
			mainscreen.update();
			
			try{Thread.sleep(1000/100);}catch(Exception e){}
		}
		
		//now drawing the same stuff, except having it fade out
		int fadeframerate = 100;
		int fadetime = 1;
		int totalframes = fadeframerate*fadetime;
		for(int i = 0; i<fadeframerate*fadetime; i++){
			double countdown = ((double)totalframes-i)/100;
			
			//drawing the actual gameplay stuff on the screen, eg. aliens, etc
			draw();
			
			//drawing the black background
			mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)(1-(double)i/(double)totalframes)));
			mainscreen.screengraphics.setColor(Color.black);
			mainscreen.screengraphics.fillRect(0, 0, mainscreen.getsizex(), mainscreen.getsizey());
			
			drawintrotext(countdown);
			
			mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)1));
			
			if(mainscreen.keyboard.escpressed){return;}
			
			mainscreen.update();
			
			try{Thread.sleep(1000/fadeframerate);}catch(Exception e){}
		}
		mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)1));
	}
	
	private void drawintrotext(double countdown){
		//drawing the wave text
		mainscreen.screengraphics.setColor(Color.white);
		String message = "BOSS FIGHT: "+(mainboss.getname());
		int fontsize = 72;
		Font f = new Font(Font.DIALOG_INPUT, Font.PLAIN, fontsize);
		mainscreen.screengraphics.setFont(f);
		int textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
		mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2);
		
		//drawing the snarky comment
			mainscreen.screengraphics.setColor(Color.white);
			fontsize = 20;
			f = new Font(Font.DIALOG_INPUT, Font.PLAIN, fontsize);
			mainscreen.screengraphics.setFont(f);
			message = "Be ready to fight a fearsome boss!";
			textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
			mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2+20);
			
			//drawing the second line, whether it exists or not...
			message = "since you're a bit of a noob, i'll give you 2 extra lives...";
			textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
			mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2+40);
			
			//drawing the counter text to the screen
			fontsize = 30;
			f = new Font(Font.DIALOG_INPUT, Font.PLAIN, fontsize);
			mainscreen.screengraphics.setFont(f);
			message = "Starting in:  1.23s";
			textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
			message = "Starting in:  "+countdown+"s";
			mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2+70);
			
			//drawing a message that allows us to press escape to begin the game
			fontsize = 30;
			f = new Font(Font.DIALOG_INPUT, Font.PLAIN, fontsize);
			mainscreen.screengraphics.setFont(f);
			message = "PRESS ESCAPE TO BEGIN INSTANTLY";
			textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
			mainscreen.screengraphics.setColor(Color.LIGHT_GRAY);
			mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()-70);
	}


	@Override
	public void input() {
		left = mainscreen.keyboard.leftpressed;
		right = mainscreen.keyboard.rightpressed;
		space = mainscreen.keyboard.spacepressed;
		if(mainscreen.keyboard.enterpressed && !quit && complete){quittype = 1; quit=true;} // this is the button that will be used to progress to the next round
		if(mainscreen.keyboard.uppressed)mainboss.sethealth(0);
		if(mainscreen.keyboard.escpressed){
			pause();
			lastpaused=true;
		}
		
		
	}

	@Override
	public void update() {
		//keeping track of the time spent in the pause menu so we can deduct it from the total play time
				if(!paused && lastpaused){
					lastpaused=false;
					gamestart+=frametimer.getlastpauselength();
				}
		
		//updating all bullets
		updatebullets();	
		
		//updating the player and checking it's collisions
		updateplayer();
		
		//updating the main boss and checking it's collisions
		if(!complete)updateboss();
		
		//bullets are stored in a separate list for efficiency, so we must destroy these manually
		bullets.removeAll(destroybulletslist);
		destroybulletslist.clear();
	}
	
	private void updatebullets(){
		for(int i = 0; i<bullets.size(); i++){
			
			entity tempent = bullets.get(i);
			
			tempent.move(frametimer.getactualframerate());
			
			if(tempent instanceof bullet){
				if(tempent.gety()>mainscreen.getsizey() || tempent.gety()<0){destroybullet(tempent); continue;}
				
				//checking to see if the bullet is colliding with a bossbomb, and if it is a player's bullet, because if so, we destroy it and the bossbomb
				if(!((bullet)tempent).isalien()){
					for(int j = 0; j<bullets.size(); j++){
						entity tempent2 = bullets.get(j);
						if(tempent2 instanceof bossbomb && tempent2.checkcols(tempent)){
							((bossbomb)tempent2).explode(this);
							destroybullet(tempent2);
							destroybullet(tempent);
							continue;
						}
					}
				}
			}
			
			if(tempent instanceof bossbomb){
				if(tempent.gety()-((bossbomb)tempent).getradius()>mainscreen.getsizey()){destroybullet(tempent); continue;}
			}
		}
	}
	
	private void updateplayer(){//a wrapper function so we can update the player when the frametimer isn't being updated
		updateplayer(frametimer.getactualframerate());
	}
	
	private void updateplayer(int theframerate){
		//first we see which direction the player wants the ship to move
		
		if(left)player.setaccelx(-I_Intruders.SHIP_SPEED);
		else if(right)player.setaccelx(I_Intruders.SHIP_SPEED);
		else player.setaccelx(0);
		
		//now we move the player
		player.move(theframerate);
		
		//checking if the ship is colliding with the walls of the screen
				if(player.getx()<0){
					player.setx(0);
					player.setaccelx(0);
					player.setdirx(0);
				}
				else if(player.getx()+player.getsizex()>=mainscreen.getsizex()){
					player.setx(mainscreen.getsizex()-player.getsizex());
					player.setaccelx(0);
					player.setdirx(0);
				}
		
		//now we check collisions with bullets, etc.
		if(respawning)return;
		for(int i = 0; i<bullets.size(); i++){
			entity tempent = bullets.get(i);
			
			//checking if it is colliding. if so then determine what to do based on what type of instance it is
			if(tempent.checkcols(player)){
				
				//acting if the colliding object was a bullet
				if(tempent instanceof bullet && ((bullet)tempent).isalien()){destroyentity(tempent); killplayer(); continue;}
				
				//acting if the colliding object is a bossbomb
				if(tempent instanceof bossbomb){((bossbomb) tempent).explode(this); destroyentity(tempent); killplayer(); continue;}
			}
		}
		
		//now checking collisions with the boss, and destroying the player if so
		if(mainboss.checkcols(player)){
			killplayer();
		}
		
		//now we just determine whether the player can shoot, and if so we let them shoot
		if(player.getcanshoot() && space && System.currentTimeMillis()>lastshoottime+I_Intruders.SHOOT_TIME_GAP){ // if the player is pressing the space button, and it has been a specified time since the last shot, then add a bullet
			addbullet(false, (int)(player.getx()+player.getsizex()/2-3), I_Intruders.TRUE_SHIP_Y);
			lastshoottime = System.currentTimeMillis();
		}
	}
	
	public void addbullet(boolean isalien, int positionx, int positiony){//adds a new bullet to the entities list
		if(!isalien)shotsfired++;
		bullets.add(new bullet(this, positionx, positiony, isalien));
	}
	
	//similar to destroyentity except this removes it from the bullets arraylist
	private void destroybullet(entity tempent){
		destroybulletslist.add(tempent);
	}
	
	private void killplayer() {//this will perform the action that is taken when the player is killed
		lives--;
		deaths++;
		respawning = true;//changing this to true here to prevent a lag during thread execution to cause multiple lives to be deducted
		player.createpeffect(this);
		if(lives<=0){quittype = 2; quit=true; return;}
		new Thread(new respawner(700)).start();//this does all of the necessary things. eg, setting respawning=true, counting down till respawning, hiding the player, removing bullets, once it's finished it also respawns the player and sets it back to the default start position
	}
	
	private void updateboss(){
		//checking if the boss is colliding with any player bullets
		for(int i = 0; i<bullets.size(); i++){
			entity tempent  = bullets.get(i);
			if(tempent instanceof bullet && !((bullet)tempent).isalien()){
				if(tempent.checkcols(mainboss)){//the boss has collided with a player bullet if this if is true
					score+=bulletdamage*10;
					I_Intruders.SP_TOTAL_BOSS_HITS++;
					mainboss.damageparticle(this, (int)tempent.getx(), (int)tempent.gety());
					destroybullet(tempent);
					mainboss.damage(bulletdamage);
				}
			}
		}
			
		//mainboss.movetotarget(frametimer.getactualframerate(), 200);
		if(!mainboss.isalive()){destroyboss();}
		
			if(mainboss.isidle()){
				if(0==Math.abs(random.nextInt())%(((double)bossshootfrequency/1000)*frametimer.getactualframerate())){mainboss.shoot(this);}
				if(0==Math.abs(random.nextInt())%(((double)bossattack1frequency/1000)*frametimer.getactualframerate())){mainboss.attack1(this);}
				if(0==Math.abs(random.nextInt())%(((double)bossramfrequency/1000)*frametimer.getactualframerate())){mainboss.ram(this);}
				mainboss.moverandomly(frametimer.getactualframerate());
				
			}else mainboss.movetotarget(frametimer.getactualframerate());

	}

	@Override
	public void draw() {
		I_Intruders.parisbackground.draw(mainscreen.screengraphics, 0, I_Intruders.PARIS_BACKGROUND_Y_OFFSET);
		
		//drawing the player
				player.draw();
		
		//drawing the boss sprite.. this is drawn under the particle effects so we can add little sparks on to it, and fire...
				mainboss.draw();
		
		//drawing entities which are mostly particleffects
		for(int i = 0; i<entities.size(); i++){
			entities.get(i).draw();
		}
				
		//drawing bullets
		if(!paused){//preventing ENORMOUS lag due to too many particle effects being created and not destroyed during pause
			for(int i = 0; i<bullets.size(); i++){
				if(bullets.get(i) instanceof bullet)((bullet)bullets.get(i)).draw(this);
				else if(bullets.get(i) instanceof bossbomb)((bossbomb)bullets.get(i)).draw(this);
			}
		}else{
			for(int i = 0; i<bullets.size(); i++){
				if(bullets.get(i) instanceof bullet)((bullet)bullets.get(i)).draw();
				else if(bullets.get(i) instanceof bossbomb)((bossbomb)bullets.get(i)).draw();
			}
		}
		
		
		
		//drawing the number of lives the player has
		int tempoffsetx = 90;
		I_Intruders.scaledshipsprite.draw(mainscreen.screengraphics, mainscreen.getsizex()-tempoffsetx, 10);
		Font tempfont = mainscreen.screengraphics.getFont();
		mainscreen.screengraphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));//size 18
		mainscreen.screengraphics.setColor(Color.white);
		mainscreen.screengraphics.drawString("= "+lives, mainscreen.getsizex()-tempoffsetx+40, 10+18);//10+18
		mainscreen.screengraphics.setFont(tempfont);
		
		//drawing the wave number that the player is currently on.
		mainscreen.screengraphics.setColor(Color.white);
		String message1 = "BOSS: "+((boss)mainboss).getname();
		Font f1 = new Font(Font.DIALOG_INPUT, Font.PLAIN, 25);
		mainscreen.screengraphics.setFont(f1);
		int textlength1 = mainscreen.screengraphics.getFontMetrics().stringWidth(message1);
		mainscreen.screengraphics.drawString(message1, mainscreen.getsizex()/2-textlength1/2, 30);
		
		//drawing the healthbar of the boss
		mainscreen.screengraphics.setColor(Color.red);
		mainscreen.screengraphics.fillRect(healthbarstart, healthbaroffsety, healthbarsize, healthbarsizey);
		mainscreen.screengraphics.setColor(Color.green);
		mainscreen.screengraphics.fillRect(healthbarstart, healthbaroffsety, (int)((double)healthbarsize*((double)mainboss.gethealth()/(double)totalbosshealth)), healthbarsizey);
		
		//deciding whether to draw the "press enter to progress to the next wave" text
		if(complete && !quit){
			mainscreen.screengraphics.setColor(Color.white);
			String message = "PRESS ENTER TO PROGRESS TO WAVE "+(I_Intruders.SP_WAVES_COMPLETED+1)+"...";
			Font f = new Font(Font.DIALOG_INPUT, Font.PLAIN, 30);
			mainscreen.screengraphics.setFont(f);
			int textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
			mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2);
		}
		
		//if we are in the process of respawning, draw a transparent red layer over the top of the screen, and draw the big "respawning" text thing in the middle
		if(respawning){
			//drawing the red layer
			mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.32));
			mainscreen.screengraphics.setColor(new Color(201, 0, 46));
			mainscreen.screengraphics.fillRect(0, 0, mainscreen.getsizex(), mainscreen.getsizey());
			mainscreen.screengraphics.setColor(Color.DARK_GRAY);
			mainscreen.screengraphics.fillRect(0, 0, mainscreen.getsizex(), mainscreen.getsizey());
			mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)1));
			
			//drawing the "respawning in 3,2,1 text
			mainscreen.screengraphics.setColor(Color.white);
			String message = "Respawning in: "+respawncounter+"s";
			Font f = new Font(Font.DIALOG_INPUT, Font.BOLD, 23);
			mainscreen.screengraphics.setFont(f);
			int textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
			mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2);
		}
				
		//drawing the framerate indicator
		frametimer.draw(mainscreen.screengraphics);
		
	}

	public void destroyboss(){
		frametimer.pause();
		//this happens if we successfully kill the boss
		
		//making the player invincible
		player.setcollideable(false);
		
		//now doing the exploding animation
		int mainx = (int)mainboss.getx();
		int mainy = (int)mainboss.gety();
		long thistime = System.currentTimeMillis();
		
		mainboss.deathexplosion(this, 2000);
		mainboss.setnewtarget((int)mainboss.getx(), (int)mainboss.gety());
		while(System.currentTimeMillis()<thistime+2000){
			input();
			
			updateplayer();
			updatebullets();
			if(mainboss.hasreachedtarget()){
				mainboss.setnewtarget(mainx+random.nextInt()%6, mainy+random.nextInt()%6);
			}else{mainboss.movetotarget(100);}
			try{Thread.sleep(10);}catch(Exception e){}
			
			draw();
			mainscreen.update();
		}
		
		//now doing a final big explosion and making the boss disappear
		
		//finally we start the fireworks
		new Thread(new fireworkspawner(this)).start();
		
		complete=true;
		
		frametimer.unpause();
	}
	
	public int quit() {
		updategamestats();
		int length = 500;//length of the fade in milliseconds
		int iterations = length/10;
		for(int i = 0; i<iterations; i++){
			draw();
			//drawing a black layer on top
			mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)((double)i/(double)iterations)));
			mainscreen.screengraphics.setColor(Color.black);
			mainscreen.screengraphics.fillRect(0, 0, mainscreen.getsizex(), mainscreen.getsizey());
			mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)1));
			
			mainscreen.update();
			
			try{Thread.sleep(10);}catch(Exception e){}
		}
		return quittype;
	}
	
	//this method updates the single player game stat variables in I_Invaders with the ones stored in this game
		private void updategamestats(){
			if(quittype == 1)score += 2000*mainboss.getlevel();
			I_Intruders.SP_TOTAL_SHOTS_FIRED += shotsfired;
			I_Intruders.SP_TOTAL_TIME_PLAYED += System.currentTimeMillis()-gamestart;
			I_Intruders.SP_TOTAL_DEATHS += deaths;
			I_Intruders.SP_LIVES = lives;
			I_Intruders.SP_TOTAL_ALIENS_KILLED ++;
			I_Intruders.SP_TOTAL_SCORE +=score;
			I_Intruders.SP_SCORES.add(score);
		}
		
		
	public entity getplayer(){return player;}
	
	//this class is responsible for all respawning needs, including removing bullets, and then counting down before respawning the player.
	private class respawner implements Runnable{
		
		private int timedelay = 500;
		
		public respawner(int timedelay){
			this.timedelay = timedelay;
		}

		public void run() {
			//changing "respawning" to true so the draw method knows what to draw
			respawning = true;
			
			//hiding the player and making it unable to shoot
			player.setdrawable(false);
			player.setcanshoot(false);
			
			//removing all bullets from the game. the particle effects are allowed to continue, in case it is the player explode particle effect
			bullets.clear();
			destroybulletslist.clear();
			
			//creating a loop that counts down until the player is respawned.
			for(int i = 3; i>0; i--){
				respawncounter = i;
				try{Thread.sleep(timedelay);}catch(Exception e){}
			}
			
			//finally, resetting the player's location, and starting the "respawn" sequence for the player
			player.setx(I_Intruders.SHIP_START_X);
			player.respawn();//respawns the player without a particle effect
			
			//setting respawning = false again, so we can stop drawing the red haze.
			respawning = false;
		}
		
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
	

}
