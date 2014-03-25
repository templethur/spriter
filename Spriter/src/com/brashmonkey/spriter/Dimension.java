package com.brashmonkey.spriter;

public class Dimension {
	
	public float width, height;
	
	public Dimension(float width, float height){
		this.width = width;
		this.height = height;
	}
	
	public void set(float width, float height){
		this.width = width;
		this.height = height;
	}
	
	public void set(Dimension size){
		this.set(size.width, size.height);
	}
	
	public String toString(){
		return "["+width+"x"+height+"]";
	}

}
