package com.brashmonkey.spriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.brashmonkey.spriter.Entity.CharacterMap;
import com.brashmonkey.spriter.Entity.ObjectInfo;
import com.brashmonkey.spriter.Mainline.Key.BoneRef;
import com.brashmonkey.spriter.Mainline.Key.ObjectRef;
import com.brashmonkey.spriter.Timeline.Key.Bone;
import com.brashmonkey.spriter.Timeline.Key.Object;

public class Player {
	
	protected Entity entity;
	Animation animation;
	int time;
	public int speed;
	public List<Timeline.Key> tweenedKeys, unmappedTweenedKeys;
	private List<PlayerListener> listeners;
	Timeline.Key.Bone root = new Timeline.Key.Bone(new Point(0,0));
	private final Point position = new Point(0,0), pivot = new Point(0,0);
	private final HashMap<Object, Timeline.Key> objToTimeline = new HashMap<Object, Timeline.Key>();
	private float angle;
	private boolean dirty = true;
	public CharacterMap characterMap;
	private Rectangle rect;
	public final BoundingBox prevBBox;
	private BoneIterator boneIterator;
	private ObjectIterator objectIterator;
	private Mainline.Key currentKey, prevKey;
	
	public Player(Entity entity){
		this.boneIterator = new BoneIterator();
		this.objectIterator = new ObjectIterator();
		this.speed = 15;
		this.rect = new Rectangle(0,0,0,0);
		this.prevBBox = new BoundingBox();
		this.tweenedKeys = new ArrayList<Timeline.Key>();
		this.unmappedTweenedKeys = new ArrayList<Timeline.Key>();
		this.listeners = new ArrayList<PlayerListener>();
		this.setEntity(entity);
	}
	
	public void update(){
		for(PlayerListener listener: listeners)
			listener.preProcess(this);
		if(dirty) this.updateRoot();
		this.animation.update(time, root);
		this.currentKey = this.animation.currentKey;
		if(prevKey != currentKey){
			for(PlayerListener listener: listeners)
				listener.mainlineKeyChanged(prevKey, currentKey);
			prevKey = currentKey;
		}
		for(Timeline.Key key: animation.tweenedKeys){
			this.tweenedKeys.get(key.id).active = key.active;
			this.unmappedTweenedKeys.get(key.id).active = animation.mappedTweenedKeys[key.id].active;
			((Object)this.tweenedKeys.get(key.id).object()).set((Object)key.object());
			((Object)this.unmappedTweenedKeys.get(key.id).object()).set((Object)animation.mappedTweenedKeys[key.id].object());
		}
		for(PlayerListener listener: listeners)
			listener.postProcess(this);
		this.increaseTime();
	}
	
	private void increaseTime(){
		time += speed;
		if(time > animation.length){
			time = time-animation.length;
			for(PlayerListener listener: listeners)
				listener.animationFinished(animation);
		}
		if(time < 0){
			for(PlayerListener listener: listeners)
				listener.animationFinished(animation);
			time += animation.length;
		}
	}
	
