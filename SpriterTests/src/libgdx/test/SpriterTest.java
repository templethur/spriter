package libgdx.test;
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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.brashmonkey.spriter.SpriterRectangle;
import com.brashmonkey.spriter.update.*;
import com.brashmonkey.spriter.update.Timeline.Key.Object;


public class SpriterTest implements ApplicationListener{
	
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
		SpriterData data = new FileHandleSCMLReader(handle).getSpriterData();
		player = new Player(data.getEntity(0));
		
		this.loader = new SpriteLoader(data);
		this.loader.load(handle.file().getParent());
		
		this.drawer = new Drawer<Sprite>(this.loader) {
			@Override
			public void setDrawColor(float r, float g, float b, float a) {
				renderer.setColor(r, g, b, a);
			}
			
			@Override
			public void drawRectangle(float x, float y, float width, float height) {
				renderer.rect(x, y, width, height);
			}
			
			@Override
			public void drawLine(float x1, float y1, float x2, float y2) {
				renderer.line(x1, y1, x2, y2);
			}

			@Override
			public void drawCircle(float x, float y, float radius) {
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
	SpriterRectangle bbox;
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
				
				drawer.setDrawColor(1, 1, 1, 1);
				bbox = player.getBoundingRectangle(null);
				drawer.drawRectangle(bbox.left, bbox.bottom, bbox.width, bbox.height);
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
		cfg.useGL20 = true;
		
		new LwjglApplication(new SpriterTest(), cfg);
		Keyboard.enableRepeatEvents(true);
	}
	
	private class SpriteLoader extends Loader<Sprite> implements Disposable{

		public SpriteLoader(SpriterData data) {
			super(data);
		}

		@Override
		protected Sprite loadResource(String path) {
			Texture tex = new Texture(Gdx.files.absolute(path));
			tex.setFilter(TextureFilter.Nearest, TextureFilter.Linear);
			return new Sprite(tex);
		}

		@Override
		public void dispose() {
			for(Entry<FileReference, Sprite> entry: super.resources.entrySet()){
				entry.getValue().getTexture().dispose();
			}
		}
		
	}
	
	private class FileHandleSCMLReader extends com.brashmonkey.spriter.update.SCMLReader{

		public FileHandleSCMLReader(FileHandle file) {
			super(file.read());
		}
		
	}

}
