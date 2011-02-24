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
	

	public static Vert3 cross(Vert3 a, Vert3 b){
		Vert3 normal = new Vert3();
		normal.setVertA(a.getVertB() * b.getVertC() - a.getVertC() * b.getVertB());
		normal.setVertB(a.getVertC() * b.getVertA() - a.getVertA() * b.getVertC());
		normal.setVertC(a.getVertA() * b.getVertB() - a.getVertB() * b.getVertA());		
		
		return normal;
	}
	
	public void makeAbsolute(){
		if(this.a < 0)			
			this.a *= -1;
		if(this.b < 0)
			this.b *= -1;
		if(this.c < 0)
			this.c *= -1;
	}
	
	public static Vert3 multiply(double scalar, Vert3 vert){
		Vert3 result = new Vert3();
		result.setVertA(vert.getVertA() * scalar);
		result.setVertB(vert.getVertB() * scalar);
		result.setVertC(vert.getVertC() * scalar);
		return result;
	}
	
	
/*	// Normalize pIn vector into pOut
	bool VectorNormalize (GLpoint *pIn, GLpoint *pOut)
	{
	   GLfloat len = (GLfloat)(sqrt(	sqr(pIn->x) + sqr(pIn->y) + sqr(pIn->z)));
	   if (len)
	   {
	      pOut->x = pIn->x / len;
	      pOut->y = pIn->y / len;
	      pOut->z = pIn->z / len;
	      return true;
	   }
	   return false;
	}
*/
	public static Vert3 normalizeVector(Vert3 vert){
		Double len = (Math.sqrt(
				Math.sqrt(vert.getVertA()) + 
				Math.sqrt(vert.getVertB()) + 
				Math.sqrt(vert.getVertC())));
		vert.setVert(vert.getVertA() / len, vert.getVertB() / len, vert.getVertC() / len);
			
		return vert;
	}
	
	public void printVert(){
		System.out.println("Vert3: " + this.getVertA() + " " + this.getVertB() + " " + this.getVertC() + " " );
	}
	
	public String toString(){
		return "Vert3: " + this.getVertA() + " " + this.getVertB() + " " + this.getVertC() + " ";
	}
	

}
