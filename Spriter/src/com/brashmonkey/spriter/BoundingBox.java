package com.brashmonkey.spriter;

import com.brashmonkey.spriter.Entity.ObjectInfo;

public class BoundingBox {
	public final Point[] points, temp;
	private Rectangle rect;
	
	public BoundingBox(){
		this.points = new Point[4];
		this.temp = new Point[4];
		for(int i = 0; i < 4; i++){
			this.points[i] = new Point(0,0);
			this.temp[i] = new Point(0,0);
		}
		this.rect = new Rectangle(0,0,0,0);
	}
	
	public void calcFor(Timeline.Key.Bone bone, ObjectInfo info){
		float width = info.size.width*bone.scale.x;
		float height = info.size.height*bone.scale.y;
	
		float pivotX = width*bone.pivot.x;
		float pivotY = height*bone.pivot.y;
		
		this.points[0].set(-pivotX,-pivotY);
		this.points[1].set(width-pivotX, -pivotY);
		this.points[2].set(-pivotX,height-pivotY);
		this.points[3].set(width-pivotX,height-pivotY);
		
		for(int i = 0; i < 4; i++)
			this.points[i].rotate(bone.angle);
		for(int i = 0; i < 4; i++)
			this.points[i].translate(bone.position);
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
	
	public boolean isInside(Rectangle rect){
		boolean inside = false;
		for(Point p: points)
			inside |= rect.isInside(p);
		return inside;
	}
	
	public Rectangle getBoundingRect(){
		this.rect.set(points[0].x,points[0].y,points[0].x,points[0].y);
		this.rect.left = Math.min(Math.min(Math.min(Math.min(points[0].x, points[1].x),points[2].x),points[3].x), this.rect.left);
		this.rect.right = Math.max(Math.max(Math.max(Math.max(points[0].x, points[1].x),points[2].x),points[3].x), this.rect.right);
		this.rect.top = Math.max(Math.max(Math.max(Math.max(points[0].y, points[1].y),points[2].y),points[3].y), this.rect.top);
		this.rect.bottom = Math.min(Math.min(Math.min(Math.min(points[0].y, points[1].y),points[2].y),points[3].y), this.rect.bottom);
		return this.rect;
	}

}
