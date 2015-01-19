package com.Capslock.Engine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class keyboardinput extends KeyAdapter{
	
	public boolean leftpressed = false;
	public boolean rightpressed = false;
	public boolean uppressed = false;
	public boolean downpressed = false;
	public boolean spacepressed = false;
	public boolean enterpressed=false;
	public boolean escpressed=false;
	
	public void keyPressed(KeyEvent e){
		if(e.getKeyCode()==KeyEvent.VK_LEFT){
			leftpressed=true;
		}
		if(e.getKeyCode()==KeyEvent.VK_RIGHT){
			rightpressed=true;
		}
		if(e.getKeyCode()==KeyEvent.VK_UP){
			uppressed=true;
		}
		if(e.getKeyCode()==KeyEvent.VK_DOWN){
			downpressed=true;
		}
		if(e.getKeyCode()==KeyEvent.VK_SPACE){
			spacepressed=true;
		}
		if(e.getKeyCode()==KeyEvent.VK_ENTER){
			enterpressed=true;
		}
		if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
			escpressed=true;
		}
	}
	
	public void keyReleased(KeyEvent e){
		if(e.getKeyCode()==KeyEvent.VK_LEFT){
			leftpressed=false;
		}
		if(e.getKeyCode()==KeyEvent.VK_RIGHT){
			rightpressed=false;
		}
		if(e.getKeyCode()==KeyEvent.VK_UP){
			uppressed=false;
		}
		if(e.getKeyCode()==KeyEvent.VK_DOWN){
			downpressed=false;
		}
		if(e.getKeyCode()==KeyEvent.VK_SPACE){
			spacepressed=false;
		}
		if(e.getKeyCode()==KeyEvent.VK_ENTER){
			enterpressed=false;
		}
		if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
			escpressed=false;
		}
	}

}
