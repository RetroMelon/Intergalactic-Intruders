package com.Capslock.IntergalacticIntruders;

import java.awt.Color;

import com.Capslock.Engine.entity;
import com.Capslock.Engine.game;
import com.Capslock.Engine.peffectfactory;
import com.Capslock.Engine.screen;

public class mpbullet extends bullet{
	
	private boolean alien = false;
	private Color bulletcolor = Color.white;
	private int idnumber = 0;
	
	private game maingame;
	
	private peffectfactory bullettraileffect;
	
	public mpbullet(game maingame, int x, int y, boolean alien, int idnumber) {
		super(maingame, x, y, alien);
		I_Intruders.alienshoot.play();
		this.idnumber = idnumber;		
	}

	public int getid(){
		return idnumber;
	}

}
