package com.dim.objviewer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLContext;
import javax.swing.text.html.ObjectView;

public class MyKeyListener implements KeyListener {

	private boolean show;
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
			ov.rotData.rotateGl(true, 'y');
			break;
		case java.awt.event.KeyEvent.VK_6:
			ov.rotData.rotateGl(false, 'y');
			break;
		case java.awt.event.KeyEvent.VK_8:
			ov.rotData.rotateGl(true, 'x');
			break;
		case java.awt.event.KeyEvent.VK_2:
			ov.rotData.rotateGl(false, 'x');
			break;
		case java.awt.event.KeyEvent.VK_SPACE:
			
			can.display();
			break;
		default:
			System.out.print("key typed - default\nKey: " + e.getKeyChar());
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
