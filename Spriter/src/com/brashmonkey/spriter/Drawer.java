package com.brashmonkey.spriter;

import java.util.Iterator;

import com.brashmonkey.spriter.Entity.CharacterMap;
import com.brashmonkey.spriter.Entity.ObjectInfo;
import com.brashmonkey.spriter.Entity.ObjectType;
import com.brashmonkey.spriter.Timeline.Key.Bone;
import com.brashmonkey.spriter.Timeline.Key.Object;

public abstract class Drawer<R> {
	
	public float pointRadius = 5f;
	//private final BoundingBox box;
	protected Loader<R> loader;
	
	public Drawer(Loader<R> loader){
		this.loader = loader;
		//this.box = new BoundingBox();
	}
	
	public void setLoader(Loader<R> loader){
		if(loader == null) throw new SpriterException("The loader instance can not be null!");
		this.loader = loader;
	}
	
	public void drawBones(Player player){
		this.setColor(1, 0, 0, 1);
		for(Mainline.Key.BoneRef ref: player.getCurrentKey().boneRefs){
			Timeline.Key key = player.unmappedTweenedKeys.get(ref.timeline);
			Timeline.Key.Bone bone = key.object();
			if(player.animation.getTimeline(ref.timeline).objectInfo.type != ObjectType.Bone || !key.active) continue;
			ObjectInfo info = player.animation.getTimeline(ref.timeline).objectInfo;
			if(info == null) continue;
			Dimension size = info.size;
			
			float halfHeight = size.height/2;
			float xx = bone.position.x+(float)Math.cos(Math.toRadians(bone.angle))*info.size.height;
			float yy = bone.position.y+(float)Math.sin(Math.toRadians(bone.angle))*info.size.height;
			float x2 = (float)Math.cos(Math.toRadians(bone.angle+90))*halfHeight*bone.scale.y;
			float y2 = (float)Math.sin(Math.toRadians(bone.angle+90))*halfHeight*bone.scale.y;
			
			float targetX = bone.position.x+(float)Math.cos(Math.toRadians(bone.angle))*size.width*bone.scale.x,
					targetY = bone.position.y+(float)Math.sin(Math.toRadians(bone.angle))*size.width*bone.scale.x;
			float upperPointX = xx+x2, upperPointY = yy+y2;
			this.line(bone.position.x, bone.position.y, upperPointX, upperPointY);
			this.line(upperPointX, upperPointY, targetX, targetY);

			float lowerPointX = xx-x2, lowerPointY = yy-y2;
			this.line(bone.position.x, bone.position.y, lowerPointX, lowerPointY);
			this.line(lowerPointX, lowerPointY, targetX, targetY);
			this.line(bone.position.x, bone.position.y, targetX, targetY);
		}
	}
	
	public void drawBoxes(Player player){
		this.setColor(0f, 1f, 0f, 1f);
		this.drawBoneBoxes(player);
		this.drawObjectBoxes(player);
		this.drawPoints(player);
	}
	
	public void drawBoneBoxes(Player player){
		drawBoneBoxes(player, player.boneIterator());
	}
	
	public void drawBoneBoxes(Player player, Iterator<Bone> it){
		while(it.hasNext()){
			Bone bone = it.next();
			this.drawBBox(player.getBox(bone));
		}
	}
	
	public void drawObjectBoxes(Player player){
		drawObjectBoxes(player, player.objectIterator());
	}
	
	public void drawObjectBoxes(Player player, Iterator<Object> it){
		while(it.hasNext()){
			Object bone = it.next();
			this.drawBBox(player.getBox(bone));
		}
	}
	
	public void drawPoints(Player player){
		drawPoints(player, player.objectIterator());
	}
	
	public void drawPoints(Player player, Iterator<Object> it){
		while(it.hasNext()){
			Object point = it.next();
			if(player.getObjectInfoFor(point).type == ObjectType.Point){
				float x = point.position.x+(float)(Math.cos(Math.toRadians(point.angle))*pointRadius);
				float y = point.position.y+(float)(Math.sin(Math.toRadians(point.angle))*pointRadius);
				circle(point.position.x, point.position.y, pointRadius);
				line(point.position.x, point.position.y, x,y);
			}
		}
	}
	
	public void draw(Player player){
		this.draw(player, player.characterMap);
	}
	
	public void draw(Player player, CharacterMap map){
		this.draw(player.objectIterator(), map);
	}
	
	public void draw(Iterator<Timeline.Key.Object> it, CharacterMap map){
		while(it.hasNext()){
			Timeline.Key.Object object = it.next();
			if(object.ref.hasFile()){
				if(map != null) object.ref.set(map.get(object.ref));
				this.draw(object);
			}
		}
	}
	
	public void drawBBox(BoundingBox box){
		this.line(box.points[0].x, box.points[0].y, box.points[1].x, box.points[1].y);
		this.line(box.points[1].x, box.points[1].y, box.points[3].x, box.points[3].y);
		this.line(box.points[3].x, box.points[3].y, box.points[2].x, box.points[2].y);
		this.line(box.points[2].x, box.points[2].y, box.points[0].x, box.points[0].y);
	}
	
	public abstract void setColor(float r, float g, float b, float a);
	public abstract void line(float x1, float y1, float x2, float y2);
	public abstract void rectangle(float x, float y, float width, float height);
	public abstract void circle(float x, float y, float radius);
	public abstract void draw(Timeline.Key.Object object);
}
