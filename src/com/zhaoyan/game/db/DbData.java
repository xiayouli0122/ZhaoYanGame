package com.zhaoyan.game.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class DbData {
	public static final String DATABASE_NAME = "game.db";
	public static final int DATABASE_VERSION = 1;

	public static final String AUTHORITY = "com.zhaoyan.game.db.dbprovider";
	
	public static final class SpyColumns implements BaseColumns{
		public static final String TABLE_NAME = "spy";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + TABLE_NAME;
		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/" + TABLE_NAME;
		
		public static final String WORD1 = "word1"; 
		public static final String WORD2 = "word2";
		public static final String GROUP = "spy_group";
	} 
	
}
