/**
 * 
 */
package com.dim.ArcBall;

/**
 * A 4-element tuple represented by single-precision floating point x,y,z,w coordinates
 * @author dim
 *
 */
public class Quat4f_t {
	private float x;
	private float y;
	private float z;
	private float w;
	
	
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public float getZ() {
		return z;
	}
	public void setZ(float z) {
		this.z = z;
	}
	public float getW() {
		return w;
	}
	public void setW(float w) {
		this.w = w;
	}
}
