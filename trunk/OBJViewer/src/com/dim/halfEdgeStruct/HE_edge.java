package com.dim.halfEdgeStruct;

public class HE_edge {
	/**
	 * 1. Endpunkt (vertex) 
	 * 2. nächste Halbkante (nHedge) 
	 * 3. andere (Gegenkante) Halbkante  (oHedge) 
	 * 4. zugehöriges Polygon (face) 
	 * 5. vorherige Halbkante (pHedge)
	 * (optional)
	 */
//	HE_vert vert; // vertex at the end of the half-edge
//	HE_edge next; // next half-edge around the face
//	HE_edge pair; // oppositely oriented adjacent half-edge
//	HE_face face; // face the half-edge borders
//		
	private static int eID = 0;
	public int id = -1;
	public int starting_point; //startingpoint
	public int vert; // vertex at the end of the half-edge
	
	HE_edge next; // next half-edge around the face
	HE_edge pair; // oppositely oriented adjacent half-edge
	HE_face face; // face the half-edge borders
	
	@Override
	public String toString() {
		String ndummy = "null";
		String pdummy = "null";
		if(next != null)
			ndummy = String.valueOf(this.next.id);
		if(pair != null)
			pdummy = String.valueOf(this.pair.id);
		
		this.starting_point = this.getNext().getNext().getVert();
		
		return this.id + ": HE_edge [face=" + face + ", next=" + ndummy + ", pair=" + pdummy + " Start: " + this.starting_point + ", vert=" + vert + "]";
	}
	
	public void printEdge(){
		if(this.pair != null && this.next != null)
			System.out.println("HE_edge [face=" + face + ", next= null" + ", pair= null" + ", vert=" + vert + "]");
		else
			System.out.println("HE_edge [face=" + face + ", next=" + next + ", pair=" + pair + ", vert=" + vert + "]");
		
	}
	
	/**
	 * Konstruktor der Klasse HAlf-Edge
	 * @param vert	vertex at the end of the half-edge
	 * @param next	next half-edge around the face
	 * @param pair	oppositely oriented adjacent half-edge
	 * @param face	face the half-edge borders
	 */
	public HE_edge(int vert, HE_edge next, HE_edge pair, HE_face face) {
		super();		
		this.vert = vert;
		this.next = next;
		this.pair = pair;
		this.face = face;
		
		this.id = eID;
		eID++;
	}
	
	
//	public HE_edge(HE_vert vert, HE_edge next, HE_edge pair, HE_face face) {		
//		this.vert = vert;
//		this.next = next;
//		this.pair = pair;
//		this.face = face;
//	}
	public void setStratingPoint(int sp){
		this.starting_point = sp;
	}
	
	public static int getObjID(){
		return HE_edge.eID;
	}
	
	public int getVert() {
		return vert;
	}
	public void setVert(int vert) {
		this.vert = vert;
	}
	public HE_edge getNext() {
		return next;
	}
	public void setNext(HE_edge next) {
		this.next = next;
	}
	public HE_edge getPair() {
		return pair;
	}
	public void setPair(HE_edge pair) {
		this.pair = pair;
	}
	public HE_face getFace() {
		return face;
	}
	public void setFace(HE_face face) {
		this.face = face;
	}
	
	public boolean testForNull(){
		if(this.next == null)
			return true;
		else
			return false;
	}
	
	
}
