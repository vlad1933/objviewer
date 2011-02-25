/**
 * Big 2Dos:
 * 	keine wireframe
 * skalieren können
 * normalen berechnen
 * initiale position nach drehung abfangen 
 */
package com.dim.objviewer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.StringTokenizer;

import com.dim.ArcBall.*;
import com.dim.halfEdgeStruct.Mesh;
import com.dim.sceneGraph.SceneGraph;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.StreamUtil;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Woodstox;

public class ObjViewer extends JFrame implements GLEventListener {
	private static final long serialVersionUID = 1L;

	public float WINWIDTH = 1000.0f;
	public float WINHEIGHT = 800.0f;

	/*
	 * RENDERING STATES
	 */
	public boolean highlightMeshHoles = false;
	public boolean PICKED = false;

	private Plain plain;
	private GLCanvas canvas = null;

	public Shading shadingData;

	public float scaling = 0.01f;
	public float lastScale = -1.0f;

	public int shaderprogram;
	public GL gl;

	private SceneGraph sceneGraph;
	private Point lastPickPoint = null;

	/*
	 * ARcBALL
	 */	
	private Matrix4f LastRot = new Matrix4f();
	private Matrix4f ThisRot = new Matrix4f();
	private final Object matrixLock = new Object();
	private float[] matrix = new float[16];

	public ArcBall arcBall = new ArcBall(this.WINWIDTH, this.WINHEIGHT);

