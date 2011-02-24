package com.dim.objviewer;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLContext;
import javax.swing.SwingUtilities;

class MyMouseListener implements MouseListener {	
	public ObjViewer ov;

	// private int dragStartX, dragStartY;
	// double viewRotX, viewRotY;
	
	public MyMouseListener(ObjViewer ov) {
		// TODO Auto-generated method stub		
		this.ov = ov;
	}

	public void mouseClicked(MouseEvent e) {
		GLCanvas can = (GLCanvas) e.getComponent();
//		GLContext context = can.getContext();
//		context.makeCurrent();
//		GL gl = context.getGL();
//		
		if (SwingUtilities.isRightMouseButton(e)) {
			ov.reset();
			//ov.MouseToWorld(e.getX(), e.getY());			
			
			can.display();
		} else if (SwingUtilities.isLeftMouseButton(e)) {	
			ov.PICKED = true;
			ov.setLastPickPoint(e.getPoint());
			can.display();
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			ov.PANNING = false;
			System.out.println("panning ist false");
		}
	}

	public void mousePressed(MouseEvent e) {

		if (SwingUtilities.isLeftMouseButton(e)) {
			ov.startDrag(e.getPoint());			
		}
		if (SwingUtilities.isRightMouseButton(e)) {
			ov.PANNING = true;			
			System.out.println("panning ist true");
			ov.panPt = e.getPoint();
		}
	}

}

class MyMouseMotionListener implements MouseMotionListener {
	
	public ObjViewer ov;

	public MyMouseMotionListener(ObjViewer ov) {
		this.ov = ov;
	}

	public void mouseMoved(MouseEvent arg0) {
	}

	public void mouseDragged(MouseEvent e) {

		GLCanvas can = (GLCanvas) e.getComponent();

		if (SwingUtilities.isLeftMouseButton(e)) {
			ov.drag(e.getPoint());	
			can.display();
		} else if (SwingUtilities.isRightMouseButton(e)) {
			//ov.dragScale(e.getPoint());
			//ov.startPan(e.getPoint());
			ov.panPt = e.getPoint();
			can.display();
		}
	}
}

class MyMouseWheelListener implements MouseWheelListener {

	private ObjViewer ov = null;

	public MyMouseWheelListener(ObjViewer ov) {
		this.ov = ov;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		if (e.getWheelRotation() < 0) {
			ov.scaling -= 0.1;
		} else {
			ov.scaling += 0.1;
		}
		GLCanvas can = (GLCanvas) (e.getComponent());
		can.display();
	}

	public int foogetWheelRotation(MouseWheelEvent e) {

		return e.getScrollAmount();
	}

}
