package com.dim.objviewer;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.media.opengl.GLCanvas;

class MyMouseListener implements MouseListener {
	public RotationData rotData;
	
//	private int dragStartX, dragStartY;
//	double viewRotX, viewRotY;

	public MyMouseListener(RotationData rd){
		// TODO Auto-generated method stub
		this.rotData = rd;
		
	}
	
	public void mouseClicked(MouseEvent e) {
	
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		rotData.dragStartX = e.getX();
		rotData.dragStartY = e.getY();
	}

}

class MyMouseMotionListener implements MouseMotionListener {

	public RotationData rotData;
	
	public MyMouseMotionListener(RotationData rd) {
		this.rotData = rd;
	}
	
	public void mouseMoved(MouseEvent arg0) {
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		GLCanvas can = (GLCanvas) e.getComponent();
		
		rotData.viewRotY = 360.0 * (x - rotData.dragStartX) / can.getWidth();
		rotData.viewRotX = 360.0 * (y - rotData.dragStartY) / can.getHeight();
		
		can.display();
	}
}
