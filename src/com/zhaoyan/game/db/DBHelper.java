package com.zhaoyan.game.db;

import com.zhaoyan.game.db.DbData.SpyColumns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	/**
	 * @param context application context
	 * @param name database name
	 * @param factory
	 * @param version database version
	 */
	public DBHelper(Context context) {
		super(context, DbData.DATABASE_NAME, null, DbData.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//create table
		String sql = "create table "
				+SpyColumns.TABLE_NAME
				+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ SpyColumns.WORD1 + " TEXT, "
				+ SpyColumns.WORD2 + " TEXT, "
				+ SpyColumns.GROUP + " INTEGER);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//如果数据库版本发生变化，则删掉重建
		String dropTable = "DROP TABLE IF EXISTS " + DbData.SpyColumns.TABLE_NAME;
		db.execSQL(dropTable);
		onCreate(db);
	}

}
