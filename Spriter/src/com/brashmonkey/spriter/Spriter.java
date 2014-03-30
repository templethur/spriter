package com.brashmonkey.spriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("rawtypes")
public class Spriter {
	
	private static Object[] loaderDependencies = new Object[1], drawerDependencies = new Object[1];
	private static Class<?>[] loaderTypes = new Class<?>[1], drawerTypes = new Class<?>[1];
	static{
		loaderTypes[0] = Data.class;
		drawerTypes[0] = Loader.class;
	}
	private static Class<? extends Loader> loaderClass;
	
	private static final HashMap<String, Data> loadedData = new HashMap<String, Data>();
	private static final List<Player> players = new ArrayList<Player>();
	private static final List<Loader> loaders = new ArrayList<Loader>();
	private static Drawer<?> drawer;
	private static final HashMap<Entity, Loader> entityToLoader = new HashMap<Entity, Loader>();
	private static boolean initialized = false;
	
	
	public static void setLoaderDependencies(Object... loaderDependencies){
		if(loaderDependencies == null) return;
		Spriter.loaderDependencies = new Object[loaderDependencies.length+1];
		System.arraycopy(loaderDependencies, 0, Spriter.loaderDependencies, 1, loaderDependencies.length);
		loaderTypes = new Class[loaderDependencies.length+1];
		loaderTypes[0] = Data.class;
		for(int i = 0; i< loaderDependencies.length; i++)
			loaderTypes[i+1] = loaderDependencies[i].getClass();
	}
	
	public static void setDrawerDependencies(Object... drawerDependencies){
		if(drawerDependencies == null) return;
		Spriter.drawerDependencies = new Object[drawerDependencies.length+1];
		Spriter.drawerDependencies[0] = null;
		System.arraycopy(drawerDependencies, 0, Spriter.drawerDependencies, 1, drawerDependencies.length);
		drawerTypes = new Class[drawerDependencies.length+1];
		drawerTypes[0] = Loader.class;
		for(int i = 0; i< drawerDependencies.length; i++)
			drawerTypes[i+1] = drawerDependencies[i].getClass();
	}
	
	public static void init(Class<? extends Loader> loaderClass, Class<? extends Drawer> drawerClass){
		Spriter.loaderClass = loaderClass;
		try {
			drawer = drawerClass.getDeclaredConstructor(drawerTypes).newInstance(drawerDependencies);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		initialized = drawer != null;
	}
	
	public static void load(String scmlFile){
		load(new File(scmlFile));
	}
	
	public static void load(File scmlFile){
		try {
			load(new FileInputStream(scmlFile), scmlFile.getPath().replaceAll("\\\\", "/"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void load(InputStream stream, String scmlFile){
		SCMLReader reader = new SCMLReader(stream);
		Data data = reader.data;
		loadedData.put(scmlFile, data);
		loaderDependencies[0] = data;
		try {
			Loader loader = loaderClass.getDeclaredConstructor(loaderTypes).newInstance(loaderDependencies);
			loader.load(new File(scmlFile));
			loaders.add(loader);
			for(Entity entity: data.entities)
				entityToLoader.put(entity, loader);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public static Player newPlayer(String scmlFile, int entityIndex){
		return newPlayer(scmlFile, entityIndex, Player.class);
	}
	
	public static Player newPlayer(String scmlFile, int entityIndex, Class<? extends Player> playerClass){
		if(!loadedData.containsKey(scmlFile)) throw new SpriterException("You have to load \""+scmlFile+"\" before using it!");
		try {
			Player player = playerClass.getDeclaredConstructor(Entity.class).newInstance(loadedData.get(scmlFile).getEntity(entityIndex));
			players.add(player);
			return player;
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Player newPlayer(String scmlFile, String entityName){
		if(!loadedData.containsKey(scmlFile)) throw new SpriterException("You have to load \""+scmlFile+"\" before using it!");
		return newPlayer(scmlFile, loadedData.get(scmlFile).getEntityIndex(entityName));
	}
	
	public static void updateAndDraw(){
		update();
		draw();
	}
	
	public static void update(){
		if(!initialized) throw new SpriterException("Call init() before updating!");
		for(Player player: players)
			player.update();
	}
	
	@SuppressWarnings("unchecked")
	public static void draw(){
		if(!initialized) throw new SpriterException("Call init() before drawing!");
		for(Player player: players){
			drawer.loader = entityToLoader.get(player.getEntity());
			drawer.draw(player);
		}
	}
	
	public static Drawer drawer(){
		return drawer;
	}
	
	public static Data getData(String fileName){
		return loadedData.get(fileName);
	}
	
	public static int players(){
		return players.size();
	}
	
	public static void dispose(){
		drawer = null;
		drawerDependencies = new Object[1];
		drawerTypes = new Class<?>[1];
		drawerTypes[0] = Loader.class;
		
		entityToLoader.clear();
		
		for(Loader loader: loaders) loader.dispose();
			loaders.clear();
		loadedData.clear();
		loaderClass = null;
		loaderTypes = new Class<?>[1];
		loaderTypes[0] = Data.class;
		loaderDependencies = new Object[1];
		
		players.clear();
		
		initialized = false;
	}

}
