package com.Capslock.IntergalacticIntruders;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;

import com.Capslock.Engine.entity;
import com.Capslock.Engine.screen;
import com.Capslock.Engine.sprite;

public class spacebutton extends entity{

	private boolean drawbasic = true;
	private boolean drawoutline = true;
	private Color outlinecolor = Color.GRAY;
	private Color textcolor = Color.WHITE;
	private Color bodycolor = Color.BLACK;
	private String title = "Untitled";
	private int textsize = 25;
	private int number = 0;

	
	public spacebutton(screen mainscreen, String title, int x, int y, int sizex, int sizey, int number, sprite newsprite) {
		super(mainscreen);
		//adding the texture to the entity's image list. it is scaled in the process.
		try{addsprite(new sprite(newsprite.getimage(), sizex, sizey));}catch(Exception e){}
		this.x=x;
		this.y=y;
		this.title=title;
		this.boundsx=sizex;
		this.boundsy=sizey;
		this.number=number;
	}
	
	public spacebutton(screen mainscreen, String title, int x, int y, int sizex, int sizey, int number) {
		super(mainscreen);
		//adding the texture to the entity's image list. it is scaled in the process.
		this.x=x;
		this.y=y;
		this.title=title;
		this.boundsx=sizex;
		this.boundsy=sizey;
		this.number=number;
	}

	@Override
	public void setup() {
		drawoutline=false;
		collisionmode=3;
	}
	
	public void draw(){
		if(!drawbasic && currentspriteexists()){//aka, if we have a sprite that exists, and we have not decided to draw bounds instead
			try{		
					//drawing the image which has already been scaled
					mainscreen.screengraphics.drawImage(image.get(currentspritenumber).getimage(), (int)(x+spriteoffsetx), (int)(y+spriteoffsety), null);

					
					if(drawoutline){
						mainscreen.screengraphics.setColor(outlinecolor);
						mainscreen.screengraphics.drawRect((int)x, (int)y, (int)boundsx, (int)boundsy);
					}
					
					//creating the correct font, and deciding where to position the text
					Font f = new Font(Font.DIALOG, Font.PLAIN, textsize);
					mainscreen.screengraphics.setFont(f);
					int textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(title);
					
					
					mainscreen.screengraphics.setColor(textcolor);
					mainscreen.screengraphics.drawString(title, (int)(x+boundsx/2-textlength/2), (int)(y+boundsy/2+textsize/2));
					
			}catch(Exception e){drawbasic();}
		}else{
			drawbasic();
		}
	}
	
	private void drawbasic(){
		
		//drawing the basic shape of the button
		mainscreen.screengraphics.setColor(bodycolor);
		mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.5));
		mainscreen.screengraphics.fillRect((int)x, (int)y, (int)boundsx, (int)boundsy);
		mainscreen.screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		
		//drawing the border
		mainscreen.screengraphics.setColor(outlinecolor);
		mainscreen.screengraphics.drawRect((int)x, (int)y, (int)boundsx, (int)boundsy);
		
		//creating the font, and deciding where to position the text.
		Font f = new Font(Font.DIALOG_INPUT, Font.PLAIN, textsize);
		mainscreen.screengraphics.setFont(f);
		int textlength = mainscreen.screengraphics.getFontMetrics().stringWidth(title);
		
		//changing the colour to the text colour, and drawing the text.
		mainscreen.screengraphics.setColor(textcolor);
		mainscreen.screengraphics.drawString(title, (int)(x+this.getsizex()/2-textlength/2), (int)(y+this.getsizey()/2+textsize/2));
	}

	
	public int getnumber(){return number;}
}
