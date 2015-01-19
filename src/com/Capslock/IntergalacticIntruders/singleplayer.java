package com.Capslock.IntergalacticIntruders;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.util.Random;

import com.Capslock.Engine.*;

public class singleplayer extends game{
	
	Random random = new Random(System.currentTimeMillis());
	
	//variables to do with the alien grid
	private final int aliensacross = 13; //13
	private final int aliensdown = 5; //5
	private final int margins = 10; //10
	private final int squishingmargin = 90;//60 // this is used to give the aliens some initial space to move
	private int ALIEN_GAP_X = 10; //20 //this is the gap between aliens on the x axis... the gap on the y axis will always be the same, no matter how many rows there are
	private final int ALIEN_GAP_Y = 10; //10
	private final int ALIEN_START_Y = 70; //70 // the point on the screen where the top of the first alien will be :)
	private int aliensalive = aliensacross*aliensdown;
	
	//to do with lives
	private int deaths = 0;
	private int lives = I_Intruders.SP_LIVES;
	private boolean respawning = false;
	private int respawncounter = 3;
	private boolean complete = false; //this is the boolean that is set to true when all aliens are killed
	
	//variables related to game stats
	private final int thiswave = I_Intruders.SP_WAVES_COMPLETED+1;
	private int score = 0;
	private long gamestart;
	private int shotsfired = 0;
	
	tracker alientracker;
	alien[][] aliens = new alien[aliensacross][aliensdown];
	ship player;
	//bullets are stored in the entities arraylist
	
	//input keys
	private boolean left = false;
	private boolean right = false;
	private boolean space = false;
	private boolean lastpaused = false;
	
	//variables related to shooting of player and aliens
	private long lastshoottime = System.currentTimeMillis();
	private int defaultalienshootfrequency = 4;// the higher the second number, the slower the shoot speed
	private int alienshootfrequency = defaultalienshootfrequency+(thiswave>=10 && thiswave<=20 ? (10+2*(thiswave-9)) : thiswave ); //higher means faster shooting. 100 means approx once a second. 300 means approx 3 times a second, etc
	


	public singleplayer(screen thescreen) {
		super(thescreen);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
		System.out.println("SINGLEPLAYER GAME INITIALISING. WAVE:  "+thiswave + "    ALIENSHOOTFREQUENCY:  "+alienshootfrequency + "      COMMENT:  "+I_Intruders.statements[thiswave][0] + I_Intruders.statements[thiswave][1]);
				
		//creating the player
		player = new ship(mainscreen, 1);
		
		
		//creating a "tracker" entity, which will be used to keep track of the aliens. it will be positioned in the uppermost left hand corner, and all other aliens will set their positons relative to this
		alientracker = new tracker(mainscreen, margins+squishingmargin, ALIEN_START_Y);
		
		
		//calculating spacing values for aliens
		ALIEN_GAP_X = (mainscreen.getsizex() -2*(margins+squishingmargin) - I_Intruders.ALIEN_SIZE_X*aliensacross) / (aliensacross-1);//aliensacross-1
		
		//creating the aliens
		//starting going across each column, then for each cycle, populating down each row.
		for(int i = 0; i<aliensacross; i++){
			for(int j = 0; j<aliensdown; j++){
				alien tempalien = new alien(mainscreen);
				tempalien.setalienoffsetx(i*(ALIEN_GAP_X+I_Intruders.ALIEN_SIZE_X));
				tempalien.setalienoffsety(j*(I_Intruders.ALIEN_SIZE_X+ALIEN_GAP_Y));
				//tempalien.setdebugbounds(true);
				aliens[i][j] = tempalien;
			}
		}
		
		updatealienpositions();
		alientracker.setdirx(I_Intruders.DEFAULT_ALIEN_SPEED);
		
		//playing the "wave something" fade-in animation, and the countdown until the game starts
		dointroscreen();
		
		//setting the starting time of the game, which is used later to calculate how long the game lasted
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
			
			//drawing the wave text
			mainscreen.screengraphics.setColor(Color.white);
			String message = "WAVE "+(thiswave);
			int fontsize = 72;
			Font f = new Font(Font.DIALOG_INPUT, Font.PLAIN, fontsize);
			mainscreen.screengraphics.setFont(f);
			int textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
			mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2);
			
