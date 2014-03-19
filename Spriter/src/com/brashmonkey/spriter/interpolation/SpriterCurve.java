package com.brashmonkey.spriter.interpolation;

import static com.brashmonkey.spriter.interpolation.SpriterInterpolator.*;
import static  com.brashmonkey.spriter.SpriterCalculator.*;

/**
 * Represents a curve in Spriter.
 * An instance of this class is responsible for tweening given data.
 * @author Trixt0r
 *
 */
public class SpriterCurve {
	
	public static enum Type {
		Instant, Linear, Quadratic, Cubic, Quartic, Quintic, Bezier;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static Type getType(String name){
		switch(name){
		case "instant": return Type.Instant;
		case "quadratic": return Type.Quadratic;
		case "cubic": return Type.Cubic;
		case "quartic": return Type.Quartic;
		case "quintic": return Type.Quintic;
		case "bezier": return Type.Bezier;
		default: return Type.Linear;
		}
	}
	
	private Type curveType;
	public SpriterCurve subCurve;
	public float c1, c2, c3, c4;
	
	public SpriterCurve(){
		this(Type.Linear);
	}
	
	public SpriterCurve(Type type){
		this(type, null);
	}
	
	public SpriterCurve(Type type, SpriterCurve subCurve){
		this.setCurveType(type);
		this.subCurve = subCurve;
	}
	
	public void setCurveType(Type type){
		if(type == null) throw new NullPointerException("The type of a curve cannot be null!");
		this.curveType = type;
	}
	
	public Type getType(){
		return this.curveType;
	}

	
	private float lastCubicSolution = 0f;
	public float tween(float a, float b, float t){
		t = tweenSub(0f,1f,t);
		switch(curveType){
		case Instant: return a;
		case Linear: return linear(a, b, t);
		case Quadratic: return quadratic(a, linear(a, b, c1), b, t);
		case Cubic: return cubic(a, linear(a, b, c1), linear(a, b, c2), b, t);
		case Quartic: return quartic(a, linear(a, b, c1), linear(a, b, c2),	linear(a, b, c3), b, t);
		case Quintic: return quintic(a, linear(a, b, c1), linear(a, b, c2),	linear(a, b, c3), linear(a, b, c4), b, t);
		case Bezier: Float cubicSolution = solveCubic(3f*(c1-c3) + 1f, 3f*(c3-2f*c1), 3f*c1, -t);
					 if(cubicSolution == null) cubicSolution = lastCubicSolution;
					 else lastCubicSolution = cubicSolution;
					 return linear(a, b, bezier(cubicSolution, 0f, c2, c4, 1f));
		default: return linear(a, b, t);
		}
	}
	
	private float tweenSub(float a, float b, float t){
		if(this.subCurve != null) return subCurve.tween(a, b, t);
		else return t;
	}
	
	public float tweenAngle(float a, float b, float t, int spin){
	    if(spin>0)
	    {
	        if((b-a)<0)
	            b+=360;
	    }
	    else if(spin<0)
	    {
	        if((b-a)>0)
	            b-=360;
	    } else return a;

	    return tween(a, b, t);
	}
	
	public float tweenAngle(float a, float b, float t){
		t = tweenSub(0f,1f,t);
		switch(curveType){
		case Instant: return a;
		case Quadratic: return quadraticAngle(a, linearAngle(a, b, c1), b, t);
		case Cubic: return cubicAngle(a, linearAngle(a, b, c1), linearAngle(a, b, c2), b, t);
		case Quartic: return quarticAngle(a, linearAngle(a, b, c1), linearAngle(a, b, c2),	linearAngle(a, b, c3), b, t);
		case Quintic: return quinticAngle(a, linearAngle(a, b, c1), linearAngle(a, b, c2),	linearAngle(a, b, c3), linearAngle(a, b, c4), b, t);
		case Bezier: Float cubicSolution = solveCubic(3f*(c1-c3) + 1f, 3f*(c3-2f*c1), 3f*c1, -t);
					 if(cubicSolution == null) cubicSolution = lastCubicSolution;
					 else lastCubicSolution = cubicSolution;
					 return linearAngle(a, b, bezier(cubicSolution, 0f, c2, c4, 1f));
		default: return linearAngle(a, b, t);
		}
	}

}
