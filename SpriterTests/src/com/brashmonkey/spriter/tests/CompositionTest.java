package com.brashmonkey.spriter.tests;

import static com.brashmonkey.spriter.tests.TestBase.*;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.brashmonkey.spriter.PlayerTweener;
import com.brashmonkey.spriter.TweenedAnimation;

public class CompositionTest {
	public static void main(String[] args){
		create("GreyGuy/player.scml", "Simple compisition test");
		infoPosition.y = -250;
		information = "Click to change animations for the legs. Scroll to change the weight for the chest bone.";
		
		test = new ApplicationAdapter() {
			 PlayerTweener tweener;
			 TweenedAnimation anim;
			 
			 public void create(){
				 tweener = new PlayerTweener(data.getEntity(0));
				 players.add(tweener);
				 anim = (TweenedAnimation) tweener.getAnimation();
				 tweener.getFirstPlayer().setAnimation("shoot");
				 tweener.getSecondPlayer().setAnimation("walk");
				 tweener.baseBoneName = "chest";
				 anim.weight = 0f;
				 
				 tweener.getFirstPlayer().speed = 50;
				 
				 anim.baseAnimation = tweener.getEntity().getAnimation("walk");
				 addInputProcessor(new AnimationSwitchTest.AnimationSwitcher(tweener.getSecondPlayer()));
				 addInputProcessor(new InputAdapter(){
						public boolean scrolled(int am){
							anim.weight -= (float)am/10f;
							anim.weight = MathUtils.clamp(anim.weight, 0f, 1f);
							return false;
						}
					});
			 }
			 
			 public void render(){
				 anim.baseAnimation = tweener.getSecondPlayer().getAnimation();
			 }
		};
	}

}
