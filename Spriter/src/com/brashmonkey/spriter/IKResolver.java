package com.brashmonkey.spriter;

import java.util.HashMap;
import java.util.Map.Entry;

import com.brashmonkey.spriter.Mainline.Key.BoneRef;

/**
 * A IKResolver is responsible for resolving previously set constraints.
 * @see <a href="http://en.wikipedia.org/wiki/Inverse_kinematics"> Inverse kinematics</a>
 * @author Trixt0r
 *
 */
public abstract class IKResolver {
	
	/**
	 * Resolves the inverse kinematics constraint with a specific algtorithm
	 * @param x the target x value
	 * @param y the target y value
	 * @param chainLength number of parents which are affected
	 * @param effector the actual effector where the resolved information has to be stored in.
	 */
	protected abstract void resolve(float x, float y, int chainLength, BoneRef effector, Player player);
	
	protected HashMap<IKObject, BoneRef> ikMap;
	protected float tolerance;

	/**
	 * Creates a resolver with a default tolerance of 5f.
	 */
	public IKResolver() {
		this.tolerance = 5f;
		this.ikMap = new HashMap<IKObject, BoneRef>();
	}
	
	/**
	 * Resolves the inverse kinematics constraints with the implemented algorithm in {@link #resolve(float, float, int, SpriterAbstractObject, SpriterAbstractPlayer)}.
	 * @param player player to apply the resolving.
	 */
	public void resolve(Player player){
		for(Entry<IKObject, BoneRef> entry: this.ikMap.entrySet()){
			for(int j = 0; j < entry.getKey().iterations; j++)
				this.resolve(entry.getKey().x, entry.getKey().y, entry.getKey().chainLength, entry.getValue(),player);
		}
	}
	
	/**
	 * Adds the given object to the internal SpriterIKObject - SpriterBone map, which works like a HashMap.
	 * This means, the values of the given object affect the mapped bone.
	 * @param ikObject
	 * @param bone
	 */
	public void mapIKObject(IKObject ikObject, BoneRef object){
		this.ikMap.put(ikObject, object);
	}
	
	/**
	 * Removes the given object from the internal map.
	 * @param ikObject the ik object to remove
	 */
	public void unmapIKObject(IKObject ikObject){
		this.ikMap.remove(ikObject);
	}
	
	/**
	 * Returns the tolerance of this resolver.
	 * @return the tolerance
	 */
	public float getTolerance() {
		return tolerance;
	}

	/**
	 * Sets the tolerance distance of this resolver.
	 * The resolver should stop the algorithm if the distance to the set ik object is less than the tolerance.
	 * @param tolerance the tolerance
	 */
	public void setTolerance(float tolerance) {
		this.tolerance = tolerance;
	}

}
