package com.dim.objviewer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.*;

import javax.media.opengl.GL;

import com.dim.halfEdgeStruct.Mesh;
import com.sun.opengl.util.BufferUtil;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BEncoderStream;

import java.util.regex.*;

/*
 * Fourth Revision! 12JAn2011
 * */

public class Model {
	// Buffer for the vertex array
	private DoubleBuffer vertexBuffer;
	// Buffer for the normal arrray
	private DoubleBuffer normalBuffer;
	// Buffer for the index array
	private IntBuffer indices;
	private DoubleBuffer texcoordBuffer;
	private String fileName = " ";

	private Mesh mesh = null;

	public static boolean DEBUG = false;
	public static int _ID = 1;
	private int id;

	private IntBuffer VBOVertices, VBONormals, VBOTexcoords, VBOIndices;

	// Array of vertices building the house
	private ArrayList<Vert3> vertexList = new ArrayList<Vert3>();
	private ArrayList<Vert3> normalList = new ArrayList<Vert3>();
	// deprecated
	private ArrayList<Face3> faceList = new ArrayList<Face3>();
	// new faceList is used in future
	private ArrayList<Facette> nfaceList = new ArrayList<Facette>();
	private ArrayList<Vert3> texcoordList = new ArrayList<Vert3>();

	public Pattern fpat = Pattern
			.compile("^f\\s+(\\d+)(?:/(\\d*))?(?:/(\\d+))?\\s+(\\d+)(?:/(\\d*))?(?:/(\\d+))?\\s+(\\d+)(?:/(\\d*))?(?:/(\\d+))?(?:\\s+(\\d+)(?:/(\\d*))?(?:/(\\d+))?)?$");
	public Pattern f2pat = Pattern.compile("^f\\s+(\\d+)\\s(\\d+)\\s(\\d+)");
	public Matcher fm;
	public Thread tr = null;

	public Model(GL gl, String path) {
		this.id = Model._ID++;
		this.fileName = path;

		loadModel(path);
		initModelDimension();		
		buildVBOS(gl);

		if (nfaceList.size() > 0) {
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

		int[][] faceArray = new int[nfaceList.size()][3];

		for (int i = 0; i < nfaceList.size(); i++) {
			faceArray[i][0] = nfaceList.get(i).getVertInd1();
			faceArray[i][1] = nfaceList.get(i).getVertInd2();
			faceArray[i][2] = nfaceList.get(i).getVertInd3();
		}

		this.mesh = new Mesh(faceArray);
		// new Thread(mesh).start();
		Thread thread = new Thread(mesh);
		this.tr = thread;
		thread.start();
	}

	public void initModelDimension(){		

			double maxX = vertexList.get(0).getVertA();
			double minX = vertexList.get(0).getVertA();
			double maxY = vertexList.get(0).getVertB();
			double minY = vertexList.get(0).getVertB();
			double maxZ = vertexList.get(0).getVertC();
			double minZ = vertexList.get(0).getVertC();
		
			double diffX = 0;
			double diffY = 0;
			double diffZ = 0;
			
			for(int i= 0; i < vertexList.size(); i++){
				if(vertexList.get(i).getVertA() > maxX){
					maxX = vertexList.get(i).getVertA();
				}
				
				else if(vertexList.get(i).getVertA() < minX){
					minX = vertexList.get(i).getVertA();					
				}
				
				else if(vertexList.get(i).getVertB() > maxY){
					maxY = vertexList.get(i).getVertB(); 
				}
				
				else if (vertexList.get(i).getVertB() < minY){
					minY = vertexList.get(i).getVertB();
				}
				
				else if (vertexList.get(i).getVertC() > maxZ){					
					maxZ = vertexList.get(i).getVertC();
				}
				
				else if (vertexList.get(i).getVertC() < minZ){
					minZ = vertexList.get(i).getVertC();
				}
				
			}
			System.out.println(maxX + "      " + minX);
			System.out.println(maxY + "      " + minY);		
			System.out.println(maxZ + "      " + minZ);		
			
			double xc = 0;
			double yc = 0;
			double zc = 0;
			
			if((maxX < 0 && minX < 0) || (maxX >= 0 && minX >= 0)){
				xc = (maxX + minX)/2;
				diffX = maxX - minX;
			}
				
			if((maxY < 0 && minY < 0) || (maxY >= 0 && minY >= 0)){
				yc = (maxY + minY)/2;	
				diffY = maxY - minY;
			}
			if((maxZ < 0 && minZ < 0) || (maxZ >= 0 && minZ >= 0)){
				zc = (maxZ + minZ)/2;	
				diffZ = maxZ - minZ;
			}
			
			if((maxX >= 0 && minX < 0) || (maxX < 0 && minX >= 0)){
				xc = (maxX + minX)/2;
				diffX = maxX - minX;
			}
				
			if((maxY >= 0 && minY < 0) || (maxY < 0 && minY >= 0)){
				yc = (maxY + minY)/2;	
				diffY = maxY - minY;
			}
			if((maxZ >= 0 && minZ < 0) || (maxZ < 0 && minZ >= 0)){
				zc = (maxZ + minZ)/2;
				diffZ = maxZ - minZ;
			}
			
			Vert3 center = new Vert3(xc, yc, zc);	
			 
	/*			System.out.println("xc: " + xc);
				System.out.println("yc: " + yc);
				System.out.println("zc: " + zc);
*/

			
			
			if (diffX < 0)
				diffX = diffX * (-1);
			
			if (diffY < 0)
				diffY = diffY * (-1);
			
			if (diffZ < 0)
				diffZ = diffZ * (-1);
			
			/*System.out.println("diffX: " + diffX);
			System.out.println("diffY: " + diffY);
			System.out.println("diffZ: " + diffZ);
			*/
			if(diffX >= diffY && diffX >= diffZ ){		//x ist am größten
				// ganze vertexList durch x
				diffX= 1/diffX;
				//scaleCenter(diffX , center);
				//wichtig ist dass man es auf 6 restkommastellen beschränkt
				unitVertex(diffX , center);
				System.out.println("X!!!" + diffX);
			}
			
			else if (diffY >= diffZ && diffY >= diffX){ 	//y ist am größten
				//ganze VertexList durch y
				diffY = 1/diffY;
				//scaleCenter(diffY , center);
				unitVertex(diffY , center);
				System.out.println("Y!!!" + diffY);
			}
			
			else if (diffZ >= diffX && diffZ >= diffY){	//z ist am größten
				//ganze VertexList durch z

				System.out.println("Z!!!!" + diffZ);
				System.out.println("center: " + center.getVertA());
				//scaleCenter(diffZ , center);
				diffZ = 1/diffZ;
				unitVertex(diffZ, center);
				System.out.println("center: " + center.getVertA());
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

	public void draw(GL gl, boolean mode) {
				
		if (mode) {
			drawInPickingMode(gl);
			return;
		}

		// Enable vertex arrays
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
		// gl.glEnableClientState(GL.GL_INDEX_ARRAY); //If enabled, the index
		// array is enabled for writing and used during rendering when
		// drawelemnts() is called

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices.get(0));
		gl.glVertexPointer(3, GL.GL_DOUBLE, 0, 0);

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOTexcoords.get(0));
		gl.glTexCoordPointer(2, GL.GL_DOUBLE, 0, 0);

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBONormals.get(0));
		gl.glNormalPointer(GL.GL_DOUBLE, 0, 0);

		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, VBOIndices.get(0));

