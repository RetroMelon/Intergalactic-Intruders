package com.Capslock.Engine;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;


public abstract class entity {
	
	//the screen the entity will draw to.
	protected screen mainscreen;
	
	//physical properties.
	protected double x = 0;
	protected double y = 0;
	protected double dirx = 0;
	protected double diry = 0;
	//max dirs are the maximum directional speeds of the object excluding the frictional force.
	//if the maxdirs are 0, then there are no maximum dirs
	protected double maxdirx = 0;
	protected double maxdiry = 0;
	//weight affects the acceleration and the frictional forces applied to the object
	protected double weight = 1;
	//friction is the number that will be subtracted from the direction every second.
	protected double frictionx = 0;
	protected double frictiony = 0;
	protected double accelx = 0;
	protected double accely = 0;
	//bounds can be used if we want a custom size rather than that of the sprite.
	//boundsx and boundsy are merely sizes, and the boundsoffsets is in case the bounding box does not start at 0,0 relative to the actual x and y.
	protected double boundsx = 50;
	protected double boundsy = 50;
	//spriteoffsets can be used to tweak where the sprite appears in relation to the object without actually changing the image.
	protected double spriteoffsetx = 0;
	protected double spriteoffsety = 0;
	
	//collision modes are a means of choosing how you want the entity's collision box to act.
	//collision mode 1 means that the collision box is the size of the sprite used for the entity. it ignores any sprite offsets
	//collision mode 2 means that the collision box ends with the lower right edge of the sprite. this means that the sprite offset is taken in to account
	//collision mode 3 is where the collision box is defined by the boundsx and boundsy variables.
	protected boolean collideable = true;
	protected int collisionmode = 1;
	public final static int COLLISIONMODE_SPRITEBOUNDS = 1;
	public final static int COLLISIONMODE_OFFSETSPRITEBOUNDS = 2;
	public final static int COLLISIONMODE_CUSTOMBOUNDS = 3;
	
	//other variables.
	protected int lastframerate = 100;
	protected boolean debugbounds = false; // used to draw the collision bounds on to the object- these are the boundsx and boundsy, not the collision area of a sprite if in use.
	
	//variables related to drawing
	protected boolean drawable = true;
	protected int currentspritenumber = -1; //the index on the images array that should be used when checking bounds
	protected Color drawcolor = Color.yellow;
	//drawmodes are a way of choosing how you want the sprite to be drawn.
	//drawmode 1 is to draw the sprite normally.... do not clip to the bounds of the object, and do not stretch
	//drawmode 2 is to clip the sprite to within the bounds of the object
	//drawmode 3 is to stretch the sprite to fit the bounds of the object.
	protected int drawmode = 1;
	public final static int DRAWMODE_STANDARD = 1;
	public final static int DRAWMODE_CLIP = 2;
	public final static int DRAWMODE_STRETCH = 3;
	
	//files used by the entity.
	protected ArrayList<sprite> image= new ArrayList<sprite>();
	protected ArrayList<sound> audio= new ArrayList<sound>();
	
	//all different variations of the constructor.
	public entity(screen mainscreen){
		this.mainscreen=mainscreen;
		setup();
		
	}
	public entity(screen mainscreen, boolean drawable){
		this.drawable=drawable;
		this.mainscreen=mainscreen;
		setup();
		
	}
	
	public entity(screen mainscreen, sprite newsprite){
		addsprite(newsprite);
		this.mainscreen=mainscreen;
		setup();
		
	}
	public entity(screen mainscreen, double x, double y){
		this.x=x;
		this.y=y;
		this.mainscreen=mainscreen;
		setup();
	}
	
	//a method overridden by any class extending this, which is generally used to include sprites and sounds.
	//it is also used to select whether to use custom bounds or the size of the sprite.
	//by default we use the bounds of the sprite
	public abstract void setup(); 
	
	//the move method adds a percentage of (based on framerate) the current accels (if there are any) to the dirs, then the dirs to the x and y coords.
	public void move(int framerate){
		lastframerate=framerate;
		//applying acceleration
		dirx=dirx+((accelx/weight)/framerate);
		diry=diry+((accely/weight)/framerate);
		//applying friction for x axis
		//when applying a friction, we only do it if the accel'n is 0. this prevents the case where the object is unable to accelerate due to being stopped by friction.
		double frictionresult;
		if(accelx==0){
			frictionresult=frictionx/framerate;
			if(dirx<-frictionresult){
				dirx+=frictionresult;
			}else if(dirx>frictionresult){
				dirx-=frictionresult;
			}else{dirx=0;}
		}
		//applying friction for y axis
		if(accely==0){
			frictionresult=frictiony/framerate;
			if(diry<-frictionresult){
				diry+=frictionresult;
			}else if(diry>frictionresult){
				diry-=frictionresult;
			}else{diry=0;}
		}
		//making sure the speeds do not exceed the speed cap
		if(maxdirx!=0){if(dirx>maxdirx){dirx=maxdirx;}else if(dirx<-maxdirx){dirx=-maxdirx;}}
		if(maxdiry!=0){if(diry>maxdiry){diry=maxdiry;}else if(diry<-maxdiry){diry=-maxdiry;}}
		//applying the speed to the position of the entity
		x=x+dirx/framerate;
		y=y+diry/framerate;
	}
	
