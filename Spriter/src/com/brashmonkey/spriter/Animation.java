package com.brashmonkey.spriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.brashmonkey.spriter.Mainline.Key;
import com.brashmonkey.spriter.Mainline.Key.BoneRef;
import com.brashmonkey.spriter.Mainline.Key.ObjectRef;
import com.brashmonkey.spriter.Timeline.Key.Bone;
import com.brashmonkey.spriter.Timeline.Key.Object;

public class Animation {

    public final Mainline mainline;
    private final List<Timeline> timelines;
    private final HashMap<String, Timeline> nameToTimeline;
    public final int id, length;
    public final String name;
    public final boolean looping;
	Key currentKey;
	Timeline.Key[] tweenedKeys, mappedTweenedKeys;
	private boolean prepared;
    
    public Animation(Mainline mainline, int id, String name, int length, boolean looping){
    	this(mainline, id, name, length, looping, new ArrayList<Timeline>());
    }
    
    public Animation(int id, String name, int length, boolean looping, List<Timeline> timelines){
    	this(new Mainline(), id, name, length, looping, timelines);
    }
    
    public Animation(Mainline mainline, int id, String name, int length, boolean looping, List<Timeline> timelines){
    	this.mainline = mainline;
    	this.id = id;
    	this.name = name;
    	this.length = length;
    	this.looping = looping;
    	this.timelines = timelines;
    	this.prepared = false;
    	this.nameToTimeline = new HashMap<String, Timeline>();
    	//this.currentKey = mainline.getKey(0);
    }
    
    public Animation(Mainline mainline, int id, String name, int length){
    	this(mainline, id, name, length, true);
    }
    
    public Animation(int id, String name, int length, boolean looping){
    	this(new Mainline(), id, name, length, looping);
    }
    
    public Timeline getTimeline(int id){
    	return this.timelines.get(id);
    }
    
    public Timeline getTimeline(String name){
    	return this.nameToTimeline.get(name);
    }
    
    public void addTimeline(Timeline timeline){
    	this.timelines.add(timeline);
    	this.nameToTimeline.put(timeline.name, timeline);
    }
    
    public int timelines(){
    	return timelines.size();
    }
    
    public String toString(){
    	String toReturn = getClass().getSimpleName()+"|[id: "+id+", "+name+", duration: "+length+", is looping: "+looping;
    	toReturn +="Mainline:\n";
    	toReturn += mainline;
    	toReturn += "Timelines\n";
    	for(Timeline timeline: this.timelines)
    		toReturn += timeline;
    	toReturn+="]";
    	return toReturn;
    }
    
    /**
     * Updates the bone and object structure.
     * @param time The time which has to be between 0 and {@link #length} to work properly.
     * @param root The root bone which is not allowed to be null. The whole animation runs relative to the root bone.
     */
    public void update(int time, Bone root){
    	if(!this.prepared) throw new SpriterException("This animation is not ready yet to animate itself. Please call prepare()!");
    	if(root == null) throw new SpriterException("The root can not be null! Set a root bone to apply this animation relative to the root bone.");
    	this.currentKey = mainline.getKeyBeforeTime(time);
    	
    	for(Timeline.Key timelineKey: this.mappedTweenedKeys)
			timelineKey.active = false;
		for(BoneRef ref: currentKey.boneRefs)
			this.update(ref, root, time);
		for(ObjectRef ref: currentKey.objectRefs)
			this.update(ref, root, time);
    }
    
    protected void update(BoneRef ref, Bone root, int time){
    	boolean isObject = ref instanceof ObjectRef;
		//Get the timelines, the refs pointing to
		Timeline timeline = getTimeline(ref.timeline);
		Timeline.Key key = timeline.getKey(ref.key);
		Timeline.Key nextKey = timeline.getKey((ref.key+1)%timeline.keys.size());
		int currentTime = key.time;
		int nextTime = nextKey.time;
		if(nextTime < currentTime){
			if(!looping) nextKey = key;
			else nextTime = length;
		}
		//Normalize the time
		float t = (float)(time - currentTime)/(float)(nextTime - currentTime);
		if(Float.isNaN(t) || Float.isInfinite(t)) t = 1;
		t = currentKey.curve.tween(0, 1, t);//TODO: Mainline curve is not applied properly
		//Tween bone/object
		Bone bone1 = key.object();
		Bone bone2 = nextKey.object();
		Bone tweenTarget = this.tweenedKeys[ref.timeline].object();
		if(isObject) this.tweenObject((Object)bone1, (Object)bone2, (Object)tweenTarget, t, key.curve);
		else this.tweenBone(bone1, bone2, tweenTarget, t, key.curve);
		this.mappedTweenedKeys[ref.timeline].active = true;
		this.unmapTimelineObject(ref.timeline, isObject,(ref.parent != null) ?
				this.mappedTweenedKeys[ref.parent.timeline].object(): root);
    }
    
