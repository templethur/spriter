package com.brashmonkey.spriter;

import java.util.ArrayList;
import java.util.List;

import com.brashmonkey.spriter.Entity.ObjectInfo;

/**
 * Represents a time line in a Spriter SCML file.
 * A time line holds an {@link #id}, a {@link #name} and at least one {@link Key}.
 * @author Trixt0r
 *
 */
public class Timeline {

    public final List<Key> keys;
    public final int id;
    public final String name;
    public final ObjectInfo objectInfo;
    
    Timeline(int id, String name, ObjectInfo objectInfo){
    	this(id, name, objectInfo, new ArrayList<Key>());
    }
    
    Timeline(int id, String name, ObjectInfo objectInfo, List<Key> keys){
    	this.id = id;
    	this.name = name;
    	this.objectInfo = objectInfo;
    	this.keys = keys;
    }
    
    void addKey(Key key){
    	this.keys.add(key);
    }
    
    /**
     * Returns a {@link Key} at the given index
     * @param index the index of the key.
     * @return the key with the given index.
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Key getKey(int index){
    	return this.keys.get(index);
    }
    
    public String toString(){
    	String toReturn = getClass().getSimpleName()+"|[id:"+id+", name: "+name+", object_info: "+objectInfo;
    	for(Key key: keys)
    		toReturn += "\n"+key;
    	toReturn+="]";
    	return toReturn;
    }
    
    /**
     * Represents a time line key in a Spriter SCML file.
     * A key holds an {@link #id}, a {@link #time}, a {@link #spin}, an {@link #object()} and a {@link #curve}.
     * @author Trixt0r
     *
     */
    public static class Key{
    	
    	public final int id, spin;
    	public int time;
    	public final Curve curve;
    	public boolean active;
    	private Object object;
    	
    	public Key(int id, int time, int spin, Curve curve){
    		this.id = id;
    		this.time = time;
    		this.spin = spin;
    		this.curve = curve;
    	}
    	
    	public Key(int id,int time, int spin){
    		this(id, time, 1, new Curve());
    	}
    	
    	public Key(int id, int time){
    		this(id, time, 1);
    	}
    	
    	public Key(int id){
    		this(id, 0);
    	}
    	
    	public void setObject(Object object){
    		if(object == null) throw new IllegalArgumentException("object can not be null!");
    		this.object = object;
    	}
    	
    	public Object object(){
    		return this.object;
    	}
    	
    	public String toString(){
    		return getClass().getSimpleName()+"|[id: "+id+", time: "+time+", spin: "+spin+"\ncurve: "+curve+"\nobject:"+object+"]";
    	}
    	
    	/**
    	 * Represents a bone in a Spriter SCML file.
    	 * A bone holds a {@link #position}, {@link #scale}, an {@link #angle} and a {@link #pivot}.
    	 * Bones are the only objects which can be used as a parent for other tweenable objects.
    	 * @author Trixt0r
    	 *
    	 */
    	public static class Bone{
        	public final Point position, scale, pivot;
        	public float angle;
        	
        	public Bone(Point position, Point scale, Point pivot, float angle){
        		this.position = position;
        		this.scale = scale;
        		this.angle = angle;
        		this.pivot = pivot;
        	}
        	
        	public Bone(Bone bone){
        		this(bone.position.copy(), bone.scale.copy(), bone.pivot.copy(), bone.angle);
        	}
        	
        	public Bone(Point position){
        		this(position, new Point(1f,1f), new Point(0f, 1f), 0f);
        	}
        	
        	public Bone(){
        		this(new Point());
        	}
        	
        	public boolean isBone(){
        		return !(this instanceof Object);
        	}
        	
        	public String toString(){
        		return getClass().getSimpleName()+"|position: "+position+", scale: "+scale+", angle: "+angle;
        	}
        	
        	public void set(Bone bone){
        		this.angle = bone.angle;
        		this.position.set(bone.position);
        		this.scale.set(bone.scale);
				this.pivot.set(bone.pivot);
        	}
        	
        	public void set(float x, float y, float angle, float scaleX, float scaleY, float pivotX, float pivotY){
        		this.angle = angle;
        		this.position.set(x, y);
        		this.scale.set(scaleX, scaleY);
        		this.pivot.set(pivotX, pivotY);
        	}
        	
        	/**
        	 * Maps this bone from it's parent's coordinate system to a global one.
        	 * @param parent the parent bone of this bone
        	 */
        	public void unmap(Bone parent){
        		this.angle *= Math.signum(parent.scale.x)*Math.signum(parent.scale.y);
        		this.angle += parent.angle;
        		this.scale.scale(parent.scale);
        		this.position.scale(parent.scale);
        		this.position.rotate(parent.angle);
        		this.position.translate(parent.position);
        	}
        	
        	/**
        	 * Maps this from it's global coordinate system to the parent's one.
        	 * @param parent the parent bone of this bone
        	 */
        	public void map(Bone parent){
        		this.position.translate(-parent.position.x, -parent.position.y);
        		this.position.rotate(-parent.angle);
        		this.position.scale(1f/parent.scale.x, 1f/parent.scale.y);
        		this.scale.scale(1f/parent.scale.x, 1f/parent.scale.y);
        		this.angle -=parent.angle;
    			this.angle *= Math.signum(parent.scale.x)*Math.signum(parent.scale.y);
        	}
    	}
    	
    	
    	/**
    	 * Represents an object in a Spriter SCML file.
    	 * A file has the same properties as a bone with an alpha and file extension.
    	 * @author Trixt0r
    	 *
    	 */
    	public static class Object extends Bone{
    		
    		public float alpha;
    		public final FileReference ref;

			public Object(Point position, Point scale, Point pivot, float angle, float alpha, FileReference ref) {
				super(position, scale, pivot, angle);
				this.alpha = alpha;
				this.ref = ref;
			}
			
			public Object(Point position) {
				this(position, new Point(1f,1f), new Point(0f,1f), 0f, 1f, new FileReference(-1,-1));
			}
			
			public Object(Object object){
				this(object.position.copy(), object.scale.copy(),object.pivot.copy(),object.angle,object.alpha,object.ref);
			}
			
			public Object(){
				this(new Point());
			}
			
			public String toString(){
				return super.toString()+", pivot: "+pivot+", alpha: "+alpha+", reference: "+ref;
			}
			
			public void set(Object object){
				super.set(object);
				this.alpha = object.alpha;
				this.ref.set(object.ref);
			}
			
			public void set(float x, float y, float angle, float scaleX, float scaleY, float pivotX, float pivotY, float alpha, int folder, int file){
				super.set(x, y, angle, scaleX, scaleY, pivotX, pivotY);
				this.alpha = alpha;
				this.ref.folder = folder;
				this.ref.file = file;
			}
    		
    	}
    }

}
