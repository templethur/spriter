package com.discobeard.spriter.dom;

import java.util.HashMap;

import com.brashmonkey.spriter.file.Reference;

public class CharacterMap extends HashMap<Reference, Reference>{
	private static final long serialVersionUID = 6062776450159802283L;
	
	public final int id;
	public final String name;
	
	public CharacterMap(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public Reference get(Reference key){
		if(!super.containsKey(key)) return key;
		else return super.get(key);
	}
}