	//handles all of the collision checking.
	//would generally use "instanceof" to check which type of object it is colliding with.
	//it would then resolve the collision for the 2 entities.
	//this should almost never be carried out on an entity which is not drawable, because it does not have a sprite from which to get dimensions from.
	//(an optional bounding box may be implemented later.)
	//(it can also be used to check if the entity encompasses an x and y coordinate.)
	public boolean checkcols(entity e){
		if(!collideable || !e.getcollideable()){return false;}
		double thisstartx=0;
		double thisstarty=0;
		double thisendx=0;
		double thisendy=0;
		double otherstartx=0;
		double otherstarty=0;
		double otherendx=0;
		double otherendy=0;
		
		//finding out the bounds to use for this entity
		
			thisstartx=x;
			thisstarty=y;
			thisendx=x+getsizex();
			thisendy=y+getsizey();
		
		//finding out the bounds to use of the other entity
			otherstartx=e.getx();
			otherstarty=e.gety();
			otherendx=otherstartx+e.getsizex();
			otherendy=otherstarty+e.getsizey();
		
		//checking if the two intersect on the X axis
		if(thisstartx > (otherendx)) return false; // box1 is too far right, no collision
		   else if((thisendx) < otherstartx) return false; // box1 is too far left, no collision
		   else if(thisstarty > (otherendy)) return false; // box1 is too far down, no collision
		   else if((thisendy) < otherstarty) return false; // box1 is too far up, no collision
		   else return true; // there is a collision
	}
	
	public boolean checkcols(double posx, double posy){
		if(!collideable)return false;
		//we do not need to check whether to use bounds or to use a sprite because that is decided when we call the getsizex and getsizey functions.
			if(x<=posx && x+getsizex()>=posx && y<=posy && y+getsizey()>=posy){return true;}

		return false;
	}
	
	public boolean checkxaxiscols(double posx){
		if(!collideable)return false;
		//we do not need to check whether to use bounds or to use a sprite because that is decided when we call the getsizex and getsizey functions.
			if(x<=posx && x+getsizex()>=posx){return true;}

		return false;
	}
	
	public boolean checkyaxiscols(double posy){
		if(!collideable)return false;
		//we do not need to check whether to use bounds or to use a sprite because that is decided when we call the getsizex and getsizey functions.
			if(y<=posy && y+getsizey()>=posy){return true;}

		return false;
	}
	
	//apply angled force requires an angle and a magnitude.
	public void applyangledforce(double magnitude, double angle){
		
		//some fancy trig will go here to do this, right now this method does nothing.
		
		System.out.println("apply angled force not yet available");
	}
	
	//all get methods.
	public double getx(){return x;}
	public double gety(){return y;}
	public double getdirx(){return dirx;}
	public double getdiry(){return diry;}
	public double getmaxdirx(){return maxdirx;}
	public double getmaxdiry(){return maxdiry;}
	public double getfrictionx(){return frictionx;}
	public double getfrictiony(){return frictiony;}
	public double getaccelx(){return accelx;}
	public double getaccely(){return accely;}
	public double getboundsx(){return boundsx;}
	public double getboundsy(){return boundsy;}
	public double getspriteoffsetx(){return spriteoffsetx;}
	public double getspriteoffsety(){return spriteoffsety;}
	public boolean getdrawable(){return drawable;}
	public int getcollisionmode(){return collisionmode;}
	public boolean getcollideable() {return collideable;}
	public boolean getdebugbounds(){return debugbounds;}
	public int getcurrentspritenumber(){return currentspritenumber;}
	public sprite getcurrentsprite(){try{return image.get(currentspritenumber);}catch(Exception e){return null;}}//use with caution!. if the currentsprite is set to -1 or doesn't exist we'll get an error
	//the getsize functions take in to account whether the bounds, sprite size, or spritesize+spriteoffset should be used to determine the collision area of the entity
	public double getsizex(){if(currentspriteexists() && collisionmode!=3){return image.get(currentspritenumber).getsizex()+(collisionmode==2 ? spriteoffsetx : 0);}else{return boundsx;}}
	public double getsizey(){if(currentspriteexists() && collisionmode!=3){return image.get(currentspritenumber).getsizey()+(collisionmode==2 ? spriteoffsety : 0);}else{return boundsy;}}
	public boolean currentspriteexists(){return (image.size()-1>=currentspritenumber)&&(currentspritenumber!=-1);}
	
