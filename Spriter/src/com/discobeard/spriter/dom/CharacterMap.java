package com.discobeard.spriter.dom;

import java.util.HashMap;

import com.brashmonkey.spriter.file.Reference;

/**
 * Representation of a SCML character map. Instances of this class map references to references.
 * @author Trixt0r
 *
 */
public class CharacterMap extends HashMap<Reference, Reference>{
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
	public Reference get(Reference key){
		if(!super.containsKey(key)) return key;
		else return super.get(key);
	}
}
