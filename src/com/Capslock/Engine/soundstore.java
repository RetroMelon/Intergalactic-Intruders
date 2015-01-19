package com.Capslock.Engine;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;
import java.util.ArrayList;

public class soundstore {
	
	//the arraylist that holds all of the audioclips
	private static ArrayList<AudioClip> audioarray = new ArrayList<AudioClip>();
	
	//used by a sound class to load it's sound in to the soundstore
	//this method returns the reference number. if the reference is -1, then the sound failed to load
	public static int addsound(sound s){
		int ref=-1;
		URL locationurl = soundstore.class.getResource(s.getfilelocation());
		System.out.println("-----");
		System.out.println("SOUNDSTORE ATTEMPTING TO LOAD SOUND AT LOCATION:  "+ locationurl);
		try{
			AudioClip tempsound = Applet.newAudioClip(locationurl);
			//adding the sound to the array
			ref=add(tempsound);
			if(ref==-1){throw new Exception("FAILED TO ALLOC. SOUND TO ARRAY");}
			//if exception has not been thrown before here, we have succeeded!
			System.out.println("SOUNDSTORE SUCCESSFULLY ADDED SOUND AT REF:  "+ref);
		}catch(Exception e){System.out.println("SOUNDSTORE FAILED TO LOAD SOUND:  "+e.getMessage()); ref=-1;}
		System.out.println("-----");
		return ref;
	}
	public static int addsound(String filelocation){
		int ref=-1;
		URL locationurl = soundstore.class.getResource(filelocation);
		System.out.println("-----");
		System.out.println("SOUNDSTORE ATTEMPTING TO LOAD SOUND AT LOCATION:  "+ locationurl);
		try{
			AudioClip tempsound = Applet.newAudioClip(locationurl);
			//adding the sound to the array
			ref=add(tempsound);
			if(ref==-1){throw new Exception("FAILED TO ALLOC. SOUND TO ARRAY");}
			//if exception has not been thrown before here, we have succeeded!
			System.out.println("SOUNDSTORE SUCCESSFULLY ADDED SOUND AT REF:  "+ref);
		}catch(Exception e){System.out.println("SOUNDSTORE FAILED TO LOAD SOUND:  "+e.getMessage()); ref=-1;}
		System.out.println("-----");
		return ref;
	}
	public static int addsound(AudioClip soundclip){
		int ref=-1;
		System.out.println("-----");
		System.out.println("SOUNDSTORE ATTEMPTING TO ADD A PRELOADED SOUND");
		if(soundclip==null){System.out.println("SOUNDSTORE FAILED TO ADD PRELOADED SOUND"); System.out.println("-----"); return -1;}
		try{
			//adding the sound to the array
			ref=add(soundclip);
			if(ref==-1){throw new Exception("FAILED TO ALLOC. SOUND TO ARRAY");}
			//if exception has not been thrown before here, we have succeeded!
			System.out.println("SOUNDSTORE SUCCESSFULLY ADDED PRELOADED SOUND AT REF:  "+ref);
		}catch(Exception e){System.out.println("SOUNDSTORE FAILED TO ADD PRELOADED SOUND"); ref=-1;}
		System.out.println("-----");
		return ref;
	}
	
	//returns the actual image content of a sprite
	public static AudioClip getaudio(int ref) {
		try{return audioarray.get(ref);}catch(Exception e){return null;}
	}
	
	//directly loads an audioclip and returns it. does not add it to the store
		public static AudioClip directload(String filelocation){
			URL locationurl = soundstore.class.getResource(filelocation);
			System.out.println("SOUNDSTORE ATTEMPTING TO DIRECT-LOAD SOUND AT LOCATION:  "+locationurl);
			AudioClip tempclip;
			//attempting to load the image.
			try{tempclip= Applet.newAudioClip(locationurl);
				System.out.println("SOUNDSTORE STORED SOUND SUCCESSFULLY:  "+locationurl);
			}catch(Exception e){System.out.println("SOUNDSTORE FAILED TO DIRECT-LOAD SOUND:  "+e.getMessage()); return null;}
			
			return tempclip;
		}
	
	//all get methods
	public static int getotalclips(){return audioarray.size();}
	
	/*
	*the method used to attempt to add the audioclip to the soundstore.
	*this will return the reference in the array of the sound.
	*if the image exists in the array, it will return the reference to the array.
	*otherwise it will add it and return the position of the sound that it added.
	*if it fails, it will return -1.
	*
	*NOTE: DOES NOT ACTUALLY WORK.
	*(but is still useful as a way to tidy up other methods)
	*/
	private static int add(AudioClip i){
		//int tempref = audioarray.indexOf(i);
		//if(tempref!=-1){System.out.println("ALREADY AN INSTANCE AT REF:  "+tempref); return tempref;}
		try{
			audioarray.add(i);
			return audioarray.size()-1;
		}catch(Exception e){return -1;}
	}
	
}
