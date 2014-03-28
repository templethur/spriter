package com.brashmonkey.spriter;

public class PlayerInterpolator extends Player{
	
	private InterpolatedAnimation anim;
	private Player player1, player2;
	public float weight = .5f;
	public float spriteThreshold = .5f;
	
	public PlayerInterpolator(Player player1, Player player2){
		super(player1.getEntity());
		this.setPlayers(player1, player2);
	}
	
	public void update(){
		if(player1 == null){
			this.anim.setAnimations(entity.getAnimation(0), entity.getAnimation(0));
			return;
		}
		this.anim.weight = weight;
		this.anim.spriteThreshold = spriteThreshold;
		this.anim.setAnimations(player1.animation, player2.animation);
		this.anim.update(time, root);
	}
	
	public void setPlayers(Player player1, Player player2){
		if(player1.entity != player2.entity)
			throw new SpriterException("player1 and player2 have to hold the same entity!");
		if(player1.entity != this.entity) this.setEntity(player1.entity);
		this.player1 = player1;
		this.player2 = player2;
	}
	
	public Player getFirstPlayer(){
		return this.player1;
	}
	
	public Player getSecondPlayer(){
		return this.player2;
	}
	
	public void setEntity(Entity entity){
		super.setEntity(entity);
		this.anim = new InterpolatedAnimation(entity);
		super.setAnimation(this.anim);
	}
	
	public void setAnimation(Animation anim){
		//throw new SpriterException("This is not possible with a PlayerInterpolator instance!");
	}

}
