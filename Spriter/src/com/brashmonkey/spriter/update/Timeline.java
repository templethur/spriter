package com.brashmonkey.spriter.update;

import java.util.ArrayList;
import java.util.List;

import com.brashmonkey.spriter.SpriterPoint;
import com.brashmonkey.spriter.interpolation.SpriterCurve;
import com.brashmonkey.spriter.update.Entity.ObjectInfo;


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
    	public final SpriterCurve curve;
    	public boolean active;
    	private Bone object;
    	
    	public Key(int id, int time, int spin, SpriterCurve curve){
    		this.id = id;
    		this.time = time;
    		this.spin = spin;
    		this.curve = curve;
    	}
    	
    	public Key(int id,int time, int spin){
    		this(id, time, 1, new SpriterCurve());
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
        	public final SpriterPoint position, scale, pivot;
        	public float angle;
        	
        	public Bone(SpriterPoint position, SpriterPoint scale, SpriterPoint pivot, float angle){
        		this.position = position;
        		this.scale = scale;
        		this.angle = angle;
        		this.pivot = pivot;
        	}
        	
        	public Bone(SpriterPoint position){
        		this(position, new SpriterPoint(1f,1f), new SpriterPoint(0f, 1f), 0f);
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
        	
        	public void unmap(Bone parent){
        		this.angle *= Math.signum(parent.scale.x)*Math.signum(parent.scale.y);
        		this.angle += parent.angle;
        		this.scale.scale(parent.scale);
        		this.position.scale(parent.scale);
        		this.position.rotate(parent.angle);
        		this.position.translate(parent.position);
        	}
        	
        	public void map(Bone parent){
        		this.position.translate(- parent.position.x, - parent.position.y);
        		this.position.rotate(-parent.angle);
        		this.position.scale(1f/parent.scale.x, 1f/parent.scale.y);
        		this.scale.scale(1f/parent.scale.x, 1f/parent.scale.y);
        		this.angle -=parent.angle;
    			/*float xx = this.position.x - parent.position.x;
    			float yy = this.position.y - parent.position.y;
    			float angle = (float) toRadians(parent.angle); 
    			float cos = (float) Math.cos(angle);
    			float sin = (float) Math.sin(angle);
    			float newX = yy * sin + xx * cos;
    			float newY = yy * cos - xx * sin;*/
    			//this.position.set(this.position.x/parent.scale.x, this.position.y/parent.scale.y);
    			this.angle *= Math.signum(parent.scale.x)*Math.signum(parent.scale.y);
        	}
    	}
    	
    	public static class Object extends Bone{
    		
    		public float alpha;
    		public final FileReference ref;

			public Object(SpriterPoint position, SpriterPoint scale, SpriterPoint pivot, float angle, float alpha, FileReference ref) {
				super(position, scale, pivot, angle);
				this.alpha = alpha;
				this.ref = ref;
			}
			
			public Object(SpriterPoint position) {
				this(position, new SpriterPoint(1f,1f), new SpriterPoint(0f,1f), 0f, 1f, new FileReference(-1,-1));
			}
			
			public String toString(){
				return super.toString()+", pivot: "+pivot+", alpha: "+alpha+", reference: "+ref;
			}
			
			public void set(Object object){
				super.set(object);
				this.alpha = object.alpha;
				this.ref.set(object.ref);
			}
    		
    	}
    }
    
    public void print(){
    	System.out.println(this);
    }

}
