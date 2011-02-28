package com.dim.objviewer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.*;

import javax.media.opengl.GL;

import com.dim.halfEdgeStruct.Mesh;
import com.dim.sceneGraph.SceneGraph;
import com.sun.opengl.util.BufferUtil;

import java.util.regex.*;


public class Model {
	// Buffer for the vertex array
	private DoubleBuffer vertexBuffer;
	// Buffer for the normal arrray
	private DoubleBuffer normalBuffer;
	// Buffer for the index array
	private IntBuffer indices;
	private DoubleBuffer texcoordBuffer;
	public String fileName = " ";

	private Mesh mesh = null;
	
	public static int _ID = 1;
	public int id;
	private static Model modelRef;

	private IntBuffer VBOVertices, VBONormals, VBOTexcoords, VBOIndices;

	public static Model getModelref() {
		return modelRef;
	}

	// Array of vertices building the house
	public ArrayList<Vert3> vertexList = new ArrayList<Vert3>();
	private ArrayList<Vert3> normalList = new ArrayList<Vert3>();
	private ArrayList<Facette> faceList = new ArrayList<Facette>();
	private ArrayList<Vert3> texcoordList = new ArrayList<Vert3>();

	public Pattern fpat = Pattern
			.compile("^f\\s+(\\d+)(?:/(\\d*))?(?:/(\\d+))?\\s+(\\d+)(?:/(\\d*))?(?:/(\\d+))?\\s+(\\d+)(?:/(\\d*))?(?:/(\\d+))?(?:\\s+(\\d+)(?:/(\\d*))?(?:/(\\d+))?)?$");
	public Pattern f2pat = Pattern.compile("^f\\s+(\\d+)\\s(\\d+)\\s(\\d+)");
	public Matcher fm;

	public boolean vertexListisTouched = false;

	public Model(GL gl, String path) {
		Model.modelRef = this;
		this.id = Model._ID++;
		this.fileName = path;

		loadModel(path);
		initModelDimension();
		buildVBOS(gl);

		if (faceList.size() > 0) {
			createHalfEdgeMesh();
		} else {
			System.out.println("FaceList unfilled - Mesh not created!");
		}
	}

	public String getFileName() {
		return this.fileName;
	}

	public void createHalfEdgeMesh() {
		/*
		 * Der Konstruktor von Mesh benötigt ein Array der Faces der Form {
		 * {1,2,3} , {2,3,4} ... etc } in diese Form wird die Face-Liste
		 * gebracht und übergeben.
		 */

		int[][] faceArray = new int[faceList.size()][3];

		for (int i = 0; i < faceList.size(); i++) {
			faceArray[i][0] = faceList.get(i).getVertInd1();
			faceArray[i][1] = faceList.get(i).getVertInd2();
			faceArray[i][2] = faceList.get(i).getVertInd3();
		}

		this.mesh = new Mesh(faceArray);
		// new Thread(mesh).start();
		Thread thread = new Thread(mesh);
		thread.start();
	}

