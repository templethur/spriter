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

package com.brashmonkey.spriter.player;

import com.brashmonkey.spriter.animation.SpriterKeyFrame;
import com.brashmonkey.spriter.draw.DrawInstruction;
import com.brashmonkey.spriter.interpolation.SpriterCurve;
import com.brashmonkey.spriter.objects.SpriterAbstractObject;
import com.brashmonkey.spriter.objects.SpriterObject;

/**
 * This class is made to interpolate between two running animations.
 * The idea is, to give an instance of this class two AbstractSpriterPlayer objects which hold and animate the same spriter entity.
 * This will interpolate the runtime transformations of the bones and objects with a weight between 0 and 1.
 * You will be also able to interpolate SpriterPlayerInterpolators with each other, since it extends  #SpriterAbstractPlayer.
 * Note that this #SpriterAbstractPlayer needs 3 times more calculation effort than a normal #SpriterPlayer.
 * 
 * @author Trixt0r
 */
public class SpriterPlayerInterpolator extends SpriterAbstractPlayer{
	
	private SpriterAbstractPlayer first, second;
	private float weight;
	public boolean updatePlayers = true;
	private SpriterCurve curve = new SpriterCurve();

	/**
	 * Returns an instance of this class, which will manage the interpolation between two #SpriterAbstractPlayer instances.
	 * @param first player to interpolate with the second one.
	 * @param second player to interpolate with the first one.
	 */
	public SpriterPlayerInterpolator(SpriterAbstractPlayer first, SpriterAbstractPlayer second){
		super(first.loader, first.animations);
		this.weight = 0.5f;
		setPlayers(first, second);
		this.generateData();
		this.update(0, 0);
	}
	
	/**
	 * Note: Make sure, that both instances hold the same bone and object structure.
	 * Otherwise you will not get the interpolation you wish.
	 * @param first SpriterPlayer instance to interpolate.
	 * @param second SpriterPlayer instance to interpolate.
	 */
	public void setPlayers(SpriterAbstractPlayer first, SpriterAbstractPlayer second){
		this.first = first;
		this.second = second;
		this.moddedBones = this.first.moddedBones;
		this.moddedObjects = this.first.moddedObjects;
		this.first.setRootParent(this.rootParent);
		this.second.setRootParent(this.rootParent);
	}
	
	/**
	 * @param weight to set. 0 means the animation of the first player will get played back.
	 * 1 means the second player will get played back.
	 */
	public void setWeight(float weight){
		this.weight = weight;
	}
	
	/**
	 * @return The current weight.
	 */
	public float getWeight(){
		return this.weight;
	}
	
	/**
	 * @return The first player.
	 */
	public SpriterAbstractPlayer getFirst(){
		return this.first;
	}
	
	/**
	 * @return The second player.
	 */
	public SpriterAbstractPlayer getSecond(){
		return this.second;
	}
	
	@Override
	protected void step(float xOffset, float yOffset){
		int firstLastSpeed = first.frameSpeed, secondLastSpeed = second.frameSpeed;
		//int speed = this.frameSpeed;
		//if(this.interpolateSpeed)	speed = (int)this.interpolate(first.frameSpeed, second.frameSpeed, 0, 1, this.weight);
		//this.first.frameSpeed = speed;
		//this.second.frameSpeed = speed;
		
		this.moddedBones = (this.weight <= 0.5f) ? this.first.moddedBones: this.second.moddedBones;
		this.moddedObjects = (this.weight <= 0.5f) ? this.first.moddedObjects: this.second.moddedObjects;
		this.currenObjectsToDraw = Math.max(first.currenObjectsToDraw, second.currenObjectsToDraw);
		this.currentBonesToAnimate =  Math.max(first.currentBonesToAnimate, second.currentBonesToAnimate);
		if(this.updatePlayers){
			this.first.update(xOffset, yOffset);
			this.second.update(xOffset, yOffset);
		}
	
		SpriterKeyFrame key1 = (first.transitionFixed) ? first.lastFrame: first.lastTempFrame;
		SpriterKeyFrame key2 = (second.transitionFixed) ? second.lastFrame: second.lastTempFrame;
		this.transformBones(key1, key2, xOffset, yOffset);
		this.transformObjects(first.lastFrame, second.lastFrame, xOffset, yOffset);
		this.first.frameSpeed = firstLastSpeed;
		this.second.frameSpeed = secondLastSpeed;
	}
	
	@Override
	protected void setInstructionRef(DrawInstruction dI, SpriterObject obj1, SpriterObject obj2){
		dI.ref = (this.weight <= 0.5f || obj2 == null) ? obj1.getRef(): obj2.getRef();
		dI.obj = (this.weight <= 0.5f || obj2 == null) ? obj1: obj2;
	}

	@Override
	protected void interpolateAbstractObject(SpriterAbstractObject target, SpriterAbstractObject obj1, SpriterAbstractObject obj2, float t){
		if(obj2 == null) return;
		target.setX(curve.tween(obj1.getX(), obj2.getX(), this.weight));
		target.setY(curve.tween(obj1.getY(), obj2.getY(), this.weight));
		target.setScaleX(curve.tween(obj1.getScaleX(), obj2.getScaleX(), this.weight));
		target.setScaleY(curve.tween(obj1.getScaleY(), obj2.getScaleY(), this.weight));
		//target.setAngle(SpriterCalculator.calculateAngleInterpolation(obj1.getAngle(), obj2.getAngle(), 0, 1, this.weight));
		target.setAngle(curve.tweenAngle(obj1.getAngle(), obj2.getAngle(), this.weight/*, (int) -SpriterCalculator.angleDifference(obj1.getAngle(), obj2.getAngle())*/));
	}

	@Override
	protected void interpolateSpriterObject(SpriterObject target, SpriterObject obj1, SpriterObject obj2, float t){
		if(obj2 == null) return;
		this.interpolateAbstractObject(target, obj1, obj2, this.weight);
		target.setPivotX((curve.tween(obj1.getPivotX(), obj2.getPivotX(), this.weight)));
		target.setPivotY((curve.tween(obj1.getPivotY(), obj2.getPivotY(), this.weight)));
		target.setAlpha((curve.tween(obj1.getAlpha(), obj2.getAlpha(), this.weight)));
	}
}
