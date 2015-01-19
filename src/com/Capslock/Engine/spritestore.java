package com.Capslock.Engine;

import java.awt.Image;

import java.net.URL;
import java.util.ArrayList;
import javax.swing.ImageIcon;


public class spritestore{
	
	//the arraylist where all of the images are stored
	private static ArrayList<Image> imagearray = new ArrayList<Image>();
	
	//the hint to use when scaling
	private static int scalinghint = Image.SCALE_DEFAULT;
	
	public static void setscalinghint(int newscalinghint){scalinghint = newscalinghint;}

	//used by a sprite class to load it's image in to the spritestore.
	//this method returns the reference number. if the reference is -1 then the image failed to load.
	public static int addsprite(sprite s){
		int ref=-1;
		String filelocation=s.getfilelocation();
		URL locationurl = spritestore.class.getResource(filelocation);
		System.out.println("-----");
		System.out.println("SPRITESTORE ATTEMPTING TO LOAD IMAGE AT LOCATION:  "+locationurl);
		Image tempimage;
		//attempting to load the image, and put it in the imagearray.
		try{
			tempimage=new ImageIcon(locationurl).getImage();
			//adding the image to the array
			ref=add(tempimage);
			if(ref==-1){throw new Exception("FAILED TO ALLOC. IMAGE TO ARRAY");}
			//if exception has not been thrown before here, we have succeeded!
			System.out.println("SPRITESTORE STORED IMAGE SUCCESSFULLY AT REF:  "+ref);
		}catch(Exception e){System.out.println("SPRITESTORE FAILED TO LOAD IMAGE:  "+e.getMessage()); ref=-1;}
		System.out.println("-----");
		return ref;
	}
	public static int addsprite(String filelocation){
		int ref=-1;
		URL locationurl = spritestore.class.getResource(filelocation);
		System.out.println("-----");
		System.out.println("SPRITESTORE ATTEMPTING TO LOAD IMAGE AT LOCATION:  "+locationurl);
		Image tempimage;
		//attempting to load the image, and put it in the imagearray.
		try{
			//loading image
			tempimage=new ImageIcon(locationurl).getImage();
			//adding the image to the array
			ref=add(tempimage);
			if(ref==-1){throw new Exception("FAILED TO ALLOC. IMAGE TO ARRAY");}
			//if exception has not been thrown before here, we have succeeded!
			System.out.println("SPRITESTORE STORED IMAGE SUCCESSFULLY AT REF:  "+ref);
		}catch(Exception e){System.out.println("SPRITESTORE FAILED TO LOAD IMAGE:  "+e.getMessage()); ref=-1;}
		System.out.println("-----");
		return ref;
	}
	public static int addsprite(Image theimage){
		int ref=-1;
		System.out.println("-----");
		System.out.println("SPRITESTORE ATTEMPTING TO ADD PRELOADED IMAGE");
		//attempting to load the image, and put it in the imagearray.
		if(theimage==null){System.out.println("SPRITESTORE FAILED TO ADD PRELOADED IMAGE"); return -1;}
		try{
			//adding the image to the array
			ref=add(theimage);
			if(ref==-1){throw new Exception("FAILED TO ALLOC. IMAGE TO ARRAY");}
			//if exception has not been thrown before here, we have succeeded!
			System.out.println("SPRITESTORE ADDED PRELOADED IMAGE AT REF:  "+ref);
		}catch(Exception e){System.out.println("SPRITESTORE FAILED TO ADD PRELOADED IMAGE"); ref=-1;}
		System.out.println("-----");
		return ref;
	}
	
