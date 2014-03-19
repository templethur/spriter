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

package com.brashmonkey.spriter.objects;

import com.discobeard.spriter.dom.Entity.ObjectInfo;

/**
 * A SpriterAbstractObject is, as the name says, an abstract object which holds the same properties a #SpriterObject and a #SpriterBone have.
 * Such as x,y coordinates, angle, id, parent, scale and the timeline.
 * @author Trixt0r
 */
public abstract class SpriterAbstractObject extends SpriterPoint{
	protected float scaleX, scaleY;
	public ObjectInfo info;

	public SpriterAbstractObject(){
		super();
		this.scaleX = 1f;
		this.scaleY = 1f;
	}

	/**
	 * @return the scaleX
	 */
	public float getScaleX() {
		return scaleX;
	}

	/**
	 * @param scaleX the scaleX to set
	 */
	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	/**
	 * @return the scaleY
	 */
	public float getScaleY() {
		return scaleY;
	}

	/**
	 * @param scaleY the scaleY to set
	 */
	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	/**
	 * Sets the values of this instance to the given one.
	 * @param object which has to be manipulated.
	 */
	public void copyValuesTo(SpriterAbstractObject object){
		super.copyValuesTo(object);
		object.setScaleX(scaleX);
		object.setScaleY(scaleY);
		object.info = info;
	}
}