		try {
			gl.glDrawElements(GL.GL_TRIANGLES, nfaceList.size() * 3,
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
	}

	/* Nur einmal laufen lassen möglich? */
	public void showHoles(GL gl) {

		if (mesh == null)
			return;

		if (!mesh.isReady())
			return;

		int[] bedgePnts = mesh.getBorderEdgePoints();
		if (bedgePnts.length <= 0) {
			System.out.println("Keine Randkanten!");
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
		gl.glColor3f(0.0f, 0.0f, 0.0f);
	}

	private void buildVBOS(GL gl) {

		vertexBuffer = BufferUtil.newDoubleBuffer(vertexList.size() * 3);
		normalBuffer = BufferUtil.newDoubleBuffer(vertexList.size() * 3);

		texcoordBuffer = BufferUtil.newDoubleBuffer(vertexList.size() * 2);
		indices = BufferUtil.newIntBuffer(nfaceList.size() * 3);

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

		for (int i = 0; i < nfaceList.size(); i++) {
			/* Filling in the normals */
			Facette face = nfaceList.get(i);

			int normalIndex = face.getNormInd1();
			int vertIndex = face.getVertInd1();
			Vert3 normal1 = normalList.get(normalIndex);
			dummyList.set(vertIndex, normal1);

			normalIndex = nfaceList.get(i).getNormInd2();
			vertIndex = face.getVertInd2();
			Vert3 normal2 = normalList.get(normalIndex);
			dummyList.set(vertIndex, normal2);

			normalIndex = nfaceList.get(i).getNormInd3();
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
		for (Facette f : nfaceList) {
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
				* BufferUtil.SIZEOF_DOUBLE, vertexBuffer, GL.GL_STATIC_DRAW); // nach
																				// resize
																				// einfach
																				// nochmal
																				// aufrufen
																				// um
																				// model
																				// zu
																				// lesen!!!

		// Generate and bind Normal Buffer
		VBONormals = BufferUtil.newIntBuffer(1);
		// Get a valid name
		gl.glGenBuffers(1, VBONormals);
		// Bind the Buffer
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBONormals.get(0));
		// Load the Data
		gl.glBufferData(GL.GL_ARRAY_BUFFER, normalList.size() * 3
				* BufferUtil.SIZEOF_DOUBLE, normalBuffer, GL.GL_STATIC_DRAW);

		// Generate and bind the Texture Coordinates Buffer
		VBOTexcoords = BufferUtil.newIntBuffer(1);
		// Get a valid name
		gl.glGenBuffers(1, VBOTexcoords);
		// Bind the Buffer
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOTexcoords.get(0));
		// Load the Data
		gl.glBufferData(GL.GL_ARRAY_BUFFER, texcoordList.size() * 2
				* BufferUtil.SIZEOF_DOUBLE, texcoordBuffer, GL.GL_STATIC_DRAW);

		VBOIndices = BufferUtil.newIntBuffer(1);
		gl.glGenBuffers(1, VBOIndices);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, VBOIndices.get(0));
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, nfaceList.size() * 3
				* BufferUtil.SIZEOF_INT, indices, GL.GL_STATIC_DRAW);

