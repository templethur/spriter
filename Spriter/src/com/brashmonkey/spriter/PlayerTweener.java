package com.brashmonkey.spriter;

public class PlayerTweener extends Player{
	
	private TweenedAnimation anim;
	private Player player1, player2;
	public boolean updatePlayers = true;
	public String baseBoneName = null;
	
	public PlayerTweener(Player player1, Player player2){
		super(player1.getEntity());
		this.setPlayers(player1, player2);
	}
	
	public PlayerTweener(Entity entity){
		super(entity);
		player1 = new Player(entity);
		player2 = new Player(entity);
		this.setPlayers(player1, player2);
	}
	
	@Override
	public void update(){
		if(updatePlayers){
			player1.update();
			player2.update();
		}
		anim.setAnimations(player1.animation, player2.animation);
		super.update();
		if(baseBoneName != null){
			int index = anim.onFirstMainLine()? player1.getBoneIndex(baseBoneName) : player2.getBoneIndex(baseBoneName);
			if(index == -1) throw new SpriterException("A bone with name \""+baseBoneName+"\" does no exist!");
			anim.base = anim.getCurrentKey().getBoneRef(index);
			super.update();
		}
	}
	
	public void setPlayers(Player player1, Player player2){
		if(player1.entity != player2.entity)
			throw new SpriterException("player1 and player2 have to hold the same entity!");
		this.player1 = player1;
		this.player2 = player2;
		if(player1.entity == entity) return;
		this.anim = new TweenedAnimation(player1.getEntity());
		anim.setAnimations(player1.animation, player2.animation);
		entity = player1.getEntity();
		super.setAnimation(anim);
	}
	
	public Player getFirstPlayer(){
		return this.player1;
	}
	
	public Player getSecondPlayer(){
		return this.player2;
	}
	
	@Override
	public void setAnimation(Animation anim){}
	@Override
	public void setEntity(Entity entity){}
}
