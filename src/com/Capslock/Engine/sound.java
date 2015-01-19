package com.Capslock.Engine;

import java.applet.AudioClip;


public class sound {
	
	//the array location in the sound store
	private int ref;
	//the address of the sound file on the file system
	String filelocation = "";
	
	//properties of the sound
	private boolean loaded = false;
	

	public sound(String filelocation){
		this.filelocation=filelocation;
		ref = soundstore.addsound(this);
		if(ref>=0){loaded=true;}else{loaded=false; return;} // if the reference returned is -1 then the sound wa snot loaded successfully.
	}
	public sound(AudioClip thesound){
		ref = soundstore.addsound(thesound);
		if(ref>=0){loaded=true;}else{loaded=false; return;} // if the reference returned is -1 then the sound was not loaded successfully.
	}
	
	//the methods used to play, atop and loop the sound
	public void play(){
		if(loaded){try{soundstore.getaudio(ref).play();}catch(Exception e){System.out.println("SOUND AT SOUNDSTORE REF '"+ref+"' FAILED TO PLAY");}}else{System.out.println("SOUND AT SOUNDSTORE REF '"+ref+"' FAILED TO PLAY - LOADED UNSUCCESSFULLY");}
	}
	public void stop(){
		if(loaded){try{soundstore.getaudio(ref).stop();}catch(Exception e){System.out.println("SOUND AT SOUNDSTORE REF '"+ref+"' FAILED TO STOP");}}else{System.out.println("SOUND AT SOUNDSTORE REF '"+ref+"' FAILED TO STOP - LOADED UNSUCCESSFULLY");}
	}
	public void loop(){
		if(loaded){try{soundstore.getaudio(ref).loop();}catch(Exception e){System.out.println("SOUND AT SOUNDSTORE REF '"+ref+"' FAILED TO LOOP");}}else{System.out.println("SOUND AT SOUNDSTORE REF '"+ref+"' FAILED TO LOOP - LOADED UNSUCCESSFULLY");}
	}
	
	public int getref(){return ref;}
	public String getfilelocation(){return filelocation;}
	public boolean loaded(){return loaded;}
	
}
