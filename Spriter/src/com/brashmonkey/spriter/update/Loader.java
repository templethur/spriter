package com.brashmonkey.spriter.update;

import java.util.HashMap;

public abstract class Loader<R> {
	
	protected final HashMap<FileReference, R> resources;
	protected final SpriterData data;
	
	public Loader(SpriterData data){
		this.data = data;
		this.resources = new HashMap<FileReference, R>(50);
	}
	
	protected abstract R loadResource(String path);
	
	public void load(String root){
		for(Folder folder: data.folders){
			for(File file: folder.files){
				FileReference ref = new FileReference(folder.id, file.id);
				this.resources.put(ref, this.loadResource(root+"/"+file.name));
			}
		}
	}
	
	public R get(FileReference ref){
		return this.resources.get(ref);
	}

}