	public void initModelDimension() {

		double maxX = vertexList.get(0).getVertA();
		double minX = vertexList.get(0).getVertA();
		double maxY = vertexList.get(0).getVertB();
		double minY = vertexList.get(0).getVertB();
		double maxZ = vertexList.get(0).getVertC();
		double minZ = vertexList.get(0).getVertC();

		double diffX = 0;
		double diffY = 0;
		double diffZ = 0;

		for (int i = 0; i < vertexList.size(); i++) {
			if (vertexList.get(i).getVertA() > maxX) {
				maxX = vertexList.get(i).getVertA();
			}

			else if (vertexList.get(i).getVertA() < minX) {
				minX = vertexList.get(i).getVertA();
			}

			else if (vertexList.get(i).getVertB() > maxY) {
				maxY = vertexList.get(i).getVertB();
			}

			else if (vertexList.get(i).getVertB() < minY) {
				minY = vertexList.get(i).getVertB();
			}

			else if (vertexList.get(i).getVertC() > maxZ) {
				maxZ = vertexList.get(i).getVertC();
			}

			else if (vertexList.get(i).getVertC() < minZ) {
				minZ = vertexList.get(i).getVertC();
			}

		}
		
		double xc = 0;
		double yc = 0;
		double zc = 0;

		if ((maxX < 0 && minX < 0) || (maxX >= 0 && minX >= 0)) {
			xc = (maxX + minX) / 2;
			diffX = maxX - minX;
		}

		if ((maxY < 0 && minY < 0) || (maxY >= 0 && minY >= 0)) {
			yc = (maxY + minY) / 2;
			diffY = maxY - minY;
		}
		if ((maxZ < 0 && minZ < 0) || (maxZ >= 0 && minZ >= 0)) {
			zc = (maxZ + minZ) / 2;
			diffZ = maxZ - minZ;
		}

		if ((maxX >= 0 && minX < 0) || (maxX < 0 && minX >= 0)) {
			xc = (maxX + minX) / 2;
			diffX = maxX - minX;
		}

		if ((maxY >= 0 && minY < 0) || (maxY < 0 && minY >= 0)) {
			yc = (maxY + minY) / 2;
			diffY = maxY - minY;
		}
		if ((maxZ >= 0 && minZ < 0) || (maxZ < 0 && minZ >= 0)) {
			zc = (maxZ + minZ) / 2;
			diffZ = maxZ - minZ;
		}

		Vert3 center = new Vert3(xc, yc, zc);

		if (diffX < 0)
			diffX = diffX * (-1);

		if (diffY < 0)
			diffY = diffY * (-1);

		if (diffZ < 0)
			diffZ = diffZ * (-1);

		
		if (diffX >= diffY && diffX >= diffZ) { // x ist am größten
			// ganze vertexList durch x
			diffX = 1 / diffX;
			// scaleCenter(diffX , center);
			// wichtig ist dass man es auf 6 restkommastellen beschränkt
			unitVertex(diffX, center);
			System.out.println("X!!!" + diffX);
		}

		else if (diffY >= diffZ && diffY >= diffX) { // y ist am größten
			// ganze VertexList durch y
			diffY = 1 / diffY;
			// scaleCenter(diffY , center);
			unitVertex(diffY, center);
			System.out.println("Y!!!" + diffY);
		}

		else if (diffZ >= diffX && diffZ >= diffY) { // z ist am größten
			// scaleCenter(diffZ , center);
			diffZ = 1 / diffZ;
			unitVertex(diffZ, center);
		}

	}

	private void unitVertex(double factor, Vert3 center) {

		double helpx = 0;
		double helpy = 0;
		double helpz = 0;

		for (int i = 0; i < vertexList.size(); i++) {
			helpx = (vertexList.get(i).getVertA() - center.getVertA()) * factor;
			helpy = (vertexList.get(i).getVertB() - center.getVertB()) * factor;
			helpz = (vertexList.get(i).getVertC() - center.getVertC()) * factor;

			Vert3 element = new Vert3(helpx, helpy, helpz);

			vertexList.set(i, element);

		}

	}

	/**
	 * Using the umbrella operator to smooth mesh at the specified point
	 * 
	 * @param vertIndex
	 *            Index (in vertexBuffer) of vertex
	 */
	private Vert3 smoothVert(int toSmoothVertIndex) {
		// Check adjacent Verts for null
		if (mesh.getAdjacentVertIndeces(toSmoothVertIndex) == null) {
			System.out.println("Vert in Point " + toSmoothVertIndex
					+ " was either on border or invalid.");
			return null;
		}

		int[] vertIndices = mesh.getAdjacentVertIndeces(toSmoothVertIndex); // sind
		// vertIndices		
		Vert3 sum = new Vert3();
		double sumWeights = 0.0;

		// Inner sum of Ug(p)
		for (int i = 0; i < vertIndices.length; i++) {
			// sum of Wi * pi - p
			double wi = getWeighting(vertexList.get(vertIndices[i]), vertexList
					.get(toSmoothVertIndex));
			sumWeights += wi;
			Vert3 foo = Vert3.multiply(wi, vertexList.get(vertIndices[i]));
			sum = Vert3.plus(sum, foo);
		}

		double invWeights = (1 / sumWeights);

		Vert3 direction = Vert3.minus(Vert3.multiply(invWeights, sum),
				vertexList.get(toSmoothVertIndex));
		return direction;
	}

	public void smoothVerts() {
		int i = 0;
		Vert3 smoothed = new Vert3();

		ArrayList<Vert3> tempVertList = new ArrayList<Vert3>();

		for (Vert3 v : vertexList) {
			// System.out.println(i++ + ": " +v);
			tempVertList.add(v);
		}

		System.out.println("-------");
		for (Vert3 v : tempVertList) {
			smoothed = smoothVert(i);
			if (smoothed == null) {
				i++;
				continue;
			}
			System.out.println("old Vert: " + v + "\nnew Vert smoothed: "
					+ smoothed);
			Vert3 smoothedP = Vert3.plus(smoothed, vertexList.get(i));
			tempVertList.set(i, smoothedP);
			i++;
		}
		System.out.println("-------");

		// flush templist in vertexList
		vertexList.clear();
		for (Vert3 v : tempVertList) {
			vertexList.add(v);
		}
		
		tempVertList.clear();

		vertexListisTouched = true;

	}

