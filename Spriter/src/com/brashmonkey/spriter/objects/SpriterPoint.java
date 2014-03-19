package com.brashmonkey.spriter.objects;

import com.brashmonkey.spriter.interpolation.SpriterCurve;

public class SpriterPoint {
	
	protected float x, y, angle;
	protected int id, parentId, timeline, spin;
	protected SpriterAbstractObject parent;
	protected String name;
	public boolean active = true;
	public SpriterCurve curve;
	
	public SpriterPoint(){
		this.x = 0;
		this.y = 0;
		this.angle = 0f;
		this.id = -1;
		this.parentId = -1;
		this.name = "";
		this.parent = null;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * @return the angle
	 */
	public float getAngle() {
		return angle;
	}

	/**
	 * @param angle the angle to set
	 */
	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	public int getSpin() {
		return spin;
	}
	
	public void setSpin(int spin) {
		this.spin = spin;
	}
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the parent
	 */
	public SpriterAbstractObject getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(SpriterAbstractObject parent) {
		this.parent = parent;
	}

	/**
	 * @return the parentId
	 */
	public Integer getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the timeline
	 */
	public Integer getTimeline() {
		return timeline;
	}

	/**
	 * @param timeline the timeline to set
	 */
	public void setTimeline(int timeline) {
		this.timeline = timeline;
	}

	/**
	 * Sets the values of this instance to the given one.
	 * @param object which has to be manipulated.
	 */
	public void copyValuesTo(SpriterPoint object){
		object.setAngle(angle);
		object.setX(x);
		object.setY(y);
		object.setId(id);
		object.setParentId(parentId);
		object.setParent(parent);
		object.setTimeline(timeline);
		object.setSpin(spin);
		object.setName(name);
		object.curve = curve;
	}
	
	/**
	 * @param object to compare with
	 * @return true if both objects have the same id.
	 */
	public boolean equals(SpriterAbstractObject object){
		if(object == null) return false;
		return this.timeline == object.getTimeline();
	}
	
	/**
	 * @return whether this has a parent or not.
	 */
	public boolean hasParent(){
		return this.parentId != -1;
	}
	
	@Override
	public String toString(){
		return "id: "+ this.id+", name: "+this.name+", parent: "+ this.parentId +", x: "+this.x+", y: "+this.y+", angle:"+ this.angle+" timeline: "+this.timeline;
	}

}
