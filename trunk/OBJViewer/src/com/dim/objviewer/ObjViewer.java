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

import sun.org.mozilla.javascript.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.StringTokenizer;

import com.dim.ArcBall.*;
import com.dim.halfEdgeStruct.Mesh;
import com.dim.sceneGraph.SceneGraph;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.StreamUtil;

public class ObjViewer extends JFrame implements GLEventListener {
	private static final long serialVersionUID = 1L;

	
	public float WINWIDTH = 1000.0f;
	public float WINHEIGHT = 800.0f;
	public static boolean DEBUG = false;
	
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
	 * */	  
    private GLU glu = new GLU();

    private Matrix4f LastRot = new Matrix4f();
    private Matrix4f ThisRot = new Matrix4f();
    private final Object matrixLock = new Object();
    private float[] matrix = new float[16];
    
    public ArcBall arcBall = new ArcBall(this.WINWIDTH, this.WINHEIGHT);  // NEW: ArcBall Instance
    
    
    
	
	public ObjViewer() {
		super("OpenGL JOGL VIEWER");
		/*
		 * GLCanvas reports the following four events to a registered Listener:
		 * - init() - display() - displayChanged() - reshape() Therefore we
		 * implement the GLEventListener interface
		 */
		canvas = new GLCanvas();
		canvas.getContext().makeCurrent();
		
		shadingData = new Shading();

		canvas.addGLEventListener(this);
		canvas.addKeyListener(new MyKeyListener(this));
		canvas.addMouseListener(new MyMouseListener(this));
		canvas.addMouseMotionListener(new MyMouseMotionListener(this));
		canvas.addMouseWheelListener(new MyMouseWheelListener(this));

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
			
		this.plain  = new Plain(gl);
	
		scaling = 1.0f;

		this.initUI(gl);
//		try {
//			cornette(drawable);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		/*
		 * ArcBALL STUFF 
		 */
		
		// Start Of User Initialization
        LastRot.setIdentity();                                // Reset Rotation
        ThisRot.setIdentity();                                // Reset Rotation
        ThisRot.get(matrix);
        
        gl.glDepthFunc(GL.GL_LEQUAL);  // The Type Of Depth Testing (Less Or Equal)
        gl.glEnable(GL.GL_DEPTH_TEST);                        // Enable Depth Testing
        // Set Perspective Calculations To Most Accurate
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);                

