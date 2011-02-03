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

	private Mesh mesh = null;
	
	public static boolean DEBUG = false;

	private IntBuffer VBOVertices, VBONormals, VBOTexcoords, VBOIndices;


	// Array of vertices building the house
	private ArrayList<Vert3> vertexList = new ArrayList<Vert3>();
	private ArrayList<Vert3> normalList = new ArrayList<Vert3>();
	// deprecated
	private ArrayList<Face3> faceList = new ArrayList<Face3>();
	// new faceList is used in future
	private ArrayList<Facette> nfaceList = new ArrayList<Facette>();
	private ArrayList<Vert3> texcoordList = new ArrayList<Vert3>();
	

	public Pattern fpat = Pattern.compile("^f\\s+(\\d+)(?:/(\\d*))?(?:/(\\d+))?\\s+(\\d+)(?:/(\\d*))?(?:/(\\d+))?\\s+(\\d+)(?:/(\\d*))?(?:/(\\d+))?(?:\\s+(\\d+)(?:/(\\d*))?(?:/(\\d+))?)?$");
	public Pattern f2pat = Pattern.compile("^f\\s+(\\d+)\\s(\\d+)\\s(\\d+)");
	public Matcher fm;

	private ModelDimensions modeldims = new ModelDimensions();
	private double maxSize;

	public Model(GL gl) {
		loadModel("models/cube");
		buildVBOS(gl);
		//centerScale();

		if (nfaceList.size() > 0)
			System.out.println("mesh..");//createHalfEdgeMesh();
		else
			System.out.println("FaceList unfilled - Mesh not created!");
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
		new Thread(mesh).start();
	}

	/*
	 * Draw the house.
	 */
	public void draw(GL gl) {
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

		if (DEBUG) {
			printBuffer(VBOIndices, "indizee VBO");
		}
		
		try {
			gl.glDrawElements(GL.GL_TRIANGLES, nfaceList.size() * 3, GL.GL_UNSIGNED_INT, 0);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		//this.showHoles(gl);

		// unbind?!
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);

		// Disable vertex arrays
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
	}

	/*Nur einmal laufen lassen möglich?*/
	public void showHoles(GL gl) {

		if (mesh == null)
			return;

		if (!mesh.isReady())
			return;

		int[] bedgePnts = mesh.getBorderEdgePoints();
		if (bedgePnts.length <= 0) {
			//System.out.println("Keine Randkanten!");
			return;
		}

		gl.glColor3i(1, 0, 0);
		gl.glLineWidth(3.0f);

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

		gl.glColor3i(0, 0, 0);
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

		String filename = name + ".obj";

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
		
		/*Wenn das Face nur Indizees enthält -> f 1 2 3
		 *die Methode v. Cornette versagt sonst an dieser Stelle*/
		if (fm2.find()){
			int v1 = Integer.parseInt(fm2.group(1));
			int v2 = Integer.parseInt(fm2.group(2));
			int v3 = Integer.parseInt(fm2.group(3));
			int[] vertsArray = {v1,v2,v3};
			int[] nullArray = {1,1,1}; //weil indizee im Konstruktor dekrementiert wird!
						
			nfaceList.add(new Facette(nullArray,nullArray , vertsArray));
			return;
		}
			
		
		
		String tcs0 = fm.group(1);
		String tc1s = fm.group(2);
		String tc2s = fm.group(5);
		String tc3s = fm.group(8);
		String tc4s = fm.group(11);

		
		boolean hastexcoords = ((!tc1s.equals("")) && (!tc2s.equals(""))
				&& (!tc3s.equals("")) && ((tc4s == null) || (!tc4s.equals(""))));
		
				
		String	n1s = fm.group(3);
		String	n2s = fm.group(6);
		String	n3s = fm.group(9);
		String	n4s = fm.group(12);
				
		
		boolean	hasnormals = ((!n1s.equals("")) && (!n2s.equals(""))
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

	private void centerScale()
	/*
	 * Position the model so it's center is at the origin, and scale it so its
	 * longest dimension is no bigger than maxSize.
	 */
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

	private void computeNormals() {
//		for(Vert3 v : vertexList){
//			normalList.add(new Vert3());
//		}
//		
//		return;
		// Will man anhand von Dreiecksdaten Normalen erzeugen:
		// Das Vektor-Kreuzprodukt. Um die Normale der Fläche beschrieben durch
		// die 3 Ecken V1, V2, V3 zu bekommen, braucht man nur zu rechnen:
		//
		// N = (V1 - V2)x(V2 - V3)
		//
		// Wenn wir das für jedes Polygon durchziehen, dann können wir für
		// jeweils 3 Vertices die Normale angeben.
		// Das schaut ein bisschen eckig aus, bringt uns unserem Ziel aber
		// gewaltig näher.
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
			//norm = Vert3.cross((Vert3.multiply(v2, v3)), (Vert3.multiply(v2, v1)));
			//norm = Vert3.normalizeVector(norm);

			nfaceList.get(i).setNormIndex(i);

			
			normalList.add(norm);
			normalList.add(norm);
			normalList.add(norm);
			
		 
		}
		
		System.out.println("Normals Computed!");
		 
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
