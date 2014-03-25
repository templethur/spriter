package com.brashmonkey.spriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Entity {

    public final int id;
    public final String name;
    private final List<Animation> animations;
    private final HashMap<String, Animation> namedAnimations;
    public final List<CharacterMap> characterMaps;
    public final List<ObjectInfo> objectInfos;
	
	public Entity(int id, String name){
		this(id, name, new ArrayList<Animation>(),  new ArrayList<CharacterMap>(), new ArrayList<Entity.ObjectInfo>());
	}
	
	public Entity(int id, String name, List<Animation> animations, List<CharacterMap> characterMaps, List<ObjectInfo> objectInfos){
		this.id = id;
		this.name = name;
		this.animations = animations;
		this.characterMaps = characterMaps;
		this.objectInfos = objectInfos;
		this.namedAnimations = new HashMap<String, Animation>();
	}
	
	public void addAnimation(Animation anim){
		this.animations.add(anim);
		this.namedAnimations.put(anim.name, anim);
	}
	
	public Animation getAnimation(int index){
		return this.animations.get(index);
	}
	
	public Animation getAnimation(String name){
		return this.namedAnimations.get(name);
	}
	
	public int animations(){
		return this.animations.size();
	}
	
	public boolean containsAnimation(Animation anim){
		return this.animations.contains(anim);
	}
	
	public Animation getMaxAnimationTimelines(){
		Animation maxAnim = getAnimation(0);
		for(Animation anim: this.animations){
			if(maxAnim.timelines() < anim.timelines()) maxAnim = anim;
		}
		return maxAnim;
	}
    
    /**
     * Searches for a character map for the given name.
     * @param name A name to search for.
     * @return The character map if one was found with the given name, null otherwise.
     */
    public CharacterMap getCharacterMapByName(String name){
    	for(CharacterMap map: this.characterMaps)
    		if(map.name.equals(name)) return map;
    	return null;
    }
    
    public void addCharacterMap(CharacterMap map){
    	this.characterMaps.add(map);
    }
    
    public void addInfo(ObjectInfo info){
    	this.objectInfos.add(info);
    }
    
    public ObjectInfo getInfo(int index){
    	if(this.objectInfos.size() == 0) return null;
    	else return this.objectInfos.get(index);
    }
    
    public ObjectInfo getInfo(String name){
    	for(ObjectInfo info: this.objectInfos)
    		if(info.name.equals(name)) return info;
    	return null;
    }
    
    public ObjectInfo getInfo(String name, ObjectType type){
    	ObjectInfo info = this.getInfo(name);
    	if(info.type == type) return info;
    	else return null;
    }
    
    public static enum ObjectType{
    	Sprite, Bone, Box, Point;
    	
    	public static ObjectType getObjectInfoFor(String type){
    		switch(type){
    		case "bone": return Bone;
    		case "box": return Box;
    		case "point": return Point;
    		default: return Sprite;
    		}
    	}
    }
    
    public static class ObjectInfo{
    	public final ObjectType type;
    	public final List<FileReference> frames;
    	public final String name;
    	public final Dimension size;
    	
    	public ObjectInfo(String name, ObjectType type, Dimension size, List<FileReference> frames){
    		this.type = type;
    		this.frames = frames;
    		this.name = name;
    		this.size = size;
    	}
    	
    	public ObjectInfo(String name, ObjectType type, Dimension size){
    		this(name, type, size, new ArrayList<FileReference>());
    	}
    	
    	public ObjectInfo(String name, ObjectType type, List<FileReference> frames){
    		this(name, type, new Dimension(0,0), frames);
    	}
    	
    	public String toString(){
    		return name + ": "+ type + ", size: "+size+"|frames:\n"+frames;
    	}
    }
    
    /**
     * Representation of a SCML character map. Instances of this class map references to references.
     * @author Trixt0r
     *
     */
    public static class CharacterMap extends HashMap<FileReference, FileReference>{
    	private static final long serialVersionUID = 6062776450159802283L;
    	
    	public final int id;
    	public final String name;
    	
    	public CharacterMap(int id, String name){
    		this.id = id;
    		this.name = name;
    	}
    	
    	/**
    	 * Returns the mapped reference for the given key.
    	 * @param key
    	 * @return The mapped reference if the key is in this map,
    	 * otherwise the given key itself is returned.
    	 */
    	public FileReference get(FileReference key){
    		if(!super.containsKey(key)) return key;
    		else return super.get(key);
    	}
    }
    
    public String toString(){
    	String toReturn = getClass().getSimpleName()+"|[id: "+id+", name: "+name+"]";
    	toReturn +="Object infos:\n";
    	for(ObjectInfo info: this.objectInfos)
    		toReturn += "\n"+info;
    	toReturn +="Character maps:\n";
    	for(CharacterMap map: this.characterMaps)
    		toReturn += "\n"+map;
    	toReturn +="Animations:\n";
    	for(Animation animaton: this.animations)
    		toReturn += "\n"+animaton;
    	toReturn+="]";
    	return toReturn;
    }

}
