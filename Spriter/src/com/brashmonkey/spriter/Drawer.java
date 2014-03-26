package com.brashmonkey.spriter;

import com.brashmonkey.spriter.Entity.CharacterMap;
import com.brashmonkey.spriter.Entity.ObjectInfo;
import com.brashmonkey.spriter.Entity.ObjectType;

public abstract class Drawer<R> {
	
	public float pointRadius = 5f;
	private final BoundingBox box;
	protected Loader<R> loader;
	
	public Drawer(Loader<R> loader){
		this.loader = loader;
		this.box = new BoundingBox();
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
		for(Mainline.Key.BoneRef ref: player.getCurrentKey().boneRefs){
			Timeline.Key key = player.unmappedTweenedKeys.get(ref.timeline);
			if(!key.active || player.animation.getTimeline(ref.timeline).objectInfo.type == ObjectType.Point) continue;
			Timeline.Key.Bone bone = key.object();
			ObjectInfo info = player.animation.getTimeline(ref.timeline).objectInfo;
			if(info == null) continue;
			this.box.updateFor(bone, info);
			this.drawBBox(this.box);
		}
		for(Mainline.Key.ObjectRef ref: player.getCurrentKey().objectRefs){
			Timeline.Key key = player.unmappedTweenedKeys.get(ref.timeline);
			if(!key.active || player.animation.getTimeline(ref.timeline).objectInfo.type != ObjectType.Sprite) continue;
			Timeline.Key.Bone bone = key.object();
			ObjectInfo info = player.animation.getTimeline(ref.timeline).objectInfo;
			if(info == null) continue;
			this.box.updateFor(bone, info);
			this.drawBBox(this.box);
		}

		for(Mainline.Key.ObjectRef ref: player.getCurrentKey().objectRefs){
			Timeline.Key key = player.unmappedTweenedKeys.get(ref.timeline);
			if(player.animation.getTimeline(ref.timeline).objectInfo.type != ObjectType.Point || !key.active) continue;
			Timeline.Key.Bone bone = key.object();
			float x = bone.position.x+(float)(Math.cos(Math.toRadians(bone.angle))*pointRadius);
			float y = bone.position.y+(float)(Math.sin(Math.toRadians(bone.angle))*pointRadius);
			circle(bone.position.x, bone.position.y, pointRadius);
			line(bone.position.x, bone.position.y, x,y);
		}
	}
	
	public void draw(Player player){
		this.draw(player, player.characterMap);
	}
	
	public void draw(Player player, CharacterMap map){
		for(Mainline.Key.ObjectRef ref: player.getCurrentKey().objectRefs){
			Timeline.Key key = player.unmappedTweenedKeys.get(ref.timeline);
			if(!key.active) continue;
			Timeline.Key.Bone bone = key.object();
			if(bone.isBone()) continue;
			Timeline.Key.Object object = (Timeline.Key.Object)bone;
			if(object.ref.hasFile()){
				if(map != null) object.ref.set(map.get(object.ref));
				this.draw(object);
			}
		}
	}
	
	public void drawBBox(BoundingBox box){
		this.line(box.boundingPoints[0].x, box.boundingPoints[0].y, box.boundingPoints[1].x, box.boundingPoints[1].y);
		this.line(box.boundingPoints[1].x, box.boundingPoints[1].y, box.boundingPoints[3].x, box.boundingPoints[3].y);
		this.line(box.boundingPoints[3].x, box.boundingPoints[3].y, box.boundingPoints[2].x, box.boundingPoints[2].y);
		this.line(box.boundingPoints[2].x, box.boundingPoints[2].y, box.boundingPoints[0].x, box.boundingPoints[0].y);
	}
	
	public abstract void setColor(float r, float g, float b, float a);
	public abstract void line(float x1, float y1, float x2, float y2);
	public abstract void rectangle(float x, float y, float width, float height);
	public abstract void circle(float x, float y, float radius);
	public abstract void draw(Timeline.Key.Object object);
}
