package com.dim.objviewer;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GLCanvas;
import javax.swing.SwingUtilities;

class MyMouseListener implements MouseListener {
	public RotationData rotData;
	public ObjViewer ov;
//	private int dragStartX, dragStartY;
//	double viewRotX, viewRotY;

	public MyMouseListener(RotationData rd, ObjViewer ov){
		// TODO Auto-generated method stub
		this.rotData = rd;
		this.ov = ov;		
	}
		
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
            //ov.reset();
            GLCanvas can = (GLCanvas) e.getComponent();
            can.display();
        }
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
	
	public void mouseReleased(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {		
		
		if (SwingUtilities.isLeftMouseButton(e)) {
            ov.startDrag(e.getPoint());
            //System.out.println("pressed");
        }
	}

}

class MyMouseMotionListener implements MouseMotionListener {

	public RotationData rotData;
	public ObjViewer ov;
	
	public MyMouseMotionListener(RotationData rd, ObjViewer ov) {
		this.rotData = rd;
		this.ov = ov;
	}
	
	public void mouseMoved(MouseEvent arg0) {
	}

	public void mouseDragged(MouseEvent e) {

		GLCanvas can = (GLCanvas) e.getComponent();
		
		if (SwingUtilities.isLeftMouseButton(e)) {
            ov.drag(e.getPoint());
            //System.out.println("dragging");
            can.display();
        }
		else if (SwingUtilities.isRightMouseButton(e)) {
            ov.dragScale(e.getPoint());            
            can.display();
        }
	}
}

class MyMouseWheelListener implements MouseWheelListener{

	private ObjViewer ov = null;
	
	public MyMouseWheelListener(ObjViewer ov){
		this.ov  = ov;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		if(e.getScrollAmount() < 0)
			System.out.println("kleiner");
		
		System.out.println(e.getScrollAmount());
	}
	
	public int foogetWheelRotation(MouseWheelEvent e){
		
		return e.getScrollAmount();		
	}
	
	
	
}
