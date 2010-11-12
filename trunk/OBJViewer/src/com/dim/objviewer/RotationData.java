package com.dim.objviewer;

public class RotationData {
	public float ROTATION_SPEED;
	public float rotation = 0;	
	public boolean rotmode = false;
	public boolean turnLeft = false;
	
	public char axis = '\0';
	public int axArr[] = {0,0,0};
	
	//MouseStuff
	public int dragStartX, dragStartY;
	double viewRotX, viewRotY;
	
	public RotationData(float speed){
		this.ROTATION_SPEED = speed;
	}
	
	public int[] getAxis(){
		return axArr;
	}
	
	public void rotateGl(boolean left, char axis) {
		this.turnLeft = left;
		
		if (left) {
			rotation += this.ROTATION_SPEED;
		} else {
			rotation -= this.ROTATION_SPEED;
		}
		
		//Float-Overflow Abfangen!

		switch (axis) {
		case 'x':
			//gl.glRotatef(rotation, 1, 0, 0);
			axArr[0] = 1; axArr[1] = 0; axArr[2] = 0;
			break;
		case 'y':
			//gl.glRotatef(rotation, 0, 1, 0);
			axArr[0] = 0; axArr[1] = 1; axArr[2] = 0;
			break;
		case 'z':
			//gl.glRotatef(rotation, 0, 0, 1);
			axArr[0] = 0; axArr[1] = 0; axArr[2] = 1;
			break;
		default:
			axArr[0] = 0; axArr[1] = 0; axArr[2] = 0;
			//gl.glRotatef(0, 0, 0, 0);
			break;
		}
	}
	
	
}
