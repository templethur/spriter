package com.brashmonkey.spriter.update;

import com.brashmonkey.spriter.SpriterPoint;
import com.brashmonkey.spriter.SpriterRectangle;
import com.brashmonkey.spriter.update.Entity.ObjectInfo;

public class BoundingBox {
	public final SpriterPoint[] boundingPoints, temp;
	private SpriterRectangle boundingRect;
	
	public BoundingBox(){
		this.boundingPoints = new SpriterPoint[4];
		this.temp = new SpriterPoint[4];
		for(int i = 0; i < 4; i++){
			this.boundingPoints[i] = new SpriterPoint(0,0);
			this.temp[i] = new SpriterPoint(0,0);
		}
		this.boundingRect = new SpriterRectangle(0,0,0,0);
	}
	
	public void updateFor(Timeline.Key.Bone bone, ObjectInfo info){
		float width = info.size.width*bone.scale.x;
		float height = info.size.height*bone.scale.y;
	
		float pivotX = width*bone.pivot.x;
		float pivotY = height*bone.pivot.y;
		
		this.boundingPoints[0].set(-pivotX,-pivotY);
		this.boundingPoints[1].set(width-pivotX, -pivotY);
		this.boundingPoints[2].set(-pivotX,height-pivotY);
		this.boundingPoints[3].set(width-pivotX,height-pivotY);
		
		for(int i = 0; i < 4; i++)
			this.boundingPoints[i].rotate(bone.angle);
		for(int i = 0; i < 4; i++)
			this.boundingPoints[i].translate(bone.position);
	}
	
	public boolean collides(Timeline.Key.Bone bone, ObjectInfo info, float x, float y){
		float width = info.size.width*bone.scale.x;
		float height = info.size.height*bone.scale.y;
		
		float pivotX = width*bone.pivot.x;
		float pivotY = height*bone.pivot.y;
		
		SpriterPoint point = new SpriterPoint(x-bone.position.x,y-bone.position.y);
		point.rotate(-bone.angle);
		
		return point.x >= -pivotX && point.x <= width-pivotX && point.y >= -pivotY && point.y <= height-pivotY;
	}
	
	public SpriterRectangle getBoundingRect(){
		this.boundingRect.set(boundingPoints[0].x,boundingPoints[0].y,boundingPoints[0].x,boundingPoints[0].y);
		this.boundingRect.left = Math.min(Math.min(Math.min(Math.min(boundingPoints[0].x, boundingPoints[1].x),boundingPoints[2].x),boundingPoints[3].x), this.boundingRect.left);
		this.boundingRect.right = Math.max(Math.max(Math.max(Math.max(boundingPoints[0].x, boundingPoints[1].x),boundingPoints[2].x),boundingPoints[3].x), this.boundingRect.right);
		this.boundingRect.top = Math.max(Math.max(Math.max(Math.max(boundingPoints[0].y, boundingPoints[1].y),boundingPoints[2].y),boundingPoints[3].y), this.boundingRect.top);
		this.boundingRect.bottom = Math.min(Math.min(Math.min(Math.min(boundingPoints[0].y, boundingPoints[1].y),boundingPoints[2].y),boundingPoints[3].y), this.boundingRect.bottom);
		return this.boundingRect;
	}

}
