package com.brashmonkey.spriter.update;

import java.util.ArrayList;
import java.util.List;

public class Folder {

    public final List<File> files;
    public final Integer id;
    public final String name;
    
    public Folder(int id, String name){
    	this(id, name, new ArrayList<File>());
    }
    
    public Folder(int id, String name, List<File> files){
    	this.id = id;
    	this.name = name;
    	this.files = files;
    }
    
    public void addFile(File file){
    	this.files.add(file);
    }
    
    public File getFile(int i){
    	return files.get(i);
    }
    
    public String toString(){
    	String toReturn = getClass().getSimpleName()+"|[id: "+id+", name: "+name;
    	for(File file: files)
    		toReturn += "\n"+file;
    	toReturn += "]";
    	return toReturn;
    }
}