	/**
	 * Computes the distance between two Verts
	 * 
	 * @param p
	 * @param q
	 */
	private double getWeighting(Vert3 p, Vert3 q) {
		Vert3 helpVert = Vert3.minus(p, q);
		double weight = Math.pow(helpVert.getVertA(), 2)
				+ Math.pow(helpVert.getVertB(), 2)
				+ Math.pow(helpVert.getVertC(), 2);
		weight = Math.sqrt(weight);

		return weight;
	}

	/**
	 * Draw the Model considering the drawing mode which is either
	 * "model is picked" or not
	 * 
	 * @param gl
	 * @param mode
	 *            indicates if model should be drawn in picking mode (In solid
	 *            colors for color Picking)
	 */
	public void draw(GL gl, boolean solidColorMode, boolean pickedMode) {

		// If vertexList is modified (e.g. smoothing) the VBOs must be rebuild
		if (vertexListisTouched)
			buildVBOS(gl);

		
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		// Enable vertex arrays
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
		//gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
		


		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices.get(0));
		gl.glVertexPointer(3, GL.GL_DOUBLE, 0, 0);

		//gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOTexcoords.get(0));
		//gl.glTexCoordPointer(2, GL.GL_DOUBLE, 0, 0);

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBONormals.get(0));
		gl.glNormalPointer(GL.GL_DOUBLE, 0, 0);

		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, VBOIndices.get(0));

		
		gl.glUseProgram(0);
		
		//if drawn in solid color mode, lights and dithering are switched off 
		if (solidColorMode) {
			gl.glDisable(GL.GL_LIGHTING);
			gl.glDisable(GL.GL_DITHER);
			gl.glColor3f(mapIdToColor(this.id)[0], mapIdToColor(this.id)[1], mapIdToColor(this.id)[2]);
			
			try {
				gl.glDrawElements(GL.GL_TRIANGLES, faceList.size() * 3,
						GL.GL_UNSIGNED_INT, 0);

			} catch (Exception e) {
				System.out.println("GL Error: " + gl.glGetError());
				System.out.println(e.getMessage());
			}
			gl.glEnable(GL.GL_LIGHTING);
			gl.glEnable(GL.GL_DITHER);
		} else {
			if(pickedMode){				
				gl.glEnable(GL.GL_LIGHTING);
				gl.glColor3f(1, 0, 1);
			}
			else{
				if(!SceneGraph.getSceneGraphRef().wireframeMode)
					gl.glColor3f(1, 1, 1);
			}
			try {				
				gl.glDrawElements(GL.GL_TRIANGLES, faceList.size() * 3,
						GL.GL_UNSIGNED_INT, 0);
			} catch (Exception e) {
				System.out.println("GL Error: " + gl.glGetError());
				System.out.println(e.getMessage());
			}			
				
		}
		
		

		//gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);

		// Disable vertex arrays
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
		//gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
		
	}

	/* Nur einmal laufen lassen möglich? */
	/**
	 * @param gl
	 */
	public void highLightHoles(GL gl) {

		if (mesh == null){
			System.out.println("Mesh not initialized jet!");
			return;
		}

		if (!mesh.isReady()){
			System.out.println("Mesh construction not ready jet!");
			return;
		}

		// auslagern muss nciht jedes mal aufgerufen werden -> wegspoeichern
		// einmal!
		int[] bedgePnts = mesh.getBorderEdgePoints();

		if (bedgePnts.length <= 0) {
			return;
		}

		gl.glColor3f(1.0f, 0.0f, 0.0f);
		gl.glLineWidth(3.0f);
		gl.glDisable(GL.GL_LIGHTING);
		gl.glBegin(GL.GL_LINES);
		for (int i = 0; i < bedgePnts.length; i = i + 2) {
			gl.glVertex3d(vertexList.get(bedgePnts[i]).getVertA(), vertexList
					.get(bedgePnts[i]).getVertB(), vertexList.get(bedgePnts[i])
					.getVertC());
			gl.glVertex3d(vertexList.get(bedgePnts[i + 1]).getVertA(),
					vertexList.get(bedgePnts[i + 1]).getVertB(), vertexList
							.get(bedgePnts[i + 1]).getVertC());
		}
		gl.glEnd();
		gl.glEnable(GL.GL_LIGHTING);
		
	}

	private void buildVBOS(GL gl) {

		vertexListisTouched = false;

		vertexBuffer = BufferUtil.newDoubleBuffer(vertexList.size() * 3);
		normalBuffer = BufferUtil.newDoubleBuffer(vertexList.size() * 3);

		texcoordBuffer = BufferUtil.newDoubleBuffer(vertexList.size() * 2);
		indices = BufferUtil.newIntBuffer(faceList.size() * 3);

		for (int i = 0; i < vertexList.size(); i++) {
			Vert3 vert = vertexList.get(i);
			vertexBuffer.put(vert.getVert());
		}

		/*
		 * Dummylist wird verwendet da der Umgang mit ArrayList einfacher ist
		 * (im Speziellen das nicht sequentielle Füllen) als mit dem Buffer Im
		 * Anschluss wird die Liste in den normalBuffer übertragen und für den
		 * GC freigegeben
		 */
		ArrayList<Vert3> dummyList = new ArrayList<Vert3>();
		// Aufblasen d. Liste für asequentielles befüllen
		for (int i = 0; i < vertexList.size(); i++)
			dummyList.add(new Vert3());

		for (int i = 0; i < faceList.size(); i++) {
			/* Filling in the normals */
			Facette face = faceList.get(i);

			int normalIndex = face.getNormInd1();
			int vertIndex = face.getVertInd1();
			Vert3 normal1 = normalList.get(normalIndex);
			dummyList.set(vertIndex, normal1);

			normalIndex = faceList.get(i).getNormInd2();
			vertIndex = face.getVertInd2();
			Vert3 normal2 = normalList.get(normalIndex);
			dummyList.set(vertIndex, normal2);

			normalIndex = faceList.get(i).getNormInd3();
			vertIndex = face.getVertInd3();
			Vert3 normal3 = normalList.get(normalIndex);
			dummyList.set(vertIndex, normal3);
		}

		/*
		 * Übertragen der DummyListe in den NormalBuffer
		 */
		for (Vert3 v : dummyList) {
			normalBuffer.put(v.getVert());
		}
		// Freigabe für GC
		dummyList.clear();

		/*
		 * Texcoords übertragen - TODO! nicht implementiert bisher werden nur
		 * null Vert3 eingefügt
		 */
		if (texcoordList.size() < vertexList.size() || texcoordList.size() == 0) {
			texcoordList.clear();
			for (int i = 0; i < vertexList.size(); i++)
				texcoordList.add(new Vert3());
		}

		for (Vert3 v : texcoordList) {
			double a = v.getVertA();
			double b = v.getVertB();
			texcoordBuffer.put(a);
			texcoordBuffer.put(b);
		}

		/*
		 * Indicees
		 */
		for (Facette f : faceList) {
			/*
			 * Indizees eines Face eintragen
			 */
			indices.put(f.getVertInd1());
			indices.put(f.getVertInd2());
			indices.put(f.getVertInd3());
		}

		// Rewind all buffers
		vertexBuffer.rewind();
		normalBuffer.rewind();
		texcoordBuffer.rewind();
		indices.rewind();

		VBOVertices = BufferUtil.newIntBuffer(1);
		// Get a valid name
		gl.glGenBuffers(1, VBOVertices);
		// Bind the Buffer
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices.get(0));
		// Load the Data
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexList.size() * 3
				* BufferUtil.SIZEOF_DOUBLE, vertexBuffer, GL.GL_STATIC_DRAW);

		// Generate and bind Normal Buffer
		VBONormals = BufferUtil.newIntBuffer(1);
		// Get a valid name
		gl.glGenBuffers(1, VBONormals);
		// Bind the Buffer
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBONormals.get(0));
		// Load the Data
		gl.glBufferData(GL.GL_ARRAY_BUFFER, normalList.size() * 3
				* BufferUtil.SIZEOF_DOUBLE, normalBuffer, GL.GL_STATIC_DRAW);

