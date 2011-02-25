package com.dim.objviewer;

import javax.media.opengl.GL;

public class Shading {
	
	private int shadingmode;
	private boolean shadingEnabled = false;
	
	
	public Shading(){
		
	}
	
	public int getShadingmode() {		
		return shadingmode;		
	}


	public void setShadingmode(String shadeStr) {
		GL gl = null;
		
		shadingEnabled = true;
		
		if(shadeStr == "smooth")
			shadingmode = gl.GL_SMOOTH;
		else if(shadeStr == "flat")
			shadingmode = gl.GL_FLAT;
		else
			shadingEnabled = false;
		
	}
	
	public void resetShading(){
		shadingEnabled = false;
	}
	

	public boolean isShadingEnabled() {
		return shadingEnabled;
	}
	
	
}
