package com.brashmonkey.spriter.tests.backend;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.brashmonkey.spriter.Drawer;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.SCMLReader;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.tests.LibGdxDrawer;
import com.brashmonkey.spriter.tests.LibGdxLoader;


public class LibGdxTest implements ApplicationListener{
	
	Player player;
	ShapeRenderer renderer;
	SpriteBatch batch;
	Drawer<Sprite> drawer;
	LibGdxLoader loader;
	OrthographicCamera cam;

	@Override
	public void create() {
		cam = new OrthographicCamera();
		cam.zoom = 1f;
		renderer = new ShapeRenderer();
		batch = new SpriteBatch();
		FileHandle handle = Gdx.files.internal("monster/basic_002.scml");
		Data data = new SCMLReader(handle.read()).getData();
		
		loader = new LibGdxLoader(data);
		loader.load(handle.file());
		
		drawer = new LibGdxDrawer(loader, batch, renderer);
		
		player = new Player(data.getEntity(0));
	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, 0, 0f);
		cam.update();
		renderer.setProjectionMatrix(cam.combined);
		batch.setProjectionMatrix(cam.combined);
	}
	
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		player.update();
		
		batch.begin();
			drawer.draw(player);
		batch.end();
		
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
		cfg.title = "LibGdx test";
		cfg.width = 1280;
		cfg.height = 720;
		
		new LwjglApplication(new LibGdxTest(), cfg);
	}

}
