package com.dim.ArcBall;

/**
 * A 2 element point that is represented by single precision floating point x,y coordinates
 * @author dim
 *
 */
public class Tuple2f_t {
	private float x;
	private float y;
	
	public Tuple2f_t(float x , float y){
		this.x = x;
		this.y = y;
	}

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
	
	
}
