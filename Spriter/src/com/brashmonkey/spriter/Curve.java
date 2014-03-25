package com.brashmonkey.spriter;

import static  com.brashmonkey.spriter.Calculator.*;
import static com.brashmonkey.spriter.Interpolator.*;

/**
 * Represents a curve in Spriter.
 * An instance of this class is responsible for tweening given data.
 * @author Trixt0r
 *
 */
public class Curve {
	
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
	
	private Type type;
	public Curve subCurve;
	public final Constraints constraints = new Constraints(0, 0, 0, 0);
	
	public Curve(){
		this(Type.Linear);
	}
	
	public Curve(Type type){
		this(type, null);
	}
	
	public Curve(Type type, Curve subCurve){
		this.setType(type);
		this.subCurve = subCurve;
	}
	
	public void setType(Type type){
		if(type == null) throw new NullPointerException("The type of a curve cannot be null!");
		this.type = type;
	}
	
	public Type getType(){
		return this.type;
	}

	
	private float lastCubicSolution = 0f;
	public float tween(float a, float b, float t){
		t = tweenSub(0f,1f,t);
		switch(type){
		case Instant: return a;
		case Linear: return linear(a, b, t);
		case Quadratic: return quadratic(a, linear(a, b, constraints.c1), b, t);
		case Cubic: return cubic(a, linear(a, b, constraints.c1), linear(a, b, constraints.c2), b, t);
		case Quartic: return quartic(a, linear(a, b, constraints.c1), linear(a, b, constraints.c2),	linear(a, b, constraints.c3), b, t);
		case Quintic: return quintic(a, linear(a, b, constraints.c1), linear(a, b, constraints.c2),	linear(a, b, constraints.c3), linear(a, b, constraints.c4), b, t);
		case Bezier: Float cubicSolution = solveCubic(3f*(constraints.c1-constraints.c3) + 1f, 3f*(constraints.c3-2f*constraints.c1), 3f*constraints.c1, -t);
					 if(cubicSolution == null) cubicSolution = lastCubicSolution;
					 else lastCubicSolution = cubicSolution;
					 return linear(a, b, bezier(cubicSolution, 0f, constraints.c2, constraints.c4, 1f));
		default: return linear(a, b, t);
		}
	}
	
	public void tweenPoint(Point a, Point b, float t, Point target){
		target.set(this.tween(a.x, b.x, t), this.tween(a.y, b.y, t));
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
		switch(type){
		case Instant: return a;
		case Quadratic: return quadraticAngle(a, linearAngle(a, b, constraints.c1), b, t);
		case Cubic: return cubicAngle(a, linearAngle(a, b, constraints.c1), linearAngle(a, b, constraints.c2), b, t);
		case Quartic: return quarticAngle(a, linearAngle(a, b, constraints.c1), linearAngle(a, b, constraints.c2),	linearAngle(a, b, constraints.c3), b, t);
		case Quintic: return quinticAngle(a, linearAngle(a, b, constraints.c1), linearAngle(a, b, constraints.c2),	linearAngle(a, b, constraints.c3), linearAngle(a, b, constraints.c4), b, t);
		case Bezier: Float cubicSolution = solveCubic(3f*(constraints.c1-constraints.c3) + 1f, 3f*(constraints.c3-2f*constraints.c1), 3f*constraints.c1, -t);
					 if(cubicSolution == null) cubicSolution = lastCubicSolution;
					 else lastCubicSolution = cubicSolution;
					 return linearAngle(a, b, bezier(cubicSolution, 0f, constraints.c2, constraints.c4, 1f));
		default: return linearAngle(a, b, t);
		}
	}
	
	public String toString(){
		return getClass().getSimpleName()+"|["+type+":"+constraints+", subCurve: "+subCurve+"]";
	}
	
	public static class Constraints{
		public float c1, c2, c3, c4;
		
		public Constraints(float c1, float c2, float c3, float c4){
			this.set(c1, c2, c3, c4);
		}
		
		public void set(float c1, float c2, float c3, float c4){
			this.c1 = c1;
			this.c2 = c2;
			this.c3 = c3;
			this.c4 = c4;
		}
		
		public String toString(){
			return getClass().getSimpleName()+"| [c1:"+c1+", c2:"+c2+", c3:"+c3+", c4:"+c4+"]";
		}
	}

}
