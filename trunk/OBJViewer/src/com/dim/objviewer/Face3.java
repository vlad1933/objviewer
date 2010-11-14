package com.dim.objviewer;

public class Face3 {
private int[] a,b,c;

	/* a/b/c consists of 
	 * [0] Index of Vertex which is part of the Face
	 * [1] Index of the Texture 
	 * [2] Index of the Normal*/
	
	public Face3(int[] a, int[] b,int[] c){
		this.a = a;
		this.b = b;
		this.c = c;
		
		this.decrValuesByOne();
	}
	
	private void decrValuesByOne(){
		a[0]--;
		b[0]--;
		c[0]--;
	}
	
	public int[] getFaceVerts(){
		int[] f = {a[0],b[0],c[0]};
		return f;
	}
	
	public int[] getFaceNormals(){
		int[] f = {a[2],b[2],c[2]};
		return f;
	}
	
	public int[] getFaceTexVert(){
		int[] f = {a[1],b[1],c[1]};
		return f;
	}
	
	public boolean hasTexVert(){
		if(a[1] == -1 | b[1] == -1 | c[1] == -1)
			return false;
		else
			return true;
	}
		
}
