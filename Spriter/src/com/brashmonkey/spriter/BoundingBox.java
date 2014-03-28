package com.brashmonkey.spriter;

import com.brashmonkey.spriter.Entity.ObjectInfo;

public class BoundingBox {
	public final Point[] boundingPoints, temp;
	private Rectangle boundingRect;
	
	public BoundingBox(){
		this.boundingPoints = new Point[4];
		this.temp = new Point[4];
		for(int i = 0; i < 4; i++){
			this.boundingPoints[i] = new Point(0,0);
			this.temp[i] = new Point(0,0);
		}
		this.boundingRect = new Rectangle(0,0,0,0);
	}
	
	public void calcFor(Timeline.Key.Bone bone, ObjectInfo info){
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
		
		Point point = new Point(x-bone.position.x,y-bone.position.y);
		point.rotate(-bone.angle);
		
		return point.x >= -pivotX && point.x <= width-pivotX && point.y >= -pivotY && point.y <= height-pivotY;
	}
	
	public Rectangle getBoundingRect(){
		this.boundingRect.set(boundingPoints[0].x,boundingPoints[0].y,boundingPoints[0].x,boundingPoints[0].y);
		this.boundingRect.left = Math.min(Math.min(Math.min(Math.min(boundingPoints[0].x, boundingPoints[1].x),boundingPoints[2].x),boundingPoints[3].x), this.boundingRect.left);
		this.boundingRect.right = Math.max(Math.max(Math.max(Math.max(boundingPoints[0].x, boundingPoints[1].x),boundingPoints[2].x),boundingPoints[3].x), this.boundingRect.right);
		this.boundingRect.top = Math.max(Math.max(Math.max(Math.max(boundingPoints[0].y, boundingPoints[1].y),boundingPoints[2].y),boundingPoints[3].y), this.boundingRect.top);
		this.boundingRect.bottom = Math.min(Math.min(Math.min(Math.min(boundingPoints[0].y, boundingPoints[1].y),boundingPoints[2].y),boundingPoints[3].y), this.boundingRect.bottom);
		return this.boundingRect;
	}

}
