/**************************************************************************
 * Copyright 2013 by Trixt0r
 * (https://github.com/Trixt0r, Heinrich Reich, e-mail: trixter16@web.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
***************************************************************************/

package com.brashmonkey.spriter;

import com.brashmonkey.spriter.objects.SpriterAbstractObject;
import static java.lang.Math.*;

/**
 * A class which provides methods to calculate Spriter specific issues,
 * like linear interpolation and rotation around a parent object.
 * Other interpolation types are coming with the next releases of Spriter.
 * 
 * @author Trixt0r
 *
 */

public class SpriterCalculator {
	
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
	 * Rotates the given child around the given parent.
	 * @param parent
	 * @param child
	 */
	public static void translateRelative(SpriterAbstractObject parent, SpriterAbstractObject child) {
		translateRelative(parent, child.getX(), child.getY(), child);
	}
	
	/**
	 * Rotates the given point around the given parent.
	 * @param parent
	 * @param x
	 * @param y
	 * @param target save new position in
	 */
	public static void translateRelative(SpriterAbstractObject parent, float x, float y, SpriterAbstractObject target) {

		float px = x * (parent.getScaleX());
		float py = y * (parent.getScaleY());

		float s = sin((float) toRadians(parent.getAngle()));
		float c = cos((float) toRadians(parent.getAngle()));
		float xnew = (px * c) - (py * s);
		float ynew = (px * s) + (py * c);
		xnew += parent.getX();
		ynew += parent.getY();
		
		target.setX(xnew);
		target.setY(ynew);
	}
	
	public static void reTranslateRelative(SpriterAbstractObject parent, SpriterAbstractObject child){
		reTranslateRelative(parent, child.getX(), child.getY(), child);
	}
	
	public static void reTranslateRelative(SpriterAbstractObject parent, float x, float y, SpriterAbstractObject target){
		target.setAngle(target.getAngle()-parent.getAngle());
		target.setScaleX(target.getScaleX()/parent.getScaleX());
		target.setScaleY(target.getScaleY()/parent.getScaleY());
		float xx = x - parent.getX(), yy = y - parent.getY();
		float angle = (float) toRadians(parent.getAngle()); 
		float cos = cos(angle);
		float sin = sin(angle);
		float newX = yy * sin + xx * cos;
		float newY = yy * cos - xx * sin;
		target.setX(newX/parent.getScaleX()); target.setY(newY/parent.getScaleY());
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
	    return (float)sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
	}
	
	public static Float solveCubic(float a, float b, float c, float d) {
        if (a == 0) return solveQuadratic(b, c, d);
        if (d == 0) return 0f;

        b /= a;
        c /= a;
        d /= a;
        float q = (3f * c - Squared(b)) / 9f;
        float r = (-27f * d + b * (9f * c - 2f * Squared(b))) / 54f;
        float disc = Cubed(q) + Squared(r);
        float term1 = b / 3f;

        if (disc > 0) {
            float s = r + sqrt(disc);
            s = (s < 0) ? -CubicRoot(-s) : CubicRoot(s);
            float t = r - sqrt(disc);
            t = (t < 0) ? -CubicRoot(-t) : CubicRoot(t);

            float result = -term1 + s + t;
            if (result >= 0 && result <= 1) return result;
        } else if (disc == 0) {
            float r13 = (r < 0) ? -CubicRoot(-r) : CubicRoot(r);

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
	
	public static Float solveQuadratic(float a, float b, float c) {
        float result = (-b + sqrt(Squared(b) - 4 * a * c)) / (2 * a);
        if (result >= 0 && result <= 1) return result;

        result = (-b - sqrt(Squared(b) - 4 * a * c)) / (2 * a);
        if (result >= 0 && result <= 1) return result;

        return null;
    }
	
	public static float Squared(float f) { return f * f; }
	public static float Cubed(float f) { return f * f * f; }
	public static float CubicRoot(float f) { return (float) pow(f, 1f / 3f); }
	public static float sqrt(float x){ return (float)Math.sqrt(x); }
	public static float cos(float x){ return (float)Math.cos(x); }
	public static float sin(float x){ return (float)Math.sin(x); }
	public static float acos(float x){ return (float)Math.acos(x); }

}
