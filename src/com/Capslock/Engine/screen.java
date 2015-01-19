package com.Capslock.Engine;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;

public class screen extends Canvas implements MouseMotionListener, MouseListener{
	private static final long serialVersionUID = 1L;
	
	//public objects that all other classes can see
	public Graphics2D screengraphics;
	public keyboardinput keyboard;
	
	//properties of the window
	private String title = "CAPSLOCK Game Engine v1.0 - By Joe Frew";
	private int sizex = 640;
	private int sizey = 480;
	private double aspectratio=4/3;
	private Color backgroundcolor = Color.BLACK;
	private boolean clearonupdate = true;
	private boolean focussed = true;
	private boolean drawfocussedscreen = true;
	
	//other variables and objects, used during operation of this class
	private BufferStrategy strategy;
	public JFrame frame;
	
	//variables concerning the mouse
	private boolean mousedown = false;
	private int mousex = 0;
	private int mousey = 0;
	
	public screen(String title, int sizex, int sizey){
		//setting variables
		this.title = title;
		this.sizex = sizex;
		this.sizey = sizey;
		System.out.println("CREATED GAME SCREEN WITH DIMENSIONS: "+sizex+"x"+sizey);
		aspectratio=(double)sizex/(double)sizey;
		//Initialising objects such as keyboard
		keyboard= new keyboardinput();
		
		//setting up a window
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		//changing some settings for the canvas
		setIgnoreRepaint(true);
		setSize(sizex, sizey);
		
		//making the window visible on screen
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
		
		//adding the key listener, focus listener and requesting focus
		addKeyListener(keyboard);
		addMouseMotionListener(this);
		addMouseListener(this);
		addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {focussed=true;}
			public void focusLost(FocusEvent e) {
				focussed=false;
				keyboard.leftpressed = false;
				keyboard.rightpressed = false;
				keyboard.uppressed = false;
				keyboard.downpressed = false;
				keyboard.spacepressed = false;
				keyboard.enterpressed=false;
				keyboard.escpressed=false;
				mousedown=false;
			}
		});
		requestFocus();
		
		//setting up the buffer
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		screengraphics = (Graphics2D) strategy.getDrawGraphics();
		screengraphics.setColor(Color.RED);
	}
	
	public void update(){
		if(!focussed && drawfocussedscreen){
			screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.72));
			screengraphics.setColor(Color.DARK_GRAY);
			screengraphics.fillRect(0, 0, sizex, sizey);
			screengraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)1));
			screengraphics.setColor(Color.white);
			String message = "CLICK TO REGAIN FOCUS!";
			Font f = new Font(Font.DIALOG, Font.BOLD, 23);
			screengraphics.setFont(f);
			int textlength = screengraphics.getFontMetrics().stringWidth(message);
			screengraphics.drawString(message, sizex/2-textlength/2, sizey/2);
		}
			screengraphics.dispose();
			strategy.show();
			screengraphics = (Graphics2D) strategy.getDrawGraphics();
			Color tempcol = screengraphics.getColor();
			screengraphics.setColor(backgroundcolor);
			if(clearonupdate)screengraphics.fillRect(0, 0, sizex, sizey);
			screengraphics.setColor(tempcol);
	}
	
	//clears both sides of the buffer
	public void flush(){
		Color tempcol = screengraphics.getColor();
		screengraphics.setColor(backgroundcolor);
		screengraphics.fillRect(0, 0, sizex, sizey);
		screengraphics.dispose();
		strategy.show();
		screengraphics =(Graphics2D) strategy.getDrawGraphics();
		screengraphics.setColor(backgroundcolor);
		screengraphics.fillRect(0, 0, sizex, sizey);
		screengraphics.dispose();
		strategy.show();
		screengraphics =(Graphics2D) strategy.getDrawGraphics();
		screengraphics.setColor(tempcol);
		
	}
	
	//get methods
	public int getsizex(){return sizex;}
	public int getsizey(){return sizey;}
	public double getaspectratio(){return aspectratio;}
	public boolean getclearonupdate(){return clearonupdate;}
	public String getwindowtitle(){return title;}
	public Color getbackgroundcolor(){return backgroundcolor;}
	public boolean getfocussed(){return focussed;}
	public int getmousex(){return mousex;}
	public int getmousey(){return mousey;}
	public boolean getmousedown(){return mousedown;}
	
	//set methods
	//public void setsizex(int newsizex){sizex=newsizex; aspectratio=(double)sizex/(double)sizey;}
	//public void setsizey(int newsizey){sizey=newsizey; aspectratio=(double)sizex/(double)sizey;}
	public void setclearonupdate(boolean clearonupdate){this.clearonupdate=clearonupdate;}
	public void setwindowtitle(String newtitle){title=newtitle; frame.setTitle(title);}
	public void setbackgroundcolor(Color newbackgroundcolor){backgroundcolor=newbackgroundcolor;}

	@Override
	public void mouseDragged(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
	}

	//**********************************methods for detecting clicks*********************************************
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousedown=true;
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mousedown=false;
		
	}

}
