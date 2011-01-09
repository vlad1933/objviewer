package com.dim.halfEdgeStruct;

import java.util.Arrays;

public class HE_face {
	public HE_edge edge;  // one of the half-edges bordering the face
	public int index;
	
	public HE_face(int index){
		this.index = index;
	}

	public HE_edge getEdge() {
		return edge;
	}

	public void setEdge(HE_edge edge) {
		this.edge = edge;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public String toString() {
		return "HE_face [edge=" + edge + ", index=" + index
				+ "]";
	}
	
}
