/**
 * Big 2Dos:
 * 	keine wireframe
 * skalieren können
 * normalen berechnen
 * initiale position nach drehung abfangen 
 */
package com.dim.objviewer;

import java.awt.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;

public class ObjViewer extends JFrame implements GLEventListener {
	private static final long serialVersionUID = 1L;

	// The house to display
	private Model model;
	private static GLCanvas canref;
	

	public RotationData rotData = new RotationData(10.0f);

	public ObjViewer() {
		super("OpenGL JOGL VIEWER");
		/*
		 * GLCanvas reports the following four events to a registered Listener:
		 * - init() - display() - displayChanged() - reshape() Therefore we
		 * implement the GLEventListener interface
		 */
		GLCanvas canvas = new GLCanvas();		
		rotData = new RotationData(10.0f);
		
		canvas.addGLEventListener(this);
		canvas.addKeyListener(new MyKeyListener(this));
		canvas.addMouseListener(new MyMouseListener(rotData));
		canvas.addMouseMotionListener(new MyMouseMotionListener(rotData));
		
		this.add(canvas, BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
	}

	public void init(GLAutoDrawable drawable) {
		//this.drawable = drawable;
		GL gl = drawable.getGL();

		// Set background color
		gl.glClearColor(0.3F, 0.3F, 0.3F, 1.0F);
		// Set foreground color
		//gl.glColor3f(0.0F, 0.0F, 0.0F);
		
		
		gl.glEnable(GL.GL_LIGHTING);
		// Switch on light0
		gl.glEnable(GL.GL_LIGHT0);
		// Enable automatic normalization of normals
		gl.glEnable(GL.GL_NORMALIZE);
		// Enable z-Buffer test
		gl.glEnable(GL.GL_DEPTH_TEST);
		// Enable color material
		gl.glEnable(GL.GL_COLOR_MATERIAL);
		
		model = new Model(gl);

	}

	public static GLCanvas getCanvas() {
		return canref;
	}

	public void display(GLAutoDrawable drawable) {

		GL gl = drawable.getGL();
		GLU glu = new GLU();
		GLUT glut = new GLUT();
		
		//System.out.print("\ngl instanz " + gl.toString());

		//gl.glClear(GL.GL_COLOR_BUFFER_BIT);		//Leert die im Parameter festgelegten Buffer, indem sie mit einen Leerwert gefüllt werden
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL.GL_PROJECTION);	//Legt fest, welche Matrix gerade aktiv ist
		gl.glLoadIdentity();		//Die Funktion glLoadIdentity ersetzt die aktuelle Matrix durch die Identitätsmatrix - Multiplikation einer Matrix A mit einer Einheitsmatrix ergibt wieder die Matrix A
		
		gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT);
		gl.glColor3f(0, 0, 1);

		
		//Mouse Interaction
		glu.gluLookAt(0,0,5, 0,0,0, 0,1,0);
		gl.glRotated(rotData.viewRotX, 1, 0, 0);			
		gl.glRotated(rotData.viewRotY, 0, 1, 0);

		//Keyboard Interaction
//		if(rotData.rotmode){
//			gl.glRotatef(rotData.rotation, rotData.getAxis()[0], rotData.getAxis()[1], rotData.getAxis()[2]);
//			//gl.glRotatef(rotData.rotation, rotData.getAxis()[0], rotData.getAxis()[1], rotData.getAxis()[2]);
//		}
		if (rotData.rotmode) {
			gl.glRotatef(rotData.rotx, 1.0f, 0.0f, 0.0f);
			gl.glRotatef(rotData.roty, 0.0f, 1.0f, 0.0f);
			gl.glRotatef(rotData.rotz, 0.0f, 0.0f, 1.0f);
		}
				
		
		//glut.glutSolidTeapot(1);
		//gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE); WireFrame
		model.draw(gl);
		
		System.out.print("\nin display");		
		

	}


	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL gl = drawable.getGL();

		
		int size = width < height ? width : height;
		int xbeg = width < height ? 0 : (width - height) / 2;
		int ybeg = width < height ? (height - width) / 2 : 0;

		gl.glViewport(xbeg, ybeg, size, size);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-2, 2, -2, 2, -10, 10);

	}

	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {

	}

	public static void main(String[] args) {
		Frame frame = new ObjViewer();
		frame.setBounds(0, 0, 400, 400);
		frame.setVisible(true);
	}


}