	public ObjViewer() {
		super("Dimitr Martens - 3D Computergrafik");
		
		canvas = new GLCanvas();
		
		shadingData = new Shading();

		canvas.addGLEventListener(this);
		canvas.addKeyListener(new MyKeyListener(this));
		canvas.addMouseListener(new MyMouseListener(this));
		canvas.addMouseMotionListener(new MyMouseMotionListener(this));
		canvas.addMouseWheelListener(new MyMouseWheelListener(this));

		//InitUI inits the UI and returns a JMenuBar Object
		this.add(initUI(), BorderLayout.NORTH);
		
		this.add(canvas, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public void init(GLAutoDrawable drawable) {

		GL gl = drawable.getGL();
		this.gl = gl;

		sceneGraph = new SceneGraph(this);

		// Set background color
		gl.glClearColor(0.3F, 0.3F, 0.3F, 1.0F);
		gl.glEnable(GL.GL_LIGHTING);
		// Switch on light0
		gl.glEnable(GL.GL_LIGHT0);
		// Enable automatic normalization of normals
		gl.glEnable(GL.GL_NORMALIZE);
		// Enable z-Buffer test
		gl.glEnable(GL.GL_DEPTH_TEST);
		// Enable color material
		gl.glEnable(GL.GL_COLOR_MATERIAL);

		//Bottom plain
		this.plain = new Plain(gl);

		//scaling factor which is used for scaling the models
		scaling = 1.0f;

		// ArcBall: Start Of User Initialization
		LastRot.setIdentity(); // Reset Rotation
		ThisRot.setIdentity(); // Reset Rotation
		ThisRot.get(matrix);

		gl.glDepthFunc(GL.GL_LEQUAL); // The Type Of Depth Testing (Less Or Equal)
		gl.glEnable(GL.GL_DEPTH_TEST); // Enable Depth Testing
		// Set Perspective Calculations To Most Accurate
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

	}

		
	// ######################## ARCBALL METHODS ########################################
	void reset() {
		synchronized (matrixLock) {
			LastRot.setIdentity(); // Reset Rotation
			ThisRot.setIdentity(); // Reset Rotation
		}
	}

	void startDrag(Point MousePt) {
		synchronized (matrixLock) {
			LastRot.set(ThisRot); // Set Last Static Rotation To Last Dynamic
									// One
		}
		arcBall.click(MousePt); // Update Start Vector And Prepare For Dragging
	}

	void drag(Point MousePt) // Perform Motion Updates Here
	{
		Quat4f_t ThisQuat = new Quat4f_t();

		// Update End Vector And Get Rotation As Quaternion
		arcBall.drag(MousePt, ThisQuat);
		synchronized (matrixLock) {
			ThisRot.setRotation(ThisQuat); // Convert Quaternion Into Matrix3fT
			ThisRot.mul(ThisRot, LastRot); // Accumulate Last Rotation Into This
											// One
		}
	}

	
	// ######################## END ARCBALL METHODS ###########################################
	
	
	public String readFile(File file) {

		StringBuffer fileBuffer = null;
		String fileString = null;
		String line = null;

		try {
			FileReader in = new FileReader(file);
			BufferedReader brd = new BufferedReader(in);
			fileBuffer = new StringBuffer();

			while ((line = brd.readLine()) != null) {
				fileBuffer.append(line).append(
						System.getProperty("line.separator"));
			}

			in.close();
			fileString = fileBuffer.toString();
		} catch (IOException e) {
			return null;
		}
		return fileString;
	}

	/**
	 * Inits the UI
	 * @return JMenuBar
	 */
	public final JMenuBar initUI() {

		JMenuBar menubar = new JMenuBar();

		final JMenu menu = new JMenu("File");

		menu.setMnemonic(KeyEvent.VK_F);

		menubar.add(menu);

		/*
		 * Open Dialog
		 */
		JMenuItem openFileMenuItem = new JMenuItem("Open OBJ File");
		menu.add(openFileMenuItem);

		openFileMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				JFileChooser fileopen = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"OBJ files", "obj");
				fileopen.addChoosableFileFilter(filter);

				int returned = fileopen.showDialog(menu, "Open file");

				if (returned == JFileChooser.APPROVE_OPTION) {
					File file = fileopen.getSelectedFile();
					// Adding the Model to load
					addModel(file.toString());
				}

			}
		});

		/*
		 * Sepperator
		 */

		menu.addSeparator();

		/*
		 * EXIT Button
		 */
		
		JMenuItem eMenuItem = new JMenuItem("Exit", new ImageIcon(
				"images/exit.png"));

		eMenuItem.setMnemonic(KeyEvent.VK_C);

		eMenuItem.setToolTipText("Exit application");

		menu.add(eMenuItem);

		eMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}

		});

		/*
		 * Control the appearance of the drawn object
		 */

		final JMenu appearanceMenu = new JMenu("Appearance");

		/*
		 * SHADER Smooth
		 */
		JMenuItem sMenuItem = new JMenuItem("Goraud");
		sMenuItem.setMnemonic(KeyEvent.VK_G);

		sMenuItem.setToolTipText("Set Smooth-Shading");

		appearanceMenu.add(sMenuItem);

		sMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				shadingData.setShadingmode("smooth");
			}

		});

		/*
		 * Wireframe Mode
		 */
		JMenuItem wMenuItem = new JMenuItem("Toggle wireframe");
		wMenuItem.setMnemonic(KeyEvent.VK_W);

		wMenuItem.setToolTipText("Toggle Wireframe Mode");

		appearanceMenu.add(wMenuItem);

		wMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				sceneGraph.toggleWireframe();
			}

		});
		
		
		/*
		 * SHADER Flat
		 */
		JMenuItem fMenuItem = new JMenuItem("Flat");
		fMenuItem.setMnemonic(KeyEvent.VK_F);

		fMenuItem.setToolTipText("Set Falt-Shading");

		appearanceMenu.add(fMenuItem);

		fMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				shadingData.setShadingmode("flat");
			}

		});

		/*
		 * Color Borders
		 */
		JMenuItem bMenuItem = new JMenuItem("(Un)Highlight holes in mesh");
		bMenuItem.setMnemonic(KeyEvent.VK_H);

		bMenuItem.setToolTipText("(Un)Highlight holes in mesh");

		appearanceMenu.add(bMenuItem);

		bMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				if (!highlightMeshHoles) {
					highlightMeshHoles = true;
					System.out.println("Highlight holes in mesh");
				} else {
					highlightMeshHoles = false;
					System.out.println("Unhighlight holes in mesh");
				}
			}

		});

		/*
		 * Smooth Model
		 */
		JMenuItem cMenuItem = new JMenuItem("smooth mesh");

		cMenuItem.setToolTipText("smooth mesh");

		appearanceMenu.add(cMenuItem);

		cMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				sceneGraph.smoothModel();
			}

		});

		menubar.add(appearanceMenu);

		setTitle("Dimitri Martens - Interaktive 3D-Computergrafik");
		setSize((int) WINWIDTH, (int) WINHEIGHT);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		return menubar;

	}

	/**
	 * Push OBJ-File-Path into initList of the SceneGraph so it can be
	 * initialized by the next OBJViewer.display() call
	 * 
	 * @param filePath
	 */
	public final void addModel(String filePath) {
		sceneGraph.pushIntoInitList(filePath);
	}

	/**
	 * Is used to trigger a redrawing of canvas
	 */
	public void triggerCanvasDisplay() {
		this.canvas.display();
	}


	public void determShaderVersion(GLAutoDrawable drawable) {
		/**
		 * Print supported Extensions and OpenGL Version
		 */
		GL gl = drawable.getGL();
		String glVersion = gl.glGetString(GL.GL_VERSION);

		System.out.printf("Supported OpenGL Version: %s\n", glVersion);
		float versionNr = 2.1f;// Float.parseFloat(glVersion.substring(0, 4));

		if (versionNr >= 2.0) {

			System.out.println("OpenGL Version >= 2.0. Setting up shaders");

		} else {

			System.out.println("OpenGL Version < 2.0. Can not set up shaders");

		}
		// Print supported extensions

		int cnt = 0;

		String text = gl.glGetString(GL.GL_EXTENSIONS);
		StringTokenizer tokenizer = new StringTokenizer(text);

		while (tokenizer.hasMoreTokens()) {
			System.out.printf("Extension Nr. %d : %s \n", cnt++, tokenizer
					.nextToken());
		}

		// End of print
	}

	/*
	 * ##################### Picking related methods
	 * #######################################
	 */
	/**
	 * Reads the color of the pixel underneath pickPoint. Is used for color-picking
	 */
	public void processPick(Point pickPoint) {

		int viewport[] = new int[4];
		ByteBuffer pixel = BufferUtil.newByteBuffer(3);

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		/*
		 * x,y : the bottom left corner width, length: the size of the area to
		 * be read format: The type of data to be read. In here it is assumed to
		 * GL_RGB. type: the data type of the elements of the pixel. In here
		 * we'll use GL_UNSIGNED_BYTE. pixels: An array where the pixels will be
		 * stored. This is the result of the function
		 */
		gl.glReadPixels(pickPoint.x, (viewport[3] - pickPoint.y), 1, 1,
				GL.GL_RGB, GL.GL_UNSIGNED_BYTE, pixel);

		/**
		 * Idea by Henning Tjaden
		 */
		int modelId = 0;
		modelId |= 255 & pixel.get(0);
		modelId |= (255 & pixel.get(1)) << 8;
		modelId |= (255 & pixel.get(2)) << 16;
		
		// Background color equals 5000268
		if (modelId == 5000268)
			modelId = -1;

		//modelId is == ID of the picked Model. 
		sceneGraph.setUpPickedModel(modelId);

		canvas.display();

	}

	/*
	 * ######################### END PICKING
	 * #####################################################
	 */

	public void display(GLAutoDrawable drawable) {

		GL gl = drawable.getGL();
		GLU glu = new GLU();
		GLUT glut = new GLUT();		
		
		/*
		 * check for to-load Models
		 */
		if (sceneGraph.checkForInitList()) {
			Model model = new Model(gl, sceneGraph.popFromInitList());
			sceneGraph.addNode(model, 0);
		}
				

		/*
		 * ############# ARCBALL STUFF
		 */
		synchronized (matrixLock) {
			ThisRot.get(matrix);
		}

		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		
		gl.glLoadIdentity(); 

		gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT);
		
		//the shadingData Object holds information about the kind of shading to be applied (Flat|Gouroud)
		if (shadingData.isShadingEnabled()) {
			gl.glShadeModel(shadingData.getShadingmode());
		}

		/*
		 * arcball stuff
		 */


		gl.glPushMatrix(); // NEW: Prepare Dynamic Transform
		gl.glMultMatrixf(matrix, 0); // NEW: Apply Dynamic Transform
		
		gl.glScalef(scaling, scaling, scaling);

		plain.drawAxes(gl);
		sceneGraph.draw(gl, this.PICKED);
	
		if (sceneGraph.wireframeMode) {
			
			gl.glLineWidth(3);			
			gl.glDisable(GL.GL_LIGHTING);			
			gl.glColor3f(1, 0, 0);
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE); // WireFrame				
				sceneGraph.draw(gl, this.PICKED);			
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			gl.glEnable(GL.GL_LIGHTING);
		}

		gl.glPopMatrix(); // NEW: Unapply Dynamic Transform

		//Models were drawn in solid colors for color-picking
		if (this.PICKED) {
			this.PICKED = false;
			//Determine (by color) which model was picked 
			processPick(getLastPickPoint());
			System.out.println("Point picked: " + getLastPickPoint().x + " "
					+ getLastPickPoint().y);
		}

		gl.glFlush(); // Flush The GL Rendering Pipeline
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

		//set new bounds for Arcball
		arcBall.setBounds((float) width, (float) height);

	}

	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {

	}

	/**
	 * MAps Mouse coordinates to world coordinates. Is not yet used in program, but works.
	 * @param Mouse_X
	 * @param Mouse_Y
	 * @return World coordinates as double[]
	 */
	public double[] MouseToWorld(int Mouse_X, int Mouse_Y)
	{
			GLU glu = new GLU();
			
			
			int viewport[] = new int[4];
		    double ModelviewMatrix[] = new double[16];
		    double ProjectionMatrix[] = new double[16];
		    double wcoord[] = new double[4];
			
			int OGL_Y;
			
			// Read matrices
			gl.glGetIntegerv(GL.GL_VIEWPORT, viewport,0);
			gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, ModelviewMatrix,0);
			gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, ProjectionMatrix,0);
			
			
			// turn y-axis
			OGL_Y = viewport[3] - Mouse_Y - 1;
			FloatBuffer tiefe = BufferUtil.newFloatBuffer(4);;
			tiefe.put(0.0f);
			
			gl.glReadPixels(Mouse_X, OGL_Y, 1, 1, GL.GL_DEPTH_COMPONENT,GL.GL_FLOAT, tiefe);
			
			glu.gluUnProject((double)Mouse_X, (double) OGL_Y, 0.0,
					ModelviewMatrix, 0,
					ProjectionMatrix, 0, 
		              viewport, 0, 
		              wcoord, 0);
			
		    System.out.println("World coords at z=0.0 are ( " //
		                             + wcoord[0] + ", " + wcoord[1] + ", " + wcoord[2]
		                             + ")");
			
			return wcoord;
			
	}

	public static void main(String[] args) {
		Frame frame = new ObjViewer();
		frame.setBounds(0, 0, 1000, 800);
		frame.setVisible(true);

	}

	/**
	 * Sets the last picked Point. Is set in MouseListener class by MouseClick Event.
	 * @param lastPickPoint
	 */
	public void setLastPickPoint(Point lastPickPoint) {
		this.lastPickPoint = lastPickPoint;
	}

	public Point getLastPickPoint() {
		return lastPickPoint;
	}

}
