package com.brashmonkey.spriter;

public class FileReference {
	
	public int folder, file;
	private int hashCode;
	
	public FileReference(int folder, int file){
		this.set(folder, file);
	}
	
	@Override
	public int hashCode(){
		return this.hashCode;
	}
	
	@Override
	public boolean equals(Object ref){
		if(ref instanceof FileReference){
			return this.file == ((FileReference)ref).file && this.folder == ((FileReference)ref).folder;
		} else return false;
	}
	
	public void set(int folder, int file){
		this.folder = folder;
		this.file = file;
		this.hashCode = (folder+","+file).hashCode();
	}
	
	public void set(FileReference ref){
		this.set(ref.folder, ref.file);
	}
	
	public boolean hasFile(){
		return this.file != -1;
	}
	
	public boolean hasFolder(){
		return this.folder != -1;
	}
	
	public String toString(){
		return "[folder: "+folder+", file: "+file+"]";
	}

}
