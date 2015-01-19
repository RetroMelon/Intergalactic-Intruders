package com.Capslock.IntergalacticIntruders;

import java.awt.Color;

import com.Capslock.Engine.screen;

public class zblocky extends boss{
	
	public static final int SIZE_X = 192;
	public static final int SIZE_Y = 256;
	

	public zblocky(screen mainscreen) {
		super(mainscreen);
		name = "Zombie Blocky!";
		addsprite(I_Intruders.zblockysprite);
		addsprite(I_Intruders.zblockysprite_a);
		bombcolor = new Color(0, 102, 51); 
		boundsx = SIZE_X;
		boundsy = SIZE_Y;
		x = 512;
		y = 70;
		totalhealth = 350;
		targetx = 200;
		targety = 200;
		level = 2;
	}
	
	@Override
	public void shoot(spbossfight maingame) {
		maingame.addbullet(true, (int)x+SIZE_X/2-70, (int)y+SIZE_Y-40);
		maingame.addbullet(true, (int)x+SIZE_X/2, (int)y+SIZE_Y);
		maingame.addbullet(true, (int)x+SIZE_X/2+70, (int)y+SIZE_Y-40);
		
	}
	
}
