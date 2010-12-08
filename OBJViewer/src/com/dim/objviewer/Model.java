package com.dim.objviewer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.*;

import javax.media.opengl.GL;
import com.sun.opengl.util.BufferUtil;

/*
 * Third Revision!
 * */

public class Model {
	// Buffer for the vertex array
	private DoubleBuffer vertexBuffer;
	// Buffer for the normal arrray
	private DoubleBuffer normalBuffer;
	// Buffer for the index array
	private IntBuffer indices;
	private DoubleBuffer texcoordBuffer;

	private IntBuffer VBOVertices, VBONormals, VBOTexcoords, VBOIndices;
	
	private boolean noVT = true;

	// Array of vertices building the house
	private ArrayList<Vert3> vertexList = new ArrayList<Vert3>();
	private ArrayList<Vert3> normalList = new ArrayList<Vert3>();
	private ArrayList<Face3> faceList = new ArrayList<Face3>();
	private ArrayList<Vert3> texcoordList = new ArrayList<Vert3>();
	private ArrayList<Vert3> newNormalList = new ArrayList<Vert3>();
	
	private ModelDimensions modeldims = new ModelDimensions();
	private double maxSize;

	public Model(GL gl) {
		loadModel("models/bunny");
		
				
		buildVBOS(gl);
		centerScale();
	}

	/*
	 * Draw the house.
	 */
	public void draw(GL gl) {
		// Enable vertex arrays
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
		//gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
		
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices.get(0));
		gl.glVertexPointer(3, GL.GL_DOUBLE, 0, 0);
		
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBONormals.get(0));
		gl.glNormalPointer(GL.GL_DOUBLE, 0, 0);
		
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, VBOIndices.get(0));
		
		
		//gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOTexcoords.get(0));
		//gl.glTexCoordPointer(3, GL.GL_DOUBLE, 0, 0);
				
		
		try {
			gl.glDrawElements(GL.GL_TRIANGLES, 	faceList.size() * 3, 			GL.GL_UNSIGNED_INT, 0); //läuft nicht -> exception
			//gl.glDrawElements(GL.GL_TRIANGLES, indexList.length * 3, GL.GL_UNSIGNED_INT, 0);
			//					Mode,			Anzahl d. z. rendernden Elems,	Typ im Array	,	indices
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
		
				
		//gl.glDrawArrays(GL.GL_TRIANGLES, 0, vertexList.size()-1); //-> man sieht schrott aber es läuft
		
		
		//unbind?!
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		// Disable vertex arrays
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
		//gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
	}

	/*
	 * Builds the vertex arrays for this house.
	 */


	private void buildVBOS(GL gl) {
		
		vertexBuffer = BufferUtil.newDoubleBuffer(vertexList.size() * 3);
		normalBuffer = BufferUtil.newDoubleBuffer(vertexList.size() * 3);
		
		//texcoordBuffer = BufferUtil.newDoubleBuffer(texcoordList.size() * 3);
		indices = BufferUtil.newIntBuffer(faceList.size() * 3);

		// Build the vertex, normal and texture arrays
		for (int i = 0; i < vertexList.size(); i++) {
			double[] v = vertexList.get(i).getVert();
			vertexBuffer.put(v);			
			newNormalList.add(new Vert3(0, 0, 0));			
		}
		
		//normalList padden mit Nullen - wird überschrieben
		if(normalList.size() < vertexList.size()){
			for(int i = normalList.size(); i < vertexList.size(); i++ ){
				normalList.add(new Vert3(0,0,0));
			}
		}
		
		for (int i = 0; i < faceList.size(); i++) {
			int[] f = faceList.get(i).getFaceVerts();
			int[] fn = faceList.get(i).getFaceNormals();
			indices.put(faceList.get(i).getFaceVerts());
			
			newNormalList.set(f[0],normalList.get(fn[0]));
			newNormalList.set(f[1],normalList.get(fn[1]));
			newNormalList.set(f[2],normalList.get(fn[2]));
			
			//System.out.println(newNormalList.get(f[0]));
		}
		
		normalBuffer.rewind();
		for(Vert3 v: newNormalList){
			normalBuffer.put(v.getVert());			
		}		
		
//		for(int i = 0; i < normalBuffer.capacity(); i++){
//			double foo = normalBuffer.get(i);
//			System.out.println(i + " : " + foo);
//		}
		
		for(int i = 0; i < indices.capacity(); i++){
			int foo = indices.get(i);
			//System.out.println(i + " : " + foo);
		}
		
		// Rewind all buffers
		vertexBuffer.rewind();
		normalBuffer.rewind();
		//texcoordBuffer.rewind();
		indices.rewind();

		VBOVertices = BufferUtil.newIntBuffer(1);
		// Get a valid name
		gl.glGenBuffers(1, VBOVertices);
		// Bind the Buffer
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices.get(0));
		// Load the Data
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexList.size() * 3 * BufferUtil.SIZEOF_DOUBLE, vertexBuffer, GL.GL_STATIC_DRAW);

		// Generate and bind Normal Buffer
		VBONormals = BufferUtil.newIntBuffer(1);
		// Get a valid name
		gl.glGenBuffers(1, VBONormals);
		// Bind the Buffer
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBONormals.get(0));
		// Load the Data
		gl.glBufferData(GL.GL_ARRAY_BUFFER, normalList.size() * 3 * BufferUtil.SIZEOF_DOUBLE, normalBuffer, GL.GL_STATIC_DRAW);

		// Generate and bind the Texture Coordinates Buffer
