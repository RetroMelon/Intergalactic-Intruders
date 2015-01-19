package com.Capslock.IntergalacticIntruders;

import java.awt.Color;

import com.Capslock.Engine.screen;

public class blocky extends boss{
	
	public static final int SIZE_X = 192;
	public static final int SIZE_Y = 256;
	

	public blocky(screen mainscreen) {
		super(mainscreen);
		name = "Zombie Blocky!";
		addsprite(I_Intruders.blockysprite);
		addsprite(I_Intruders.blockysprite_a);
		bombcolor = Color.red;
		boundsx = SIZE_X;
		boundsy = SIZE_Y;
		x = 512;
		y = 70;
		totalhealth = 200;
		targetx = 200;
		targety = 200;
		level = 1;
	}
	
	@Override
	public void shoot(spbossfight maingame) {
		maingame.addbullet(true, (int)x+SIZE_X/2-70, (int)y+SIZE_Y-40);
		maingame.addbullet(true, (int)x+SIZE_X/2, (int)y+SIZE_Y);
		maingame.addbullet(true, (int)x+SIZE_X/2+70, (int)y+SIZE_Y-40);
		
	}
	
}
