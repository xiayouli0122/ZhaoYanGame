package com.zhaoyan.game.spy;

public class SpyConstant {
	public static final int INIT_PLAYER_NUM = 4;
	
	public static final int MY_WORDS = 0;
	public static final int GUESS_WORDS = 1;
	
	public static final String EXTRA_TOTAL_PLAYER_NUM = "total_player_num";
	public static final String EXTRA_SPY_NUM = "spy_num";
	public static final String EXTRA_HAS_BLANK = "has_blank";
	public static final String EXTRA_WORD = "word";
	
	public enum Spys{
		Blank, Spy, Civilian
	}
}
