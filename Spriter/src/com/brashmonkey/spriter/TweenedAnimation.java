package com.brashmonkey.spriter;

import com.brashmonkey.spriter.Mainline.Key.BoneRef;
import com.brashmonkey.spriter.Mainline.Key.ObjectRef;
import com.brashmonkey.spriter.Timeline.Key.Bone;
import com.brashmonkey.spriter.Timeline.Key.Object;

public class TweenedAnimation extends Animation{
	
	public float weight = .5f, spriteThreshold = .5f;
	public final Curve curve;
	public final Entity entity;
	private Animation anim1, anim2;
	public Animation baseAnimation;
	public BoneRef base = null;
	public boolean tweenSprites = false;

	public TweenedAnimation(Entity entity) {
		super(-1, "interpolatedAnimation", 0, true);
		this.entity = entity;
		this.curve = new Curve();
		this.setUpTimelines();
	}
	
	public Mainline.Key getCurrentKey(){
		return this.currentKey;
	}
	@Override
	public void update(int time, Bone root){
		super.currentKey = onFirstMainLine() ? anim1.currentKey: anim2.currentKey;
    	for(Timeline.Key timelineKey: this.mappedTweenedKeys)
			timelineKey.active = false;
    	if(base != null){//TODO: Sprites not working properly because of different timeline naming
        	Animation currentAnim = onFirstMainLine() ? anim1: anim2;
        	Animation baseAnim = baseAnimation == null ? (onFirstMainLine() ? anim1:anim2) : baseAnimation;
	    	for(BoneRef ref: currentKey.boneRefs){
	        	Timeline timeline = baseAnim.getSimilarTimeline(currentAnim.getTimeline(ref.timeline));
	        	if(timeline == null) continue;
	    		Timeline.Key key, mappedKey;
    			key = baseAnim.tweenedKeys[timeline.id];
    			mappedKey = baseAnim.mappedTweenedKeys[timeline.id];
	    		this.tweenedKeys[ref.timeline].active = key.active;
	    		this.tweenedKeys[ref.timeline].object().set(key.object());
	    		this.mappedTweenedKeys[ref.timeline].active = mappedKey.active;
				this.unmapTimelineObject(ref.timeline, false,(ref.parent != null) ?
						this.mappedTweenedKeys[ref.parent.timeline].object(): root);
	    	}
	    	/*for(ObjectRef ref: baseAnim.currentKey.objectRefs){
	        	Timeline timeline = baseAnim.getTimeline(ref.timeline);//getSimilarTimeline(ref, tempTimelines);
	        	if(timeline != null){
	        		//tempTimelines.addLast(timeline);
	        		Timeline.Key key = baseAnim.tweenedKeys[timeline.id];
	        		Timeline.Key mappedKey = baseAnim.mappedTweenedKeys[timeline.id];
	        		Object obj = (Object) key.object();
	        		
		    		this.tweenedKeys[ref.timeline].active = key.active;
		    		((Object)this.tweenedKeys[ref.timeline].object()).set(obj);
		    		this.mappedTweenedKeys[ref.timeline].active = mappedKey.active;
					this.unmapTimelineObject(ref.timeline, true,(ref.parent != null) ?
							this.mappedTweenedKeys[ref.parent.timeline].object(): root);
	        	}
	    	}*/
	    	//tempTimelines.clear();
    	}
    		
    	this.tweenBoneRefs(base, root);
		for(ObjectRef ref: super.currentKey.objectRefs){
			//if(ref.parent == base)
				this.update(ref, root, 0);
		}
    }
	
	private void tweenBoneRefs(BoneRef base, Bone root){
    	int startIndex = base == null ? -1 : base.id-1;
    	int length = super.currentKey.boneRefs.size();
		for(int i = startIndex+1; i < length; i++){
			BoneRef ref = currentKey.boneRefs.get(i);
			if(base == ref || ref.parent == base) this.update(ref, root, 0);
			if(base == ref.parent) this.tweenBoneRefs(ref, root);
		}
	}
	
	@Override
	protected void update(BoneRef ref, Bone root, int time){
    	boolean isObject = ref instanceof ObjectRef;
		//Tween bone/object
    	Bone bone1 = null, bone2 = null, tweenTarget = null;
    	Timeline t1 = onFirstMainLine() ? anim1.getTimeline(ref.timeline) : anim1.getSimilarTimeline(anim2.getTimeline(ref.timeline));
    	Timeline t2 = onFirstMainLine() ? anim2.getSimilarTimeline(t1) : anim2.getTimeline(ref.timeline);
    	Timeline targetTimeline = super.getTimeline(onFirstMainLine() ? t1.id:t2.id);
    	if(t1 != null) bone1 = anim1.tweenedKeys[t1.id].object();
    	if(t2 != null) bone2 = anim2.tweenedKeys[t2.id].object();
    	if(targetTimeline != null) tweenTarget = this.tweenedKeys[targetTimeline.id].object();
    	if(isObject && (t2 == null || !tweenSprites)){
    		if(!onFirstMainLine()) bone1 = bone2;
    		else bone2 = bone1;
    	}
		if(bone2 != null && tweenTarget != null && bone1 != null){
			if(isObject) this.tweenObject((Object)bone1, (Object)bone2, (Object)tweenTarget, this.weight, this.curve);
			else this.tweenBone(bone1, bone2, tweenTarget, this.weight, this.curve);
			this.mappedTweenedKeys[targetTimeline.id].active = true;
		}
		//Transform the bone relative to the parent bone or the root
		if(this.mappedTweenedKeys[ref.timeline].active){
			this.unmapTimelineObject(targetTimeline.id, isObject,(ref.parent != null) ?
					this.mappedTweenedKeys[ref.parent.timeline].object(): root);
		}
    }
	protected void tweenObject(Object object1, Object object2, Object target, float t, Curve curve){
		this.tweenBone(object1, object2, target, t, curve);
		target.alpha = curve.tweenAngle(object1.alpha, object2.alpha, t);
		target.ref.set(onFirstMainLine() ? object1.ref: object2.ref);
	}
	
	public boolean onFirstMainLine(){
		return this.weight < this.spriteThreshold;
	}
	
	private void setUpTimelines(){
		Animation maxAnim = this.entity.getMaxAnimationTimelines();
		int max = maxAnim.timelines();
		for(int i = 0; i < max; i++){
			Timeline t = new Timeline(i, maxAnim.getTimeline(i).name, maxAnim.getTimeline(i).objectInfo);
			addTimeline(t);
		}
		prepare();
	}
	
	public void setAnimations(Animation animation1, Animation animation2){
		boolean areInterpolated = animation1 instanceof TweenedAnimation || animation2 instanceof TweenedAnimation;
		if(animation1 == anim1 && animation2 == anim2) return;
		if((!this.entity.containsAnimation(animation1) || !this.entity.containsAnimation(animation2)) && !areInterpolated)
			throw new SpriterException("Both animations have to be part of the same entity!");
		this.anim1 = animation1;
		this.anim2 = animation2;
	}
	
	public Animation getFirstAnimation(){
		return this.anim1;
	}
	
	public Animation getSecondAnimation(){
		return this.anim2;
	}

}
