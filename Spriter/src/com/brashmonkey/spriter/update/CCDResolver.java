package com.brashmonkey.spriter.update;

import com.brashmonkey.spriter.SpriterCalculator;
import com.brashmonkey.spriter.update.Mainline.Key.BoneRef;
import com.brashmonkey.spriter.update.Timeline.Key.Bone;

public class CCDResolver extends IKResolver {

	@Override
	public void resolve(float x, float y, int chainLength, BoneRef effectorRef, Player player) {
		//player.unmapObjects(null);
		Timeline timeline = player.animation.getTimeline(effectorRef.timeline);
		Timeline.Key key = player.tweenedKeys.get(effectorRef.timeline);
		Timeline.Key unmappedKey = player.unmappedTweenedKeys.get(effectorRef.timeline);
		Bone effector = key.object();
		Bone unmappedffector = unmappedKey.object();
		float width = (timeline.objectInfo != null) ? timeline.objectInfo.size.width: 200;
		width *= unmappedffector.scale.x;
		float xx = unmappedffector.position.x+(float)Math.cos(Math.toRadians(unmappedffector.angle))*width,
				yy = unmappedffector.position.y+(float)Math.sin(Math.toRadians(unmappedffector.angle))*width;
		if(SpriterCalculator.distanceBetween(xx, yy, x, y) <= this.tolerance)
			return;
		
		effector.angle = SpriterCalculator.angleBetween(unmappedffector.position.x, unmappedffector.position.y, x, y);
		if(Math.signum(player.root.scale.x) == -1) effector.angle += 180f;
		BoneRef parentRef = effectorRef.parent;
		Bone parent = null, unmappedParent = null;
		if(parentRef != null){
			parent = player.tweenedKeys.get(parentRef.timeline).object();
			unmappedParent = player.unmappedTweenedKeys.get(parentRef.timeline).object();
			effector.angle -= unmappedParent.angle;
		}
		player.unmapObjects(null);
		for(int i = 0; i < chainLength && parentRef != null; i++){
			if(SpriterCalculator.distanceBetween(xx, yy, x, y) <= this.tolerance)
				return;
			parent.angle += SpriterCalculator.angleDifference(SpriterCalculator.angleBetween(unmappedParent.position.x, unmappedParent.position.y, x, y),
					SpriterCalculator.angleBetween(unmappedParent.position.x, unmappedParent.position.y, xx, yy));
			parentRef = parentRef.parent;
			if(parentRef != null && i < chainLength-1){
				parent = player.tweenedKeys.get(parentRef.timeline).object();
				unmappedParent = player.unmappedTweenedKeys.get(parentRef.timeline).object();
				parent.angle -= unmappedParent.angle;
			}
			else parent = null;
			player.unmapObjects(null);
			xx = unmappedffector.position.x+(float)Math.cos(Math.toRadians(unmappedffector.angle))*width;
			yy = unmappedffector.position.y+(float)Math.sin(Math.toRadians(unmappedffector.angle))*width;
		}
	}

}
