package com.brashmonkey.tests;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.brashmonkey.spriter.BoundingBox;
import com.brashmonkey.spriter.Drawer;
import com.brashmonkey.spriter.FileReference;
import com.brashmonkey.spriter.Loader;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.SCMLReader;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.Rectangle;
import com.brashmonkey.spriter.Timeline.Key.Object;


public class LibGdxTest implements ApplicationListener{
	
	Player player;
	ShapeRenderer renderer;
	SpriteBatch batch;
	Drawer<Sprite> drawer;
	SpriteLoader loader;
	OrthographicCamera cam;
	BoundingBox box = new BoundingBox();

	@Override
	public void create() {
		cam = new OrthographicCamera();
		cam.zoom = 1f;
		renderer = new ShapeRenderer();
		batch = new SpriteBatch();
		FileHandle handle = Gdx.files.absolute("assets/monster/basic_002.scml");
		Data data = new FileHandleSCMLReader(handle).getSpriterData();
		player = new Player(data.getEntity(0));
		
		this.loader = new SpriteLoader(data);
		this.loader.load(handle.file().getParent());
		
		this.drawer = new Drawer<Sprite>(this.loader) {
			@Override
			public void setColor(float r, float g, float b, float a) {
				renderer.setColor(r, g, b, a);
			}
			
			@Override
			public void rectangle(float x, float y, float width, float height) {
				renderer.rect(x, y, width, height);
			}
			
			@Override
			public void line(float x1, float y1, float x2, float y2) {
				renderer.line(x1, y1, x2, y2);
			}

			@Override
			public void circle(float x, float y, float radius) {
				renderer.circle(x, y, radius);
			}

			@Override
			public void draw(Object object) {
				Sprite sprite = loader.get(object.ref);
				float newPivotX = (sprite.getWidth() * object.pivot.x);
				float newX = object.position.x - newPivotX;
				float newPivotY = (sprite.getHeight() * object.pivot.y);
				float newY = object.position.y - newPivotY;
				
				sprite.setX(newX);
				sprite.setY(newY);
				
				sprite.setOrigin(newPivotX, newPivotY);
				sprite.setRotation(object.angle);
				
				sprite.setColor(1f, 1f, 1f, object.alpha);
				sprite.setScale(object.scale.x, object.scale.y);
				sprite.draw(batch);
			}
		};
	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, 0, 0f);
		
