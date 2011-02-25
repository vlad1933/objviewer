package com.dim.objviewer;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.BufferUtil;

public class Plain {

	private GL gl;
	private ArrayList<Vert3> vertexList = new ArrayList<Vert3>();
	private ArrayList<Integer> faceList = new ArrayList<Integer>();
	private ArrayList<Vert3> normalList = new ArrayList<Vert3>();
	
	private IntBuffer VBOVertices, VBONormals, VBOIndices;
	
	private DoubleBuffer vertexBuffer;
	private IntBuffer indices;

	public Plain(GL gl) {
		this.gl = gl;
		this.buildArrays();
		this.buildVBOS(gl);
	}

	public void buildArrays() {
		int MATRIXSIZE = 10;

		int i = 0;
		int j = 0;

		for (i = 0; i < MATRIXSIZE; i++) {
			for (j = 0; j < MATRIXSIZE; j++) {
				Vert3 v = new Vert3(i, j, 0);
				vertexList.add(v);
			}
		}
		
		for (i = 0; i < vertexList.size() - MATRIXSIZE; i++) {
			if(((i+1)%MATRIXSIZE) == 0)
				continue;
			
			//counterclockwise
			faceList.add(i);			
			faceList.add(i+MATRIXSIZE);
			faceList.add(i+MATRIXSIZE+1);
			faceList.add(i+1);
		}
		
		double shrinkFactor = 0.25;
		for(Vert3 v : vertexList){			
			v.setVertA(v.getVertA()*shrinkFactor);
			v.setVertB(v.getVertB()*shrinkFactor);
		}
		
		//System.out.println(vertexList.get(0) + " " + faceList.get(0));
	}

	public void draw(GL gl) {

		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices.get(0));
		gl.glVertexPointer(3, GL.GL_DOUBLE, 0, 0);

		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, VBOIndices.get(0));


		
		try {
						
			gl.glDisable(GL.GL_LIGHTING);
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE); // WireFrame
			
			gl.glColor3f(1.0f, 1.0f, 1.0f);
			gl.glLineWidth(1.0f);
			gl.glTranslated(-1.125, -1, 0);
			gl.glRotated(90, 1, 0, 0);
									
			gl.glDrawElements(GL.GL_QUADS, faceList.size() * 4,
					GL.GL_UNSIGNED_INT, 0);
			
			gl.glEnable(GL.GL_LIGHTING);
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// unbind?!
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);

		// Disable vertex arrays
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);		
	}
	
	public void drawAxes(GL gl){

	
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glDisable(GL.GL_LIGHTING);
		gl.glLineWidth(1);
		gl.glBegin(GL.GL_LINES);

			gl.glColor3d(1.0, 0.0, 0.0);
			gl.glVertex3d(0.0, 0.0, 0.0);
			gl.glVertex3d(4.0, 0.0, 0.0);
	
			gl.glColor3d(0.0, 1.0, 0.0);
			gl.glVertex3d(0.0, 0.0, 0.0);
			gl.glVertex3d(0.0, 4.0, 0.0);
	
			gl.glColor3d(0.0, 0.0, 1.0);
			gl.glVertex3d(0.0, 0.0, 0.0);
			gl.glVertex3d(0.0, 0.0, 4.0);

		gl.glEnd();
		gl.glEnable(GL.GL_LIGHTING);
		
		//glFlush();

	}
	
	private void buildVBOS(GL gl) {

		vertexBuffer = BufferUtil.newDoubleBuffer(vertexList.size() * 3);
		
		indices = BufferUtil.newIntBuffer(faceList.size() * 4);

		for (int i = 0; i < vertexList.size(); i++) {
			Vert3 vert = vertexList.get(i);
			vertexBuffer.put(vert.getVert());
		}

		
		/*
		 * Indicees
		 */
		for (Integer f : faceList) {
			/*
			 * Indizees eines Face eintragen
			 */
			indices.put((int)f);
			
		}

		// Rewind all buffers
		vertexBuffer.rewind();		
		indices.rewind();

		VBOVertices = BufferUtil.newIntBuffer(1);
		// Get a valid name
		gl.glGenBuffers(1, VBOVertices);
		// Bind the Buffer
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices.get(0));
		// Load the Data
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexList.size() * 3
				* BufferUtil.SIZEOF_DOUBLE, vertexBuffer, GL.GL_STATIC_DRAW);

		
		VBOIndices = BufferUtil.newIntBuffer(1);
		gl.glGenBuffers(1, VBOIndices);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, VBOIndices.get(0));
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, faceList.size() * 4
				* BufferUtil.SIZEOF_INT, indices, GL.GL_STATIC_DRAW);

		
	}
}
