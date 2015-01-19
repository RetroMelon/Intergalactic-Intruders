package com.Capslock.Engine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

public class sprite {
	
	
	//the array location of the image in the spritestore
	private int ref;
	//the address of the image on the file system
	private String filelocation="";
	
	//properties of the image
	private int sizex = 0;
	private int sizey = 0;
	private boolean loaded = false;
	
	public sprite(String filelocation){
		this.filelocation=filelocation;
		ref = spritestore.addsprite(this);
		if(ref>=0){loaded=true;}else{loaded=false; return;} // if the reference returned is -1 then the image wasnot loaded successfully.
		sizex = spritestore.getsizex(ref);
		sizey = spritestore.getsizey(ref);
	}
	public sprite(Image theimage){
		ref = spritestore.addsprite(theimage);
		if(ref>=0){loaded=true;}else{loaded=false; return;} // if the reference returned is -1 then the image wasnot loaded successfully.
		sizex = spritestore.getsizex(ref);
		sizey = spritestore.getsizey(ref);
	}
	
	public sprite(Image theimage, int stretchedsizex, int stretchedsizey){
		ref = spritestore.addscaledsprite(theimage, stretchedsizex, stretchedsizey);
		if(ref>=0){loaded=true;}else{loaded=false; return;} // if the reference returned is -1 then the image wasnot loaded successfully.
		sizex = spritestore.getsizex(ref);
		sizey = spritestore.getsizey(ref);
	}
	public sprite(String filelocation, int stretchedsizex, int stretchedsizey){
		this.filelocation=filelocation;
		ref = spritestore.addscaledsprite(this, stretchedsizex, stretchedsizey);
		if(ref>=0){loaded=true;}else{loaded=false; return;} // if the reference returned is -1 then the image wasnot loaded successfully.
		sizex = spritestore.getsizex(ref);
		sizey = spritestore.getsizey(ref);
	}
	
	public int getsizex(){return sizex;}
	public int getsizey(){return sizey;}
	public int getref(){return ref;}
	public boolean loaded(){return loaded;}
	public String getfilelocation(){return filelocation;}
	public Image getimage(){return spritestore.getimage(ref);}
	
	public boolean draw(Graphics2D output, int x, int y) {
		try {
			output.drawImage(getimage(), x, y, null);
			return true;
		} catch (Exception e) {}
		output.setColor(Color.red);
		output.drawRect(x, y, getsizex(), getsizey());
		return false;
	}
	public boolean draw(Graphics2D output, int x, int y, double transparency) {
		try {
			output.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)transparency));//transparency rating 1 means opaque. 0.5 means half way towards invisible.
			output.drawImage(getimage(), x, y, null);
			output.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)1));//setting the composite setting back to normal, so that we don't accidentally draw other things with a transparency.
			return true;
		} catch (Exception e) {}
		return false;
	}	

}
