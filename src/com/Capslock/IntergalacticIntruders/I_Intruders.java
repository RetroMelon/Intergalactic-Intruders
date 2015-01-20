package com.Capslock.IntergalacticIntruders;

import java.awt.Image;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.Capslock.Engine.*;

public class I_Intruders {
	
	protected static screen mainscreen;
	
	//declaring the variables needed for the screen
	protected static final int windowx = 1024;
	protected static final int windowy = 600;
	protected static final String title = "Capslock Game Studios - Intergalactic Intruders v1.0";
	
	//variables relating to the networking in multiplayer
	public static netserver MP_SERVER;
	public static netclient MP_CLIENT;
	
	//the socket used to connect in multiplayer
	public static final int MP_SOCKET = 9999;
	
	//initialising all sprites
	public static sprite musictogglesprite;
	public static sprite scaledlogo;
	public static sprite titlesprite;
	public static sprite parisbackground;
	public static sprite aliensprite;
	public static sprite blockysprite;
	public static sprite blockysprite_a;
	public static sprite zblockysprite;
	public static sprite zblockysprite_a;
	public static sprite howthehell;
	public static sprite shipsprite;
	public static sprite scaledshipsprite;
	public static sprite shipsprite_2;
	
	//initialising all sounds
	public static boolean musicplaying = false;
	public static sound helixnebula;
	public static sound playershoot;
	public static sound playerexplode;
	public static sound alienshoot;
	public static sound alienexplode;
	public static sound bosshit;
	public static sound bombshoot;
	public static sound buttonsound;
	
	//declaring global variables that will always apply to the ship. so that it is consistent throughout all game modes
	public static final int WAVE_LIMIT = 30;
	public static final int SHIP_SPEED = 400;
	public static final int SHIP_Y = windowy-50;
	public static final int SHIP_OFFSET_Y = -7; // the amount by which the image is offset from the bounds
	public static final int TRUE_SHIP_Y = SHIP_Y+SHIP_OFFSET_Y; //the y position of the tip of the cannon
	public static final int SHIP_START_X = windowx/2-25;
	public static final int ALIEN_SIZE_X = 45; // 50
	public static final int DEFAULT_ALIEN_SPEED = 110;
	public static final int SHOOT_TIME_GAP = 400;
	public static final int BULLET_BOUNDS_X = 6;
	public static final int BULLET_BOUNDS_Y = 16;
	public static final int PLAYER_BULLET_SPEED = 600;
	public static final int ALIEN_BULLET_SPEED = 250;	
	
	public static final int PARIS_BACKGROUND_Y_OFFSET = -40;
	
	public static int SHIP_PERSISTANT_X = SHIP_START_X;
	

	//creating the array of snarky comments to be put under the "wave <number>" screen
	public static String[][] statements = new String[WAVE_LIMIT+1][2];
	
	//some variables used when tracking single player progress
	public static int SP_LIVES = 3;
	public static int SP_TOTAL_DEATHS = 0;
	public static int SP_WAVES_COMPLETED = 0;
	public static long SP_TOTAL_TIME_PLAYED = 0;
	public static double SP_TOTAL_DISTANCE_TRAVELLED = 0;
	public static int SP_TOTAL_SHOTS_FIRED = 1;
	public static int SP_TOTAL_ALIENS_KILLED = 0;
	public static int SP_TOTAL_BOSS_HITS = 0;
	public static int SP_TOTAL_SCORE = 0;
	public static ArrayList<Integer> SP_SCORES = new ArrayList<Integer>();
	
	//some variables used when tracking multiplayer progress
	public static int MP_LIVES = 5;
	public static int MP_SCORE = 0;
	public static int MP_SCORE_P1 = 0;
	public static int MP_SCORE_P2 = 0;
	public static int MP_TOTAL_DEATHS_P1 = 0;
	public static int MP_TOTAL_DEATHS_P2 = 0;
	public static int MP_WAVES_COMPLETED = 0;
	public static long MP_TOTAL_TIME_PLAYED = 0;
	public static double MP_TOTAL_DISTANCE_TRAVELLED_P1 = 0; // for p1 and p2, each player appears as p1 to themselves...
	public static double MP_TOTAL_DISTANCE_TRAVELLED_P2 = 0; // to avoid comfusion as to who player 1 is, players are always referred to as "you" and "team mate"
	public static int MP_TOTAL_SHOTS_FIRED_P1 = 1;
	public static int MP_TOTAL_SHOTS_FIRED_P2 = 1;
	public static int MP_TOTAL_ALIENS_KILLED_P1 = 0;
	public static int MP_TOTAL_ALIENS_KILLED_P2 = 0;
	
