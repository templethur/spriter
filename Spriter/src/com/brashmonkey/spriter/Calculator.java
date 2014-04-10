package com.brashmonkey.spriter;

import static java.lang.Math.*;

/**
 * A class which provides methods to calculate Spriter specific issues,
 * like linear interpolation and rotation around a parent object.
 * Other interpolation types are coming with the next releases of Spriter.
 * 
 * @author Trixt0r
 *
 */

public class Calculator {
	
	public static float PI = (float)Math.PI;
	
	/**
	 * Calculates interpolated value for positions and scale.
	 * @param a first value
	 * @param b second value
	 * @param timeA first time
	 * @param timeB second time
	 * @param currentTime
	 * @return interpolated value between a and b.
	 */
	public static float calculateInterpolation(float a, float b, float timeA, float timeB, float currentTime) {
		return a + (b - a) * getNormalizedTime(timeA, timeB, currentTime);
	}
	
	public static float getNormalizedTime(float startTime, float endTime, float currentTime){
		return (currentTime - startTime)/(endTime - startTime);
	}

	/**
	 * Calculates interpolated value for angles.
	 * @param a first angle
	 * @param b second angle
	 * @param timeA first time
	 * @param timeB second time
	 * @param currentTime
	 * @return interpolated angle
	 */
	public static float calculateAngleInterpolation(float a, float b, float timeA, float timeB, float currentTime) {
		return a + (angleDifference(b, a) * ((currentTime - timeA) / (timeB - timeA)));
	}
	
	/**
	 * Calculates the smallest difference between angle a and b.
	 * @param a first angle (in degrees)
	 * @param b second angle (in degrees)
	 * @return Smallest difference between a and b (between 180° and -180°).
	 */
	public static float angleDifference(float a, float b){
		return ((((a - b) % 360) + 540) % 360) - 180;
	}
	
	/**
	 * @param x1 x coordinate of first point.
	 * @param y1 y coordinate of first point.
	 * @param x2 x coordinate of second point.
	 * @param y2 y coordinate of second point.
	 * @return Angle between the two given points.
	 */
	public static float angleBetween(float x1, float y1, float x2, float y2){
	    return (float)toDegrees(atan2(y2-y1,x2-x1));
	}

	/**
	 * @param x1 x coordinate of first point.
	 * @param y1 y coordinate of first point.
	 * @param x2 x coordinate of second point.
	 * @param y2 y coordinate of second point.
	 * @return Distance between the two given points.
	 */
	public static float distanceBetween(float x1, float y1, float x2, float y2){
		float xDiff = x2-x1;
		float yDiff = y2-y1;
	    return (float)sqrt(xDiff*xDiff+yDiff*yDiff);
	}
	
	/**
	 * Solves the equation a*x^3 + b*x^2 + c*x +d = 0
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	public static Float solveCubic(float a, float b, float c, float d) {
        if (a == 0) return solveQuadratic(b, c, d);
        if (d == 0) return 0f;

        b /= a;
        c /= a;
        d /= a;
        float squaredB = squared(b);
        float q = (3f * c - squaredB) / 9f;
        float r = (-27f * d + b * (9f * c - 2f * squaredB)) / 54f;
        float disc = cubed(q) + squared(r);
        float term1 = b / 3f;

        if (disc > 0) {
            float s = r + sqrt(disc);
            s = (s < 0) ? -cubicRoot(-s) : cubicRoot(s);
            float t = r - sqrt(disc);
            t = (t < 0) ? -cubicRoot(-t) : cubicRoot(t);

            float result = -term1 + s + t;
            if (result >= 0 && result <= 1) return result;
        } else if (disc == 0) {
            float r13 = (r < 0) ? -cubicRoot(-r) : cubicRoot(r);

            float result = -term1 + 2f * r13;
            if (result >= 0 && result <= 1) return result;

            result = -(r13 + term1);
            if (result >= 0 && result <= 1) return result;
        } else {
            q = -q;
            float dum1 = q * q * q;
            dum1 = acos(r / sqrt(dum1));
            float r13 = 2f * sqrt(q);

            float result = -term1 + r13 * cos(dum1 / 3f);
            if (result >= 0 && result <= 1) return result;

            result = -term1 + r13 * cos((dum1 + 2f * PI) / 3f);
            if (result >= 0 && result <= 1) return result;

            result = -term1 + r13 * cos((dum1 + 4f * PI) / 3f);
            if (result >= 0 && result <= 1) return result;
        }

        return null;
    }
	
	/**
	 * Solves the equation a*x^2 + b*x + c = 0
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static Float solveQuadratic(float a, float b, float c) {
		float squaredB = squared(b);
		float twoA = 2 * a;
		float fourAC = 4 * a * c;
        float result = (-b + sqrt(squaredB - fourAC)) / twoA;
        if (result >= 0 && result <= 1) return result;

        result = (-b - sqrt(squaredB - fourAC)) / twoA;
        if (result >= 0 && result <= 1) return result;

        return null;
    }
	
	public static float squared(float f) { return f * f; }
	public static float cubed(float f) { return f * f * f; }
	public static float cubicRoot(float f) { return (float) pow(f, 1f / 3f); }
	public static float sqrt(float x){ return (float)Math.sqrt(x); }
	public static float acos(float x){ return (float)Math.acos(x); }
	
	static private final int SIN_BITS = 14; // 16KB. Adjust for accuracy.
	static private final int SIN_MASK = ~(-1 << SIN_BITS);
	static private final int SIN_COUNT = SIN_MASK + 1;

	static private final float radFull = PI * 2;
	static private final float degFull = 360;
	static private final float radToIndex = SIN_COUNT / radFull;
	static private final float degToIndex = SIN_COUNT / degFull;

	/** multiply by this to convert from radians to degrees */
	static public final float radiansToDegrees = 180f / PI;
	static public final float radDeg = radiansToDegrees;
	/** multiply by this to convert from degrees to radians */
	static public final float degreesToRadians = PI / 180;
	static public final float degRad = degreesToRadians;

	static private class Sin {
		static final float[] table = new float[SIN_COUNT];
		static {
			for (int i = 0; i < SIN_COUNT; i++)
				table[i] = (float)Math.sin((i + 0.5f) / SIN_COUNT * radFull);
			for (int i = 0; i < 360; i += 90)
				table[(int)(i * degToIndex) & SIN_MASK] = (float)Math.sin(i * degreesToRadians);
		}
	}

	/** Returns the sine in radians from a lookup table. */
	static public final float sin (float radians) {
		return Sin.table[(int)(radians * radToIndex) & SIN_MASK];
	}

	/** Returns the cosine in radians from a lookup table. */
	static public final float cos (float radians) {
		return Sin.table[(int)((radians + PI / 2) * radToIndex) & SIN_MASK];
	}

	/** Returns the sine in radians from a lookup table. */
	static public final float sinDeg (float degrees) {
		return Sin.table[(int)(degrees * degToIndex) & SIN_MASK];
	}

	/** Returns the cosine in radians from a lookup table. */
	static public final float cosDeg (float degrees) {
		return Sin.table[(int)((degrees + 90) * degToIndex) & SIN_MASK];
	}

}