		if (ObjViewer.DEBUG) {
			printBuffer(vertexBuffer, " vertexBfr");
			printBuffer(normalBuffer, " normalBuffer");
			printBuffer(texcoordBuffer, " tex coordBfr");
			printList(nfaceList, " face List ");
			printBuffer(indices, " index Bfr ");
		}

		// this.debugPrintFaces();
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

			nfaceList.add(new Facette(nullArray, nullArray, vertsArray));
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

		if (ObjViewer.DEBUG) {
			// System.out.println(line);
			System.out.println("Normalenindizes: " + n1 + " " + n2 + " " + n3);
			System.out.println("Vertexindizes: " + vert1 + " " + vert2 + " "
					+ vert3);
			System.out.println("TexKoordinatenindizes: " + tc1 + " " + tc2
					+ " " + tc3);
		}

		int[] normInds = { n1, n2, n3 };
		int[] texInds = { tc1, tc2, tc3 };
		int[] vertInds = { vert1, vert2, vert3 };

		nfaceList.add(new Facette(normInds, texInds, vertInds));

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

		if (ObjViewer.DEBUG) {
			System.out.println("file read");
			System.out.println("var numFaces: " + numFaces);
			System.out.println("old numFaces: " + faceList.size());
			System.out.println("new numFaces: " + nfaceList.size());
		}
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

		// solange wie faceList lang ist
		for (int i = 0; i < nfaceList.size(); i++) {
			Vert3 v1 = new Vert3(0, 0, 0);
			Vert3 v2 = new Vert3(0, 0, 0);
			Vert3 v3 = new Vert3(0, 0, 0);
			Vert3 norm = new Vert3(0, 0, 0);

			int[] vertIndices = nfaceList.get(i).getVertsAsArray();

			v1 = vertexList.get(vertIndices[0]);
			v2 = vertexList.get(vertIndices[1]);
			v3 = vertexList.get(vertIndices[2]);

			norm = Vert3.cross((Vert3.minus(v2, v1)), (Vert3.minus(v3, v1)));
			// norm = Vert3.cross((Vert3.multiply(v2, v3)), (Vert3.multiply(v2,
			// v1)));
			// norm = Vert3.normalizeVector(norm);

			nfaceList.get(i).setNormIndex(i);

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

	public void drawInPickingMode(GL gl) {
		gl.glDisable(GL.GL_LIGHTING);
		gl.glDisable(GL.GL_DITHER);
		// float uni_color = (1.0f / (float) this._name);

		// System.out.println(uni_color + " " + this._name);
		gl.glColor3f(mapIdToColor(this.id), 0, 0);
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

		if (DEBUG) {
			printBuffer(VBOIndices, "indizee VBO");
		}

		try {
			gl.glDrawElements(GL.GL_TRIANGLES, nfaceList.size() * 3,
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
	 * Maps ID to color with splay so that colors are not similar
	 * 
	 * @param id
	 *            ID of Model-Object
	 * @return
	 */
	public float mapIdToColor(float id) {
		float color;

		System.out.println("\tcolor: " + (1 / id));

		return (1 / id);
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
			System.out.print(" " + al.get(i));
		}
		System.out.println(" Capacity: " + al.size());
	}

	public void debugPrintFaces() {
		System.out.print("{");
		for (Facette f : this.nfaceList) {
			System.out.print("{" + f.getVertInd1() + "," + f.getVertInd2()
					+ "," + f.getVertInd3() + "}, ");
		}
		System.out.print("}");
	}
}
