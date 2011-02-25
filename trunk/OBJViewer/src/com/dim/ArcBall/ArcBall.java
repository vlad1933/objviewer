package com.dim.ArcBall;

import java.awt.Point;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

/**
 * Tutorial and code taken from Nehe Lesson 48
 *
 */
public class ArcBall {
    private static final float Epsilon = 1.0e-5f;

    Vert3f StVec;          //Saved click vector
    Vert3f EnVec;          //Saved drag vector
    float adjustWidth;       //Mouse bounds width
    float adjustHeight;      //Mouse bounds height

    public ArcBall(float NewWidth, float NewHeight) {
        StVec = new Vert3f();
        EnVec = new Vert3f();
        setBounds(NewWidth, NewHeight);
    }

    public void mapToSphere(Point point, Vert3f vector) {
        // Copy paramter into temp point
        Tuple2f_t tempPoint = new Tuple2f_t(point.x, point.y);

        // Adjust point coords and scale down to range of [-1 ... 1]
        tempPoint.setX((tempPoint.getX() * this.adjustWidth) - 1.0f);
        tempPoint.setY(1.0f - (tempPoint.getY() * this.adjustHeight));

        // Compute the square of the length of the vector to the point from the center
        float length = (tempPoint.getX() * tempPoint.getX()) + (tempPoint.getY() * tempPoint.getY());

        // If the point is mapped outside of the sphere... (length > radius squared)
        if (length > 1.0f) {
            // Compute a normalizing factor (radius / sqrt(length))
            float norm = (float) (1.0 / Math.sqrt(length));

            // Return the "normalized" vector, a point on the sphere
            vector.x = tempPoint.getX() * norm;
            vector.y = tempPoint.getY() * norm;
            vector.z = 0.0f;
        } else    //Else it's on the inside
        {
            // Return a vector to a point mapped inside the sphere 
            // sqrt(radius squared - length)
            vector.x = tempPoint.getX();
            vector.y = tempPoint.getY();
            vector.z = (float) Math.sqrt(1.0f - length);
        }

    }

    public void setBounds(float NewWidth, float NewHeight) {
        assert((NewWidth > 1.0f) && (NewHeight > 1.0f));

        // Set adjustment factor for width/height
        adjustWidth = 1.0f / ((NewWidth - 1.0f) * 0.5f);
        adjustHeight = 1.0f / ((NewHeight - 1.0f) * 0.5f);
    }

    // Mouse down
    public void click(Point NewPt) {
        mapToSphere(NewPt, this.StVec);
    }

    // Mouse drag, calculate rotation
    public void drag(Point NewPt, Quat4f_t NewRot) {
        // Map the point to the sphere
        this.mapToSphere(NewPt, EnVec);

        // Return the quaternion equivalent to the rotation
        if (NewRot != null) {
            Vert3f Perp = new Vert3f();

            // Compute the vector perpendicular to the begin and end vectors
            Vert3f.cross(Perp, StVec, EnVec);

            // Compute the length of the perpendicular vector
            if (Perp.length() > Epsilon)    //if its non-zero
            {
                // We're ok, so return the perpendicular vector as the transform 
                // after all
                NewRot.setX(Perp.x);
                NewRot.setY(Perp.y);
                NewRot.setZ(Perp.z);
                // In the quaternion values, w is cosine (theta / 2), 
                // where theta is rotation angle
                NewRot.setW(0.3f*Vert3f.dot(StVec, EnVec)); //Faktor 0.3 beschleunigt die Bewegung etwas
            } else                                    //if its zero
            {
                // The begin and end vectors coincide, so return an identity transform
                NewRot.setX(0.0f);
                NewRot.setY(0.0f);
                NewRot.setZ(0.0f);
                NewRot.setW(0.0f);
            }
        }
    }

}
