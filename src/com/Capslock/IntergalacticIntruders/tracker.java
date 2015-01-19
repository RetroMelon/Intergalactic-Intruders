package com.Capslock.IntergalacticIntruders;

import java.awt.Color;

import com.Capslock.Engine.entity;
import com.Capslock.Engine.screen;

public class tracker extends entity{

	public tracker(screen mainscreen, double x, double y) {
		super(mainscreen, x, y);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setup() {
		drawable = false;
		collideable = false;
		
	}
	
	public void draw(){
		mainscreen.screengraphics.setColor(Color.red);
		mainscreen.screengraphics.drawLine((int)x-10, (int)y, (int)x+10, (int)y);
		mainscreen.screengraphics.drawLine((int)x, (int)y-10, (int)x, (int)y+10);
	}

}