//		// Generate and bind the Texture Coordinates Buffer
//		VBOTexcoords = BufferUtil.newIntBuffer(1);
//		// Get a valid name
//		gl.glGenBuffers(1, VBOTexcoords);
//		// Bind the Buffer
//		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOTexcoords.get(0));
//		// Load the Data
//		gl.glBufferData(GL.GL_ARRAY_BUFFER, texcoordList.size() * 2
//				* BufferUtil.SIZEOF_DOUBLE, texcoordBuffer, GL.GL_STATIC_DRAW);

		VBOIndices = BufferUtil.newIntBuffer(1);
		gl.glGenBuffers(1, VBOIndices);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, VBOIndices.get(0));
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, faceList.size() * 3
				* BufferUtil.SIZEOF_INT, indices, GL.GL_STATIC_DRAW);

	}

	private void loadModel(String name) {

		String filename = name;

		try {
			System.out.println("Loading model from " + filename + " ...");
			BufferedReader br = new BufferedReader(new FileReader(filename));

			parseOBJFile(br);

			br.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

	}

	/**
	 * @author David Cornette moded by D. Martens
	 * 
	 */
	public void faceRegExpDeterm(String line) {

		Matcher fm2 = f2pat.matcher(line);

		/*
		 * Wenn das Face nur Indizees enthält -> f 1 2 3die Methode v. Cornette
		 * versagt sonst an dieser Stelle
		 */
		if (fm2.find()) {
			int v1 = Integer.parseInt(fm2.group(1));
			int v2 = Integer.parseInt(fm2.group(2));
			int v3 = Integer.parseInt(fm2.group(3));
			int[] vertsArray = { v1, v2, v3 };
			int[] nullArray = { 1, 1, 1 }; // weil indizee im Konstruktor
			// dekrementiert wird!

			faceList.add(new Facette(nullArray, nullArray, vertsArray));
			return;
		}

		String tcs0 = fm.group(1);
		String tc1s = fm.group(2);
		String tc2s = fm.group(5);
		String tc3s = fm.group(8);
		String tc4s = fm.group(11);

		boolean hastexcoords = ((!tc1s.equals("")) && (!tc2s.equals(""))
				&& (!tc3s.equals("")) && ((tc4s == null) || (!tc4s.equals(""))));

		String n1s = fm.group(3);
		String n2s = fm.group(6);
		String n3s = fm.group(9);
		String n4s = fm.group(12);

		boolean hasnormals = ((!n1s.equals("")) && (!n2s.equals(""))
				&& (!n3s.equals("")) && ((n4s == null) || (!n4s.equals(""))));

		int vert1 = Integer.parseInt(fm.group(1));
		int vert2 = Integer.parseInt(fm.group(4));
		int vert3 = Integer.parseInt(fm.group(7));
		int vert4 = 0;

		if (fm.group(11) != null) {
			vert4 = Integer.parseInt(fm.group(10));
		}

		int tc1 = 0, tc2 = 0, tc3 = 0, tc4 = 0;
		if (hastexcoords) {
			tc1 = Integer.parseInt(tc1s);
			tc2 = Integer.parseInt(tc2s);
			tc3 = Integer.parseInt(tc3s);
			if (tc4s != null) {
				tc4 = Integer.parseInt(tc4s);
			}
		}

		int n1 = 0, n2 = 0, n3 = 0, n4 = 0;
		if (hasnormals) {
			n1 = Integer.parseInt(n1s);
			n2 = Integer.parseInt(n2s);
			n3 = Integer.parseInt(n3s);
			if (n4s != null) {
				n4 = Integer.parseInt(n4s);
			}
		}


		int[] normInds = { n1, n2, n3 };
		int[] texInds = { tc1, tc2, tc3 };
		int[] vertInds = { vert1, vert2, vert3 };

		faceList.add(new Facette(normInds, texInds, vertInds));

	}

	private void parseOBJFile(BufferedReader br)
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
						extractTexCord(line);// not supported yet
					} else if (line.startsWith("vn")) // normal
						extractNormal(line);
					else if (line.startsWith("f ")) { // face
						fm = fpat.matcher(line);
						if (fm.find())
							faceRegExpDeterm(line);

					} else if (line.startsWith("#")) // comment line
						System.out.println("comment: " + line);
					else
						System.out.println("Ignoring line " + lineNum + " : "
								+ line);
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
		texcoordList.add(new Vert3(0, 0, 0));
	}

	private void computeNormals() {
		// Will man anhand von Dreiecksdaten Normalen erzeugen:
		// Das Vektor-Kreuzprodukt. Um die Normale der Fläche beschrieben durch
		// die 3 Ecken V1, V2, V3 zu bekommen, braucht man nur zu rechnen:
		//
		// N = (V1 - V2)x(V2 - V3)
		//
		// Wenn wir das für jedes Polygon durchziehen, dann können wir für
		// jeweils 3 Vertices die Normale angeben.
		System.out.println("Computing the Normals!");

		// so oft wie faceList lang ist
		for (int i = 0; i < faceList.size(); i++) {
			Vert3 v1 = new Vert3(0, 0, 0);
			Vert3 v2 = new Vert3(0, 0, 0);
			Vert3 v3 = new Vert3(0, 0, 0);
			Vert3 norm = new Vert3(0, 0, 0);

			int[] vertIndices = faceList.get(i).getVertsAsArray();

			v1 = vertexList.get(vertIndices[0]);
			v2 = vertexList.get(vertIndices[1]);
			v3 = vertexList.get(vertIndices[2]);

			norm = Vert3.cross((Vert3.minus(v2, v1)), (Vert3.minus(v3, v1)));			

			faceList.get(i).setNormIndex(i);

			normalList.add(norm);
			normalList.add(norm);
			normalList.add(norm);

		}

		System.out.println("Normals Computed!");

	}

	/*
	 * ##########################################################################
	 * ########## Picking related methods
	 */

	/**
	 * Draws the Model in solid colors for color Picking. A redraw with normal
	 * lighting is triggert afterwards
	 */
	public void drawInSolidColorMode(GL gl) {
		gl.glDisable(GL.GL_LIGHTING);
		gl.glDisable(GL.GL_DITHER);
		// float uni_color = (1.0f / (float) this._name);

		// System.out.println(uni_color + " " + this._name);
		gl.glColor3f(mapIdToColor(this.id)[0], mapIdToColor(this.id)[1],
				mapIdToColor(this.id)[2]);
		// Enable vertex arrays
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices.get(0));
		gl.glVertexPointer(3, GL.GL_DOUBLE, 0, 0);

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOTexcoords.get(0));
		gl.glTexCoordPointer(2, GL.GL_DOUBLE, 0, 0);

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBONormals.get(0));
		gl.glNormalPointer(GL.GL_DOUBLE, 0, 0);

		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, VBOIndices.get(0));



		try {
			gl.glDrawElements(GL.GL_TRIANGLES, faceList.size() * 3,
					GL.GL_UNSIGNED_INT, 0);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// unbind?!
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);

		// Disable vertex arrays
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);

		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_DITHER);
	}

	/**
	 * Maps ID to color
	 * 
	 * @param id
	 *            ID of Model-Object
	 * @return
	 */
	public float[] mapIdToColor(int id) {

		float[] color = new float[4];
		color[0] = (255 & (id)) / 255f;
		color[1] = (255 & ((id) >> 8)) / 255f;
		color[2] = (255 & ((id) >> 16)) / 255f;
		color[3] = 1.0f;

		//System.out.println("    from id:" + id + "color: " + color[0] + " " + color[1] + " " + color[2]);

		return color;
	}

	/*
	 * ##########################################################################
	 * ########################## DEBUGING METHODS
	 */

	public static void printBuffer(DoubleBuffer db, String name) {
		if (name == null)
			name = "init";

		System.out.println("Buffer" + name + " wird ausgegeben");

		for (int i = 0; i < db.capacity(); i++) {
			System.out.print(" " + db.get(i));
			if ((i + 1) % 3 == 0)
				System.out.print(" | ");
		}
		System.out.println(" Capacity: " + db.capacity());
	}

	public static void printBuffer(IntBuffer db, String name) {
		if (name == null)
			name = "init";

		System.out.println("Buffer" + name + " wird ausgegeben");

		for (int i = 0; i < db.capacity(); i++) {
			System.out.print(" " + db.get(i));
			if ((i + 1) % 3 == 0)
				System.out.print(" | ");
		}
		System.out.println(" Capacity: " + db.capacity());
	}

	public static void printList(ArrayList al, String name) {
		if (name == null)
			name = "init";

		System.out.println("Liste " + name + " wird ausgegeben");

		for (int i = 0; i < al.size(); i++) {
			System.out.print("\n" + al.get(i));
		}
		System.out.println(" Capacity: " + al.size());
	}

	public void debugPrintFaces() {
		System.out.print("{");
		for (Facette f : this.faceList) {
			System.out.print("{" + f.getVertInd1() + "," + f.getVertInd2()
					+ "," + f.getVertInd3() + "}, ");
		}
		System.out.print("}");
	}
}