		cam.update();
		renderer.setProjectionMatrix(cam.combined);
		batch.setProjectionMatrix(cam.combined);
	}
	Rectangle bbox;
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Vector3 vec = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0f);
		this.cam.unproject(vec);
		
		player.update();
		
		batch.begin();
			drawer.draw(player);
		batch.end();
		
		if(Gdx.input.isKeyPressed(Keys.SPACE)){
			renderer.begin(ShapeType.Line);
				drawer.drawBones(player);
				
				drawer.setColor(1, 1, 1, 1);
				bbox = player.getBoundingRectangle(null);
				drawer.rectangle(bbox.left, bbox.bottom, bbox.size.width, bbox.size.height);
				drawer.drawBoxes(player);
			renderer.end();
		}
		if(Gdx.input.isKeyPressed(Keys.ENTER))
			player.characterMap = player.getEntity().getCharacterMapByName("standard");
		else player.characterMap = null;
		
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		renderer.dispose();
		loader.dispose();
	}
	
	public static void main(String[] args){
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Press Enter for changing character map| Press Space to show bounding box";
		cfg.width = 1280;
		cfg.height = 720;
		
		new LwjglApplication(new LibGdxTest(), cfg);
		Keyboard.enableRepeatEvents(true);
	}
	
	public class SpriteLoader extends Loader<Sprite> implements Disposable{
		
		private PixmapPacker packer;
		private HashMap<FileReference, Pixmap> pixmaps;	
		private HashMap<Pixmap, Boolean> pixmapsToDispose;
		private boolean pack;
		private int atlasWidth, atlasHeight;
		
		public SpriteLoader(Data data){
			this(data, true);
		}
		
		public SpriteLoader(Data data, boolean pack){
			this(data, 2048, 2048);
			this.pack = pack;
		}

		public SpriteLoader(Data data, int atlasWidth, int atlasHeight) {
			super(data);
			this.pack = true;
			this.atlasWidth = atlasWidth;
			this.atlasHeight = atlasHeight;
			this.pixmaps = new HashMap<FileReference, Pixmap>();
			this.pixmapsToDispose = new HashMap<Pixmap, Boolean>();
		}

		@Override
		protected Sprite loadResource(FileReference ref) {
			FileHandle f;
			String path = super.root+"/"+data.getFile(ref).name;
			switch(Gdx.app.getType()){
			case iOS: f = Gdx.files.absolute(path); break;
			default: f = Gdx.files.internal(path); break;
			}
			
			if(!f.exists()) throw new GdxRuntimeException("Could not find file handle "+ path + "! Please check your paths.");
			if(this.packer == null && this.pack)
				this.packer = new PixmapPacker(this.atlasWidth, this.atlasHeight, Pixmap.Format.RGBA8888, 2, true);
			final Pixmap pix = new Pixmap(f);
			this.pixmaps.put(ref, pix);
			return null;
		}
		
		/**
		 * Packs all loaded sprites into an atlas. Has to called after loading all sprites.
		 */
		protected void generatePackedSprites(){
			if(this.packer == null) return;
			TextureAtlas tex = this.packer.generateTextureAtlas(TextureFilter.Linear, TextureFilter.Linear, false);
			Set<FileReference> keys = this.resources.keySet();
			this.disposeNonPackedTextures();
			for(FileReference ref: keys){
				TextureRegion texReg = tex.findRegion(data.getFile(ref).name);
				texReg.setRegionWidth((int) data.getFile(ref).size.width);
				texReg.setRegionHeight((int) data.getFile(ref).size.height);
				super.resources.put(ref, new Sprite(texReg));
			}
		}
		
		private void disposeNonPackedTextures(){
			for(Entry<FileReference, Sprite> entry: super.resources.entrySet())
				entry.getValue().getTexture().dispose();
		}

		@Override
		public void dispose() {
			if(this.pack && this.packer != null) this.packer.dispose();
			else this.disposeNonPackedTextures();
		}
		
		protected void finishLoading() {
			Set<FileReference> refs = this.resources.keySet();
			for(FileReference ref: refs){
				Pixmap pix = this.pixmaps.get(ref);
				this.pixmapsToDispose.put(pix, false);
				this.createSprite(ref, pix);
				
				if(this.packer != null)	packer.pack(data.getFile(ref).name, pix);
			}
			if(this.pack) generatePackedSprites();
			this.disposePixmaps();
		}
		
		protected void createSprite(FileReference ref, Pixmap image){
			Texture tex = new Texture(image);
			tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			int width = (int) data.getFile(ref.folder, ref.file).size.width;
			int height = (int) data.getFile(ref.folder, ref.file).size.height;
			TextureRegion texRegion = new TextureRegion(tex, width, height);
			super.resources.put(ref, new Sprite(texRegion));
			pixmapsToDispose.put(image, true);
		}
		
		protected void disposePixmaps(){
			Pixmap[] maps = new Pixmap[this.pixmapsToDispose.size()];
			this.pixmapsToDispose.keySet().toArray(maps);
			for(Pixmap pix: maps){
				try{
					while(pixmapsToDispose.get(pix)){
						pix.dispose();
						pixmapsToDispose.put(pix, false);
					}
				} catch(GdxRuntimeException e){
					System.err.println("Pixmap was already disposed!");
				}
			}
			pixmapsToDispose.clear();
		}
		
	}
	
	private class FileHandleSCMLReader extends SCMLReader{

		public FileHandleSCMLReader(FileHandle file) {
			super(file.read());
		}
		
	}

}
