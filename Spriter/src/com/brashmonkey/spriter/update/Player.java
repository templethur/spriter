package com.brashmonkey.spriter.update;

import java.util.ArrayList;
import java.util.List;

import com.brashmonkey.spriter.SpriterException;
import com.brashmonkey.spriter.SpriterPoint;
import com.brashmonkey.spriter.SpriterRectangle;
import com.brashmonkey.spriter.update.Entity.CharacterMap;
import com.brashmonkey.spriter.update.Mainline.Key.BoneRef;
import com.brashmonkey.spriter.update.Mainline.Key.ObjectRef;
import com.brashmonkey.spriter.update.Timeline.Key.Bone;
import com.brashmonkey.spriter.update.Timeline.Key.Object;

public class Player {
	
	public final SpriterPoint position;
	private Entity entity;
	Animation animation;
	int time;
	public int speed;
	public List<Timeline.Key> tweenedKeys, unmappedTweenedKeys;
	public Timeline.Key.Bone root = new Timeline.Key.Bone(new SpriterPoint(0,0));
	public CharacterMap characterMap;
	private SpriterRectangle rect;
	private BoundingBox box;
	
	public Player(Entity entity){
		this.position = new SpriterPoint(0f,0f);
		this.speed = 15;
		this.rect = new SpriterRectangle(0,0,0,0);
		this.box = new BoundingBox();
		this.tweenedKeys = new ArrayList<Timeline.Key>();
		this.unmappedTweenedKeys = new ArrayList<Timeline.Key>();
		this.setEntity(entity);
	}
	
	public void update(){
		this.animation.update(time, root);
		for(Timeline.Key key: animation.tweenedKeys){
			this.tweenedKeys.get(key.id).active = key.active;
			this.unmappedTweenedKeys.get(key.id).active = animation.mappedTweenedKeys[key.id].active;
			((Object)this.tweenedKeys.get(key.id).object()).set((Object)key.object());
			((Object)this.unmappedTweenedKeys.get(key.id).object()).set((Object)animation.mappedTweenedKeys[key.id].object());
		}
		this.increaseTime();
	}
	
	public void setBone(int index, Bone bone){
		this.animation.setBone(index, bone, root);
	}
	
	public void setBone(String name, Bone bone){
		int targetIndex = getBoneIndex(name);
		this.animation.setBone(targetIndex, bone, root);
	}
	
	public void setObject(int index, Object obj){
		this.animation.setObject(index, obj);
	}
	
	public void setObject(String name, Object obj){
		int targetIndex = getObjectIndex(name);
		this.animation.setObject(targetIndex, obj);
	}
	
	public Bone getBone(int index){
		return this.unmappedTweenedKeys.get(getCurrentKey().getBoneRef(index).timeline).object();
	}
	
	public Object getObject(int index){
		return (Object) this.unmappedTweenedKeys.get(getCurrentKey().getObjectRef(index).timeline).object();
	}
	
	public int getBoneIndex(String name){
		for(BoneRef ref: getCurrentKey().boneRefs)
			if(animation.getTimeline(ref.timeline).name.equals(name))
				return ref.id;
		return -1;
	}
	
	public Bone getBone(String name){
		return this.unmappedTweenedKeys.get(animation.getTimeline(name).id).object();
	}
	
	public int getObjectIndex(String name){
		for(ObjectRef ref: getCurrentKey().objectRefs)
			if(animation.getTimeline(ref.timeline).name.equals(name))
				return ref.id;
		return -1;
	}
	
	public Object getObject(String name){
		return (Object)this.unmappedTweenedKeys.get(animation.getTimeline(name).id).object();
	}
	
	public void unmapObjects(BoneRef base){
		int start = base == null ? -1 : base.id;
    	for(int i = start+1; i < getCurrentKey().boneRefs.size(); i++){
    		BoneRef ref = getCurrentKey().getBoneRef(i);
    		if(ref.parent != base) continue;
			Bone parent = ref.parent == null ? this.root : this.unmappedTweenedKeys.get(ref.parent.timeline).object();
			unmappedTweenedKeys.get(ref.timeline).object().set(tweenedKeys.get(ref.timeline).object());
			unmappedTweenedKeys.get(ref.timeline).object().unmap(parent);
			unmapObjects(ref);
		}
		for(ObjectRef ref: getCurrentKey().objectRefs){
    		if(ref.parent != base) continue;
			Bone parent = ref.parent == null ? this.root : this.unmappedTweenedKeys.get(ref.parent.timeline).object();
			unmappedTweenedKeys.get(ref.timeline).object().set(tweenedKeys.get(ref.timeline).object());
			unmappedTweenedKeys.get(ref.timeline).object().unmap(parent);
		}
	}
	
	public void setEntity(Entity entity){
		if(entity == null) throw new SpriterException("entity can not be null!");
		this.entity = entity;
		this.setAnimation(entity.getAnimation(0));
	}
	
	private void increaseTime(){
		time += speed;
		if(time > animation.length)	time = 0;
		if(time < 0) time = animation.length;
	}
	
	public Entity getEntity(){
		return this.entity;
	}
	
	public void setAnimation(Animation animation){
		if(animation == null) throw new SpriterException("animation can not be null!");
		if(animation != this.animation) time = 0;
		this.animation = animation;
		this.animation.prepare();
		for(int i = this.tweenedKeys.size(); i < animation.tweenedKeys.length; i++){
			Timeline.Key key = new Timeline.Key(i);
			Timeline.Key keyU = new Timeline.Key(i);
			key.setObject(new Timeline.Key.Object(new SpriterPoint(0,0)));
			keyU.setObject(new Timeline.Key.Object(new SpriterPoint(0,0)));
			this.tweenedKeys.add(key);
			this.unmappedTweenedKeys.add(keyU);
		}
	}
	
	public Animation getAnimation(){
		return this.animation;
	}
	
	public Mainline.Key getCurrentKey(){
		return this.animation.currentKey;
	}

	public int getTime() {
		return time;
	}
	
	public void setPosition(float x, float y){
		this.root.position.set(x,y);
	}
	
	public void setScale(float scale){
		this.root.scale.set(scale, scale);
	}
	
	public void setAngle(float angle){
		this.root.angle = angle;
	}
	
	public SpriterRectangle getBoundingRectangle(BoneRef root){
		Bone boneRoot = root == null ? this.root : this.unmappedTweenedKeys.get(root.timeline).object();
		this.rect.set(boneRoot.position.x, boneRoot.position.y, boneRoot.position.x, boneRoot.position.y);
		this.calcBoundingRectangle(root);
		this.rect.calculateSize();
		return this.rect;
	}
	
	private void calcBoundingRectangle(BoneRef root){
		for(BoneRef ref: getCurrentKey().boneRefs){
			if(ref.parent != root && root != null) continue;
			Bone bone = this.unmappedTweenedKeys.get(ref.timeline).object();
			this.box.updateFor(bone, animation.getTimeline(ref.timeline).objectInfo);
			SpriterRectangle.setBiggerRectangle(rect, this.box.getBoundingRect(), rect);
			this.calcBoundingRectangle(ref);
		}
		for(ObjectRef ref: getCurrentKey().objectRefs){
			if(ref.parent != root) continue;
			Bone bone = this.unmappedTweenedKeys.get(ref.timeline).object();
			this.box.updateFor(bone, animation.getTimeline(ref.timeline).objectInfo);
			SpriterRectangle.setBiggerRectangle(rect, this.box.getBoundingRect(), rect);
		}
	}

}
