package com.dim.objviewer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLContext;
import javax.swing.text.html.ObjectView;

public class MyKeyListener implements KeyListener {

	
	private ObjViewer ov;

	public MyKeyListener(ObjViewer ov) {
		this.ov = ov;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		char c = e.getKeyChar();
		GLCanvas can = (GLCanvas) e.getComponent();

		ov.rotData.rotmode = true;

		switch (c) {
		case java.awt.event.KeyEvent.VK_Q:
			System.exit(0);
			break;
		case java.awt.event.KeyEvent.VK_4:
			//ov.rotData.rotateGl(true, 'y');
			ov.o -= 0.02f;
			break;
		case java.awt.event.KeyEvent.VK_6:
			//ov.rotData.rotateGl(false, 'y');
			ov.o += 0.02f;
			break;
		case java.awt.event.KeyEvent.VK_8:
			//ov.rotData.rotateGl(true, 'x');
			ov.l += 0.02f;
			break;
		case java.awt.event.KeyEvent.VK_2:
			//ov.rotData.rotateGl(false, 'x');
			ov.l -= 0.02f;
			break;
		case java.awt.event.KeyEvent.VK_SPACE:
			ov.shadingData.toggleWireframe();			
			break;
		case java.awt.event.KeyEvent.VK_S:
			ov.shadingData.setShadingmode("smooth");			
			break;
		case java.awt.event.KeyEvent.VK_F:
			ov.shadingData.setShadingmode("flat");		
			break;
		case java.awt.event.KeyEvent.VK_O:
			ov.scaling += 0.02f;			
			break;
		case java.awt.event.KeyEvent.VK_P:
			ov.scaling -= 0.02f;			
			break;
		case java.awt.event.KeyEvent.VK_BACK_SPACE:
			ov.scaling = 1.0f;
			ov.o = 0.0f;
			ov.l = 0.0f;			
			break;
		default:
			System.out.print("key typed - default\nKey: " + e.getKeyCode());
			break;
		}
		can.display();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO implement
		ov.rotData.rotmode = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