//		VBOTexcoords = BufferUtil.newIntBuffer(1);
//		// Get a valid name
//		gl.glGenBuffers(1, VBOTexcoords);
//		// Bind the Buffer
//		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOTexcoords.get(0));
//		// Load the Data
//		gl.glBufferData(GL.GL_ARRAY_BUFFER, texcoordList.size() * 3 * BufferUtil.SIZEOF_DOUBLE, texcoordBuffer, GL.GL_STATIC_DRAW);
		
		VBOIndices = BufferUtil.newIntBuffer(1);
		gl.glGenBuffers(1, VBOIndices);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, VBOIndices.get(0));
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER,	faceList.size() * 3 * BufferUtil.SIZEOF_INT, indices, GL.GL_STATIC_DRAW);
		
		
//		gl.glGenBuffers(1, indices);
//		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indices.get(0));
//		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, faceList.size() * 3 * BufferUtil.SIZEOF_INT, indices, GL.GL_STATIC_DRAW);
		
		
		/** GL_ARRAY_BUFFER
				Das Puffer-Objekt wird zur Speicherung von Vertexarray-Daten benutzt.
			GL_ELEMENT_ARRAY_BUFFER
				Das Puffer-Objekt dient zur Speicherung von Indexwerten für Vertexarrays.
		**/

		
		
		/** glVertexPointer specifies the location and data format of an array of vertex coordinates to use when rendering.
        size specifies the number of coordinates per vertex, and must be 2, 3, or 4.
        type specifies the data type of each coordinate, and stride specifies the byte stride from one vertex to the next, allowing vertices and attributes
        to be packed into a single array or stored in separate arrays
		**/
		
