package com.brashmonkey.spriter;

import java.util.HashMap;

public abstract class Loader<R> {
	
	protected final HashMap<FileReference, R> resources;
	protected final Data data;
	protected String root = "";
	
	public Loader(Data data){
		this.data = data;
		this.resources = new HashMap<FileReference, R>(50);
	}
	
	protected abstract R loadResource(FileReference ref);
	
	protected void finishLoading(){}
	protected void beginLoading(){}
	
	
	public void load(String root){
		this.root = root;
		this.beginLoading();
		for(Folder folder: data.folders){
			for(File file: folder.files){
				FileReference ref = new FileReference(folder.id, file.id);
				this.resources.put(ref, this.loadResource(ref));
			}
		}
		this.finishLoading();
	}
	
	public void load(java.io.File file){
		this.load(file.getParent());
	}
	
	public R get(FileReference ref){
		return this.resources.get(ref);
	}

}
