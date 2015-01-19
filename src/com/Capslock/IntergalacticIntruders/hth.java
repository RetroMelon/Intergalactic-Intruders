package com.Capslock.IntergalacticIntruders;

import java.awt.Color;

import com.Capslock.Engine.screen;

public class hth extends boss{
	
	public static final int SIZE_X = 192;
	public static final int SIZE_Y = 256;
	

	public hth(screen mainscreen) {
		super(mainscreen);
		name = "WTF, BRAH?";
		addsprite(I_Intruders.howthehell);
		addsprite(I_Intruders.howthehell);
		bombcolor = Color.WHITE;
		boundsx = SIZE_X;
		boundsy = SIZE_Y;
		x = 512;
		y = 70;
		totalhealth = 500;
		targetx = 200;
		targety = 200;
		level = 5;
	}
	
	@Override
	public void shoot(spbossfight maingame) {
		maingame.addbullet(true, (int)x+SIZE_X/2-70, (int)y+SIZE_Y-40);
		maingame.addbullet(true, (int)x+SIZE_X/2, (int)y+SIZE_Y);
		maingame.addbullet(true, (int)x+SIZE_X/2+70, (int)y+SIZE_Y-40);
		
	}
	
}
