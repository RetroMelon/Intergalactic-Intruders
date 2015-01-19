package com.Capslock.Engine;

import java.awt.Color;
import java.awt.Image;
import javax.swing.ImageIcon;


public class splashscreen {
	
	//this is the scale of the image - at 1 the image is bound to the edges of the screen,
	// below one and the image is made slightly smaller than the edges.
	//will probably use 0.8 for game
	private double scale=0.8;
	private boolean inverted = false;
	
	public splashscreen(screen mainscreen) throws Exception{
		//just printing the capslock name and disclaimer in to the console
		System.out.println("CAPSLOCK Game Studios.");
		System.out.println("NOTE: GAME ENGINE STILL IN DEVELOPMENT.");
		System.out.println("Name, Logo and following content created by Joe Frew and Ryan Steel. Game Engine by Joe Frew.");
		//declaring the image variable
		Image splashimage;
		
		//deciding which image to use
		String imagefilename="CLGS.jpg";
		//if this statement is true, then it is widescreen.
		if(mainscreen.getaspectratio()<=(double)16/(double)7 && mainscreen.getaspectratio()>=(double)16/(double)10){
			if(inverted){imagefilename="CLGSwi.jpg";}
			else{imagefilename="CLGSw.jpg";}
		}else{
			if(inverted){imagefilename="CLGSi.jpg";}
			else{imagefilename="CLGS.jpg";}
		}
		
		//getting the image resource
		//Logos/"+imagefilename
		try{splashimage = new ImageIcon(getClass().getResource("/Engine/Resources/Images/Logos/"+imagefilename)).getImage();}catch(Exception e){System.out.println("Failed to Load SplashImage Error:  "+e.getMessage()); return;}
		
		//scaling the image, taking into account the screen size, and the scalefactor. also takes uses smooth scaling algorithm, because we want our splashscreen to look good!
		int sizex = (int) Math.ceil(mainscreen.getsizex()*scale);
		int sizey = (int)Math.ceil(mainscreen.getsizey()*scale);
		// one of the dimensions will be -1, as in this function, -1 can be used to tell the function to scale to the same ratio as the other side
		if((double)sizex/splashimage.getWidth(null)>=(double)sizey/splashimage.getHeight(null)){sizex=-1;} //here, if x is bigger than y, then we scale so that the image fits within y. and like wise, if y is larger than x
		else if((double)sizex/splashimage.getWidth(null)<=(double)sizey/splashimage.getHeight(null)){sizey=-1;}
		try{splashimage = splashimage.getScaledInstance(sizex, sizey, Image.SCALE_SMOOTH);}catch(Exception e){System.out.println("Failed to Scale SplashImage Error:   "+e.getMessage()); return;}
		
		//setting main color to the required one, just in case the image won't take up the full screen.
		Color tempcol=Color.black;
		if(inverted){tempcol=Color.white;}
		mainscreen.setbackgroundcolor(tempcol);
		
		//because the scaling method waits until the image is needed, it doesn't always scale it in time, so we keep trying to draw it, until it is fully loaded, and we succeed
		boolean drawsuccess=false;
		mainscreen.flush();
		while(!drawsuccess){
			try{drawsuccess = mainscreen.screengraphics.drawImage(splashimage, (int)Math.ceil(mainscreen.getsizex()/2-splashimage.getWidth(null)/2), (int)Math.floor(mainscreen.getsizey()/2-splashimage.getHeight(null)/2), null);}catch(Exception e){System.out.println("Failed to Draw SplashImage Error:   "+e.getMessage());}
			Thread.sleep(5);
		}
		
		//drawing some extra text o, just because the game's not complete
		mainscreen.screengraphics.setColor(Color.RED);
		mainscreen.screengraphics.setColor(inverted ? Color.BLACK : Color.white);
		mainscreen.screengraphics.drawString("GAME ENGINE, STILL IN DEVELOPMENT.", 5, 12);
		mainscreen.screengraphics.setColor(Color.DARK_GRAY);
		mainscreen.screengraphics.drawString("Name, Logo and following content created by Joe Frew and Ryan Steel. Game Engine by Joe Frew.", 5, mainscreen.getsizey()-8);
		mainscreen.update();
		
		//give people some time to admire our logo
		try{Thread.sleep(2000);}catch(Exception e){}
	}

}
