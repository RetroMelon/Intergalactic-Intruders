package com.Capslock.IntergalacticIntruders;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Random;

import com.Capslock.Engine.*;



/*QUIT TYPES///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 			
 			0		--		QUIT TO MAIN MENU
 			1		--		MOVE TO NEXT WAVE
 			2		--		END GAME AND GO TO STATS SCREEN
 			3		--		LOST CONNECTION
 			
 *//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

			LOOK AT MPSERVER FOR DETAILS ON NETWORK TRAFFIC
									
*////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class mpclient extends game{
	
	Random random = new Random(System.currentTimeMillis());

	
	//variables which will not be required by the client
	private int aliensalive = mpserver.aliensacross*mpserver.aliensdown;
	
	//to do with respawning
	private boolean respawning = false; //this determines wether the respawn counter is drawn to the screen
	private int respawncounter = 3;
	
	//variables related to being finished the game
	private boolean fail = false;
	private boolean complete = false; //this is the boolean that is set to true when all aliens are killed
	private boolean P1_ready = false; //this variable is set to true when the player pressed the enter button when a wave is finished
	private boolean P2_ready = false; //this variable is set to true when the server recieves an "R" from the client, letting it know that the client is ready to progress to the next wave
	
	
	//variables related to game stats
	private final int thiswave = I_Intruders.MP_WAVES_COMPLETED+1;
	private long gamestart;
	
	//variables used when testing the connection
	private boolean connected = true; //this is whether the socket is still connected to the client
	private connectiontester ctester;
	
	//actual entities within the game
	ship player1;
	ship player2;
	tracker alientracker;
	alien[][] aliens = new alien[mpserver.aliensacross][mpserver.aliensdown];
	
	ArrayList<mpbullet> alien_bullets = new ArrayList<mpbullet>(); // the array that the aliens' bullets are stored in.
	ArrayList<mpbullet> P1_bullets = new ArrayList<mpbullet>();//the array that the player's bullets are stored in.
	ArrayList<mpbullet> P2_bullets = new ArrayList<mpbullet>();//the array that the team-mate's bullets are stored in.
	
	ArrayList<mpbullet> remove_alien_bullets = new ArrayList<mpbullet>();
	ArrayList<mpbullet> remove_P1_bullets = new ArrayList<mpbullet>();
	ArrayList<mpbullet> remove_P2_bullets = new ArrayList<mpbullet>();
	
	//variables relating to tracking the id numbers of bullets...
	//the ++ operation should be performed on this AFTER each use of it
	private int nextplayerbulletid = 0;
	
	//input keys
	private boolean left = false;
	private boolean right = false;
	private boolean space = false;
	private int lastx = 0; //the position that the player was at last frame. this prevents excessive network traffic
	
	//variables to do with pausing
	private boolean sudopaused = false;//refer to server for explanation
	private boolean lastescpressed = false; //refer to server for explanation
	
	//variables related to shooting of player and aliens
	private long lastshoottime = System.currentTimeMillis();
	private int defaultalienshootfrequency = 4;// the higher the second number, the slower the shoot speed
	private int alienshootfrequency = defaultalienshootfrequency+(thiswave>=10 && thiswave<=20 ? (10+2*(thiswave-9)) : thiswave ); //higher means faster shooting. 100 means approx once a second. 300 means approx 3 times a second, etc
	


	public mpclient(screen thescreen) {
		super(thescreen);

		//retrieving the server from the previous game...
		//connection = s;
		
	}

	@Override
	public void init() {
						
		//creating the players
		player1 = new ship(mainscreen, 1);
		player2 = new ship(mainscreen, 2);
		
		player1.setx(I_Intruders.SHIP_START_X);
		player2.setx(I_Intruders.SHIP_START_X);
		
		
		//creating a "tracker" entity, which will be used to keep track of the aliens. it will be positioned in the uppermost left hand corner, and all other aliens will set their positons relative to this
		alientracker = new tracker(mainscreen, mpserver.margins+mpserver.squishingmargin, mpserver.ALIEN_START_Y);
		
		
		//calculating spacing values for aliens
		mpserver.ALIEN_GAP_X = (mainscreen.getsizex() -2*(mpserver.margins+mpserver.squishingmargin) - I_Intruders.ALIEN_SIZE_X*mpserver.aliensacross) / (mpserver.aliensacross-1);//aliensacross-1
		
		//creating the aliens
		//starting going across each column, then for each cycle, populating down each row.
		for(int i = 0; i<mpserver.aliensacross; i++){
			for(int j = 0; j<mpserver.aliensdown; j++){
				alien tempalien = new alien(mainscreen);
				tempalien.setalienoffsetx(i*(mpserver.ALIEN_GAP_X+I_Intruders.ALIEN_SIZE_X));
				tempalien.setalienoffsety(j*(I_Intruders.ALIEN_SIZE_X+mpserver.ALIEN_GAP_Y));
				aliens[i][j] = tempalien;
			}
		}
		
		updatealienpositions();
		alientracker.setdirx(I_Intruders.DEFAULT_ALIEN_SPEED);
		
		//starting the connection testing thread
		ctester = new connectiontester();
		new Thread(ctester).start();
		
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
			
			//drawing the counter text to the screen
			fontsize = 30;
			f = new Font(Font.DIALOG_INPUT, Font.PLAIN, fontsize);
			mainscreen.screengraphics.setFont(f);
			message = "Starting in:  1.23s";
			textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
			message = "Starting in:  "+countdown+"s";
			mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2+70);
			
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
						
			mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)1));
			
			//drawing the counter text to the screen
			fontsize = 30;
			f = new Font(Font.DIALOG_INPUT, Font.PLAIN, fontsize);
			mainscreen.screengraphics.setFont(f);
			message = "Starting in:  1.23s";
			textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
			message = "Starting in:  "+countdown+"s";
			mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2+70);
			
			mainscreen.update();
			
			try{Thread.sleep(1000/fadeframerate);}catch(Exception e){}
		}
		mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)1));
	}

	/*
	 * 
	 *		 0		--		QUIT TO MAIN MENU
 			1		--		MOVE TO NEXT WAVE
 			2		--		END GAME AND GO TO STATS SCREEN
 			3		--		LOST CONNECTION

	 */

 			
 			
	
	@Override
	public void input() {	
		if(mainscreen.keyboard.escpressed){
			if(sudopaused && !lastescpressed){//we were paused, so we unpause
				sudopaused=false;
			}else if(!sudopaused && !lastescpressed){//we weren't paused but now we are pausing
				sudopaused=true;
			}
		}
		lastescpressed=mainscreen.keyboard.escpressed;
		
		if(mainscreen.keyboard.enterpressed){
			if (sudopaused){//we send the with command and then quit
					quittype = 0;
					sendquit();
					quit=true;
			}
			else if(complete || fail){//send the signal that we are ready
				P1_ready=true;
				sendready();
			}
		} // this is the button that will be used to progress to the next round
		
		left = mainscreen.keyboard.leftpressed;
		right = mainscreen.keyboard.rightpressed;
		space = mainscreen.keyboard.spacepressed;
	}

	@Override
	public void update() {
		//testing that the connection to the client is still open... if it is not, we quit with quit type 3
		if(!connected){System.out.println("quitting MP game with quit type 3 - lost connection..."); quittype = 3; quit = true;}
		
		/*
		moveall();//moving all entities
		otheractions();//shooting, and producing particle effects for bullets, but not explosions- they are done by the alien
		checkallcols();//checking bullet collisions, and whether any entities should be allowed to move any further, eg. when hitting a wall
		*/
		
		processnetworktraffic();
		
		moveall(); //moving of all bullets, players, aliens. checks if the player and aliens are out of bounds, and if so, corrects them
		
		removebullets();//removing bullets if they are outside the screen
		
		updateplayer1();//checks if the player wants to fire, and acts on it if so
		updateplayer2();
		
		updatealiens(); // does stuff like firing alien bullets and sending the firing as network traffic
		
		checkcollisions(); // checks collisions of player 1 with alien bullets, and aliens with player 1 bullets.... sends all data about destroying bullets and aliens
		
		sendnetworktraffic(); // sends all other network traffic about lives and scores etc
				
		
	}
	
	private void processnetworktraffic(){
		//recieving all network traffic for the frame and acting on it
		//network traffic will be sent after updating
			String message = I_Intruders.MP_CLIENT.read();
			while(message.length()>0){
				switch(message.charAt(0)){
				
				//player 2 died
				case 'D':
					int coord = Integer.parseInt(message.substring(1));
					killP2(coord);
					break;
					
				//player 2 killed an alien
				case 'K':
					int aliennumber = Integer.parseInt(message.substring(1));
					killalien(2, aliennumber);
					break;
				
				//player 2 sends it's new position
				case 'P':
					int newposition = Integer.parseInt(message.substring(1));
					player2.setx(newposition);
					break;
				
				//player 2 has fired a bullet
				case 'S':
					message = message.substring(1);
					try{
						String[] numbers = message.split("¬");
						addP2bullet(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]));
					}catch(Exception e){System.out.println("FAILED TO ADD P2 BULLET... COULD NOT PARSE NETWORK DATA..."); break;}
					break;
					
				//the server is notifying us that an alien has fired a bullet
				case 'A':
					message=message.substring(1);
					try{
						String[] numbers = message.split("¬");
						addalienbullet(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]), Integer.parseInt(numbers[2]));
					}catch(Exception e){System.out.println("FAILED TO ADD ALIEN BULLET... COULD NOT PARSE NETWORK DATA..."); break;}
					break;
				
				//player 2 requires one of it's bullet to be destroyed
				case 'B':
					int bulletid = Integer.parseInt(message.substring(1));
					destroyP2bullet(bulletid);
					break;
				
				//player 2 requires an alien bullet to be destroyed
				case 'X':
					int bulletid1 = Integer.parseInt(message.substring(1));
					destroyalienbullet(bulletid1);
					break;
					
				//the server is telling the client what the new number of lives is
				case 'L':
					I_Intruders.MP_LIVES=Integer.parseInt(message.substring(1));
					break;
					
				//the server is notifying us that all aliens have been killed, and that we can, once ready, move on to the next round
				case 'C':
					complete();
					break;
				
				//player 2 wishes to quit the game
				case 'Q':
					quittype = 0;
					quit = true;
					return;
					
				//'player 2 is ready to move on to the next wave
				case 'R':
					P2_ready = true;
					break;
					
				//server is telling us to move on to the next wave
				case 'N':
					nextwave();
					break;
					
				//the server is telling us that we have failed, and to display the fail screen
				case 'F':
					fail();
					break;
					
				//server is telling us to actually progress to the score screen
				case 'E':
					endfail();
					break;
					
				case ' ':
					return;
					
				default:
					System.out.println("SERVER RECIEVED UNFAMILIAR NETWORK TRAFFIC...");
					
				}//end of the switch statement
				message=I_Intruders.MP_CLIENT.read();
			}
	}
	
	private void moveall(){
		moveplayer1();
		moveplayer2();
		
		movebullets();
		
		movealiens();		
	}
	
	private void moveplayer1(){
		//moving player
		player1.setaccelx(0);
		
		if(right)player1.setaccelx(I_Intruders.SHIP_SPEED);
		else if(left)player1.setaccelx(-I_Intruders.SHIP_SPEED);
		
		player1.move(frametimer.getactualframerate());
		
		//checking to make sure the player is not going outside the screen
		if(player1.getx()<=0){player1.setx(0); player1.setdirx(0);}
		else if(player1.getx()+player1.getsizex()>=mainscreen.getsizex()){player1.setx(mainscreen.getsizex()-player1.getsizex()); player1.setdirx(0);}
		
	}
	
	private void moveplayer2(){
		//does nothing
	}
	
	private void movebullets(){
		int framerate = frametimer.getactualframerate();
		//moving alien bullets
		for(int i = 0; i<alien_bullets.size(); i++){
			alien_bullets.get(i).move(framerate);
		}
		//moving player 1 bullets
		for(int i = 0; i<P1_bullets.size(); i++){
			P1_bullets.get(i).move(framerate);
		}
		//moving player 2 bullets
		for(int i = 0; i<P2_bullets.size(); i++){
			P2_bullets.get(i).move(framerate);
		}
	}
	
	private void movealiens(){
		
		//actually moving the tracker
		alientracker.move(frametimer.getactualframerate());
		
		//updating where each alien sits relative to the tracker
		updatealienpositions();
		
		//making sure the aliens do not go outwith the boundaries of the screen
				outerloop:for(int i = 0; i<mpserver.aliensacross; i++){
					for(int j = 0; j<mpserver.aliensdown; j++){
						
						alien tempent = aliens[i][j];
						if(!tempent.isalive()){continue;}
						
						if(tempent.getx()<=mpserver.margins){
							alientracker.setdirx(I_Intruders.DEFAULT_ALIEN_SPEED);
							alientracker.setx(mpserver.margins+1-aliens[i][j].getalienoffsetx());
							break outerloop; //here we break out of the loop, because we do not need to check if this is the case for any other loop
						}
						else if(tempent.getx()+I_Intruders.ALIEN_SIZE_X>=mainscreen.getsizex()-mpserver.margins){
							alientracker.setdirx(-I_Intruders.DEFAULT_ALIEN_SPEED);
							alientracker.setx(mainscreen.getsizex()-mpserver.margins-aliens[i][j].getalienoffsetx()-I_Intruders.ALIEN_SIZE_X);
							break outerloop; //here we break out of the loop, because we do not need to check if this is the case for any other loop
						}
						
						
					}
				}//end of outerloop
		
		//updating where each alien sits relative to the tracker
		updatealienpositions();
		
	}
	
	private void updateplayer1(){//this function currently really only decides whether the player can shoot or not
		
		//checking if we are actually able to shoot
		boolean canshoot = (System.currentTimeMillis()>=lastshoottime+I_Intruders.SHOOT_TIME_GAP) && player1.getcanshoot();
		
		if(space && canshoot){//if we are pressing the space bar and we are allowed to shoot then shoot.
			//calling the function which will add a bullet to the screen will also send the network packet to the client to add the bullet
			addP1bullet((int)(player1.getx()+player1.getsizex()/2-I_Intruders.BULLET_BOUNDS_X/2));
			
			//updating the last shoot time to be the corrent time
			lastshoottime = System.currentTimeMillis();
		}
		
	}
	
	private void updateplayer2(){
		//does nothing
	}
	
	private void updatealiens(){
		//getting the aliens to randomly shoot
		outershootloop:for(int i = 0; i<mpserver.aliensacross; i++){//for each alien column across the x axis, we go up the y axis from bottom to top, and record the first alien we come across, if any.
			int lowestalienj= -1;
			for(int j = mpserver.aliensdown-1; j>0; j--){
				if(aliens[i][j].isalive()){lowestalienj=j; break;} //if we have found an alien that is alive, then it will be the lowest, so we break, and start to get it to actually shoot
				if(j==0)continue outershootloop;
			}
		}
		
	}
	
	private void checkcollisions(){
		
		checkP1collisions();
		
		checkaliencollisions();
		
	}
	
	private void checkP1collisions(){
		//checking collisions between the player and the alien bullets
		for(int i = 0; i<alien_bullets.size(); i++){
			//if the player is colliding with the bullet
			if(player1.checkcols(alien_bullets.get(i))){
				//calling the kill function, which will also send network data
				killP1();
				
				//destroying the bullet
				destroyalienbullet(alien_bullets.get(i));
				
				//sending a message to the client to destroy the bullet. this is not built in to the destroybullet function because sometimes we don't want to have to send the data
				sendalienbulletdestroy(alien_bullets.get(i).getid());
				
			}
		}
	}
	
	private void checkaliencollisions(){
		//checking the aliens' collisions between only player 1's bullets
		for(int i = 0; i<P1_bullets.size(); i++){
			//going through each of the aliens which are still alive and checking the collisions with them
			for(int x = 0; x<mpserver.aliensacross; x++){
				for(int y = 0; y<mpserver.aliensdown; y++){
					alien tempalien = aliens[x][y];
					
					if(tempalien.checkcols(P1_bullets.get(i))){
						//killing the alien
						//this method will kill the alien, play an animation, subtract one from the lives, and decide whether the game has been completed
						killalien(1, x, y);
						
						//destroying the bullet
						destroyP1bullet(P1_bullets.get(i).getid());
						
						//sending the data to destroy an alien, and the appropriate bullet
						sendalienkill(getalienid(x, y));
						sendplayerbulletdestroy(P1_bullets.get(i).getid());
						
					}
					
					
				}
			}
		}
	}

	private void sendnetworktraffic(){
		//sending the position of the player
		sendplayerpos();
				
	}
	
	private void updatealienpositions(){//this functions sets the positions of the aliens relative to the "alientracker" entity.
		for(int i = 0; i<mpserver.aliensacross; i++){
			for(int j = 0; j<mpserver.aliensdown; j++){
				aliens[i][j].setalienposition(alientracker);
			}
		}
	}
	
	private void killalien(int playernumber, int aliennumber){
		killalien(playernumber, getidx(aliennumber), getidy(aliennumber));
	}
	
	private void killalien(int playernumber, int arrayposition1, int arrayposition2){ //this subtracts one from the alien counter
		//destroy the alien
		aliens[arrayposition1][arrayposition2].kill(this);
		
		//take one away from aliens alive
		aliensalive--;
		I_Intruders.MP_SCORE+=50;
	}
	
	//this will perform the action that is taken when the player is killed
	private void killP1() {
		//first creating a particle effect explosion
		player1.createpeffect(this);
		
		//subtracting one from the lives
		I_Intruders.MP_LIVES--;
		
		//sending network data
		senddeath((int)player1.getx());
		
		//deciding what to do based on the number of lives remaining
		if(I_Intruders.MP_LIVES>=0){
			//respawning the player
			new Thread(new p1respawner()).start();
		}
	}
	
	private void killP2(int xcoord){
		System.out.println("PLAYER 2 DIED... ");
		
		respawnP2();//does the animation required
	}
	
	private void respawnP2(){
		player2.createpeffect(this);
		new Thread(new p2respawner()).start();
	}
	
	
	private void addalienbullet(int positionx, int positiony, int idnumber){//adds a new alien bullet, and also tracks the id numbers and sends a packet across the network
		//adding a new bullet to the array
		alien_bullets.add(new mpbullet(this, positionx, positiony, true, idnumber));		
	}
	
	private void addP1bullet(int positionx){//does all the actions required in tracking and shooting a bullet. including sending network traffic
		//first adding the bullet
		P1_bullets.add(new mpbullet(this, positionx, I_Intruders.SHIP_Y+mpserver.BULLET_OFFSET, false, nextplayerbulletid));
		
		//sending the network traffic required to notify the client that a bullet has been fired
		sendplayershoot(positionx, nextplayerbulletid);
		
		//now adding one to the id numbers so it is ready to be used with the next bullet
		nextplayerbulletid++;
	}
	
	private void addP2bullet(int positionx, int idnumber){//this doesnot require any data to be sent across the network or tracked so it is simpler
		P2_bullets.add(new mpbullet(this, positionx, I_Intruders.SHIP_Y, false, idnumber));
	}

	private void removebullets(){
		//getting rid of any bullets which are no longer needed
				for(int i = 0; i<P1_bullets.size(); i++){
					mpbullet tempbullet = P1_bullets.get(i);
					if(tempbullet.gety()>mainscreen.getsizey() || tempbullet.gety()+I_Intruders.BULLET_BOUNDS_Y<0){
						destroyP1bullet(tempbullet);
					}
				}
				for(int i = 0; i<P2_bullets.size(); i++){
					mpbullet tempbullet = P2_bullets.get(i);
					if(tempbullet.gety()>mainscreen.getsizey() || tempbullet.gety()+I_Intruders.BULLET_BOUNDS_Y<0){
						destroyP2bullet(tempbullet);
					}
				}
				for(int i = 0; i<alien_bullets.size(); i++){
					mpbullet tempbullet = alien_bullets.get(i);
					if(tempbullet.gety()>mainscreen.getsizey() || tempbullet.gety()+I_Intruders.BULLET_BOUNDS_Y<0){
						destroyalienbullet(tempbullet);
					}
				}
				
				updateallbulletdestroys();//actually removes all bullets from the arraylists
		
	}
	
	private void destroyP1bullet(mpbullet tempbullet){
		remove_P1_bullets.add(tempbullet);
	}
	
	private void destroyP1bullet(int bulletid){
		for(int i = 0; i<P1_bullets.size(); i++){
			if(P1_bullets.get(i).getid()==bulletid){
				destroyP1bullet(P1_bullets.get(i));
				return;
			}
		}
	}
	
	private void destroyP2bullet(mpbullet tempbullet){
		remove_P2_bullets.add(tempbullet);
	}
	
	private void destroyP2bullet(int bulletid){
		for(int i = 0; i<P2_bullets.size(); i++){
			if(P2_bullets.get(i).getid()==bulletid){
				destroyP2bullet(P2_bullets.get(i));
				return;
			}
		}
	}
	
	private void destroyalienbullet(mpbullet tempbullet){
		remove_alien_bullets.add(tempbullet);
	}
	
	private void destroyalienbullet(int bulletid){
		for(int i = 0; i<alien_bullets.size(); i++){
			if(alien_bullets.get(i).getid()==bulletid){
				destroyalienbullet(alien_bullets.get(i));
				return;
			}
		}
	}
	
	private void updateallbulletdestroys(){
		P1_bullets.removeAll(remove_P1_bullets);
		P2_bullets.removeAll(remove_P2_bullets);
		alien_bullets.removeAll(remove_alien_bullets);
		
		
		remove_P1_bullets.clear();
		remove_P2_bullets.clear();
		remove_alien_bullets.clear();
	}
	
	public void draw() {
		//blitting background
		I_Intruders.parisbackground.draw(mainscreen.screengraphics, 0, I_Intruders.PARIS_BACKGROUND_Y_OFFSET);
		
		//drawing entities aka, particle effects
		for(int i = 0; i<entities.size(); i++){
			entity tempent = entities.get(i);
			if(tempent instanceof bullet){((bullet)tempent).draw(this); }
			entities.get(i).draw();
		}
		
		//drawing the bullets of all players
		for(int i = 0; i<P1_bullets.size(); i++){
			P1_bullets.get(i).draw(this);
		}
		for(int i = 0; i<P2_bullets.size(); i++){
			P2_bullets.get(i).draw(this);
		}
		for(int i = 0; i<alien_bullets.size(); i++){
			alien_bullets.get(i).draw(this);
		}
		
		//drawing the players...the player 2 goes in front of player 1 because it is mostly transparent
		if(!fail){
			player1.draw();
			player2.draw();
		}
		
		//drawing the aliens
		for(int i = 0; i<mpserver.aliensacross; i++){//going across the aliens on the x axis first
			for(int j = 0; j<mpserver.aliensdown; j++){//for each column on the x axis, drwing each on going down the y axis
				aliens[i][j].draw();
			}
		}
		
		//drawing the number of lives the players have
		int tempoffsetx = 90;
		I_Intruders.scaledshipsprite.draw(mainscreen.screengraphics, mainscreen.getsizex()-tempoffsetx, 10);
		Font tempfont = mainscreen.screengraphics.getFont();
		mainscreen.screengraphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));//size 18
		mainscreen.screengraphics.setColor(Color.white);
		mainscreen.screengraphics.drawString("= "+I_Intruders.MP_LIVES, mainscreen.getsizex()-tempoffsetx+40, 10+18);//10+18
		mainscreen.screengraphics.setFont(tempfont);
		
		//drawing the wave number that the player is currently on.
		mainscreen.screengraphics.setColor(Color.white);
		String message1 = "WAVE "+thiswave;
		Font f1 = new Font(Font.DIALOG_INPUT, Font.PLAIN, 25);
		mainscreen.screengraphics.setFont(f1);
		int textlength1 = mainscreen.screengraphics.getFontMetrics().stringWidth(message1);
		mainscreen.screengraphics.drawString(message1, mainscreen.getsizex()/2-textlength1/2, 30);
		
		//drawing teh score on to the screen
				mainscreen.screengraphics.setColor(Color.white);
				message1 = "Score: "+I_Intruders.MP_SCORE;
				f1 = new Font(Font.DIALOG_INPUT, Font.PLAIN, 20);
				mainscreen.screengraphics.setFont(f1);
				textlength1 = mainscreen.screengraphics.getFontMetrics().stringWidth(message1);
				mainscreen.screengraphics.drawString(message1, mainscreen.getsizex()/2-textlength1/2, 50);
		
		//telling the player that they are the client
		mainscreen.screengraphics.setColor(Color.white);
		message1 = "CLIENT";
		f1 = new Font(Font.DIALOG_INPUT, Font.PLAIN, 15);
		mainscreen.screengraphics.setFont(f1);
		textlength1 = mainscreen.screengraphics.getFontMetrics().stringWidth(message1);
		mainscreen.screengraphics.drawString(message1, 5, 25);
				
		//drawing the current score to the screen (the score will be a set amount for each wave minus the number of milliseconds taken to kill the wave)
		
		
		//deciding whether to draw the "press enter to progress to the next wave" text
		if(complete && !quit){
			mainscreen.screengraphics.setColor(Color.white);
			String message = "PRESS ENTER TO PROGRESS TO WAVE "+(thiswave+1)+"...";
			Font f = new Font(Font.DIALOG_INPUT, Font.PLAIN, 30);
			mainscreen.screengraphics.setFont(f);
			int textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
			mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2);
			
			int offset = 0;
			drawreadyindicators(offset);
		}
		
		//if we have failed
		else if(fail && !quit){
			drawgameoverscreen();
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
		
		if(sudopaused){
			drawsudopausedscreen();
		}
		
		//drawing the framerate indicator
		frametimer.draw(mainscreen.screengraphics);
	}
	
	private void drawsudopausedscreen(){
		mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.72));
		mainscreen.screengraphics.setColor(Color.DARK_GRAY);
		mainscreen.screengraphics.fillRect(0, 0, mainscreen.getsizex(), mainscreen.getsizey());
		mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)1));
		mainscreen.screengraphics.setColor(Color.white);
		String message = "GAME PAUSED!";
		Font f = new Font(Font.DIALOG_INPUT, Font.BOLD, 23);
		mainscreen.screengraphics.setFont(f);
		int textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
		mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2);
		message= "Press ESC to unpause, or ENTER to return to the previous screen.";
		f = new Font(Font.DIALOG_INPUT, Font.BOLD, 18);
		mainscreen.screengraphics.setFont(f);
		textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
		mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2+40);
	}
	
	private void drawgameoverscreen(){
			int yconstant = -30;
			
			//drawing the red layer
			mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.52));
			mainscreen.screengraphics.setColor(new Color(201, 0, 46));
			mainscreen.screengraphics.fillRect(0, 0, mainscreen.getsizex(), mainscreen.getsizey());
			mainscreen.screengraphics.setColor(Color.DARK_GRAY);
			mainscreen.screengraphics.fillRect(0, 0, mainscreen.getsizex(), mainscreen.getsizey());
			mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)1));
			
			//game over text
			mainscreen.screengraphics.setColor(Color.white);
			String message = "GAME OVER!";
			Font f = new Font(Font.DIALOG_INPUT, Font.PLAIN, 35);
			mainscreen.screengraphics.setFont(f);
			int textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
			mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2+yconstant);
			
			drawreadyindicators(yconstant);
	}
	
	private void drawreadyindicators(int yconst){
		String message = "";
		Font f;
		int textlength;
		
		int yconstant = yconst;
		
		//press enter to ready yourself text
		mainscreen.screengraphics.setColor(Color.white);
		message = "PRESS ENTER TO PROGRESS!";
		f = new Font(Font.DIALOG_INPUT, Font.PLAIN, 20);
		mainscreen.screengraphics.setFont(f);
		textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
		mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2+30+yconstant);
		
		
		message = "YOU     -     READY";
		f = new Font(Font.DIALOG_INPUT, Font.PLAIN, 18);
		mainscreen.screengraphics.setFont(f);
		textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(message);
		//player 1 is ready text
		if(P1_ready){
			mainscreen.screengraphics.setColor(Color.GREEN);
			message = "YOU     -     READY";
			
		}
		else{
			mainscreen.screengraphics.setColor(Color.RED);
			message = "YOU     - NOT READY";
		}
		mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2+60+yconstant);
		
		
		//player 2 is ready text
		if(P2_ready){
			mainscreen.screengraphics.setColor(Color.GREEN);
			message = "P2      -     READY";
			
		}
		else{
			mainscreen.screengraphics.setColor(Color.RED);
			message = "P2      - NOT READY";
		}
		mainscreen.screengraphics.drawString(message, mainscreen.getsizex()/2-textlength/2, mainscreen.getsizey()/2+80+yconstant);
	}
	
	
	private void complete(){//does all the tasks that should be done after all the aliens have been destroyed
		complete=true;
		
		//starting the fireworks display
		System.out.println("starting client fireworks...");
		new Thread(new fireworkspawner(this)).start();
		
		//making the player invincible
		player1.setcollideable(false);
		
		//killing all the aliens even though they should be dead anyways
		//killallaliens();
	}
	
	private void fail(){//the method that is called if all the lives have been used up.
		fail=true;
		
		//make the players invincible
		player1.setcollideable(false);
		player2.setcollideable(false);
		
		//making the players unable to shoot
		player1.setcanshoot(false);
		player2.setcanshoot(false);
		
		//destroying all bullets
		P1_bullets.clear();
		P2_bullets.clear();
		alien_bullets.clear();
		
	}
	
	//the method that is called when we are actually exiting the game to move to the score screen.
	private void endfail(){
		System.out.println("CLIENT CALLING ENDFAIL()...");
		quittype = 2;
		quit=true;
	}
	
	private void nextwave(){//does the actual moving to next wave, eg quitting with certain quittypes
		System.out.println("CLIENT CALLING NEXTWAVE()...");
		
		I_Intruders.MP_SCORE+=100*thiswave;
		
		quittype = 1;
		quit=true;
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
		
		//if we have actually quit, then close the socket
		if(quittype==3){
			 I_Intruders.MP_CLIENT.disconnect();
		}
		
		return quittype;
	}
	
	//this method updates the single player game stat variables in I_Invaders with the ones stored in this game
	private void updategamestats(){

	}
	
	private int getalienid(int xarraynum, int yarraynum){//used to get the id number of an alien in the grid given the coords
		return (mpserver.aliensdown*xarraynum)+yarraynum;
	}
	
	private int getidy(int alienid){//used to get the y coord of an alien in the grid given the id number
		return alienid%mpserver.aliensdown;
	}
	
	private int getidx(int alienid){//used to get the x coord of an alien in the grid given the id number
		try{
			return (int)Math.floor((double)alienid/(double)mpserver.aliensdown);
		}catch(Exception e){return 0;}
	}

	
	
	//this class is responsible for all respawning needs, including removing bullets, and then counting down before respawning the player.
	private class p1respawner implements Runnable{
		
		private int timedelay = 500;
		
		public p1respawner(){
		}

		public void run() {
			//changing "respawning" to true so the draw method knows what to draw
			respawning = true;
			
			//hiding the player and making it unable to shoot
			player1.setcollideable(false);
			player1.setcanshoot(false);
			player1.setdrawable(false);
			
			//creating a loop that counts down until the player is respawned.
			for(int i = 3; i>0; i--){
				respawncounter = i;
				try{Thread.sleep(timedelay);}catch(Exception e){}
			}
			
			//starting the "respawn" sequence for the player which causes it to flash on and off, but it is still invincible
			player1.respawn();//respawns the player without a particle effect
			
			//resetting the player's position
			player1.setx(I_Intruders.SHIP_START_X);
			
			//setting respawning = false again, so we can stop drawing the red haze.
			respawning = false;
		}
		
	}
	
	private class p2respawner implements Runnable{
		
		public p2respawner(){}
	
		public void run(){
			player2.setdrawable(false);
			try{Thread.sleep(1500);}catch(Exception e){}
			player2.setx(I_Intruders.SHIP_START_X);
			player2.respawn();
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
	
	private class connectiontester implements Runnable{
		
		private int interval = 3000; //milliseconds between testings

		@Override
		public void run() {
			while(!quit && connected){
				connected = I_Intruders.MP_CLIENT.testconnection();
				try{Thread.sleep(interval);}catch(Exception e){}
			}
		}
		
		
	}
	
	//all methods relating to networking
	private void sendplayershoot(int bulletposition, int idnumber){I_Intruders.MP_CLIENT.println("S"+bulletposition+"¬"+idnumber);}
	private void sendalienshoot(int bulletx, int bullety, int idnumber){I_Intruders.MP_CLIENT.println("A"+bulletx+"¬"+bullety+"¬"+idnumber);}
	private void senddeath(int xcoord){I_Intruders.MP_CLIENT.println("D"+xcoord);}
	
	private void sendalienkill(int idnumber){I_Intruders.MP_CLIENT.println("K"+idnumber);}
	private void sendalienbulletdestroy(int idnumber){I_Intruders.MP_CLIENT.println("X"+idnumber);}
	private void sendplayerbulletdestroy(int idnumber){I_Intruders.MP_CLIENT.println("B"+idnumber);}
	
	//more methods relating to network traffic, that don't require parameters
	private void sendplayerpos(){
		//to reduce network traffic, we check if the player's position is different mefore sending the traffic
		if((int)player1.getx()!=lastx){
			lastx=(int)player1.getx();
			I_Intruders.MP_CLIENT.println("P"+(int)player1.getx());
		}
	}
	private void sendready(){I_Intruders.MP_CLIENT.println("R");}
	private void sendquit(){I_Intruders.MP_CLIENT.println("Q");}
	private void sendlives(){I_Intruders.MP_CLIENT.println("L"+I_Intruders.MP_LIVES);}
	
}
