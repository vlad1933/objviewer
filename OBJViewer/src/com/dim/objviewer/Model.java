package com.dim.objviewer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.*;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;


/*
 * Second Revision!
 * */


public class Model {
	// Buffer for the vertex array
	private DoubleBuffer vertexBuffer;
	// Buffer for the normal arrray
	private DoubleBuffer normalBuffer;
	// Buffer for the index array
	private IntBuffer indices;
	private DoubleBuffer texcoordBuffer;
	
	private IntBuffer VBOVertices, VBONormals, VBOTexcoords;

	
	// Array of vertices building the house
	private ArrayList<Vert3> vertexList = new ArrayList<Vert3>();
	private ArrayList<Vert3> normalList = new ArrayList<Vert3>();
	private ArrayList<Face3> faceList = new ArrayList<Face3>();

	
	private double[][] texcoordList = {};

	public Model(GL gl) {
		loadModel("models/cube");
		//decrFaceList();
		buildArrays(gl);
		//buildVBOS(gl);
	}

	public void decrFaceList(){
//		int cnt = faceList.length;
//		int i = 0; int j = 0;
//		for (i = 0; i < faceList.length; i++) {
//			for(j = 0; j < 3; j++){				
//				faceList[i][j] = faceList[i][j] -1;
//				System.out.print("\n"+faceList[i][j]);
//			}			
//		}
	}
	/*
	 * Draw the house.
	 */
	public void draw(GL gl) {
		// Enable vertex arrays
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
		gl.glDrawElements(GL.GL_TRIANGLES, faceList.size() * 3, GL.GL_UNSIGNED_INT, indices);
		// Disable vertex arrays
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
	}

	/*
	 * Builds the vertex arrays for this house.
	 */
	private void buildArrays(GL gl) {
		/*
		 * Allocate space for the vertex and normal arrays. The class
		 * BufferUtils is used for convenience. You can also create the buffers
		 * using the java.nio methods. For example: vertexBuffer =
		 * ByteBuffer.allocateDirect(sizeInBytes).asDoubleBuffer();
		 */
		vertexBuffer = BufferUtil.newDoubleBuffer(vertexList.size() * 3);
		normalBuffer = BufferUtil.newDoubleBuffer(normalList.size() * 3);
		// Build the vertex and normal arrays
		for (int i = 0; i < vertexList.size(); i++) {
			vertexBuffer.put((vertexList.get(i)).getVert());
			normalBuffer.put(normalList.get(i).getVert());
		}

		// Allocate space for the index array
		indices = BufferUtil.newIntBuffer(faceList.size() * 3);
		// Build the index array
//		for (int[] f : faceList) {
//			indices.put(f);
//		}
		
		for (int i = 0; i < faceList.size(); i++){
			indices.put(faceList.get(i).getFace());
		}

		// Rewind all buffers
		vertexBuffer.rewind();
		normalBuffer.rewind();
		indices.rewind();

		// Tell OpenGL where to find the data
		gl.glVertexPointer(3, GL.GL_DOUBLE, 0, vertexBuffer);
		gl.glNormalPointer(GL.GL_DOUBLE, 0, normalBuffer);
	}
	
	/*private void buildVBOS(GL gl) {
		vertexBuffer = BufferUtil.newDoubleBuffer(vertexList.length * 3);
		normalBuffer = BufferUtil.newDoubleBuffer(normalList.length * 3);
		//texcoordBuffer = BufferUtil.newDoubleBuffer(texcoordList.length * 2);
		indices = BufferUtil.newIntBuffer(faceList.length * 4);

		// Build the vertex, normal and texture arrays
		for (int i = 0; i < vertexList.length; i++) {
			vertexBuffer.put(vertexList[i]);
			normalBuffer.put(normalList[i]);
			//texcoordBuffer.put(texcoordList[i]);
		}
		// Build the index array
		for (int[] f : faceList) {
			indices.put(f);
		}

		VBOVertices = BufferUtil.newIntBuffer(1);
		// Get a valid name
		gl.glGenBuffers(1, VBOVertices);
		// Bind the Buffer
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices.get(0));
		// Load the Data
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexList.length * 3 * BufferUtil.SIZEOF_DOUBLE, vertexBuffer, GL.GL_STATIC_DRAW);

		// Generate and bind Normal Buffer
		VBONormals = BufferUtil.newIntBuffer(1);
		// Get a valid name
		gl.glGenBuffers(1, VBONormals);
		// Bind the Buffer
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBONormals.get(0));
		// Load the Data
		gl.glBufferData(GL.GL_ARRAY_BUFFER, normalList.length * 3 * BufferUtil.SIZEOF_DOUBLE, normalBuffer, GL.GL_STATIC_DRAW);

		// Generate and bind the Texture Coordinates Buffer
//		VBOTexcoords = BufferUtil.newIntBuffer(1);
//		// Get a valid name
//		gl.glGenBuffers(1, VBOTexcoords);
//		// Bind the Buffer
//		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOTexcoords.get(0));
//		// Load the Data
//		gl.glBufferData(GL.GL_ARRAY_BUFFER, texcoordList.length * 2	* BufferUtil.SIZEOF_DOUBLE, texcoordBuffer, GL.GL_STATIC_DRAW);

		// Rewind all buffers
		vertexBuffer.rewind();
		normalBuffer.rewind();
		//texcoordBuffer.rewind();
		indices.rewind();

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices.get(0));
		gl.glVertexPointer(3, GL.GL_DOUBLE, 0, 0);
		//gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOTexcoords.get(0));
		//gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, 0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBONormals.get(0));
		gl.glNormalPointer(GL.GL_DOUBLE, 0, 0);
	}*/
	
