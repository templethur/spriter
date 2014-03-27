package com.brashmonkey.spriter;

import java.util.ArrayList;
import java.util.List;

import com.brashmonkey.spriter.Entity.ObjectInfo;


public class Timeline {

    public final List<Key> keys;
    public final int id;
    public final String name;
    public final ObjectInfo objectInfo;
    
    public Timeline(int id, String name, ObjectInfo objectInfo){
    	this(id, name, objectInfo, new ArrayList<Key>());
    }
    
    public Timeline(int id, String name, ObjectInfo objectInfo, List<Key> keys){
    	this.id = id;
    	this.name = name;
    	this.objectInfo = objectInfo;
    	this.keys = keys;
    }
    
    public void addKey(Key key){
    	this.keys.add(key);
    }
    
    public Key getKey(int id){
    	return this.keys.get(id);
    }
    
    public String toString(){
    	String toReturn = getClass().getSimpleName()+"|[id:"+id+", name: "+name+", object_info: "+objectInfo;
    	for(Key key: keys)
    		toReturn += "\n"+key;
    	toReturn+="]";
    	return toReturn;
    }
    
    public static class Key{
    	
    	public final int id, spin;
    	public int time;
    	public final Curve curve;
    	public boolean active;
    	private Bone object;
    	
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
    	
    	public void setObject(Bone object){
    		if(object == null) throw new IllegalArgumentException("object can not be null!");
    		this.object = object;
    	}
    	
    	public Bone object(){
    		return this.object;
    	}
    	
    	public String toString(){
    		return getClass().getSimpleName()+"|[id: "+id+", time: "+time+", spin: "+spin+"\ncurve: "+curve+"\nobject:"+object+"]";
    	}
    	
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
        	
        	public void unmap(Bone parent){
        		this.angle *= Math.signum(parent.scale.x)*Math.signum(parent.scale.y);
        		this.angle += parent.angle;
        		this.scale.scale(parent.scale);
        		this.position.scale(parent.scale);
        		this.position.rotate(parent.angle);
        		this.position.translate(parent.position);
        	}
        	
        	public void map(Bone parent){
        		this.position.translate(-parent.position.x, -parent.position.y);
        		this.position.rotate(-parent.angle);
        		this.position.scale(1f/parent.scale.x, 1f/parent.scale.y);
        		this.scale.scale(1f/parent.scale.x, 1f/parent.scale.y);
        		this.angle -=parent.angle;
    			this.angle *= Math.signum(parent.scale.x)*Math.signum(parent.scale.y);
        	}
    	}
    	
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
    
    public void print(){
    	System.out.println(this);
    }

}
