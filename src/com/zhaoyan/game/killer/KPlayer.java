package com.zhaoyan.game.killer;

import com.zhaoyan.game.util.Constants.Killers;

/**player for killer  game*/
public class KPlayer {

	//±‡∫≈
	private int number;
	//…Ì∑›
	private Killers identity;
	private boolean showIdentity;
	private boolean isDead;
	//checked or not
	private boolean isChecked;
	
	public KPlayer(int number){
		this.number = number;
		isDead = false;
		showIdentity = false;
	}
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	
	public Killers getIdentity() {
		return identity;
	}
	public void setIdentity(Killers identity) {
		this.identity = identity;
	}
	
	public boolean showIdentity() {
		return showIdentity;
	}
	public void setShowIdentity(boolean showIdentity) {
		this.showIdentity = showIdentity;
	}
	
	public boolean isDead() {
		return isDead;
	}
	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}
	
	public boolean isChecked(){
		return isChecked;
	}
	
	public void setChecked(boolean isChecked){
		this.isChecked = isChecked;
	}
}