	private void loadModel(String name)
    {
      String fnm = name + ".obj";
      try {
        System.out.println("Loading model from " + fnm + " ...");
        BufferedReader br = new BufferedReader( new FileReader(fnm) );

        readModel(br);

        br.close();
      }
      catch(IOException e)
      {  System.out.println(e.getMessage());
         System.exit(1);
      }
    }


  private void readModel(BufferedReader br)
    // parse the OBJ file line-by-line
    {
      boolean isLoaded = true;   

      int lineNum = 0;
      String line;
      boolean isFirstCoord = true;
      boolean isFirstTC = true; //tex coord
      int numFaces = 0;

      try {
        while (((line = br.readLine()) != null) && isLoaded) {
          lineNum++;
          if (line.length() > 0) {
            line = line.trim();

            if (line.startsWith("v ")) {   // vertex
              extractVert(line);
              if (isFirstCoord)
                isFirstCoord = false;
            }
            else if (line.startsWith("vt")) {   // tex coord
            	extractTexCord(line);
              if (isFirstTC)
                isFirstTC = false;
            }
            else if (line.startsWith("vn"))    // normal
                    extractNormal(line);
                    //isLoaded = addNormal(line);
            else if (line.startsWith("f ")) {  // face
              extractFace(line);
              numFaces++;
            }            
            else if (line.startsWith("#"))   // comment line
              continue;
            else
              System.out.println("Ignoring line " + lineNum + " : " + line);
          }
        }
      }
      catch (IOException e) {
        System.out.println( e.getMessage() );
        System.exit(1);
      }

      if (!isLoaded) {
        System.out.println("Error loading model");
        System.exit(1);
      }
      System.out.println("file read");      
    }
  
  private void extractVert(String line){
      StringTokenizer tokens = new StringTokenizer(line, " ");
      tokens.nextToken();    // skip the OBJ word

      try {
        float x = Float.parseFloat(tokens.nextToken());
        float y = Float.parseFloat(tokens.nextToken());
        float z = Float.parseFloat(tokens.nextToken());

        vertexList.add(new Vert3(x,y,z));

      }
      catch (NumberFormatException e)
      {  System.out.println(e.getMessage());  }

  }
  
  private void extractNormal(String line){
	  StringTokenizer tokens = new StringTokenizer(line, " ");
      tokens.nextToken();    // skip the OBJ word

      try {
        float x = Float.parseFloat(tokens.nextToken());
        float y = Float.parseFloat(tokens.nextToken());
        float z = Float.parseFloat(tokens.nextToken());

        normalList.add(new Vert3(x,y,z));

      }
      catch (NumberFormatException e)
      {  System.out.println(e.getMessage());  }
  }
  
  private void extractTexCord(String line){
	  
  }
  
private void extractFace(String line){
	StringTokenizer blank_tokens = new StringTokenizer(line, " ");
	
    blank_tokens.nextToken();    // skip the OBJ word

    try {
      
      int[] a = cutSlash(blank_tokens.nextToken());  
      int[] b = cutSlash(blank_tokens.nextToken());
      int[] c = cutSlash(blank_tokens.nextToken());

      Face3 face = new Face3(a, b, c);
      //faceList.add(new Face3(x,y,z));

    }
    catch (NumberFormatException e)
    {  System.out.println(e.getMessage());  }
  }

private int[] cutSlash(String str){
	int[] token_arr = {-1,-1,-1};
	
	StringTokenizer slash_tokens = new StringTokenizer(str, "/") ;
	
	token_arr[0] = Integer.parseInt(slash_tokens.nextToken());
	token_arr[1] = Integer.parseInt(slash_tokens.nextToken());
	if(slash_tokens.hasMoreTokens()){
		token_arr[2] = Integer.parseInt(slash_tokens.nextToken());
	}else{
		token_arr[2] = token_arr[1];
		token_arr[1] = -1;
	}
	
	return token_arr;
}
}
