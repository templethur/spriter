package com.brashmonkey.spriter;


public class File {

    public final int id;
    public final String name;
    public final Dimension size;
    public final Point pivot;
    
    public File(int id, String name, Dimension size, Point pivot){
    	this.id = id;
    	this.name = name;
    	this.size = size;
    	this.pivot = pivot;
    }
    
    public String toString(){
    	return getClass().getSimpleName()+"|[id: "+id+", name: "+name+", size: "+size+", pivot: "+pivot;
    }

}
