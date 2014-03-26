package com.brashmonkey.spriter.tests;

import static com.brashmonkey.spriter.tests.TestBase.*;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.SCMLReader;

public class EntitySwitchTest {
	
	public static void main(String[] args){
		create("monster/basic_002.scml", "Animation speed test");
		test = new ApplicationAdapter() {
			Player player;
			LibGdxLoader loader;
			public void create(){
				player = new Player(data.getEntity(0));
				players.add(player);

				final Data guyData = new SCMLReader(Gdx.files.internal("GreyGuy/player.scml").read()).getData();
				loader = new LibGdxLoader(guyData);
				loader.load(Gdx.files.internal("GreyGuy/player.scml").file());
				
				addInputProcessor(new AnimationSwitchTest.AnimationSwitcher(player));
				addInputProcessor(new InputAdapter(){					
					@Override
					public boolean keyDown(int keycode){
						if(keycode == Keys.ENTER){
							if(player.getEntity() == data.getEntity(0)){
								player.setEntity(guyData.getEntity(0));
								drawer.setLoader(loader); //We have to set the loader because the entities are not in the same file
							}
							else{
								player.setEntity(data.getEntity(0));
								drawer.setLoader(TestBase.loader);
							}
						}
						return false;
					}
				});
			}
			
			public void dispose(){
				loader.dispose();
			}
			
			public void render(){
				information = "Press Enter to switch the entity for the player. Current entity: "+player.getEntity().name;
			}
		};
	}

}