	//declaring arrays for the instructions, these will be set up by a function during the loading sequence
	
	private static String[] singleplayerinstructions;
	private static String[] multiplayerinstructions;

	public static void main(String[] args) {
		//setting up the list of comments
		setupcomments();
		
		//creating the screen to be used for the game.
		mainscreen = new screen(title, windowx, windowy);
		
		helixnebula = new sound("/Resources/Sounds/Music/helixnebula.wav");
		musicplaying=true;
		helixnebula.loop();
		
		//displaying the splashscreen
		try {new splashscreen(mainscreen);}catch(Exception e){}
		
		//loading sounds
		playershoot= new sound("/Resources/Sounds/SFX/playershoot.wav");
		playerexplode= new sound("/Resources/Sounds/SFX/playerexplode.wav");
		alienshoot= new sound("/Resources/Sounds/SFX/alienshoot.wav");
		alienexplode= new sound("/Resources/Sounds/SFX/alienexplode.wav");
		bosshit= new sound("/Resources/Sounds/SFX/bosshit.wav");
		bombshoot= new sound("/Resources/Sounds/SFX/bombshoot.wav");
		buttonsound= new sound("/Resources/Sounds/SFX/buttonsound.wav");
		
		
		//loading sprites
		titlesprite = new sprite("/Resources/Images/Other/title.png");
		
		musictogglesprite = new sprite("/Resources/Images/Other/togglemusic.png");
		
		spritestore.setscalinghint(Image.SCALE_SMOOTH);
		
		scaledlogo = new sprite(spritestore.directload("/Engine/Resources/Images/Logos/CLGSwt.png"), 150, -1);
		
		parisbackground = new sprite(spritestore.directload("/Resources/Images/Background/Paris.jpg"), windowx, windowy);
		
		aliensprite = new sprite(spritestore.directload("/Resources/Images/NPC/alien.png"), ALIEN_SIZE_X, ALIEN_SIZE_X);
		blockysprite = new sprite(spritestore.directload("/Resources/Images/NPC/testboss.png"), blocky.SIZE_X, blocky.SIZE_Y);
		blockysprite_a = new sprite(spritestore.directload("/Resources/Images/NPC/testboss_a.png"), blocky.SIZE_X, blocky.SIZE_Y);
		zblockysprite = new sprite(spritestore.directload("/Resources/Images/NPC/zblocky.png"), zblocky.SIZE_X, zblocky.SIZE_Y);    
		zblockysprite_a= new sprite(spritestore.directload("/Resources/Images/NPC/zblocky_a.png"), zblocky.SIZE_X, zblocky.SIZE_Y); 
		howthehell = new sprite(spritestore.directload("/Resources/Images/NPC/howthehell.png"), hth.SIZE_X, hth.SIZE_Y); 
		
		shipsprite = new sprite("/Resources/Images/Player/Bship.png");
		shipsprite_2 = new sprite("/Resources/Images/Player/Bship2.png");
		
		scaledshipsprite = new sprite(shipsprite.getimage(), 35,  -1);
		
		spritestore.setscalinghint(Image.SCALE_DEFAULT);
		
		//setting up the instructions
		setupinstructions();
		
		//the loop that allows for different game modes
		while(true){
			//allowing the player to select what to do.
			menu mainmenu = new menu(mainscreen,"<T>", "Single-Player", "Multi-Player", "Credits/Guides");
			mainmenu.setanim(0, menu.ANIM_CENTRE);
			mainmenu.setanim(1, menu.ANIM_STAY);
			mainmenu.setanim(2, menu.ANIM_STAY);
			
			int outcome = mainmenu.run();
			
			//decides what to o based on the outcome of the menu
			if(outcome==0)startsingleplayer();
			else if(outcome==1)startmultiplayer();
			else if(outcome==2)startextrasmenu();
			else break;
		}
		
		
		//ending the game
		System.out.println("----QUITTING----");
		System.exit(0);
	}
	
	private static int startextrasmenu(){
		
		menu extrasmenu = new menu(mainscreen, "What would you like to view?", "Singleplayer Guide", "Multiplayer Guide", "Credits");
		extrasmenu.setanim(0, menu.ANIM_STAY);
		extrasmenu.setanim(1, menu.ANIM_STAY);
		extrasmenu.setanim(2, menu.ANIM_STAY);
		
		int outcome = extrasmenu.run();
		
		if(outcome==0)new instructions(mainscreen, "Singleplayer Instructions", singleplayerinstructions).run();
		else if(outcome==1)new instructions(mainscreen, "Multiplayer Instructions", multiplayerinstructions).run();
		else if(outcome==2)new credits(mainscreen).run();
		
		return 0;
	}
	
