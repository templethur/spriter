package com.brashmonkey.spriter;

public class SpriterDimension {
	
	public float width, height;
	
	public SpriterDimension(float width, float height){
		this.width = width;
		this.height = height;
	}
	
	public void set(float width, float height){
		this.width = width;
		this.height = height;
	}
	
	public void set(SpriterDimension size){
		this.set(size.width, size.height);
	}
	
	public String toString(){
		return "["+width+"x"+height+"]";
	}

}