			//drawing the snarky comment
			if(!I_Intruders.statements[thiswave].equals("")){
				mainscreen.screengraphics.setColor(Color.white);
				fontsize = 20;
				f = new Font(Font.DIALOG_INPUT, Font.PLAIN, fontsize);
				mainscreen.screengraphics.setFont(f);
				message = I_Intruders.statements[thiswave][0];
				textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
				mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2+20);
				
				//drawing the second line, whether it exists or not...
				message = I_Intruders.statements[thiswave][1];
				textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
				mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2+40);
			}
			
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
			
			//drawing the wave text
			mainscreen.screengraphics.setColor(Color.white);
			String message = "WAVE "+(thiswave);
			int fontsize = 72;
			Font f = new Font(Font.DIALOG_INPUT, Font.PLAIN, fontsize);
			mainscreen.screengraphics.setFont(f);
			int textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
			mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2);
			
			//drawing the snarky comment
			if(!I_Intruders.statements[thiswave].equals("")){
				mainscreen.screengraphics.setColor(Color.white);
				fontsize = 20;
				f = new Font(Font.DIALOG_INPUT, Font.PLAIN, fontsize);
				mainscreen.screengraphics.setFont(f);
				message = I_Intruders.statements[thiswave][0];
				textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
				mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2+20);
				
				//drawing the second line, whether it exists or not...
				message = I_Intruders.statements[thiswave][1];
				textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
				mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2+40);
			}
			
			mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)1));
			
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
			
			if(mainscreen.keyboard.escpressed){return;}
			
			mainscreen.update();
			
			try{Thread.sleep(1000/fadeframerate);}catch(Exception e){}
		}
		mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)1));
	}

	
	@Override
	public void input() {	
		if(mainscreen.keyboard.escpressed){
			pause();
			lastpaused=true;
		}
		if(mainscreen.keyboard.enterpressed && !quit && complete){quittype = 1; quit=true;} // this is the button that will be used to progress to the next round
		
		// CHEAT KEYS
		if(mainscreen.keyboard.downpressed){quittype = 2; quit=true;}
		if(mainscreen.keyboard.uppressed){
			for(int i = 0; i<aliensacross; i++){
				for(int j = 0; j<aliensdown; j++){
					killalien(i, j);
				}
			}
		}
		
		left = mainscreen.keyboard.leftpressed;
		right = mainscreen.keyboard.rightpressed;
		space = mainscreen.keyboard.spacepressed;
		
	}

	@Override
	public void update() {
		//keeping track of the time spent in the pause menu so we can deduct it from the total play time
		if(!paused && lastpaused){
			lastpaused=false;
			gamestart+=frametimer.getlastpauselength();
		}
		moveall();//moving all entities
		otheractions();//shooting, and producing particle effects for bullets, but not explosions- they are done by the alien
		checkallcols();//checking bullet collisions, and whether any entities should be allowed to move any further, eg. when hitting a wall
	}
	
	private void moveall(){
		//MOVING THE SHIP
		player.setaccelx(0);
		
		if(left){player.setaccelx(-I_Intruders.SHIP_SPEED);}
		else if(right){player.setaccelx(I_Intruders.SHIP_SPEED);}
		
		player.move(frametimer.getactualframerate());
		
		//MOVING THE ALIENS
		alientracker.move(frametimer.getactualframerate());
		this.updatealienpositions();
		
		//MOVING ALL OTHER ENTITIES
		for(int i = 0; i<entities.size(); i++){
			entities.get(i).move(frametimer.getactualframerate());
		}
		
	}
	
	private void otheractions() {
		//CHECKING WHETHER THE PLAYER SHOULD SHOOT
		if(player.getcanshoot() && space && System.currentTimeMillis()>lastshoottime+I_Intruders.SHOOT_TIME_GAP){ // if the player is pressing the space button, and it has been a specified time since the last shot, then add a bullet
			addbullet(false, (int)(player.getx()+player.getsizex()/2-3), I_Intruders.TRUE_SHIP_Y);
			lastshoottime = System.currentTimeMillis();
		}
		
		//CHECKING WHETHER EACH ALIEN SHOULD SHOOT
		outershootloop:for(int i = 0; i<aliensacross; i++){//for each alien column across the x axis, we go up the y axis from bottom to top, and record the first alien we come across, if any.
			int lowestalienj= -1;
			for(int j = aliensdown-1; j>0; j--){
				if(aliens[i][j].isalive()){lowestalienj=j; break;} //if we have found an alien that is alive, then it will be the lowest, so we break, and start to get it to actually shoot
				if(j==0)continue outershootloop;
			}
			//if there is an available alien, and there is the chance that we can shoot, then add an alien bullet
			if(lowestalienj>=0 && 0==Math.abs(random.nextInt())%(100/alienshootfrequency*frametimer.getactualframerate())){aliens[i][lowestalienj].shoot(this);}
		}
		
	}


	private void checkallcols(){
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
		
		
		//making sure the aliens do not go outwith the boundaries of the screen
		outerloop:for(int i = 0; i<aliensacross; i++){
			for(int j = 0; j<aliensdown; j++){
				
				alien tempent = aliens[i][j];
				if(!tempent.isalive()){continue;}
				
				if(tempent.getx()<=margins){
					alientracker.setdirx(I_Intruders.DEFAULT_ALIEN_SPEED);
					alientracker.setx(margins+1-aliens[i][j].getalienoffsetx());
					break outerloop; //here we break out of the loop, because we do not need to check if this is the case for any other loop
				}
				else if(tempent.getx()+I_Intruders.ALIEN_SIZE_X>=mainscreen.getsizex()-margins){
					alientracker.setdirx(-I_Intruders.DEFAULT_ALIEN_SPEED);
					alientracker.setx(mainscreen.getsizex()-margins-aliens[i][j].getalienoffsetx()-I_Intruders.ALIEN_SIZE_X);
					break outerloop; //here we break out of the loop, because we do not need to check if this is the case for any other loop
				}
				
				
			}
		}//end of outerloop
		
		
		
		//checking the collisions of all bullets with the corresponding entity
		for(int i = 0; i<entities.size(); i++){
			entity tempent = entities.get(i);
			if(tempent instanceof bullet){
				
				if(tempent.gety()+tempent.getsizey()<0 || tempent.gety()>mainscreen.getsizey()){destroyentity(tempent); continue;}//first checking if the bullet is still in bounds
				if(((bullet) tempent).isalien()){//the bullet is alien, so checking the collisions with the player
					if(!respawning && player.checkcols(tempent)){killplayer(); destroybullet(tempent); continue;}
				}
				else{//the bullet belongs to the player, so checking collisions with all aliens
					for(int j = 0; j<aliensacross; j++){
						for(int k = 0; k<aliensdown; k++){
							alien tempalien = aliens[j][k];
							
							//if(!tempalien.isalive()){continue;} // if the alien is not alive, then just go on to the next alien
							if(tempalien.checkcols(tempent)){//there is a collision between the alien and bullet, so destroy the bullet and kill the alien
								destroybullet(tempent);
								killalien(j, k);
							}
						}
					}
				}
			}
			//if the entity was not a bullet, skip to the next entity
			else continue;
		}
	}
	

	private void updatealienpositions(){//this functions sets the positions of the aliens relative to the "alientracker" entity.
		for(int i = 0; i<aliensacross; i++){
			for(int j = 0; j<aliensdown; j++){
				aliens[i][j].setalienposition(alientracker);
			}
		}
	}
	
	private void killalien(int arrayposition1, int arrayposition2){ //this subtracts one from the alien counter
		alien tempalien = aliens[arrayposition1][arrayposition2];
		if(tempalien.isalive()){
			aliensalive--;
			aliens[arrayposition1][arrayposition2].kill(this);
			score+=50;
		}
		//checking if we should:  start the fireworks creation thread, draw the text to the screen, 3
		if(aliensalive == 0 && !complete){
			complete=true;
			player.setcollideable(false); //setting the player to non-collideable just to make sure that we are not killed by a remaining bullet
			new Thread(new fireworkspawner(this)).start();
		}
	}
	
	
	private void killplayer() {//this will perform the action that is taken when the player is killed
		lives--;
		deaths++;
		respawning = true;//changing this to true here to prevent a lag during thread execution to cause multiple lives to be deducted
		player.createpeffect(this);
		if(lives<=0){quittype = 2; quit=true; return;}
		new Thread(new respawner(700)).start();//this does all of the necessary things. eg, setting respawning=true, counting down till respawning, hiding the player, removing bullets, once it's finished it also respawns the player and sets it back to the default start position
	}
	
	private void addbullet(boolean isalien, int positionx, int positiony){//adds a new bullet to the entities list
		if(!isalien)shotsfired++;
		entities.add(new bullet(this, positionx, positiony, isalien));
	}
	
	private void destroybullet(entity tempbullet){
		//entities.add(bulletexplodeeffects.createnewpeffect((int)tempbullet.getx()+1, (int)tempbullet.gety()));
		destroyentity(tempbullet);
	}
	
	public void draw() {
		//blitting background
		I_Intruders.parisbackground.draw(mainscreen.screengraphics, 0, I_Intruders.PARIS_BACKGROUND_Y_OFFSET);
		
		//drawing entities, including particle effects and bullets
		for(int i = 0; i<entities.size(); i++){
			entity tempent = entities.get(i);
			if(tempent instanceof bullet){((bullet)tempent).draw(this); }
			entities.get(i).draw();
		}
		
		//drawing the player's ship
		player.draw();
		
		//drawing the aliens
		for(int i = 0; i<aliensacross; i++){//going across the aliens on the x axis first
			for(int j = 0; j<aliensdown; j++){//for each column on the x axis, drwing each on going down the y axis
				aliens[i][j].draw();
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
		String message1 = "WAVE "+thiswave;
		Font f1 = new Font(Font.DIALOG_INPUT, Font.PLAIN, 25);
		mainscreen.screengraphics.setFont(f1);
		int textlength1 = mainscreen.screengraphics.getFontMetrics().stringWidth(message1);
		mainscreen.screengraphics.drawString(message1, mainscreen.getsizex()/2-textlength1/2, 30);
		
		Font f2 = new Font(Font.DIALOG_INPUT, Font.PLAIN, 15);
		mainscreen.screengraphics.setFont(f2);
		String message2 = "DEMO - Press up to kill all aliens, down to go to stats.";
		int textlength2 = mainscreen.screengraphics.getFontMetrics().stringWidth(message2);
		mainscreen.screengraphics.drawString(message2, mainscreen.getsizex()/2-textlength2/2, 45);
		
		
		/*
		//drawing the margins for debugging purposes
		mainscreen.screengraphics.setColor(Color.yellow);
		mainscreen.screengraphics.drawLine(margins, 0, margins, mainscreen.getsizey());
		mainscreen.screengraphics.drawLine(mainscreen.getsizex()-margins, 0, mainscreen.getsizex()-margins, mainscreen.getsizey());
		
		//drawing the alientracker for debugging purposes
		alientracker.draw();
		*/
		
		//deciding whether to draw the "press enter to progress to the next wave" text
		if(complete && !quit){
			mainscreen.screengraphics.setColor(Color.white);
			String message = "PRESS ENTER TO PROGRESS TO WAVE "+(thiswave+1)+"...";
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

	@Override
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
		if(quittype == 1)score += 100*thiswave;
		I_Intruders.SP_TOTAL_SHOTS_FIRED += shotsfired;
		I_Intruders.SP_TOTAL_TIME_PLAYED += System.currentTimeMillis()-gamestart;
		I_Intruders.SP_TOTAL_DEATHS += deaths;
		I_Intruders.SP_LIVES = lives;
		I_Intruders.SP_WAVES_COMPLETED++;
		I_Intruders.SP_TOTAL_ALIENS_KILLED += aliensacross*aliensdown-aliensalive;
		I_Intruders.SP_TOTAL_SCORE +=score;
		I_Intruders.SP_SCORES.add(score);
	}
	
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
			for(int i = 0; i<entities.size(); i++){
				if(entities.get(i) instanceof bullet){destroyentity(entities.get(i));}
			}
			updateremoveentities();
			
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
