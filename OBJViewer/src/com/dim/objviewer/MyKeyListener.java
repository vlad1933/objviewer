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

		switch (c) {
		case java.awt.event.KeyEvent.VK_Q:
			System.exit(0);
			break;
		case java.awt.event.KeyEvent.VK_SPACE:
			ov.shadingData.toggleWireframe();
			Model.DEBUG = true;
			break;
		case java.awt.event.KeyEvent.VK_S:
			ov.shadingData.setShadingmode("smooth");			
			break;
		case java.awt.event.KeyEvent.VK_F:
			ov.shadingData.setShadingmode("flat");		
			break;
		case java.awt.event.KeyEvent.VK_BACK_SPACE:
			ov.scaling = 1.0f;
			break;
		case java.awt.event.KeyEvent.VK_1:
			ov.initShader(can);
		default:
			System.out.print("key typed - default\nKey: " + e.getKeyCode());
			break;
		}
		can.display();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO implement		
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
