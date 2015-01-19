package com.Capslock.Engine;

//used to track the location of the instructions text

public class instructiontracker extends entity{

	public instructiontracker(screen mainscreen, int x, int y) {
		super(mainscreen);
		this.x=x;
		this.y=y;
	}

	@Override
	public void setup() {
		boundsx=10;
		boundsy=10;
		
		
	}

}
