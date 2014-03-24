package com.brashmonkey.spriter.update;

import com.brashmonkey.spriter.SpriterDimension;
import com.brashmonkey.spriter.SpriterPoint;

public class File {

    public final int id;
    public final String name;
    public final SpriterDimension size;
    public final SpriterPoint pivot;
    
    public File(int id, String name, SpriterDimension size, SpriterPoint pivot){
    	this.id = id;
    	this.name = name;
    	this.size = size;
    	this.pivot = pivot;
    }
    
    public String toString(){
    	return getClass().getSimpleName()+"|[id: "+id+", name: "+name+", size: "+size+", pivot: "+pivot;
    }

}
