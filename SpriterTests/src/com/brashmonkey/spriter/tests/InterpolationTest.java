package com.brashmonkey.spriter.tests;

import static com.brashmonkey.spriter.tests.TestBase.*;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.MathUtils;
import com.brashmonkey.spriter.InterpolatedAnimation;
import com.brashmonkey.spriter.Player;

public class InterpolationTest {
	public static void main(String[] args){
		create("GreyGuy/player.scml", "Simple interpolation test");
		infoPosition.y = -250;
		information = "Press left mouse button to change left animation.\n"
						+"Press right mouse button to change right animation.\n"
						+"Scroll to change interpolation weight.";
		test = new ApplicationAdapter() {
			Player player1, player2, player3;
			InterpolatedAnimation inter;
			
			public void create(){
				player1 = createPlayer(data.getEntity(0));
				player1.setPosition(-400, 0);
				player2 = createPlayer(data.getEntity(0));
				player2.setPosition(400, 0);
				player3 = createPlayer(data.getEntity(0));
				inter = new InterpolatedAnimation(data.getEntity(0));
				inter.setAnimations(player1.getAnimation(), player2.getAnimation());
				player3.setAnimation(inter);
				
				final AnimationSwitchTest.AnimationSwitcher switcher = new AnimationSwitchTest.AnimationSwitcher(player1);
				
				addInputProcessor(new InputAdapter(){		
					public boolean touchDown(int x, int y, int p, int b){
						if(b == Buttons.LEFT) switcher.player = player1;
						else if(b == Buttons.RIGHT) switcher.player = player2;
						return false;
					}
					
					public boolean scrolled(int am){
						inter.weight -= (float)am/10f;
						inter.weight = MathUtils.clamp(inter.weight, 0f, 1f);
						return false;
					}
				});
				
				addInputProcessor(switcher);
			}
			
			public void render(){
				inter.setAnimations(player1.getAnimation(), player2.getAnimation());
			}
		};
	}
}
