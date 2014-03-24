package com.brashmonkey.spriter;

public class SpriterPoint {
	
	public float x,y;
	
	public SpriterPoint(float x, float y){
		this.set(x, y);
	}
	
	public void translate(float x, float y){
		this.x += x;
		this.y += y;
	}
	
	public void set(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public void scale(float x, float y){
		this.x *= x;
		this.y *= y;
	}
	
	public void set(SpriterPoint point){
		this.set(point.x, point.y);
	}
	
	public void translate(SpriterPoint point){
		this.translate(point.x, point.y);
	}
	
	public void scale(SpriterPoint point){
		this.scale(point.x, point.y);
	}
	
	public void rotate(float degrees){
		double radians = Math.toRadians(degrees);
		float cos = (float)Math.cos(radians);
		float sin = (float)Math.sin(radians);
		
		float xx = x*cos-y*sin;
		float yy = x*sin+y*cos;
		
		this.x = xx;
		this.y = yy;
	}
	
	public String toString(){
		return "["+x+","+y+"]";
	}

}
