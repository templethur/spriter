package com.brashmonkey.spriter.objects;

public class SpriterBox extends SpriterAbstractObject implements Comparable<SpriterObject>{
	float pivotX, pivotY;
	int zIndex;
	
	public float getPivotX() {
		return pivotX;
	}
	
	public void setPivotX(float pivotX) {
		this.pivotX = pivotX;
	}
	
	public float getPivotY() {
		return pivotY;
	}
	
	public void setPivotY(float pivotY) {
		this.pivotY = pivotY;
	}
	
	public int getZIndex() {
		return zIndex;
	}
	
	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}
	
	@Override
	public void copyValuesTo(SpriterAbstractObject object){
		super.copyValuesTo(object);
		if(!(object instanceof SpriterObject)) return;
		((SpriterObject)object).setPivotX(pivotX);
		((SpriterObject)object).setPivotY(pivotY);
		((SpriterObject)object).setZIndex(zIndex);
	}
	
	
	/**
	 * Compares the z_index of the given SpriterObject with this.
	 * @param o SpriterObject to compare with.
	 */
	public int compareTo(SpriterObject o) {
		if(this.zIndex < o.zIndex) return -1;
		else if(this.zIndex > o.zIndex) return 1;
		else return 0;
	}
}
