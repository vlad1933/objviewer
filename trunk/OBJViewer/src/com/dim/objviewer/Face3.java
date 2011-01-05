package com.dim.objviewer;

public class Face3 {
private int[] a,b,c;
public boolean isDecremented = false;

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
	
	public void printFace(){
		System.out.println("Normalenindizes: " + a[2] + " " + b[2] + " " + c[2]);
		System.out.println("Vertex Indizes: " + a[0] + " " + b[0] + " " + c[0]);
		System.out.println("TexKoordinatenindizes: " + a[1] + " " + b[1] + " " + c[1]);		
	}
	
	private void decrValuesByOne(){
		a[0]--;
		b[0]--;
		c[0]--;
		
		a[2]--;
		b[2]--;
		c[2]--;
		
		this.isDecremented = true;
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
	
	public void setNormIndex(int normIndex){
		a[2] = normIndex;
		b[2] = normIndex;
		c[2] = normIndex;
	}
	
	public void setVertIndex(int vertIndex1,int vertIndex2,int vertIndex3){
		System.out.println("index: " + vertIndex1+vertIndex2+vertIndex3);
		
		this.a[0] = vertIndex1;
		this.b[0] = vertIndex2;
		this.c[0] = vertIndex3;
		
		System.out.println("index: " + a[0] + b[0] + c[0] + "WTF???!!");
	}

		
}
