package com.brashmonkey.spriter.tests;

import static com.brashmonkey.spriter.tests.TestBase.*;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.Spriter;

public class SpriterStressTest {
	
	public static void main(String[] args){
		create(null, "Simple stress test");
		information = "Hold space to draw.";
		test = new ApplicationAdapter() {
			String[] files = {"monster/basic_002.scml", "GreyGuy/player.scml"};
			public void create(){
				Spriter.setDrawerDependencies(batch, renderer);
				Spriter.init(LibGdxLoader.class, LibGdxDrawer.class);
				for(String file: files)
					Spriter.load(Gdx.files.internal(file).read(), file);
			}
			
			public void render(){
				if(Gdx.input.isKeyPressed(Keys.SPACE))
					Spriter.updateAndDraw(Gdx.graphics.getDeltaTime());
				else
					Spriter.update(Gdx.graphics.getDeltaTime());
				
				//if(Gdx.input.isKeyPressed(Keys.SPACE))
					//Spriter.draw();
				
				Gdx.graphics.setTitle("Fps: "+Gdx.graphics.getFramesPerSecond() + " @ " + Spriter.players()+" players");
				if(Gdx.graphics.getFramesPerSecond() >= 60) addPlayer();
			}
			
			public void dispose(){
				Spriter.dispose();
			}
			
			private void addPlayer(){
				Player p =  Spriter.newPlayer(files[MathUtils.random(files.length-1)], 0);
				p.copyObjects = false;
				p.speed = MathUtils.random(4f);
				p.setPosition(-Gdx.graphics.getWidth()/2f + MathUtils.random((float)Gdx.graphics.getWidth()),
						-Gdx.graphics.getHeight()/2f + MathUtils.random((float)Gdx.graphics.getHeight()));
				p.setAngle(MathUtils.random(360f));
				p.setAnimation(p.getEntity().getAnimation(MathUtils.random(p.getEntity().animations()-1)));
			}
		};
	}

}