package com.brashmonkey.spriter.tests;

import static com.brashmonkey.spriter.tests.TestBase.*;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.brashmonkey.spriter.Spriter;

public class SpriterClassTest {
	
	public static void main(String[] args){
		create("GreyGuy/player.scml", "Simple compisition test");
		test = new ApplicationAdapter() {
			public void create(){
				Spriter.setDrawerDependencies(batch, renderer);
				Spriter.init(LibGdxLoader.class, LibGdxDrawer.class);
				Spriter.load(Gdx.files.internal("monster/basic_002.scml").read(), "monster/basic_002.scml");
				Spriter.load(Gdx.files.internal("GreyGuy/player.scml").read(), "GreyGuy/player.scml");
				Spriter.newPlayer("monster/basic_002.scml", 0);
				Spriter.newPlayer("GreyGuy/player.scml", 0);
			}
			
			public void render(){
				Spriter.updateAndDraw();
			}
			
			public void dispose(){
				Spriter.dispose();
			}
		};
	}

}
