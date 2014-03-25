package com.brashmonkey.spriter.update;

import com.brashmonkey.spriter.SpriterException;
import com.brashmonkey.spriter.SpriterPoint;

public class IKObject extends SpriterPoint {
	
	public int chainLength, iterations;

	public IKObject(float x, float y, int length, int iterations) {
		super(x, y);
		this.setLength(length);
		this.setIterations(iterations);
	}
	
	public void setLength(int chainLength){
		if(chainLength < 0) throw new SpriterException("The chain has to be at least 0!");
		this.chainLength = chainLength;
	}
	
	public void setIterations(int iterations){
		if(iterations < 0) throw new SpriterException("The number of iterations has to be at least 1!");
		this.iterations = iterations;
	}

}
