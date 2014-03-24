package libgdx.test;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.brashmonkey.spriter.Spriter;
import com.brashmonkey.spriter.player.SpriterPlayer;
import com.brashmonkey.spriter.update.SCMLReader;
import com.brashmonkey.spriter.update.SpriterData;
import com.brashmonkey.spriter.xml.FileHandleSCMLReader;

public class SpriterTestLibGDX implements ApplicationListener{
	
	public static void main(String... args){
		/*LwjglApplicationConfiguration cfg =  new LwjglApplicationConfiguration();
		cfg.title = "Spriter test for LibGDX|Click to see character maps in action";
		cfg.useGL20 = false;
		cfg.width = 1280;
		cfg.height = 720;
		cfg.resizable = false;
		new LwjglApplication(new SpriterTestLibGDX(), cfg);*/
		long time = System.currentTimeMillis();
		SpriterData data = new SCMLReader("C:/Users/Trixt0r/SCML files/AdventurePlatfortmerPack_Essentials/PlatformerPack/player.scml").getSpriterData();
		System.out.println(System.currentTimeMillis()-time+"ms");
		//System.out.println(data);
	}

	private SpriteBatch batch;
	private ShapeRenderer renderer;
	private OrthographicCamera cam;
	private SpriteLoader loader;
	private SpriteDrawer drawer;
	private SpriterPlayer player;
	private Spriter spriter;
	
	@Override
	public void create() {
		this.batch = new SpriteBatch();
		this.renderer = new ShapeRenderer();
		this.cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		this.loader = new SpriteLoader(2048, 2048);
		this.drawer = new SpriteDrawer(this.loader, this.batch);
		this.drawer.renderer = this.renderer;
		this.drawer.drawBoxes = true;
		
		this.spriter = FileHandleSCMLReader.getSpriter(Gdx.files.absolute(/*"assets/monster/basic_002.scml"*/"C:/Users/Trixt0r/SCML files/AdventurePlatfortmerPack_Essentials/PlatformerPack/player.scml"), this.loader);
		this.player = new SpriterPlayer(this.spriter.getSpriterData(), 0, this.loader);
		this.player.setFrameSpeed(15);
		
		Gdx.input.setInputProcessor(new InputAdapter() {			
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				//if(player.characterMap == null) player.setCharacterMap("standard");
				//else player.characterMap = null;
				if(button == Buttons.RIGHT)
					player.setAnimationIndex((player.getAnimationIndex()-1 + player.getEntity().getAnimation().size())%player.getEntity().getAnimation().size());
				else player.setAnimationIndex((player.getAnimationIndex()+1)%player.getEntity().getAnimation().size());
				return false;
			}
		});
	}

	@Override
	public void resize(int width, int height) {
		this.cam.setToOrtho(false, width, height);
		this.batch.setProjectionMatrix(this.cam.combined);
		this.renderer.setProjectionMatrix(this.cam.combined);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		this.player.update(this.cam.viewportWidth/2, this.cam.viewportHeight/2);
		this.player.calcBoundingBox(null);
		
		this.batch.begin();
			this.drawer.draw(player);
		this.batch.end();
		
		this.renderer.begin(ShapeType.Line);
			this.drawer.debugDraw(player);
		this.renderer.end();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		this.batch.dispose();
		this.renderer.dispose();
		this.loader.dispose();
	}

}