	//the method used to initialise the menu that lets the player choose to be a server or a client
	private static int startmultiplayer(){
		
		resetstatvariables();
		
		System.out.println("Game mode selected:  Multiplayer");
		
		menu multiplayermenu = new menu(mainscreen, "What would you like to act as?", "Server", "Client", "Back");
		multiplayermenu.setanim(0, menu.ANIM_STAY);
		multiplayermenu.setanim(1, menu.ANIM_NONE);
		multiplayermenu.setanim(2, menu.ANIM_STAY);
		int outcome =  multiplayermenu.run();
		
		
		if(outcome==0){
			
			startserver();
			
		}
		else if(outcome==1){
			
			startclient();

		}
		
		return outcome;
	}
	
	//does all the things related to opeating the server
	//return values should not matter... all errors are handled in the startserver method
	private static void startserver(){
		
		//resetting all of the variables relating to stats
		resetstatvariables();
		
		int returnvalue = new mplistener(mainscreen).run();
		
		if(returnvalue==1){//checking if we connected successfully
			
			System.out.println("CONNECTED SUCCESSFULLY...");
			int outcome = 1;
			
			MP_WAVES_COMPLETED = -1;
			
			while(outcome==1){//while we still have lives, start a new round
				MP_WAVES_COMPLETED++;
				outcome = new mpserver(mainscreen).run();
			}
			if(outcome==0){//the player decided to quit
				System.out.println("PLAYER HAS QUIT MULTIPLAYER GAME...");
				return;
			}
			else if(outcome==2){//we go to the scoring screen
				System.out.println("GOING TO SCORING SCREEN...");
				new mpserverscoredisplay(mainscreen).run();
				return;
			}
			else if(outcome==3){//we display a connection error message
				new errorscreen(mainscreen, "THE CONNECTION WAS INTERRUPTED...", "", "(In order to try again, a restart of the game may be necessary)...").run();
				return; 
			}
			
		}else{new errorscreen(mainscreen, "THE SERVER FAILED TO CONNECT...", "", "(In order to try again, a restart of the game may be necessary)...").run(); return; }
		
	}
	
	//does all things related to operating the client
	private static void startclient(){
		//resetting all of the variables relating to stats
		resetstatvariables();
		
		//attempting to connect
		MP_CLIENT = new netclient(JOptionPane.showInputDialog("What IP address would you like to connect to? (press okay to attempt to connect)..."), 9999);
		MP_CLIENT.connect();
		if(!MP_CLIENT.testconnection()){new errorscreen(mainscreen, "THE CLIENT FAILED TO CONNECT...", "", "(In order to try again, a restart of the game may be necessary)...").run(); MP_CLIENT.disconnect(); return;}
		
		int outcome = 1;
		
		MP_WAVES_COMPLETED = -1;
		
		while(outcome==1){//while we are still instructed to play another round
			MP_WAVES_COMPLETED++;
			outcome = (new mpclient(mainscreen)).run();
		}
		if(outcome==0){//the player decided to quit
			System.out.println("PLAYER HAS QUIT MULTIPLAYER GAME...");
			return;
		}
		else if(outcome==2){//we go to the scoring screen
			System.out.println("GOING TO SCORING SCREEN...");
			new mpclientscoredisplay(mainscreen).run();
			return;
		}
		else if(outcome==3){//we display a connection error message
			new errorscreen(mainscreen, "THE CONNECTION WAS INTERRUPTED...", "", "(In order to try again, a restart of the game may be necessary)...").run(); return; 
		}
		
	}
	
