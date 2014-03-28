package com.brashmonkey.spriter;

import java.util.ArrayList;
import java.util.List;

public class Mainline {

    public final List<Key> keys;

    public Mainline(){
    	this(new ArrayList<Key>());
    }
    
    public Mainline(List<Key> keys){
    	this.keys = keys;
    }
    
    public String toString(){
    	String toReturn = getClass().getSimpleName()+"|";
    	for(Key key: keys)
    		toReturn += "\n"+key;
    	toReturn+="]";
    	return toReturn;
    }
    
    public Key getKey(int id){
    	return this.keys.get(id);
    }
    
    public Key getKeyBeforeTime(int time){
    	Key found = null;
    	for(Key key: this.keys){
    		if(key.time <= time) found = key;
    		else break;
    	}
    	return found;
    }
    
    public static class Key{
    	
    	public final int id, time;
    	final List<BoneRef> boneRefs;
    	final List<ObjectRef> objectRefs;
    	public final Curve curve;
    	
    	public Key(int id, int time, Curve curve){
    		this(id, time, curve, new ArrayList<BoneRef>(), new ArrayList<ObjectRef>());
    	}
    	
    	public Key(int id, int time, Curve curve, List<BoneRef> boneRefs, List<ObjectRef> objectRefs){
    		this.id = id;
    		this.time = time;
    		this.curve = curve;
    		this.boneRefs = boneRefs;
    		this.objectRefs = objectRefs;
    	}
    	
    	public void addBoneRef(BoneRef ref){
    		this.boneRefs.add(ref);
    	}
    	
    	public void addObjectRef(ObjectRef ref){
    		this.objectRefs.add(ref);
    	}
    	
    	public BoneRef getBoneRef(int i){
    		if(i < 0 || i >= this.boneRefs.size()) return null;
    		else return this.boneRefs.get(i);
    	}
    	
    	public ObjectRef getObjectRef(int i){
    		if(i < 0 || i >= this.objectRefs.size()) return null;
    		else return this.objectRefs.get(i);
    	}
    	
        public BoneRef getBoneRef(BoneRef ref){
    		for(BoneRef boneRef: this.boneRefs)
    			if(boneRef.timeline == ref.timeline) return boneRef;
        	return null;
        }
        
        public BoneRef getBoneRefTimeline(int timeline){
    		for(BoneRef boneRef: this.boneRefs)
    			if(boneRef.timeline == timeline) return boneRef;
        	return null;
        }
    	
        public ObjectRef getObjectRef(ObjectRef ref){
    		for(ObjectRef objRef: this.objectRefs)
    			if(objRef.timeline == ref.timeline) return objRef;
        	return null;
        }
        
        public BoneRef getObjectRefTimeline(int timeline){
    		for(ObjectRef objRef: this.objectRefs)
    			if(objRef.timeline == timeline) return objRef;
        	return null;
        }
    	
    	public String toString(){
        	String toReturn = getClass().getSimpleName()+"|[id:"+id+", time: "+time+", curve: ["+curve+"]";
        	for(BoneRef ref: boneRefs)
        		toReturn += "\n"+ref;
        	for(ObjectRef ref: objectRefs)
        		toReturn += "\n"+ref;
        	toReturn+="]";
        	return toReturn;
        }
    	
    	public static class BoneRef{
    		public final int id, key, timeline;
    		public final BoneRef parent;
    		
    		public BoneRef(int id, int timeline, int key, BoneRef parent){
    			this.id = id;
    			this.timeline = timeline;
    			this.key = key;
    			this.parent = parent;
    		}
    		
    		public String toString(){
    			int parentId = (parent != null) ? parent.id:-1;
    			return getClass().getSimpleName()+"|id: "+id+", parent:"+parentId+", timeline: "+timeline+", key: "+key;
    		}
    	}
    	
    	public static class ObjectRef extends BoneRef implements Comparable<ObjectRef>{
    		public final int zIndex;
    		
    		public ObjectRef(int id, int timeline, int key, BoneRef parent, int zIndex){
    			super(id, timeline, key, parent);
    			this.zIndex = zIndex;
    		}
    		
    		public String toString(){
    			return super.toString()+", z_index: "+zIndex;
    		}

			@Override
			public int compareTo(ObjectRef o) {
				return (int)Math.signum(zIndex-o.zIndex);
			}
    	}
    }
    
    public void print(){
    	System.out.println(this);
    }

}
