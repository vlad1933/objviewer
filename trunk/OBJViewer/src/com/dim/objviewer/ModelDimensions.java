package com.dim.objviewer;


//Größtenteils übernommen von Andrew Davisons ModelDimensions Klasse - etwas angepasst


//Andrew Davison, Novemeber 2006, ad@fivedots.coe.psu.ac.th

/* This class calculates the 'edge' coordinates for the model
along its three dimensions. 

The edge coords are used to calculate the model's:
   * width, height, depth
   * its largest dimension (width, height, or depth)
   * (x, y, z) center point
*/


import java.text.DecimalFormat;


public class ModelDimensions
{
// edge coordinates
private double leftPt, rightPt;   // on x-axis
private double topPt, bottomPt;   // on y-axis
private double farPt, nearPt;     // on z-axis

// for reporting
private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp


public ModelDimensions()
{
 leftPt = 0.0f;  rightPt = 0.0f;
 topPt = 0.0f;  bottomPt = 0.0f;
 farPt = 0.0f;  nearPt = 0.0f;
}  // end of ModelDimensions()


public void set(Vert3 vert)
// initialize the model's edge coordinates
{
 rightPt = vert.getVertA();
 leftPt = vert.getVertA();

 topPt = vert.getVertB();
 bottomPt = vert.getVertB();

 nearPt = vert.getVertC();
 farPt = vert.getVertC();
}  // end of set()


public void update(Vert3 vert)
// update the edge coordinates using vert
{
 if (vert.getVertA() > rightPt)
   rightPt = vert.getVertA();
 if (vert.getVertA() < leftPt)
   leftPt = vert.getVertA();
         
 if (vert.getVertB() > topPt)
   topPt = vert.getVertB();
 if (vert.getVertB() < bottomPt)
   bottomPt = vert.getVertB();
         
 if (vert.getVertC() > nearPt)
   nearPt = vert.getVertC();
 if (vert.getVertC() < farPt)
   farPt = vert.getVertC();
}  // end of update()


// ------------- use the edge coordinates ----------------------------

public double getWidth()
{ return (rightPt - leftPt); }

public double getHeight()
{  return (topPt - bottomPt); }

public double getDepth()
{ return (nearPt - farPt); } 


public double getLargest()
{
	double height = getHeight();
	double depth = getDepth();

	double largest = getWidth();
 if (height > largest)
   largest = height;
 if (depth > largest)
   largest = depth;

 return largest;
}  // end of getLargest()


public Vert3 getCenter()
{ 
 double xc = (rightPt + leftPt)/2.0f; 
 double yc = (topPt + bottomPt)/2.0f;
 double zc = (nearPt + farPt)/2.0f;
 return new Vert3(xc, yc, zc);
} // end of getCenter()


public void reportDimensions()
{
 Vert3 center = getCenter();

 System.out.println("x Coords: " + df.format(leftPt) + " to " + df.format(rightPt));
 System.out.println("  Mid: " + df.format(center.getVertA()) + 
                    "; Width: " + df.format(getWidth()) );

 System.out.println("y Coords: " + df.format(bottomPt) + " to " + df.format(topPt));
 System.out.println("  Mid: " + df.format(center.getVertB()) + 
                    "; Height: " + df.format(getHeight()) );

 System.out.println("z Coords: " + df.format(nearPt) + " to " + df.format(farPt));
 System.out.println("  Mid: " + df.format(center.getVertC()) + 
                    "; Depth: " + df.format(getDepth()) );
}  // end of reportDimensions()


}  // end of ModelDimensions class