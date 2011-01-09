package com.dim.objviewer;

public class Facette {
	private int normInd1;
	private int normInd2;
	private int normInd3;
	
	private int texCoordInd1;
	private int texCoordInd2;
	private int texCoordInd3;
	
	private int vertInd1;
	private int vertInd2;
	private int vertInd3;
	
	public Facette(){
		normInd1 = normInd2 = normInd3 = 0;
		texCoordInd1 = texCoordInd2 = texCoordInd3 = 0;
		vertInd1 = vertInd2 = vertInd3 = 0;
	}
	
	/**
	 * Arrays haben immer die Mächtigkeit 3
	 * Nicht vorhandene Werte (z.B. kein TexCoord) werden mit einer 0 besetzt
	 * @param normInds
	 * @param texInds
	 * @param vertInds
	 */
	public Facette(int[] normInds, int[] texInds, int[] vertInds){
		normInd1 = normInds[0]-1;
		normInd2 = normInds[1]-1;
		normInd3 = normInds[2]-1;
		
		texCoordInd1 = texInds[0]-1;
		texCoordInd2 = texInds[1]-1;
		texCoordInd3 = texInds[2]-1;
		
		vertInd1 = vertInds[0]-1;
		vertInd2 = vertInds[1]-1;
		vertInd3 = vertInds[2]-1;		
	}
	
	public void printFace(){
		System.out.println("Normalenindizes: " + normInd1 + " " + normInd2 + " " + normInd3);
		System.out.println("Vertex Indizes: " + vertInd1 + " " + vertInd2 + " " + vertInd3);
		System.out.println("TexKoordinatenindizes: " + texCoordInd1 + " " + texCoordInd2 + " " + texCoordInd3);		
	}
	
	public String toString(){
		return "Normalenindizes: " + normInd1 + " " + normInd2 + " " + normInd3 + "\n" + "Vertex Indizes: " + vertInd1 + " " + vertInd2 + " " + vertInd3 +
		"\n" + "TexKoordinatenindizes: " + texCoordInd1 + " " + texCoordInd2 + " " + texCoordInd3 + "\n";
	}

	public int getNormInd1() {
		return normInd1;
	}

	public void setNormInd1(int normInd1) {
		this.normInd1 = normInd1;
	}

	public int getNormInd2() {
		return normInd2;
	}

	public void setNormInd2(int normInd2) {
		this.normInd2 = normInd2;
	}

	public int getNormInd3() {
		return normInd3;
	}

	public void setNormInd3(int normInd3) {
		this.normInd3 = normInd3;
	}

	public int getTexCoordInd1() {
		return texCoordInd1;
	}

	public void setTexCoordInd1(int texCoordInd1) {
		this.texCoordInd1 = texCoordInd1;
	}

	public int getTexCoordInd2() {
		return texCoordInd2;
	}

	public void setTexCoordInd2(int texCoordInd2) {
		this.texCoordInd2 = texCoordInd2;
	}

	public int getTexCoordInd3() {
		return texCoordInd3;
	}

	public void setTexCoordInd3(int texCoordInd3) {
		this.texCoordInd3 = texCoordInd3;
	}

	public int getVertInd1() {
		return vertInd1;
	}

	public void setVertInd1(int vertInd1) {
		this.vertInd1 = vertInd1;
	}

	public int getVertInd2() {
		return vertInd2;
	}

	public void setVertInd2(int vertInd2) {
		this.vertInd2 = vertInd2;
	}

	public int getVertInd3() {
		return vertInd3;
	}

	public void setVertInd3(int vertInd3) {
		this.vertInd3 = vertInd3;
	}
	
	public int[] getVertsAsArray(){
		int[] verts = { this.vertInd1 , this.vertInd2, this.vertInd3 };
		return verts;
	}
	
}