	//adding scaled sprites
	public static int addscaledsprite(sprite s, int scaledsizex, int scaledsizey){
		int ref=-1;
		String filelocation=s.getfilelocation();
		URL locationurl = spritestore.class.getResource(filelocation);
		System.out.println("-----");
		System.out.println("SPRITESTORE ATTEMPTING TO SCALE AND LOAD IMAGE AT LOCATION:  "+locationurl);
		Image tempimage;
		//attempting to load the image, and put it in the imagearray.
		try{
			//loading and scaling image
			tempimage=new ImageIcon(locationurl).getImage();
			tempimage=tempimage.getScaledInstance(scaledsizex, scaledsizey, scalinghint);
			//adding the image to the array
			ref=add(tempimage);
			if(ref==-1){throw new Exception("FAILED TO ALLOC. IMAGE TO ARRAY");}
			//if exception has not been thrown before here, we have succeeded!
			System.out.println("SPRITESTORE SCALED AND STORED IMAGE SUCCESSFULLY AT REF:  "+ref);
		}catch(Exception e){System.out.println("SPRITESTORE FAILED TO SCALE AND/OR LOAD IMAGE:  "+e.getMessage()); ref=-1;}
		System.out.println("-----");
		return ref;
	}
	public static int addscaledsprite(String filelocation, int scaledsizex, int scaledsizey){
		int ref=-1;
		URL locationurl = spritestore.class.getResource(filelocation);
		System.out.println("-----");
		System.out.println("SPRITESTORE ATTEMPTING TO LOAD AND SCALE IMAGE AT LOCATION:  "+locationurl);
		Image tempimage;
		//attempting to load the image, and put it in the imagearray.
		try{
			//loading and scaling image
			tempimage=new ImageIcon(locationurl).getImage();
			tempimage=tempimage.getScaledInstance(scaledsizex, scaledsizey, scalinghint);
			//adding the image to the array
			ref=add(tempimage);
			if(ref==-1){throw new Exception("FAILED TO ALLOC. IMAGE TO ARRAY");}
			//if exception has not been thrown before here, we have succeeded!
			System.out.println("SPRITESTORE SCALED AND STORED IMAGE SUCCESSFULLY AT REF:  "+ref);
		}catch(Exception e){System.out.println("SPRITESTORE FAILED TO SCALE AND/OR LOAD IMAGE:  "+e.getMessage()); ref=-1;}
		System.out.println("-----");		
		return ref;
	}
	public static int addscaledsprite(Image theimage, int scaledsizex, int scaledsizey){
		int ref=-1;
		System.out.println("SPRITESTORE ATTEMPTING TO SCALE AND ADD PRELOADED IMAGE");
		//attempting to load the image, and put it in the imagearray.
		if(theimage==null){System.out.println("SPRITESTORE FAILED TO ADD PRELOADED IMAGE"); return -1;}
		try{
			//scaling the preloaded image
			theimage=theimage.getScaledInstance(scaledsizex, scaledsizey, scalinghint);
			//adding the image to the array
			ref=add(theimage);
			if(ref==-1){throw new Exception("FAILED TO ALLOC. IMAGE TO ARRAY");}
			//if exception has not been thrown before here, we have succeeded!
			System.out.println("SPRITESTORE SCALED AND ADDED PRELOADED IMAGE AT REF:  "+ref);
		}catch(Exception e){System.out.println("SPRITESTORE FAILED TO SCALE AND/OR ADD PRELOADED IMAGE"); ref=-1;}
		System.out.println("-----");
		
		return ref;
	}
	
	//returns the actual image content of a sprite
	public static Image getimage(int ref) {
		try{return imagearray.get(ref);}catch(Exception e){return null;}
	}
	
	//directly loads an image and returns it. does not add it to the store
	public static Image directload(String filelocation){
		URL locationurl = spritestore.class.getResource(filelocation);
		System.out.println("SPRITESTORE ATTEMPTING TO DIRECT-LOAD IMAGE AT LOCATION:  "+locationurl);
		Image tempimage;
		//attempting to load the image.
		try{tempimage=new ImageIcon(locationurl).getImage();
			System.out.println("SPRITESTORE STORED IMAGE SUCCESSFULLY AT REF:  "+locationurl);
		}catch(Exception e){System.out.println("SPRITESTORE FAILED TO DIRECT-LOAD IMAGE:  "+e.getMessage()); return null;}
		
		return tempimage;
	}
	
	//used by the sprites to find out their sizes when they are first created
	public static int getsizex(int ref){return imagearray.get(ref).getWidth(null);}
	public static int getsizey(int ref){return imagearray.get(ref).getHeight(null);}
	public static int getsizex(sprite s){return imagearray.get(s.getref()).getWidth(null);}
	public static int getsizey(sprite s){return imagearray.get(s.getref()).getHeight(null);}
	public static int getotalimages(){return imagearray.size();}
	
	/*
	*the method used to attempt to add the sprite to the spritestore.
	*this will return the reference in the array of the image.
	*if the image exists in the array, it will return the reference to the array.
	*otherwise it will add it and return the position of the image that it added.
	*if it fails, it will return -1.
	*
	*NOTE: DOES NOT WORK WITH SCALED SPRITES. (possibly to do with being created on demand.)
	*/
	private static int add(Image i){
		int tempref = imagearray.indexOf(i);
		if(tempref!=-1){System.out.println("ALREADY AN INSTANCE AT REF:  "+tempref); return tempref;}
		try{
			imagearray.add(i);
			return imagearray.size()-1;
		}catch(Exception e){return -1;}
	}

}

