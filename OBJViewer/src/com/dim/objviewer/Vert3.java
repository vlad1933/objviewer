package com.dim.objviewer;

public class Vert3 {
	private double a,b,c;
	
	

	public Vert3(double a, double b,double c){
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	/**
	 * Legt einen Nullvektor (0,0,0) an
	 */
	public Vert3(){
		this.a = 0;
		this.b = 0;
		this.c = 0;
	}
	
	/**  
	 * Gibt den ersten Wert a der Vektors (a,b,c) zurück
	 * @return Double
	 */
	public double getVertA(){
		return a;
	}
	
	public double getVertB(){
		return b;
	}
	
	public double getVertC(){
		return c;
	}
	
	/**
	 * getVert() gibt einen Vektor als double array (größe 3) zurück
	 * @return Double Array der Größe 3
	 */
	public double[] getVert(){
		double[] vt = {a,b,c};
		return vt;
	}
	
	public void setVertA(double a) {
		this.a = a;
	}

	public void setVertB(double b) {
		this.b = b;
	}

	public void setVertC(double c) {
		this.c = c;
	}
	
	public void setVert(double a,double b,double c){
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public static Vert3 plus(Vert3 leftVert, Vert3 rightVert){
		Vert3 resultVert = new Vert3(0,0,0);
		
		resultVert.setVertA(leftVert.getVertA()+rightVert.getVertA());
		resultVert.setVertB(leftVert.getVertB()+rightVert.getVertB());
		resultVert.setVertC(leftVert.getVertC()+rightVert.getVertC());
		
		return resultVert;
	}
		
	
	public static Vert3 minus(Vert3 leftVert, Vert3 rightVert){
		Vert3 resultVert = new Vert3(0,0,0);
		
		resultVert.setVertA(leftVert.getVertA()-rightVert.getVertA());
		resultVert.setVertB(leftVert.getVertB()-rightVert.getVertB());
		resultVert.setVertC(leftVert.getVertC()-rightVert.getVertC());
		
		return resultVert;
	}
	
	public static Vert3 multiply(Vert3 leftVert, Vert3 rightVert){
		Vert3 resultVert = new Vert3(0,0,0);
		
		resultVert.setVertA(leftVert.getVertA()*rightVert.getVertA());
		resultVert.setVertB(leftVert.getVertB()*rightVert.getVertB());
		resultVert.setVertC(leftVert.getVertC()*rightVert.getVertC());
		
		return resultVert;
	}
	
	public void printVert(){
		System.out.println("Vert3: " + this.getVertA() + " " + this.getVertB() + " " + this.getVertC() + " " );
	}
	
	public String toString(){
		return "Vert3: " + this.getVertA() + " " + this.getVertB() + " " + this.getVertC() + " ";
	}
	

}
