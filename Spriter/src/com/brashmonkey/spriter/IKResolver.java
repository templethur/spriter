package com.brashmonkey.spriter;

import java.util.HashMap;
import java.util.Map.Entry;

import com.brashmonkey.spriter.Mainline.Key.BoneRef;

public abstract class IKResolver {
	
	/**
	 * Resolves the inverse kinematics constraint with a specific algtorithm
	 * @param x the target x value
	 * @param y the target y value
	 * @param chainLength number of parents which are affected
	 * @param effector the actual effector where the resolved information has to be stored in.
	 */
	protected abstract void resolve(float x, float y, int chainLength, BoneRef effector, Player player);
	
	protected boolean resovling;
	protected HashMap<IKObject, BoneRef> ikMap;
	protected float tolerance;

	public IKResolver() {
		this.resovling = true;
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
	 * @return the resovling
	 */
	public boolean isResovling() {
		return resovling;
	}

	/**
	 * @param resovling the resovling to set
	 */
	public void setResovling(boolean resovling) {
		this.resovling = resovling;
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
	 * @param object
	 */
	public void unmapIKObject(IKObject ikObject){
		this.ikMap.remove(ikObject);
	}

	public float getTolerance() {
		return tolerance;
	}

	public void setTolerance(float tolerance) {
		this.tolerance = tolerance;
	}
	
	/**
	 * Changes the state of each effector to unactive. The effect results in non animated bodyparts.
	 * @param parents indicates whether parents of the effectors have to be deactivated or not.
	 
	public void deactivateEffectors(Player player, boolean parents){
		for(Entry<IKObject, Bone> entry: this.ikMap.entrySet()){
			Bone obj = entry.getValue();
			if(!parents) continue;
			SpriterBone par = (SpriterBone) entry.getValue().getParent();
			for(int j = 0; j < entry.getKey().chainLength && par != null; j++){
				player.getRuntimeBones()[par.getId()].active = false;
				par = (SpriterBone) par.getParent();
			}
		}
	}
	
	public void activateEffectors(SpriterAbstractPlayer player){
		for(Entry<SpriterIKObject, SpriterAbstractObject> entry: this.ikMap.entrySet()){
			SpriterAbstractObject obj = entry.getValue();
			obj = (obj instanceof SpriterBone) ? player.getRuntimeBones()[obj.getId()]: player.getRuntimeObjects()[obj.getId()];
			obj.active = true;
			SpriterBone par = (SpriterBone) entry.getValue().getParent();
			for(int j = 0; j < entry.getKey().chainLength && par != null; j++){
				player.getRuntimeBones()[par.getId()].active = false;
				par = (SpriterBone) par.getParent();
			}
		}
	}
	
	public void activateAll(SpriterAbstractPlayer player){
		for(SpriterBone bone: player.getRuntimeBones())
			bone.active = true;
		for(SpriterObject obj: player.getRuntimeObjects())
			obj.active = true;
	}*/
	
	/*protected void updateObject(SpriterAbstractPlayer player, SpriterAbstractObject object){
		player.updateAbstractObject(object);
	}
	
	protected void updateRecursively(SpriterAbstractPlayer player, SpriterAbstractObject object){
		this.updateObject(player, object);
		if(object instanceof SpriterBone){
			for(SpriterBone child: ((SpriterBone) object).getChildBones())
				this.updateRecursively(player, player.getRuntimeBones()[child.getId()]);
			for(SpriterObject child: ((SpriterBone) object).getChildObjects())
				this.updateRecursively(player, player.getRuntimeObjects()[child.getId()]);
		}
	}*/

}
