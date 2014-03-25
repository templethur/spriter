package com.brashmonkey.spriter;

public class Point {
	
	public float x,y;
	
	public Point(float x, float y){
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
	
	public void set(Point point){
		this.set(point.x, point.y);
	}
	
	public void translate(Point point){
		this.translate(point.x, point.y);
	}
	
	public void scale(Point point){
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
