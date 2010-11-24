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
		a[2]--;
		b[2]--;
		c[2]--;
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
	
	public Vert3 calcNormals(Vert3 v1, Vert3 v2){
		
		if(this.hasTexVert())
			return new Vert3(a[1],b[1],c[1]);
		
		
		Vert3 out = new Vert3(0,0,0);
		
//		out[x] = v1[y] * v2[z] - v1[z] * v2[y];
//		out[y] = v1[z] * v2[x] - v1[x] * v2[z];
//		out[z] = v1[x] * v2[y] - v1[y] * v2[x];
		
		return out;
		                                    
	}
		
}