    void unmapTimelineObject(int timeline, boolean isObject, Bone root){
		Bone tweenTarget = this.tweenedKeys[timeline].object();
		Bone mapTarget = this.mappedTweenedKeys[timeline].object();
		if(isObject) ((Object)mapTarget).set((Object)tweenTarget);
		else mapTarget.set(tweenTarget);
		mapTarget.unmap(root);
    }
    
    /*void setBone(int index, Bone bone, Bone root){
    	BoneRef base = currentKey.getBoneRef(index);
    	this.mappedTweenedKeys[base.timeline].setObject(bone);
    	for(int i = base.id+1; i < currentKey.boneRefs.size(); i++){
    		BoneRef ref = currentKey.getBoneRef(i);
    		if(ref.parent != base) continue;
    		this.unmapTimelineObject(ref.timeline, ref instanceof ObjectRef,(ref.parent != null) ?
    				this.mappedTweenedKeys[ref.parent.timeline].object(): root);
    		setBone(ref.id, this.mappedTweenedKeys[ref.timeline].object(), root);
    	}
    	for(ObjectRef ref: currentKey.objectRefs){
    		if(ref.parent != base) continue;
    		this.unmapTimelineObject(ref.timeline, ref instanceof ObjectRef, (ref.parent != null) ?
    				this.mappedTweenedKeys[ref.parent.timeline].object(): root);
    	}
    }*/
    
    /*void setObject(int index, Object obj){
    	this.mappedTweenedKeys[currentKey.getObjectRef(index).timeline].setObject(obj);
    }*/
	
	protected void tweenBone(Bone bone1, Bone bone2, Bone target, float t, Curve curve){
		target.angle = curve.tweenAngle(bone1.angle, bone2.angle, t);
		curve.tweenPoint(bone1.position, bone2.position, t, target.position);
		curve.tweenPoint(bone1.scale, bone2.scale, t, target.scale);
		curve.tweenPoint(bone1.pivot, bone2.pivot, t, target.pivot);
	}
	
	protected void tweenObject(Object object1, Object object2, Object target, float t, Curve curve){
		this.tweenBone(object1, object2, target, t, curve);
		target.alpha = curve.tweenAngle(object1.alpha, object2.alpha, t);
		target.ref.set(object1.ref);
	}
	
	public Timeline getSimilarTimeline(Timeline t){
    	Timeline found = getTimeline(t.name);
    	if(found == null && t.id < this.timelines()) found = this.getTimeline(t.id);
    	return found;
	}
	
	public Timeline getSimilarTimeline(BoneRef ref, Collection<Timeline> coveredTimelines){
		if(ref.parent == null) return null;
    	for(BoneRef boneRef: this.currentKey.objectRefs){
    		Timeline t = this.getTimeline(boneRef.timeline);
    		if(boneRef.parent != null && boneRef.parent.id == ref.parent.id && !coveredTimelines.contains(t))
    			return t;
    	}
    	return null;
	}
	
	public Timeline getSimilarTimeline(ObjectRef ref, Collection<Timeline> coveredTimelines){
		if(ref.parent == null) return null;
    	for(ObjectRef objRef: this.currentKey.objectRefs){
    		Timeline t = this.getTimeline(objRef.timeline);
    		if(objRef.parent != null && objRef.parent.id == ref.parent.id && !coveredTimelines.contains(t))
    			return t;
    	}
    	return null;
	}
	
	/**
	 * Prepares this animation to set this animation in any time state.
	 * This method has to be called before {@link #update(int, Bone)}.
	 */
	public void prepare(){
		if(this.prepared) return;
		this.tweenedKeys = new Timeline.Key[timelines.size()];
		this.mappedTweenedKeys = new Timeline.Key[timelines.size()];
		
		for(int i = 0; i < this.tweenedKeys.length; i++){
			this.tweenedKeys[i] = new Timeline.Key(i);
			this.mappedTweenedKeys[i] = new Timeline.Key(i);
			this.tweenedKeys[i].setObject(new Timeline.Key.Object(new Point(0,0)));
			this.mappedTweenedKeys[i].setObject(new Timeline.Key.Object(new Point(0,0)));
		}
		if(mainline.keys.size() > 0) currentKey = mainline.getKey(0);
		this.prepared = true;
	}

}