	//the method used to initialise the single player game.
	private static void startsingleplayer(){
		while(true){//(used as a means of being able to play again)
			
			resetstatvariables();
			
			int quittype = 0; // where quittype 0 = quit to main menu, quittype 1 = nextwave and quittype 2 (or anything else) = lost all lives
			//0 = quit to main menu
			//1 = move on to next wave
			//2 = lost all lives, display score screen
			do{		  //should be 10 here\/
				if(SP_WAVES_COMPLETED == 3){quittype = new spbossfight(mainscreen, new blocky(mainscreen)).run(); if(quittype!=1){break;}else{SP_LIVES++;}}
				else if(SP_WAVES_COMPLETED == 20){quittype = new spbossfight(mainscreen, new zblocky(mainscreen)).run(); if(quittype!=1){break;}else{SP_LIVES++;}}
				else if(SP_WAVES_COMPLETED == 29){quittype = new spbossfight(mainscreen, new hth(mainscreen)).run(); if(quittype!=1){break;}else{SP_LIVES++;}}
				quittype = new singleplayer(mainscreen).run();
				SP_LIVES++;
			}while(quittype == 1  && SP_WAVES_COMPLETED<WAVE_LIMIT);
			
			
			//deciding what to do based on outcome of "quit type"
			if(quittype == 0)return;
			
			//if we have reached here, then the quit type must have been 3 (or they have reached level 45), so we display the single player scores screen
			spscoredisplay tempgame = new spscoredisplay(mainscreen, new String[]{"Restart", "Quit to title screen"});
			tempgame.setanim(0, menu.ANIM_CENTRE);
			tempgame.setanim(1, menu.ANIM_STAY);
			int outcome = tempgame.run();

			//if the player selected the second button, then we break out of the while loop. otherwise they must want to play again, so we let the while loop bring us back to the start
			if(outcome == 1){return;}
		}
	}
	
	private static void resetstatvariables(){
		//resetting singpleplayer variables
		SP_LIVES = 3;
		SP_TOTAL_DEATHS = 0;
		SP_WAVES_COMPLETED = 0;
		SP_TOTAL_TIME_PLAYED = 0;
		SP_TOTAL_DISTANCE_TRAVELLED = 0;
		SP_TOTAL_SHOTS_FIRED = 1;
		SP_TOTAL_ALIENS_KILLED = 0;
		SP_TOTAL_SCORE = 0;
		SP_SCORES.clear();
		
		//resetting multiplayer variables
		 MP_LIVES = 5;
		 MP_SCORE = 0;   
		 MP_SCORE_P1 = 0;
		 MP_SCORE_P2 = 0;
		 MP_WAVES_COMPLETED = 0;
		 MP_TOTAL_TIME_PLAYED = 0;
		 MP_TOTAL_DEATHS_P1 = 0;
		 MP_TOTAL_DEATHS_P2 = 0;
		 MP_TOTAL_DISTANCE_TRAVELLED_P1 = 0; // for p1 and p2, each player appears as p1 to themselves...
		 MP_TOTAL_DISTANCE_TRAVELLED_P2 = 0; // to avoid confusion as to who player 1 is, players are always referred to as "you" and "team mate"
		 MP_TOTAL_SHOTS_FIRED_P1 = 1;
		 MP_TOTAL_SHOTS_FIRED_P2 = 1;
		 MP_TOTAL_ALIENS_KILLED_P1 = 0;
		 MP_TOTAL_ALIENS_KILLED_P2 = 0;
	}	
	
	private static void setupcomments(){
		for(int i = 0; i<statements.length; i++){
			statements[i][0]="";
			statements[i][1]="";
		}
		
		try{
			statements[1][0] = "Okay, you're probably a noob... So, let's start this off nice and easy...";
			statements[2][0] = "CONGRATULATIONS!... You passed the first round of the easiest game ever!...";
			statements[2][1] =  "*sarcastic clap*";
			statements[3][0] = "Well, it's still not exactly impressive now, is it?";
			statements[4][0] = "Okay, now we're BEGINNING to move in the right direction...";
			statements[5][0] = "You could compare your achievements so far to, i don't know... ";
			statements[5][1] = "learning to tie your shoelaces?";
			statements[6][0] = "So, from now on i might not make a sarcastic comment after EVERY wave...";
			statements[6][1] = "(HINT: Next one is at wave 8)";
			statements[8][0] = "Oh hi, did you miss me?... So anyways,";
			statements[8][1] = "we're stepping the difficulty up a little from here onwards...";
			statements[10][0] = "Okay, you could stop any time now...";
			statements[12][0] = "Your time really means this little to you?";
			statements[14][0] = "Okay, this isn't funny any more... Go away, and get on with your life...";
			statements[15][0] = "OMGZ, U MAD BRO?...";
			statements[17][0] = "over 9000 waves, here we come! (*WARNING*, WAVE LIMIT = "+WAVE_LIMIT+")";
			statements[19][0] = "Chuck Norris fears you...";
			statements[20][0] = "Okay, i'm not talking to you any more...";
			statements[21][0] = "Oh, you thought i was joking when i said i wasn't talking to you?...";
			statements[21][1] = "Well, i wasn't!";
			statements[22][0] = "*Silence*...";
			statements[23][0] = "I used to be good at these sarcastic statements.. Then i took wave 20 to the knee...";
			statements[25][0] = "Okay, i didn't really make up many more comments... ";
			statements[25][1] = "Who gets this far in SPACE INVADERS!?...";
			statements[26][0] = "OMG, SRSLY?...";
			statements[28][0] = "Stop it, you're scaring me...";
			statements[30][0] = "Okay, there's only one explanation for this......... HAAAAXXXXX!";
			
			
		}catch(Exception e){}
	}
	
