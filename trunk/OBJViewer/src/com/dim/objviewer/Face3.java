package com.dim.objviewer;

public class Face3 {
private int a,b,c;
	
	public Face3(int a, int b,int c){
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public int[] getFace(){
		int[] f = {a,b,c};
		return f;
	}
	
	public void setVert(int a,int b,int c){
		this.a = a;
		this.b = b;
		this.c = c;
	}
}
