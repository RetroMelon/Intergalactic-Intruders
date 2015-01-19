package com.Capslock.IntergalacticIntruders;

import com.Capslock.Engine.entity;
import com.Capslock.Engine.screen;

public class musictoggle extends entity{

	public musictoggle(screen mainscreen, double x, double y) {
		super(mainscreen, x, y);
	}

	@Override
	public void setup() {
		this.addsprite(I_Intruders.musictogglesprite);
		boundsx = 50;
		boundsy = 50;
		
	}

}
