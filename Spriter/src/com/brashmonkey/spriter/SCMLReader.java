/**************************************************************************
 * Copyright 2013 by Trixt0r
 * (https://github.com/Trixt0r, Heinrich Reich, e-mail: trixter16@web.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
***************************************************************************/
package com.brashmonkey.spriter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import com.brashmonkey.spriter.Entity.*;
import com.brashmonkey.spriter.Mainline.Key.*;
import com.brashmonkey.spriter.XmlReader.*;

/**
 * This class was implemented to give you the chance loading scml files on android since JAXB does not run on android devices.
 * @author Trixt0r
 */
public class SCMLReader {
	
	protected Data data;
	
	public SCMLReader(String filename){
		this.data = this.load(filename);
	}
	
	public SCMLReader(InputStream stream){
		this.data = this.load(stream);
	}
	
	/**
	 * Reads the whole given scml file.
	 * @param filename Path to scml file.
	 * @return Spriter data in form of lists.
	 */
	public Data load(String filename){
		try {
			return load(new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Data load(InputStream stream){
		XmlReader reader = new XmlReader();
		try {
			Element root = reader.parse(stream);
			ArrayList<Element> folders = root.getChildrenByName("folder");
			ArrayList<Element> entities = root.getChildrenByName("entity");
			data = new Data(root.get("scml_version"), root.get("generator"),root.get("generator_version"),
					new ArrayList<Folder>(folders.size()),
					new ArrayList<Entity>(entities.size()));
			loadFolders(folders);
			loadEntities(entities);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	protected void loadFolders(ArrayList<Element> folders){
		for(int i = 0; i < folders.size(); i++){
			Element repo = folders.get(i);
			Folder folder = new Folder(repo.getInt("id"), repo.get("name"));
			ArrayList<Element> files = repo.getChildrenByName("file");
			for(int j = 0; j < files.size(); j++){
				Element f = files.get(j);
				File file = new File(f.getInt("id"), f.get("name"),
						new Dimension(f.getInt("width", 0), f.getInt("height", 0)),
						new Point(f.getFloat("pivot_x", 0f), f.getFloat("pivot_y", 1f)));
				folder.addFile(file);
			}
			data.addFolder(folder);
		}
	}

	protected void loadEntities(ArrayList<Element> entities){
		for(int i = 0; i < entities.size(); i++){
			Element e = entities.get(i);
			ArrayList<Element> infos = e.getChildrenByName("obj_info");
			ArrayList<Element> charMaps = e.getChildrenByName("character_map");
			ArrayList<Element> animations = e.getChildrenByName("animation");
			Entity entity = new Entity(e.getInt("id"), e.get("name"),
					new ArrayList<Animation>(animations.size()),
					new ArrayList<CharacterMap>(charMaps.size()),
					new ArrayList<ObjectInfo>(infos.size()));
			data.addEntity(entity);
			loadObjectInfos(infos, entity);
			loadCharacterMaps(charMaps, entity);
			loadAnimations(animations, entity);
		}
	}
	
	protected void loadObjectInfos(ArrayList<Element> infos, Entity entity){
		for(int i = 0; i< infos.size(); i++){
			Element info = infos.get(i);
			Entity.ObjectInfo objInfo = new Entity.ObjectInfo(info.get("name","info"+i),
									Entity.ObjectType.getObjectInfoFor(info.get("type","")),
									new Dimension(info.getFloat("w", 0), info.getFloat("h", 0)));
			entity.addInfo(objInfo);
			Element frames = info.getChildByName("frames");
			if(frames == null) continue;
			ArrayList<Element> frameIndices = frames.getChildrenByName("i");
			for(Element index: frameIndices){
				int folder = index.getInt("folder", 0);
				int file =  index.getInt("file", 0);
				objInfo.frames.add(new FileReference(folder, file));
			}
		}
	}
	
	protected void loadCharacterMaps(ArrayList<Element> maps, Entity entity){
		for(int i = 0; i< maps.size(); i++){
			Element map = maps.get(i);
			Entity.CharacterMap charMap = new Entity.CharacterMap(map.getInt("id"), map.getAttribute("name", "charMap"+i));
			entity.characterMaps.add(charMap);
			ArrayList<Element> mappings = map.getChildrenByName("map");
			for(Element mapping: mappings){
				int folder = mapping.getInt("folder");
				int file =  mapping.getInt("file");
				charMap.put(new FileReference(folder, file),
						new FileReference(mapping.getInt("target_folder", folder), mapping.getInt("target_file", file)));
			}
		}
	}
	
	protected void loadAnimations(ArrayList<Element> animations, Entity entity){
		for(int i = 0; i < animations.size(); i++){
			Element a = animations.get(i);
			ArrayList<Element> timelines = a.getChildrenByName("timeline");
			Animation animation = new Animation(a.getInt("id"), a.get("name"), a.getInt("length"), a.getBoolean("looping", true),
					new ArrayList<Timeline>(timelines.size()));
			entity.addAnimation(animation);
			loadMainline(a.getChildByName("mainline"), animation);
			loadTimelines(timelines, animation, entity);
			animation.prepare();
		}
	}
	
	protected void loadMainline(Element mainline, Animation animation){
		Mainline main = animation.mainline;
		loadMainlineKeys(mainline.getChildrenByName("key"),main);
	}
	
	protected void loadMainlineKeys(ArrayList<Element> keys, Mainline main){
		for(int i = 0; i < keys.size(); i++){
			Element k = keys.get(i);
			ArrayList<Element> objectRefs = k.getChildrenByName("object_ref");
			ArrayList<Element> boneRefs = k.getChildrenByName("bone_ref");
			Curve curve = new Curve();
			curve.setType(Curve.getType(k.get("curve_type","linear")));
			curve.constraints.set(k.getFloat("c1", 0f),k.getFloat("c2", 0f),k.getFloat("c3", 0f),k.getFloat("c4", 0f));
			Mainline.Key key = new Mainline.Key(k.getInt("id"), k.getInt("time", 0), curve,
					new ArrayList<BoneRef>(boneRefs.size()),
					new ArrayList<ObjectRef>(objectRefs.size()));
			main.keys.add(key);
			loadRefs(objectRefs, boneRefs, key);
		}
	}
	
	protected void loadRefs(ArrayList<Element> objectRefs, ArrayList<Element> boneRefs, Mainline.Key key){
		for(Element e: boneRefs){
			BoneRef boneRef = new BoneRef(e.getInt("id"),e.getInt("timeline"),
							e.getInt("key"), key.getBoneRef(e.getInt("parent", -1)));
			key.addBoneRef(boneRef);
		}

		for(Element o: objectRefs){
			ObjectRef objectRef = new ObjectRef(o.getInt("id"),o.getInt("timeline"),
							o.getInt("key"), key.getBoneRef(o.getInt("parent", -1)), o.getInt("z_index",0));
			key.addObjectRef(objectRef);
		}
		Collections.sort(key.objectRefs);
	}
	
	protected void loadTimelines(ArrayList<Element> timelines, Animation animation, Entity entity){
		for(int i = 0; i< timelines.size(); i++){
			Element t = timelines.get(i);
			ArrayList<Element> keys = timelines.get(i).getChildrenByName("key");
			String name = t.get("name");
			ObjectType type = ObjectType.getObjectInfoFor(t.get("object_type", "sprite"));
			ObjectInfo info = entity.getInfo(name);
			if(info == null) info = new ObjectInfo(name, type, new Dimension(0,0));
			Timeline timeline = new Timeline(t.getInt("id"), name, info, new ArrayList<Timeline.Key>(keys.size()));
			animation.addTimeline(timeline);
			loadTimelineKeys(keys, timeline);
		}
	}
	
	protected void loadTimelineKeys(ArrayList<Element> keys, Timeline timeline){
		for(int i = 0; i< keys.size(); i++){
			Element k = keys.get(i);
			Curve curve = new Curve();
			curve.setType(Curve.getType(k.get("curve_type", "linear")));
			curve.constraints.set(k.getFloat("c1", 0f),k.getFloat("c2", 0f),k.getFloat("c3", 0f),k.getFloat("c4", 0f));
			Timeline.Key key = new Timeline.Key(k.getInt("id"), k.getInt("time", 0), k.getInt("spin", 1), curve);
			Element obj = k.getChildByName("bone");
			if(obj == null) obj = k.getChildByName("object");
			
			Point position = new Point(obj.getFloat("x", 0f), obj.getFloat("y", 0f));
			Point scale = new Point(obj.getFloat("scale_x", 1f), obj.getFloat("scale_y", 1f));
			Point pivot = new Point(obj.getFloat("pivot_x", 0f), obj.getFloat("pivot_y", (timeline.objectInfo.type == ObjectType.Bone)? .5f:1f));
			float angle = obj.getFloat("angle", 0f), alpha = 1f;
			int folder = -1, file = -1;
			if(obj.getName().equals("object")){
				if(timeline.objectInfo.type == ObjectType.Sprite){
					alpha = obj.getFloat("a", 1f);
					folder = obj.getInt("folder", -1);
					file = obj.getInt("file", -1);
					File f = data.getFolder(folder).getFile(file);
					pivot = new Point(obj.getFloat("pivot_x", f.pivot.x), obj.getFloat("pivot_y", f.pivot.y));
					timeline.objectInfo.size.set(f.size);
				}
			}
			Timeline.Key.Object object;
			if(obj.getName().equals("bone")) object = new Timeline.Key.Object(position, scale, pivot, angle, alpha, new FileReference(folder, file));
			else object = new Timeline.Key.Object(position, scale, pivot, angle, alpha, new FileReference(folder, file));
			key.setObject(object);
			timeline.addKey(key);
		}
	}
	
	public Data getData(){
		return data;
	}
	
}