        //quadratic = glu.gluNewQuadric(); // Create A Pointer To The Quadric Object
        //glu.gluQuadricNormals(quadratic, GLU.GLU_SMOOTH); // Create Smooth Normals
        //glu.gluQuadricTexture(quadratic, true); // Create Texture Coords
        
        
	}
	
	//######################## ARCBALL METHODS ########################################
    void reset() {
        synchronized(matrixLock) {
            LastRot.setIdentity();   // Reset Rotation
            ThisRot.setIdentity();   // Reset Rotation
        }
    }

    void startDrag( Point MousePt ) {
        synchronized(matrixLock) {
            LastRot.set( ThisRot );  // Set Last Static Rotation To Last Dynamic One
        }
        arcBall.click( MousePt );    // Update Start Vector And Prepare For Dragging
    }

    void drag( Point MousePt )       // Perform Motion Updates Here
    {
        Quat4f_t ThisQuat = new Quat4f_t();

        // Update End Vector And Get Rotation As Quaternion
        arcBall.drag( MousePt, ThisQuat); 
        synchronized(matrixLock) {
            ThisRot.setRotation(ThisQuat);  // Convert Quaternion Into Matrix3fT
            ThisRot.mul( ThisRot, LastRot); // Accumulate Last Rotation Into This One
        }
    }
    
    void dragScale(Point MousePt){
    	System.out.println(MousePt);
    	this.scaling = (MousePt.x / 100);
    }
    
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
    
    
    //######################## END ARCBALL METHODS ########################################
	public final void initUI(final GL gl) {

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
                FileNameExtensionFilter filter = new FileNameExtensionFilter("OBJ files", "obj");
                fileopen.addChoosableFileFilter(filter);

                int ret = fileopen.showDialog(menu, "Open file");

                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileopen.getSelectedFile();
                    //Adding the Model to load
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
        //JMenuItem eMenuItem = new JMenuItem("Exit");
        JMenuItem eMenuItem = new JMenuItem("Exit", new ImageIcon("images/exit.png"));

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
        
        final JMenu appearanceMenu = new JMenu("Apperance");

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
            	
            	if(!highlightMeshHoles){
            		highlightMeshHoles = true;
            		System.out.println("Highlight holes in mesh");
            	}else{
            		highlightMeshHoles = false;
            		System.out.println("Unhighlight holes in mesh");
            	}
            }

        });
        
        menubar.add(appearanceMenu);
        
        setJMenuBar(menubar);

        setTitle("Dimitri Martens - Interaktive 3D-Computergrafik");
        setSize((int)WINWIDTH, (int)WINHEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        
    }
	
	/**
	 * Push OBJ-File-Path into initList of the SceneGRaph so it can be initialized by the next Display() call 
	 * @param filePath
	 */
	public final void addModel(String filePath){
		sceneGraph.pushIntoInitList(filePath);		
	}
	
	public void triggerCanvasDisplay(){
		this.canvas.display();
	}
	
	//######################## SHADER STUFF ###############################################
	public void cornette(GLAutoDrawable drawable) throws IOException{
		GL gl = drawable.getGL();
		
		int v = gl.glCreateShader(GL.GL_VERTEX_SHADER);
		int f = gl.glCreateShader(GL.GL_FRAGMENT_SHADER);

		BufferedReader brv = new BufferedReader(new FileReader("shaders/vert.glsl"));//"shaders/vert.glsl"));
		String vsrc = "";
		String line;
		while ((line=brv.readLine()) != null) {
		  vsrc += line + "\n";
		}
		
		String[] vs = { vsrc };
		gl.glShaderSource(v, 1, vs, null);		
		gl.glCompileShader(v);
		
		IntBuffer success = BufferUtil.newIntBuffer(1);
		gl.glGetObjectParameterivARB(shaderprogram, GL.GL_OBJECT_COMPILE_STATUS_ARB, success);
		if(success.get(0)==0)
			System.out.println("Failed to compile shader source:\n"+vs[0]);

		BufferedReader brf = new BufferedReader(new FileReader("shaders/frag.glsl"));
		String fsrc = "";
		line = "";
		while ((line=brf.readLine()) != null) {
		  fsrc += line + "\n";
		}
		
		String[] fs = { fsrc };
		gl.glShaderSource(f, 1, fs, null);
		gl.glCompileShader(f);

		int shaderprogram = gl.glCreateProgram();
		gl.glAttachShader(shaderprogram, v);
		gl.glAttachShader(shaderprogram, f);
		gl.glLinkProgram(shaderprogram);
		gl.glValidateProgram(shaderprogram);
		
		this.shaderprogram = shaderprogram;
	}
	
	public void determShaderVersion(GLAutoDrawable drawable){
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
			 System.out.printf("Extension Nr. %d : %s \n", cnt++,
			 tokenizer.nextToken());
		}

		// End of print
	}
			

	public boolean initShader(GLAutoDrawable drawable) {
		System.out.println("Initializing shaders");

		IntBuffer success = BufferUtil.newIntBuffer(1);
		IntBuffer success_ = BufferUtil.newIntBuffer(1);
		
		boolean initialized = true;
		GL gl = drawable.getGL();

		shaderprogram = gl.glCreateProgram();

		//String[] vsrc =  loadFile("shaders/vsrc.glsl") ;
		//String[] fsrc =  loadFile("shaders/fsrc.glsl") ;
				
		String[] vsrc =  loadFile("shaders/dispersion_vertex.glsl") ;
		String[] fsrc =  loadFile("shaders/dispersion_fragment.glsl") ;
	
		
		/**
		 * String[] gsrc = { loadFile("src/simpleGeometryshader.glsl") }; int
		 * geometryShader = gl.glCreateShader(GL.GL_GEOMETRY_SHADER_EXT);
		 * gl.glShaderSource(geometryShader, 1, gsrc, null);
		 * gl.glCompileShader(geometryShader);
		 * 
		
		gl.glProgramParameteriEXT(shaderprogram, GL.GL_GEOMETRY_INPUT_TYPE_EXT, GL.GL_TRIANGLES);
		gl.glProgramParameteriEXT(shaderprogram, GL.GL_GEOMETRY_OUTPUT_TYPE_EXT, GL.GL_TRIANGLE_STRIP);
		gl.glProgramParameteriEXT(shaderprogram, GL.GL_GEOMETRY_VERTICES_OUT_EXT, 30);
		**/ 

		int vertexShader = gl.glCreateShader(GL.GL_VERTEX_SHADER);
		int fragmentShader = gl.glCreateShader(GL.GL_FRAGMENT_SHADER);
		
		gl.glShaderSource(vertexShader, 1, vsrc, null);
		gl.glShaderSource(fragmentShader, 1, fsrc, null);
		
		gl.glCompileShader(fragmentShader);
		gl.glCompileShader(vertexShader);	

		if (initialized) {
			gl.glAttachShader(shaderprogram, vertexShader);
			// gl.glAttachShader(shaderprogram, geometryShader);
			gl.glAttachShader(shaderprogram, fragmentShader);

			gl.glLinkProgram(shaderprogram);

			gl.glGetObjectParameterivARB(shaderprogram, GL.GL_OBJECT_COMPILE_STATUS_ARB, success_);
			
			if(success_.get(0)==0)
				System.out.println("Failed to compile shader source:\n"+vsrc[0]);

			gl.glValidateProgram(shaderprogram);					
			
			gl.glGetObjectParameterivARB(shaderprogram, GL.GL_OBJECT_LINK_STATUS_ARB, success);
			
			if(success.get(0)==0)
				System.out.println("Failed to link shaders");
			
			
		}
		return initialized;

	}

	public String[] loadFile(String fileName) {
		BufferedReader br;
		String[] shaderSrc = null;
		String line;

//		try {
//			br = new BufferedReader(new FileReader(fileName));
//
//			while ((line = br.readLine()) != null) {
//				shaderSrc += line + "\n";
//			}
//		} catch (FileNotFoundException e) {
//
//			System.out.println("Something went wrong while opening the file.");
//			System.out.println(e.getMessage());
//		} catch (IOException e) {
//
//			System.out.println("Something went wrong while reading the file.");
//			System.out.println(e.getMessage());
//		}
		
		try
		{
		shaderSrc = (new String (StreamUtil.readAll (new FileInputStream (fileName)))).split ("\n");
		}
		catch (Exception ex)
		{
		System.err.println (ex);
		}


		return shaderSrc;
	}

	//######################## END SHADER STUFF ###############################################
	
	/* ##################### Picking related methods #######################################*/
	public void processPick (Point pickPoint)
	{
		
		int viewport[] = new int[4];
		ByteBuffer pixel = BufferUtil.newByteBuffer(12);
		byte pixel_[] = new byte[12];

		gl.glGetIntegerv(GL.GL_VIEWPORT,viewport,0);

		
		/*
		 *     x,y : the bottom left corner
		 *     width, length: the size of the area to be read
		 *     format: The type of data to be read. In here it is assumed to GL_RGB.
		 *     type: the data type of the elements of the pixel. In here we'll use GL_UNSIGNED_BYTE.
		 *     pixels: An array where the pixels will be stored. This is the result of the function 
		 */
		gl.glReadPixels(pickPoint.x,(viewport[3]-pickPoint.y),
				1,1,
				GL.GL_RGB,GL.GL_FLOAT,pixel);
		
		
		/*
		 * DEBUG ###########################################
		 */
		int[] intbuffer = new int[12];
        
        for (int i = 0; i <pixel.limit(); i++)
        {
            if(pixel.get(i)<0)
                intbuffer[i] = 256+pixel.get(i);
            else
                intbuffer[i] = pixel.get(i);
        }
        //###################################################
		
		System.out.println(intbuffer[0] +" " + intbuffer[1]+" " +intbuffer[2]);
		System.out.println(pixel.get(0) +" " + pixel.get(1)+" " +pixel.get(2));
			
		
		//canvas.display();
			   
	}
	/* ######################### END PICKING #####################################################*/
	
	public void display(GLAutoDrawable drawable) {

		GL gl = drawable.getGL();
		GLU glu = new GLU();
		GLUT glut = new GLUT();
		
		/*
		 * check for to-load Models
		 */
		if(sceneGraph.checkForInitList()){
			Model model = new Model(gl,sceneGraph.popFromInitList());			
			sceneGraph.addNode(model, 0);			
		}
			
		
		
		/* #############
		 * ARCBALL STUFF
		 */
		synchronized(matrixLock) {
            ThisRot.get(matrix);
        }
		

		// festgelegten Buffer, indem sie mit einen Leerwert gefüllt werden
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL.GL_MODELVIEW); // Legt fest, welche Matrix gerade
		// aktiv ist
		gl.glLoadIdentity(); // Die Funktion glLoadIdentity ersetzt die aktuelle
		// Matrix durch die Identitätsmatrix -
		// Multiplikation einer Matrix A mit einer
		// Einheitsmatrix ergibt wieder die Matrix A

		
		gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT);
		gl.glColor3f(0, 0, 1);

		// Mouse Interaction
		//glu.gluLookAt(0, 0, 3, 0, 0, 0, 0, 1, 0);
		
		
		if (shadingData.isShadingEnabled()) {
			gl.glShadeModel(shadingData.getShadingmode());
		}
		
		
		/*
		 * arcball stuff
		 */
		// Move Left 1.5 Units And Into The Screen 6.0
        //gl.glTranslatef(-1.5f, 0.0f, -6.0f);  

        gl.glPushMatrix();                  // NEW: Prepare Dynamic Transform
        gl.glMultMatrixf(matrix, 0);        // NEW: Apply Dynamic Transform
                
        gl.glPopMatrix();                   // NEW: Unapply Dynamic Transform

        gl.glLoadIdentity();                // Reset The Current Modelview Matrix
        
        gl.glPushMatrix();                  // NEW: Prepare Dynamic Transform
        gl.glMultMatrixf(matrix, 0);        // NEW: Apply Dynamic Transform
        gl.glColor3f(1.0f, 0.75f, 0.75f);
                
        gl.glScalef(scaling, scaling, scaling);
            
        plain.drawAxes(gl);
        sceneGraph.draw(gl,this.PICKED);
        
        
        if (shadingData.isWireframe()) {
			gl.glDisable(GL.GL_LIGHTING);
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE); // WireFrame
			sceneGraph.draw(gl,this.PICKED);			
			gl.glEnable(GL.GL_LIGHTING);
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
		}
                
        gl.glPopMatrix();                   // NEW: Unapply Dynamic Transform

        /*
        gl.glPushMatrix();
        	gl.glLoadIdentity();
        	
        	 gl.glDisable(GL.GL_LIGHTING);
             plain.drawAxes(gl);
             gl.glEnable(GL.GL_LIGHTING);
        	
        gl.glPopMatrix();
        
        //Bottom-Plain
        gl.glPushMatrix();
        	gl.glLoadIdentity();
        	gl.glDisable(GL.GL_LIGHTING);
            	plain.draw(gl);
            gl.glEnable(GL.GL_LIGHTING);        	
        	gl.glTranslatef(-1.5f, 0.0f, 0.5f);
	        gl.glRotatef(90, 1, 0, 0);	        	        
        gl.glPopMatrix();
        //plain.drawAxes(gl);
        
        */
        
        if (this.PICKED) {
			this.PICKED = false;
			processPick(getLastPickPoint());
			System.out.println("Point picked: " + getLastPickPoint().x + " "
					+ getLastPickPoint().y);				
			
		}
        
        gl.glFlush();                       // Flush The GL Rendering Pipeline        
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL gl = drawable.getGL();

		int size = width < height ? width : height;
		int xbeg = width < height ? 0 : (width - height) / 2;
		int ybeg = width < height ? (height - width) / 2 : 0;

		gl.glViewport(xbeg, ybeg, size, size);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-2, 2, -2, 2, -10, 10);
		
		arcBall.setBounds((float) width, (float) height);

	}

	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {

	}

	public static void main(String[] args) {
		Frame frame = new ObjViewer();		
		frame.setBounds(0, 0, 1000, 800);
		frame.setVisible(true);
				
	}

	public void setLastPickPoint(Point lastPickPoint) {
		this.lastPickPoint = lastPickPoint;
	}

	public Point getLastPickPoint() {
		return lastPickPoint;
	}
	
}
