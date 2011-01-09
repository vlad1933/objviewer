package com.dim.halfEdgeStruct;


public class pList {
	private HE_face face;	
	private int point;

	
	public pList(int p, HE_face f){
		this.point = p;
		this.face = f;
	}
	
	public int getPoint(){
		return point;
	}
	
	public HE_face getFace(){
		return this.face;
	}


	@Override
	public String toString() {
		return "pList [ point=" + point + " face=" + face.index + "]";
	}
}
