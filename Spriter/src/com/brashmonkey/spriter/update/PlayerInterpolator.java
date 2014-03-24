package com.brashmonkey.spriter.update;

import com.brashmonkey.spriter.interpolation.SpriterCurve;
import com.brashmonkey.spriter.update.Mainline.Key.BoneRef;
import com.brashmonkey.spriter.update.Mainline.Key.ObjectRef;
import com.brashmonkey.spriter.update.Timeline.Key.Bone;
import com.brashmonkey.spriter.update.Timeline.Key.Object;

public class PlayerInterpolator {
	
	private Animation animation;
	private Player player1, player2;
	public float weight = .5f;
	public float spriteThreshold = .5f;
	final SpriterCurve curve;
	private Entity lastEntity1, lastEntity2;
	
	public PlayerInterpolator(Player player1, Player player2){
		this.curve = new SpriterCurve();
		this.setPlayers(player1, player2);
	}
	
	public void setPlayers(Player player1, Player player2){
		this.player1 = player1;
		this.player2 = player2;
		this.setUpAnimation();
		this.setUpTimelines(player1.getEntity(), player2.getEntity());
	}
	
	public Animation getAnimation(){
		return this.animation;
	}
	
	private void setUpAnimation(){
		this.animation = new Animation(-1, "interpolatedAnimation"+this, 0, true){
			public void update(int time, Bone root){
				super.currentKey = onFirstMainLine() ? player1.getCurrentKey(): player2.getCurrentKey();
		    	for(Timeline.Key timelineKey: this.mappedTweenedKeys)
					timelineKey.active = false;
				for(BoneRef ref: super.currentKey.boneRefs)
					this.update(ref, root, time);
				for(ObjectRef ref: super.currentKey.objectRefs)
					this.update(ref, root, time);
		    }
			@Override
			protected void update(BoneRef ref, Bone root, int time){
		    	boolean isObject = ref instanceof ObjectRef;
				//Tween bone/object
		    	Bone bone1 = null, bone2 = null, tweenTarget = null;
		    	Timeline t1 = onFirstMainLine() ? player1.animation.getTimeline(ref.timeline) : player1.animation.getSimilarTimeline(player2.animation.getTimeline(ref.timeline));
		    	Timeline t2 = onFirstMainLine() ? player2.animation.getSimilarTimeline(t1) : player2.animation.getTimeline(ref.timeline);
		    	Timeline targetTimeline = super.getTimeline(onFirstMainLine() ? t1.id:t2.id);
		    	if(t1 != null) bone1 = player1.tweenedKeys.get(t1.id).object();
		    	if(t2 != null) bone2 = player2.tweenedKeys.get(t2.id).object();
		    	if(targetTimeline != null) tweenTarget = this.tweenedKeys[targetTimeline.id].object();
		    	if(isObject){
		    		if(!onFirstMainLine()) bone1 = bone2;
		    		else bone2 = bone1;
		    	}
				if(bone2 != null && tweenTarget != null && bone1 != null){
					if(isObject) this.tweenObject((Object)bone1, (Object)bone2, (Object)tweenTarget, weight, curve);
					else this.tweenBone(bone1, bone2, tweenTarget, weight, curve);
					this.mappedTweenedKeys[targetTimeline.id].active = true;
				}
				//Transform the bone relative to the parent bone or the root
				if(this.mappedTweenedKeys[ref.timeline].active){
					this.unmapTimelineObject(targetTimeline.id, isObject,(ref.parent != null) ?
							this.mappedTweenedKeys[ref.parent.timeline].object(): root);
				}
		    }
			protected void tweenObject(Object object1, Object object2, Object target, float t, SpriterCurve curve){
				this.tweenBone(object1, object2, target, t, curve);
				target.alpha = curve.tweenAngle(object1.alpha, object2.alpha, t);
				target.ref.set(onFirstMainLine() ? object1.ref: object2.ref);
			}
			
			private boolean onFirstMainLine(){
				return weight < spriteThreshold;
			}
		};
	}
	
	private void setUpTimelines(Entity entity1, Entity entity2){
		if(entity1 == this.lastEntity1 && entity2 == this.lastEntity2) return;
		this.lastEntity1 = entity1;
		this.lastEntity2 = entity2;
		Animation maxAnim = entity1.getMaxAnimationTimelines();
		Animation anim2 = entity2.getMaxAnimationTimelines();
		if(anim2.timelines() > maxAnim.timelines()) maxAnim = anim2;
		int max = maxAnim.timelines();
		for(int i = 0; i < max; i++){
			Timeline t = new Timeline(i, maxAnim.getTimeline(i).name, maxAnim.getTimeline(i).objectInfo);
			this.animation.addTimeline(t);
		}
		this.animation.prepare();
	}

}