	private static void setupinstructions(){
		
		//setting up singleplayer instructions
		singleplayerinstructions = new String[]{
						
				"CONTROLS",
				"",
				"Arrow Keys  -    Move ",
				"Spacebar    -    Fire ",
				"Escape      -    Pause",
				"",
				"",
				"PAUSING",
				"",
				"The game can be paused at any time by pressing the ESCAPE key. On pressing",
				"the ESCAPE key the game will be suspended and the player will be given the",
				"option to exit to the main menu.                                          ",
				"",
				"",
				"SCORING",
				"",
				"The score in singleplayer is determined based on the following rules:",
				"50 points are gained for every alien killed. (50 PER ALIEN)          ",
				"Points are awarded on completion of a round. (100*WAVE_NUMBER)       ",
				"Points are awarded on killing a boss.        (2000*BOSSNUMBER)       ",
				"",
				"",
				"LIVES",
				"",
				"The player is granted 3 lives at the start of a singleplayer game. One life ",
				"is lost when the player is hit by an alien bullet. On completing a wave the ",
				"player is granted 1 extra life, and on defeating a boss 2 lives are granted.",
				"",
				"",
				"VICTORY/DEFEAT",
				"",
				"The game can be beaten by reaching wave 30 and defeating all 3 bosses along the",
				"way. The game is also lost if the player's lives reach 0. On either victory or ",
				"defeat, the player will be taken to a stat screen where they will be told their",
				"final score along with statistics relating to their gameplay.                  ",
				"",
				"",
				"THE MOST IMPORTANT INSTRUCTION OF ALL",
				"",
				"Have fun! :D"
		};
		
				
		multiplayerinstructions = new String[]{
				
				"CONTROLS",
				"",
				"Arrow Keys  -    Move ",
				"Spacebar    -    Fire ",
				"Escape      -    Pause",
				"",
				"",
				"PAUSING",
				"",
				"The game can be paused at any time by pressing the ESCAPE key. In multiplayer   ",
				"mode the ESCAPE key will not suspend the game as it did in singleplayer. Instead",
				"the pausing functionality should merely be used as a means to quit the game.    ",
				"",
				"",
				"SCORING",
				"",
				"The score in multiplayer is determined based on the following rules: ",
				"50 points are gained for every alien killed. (50 PER ALIEN)          ",
				"Points are awarded on completion of a round. (100*WAVE_NUMBER)       ",
				"",
				"",
				"LIVES",
				"",
				"The players are granted five lives at the start of a singleplayer game. One life",
				"is lost when the player is hit by an alien bullet. On completing a wave, players",
				"are granted 1 life.                                                             ",
				"",
				"",
				"VICTORY/DEFEAT",
				"",
				"In the multiplayer game mode the game cannot be beaten. Instead the objective is  ",
				"to survive for as long as possible, reaching the highest wave number and achieving",
				"the highest score possible. On defeat, the players arrive at a statistics screen. ",
				"Here the players are told their final score as in singleplayer.                   ",
				"",
				"",
				"ACTING AS A SERVER",
				"",
				"If you wish to act as a server for another player to connect to, you must ensure ",
				"that your internet connection is such that it allows for TCP traffic between your",
				"computer and the internet. To allow for this to happen, you may be required to   ",
				"portforward your router. Guides on how to do this are freely available online.   ",
				"Also note that the port that this game uses is port 9999.                        ",
				"",
				"",
				
				"NOTE",
				"",
				"For a reason that eludes me, i am unable to reset the 'sockets' which are used to  ",
				"connect players if they have been activated at any point. This means that if you   ",
				"attempt to host/connect to a game, and it fails OR you have finished a multiplayer ",
				"game, the entire game may have to be restarted. You can restart the game by closing",
				"the window, and starting the game again.     (SORRY)                               "
		};
	
	}

}