	//all set methods.
	public void setx(double newval){x=newval;}
	public void sety(double newval){y=newval;}
	public void setdirx(double newval){dirx=newval;}
	public void setdiry(double newval){diry=newval;}
	public void setmaxdirx(double newval){maxdirx=newval;}
	public void setmaxdiry(double newval){maxdiry=newval;}
	public void setfrictionx(double newval){frictionx=newval;}
	public void setfrictiony(double newval){frictiony=newval;}
	public void setaccelx(double newval){accelx=newval;}
	public void setaccely(double newval){accely=newval;}
	public void setboundsx(int newval){boundsx=newval;}
	public void setboundsy(int newval){boundsy=newval;}
	public void setscreen(screen newscreen){mainscreen=newscreen;}
	public void setdrawable(boolean drawable){this.drawable=drawable;}
	public void setcollisionmode(int newcollisionmode){collisionmode=newcollisionmode;}
	public void setcurrentsprite(int currentspritenumber){this.currentspritenumber=currentspritenumber;}
	public void setdrawcolor(Color newval){drawcolor=newval;}
	public void setdrawmode(int drawmode){this.drawmode=drawmode;}
	public void setdebugbounds(boolean debug){debugbounds=debug;}
	
	//all add methods
	public void addsprite(sprite newsprite){try{if(newsprite.loaded()){image.add(newsprite); if(image.size()==1){currentspritenumber=0;}}}catch(Exception e){}}
	public void addsprite(String filelocation){
		sprite tempsprite = new sprite(filelocation);
		if(tempsprite.loaded()){image.add(tempsprite); if(image.size()==1){currentspritenumber=0;}}
	}
	
	public void addsound(sound newsound){try{if(newsound.loaded()){audio.add(newsound);}}catch(Exception e){}}
	public boolean addsound(String filelocation){
		try{
			sound tempsound = new sound(filelocation);
			if(tempsound.loaded()){audio.add(tempsound); return true;}
			}catch(Exception e){}
		return false;
	}
	
	public boolean playsound(int reference){try{audio.get(reference).play(); return true;}catch(Exception e){} return false;}
	
	//the draw method draws the sprite to the screen.
	//it usually calls the draw method of a sprite object rather than drawing from directly inside the class.
	public void draw(){
		if(drawable){
			if(currentspriteexists()){//aka, if we have a sprite that exists, and we have not decided to draw bounds instead
				if(drawmode==2){//if we clip the image to within it's bounds
					mainscreen.screengraphics.drawImage(image.get(currentspritenumber).getimage(), (int)(x+spriteoffsetx), (int)(y+spriteoffsety), (int)(x+spriteoffsetx+boundsx), (int)(y+spriteoffsety+boundsy), (int)(spriteoffsetx), (int)(spriteoffsety), (int)(spriteoffsetx+boundsx), (int)(spriteoffsety+boundsy), null);
				}else if(drawmode==3){//we scale the image
					boolean drawsuccess=false;
					try{
						Image tempimage = image.get(currentspritenumber).getimage().getScaledInstance((int)(getsizex()), (int)(getsizey()), Image.SCALE_FAST);
						do{
							drawsuccess = mainscreen.screengraphics.drawImage(tempimage, (int)(x+spriteoffsetx), (int)(y+spriteoffsety), null);
						}while(!drawsuccess);
					}catch(Exception e){}
				}else{
					mainscreen.screengraphics.drawImage(image.get(currentspritenumber).getimage(), (int)(x+spriteoffsetx), (int)(y+spriteoffsety),null);
				}
			}else{//we draw the image normally, taking in to account any offsets that we wish to use.
				mainscreen.screengraphics.setColor(drawcolor);
				mainscreen.screengraphics.drawRect((int)(x), (int)(y), (int)boundsx, (int)boundsy);
			}//end of drawmode checks
		}//end of ifdrawable
		//allows us to draw the bounds of the object too.
		if(debugbounds){mainscreen.screengraphics.setColor(drawcolor);
						mainscreen.screengraphics.drawRect((int)(x), (int)(y), (int)getsizex(), (int)getsizey());}
	}
	
	
}//end of class
