package com.Capslock.Engine;

import java.awt.Color;

public class particle extends entity{
	
	private Color maincolor = Color.RED;

	public particle(screen mainscreen, double x, double y, Color maincolor) {
		super(mainscreen, x, y);
		this.maincolor = maincolor;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setup() {
		drawable=true;
		drawmode = entity.DRAWMODE_STANDARD;
		collideable = false;
		
	}
	
	public void draw(){
		mainscreen.screengraphics.setColor(maincolor);
		mainscreen.screengraphics.fillRect(	(int)x, (int)y, (int)boundsx, (int)boundsy);
	}

}
