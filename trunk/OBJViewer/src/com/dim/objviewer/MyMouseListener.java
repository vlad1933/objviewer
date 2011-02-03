package com.dim.objviewer;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

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
            ov.reset();
            //can.display()
        }
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
	
	public void mouseReleased(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		//rotData.dragStartX = e.getX();
		//rotData.dragStartY = e.getY();
		
		if (SwingUtilities.isLeftMouseButton(e)) {
            ov.startDrag(e.getPoint());
            System.out.println("pressed");
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
//		int x = e.getX();
//		int y = e.getY();
		GLCanvas can = (GLCanvas) e.getComponent();
//		
//		rotData.viewRotY = 360.0 * (x - rotData.dragStartX) / can.getWidth();
//		rotData.viewRotX = 360.0 * (y - rotData.dragStartY) / can.getHeight();
//		
//		can.display();
		
		if (SwingUtilities.isLeftMouseButton(e)) {
            ov.drag(e.getPoint());
            System.out.println("dragging");
            can.display();
        }
	}
}
