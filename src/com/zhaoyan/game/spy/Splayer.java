package com.zhaoyan.game.spy;

import com.zhaoyan.game.spy.SpyConstant.Spys;

public class Splayer {
	 
	private int number;
	private Spys identity;
	private String word;
	private boolean isDead;
	
	public Splayer(int number){
		this.number = number;
		isDead = false;
	}
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public Spys getIdentity() {
		return identity;
	}
	public void setIdentity(Spys identity) {
		this.identity = identity;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public boolean isDead() {
		return isDead;
	}
	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}
	
}