	private void updateRoot(){
		this.root.angle = angle;
		this.root.position.set(pivot);
		this.root.position.rotate(angle);
		this.root.position.translate(position);
		dirty = false;
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
	
	public BoneRef getBoneRef(Bone b){
		return this.getCurrentKey().getBoneRefTimeline(this.objToTimeline.get(b).id);
	}
	
	public int getObjectIndex(String name){
		for(ObjectRef ref: getCurrentKey().objectRefs)
			if(animation.getTimeline(ref.timeline).name.equals(name))
				return ref.id;
		return -1;
	}
	
	public ObjectRef getObjectRef(Object b){
		return this.getCurrentKey().getObjectRefTimeline(this.objToTimeline.get(b).id);
	}
	
	public String getNameFor(Bone bone){
		return this.animation.getTimeline(objToTimeline.get(bone).id).name;
	}
	
	public ObjectInfo getObjectInfoFor(Bone bone){
		return this.animation.getTimeline(objToTimeline.get(bone).id).objectInfo;
	}

	public BoundingBox getBox(Bone bone){
		ObjectInfo info = getObjectInfoFor(bone);
		this.prevBBox.calcFor(bone, info);
		return this.prevBBox;
	}
	
	public boolean collidesFor(Bone bone, float x, float y){
		ObjectInfo info = getObjectInfoFor(bone);
		this.prevBBox.calcFor(bone, info);
		return this.prevBBox.collides(bone, info, x, y);
	}
	
	public void setBone(String name, float x, float y, float angle, float scaleX, float scaleY){
		int index = getBoneIndex(name);
		if(index == -1) throw new SpriterException("No bone found for name \""+name+"\"");
		BoneRef ref = getCurrentKey().getBoneRef(index);
		Bone bone = getBone(index);
		bone.set(x, y, angle, scaleX, scaleY, 0f, 0f);
		unmapObjects(ref);
	}
	
	public void setBone(String name, Point position, float angle, Point scale){
		this.setBone(name, position.x, position.y, angle, scale.x, scale.y);
	}
	
	public void setBone(String name, float x, float y, float angle){
		Bone b = getBone(name);
		setBone(name, x, y, angle, b.scale.x, b.scale.y);
	}
	
	public void setBone(String name, Point position, float angle){
		Bone b = getBone(name);
		setBone(name, position.x, position.y, angle, b.scale.x, b.scale.y);
	}
	
	public void setBone(String name, float x, float y){
		Bone b = getBone(name);
		setBone(name, x, y, b.angle);
	}
	
	public void setBone(String name, Point position){
		setBone(name, position.x, position.y);
	}
	
	public void setBone(String name, float angle){
		Bone b = getBone(name);
		setBone(name, b.position.x, b.position.y, angle);
	}
	
	public void setBone(String name, Bone bone){
		setBone(name, bone.position, bone.angle, bone.scale);
	}
	
	public void setObject(String name, float x, float y, float angle, float scaleX, float scaleY, float pivotX, float pivotY, float alpha, int folder, int file){
		int index = getObjectIndex(name);
		if(index == -1) throw new SpriterException("No object found for name \""+name+"\"");
		ObjectRef ref = getCurrentKey().getObjectRef(index);
		Object object = getObject(index);
		object.set(x, y, angle, scaleX, scaleY, pivotX, pivotY, alpha, folder, file);
		unmapObjects(ref);
	}
	
	public void setObject(String name, Point position, float angle, Point scale, Point pivot, float alpha, FileReference ref){
		this.setObject(name, position.x, position.y, angle, scale.x, scale.y, pivot.x, pivot.y, alpha, ref.folder, ref.file);
	}
	
	public void setObject(String name, float x, float y, float angle, float scaleX, float scaleY){
		Object b = getObject(name);
		setObject(name, x, y, angle, scaleX, scaleY, b.pivot.x, b.pivot.y, b.alpha, b.ref.folder, b.ref.file);
	}
	
	public void setObject(String name, float x, float y, float angle){
		Object b = getObject(name);
		setObject(name, x, y, angle, b.scale.x, b.scale.y);
	}
	
	public void setObject(String name, Point position, float angle){
		Object b = getObject(name);
		setObject(name, position.x, position.y, angle, b.scale.x, b.scale.y);
	}
	
	public void setObject(String name, float x, float y){
		Object b = getObject(name);
		setObject(name, x, y, b.angle);
	}
	
	public void setObject(String name, Point position){
		setObject(name, position.x, position.y);
	}
	
	public void setObject(String name, float angle){
		Object b = getObject(name);
		setObject(name, b.position.x, b.position.y, angle);
	}
	
	public void setObject(String name, float alpha, int folder, int file){
		Object b = getObject(name);
		setObject(name, b.position.x, b.position.y, b.angle, b.scale.x, b.scale.y, b.pivot.x, b.pivot.y, alpha, folder, file);
	}
	
	public void setObject(String name, Object object){
		setObject(name, object.position, object.angle, object.scale, object.pivot, object.alpha, object.ref);
	}
	
	
	public Object getObject(String name){
		return (Object)this.unmappedTweenedKeys.get(animation.getTimeline(name).id).object();
	}
	
	public void unmapObjects(BoneRef base){
		int start = base == null ? -1 : base.id-1;
    	for(int i = start+1; i < getCurrentKey().boneRefs.size(); i++){
    		BoneRef ref = getCurrentKey().getBoneRef(i);
    		if(ref.parent != base && base != null) continue;
			Bone parent = ref.parent == null ? this.root : this.unmappedTweenedKeys.get(ref.parent.timeline).object();
			unmappedTweenedKeys.get(ref.timeline).object().set(tweenedKeys.get(ref.timeline).object());
			unmappedTweenedKeys.get(ref.timeline).object().unmap(parent);
			unmapObjects(ref);
		}
		for(ObjectRef ref: getCurrentKey().objectRefs){
    		if(ref.parent != base && base != null) continue;
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
	
	public Entity getEntity(){
		return this.entity;
	}
	
	public void setAnimation(Animation animation){
		Animation prevAnim = this.animation;
		if(animation == this.animation) return;
		if(animation == null) throw new SpriterException("animation can not be null!");
		if(animation != this.animation) time = 0;
		this.animation = animation;
		for(int i = this.tweenedKeys.size(); i < animation.tweenedKeys.length; i++){
			Timeline.Key key = new Timeline.Key(i);
			Timeline.Key keyU = new Timeline.Key(i);
			key.setObject(new Timeline.Key.Object(new Point(0,0)));
			keyU.setObject(new Timeline.Key.Object(new Point(0,0)));
			this.tweenedKeys.add(key);
			this.unmappedTweenedKeys.add(keyU);
			this.objToTimeline.put((Object) keyU.object(), keyU);
		}
		int tempTime = this.time;
		this.time = 0;
		this.update();
		this.time = tempTime;
		for(PlayerListener listener: listeners)
			listener.animationChanged(prevAnim, animation);
	}
	
	public void setAnimation(String name){
		this.setAnimation(entity.getAnimation(name));
	}
	
	public Rectangle getBoundingRectangle(BoneRef root){
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
			this.prevBBox.calcFor(bone, animation.getTimeline(ref.timeline).objectInfo);
			Rectangle.setBiggerRectangle(rect, this.prevBBox.getBoundingRect(), rect);
			this.calcBoundingRectangle(ref);
		}
		for(ObjectRef ref: getCurrentKey().objectRefs){
			if(ref.parent != root) continue;
			Bone bone = this.unmappedTweenedKeys.get(ref.timeline).object();
			this.prevBBox.calcFor(bone, animation.getTimeline(ref.timeline).objectInfo);
			Rectangle.setBiggerRectangle(rect, this.prevBBox.getBoundingRect(), rect);
		}
	}
	
	public Animation getAnimation(){
		return this.animation;
	}
	
	public Mainline.Key getCurrentKey(){
		return this.currentKey;
	}

	public int getTime() {
		return time;
	}
	
	public void setTime(int time){
		this.time = time;
		int prevSpeed = this.speed;
		this.speed = 0;
		this.increaseTime();
		this.speed = prevSpeed;
	}
	
	public void setScale(float scale){
		this.root.scale.set(scale*flippedX(), scale*flippedY());
	}
	
	public void scale(float scale){
		this.root.scale.scale(scale, scale);
	}
	
	public float getScale(){
		return root.scale.x;
	}
	
	public void flip(boolean x, boolean y){
		if(x) this.flipX();
		if(y) this.flipY();
	}
	
	public void flipX(){
		this.root.scale.x *= -1;
	}
	
	public void flipY(){
		this.root.scale.y *= -1;
	}
	
	public int flippedX(){
		return (int) Math.signum(root.scale.x);
	}
	
	public int flippedY(){
		return (int) Math.signum(root.scale.y);
	}
	
	public Player setPosition(float x, float y){
		this.dirty = true;
		this.position.set(x,y);
		return this;
	}
	
	public Player setPosition(Point point){
		return this.setPosition(point.x, point.y);
	}
	
	public Player translatePosition(float x, float y){
		return this.setPosition(position.x+x, position.y+y);
	}
	
	public Player translate(Point point){
		return this.translatePosition(point.x, point.y);
	}
	
	public float getX(){
		return position.x;
	}
	
	public float getY(){
		return position.y;
	}
	
	public Player setAngle(float angle){
		this.dirty = true;
		this.angle = angle;
		return this;
	}
	
	public Player rotate(float angle){
		return this.setAngle(angle+this.angle);
	}
	
	public float getAngle(){
		return this.angle;
	}
	
	public Player setPivot(float x, float y){
		this.dirty = true;
		this.pivot.set(x, y);
		return this;
	}
	
	public Player setPivot(Point p){
		return this.setPivot(p.x, p.y);
	}
	
	public Player translatePivot(float x, float y){
		return this.setPivot(pivot.x+x, pivot.y+y);
	}
	
	public Player translatePivot(Point point){
		return this.translatePivot(point.x, point.y);
	}
	
	public float getPivotX(){
		return pivot.x;
	}
	
	public float getPivotY(){
		return pivot.y;
	}
	
	public Iterator<Bone> boneIterator(){
		this.boneIterator.index = 0;
		return this.boneIterator;
	}
	
	public Iterator<Bone> boneIterator(BoneRef start){
		this.boneIterator.index = start.id;
		return this.boneIterator;
	}
	
	public Iterator<Object> objectIterator(){
		this.objectIterator.index = 0;
		return this.objectIterator;
	}
	
	public Iterator<Object> objectIterator(ObjectRef start){
		this.objectIterator.index = start.id;
		return this.objectIterator;
	}
	
	class ObjectIterator implements Iterator<Object>{
		int index = 0;
		@Override
		public boolean hasNext() {
			return index < getCurrentKey().objectRefs.size();
		}

		@Override
		public Object next() {
			return (Object) unmappedTweenedKeys.get(getCurrentKey().objectRefs.get(index++).timeline).object();
		}

		@Override
		public void remove() {
			throw new SpriterException("remove() is not supported by this iterator!");
		}
		
	}
	
	class BoneIterator implements Iterator<Bone>{
		int index = 0;
		@Override
		public boolean hasNext() {
			return index < getCurrentKey().boneRefs.size();
		}

		@Override
		public Bone next() {
			return unmappedTweenedKeys.get(getCurrentKey().boneRefs.get(index++).timeline).object();
		}

		@Override
		public void remove() {
			throw new SpriterException("remove() is not supported by this iterator!");
		}
	}

	public static interface PlayerListener{
		public void animationFinished(Animation animation);
		public void animationChanged(Animation oldAnim, Animation newAnim);
		public void preProcess(Player player);
		public void postProcess(Player player);
		public void mainlineKeyChanged(Mainline.Key prevKey, Mainline.Key newKey);
	}
}