//		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices.get(0));
//		gl.glVertexPointer(3, GL.GL_DOUBLE, 0, 0);
//		
////		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOTexcoords.get(0));
////		gl.glTexCoordPointer(2, GL.GL_DOUBLE, 0, 0);
//
//		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBONormals.get(0));
//		gl.glNormalPointer(GL.GL_DOUBLE, 0, 0);
//		
//		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indices.get(0));
		
	}

	private void loadModel(String name) {
		Vert3 v1 = new Vert3(2,3,4);
		Vert3 v2 = new Vert3(2,2,2);
		Vert3 v3 = Vert3.multiply(v1, v2);
		v3.printVert();
		
		String filename = name + ".obj";
		try {
			System.out.println("Loading model from " + filename + " ...");
			BufferedReader br = new BufferedReader(new FileReader(filename));

			readModel(br);

			br.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		
		
	}

	private void readModel(BufferedReader br)
	// parse the OBJ file line-by-line
	{
		boolean isLoaded = true;

		int lineNum = 0;
		String line;
		int numFaces = 0;

		try {
			while (((line = br.readLine()) != null) && isLoaded) {
				lineNum++;
				if (line.length() > 0) {
					line = line.trim();

					if (line.startsWith("v ")) { // vertex
						extractVert(line);
					} else if (line.startsWith("vt")) { // tex-coord
						extractTexCord(line);//not supported yet
						this.noVT = false;
					} else if (line.startsWith("vn")) // normal
						extractNormal(line);
					else if (line.startsWith("f ")) { // face
						extractFace(line);
						numFaces++;
					} else if (line.startsWith("#")) // comment line
						System.out.println("comment: " + line);
					else
						System.out.println("Ignoring line " + lineNum + " : " + line);
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

		if (!isLoaded) {
			System.out.println("Error loading model");
			System.exit(1);
		}

		if (normalList.size() == 0)
			computeNormals();

		System.out.println("file read");
	}


	private void extractVert(String line) {
		StringTokenizer tokens = new StringTokenizer(line, " ");
		tokens.nextToken(); // skip the OBJ word

		try {
			float x = Float.parseFloat(tokens.nextToken());
			float y = Float.parseFloat(tokens.nextToken());
			float z = Float.parseFloat(tokens.nextToken());

			vertexList.add(new Vert3(x, y, z));

		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
		}

	}

	private void extractNormal(String line) {
		StringTokenizer tokens = new StringTokenizer(line, " ");
		tokens.nextToken(); // skip the OBJ word

		try {
			float x = Float.parseFloat(tokens.nextToken());
			float y = Float.parseFloat(tokens.nextToken());
			float z = Float.parseFloat(tokens.nextToken());

			normalList.add(new Vert3(x, y, z));

		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
		}
	}

	private void extractTexCord(String line) {
		texcoordList.add(new Vert3(0,0,0));
	}

	private void extractFace(String line) {
		StringTokenizer blank_tokens = new StringTokenizer(line, " ");

		blank_tokens.nextToken(); // skip the OBJ word

		if(line.contains("/")){			
			try {
	
				int[] a = cutSlash(blank_tokens.nextToken());
				int[] b = cutSlash(blank_tokens.nextToken());
				int[] c = cutSlash(blank_tokens.nextToken());
	
				Face3 face = new Face3(a, b, c);
				faceList.add(face);
	
			} catch (NumberFormatException e) {
				System.out.println(e.getMessage());
			}
		}else{	//Diese OBJ File hat keine Slashes. Annahme: die 3 gegebenen Int Werte sind vertexIndizes  
			try {
				
				int vertexIndex1 = Integer.parseInt(blank_tokens.nextToken());
				int vertexIndex2 = Integer.parseInt(blank_tokens.nextToken());
				int vertexIndex3 = Integer.parseInt(blank_tokens.nextToken());
				//System.out.println("index: " + vertexIndex1+vertexIndex2+vertexIndex3);
								
				int nullIndex[] = {1,1,1};
								
				//Die fehlenden Werte, TexKoord & NormalKoord, werden (erstmal) mit Nullvektoren besetzt 
				Face3 face = new Face3(nullIndex, nullIndex, nullIndex);
				face.setVertIndex(vertexIndex1, vertexIndex2, vertexIndex3);
				System.out.println("index: " + face);
				faceList.add(face);
	
			} catch (NumberFormatException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private int[] cutSlash(String str) {
		int[] token_arr = { 0, 0, 0 };

		StringTokenizer slash_tokens = new StringTokenizer(str, "/");

		/* If faces are given as 3 Integer numbers without slashes */
		if (slash_tokens.countTokens() <= 1) {
			token_arr[0] = Integer.parseInt(str);
			return token_arr;
		}

		token_arr[0] = Integer.parseInt(slash_tokens.nextToken());
		token_arr[1] = Integer.parseInt(slash_tokens.nextToken());

		/*
		 * If no texcoords are given -> // I assume the verts and the
		 * vertnormals are given -> v//vn
		 */
		if (slash_tokens.hasMoreTokens()) {
			token_arr[2] = Integer.parseInt(slash_tokens.nextToken());
		} else {
			token_arr[2] = token_arr[1];
			token_arr[1] = -1; //the normal index
		}

		return token_arr;
	}
	
	  private void centerScale()
	  /* Position the model so it's center is at the origin,
	     and scale it so its longest dimension is no bigger
	     than maxSize. */
	  {
	    // get the model's center point
	    Vert3 center = modeldims.getCenter();

	    // calculate a scale factor
	    double scaleFactor = 1.0;
	    double largest = modeldims.getLargest();
	    System.out.println("Largest dimension: " + largest);
	    if (largest != 0.0f)
	      scaleFactor = (maxSize / largest);
	    System.out.println("Scale factor: " + scaleFactor);

	    // modify the model's vertices
	    Vert3 vert;
	    double x, y, z;
	    for (int i = 0; i < vertexList.size(); i++) {
	      vert = (Vert3) vertexList.get(i);
	      x = (vert.getVertA() - center.getVertA()) * scaleFactor;
	      vert.setVertA(x);
	      y = (vert.getVertB() - center.getVertB()) * scaleFactor;
	      vert.setVertB(y);
	      z = (vert.getVertC() - center.getVertC()) * scaleFactor;
	      vert.setVertC(z);
	    }
	  } // end of centerScale()
	
	public boolean hasVT(){
		if(this.noVT)
			return false; 
		else 
			return true;
	}
	
	private void computeNormals() {
		
//		Will man anhand von Dreiecksdaten Normalen erzeugen: 
//		Das Vektor-Kreuzprodukt. Um die Normale der Fläche beschrieben durch die 3 Ecken V1, V2, V3 zu bekommen, braucht man nur zu rechnen:
//
//		N = (V1 - V2)x(V2 - V3)
//
//		Wenn wir das für jedes Polygon durchziehen, dann können wir für jeweils 3 Vertices die Normale angeben. 
//		Das schaut ein bisschen eckig aus, bringt uns unserem Ziel aber gewaltig näher.		
		
		//solange wie faceList lang ist
		for(int i = 0; i < faceList.size(); i++)
		{
			Vert3 v1 = new Vert3(0,0,0);
			Vert3 v2 = new Vert3(0,0,0);
			Vert3 v3 = new Vert3(0,0,0);
			Vert3 norm = new Vert3(0,0,0);
			
			int[] vertIndices = faceList.get(i).getFaceVerts();
			v1 = vertexList.get(vertIndices[0]);
			v2 = vertexList.get(vertIndices[1]);
			v3 = vertexList.get(vertIndices[2]);
			
			norm = Vert3.multiply((Vert3.minus(v1, v2)),(Vert3.minus(v2, v3)));
			
			faceList.get(i).setNormIndex(i);
			
			if(i%3 == 0){
				normalList.add(norm);
			}
				
		}

		
	}
}
