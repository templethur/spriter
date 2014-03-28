package com.brashmonkey.spriter;

public class PlayerInterpolator{
	
	private InterpolatedAnimation anim;
	private Player player1, player2, player3;
	public float weight = .5f;
	public float spriteThreshold = .5f;
	
	public PlayerInterpolator(Player player1, Player player2){
		player3 = new Player(player1.getEntity());
		this.setPlayers(player1, player2);
	}
	
	public void update(){
		anim.weight = weight;
		anim.spriteThreshold = spriteThreshold;
		anim.setAnimations(player1.animation, player2.animation);
		player3.update();
		//this.anim.update(0, player3.root);
	}
	
	public void setPlayers(Player player1, Player player2){
		if(player1.entity != player2.entity)
			throw new SpriterException("player1 and player2 have to hold the same entity!");
		player3.setEntity(player1.entity);
		this.player1 = player1;
		this.player2 = player2;
		this.anim = new InterpolatedAnimation(player1.getEntity());
		anim.setAnimations(player1.animation, player2.animation);
		this.player3.setAnimation(anim);
	}
	
	public Player getFirstPlayer(){
		return this.player1;
	}
	
	public Player getSecondPlayer(){
		return this.player2;
	}
	
	public Player getInterpolatedPlayer(){
		return this.player3;
	}

}
