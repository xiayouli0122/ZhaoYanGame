package com.zhaoyan.game.db;

import android.provider.BaseColumns;

public class DbData {
	public static final String DATABASE_NAME = "game.db";
	public static final int DATABASE_VERSION = 1;

	public static final String AUTHORITY = "com.zhaoyan.game.db";
	
	public static final class SpyColumns implements BaseColumns{
		public static final String TABLE_NAME = "spy";
		
		public static final String WORD1 = "word1"; 
		public static final String WORD2 = "word2";
		public static final String GROUP = "spy_group";
	} 
	
}
